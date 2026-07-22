import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'react-toastify'
import authService from '../../../lib/auth'
import { saveResetPasswordEmail } from '../../../lib/authFlow'
import { EnvelopeIcon, ArrowLeftOnRectangleIcon } from '@heroicons/react/24/outline'

const forgotSchema = z.object({
  email: z.string().email('Invalid email address'),
})

type ForgotFormData = z.infer<typeof forgotSchema>

export default function ForgotPasswordPage() {
  const [isLoading, setIsLoading] = useState(false)
  const [emailSent, setEmailSent] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm<ForgotFormData>({
    resolver: zodResolver(forgotSchema),
  })

  const navigate = useNavigate()

  const onSubmit = async (data: ForgotFormData) => {
    setIsLoading(true)
    try {
      await authService.forgotPassword(data.email)
      saveResetPasswordEmail(data.email)
      setEmailSent(true)
      toast.success('Reset OTP sent to your email')
      navigate('/verify-reset-otp')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'Failed to send reset code')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-10">
      <div className="relative w-full max-w-md">
        <div className="rounded-3xl border border-slate-800 bg-slate-950/95 p-10 shadow-2xl backdrop-blur-xl">
          <div className="mb-8 text-center">
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-3xl bg-gradient-to-br from-brand-500 to-violet-500 text-white shadow-xl">
              <EnvelopeIcon className="w-8 h-8" />
            </div>
            <h1 className="text-3xl font-semibold text-white">Forgot password?</h1>
            <p className="mt-2 text-slate-400">Enter your email to receive a password reset code.</p>
          </div>

          {emailSent ? (
            <div className="space-y-4 rounded-3xl border border-slate-800 bg-slate-900/80 p-6">
              <p className="text-slate-100 font-semibold">Check your email</p>
              <p className="text-slate-400">We’ve sent a 6-digit OTP to your inbox. Use it to reset your password.</p>
              <Link to="/verify-reset-otp" className="btn-primary w-full py-3 text-center font-semibold">
                Verify OTP
              </Link>
            </div>
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
              <div>
                <label className="label">Email Address</label>
                <input
                  {...register('email')}
                  type="email"
                  placeholder="you@example.com"
                  className="input"
                />
                {errors.email && <p className="text-red-400 text-sm mt-1">{errors.email.message}</p>}
              </div>

              <button type="submit" disabled={isLoading} className="btn-primary w-full py-3 font-semibold">
                {isLoading ? 'Sending...' : 'Send Reset Code'}
              </button>
            </form>
          )}

          <div className="mt-6 text-center">
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
