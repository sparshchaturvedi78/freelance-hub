# FreelanceHub — Project Overview

> Shared context for both `backend/` and `frontend/`. Read this first, then read your side-specific brief (`BACKEND_BRIEF.md` or `FRONTEND_BRIEF.md`).

## What this is

A multi-tenant SaaS platform for freelancers and small agencies to manage clients, projects, time tracking, invoicing, and payments. This is a resume/portfolio project built to demonstrate production-grade Java/Spring Boot + React engineering — depth over breadth. Do not add technology or complexity that isn't earning its place.

## Business problem

Freelancers and small agencies juggle client work across spreadsheets, chat threads, and disconnected invoicing tools. They lose track of billable hours, send inconsistent invoices, and have no visibility into which clients/projects are actually profitable.

## Target users

- **Freelancer / Agency Owner** — full control: manages clients, projects, team, billing
- **Team Member** — logs time against assigned projects, limited visibility
- **Client** (view-only, external) — sees their own project status and invoices, nothing else

This drives the RBAC model: `OWNER`, `ADMIN`, `MEMBER`, `CLIENT`.

## Multi-tenancy model

Shared schema, single database, every tenant-owned table carries a `tenant_id` (also called `organization_id`). Tenant isolation is enforced at the service layer (every query scoped by the authenticated user's tenant) — not relying on the database alone. This is a deliberate architectural choice for this project size (schema-per-tenant is overkill at this scale) — be ready to discuss the trade-off if asked.

## MVP scope (build this first, in order)

1. Auth: registration, login, JWT access + refresh tokens
2. Organizations (tenants), invite team members, RBAC
3. Clients CRUD (scoped per tenant)
4. Projects CRUD (belongs to a client)
5. Time entries (logged against a project, by a user)
6. Invoices generated from time entries, with line items
7. Invoice PDF export + basic email send
8. Dashboard: outstanding invoices, hours this month, active projects

## Advanced scope (after MVP works end-to-end)

- AI-generated invoice line-item descriptions from raw time-log notes
- Natural-language search over time entries / invoices ("how many hours did I bill Client X in June")
- Overdue invoice detection + automated reminder emails (scheduled job)
- Billing anomaly detection (unusually high/low hours vs. historical average)
- Redis caching for dashboard aggregates
- Rate limiting on client-facing (external) API routes

## Non-functional requirements

- Every tenant's data must be fully isolated — a bug here is the single worst failure mode for this app
- Money is handled as integer cents or `BigDecimal`, never floating point
- All financial state changes go through a transaction
- Passwords hashed with BCrypt; JWTs signed, short-lived access tokens + longer-lived rotated refresh tokens
- API responses follow one consistent envelope shape (success + error)

## Git workflow (both repos, same convention)

- `main` is always deployable
- Feature branches: `feature/short-description`, `fix/short-description`
- Conventional commits: `feat:`, `fix:`, `test:`, `refactor:`, `ci:`, `chore:`, `docs:`
- Every milestone ends the same way: implement → test → commit → push → update docs if needed

## Current status

Stage 1 complete: Spring Boot backend + React/TS (Vite) frontend scaffolded, Postgres running via Docker Compose, connected, first commit pushed.

Next: Stage 2 — database schema and core entities (tenants, users, clients, projects, time entries).
