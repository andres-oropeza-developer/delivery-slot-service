package com.walmart.deliveryslot.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record WindowResponse(
        String windowZoneCapacityId,
        String windowId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal cost,
        int availableSlots,
        int totalSlots,
        boolean available
) {}
