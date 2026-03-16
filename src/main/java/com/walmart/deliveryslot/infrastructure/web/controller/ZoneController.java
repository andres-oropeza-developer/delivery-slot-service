package com.walmart.deliveryslot.infrastructure.web.controller;

import com.walmart.deliveryslot.application.dto.response.ZoneResponse;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
@Tag(name = "Zones", description = "Gestión de zonas operacionales de despacho")
public class ZoneController {

    private final ZoneRepository zoneRepository;

    @Operation(summary = "Listar zonas activas",
               description = "Retorna todas las zonas operacionales activas")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Lista de zonas",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ZoneResponse.class)))))
    @GetMapping
    public ResponseEntity<List<ZoneResponse>> listActive() {
        return ResponseEntity.ok(zoneRepository.findAllActive()
                .stream()
                .map(z -> new ZoneResponse(z.id(), z.name(), z.description(), null))
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Obtener zona por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Zona encontrada",
            content = @Content(schema = @Schema(implementation = ZoneResponse.class))),
        @ApiResponse(responseCode = "404", description = "Zona no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ZoneResponse> findById(
            @Parameter(description = "ID de la zona", example = "z-rm-norte") @PathVariable String id) {
        return zoneRepository.findById(id)
                .map(z -> ResponseEntity.ok(new ZoneResponse(z.id(), z.name(), z.description(), null)))
                .orElse(ResponseEntity.notFound().build());
    }
}
