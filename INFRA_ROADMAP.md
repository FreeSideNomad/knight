# Infrastructure Roadmap - PostgreSQL & Kafka Integration

## Current Status (Phase 1: MVP Complete ✅)

The Knight Platform is currently a **working Micronaut DDD Modular Monolith** with:
- ✅ 5 bounded contexts fully implemented
- ✅ 4-layer DDD architecture
- ✅ In-memory repositories (MVP)
- ✅ In-process events (ApplicationEventPublisher)
- ✅ Flyway migrations defined (ready to execute)
- ✅ All tests passing (ArchUnit boundary enforcement)

## Next Phase: Production Infrastructure

### Phase 2: PostgreSQL Persistence
**Goal**: Replace in-memory repositories with real database persistence

**Scope**:
- Implement JPA entities and repositories
- Enable Flyway migrations
- Add connection pooling (HikariCP)
- Maintain schema-per-BC isolation
- Integration tests with Testcontainers

**Effort**: ~2 weeks (1 BC reference + 4 BCs adaptation)

### Phase 3: Kafka Event Bus
**Goal**: Replace in-process events with reliable async messaging

**Scope**:
- Implement Outbox pattern (transactional event publishing)
- Implement Inbox pattern (idempotent event consumption)
- Configure Kafka topics per domain
- Add Kafka consumers for cross-BC integration
- Event schema management (Avro or JSON)

**Effort**: ~2 weeks (patterns + integration)

### Phase 4: Local Development Environment
**Goal**: Docker Compose for easy local setup

**Scope**:
- PostgreSQL container
- Kafka + Zookeeper/KRaft
- Schema Registry
- AKHQ (Kafka UI)
- pgAdmin (optional)
- One-command startup: `docker-compose up`

**Effort**: ~3 days

---

## How to Use This Roadmap

### For Planning
Use the detailed prompt at `prompts/infra-prompt.md` to:
1. Generate implementation plan
2. Create work breakdown structure
3. Estimate effort per bounded context
4. Identify dependencies between tasks

### For Implementation
The prompt provides:
- Complete code samples
- Configuration templates
- Migration scripts
- Testing strategies
- Step-by-step checklist

### For Developers
1. **Read**: `prompts/infra-prompt.md` for complete specification
2. **Plan**: Break down into sprints (2-week iterations recommended)
3. **Implement**: Start with Service Profile Management as reference
4. **Test**: Use Testcontainers for integration testing
5. **Deploy**: Docker Compose for local, OpenShift for production

---

## Quick Start (For Implementers)

### Step 1: Generate Implementation Plan
```bash
# Use the infra-prompt.md with an LLM to generate:
# - Detailed architecture document
# - Code samples for all patterns
# - Complete docker-compose.yml
# - Migration strategy
```

### Step 2: Setup Local Infrastructure
```bash
# Start PostgreSQL and Kafka
docker-compose up -d

# Verify services are running
docker-compose ps

# Check logs
docker-compose logs -f postgres kafka
```

### Step 3: Implement Reference BC (Service Profile Management)
```bash
# 1. Add JPA entity
# 2. Add JPA repository
# 3. Add mapper (MapStruct)
# 4. Update repository impl
# 5. Add Outbox table migration
# 6. Add Outbox publisher
# 7. Add integration tests
```

### Step 4: Replicate to Other BCs
Follow the same pattern for:
- Indirect Client Management
- Users
- Policy
- Approval Workflow Engine

### Step 5: Validate End-to-End
```bash
# Run all tests
mvn test

# Start all services
docker-compose up -d
mvn mn:run  # For each BC in separate terminals

# Test cross-BC event flow
# Example: Create profile → User service receives event
```

---

## Dependencies to Add

### Root pom.xml
```xml
<properties>
    <!-- Already defined -->
    <micronaut.version>4.9.3</micronaut.version>
    <postgresql.version>42.7.5</postgresql.version>
    <flyway.version>11.3.3</flyway.version>

    <!-- New additions -->
    <micronaut.kafka.version>5.3.0</micronaut.kafka.version>
    <testcontainers.version>1.20.4</testcontainers.version>
    <mapstruct.version>1.6.3</mapstruct.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- Kafka Support -->
        <dependency>
            <groupId>io.micronaut.kafka</groupId>
            <artifactId>micronaut-kafka</artifactId>
            <version>${micronaut.kafka.version}</version>
        </dependency>

        <!-- Testcontainers -->
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-bom</artifactId>
            <version>${testcontainers.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Per BC infra/pom.xml
```xml
<dependencies>
    <!-- PostgreSQL (already included) -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Flyway (already included) -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- NEW: Kafka -->
    <dependency>
        <groupId>io.micronaut.kafka</groupId>
        <artifactId>micronaut-kafka</artifactId>
    </dependency>

    <!-- NEW: MapStruct for JPA mapping -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
    </dependency>

    <!-- NEW: Testcontainers (test scope) -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>kafka</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Architecture Comparison

### Before (MVP - Current)
```
Application Service
       ↓
In-Memory Repository → HashMap
       ↓
ApplicationEventPublisher → In-Process Event
       ↓
@EventListener (same JVM)
```

**Pros**: Fast, simple, no infrastructure
**Cons**: No persistence, no reliability, single instance only

### After (Production - Target)
```
Application Service
       ↓
JPA Repository → PostgreSQL (schema per BC)
       ↓
Outbox Table (same transaction)
       ↓
Outbox Publisher (scheduled) → Kafka Topic
       ↓
Kafka Consumer → Inbox Table
       ↓
Application Service (different BC)
```

**Pros**: Persistent, reliable, scalable, distributed
**Cons**: More complex, requires infrastructure

---

## Timeline Estimate

| Phase | Duration | Effort (person-days) |
|-------|----------|---------------------|
| **Planning & Design** | 3 days | 3 |
| **PostgreSQL Implementation** (1 BC reference) | 3 days | 3 |
| **PostgreSQL Replication** (4 BCs) | 4 days | 4 |
| **Kafka Outbox/Inbox** (1 BC reference) | 3 days | 3 |
| **Kafka Replication** (4 BCs) | 4 days | 4 |
| **Docker Compose Setup** | 2 days | 2 |
| **Integration Testing** | 3 days | 3 |
| **Documentation** | 2 days | 2 |
| **Buffer** | 3 days | 3 |
| **Total** | ~27 days | ~27 person-days |

**Recommended**: 2 developers × 2 weeks (with some parallel work)

---

## Success Criteria

Before considering Phase 2 complete, verify:

### PostgreSQL
- [ ] All 5 BCs use JPA repositories (no in-memory)
- [ ] Flyway migrations execute on startup
- [ ] Connection pooling configured (HikariCP)
- [ ] Schema isolation maintained
- [ ] Integration tests use Testcontainers
- [ ] Optimistic locking prevents concurrent updates

### Kafka
- [ ] All inter-BC events go through Kafka
- [ ] Outbox pattern implemented (transactional guarantees)
- [ ] Inbox pattern implemented (idempotent consumers)
- [ ] Topics created with proper partitioning
- [ ] Consumer groups configured per BC
- [ ] Dead-letter queue for failed messages
- [ ] Monitoring/observability (consumer lag, etc.)

### Developer Experience
- [ ] `docker-compose up` starts all infrastructure
- [ ] Clear documentation for local setup
- [ ] Integration tests run without manual setup
- [ ] Hot reload still works during development
- [ ] Debugging is straightforward

### Quality
- [ ] All existing unit tests still pass
- [ ] New integration tests cover happy paths
- [ ] Error scenarios tested (DB down, Kafka down)
- [ ] Performance acceptable (< 100ms per operation)
- [ ] No data loss under normal conditions

---

## Resources

- **Prompt**: `prompts/infra-prompt.md` (comprehensive specification)
- **Current Docs**: `IMPLEMENTATION_COMPLETE.md` (current state)
- **Deployment**: See earlier conversations for OpenShift deployment guide
- **Micronaut Docs**: https://docs.micronaut.io/
- **Kafka Docs**: https://kafka.apache.org/documentation/
- **Testcontainers**: https://testcontainers.com/

---

## Questions?

Refer to `prompts/infra-prompt.md` for detailed implementation guidance on:
- JPA entity mapping strategies
- Outbox/Inbox pattern implementation
- Kafka configuration
- Testcontainers setup
- Docker Compose configuration
- Migration strategies
- Troubleshooting guides

**Next Action**: Use the infra-prompt.md to generate the detailed implementation plan!
