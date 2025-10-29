# Store — Sprint 1 (Spark + H2 + JDBC + DTO)

## Overview

API service for the **users** resource built with **Spark (Java)**, **H2 (in-memory)**, and **plain JDBC**, using **DTOs** for clean request/response payloads.

**Sprint-1 scope (per instructions):**

* Routes:
  `GET /users`, `GET /users/:id`, `POST /users/:id`, `PUT /users/:id`, `OPTIONS /users/:id`, `DELETE /users/:id`
* **Creation/updates** use the **path `:id`** as the user identifier (string).
* **List endpoint hides IDs** (treated as sensitive): `GET /users` returns only `name` and `email`.
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
  │   └─ user_collection.json
  └─ src/main
      ├─ java/app
      │   ├─ Main.java
      │   ├─ config
      │   │   ├─ Db.java
      │   │   └─ Migrations.java
      │   ├─ model
      │   │   ├─ User.java
      │   │   └─ dto
      │   │       ├─ UserRequestDTO.java
      │   │       └─ UserResponseDTO.java
      │   ├─ repo
      │   │   └─ UserRepository.java
      │   ├─ service
      │   │   └─ UserService.java
      │   └─ web
      │       ├─ GlobalErrorHandler.java
      │       └─ UserController.java
      └─ resources
          └─ logback.xml
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
* Data resets on each app restart.

## API Contract

### Data Transfer Objects (DTO)

* **Request (create/update):**

```json
{
  "name": "string (required)",
  "email": "string (required, unique)"
}
```

* **List item (GET /users):** *(ID hidden)*

```json
[
  { "name": "string", "email": "string" }
]
```

* **Response Single user (GET/POST/PUT /users/:id):**

```json
{ "id": "string", "name": "string", "email": "string" }
```

### Endpoints

* `GET /users` → 200, returns array of `{name,email}` (no IDs).
* `GET /users/:id` → 200 with a single user; 404 if not found.
* `POST /users/:id` → 201 creates user with the **path id**; 409 if id/email conflict; 400 if invalid body.
* `PUT /users/:id` → 200 updates user; 404 if not found; 400/409 as applicable.
* `OPTIONS /users/:id` → 204 if exists, 404 otherwise.
* `DELETE /users/:id` → 204 on success; 404 if not found.

## Logging

* Console logging configured in `src/main/resources/logback.xml`.
