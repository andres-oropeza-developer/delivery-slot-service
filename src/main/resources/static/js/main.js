// ── State ──────────────────────────────────────────────────────────────────
const state = {
  currentStep: 1,
  commune: null,      // { id, name, zoneId, zoneName, regionName }
  zone: null,         // { id, name }
  windows: [],        // list from API
  selectedDate: null,
  selectedWindow: null, // WindowResponse
  order: null,        // OrderResponse
  reservation: null,  // ReservationResponse
};

// ── Helpers ────────────────────────────────────────────────────────────────
const $ = id => document.getElementById(id);
const DAYS   = ['Dom','Lun','Mar','Mié','Jue','Vie','Sáb'];
const MONTHS = ['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'];

function formatDate(dateStr) {
  const [y, m, d] = dateStr.split('-').map(Number);
  const dt = new Date(y, m - 1, d);
  return { day: DAYS[dt.getDay()], num: d, month: MONTHS[m - 1] };
}

function formatTime(t) {
  const [h] = t.split(':');
  const hour = parseInt(h);
  const suffix = hour >= 12 ? 'pm' : 'am';
  const h12 = hour > 12 ? hour - 12 : hour === 0 ? 12 : hour;
  return `${h12}:00 ${suffix}`;
}

function formatCost(cost) {
  return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(cost);
}

function formatLongDate(dateStr) {
  const [y, m, d] = dateStr.split('-').map(Number);
  const dt = new Date(y, m - 1, d);
  const DAYS_L = ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'];
  const MONTHS_L = ['enero','febrero','marzo','abril','mayo','junio',
                    'julio','agosto','septiembre','octubre','noviembre','diciembre'];
  return `${DAYS_L[dt.getDay()]} ${d} de ${MONTHS_L[m - 1]}`;
}

async function api(path, options = {}) {
  const res = await fetch(`/api${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Error del servidor' }));
    throw new Error(err.message || 'Error desconocido');
  }
  return res.status === 204 ? null : res.json();
}

// ── Step navigation ────────────────────────────────────────────────────────
function goToStep(n) {
  for (let i = 1; i <= 5; i++) {
    const el = $(`step-${i}`);
    if (el) el.style.display = i === n ? 'block' : 'none';
  }
  updateStepper(n);
  state.currentStep = n;
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function updateStepper(current) {
  for (let i = 1; i <= 4; i++) {
    const dot = $(`step-indicator-${i}`);
    if (!dot) continue;
    const d = dot.querySelector('.step-dot');
    d.className = 'step-dot';
    if (i < current) d.classList.add('step-dot-done');
    else if (i === current) d.classList.add('step-dot-active');
    else d.classList.add('step-dot-inactive');
    if (i < current) d.textContent = '✓';
    else d.textContent = i;
  }
  for (let i = 1; i <= 3; i++) {
    const line = $(`line-${i}-${i + 1}`);
    if (line) {
      line.className = i < current ? 'step-line step-line-done' : 'step-line';
    }
  }
}

// ── Step 1: Commune search ─────────────────────────────────────────────────
let searchTimeout;
const communeInput       = $('commune-input');
const communeSuggestions = $('commune-suggestions');
const communeClear       = $('commune-clear');

communeInput.addEventListener('input', () => {
  const q = communeInput.value.trim();
  communeClear.style.display = q ? 'flex' : 'none';
  clearTimeout(searchTimeout);
  if (q.length < 2) { hideSuggestions(); return; }
  searchTimeout = setTimeout(() => searchCommunes(q), 280);
});

communeClear.addEventListener('click', () => {
  communeInput.value = '';
  communeClear.style.display = 'none';
  hideSuggestions();
  clearSelectedCommune();
});

communeInput.addEventListener('keydown', e => {
  if (e.key === 'Escape') hideSuggestions();
});

document.addEventListener('click', e => {
  if (!e.target.closest('.commune-search-wrap')) hideSuggestions();
});

async function searchCommunes(q) {
  try {
    const results = await api(`/communes/search?q=${encodeURIComponent(q)}`);
    renderSuggestions(results);
  } catch (e) { hideSuggestions(); }
}

function renderSuggestions(results) {
  if (!results.length) { hideSuggestions(); return; }
  communeSuggestions.innerHTML = results.slice(0, 8).map(c => `
    <div class="commune-suggestion" data-id="${c.id}" data-name="${c.name}"
         data-zone-id="${c.zoneId}" data-zone-name="${c.zoneName}" data-region="${c.regionName || ''}">
      <div>
        <div class="cs-name">${c.name}</div>
        <div class="cs-region">${c.regionName || ''}</div>
      </div>
      <div class="cs-zone">${c.zoneName || ''}</div>
    </div>
  `).join('');
  communeSuggestions.style.display = 'block';

  communeSuggestions.querySelectorAll('.commune-suggestion').forEach(el => {
    el.addEventListener('click', () => selectCommune({
      id: el.dataset.id,
      name: el.dataset.name,
      zoneId: el.dataset.zoneId,
      zoneName: el.dataset.zoneName,
      regionName: el.dataset.region,
    }));
  });
}

function hideSuggestions() {
  communeSuggestions.style.display = 'none';
  communeSuggestions.innerHTML = '';
}

function selectCommune(commune) {
  state.commune = commune;
  hideSuggestions();
  communeInput.value = commune.name;
  communeClear.style.display = 'flex';
  $('selected-commune-card').style.display = 'flex';
  $('selected-commune-name').textContent = commune.name;
  $('selected-commune-zone').textContent = `Zona: ${commune.zoneName || 'Sin zona asignada'}`;
  $('step1-actions').style.display = 'flex';
  $('selected-commune-id').value = commune.id;
  $('selected-zone-id').value = commune.zoneId;
}

function clearSelectedCommune() {
  state.commune = null;
  $('selected-commune-card').style.display = 'none';
  $('step1-actions').style.display = 'none';
}

$('change-commune-btn').addEventListener('click', () => {
  communeInput.value = '';
  communeInput.focus();
  clearSelectedCommune();
});

$('step1-next-btn').addEventListener('click', async () => {
  if (!state.commune) return;
  goToStep(2);
  await loadWindows();
});

// ── Step 2: Date and time ──────────────────────────────────────────────────
async function loadWindows() {
  $('loading-slots').style.display = 'block';
  $('slots-container').style.display = 'none';
  $('step2-commune-label').textContent = state.commune.name;

  const from = new Date(); from.setDate(from.getDate() + 1);
  const to   = new Date(); to.setDate(to.getDate() + 10);
  const fmt  = d => d.toISOString().split('T')[0];

  try {
    const windows = await api(`/windows?zoneId=${state.commune.zoneId}&from=${fmt(from)}&to=${fmt(to)}`);
    state.windows = windows;
    $('loading-slots').style.display = 'none';
    $('slots-container').style.display = 'block';

    if (!windows.length) {
      $('no-slots-msg').style.display = 'block';
      $('date-tabs').innerHTML = '';
      $('slots-list').innerHTML = '';
      return;
    }

    const byDate = {};
    windows.forEach(w => { if (!byDate[w.date]) byDate[w.date] = []; byDate[w.date].push(w); });
    const dates = Object.keys(byDate).sort();
    state.selectedDate = dates[0];
    renderDateTabs(dates, byDate);
    renderSlots(byDate[state.selectedDate]);
  } catch (e) {
    $('loading-slots').style.display = 'none';
    $('slots-container').style.display = 'block';
    $('no-slots-msg').style.display = 'block';
  }
}

function renderDateTabs(dates, byDate) {
  $('date-tabs').innerHTML = dates.map(d => {
    const info = formatDate(d);
    const hasAvail = byDate[d].some(w => w.available);
    return `
      <button class="date-tab${d === state.selectedDate ? ' active' : ''}" data-date="${d}">
        <span class="dt-day">${info.day}</span>
        <span class="dt-num">${info.num}</span>
        <span class="dt-month">${info.month}</span>
        ${!hasAvail ? '<span style="font-size:9px;margin-top:2px;opacity:.7">agotado</span>' : ''}
      </button>
    `;
  }).join('');

  $('date-tabs').querySelectorAll('.date-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      state.selectedDate = tab.dataset.date;
      state.selectedWindow = null;
      document.querySelectorAll('.date-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      const byDate = {};
      state.windows.forEach(w => { if (!byDate[w.date]) byDate[w.date] = []; byDate[w.date].push(w); });
      renderSlots(byDate[state.selectedDate]);
    });
  });
}

function renderSlots(slots) {
  if (!slots || !slots.length) {
    $('slots-list').innerHTML = '<p class="no-slots-msg">No hay horarios para esta fecha.</p>';
    return;
  }
  $('slots-list').innerHTML = slots.map(s => `
    <div class="slot-card${!s.available ? ' slot-card-full' : ''}"
         data-wzc-id="${s.windowZoneCapacityId}"
         data-date="${s.date}"
         data-start="${s.startTime}"
         data-end="${s.endTime}"
         data-cost="${s.cost}"
         data-available="${s.available}">
      <div class="slot-left">
        <div class="slot-radio"></div>
        <div>
          <div class="slot-time">${formatTime(s.startTime)} – ${formatTime(s.endTime)}</div>
          ${s.available
            ? `<div class="slot-avail">${s.availableSlots} cupo${s.availableSlots !== 1 ? 's' : ''} disponible${s.availableSlots !== 1 ? 's' : ''}</div>`
            : '<div class="slot-full-lbl">Agotado</div>'
          }
        </div>
      </div>
      <div class="slot-cost">${formatCost(s.cost)}</div>
    </div>
  `).join('');

  $('slots-list').querySelectorAll('.slot-card:not(.slot-card-full)').forEach(card => {
    card.addEventListener('click', () => {
      document.querySelectorAll('.slot-card').forEach(c => c.classList.remove('selected'));
      card.classList.add('selected');
      state.selectedWindow = {
        windowZoneCapacityId: card.dataset.wzcId,
        date: card.dataset.date,
        startTime: card.dataset.start,
        endTime:   card.dataset.end,
        cost:      card.dataset.cost,
      };
      $('selected-wzc-id').value  = card.dataset.wzcId;
      $('selected-date').value    = card.dataset.date;
      $('selected-start-time').value = card.dataset.start;
      $('selected-end-time').value   = card.dataset.end;
      $('selected-cost').value    = card.dataset.cost;
      setTimeout(() => goToStep(3), 300);
      renderStep3Summary();
    });
  });
}

$('step2-back-btn').addEventListener('click', () => goToStep(1));

// ── Step 3: Customer data ──────────────────────────────────────────────────
function renderStep3Summary() {
  if (!state.selectedWindow) return;
  const w = state.selectedWindow;
  $('step3-slot-summary').innerHTML = `
    <span>🕐</span>
    <span>${formatLongDate(w.date)} · ${formatTime(w.startTime)} – ${formatTime(w.endTime)}</span>
  `;
}

$('step3-back-btn').addEventListener('click', () => goToStep(2));

$('step3-next-btn').addEventListener('click', async () => {
  const address    = $('delivery-address').value.trim();
  const customerId = $('customer-id').value.trim();
  const communeId  = state.commune?.id;
  $('step3-error').style.display = 'none';

  if (!address) {
    showError('step3-error', 'Por favor ingresa tu dirección de entrega.');
    return;
  }
  if (!customerId) {
    showError('step3-error', 'Por favor ingresa el ID de cliente.');
    return;
  }

  $('step3-next-btn').disabled = true;
  $('step3-next-btn').textContent = 'Procesando...';

  try {
    const order = await api('/orders', {
      method: 'POST',
      body: JSON.stringify({ customerId, deliveryAddress: address, communeId }),
    });
    state.order = order;
    $('created-order-id').value = order.id;
    goToStep(4);
    renderSummaryCard();
  } catch (e) {
    showError('step3-error', e.message);
  } finally {
    $('step3-next-btn').disabled = false;
    $('step3-next-btn').textContent = 'Continuar →';
  }
});

// ── Step 4: Confirm ────────────────────────────────────────────────────────
function renderSummaryCard() {
  const w = state.selectedWindow;
  const o = state.order;
  $('summary-card').innerHTML = `
    <div class="summary-row">
      <span class="summary-label">📍 Dirección</span>
      <span class="summary-value">${o.deliveryAddress}</span>
    </div>
    <div class="summary-row">
      <span class="summary-label">🗺 Zona</span>
      <span class="summary-value">${state.commune.zoneName}</span>
    </div>
    <div class="summary-row">
      <span class="summary-label">📅 Fecha</span>
      <span class="summary-value">${formatLongDate(w.date)}</span>
    </div>
    <div class="summary-row">
      <span class="summary-label">🕐 Horario</span>
      <span class="summary-value">${formatTime(w.startTime)} – ${formatTime(w.endTime)}</span>
    </div>
    <div class="summary-row">
      <span class="summary-label">💳 Costo despacho</span>
      <span class="summary-value summary-value-total">${formatCost(w.cost)}</span>
    </div>
  `;
}

$('step4-back-btn').addEventListener('click', () => goToStep(3));

$('confirm-btn').addEventListener('click', async () => {
  $('step4-error').style.display = 'none';
  $('confirm-btn').disabled = true;
  $('confirm-btn').textContent = 'Reservando...';

  try {
    const reservation = await api('/reservations', {
      method: 'POST',
      body: JSON.stringify({
        orderId: state.order.id,
        windowZoneCapacityId: state.selectedWindow.windowZoneCapacityId,
      }),
    });
    state.reservation = reservation;
    goToStep(5);
    renderSuccess();
  } catch (e) {
    showError('step4-error', e.message);
  } finally {
    $('confirm-btn').disabled = false;
    $('confirm-btn').textContent = 'Confirmar reserva';
  }
});

// ── Step 5: Success ────────────────────────────────────────────────────────
function renderSuccess() {
  const w = state.selectedWindow;
  const r = state.reservation;
  $('success-subtitle').textContent =
    `Tu despacho está agendado para el ${formatLongDate(w.date)} entre las ${formatTime(w.startTime)} y las ${formatTime(w.endTime)}.`;

  $('success-card').innerHTML = `
    <div class="summary-row">
      <span class="summary-label">🔖 ID Reserva</span>
      <span class="summary-value" style="font-family:monospace;font-size:12px">${r.id}</span>
    </div>
    <div class="summary-row">
      <span class="summary-label">📦 ID Orden</span>
      <span class="summary-value" style="font-family:monospace;font-size:12px">${r.orderId}</span>
    </div>
    <div class="summary-row">
      <span class="summary-label">📍 Entrega en</span>
      <span class="summary-value">${state.order.deliveryAddress}</span>
    </div>
  `;
}

$('new-reservation-btn').addEventListener('click', () => {
  Object.assign(state, {
    currentStep: 1, commune: null, zone: null, windows: [],
    selectedDate: null, selectedWindow: null, order: null, reservation: null,
  });
  communeInput.value = '';
  communeClear.style.display = 'none';
  clearSelectedCommune();
  goToStep(1);
});

// ── Utils ──────────────────────────────────────────────────────────────────
function showError(id, msg) {
  const el = $(id);
  el.textContent = msg;
  el.style.display = 'block';
}

// Init
goToStep(1);
