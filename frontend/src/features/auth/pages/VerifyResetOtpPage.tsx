import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'react-toastify'
import authService from '../../../lib/auth'
import { loadResetPasswordEmail, saveResetPasswordEmail } from '../../../lib/authFlow'
import { ArrowLeftOnRectangleIcon, ShieldCheckIcon } from '@heroicons/react/24/outline'
import OtpInput from '../../../components/OtpInput'

const otpSchema = z.object({
  otp: z.string().length(6, 'Enter the 6-digit code'),
})

type OtpFormData = z.infer<typeof otpSchema>

export default function VerifyResetOtpPage() {
  const navigate = useNavigate()
  const [otp, setOtp] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [resendSeconds, setResendSeconds] = useState(45)
  const email = loadResetPasswordEmail() || ''

  useEffect(() => {
    if (!email) {
      navigate('/forgot-password')
    }
  }, [email, navigate])

  useEffect(() => {
    const timer = window.setInterval(() => {
      setResendSeconds((current) => Math.max(current - 1, 0))
    }, 1000)
    return () => window.clearInterval(timer)
  }, [])

  const handleResend = async () => {
    if (!email) return
    setResendSeconds(45)
    try {
      await authService.forgotPassword(email)
      toast.success('Reset OTP resent successfully')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'Failed to resend OTP')
    }
  }

  const { handleSubmit, formState: { errors } } = useForm<OtpFormData>({
    resolver: zodResolver(otpSchema),
  })

  const onSubmit = async () => {
    if (!email) return
    setIsLoading(true)
    try {
      await authService.verifyResetOtp(email, otp)
      toast.success('OTP verified — set your new password')
      navigate('/reset-password')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'OTP verification failed')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-10">
      <div className="relative w-full max-w-xl">
        <div className="rounded-3xl border border-slate-800 bg-slate-950/95 p-10 shadow-2xl backdrop-blur-xl">
          <div className="mb-8 text-center">
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-3xl bg-gradient-to-br from-brand-500 to-violet-500 text-white shadow-xl">
              <ShieldCheckIcon className="w-8 h-8" />
            </div>
            <h1 className="text-3xl font-semibold text-white">Verify reset code</h1>
            <p className="mt-2 text-slate-400">Enter the 6-digit OTP sent to {email}</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <OtpInput value={otp} onChange={setOtp} error={errors.otp?.message} />
            <button type="submit" disabled={isLoading || otp.length < 6} className="btn-primary w-full py-3 font-semibold">
              {isLoading ? 'Verifying...' : 'Continue'}
            </button>
          </form>

          <div className="mt-6 flex items-center justify-between text-sm text-slate-400">
            <p>{resendSeconds > 0 ? `Resend code in ${resendSeconds}s` : 'Need a new code?'}</p>
            <button type="button" disabled={resendSeconds > 0} onClick={handleResend} className="text-brand-400 hover:text-brand-300 disabled:text-slate-600">
              Resend OTP
            </button>
          </div>

          <div className="mt-8 text-center">
            <Link to="/login" className="inline-flex items-center gap-2 text-sm text-slate-400 hover:text-white">
              <ArrowLeftOnRectangleIcon className="w-4 h-4" />
              Back to login
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
