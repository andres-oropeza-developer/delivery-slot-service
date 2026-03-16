package com.walmart.deliveryslot.domain.repository;

import com.walmart.deliveryslot.domain.model.Order;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String id);
    Order save(Order order);
}
