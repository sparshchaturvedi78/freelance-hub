import { DocumentIcon, ClockIcon, FolderIcon, ArrowTrendingUpIcon } from '@heroicons/react/24/outline'
import { formatCurrency } from '../../../lib/currency'

export default function DashboardPage() {
  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-white mb-2">Dashboard</h1>
        <p className="text-slate-400">Here's what's happening with your business today</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[
          { icon: DocumentIcon, label: 'Outstanding Invoices', value: formatCurrency(0), trend: '+0%' },
          { icon: ClockIcon, label: 'Hours This Month', value: '0h', trend: '+0h' },
          { icon: FolderIcon, label: 'Active Projects', value: '0', trend: '—' },
          { icon: ArrowTrendingUpIcon, label: 'Revenue', value: formatCurrency(0), trend: '+0%' },
        ].map((stat) => {
          const Icon = stat.icon
          return (
            <div key={stat.label} className="card p-6 hover:scale-105">
              <div className="flex items-start justify-between">
                <div>
                  <p className="text-slate-400 text-sm font-medium mb-2">{stat.label}</p>
                  <p className="text-3xl font-bold text-white">{stat.value}</p>
                </div>
                <div className="w-12 h-12 bg-slate-800 rounded-lg flex items-center justify-center">
                  <Icon className="w-6 h-6 text-brand-400" />
                </div>
              </div>
              <p className="text-xs text-emerald-400 mt-4">{stat.trend} from last month</p>
            </div>
          )
        })}
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Invoices */}
        <div className="card p-6">
          <h2 className="text-lg font-semibold text-white mb-4">Recent Invoices</h2>
          <div className="space-y-3">
            <p className="text-slate-400 text-sm text-center py-8">No invoices yet</p>
          </div>
        </div>

        {/* Recent Clients */}
        <div className="card p-6">
          <h2 className="text-lg font-semibold text-white mb-4">Recent Clients</h2>
          <div className="space-y-3">
            <p className="text-slate-400 text-sm text-center py-8">No clients added yet</p>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card p-6">
        <h2 className="text-lg font-semibold text-white mb-4">Quick Actions</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          <button className="btn-secondary py-2 px-4 text-sm font-medium">New Invoice</button>
          <button className="btn-secondary py-2 px-4 text-sm font-medium">Add Client</button>
          <button className="btn-secondary py-2 px-4 text-sm font-medium">Log Time</button>
          <button className="btn-secondary py-2 px-4 text-sm font-medium">View Reports</button>
        </div>
      </div>
    </div>
  )
}
