package com.walmart.deliveryslot.infrastructure.persistence.mapper;

import com.walmart.deliveryslot.domain.model.*;
import com.walmart.deliveryslot.infrastructure.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public Zone toZone(ZoneEntity e) {
        if (e == null) return null;
        return new Zone(
                e.getId(),
                e.getRegion() != null ? e.getRegion().getId() : null,
                e.getName(),
                e.getDescription(),
                e.isActive()
        );
    }

    public Commune toCommune(CommuneEntity e) {
        if (e == null) return null;
        return new Commune(
                e.getId(),
                e.getRegion() != null ? e.getRegion().getId() : null,
                e.getZone() != null ? e.getZone().getId() : null,
                e.getName()
        );
    }

    public DeliveryWindow toDeliveryWindow(DeliveryWindowEntity e) {
        if (e == null) return null;
        return new DeliveryWindow(
                e.getId(),
                e.getDeliveryDate(),
                e.getStartTime(),
                e.getEndTime(),
                e.getCapacityTotal(),
                e.getCost(),
                e.isActive(),
                e.getVersion()
        );
    }

    public WindowZoneCapacity toWindowZoneCapacity(WindowZoneCapacityEntity e) {
        if (e == null) return null;
        return new WindowZoneCapacity(
                e.getId(),
                e.getWindow() != null ? e.getWindow().getId() : null,
                e.getZone() != null ? e.getZone().getId() : null,
                e.getCapacityTotal(),
                e.getCapacityReserved()
        );
    }

    public WindowZoneCapacityEntity toWindowZoneCapacityEntity(
            WindowZoneCapacity domain,
            WindowZoneCapacityEntity existing) {
        existing.setCapacityReserved(domain.capacityReserved());
        return existing;
    }

    public Order toOrder(OrderEntity e) {
        if (e == null) return null;
        return new Order(
                e.getId(),
                e.getCustomer() != null ? e.getCustomer().getId() : null,
                e.getDeliveryAddress(),
                e.getCommune() != null ? e.getCommune().getId() : null,
                e.getStatus()
        );
    }

    public Reservation toReservation(ReservationEntity e) {
        if (e == null) return null;
        return new Reservation(
                e.getId(),
                e.getOrder() != null ? e.getOrder().getId() : null,
                e.getWindowZoneCapacity() != null ? e.getWindowZoneCapacity().getId() : null,
                e.getStatus(),
                e.getReservedAt(),
                e.getCancelledAt(),
                e.getCancellationReason()
        );
    }
}
