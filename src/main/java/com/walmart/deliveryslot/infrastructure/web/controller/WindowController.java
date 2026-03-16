package com.walmart.deliveryslot.infrastructure.web.controller;

import com.walmart.deliveryslot.application.dto.request.ListWindowsRequest;
import com.walmart.deliveryslot.application.dto.response.WindowResponse;
import com.walmart.deliveryslot.application.service.WindowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/windows")
@RequiredArgsConstructor
@Validated
@Tag(name = "Delivery Windows", description = "Consulta de ventanas de despacho disponibles")
public class WindowController {

    private final WindowService windowService;

    @Operation(summary = "Listar ventanas disponibles",
               description = """
                   Retorna las ventanas de despacho disponibles para una zona y rango de fechas.
                   Solo incluye ventanas con capacidad disponible (capacity_reserved < capacity_total).
                   Los resultados se ordenan por fecha y luego por hora de inicio.
                   """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ventanas disponibles",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = WindowResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Zona no encontrada", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<WindowResponse>> listAvailable(
            @Parameter(description = "ID de la zona operacional", example = "z-rm-norte", required = true)
            @RequestParam @NotBlank String zoneId,
            @Parameter(description = "Fecha desde (inclusive)", example = "2026-03-16", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha hasta (inclusive)", example = "2026-03-21", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(windowService.listAvailable(new ListWindowsRequest(zoneId, from, to)));
    }
}
