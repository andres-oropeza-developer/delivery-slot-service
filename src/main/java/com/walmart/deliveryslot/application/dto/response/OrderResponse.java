package com.walmart.deliveryslot.application.dto.response;

public record OrderResponse(
        String id,
        String customerId,
        String deliveryAddress,
        String communeId,
        String zoneName,
        String status
) {}
