package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.Reservation;
import java.util.Optional;

/**
 * Puerto secundario para persistencia y consulta de reservas.
 */
public interface ReservationRepository {
    Optional<Reservation> findById(String id);
    /** Retorna la reserva más reciente asociada a la orden, si existe. */
    Optional<Reservation> findByOrderId(String orderId);
    Reservation save(Reservation reservation);
}
