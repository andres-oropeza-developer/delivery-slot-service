package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.Zone;
import java.util.List;
import java.util.Optional;

public interface ZoneRepository {
    Optional<Zone> findById(String id);
    List<Zone> findAllActive();
}
