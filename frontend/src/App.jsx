import React from 'react'
import Header from './components/Header'
import Stepper from './components/Stepper'
import CommuneSearch from './components/CommuneSearch'
import WindowSelector from './components/WindowSelector'
import OrderForm from './components/OrderForm'
import ConfirmStep from './components/ConfirmStep'
import SuccessScreen from './components/SuccessScreen'
import { useBooking } from './hooks/useBooking'

export default function App() {
  const {
    step, STEPS,
    commune, windows, selectedWindow, order, reservation,
    loading, error, slotTaken,
    clearError, selectCommune, selectWindow,
    submitDetails, confirmReservation, reset, goBack,
  } = useBooking()

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Header />

      {step < STEPS.SUCCESS && <Stepper current={step} />}

      <main style={{ flex: 1, padding: '40px 24px 80px' }}>
        <div style={{ maxWidth: 640, margin: '0 auto' }}>

          {step === STEPS.COMMUNE && (
            <CommuneSearch onSelect={selectCommune} />
          )}

          {step === STEPS.WINDOW && (
            <WindowSelector
              windows={windows}
              commune={commune}
              slotTaken={slotTaken}
              loading={loading}
              onSelect={selectWindow}
              onBack={() => goBack(STEPS.COMMUNE)}
            />
          )}

          {step === STEPS.DETAILS && (
            <OrderForm
              commune={commune}
              window={selectedWindow}
              error={error}
              loading={loading}
              onSubmit={submitDetails}
              onBack={() => goBack(STEPS.WINDOW)}
            />
          )}

          {step === STEPS.CONFIRM && (
            <ConfirmStep
              commune={commune}
              window={selectedWindow}
              order={order}
              error={error}
              loading={loading}
              onConfirm={confirmReservation}
              onBack={() => goBack(STEPS.DETAILS)}
            />
          )}

          {step === STEPS.SUCCESS && (
            <SuccessScreen
              reservation={reservation}
              window={selectedWindow}
              order={order}
              commune={commune}
              onReset={reset}
            />
          )}

        </div>
      </main>

      <footer style={{
        background: 'var(--gray-1)', color: '#9CA3AF',
        padding: '16px 24px', fontSize: 13,
      }}>
        <div style={{
          maxWidth: 960, margin: '0 auto',
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          flexWrap: 'wrap', gap: 8,
        }}>
          <span>© 2026 Walmart Chile. Todos los derechos reservados.</span>
          <div style={{ display: 'flex', gap: 16 }}>
            {[
              ['API Docs', 'http://localhost:8080/swagger-ui.html'],
              ['H2 Console', 'http://localhost:8080/h2-console'],
              ['Thymeleaf UI', 'http://localhost:8080'],
            ].map(([label, href]) => (
              <a key={label} href={href} target="_blank" rel="noreferrer"
                style={{ color: '#9CA3AF', textDecoration: 'none', transition: 'color .15s' }}
                onMouseEnter={e => e.target.style.color = '#fff'}
                onMouseLeave={e => e.target.style.color = '#9CA3AF'}
              >{label}</a>
            ))}
          </div>
        </div>
      </footer>
    </div>
  )
}
