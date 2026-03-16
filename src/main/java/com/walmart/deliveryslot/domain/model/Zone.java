package com.walmart.deliveryslot.domain.model;

public record Zone(
        String id,
        String regionId,
        String name,
        String description,
        boolean active
) {}
