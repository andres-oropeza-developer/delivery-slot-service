package com.walmart.deliveryslot.infrastructure.web;

import com.walmart.deliveryslot.application.dto.response.WindowResponse;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.application.service.WindowService;
import com.walmart.deliveryslot.config.JacksonConfig;
import com.walmart.deliveryslot.infrastructure.web.controller.WindowController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WindowController.class)
@Import(JacksonConfig.class)
@DisplayName("WindowController")
class WindowControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean  WindowService windowService;

    @Test
    @DisplayName("GET /api/windows retorna 200 con lista de ventanas")
    void listAvailable_returns200() throws Exception {
        WindowResponse w = new WindowResponse(
                "wzc-01", "dw-01",
                LocalDate.of(2026, 3, 16),
                LocalTime.of(9, 0), LocalTime.of(11, 0),
                BigDecimal.valueOf(2990), 2, 3, true);

        when(windowService.listAvailable(any())).thenReturn(List.of(w));

        mockMvc.perform(get("/api/windows")
                        .param("zoneId", "z-rm-norte")
                        .param("from",   "2026-03-16")
                        .param("to",     "2026-03-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].windowZoneCapacityId").value("wzc-01"))
                .andExpect(jsonPath("$[0].availableSlots").value(2))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("GET /api/windows retorna 404 cuando zona no existe")
    void listAvailable_returns404_whenZoneNotFound() throws Exception {
        when(windowService.listAvailable(any()))
                .thenThrow(ResourceNotFoundException.zone("zona-falsa"));

        mockMvc.perform(get("/api/windows")
                        .param("zoneId", "zona-falsa")
                        .param("from",   "2026-03-16")
                        .param("to",     "2026-03-21"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/windows retorna 400 cuando faltan parámetros")
    void listAvailable_returns400_whenMissingParams() throws Exception {
        // 'from' es un LocalDate requerido — omitirlo siempre dispara
        // MissingServletRequestParameterException → 400 Bad Request
        mockMvc.perform(get("/api/windows")
                        .param("zoneId", "z-rm-norte")
                        .param("to", "2026-03-21"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/windows retorna lista vacía cuando no hay ventanas")
    void listAvailable_returnsEmptyList() throws Exception {
        when(windowService.listAvailable(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/windows")
                        .param("zoneId", "z-rm-norte")
                        .param("from",   "2026-03-16")
                        .param("to",     "2026-03-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}