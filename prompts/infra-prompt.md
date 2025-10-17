# infra-prompt.md — Add PostgreSQL & Kafka Infrastructure Support

You are an expert software architect specializing in microservices infrastructure, event-driven architecture, and containerization. Based on the existing **Micronaut DDD Modular Monolith**, generate a **complete infrastructure implementation plan** to add PostgreSQL persistence and Kafka messaging.

## Current State

The platform currently has:
- ✅ 5 Bounded Contexts (Service Profile Management, Indirect Clients, Users, Policy, Approval Workflows)
- ✅ 4-layer DDD architecture (API, Domain, App, Infra)
- ✅ Micronaut 4.9.3 framework
- ✅ In-memory repositories (MVP implementation)
- ✅ ApplicationEventPublisher for in-process events
- ✅ Flyway migrations defined (V1__initial_schema.sql per BC)
- ✅ Separate database schemas per BC (logical separation)

## Goals

### 1. PostgreSQL Persistence
- Replace in-memory repositories with **Micronaut Data JPA** implementations
- Use **PostgreSQL 16** as the database
- Maintain **schema-per-bounded-context** isolation
- Support **transactional integrity** within each BC
- Enable **connection pooling** (HikariCP)
- Flyway migrations execute automatically on startup

### 2. Kafka Messaging
- Replace in-process events with **Kafka topics** for inter-BC communication
- Implement **Outbox Pattern** for reliable event publishing
- Implement **Inbox Pattern** for idempotent event consumption
- Support **exactly-once semantics** where possible
- Use **Avro** or **JSON** for event serialization
- Enable **Kafka Streams** for event processing (optional advanced feature)

### 3. Local Development
- Provide **Docker Compose** setup with:
  - PostgreSQL 16 (single instance, multiple schemas)
  - Kafka (with Zookeeper or KRaft mode)
  - Schema Registry (if using Avro)
  - Kafka UI / AKHQ (for event inspection)
  - pgAdmin (optional, for database inspection)
- All services start with: `docker-compose up`
- Bounded contexts connect to local Docker infrastructure
- Support for **hot reload** during development

### 4. Integration Testing
- Use **Testcontainers** for integration tests
- Spin up ephemeral PostgreSQL and Kafka containers per test
- Test repositories with real database
- Test event publishing/consumption end-to-end
- Clean state between tests

---

## Deliverables Required

### Phase 1: Planning Document
Create a detailed implementation plan covering:

#### 1.1 PostgreSQL Implementation
- **JPA Entity Mapping Strategy**
  - How to map domain aggregates to JPA entities
  - Handling of embedded value objects
  - Collection mapping strategies (related persons, approvals, etc.)
  - Optimistic locking (@Version)

- **Repository Implementation**
  - Convert `InMemoryXxxRepository` to Micronaut Data JPA
  - Interface definition with `@JdbcRepository` or `@Repository(value = "default")`
  - Custom query methods using `@Query`
  - Transaction boundaries (@Transactional placement)

- **Schema Management**
  - PostgreSQL schema creation strategy
  - Flyway configuration per bounded context
  - Migration versioning strategy
  - Rollback procedures

- **Connection Configuration**
  - HikariCP connection pool settings
  - Schema selection per datasource
  - Connection URL patterns
  - Environment-specific configuration (dev, test, prod)

#### 1.2 Kafka Implementation
- **Topic Design**
  - Naming convention: `{domain}.{context}.{event-type}`
  - Example: `service-profiles.management.servicing-profile-created`
  - Partitioning strategy (by aggregate ID)
  - Retention policies
  - Compaction for entity snapshots (if applicable)

- **Outbox Pattern**
  - Outbox table schema per BC
  - Polling publisher vs CDC (Debezium) approach
  - Transaction coupling (same TX as aggregate save)
  - Retry and dead-letter queue strategy

- **Inbox Pattern**
  - Inbox table schema per BC
  - Idempotency key design (event ID)
  - Duplicate detection
  - Processing state tracking (PENDING, PROCESSED, FAILED)

- **Event Schema**
  - Event envelope structure (id, type, timestamp, correlationId, payload)
  - Versioning strategy (v1, v2 in topic or payload)
  - Avro vs JSON trade-offs
  - Backward/forward compatibility

- **Consumer Groups**
  - One consumer group per BC
  - Concurrent consumers configuration
  - Offset management (auto-commit vs manual)
  - Error handling and retry

#### 1.3 Configuration Management
- **Datasource Configuration**
  ```yaml
  # Example structure for application.yml
  datasources:
    default:
      url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:knight}
      username: ${DB_USER:knight}
      password: ${DB_PASSWORD:knight}
      schema: ${DB_SCHEMA:spm}  # Per BC
      driver-class-name: org.postgresql.Driver
      maximum-pool-size: 10
      minimum-idle: 5

  flyway:
    datasources:
      default:
        enabled: true
        schemas: ${DB_SCHEMA:spm}
        locations: classpath:db/migration
  ```

- **Kafka Configuration**
  ```yaml
  kafka:
    bootstrap.servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    producers:
      default:
        key.serializer: org.apache.kafka.common.serialization.StringSerializer
        value.serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
        schema.registry.url: ${SCHEMA_REGISTRY_URL:http://localhost:8081}
    consumers:
      default:
        key.deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value.deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
        group.id: ${spring.application.name}
        auto.offset.reset: earliest
        enable.auto.commit: false
  ```

#### 1.4 Docker Compose Setup
Provide complete `docker-compose.yml` with:
- PostgreSQL 16 container
- Kafka container (Confluent or Apache)
- Zookeeper (if needed) or KRaft
- Schema Registry
- AKHQ (Kafka UI)
- pgAdmin (optional)
- Health checks
- Volume mounts for data persistence
- Network configuration

#### 1.5 Testing Strategy
- **Unit Tests**: Mock repositories (no change needed)
- **Integration Tests**:
  - Testcontainers setup
  - Repository tests with real PostgreSQL
  - Event publish/consume tests with real Kafka
  - Transaction rollback tests
  - Idempotency tests (duplicate event handling)

---

## Phase 2: Code Samples

Provide working code examples for:

### 2.1 JPA Repository Implementation

**Domain to JPA Entity Mapping**:
```java
// Domain Aggregate (no changes)
public class ServicingProfile {
    private final ServicingProfileId id;
    private ClientId clientId;
    private Status status;
    private List<EnrolledService> enrolledServices;
    // ... domain methods
}

// JPA Entity
@Entity
@Table(name = "servicing_profiles", schema = "spm")
public class ServicingProfileJpaEntity {
    @Id
    private String id;  // URN format

    @Column(name = "client_id", nullable = false)
    private String clientId;  // URN format

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private List<EnrolledServiceJpaEntity> enrolledServices;

    @Version
    private Long version;  // Optimistic locking

    // Getters, setters, constructors
}

// Micronaut Data Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ServicingProfileJpaRepository
    extends CrudRepository<ServicingProfileJpaEntity, String> {

    Optional<ServicingProfileJpaEntity> findByClientId(String clientId);

    @Query("SELECT p FROM ServicingProfileJpaEntity p WHERE p.status = :status")
    List<ServicingProfileJpaEntity> findByStatus(Status status);
}

// Mapper (MapStruct)
@Mapper(componentModel = "jsr330")
public interface ServicingProfileMapper {
    ServicingProfile toDomain(ServicingProfileJpaEntity entity);
    ServicingProfileJpaEntity toEntity(ServicingProfile domain);
}

// Repository Implementation (Adapter)
@Singleton
public class ServicingProfileRepositoryImpl implements ServicingProfileRepository {
    private final ServicingProfileJpaRepository jpaRepository;
    private final ServicingProfileMapper mapper;

    @Override
    @Transactional
    public void save(ServicingProfile profile) {
        var entity = mapper.toEntity(profile);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<ServicingProfile> findById(ServicingProfileId id) {
        return jpaRepository.findById(id.urn())
            .map(mapper::toDomain);
    }
}
```

### 2.2 Outbox Pattern Implementation

**Outbox Table**:
```sql
-- db/migration/V2__add_outbox_table.sql
CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    correlation_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    INDEX idx_outbox_status_created (status, created_at)
);
```

**Outbox Entity & Repository**:
```java
@Entity
@Table(name = "outbox", schema = "spm")
public class OutboxEvent {
    @Id
    private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    @Column(columnDefinition = "jsonb")
    private String payload;
    private UUID correlationId;
    private Instant createdAt;
    private Instant publishedAt;
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    private int retryCount;
}

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OutboxRepository extends CrudRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);
}
```

**Event Publishing**:
```java
@Singleton
public class SpmApplicationService implements SpmCommands {
    private final ServicingProfileRepository repo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public ServicingProfileId createServicingProfile(ClientId clientId, String createdBy) {
        // 1. Create aggregate
        var profile = ServicingProfile.create(clientId, createdBy);

        // 2. Save aggregate
        repo.save(profile);

        // 3. Save event to outbox (same transaction!)
        var event = new ServicingProfileCreated(profile.getId(), clientId);
        var outboxEvent = OutboxEvent.builder()
            .id(UUID.randomUUID())
            .aggregateType("ServicingProfile")
            .aggregateId(profile.getId().urn())
            .eventType("ServicingProfileCreated")
            .payload(objectMapper.writeValueAsString(event))
            .correlationId(UUID.randomUUID())
            .createdAt(Instant.now())
            .status(OutboxStatus.PENDING)
            .build();
        outboxRepo.save(outboxEvent);

        return profile.getId();
    }
}
```

**Outbox Publisher (Scheduled)**:
```java
@Singleton
public class OutboxPublisher {
    private final OutboxRepository outboxRepo;
    private final KafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = "5s")
    @Transactional
    public void publishPendingEvents() {
        var events = outboxRepo.findByStatusOrderByCreatedAtAsc(
            OutboxStatus.PENDING,
            Pageable.from(0, 100)
        );

        for (var event : events) {
            try {
                kafkaProducer.send(
                    event.getEventType(),
                    event.getAggregateId(),
                    event.getPayload()
                ).get(10, TimeUnit.SECONDS);

                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());
                outboxRepo.update(event);

            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= 5) {
                    event.setStatus(OutboxStatus.FAILED);
                }
                outboxRepo.update(event);
            }
        }
    }
}
```

### 2.3 Inbox Pattern & Kafka Consumer

**Inbox Table**:
```sql
-- V3__add_inbox_table.sql
CREATE TABLE IF NOT EXISTS inbox (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);
```

**Kafka Consumer with Inbox**:
```java
@Singleton
public class UserEventConsumer {
    private final InboxRepository inboxRepo;
    private final UserApplicationService userService;

    @KafkaListener(
        topics = "service-profiles.management.servicing-profile-created",
        groupId = "users-service"
    )
    @Transactional
    public void onServicingProfileCreated(
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        ServicingProfileCreated event
    ) {
        // 1. Check if already processed (idempotency)
        if (inboxRepo.existsById(event.getEventId())) {
            return;  // Already processed, skip
        }

        // 2. Save to inbox (dedupe)
        var inboxEvent = InboxEvent.builder()
            .eventId(event.getEventId())
            .eventType(event.getClass().getSimpleName())
            .payload(objectMapper.writeValueAsString(event))
            .receivedAt(Instant.now())
            .status(InboxStatus.PENDING)
            .build();
        inboxRepo.save(inboxEvent);

        // 3. Process event
        try {
            userService.handleServicingProfileCreated(event);

            inboxEvent.setStatus(InboxStatus.PROCESSED);
            inboxEvent.setProcessedAt(Instant.now());
            inboxRepo.update(inboxEvent);

        } catch (Exception e) {
            inboxEvent.setStatus(InboxStatus.FAILED);
            inboxRepo.update(inboxEvent);
            throw e;  // Trigger Kafka retry
        }
    }
}
```

### 2.4 Docker Compose

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: knight-postgres
    environment:
      POSTGRES_DB: knight
      POSTGRES_USER: knight
      POSTGRES_PASSWORD: knight
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docker/init-schemas.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U knight"]
      interval: 10s
      timeout: 5s
      retries: 5

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: knight-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: knight-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    healthcheck:
      test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
      interval: 10s
      timeout: 10s
      retries: 5

  schema-registry:
    image: confluentinc/cp-schema-registry:7.5.0
    container_name: knight-schema-registry
    depends_on:
      - kafka
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: kafka:9092

  akhq:
    image: tchiotludo/akhq:latest
    container_name: knight-akhq
    depends_on:
      - kafka
      - schema-registry
    ports:
      - "8085:8080"
    environment:
      AKHQ_CONFIGURATION: |
        akhq:
          connections:
            docker:
              properties:
                bootstrap.servers: "kafka:9092"
              schema-registry:
                url: "http://schema-registry:8081"

  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: knight-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@knight.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

**Schema Initialization**:
```sql
-- docker/init-schemas.sql
CREATE SCHEMA IF NOT EXISTS spm;
CREATE SCHEMA IF NOT EXISTS indirect_clients;
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS policy;
CREATE SCHEMA IF NOT EXISTS approvals;
```

### 2.5 Testcontainers Setup

```java
@MicronautTest
@Testcontainers
class ServicingProfileRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("knight")
        .withUsername("knight")
        .withPassword("knight")
        .withInitScript("init-test-schema.sql");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    @Inject
    ServicingProfileRepository repository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("datasources.default.url", postgres::getJdbcUrl);
        registry.add("datasources.default.username", postgres::getUsername);
        registry.add("datasources.default.password", postgres::getPassword);
        registry.add("kafka.bootstrap.servers", kafka::getBootstrapServers);
    }

    @Test
    void shouldSaveAndRetrieveProfile() {
        // Given
        var clientId = ClientId.of("srf:CAN123");
        var profile = ServicingProfile.create(clientId, "test@example.com");

        // When
        repository.save(profile);
        var retrieved = repository.findById(profile.getId());

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getClientId()).isEqualTo(clientId);
    }
}
```

---

## Phase 3: Migration Strategy

Provide step-by-step migration plan:

### Step 1: Add Dependencies
- Update root `pom.xml` with PostgreSQL, Kafka, Testcontainers dependencies
- Add versions to `<properties>` section

### Step 2: Implement Per Bounded Context
For **each BC** (start with Service Profile Management as reference):
1. Create JPA entities
2. Create MapStruct mappers
3. Implement JPA repository
4. Create Outbox table migration
5. Implement Outbox publisher
6. Update ApplicationService to use Outbox
7. Create Inbox table migration
8. Implement Kafka consumers with Inbox
9. Add integration tests

### Step 3: Local Development Setup
1. Create `docker-compose.yml` in root
2. Create `docker/init-schemas.sql`
3. Create `README-INFRA.md` with setup instructions
4. Test all services with Docker infrastructure

### Step 4: Validation
1. Run all unit tests (should still pass)
2. Run integration tests with Testcontainers
3. Start Docker Compose
4. Start all bounded contexts
5. Test end-to-end scenarios with Kafka events

---

## Constraints & Requirements

1. **Maintain DDD Boundaries**: Persistence is infrastructure concern only
2. **No Breaking Changes**: Domain and Application layers remain unchanged
3. **Transaction Boundaries**: One transaction per aggregate save + outbox event
4. **Schema Isolation**: Each BC owns its schema, no cross-schema queries
5. **Event Versioning**: Support for event schema evolution
6. **Idempotency**: Duplicate events must be safely ignored
7. **Observability**: Log outbox publishing, inbox processing, Kafka consumer lag
8. **Performance**: Connection pooling, batch event publishing where possible

---

## Deliverable Format

Provide the implementation plan as a **structured document** with:
1. **Architecture Diagrams** (text-based or Mermaid)
2. **Database Schema DDL** (Flyway migrations)
3. **Code Samples** (complete, compilable Java)
4. **Configuration Files** (application.yml, docker-compose.yml)
5. **Testing Guide** (how to run tests, sample scenarios)
6. **Migration Checklist** (step-by-step task list)
7. **Troubleshooting Guide** (common issues and solutions)

---

## Success Criteria

✅ All repositories use PostgreSQL (no in-memory)
✅ All inter-BC events go through Kafka (no in-process)
✅ Outbox/Inbox pattern implemented in all BCs
✅ Docker Compose starts all infrastructure
✅ Integration tests use Testcontainers
✅ All existing unit tests still pass
✅ No domain/app layer changes required
✅ Event delivery is reliable (at-least-once)
✅ Duplicate events are handled (idempotency)
✅ Developer experience is smooth (docker-compose up → ready to code)

---

*This prompt should generate a complete, production-ready infrastructure implementation plan that developers can follow step-by-step to add PostgreSQL and Kafka to the Knight Platform.*
