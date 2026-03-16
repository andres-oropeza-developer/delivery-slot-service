import React, { useEffect, useState } from 'react'

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

export default function SuccessScreen({ reservation, window: w, order, commune, onReset }) {
  const [visible, setVisible] = useState(false)
  useEffect(() => { setTimeout(() => setVisible(true), 50) }, [])

  return (
    <div className="animate-fade-up" style={{ maxWidth: 480, margin: '0 auto', textAlign: 'center', padding: '32px 0' }}>
      {/* Animated checkmark */}
      <div style={{
        width: 80, height: 80, borderRadius: '50%',
        background: 'var(--green-bg)', border: '3px solid #86EFAC',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        margin: '0 auto 24px',
        fontSize: 36, color: 'var(--green)',
        transform: visible ? 'scale(1)' : 'scale(.5)',
        opacity: visible ? 1 : 0,
        transition: 'all .4s cubic-bezier(.34,1.56,.64,1)',
      }}>✓</div>

      <h2 style={{
        fontFamily: 'var(--font-display)', fontSize: 32, fontWeight: 700,
        color: 'var(--gray-1)', marginBottom: 8,
      }}>¡Reserva confirmada!</h2>

      <p style={{ fontSize: 15, color: 'var(--gray-3)', marginBottom: 28, lineHeight: 1.6 }}>
        Tu despacho está agendado para el{' '}
        <strong style={{ color: 'var(--gray-1)' }}>{fmtLongDate(w.date)}</strong>
        {' '}entre las{' '}
        <strong style={{ color: 'var(--gray-1)' }}>{fmtTime(w.startTime)} y las {fmtTime(w.endTime)}</strong>.
      </p>

      {/* Confirmation card */}
      <div style={{
        background: 'var(--green-bg)', border: '1px solid #86EFAC',
        borderRadius: 'var(--radius-lg)', padding: '20px 24px',
        marginBottom: 28, textAlign: 'left',
      }}>
        {[
          ['🔖 ID Reserva', reservation?.id?.substring(0, 18) + '...'],
          ['📦 ID Orden',   order?.id?.substring(0, 18) + '...'],
          ['📍 Entrega en', order?.deliveryAddress],
          ['🗺 Zona',       commune?.zoneName],
        ].map(([label, value]) => (
          <div key={label} style={{
            display: 'flex', justifyContent: 'space-between',
            padding: '8px 0', borderBottom: '1px solid rgba(134,239,172,.4)',
            gap: 12, alignItems: 'flex-start',
          }}>
            <span style={{ fontSize: 13, color: '#166534', flexShrink: 0 }}>{label}</span>
            <span style={{
              fontSize: 12, color: '#166534', fontWeight: 600,
              textAlign: 'right', fontFamily: 'monospace',
              wordBreak: 'break-all',
            }}>{value}</span>
          </div>
        ))}
      </div>

      <button onClick={onReset} style={{
        background: 'var(--blue)', color: '#fff', border: 'none',
        borderRadius: 'var(--radius-sm)', padding: '13px 32px',
        fontSize: 15, fontWeight: 600, cursor: 'pointer',
        fontFamily: 'var(--font-body)',
        boxShadow: '0 4px 14px rgba(0,113,206,.3)',
        transition: 'all .15s',
      }}
        onMouseEnter={e => e.target.style.background = 'var(--blue-dark)'}
        onMouseLeave={e => e.target.style.background = 'var(--blue)'}
      >
        Hacer otra reserva
      </button>
    </div>
  )
}
