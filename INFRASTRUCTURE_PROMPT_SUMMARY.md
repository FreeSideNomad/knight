# Infrastructure Prompt Summary

## Created Documents

### 1. `prompts/infra-prompt.md` ✅
**Purpose**: Comprehensive prompt for LLM to generate PostgreSQL & Kafka infrastructure implementation

**Contains**:
- Current state analysis
- Goals and requirements
- Complete code samples for:
  - JPA entity mapping
  - Micronaut Data repositories
  - Outbox pattern implementation
  - Inbox pattern implementation
  - Kafka producers/consumers
  - Testcontainers setup
  - Docker Compose configuration
- Migration strategy (step-by-step)
- Success criteria

**Use Case**: Feed this prompt to an LLM (like Claude) to generate a complete implementation plan

---

### 2. `INFRA_ROADMAP.md` ✅
**Purpose**: High-level roadmap and quick reference for developers

**Contains**:
- Current vs target architecture comparison
- Phase breakdown (PostgreSQL → Kafka → Docker)
- Effort estimates (~4 weeks total)
- Dependencies to add
- Success criteria checklist
- Timeline with person-days
- Quick start guide

**Use Case**: Project planning, sprint planning, developer onboarding

---

## How to Use These Documents

### Scenario 1: Generate Implementation Plan
```bash
# 1. Give infra-prompt.md to an LLM
# 2. LLM generates:
#    - Detailed architecture document
#    - All code implementations
#    - Complete docker-compose.yml
#    - Migration scripts
#    - Testing strategy
```

### Scenario 2: Plan Sprint Work
```bash
# 1. Review INFRA_ROADMAP.md
# 2. Break down into 2-week sprints
# 3. Assign tasks to developers
# 4. Track progress against success criteria
```

### Scenario 3: Implement Infrastructure
```bash
# Week 1-2: PostgreSQL
# - Use code samples from infra-prompt.md
# - Start with 1 BC (Service Profile Management)
# - Replicate pattern to other 4 BCs
# - Add Testcontainers integration tests

# Week 3-4: Kafka
# - Implement Outbox/Inbox pattern
# - Configure topics and consumers
# - Add event-driven integration tests
# - Setup Docker Compose

# Week 5: Polish & Documentation
# - End-to-end testing
# - Developer documentation
# - Troubleshooting guide
```

---

## Key Patterns Covered

### 1. JPA Repository Pattern
```
Domain Aggregate → JPA Entity → Micronaut Data Repository
                     ↓
                 MapStruct Mapper
                     ↓
              Repository Adapter
```

### 2. Outbox Pattern
```
Application Service
  ↓ (same transaction)
  ├─> Save Aggregate
  └─> Save Event to Outbox
       ↓ (async)
  Outbox Publisher → Kafka
```

### 3. Inbox Pattern
```
Kafka Consumer
  ↓
  ├─> Check Inbox (dedupe)
  ├─> Save to Inbox
  └─> Process Event
       ↓
  Update Inbox Status
```

---

## Technology Stack (After Implementation)

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Persistence** | PostgreSQL 16 | Aggregate storage |
| **ORM** | Micronaut Data JPA | Database access |
| **Migrations** | Flyway 11.3.3 | Schema versioning |
| **Messaging** | Apache Kafka | Event bus |
| **Serialization** | JSON or Avro | Event format |
| **Local Dev** | Docker Compose | Infrastructure |
| **Testing** | Testcontainers | Integration tests |
| **Mapping** | MapStruct 1.6.3 | Domain ↔ JPA |

---

## Benefits After Implementation

### Persistence (PostgreSQL)
✅ Durable storage (survives restarts)
✅ ACID transactions
✅ Query capabilities
✅ Production-ready
✅ Schema-per-BC isolation

### Messaging (Kafka)
✅ Reliable event delivery
✅ Horizontal scaling
✅ Event replay capability
✅ Decoupled bounded contexts
✅ Audit trail (event log)

### Developer Experience
✅ One-command setup: `docker-compose up`
✅ Integration tests with real infrastructure
✅ No manual database/Kafka setup
✅ Hot reload still works
✅ Easy debugging with Kafka UI

---

## Next Steps

1. **Review** both documents
2. **Plan** sprints using INFRA_ROADMAP.md
3. **Generate** implementation using infra-prompt.md
4. **Implement** following the generated plan
5. **Validate** against success criteria

---

## Estimated Effort

**Total**: 4-5 weeks (2 developers)

| Task | Duration |
|------|----------|
| PostgreSQL JPA implementation | 1.5 weeks |
| Kafka Outbox/Inbox implementation | 1.5 weeks |
| Docker Compose setup | 0.5 weeks |
| Integration testing | 1 week |
| Documentation & polish | 0.5 weeks |

---

## Success Metrics

- [ ] 0 in-memory repositories remaining
- [ ] 0 in-process events remaining
- [ ] 5 BCs with PostgreSQL persistence
- [ ] 5 BCs with Kafka integration
- [ ] 1 docker-compose.yml working
- [ ] N integration tests with Testcontainers
- [ ] 100% backward compatibility (domain/app unchanged)

---

🎯 **Result**: Production-ready infrastructure with reliable persistence and messaging!
