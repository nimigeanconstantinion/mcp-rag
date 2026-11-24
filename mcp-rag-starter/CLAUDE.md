# CLAUDE.md

## What this repo is

`online-shop-cloud-spring-api`: a Spring Boot 3.5 / Java 17 REST API for an online shop (products, customers, orders, order details, admins), secured with JWT and permission-based authorities. IDs are UUIDs. The schema is created by Hibernate (`ddl-auto`), Liquibase is disabled.

**This is the starter of the MCP lesson.** The app is a plain REST API, not an MCP server yet. In exercise 1 the student turns it into one by hand, in the 4 steps below. The student then writes by hand the `@McpTool` methods in `mcp/ShopTools.java` (exercises 2–5); the class exists, with the services injected. Do not do the 4 steps of exercise 1 or write these tools unless the student asks for that code explicitly; explain instead. Part II (OAuth on `/mcp` with Keycloak and Spring Security, a permission per tool, customers limited to their own orders) and Part III (Docker image, CI to Docker Hub, the `prod/` folder deployed on AWS EC2 behind nginx with HTTPS) are done by prompting you: there you write the code the student asks for.

## Commands

| What | Command |
|---|---|
| Build + tests | `mvn -B verify` |
| Single test | `mvn test -Dtest=<TestClass>` |
| Local run, no database needed | `mvn spring-boot:run -Dspring-boot.run.profiles=h2` (in-memory H2) |
| Local MySQL | `cd local && docker compose up -d` (MySQL on `localhost:3307`, `root`/`root`) |
| Local run on MySQL | `mvn spring-boot:run` (or `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` for another database) |
| Login for a token | `POST /api/auth/login` with `admin@shop.com` / `admin1234` |
| Probe the MCP server | `bash probe-mcp.sh [tool '{json}']` (another server: `MCP_URL=...`) |

Tests run on H2 (`src/test/resources/`), no MySQL needed. Swagger: `/swagger-ui.html`, port `8082`.

## MCP (exercise 1, done by the student)

| Step | Where | What |
|---|---|---|
| 0 | `.mcp.json` | `claude mcp add -t http -s project shop http://localhost:8082/mcp` |
| 1 | `pom.xml` | `spring-ai-bom` 1.1.8 in `dependencyManagement`, `spring-ai-starter-mcp-server-webmvc` |
| 2 | `auth/security/SecurityConfig.java` | `/mcp` and `/mcp/**` in `PUBLIC_ENDPOINTS`; the REST API stays on JWT |
| 3 | `application.yaml` | `spring.ai.mcp.server.protocol: STREAMABLE` |

## Data

`DataSeeder` creates the permissions, the admin and a demo customer. With `app.seed.demo-data=true` (the default outside tests) `DemoShopSeeder` adds 8 products, 4 customers and 10 orders. Order dates are relative to today, so the same order is always "5 days ago". Order statuses: `PENDING`, `SHIPPED`, `DELIVERED`, `CANCELLED`. Every seeded customer has the password `customer1234`.

## Layout

One package per feature under `mycode.onlineshopspring`: `auth` (`user`, `customer`, `admin`, `permission`, `jwt`, `security`), `products`, `orders`, `orderDetails`, `customer`, `admin`, `returns`, each with `controller` → `service` → `repository`, `dto`, `models` (JPA entities). MCP tools in `mcp/`, the views they return in `mcp/dto/`. Mapping in `mappers/`, cross-cutting config in `config/`, errors in `exceptions/`.

## Conventions

- Configuration only from env vars with a default in `application.yaml`.
- Constructor injection, DTOs at the controller boundary, entities never leave the service layer.
- `@Transactional` on services, never on controllers.
- Authorization with `@PreAuthorize` and the permissions in `auth/permission/Permissions.java`.
- Error messages in English.
