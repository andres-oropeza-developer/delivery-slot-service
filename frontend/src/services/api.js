const BASE = '/api'

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err.message || `Error ${res.status}`)
  }
  return res.status === 204 ? null : res.json()
}

export const api = {
  searchCommunes: (q) =>
    request(`/communes/search?q=${encodeURIComponent(q)}`),

  getWindows: (zoneId, from, to) =>
    request(`/windows?zoneId=${zoneId}&from=${from}&to=${to}`),

  createOrder: (body) =>
    request('/orders', { method: 'POST', body: JSON.stringify(body) }),

  createReservation: (body) =>
    request('/reservations', { method: 'POST', body: JSON.stringify(body) }),

  cancelReservation: (id, reason) =>
    request(`/reservations/${id}`, {
      method: 'DELETE',
      body: JSON.stringify({ reason }),
    }),
}
