package com.walmart.deliveryslot.infrastructure.persistence.mapper;

import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;
import com.walmart.deliveryslot.domain.repository.WindowZoneCapacityRepository;
import com.walmart.deliveryslot.infrastructure.persistence.repository.WindowZoneCapacityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WindowZoneCapacityRepositoryAdapter implements WindowZoneCapacityRepository {

    private final WindowZoneCapacityJpaRepository jpaRepository;
    private final EntityMapper mapper;

    @Override
    public Optional<WindowZoneCapacity> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toWindowZoneCapacity);
    }

    @Override
    public Optional<WindowZoneCapacity> findByIdWithLock(String id) {
        return jpaRepository.findByIdWithLock(id).map(mapper::toWindowZoneCapacity);
    }

    @Override
    public Optional<WindowZoneCapacity> findByWindowIdAndZoneId(
            String windowId, String zoneId) {
        return jpaRepository.findByWindowIdAndZoneId(windowId, zoneId)
                .map(mapper::toWindowZoneCapacity);
    }

    @Override
    public Optional<WindowZoneCapacity> findByWindowIdAndZoneIdWithLock(
            String windowId, String zoneId) {
        return jpaRepository.findByWindowIdAndZoneIdWithLock(windowId, zoneId)
                .map(mapper::toWindowZoneCapacity);
    }

    @Override
    public List<WindowZoneCapacity> findAvailableByZoneAndDateRange(
            String zoneId, LocalDate from, LocalDate to) {
        return jpaRepository.findAvailableByZoneAndDateRange(zoneId, from, to)
                .stream()
                .map(mapper::toWindowZoneCapacity)
                .collect(Collectors.toList());
    }

    @Override
    public List<WindowZoneCapacity> findByZoneId(String zoneId) {
        return jpaRepository.findByZoneId(zoneId)
                .stream()
                .map(mapper::toWindowZoneCapacity)
                .collect(Collectors.toList());
    }

    @Override
    public WindowZoneCapacity save(WindowZoneCapacity domain) {
        var entity = jpaRepository.findById(domain.id())
                .orElseThrow(() -> new IllegalStateException(
                        "WZC not found: " + domain.id()));
        entity.setCapacityReserved(domain.capacityReserved());
        return mapper.toWindowZoneCapacity(jpaRepository.save(entity));
    }
}
