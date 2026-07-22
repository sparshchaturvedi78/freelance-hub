import { MoonIcon, SunIcon } from '@heroicons/react/24/solid'
import { useTheme } from '../hooks/useTheme'

export default function ThemeToggle() {
  const { theme, toggleTheme } = useTheme()

  return (
    <button
      type="button"
      onClick={toggleTheme}
      className="inline-flex items-center gap-2 rounded-full border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-200 hover:border-slate-500 hover:bg-slate-800 transition-colors duration-200"
    >
      {theme === 'dark' ? (
        <SunIcon className="w-4 h-4 text-amber-300" />
      ) : (
        <MoonIcon className="w-4 h-4 text-slate-900" />
      )}
      <span>{theme === 'dark' ? 'Light' : 'Dark'}</span>
    </button>
  )
}
