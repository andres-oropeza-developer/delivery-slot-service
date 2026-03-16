package com.walmart.deliveryslot.infrastructure.persistence.repository;

import com.walmart.deliveryslot.infrastructure.persistence.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, String> {
    Optional<ReservationEntity> findByOrderId(String orderId);
}
