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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio de aplicación para la gestión de órdenes de despacho.
 * <p>
 * Orquesta la creación de órdenes validando que la comuna y zona existan
 * antes de persistir en el repositorio de dominio.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CommuneRepository communeRepository;
    private final ZoneRepository zoneRepository;

    /**
     * Crea una nueva orden en estado PENDING para el cliente indicado.
     *
     * @param request datos de la orden (cliente, dirección, comuna)
     * @return respuesta con los datos de la orden creada, incluyendo la zona asociada
     * @throws ResourceNotFoundException si la comuna o zona no existen
     */
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
        log.info("Orden creada: {} para cliente: {} en zona: {}", saved.id(), saved.customerId(), zone.name());

        return new OrderResponse(
                saved.id(),
                saved.customerId(),
                saved.deliveryAddress(),
                saved.communeId(),
                zone.name(),
                saved.status()
        );
    }

    /**
     * Obtiene una orden por su identificador, enriqueciendo la respuesta con el nombre de zona.
     *
     * @param id identificador único de la orden
     * @return respuesta con los datos de la orden
     * @throws ResourceNotFoundException si la orden, comuna o zona no existen
     */
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
