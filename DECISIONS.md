# Key Decisions (Sprint 1)

## Framework & Architecture

* **Spark (Java microframework)** over a full-stack framework:
  Keeps the footprint small and explicit for routing and HTTP handling required by Sprint 1. We only add what the sprint asks for.

* **Layered design (Controller → Service → Repository) with DTOs:**
  Separates HTTP concerns, business rules, and data access. DTOs define the API contract; internal models and storage can evolve without breaking clients.

## Persistence

* **H2 (embedded, in-memory) + JDBC:**
  Meets the “simple database” goal with minimal configuration and no external services. JDBC prepared statements are explicit, easy to debug, and sufficient for Sprint 1.

* **Migrations at startup (plain SQL):**
  `Migrations.java` runs `CREATE TABLE IF NOT EXISTS ...` to provision the schema on boot. This avoids introducing a migrations tool for now.

* **Connection strategy:**
  Each repository method obtains a short-lived connection via `Db.getConnection()` using try-with-resources. Simple, safe, and adequate for the current scope.

## Resource Modeling & IDs

* **User IDs provided via path parameter (`/users/:id`)** as **strings**:
  Aligns exactly with Sprint-1 instructions (POST/PUT/GET/DELETE operate on `/users/:id`).

* **List endpoint hides IDs:**
  Treats IDs as sensitive for bulk listings; `GET /users` returns `{name,email}` only. Single-record responses include the `id` since the caller already knows/targets it.

## Validation & Errors

* **Lightweight validation in the Service layer:**

    * `name` and `email` required for create/update
    * `email` uniqueness enforced at the repository level with a pre-check
* **Centralized error mapping:**
  Domain exceptions (`BadRequest`, `Conflict`, `NotFound`) are converted to JSON with appropriate HTTP status codes via `GlobalErrorHandler`.

## JSON & Logging

* **Gson** for serialization/deserialization of DTOs.
* **Logback** for consistent console logging during development and review.

## Portability & Future Work

* **H2 URL uses `MODE=PostgreSQL`** to ease a future migration to PostgreSQL.
* Post-Sprint-1 extensions (not implemented yet):

    * Templates/views and exception module for UI (Sprint 2).
    * Item filters and WebSocket price updates (Sprint 3).
    * Optional swap to Postgres by changing the JDBC URL/driver and adjusting DDL.
