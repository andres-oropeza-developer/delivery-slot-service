package com.walmart.deliveryslot.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateReservationRequest(
        @NotBlank(message = "El ID de orden es obligatorio")
        String orderId,

        @NotBlank(message = "El ID de ventana/zona es obligatorio")
        String windowZoneCapacityId
) {}
