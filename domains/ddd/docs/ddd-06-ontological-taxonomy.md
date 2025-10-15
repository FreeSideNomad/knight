# DDD Ontological Taxonomy

## Complete Pattern Hierarchy

### Level 1: System Organization (Strategic)
```
Enterprise System
├── Domain
│   ├── Core Domain
│   ├── Supporting Subdomain
│   └── Generic Subdomain
├── Bounded Context (multiple)
│   └── Context Map (relationships)
└── Integration Patterns
```

### Level 2: Bounded Context Internal Structure
```
Bounded Context
├── Ubiquitous Language
├── Domain Layer
│   ├── Aggregates
│   │   ├── Aggregate Root (Entity)
│   │   ├── Entities
│   │   └── Value Objects
│   ├── Domain Services
│   ├── Domain Events
│   ├── Factories
│   └── Specifications
├── Application Layer
│   └── Application Services
├── Infrastructure Layer
│   ├── Repositories (implementations)
│   ├── Data Mappers
│   └── External Service Adapters
└── Presentation Layer
    └── View Models
```

---

## Pattern Categories

### 1. Strategic Patterns (System-Level)

**Purpose**: Organize large systems and manage complexity

**Patterns:**
- **Domain** (Core, Supporting, Generic)
- **Bounded Context**
- **Context Map**
- **Context Mapping Patterns**: Partnership, Shared Kernel, Customer/Supplier, Conformist, Anti-Corruption Layer, Open Host Service, Published Language, Separate Ways, Big Ball of Mud

---

### 2. Tactical Patterns (Implementation-Level)

**Purpose**: Structure domain model within a bounded context

**Core Building Blocks:**
- **Entity**: Identity-based objects
- **Value Object**: Attribute-based immutable objects
- **Aggregate**: Consistency boundary
- **Aggregate Root**: Entry point to aggregate

**Supporting Patterns:**
- **Repository**: Persistence abstraction
- **Factory**: Complex object creation
- **Domain Service**: Multi-object operations
- **Application Service**: Use case orchestration
- **Domain Event**: Something that happened
- **Specification**: Business rule encapsulation
- **Module**: Code organization

---

### 3. Process Patterns

**Purpose**: How to apply DDD

**Patterns:**
- **Ubiquitous Language**: Shared vocabulary
- **Knowledge Crunching**: Collaborative discovery
- **Model Exploration**: Understanding through prototyping
- **Continuous Refinement**: Iterative improvement
- **Distillation**: Identifying core domain

---

## Pattern Dependencies

### Must-Have Patterns
Every DDD implementation needs:
1. **Bounded Context** - defines scope
2. **Ubiquitous Language** - shared understanding
3. **Entity** OR **Value Object** - domain objects
4. **Repository** - persistence (usually)

### Common Combinations

**Rich Domain Model:**
```
Bounded Context
└── Aggregates
    ├── Entities
    ├── Value Objects
    └── Domain Events
```

**Persistence:**
```
Aggregate Root
└── Repository
    └── Data Mapper (implementation)
```

**Cross-Aggregate Communication:**
```
Aggregate
└── Domain Event
    └── Event Handler (in another aggregate)
```

---

## Decision Frameworks

### When to Apply DDD

**Apply DDD when:**
- ✓ Complex business logic
- ✓ Domain expertise available
- ✓ Long-lived system
- ✓ Competitive advantage from domain

**Don't apply DDD when:**
- ✗ Simple CRUD
- ✗ No domain experts
- ✗ Technical complexity dominates
- ✗ Short-term project

### Strategic Design Decisions

```
1. Identify Domain
   ↓
2. Break into Subdomains
   ├→ Core Domain (invest heavily)
   ├→ Supporting (adequate quality)
   └→ Generic (buy/reuse)
   ↓
3. Define Bounded Contexts
   ↓
4. Map Context Relationships
   ├→ Same team? → Partnership or Shared Kernel
   ├→ Upstream/Downstream? → Customer/Supplier or Conformist
   └→ External? → Anti-Corruption Layer
```

### Tactical Design Decisions

```
Modeling Domain Concept:

Has Identity? ───YES──→ Entity
    │
    NO
    ↓
Defined by attributes? ───YES──→ Value Object
```

```
Grouping Objects:

Need consistency boundary? ───YES──→ Aggregate
    │                              ├→ Choose Root (main entity)
    NO                              └→ Add only what's needed
    ↓
Separate objects
```

```
Business Logic:

Belongs to one object? ───YES──→ Entity/Value Object method
    │
    NO
    ↓
Spans objects in aggregate? ───YES──→ Aggregate Root method
    │
    NO
    ↓
Spans multiple aggregates? ───YES──→ Domain Service
    │
    NO
    ↓
Just orchestration? ───YES──→ Application Service
```

---

## Pattern Relationships Matrix

| Pattern | Contains | Uses | Publishes | Implemented By |
|---------|----------|------|-----------|----------------|
| Bounded Context | Aggregates, Services | Ubiquitous Language | - | Team |
| Aggregate | Entities, Value Objects | - | Domain Events | - |
| Aggregate Root | Entities, VOs | Repository | Domain Events | - |
| Entity | Value Objects | - | - | - |
| Repository | - | Data Mapper | - | Infrastructure |
| Domain Service | - | Entities, VOs, Repos | - | - |
| Application Service | - | Domain Services, Repos | - | - |
| Factory | - | - | - | Domain or Infra |

---

## Complete Pattern Catalog

### Strategic (9 patterns)

1. Domain
2. Subdomain (Core/Supporting/Generic)
3. Bounded Context
4. Context Map
5. Partnership
6. Shared Kernel
7. Customer/Supplier
8. Conformist
9. Anti-Corruption Layer
10. Open Host Service
11. Published Language
12. Separate Ways
13. Big Ball of Mud

### Tactical (12 patterns)

1. Entity
2. Value Object
3. Aggregate
4. Aggregate Root
5. Repository
6. Factory
7. Domain Service
8. Application Service
9. Domain Event
10. Specification
11. Module
12. Layered Architecture

### Process (5 patterns)

1. Ubiquitous Language
2. Knowledge Crunching
3. Model Exploration
4. Continuous Refinement
5. Distillation

### Supporting (from PoEAA)

1. Data Mapper
2. Unit of Work
3. Identity Map
4. Service Layer
5. Presentation Model
6. MVC/MVP/MVVM

---

## Anti-Pattern Catalog

1. **Anemic Domain Model**: Entities with no behavior
2. **Big Ball of Mud**: No clear structure (sometimes unavoidable for legacy)
3. **Smart UI**: Business logic in presentation
4. **God Object**: One object does everything
5. **Primitive Obsession**: Not using Value Objects
6. **Feature Envy**: Methods using data from other objects
7. **Shotgun Surgery**: Changes require modifications everywhere
8. **Leaky Abstraction**: Domain knows about persistence
9. **Transaction Script in Domain**: Procedural logic in domain layer
10. **Too Many Aggregates in Transaction**: Violating one-aggregate rule

---

## Evolution Paths

### From Simple to Complex

**Level 1: Simple Domain**
```
Simple Objects + Transaction Scripts
```

**Level 2: Rich Domain Model**
```
Entities + Value Objects
├→ Validation in constructors
└→ Business methods
```

**Level 3: Aggregates**
```
Aggregates with Roots
├→ Consistency boundaries
├→ Reference by ID
└→ Domain Events
```

**Level 4: Strategic Design**
```
Bounded Contexts
├→ Context Maps
├→ Multiple models
└→ Anti-Corruption Layers
```

### Refactoring Patterns

**Primitive → Value Object:**
```java
// Before
String email;

// After
Email email;
```

**Anemic → Rich:**
```java
// Before
class Order {
    OrderStatus status;
    // ... getters/setters
}

// Service does everything
orderService.submitOrder(order);

// After
class Order {
    public void submit() {
        // Business logic here
    }
}
```

**Large Aggregate → Multiple Small Aggregates:**
```java
// Before
Customer aggregate contains all Orders

// After
Customer aggregate (just customer data)
Order aggregates (separate)
Reference: Customer.customerId in Order
```

---

## Integration with Other Practices

### DDD + Event Sourcing
- Aggregates produce events
- Events persisted as primary storage
- Current state derived from events
- Natural fit for DDD

### DDD + CQRS
- Write model: DDD aggregates
- Read model: Separate, optimized
- Domain events sync read model
- Different models for different purposes

### DDD + Microservices
- Bounded Context → Microservice
- Context mapping → Service integration
- Share nothing (or minimal sharing)
- Domain events → Message bus

### DDD + Hexagonal Architecture
- Domain at center
- Ports = Repositories, External Service interfaces
- Adapters = Infrastructure implementations
- Natural alignment

---

## References

- Evans, Eric (2003). "Domain-Driven Design"
- Vernon, Vaughn (2013). "Implementing Domain-Driven Design"
- Fowler, Martin. DDD articles
- DDD Community resources

---

*Document Status: Ontological taxonomy complete*
