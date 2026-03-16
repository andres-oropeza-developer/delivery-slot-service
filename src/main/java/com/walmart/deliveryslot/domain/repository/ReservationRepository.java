package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.Reservation;
import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(String id);
    Optional<Reservation> findByOrderId(String orderId);
    Reservation save(Reservation reservation);
}
