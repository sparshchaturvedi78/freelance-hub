import { useEffect, useState } from 'react'

const THEME_STORAGE_KEY = 'freelancehub_theme'

export type ThemeMode = 'light' | 'dark'

export function useTheme() {
  const [theme, setTheme] = useState<ThemeMode>('dark')

  useEffect(() => {
    const saved = window.localStorage.getItem(THEME_STORAGE_KEY) as ThemeMode | null
    const preferred = saved ?? (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light')
    setTheme(preferred)
  }, [])

  useEffect(() => {
    const root = document.documentElement
    root.classList.toggle('dark', theme === 'dark')
    root.classList.toggle('light', theme === 'light')
    window.localStorage.setItem(THEME_STORAGE_KEY, theme)
  }, [theme])

  const toggleTheme = () => {
    setTheme((current) => (current === 'dark' ? 'light' : 'dark'))
  }

  return { theme, toggleTheme }
}
