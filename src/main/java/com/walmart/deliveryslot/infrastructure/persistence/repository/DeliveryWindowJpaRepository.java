package com.walmart.deliveryslot.infrastructure.persistence.repository;

import com.walmart.deliveryslot.infrastructure.persistence.entity.DeliveryWindowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryWindowJpaRepository extends JpaRepository<DeliveryWindowEntity, String> {
    List<DeliveryWindowEntity> findByDeliveryDateBetweenAndActiveTrue(LocalDate from, LocalDate to);
}
