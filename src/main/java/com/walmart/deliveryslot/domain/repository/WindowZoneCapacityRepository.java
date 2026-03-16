package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto secundario para acceso a capacidades de ventana por zona.
 */
public interface WindowZoneCapacityRepository {

    Optional<WindowZoneCapacity> findById(String id);

    /**
     * Busca la capacidad por ID adquiriendo un lock pesimista (SELECT FOR UPDATE).
     * Debe usarse dentro de una transacción activa.
     */
    Optional<WindowZoneCapacity> findByIdWithLock(String id);

    Optional<WindowZoneCapacity> findByWindowIdAndZoneId(String windowId, String zoneId);

    /**
     * Busca la capacidad por ventana y zona adquiriendo un lock pesimista (SELECT FOR UPDATE).
     * Debe usarse dentro de una transacción activa.
     */
    Optional<WindowZoneCapacity> findByWindowIdAndZoneIdWithLock(String windowId, String zoneId);

    /**
     * Retorna las capacidades con cupos disponibles para una zona en un rango de fechas,
     * incluyendo solo ventanas activas.
     */
    List<WindowZoneCapacity> findAvailableByZoneAndDateRange(
            String zoneId, LocalDate from, LocalDate to);

    List<WindowZoneCapacity> findByZoneId(String zoneId);

    WindowZoneCapacity save(WindowZoneCapacity wzc);
}
