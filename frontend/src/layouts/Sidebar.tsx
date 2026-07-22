import { Link, useLocation } from 'react-router-dom'
import {
  HomeIcon,
  UsersIcon,
  FolderIcon,
  BuildingOfficeIcon,
  ChartBarIcon,
  CogIcon,
} from '@heroicons/react/24/outline'
import clsx from 'clsx'

const menuItems = [
  { icon: HomeIcon, label: 'Dashboard', path: '/dashboard' },
  { icon: UsersIcon, label: 'Clients', path: '/clients' },
  { icon: FolderIcon, label: 'Projects', path: '/projects' },
  { icon: BuildingOfficeIcon, label: 'Organizations', path: '/organizations' },
  { icon: ChartBarIcon, label: 'Analytics', path: '/settings' },
]

export default function Sidebar() {
  const location = useLocation()

  const isActive = (path: string) => location.pathname === path || location.pathname.startsWith(path + '/')

  return (
    <aside className="w-72 hidden md:flex flex-col bg-slate-950 border-r border-slate-800">
      <div className="p-6 border-b border-slate-800">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-3xl bg-gradient-to-br from-brand-500 to-violet-500 shadow-glow-purple flex items-center justify-center text-white text-lg font-bold">
            FH
          </div>
          <div>
            <h1 className="text-lg font-semibold text-white">FreelanceHub</h1>
            <p className="text-xs text-slate-400">Premium freelancing workspace</p>
          </div>
        </div>
      </div>

      <nav className="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
        <p className="text-xs font-semibold text-slate-500 uppercase tracking-[0.2em] mb-4">Navigation</p>
        {menuItems.map((item) => {
          const Icon = item.icon
          const active = isActive(item.path)
          return (
            <Link
              key={item.path}
              to={item.path}
              className={clsx(
                'flex items-center gap-3 px-4 py-3 rounded-2xl transition-all duration-200',
                active
                  ? 'bg-slate-800 text-brand-400 shadow-glow-blue border-l-2 border-brand-400'
                  : 'text-slate-300 hover:text-white hover:bg-slate-800'
              )}
            >
              <Icon className="w-5 h-5" />
              <span className="text-sm font-medium">{item.label}</span>
            </Link>
          )
        })}
      </nav>

      <div className="border-t border-slate-800 p-4">
        <Link
          to="/settings"
          className="flex items-center gap-3 rounded-2xl border border-slate-800 bg-slate-900 px-4 py-3 text-slate-300 hover:text-white hover:border-slate-700 transition-colors duration-200"
        >
          <CogIcon className="w-5 h-5" />
          <span className="text-sm font-medium">Settings</span>
        </Link>
      </div>
    </aside>
  )
}
