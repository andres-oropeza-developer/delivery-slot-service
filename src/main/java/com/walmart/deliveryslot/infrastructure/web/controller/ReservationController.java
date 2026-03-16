package com.walmart.deliveryslot.infrastructure.web.controller;

import com.walmart.deliveryslot.application.dto.request.CancelReservationRequest;
import com.walmart.deliveryslot.application.dto.request.CreateReservationRequest;
import com.walmart.deliveryslot.application.dto.response.ReservationResponse;
import com.walmart.deliveryslot.application.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Gestión de reservas de ventanas de despacho")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Crear reserva",
               description = """
                   Crea una reserva para una orden en una ventana/zona específica.
                   
                   Implementa pessimistic locking (SELECT FOR UPDATE) para garantizar
                   que no se pueda sobre-reservar un cupo, incluso bajo alta concurrencia.
                   
                   Una orden solo puede tener una reserva activa a la vez.
                   """)
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente",
            content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden o ventana no encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Ventana agotada o reserva duplicada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request));
    }

    @Operation(summary = "Cancelar reserva",
               description = "Cancela una reserva activa y libera el cupo para otros usuarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva cancelada",
            content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
        @ApiResponse(responseCode = "400", description = "La reserva ya está cancelada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationResponse> cancel(
            @Parameter(description = "ID de la reserva") @PathVariable String id,
            @RequestBody(required = false) CancelReservationRequest request) {
        return ResponseEntity.ok(reservationService.cancel(id, request));
    }

    @Operation(summary = "Obtener reserva por orden",
               description = "Retorna la reserva asociada a una orden específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva encontrada",
            content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
        @ApiResponse(responseCode = "404", description = "No existe reserva para esa orden", content = @Content)
    })
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<ReservationResponse> findByOrderId(
            @Parameter(description = "ID de la orden") @PathVariable String orderId) {
        return ResponseEntity.ok(reservationService.findByOrderId(orderId));
    }
}
