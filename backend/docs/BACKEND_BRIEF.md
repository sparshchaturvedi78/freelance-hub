# FreelanceHub — Backend Brief

> Read `PROJECT_OVERVIEW.md` first for business context. This document is the backend-specific spec. Working directory: `backend/`.

## Stack

- Java 21, Spring Boot 3.x, Maven
- Spring Web, Spring Data JPA (Hibernate), Spring Security, JWT (jjwt or similar)
- PostgreSQL (Docker locally; cloud free-tier for deployed env — Neon/Supabase/Railway, TBD later)
- Flyway for schema migrations (no `ddl-auto: update` in any environment beyond local scratch work)
- JUnit 5 + Mockito for testing; Testcontainers for integration tests against a real Postgres
- springdoc-openapi for Swagger/OpenAPI docs
- Redis (added later, once caching is actually needed — do not add prematurely)

## Package structure (modular monolith, package-by-feature inside a layered skeleton)

```
com.freelancehub.backend
├── config/            # security config, CORS, OpenAPI config, web config
├── security/           # JWT filter, token provider, UserDetailsService impl
├── common/             # global exception handler, API response wrapper, pagination DTOs
├── tenant/             # Organization entity, membership, invite flow
├── auth/               # register, login, refresh, controller/service/dto
├── client/             # Client entity + CRUD
├── project/            # Project entity + CRUD
├── timeentry/          # TimeEntry entity + CRUD, filtering/search
├── invoice/            # Invoice + InvoiceLineItem, generation logic
├── ai/                 # AI integration module — isolated, behind an interface
└── BackendApplication.java
```

Each feature package internally separates `controller/`, `service/`, `repository/`, `dto/`, `entity/` (or flatten if a feature is small — don't force ceremony on a 2-class feature).

## Core entities (Stage 2 target — build in this order)

1. `Organization` (tenant) — id, name, createdAt
2. `User` — id, email, passwordHash, role, organizationId, createdAt
3. `Client` — id, organizationId, name, contactEmail, notes
4. `Project` — id, organizationId, clientId, name, status (ACTIVE/ARCHIVED), hourlyRate
5. `TimeEntry` — id, projectId, userId, date, hours, description, billable (boolean)
6. `Invoice` — id, organizationId, clientId, status (DRAFT/SENT/PAID/OVERDUE), issuedDate, dueDate, totalAmount
7. `InvoiceLineItem` — id, invoiceId, description, quantity, unitPrice, amount

Every tenant-owned entity gets an `organizationId` column, non-nullable, indexed. Every repository query for these entities must be scoped by the authenticated user's organization — never a bare `findAll()` for tenant data.

## Security requirements

- Passwords: BCrypt, never store plaintext, never log passwords
- JWT: short-lived access token (~15 min), longer-lived refresh token (~7 days) stored server-side (or hashed) to allow revocation
- Refresh token rotation: issuing a new refresh token invalidates the old one
- Role-based method security (`@PreAuthorize`) on service or controller methods, not just route-level checks
- CLIENT role gets read-only access scoped to their own client_id — this is a distinct authorization path from staff roles, test it explicitly

## API conventions

- All responses wrapped in a consistent envelope: `{ success, data, error, timestamp }` (design this once in `common/`, reuse everywhere)
- Validation via `@Valid` + Bean Validation annotations on DTOs, never on entities directly
- Global `@ControllerAdvice` exception handler — no raw stack traces or unhandled exceptions reaching the client
- Pagination: standard `page`, `size`, `sort` query params, returned with total count and page metadata
- Filtering/search on time entries and invoices: query params, translated to JPA Specifications (not string-concatenated JPQL)

## Testing expectations

- Unit tests: service layer business logic (invoice total calculation, tenant scoping logic, RBAC checks) with Mockito
- Integration tests: Testcontainers-backed Postgres, real HTTP calls through `MockMvc` or `WebTestClient`, covering auth flow and at least one full CRUD + authorization path end to end
- Every milestone's Definition of Done includes: tests passing, Swagger docs updated, meaningful commit pushed

## AI module (build after MVP CRUD is solid — do not start here)

- Isolated behind an interface (e.g. `AiInsightService`) so the rest of the app doesn't depend on a specific provider
- Called from the backend server-side only — API key never touches the frontend
- Must handle: rate limits, timeouts, and provider errors with a graceful fallback (e.g. return the raw time-log text if AI summarization fails, don't break the request)
- Log every AI call (prompt metadata, latency, success/failure) for the "observability" talking point later
- First AI feature to build: generating a clean invoice line-item description from a project's raw time-entry notes for a billing period

## What NOT to do yet

- No microservices — everything above lives in one Spring Boot app
- No Redis/caching until there's a real dashboard aggregate query slow enough to justify it
- No premature abstraction — don't build a generic "plugin system" for the AI module; one interface + one implementation is enough for now
