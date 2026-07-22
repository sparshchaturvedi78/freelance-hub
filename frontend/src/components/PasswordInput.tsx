import { useState } from 'react'
import { EyeIcon, EyeSlashIcon } from '@heroicons/react/24/outline'
import type { UseFormRegisterReturn } from 'react-hook-form'

interface PasswordInputProps {
  label: string
  placeholder?: string
  error?: string
  register: UseFormRegisterReturn
}

export default function PasswordInput({ label, placeholder, error, register }: PasswordInputProps) {
  const [visible, setVisible] = useState(false)

  return (
    <div>
      <label className="label">{label}</label>
      <div className="relative">
        <input
          {...register}
          type={visible ? 'text' : 'password'}
          placeholder={placeholder}
          className="input pr-12"
        />
        <button
          type="button"
          onClick={() => setVisible((current) => !current)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-200"
          aria-label={visible ? 'Hide password' : 'Show password'}
        >
          {visible ? <EyeSlashIcon className="w-5 h-5" /> : <EyeIcon className="w-5 h-5" />}
        </button>
      </div>
      {error ? <p className="text-red-400 text-sm mt-1">{error}</p> : null}
    </div>
  )
}
