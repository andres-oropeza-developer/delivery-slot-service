package com.walmart.deliveryslot.domain.model;

public record Commune(
        String id,
        String regionId,
        String zoneId,
        String name
) {}
