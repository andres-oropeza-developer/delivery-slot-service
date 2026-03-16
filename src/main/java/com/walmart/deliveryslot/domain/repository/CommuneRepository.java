package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.Commune;
import java.util.List;
import java.util.Optional;

public interface CommuneRepository {
    Optional<Commune> findById(String id);
    Optional<Commune> findByNameIgnoreCase(String name);
    List<Commune> findByZoneId(String zoneId);
}
