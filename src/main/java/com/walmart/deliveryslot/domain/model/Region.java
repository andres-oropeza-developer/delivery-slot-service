package com.walmart.deliveryslot.domain.model;

import java.util.List;

public record Region(
        String id,
        String name,
        String code,
        int ordinal,
        boolean active,
        List<Zone> zones
) {}
