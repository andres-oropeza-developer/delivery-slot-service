package com.walmart.deliveryslot.infrastructure.web;

import com.walmart.deliveryslot.application.dto.request.CreateReservationRequest;
import com.walmart.deliveryslot.application.dto.response.ReservationResponse;
import com.walmart.deliveryslot.application.exception.WindowUnavailableException;
import com.walmart.deliveryslot.application.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.walmart.deliveryslot.config.JacksonConfig;
import com.walmart.deliveryslot.infrastructure.web.controller.ReservationController;

@WebMvcTest(ReservationController.class)
@Import(JacksonConfig.class)
@DisplayName("ReservationController")
class ReservationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean ReservationService reservationService;

    @Test
    @DisplayName("POST /api/reservations retorna 201 con reserva creada")
    void create_returns201() throws Exception {
        ReservationResponse response = new ReservationResponse(
                "r-01", "or-01", "wzc-01", "CONFIRMED",
                LocalDateTime.now(), null, null);

        when(reservationService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateReservationRequest("or-01", "wzc-01"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("r-01"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("POST /api/reservations retorna 409 cuando ventana agotada")
    void create_returns409_whenUnavailable() throws Exception {
        when(reservationService.create(any()))
                .thenThrow(WindowUnavailableException.noSlots("dw-01", "z-rm-norte"));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateReservationRequest("or-01", "wzc-01"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /api/reservations retorna 400 cuando body inválido")
    void create_returns400_whenInvalidBody() throws Exception {
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/reservations/{id} retorna 200 con reserva cancelada")
    void cancel_returns200() throws Exception {
        ReservationResponse cancelled = new ReservationResponse(
                "r-01", "or-01", "wzc-01", "CANCELLED",
                LocalDateTime.now(), LocalDateTime.now(), "Cambio de planes");

        when(reservationService.cancel(any(), any())).thenReturn(cancelled);

        mockMvc.perform(delete("/api/reservations/r-01")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"Cambio de planes\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
