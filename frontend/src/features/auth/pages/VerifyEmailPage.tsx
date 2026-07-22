import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Controller, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'react-toastify'
import { useDispatch } from 'react-redux'
import { setAuth } from '../authSlice'
import authService from '../../../lib/auth'
import { loadSignupData, saveVerifyEmailEmail, clearSignupData } from '../../../lib/authFlow'
import { ShieldCheckIcon, ArrowLeftOnRectangleIcon } from '@heroicons/react/24/outline'
import OtpInput from '../../../components/OtpInput'

const verifySchema = z.object({
  otp: z.string().length(6, 'Enter the 6-digit code'),
})

type VerifyFormData = z.infer<typeof verifySchema>

export default function VerifyEmailPage() {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const [isLoading, setIsLoading] = useState(false)
  const [resendSeconds, setResendSeconds] = useState(45)
  const signupData = loadSignupData()
  const email = signupData?.email || ''

  const { control, handleSubmit, watch, formState: { errors, isValid } } = useForm<VerifyFormData>({
    resolver: zodResolver(verifySchema),
    defaultValues: { otp: '' },
    mode: 'onChange',
  })

  useEffect(() => {
    if (!signupData) {
      navigate('/register')
    }
  }, [navigate, signupData])

  useEffect(() => {
    if (!email) return
    saveVerifyEmailEmail(email)
  }, [email])

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
      await authService.resendVerificationOtp(email)
      toast.success('Verification code resent successfully')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'Failed to resend OTP')
    }
  }

  const onSubmit = async (data: VerifyFormData) => {
    if (!email) return
    setIsLoading(true)
    try {
      const response = await authService.verifyEmailOtp(email, data.otp)
      const { accessToken, refreshToken, user } = response.data.data
      dispatch(setAuth({ user, accessToken, refreshToken }))
      clearSignupData()
      toast.success('Email verified successfully')
      navigate('/dashboard')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'Verification failed')
    } finally {
      setIsLoading(false)
    }
  }

  const otpValue = watch('otp')

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-10">
      <div className="relative w-full max-w-xl">
        <div className="absolute inset-x-0 top-0 h-40 bg-gradient-to-r from-brand-500 to-violet-500 blur-3xl opacity-40"></div>
        <div className="relative overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/95 p-10 shadow-2xl backdrop-blur-xl">
          <div className="mb-8 text-center">
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-3xl bg-gradient-to-br from-brand-500 to-violet-500 text-white shadow-xl">
              <ShieldCheckIcon className="w-8 h-8" />
            </div>
            <h1 className="text-3xl font-semibold text-white">Verify your email</h1>
            <p className="mt-2 text-slate-400">Enter the 6-digit code sent to {email}</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <Controller
              name="otp"
              control={control}
              render={({ field }) => (
                <OtpInput value={field.value} onChange={field.onChange} error={errors.otp?.message} />
              )}
            />

            <button type="submit" disabled={isLoading || otpValue.length < 6} className="btn-primary w-full py-3 font-semibold">
              {isLoading ? 'Verifying...' : 'Verify Email'}
            </button>
          </form>

          <div className="mt-6 flex items-center justify-between text-sm text-slate-400">
            <p>{resendSeconds > 0 ? `Resend code in ${resendSeconds}s` : 'Didn’t receive a code?'}</p>
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
