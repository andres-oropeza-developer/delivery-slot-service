-- ============================================================
-- V1__create_schema.sql
-- Esquema inicial del sistema de ventanas de despacho
-- ============================================================

CREATE TABLE region (
    id      VARCHAR(36)  PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    code    VARCHAR(10)  NOT NULL UNIQUE,
    ordinal INTEGER      NOT NULL,
    active  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE zone (
    id          VARCHAR(36)  PRIMARY KEY,
    region_id   VARCHAR(36)  REFERENCES region(id),
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    active      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE commune (
    id        VARCHAR(36)  PRIMARY KEY,
    region_id VARCHAR(36)  NOT NULL REFERENCES region(id),
    zone_id   VARCHAR(36)  NOT NULL REFERENCES zone(id),
    name      VARCHAR(100) NOT NULL
);

CREATE TABLE delivery_window (
    id             VARCHAR(36)    PRIMARY KEY,
    delivery_date  DATE           NOT NULL,
    start_time     TIME           NOT NULL,
    end_time       TIME           NOT NULL,
    capacity_total INTEGER        NOT NULL CHECK (capacity_total > 0),
    cost           DECIMAL(10, 2) NOT NULL,
    active         BOOLEAN        NOT NULL DEFAULT TRUE,
    version        INTEGER        NOT NULL DEFAULT 0,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE window_zone_capacity (
    id                VARCHAR(36) PRIMARY KEY,
    window_id         VARCHAR(36) NOT NULL REFERENCES delivery_window(id),
    zone_id           VARCHAR(36) NOT NULL REFERENCES zone(id),
    capacity_total    INTEGER     NOT NULL CHECK (capacity_total >= 0),
    capacity_reserved INTEGER     NOT NULL DEFAULT 0 CHECK (capacity_reserved >= 0),
    CONSTRAINT uq_window_zone UNIQUE (window_id, zone_id),
    CONSTRAINT chk_capacity CHECK (capacity_reserved <= capacity_total)
);

CREATE TABLE customer (
    id         VARCHAR(36)  PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    phone      VARCHAR(20),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "order" (
    id               VARCHAR(36)  PRIMARY KEY,
    customer_id      VARCHAR(36)  NOT NULL REFERENCES customer(id),
    delivery_address VARCHAR(255) NOT NULL,
    commune_id       VARCHAR(36)  NOT NULL REFERENCES commune(id),
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservation (
    id                       VARCHAR(36)  PRIMARY KEY,
    order_id                 VARCHAR(36)  NOT NULL UNIQUE REFERENCES "order"(id),
    window_zone_capacity_id  VARCHAR(36)  NOT NULL REFERENCES window_zone_capacity(id),
    status                   VARCHAR(20)  NOT NULL DEFAULT 'CONFIRMED',
    reserved_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at             TIMESTAMP,
    cancellation_reason      TEXT
);

-- Indices para mejorar performance de queries frecuentes
CREATE INDEX idx_commune_zone    ON commune(zone_id);
CREATE INDEX idx_commune_region  ON commune(region_id);
CREATE INDEX idx_wzc_window      ON window_zone_capacity(window_id);
CREATE INDEX idx_wzc_zone        ON window_zone_capacity(zone_id);
CREATE INDEX idx_window_date     ON delivery_window(delivery_date);
CREATE INDEX idx_order_customer  ON "order"(customer_id);
CREATE INDEX idx_order_commune   ON "order"(commune_id);
CREATE INDEX idx_reservation_wzc ON reservation(window_zone_capacity_id);
