package com.walmart.deliveryslot.application.dto.response;

import java.time.LocalDateTime;

public record ReservationResponse(
        String id,
        String orderId,
        String windowZoneCapacityId,
        String status,
        LocalDateTime reservedAt,
        LocalDateTime cancelledAt,
        String cancellationReason
) {}
