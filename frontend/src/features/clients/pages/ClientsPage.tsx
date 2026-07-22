import { PlusIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline'
import { useState } from 'react'

export default function ClientsPage() {
  const [searchQuery, setSearchQuery] = useState('')

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-bold text-white mb-2">Clients</h1>
          <p className="text-slate-400">Manage your clients and their projects</p>
        </div>
        <button className="btn-primary py-2.5 px-6 font-semibold flex items-center gap-2">
          <PlusIcon className="w-5 h-5" />
          New Client
        </button>
      </div>

      {/* Search & Filter */}
      <div className="card p-6">
        <div className="relative">
          <MagnifyingGlassIcon className="absolute left-3 top-3 w-5 h-5 text-slate-500" />
          <input
            type="text"
            placeholder="Search clients by name or email..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="input pl-10 w-full"
          />
        </div>
      </div>

      {/* Clients List */}
      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-slate-800">
              <tr className="border-b border-slate-700">
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">Name</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">Email</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">Projects</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">Status</th>
                <th className="px-6 py-3 text-right text-xs font-semibold text-slate-300 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700">
              <tr className="hover:bg-slate-800/50 transition-colors">
                <td colSpan={5} className="px-6 py-12 text-center text-slate-400">
                  <p className="text-sm">No clients yet</p>
                  <p className="text-xs mt-1">Click "New Client" to get started</p>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
