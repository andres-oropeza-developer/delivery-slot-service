package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WindowZoneCapacityRepository {
    Optional<WindowZoneCapacity> findById(String id);
    Optional<WindowZoneCapacity> findByWindowIdAndZoneIdWithLock(String windowId, String zoneId);
    List<WindowZoneCapacity> findAvailableByZoneAndDateRange(String zoneId, LocalDate from, LocalDate to);
    WindowZoneCapacity save(WindowZoneCapacity wzc);
}
