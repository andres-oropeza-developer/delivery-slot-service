package com.walmart.deliveryslot.infrastructure.persistence.repository;

import com.walmart.deliveryslot.infrastructure.persistence.entity.ZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZoneJpaRepository extends JpaRepository<ZoneEntity, String> {
    List<ZoneEntity> findByActiveTrue();
}
