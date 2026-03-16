package com.walmart.deliveryslot.infrastructure.persistence.repository;

import com.walmart.deliveryslot.infrastructure.persistence.entity.WindowZoneCapacityEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WindowZoneCapacityJpaRepository
        extends JpaRepository<WindowZoneCapacityEntity, String> {

    List<WindowZoneCapacityEntity> findByZoneId(String zoneId);

    // Usado en WindowService — ventanas disponibles por zona y rango de fechas
    @Query("SELECT wzc FROM WindowZoneCapacityEntity wzc " +
           "JOIN wzc.window w " +
           "WHERE wzc.zone.id = :zoneId " +
           "AND w.deliveryDate >= :from " +
           "AND w.deliveryDate <= :to " +
           "AND w.active = true " +
           "AND wzc.capacityReserved < wzc.capacityTotal " +
           "ORDER BY w.deliveryDate, w.startTime")
    List<WindowZoneCapacityEntity> findAvailableByZoneAndDateRange(
            @Param("zoneId") String zoneId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    // Pessimistic lock directo por ID — usado en ReservationService
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT wzc FROM WindowZoneCapacityEntity wzc WHERE wzc.id = :id")
    Optional<WindowZoneCapacityEntity> findByIdWithLock(@Param("id") String id);

    // Lock por windowId + zoneId
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT wzc FROM WindowZoneCapacityEntity wzc " +
           "WHERE wzc.window.id = :windowId AND wzc.zone.id = :zoneId")
    Optional<WindowZoneCapacityEntity> findByWindowIdAndZoneIdWithLock(
            @Param("windowId") String windowId,
            @Param("zoneId") String zoneId);

    @Query("SELECT wzc FROM WindowZoneCapacityEntity wzc " +
           "WHERE wzc.window.id = :windowId AND wzc.zone.id = :zoneId")
    Optional<WindowZoneCapacityEntity> findByWindowIdAndZoneId(
            @Param("windowId") String windowId,
            @Param("zoneId") String zoneId);
}
