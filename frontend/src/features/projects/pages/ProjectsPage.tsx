import { PlusIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline'
import { useState } from 'react'

export default function ProjectsPage() {
  const [searchQuery, setSearchQuery] = useState('')

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-bold text-white mb-2">Projects</h1>
          <p className="text-slate-400">Track and manage all your projects</p>
        </div>
        <button className="btn-primary py-2.5 px-6 font-semibold flex items-center gap-2">
          <PlusIcon className="w-5 h-5" />
          New Project
        </button>
      </div>

      {/* Search & Filter */}
      <div className="card p-6">
        <div className="relative">
          <MagnifyingGlassIcon className="absolute left-3 top-3 w-5 h-5 text-slate-500" />
          <input
            type="text"
            placeholder="Search projects by name..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="input pl-10 w-full"
          />
        </div>
      </div>

      {/* Projects Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div className="card p-6">
          <p className="text-slate-400 text-center py-12">No projects yet</p>
        </div>
      </div>
    </div>
  )
}
