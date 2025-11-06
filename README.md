# Java Spark for web apps (Spark + H2 + JDBC + DTO + Mustache + WebSocket)

## Overview

Web API and HTML views for **Users**, **Items**, and **Offers** built with **Spark (Java)**, **H2 (in-memory)**, **plain JDBC**, **DTOs** for clean payloads, **Mustache** templates for the UI, and a **WebSocket** channel for real-time updates.

**Sprint-1 scope**

* **Users routes:** `GET /users`, `GET /users/:id`, `POST /users/:id`, `PUT /users/:id`, `OPTIONS /users/:id`, `DELETE /users/:id`
* **Items routes:** `GET /items`, `GET /items/:id`, `POST /items/:id`, `PUT /items/:id`, `OPTIONS /items/:id`, `DELETE /items/:id`
* IDs supplied in the **path** for create/update.
* **Users list hides IDs**; **Items list includes IDs** (for navigation).
* Logback logging; decisions in `DECISIONS.md`.

**Sprint-2 scope**

* **Exception handling module** (domain errors → JSON with proper HTTP codes).
* **Views & templates (Mustache)**

  * `GET /ui/items` — items list (name, price, currency)
  * `GET /ui/items/:id` — item detail (name, description, price, currency) + **offer form**
* **Offers**

  * `POST /api/offer` — submit an offer (JSON or form-urlencoded)
  * `GET /api/offers` — list offers
* Static assets wired: `styles.css`, `offer.css`, `script.js`, `offer.js`.
* **Seeding** extended (optional `resources/data/ofertas.json`).

**Sprint-3 scope**

* **Item filters** (API + UI): `min_price`, `max_price`, `currency`, `q` (name contains).
  – No params → return **all**. Any subset works in combination.
* **WebSocket live updates** (`/ws`):

  * Item **update** (name/description/price/currency) reflected instantly in list and detail.
  * Item **create/delete** reflected instantly in the list (respects current filters).
  * **New offers** show up instantly in the item’s **offers table** on its detail page.
  * Visual cue: changed fields fade **to red**, hold 2s, then fade back to black.

## Tech used

* Java 17+
* Spark (HTTP microframework) + Jetty WebSocket (via Spark)
* H2 (embedded, in-memory)
* JDBC (prepared statements)
* Gson (JSON)
* Logback (logging)
* Mustache (templates via `spark-template-mustache`)

## Project structure

```
/
  ├─ pom.xml
  ├─ README.md
  ├─ DECISIONS.md
  ├─ postman
  │   ├─ user_collection.json
  │   ├─ item_collection.json
  │   └─ offer_collection.json
  └─ src/main
      ├─ java/app
      │   ├─ Main.java
      │   ├─ config
      │   │   ├─ Db.java
      │   │   ├─ Migrations.java
      │   │   └─ DataSeeder.java
      │   ├─ model
      │   │   ├─ User.java
      │   │   ├─ Item.java
      │   │   └─ Offer.java
      │   ├─ model/dto
      │   │   ├─ UserRequestDTO.java
      │   │   ├─ UserListItemDTO.java
      │   │   ├─ UserResponseDTO.java
      │   │   ├─ ItemRequestDTO.java
      │   │   ├─ ItemListDTO.java
      │   │   ├─ ItemResponseDTO.java
      │   │   ├─ OfferRequestDTO.java
      │   │   └─ OfferResponseDTO.java
      │   ├─ repo
      │   │   ├─ UserRepository.java
      │   │   ├─ ItemRepository.java
      │   │   └─ OfferRepository.java
      │   ├─ service
      │   │   ├─ UserService.java
      │   │   ├─ ItemService.java
      │   │   └─ OfferService.java
      │   ├─ realtime
      │   │   └─ WsEndpoint.java
      │   └─ web
      │       ├─ GlobalErrorHandler.java
      │       ├─ UserController.java
      │       ├─ ItemController.java
      │       ├─ OfferController.java
      │       ├─ ItemViewController.java
      │       └─ OfferViewController.java (if present)
      └─ resources
          ├─ logback.xml
          ├─ templates
          │   ├─ items.mustache
          │   └─ item_detail.mustache
          ├─ public
          │   ├─ styles.css
          │   ├─ offer.css
          │   ├─ script.js
          │   ├─ offer.js
          │   └─ ws.js
          └─ data
              ├─ items.json
              └─ ofertas.json
```

## How to run

1. Java 17+ and Maven installed.
2. Run `app.Main`.
3. Health check → `http://localhost:4567/health` returns `OK`.
4. UI:

  * List: `http://localhost:4567/ui/items`
  * Detail: click any item

> Static files served from `/public` (`staticFiles.location("/public")`).
> WebSocket mounted at `/ws`.

## Database

* H2 URL in `Db.java`:
  `jdbc:h2:mem:collectibles;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
* Tables created by `Migrations.java` on startup.

## API

### Users DTOs

**Request (create/update)**

```json
{ "name": "string (required)", "email": "string (required, unique)" }
```

**List (GET /users)** – ID hidden:

```json
[{ "name": "string", "email": "string" }]
```

**Single (GET/POST/PUT /users/:id)**

```json
{ "id": "string", "name": "string", "email": "string" }
```

### Items DTOs

**Request (create/update)**

```json
{
  "name": "string (required)",
  "description": "string (optional)",
  "price": 123.45,
  "currency": "USD"
}
```

**List (GET /items)**

```json
[{ "id": "string", "name": "string", "price": 123.45, "currency": "USD" }]
```

**Single (GET/POST/PUT /items/:id)**

```json
{ "id": "string", "name": "string", "description": "string", "price": 123.45, "currency": "USD" }
```

### Offers DTOs

**Submit (JSON or form)**

```json
{ "itemId": "i1", "name": "Alice", "email": "alice@example.com", "amount": 123.45 }
```

Form fields also accepted: `id` (alias of `itemId`), `name`, `email`, `amount`.

**List**

```json
[{ "id": 7, "itemId": "i1", "name": "Alice", "email": "alice@example.com", "amount": 123.45 }]
```

## Endpoints

**Users**

* `GET /users`
* `GET /users/:id`
* `POST /users/:id`
* `PUT /users/:id`
* `OPTIONS /users/:id`
* `DELETE /users/:id`

**Items**

* `GET /items`  *(supports filters: `min_price`, `max_price`, `currency`, `q`)*
* `GET /items/:id`
* `POST /items/:id`
* `PUT /items/:id`
* `OPTIONS /items/:id`
* `DELETE /items/:id`

**Offers**

* `POST /api/offer`
* `GET /api/offers`

## Views

**`/ui/items`**

* Server-rendered table (Mustache).
* Filters via query string; empty form → **all** items.

**`/ui/items/:id`**

* Two columns (6/12 + 6/12): **details** and **offer form** (toggle).
* Offers table below (name, email, amount), newest first.

## Real-time (WebSocket)

**Server setup**

* `webSocket("/ws", WsEndpoint.class);` in `Main`.

**Server broadcasts**

* `updateItem` — after `PUT /items/:id`
* `itemCreated` — after `POST /items/:id`
* `itemDeleted` — after `DELETE /items/:id`
* `newOffer` — after `POST /api/offer`

**Client (`public/ws.js`)**

* Connects to `/ws`.
* Updates fields in place by element id:

  * Items list/detail: `#name-{id}`, `#price-{id}`, `#currency-{id}`, `#description-{id}`
  * Offers detail table: prepends a row to `#offer-tbody` for the current item.
* Visual cue: text color transitions **red → hold 2s → black** on any changed field.
* Respects current filters when inserting newly created items.

## Error handling

JSON error shape: `{"message":"..."}`
Statuses: `400` (validation), `404` (not found), `409` (conflict), `500` (unexpected).

## Logging

SLF4J + Logback (`src/main/resources/logback.xml`).
Pattern: `HH:mm:ss.SSS LEVEL [thread] logger - message`.

## Postman collections

* `postman/user_collection.json`
* `postman/item_collection.json`
* `postman/offer_collection.json`

## Quick real-time test

1. Open **two** tabs: `/ui/items` and one item’s `/ui/items/:id`.
2. In Postman:

  * `PUT /items/i1` with a new price/name → both tabs update instantly.
  * `POST /items/newId` → appears in the list (if it matches filters).
  * `DELETE /items/newId` → disappears from the list.
  * `POST /api/offer` (for that item) → new row appears in the offers table on its detail page.

---
