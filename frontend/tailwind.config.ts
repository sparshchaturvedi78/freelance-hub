import type { Config } from 'tailwindcss'
import plugin from 'tailwindcss/plugin'

export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#EFF6FF',
          100: '#DBEAFE',
          200: '#BFDBFE',
          300: '#93C5FD',
          400: '#60A5FA',
          500: '#3B82F6',
          600: '#2563EB',
          700: '#1D4ED8',
          800: '#1E40AF',
          900: '#1E3A8A',
        },
      },
      backgroundImage: {
        'gradient-brand': 'linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%)',
      },
      backdropBlur: {
        xs: '2px',
      },
      boxShadow: {
        'glow-blue': '0 0 20px rgba(59, 130, 246, 0.3)',
        'glow-purple': '0 0 20px rgba(139, 92, 246, 0.3)',
      },
      animation: {
        'pulse-slow': 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'fade-in': 'fadeIn 300ms ease-in-out',
        'slide-up': 'slideUp 300ms ease-out',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    plugin(function ({ addBase, addComponents, addUtilities }) {
      addBase({
        '@layer base': {
          'body': {
            '@apply bg-slate-950 text-slate-100': {},
          },
        },
      })
      addComponents({
        '.card': {
          '@apply bg-slate-900 border border-slate-800 rounded-lg shadow-lg transition-all duration-200 hover:shadow-glow-blue hover:border-slate-700': {},
        },
        '.card-light': {
          '@apply bg-white border border-slate-200 rounded-lg shadow-md transition-all duration-200': {},
        },
        '.btn': {
          '@apply inline-flex items-center justify-center font-medium rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-950 focus:ring-brand-500': {},
        },
        '.btn-primary': {
          '@apply btn bg-gradient-brand text-white hover:shadow-glow-blue': {},
        },
        '.btn-secondary': {
          '@apply btn bg-slate-800 text-slate-100 hover:bg-slate-700 border border-slate-700': {},
        },
        '.btn-ghost': {
          '@apply btn text-slate-400 hover:text-slate-100 hover:bg-slate-800': {},
        },
        '.input': {
          '@apply w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-lg text-slate-100 placeholder-slate-500 transition-colors duration-200 focus:outline-none focus:border-brand-500 focus:ring-1 focus:ring-brand-500': {},
        },
        '.label': {
          '@apply block text-sm font-medium text-slate-300 mb-1.5': {},
        },
      })
      addUtilities({
        '.backdrop-blur-xs': {
          backdropFilter: 'blur(2px)',
        },
      })
    }),
  ],
} satisfies Config
