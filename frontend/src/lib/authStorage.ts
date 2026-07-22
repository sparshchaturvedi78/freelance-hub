import type { User } from '../features/auth/authSlice'

const AUTH_STORAGE_KEY = 'freelancehub_auth'

export interface PersistedAuthState {
  user: User
  accessToken: string
  refreshToken: string
}

export function loadAuthState(): PersistedAuthState | null {
  try {
    const serialized = window.localStorage.getItem(AUTH_STORAGE_KEY)
    if (!serialized) return null
    return JSON.parse(serialized) as PersistedAuthState
  } catch {
    return null
  }
}

export function saveAuthState(state: PersistedAuthState): void {
  try {
    window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(state))
  } catch {
    // ignore storage errors
  }
}

export function clearAuthState(): void {
  try {
    window.localStorage.removeItem(AUTH_STORAGE_KEY)
  } catch {
    // ignore storage errors
  }
}
