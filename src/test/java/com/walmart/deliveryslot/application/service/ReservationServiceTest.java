package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.request.CancelReservationRequest;
import com.walmart.deliveryslot.application.dto.request.CreateReservationRequest;
import com.walmart.deliveryslot.application.dto.response.ReservationResponse;
import com.walmart.deliveryslot.application.exception.OrderAlreadyReservedException;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.application.exception.WindowUnavailableException;
import com.walmart.deliveryslot.domain.model.Order;
import com.walmart.deliveryslot.domain.model.Reservation;
import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;
import com.walmart.deliveryslot.domain.repository.OrderRepository;
import com.walmart.deliveryslot.domain.repository.ReservationRepository;
import com.walmart.deliveryslot.domain.repository.WindowZoneCapacityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService")
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private WindowZoneCapacityRepository wzcRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Order order;
    private WindowZoneCapacity wzc;
    private Reservation confirmedReservation;

    @BeforeEach
    void setUp() {
        order = new Order("or-01", "cu-01", "Av. Test 123", "c-005", "PENDING");
        wzc   = new WindowZoneCapacity("wzc-01", "dw-01", "z-rm-norte", 3, 1);

        confirmedReservation = new Reservation(
                "r-01", "or-01", "wzc-01", "CONFIRMED",
                LocalDateTime.now(), null, null);
    }

    // ── Nota: el servicio usa findByIdWithLock (SELECT FOR UPDATE) ──────────
    // Los mocks deben apuntar a findByIdWithLock, no a findById

    @Test
    @DisplayName("crea reserva y decrementa capacidad disponible")
    void create_success_decrementsCapacity() {
        when(orderRepository.findById("or-01")).thenReturn(Optional.of(order));
        when(reservationRepository.findByOrderId("or-01")).thenReturn(Optional.empty());
        when(wzcRepository.findByIdWithLock("wzc-01")).thenReturn(Optional.of(wzc));
        when(wzcRepository.save(any())).thenReturn(
                new WindowZoneCapacity("wzc-01", "dw-01", "z-rm-norte", 3, 2));
        when(reservationRepository.save(any())).thenReturn(confirmedReservation);

        ReservationResponse result = reservationService.create(
                new CreateReservationRequest("or-01", "wzc-01"));

        assertThat(result.status()).isEqualTo("CONFIRMED");
        assertThat(result.orderId()).isEqualTo("or-01");

        ArgumentCaptor<WindowZoneCapacity> captor =
                ArgumentCaptor.forClass(WindowZoneCapacity.class);
        verify(wzcRepository).save(captor.capture());
        assertThat(captor.getValue().capacityReserved()).isEqualTo(2);
    }

    @Test
    @DisplayName("lanza WindowUnavailableException cuando no hay cupos")
    void create_throwsUnavailable_whenNoSlots() {
        WindowZoneCapacity fullWzc =
                new WindowZoneCapacity("wzc-01", "dw-01", "z-rm-norte", 3, 3);

        when(orderRepository.findById("or-01")).thenReturn(Optional.of(order));
        when(reservationRepository.findByOrderId("or-01")).thenReturn(Optional.empty());
        when(wzcRepository.findByIdWithLock("wzc-01")).thenReturn(Optional.of(fullWzc));

        assertThatThrownBy(() -> reservationService.create(
                new CreateReservationRequest("or-01", "wzc-01")))
                .isInstanceOf(WindowUnavailableException.class);

        verify(wzcRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("lanza OrderAlreadyReservedException si la orden ya tiene reserva activa")
    void create_throwsAlreadyReserved_whenOrderHasActiveReservation() {
        when(orderRepository.findById("or-01")).thenReturn(Optional.of(order));
        when(reservationRepository.findByOrderId("or-01"))
                .thenReturn(Optional.of(confirmedReservation));

        assertThatThrownBy(() -> reservationService.create(
                new CreateReservationRequest("or-01", "wzc-01")))
                .isInstanceOf(OrderAlreadyReservedException.class);

        verify(wzcRepository, never()).findByIdWithLock(any());
    }

    @Test
    @DisplayName("permite nueva reserva si la reserva anterior fue cancelada")
    void create_allowsReservation_whenPreviousWasCancelled() {
        Reservation cancelledReservation = new Reservation(
                "r-01", "or-01", "wzc-01", "CANCELLED",
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), "Test");

        when(orderRepository.findById("or-01")).thenReturn(Optional.of(order));
        when(reservationRepository.findByOrderId("or-01"))
                .thenReturn(Optional.of(cancelledReservation));
        when(wzcRepository.findByIdWithLock("wzc-01")).thenReturn(Optional.of(wzc));
        when(wzcRepository.save(any())).thenReturn(wzc);
        when(reservationRepository.save(any())).thenReturn(confirmedReservation);

        assertThatCode(() -> reservationService.create(
                new CreateReservationRequest("or-01", "wzc-01")))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("cancela reserva y libera el cupo")
    void cancel_success_releasesSlot() {
        when(reservationRepository.findById("r-01"))
                .thenReturn(Optional.of(confirmedReservation));
        when(wzcRepository.findById("wzc-01")).thenReturn(Optional.of(wzc));

        Reservation cancelledReservation = new Reservation(
                "r-01", "or-01", "wzc-01", "CANCELLED",
                confirmedReservation.reservedAt(), LocalDateTime.now(), "Cambio de planes");
        when(wzcRepository.save(any())).thenReturn(wzc);
        when(reservationRepository.save(any())).thenReturn(cancelledReservation);

        ReservationResponse result = reservationService.cancel(
                "r-01", new CancelReservationRequest("Cambio de planes"));

        assertThat(result.status()).isEqualTo("CANCELLED");

        ArgumentCaptor<WindowZoneCapacity> captor =
                ArgumentCaptor.forClass(WindowZoneCapacity.class);
        verify(wzcRepository).save(captor.capture());
        assertThat(captor.getValue().capacityReserved()).isEqualTo(0);
    }

    @Test
    @DisplayName("lanza ResourceNotFoundException al cancelar reserva inexistente")
    void cancel_throwsNotFound_whenReservationNotFound() {
        when(reservationRepository.findById("r-inexistente"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancel("r-inexistente", null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("lanza IllegalStateException al cancelar reserva ya cancelada")
    void cancel_throwsIllegalState_whenAlreadyCancelled() {
        Reservation cancelled = new Reservation(
                "r-01", "or-01", "wzc-01", "CANCELLED",
                LocalDateTime.now(), LocalDateTime.now(), "ya cancelada");

        when(reservationRepository.findById("r-01")).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> reservationService.cancel("r-01", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("r-01");
    }
}
