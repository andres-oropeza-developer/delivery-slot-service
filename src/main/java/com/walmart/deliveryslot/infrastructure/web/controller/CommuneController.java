package com.walmart.deliveryslot.infrastructure.web.controller;

import com.walmart.deliveryslot.application.dto.response.CommuneResponse;
import com.walmart.deliveryslot.application.service.CommuneService;
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

@RestController
@RequestMapping("/api/communes")
@RequiredArgsConstructor
@Tag(name = "Communes", description = "Búsqueda de comunas para resolución de zona")
public class CommuneController {

    private final CommuneService communeService;

    @Operation(summary = "Buscar comunas por nombre",
               description = "Búsqueda parcial de comunas. Retorna la zona operacional asociada a cada una.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Resultados de búsqueda",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommuneResponse.class)))))
    @GetMapping("/search")
    public ResponseEntity<List<CommuneResponse>> search(
            @Parameter(description = "Texto a buscar en el nombre de la comuna", example = "provi")
            @RequestParam String q) {
        return ResponseEntity.ok(communeService.search(q));
    }

    @Operation(summary = "Obtener comuna por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comuna encontrada",
            content = @Content(schema = @Schema(implementation = CommuneResponse.class))),
        @ApiResponse(responseCode = "404", description = "Comuna no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommuneResponse> findById(
            @Parameter(description = "ID de la comuna", example = "c-013") @PathVariable String id) {
        return ResponseEntity.ok(communeService.findById(id));
    }
}
