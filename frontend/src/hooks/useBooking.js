import { useState, useCallback } from 'react'
import { api } from '../services/api'

const STEPS = { COMMUNE: 1, WINDOW: 2, DETAILS: 3, CONFIRM: 4, SUCCESS: 5 }

function addDays(d, n) {
  const r = new Date(d); r.setDate(r.getDate() + n); return r
}
function fmtDate(d) { return d.toISOString().split('T')[0] }

export function useBooking() {
  const [step, setStep]         = useState(STEPS.COMMUNE)
  const [commune, setCommune]   = useState(null)
  const [windows, setWindows]   = useState([])
  const [selectedWindow, setSelectedWindow] = useState(null)
  const [order, setOrder]       = useState(null)
  const [reservation, setReservation] = useState(null)
  const [loading, setLoading]   = useState(false)
  const [error, setError]       = useState(null)
  const [slotTaken, setSlotTaken] = useState(false)

  const clearError = () => setError(null)

  // Step 1 → 2: commune selected, load windows
  const selectCommune = useCallback(async (c) => {
    setCommune(c)
    setStep(STEPS.WINDOW)
    setSlotTaken(false)
    setLoading(true)
    setError(null)
    try {
      const from = fmtDate(addDays(new Date(), 1))
      const to   = fmtDate(addDays(new Date(), 10))
      const data = await api.getWindows(c.zoneId, from, to)
      setWindows(data)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  // Step 2 → 3: window selected
  const selectWindow = useCallback((w) => {
    setSelectedWindow(w)
    setStep(STEPS.DETAILS)
    setError(null)
  }, [])

  // Step 3 → 4: create order
  const submitDetails = useCallback(async ({ customerId, address, communeId }) => {
    setLoading(true)
    setError(null)
    try {
      const o = await api.createOrder({ customerId, deliveryAddress: address, communeId })
      setOrder(o)
      setStep(STEPS.CONFIRM)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  // Step 4 → 5: confirm reservation
  const confirmReservation = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const r = await api.createReservation({
        orderId: order.id,
        windowZoneCapacityId: selectedWindow.windowZoneCapacityId,
      })
      setReservation(r)
      setStep(STEPS.SUCCESS)
    } catch (e) {
      const isUnavailable = e.message?.toLowerCase().match(/agotad|unavailable|cupo|capacidad/)
      if (isUnavailable) {
        setSelectedWindow(null)
        setSlotTaken(true)
        setLoading(true)
        try {
          const from = fmtDate(addDays(new Date(), 1))
          const to   = fmtDate(addDays(new Date(), 10))
          const data = await api.getWindows(commune.zoneId, from, to)
          setWindows(data)
        } catch (_) {}
        setStep(STEPS.WINDOW)
      } else {
        setError(e.message)
      }
    } finally {
      setLoading(false)
    }
  }, [order, selectedWindow, commune])

  const reset = useCallback(() => {
    setStep(STEPS.COMMUNE)
    setCommune(null)
    setWindows([])
    setSelectedWindow(null)
    setOrder(null)
    setReservation(null)
    setError(null)
    setSlotTaken(false)
  }, [])

  const goBack = useCallback((toStep) => {
    setStep(toStep)
    setError(null)
  }, [])

  return {
    step, STEPS,
    commune, windows, selectedWindow, order, reservation,
    loading, error, slotTaken,
    clearError, selectCommune, selectWindow,
    submitDetails, confirmReservation, reset, goBack,
  }
}
