# Key Decisions (Sprint 1)

## Framework & Architecture

* **Spark (Java microframework)** over a full-stack framework: keeps footprint small and explicit for routing and HTTP handling required by Sprint 1.
* **Layered design (Controller → Service → Repository) with DTOs:** separates HTTP concerns, business rules, and data access. DTOs define the API contract; internals can evolve without breaking clients.
* **Single shared `Gson` instance** injected into controllers and error handler for consistent JSON.

## Persistence

* **H2 (embedded, in-memory) + JDBC:** minimal configuration, no external services; prepared statements are explicit and easy to debug.
* **Migrations at startup (plain SQL):** `Migrations.java` provisions schema. Items table includes `price DECIMAL(12,2)` and `currency VARCHAR(8) NOT NULL`. Defensive `ALTER` adds `currency` if a prior table exists without it.
* **Connection strategy:** each repository method obtains a short-lived connection (`Db.getConnection()`) via try-with-resources.

## Seeding Strategy

* **DataSeeder** runs after migrations and only when tables are empty:

* Seeds three demo **users** (`u1/u2/u3`).
* Loads **items** from `resources/data/items.json`.
* Supports **new JSON** shape (`price` numeric + `currency`) and **legacy** string prices (e.g., `"$621.34 USD"`), extracting amount and a 3-letter currency. Defaults to `USD` if none is found.

## Resource Modeling & IDs

* **IDs provided in path** for both Users and Items (`/users/:id`, `/items/:id`) as strings—aligns with Sprint-1.
* **Users list hides IDs** (privacy); single-user responses include the `id`.
* **Items list includes IDs** to allow navigation to detail and further actions.

## Validation & Errors

* **Users:** `name` and `email` required; email uniqueness checked.
* **Items:** `name` required; `price` required and non-negative; `currency` required as a **3-letter code** (e.g., USD, MXN).
* **Centralized error mapping:** `GlobalErrorHandler` maps:

* `BadRequest` → 400, `NotFound` → 404, `Conflict` → 409 (for **both** UserService and ItemService),
* plus `SQLIntegrityConstraintViolationException` (and H2’s specific subclass) → 409,
* and a generic 500 for unexpected exceptions.
* **Error JSON** unified as `{"message":"..."}`.

## JSON & Logging

* **Gson** for serialization/deserialization of DTOs.
* **Logback** for consistent console logging (SLF4J binding present; no NOP fallback).

## Portability & Future Work

* **H2 URL with `MODE=PostgreSQL`** to ease migration to PostgreSQL later.
* **Potential next steps (post Sprint-1):**

* Templates/views and exception module for UI (Sprint 2).
* Item filters and WebSocket price updates (Sprint 3).
* Currency normalization or FX conversions (if needed in future).
* Optional swap to Postgres by changing JDBC driver/URL and adjusting DDL.
