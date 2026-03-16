package com.walmart.deliveryslot.infrastructure.persistence.mapper;

import com.walmart.deliveryslot.domain.model.DeliveryWindow;
import com.walmart.deliveryslot.domain.repository.DeliveryWindowRepository;
import com.walmart.deliveryslot.infrastructure.persistence.repository.DeliveryWindowJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeliveryWindowRepositoryAdapter implements DeliveryWindowRepository {

    private final DeliveryWindowJpaRepository jpaRepository;
    private final EntityMapper mapper;

    @Override
    public Optional<DeliveryWindow> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDeliveryWindow);
    }

    @Override
    public List<DeliveryWindow> findByDateRange(LocalDate from, LocalDate to) {
        return jpaRepository.findByDeliveryDateBetweenAndActiveTrue(from, to)
                .stream()
                .map(mapper::toDeliveryWindow)
                .collect(Collectors.toList());
    }
}
