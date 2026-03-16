package com.walmart.deliveryslot.domain.model;

public record Order(
        String id,
        String customerId,
        String deliveryAddress,
        String communeId,
        String status
) {}
