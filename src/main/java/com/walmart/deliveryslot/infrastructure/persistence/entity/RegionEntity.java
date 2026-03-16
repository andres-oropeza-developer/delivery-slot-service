package com.walmart.deliveryslot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "region")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10, unique = true)
    private String code;

    @Column(nullable = false)
    private int ordinal;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<ZoneEntity> zones;
}
