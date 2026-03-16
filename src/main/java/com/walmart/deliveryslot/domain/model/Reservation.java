package com.walmart.deliveryslot.domain.model;

import java.time.LocalDateTime;

/**
 * Reserva de un cupo en una ventana de despacho para una orden.
 * <p>
 * Estados posibles: CONFIRMED → CANCELLED.
 * Una orden solo puede tener una reserva activa (no CANCELLED) a la vez.
 */
public record Reservation(
        String id,
        String orderId,
        String windowZoneCapacityId,
        String status,
        LocalDateTime reservedAt,
        LocalDateTime cancelledAt,
        String cancellationReason
) {}
