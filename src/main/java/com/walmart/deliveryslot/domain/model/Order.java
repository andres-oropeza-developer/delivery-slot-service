package com.walmart.deliveryslot.domain.model;

/**
 * Orden de despacho de un cliente.
 * <p>
 * Estados posibles: PENDING → (una vez reservada la ventana) CONFIRMED.
 */
public record Order(
        String id,
        String customerId,
        String deliveryAddress,
        String communeId,
        String status
) {}
