import React, { useState, useEffect, useRef } from 'react'
import { api } from '../services/api'

export default function CommuneSearch({ onSelect }) {
  const [query, setQuery]     = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [open, setOpen]       = useState(false)
  const timerRef = useRef(null)
  const wrapRef  = useRef(null)

  useEffect(() => {
    clearTimeout(timerRef.current)
    if (query.trim().length < 2) { setResults([]); setOpen(false); return }
    timerRef.current = setTimeout(async () => {
      setLoading(true)
      try {
        const data = await api.searchCommunes(query.trim())
        setResults(data.slice(0, 8))
        setOpen(data.length > 0)
      } catch (_) {}
      finally { setLoading(false) }
    }, 260)
  }, [query])

  useEffect(() => {
    const handler = (e) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) setOpen(false)
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [])

  const handleSelect = (c) => {
    setQuery('')
    setOpen(false)
    setResults([])
    onSelect(c)
  }

  return (
    <div className="animate-fade-up" style={{ maxWidth: 520 }}>
      <div style={{ marginBottom: 32 }}>
        <h2 style={{
          fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 700,
          color: 'var(--gray-1)', marginBottom: 8, lineHeight: 1.2,
        }}>
          ¿En qué comuna recibes tu pedido?
        </h2>
        <p style={{ fontSize: 15, color: 'var(--gray-3)' }}>
          Escribe el nombre de tu comuna para ver los horarios disponibles.
        </p>
      </div>

      <div ref={wrapRef} style={{ position: 'relative' }}>
        <div style={{
          display: 'flex', alignItems: 'center',
          background: '#fff', border: '2px solid var(--gray-5)',
          borderRadius: 'var(--radius-md)',
          transition: 'border-color .15s, box-shadow .15s',
          boxShadow: 'var(--shadow-sm)',
        }}
          onFocus={e => e.currentTarget.style.borderColor = 'var(--blue)'}
          onBlur={e => e.currentTarget.style.borderColor = 'var(--gray-5)'}
        >
          <span style={{ paddingLeft: 16, fontSize: 18, color: 'var(--gray-4)', flexShrink: 0 }}>🔍</span>
          <input
            type="text"
            value={query}
            onChange={e => setQuery(e.target.value)}
            placeholder="Ej: Providencia, Las Condes, Maipú..."
            style={{
              flex: 1, padding: '14px 12px', fontSize: 15,
              border: 'none', outline: 'none', background: 'transparent',
              fontFamily: 'var(--font-body)', color: 'var(--gray-1)',
            }}
          />
          {loading && (
            <div style={{
              width: 18, height: 18, margin: '0 14px',
              border: '2px solid var(--gray-5)',
              borderTopColor: 'var(--blue)',
              borderRadius: '50%',
              animation: 'spin .7s linear infinite',
              flexShrink: 0,
            }} />
          )}
          {query && !loading && (
            <button onClick={() => { setQuery(''); setOpen(false) }} style={{
              background: 'none', border: 'none', cursor: 'pointer',
              padding: '0 14px', color: 'var(--gray-4)', fontSize: 16,
            }}>✕</button>
          )}
        </div>

        {open && results.length > 0 && (
          <div style={{
            position: 'absolute', top: 'calc(100% + 6px)', left: 0, right: 0,
            background: '#fff', border: '1px solid var(--gray-5)',
            borderRadius: 'var(--radius-md)', boxShadow: 'var(--shadow-lg)',
            zIndex: 50, overflow: 'hidden',
            animation: 'fadeIn .15s ease',
          }}>
            {results.map((c, i) => (
              <button key={c.id} onClick={() => handleSelect(c)} style={{
                width: '100%', padding: '11px 16px',
                display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                background: 'none', border: 'none', cursor: 'pointer',
                borderBottom: i < results.length - 1 ? '1px solid var(--gray-5)' : 'none',
                textAlign: 'left', transition: 'background .1s',
              }}
                onMouseEnter={e => e.currentTarget.style.background = 'var(--blue-light)'}
                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
              >
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--gray-1)' }}>{c.name}</div>
                  <div style={{ fontSize: 12, color: 'var(--gray-3)', marginTop: 1 }}>{c.regionName}</div>
                </div>
                <span style={{
                  fontSize: 11, fontWeight: 600,
                  background: 'var(--blue-light)', color: 'var(--blue-dark)',
                  padding: '3px 10px', borderRadius: 99,
                }}>{c.zoneName}</span>
              </button>
            ))}
          </div>
        )}
      </div>

      <p style={{ fontSize: 12, color: 'var(--gray-4)', marginTop: 12 }}>
        Cobertura en Región Metropolitana y principales ciudades del país
      </p>
    </div>
  )
}
