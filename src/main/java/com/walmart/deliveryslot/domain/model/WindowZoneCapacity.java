package com.walmart.deliveryslot.domain.model;

/**
 * Capacidad de una ventana de despacho para una zona específica.
 * <p>
 * Contiene la lógica de negocio para determinar disponibilidad de cupos.
 * Es inmutable: para modificar la capacidad se crea una nueva instancia.
 */
public record WindowZoneCapacity(
        String id,
        String windowId,
        String zoneId,
        int capacityTotal,
        int capacityReserved
) {
    /** Cupos restantes disponibles para reserva. */
    public int availableSlots() {
        return capacityTotal - capacityReserved;
    }

    /** Indica si quedan cupos disponibles. */
    public boolean hasAvailability() {
        return availableSlots() > 0;
    }
}
