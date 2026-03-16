import React from 'react'

export default function Header() {
  return (
    <header style={{
      background: '#0071CE',
      boxShadow: '0 2px 12px rgba(0,71,206,.25)',
      position: 'sticky', top: 0, zIndex: 100,
    }}>
      <div style={{
        maxWidth: 960, margin: '0 auto',
        padding: '0 24px', height: 60,
        display: 'flex', alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <span style={{ color: '#FFC220', fontSize: 26, lineHeight: 1 }}>✦</span>
          <span style={{
            color: '#fff', fontSize: 20, fontWeight: 700,
            fontFamily: 'var(--font-display)', letterSpacing: '-.3px',
          }}>Walmart</span>
          <span style={{ color: 'rgba(255,255,255,.35)', fontSize: 18, margin: '0 4px' }}>|</span>
          <span style={{ color: 'rgba(255,255,255,.8)', fontSize: 13 }}>
            Despacho a domicilio
          </span>
        </div>
        <nav style={{ display: 'flex', gap: 4 }}>
          <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noreferrer"
            style={{
              color: 'rgba(255,255,255,.7)', fontSize: 12, padding: '5px 10px',
              borderRadius: 6, textDecoration: 'none', border: '1px solid rgba(255,255,255,.2)',
              transition: 'all .15s',
            }}
            onMouseEnter={e => e.target.style.background = 'rgba(255,255,255,.12)'}
            onMouseLeave={e => e.target.style.background = 'transparent'}
          >API Docs</a>
        </nav>
      </div>
    </header>
  )
}
