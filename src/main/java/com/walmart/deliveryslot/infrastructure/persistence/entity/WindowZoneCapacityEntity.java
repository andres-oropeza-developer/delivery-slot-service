package com.walmart.deliveryslot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "window_zone_capacity",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_window_zone",
        columnNames = {"window_id", "zone_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WindowZoneCapacityEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "window_id", nullable = false)
    private DeliveryWindowEntity window;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private ZoneEntity zone;

    @Column(name = "capacity_total", nullable = false)
    private int capacityTotal;

    @Column(name = "capacity_reserved", nullable = false)
    private int capacityReserved;
}
