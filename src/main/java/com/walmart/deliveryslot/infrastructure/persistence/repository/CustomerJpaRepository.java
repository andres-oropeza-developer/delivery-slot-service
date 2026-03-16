package com.walmart.deliveryslot.infrastructure.persistence.repository;

import com.walmart.deliveryslot.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, String> {}
