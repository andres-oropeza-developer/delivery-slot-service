package com.walmart.deliveryslot.application.dto.response;

public record CommuneResponse(
        String id,
        String name,
        String regionName,
        String zoneId,
        String zoneName
) {}
