package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.response.CommuneResponse;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.domain.model.Commune;
import com.walmart.deliveryslot.domain.repository.CommuneRepository;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import com.walmart.deliveryslot.infrastructure.persistence.repository.CommuneJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para consulta de comunas.
 * <p>
 * Permite buscar comunas por nombre (autocomplete) y obtener el detalle por ID.
 * Enriquece la respuesta con el nombre de zona y región.
 */
@Service
@RequiredArgsConstructor
public class CommuneService {

    private final CommuneRepository communeRepository;
    private final ZoneRepository zoneRepository;
    private final CommuneJpaRepository communeJpaRepository;

    /**
     * Busca comunas cuyo nombre contenga el texto indicado (mínimo 2 caracteres).
     *
     * @param query texto de búsqueda (case-insensitive)
     * @return lista de comunas coincidentes, vacía si el query es menor a 2 caracteres
     */
    @Transactional(readOnly = true)
    public List<CommuneResponse> search(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return communeJpaRepository.searchByName(query.trim())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el detalle de una comuna por su identificador.
     *
     * @param id identificador único de la comuna
     * @return respuesta con nombre, zona y región
     * @throws ResourceNotFoundException si la comuna no existe
     */
    @Transactional(readOnly = true)
    public CommuneResponse findById(String id) {
        Commune commune = communeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.commune(id));
        return toResponse(commune);
    }

    private CommuneResponse toResponse(Commune commune) {
        var zone = zoneRepository.findById(commune.zoneId()).orElse(null);
        return new CommuneResponse(
                commune.id(),
                commune.name(),
                commune.regionId(),
                commune.zoneId(),
                zone != null ? zone.name() : null
        );
    }

    private CommuneResponse toResponse(com.walmart.deliveryslot.infrastructure.persistence.entity.CommuneEntity e) {
        String zoneName = e.getZone() != null ? e.getZone().getName() : null;
        String regionName = e.getRegion() != null ? e.getRegion().getName() : null;
        String zoneId = e.getZone() != null ? e.getZone().getId() : null;
        return new CommuneResponse(e.getId(), e.getName(), regionName, zoneId, zoneName);
    }
}
