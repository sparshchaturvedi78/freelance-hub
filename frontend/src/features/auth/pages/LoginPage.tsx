import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'react-toastify'
import { setAuth } from '../authSlice'
import authService from '../../../lib/auth'
import { EnvelopeIcon, LockClosedIcon, SparklesIcon, ArrowRightOnRectangleIcon } from '@heroicons/react/24/outline'

const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
})

type LoginFormData = z.infer<typeof loginSchema>

export default function LoginPage() {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const [isLoading, setIsLoading] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true)
    try {
      const response = await authService.login(data.email, data.password)
      const { accessToken, refreshToken, user } = response.data.data
      dispatch(setAuth({ user, accessToken, refreshToken }))
      toast.success('Welcome back!')
      navigate('/dashboard')
    } catch (error: any) {
      toast.error(error.response?.data?.error || 'Login failed')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-10">
      <div className="relative w-full max-w-6xl">
        <div className="grid gap-8 rounded-[2rem] border border-slate-800 bg-slate-950/90 p-6 shadow-[0_30px_80px_rgba(15,23,42,.55)] backdrop-blur-xl md:grid-cols-[1.2fr_1fr]">
          <div className="relative overflow-hidden rounded-[1.75rem] bg-gradient-to-b from-brand-600 via-slate-900 to-slate-950 p-10 text-white shadow-inner shadow-slate-950/30">
            <div className="absolute -right-24 top-8 h-48 w-48 rounded-full bg-white/10 blur-3xl" />
            <div className="absolute -left-20 bottom-8 h-56 w-56 rounded-full bg-violet-500/10 blur-3xl" />
            <div className="relative z-10 space-y-8">
              <div className="inline-flex items-center gap-3 rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80">
                <SparklesIcon className="h-5 w-5 text-white" />
                FreelanceHub for freelancers
              </div>
              <div className="space-y-6">
                <h1 className="text-4xl font-semibold leading-tight">Log in and manage your clients, invoices, and time with clarity.</h1>
                <p className="max-w-md text-slate-200/90">Powerful freelance management tools in a clean dashboard. Secure authentication, team-ready workspaces, and fast workflows.</p>
              </div>
              <div className="space-y-4">
                <div className="flex items-center gap-3 rounded-3xl bg-white/5 p-4">
                  <span className="inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-white/10 text-white">
                    1
                  </span>
                  <div>
                    <p className="text-sm uppercase tracking-[0.2em] text-slate-200/70">Secure login</p>
                    <p className="text-sm text-slate-200/90">Sign in once and stay signed in across sessions.</p>
                  </div>
                </div>
                <div className="flex items-center gap-3 rounded-3xl bg-white/5 p-4">
                  <span className="inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-white/10 text-white">
                    2
                  </span>
                  <div>
                    <p className="text-sm uppercase tracking-[0.2em] text-slate-200/70">Fast access</p>
                    <p className="text-sm text-slate-200/90">Quickly resume your freelance workflows and stay on schedule.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="relative rounded-[1.75rem] bg-slate-950 p-8 sm:p-10">
            <div className="absolute inset-x-0 top-0 h-1 rounded-t-[1.5rem] bg-gradient-to-r from-brand-400 via-violet-500 to-sky-400" />
            <div className="relative z-10 space-y-6">
              <div className="mb-4 space-y-2 text-center">
                <p className="text-sm uppercase tracking-[0.25em] text-slate-500">Sign in</p>
                <h2 className="text-3xl font-semibold text-white">Welcome back</h2>
                <p className="text-sm text-slate-400">Use your FreelanceHub account to continue.</p>
              </div>

              <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
                <div>
                  <label className="label">Email Address</label>
                  <div className="relative">
                    <EnvelopeIcon className="pointer-events-none absolute left-3 top-3 h-5 w-5 text-slate-500" />
                    <input
                      {...register('email')}
                      type="email"
                      placeholder="you@example.com"
                      className="input pl-10"
                    />
                  </div>
                  {errors.email && <p className="text-red-400 text-sm mt-1">{errors.email.message}</p>}
                </div>

                <div>
                  <label className="label">Password</label>
                  <div className="relative">
                    <LockClosedIcon className="pointer-events-none absolute left-3 top-3 h-5 w-5 text-slate-500" />
                    <input
                      {...register('password')}
                      type="password"
                      placeholder="••••••••"
                      className="input pl-10"
                    />
                  </div>
                  {errors.password && <p className="text-red-400 text-sm mt-1">{errors.password.message}</p>}
                </div>

                <button type="submit" disabled={isLoading} className="btn-primary w-full py-3 font-semibold">
                  {isLoading ? 'Signing in...' : 'Sign in'}
                </button>
              </form>

              <div className="flex flex-col gap-4 pt-3 text-sm sm:flex-row sm:items-center sm:justify-between">
                <Link to="/forgot-password" className="text-slate-400 hover:text-white">
                  Forgot password?
                </Link>
                <Link to="/register" className="inline-flex items-center gap-2 text-brand-300 hover:text-white">
                  Create account <ArrowRightOnRectangleIcon className="h-4 w-4" />
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
