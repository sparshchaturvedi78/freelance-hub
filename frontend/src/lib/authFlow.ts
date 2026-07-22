const VERIFY_EMAIL_EMAIL_KEY = 'freelancehub_verify_email_email'
const RESET_PASSWORD_EMAIL_KEY = 'freelancehub_reset_password_email'
const SIGNUP_DATA_KEY = 'freelancehub_signup_data'

export interface SignupData {
  fullName: string
  email: string
  organizationName?: string
  password: string
  confirmPassword: string
}

export function saveVerifyEmailEmail(email: string) {
  window.sessionStorage.setItem(VERIFY_EMAIL_EMAIL_KEY, email)
}

export function loadVerifyEmailEmail() {
  return window.sessionStorage.getItem(VERIFY_EMAIL_EMAIL_KEY)
}

export function clearVerifyEmailEmail() {
  window.sessionStorage.removeItem(VERIFY_EMAIL_EMAIL_KEY)
}

export function saveResetPasswordEmail(email: string) {
  window.sessionStorage.setItem(RESET_PASSWORD_EMAIL_KEY, email)
}

export function loadResetPasswordEmail() {
  return window.sessionStorage.getItem(RESET_PASSWORD_EMAIL_KEY)
}

export function clearResetPasswordEmail() {
  window.sessionStorage.removeItem(RESET_PASSWORD_EMAIL_KEY)
}

export function saveSignupData(data: SignupData) {
  window.sessionStorage.setItem(SIGNUP_DATA_KEY, JSON.stringify(data))
}

export function loadSignupData(): SignupData | null {
  const raw = window.sessionStorage.getItem(SIGNUP_DATA_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as SignupData
  } catch {
    return null
  }
}

export function clearSignupData() {
  window.sessionStorage.removeItem(SIGNUP_DATA_KEY)
}
