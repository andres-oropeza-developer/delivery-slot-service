package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.request.ListWindowsRequest;
import com.walmart.deliveryslot.application.dto.response.WindowResponse;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.domain.model.DeliveryWindow;
import com.walmart.deliveryslot.domain.model.WindowZoneCapacity;
import com.walmart.deliveryslot.domain.repository.DeliveryWindowRepository;
import com.walmart.deliveryslot.domain.repository.WindowZoneCapacityRepository;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WindowService {

    private final WindowZoneCapacityRepository wzcRepository;
    private final DeliveryWindowRepository windowRepository;
    private final ZoneRepository zoneRepository;

    @Transactional(readOnly = true)
    public List<WindowResponse> listAvailable(ListWindowsRequest request) {
        zoneRepository.findById(request.zoneId())
                .filter(z -> z.active())
                .orElseThrow(() -> ResourceNotFoundException.zone(request.zoneId()));

        List<WindowZoneCapacity> capacities = wzcRepository
                .findAvailableByZoneAndDateRange(request.zoneId(), request.from(), request.to());

        // Batch-load windows to avoid N+1
        Map<String, DeliveryWindow> windowsById = windowRepository
                .findByDateRange(request.from(), request.to())
                .stream()
                .collect(Collectors.toMap(DeliveryWindow::id, Function.identity()));

        return capacities.stream()
                .filter(wzc -> windowsById.containsKey(wzc.windowId()))
                .map(wzc -> {
                    DeliveryWindow window = windowsById.get(wzc.windowId());
                    return new WindowResponse(
                            wzc.id(),
                            window.id(),
                            window.deliveryDate(),
                            window.startTime(),
                            window.endTime(),
                            window.cost(),
                            wzc.availableSlots(),
                            wzc.capacityTotal(),
                            wzc.hasAvailability()
                    );
                })
                .sorted((a, b) -> {
                    int dateCompare = a.date().compareTo(b.date());
                    return dateCompare != 0 ? dateCompare : a.startTime().compareTo(b.startTime());
                })
                .collect(Collectors.toList());
    }
}
