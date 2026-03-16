package com.walmart.deliveryslot.domain.model;

import java.time.LocalDateTime;

public record Reservation(
        String id,
        String orderId,
        String windowZoneCapacityId,
        String status,
        LocalDateTime reservedAt,
        LocalDateTime cancelledAt,
        String cancellationReason
) {}
