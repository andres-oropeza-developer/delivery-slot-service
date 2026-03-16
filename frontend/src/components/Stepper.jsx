import React from 'react'

const LABELS = ['Tu comuna', 'Fecha y hora', 'Tus datos', 'Confirmar']

export default function Stepper({ current }) {
  return (
    <div style={{
      background: '#fff',
      borderBottom: '1px solid var(--gray-5)',
      padding: '20px 24px',
    }}>
      <div style={{
        maxWidth: 640, margin: '0 auto',
        display: 'flex', alignItems: 'center',
      }}>
        {LABELS.map((label, i) => {
          const n = i + 1
          const done    = n < current
          const active  = n === current
          const inactive = n > current
          return (
            <React.Fragment key={n}>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4 }}>
                <div style={{
                  width: 32, height: 32, borderRadius: '50%',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: 13, fontWeight: 700,
                  background: done ? 'var(--green)' : active ? 'var(--blue)' : 'var(--gray-5)',
                  color: done || active ? '#fff' : 'var(--gray-4)',
                  transition: 'all .3s',
                  boxShadow: active ? '0 0 0 4px rgba(0,113,206,.15)' : 'none',
                }}>
                  {done ? '✓' : n}
                </div>
                <span style={{
                  fontSize: 11, fontWeight: 500, whiteSpace: 'nowrap',
                  color: active ? 'var(--blue)' : done ? 'var(--green)' : 'var(--gray-4)',
                }}>{label}</span>
              </div>
              {i < LABELS.length - 1 && (
                <div style={{
                  flex: 1, height: 2, margin: '-16px 8px 0',
                  background: done ? 'var(--green)' : 'var(--gray-5)',
                  transition: 'background .3s',
                }} />
              )}
            </React.Fragment>
          )
        })}
      </div>
    </div>
  )
}
