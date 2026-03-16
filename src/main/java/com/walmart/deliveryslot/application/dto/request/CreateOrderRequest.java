package com.walmart.deliveryslot.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank(message = "El ID de cliente es obligatorio")
        String customerId,

        @NotBlank(message = "La dirección de entrega es obligatoria")
        String deliveryAddress,

        @NotBlank(message = "El ID de comuna es obligatorio")
        String communeId
) {}
