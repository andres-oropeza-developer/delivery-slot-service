import React, { useState } from 'react'

const DAYS_L   = ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado']
const MONTHS_L = ['enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre']

function fmtLongDate(s) {
  const [y,m,d] = s.split('-').map(Number)
  const dt = new Date(y, m-1, d)
  return `${DAYS_L[dt.getDay()]} ${d} de ${MONTHS_L[m-1]}`
}
function fmtTime(t) {
  const [h] = t.split(':'); const hr = parseInt(h)
  return `${hr > 12 ? hr-12 : hr}:00 ${hr >= 12 ? 'pm' : 'am'}`
}

export default function OrderForm({ commune, window: w, error, loading, onSubmit, onBack }) {
  const [customerId, setCustomerId] = useState('12345678-9')
  const [address, setAddress]       = useState('')
  const [touched, setTouched]       = useState(false)

  const handleSubmit = (e) => {
    e.preventDefault()
    setTouched(true)
    if (!address.trim()) return
    onSubmit({ customerId, address: address.trim(), communeId: commune.id })
  }

  return (
      <div className="animate-fade-up" style={{ maxWidth: 520 }}>
        <div style={{ marginBottom: 28 }}>
          <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 700, marginBottom: 8 }}>
            Ingresa tu dirección
          </h2>
          <p style={{ fontSize: 15, color: 'var(--gray-3)' }}>
            Completa los datos para coordinar tu despacho
          </p>
        </div>

        {/* Slot summary */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '12px 16px', background: 'var(--blue-light)',
          border: '1px solid #B3D4F5', borderRadius: 'var(--radius-md)',
          marginBottom: 24, fontSize: 13, color: 'var(--blue-dark)', fontWeight: 500,
        }}>
          <span>🕐</span>
          <span>{fmtLongDate(w.date)} · {fmtTime(w.startTime)} – {fmtTime(w.endTime)}</span>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <div>
            <label style={{ display: 'block', fontSize: 13, fontWeight: 600, color: 'var(--gray-2)', marginBottom: 8 }}>
              ID Cliente
            </label>
            <select value={customerId} onChange={e => setCustomerId(e.target.value)} style={{
              width: '100%', padding: '11px 14px', fontSize: 14,
              border: '2px solid var(--gray-5)', borderRadius: 'var(--radius-md)',
              background: '#fff', fontFamily: 'var(--font-body)', color: 'var(--gray-1)',
              cursor: 'pointer', outline: 'none',
            }}>
              <option value="12345678-9">12345678-9 — María González</option>
              <option value="9876543-2">9876543-2 — Carlos Muñoz</option>
              <option value="15678234-K">15678234-K — Ana Pérez</option>
            </select>
            <p style={{ fontSize: 12, color: 'var(--gray-4)', marginTop: 5 }}>
              RUTs de prueba: 12345678-9 · 9876543-2 · 15678234-K
            </p>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: 13, fontWeight: 600, color: 'var(--gray-2)', marginBottom: 8 }}>
              Dirección de entrega
            </label>
            <input
                type="text" value={address}
                onChange={e => setAddress(e.target.value)}
                onBlur={() => setTouched(true)}
                placeholder="Av. Providencia 456, Depto 3"
                style={{
                  width: '100%', padding: '12px 14px', fontSize: 14,
                  border: `2px solid ${touched && !address.trim() ? 'var(--red)' : 'var(--gray-5)'}`,
                  borderRadius: 'var(--radius-md)', fontFamily: 'var(--font-body)',
                  color: 'var(--gray-1)', outline: 'none',
                  transition: 'border-color .15s',
                }}
                onFocus={e => e.target.style.borderColor = 'var(--blue)'}
                onBlur={e => e.target.style.borderColor = touched && !address.trim() ? 'var(--red)' : 'var(--gray-5)'}
            />
            {touched && !address.trim() && (
                <p style={{ fontSize: 12, color: 'var(--red)', marginTop: 4 }}>
                  Por favor ingresa tu dirección
                </p>
            )}
          </div>

          {error && (
              <div style={{
                padding: '12px 16px', background: 'var(--red-bg)',
                border: '1px solid #FCA5A5', borderRadius: 'var(--radius-md)',
                fontSize: 13, color: 'var(--red)',
              }}>{error}</div>
          )}

          <div style={{ display: 'flex', gap: 10 }}>
            <button type="button" onClick={onBack} style={{
              flex: '0 0 auto', background: 'none',
              border: '1px solid var(--gray-5)', borderRadius: 'var(--radius-sm)',
              padding: '11px 18px', cursor: 'pointer',
              fontSize: 14, color: 'var(--gray-3)', fontFamily: 'var(--font-body)',
            }}>← Cambiar horario</button>
            <button type="submit" disabled={loading} style={{
              flex: 1, background: loading ? 'var(--gray-4)' : 'var(--blue)',
              color: '#fff', border: 'none', borderRadius: 'var(--radius-sm)',
              padding: '12px 24px', fontSize: 15, fontWeight: 600,
              cursor: loading ? 'not-allowed' : 'pointer', fontFamily: 'var(--font-body)',
              transition: 'background .15s',
            }}>
              {loading ? 'Procesando...' : 'Continuar →'}
            </button>
          </div>
        </form>
      </div>
  )
}