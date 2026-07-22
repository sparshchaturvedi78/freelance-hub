import { configureStore } from '@reduxjs/toolkit'
import authReducer, { initialAuthState } from '../features/auth/authSlice'
import { loadAuthState, saveAuthState, clearAuthState } from '../lib/authStorage'

const persistedAuth = loadAuthState()

const store = configureStore({
  reducer: {
    auth: authReducer,
  },
  preloadedState: persistedAuth
    ? {
        auth: {
          ...initialAuthState,
          user: persistedAuth.user,
          accessToken: persistedAuth.accessToken,
          refreshToken: persistedAuth.refreshToken,
          isAuthenticated: true,
        },
      }
    : undefined,
})

store.subscribe(() => {
  const state = store.getState().auth
  if (state.isAuthenticated && state.user && state.accessToken && state.refreshToken) {
    saveAuthState({
      user: state.user,
      accessToken: state.accessToken,
      refreshToken: state.refreshToken,
    })
  } else {
    clearAuthState()
  }
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

export default store
