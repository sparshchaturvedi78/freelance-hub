import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'react-toastify'
import { saveSignupData } from '../../../lib/authFlow'
import authService from '../../../lib/auth'
import PasswordInput from '../../../components/PasswordInput'
import {
  UserIcon,
  EnvelopeIcon,
  LockClosedIcon,
  BuildingOfficeIcon,
  SparklesIcon,
} from '@heroicons/react/24/outline'

const registerSchema = z.object({
  fullName: z.string().min(2, 'Name must be at least 2 characters'),
  email: z.string().email('Invalid email address'),
  organizationName: z
    .string()
    .optional()
    .refine((value) => value === undefined || value.trim().length === 0 || value.trim().length >= 2, {
      message: 'Organization name must be at least 2 characters',
    }),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
})

type RegisterFormData = z.infer<typeof registerSchema>

export default function RegisterPage() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  })

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true)
    try {
      const payload = {
        fullName: data.fullName,
        email: data.email,
        organizationName: data.organizationName?.trim() || undefined,
        password: data.password,
        confirmPassword: data.confirmPassword,
      }
      await authService.register(payload)
      saveSignupData(payload)
      toast.success('Verification code sent to your email')
      navigate('/verify-email')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'Registration failed')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-10">
      <div className="relative w-full max-w-6xl">
        <div className="grid gap-8 rounded-[2rem] border border-slate-800 bg-slate-950/90 p-6 shadow-[0_30px_80px_rgba(15,23,42,.55)] backdrop-blur-xl md:grid-cols-[1.2fr_1fr]">
          <div className="relative overflow-hidden rounded-[1.75rem] bg-gradient-to-b from-slate-950 via-brand-700 to-violet-900 p-10 text-white shadow-inner shadow-slate-950/30">
            <div className="absolute -right-24 top-8 h-48 w-48 rounded-full bg-white/10 blur-3xl" />
            <div className="absolute -left-20 bottom-8 h-56 w-56 rounded-full bg-sky-500/10 blur-3xl" />
            <div className="relative z-10 space-y-8">
              <div className="inline-flex items-center gap-3 rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80">
                <SparklesIcon className="h-5 w-5 text-white" />
                Join FreelanceHub
              </div>
              <div className="space-y-6">
                <h1 className="text-4xl font-semibold leading-tight">Create your freelance workspace with ease.</h1>
                <p className="max-w-md text-slate-200/90">Launch invoices, clients, and project workflows from one polished dashboard designed for modern freelancers.</p>
              </div>
              <div className="space-y-4">
                <div className="flex items-center gap-3 rounded-3xl bg-white/5 p-4">
                  <span className="inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-white/10 text-white">✓</span>
                  <div>
                    <p className="text-sm uppercase tracking-[0.2em] text-slate-200/70">Organize clients</p>
                    <p className="text-sm text-slate-200/90">Keep contact, project and invoice history in one place.</p>
                  </div>
                </div>
                <div className="flex items-center gap-3 rounded-3xl bg-white/5 p-4">
                  <span className="inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-white/10 text-white">✓</span>
                  <div>
                    <p className="text-sm uppercase tracking-[0.2em] text-slate-200/70">Stay secure</p>
                    <p className="text-sm text-slate-200/90">Verified registration and password recovery flows keep your account safe.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="relative rounded-[1.75rem] bg-slate-950 p-8 sm:p-10">
            <div className="absolute inset-x-0 top-0 h-1 rounded-t-[1.5rem] bg-gradient-to-r from-brand-400 via-violet-500 to-sky-400" />
            <div className="relative z-10 space-y-6">
              <div className="mb-4 space-y-2 text-center">
                <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Create account</p>
                <h2 className="text-3xl font-semibold text-white">Start your FreelanceHub journey.</h2>
                <p className="text-sm text-slate-400">One account, instant access to projects, clients, and invoices.</p>
              </div>

              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <div>
                  <label className="label">Full Name</label>
                  <div className="relative">
                    <UserIcon className="pointer-events-none absolute left-3 top-3 h-5 w-5 text-slate-500" />
                    <input {...register('fullName')} type="text" placeholder="Your name" className="input pl-10" />
                  </div>
                  {errors.fullName && <p className="text-red-400 text-sm mt-1">{errors.fullName.message}</p>}
                </div>

                <div>
                  <label className="label">Email Address</label>
                  <div className="relative">
                    <EnvelopeIcon className="pointer-events-none absolute left-3 top-3 h-5 w-5 text-slate-500" />
                    <input {...register('email')} type="email" placeholder="you@example.com" className="input pl-10" />
                  </div>
                  {errors.email && <p className="text-red-400 text-sm mt-1">{errors.email.message}</p>}
                </div>

                <div>
                  <label className="label">Organization Name (optional)</label>
                  <div className="relative">
                    <BuildingOfficeIcon className="pointer-events-none absolute left-3 top-3 h-5 w-5 text-slate-500" />
                    <input {...register('organizationName')} type="text" placeholder="Your company" className="input pl-10" />
                  </div>
                  {errors.organizationName && <p className="text-red-400 text-sm mt-1">{errors.organizationName.message}</p>}
                </div>

                <div>
                  <PasswordInput label="Password" placeholder="••••••••" error={errors.password?.message} register={register('password')} />
                </div>

                <div>
                  <PasswordInput label="Confirm Password" placeholder="••••••••" error={errors.confirmPassword?.message} register={register('confirmPassword')} />
                </div>

                <button type="submit" disabled={isLoading} className="btn-primary w-full py-3 font-semibold">
                  {isLoading ? 'Creating account...' : 'Create account'}
                </button>
              </form>

              <div className="flex flex-col gap-3 pt-3 text-sm sm:flex-row sm:items-center sm:justify-between">
                <p className="text-slate-400">Already have an account?</p>
                <Link to="/login" className="text-brand-300 hover:text-white">
                  Sign in
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
