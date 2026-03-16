package com.walmart.deliveryslot.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ListWindowsRequest(
        @NotBlank(message = "El ID de zona es obligatorio")
        String zoneId,

        @NotNull(message = "La fecha desde es obligatoria")
        LocalDate from,

        @NotNull(message = "La fecha hasta es obligatoria")
        LocalDate to
) {}
