# FreelanceHub — Frontend Brief

> Read `PROJECT_OVERVIEW.md` first for business context. This document is the frontend-specific spec. Working directory: `frontend/`.

## Stack

- React 18 + TypeScript, built with Vite
- React Router for routing
- Redux Toolkit — only for genuinely global/cross-cutting state (auth session, current organization). Don't put everything in Redux.
- TanStack Query (React Query) for all server data fetching/caching — this owns API state, Redux does not duplicate it
- A form library (React Hook Form + Zod for schema validation) for all forms
- Tailwind CSS for styling
- Vitest + React Testing Library for component tests

## Folder structure (feature-based, not type-based)

```
src/
├── app/                # store setup, router setup, root App component
├── features/
│   ├── auth/            # login, register, forms, auth slice, protected route wrapper
│   ├── organizations/    # org switcher, team invite UI
│   ├── clients/          # client list, client detail, client form
│   ├── projects/         # project list, project detail, project form
│   ├── timeEntries/      # time log table, quick-add entry, filters
│   ├── invoices/         # invoice list, invoice builder, invoice preview/PDF view
│   └── dashboard/        # summary widgets
├── components/          # truly shared/reusable UI (Button, Modal, Table, Input, etc.)
├── lib/                 # api client instance, query client setup, utils
├── types/                # shared TypeScript interfaces (or colocate per-feature, your call)
└── main.tsx
```

Avoid a generic `utils/`-everything dumping ground — keep logic close to the feature that owns it; only truly shared helpers go in `lib/`.

## Core pages (Stage-aligned with backend MVP order)

1. `/login`, `/register`
2. `/dashboard` — outstanding invoices, hours this month, active projects (built last, once data exists to summarize)
3. `/clients` (list), `/clients/:id` (detail)
4. `/projects` (list), `/projects/:id` (detail — shows time entries for that project)
5. `/time-entries` — loggable table with filter by project/date range
6. `/invoices` (list), `/invoices/:id` (detail/preview), invoice builder flow
7. Organization settings / team invite screen

## Auth & routing

- JWT access token held in memory (Redux or a React Query cache, not localStorage — avoid XSS token theft; refresh token handled via httpOnly cookie if the backend supports it, otherwise document the trade-off)
- `ProtectedRoute` wrapper component checks auth state before rendering; redirects to `/login` if absent
- Role-based rendering: a `CLIENT`-role user should never see staff-only routes/components, not just have them hidden by CSS — gate at the route level
- Automatic silent refresh: React Query / axios interceptor catches 401s, attempts refresh, retries the original request once

## API layer conventions

- Single typed API client (axios or fetch wrapper) in `lib/api.ts`, base URL from environment variable
- One TanStack Query hook per resource per operation, e.g. `useClients()`, `useCreateClient()`, `useClient(id)` — colocated in each feature folder, not one giant `api/` file
- TypeScript interfaces for every API request/response shape, matching the backend DTOs (keep these in sync manually for now; codegen from OpenAPI is a nice-to-have stretch goal, not required)
- Consistent error handling: surface backend's error envelope in toast/inline form errors, don't let raw axios errors leak to the UI

## Forms

- React Hook Form + Zod schema per form, validation messages matching backend validation rules (so the user never sees a valid-looking form get rejected by the server for a reason the frontend could've caught)
- Reusable form field components (`TextField`, `SelectField`, `DateField`) in `components/`, not hand-rolled per form

## Testing expectations

- Component tests for: login/register form validation, protected route redirect behavior, and at least one full feature (e.g. client CRUD list+form) with mocked API responses
- Don't aim for 100% coverage — prioritize auth flow and one representative CRUD feature as your testing talking point

## What NOT to do yet

- No Redux for server data — that's React Query's job
- No premature component library abstraction — build real components as features need them, extract shared ones only once you see duplication
- No AI-specific UI until the corresponding backend AI endpoint exists and is stable
