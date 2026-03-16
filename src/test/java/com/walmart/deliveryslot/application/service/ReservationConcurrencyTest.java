package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.request.CreateOrderRequest;
import com.walmart.deliveryslot.application.dto.request.CreateReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ReservationService — concurrencia")
class ReservationConcurrencyTest {

    @Autowired private ReservationService reservationService;
    @Autowired private OrderService orderService;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("solo se permite una reserva cuando N threads compiten por el ultimo cupo")
    void onlyOneReservationWins_whenMultipleThreadsCompete() throws InterruptedException {

        // ── Setup: crear ventana y wzc con exactamente 1 cupo ──────
        String windowId = UUID.randomUUID().toString();
        String wzcId    = UUID.randomUUID().toString();
        String zoneId   = "z-rm-norte";

        jdbcTemplate.update(
            "INSERT INTO delivery_window " +
            "(id, delivery_date, start_time, end_time, capacity_total, cost, active, version) " +
            "VALUES (?, '2099-12-31', '10:00', '12:00', 1, 2990, true, 0)",
            windowId);

        jdbcTemplate.update(
            "INSERT INTO window_zone_capacity " +
            "(id, window_id, zone_id, capacity_total, capacity_reserved) " +
            "VALUES (?, ?, ?, 1, 0)",
            wzcId, windowId, zoneId);

        // ── Crear una orden por thread ──────────────────────────────
        String communeId = jdbcTemplate.queryForObject(
            "SELECT id FROM commune LIMIT 1", String.class);
        String[] customers = {"cu-01", "cu-02", "cu-03"};

        int THREAD_COUNT = 5;
        List<String> orderIds = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            try {
                var resp = orderService.create(new CreateOrderRequest(
                    customers[i % 3],
                    "Calle Concurrencia " + i,
                    communeId));
                orderIds.add(resp.id());
            } catch (Exception e) {
                orderIds.add(null);
            }
        }

        // ── Lanzar N threads simultáneos ───────────────────────────
        ExecutorService executor   = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch  startLatch = new CountDownLatch(1);
        CountDownLatch  doneLatch  = new CountDownLatch(THREAD_COUNT);
        AtomicInteger   successes  = new AtomicInteger(0);
        AtomicInteger   failures   = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final String orderId = orderIds.get(i);
            if (orderId == null) { doneLatch.countDown(); failures.incrementAndGet(); continue; }

            executor.submit(() -> {
                try {
                    startLatch.await(); // todos esperan la señal de largada
                    reservationService.create(new CreateReservationRequest(orderId, wzcId));
                    successes.incrementAndGet();
                } catch (Exception e) {
                    failures.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // ── Señal de largada ────────────────────────────────────────
        startLatch.countDown();
        doneLatch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("Exitos: " + successes.get() + " / Fallos: " + failures.get());

        // ── Con 1 cupo disponible, exactamente 1 thread debe ganar ─
        assertThat(successes.get()).isEqualTo(1);
        assertThat(failures.get()).isEqualTo(THREAD_COUNT - 1);

        // Verificar que la BD tiene exactamente 1 reservado ────────
        Integer reserved = jdbcTemplate.queryForObject(
            "SELECT capacity_reserved FROM window_zone_capacity WHERE id = ?",
            Integer.class, wzcId);
        assertThat(reserved).isEqualTo(1);
    }
}
