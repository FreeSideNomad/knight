# Commercial Banking Cash Management Platform

A modular monolith implementing Domain-Driven Design (DDD) for a commercial banking cash management platform. Built with Spring Boot 3.3, Java 17, following strategic and tactical DDD patterns with clean boundaries for future microservices extraction.

## Architecture

### Design Principles

- **Modular Monolith** with clean seams for future service extraction
- **Bounded Contexts** as separate Maven modules: `api`, `domain`, `app`, `infra`
- **Strict dependency rules** enforced by ArchUnit tests
- **In-process domain events** with Spring `ApplicationEventPublisher` (Outbox/Kafka-ready)
- **One schema per bounded context** with Flyway migrations
- **Hexagonal architecture** within each bounded context

### Module Structure

```
cash-management-platform/
├── platform/
│   └── shared-kernel/          # Common value objects (ClientId, ProfileId hierarchy)
├── contexts/
│   ├── service-profiles/
│   │   ├── management/         # Service Profile Management BC
│   │   │   ├── api/           # Commands, queries, events (public interface)
│   │   │   ├── domain/        # Aggregates, entities, value objects (pure domain)
│   │   │   ├── app/           # Application services, orchestration
│   │   │   └── infra/         # REST controllers, JPA, Flyway, Spring Boot app
│   │   └── indirect-clients/  # Indirect Client Management BC (pending)
│   └── users/
│       ├── users/             # Users BC (pending)
│       └── policy/            # Policy BC (pending)
└── bff/
    └── web/                   # Backend-for-Frontend (pending)
```

### Dependency Rules

```
infra → app → domain → api
                        ↑
                   shared-kernel
```

**Key constraints (enforced by ArchUnit):**
- **Domain** layer: NO dependencies on `app`, `infra`, Spring Framework, JPA
- **Application** layer: NO dependencies on `infra`
- **API** layer: Only depends on `shared-kernel`
- **BFF** layer: Only depends on BC `api` modules (never `domain` or `app`)

## Technology Stack

- **Java 17** (toolchain enforced)
- **Spring Boot 3.3.4**
- **Spring Modulith 1.2.4** (dependency checks + scenario tests)
- **PostgreSQL** (one schema per BC)
- **Flyway** (database migrations)
- **Maven** (multi-module reactor)
- **Lombok** (reduce boilerplate)
- **MapStruct** (domain ↔ JPA mapping)
- **ArchUnit** (architecture guardrails)

## Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+**
- **Docker** (for PostgreSQL)
- **PostgreSQL 15+** (or use Docker Compose below)

## Quick Start

### 1. Start PostgreSQL (Docker)

```bash
docker run -d \
  --name knight-postgres \
  -e POSTGRES_USER=knight \
  -e POSTGRES_PASSWORD=knight_dev \
  -e POSTGRES_DB=knight_spm \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2. Build the Project

```bash
# From project root
mvn clean install
```

### 3. Run Service Profile Management BC

```bash
cd contexts/service-profiles/management/infra
mvn spring-boot:run
```

Application starts on **http://localhost:8081**

### 4. Test with cURL

#### Create Servicing Profile

```bash
curl -X POST http://localhost:8081/commands/service-profiles/servicing/create \
  -H "Content-Type: application/json" \
  -d '{
    "clientUrn": "srf:12345",
    "createdBy": "admin@bank.com"
  }'
```

Response:
```json
{
  "profileUrn": "servicing:srf:12345"
}
```

#### Enroll Service (BTR)

```bash
curl -X POST http://localhost:8081/commands/service-profiles/servicing/enroll-service \
  -H "Content-Type: application/json" \
  -d '{
    "profileUrn": "servicing:srf:12345",
    "serviceType": "BTR",
    "configuration": {
      "frequency": "DAILY"
    }
  }'
```

#### Enroll Account

```bash
curl -X POST http://localhost:8081/commands/service-profiles/servicing/enroll-account \
  -H "Content-Type: application/json" \
  -d '{
    "profileUrn": "servicing:srf:12345",
    "serviceEnrollmentId": "<enrollment-id-from-previous-step>",
    "accountId": "DDA-CAD-98765"
  }'
```

## Domain Model

### Shared Kernel Value Objects

- **`ClientId`**: Client identifier URN (`srf:12345`, `gid:G789`, `ind:IND001`)
- **`ProfileId`** (sealed interface):
  - `ServicingProfileId`: `servicing:{client_urn}`
  - `OnlineProfileId`: `online:{client_urn}:{sequence}`
  - `IndirectProfileId`: `indirect:{indirect_client_urn}`
- **`IndirectClientId`**: `ind-client:{client_urn}:{sequence}`

### Service Profile Management BC

#### Aggregates

**ServicingProfile** (Root)
- **Entities**: ServiceEnrollment, AccountEnrollment
- **Invariants**:
  - `profile_id` must be unique
  - `client_id` immutable after creation
  - At least one service enrolled for ACTIVE status
- **Lifecycle**: PENDING → ACTIVE → SUSPENDED → CLOSED

#### Commands

- `createServicingProfile(clientId, createdBy)`
- `enrollService(profileId, serviceType, configuration)`
- `enrollAccount(profileId, serviceEnrollmentId, accountId)`
- `suspendProfile(profileId, reason, suspendedBy)`

#### Queries

- `getServicingProfileSummary(profileId)` → `{profileUrn, clientUrn, status, enrolledServices, enrolledAccounts}`

#### Events

- `ServicingProfileCreated(profileId, clientId, createdBy, createdAt)`

## Development

### Running Tests

```bash
# All tests including ArchUnit
mvn test

# ArchUnit tests only
mvn test -Dtest=DddArchitectureTest
```

### Database Migrations

Flyway migrations in `infra/src/main/resources/db/migration`:

- `V1__create_servicing_profile_schema.sql` (pending - add JPA entities)

### Adding a New Bounded Context

1. Create module structure:
   ```
   contexts/<domain>/<bc-name>/
   ├── api/
   ├── domain/
   ├── app/
   └── infra/
   ```

2. Add to root `pom.xml`:
   ```xml
   <module>contexts/<domain>/<bc-name></module>
   ```

3. Follow existing patterns:
   - **API**: Command/query interfaces + events (records)
   - **Domain**: Aggregates with invariants + business methods
   - **App**: Application services implementing API + repository interfaces
   - **Infra**: Spring Boot app + REST controllers + JPA repos + Flyway

4. Add ArchUnit tests to enforce layering

## Production Readiness Checklist

- [ ] Replace `InMemoryServicingProfileRepository` with JPA implementation
- [ ] Add Flyway schema migrations for all entities
- [ ] Implement Outbox/Inbox pattern for external events (Kafka)
- [ ] Add OpenAPI/Swagger documentation
- [ ] Implement authentication/authorization (OIDC)
- [ ] Add distributed tracing (Spring Cloud Sleuth)
- [ ] Implement health checks and metrics (Actuator)
- [ ] Add integration tests with Testcontainers
- [ ] Configure connection pooling (HikariCP)
- [ ] Implement retry and circuit breaker patterns
- [ ] Add CQRS read models (separate query database)
- [ ] Implement event sourcing for audit trail

## References

- **DDD Model**: `model/platform-ddd.yaml` (strategic + tactical patterns)
- **Workflow**: `prompts/workflow-prompt.md`
- **Domain Schemas**: `domains/ddd/model-schema.yaml`

## License

Proprietary - Internal Bank Use Only
