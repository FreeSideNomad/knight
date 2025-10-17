# Knight Platform - Prompts Index

This document catalogs all available prompts and documentation for generating and evolving the Knight Platform.

---

## 📋 Available Prompts

### 1. **Core Platform Generation** (`prompts/prompt.md`)
**Purpose**: Generate Micronaut-based DDD modular monolith from scratch

**Status**: ✅ **USED** - Current implementation generated with this prompt

**Generates**:
- Complete Maven multi-module structure
- Shared kernel with value objects
- BFF composition layer
- Bounded contexts with 4-layer DDD architecture:
  - API layer (commands, queries, events)
  - Domain layer (aggregates, entities, value objects)
  - Application layer (services with @Transactional)
  - Infrastructure layer (REST controllers, repositories, tests)

**Tech Stack**:
- Micronaut 4.9.3
- Java 17
- Micronaut Data JPA
- Flyway migrations
- ArchUnit tests
- Micrometer metrics

**When to Use**:
- Starting new bounded context from DDD model
- Regenerating code after domain model changes
- Creating reference implementation for new team members

**Input Required**:
- YAML DDD model (`model/platform-ddd.yaml`)
- Artifact coordinates (groupId, artifactId, version)

**Output**:
- Complete Java codebase
- POMs for all modules
- Application configuration
- Flyway migration stubs
- ArchUnit architectural tests

---

### 2. **Infrastructure Integration** (`prompts/infra-prompt.md`)
**Purpose**: Add PostgreSQL persistence and Kafka messaging to existing implementation

**Status**: ✅ **CREATED** - Ready to use for Phase 2

**Generates**:
- JPA entity implementations
- Micronaut Data repositories
- Outbox pattern for reliable event publishing
- Inbox pattern for idempotent event consumption
- Docker Compose setup
- Testcontainers integration tests
- Migration strategy

**Tech Stack Additions**:
- PostgreSQL 16
- Apache Kafka
- Schema Registry (Avro)
- Testcontainers
- MapStruct (domain ↔ JPA mapping)

**When to Use**:
- Moving from MVP (in-memory) to production (persistent)
- Adding event-driven integration between bounded contexts
- Setting up local development environment
- Implementing integration tests

**Input Required**:
- Existing Micronaut codebase
- Domain model knowledge
- Infrastructure requirements

**Output**:
- Complete implementation plan
- JPA entities and repositories
- Outbox/Inbox implementations
- Kafka producers and consumers
- docker-compose.yml
- Testcontainers test setup
- Migration guide

---

### 3. **Domain Modeling Workflow** (`prompts/workflow-prompt.md`)
**Purpose**: Guide iterative development of DDD models (strategic & tactical)

**Status**: ✅ **COMPLETED** - Used to create current domain model

**Phases**:
1. Vision & Technology Foundation
2. Strategic Domain Models (domains, bounded contexts, context maps)
3. MVP Release Planning
4. Tactical DDD Refinement (aggregates, entities, value objects)
5. Cross-Model Enrichment (UX, Data-Eng, QE, Agile)

**Output Models**:
- `platform-ddd.yaml` - Strategic + Tactical DDD
- `platform-data-eng.yaml` - Data pipelines & contracts
- `platform-ux.yaml` - UI/UX specification
- `platform-qe.yaml` - Test strategy
- `platform-agile.yaml` - Product vision & releases

**When to Use**:
- Starting new platform from business requirements
- Refining existing domain model
- Planning MVP scope
- Aligning architecture with business strategy

---

### 4. **Platform Description** (`prompts/egg.md`)
**Purpose**: Business context and domain description for commercial banking platform

**Status**: ✅ **REFERENCE** - Context document

**Contains**:
- Business domain overview
- Client types (small business, commercial, large commercial)
- Geographic coverage (Canada, US, UK)
- Client identification systems (SRF, GID, IND)
- Account types (DDA, FCA, OLB, etc.)
- Service categories (stand-alone, online, indirect)
- User management requirements
- Permission & approval policies

**When to Use**:
- Understanding business context
- Onboarding new team members
- Validating domain model against requirements
- Clarifying terminology

---

## 📚 Supporting Documentation

### Implementation Documentation

#### `IMPLEMENTATION_COMPLETE.md`
**Complete implementation status of all 5 bounded contexts**
- Architecture overview
- Technology stack
- API endpoints
- Database schemas
- Build & test results
- Running instructions

#### `INFRA_ROADMAP.md`
**Roadmap for PostgreSQL & Kafka integration**
- Phase breakdown
- Timeline estimates
- Dependencies to add
- Success criteria
- Quick start guide

#### `INFRASTRUCTURE_PROMPT_SUMMARY.md`
**Summary of infrastructure prompt and roadmap**
- How to use infra-prompt.md
- Key patterns (Outbox, Inbox, JPA)
- Technology stack
- Benefits & success metrics

#### `CLAUDE.md`
**Project workflow and context**
- Current phase tracking
- Workflow resume instructions
- Key decisions log
- Terminology reminder
- Python environment setup
- Schema validation requirements

---

## 🔄 Typical Workflow

### Phase 1: Domain Modeling (✅ COMPLETE)
```
1. Read: prompts/egg.md (business context)
2. Use: prompts/workflow-prompt.md
3. Output: model/platform-ddd.yaml
4. Validate: ./validate-schema.sh
```

### Phase 2: Code Generation (✅ COMPLETE)
```
1. Read: model/platform-ddd.yaml
2. Use: prompts/prompt.md
3. Output: Complete Micronaut codebase
4. Build: mvn clean package
5. Test: mvn test (ArchUnit validates architecture)
```

### Phase 3: Infrastructure (📋 NEXT)
```
1. Read: INFRA_ROADMAP.md (planning)
2. Use: prompts/infra-prompt.md
3. Generate: Implementation plan
4. Implement: PostgreSQL + Kafka
5. Setup: docker-compose.yml
6. Test: Integration tests with Testcontainers
```

---

## 📖 Quick Reference

### For Project Planning
- **Roadmap**: `INFRA_ROADMAP.md`
- **Current Status**: `IMPLEMENTATION_COMPLETE.md`
- **Workflow**: `prompts/workflow-prompt.md`

### For Development
- **Code Generation**: `prompts/prompt.md`
- **Infrastructure**: `prompts/infra-prompt.md`
- **Domain Context**: `prompts/egg.md`

### For Validation
- **Schema Validation**: `./validate-schema.sh`
- **ArchUnit Tests**: Run `mvn test`
- **Build Verification**: `mvn clean package`

---

## 🎯 Prompt Usage Patterns

### Pattern 1: Generate New Bounded Context
```markdown
Input: prompts/prompt.md + model/platform-ddd.yaml
LLM: Generate complete 4-layer implementation
Output: contexts/{domain}/{context}/ (api, domain, app, infra)
Validate: ArchUnit tests enforce boundaries
```

### Pattern 2: Add Infrastructure
```markdown
Input: prompts/infra-prompt.md + existing codebase
LLM: Generate JPA + Kafka implementation
Output: JPA entities, Outbox/Inbox, docker-compose.yml
Validate: Integration tests with Testcontainers
```

### Pattern 3: Refine Domain Model
```markdown
Input: prompts/workflow-prompt.md + business feedback
LLM: Generate refined DDD model
Output: Updated platform-ddd.yaml
Validate: ./validate-schema.sh
Regenerate: Use prompts/prompt.md to update code
```

---

## 🔧 Tools & Scripts

### Validation
- `./validate-schema.sh` - Validate YAML models against schemas
- `./convert-yaml.sh` - Convert YAML to Markdown for review
- `mvn test` - Run ArchUnit architectural tests

### Generation
- Use prompts with LLM (Claude, GPT-4, etc.)
- Iterate based on feedback
- Validate before committing

### Deployment
- Docker Compose (local development)
- OpenShift (production) - see deployment guides in conversation history

---

## 📊 Implementation Status

| Component | Status | Prompt Used |
|-----------|--------|-------------|
| Domain Model | ✅ Complete | workflow-prompt.md |
| Shared Kernel | ✅ Complete | prompt.md |
| BFF | ✅ Complete | prompt.md |
| Service Profile Management BC | ✅ Complete | prompt.md |
| Indirect Client Management BC | ✅ Complete | prompt.md |
| Users BC | ✅ Complete | prompt.md |
| Policy BC | ✅ Complete | prompt.md |
| Approval Workflow Engine BC | ✅ Complete | prompt.md |
| PostgreSQL Integration | 📋 Planned | infra-prompt.md |
| Kafka Integration | 📋 Planned | infra-prompt.md |
| Docker Compose | 📋 Planned | infra-prompt.md |
| Testcontainers Tests | 📋 Planned | infra-prompt.md |

---

## 🚀 Next Actions

### Immediate (Next Sprint)
1. Review `INFRA_ROADMAP.md` for planning
2. Use `prompts/infra-prompt.md` to generate implementation plan
3. Implement PostgreSQL persistence (1 BC reference + replicate)
4. Implement Kafka messaging (Outbox/Inbox pattern)
5. Create docker-compose.yml for local development

### Future Enhancements
1. API Gateway implementation (see conversation history)
2. Multi-repo extraction (see multi-repo vs monorepo discussion)
3. Security (JWT, OAuth2)
4. Observability (Grafana dashboards)
5. CI/CD pipeline (Tekton, GitLab CI)

---

## 💡 Tips & Best Practices

### When Using Prompts
1. **Always validate** YAML models before code generation
2. **Start with reference BC** (Service Profile Management)
3. **Replicate pattern** to other bounded contexts
4. **Test incrementally** after each bounded context
5. **Keep domain/app layers unchanged** when adding infrastructure

### When Evolving Architecture
1. **DDD boundaries are sacred** - never violate with shortcuts
2. **Events are immutable** - create new versions, don't modify
3. **Schema per BC** - no shared tables across contexts
4. **Outbox for reliability** - never publish events without transaction
5. **Inbox for idempotency** - always dedupe on consumer side

---

## 📞 Support & Resources

### Internal
- `CLAUDE.md` - Project context and workflow
- Conversation history - Detailed Q&A and decisions
- ArchUnit tests - Living architecture documentation

### External
- Micronaut Docs: https://docs.micronaut.io/
- DDD Reference: https://www.domainlanguage.com/
- Event Sourcing: https://martinfowler.com/eaaDev/EventSourcing.html
- Outbox Pattern: https://microservices.io/patterns/data/transactional-outbox.html

---

**Last Updated**: October 17, 2025
**Platform Version**: 0.1.0-SNAPSHOT
**Phase**: MVP Complete, Infrastructure Planning
