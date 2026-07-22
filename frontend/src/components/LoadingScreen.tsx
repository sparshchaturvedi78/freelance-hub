export default function LoadingScreen({ message = 'Loading...' }: { message?: string }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-950 px-4">
      <div className="rounded-3xl bg-slate-900/90 border border-slate-800 px-8 py-10 text-center shadow-xl">
        <div className="mb-4 h-16 w-16 rounded-full border-4 border-t-brand-400 border-slate-800 animate-spin mx-auto"></div>
        <p className="text-slate-200 text-lg font-semibold">{message}</p>
      </div>
    </div>
  )
}
