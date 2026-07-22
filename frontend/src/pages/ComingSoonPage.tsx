import { SparklesIcon } from '@heroicons/react/24/outline'

export default function ComingSoonPage({ title = 'Coming Soon', description = 'This area is being built to support your freelance workflow.' }: { title?: string; description?: string }) {
  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-10">
      <div className="card max-w-xl p-10 text-center">
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-500 to-violet-500 text-white shadow-glow-blue">
          <SparklesIcon className="w-8 h-8" />
        </div>
        <h1 className="text-3xl font-bold text-white mb-4">{title}</h1>
        <p className="text-slate-400 max-w-2xl mx-auto">{description}</p>
      </div>
    </div>
  )
}
