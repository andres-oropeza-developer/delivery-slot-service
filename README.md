# Delivery Slot Service

Sistema de reserva de ventanas de despacho a domicilio — Walmart Chile.

---

## Ejecutar el proyecto

### Windows
```
start.bat
```

### Mac / Linux
```bash
chmod +x start.sh && ./start.sh
```

**Un solo comando levanta todo:**
- Verifica e instala Java 21 automáticamente si no está
- Verifica e instala Node.js automáticamente si no está
- Descarga dependencias Maven y npm
- Levanta el backend Spring Boot en `localhost:8080`
- Levanta el frontend React + Vite en `localhost:5173`
- Abre los browsers automáticamente

---

## URLs disponibles

| URL | Descripción |
|---|---|
| `http://localhost:5173` | Frontend React + Vite |
| `http://localhost:8080` | Frontend Thymeleaf |
| `http://localhost:8080/swagger-ui.html` | Documentación interactiva de la API |
| `http://localhost:8080/api-docs` | OpenAPI 3 en formato JSON |
| `http://localhost:8080/h2-console` | Consola H2 — ver tablas y datos |
| `http://localhost:8080/concurrency-test.html` | Test visual de concurrencia |

**H2 Console:** JDBC URL `jdbc:h2:mem:deliveryslot` · Usuario `sa` · Password *(vacío)*

---

## Datos precargados

El sistema carga automáticamente al arrancar mediante Flyway:

| Migración | Contenido |
|---|---|
| `V1__create_schema.sql` | Esquema completo con constraints e índices |
| `V2__seed_regions_zones_communes.sql` | 16 regiones · 9 zonas · 346 comunas de Chile |
| `V3__seed_sample_data.sql` | Ventanas de despacho · clientes · órdenes |
| `V4__update_customer_ids_to_rut.sql` | IDs de clientes en formato RUT chileno |

**Clientes de prueba:**

| RUT | Nombre | Email |
|---|---|---|
| `12345678-9` | María González | maria.gonzalez@gmail.com |
| `9876543-2` | Carlos Muñoz | c.munoz@outlook.com |
| `15678234-K` | Ana Pérez | ana.perez@empresa.cl |

---

## Ejecutar los tests

```bash
./mvnw test          # Mac / Linux
mvnw.cmd test        # Windows
```

27 tests en verde — unitarios, integración y concurrencia.

---

## Flujo principal de la API

```
1. GET  /api/communes/search?q=          Buscar comuna por nombre
2. GET  /api/windows?zoneId=&from=&to=   Ventanas disponibles
3. POST /api/orders                      Crear orden
4. POST /api/reservations                Reservar (con pessimistic lock)
5. GET  /api/reservations/by-order/{id}  Consultar reserva
6. DELETE /api/reservations/{id}         Cancelar reserva
```

---

## Probar con Postman

1. Abrir Postman → `Import`
2. Seleccionar `docs/DeliverySlotService.postman_collection.json`
3. Ejecutar carpetas en orden: `0. Comunas` → `1. Zonas` → `2. Ventanas` → `3. Órdenes` → `4. Reservas`

---

## Probar concurrencia

Abre `http://localhost:8080/concurrency-test.html` con el servidor corriendo:

1. Click en **Cargar slot disponible**
2. Selecciona número de threads (recomendado: 20)
3. Click en **Iniciar prueba**

Verás en tiempo real cómo el pessimistic locking garantiza que solo se reservan
exactamente los cupos disponibles, sin importar cuántos usuarios intenten reservar simultáneamente.

---

## Arquitectura

El proyecto implementa **Arquitectura Hexagonal (Ports and Adapters)**:

```
infrastructure  →  application  →  domain
                                  (nunca al revés)
```

- **domain/** — Records Java puros, sin dependencias de frameworks
- **application/** — Casos de uso, DTOs, excepciones de negocio
- **infrastructure/** — JPA, controllers REST, Thymeleaf

Ver `docs/ARCHITECTURE.md` para documentación técnica completa.

---

## Manejo de concurrencia

Implementado con **Pessimistic Locking** (`SELECT FOR UPDATE`) sobre `window_zone_capacity`:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT wzc FROM WindowZoneCapacityEntity wzc WHERE wzc.id = :id")
Optional<WindowZoneCapacityEntity> findByIdWithLock(@Param("id") String id);
```

Solo una transacción puede modificar un cupo a la vez. Segunda línea de defensa:
`CHECK CONSTRAINT` en la BD que impide `capacity_reserved > capacity_total`.

---

## Stack

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.4 |
| ORM | Spring Data JPA + Hibernate 6 |
| Base de datos dev | H2 (embebido, en memoria) |
| Base de datos prod | PostgreSQL 16 |
| Migraciones | Flyway |
| Frontend 1 | Thymeleaf (integrado en el JAR) |
| Frontend 2 | React 18 + Vite 5 |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Tests | JUnit 5 + Mockito + MockMvc |
| Build | Maven 3.9 (wrapper incluido) |
| Contenedores | Docker + Docker Compose |