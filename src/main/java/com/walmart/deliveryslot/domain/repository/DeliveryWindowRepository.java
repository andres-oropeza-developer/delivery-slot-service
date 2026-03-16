package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.DeliveryWindow;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeliveryWindowRepository {
    Optional<DeliveryWindow> findById(String id);
    List<DeliveryWindow> findByDateRange(LocalDate from, LocalDate to);
}
