package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.request.ListWindowsRequest;
import com.walmart.deliveryslot.application.dto.response.WindowResponse;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.domain.model.DeliveryWindow;
import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;
import com.walmart.deliveryslot.domain.model.Zone;
import com.walmart.deliveryslot.domain.repository.DeliveryWindowRepository;
import com.walmart.deliveryslot.domain.repository.WindowZoneCapacityRepository;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WindowService")
class WindowServiceTest {

    @Mock private WindowZoneCapacityRepository wzcRepository;
    @Mock private DeliveryWindowRepository windowRepository;
    @Mock private ZoneRepository zoneRepository;

    @InjectMocks
    private WindowService windowService;

    private static final String ZONE_ID = "z-rm-norte";
    private static final LocalDate FROM = LocalDate.of(2026, 2, 1);
    private static final LocalDate TO   = LocalDate.of(2026, 2, 7);

    private Zone activeZone;
    private DeliveryWindow window;
    private WindowZoneCapacity wzc;

    @BeforeEach
    void setUp() {
        activeZone = new Zone(ZONE_ID, "r-07", "RM Norte", "Zona norte RM", true);

        window = new DeliveryWindow(
                "dw-01", FROM, LocalTime.of(9, 0), LocalTime.of(11, 0),
                5, BigDecimal.valueOf(2990), true, 0);

        wzc = new WindowZoneCapacity("wzc-01", "dw-01", ZONE_ID, 3, 1);
    }

    @Test
    @DisplayName("retorna ventanas disponibles para zona y rango de fechas")
    void listAvailable_returnsWindows() {
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(activeZone));
        when(wzcRepository.findAvailableByZoneAndDateRange(ZONE_ID, FROM, TO))
                .thenReturn(List.of(wzc));
        when(windowRepository.findByDateRange(FROM, TO)).thenReturn(List.of(window));

        List<WindowResponse> result = windowService.listAvailable(
                new ListWindowsRequest(ZONE_ID, FROM, TO));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).windowId()).isEqualTo("dw-01");
        assertThat(result.get(0).availableSlots()).isEqualTo(2);
        assertThat(result.get(0).available()).isTrue();
    }

    @Test
    @DisplayName("retorna lista vacía cuando no hay ventanas disponibles")
    void listAvailable_returnsEmpty_whenNoSlots() {
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(activeZone));
        when(wzcRepository.findAvailableByZoneAndDateRange(ZONE_ID, FROM, TO))
                .thenReturn(List.of());
        when(windowRepository.findByDateRange(FROM, TO)).thenReturn(List.of());

        List<WindowResponse> result = windowService.listAvailable(
                new ListWindowsRequest(ZONE_ID, FROM, TO));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("lanza ResourceNotFoundException cuando la zona no existe")
    void listAvailable_throwsNotFound_whenZoneNotFound() {
        when(zoneRepository.findById("zona-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> windowService.listAvailable(
                new ListWindowsRequest("zona-inexistente", FROM, TO)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("zona-inexistente");
    }

    @Test
    @DisplayName("lanza ResourceNotFoundException cuando la zona está inactiva")
    void listAvailable_throwsNotFound_whenZoneInactive() {
        Zone inactiveZone = new Zone(ZONE_ID, "r-07", "RM Norte", "desc", false);
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(inactiveZone));

        assertThatThrownBy(() -> windowService.listAvailable(
                new ListWindowsRequest(ZONE_ID, FROM, TO)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
