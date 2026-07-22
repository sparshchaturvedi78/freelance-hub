import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'react-toastify'
import authService from '../../../lib/auth'
import { loadResetPasswordEmail, clearResetPasswordEmail } from '../../../lib/authFlow'
import PasswordInput from '../../../components/PasswordInput'
import { CheckCircleIcon, ArrowLeftOnRectangleIcon } from '@heroicons/react/24/outline'

const passwordSchema = z.object({
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
})

type PasswordFormData = z.infer<typeof passwordSchema>

export default function ResetPasswordPage() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const email = loadResetPasswordEmail() || ''

  useEffect(() => {
    if (!email) {
      navigate('/forgot-password')
    }
  }, [email, navigate])

  const { register, handleSubmit, formState: { errors } } = useForm<PasswordFormData>({
    resolver: zodResolver(passwordSchema),
  })

  const onSubmit = async (data: PasswordFormData) => {
    setIsLoading(true)
    try {
      await authService.resetPassword({
        email,
        password: data.password,
        confirmPassword: data.confirmPassword,
      })
      clearResetPasswordEmail()
      toast.success('Password reset successfully')
      navigate('/login')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'Password reset failed')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-10">
      <div className="relative w-full max-w-lg">
        <div className="rounded-[2rem] border border-slate-800 bg-slate-950/95 p-10 shadow-[0_30px_80px_rgba(15,23,42,.55)] backdrop-blur-xl">
          <div className="mb-8 text-center">
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-3xl bg-gradient-to-br from-brand-500 to-violet-500 text-white shadow-xl">
              <CheckCircleIcon className="w-8 h-8" />
            </div>
            <h1 className="text-3xl font-semibold text-white">Choose a new password</h1>
            <p className="mt-2 text-slate-400">Reset your password for {email}</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <PasswordInput label="New Password" placeholder="••••••••" error={errors.password?.message} register={register('password')} />
            <PasswordInput label="Confirm Password" placeholder="••••••••" error={errors.confirmPassword?.message} register={register('confirmPassword')} />
            <button type="submit" disabled={isLoading} className="btn-primary w-full py-3 font-semibold">
              {isLoading ? 'Resetting...' : 'Reset Password'}
            </button>
          </form>

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
