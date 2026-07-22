import api from './api'
import type { User } from '../features/auth/authSlice'

interface AuthResponseData {
  accessToken: string
  refreshToken: string
  expiresInSeconds?: number
  user: User
}

interface ApiResponse<T = unknown> {
  success: boolean
  data: T
  error?: string
}

interface OtpResponse {
  verified: boolean
  message: string
}

const authService = {
  login: (email: string, password: string) =>
    api.post<ApiResponse<AuthResponseData>>('/auth/login', { email, password }),

  register: (payload: {
    fullName: string
    email: string
    organizationName?: string
    password: string
    confirmPassword: string
  }) => api.post<ApiResponse<OtpResponse>>('/auth/register', payload),

  resendVerificationOtp: (email: string) =>
    api.post<ApiResponse<OtpResponse>>('/auth/resend-otp', { email }),

  verifyEmailOtp: (email: string, otp: string) =>
    api.post<ApiResponse<AuthResponseData>>('/auth/verify-email-otp', { email, otp }),

  forgotPassword: (email: string) => api.post<ApiResponse<OtpResponse>>('/auth/forgot-password', { email }),

  verifyResetOtp: (email: string, otp: string) =>
    api.post<ApiResponse<OtpResponse>>('/auth/verify-reset-otp', { email, otp }),

  resetPassword: (payload: {
    email: string
    password: string
    confirmPassword: string
  }) => api.post<ApiResponse<OtpResponse>>('/auth/reset-password', payload),

  refreshToken: (refreshToken: string) =>
    api.post<ApiResponse<AuthResponseData>>('/auth/refresh', { refreshToken }),

  logout: (refreshToken: string) => api.post<ApiResponse<void>>('/auth/logout', { refreshToken }),
}

export type { AuthResponseData, OtpResponse }
export default authService
