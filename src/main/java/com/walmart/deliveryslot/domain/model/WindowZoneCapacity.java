package com.walmart.deliveryslot.domain.model;

public record WindowZoneCapacity(
        String id,
        String windowId,
        String zoneId,
        int capacityTotal,
        int capacityReserved
) {
    public int availableSlots() {
        return capacityTotal - capacityReserved;
    }

    public boolean hasAvailability() {
        return availableSlots() > 0;
    }
}
