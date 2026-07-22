import { useEffect, useRef, useState } from 'react'

interface OtpInputProps {
  value: string
  length?: number
  onChange: (value: string) => void
  error?: string
}

export default function OtpInput({ value, length = 6, onChange, error }: OtpInputProps) {
  const [digits, setDigits] = useState<string[]>(Array(length).fill(''))
  const inputsRef = useRef<Array<HTMLInputElement | null>>([])

  useEffect(() => {
    const normalized = value.split('').concat(Array(length).fill('')).slice(0, length)
    setDigits(normalized)
  }, [length, value])

  const handleChange = (index: number, digit: string) => {
    if (!/^[0-9]?$/.test(digit)) return
    const next = [...digits]
    next[index] = digit
    setDigits(next)
    onChange(next.join(''))
    if (digit && index < length - 1) {
      inputsRef.current[index + 1]?.focus()
    }
  }

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>, index: number) => {
    if (event.key === 'Backspace' && !digits[index] && index > 0) {
      const prevInput = inputsRef.current[index - 1]
      prevInput?.focus()
    }
  }

  const handlePaste = (event: React.ClipboardEvent<HTMLInputElement>) => {
    event.preventDefault()
    const paste = event.clipboardData.getData('text').trim().slice(0, length)
    if (!/^[0-9]+$/.test(paste)) return
    const next = paste.split('').concat(Array(length).fill('')).slice(0, length)
    setDigits(next)
    onChange(next.join(''))
    const focusIndex = Math.min(paste.length, length - 1)
    inputsRef.current[focusIndex]?.focus()
  }

  return (
    <div className="space-y-3">
      <div className="rounded-3xl border border-slate-700 bg-slate-900/90 p-4 shadow-sm shadow-slate-950/30">
        <div className="grid grid-cols-6 gap-3">
          {digits.map((digit, index) => (
            <input
              key={index}
              ref={(el) => {
                inputsRef.current[index] = el
              }}
              value={digit}
              onChange={(event) => handleChange(index, event.target.value)}
              onKeyDown={(event) => handleKeyDown(event, index)}
              onPaste={handlePaste}
              inputMode="numeric"
              maxLength={1}
              aria-label={`OTP digit ${index + 1}`}
              className="h-16 w-14 rounded-3xl border border-slate-600 bg-slate-800 text-center text-2xl font-semibold text-white shadow-sm shadow-slate-950/20 transition-colors duration-200 focus:border-brand-500 focus:bg-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20"
            />
          ))}
        </div>
      </div>
      {error ? <p className="text-red-400 text-sm">{error}</p> : null}
    </div>
  )
}
