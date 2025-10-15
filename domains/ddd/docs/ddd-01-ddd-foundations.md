# DDD Foundations

## Core Philosophy

### What Problem Does DDD Solve?

Domain-Driven Design addresses the fundamental challenge of managing complexity in software systems by centering development on a rich domain model that reflects deep understanding of the business domain. Traditional approaches often create a gap between business experts and technical implementation, leading to:

- **Translation Errors**: Business concepts get lost or distorted in translation to code
- **Model Fragmentation**: Different parts of the system use inconsistent models
- **Maintenance Burden**: Changes to business logic require extensive code archeology
- **Communication Breakdown**: Developers and domain experts speak different languages

DDD solves these problems by making the domain model the heart of the software, using a common language (Ubiquitous Language) shared by all stakeholders, and providing patterns for managing complexity at both strategic and tactical levels.

### Why Does DDD Exist?

DDD emerged from Eric Evans' experiences in the early 2000s working on complex enterprise systems. He observed that:

1. **Complexity is Inevitable**: Complex domains require complex models; simplification loses essential knowledge
2. **Model Quality Matters**: The quality of the domain model directly impacts the quality and maintainability of the software
3. **Knowledge Crunching**: Effective design requires continuous collaboration between developers and domain experts
4. **Patterns Accelerate**: Recurring patterns in domain modeling can be cataloged and reused

DDD provides a systematic approach to tackling these challenges through a set of principles and patterns that have proven effective across diverse domains.

---

## Key Principles

### 1. **Ubiquitous Language**

**Definition**: A common, rigorous language built up between developers and domain experts, based on the domain model used in the software.

**Rationale**:
- Software cannot cope with ambiguity
- Translation between business language and technical language introduces errors
- A shared language ensures everyone is discussing the same concepts

**Implications**:
- Domain terminology must be embedded directly in the code (class names, method names, etc.)
- The language evolves as understanding deepens
- When language changes, the model must change
- Documentation uses the same language as the code

**Practice**:
- Develop language through ongoing conversations with domain experts
- Continuously test and refine the language
- Domain experts challenge inadequate terminology
- Developers identify potential ambiguities
- Language appears in code, documentation, and verbal communication

**Example**:
If domain experts talk about "Policy" meaning an insurance policy, the code should have a `Policy` class, not `InsuranceContract` or `Agreement`. Method names like `calculatePremium()` should match how experts describe the process.

---

### 2. **Model-Driven Design**

**Definition**: The software model is tightly linked to the domain model; code structure directly reflects domain concepts.

**Rationale**:
- Keeps code aligned with business reality
- Makes changes to business logic straightforward
- Allows domain experts to understand code structure
- Preserves design decisions in the code itself

**Implications**:
- Models are not just diagrams; they are working code
- Refactoring towards deeper insight is continuous
- Technical patterns serve the domain, not vice versa
- Architecture enables the domain model to shine through

**Practice**:
- Build models collaboratively with domain experts
- Express model concepts in code structure
- Continuously refine based on learning
- Keep model and implementation synchronized

---

### 3. **Continuous Learning and Refinement**

**Definition**: Understanding of the domain deepens over time through continuous collaboration and reflection.

**Rationale**:
- Initial understanding is always incomplete
- Hidden complexity emerges during implementation
- Domain knowledge exists in experts' heads, not in requirements documents

**Implications**:
- Expect the model to change
- Build in refactoring time
- Create safe spaces for experimentation
- Value breakthrough insights

**Practice**:
- Regular knowledge crunching sessions
- Refactoring towards deeper insight
- Breakthrough moments reshape the model
- Learning is captured in code and Ubiquitous Language

---

### 4. **Bounded Context**

**Definition**: An explicit boundary within which a particular domain model is defined and applicable.

**Rationale**:
- Large systems cannot have a single unified model
- Different parts of an organization use concepts differently
- Attempting total unification is not feasible or cost-effective

**Implications**:
- Each context has its own Ubiquitous Language
- The same term can mean different things in different contexts
- Context boundaries must be explicitly defined
- Integration between contexts requires translation

**Practice**:
- Identify linguistic boundaries
- Align contexts with team boundaries when possible
- Make context boundaries explicit in code organization
- Use Context Mapping to document relationships

---

### 5. **Focus on the Core Domain**

**Definition**: Identify the most valuable and differentiating part of the domain and concentrate effort there.

**Rationale**:
- Not all parts of the system are equally important
- Investment should match strategic value
- Core domain determines competitive advantage

**Implications**:
- Distinguish core, supporting, and generic subdomains
- Apply DDD most rigorously to the core domain
- Consider buying/outsourcing generic subdomains
- Protect the core from corruption

**Practice**:
- Distill the core domain through analysis
- Invest best resources in core
- Accept simpler solutions for supporting subdomains
- Use off-the-shelf for generic subdomains

---

## Foundational Patterns Overview

DDD provides two categories of patterns:

### Strategic Design Patterns

Strategic patterns address large-scale organization of the system:

1. **Bounded Context**: Defining boundaries where models apply
2. **Context Map**: Documenting relationships between contexts
3. **Core Domain**: Identifying the most important domain
4. **Subdomain**: Breaking large domains into manageable pieces
5. **Context Mapping Patterns**:
   - Shared Kernel
   - Customer/Supplier
   - Conformist
   - Anti-Corruption Layer
   - Open Host Service
   - Published Language
   - Separate Ways
   - Big Ball of Mud

Strategic patterns help answer:
- How do we organize a large domain?
- Where should we invest our effort?
- How do different contexts relate?
- How do we integrate across boundaries?

### Tactical Design Patterns

Tactical patterns address implementation within a bounded context:

1. **Entity**: Objects with identity and lifecycle
2. **Value Object**: Objects defined by their attributes
3. **Aggregate**: Consistency boundary around entities and value objects
4. **Aggregate Root**: Entry point to an aggregate
5. **Repository**: Abstraction for object persistence
6. **Domain Service**: Stateless operations that don't belong to an entity
7. **Application Service**: Orchestration of use cases
8. **Factory**: Complex object creation
9. **Domain Event**: Something that happened in the domain
10. **Module**: Code organization following domain concepts

Tactical patterns help answer:
- How do we structure domain objects?
- How do we maintain consistency?
- How do we persist domain objects?
- How do we handle complex operations?

---

## Layered Architecture

DDD typically employs a layered architecture to organize code:

### Four Standard Layers

```
┌─────────────────────────────────┐
│   User Interface Layer          │  Presentation, API endpoints
├─────────────────────────────────┤
│   Application Layer             │  Use case orchestration
├─────────────────────────────────┤
│   Domain Layer                  │  Business logic, domain model
├─────────────────────────────────┤
│   Infrastructure Layer          │  Persistence, external services
└─────────────────────────────────┘
```

### Layer Responsibilities

#### 1. User Interface (Presentation) Layer
- Displays information to users
- Interprets user commands
- Thin layer - no business logic
- Examples: REST controllers, GraphQL resolvers, UI components

#### 2. Application Layer
- Orchestrates domain objects to perform use cases
- Transaction management
- Security/authorization
- Delegates to domain layer for business logic
- Coordinates application workflow
- Examples: Application services, use case handlers

#### 3. Domain Layer
- **Heart of the software**
- Contains business logic
- Represents domain concepts
- Maintains business invariants
- Independent of infrastructure concerns
- Examples: Entities, value objects, aggregates, domain services

#### 4. Infrastructure Layer
- Provides technical capabilities
- Persistence mechanisms
- Message queues
- External service integration
- Examples: Repositories (implementation), ORM mappings, HTTP clients

### Dependency Direction

```
UI Layer ────→ Application Layer ────→ Domain Layer
                        ↓
                Infrastructure Layer
```

Key principle: **Domain layer depends on nothing** (or only on itself). Other layers may depend on domain layer, but domain layer is independent.

---

## Model-Driven Design Approach

### The Design Process

1. **Knowledge Crunching**
   - Deep collaboration between developers and domain experts
   - Exploring domain through conversation and modeling
   - Challenging assumptions
   - Finding the core concepts

2. **Modeling Out Loud**
   - Sketch models collaboratively
   - Try different arrangements
   - Use visual models (diagrams, sketches)
   - Verbal exploration of scenarios

3. **Express in Code**
   - Implement model concepts as code
   - Use Ubiquitous Language in naming
   - Make implicit concepts explicit
   - Structure reflects domain structure

4. **Refactor Toward Deeper Insight**
   - Recognize when the model is awkward
   - Look for breakthrough insights
   - Simplify by finding the right abstraction
   - Large-scale refactorings when justified

5. **Continuous Learning**
   - Expect the model to evolve
   - Value learning over adherence to plan
   - Capture learning in the code

### Signs of a Good Domain Model

- **Natural alignment with business concepts**: Domain experts recognize their mental model
- **Flexibility**: Accommodates foreseeable changes without major restructuring
- **Clear responsibilities**: Each concept has a well-defined purpose
- **Minimal coupling**: Changes in one area don't ripple excessively
- **Explicit invariants**: Business rules are clear and enforced
- **Testability**: Business scenarios can be tested without infrastructure

### Signs of a Struggling Model

- **Constant translation**: Developers translating between business terms and code
- **Anemic domain**: Objects are just data containers, logic elsewhere
- **Confusion**: Team members unclear about where logic belongs
- **Rigid**: Minor business changes require major refactoring
- **Leaky abstractions**: Infrastructure concerns bleeding into domain

---

## Initial Glossary

Terms to be fully defined in later documents:

### Strategic Design
- Bounded Context
- Context Map
- Core Domain
- Supporting Subdomain
- Generic Subdomain
- Shared Kernel
- Customer/Supplier
- Conformist
- Anti-Corruption Layer
- Open Host Service
- Published Language
- Separate Ways
- Partnership
- Distillation

### Tactical Design
- Entity
- Value Object
- Aggregate
- Aggregate Root
- Repository
- Domain Service
- Application Service
- Factory
- Domain Event
- Module
- Specification

### Process Terms
- Ubiquitous Language
- Knowledge Crunching
- Model Exploration
- Refactoring Toward Deeper Insight
- Breakthrough
- Supple Design
- Declarative Design
- Closure of Operations
- Intention-Revealing Interfaces
- Side-Effect-Free Functions
- Assertions
- Conceptual Contours
- Standalone Classes

---

## Comparison with Evans

Eric Evans emphasizes:
- **Collaboration is key**: Deep engagement with domain experts is non-negotiable
- **Code is the model**: The code is not a translation of the model; it IS the model
- **Iterative discovery**: Understanding emerges through iterative modeling
- **Strategic importance**: Not all parts deserve equal investment
- **Constraints liberate**: Patterns provide structure that enables creativity

---

## When to Apply DDD

### DDD is Most Valuable When:

✓ **Complex business logic**: The domain has intricate business rules and processes
✓ **Domain expertise exists**: There are true domain experts to collaborate with
✓ **Long-lived system**: The system will evolve over years
✓ **Strategic value**: The software provides competitive advantage
✓ **Learning opportunity**: The team is willing to invest in deep understanding
✓ **Complexity is essential**: Cannot be simplified away

### DDD May Be Overkill When:

✗ **Simple CRUD**: The system is mostly data storage and retrieval
✗ **Technical complexity dominates**: The challenges are mainly technical, not domain-related
✗ **Throwaway prototype**: Short-lived system with no evolution expected
✗ **Clear specification**: Requirements are complete and stable
✗ **No domain expertise**: Cannot access domain experts
✗ **Small scope**: The domain is simple enough to hold entirely in one head

---

## DDD and Agile

DDD and Agile are highly complementary:

**Shared Values:**
- Embracing change
- Continuous learning
- Close collaboration
- Working software over documentation
- Iterative refinement

**How They Support Each Other:**
- **Agile provides rhythm**: Sprints create opportunities for knowledge crunching
- **DDD provides depth**: Model gives structure to user stories
- **Shared understanding**: Ubiquitous Language supports agile communication
- **Refactoring culture**: Both value continuous improvement
- **Evolutionary design**: Both accept that design emerges

**Cautions:**
- DDD refactorings may span multiple sprints
- Don't sacrifice model quality for sprint commitment
- Some modeling needs upfront thought
- Strategic design requires looking beyond current sprint

---

## Common Misconceptions

### ❌ "DDD requires extensive upfront design"
✓ **Reality**: DDD embraces iterative discovery; the model emerges and evolves

### ❌ "DDD is just tactical patterns"
✓ **Reality**: Strategic design (Bounded Contexts, Context Mapping) is equally important

### ❌ "DDD means OOP with repositories and services"
✓ **Reality**: DDD is paradigm-agnostic; patterns apply to functional programming too

### ❌ "DDD requires event sourcing/CQRS/microservices"
✓ **Reality**: These patterns can complement DDD but are not requirements

### ❌ "The domain model must handle everything"
✓ **Reality**: Some concerns (logging, caching) belong in infrastructure

### ❌ "We need perfect domain knowledge before starting"
✓ **Reality**: Understanding deepens through implementation; start with incomplete knowledge

### ❌ "DDD is only for large enterprise systems"
✓ **Reality**: DDD scales; even small core domains benefit from clear modeling

---

## Relationship to Other Patterns and Practices

### PoEAA (Patterns of Enterprise Application Architecture)
- **Domain Model pattern**: Foundation of DDD
- **Repository**: Used in DDD for aggregate persistence
- **Service Layer**: Similar to DDD Application Services
- **Data Mapper**: Often used to implement DDD repositories
- PoEAA focuses more on technical patterns; DDD focuses on domain modeling

### Clean Architecture / Hexagonal Architecture
- **Shared goal**: Protect domain logic from infrastructure
- **Dependency inversion**: Both put domain at center
- **Ports and Adapters**: Maps well to DDD's infrastructure abstraction
- **Difference**: DDD provides more domain modeling patterns

### Event Sourcing
- **Complementary**: Can implement DDD aggregates with event sourcing
- **Domain Events**: Central to both
- **Not required**: Can do DDD without event sourcing
- **Benefits**: Audit trail, temporal queries, event replay

### CQRS (Command Query Responsibility Segregation)
- **Often paired with DDD**: Separate read and write models
- **Supports complex queries**: Read models optimized differently than write model
- **Not required**: Can do DDD with single model
- **Bounded Contexts**: CQRS read/write models are separate bounded contexts

### Microservices
- **Natural alignment**: Bounded Contexts map to services
- **Not synonymous**: Can have DDD monoliths or non-DDD microservices
- **Strategic design**: Context mapping patterns apply to service boundaries
- **Caution**: Don't let technical service boundaries dictate domain boundaries

---

## Next Steps in Research

The following areas require deeper exploration in subsequent documents:

1. **Strategic Patterns** (02-strategic-patterns.md)
   - Detailed Bounded Context patterns
   - All context mapping patterns
   - Subdomain identification
   - Core domain distillation

2. **Tactical Patterns** (03-tactical-patterns.md)
   - Entity and Value Object design
   - Aggregate design rules
   - Repository patterns
   - Service types
   - Domain Event patterns

3. **Ubiquitous Language** (04-ubiquitous-language.md)
   - Language development process
   - Documentation strategies
   - Evolution management
   - Team communication

4. **PoEAA Integration** (05-poeaa-integration.md)
   - View model patterns
   - Persistence patterns
   - Service layer patterns
   - Architectural alignment

---

## References

- Evans, Eric (2003). "Domain-Driven Design: Tackling Complexity in the Heart of Software"
- Fowler, Martin. "Domain Driven Design" - https://martinfowler.com/bliki/DomainDrivenDesign.html
- Fowler, Martin. "Bounded Context" - https://martinfowler.com/bliki/BoundedContext.html
- Fowler, Martin. "Ubiquitous Language" - https://martinfowler.com/bliki/UbiquitousLanguage.html
- Vernon, Vaughn (2013). "Implementing Domain-Driven Design"
- DDD Community resources - https://www.dddcommunity.org/

---

*Document Status: Foundation complete - ready for strategic patterns research*
