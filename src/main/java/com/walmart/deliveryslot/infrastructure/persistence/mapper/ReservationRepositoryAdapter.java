package com.walmart.deliveryslot.infrastructure.persistence.mapper;

import com.walmart.deliveryslot.domain.model.Reservation;
import com.walmart.deliveryslot.domain.repository.ReservationRepository;
import com.walmart.deliveryslot.infrastructure.persistence.entity.OrderEntity;
import com.walmart.deliveryslot.infrastructure.persistence.entity.ReservationEntity;
import com.walmart.deliveryslot.infrastructure.persistence.entity.WindowZoneCapacityEntity;
import com.walmart.deliveryslot.infrastructure.persistence.repository.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;
    private final EntityMapper mapper;

    @Override
    public Optional<Reservation> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toReservation);
    }

    @Override
    public Optional<Reservation> findByOrderId(String orderId) {
        return jpaRepository.findByOrderId(orderId).map(mapper::toReservation);
    }

    @Override
    public Reservation save(Reservation domain) {
        ReservationEntity entity = jpaRepository.findById(domain.id())
                .orElseGet(() -> {
                    OrderEntity order = new OrderEntity();
                    order.setId(domain.orderId());

                    WindowZoneCapacityEntity wzc = new WindowZoneCapacityEntity();
                    wzc.setId(domain.windowZoneCapacityId());

                    return ReservationEntity.builder()
                            .id(domain.id())
                            .order(order)
                            .windowZoneCapacity(wzc)
                            .build();
                });

        entity.setStatus(domain.status());
        entity.setReservedAt(domain.reservedAt());
        entity.setCancelledAt(domain.cancelledAt());
        entity.setCancellationReason(domain.cancellationReason());

        return mapper.toReservation(jpaRepository.save(entity));
    }
}
