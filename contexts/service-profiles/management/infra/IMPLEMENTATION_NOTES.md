# Service Profile Management - Infrastructure Implementation

## Overview
Complete PostgreSQL + Kafka infrastructure implementation for the Service Profile Management bounded context, following DDD tactical patterns and using Micronaut framework.

## Architecture

### 4-Layer DDD Architecture
```
┌─────────────────────────────────────┐
│  API Layer (Controllers)            │
│  - REST endpoints                   │
│  - Request/Response DTOs            │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│  Application Layer (Services)       │
│  - Use cases                        │
│  - Command handlers                 │
│  - Transaction boundaries           │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│  Domain Layer (Aggregates)          │
│  - ServicingProfile (Aggregate Root)│
│  - Business logic                   │
│  - Domain events                    │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│  Infrastructure Layer               │
│  - JPA persistence                  │
│  - Kafka messaging                  │
│  - Outbox/Inbox patterns            │
└─────────────────────────────────────┘
```

## Components Implemented

### 1. PostgreSQL Persistence (JPA)

#### Entities (5 files)
- `ServicingProfileJpaEntity` - Aggregate root mapping
- `EnrolledServiceJpaEntity` - Service enrollment child entity
- `EnrolledAccountJpaEntity` - Account enrollment child entity
- `OutboxEventEntity` - Transactional outbox for events
- `InboxEventEntity` - Idempotent inbox for consumed events

#### Repositories (4 files)
- `ServicingProfileJpaRepository` - Micronaut Data JPA repository
- `OutboxEventRepository` - Outbox event queries with pagination
- `InboxEventRepository` - Inbox deduplication queries
- `ServicingProfileRepositoryImpl` - Adapter implementing domain interface

#### Mapper (1 file)
- `ServicingProfileMapper` - Manual mapper (domain ↔ JPA)
  - Note: Using manual mapping due to private constructors in domain aggregate
  - MapStruct cannot construct domain objects with private constructors

### 2. Kafka Messaging

#### Outbox Pattern
- **Purpose**: Ensures atomic writes to database and message broker
- **Flow**: Save aggregate + outbox event in same transaction → scheduled publisher polls outbox → publish to Kafka → mark as published
- **Files**:
  - `OutboxPublisher` - @Scheduled task (every 5 seconds)
  - `OutboxEventEntity` - Stores pending events
  - Status: PENDING → PUBLISHED → FAILED (with retry count)

#### Inbox Pattern
- **Purpose**: Idempotent event consumption (prevent duplicate processing)
- **Flow**: Receive event → check inbox for event_id → if not exists, process + save to inbox → if exists, skip
- **Files**:
  - `ServicingProfileEventConsumer` - @KafkaListener
  - `InboxEventEntity` - Deduplication store

#### Producers/Consumers
- `ServicingProfileEventProducer` - Kafka producer wrapper
- `ServicingProfileEventConsumer` - Kafka consumer with inbox checking

### 3. Database Migrations (Flyway)

Located in `src/main/resources/db/migration/`:
- `V2__create_servicing_profiles.sql` - Main aggregate table
- `V3__add_outbox_table.sql` - Transactional outbox
- `V4__add_inbox_table.sql` - Idempotent inbox
- `V5__add_indexes.sql` - Performance indexes

All tables use schema: `spm`

### 4. Configuration Files

#### `application.yml`
```yaml
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/knight
    schema-generate: NONE

jpa:
  default:
    entity-scan:
      packages: 'com.knight.contexts.serviceprofiles.management.infra.persistence.entity'
    properties:
      hibernate:
        default_schema: spm

flyway:
  datasources:
    default:
      schemas: spm
      locations: classpath:db/migration

kafka:
  bootstrap.servers: localhost:9092
  producer:
    acks: all
    enable-idempotence: true
  consumer:
    group-id: service-profile-management
```

#### `application-test.yml`
```yaml
# Datasource configured dynamically by TestPropertyProvider
flyway:
  datasources:
    default:
      enabled: true
      schemas: spm

kafka:
  enabled: false  # Disable in repository tests

jpa:
  default:
    entity-scan:
      packages: 'com.knight.contexts.serviceprofiles.management.infra.persistence.entity'
    properties:
      hibernate:
        default_schema: spm
```

### 5. Integration Tests (3 files)

#### `ServicingProfileRepositoryIntegrationTest`
- **Purpose**: Test JPA persistence with Testcontainers
- **Features**:
  - PostgreSQL container (postgres:16-alpine)
  - TestPropertyProvider for dynamic datasource
  - Tests save/retrieve and update operations

#### `OutboxPublisherIntegrationTest`
- **Purpose**: Test outbox pattern
- **Features**:
  - Verify events saved to outbox
  - Test scheduled publisher polling
  - Verify status transitions (PENDING → PUBLISHED)

#### `EventPublishConsumeIntegrationTest`
- **Purpose**: End-to-end Kafka integration
- **Features**:
  - PostgreSQL + Kafka containers
  - Test complete flow: save aggregate → outbox → Kafka → consumer → inbox
  - Verify idempotency (duplicate events ignored)

### 6. Local Development Setup

#### `docker-compose.yml` (Project Root)
```yaml
services:
  postgres:
    image: postgres:16-alpine
    ports: ["5432:5432"]
    environment:
      POSTGRES_DB: knight
      POSTGRES_USER: knight
      POSTGRES_PASSWORD: knight

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports: ["2181:2181"]

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports: ["9092:9092"]

  schema-registry:
    image: confluentinc/cp-schema-registry:7.5.0
    ports: ["8081:8081"]

  akhq:
    image: tchiotludo/akhq:latest
    ports: ["8080:8080"]

  pgadmin:
    image: dpage/pgadmin4:latest
    ports: ["5050:80"]
```

**Usage**:
```bash
# Start all services
docker-compose up -d

# View Kafka topics/messages
open http://localhost:8080  # AKHQ

# View PostgreSQL
open http://localhost:5050  # pgAdmin
```

## Dependencies

### Runtime
- `io.micronaut.data:micronaut-data-jpa`
- `io.micronaut.sql:micronaut-hibernate-jpa`
- `io.micronaut.sql:micronaut-jdbc-hikari`
- `io.micronaut.kafka:micronaut-kafka`
- `org.postgresql:postgresql`
- `org.flywaydb:flyway-core`
- `org.flywaydb:flyway-database-postgresql`
- `io.micronaut.serde:micronaut-serde-jackson`

### Test
- `io.micronaut.test:micronaut-test-junit5`
- `org.testcontainers:testcontainers`
- `org.testcontainers:postgresql`
- `org.testcontainers:kafka`
- `org.testcontainers:junit-jupiter`
- `commons-codec:commons-codec` (required for Kafka Testcontainers)
- `org.awaitility:awaitility`
- `org.assertj:assertj-core`

## Key Design Decisions

### 1. Manual Mapper vs MapStruct
**Decision**: Use manual mapper
**Reason**: Domain aggregate has private constructor (tactical DDD pattern). MapStruct cannot construct domain objects directly.

### 2. Secondary Repository
**Decision**: Mark `InMemoryServicingProfileRepository` as `@Secondary`
**Reason**: Allows both JPA and in-memory implementations to coexist. JPA takes precedence when available.

### 3. Outbox Polling Interval
**Decision**: 5 seconds (`@Scheduled(fixedDelay = "5s")`)
**Reason**: Balance between latency and database load. Adjust based on throughput requirements.

### 4. Schema-per-BC
**Decision**: Each bounded context uses separate schema (`spm`, `icm`, `users`, `policy`, `approvals`)
**Reason**: Clear boundaries, independent migrations, supports eventual microservices extraction.

### 5. Testcontainers Configuration
**Decision**: Use `TestPropertyProvider` instead of static JDBC URLs
**Reason**: Allows dynamic port binding and proper container lifecycle management.

## Known Issues & Solutions

### Issue: No bean of type [PrimaryRepositoryOperations]
**Symptoms**: Integration tests fail with "No backing RepositoryOperations configured"
**Root Cause**: Micronaut Data JPA not finding entity classes
**Solutions Applied**:
1. Added `entity-scan.packages` to `application.yml`
2. Added `packages` parameter to `@MicronautTest`
3. Created `JpaConfiguration` factory class
4. Ensured `micronaut-hibernate-jpa` dependency is present

**If issue persists**:
- Verify PostgreSQL driver is on classpath
- Check Hibernate is bootstrapping (enable `show_sql: true`)
- Ensure `@Entity` annotations present on all JPA entities
- Verify DataSource bean is created (check application startup logs)

## Replication Guide

To replicate this pattern to other bounded contexts:

### 1. Copy Structure
```bash
# Copy infra module as template
cp -r contexts/service-profiles/management/infra \
      contexts/YOUR_CONTEXT/YOUR_BC/infra
```

### 2. Update Package Names
- Find/replace: `serviceprofiles.management` → `YOUR_CONTEXT.YOUR_BC`
- Update schema name: `spm` → `YOUR_SCHEMA`

### 3. Update Domain Mapping
- Modify `YourAggregateJpaEntity` to match your domain model
- Update mapper to handle your aggregate's structure
- Adjust repositories for your query needs

### 4. Update Migrations
- Rename migration files with new version numbers
- Update table/schema names
- Adjust columns to match your aggregate

### 5. Update Configuration
- Change `application.name`
- Update `server.port` (8082, 8083, 8084, 8085)
- Update Kafka `group-id`
- Update JPA `entity-scan.packages`

### 6. Update Tests
- Rename test classes
- Update test data to match your domain
- Adjust assertions for your business logic

## Performance Tuning

### Connection Pool (HikariCP)
```yaml
datasources:
  default:
    maximum-pool-size: 10      # Max connections
    minimum-idle: 5            # Min idle connections
    connection-timeout: 30000  # 30s
    idle-timeout: 600000       # 10min
    max-lifetime: 1800000      # 30min
```

### Hibernate Batch Processing
```yaml
jpa:
  default:
    properties:
      hibernate:
        jdbc.batch_size: 20
        order_inserts: true
        order_updates: true
```

### Kafka Producer
```yaml
kafka:
  producer:
    acks: all                              # Strongest durability
    enable-idempotence: true              # Exactly-once semantics
    max-in-flight-requests-per-connection: 1  # Ordering guarantee
    compression-type: snappy              # Fast compression
```

## Monitoring

### Database
- Connection pool metrics via Micrometer
- Hibernate statistics (enable in dev only)
- Flyway migration status endpoint

### Kafka
- Producer/consumer metrics via Micrometer
- Outbox processing lag
- Failed event retry count

### Application
- Health check: `http://localhost:8081/health`
- Metrics: `http://localhost:8081/metrics`

## Security Considerations

### Database
- Use environment variables for credentials
- Enable SSL for production (`ssl=true` in JDBC URL)
- Restrict user permissions (GRANT only needed privileges)

### Kafka
- Enable SASL/SSL for production
- Use separate credentials per bounded context
- Configure ACLs for topic access

## Troubleshooting

### Tests fail with "Docker not found"
**Solution**: Ensure Docker Desktop is running

### Flyway migration fails
**Solution**: Check schema exists, run `CREATE SCHEMA IF NOT EXISTS spm;`

### Kafka connection refused
**Solution**: Verify Kafka is running: `docker ps | grep kafka`

### Outbox events not publishing
**Solution**:
1. Check scheduler is enabled (`@Scheduled` not disabled)
2. Verify Kafka is reachable
3. Check outbox table for FAILED events with error messages

## Next Steps

1. ✅ Service Profile Management - COMPLETE
2. ⏳ Replicate to Indirect Client Management
3. ⏳ Replicate to Users
4. ⏳ Replicate to Policy
5. ⏳ Replicate to Approval Workflows

---

**Author**: Claude Code
**Date**: 2025-10-17
**Version**: 1.0
