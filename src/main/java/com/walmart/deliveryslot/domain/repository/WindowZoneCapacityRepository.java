package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WindowZoneCapacityRepository {

    Optional<WindowZoneCapacity> findById(String id);

    // Pessimistic lock por ID — usado en ReservationService
    Optional<WindowZoneCapacity> findByIdWithLock(String id);

    Optional<WindowZoneCapacity> findByWindowIdAndZoneId(String windowId, String zoneId);

    Optional<WindowZoneCapacity> findByWindowIdAndZoneIdWithLock(String windowId, String zoneId);

    // Usado en WindowService para listar ventanas disponibles
    List<WindowZoneCapacity> findAvailableByZoneAndDateRange(
            String zoneId, LocalDate from, LocalDate to);

    List<WindowZoneCapacity> findByZoneId(String zoneId);

    WindowZoneCapacity save(WindowZoneCapacity wzc);
}
