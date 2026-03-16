# Delivery Slot Service

Sistema de reserva de ventanas de despacho a domicilio — Walmart Chile.

---

## Ejecutar el proyecto

### Windows — doble clic o ejecutar en terminal

```
start.bat
```

### Mac / Linux

```bash
chmod +x start.sh && ./start.sh
```

**Eso es todo.** El script se encarga de todo lo demás:
- Verifica si Java 21 está instalado
- Si no está, lo instala automáticamente
- Descarga las dependencias del proyecto
- Compila y levanta la aplicación
- Abre el browser en `http://localhost:8080`

---

## URLs disponibles

| URL | Descripción |
|---|---|
| `http://localhost:8080` | Frontend — flujo de reserva |
| `http://localhost:8080/reservar` | Página de reserva directa |
| `http://localhost:8080/swagger-ui.html` | Documentación interactiva de la API |
| `http://localhost:8080/api-docs` | OpenAPI 3 en formato JSON |
| `http://localhost:8080/h2-console` | Consola H2 — ver tablas y datos |

**H2 Console — credenciales:**
- JDBC URL: `jdbc:h2:mem:deliveryslot`
- Usuario: `sa`
- Contraseña: *(vacía)*

---

## Datos precargados

El sistema carga automáticamente al arrancar:

| Datos | Cantidad |
|---|---|
| Regiones de Chile | 16 |
| Zonas operacionales | 9 (5 RM + 4 macrozonas) |
| Comunas | 346 (todas las oficiales de Chile) |
| Ventanas de despacho | 10 (del 16 al 21 de marzo 2026) |
| Clientes de ejemplo | 3 (cu-01, cu-02, cu-03) |

---

## Probar con Postman

1. Abrir Postman → `Import`
2. Seleccionar `docs/DeliverySlotService.postman_collection.json`
3. Ejecutar las carpetas en orden: `0. Comunas` → `1. Zonas` → `2. Ventanas` → `3. Órdenes` → `4. Reservas`

---

## Ejecutar los tests

```bash
# Mac / Linux
./mvnw test

# Windows
.\mvnw.cmd test
```

---

## Ejecutar con PostgreSQL (opcional)

```bash
docker-compose up --build
```

Requiere Docker Desktop instalado.

---

## Arquitectura

Ver `docs/ARCHITECTURE.md` para la documentación técnica completa.

---

## Stack

| | |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.4 |
| Base de datos | H2 (dev) · PostgreSQL (prod) |
| Migraciones | Flyway |
| Frontend | Thymeleaf |
| API Docs | Swagger UI (SpringDoc OpenAPI 3) |
| Tests | JUnit 5 + Mockito |
| Build | Maven (wrapper incluido) |
