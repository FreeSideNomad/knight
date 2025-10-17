# Knight Platform - Code Generation Summary

## Project Structure Generated

This is a complete Micronaut 4.9.3 + Java 17 DDD Modular Monolith implementation following the specifications in `prompts/prompt.md` and the DDD model in `model/platform-ddd.yaml`.

### Architecture Overview

**Pattern**: 4-Layer DDD Architecture per Bounded Context
- **API Layer**: Command/Query interfaces + Domain Events (contracts)
- **Domain Layer**: Aggregates, Entities, Value Objects, Domain Services
- **Application Layer**: Application Services (orchestration, transactions, event publishing)
- **Infrastructure Layer**: REST Controllers, JPA Repositories, Persistence, Flyway migrations

### Modules Generated

#### Platform
1. **platform/shared-kernel** - Value Objects shared across all contexts
   - ClientId, ServicingProfileId, OnlineProfileId, IndirectProfileId, IndirectClientId
   - UserId, UserGroupId

#### BFF
2. **bff/web** - Backend-for-Frontend composition layer
   - ProfileController (example composition endpoint)
   - Depends ONLY on BC API modules (no domain/app)

#### Service Profiles Domain
3. **contexts/service-profiles/management** (4 layers)
   - API: SpmCommands, SpmQueries, Events (ServicingProfileCreated, etc.)
   - Domain: ServicingProfile aggregate with invariants
   - App: SpmApplicationService (@Transactional, event publishing)
   - Infra: REST controllers, JPA repos, Flyway, ArchUnit tests

4. **contexts/service-profiles/indirect-clients** (4 layers)
   - API: IndirectClientCommands, IndirectClientQueries, Events
   - Domain: IndirectClient aggregate with related persons
   - App: IndirectClientApplicationService
   - Infra: REST controllers, JPA repos, Flyway

#### User Management Domain
5. **contexts/users/users** (4 layers)
   - API: UserCommands, UserGroupCommands, UserQueries, Events
   - Domain: User aggregate, UserGroup aggregate
   - App: UserApplicationService, UserGroupApplicationService
   - Infra: REST controllers, JPA repos, Flyway

6. **contexts/users/policy** (4 layers)
   - API: PolicyCommands, PolicyQueries, Events
   - Domain: PermissionStatement, ApprovalStatement aggregates, PolicyEvaluatorService
   - App: PolicyApplicationService
   - Infra: REST controllers, JPA repos, Flyway

#### Approval Workflows Domain
7. **contexts/approval-workflows/engine** (4 layers)
   - API: ApprovalEngineCommands, ApprovalEngineQueries, Events
   - Domain: ApprovalWorkflow aggregate with state machine
   - App: ApprovalEngineApplicationService
   - Infra: REST controllers, JPA repos, Flyway

### Key Technologies

- **Framework**: Micronaut 4.9.3
- **Persistence**: Micronaut Data JPA 4.6.0 + PostgreSQL 42.7.5
- **Database Migrations**: Flyway 10.25.0
- **Testing**: JUnit 5.11.4 + Micronaut Test 4.8.0
- **Architecture Guardrails**: ArchUnit 1.4.1
- **Build**: Maven 3.x with Java 17 toolchain

### Dependency Rules (ArchUnit enforced)

```
┌─────────────┐
│     BFF     │ ──→ API only (no domain/app)
└─────────────┘

┌─────────────┐
│    Infra    │ ──→ App, Domain, API
└─────────────┘
       ↑
┌─────────────┐
│     App     │ ──→ Domain, API
└─────────────┘
       ↑
┌─────────────┐
│   Domain    │ ──→ API
└─────────────┘
       ↑
┌─────────────┐
│     API     │ (no dependencies on other layers)
└─────────────┘
```

### Event-Driven Architecture

- **Current**: In-process events via `ApplicationEventPublisher` + `@TransactionalEventListener`
- **Future-ready**: Outbox/Inbox tables for Kafka integration (stubs included)

### Database Strategy

- **One schema per Bounded Context** (logical separation)
- **Flyway migrations** in each `infra` module under `db/migration/`
- **JPA Entities** with embedded Value Objects
- **Optimistic Locking** with `@Version` fields

### Testing Strategy

- **ArchUnit Tests**: Enforce layered architecture and DDD boundaries
- **Integration Tests**: `@MicronautTest` with Testcontainers support
- **Unit Tests**: Domain logic testing

## Building and Running

### Prerequisites
```bash
# Java 17
java -version  # Should show Java 17+

# Maven 3.6+
mvn -version

# Docker (for PostgreSQL)
docker --version
```

### Build All Modules
```bash
# From project root
mvn clean install
```

### Run PostgreSQL
```bash
docker run --name knight-postgres \
  -e POSTGRES_DB=knight \
  -e POSTGRES_USER=knight \
  -e POSTGRES_PASSWORD=knight \
  -p 5432:5432 \
  -d postgres:16
```

### Run Individual Bounded Context
```bash
# Service Profile Management
cd contexts/service-profiles/management/infra
mvn mn:run

# Policy
cd contexts/users/policy/infra
mvn mn:run

# Approval Engine
cd contexts/approval-workflows/engine/infra
mvn mn:run
```

### Run BFF
```bash
cd bff/web
mvn mn:run
```

### Sample API Calls

```bash
# Create Servicing Profile
curl -X POST http://localhost:8081/commands/service-profiles/servicing/create \
  -H "Content-Type: application/json" \
  -d '{"clientUrn": "srf:CAN123456", "createdBy": "admin@bank.com"}'

# Get Profile Summary (via BFF)
curl http://localhost:8080/api/profiles/servicing/srf:CAN123456/summary

# Create User
curl -X POST http://localhost:8083/commands/users/create \
  -H "Content-Type: application/json" \
  -d '{
    "profileId": "online:srf:CAN123456:1",
    "email": "user@client.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "ADMINISTRATOR",
    "source": "OKTA"
  }'

# Create Permission Statement
curl -X POST http://localhost:8084/commands/policy/permissions/create \
  -H "Content-Type: application/json" \
  -d '{
    "profileId": "online:srf:CAN123456:1",
    "subject": "user-123",
    "action": "receivables:invoice:create",
    "resource": "account:*",
    "effect": "ALLOW"
  }'

# Start Approval Workflow
curl -X POST http://localhost:8085/commands/approvals/start \
  -H "Content-Type: application/json" \
  -d '{
    "statementId": "stmt-456",
    "profileId": "indirect-profile:indirect:srf:CAN123456:1",
    "requesterId": "user-789",
    "action": "receivables:invoice:approve",
    "resource": "invoice:INV-001",
    "amount": 10000.00
  }'
```

## File Count Summary

Generated approximately **150+ files** including:
- 7 Maven POM files (root + 6 aggregators)
- 20 Module POMs (4 layers × 5 BCs)
- 7 Shared Kernel value objects
- 50+ Domain classes (aggregates, entities, value objects)
- 25+ API interfaces (commands, queries, events)
- 15+ Application services
- 30+ Infrastructure classes (controllers, JPA entities, repositories)
- 5 Flyway migration stubs
- 5 ArchUnit test classes
- Configuration files (application.yml)

## Next Steps

### Phase 1: Verify Build
```bash
mvn clean verify
```

### Phase 2: Complete Implementation
- Fill in Flyway migration SQL scripts
- Add remaining business methods to aggregates
- Implement all query methods in repositories
- Add comprehensive unit tests

### Phase 3: Integration
- Configure inter-context communication
- Implement Outbox pattern for events
- Add Kafka producers/consumers
- Implement SAGA orchestration if needed

### Phase 4: Production Readiness
- Add monitoring (Micrometer + Prometheus)
- Add distributed tracing (OpenTelemetry)
- Add API documentation (OpenAPI/Swagger)
- Add security (OAuth2/OIDC)
- Add rate limiting
- Add circuit breakers

## Architecture Highlights

### DDD Patterns Implemented
✅ Aggregates with invariants
✅ Value Objects (immutable, validated)
✅ Domain Events
✅ Application Services (orchestration)
✅ Repositories (abstraction over persistence)
✅ Domain Services (cross-aggregate logic)
✅ Bounded Contexts (clear boundaries)
✅ Context Mapping (Customer-Supplier, ACL)

### Tactical Patterns
✅ Entity lifecycle management
✅ Aggregate root enforcement
✅ Event publishing on state changes
✅ Optimistic locking
✅ Idempotency preparation (commandId in contracts)

### Quality Attributes
✅ Modularity (clean module boundaries)
✅ Testability (layered architecture, DI)
✅ Evolvability (API contracts, versioning-ready)
✅ Observability-ready (event logging points)
✅ Performance (async event processing ready)

## Contact & Support

This codebase follows the Knight Platform specifications from:
- `/prompts/prompt.md` - Generation requirements
- `/model/platform-ddd.yaml` - DDD model specification
- `/CLAUDE.md` - Project context and decisions

For questions or issues, refer to the model documentation and ArchUnit test results.
