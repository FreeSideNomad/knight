# Knight Platform - Build & Run Instructions

## Overview

This is a complete Micronaut 4.9.3 + Java 17 DDD Modular Monolith with 5 bounded contexts implementing commercial banking cash management platform capabilities.

## Prerequisites

```bash
# Java 17
java -version  # Should show Java 17 or higher

# Maven 3.6+
mvn -version

# Docker (for PostgreSQL)
docker --version
```

## Quick Start

### 1. Build All Modules

```bash
# From project root (/Users/igor/code/knight)
mvn clean install -DskipTests
```

### 2. Start PostgreSQL

```bash
# Run PostgreSQL container
docker run --name knight-postgres \
  -e POSTGRES_USER=knight \
  -e POSTGRES_PASSWORD=knight \
  -e POSTGRES_DB=knight \
  -p 5432:5432 \
  -d postgres:16

# Create additional databases for each bounded context
docker exec knight-postgres psql -U knight -c "CREATE DATABASE knight_spm;"
docker exec knight-postgres psql -U knight -c "CREATE DATABASE knight_indirect;"
docker exec knight-postgres psql -U knight -c "CREATE DATABASE knight_users;"
docker exec knight-postgres psql -U knight -c "CREATE DATABASE knight_policy;"
docker exec knight-postgres psql -U knight -c "CREATE DATABASE knight_approvals;"
```

### 3. Run Individual Bounded Contexts

Each bounded context runs as a standalone Micronaut application on a different port:

```bash
# Service Profile Management (port 8081)
cd contexts/service-profiles/management/infra
mvn mn:run

# Indirect Client Management (port 8082)
cd contexts/service-profiles/indirect-clients/infra
mvn mn:run

# Users (port 8083)
cd contexts/users/users/infra
mvn mn:run

# Policy (port 8084)
cd contexts/users/policy/infra
mvn mn:run

# Approval Engine (port 8085)
cd contexts/approval-workflows/engine/infra
mvn mn:run

# BFF (port 8080)
cd bff/web
mvn mn:run
```

## Testing the Implementation

### Create Servicing Profile

```bash
curl -X POST http://localhost:8081/commands/service-profiles/servicing/create \
  -H "Content-Type: application/json" \
  -d '{
    "clientUrn": "srf:CAN123456",
    "createdBy": "admin@bank.com"
  }'
```

Expected response:
```json
{
  "profileUrn": "servicing:srf:CAN123456"
}
```

### Enroll Service

```bash
curl -X POST http://localhost:8081/commands/service-profiles/servicing/enroll-service \
  -H "Content-Type: application/json" \
  -d '{
    "profileUrn": "servicing:srf:CAN123456",
    "serviceType": "BTR",
    "configurationJson": "{\"reportFrequency\":\"DAILY\"}"
  }'
```

### Get Profile Summary (via BFF)

```bash
curl http://localhost:8080/api/profiles/servicing/srf:CAN123456/summary
```

Expected response:
```json
{
  "profileUrn": "servicing:srf:CAN123456",
  "status": "ACTIVE",
  "enrolledServices": 1,
  "enrolledAccounts": 0
}
```

## Architecture Overview

### Bounded Contexts

1. **Service Profile Management** (port 8081)
   - Manages servicing profiles for clients
   - Service and account enrollments
   - Database: `knight_spm`

2. **Indirect Client Management** (port 8082)
   - Manages indirect clients (payors)
   - Business information and related persons
   - Database: `knight_indirect`

3. **Users** (port 8083)
   - User lifecycle management
   - User groups for policy subjects
   - Database: `knight_users`

4. **Policy** (port 8084)
   - Permission and approval policies
   - Policy evaluation service
   - Database: `knight_policy`

5. **Approval Engine** (port 8085)
   - Generic approval workflow execution
   - Parallel approval support
   - Database: `knight_approvals`

6. **BFF** (port 8080)
   - UI composition layer
   - Depends only on BC APIs

### Layer Dependencies

```
Infra → App → Domain → API
  ↓       ↓      ↓      ↓
REST   Txn   Logic  Contract
```

## Development Workflow

### Running Tests

```bash
# Run all tests including ArchUnit
mvn test

# Run specific bounded context tests
cd contexts/service-profiles/management/infra
mvn test
```

### Database Migrations

Flyway migrations run automatically on application startup. Migration files are in:
```
contexts/*/infra/src/main/resources/db/migration/
```

### Adding New Features

1. Define command/query in **API layer**
2. Implement business logic in **Domain layer** (aggregate methods)
3. Orchestrate in **Application layer** (transaction + events)
4. Expose REST endpoint in **Infrastructure layer**

## Monitoring & Health Checks

Each Micronaut application exposes health endpoints:

```bash
# Service Profile Management
curl http://localhost:8081/health

# Users
curl http://localhost:8083/health
```

## Troubleshooting

### Build Failures

```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

### Database Connection Issues

```bash
# Check PostgreSQL is running
docker ps | grep knight-postgres

# View logs
docker logs knight-postgres

# Restart container
docker restart knight-postgres
```

### Port Conflicts

If ports are already in use, modify `application.yml` in each infra module:
```yaml
micronaut:
  server:
    port: <new-port>
```

## Project Structure

```
knight-platform/
├── pom.xml (root aggregator)
├── platform/
│   └── shared-kernel/ (value objects)
├── bff/
│   └── web/ (UI composition)
└── contexts/
    ├── service-profiles/
    │   ├── management/ (4 layers)
    │   └── indirect-clients/ (4 layers)
    ├── users/
    │   ├── users/ (4 layers)
    │   └── policy/ (4 layers)
    └── approval-workflows/
        └── engine/ (4 layers)
```

Each bounded context has 4 layers:
- **api/**: Commands, Queries, Events
- **domain/**: Aggregates, Entities, Domain Services
- **app/**: Application Services (orchestration)
- **infra/**: REST, Persistence, Migrations

## Next Steps

1. ✅ Build project: `mvn clean install`
2. ✅ Start PostgreSQL
3. ✅ Run bounded contexts
4. ✅ Test with curl commands
5. 📝 Add business validations
6. 📝 Implement remaining aggregates
7. 📝 Add comprehensive tests
8. 📝 Configure observability (metrics, tracing)
9. 📝 Add security (OAuth2/OIDC)
10. 📝 Deploy to environment

## Support

Refer to:
- `/GENERATION_SUMMARY.md` - Complete implementation details
- `/model/platform-ddd.yaml` - DDD model specification
- `/prompts/prompt.md` - Architecture requirements
- `/CLAUDE.md` - Project context and decisions
