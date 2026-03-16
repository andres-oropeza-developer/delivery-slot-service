package com.walmart.deliveryslot.infrastructure.persistence.repository;

import com.walmart.deliveryslot.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {}
