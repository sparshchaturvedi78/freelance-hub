# FreelanceHub Frontend Development Guide

## Project Setup

### Install Dependencies
```bash
cd frontend
npm install
```

### Environment Variables
Create a `.env` file in the frontend directory:
```env
VITE_API_URL=http://localhost:8081/api
```

### Start Development Server
```bash
npm run dev
```
The app will be available at `http://localhost:5173`

## Project Structure

```
src/
├── app/                      # Redux store configuration
│   └── store.ts             # Redux store setup
├── features/                 # Feature modules (by feature)
│   ├── auth/                 # Authentication
│   │   ├── pages/           # Auth pages (Login, Register)
│   │   ├── authSlice.ts     # Redux auth slice
│   │   └── hooks/           # Auth hooks (if any)
│   ├── dashboard/            # Dashboard
│   ├── clients/              # Client management
│   ├── projects/             # Project management
│   └── ...                   # Other features
├── layouts/                  # Layout components
│   ├── DashboardLayout.tsx   # Main dashboard layout
│   ├── Sidebar.tsx           # Sidebar navigation
│   └── Topbar.tsx            # Top navigation bar
├── components/               # Reusable UI components
│   ├── ProtectedRoute.tsx    # Protected route wrapper
│   └── ...                   # Other shared components
├── lib/                      # Utilities and configuration
│   ├── api.ts               # Axios API client with interceptors
│   └── queryClient.ts       # React Query configuration
├── types/                    # Shared TypeScript types
├── App.tsx                   # Root app component with routing
├── main.tsx                  # Entry point
└── index.css                 # Global Tailwind CSS

```

## Technology Stack

### Core
- **React 19** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **React Router v6** - Client-side routing

### State Management
- **Redux Toolkit** - Global state (auth, org context)
- **TanStack React Query** - Server state (API data, caching)

### Forms & Validation
- **React Hook Form** - Form state management
- **Zod** - Schema validation

### Styling
- **Tailwind CSS** - Utility-first CSS framework
- **clsx** - Conditional class names

### UI & Icons
- **Heroicons** - Icon library
- **React Toastify** - Notifications

### HTTP
- **Axios** - HTTP client
- Automatic token refresh via interceptors

## Design System

The design system is defined in `DESIGN_SYSTEM.md`. Key features:
- **Dark mode first** with light mode support
- **Blue gradient** primary brand color
- **Glassmorphism** effects on cards
- **Smooth animations** (200ms default)
- **Accessibility** with proper contrast and keyboard navigation

### Color Scheme
```css
/* Primary */
--brand: #3B82F6
--brand-gradient: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%)

/* Dark Mode */
--bg-dark: #0F172A
--bg-secondary: #1E293B
--text-dark: #F1F5F9
```

## Key Components

### Auth Slice (Redux)
Manages authentication state:
- `user` - Current user object
- `accessToken` - JWT access token
- `refreshToken` - JWT refresh token
- `isAuthenticated` - Auth status
- `loading` - Loading state

### API Client
- Interceptors for token injection
- Automatic 401 handling with token refresh
- Centralized error handling

### Protected Routes
Routes wrapped with `<ProtectedRoute>` check auth before rendering.

## Development Workflow

### Adding a New Feature
1. Create feature folder in `src/features/`
2. Add sub-folders: `pages/`, `components/`, `hooks/`
3. Create pages using React Router
4. Use React Query hooks for API calls
5. Export from `index.ts` if needed

### Adding a New API Endpoint
1. Import `api` from `lib/api`
2. Create React Query hooks in feature folder:
   ```typescript
   export function useClients() {
     return useQuery({
       queryKey: ['clients'],
       queryFn: () => api.get('/clients').then(res => res.data.data),
     })
   }
   ```

### Styling Components
- Use Tailwind classes directly in JSX
- Follow design system colors and spacing
- Use predefined component classes (`.btn-primary`, `.card`, etc.)

## Building for Production

```bash
npm run build
```

Output will be in `dist/` folder.

## Common Issues & Solutions

### CORS Errors
Ensure backend is running with CORS enabled for `http://localhost:5173` (dev) or production domain.

### Token Expiry
The API client automatically handles 401 responses by attempting a token refresh. If refresh fails, user is logged out.

### API URL
Ensure `VITE_API_URL` environment variable points to correct backend URL.

## Next Steps

- Implement React Query hooks for all API endpoints
- Add form pages for creating/editing clients and projects
- Build invoice generation UI
- Add data visualization/charts for dashboard
- Implement time entry logging UI
- Add PDF export for invoices

---

**Last Updated**: 2026-07-22
