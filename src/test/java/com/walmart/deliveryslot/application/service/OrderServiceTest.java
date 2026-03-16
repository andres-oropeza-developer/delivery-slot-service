package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.request.CreateOrderRequest;
import com.walmart.deliveryslot.application.dto.response.OrderResponse;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.domain.model.Commune;
import com.walmart.deliveryslot.domain.model.Order;
import com.walmart.deliveryslot.domain.model.Zone;
import com.walmart.deliveryslot.domain.repository.CommuneRepository;
import com.walmart.deliveryslot.domain.repository.OrderRepository;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CommuneRepository communeRepository;
    @Mock private ZoneRepository zoneRepository;

    @InjectMocks private OrderService orderService;

    private Commune commune;
    private Zone zone;

    @BeforeEach
    void setUp() {
        commune = new Commune("c-013", "r-07", "z-rm-oriente", "Providencia");
        zone    = new Zone("z-rm-oriente", "r-07", "RM Oriente", "Zona oriente RM", true);
    }

    @Test
    @DisplayName("crea orden correctamente con datos válidos")
    void create_success() {
        when(communeRepository.findById("c-013")).thenReturn(Optional.of(commune));
        when(zoneRepository.findById("z-rm-oriente")).thenReturn(Optional.of(zone));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.create(
                new CreateOrderRequest("cu-01", "Av. Providencia 456", "c-013"));

        assertThat(result).isNotNull();
        assertThat(result.communeId()).isEqualTo("c-013");
        assertThat(result.deliveryAddress()).isEqualTo("Av. Providencia 456");
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.zoneName()).isEqualTo("RM Oriente");
    }

    @Test
    @DisplayName("la orden se crea con status PENDING")
    void create_setsStatusPending() {
        when(communeRepository.findById("c-013")).thenReturn(Optional.of(commune));
        when(zoneRepository.findById("z-rm-oriente")).thenReturn(Optional.of(zone));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        orderService.create(new CreateOrderRequest("cu-01", "Dirección 123", "c-013"));

        assertThat(captor.getValue().status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("genera UUID único para cada orden")
    void create_generatesUniqueId() {
        when(communeRepository.findById("c-013")).thenReturn(Optional.of(commune));
        when(zoneRepository.findById("z-rm-oriente")).thenReturn(Optional.of(zone));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse r1 = orderService.create(new CreateOrderRequest("cu-01", "Dir 1", "c-013"));
        OrderResponse r2 = orderService.create(new CreateOrderRequest("cu-02", "Dir 2", "c-013"));

        assertThat(r1.id()).isNotEqualTo(r2.id());
    }

    @Test
    @DisplayName("lanza ResourceNotFoundException cuando la comuna no existe")
    void create_throwsNotFound_whenCommuneNotFound() {
        when(communeRepository.findById("c-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(
                new CreateOrderRequest("cu-01", "Dirección 123", "c-999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("c-999");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("lanza ResourceNotFoundException cuando la zona no existe")
    void create_throwsNotFound_whenZoneNotFound() {
        when(communeRepository.findById("c-013")).thenReturn(Optional.of(commune));
        when(zoneRepository.findById("z-rm-oriente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(
                new CreateOrderRequest("cu-01", "Dirección 123", "c-013")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findById retorna la orden con nombre de zona")
    void findById_returnsOrderWithZoneName() {
        Order order = new Order("or-01", "cu-01", "Av. Test 123", "c-013", "CONFIRMED");
        when(orderRepository.findById("or-01")).thenReturn(Optional.of(order));
        when(communeRepository.findById("c-013")).thenReturn(Optional.of(commune));
        when(zoneRepository.findById("z-rm-oriente")).thenReturn(Optional.of(zone));

        OrderResponse result = orderService.findById("or-01");

        assertThat(result.id()).isEqualTo("or-01");
        assertThat(result.zoneName()).isEqualTo("RM Oriente");
    }

    @Test
    @DisplayName("findById lanza ResourceNotFoundException si la orden no existe")
    void findById_throwsNotFound() {
        when(orderRepository.findById("or-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById("or-999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("or-999");
    }
}
