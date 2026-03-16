package com.walmart.deliveryslot.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record DeliveryWindow(
        String id,
        LocalDate deliveryDate,
        LocalTime startTime,
        LocalTime endTime,
        int capacityTotal,
        BigDecimal cost,
        boolean active,
        int version
) {}
