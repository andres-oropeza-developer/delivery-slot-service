# Frontend — React + Vite

Interfaz de usuario para el sistema de reserva de ventanas de despacho.

## Requisitos

- Node.js 18+
- Backend corriendo en `http://localhost:8080`

## Ejecutar

```bash
cd frontend
npm install
npm run dev
```

Abre `http://localhost:5173` en el browser.

## Proxy

Vite redirige automáticamente `/api/*` → `http://localhost:8080/api/*`.
No hay configuración de CORS necesaria.

## Estructura

```
src/
├── components/
│   ├── Header.jsx          Header con logo Walmart
│   ├── Stepper.jsx         Indicador de pasos (1-4)
│   ├── CommuneSearch.jsx   Búsqueda de comuna con autocomplete (Paso 1)
│   ├── WindowSelector.jsx  Selector de fecha y horario (Paso 2)
│   ├── OrderForm.jsx       Formulario de dirección (Paso 3)
│   ├── ConfirmStep.jsx     Resumen y confirmación (Paso 4)
│   └── SuccessScreen.jsx   Pantalla de éxito (Paso 5)
├── hooks/
│   └── useBooking.js       Estado y lógica completa del flujo
├── services/
│   └── api.js              Llamadas al backend
├── App.jsx                 Shell principal
├── index.css               Variables CSS y estilos globales
└── main.jsx                Entry point
```

## Flujo

1. **Búsqueda de comuna** — autocomplete contra `/api/communes/search`
2. **Selección de horario** — tabs por fecha + slots con disponibilidad real
3. **Datos de entrega** — dirección + cliente de prueba
4. **Confirmación** — resumen antes de reservar
5. **Éxito** — ID de reserva y detalles

## Manejo de concurrencia

Si el cupo se agota mientras el usuario confirma, el sistema vuelve
automáticamente al paso 2 con un aviso y recarga la disponibilidad actualizada.
La orden creada en el paso 3 se reutiliza — no queda huérfana en la BD.
