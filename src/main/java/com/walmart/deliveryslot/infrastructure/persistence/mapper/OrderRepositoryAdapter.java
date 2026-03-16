package com.walmart.deliveryslot.infrastructure.persistence.mapper;

import com.walmart.deliveryslot.domain.model.Order;
import com.walmart.deliveryslot.domain.repository.OrderRepository;
import com.walmart.deliveryslot.infrastructure.persistence.entity.CommuneEntity;
import com.walmart.deliveryslot.infrastructure.persistence.entity.CustomerEntity;
import com.walmart.deliveryslot.infrastructure.persistence.entity.OrderEntity;
import com.walmart.deliveryslot.infrastructure.persistence.repository.CommuneJpaRepository;
import com.walmart.deliveryslot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.walmart.deliveryslot.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final CommuneJpaRepository communeJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;
    private final EntityMapper mapper;

    @Override
    public Optional<Order> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toOrder);
    }

    @Override
    public Order save(Order domain) {
        CommuneEntity commune = communeJpaRepository.findById(domain.communeId())
                .orElseThrow(() -> new IllegalStateException(
                        "Commune not found: " + domain.communeId()));

        CustomerEntity customer = customerJpaRepository.findById(domain.customerId())
                .orElseThrow(() -> new IllegalStateException(
                        "Customer not found: " + domain.customerId() +
                        ". Use cu-01, cu-02 or cu-03."));

        OrderEntity entity = OrderEntity.builder()
                .id(domain.id())
                .customer(customer)
                .deliveryAddress(domain.deliveryAddress())
                .commune(commune)
                .status(domain.status())
                .build();

        return mapper.toOrder(jpaRepository.save(entity));
    }
}
