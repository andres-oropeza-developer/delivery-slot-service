import React from 'react'

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
function fmtCLP(n) {
  return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(n)
}

function SummaryRow({ label, value, bold }) {
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
      padding: '10px 0', borderBottom: '1px solid var(--gray-5)', gap: 16,
    }}>
      <span style={{ fontSize: 14, color: 'var(--gray-3)', flexShrink: 0 }}>{label}</span>
      <span style={{ fontSize: bold ? 16 : 14, fontWeight: bold ? 700 : 500, textAlign: 'right', color: 'var(--gray-1)' }}>{value}</span>
    </div>
  )
}

export default function ConfirmStep({ commune, window: w, order, error, loading, onConfirm, onBack }) {
  return (
    <div className="animate-fade-up" style={{ maxWidth: 520 }}>
      <div style={{ marginBottom: 28 }}>
        <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 700, marginBottom: 8 }}>
          Confirma tu despacho
        </h2>
        <p style={{ fontSize: 15, color: 'var(--gray-3)' }}>
          Revisa los detalles antes de reservar tu cupo
        </p>
      </div>

      <div style={{
        background: 'var(--gray-6)', borderRadius: 'var(--radius-lg)',
        padding: '8px 24px 4px', marginBottom: 20,
        border: '1px solid var(--gray-5)',
      }}>
        <SummaryRow label="📍 Dirección"  value={order?.deliveryAddress} />
        <SummaryRow label="🗺 Zona"       value={commune?.zoneName} />
        <SummaryRow label="📅 Fecha"      value={fmtLongDate(w.date)} />
        <SummaryRow label="🕐 Horario"    value={`${fmtTime(w.startTime)} – ${fmtTime(w.endTime)}`} />
        <SummaryRow label="💳 Costo despacho" value={fmtCLP(w.cost)} bold />
      </div>

      {error && (
        <div style={{
          padding: '12px 16px', background: 'var(--red-bg)',
          border: '1px solid #FCA5A5', borderRadius: 'var(--radius-md)',
          fontSize: 13, color: 'var(--red)', marginBottom: 16,
        }}>{error}</div>
      )}

      <div style={{ display: 'flex', gap: 10 }}>
        <button onClick={onBack} style={{
          flex: '0 0 auto', background: 'none',
          border: '1px solid var(--gray-5)', borderRadius: 'var(--radius-sm)',
          padding: '11px 18px', cursor: 'pointer',
          fontSize: 14, color: 'var(--gray-3)', fontFamily: 'var(--font-body)',
        }}>← Volver</button>
        <button onClick={onConfirm} disabled={loading} style={{
          flex: 1, background: loading ? 'var(--gray-4)' : 'var(--green)',
          color: '#fff', border: 'none', borderRadius: 'var(--radius-sm)',
          padding: '13px 24px', fontSize: 15, fontWeight: 600,
          cursor: loading ? 'not-allowed' : 'pointer', fontFamily: 'var(--font-body)',
          boxShadow: loading ? 'none' : '0 4px 14px rgba(26,124,62,.3)',
          transition: 'all .15s',
        }}>
          {loading ? 'Reservando...' : '✓ Confirmar reserva'}
        </button>
      </div>
    </div>
  )
}
