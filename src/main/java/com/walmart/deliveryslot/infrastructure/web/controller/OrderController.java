package com.walmart.deliveryslot.infrastructure.web.controller;

import com.walmart.deliveryslot.application.dto.request.CreateOrderRequest;
import com.walmart.deliveryslot.application.dto.response.OrderResponse;
import com.walmart.deliveryslot.application.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de órdenes de despacho")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Crear orden",
               description = "Crea una nueva orden asociando al cliente con su dirección y comuna de entrega")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden creada",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comuna no encontrada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @Operation(summary = "Obtener orden por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(
            @Parameter(description = "ID de la orden") @PathVariable String id) {
        return ResponseEntity.ok(orderService.findById(id));
    }
}
