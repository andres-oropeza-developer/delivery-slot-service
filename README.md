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
- Verifica e instala Java 21 si no está
- Verifica e instala Node.js si no está
- Descarga dependencias Maven y npm
- Levanta el backend Spring Boot
- Levanta el frontend React + Vite
- Abre los browsers automáticamente

---

## URLs disponibles

| URL | Descripción |
|---|---|
| `http://localhost:5173` | Frontend React + Vite |
| `http://localhost:8080` | Frontend Thymeleaf |
| `http://localhost:8080/swagger-ui.html` | Documentación API (Swagger UI) |
| `http://localhost:8080/api-docs` | OpenAPI 3 JSON |
| `http://localhost:8080/h2-console` | Consola H2 (usuario: `sa`, password: vacío) |
| `http://localhost:8080/concurrency-test.html` | Test visual de concurrencia |

---

## Datos precargados

| Datos | Cantidad |
|---|---|
| Regiones de Chile | 16 |
| Zonas operacionales | 9 |
| Comunas | 346 (todas las oficiales) |
| Ventanas de despacho | 10 |
| Clientes de prueba | 3 (cu-01, cu-02, cu-03) |

---

## Tests

```bash
./mvnw test          # Mac/Linux
mvnw.cmd test        # Windows
```

27 tests en verde — unitarios + integración + concurrencia.

---

## Arquitectura

Ver `docs/ARCHITECTURE.md` para documentación técnica completa.

---

## Stack

| | |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.4 |
| Base de datos | H2 (dev) · PostgreSQL (prod) |
| Migraciones | Flyway |
| Frontend 1 | Thymeleaf (integrado en el JAR) |
| Frontend 2 | React 18 + Vite 5 |
| API Docs | Swagger UI (SpringDoc OpenAPI 3) |
| Tests | JUnit 5 + Mockito |
| Build | Maven (wrapper incluido) |
