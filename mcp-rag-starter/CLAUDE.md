# CLAUDE.md

## What this repo is

`online-shop-cloud-spring-api`: a Spring Boot 3.5 / Java 17 REST API for an online shop (products, customers, orders, order details, admins), secured with JWT and permission-based authorities. IDs are UUIDs. The schema is created by Hibernate (`ddl-auto`), Liquibase is disabled.

**This is a starter for the MCP + RAG lesson.** There is no `.mcp.json`, no Spring AI dependency and no vector store here on purpose: the MCP server (tools over orders and products) and the search over the shop policies are built during the lesson.

## Commands

| What | Command |
|---|---|
| Build + tests | `./mvnw -B verify` (Windows: `mvnw.cmd -B verify`) |
| Single test | `./mvnw test -Dtest=<TestClass>` |
| Local run, no database needed | `./mvnw spring-boot:run -Dspring-boot.run.profiles=h2` (in-memory H2; Windows: `mvnw.cmd`) |
| Local run on MySQL | `./mvnw spring-boot:run` (needs MySQL on `localhost:3307`, or `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`) |
| Login for a token | `POST /api/auth/login` with `admin@shop.com` / `admin1234` |

Tests run on H2 (`src/test/resources/`), no MySQL needed. Swagger: `/swagger-ui.html`, port `8082`.

## Data

`DataSeeder` creates the permissions, the admin and a demo customer. With `app.seed.demo-data=true` (the default outside tests) `DemoShopSeeder` adds 8 products, 4 customers and 10 orders. Order dates are relative to today, so the same order is always "5 days ago". Order statuses: `PENDING`, `SHIPPED`, `DELIVERED`, `CANCELLED`. Every seeded customer has the password `customer1234`.

The shop policies (returns, warranty, shipping, payment) are Markdown files in `src/main/resources/policies/`, written in Romanian.

## Layout

One package per feature under `mycode.onlineshopspring`: `auth` (`user`, `customer`, `admin`, `permission`, `jwt`, `security`), `products`, `orders`, `orderDetails`, `customer`, `admin`, each with `controller` → `service` → `repository`, `dto`, `models` (JPA entities). Mapping in `mappers/`, cross-cutting config in `config/`, errors in `exceptions/`.

## Conventions

- Configuration only from env vars with a default in `application.yaml`.
- Constructor injection, DTOs at the controller boundary, entities never leave the service layer.
- `@Transactional` on services, never on controllers.
- Authorization with `@PreAuthorize` and the permissions in `auth/permission/Permissions.java`.
- Error messages in English.
