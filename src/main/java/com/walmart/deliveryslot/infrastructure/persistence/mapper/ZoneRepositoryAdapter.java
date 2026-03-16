package com.walmart.deliveryslot.infrastructure.persistence.mapper;

import com.walmart.deliveryslot.domain.model.Zone;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import com.walmart.deliveryslot.infrastructure.persistence.repository.ZoneJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ZoneRepositoryAdapter implements ZoneRepository {

    private final ZoneJpaRepository jpaRepository;
    private final EntityMapper mapper;

    @Override
    public Optional<Zone> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toZone);
    }

    @Override
    public List<Zone> findAllActive() {
        return jpaRepository.findByActiveTrue()
                .stream()
                .map(mapper::toZone)
                .collect(Collectors.toList());
    }
}
