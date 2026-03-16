-- ============================================================
-- V3__seed_sample_data.sql
-- Datos de ejemplo con IDs fijos para clientes
-- y random_uuid() para el resto
-- ============================================================

-- ── Customers con IDs fijos conocidos ────────────────────────
INSERT INTO customer (id, name, email, phone) VALUES
    ('cu-01', 'Maria Gonzalez',  'maria.gonzalez@gmail.com',  '+56912345678'),
    ('cu-02', 'Carlos Munoz',    'c.munoz@outlook.com',       '+56987654321'),
    ('cu-03', 'Ana Perez',       'ana.perez@empresa.cl',      '+56955544433');

-- ── Delivery windows ─────────────────────────────────────────
INSERT INTO delivery_window (id, delivery_date, start_time, end_time, capacity_total, cost, active, version) VALUES
    (random_uuid(), '2026-03-16', '09:00', '11:00', 10, 2990, true, 0),
    (random_uuid(), '2026-03-16', '14:00', '16:00',  8, 2990, true, 0),
    (random_uuid(), '2026-03-17', '09:00', '11:00', 12, 2990, true, 0),
    (random_uuid(), '2026-03-17', '14:00', '16:00',  8, 2990, true, 0),
    (random_uuid(), '2026-03-18', '09:00', '11:00', 10, 2990, true, 0),
    (random_uuid(), '2026-03-18', '14:00', '16:00',  8, 2990, true, 0),
    (random_uuid(), '2026-03-19', '09:00', '11:00', 10, 2990, true, 0),
    (random_uuid(), '2026-03-20', '09:00', '11:00', 12, 2990, true, 0),
    (random_uuid(), '2026-03-20', '14:00', '16:00',  8, 2990, true, 0),
    (random_uuid(), '2026-03-21', '09:00', '11:00', 10, 2990, true, 0);

-- ── Window zone capacities ────────────────────────────────────
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='09:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='09:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='09:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,1,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='09:00' AND z.id='z-rm-poniente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,1,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='09:00' AND z.id='z-rm-centro';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='14:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='14:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='14:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,1,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-16' AND dw.start_time='14:00' AND z.id='z-rm-poniente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,4,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='09:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,4,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='09:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='09:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='09:00' AND z.id='z-rm-poniente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='09:00' AND z.id='z-rm-centro';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='14:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='14:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-17' AND dw.start_time='14:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-18' AND dw.start_time='09:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,4,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-18' AND dw.start_time='09:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-18' AND dw.start_time='09:00' AND z.id='z-rm-centro';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-18' AND dw.start_time='14:00' AND z.id='z-rm-poniente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-18' AND dw.start_time='14:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,5,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-19' AND dw.start_time='09:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,5,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-19' AND dw.start_time='09:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-19' AND dw.start_time='09:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,4,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-20' AND dw.start_time='09:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,4,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-20' AND dw.start_time='09:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-20' AND dw.start_time='09:00' AND z.id='z-rm-poniente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-20' AND dw.start_time='14:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-20' AND dw.start_time='14:00' AND z.id='z-rm-centro';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,5,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-21' AND dw.start_time='09:00' AND z.id='z-rm-norte';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,5,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-21' AND dw.start_time='09:00' AND z.id='z-rm-oriente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,3,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-21' AND dw.start_time='09:00' AND z.id='z-rm-sur';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-21' AND dw.start_time='09:00' AND z.id='z-rm-poniente';
INSERT INTO window_zone_capacity (id,window_id,zone_id,capacity_total,capacity_reserved)
SELECT random_uuid(),dw.id,z.id,2,0 FROM delivery_window dw,zone z WHERE dw.delivery_date='2026-03-21' AND dw.start_time='09:00' AND z.id='z-rm-centro';
