import { useParams } from 'react-router-dom'
import { ArrowLeftIcon } from '@heroicons/react/24/outline'

export default function ClientDetailPage() {
  const { id } = useParams()

  return (
    <div className="space-y-8 animate-fade-in">
      <button className="text-slate-400 hover:text-slate-200 flex items-center gap-2 transition-colors">
        <ArrowLeftIcon className="w-5 h-5" />
        Back to Clients
      </button>

      <div>
        <h1 className="text-4xl font-bold text-white mb-2">Client Details</h1>
        <p className="text-slate-400">Client ID: {id}</p>
      </div>

      <div className="card p-6">
        <p className="text-slate-400">Loading client details...</p>
      </div>
    </div>
  )
}
