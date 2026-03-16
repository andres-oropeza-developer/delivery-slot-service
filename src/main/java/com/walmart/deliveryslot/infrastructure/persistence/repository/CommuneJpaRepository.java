package com.walmart.deliveryslot.infrastructure.persistence.repository;

import com.walmart.deliveryslot.infrastructure.persistence.entity.CommuneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommuneJpaRepository extends JpaRepository<CommuneEntity, String> {
    Optional<CommuneEntity> findByNameIgnoreCase(String name);
    List<CommuneEntity> findByZoneId(String zoneId);

    @Query("SELECT c FROM CommuneEntity c JOIN FETCH c.zone JOIN FETCH c.region " +
           "WHERE UPPER(c.name) LIKE UPPER(CONCAT('%', :query, '%')) ORDER BY c.name")
    List<CommuneEntity> searchByName(@Param("query") String query);
}
