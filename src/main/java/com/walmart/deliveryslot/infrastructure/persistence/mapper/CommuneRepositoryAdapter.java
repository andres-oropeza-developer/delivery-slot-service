package com.walmart.deliveryslot.infrastructure.persistence.mapper;

import com.walmart.deliveryslot.domain.model.Commune;
import com.walmart.deliveryslot.domain.repository.CommuneRepository;
import com.walmart.deliveryslot.infrastructure.persistence.repository.CommuneJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommuneRepositoryAdapter implements CommuneRepository {

    private final CommuneJpaRepository jpaRepository;
    private final EntityMapper mapper;

    @Override
    public Optional<Commune> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toCommune);
    }

    @Override
    public Optional<Commune> findByNameIgnoreCase(String name) {
        return jpaRepository.findByNameIgnoreCase(name).map(mapper::toCommune);
    }

    @Override
    public List<Commune> findByZoneId(String zoneId) {
        return jpaRepository.findByZoneId(zoneId)
                .stream()
                .map(mapper::toCommune)
                .collect(Collectors.toList());
    }
}
