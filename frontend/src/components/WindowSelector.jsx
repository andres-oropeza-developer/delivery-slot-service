import React, { useState, useMemo } from 'react'

const DAYS   = ['Dom','Lun','Mar','Mié','Jue','Vie','Sáb']
const MONTHS = ['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic']

function fmtDate(s) {
  const [y,m,d] = s.split('-').map(Number)
  const dt = new Date(y, m-1, d)
  return { day: DAYS[dt.getDay()], num: d, month: MONTHS[m-1] }
}
function fmtTime(t) {
  const [h] = t.split(':')
  const hr = parseInt(h)
  return `${hr > 12 ? hr-12 : hr}:00 ${hr >= 12 ? 'pm' : 'am'}`
}
function fmtCLP(n) {
  return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(n)
}

export default function WindowSelector({ windows, commune, slotTaken, loading, onSelect, onBack }) {
  const byDate = useMemo(() => {
    const map = {}
    windows.forEach(w => { if (!map[w.date]) map[w.date] = []; map[w.date].push(w) })
    return map
  }, [windows])

  const dates = useMemo(() => Object.keys(byDate).sort(), [byDate])
  const [activeDate, setActiveDate] = useState(dates[0] || null)

  const slots = byDate[activeDate] || []

  if (loading) return (
    <div className="animate-fade-up">
      <div style={{ marginBottom: 28 }}>
        <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 700, marginBottom: 8 }}>
          Elige tu fecha y horario
        </h2>
        <p style={{ color: 'var(--gray-3)', fontSize: 15 }}>Buscando disponibilidad para <strong>{commune?.name}</strong>...</p>
      </div>
      <div style={{ display: 'flex', gap: 8, marginBottom: 20 }}>
        {[...Array(5)].map((_,i) => (
          <div key={i} className="skeleton" style={{ width: 68, height: 88, borderRadius: 12 }} />
        ))}
      </div>
      {[...Array(3)].map((_,i) => (
        <div key={i} className="skeleton" style={{ height: 72, borderRadius: 12, marginBottom: 10 }} />
      ))}
    </div>
  )

  return (
    <div className="animate-fade-up">
      <div style={{ marginBottom: 28 }}>
        <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 700, marginBottom: 8 }}>
          Elige tu fecha y horario
        </h2>
        <p style={{ color: 'var(--gray-3)', fontSize: 15 }}>
          Ventanas disponibles para <strong style={{ color: 'var(--gray-1)' }}>{commune?.name}</strong>
          {commune?.zoneName && <span style={{ color: 'var(--gray-4)' }}> · {commune.zoneName}</span>}
        </p>
      </div>

      {slotTaken && (
        <div style={{
          display: 'flex', gap: 12, padding: '14px 18px',
          background: '#FFF8EC', border: '2px solid var(--yellow)',
          borderRadius: 'var(--radius-md)', marginBottom: 24,
        }}>
          <span style={{ fontSize: 20, flexShrink: 0 }}>⚠️</span>
          <div>
            <p style={{ fontWeight: 600, fontSize: 14, color: '#92400E' }}>
              El horario que elegiste ya no tiene cupos
            </p>
            <p style={{ fontSize: 13, color: '#B45309', marginTop: 2 }}>
              Otro usuario reservó el último cupo mientras confirmabas. Elige otro horario.
            </p>
          </div>
        </div>
      )}

      {dates.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '48px 16px', color: 'var(--gray-3)' }}>
          <div style={{ fontSize: 40, marginBottom: 12 }}>📭</div>
          <p style={{ fontSize: 16 }}>No hay ventanas disponibles para esta zona.</p>
        </div>
      ) : (
        <>
          {/* Date tabs */}
          <div style={{ display: 'flex', gap: 8, overflowX: 'auto', paddingBottom: 4, marginBottom: 20 }}>
            {dates.map(d => {
              const { day, num, month } = fmtDate(d)
              const isActive = d === activeDate
              const hasSlots = byDate[d].some(w => w.available)
              return (
                <button key={d} onClick={() => setActiveDate(d)} style={{
                  flexShrink: 0, display: 'flex', flexDirection: 'column', alignItems: 'center',
                  padding: '10px 14px', minWidth: 68,
                  background: isActive ? 'var(--blue)' : '#fff',
                  color: isActive ? '#fff' : 'var(--gray-2)',
                  border: `2px solid ${isActive ? 'var(--blue)' : 'var(--gray-5)'}`,
                  borderRadius: 'var(--radius-md)', cursor: 'pointer',
                  transition: 'all .15s',
                  boxShadow: isActive ? '0 4px 12px rgba(0,113,206,.25)' : 'var(--shadow-sm)',
                }}>
                  <span style={{ fontSize: 10, fontWeight: 600, textTransform: 'uppercase', letterSpacing: '.06em', opacity: .7 }}>{day}</span>
                  <span style={{ fontSize: 22, fontWeight: 700, lineHeight: 1.1, margin: '3px 0' }}>{num}</span>
                  <span style={{ fontSize: 10, opacity: .6 }}>{month}</span>
                  {!hasSlots && <span style={{ fontSize: 9, marginTop: 2, opacity: .6 }}>agotado</span>}
                </button>
              )
            })}
          </div>

          {/* Slots */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {slots.map(w => (
              <button key={w.windowZoneCapacityId}
                onClick={() => w.available && onSelect(w)}
                disabled={!w.available}
                style={{
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                  padding: '16px 20px', background: '#fff',
                  border: `2px solid ${w.available ? 'var(--gray-5)' : 'var(--gray-5)'}`,
                  borderRadius: 'var(--radius-md)', cursor: w.available ? 'pointer' : 'not-allowed',
                  opacity: w.available ? 1 : .5,
                  transition: 'all .15s', textAlign: 'left',
                  boxShadow: 'var(--shadow-sm)',
                }}
                onMouseEnter={e => { if (w.available) { e.currentTarget.style.borderColor = 'var(--blue)'; e.currentTarget.style.transform = 'translateX(4px)' }}}
                onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--gray-5)'; e.currentTarget.style.transform = 'translateX(0)' }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
                  <div style={{
                    width: 40, height: 40, borderRadius: 10, flexShrink: 0,
                    background: w.available ? 'var(--blue-light)' : 'var(--gray-5)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: 18,
                  }}>🕐</div>
                  <div>
                    <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--gray-1)' }}>
                      {fmtTime(w.startTime)} – {fmtTime(w.endTime)}
                    </div>
                    <div style={{ fontSize: 12, marginTop: 2, color: w.available ? 'var(--green)' : 'var(--red)', fontWeight: 500 }}>
                      {w.available ? `${w.availableSlots} cupo${w.availableSlots !== 1 ? 's' : ''} disponible${w.availableSlots !== 1 ? 's' : ''}` : 'Agotado'}
                    </div>
                  </div>
                </div>
                <div style={{ fontSize: 17, fontWeight: 700, color: 'var(--gray-1)' }}>
                  {fmtCLP(w.cost)}
                </div>
              </button>
            ))}
          </div>
        </>
      )}

      <button onClick={onBack} style={{
        marginTop: 24, background: 'none', border: '1px solid var(--gray-5)',
        borderRadius: 'var(--radius-sm)', padding: '9px 18px',
        cursor: 'pointer', fontSize: 14, color: 'var(--gray-3)', fontFamily: 'var(--font-body)',
      }}>← Cambiar comuna</button>
    </div>
  )
}
