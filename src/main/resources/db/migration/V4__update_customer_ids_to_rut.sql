-- ============================================================
-- V4__update_customer_ids_to_rut.sql
-- Actualiza los IDs de clientes al formato RUT chileno
-- ============================================================

-- Actualizar referencias en orders primero (FK)
UPDATE "order" SET customer_id = '12345678-9' WHERE customer_id = 'cu-01';
UPDATE "order" SET customer_id = '9876543-2'  WHERE customer_id = 'cu-02';
UPDATE "order" SET customer_id = '15678234-K' WHERE customer_id = 'cu-03';

-- Actualizar IDs en customer
UPDATE customer SET id = '12345678-9' WHERE id = 'cu-01';
UPDATE customer SET id = '9876543-2'  WHERE id = 'cu-02';
UPDATE customer SET id = '15678234-K' WHERE id = 'cu-03';