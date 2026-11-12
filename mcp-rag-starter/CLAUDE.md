# CLAUDE.md

## What this repo is

`online-shop-cloud-spring-api`: a Spring Boot 3.5 / Java 17 REST API for an online shop (products, customers, orders, order details, admins), secured with JWT and permission-based authorities. IDs are UUIDs. The schema is created by Hibernate (`ddl-auto`), Liquibase is disabled.

**This is the starter of the MCP + RAG lesson.** The app is already an MCP server (Spring AI 1.1.8, Streamable HTTP on `/mcp`, open without a token) with no tools yet. The student writes the `@McpTool` methods by hand in `mcp/ShopTools.java` and `mcp/PolicyTools.java`: do not write or change them unless the student asks for that code explicitly; explain instead. The RAG search over `policies/*.md` (package `policies`, local ONNX embeddings, in-memory `SimpleVectorStore`) is ready and starts with `app.rag.enabled`.

## Commands

| What | Command |
|---|---|
| Build + tests | `mvn -B verify` |
| Single test | `mvn test -Dtest=<TestClass>` |
| Local run, no database needed | `mvn spring-boot:run -Dspring-boot.run.profiles=h2` (in-memory H2) |
| Local run on MySQL | `mvn spring-boot:run` (needs MySQL on `localhost:3307`, or `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`) |
| Login for a token | `POST /api/auth/login` with `admin@shop.com` / `admin1234` |
| Probe the MCP server | `bash probe-mcp.sh [tool '{json}']` (another server: `MCP_URL=...`) |

Tests run on H2 (`src/test/resources/`), no MySQL needed, and without RAG (`app.rag.enabled` is missing there, so no model is downloaded). Swagger: `/swagger-ui.html`, port `8082`.

## MCP and RAG

| Where | What |
|---|---|
| `pom.xml` | `spring-ai-bom` 1.1.8, `spring-ai-starter-mcp-server-webmvc`, `spring-ai-transformers`, `spring-ai-vector-store` |
| `application.yaml` | `spring.ai.mcp.server.protocol: STREAMABLE`, server name and version; `app.rag.*` |
| `auth/security/SecurityConfig.java` | `/mcp` and `/mcp/**` in `PUBLIC_ENDPOINTS`; the REST API stays on JWT |
| `.mcp.json` | the `shop` server for Claude Code: `http://localhost:8082/mcp` |

| Env var | Default | What |
|---|---|---|
| `RAG_ENABLED` | `false` | `true` = index the policies at startup (`Indexed 20 policy sections`), `PolicyTools` is created |
| `RAG_MODEL_URI` / `RAG_TOKENIZER_URI` | multilingual MiniLM-L12, quantized ONNX, Hugging Face | embeddings model |
| `RAG_CACHE_DIR` | `<tmp>/shop-onnx-cache` | where the model is cached |
| `DJL_CACHE_DIR` | `~/.djl.ai` | where DJL caches the tokenizer and PyTorch native libraries |

The first start with RAG downloads about 320 MB (model, tokenizer, PyTorch native library).

## Data

`DataSeeder` creates the permissions, the admin and a demo customer. With `app.seed.demo-data=true` (the default outside tests) `DemoShopSeeder` adds 8 products, 4 customers and 10 orders. Order dates are relative to today, so the same order is always "5 days ago". Order statuses: `PENDING`, `SHIPPED`, `DELIVERED`, `CANCELLED`. Every seeded customer has the password `customer1234`.

The shop policies (returns, warranty, shipping, payment) are Markdown files in `src/main/resources/policies/`, written in Romanian.

## Layout

One package per feature under `mycode.onlineshopspring`: `auth` (`user`, `customer`, `admin`, `permission`, `jwt`, `security`), `products`, `orders`, `orderDetails`, `customer`, `admin`, `returns`, each with `controller` → `service` → `repository`, `dto`, `models` (JPA entities). MCP tools in `mcp/`, the views they return in `mcp/dto/`, RAG in `policies/`. Mapping in `mappers/`, cross-cutting config in `config/`, errors in `exceptions/`.

## Conventions

- Configuration only from env vars with a default in `application.yaml`.
- Constructor injection, DTOs at the controller boundary, entities never leave the service layer.
- `@Transactional` on services, never on controllers.
- Authorization with `@PreAuthorize` and the permissions in `auth/permission/Permissions.java`.
- Error messages in English.
