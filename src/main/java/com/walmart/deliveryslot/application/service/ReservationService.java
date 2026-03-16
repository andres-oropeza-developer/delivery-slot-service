package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.request.CancelReservationRequest;
import com.walmart.deliveryslot.application.dto.request.CreateReservationRequest;
import com.walmart.deliveryslot.application.dto.response.ReservationResponse;
import com.walmart.deliveryslot.application.exception.OrderAlreadyReservedException;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.application.exception.WindowUnavailableException;
import com.walmart.deliveryslot.domain.model.Reservation;
import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;
import com.walmart.deliveryslot.domain.repository.OrderRepository;
import com.walmart.deliveryslot.domain.repository.ReservationRepository;
import com.walmart.deliveryslot.domain.repository.WindowZoneCapacityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WindowZoneCapacityRepository wzcRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReservationResponse create(CreateReservationRequest request) {

        // 1. Verify order exists
        var order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> ResourceNotFoundException.order(request.orderId()));

        // 2. Check order doesn't already have an active reservation
        reservationRepository.findByOrderId(order.id()).ifPresent(existing -> {
            if (!"CANCELLED".equals(existing.status())) {
                throw new OrderAlreadyReservedException(order.id());
            }
        });

        // 3. Acquire pessimistic lock directly by wzc ID
        // SELECT FOR UPDATE — solo una transaccion puede tener este lock a la vez
        WindowZoneCapacity wzc = wzcRepository
                .findByIdWithLock(request.windowZoneCapacityId())
                .orElseThrow(() -> ResourceNotFoundException
                        .windowZoneCapacity(request.windowZoneCapacityId()));

        // 4. Check availability — dentro del lock
        if (!wzc.hasAvailability()) {
            throw WindowUnavailableException.noSlots(wzc.windowId(), wzc.zoneId());
        }

        // 5. Decrement available slot — dentro del lock
        WindowZoneCapacity updated = new WindowZoneCapacity(
                wzc.id(),
                wzc.windowId(),
                wzc.zoneId(),
                wzc.capacityTotal(),
                wzc.capacityReserved() + 1
        );
        wzcRepository.save(updated);

        // 6. Create reservation
        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                order.id(),
                wzc.id(),
                "CONFIRMED",
                LocalDateTime.now(),
                null,
                null
        );
        Reservation saved = reservationRepository.save(reservation);

        log.info("Reserva creada: {} para orden: {}", saved.id(), order.id());
        return toResponse(saved);
    }

    @Transactional
    public ReservationResponse cancel(String reservationId, CancelReservationRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> ResourceNotFoundException.reservation(reservationId));

        if ("CANCELLED".equals(reservation.status())) {
            throw new IllegalStateException("La reserva ya esta cancelada: " + reservationId);
        }

        // Release the slot back
        wzcRepository.findById(reservation.windowZoneCapacityId()).ifPresent(wzc -> {
            WindowZoneCapacity released = new WindowZoneCapacity(
                    wzc.id(), wzc.windowId(), wzc.zoneId(),
                    wzc.capacityTotal(),
                    Math.max(0, wzc.capacityReserved() - 1)
            );
            wzcRepository.save(released);
        });

        Reservation cancelled = new Reservation(
                reservation.id(), reservation.orderId(),
                reservation.windowZoneCapacityId(), "CANCELLED",
                reservation.reservedAt(), LocalDateTime.now(),
                request != null ? request.reason() : null
        );
        Reservation saved = reservationRepository.save(cancelled);

        log.info("Reserva cancelada: {}", saved.id());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findByOrderId(String orderId) {
        return reservationRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe reserva para la orden: " + orderId));
    }

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.id(), r.orderId(), r.windowZoneCapacityId(),
                r.status(), r.reservedAt(), r.cancelledAt(), r.cancellationReason()
        );
    }
}
