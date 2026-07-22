import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate, Link } from 'react-router-dom'
import type { RootState } from '../app/store'
import { logout } from '../features/auth/authSlice'
import authService from '../lib/auth'
import ThemeToggle from '../components/ThemeToggle'
import { ArrowRightOnRectangleIcon, Bars3Icon, XMarkIcon, CogIcon, HomeIcon, UsersIcon, FolderIcon, BuildingOfficeIcon, ChartBarIcon } from '@heroicons/react/24/outline'

const mobileMenuItems = [
  { icon: HomeIcon, label: 'Dashboard', path: '/dashboard' },
  { icon: UsersIcon, label: 'Clients', path: '/clients' },
  { icon: FolderIcon, label: 'Projects', path: '/projects' },
  { icon: BuildingOfficeIcon, label: 'Organizations', path: '/organizations' },
  { icon: ChartBarIcon, label: 'Analytics', path: '/settings' },
]

export default function Topbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const user = useSelector((state: RootState) => state.auth.user)
  const refreshToken = useSelector((state: RootState) => state.auth.refreshToken)

  const handleLogout = async () => {
    try {
      if (refreshToken) {
        await authService.logout(refreshToken)
      }
    } catch {
      // ignore errors, still proceed with local logout
    } finally {
      dispatch(logout())
      navigate('/login')
    }
  }

  return (
    <header className="relative h-16 bg-slate-900/95 border-b border-slate-800 flex items-center justify-between px-6 md:px-8 backdrop-blur-sm">
      <div className="space-y-1">
        <p className="text-sm font-medium text-slate-400">Good to see you,</p>
        <h2 className="text-xl font-semibold text-white">{user?.fullName || 'Freelancer'}</h2>
      </div>

      <div className="flex items-center gap-3">
        <ThemeToggle />

        <div className="hidden md:flex items-center gap-3 rounded-2xl border border-slate-800 bg-slate-950 px-3 py-2">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-brand text-white font-semibold">
            {user?.fullName?.charAt(0) || user?.email?.charAt(0)}
          </div>
          <div className="text-right">
            <p className="text-sm font-semibold text-white">{user?.fullName || user?.email}</p>
            <p className="text-xs text-slate-500">{user?.role || 'Independent Freelancer'}</p>
          </div>
        </div>

        <button
          type="button"
          onClick={() => setIsMenuOpen((current) => !current)}
          className="inline-flex items-center justify-center rounded-full bg-slate-800 p-2 text-slate-200 hover:bg-slate-700 transition-colors duration-200 md:hidden"
          aria-label="Open navigation menu"
        >
          {isMenuOpen ? <XMarkIcon className="w-5 h-5" /> : <Bars3Icon className="w-5 h-5" />}
        </button>

        <button
          onClick={handleLogout}
          className="hidden md:inline-flex items-center gap-2 rounded-full bg-slate-800 px-3 py-2 text-sm font-medium text-slate-200 hover:bg-slate-700 transition-colors duration-200"
        >
          <ArrowRightOnRectangleIcon className="w-4 h-4" />
          Logout
        </button>
      </div>

      {isMenuOpen ? (
        <div className="md:hidden absolute inset-x-0 top-full z-30 border-b border-slate-800 bg-slate-950 px-4 py-4 shadow-2xl">
          <nav className="space-y-2">
            {mobileMenuItems.map((item) => {
              const Icon = item.icon
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setIsMenuOpen(false)}
                  className="flex items-center gap-3 rounded-2xl border border-slate-800 bg-slate-900 px-4 py-3 text-slate-100 hover:bg-slate-800 hover:text-white transition-colors duration-200"
                >
                  <Icon className="w-5 h-5" />
                  <span className="text-sm font-medium">{item.label}</span>
                </Link>
              )
            })}
          </nav>

          <div className="mt-4 border-t border-slate-800 pt-4">
            <button
              type="button"
              onClick={handleLogout}
              className="inline-flex w-full items-center justify-center gap-2 rounded-2xl border border-slate-800 bg-slate-900 px-4 py-3 text-sm font-medium text-slate-100 hover:bg-slate-800 hover:text-white transition-colors duration-200"
            >
              <ArrowRightOnRectangleIcon className="w-5 h-5" />
              Logout
            </button>
          </div>
        </div>
      ) : null}
    </header>
  )
}
