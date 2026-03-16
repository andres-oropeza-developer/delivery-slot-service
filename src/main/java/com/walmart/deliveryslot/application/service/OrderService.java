package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.request.CreateOrderRequest;
import com.walmart.deliveryslot.application.dto.response.OrderResponse;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.domain.model.Commune;
import com.walmart.deliveryslot.domain.model.Order;
import com.walmart.deliveryslot.domain.repository.CommuneRepository;
import com.walmart.deliveryslot.domain.repository.OrderRepository;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CommuneRepository communeRepository;
    private final ZoneRepository zoneRepository;

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Commune commune = communeRepository.findById(request.communeId())
                .orElseThrow(() -> ResourceNotFoundException.commune(request.communeId()));

        var zone = zoneRepository.findById(commune.zoneId())
                .orElseThrow(() -> ResourceNotFoundException.zone(commune.zoneId()));

        Order order = new Order(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.deliveryAddress(),
                commune.id(),
                "PENDING"
        );

        Order saved = orderRepository.save(order);

        return new OrderResponse(
                saved.id(),
                saved.customerId(),
                saved.deliveryAddress(),
                saved.communeId(),
                zone.name(),
                saved.status()
        );
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.order(id));

        Commune commune = communeRepository.findById(order.communeId())
                .orElseThrow(() -> ResourceNotFoundException.commune(order.communeId()));

        var zone = zoneRepository.findById(commune.zoneId())
                .orElseThrow(() -> ResourceNotFoundException.zone(commune.zoneId()));

        return new OrderResponse(
                order.id(),
                order.customerId(),
                order.deliveryAddress(),
                order.communeId(),
                zone.name(),
                order.status()
        );
    }
}
