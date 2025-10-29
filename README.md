Here are the corrected files—updated to include **Items** (with decimal `price` and `currency`), the **data seeding**, and the improved **error handling**.

---

# README.md

# Store — Sprint 1 Finished (Spark + H2 + JDBC + DTO)

## Overview

API service for **Users** and **Items** built with **Spark (Java)**, **H2 (in-memory)**, and **plain JDBC**, using **DTOs** for clean request/response payloads.

**Sprint-1 scope (per instructions):**

* **Users routes:**
  `GET /users`, `GET /users/:id`, `POST /users/:id`, `PUT /users/:id`, `OPTIONS /users/:id`, `DELETE /users/:id`
* **Items routes:**
  `GET /items`, `GET /items/:id`, `POST /items/:id`, `PUT /items/:id`, `OPTIONS /items/:id`, `DELETE /items/:id`
* **IDs in path**: creation/updates use the **path `:id`** (string).
* **Users list hides IDs:** `GET /users` returns only `name` and `email`.
* **Items list includes IDs** (to navigate to detail): `id`, `name`, `price`, `currency`.
* Logging via **Logback**.
* Decisions recorded in `DECISIONS.md`.

## Tech used

* Java 17+
* Spark (HTTP microframework)
* H2 (embedded, in-memory)
* JDBC (prepared statements)
* Gson (JSON)
* Logback (logging)
* Maven (build)

## Project Structure

```
/ (repo root)
  ├─ pom.xml
  ├─ README.md
  ├─ DECISIONS.md
  ├─ postman
  │   ├─ user_collection.json
  │   └─ items_collection.json
  └─ src/main
      ├─ java/app
      │   ├─ Main.java
      │   ├─ config
      │   │   ├─ Db.java
      │   │   ├─ Migrations.java
      │   │   └─ DataSeeder.java
      │   ├─ model
      │   │   ├─ User.java
      │   │   └─ Item.java
      │   ├─ model/dto
      │   │   ├─ UserRequestDTO.java
      │   │   ├─ UserListItemDTO.java
      │   │   ├─ UserResponseDTO.java
      │   │   ├─ ItemRequestDTO.java
      │   │   ├─ ItemListDTO.java
      │   │   └─ ItemResponseDTO.java
      │   ├─ repo
      │   │   ├─ UserRepository.java
      │   │   └─ ItemRepository.java
      │   ├─ service
      │   │   ├─ UserService.java
      │   │   └─ ItemService.java
      │   └─ web
      │       ├─ GlobalErrorHandler.java
      │       ├─ UserController.java
      │       └─ ItemController.java
      └─ resources
          ├─ logback.xml
          └─ data
             └─ items.json
```

## How to Run

### IDE

1. Ensure Java 17+ and Maven are installed.
2. Open the project, set dependency versions in `pom.xml`.
3. Run `app.Main`.
4. Verify health: `http://localhost:4567/health` → `OK`.

> Change the port in `Main.java` (`port(4567)`) if needed.

## Database

* H2 in-memory URL is defined in `Db.java`:
  `jdbc:h2:mem:collectibles;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
* Tables are created on startup by `Migrations.java`.
* **Seed data** (`DataSeeder.java`):

  * Seeds **users** (`u1/u2/u3`) if table is empty.
  * Loads **items** from `src/main/resources/data/items.json` if table is empty.
  * Accepts **new format** (`price` as number + `currency`) and is backward-compatible with legacy strings like `"$621.34 USD"`.
* Data resets on each app restart.

## API Contract

### USERS — Data Transfer Objects (DTO)

**Request (create/update):**

```json
{ "name": "string (required)", "email": "string (required, unique)" }
```

**List item (GET /users)** — *ID hidden*:

```json
[
  { "name": "string", "email": "string" }
]
```

**Single user (GET/POST/PUT /users/:id):**

```json
{ "id": "string", "name": "string", "email": "string" }
```

### ITEMS — Data Transfer Objects (DTO)

**Request (create/update):**

```json
{
  "name": "string (required)",
  "description": "string (optional)",
  "price": 123.45,
  "currency": "USD"  // 3-letter code, e.g., USD, MXN
}
```

**List item (GET /items):**

```json
[
  { "id": "string", "name": "string", "price": 123.45, "currency": "USD" }
]
```

**Single item (GET/POST/PUT /items/:id):**

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "price": 123.45,
  "currency": "USD"
}
```

### Endpoints

**Users**

* `GET /users` → 200, returns array of `{name,email}` (no IDs).
* `GET /users/:id` → 200 with a single user; 404 if not found.
* `POST /users/:id` → 201 creates user with path id; 409 if id/email conflict; 400 if invalid body.
* `PUT /users/:id` → 200 updates user; 404 if not found; 400/409 as applicable.
* `OPTIONS /users/:id` → 204 if exists, 404 otherwise.
* `DELETE /users/:id` → 204 on success; 404 if not found.

**Items**

* `GET /items` → 200, returns array of `{id,name,price,currency}`.
* `GET /items/:id` → 200 with a single item; 404 if not found.
* `POST /items/:id` → 201 creates item with path id; 409 if duplicate id; 400 if invalid body.
* `PUT /items/:id` → 200 updates item; 404 if not found; 400 on validation errors.
* `OPTIONS /items/:id` → 204 if exists, 404 otherwise.
* `DELETE /items/:id` → 204 on success; 404 if not found.

### Error Handling

* JSON error shape: `{"message":"..."}`
* Mapped statuses:

  * `400` Bad Request (validation)
  * `404` Not Found
  * `409` Conflict (duplicate id/email, constraint violations)
  * `500` Internal error (unexpected)

## Logging

* SLF4J + Logback console logging configured in `src/main/resources/logback.xml`.
* Typical pattern: `HH:mm:ss.SSS LEVEL [thread] logger - message`.