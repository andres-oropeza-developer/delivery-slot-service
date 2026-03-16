# Documentación de Arquitectura — Delivery Slot Service

## Visión general

El servicio implementa **Arquitectura Hexagonal (Ports and Adapters)**, introducida por
Alistair Cockburn en 2005. El objetivo es mantener la lógica de negocio completamente
desacoplada de las tecnologías externas (base de datos, frameworks, HTTP).

---

## Estructura de paquetes

```
com.walmart.deliveryslot/
├── domain/                    ← Núcleo. Sin dependencias de Spring ni JPA.
│   ├── model/                 ← Records Java (inmutables)
│   │   ├── Region.java
│   │   ├── Zone.java
│   │   ├── Commune.java
│   │   ├── DeliveryWindow.java
│   │   ├── WindowZoneCapacity.java
│   │   ├── Order.java
│   │   └── Reservation.java
│   └── repository/            ← Interfaces (Ports de salida)
│       ├── ZoneRepository.java
│       ├── CommuneRepository.java
│       ├── DeliveryWindowRepository.java
│       ├── WindowZoneCapacityRepository.java
│       ├── OrderRepository.java
│       └── ReservationRepository.java
│
├── application/               ← Orquesta el dominio. Casos de uso.
│   ├── service/               ← Un servicio por caso de uso
│   │   ├── WindowService.java
│   │   ├── ReservationService.java
│   │   ├── OrderService.java
│   │   └── CommuneService.java
│   ├── dto/
│   │   ├── request/           ← Lo que entra por la API
│   │   └── response/          ← Lo que sale por la API
│   └── exception/             ← Excepciones de negocio
│
├── infrastructure/            ← Todo lo externo al dominio
│   ├── persistence/
│   │   ├── entity/            ← Entidades JPA (@Entity)
│   │   ├── repository/        ← JPA Repositories (Spring Data)
│   │   └── mapper/            ← Adaptadores que implementan los Ports
│   └── web/
│       ├── controller/        ← REST Controllers + WebController (Thymeleaf)
│       └── advice/            ← GlobalExceptionHandler
│
└── config/                    ← Beans de configuración (OpenAPI, Jackson)
```

---

## Regla de dependencias

```
infrastructure  →  application  →  domain
                                  (nunca al revés)
```

El dominio no conoce Spring, JPA ni ningún framework.
La infraestructura implementa las interfaces del dominio — no al revés.

---

## Dominios del modelo

### Geográfico (region → zone → commune)
Modela la cobertura del servicio. 16 regiones, 9 zonas operacionales, 346 comunas.
La relación `commune → zone` es la que determina qué ventanas están disponibles
para una dirección dada.

### Oferta (delivery_window + window_zone_capacity)
`delivery_window` define las franjas horarias disponibles (fecha, hora inicio/fin, costo).
`window_zone_capacity` es la tabla pivote crítica — registra la capacidad por ventana
por zona, y es la fila que se lockea durante la reserva.

### Demanda (customer + order)
`customer` es el cliente. `order` representa la intención de compra con dirección
de entrega y comuna destino.

### Reserva (reservation)
Conecta una orden con un cupo específico de `window_zone_capacity`.
Una orden solo puede tener una reserva activa a la vez.

---

## Flujo de una reserva

```
POST /api/reservations
        ↓
ReservationController.create()
        ↓
ReservationService.create()
    ├── orderRepository.findById()          → verifica que la orden existe
    ├── reservationRepository.findByOrderId()  → verifica no hay reserva activa
    ├── wzcRepository.findByIdWithLock()    ← SELECT FOR UPDATE (lock pesimista)
    ├── wzc.hasAvailability()               → verifica cupo disponible
    ├── wzcRepository.save(decremented)     → capacity_reserved++
    └── reservationRepository.save()        → crea la reserva
        ↓
201 Created { id, orderId, status: "CONFIRMED", reservedAt }
```

---

## Manejo de concurrencia

### Problema
Dos usuarios intentan reservar el último cupo disponible simultáneamente.
Sin locking, ambos pasan la verificación de disponibilidad y se genera
una sobre-reserva.

### Solución: Pessimistic Locking

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<WindowZoneCapacityEntity> findByWindowIdAndZoneIdWithLock(
        String windowId, String zoneId);
```

`SELECT FOR UPDATE` sobre `window_zone_capacity` garantiza que solo una
transacción puede leer y modificar esa fila a la vez. La segunda transacción
espera hasta que la primera libere el lock.

### Segunda línea de defensa: CHECK CONSTRAINT

```sql
CONSTRAINT chk_capacity CHECK (capacity_reserved <= capacity_total)
```

Si por algún bug de aplicación se intentara superar la capacidad,
la BD rechaza la operación.

---

## Perfiles de Spring Boot

| Perfil | BD | Uso |
|---|---|---|
| `dev` (default) | H2 en memoria | Desarrollo local, evaluación |
| `test` | H2 en memoria | Tests unitarios e integración |
| `prod` | PostgreSQL | Producción con Docker |

Cambiar de perfil: `SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run`

---

## Decisiones técnicas clave

### ¿Por qué Records para el dominio?
Los Records de Java 16+ son inmutables por diseño — no tienen setters,
no pueden ser extendidos, y fuerzan al compilador a garantizar que todos
los campos están inicializados. Esto hace el dominio más predecible y seguro.
JPA no puede usar Records como entidades (requiere constructores sin args
y setters para sus proxies), por eso existen las entidades JPA separadas
en `infrastructure/persistence/entity/`.

### ¿Por qué no usar `@Entity` directamente en el dominio?
Mezclar anotaciones de JPA con el modelo de dominio acopla la lógica de negocio
a la tecnología de persistencia. Si mañana se cambia de JPA a jOOQ o a MongoDB,
el dominio no debería cambiar — solo los adaptadores de infraestructura.

### ¿Por qué H2 en modo PostgreSQL?
`MODE=PostgreSQL` en la URL de H2 habilita compatibilidad con sintaxis de
PostgreSQL, incluyendo `random_uuid()`. Permite que las mismas migraciones
de Flyway funcionen en ambas BDs sin modificaciones.

### ¿Por qué Flyway y no `ddl-auto`?
`spring.jpa.hibernate.ddl-auto=create` destruye los datos en cada reinicio.
`ddl-auto=update` intenta "adivinar" los cambios del esquema y falla en casos
complejos. Flyway da control explícito y versionado sobre el esquema —
el estándar en proyectos productivos.
