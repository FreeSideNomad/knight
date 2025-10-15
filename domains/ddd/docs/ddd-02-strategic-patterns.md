# Strategic Design Patterns

## Overview

Strategic Design in Domain-Driven Design addresses the challenge of organizing large, complex systems. While tactical patterns focus on implementation details within a bounded context, strategic patterns focus on:

- **System decomposition**: Breaking large domains into manageable pieces
- **Context boundaries**: Defining where models apply
- **Team organization**: Aligning teams with domain boundaries
- **Integration patterns**: Managing relationships between contexts
- **Investment decisions**: Where to focus effort and resources

Eric Evans emphasizes: "Strategic design is essential for maintaining order in large systems and systems that are critical to the enterprise."

---

## Bounded Context

### Definition
A **Bounded Context** is an explicit boundary within which a particular domain model is defined and applicable. It defines the scope where a specific Ubiquitous Language and model are consistently used.

### Purpose & Rationale

**Why Bounded Contexts are necessary:**
- Large systems cannot have a single unified model that satisfies all stakeholders
- Different parts of an organization use the same terms to mean different things
- Attempting total unification of the domain model is neither feasible nor cost-effective
- Polysemic terms (words with multiple meanings) are common in organizations
- Different contexts require different levels of detail and different perspectives

**The problem Bounded Context solves:**
Without explicit boundaries, models become muddled. Teams talk past each other because they use the same words with different meanings. Code becomes confused as concepts leak between contexts.

### Characteristics

1. **Explicit Boundary**: The context has a clearly defined boundary (team, codebase, database schema, etc.)
2. **Unified Model**: Within the boundary, the model is unified and consistent
3. **Ubiquitous Language**: Has its own language that may differ from other contexts
4. **Team Ownership**: Typically owned by a single team
5. **Independence**: Can evolve somewhat independently of other contexts
6. **Clear Interfaces**: Defines how it interacts with other contexts

### When to Use

Create separate Bounded Contexts when:
- ✓ Different teams have different understandings of a concept
- ✓ The same term means different things in different areas
- ✓ Different business processes require different models
- ✓ Scalability requires separating concerns
- ✓ Legacy systems need isolation from new development
- ✓ Different update frequencies or consistency requirements exist

### How to Identify Boundaries

**Linguistic Boundaries:**
- Listen for when the same word means different things
- Notice when definitions become complex or contradictory
- Pay attention to phrases like "it depends on the context"

**Organizational Boundaries:**
- Department boundaries often indicate context boundaries
- Different business processes
- Separate budget authorities
- Different compliance requirements

**Technical Boundaries:**
- Different databases
- Different deployment schedules
- Different technology stacks
- Different scalability requirements

**Process:**
1. Map the domain with domain experts
2. Identify linguistic discontinuities
3. Look for natural seams in the business
4. Consider team structure
5. Examine existing system boundaries
6. Test proposed boundaries with scenarios

### Relationships with Other Patterns

- **Contains**: Aggregates, Entities, Value Objects, Repositories, Services
- **Defined by**: Ubiquitous Language
- **Part of**: Domain or Subdomain
- **Related through**: Context Mapping patterns
- **Protects**: Model integrity

### Common Mistakes

1. **Too Large**: Context encompasses too much, model becomes confused
2. **Too Small**: Over-fragmentation, excessive integration complexity
3. **Fuzzy Boundaries**: Unclear where one context ends and another begins
4. **Ignored Boundaries**: Contexts defined but not enforced in code
5. **Premature Decomposition**: Creating contexts before understanding the domain
6. **Technical Boundaries Only**: Ignoring linguistic and organizational factors

### Examples

**E-commerce System:**

**Sales Context:**
- `Product`: Item being sold, pricing, availability, promotion
- `Customer`: Buyer, shipping address, payment method
- `Order`: Purchase transaction, items, total, shipping

**Inventory Context:**
- `Product`: SKU, stock levels, warehouse location, reorder point
- `Supplier`: Vendor relationships, lead times
- `Inventory Movement`: Stock in, stock out, transfers

**Shipping Context:**
- `Shipment`: Package, tracking number, carrier, delivery status
- `Address`: Validated shipping address
- `Package`: Dimensions, weight, contents

Notice: "Product" appears in all three contexts but with different attributes and behaviors relevant to each context.

### Decision Tree

```
Does the team use the same terms differently?
├─ YES → Separate contexts
└─ NO → Could different business processes need different models?
    ├─ YES → Separate contexts
    └─ NO → Do teams have different priorities/schedules?
        ├─ YES → Consider separate contexts
        └─ NO → Single context may be appropriate
```

---

## Context Map

### Definition
A **Context Map** is a document that describes the relationships between different Bounded Contexts, showing how they integrate and depend on each other.

### Purpose
- Provides a global view of the system
- Makes integration patterns explicit
- Helps teams coordinate
- Identifies potential problems
- Guides refactoring decisions

### Components of a Context Map
1. **Bounded Contexts**: Boxes representing each context
2. **Relationships**: Lines showing connections
3. **Integration Patterns**: Labels indicating the type of relationship
4. **Directionality**: Upstream/downstream flow
5. **Teams**: Which teams own which contexts

### Example Context Map Notation

```
[Sales Context] --Customer/Supplier--> [Inventory Context]
        |
        |--Conformist--> [Legacy Billing]
        |
        |--ACL--> [External Payment Gateway]

[Shipping Context] <--Shared Kernel--> [Inventory Context]

[Analytics Context] --Separate Ways-- (no direct integration)
```

---

## Domain & Subdomains

### Domain

**Definition**: The entire business area or problem space that the software addresses.

**Characteristics:**
- Encompasses all business activities
- Typically too large for a single unified model
- Contains multiple subdomains
- Defines the scope of the entire system

**Example**: An e-commerce company's domain includes sales, inventory, shipping, customer service, marketing, analytics, etc.

---

### Core Domain

**Definition**: The part of the domain that provides competitive advantage and is most valuable to the business. This is where the organization must excel.

**Characteristics:**
- **Strategic Importance**: Critical to business success
- **Competitive Differentiation**: What makes this business unique
- **Complex**: Often involves sophisticated business logic
- **High Investment**: Deserves the best resources
- **Custom Built**: Should not be outsourced
- **Evolves Frequently**: Business innovation happens here

**Identification Criteria:**
- ✓ If this fails, the business fails
- ✓ Competitors cannot easily replicate
- ✓ Generates revenue or saves significant costs
- ✓ Domain experts are most engaged here
- ✓ Frequent changes and refinements

**Investment Strategy:**
- Assign the best developers
- Apply DDD most rigorously
- Invest in deep modeling
- Iterate and refine continuously
- Protect from technical debt

**Examples:**
- **Netflix**: Recommendation algorithm
- **Amazon**: Product search and ranking
- **Uber**: Ride matching and pricing
- **Airbnb**: Trust and safety, search quality

---

### Supporting Subdomain

**Definition**: A subdomain that supports the core domain but doesn't provide competitive advantage. It's necessary but not differentiating.

**Characteristics:**
- **Necessary but not Strategic**: Required for operations
- **No Differentiation**: Competitors have similar solutions
- **Moderate Complexity**: Some custom logic required
- **Moderate Investment**: Adequate resources
- **Build vs Buy**: Could outsource but might customize
- **Stable**: Changes less frequently than core

**Identification Criteria:**
- ✓ Necessary for core domain to function
- ✓ No competitive advantage if done well
- ✓ Some business-specific requirements
- ✓ Moderate complexity

**Investment Strategy:**
- Adequate quality, not perfection
- Consider buying and customizing
- Simpler modeling than core domain
- Good enough is good enough
- May use junior developers for implementation

**Examples:**
- **E-commerce**: Order management, basic inventory
- **SaaS Product**: User management, billing
- **Healthcare**: Appointment scheduling
- **Logistics**: Driver scheduling

---

### Generic Subdomain

**Definition**: A subdomain that is necessary but completely generic across industries. No customization provides business value.

**Characteristics:**
- **Commodity**: Same across all companies
- **No Customization Value**: Standard solution is best
- **Well-Understood**: Solved problems
- **Minimal Investment**: Lowest resources
- **Buy Don't Build**: Strong candidate for off-the-shelf
- **Very Stable**: Rarely changes

**Identification Criteria:**
- ✓ Every company needs this
- ✓ No business-specific requirements
- ✓ Off-the-shelf solutions exist
- ✓ No competitive advantage in customization

**Investment Strategy:**
- Buy off-the-shelf solutions
- Use open-source libraries
- Minimize customization
- Assign minimal team attention
- Isolate from core domain

**Examples:**
- Authentication and authorization (unless security IS your business)
- Email sending
- Payment processing (use Stripe, PayPal, etc.)
- PDF generation
- Time zone handling
- Currency conversion

---

### Distillation Process

**Definition**: The process of identifying and isolating the core domain from supporting and generic subdomains.

**Steps:**
1. **Map the entire domain**: List all major capabilities
2. **Classify each area**: Core, Supporting, or Generic
3. **Validate with business stakeholders**: Do they agree on what's core?
4. **Refine boundaries**: Separate core from non-core
5. **Protect the core**: Prevent contamination from generic concerns
6. **Document the vision**: Make core domain explicit

**Outcome**: Clear understanding of where to invest effort and how to organize the system.

---

## Context Mapping Patterns

These patterns describe the relationships between bounded contexts. Understanding these patterns helps manage integration complexity and team dynamics.

### Pattern Classification

**By Power Dynamic:**
- **Upstream**: Influences downstream (supplier)
- **Downstream**: Influenced by upstream (customer)
- **Symmetric**: Equal partnership or separation

**By Integration Strategy:**
- **Shared**: Common code/model
- **Translation**: One side translates
- **Isolated**: No translation needed

---

## 1. Partnership

### Definition
When two contexts are tightly coupled such that the success of one depends on the success of the other, teams form a partnership with joint planning and coordinated development.

### Context
- Two teams need each other to succeed
- Failure in either context causes failure in both
- Strong interdependence
- Typically for core domain integration

### Forces
- Need for close coordination
- Shared deadlines and milestones
- Mutual dependency
- High communication overhead

### Solution
- Joint planning sessions
- Coordinated releases
- Shared responsibility for integration
- Close communication
- Mutual commitment

### Implementation

**Organizational:**
- Regular joint meetings
- Shared backlog planning
- Coordinated sprint schedules
- Cross-team pair programming

**Technical:**
- Aligned release cycles
- Shared integration tests
- Common development environment
- Coordinated API changes

### Consequences

**Benefits:**
- Strong alignment
- Quick issue resolution
- Shared understanding
- Coordinated evolution

**Drawbacks:**
- High coordination overhead
- Slower independent progress
- Meeting fatigue
- Difficult with distributed teams

### When to Use
- ✓ Core domains that must work together
- ✓ Tightly coupled features
- ✓ Single product from two contexts
- ✓ Teams in same location

### When to Avoid
- ✗ Teams in different time zones
- ✗ Different release schedules required
- ✗ Low interdependence
- ✗ One team significantly larger

### Examples
- Shopping cart and payment processing in e-commerce
- Flight booking and seat selection
- Course enrollment and student records

### Related Patterns
- **Alternative to**: Customer/Supplier when power is equal
- **Can include**: Shared Kernel for common elements
- **Contrasts with**: Separate Ways (no integration)

---

## 2. Shared Kernel

### Definition
Two teams share a subset of the domain model, including code and database schemas. Changes to the shared kernel require coordination between both teams.

### Context
- Two contexts need to share some core concepts
- Both teams contribute to shared elements
- Tight integration required
- Usually small, well-defined shared area

### Forces
- Need for consistency across contexts
- Duplication is wasteful
- Changes affect multiple teams
- Risk of stepping on each other's toes

### Solution
- Designate a small, shared subset of the model
- Both teams have responsibility for the kernel
- Changes require consensus
- Keep the kernel small
- Continuous integration of shared code

### Implementation

**Organizational:**
- Joint ownership
- Both teams approve changes
- Regular synchronization
- Clear communication protocol

**Technical:**
- Shared code repository (or module)
- Shared database schema sections
- Continuous integration
- Automated tests prevent breakage
- Version together

### Scope Guidelines
**Keep Shared Kernel SMALL:**
- 5-10% of total model
- Only truly shared concepts
- Well-defined boundaries
- Stable core concepts

### Consequences

**Benefits:**
- Eliminates duplication
- Ensures consistency
- Reduces translation overhead
- Easier integration

**Drawbacks:**
- Coordination overhead
- Slower independent development
- Risk of conflicts
- Requires discipline
- Can grow uncontrollably if not managed

### When to Use
- ✓ Very closely related contexts
- ✓ Teams can coordinate effectively
- ✓ Truly shared concepts exist
- ✓ Small, stable shared area

### When to Avoid
- ✗ Teams have different priorities
- ✗ Shared area is large or growing
- ✗ Teams in different time zones
- ✗ One team changes much more frequently

### Common Mistakes
1. **Kernel Too Large**: Shared area grows until contexts merge
2. **Unilateral Changes**: One team changes without consulting
3. **Unclear Boundaries**: Fuzzy definition of what's shared
4. **No Governance**: No process for change management

### Examples
- `Address` value object shared between Shipping and Billing contexts
- `Money` type shared across financial contexts
- `Product ID` shared between Catalog and Inventory
- `User Identity` shared across application contexts

### Best Practices
- Define shared kernel explicitly
- Keep it minimal
- Version and release together
- Automated tests for both contexts
- Regular communication
- Clear change process
- Monitor for kernel bloat

### Related Patterns
- **Smaller version of**: Partnership
- **Alternative to**: Published Language
- **Can coexist with**: Partnership
- **Avoid combining with**: Conformist

---

## 3. Customer/Supplier Development

### Definition
Upstream team (supplier) provides services to downstream team (customer). Downstream priorities factor into upstream planning, with negotiated and budgeted tasks.

### Context
- Clear upstream/downstream relationship
- Downstream depends on upstream
- Upstream is willing to support downstream
- Both teams want success

### Power Dynamic
- **Upstream (Supplier)**: Controls the model and interface
- **Downstream (Customer)**: Influences but doesn't control
- **Balance**: Negotiated relationship

### Forces
- Downstream has requirements
- Upstream has its own priorities
- Need for stability and evolution
- Resource constraints

### Solution
- Downstream expresses needs
- Upstream budgets time for downstream support
- Automated acceptance tests define contract
- Regular planning sessions
- SLAs or agreements on support

### Implementation

**Organizational:**
- Regular planning meetings
- Downstream presents requirements
- Upstream allocates budget
- Tracking downstream requests
- Prioritization process

**Technical:**
- API contracts
- Automated acceptance tests
- Versioning strategy
- Deprecation process
- Documentation

### Acceptance Tests
- **Purpose**: Define what downstream needs
- **Owner**: Downstream writes, upstream ensures they pass
- **Automation**: Must be automated
- **Coverage**: Cover actual use cases
- **Living Documentation**: Express the contract

### Consequences

**Benefits:**
- Clear responsibilities
- Downstream influence
- Predictable evolution
- Quality interface
- Reduced conflicts

**Drawbacks:**
- Overhead of coordination
- Upstream workload
- Potential delays for downstream
- Requires goodwill

### When to Use
- ✓ Clear dependency direction
- ✓ Upstream willing to support
- ✓ Downstream has leverage (internal teams)
- ✓ Ongoing relationship

### When to Avoid
- ✗ Upstream unwilling or unable to support
- ✗ External third-party upstream
- ✗ One-time integration
- ✗ Downstream has no influence

### Examples
- Internal platform team (upstream) serving feature teams (downstream)
- Core services team supporting application teams
- Data team providing APIs to analytics teams

### Related Patterns
- **Degrades to**: Conformist (if upstream stops listening)
- **Alternative to**: Partnership (when power is unequal)
- **Downstream adds**: Anti-Corruption Layer (if protection needed)

---

## 4. Conformist

### Definition
Downstream team conforms to the upstream team's model despite that model not meeting downstream needs, eliminating the complexity of translation.

### Context
- Upstream has all the power
- Downstream cannot influence upstream
- Translation layer would be complex or wasteful
- Upstream model is "good enough"

### Forces
- No influence on upstream
- Translation is expensive
- Resources are limited
- Upstream changes frequently

### Solution
- Accept upstream model as-is
- Conform downstream design to upstream
- Eliminate translation layer
- Simplify integration

### Implementation

**Organizational:**
- Accept limited influence
- Work within constraints
- Minimize customization

**Technical:**
- Use upstream model directly
- Minimal transformation
- Wrapper at most
- Stay current with upstream changes

### Consequences

**Benefits:**
- Simple integration
- Low maintenance
- Automatically stay current
- No translation bugs

**Drawbacks:**
- Suboptimal model in downstream
- No control over changes
- Potential impedance mismatch
- Upstream problems propagate

### When to Use
- ✓ Upstream is unchangeable (external system, vendor)
- ✓ Translation cost exceeds benefits
- ✓ Upstream model is adequate
- ✓ Simple integration is priority

### When to Avoid
- ✗ Upstream model is very poor fit
- ✗ Downstream is core domain (needs protection)
- ✗ Upstream changes would corrupt downstream
- ✗ Have resources for Anti-Corruption Layer

### Common Mistakes
1. **Conforming to Bad Models**: Accepting terrible upstream design in core domain
2. **No Documentation**: Not documenting that conformist is intentional
3. **Missed Opportunities**: Not recognizing when influence is possible

### Examples
- Using external payment gateway's data model directly
- Conforming to government system's structure
- Using SaaS vendor's API model
- Accepting cloud provider's resource model

### Related Patterns
- **Alternative to**: Anti-Corruption Layer
- **Result of**: Customer/Supplier (when influence lost)
- **Often paired with**: Open Host Service (upstream pattern)

---

## 5. Anti-Corruption Layer (ACL)

### Definition
An isolating layer that translates between upstream and downstream contexts, protecting the downstream model from upstream changes and preventing corruption of the downstream's Ubiquitous Language.

### Context
- Upstream model is poor fit for downstream
- Downstream is core domain needing protection
- Upstream changes frequently
- Translation cost is justified

### Forces
- Protect downstream model integrity
- Upstream doesn't match downstream language
- Changes in upstream shouldn't ripple
- Need isolation and stability

### Solution
Create a translation layer with:
- **Facades**: Simplified interface to upstream
- **Adapters**: Transform upstream to downstream model
- **Translators**: Convert between languages

### Implementation

**Structure:**
```
Downstream Model
       ↓
   [ACL Layer]
   ├─ Facades
   ├─ Adapters
   └─ Translators
       ↓
  Upstream API
```

**Components:**
1. **Facade**: Present clean interface to downstream
2. **Adapter**: Implement facade using upstream
3. **Translator**: Convert between models
4. **DTOs**: Data transfer objects for boundary

### Consequences

**Benefits:**
- Protects downstream model
- Isolates from upstream changes
- Maintains Ubiquitous Language
- Freedom to optimize downstream
- Testable isolation

**Drawbacks:**
- Development effort
- Maintenance burden
- Performance overhead
- Added complexity
- Can mask upstream problems

### When to Use
- ✓ Downstream is core domain
- ✓ Upstream model is poor fit
- ✓ Long-term relationship
- ✓ Frequent upstream changes
- ✓ Translation cost is justified

### When to Avoid
- ✗ Upstream model is good fit
- ✗ Resources are very limited
- ✗ Short-term/temporary integration
- ✗ Downstream is generic subdomain

### Design Guidelines

**Keep ACL focused:**
- Only translate what downstream needs
- Don't expose unnecessary upstream complexity
- Version the ACL interface
- Make translation explicit

**Testing:**
- Test ACL in isolation
- Mock upstream for downstream tests
- Integration tests for full path

### Examples

**Legacy System Integration:**
```
Modern Domain Model
       ↓
   [ACL]
   └─ Translates legacy COBOL structures
       ↓
  Legacy System
```

**External API:**
```
E-commerce Domain
       ↓
   [ACL]
   ├─ Maps external product codes to internal SKUs
   ├─ Converts external status to domain events
   └─ Transforms external pricing to Money value objects
       ↓
  External Supplier API
```

### Common Mistakes
1. **Leaking Abstractions**: Upstream concepts leak into downstream
2. **Thin Wrapper**: ACL is just passthrough, no real translation
3. **Bidirectional Coupling**: ACL becomes two-way bridge
4. **Premature Optimization**: Building ACL when Conformist would suffice

### Related Patterns
- **Alternative to**: Conformist
- **Used in**: Customer/Supplier (by downstream)
- **Implements**: Hexagonal Architecture (ports/adapters)
- **Similar to**: Adapter Pattern (GoF)

---

## 6. Open Host Service

### Definition
Define a protocol or API that gives access to your subsystem as a set of services. Make the protocol available so all who need to integrate can use it, and enhance and extend it to handle new requirements, except when a single team has idiosyncratic needs.

### Context
- Multiple downstream consumers
- Each has different needs
- Point-to-point integration is impractical
- Upstream wants to serve many clients

### Forces
- Many clients with varying needs
- Point-to-point customization doesn't scale
- Need stable, well-defined interface
- Upstream can't customize for everyone

### Solution
- Define a generalized API
- Make it openly available
- Comprehensive enough for most needs
- Document thoroughly
- Version appropriately

### Implementation

**API Design:**
- RESTful or GraphQL endpoints
- gRPC services
- Message queues
- Event streams

**Characteristics:**
- **Well-documented**
- **Versioned**
- **Comprehensive**: Covers common needs
- **Stable**: Doesn't change frequently
- **Standard**: Uses standard protocols

### Consequences

**Benefits:**
- Scales to many consumers
- Reduced custom integration work
- Clear contract
- Standard protocols
- Self-service integration

**Drawbacks:**
- Must satisfy many clients
- Versioning complexity
- Can't optimize for individual client
- Documentation overhead
- Breaking changes are painful

### When to Use
- ✓ Multiple downstream consumers
- ✓ General-purpose subsystem
- ✓ Shared services
- ✓ Platform or infrastructure

### When to Avoid
- ✗ Single consumer
- ✗ Highly customized integrations
- ✗ Rapidly evolving interface

### Examples
- **Platform APIs**: AWS, Google Cloud, Azure
- **Internal Platform Services**: Authentication, User Management
- **Data Services**: Product Catalog API, Customer API
- **Integration Hubs**: ESB, API Gateway

### Best Practices
- Use standard protocols (HTTP, gRPC, AMQP)
- Version the API (v1, v2, etc.)
- Comprehensive documentation
- OpenAPI/Swagger specs
- SDKs for common languages
- Backward compatibility
- Deprecation process

### Related Patterns
- **Often combined with**: Published Language
- **Consumed via**: Conformist or Anti-Corruption Layer
- **Upstream pattern for**: Customer/Supplier

---

## 7. Published Language

### Definition
Use a well-documented shared language for information exchange between bounded contexts, often in the form of a standard data format or protocol.

### Context
- Need to exchange information between contexts
- Multiple parties involved
- Standard format exists or can be created
- Translation is necessary anyway

### Forces
- Ad-hoc formats lead to confusion
- Each integration has translation cost
- Need clarity and precision
- Industry standards may exist

### Solution
- Define or adopt a standard format
- Document it thoroughly
- Use for all exchanges
- May be industry standard or internally defined

### Implementation

**Format Types:**
- **Data Formats**: JSON Schema, XML Schema, Protocol Buffers
- **Message Formats**: CloudEvents, FHIR (healthcare), FIX (finance)
- **Industry Standards**: ISO formats, SWIFT messages
- **Internal Standards**: Company-wide canonical model

### Consequences

**Benefits:**
- Clarity in communication
- Reusable across integrations
- Well-understood format
- Tooling available (for standards)
- Reduces ambiguity

**Drawbacks:**
- Overhead of maintaining schema
- May not fit all contexts perfectly
- Versioning challenges
- Learning curve

### When to Use
- ✓ Multiple integrations using same data
- ✓ Industry standard exists
- ✓ Need formal contract
- ✓ Multiple implementation technologies

### When to Avoid
- ✗ Single integration point
- ✗ Rapidly changing format
- ✗ Format is trivial

### Examples

**Industry Standards:**
- **Finance**: FIX protocol, SWIFT messages
- **Healthcare**: HL7, FHIR
- **Logistics**: EDI formats
- **E-commerce**: Product feeds (Google Shopping, Amazon)

**Internal Published Languages:**
- Canonical order format across all systems
- Standard customer representation
- Event schema registry

### Best Practices
- Use existing standards when available
- Schema versioning
- Comprehensive documentation
- Validation tools
- Schema registry
- Backward compatibility

### Related Patterns
- **Often combined with**: Open Host Service
- **Consumed via**: Anti-Corruption Layer
- **Alternative to**: Shared Kernel

---

## 8. Separate Ways

### Definition
When integration between two bounded contexts provides little value, declare the contexts to have no relationship. The teams go separate ways and solve problems independently.

### Context
- Integration cost exceeds benefit
- Contexts address different concerns
- Duplication is acceptable
- No significant overlap

### Forces
- Integration is expensive
- Limited resources
- Independent evolution preferred
- Overlap is minimal

### Solution
- Explicitly decide not to integrate
- Solve problems independently
- Accept some duplication
- Focus resources elsewhere

### Implementation

**Organizational:**
- Document the decision not to integrate
- No coordination meetings needed
- Independent roadmaps
- Separate teams

**Technical:**
- No shared code
- No API integration
- Each solves overlapping problems independently
- May duplicate some data/functionality

### Consequences

**Benefits:**
- No integration complexity
- Complete independence
- Faster independent progress
- No coordination overhead

**Drawbacks:**
- Duplication of effort
- Potentially inconsistent
- May need integration later
- Lost opportunities for synergy

### When to Use
- ✓ Minimal overlap
- ✓ Integration cost is very high
- ✓ Different lifecycles
- ✓ Different business areas
- ✓ Temporary duplication acceptable

### When to Avoid
- ✗ Significant shared concepts
- ✗ User expects consistency
- ✗ Regulatory requirements for integration
- ✗ Duplication causes real problems

### Common Scenarios

**Acceptable Separation:**
- HR system and Product Development system
- Marketing Analytics and Order Fulfillment
- Internal Training Platform and Customer-Facing App

**Questionable Separation:**
- Two customer-facing contexts with different customer models
- Separate inventory systems for same products

### Examples
- Employee training system doesn't integrate with sales system
- Internal wiki doesn't integrate with product documentation
- Marketing site separately manages product info from inventory system

### Common Mistakes
1. **Avoiding Hard Work**: Choosing separation when integration is actually needed
2. **Temporary Becomes Permanent**: "Temporary" duplication that never gets resolved
3. **Undocumented**: Not making the decision explicit

### Related Patterns
- **Opposite of**: Partnership, Shared Kernel
- **May precede**: Future integration via ACL or Published Language
- **Conservative choice when**: Unsure about integration value

---

## 9. Big Ball of Mud

### Definition
Recognize when a context (often legacy) has no useful model and isolate it from clean contexts. Don't try to apply DDD patterns to it.

### Context
- Legacy system exists
- System is a mess (spaghetti code, no clear model)
- Replacement is not feasible
- Must continue operating

### Forces
- Cannot refactor (too risky, too costly)
- New development needs to interact
- Cannot contaminate clean contexts
- Must be pragmatic

### Solution
- **Recognize it for what it is**: Don't pretend it's well-modeled
- **Isolate it**: Prevent contamination
- **Wrap it**: Anti-Corruption Layer
- **Don't expand it**: New features go in clean contexts
- **Accept it**: It works even if ugly

### Implementation

**Organizational:**
- Separate team for maintenance
- Minimal new development
- Focus on keeping it running

**Technical:**
- Anti-Corruption Layer around it
- No direct access from clean contexts
- May have API façade
- Extract functionality gradually if possible

### Consequences

**Benefits:**
- Realistic acknowledgment
- Protects clean contexts
- Pragmatic approach
- Allows progress elsewhere

**Drawbacks:**
- Continued maintenance burden
- Technical debt persists
- May be hard to extend

### When to Use
- ✓ Legacy system is unmaintainable mess
- ✓ Replacement is not feasible
- ✓ Must continue operating
- ✓ New development happening separately

### Common Mistakes
1. **Denial**: Pretending the mess is well-modeled
2. **Expansion**: Adding new features to the ball of mud
3. **No Isolation**: Letting it contaminate clean contexts
4. **Giving Up**: Not building better systems alongside

### Examples
- Decades-old mainframe system
- Heavily patched legacy application
- System with no documentation and original developers gone
- Abandoned but still running system

### Strangler Fig Pattern
Gradually extract functionality:
1. Identify capability to extract
2. Build clean version in new context
3. Route new traffic to new version
4. Gradually migrate old traffic
5. Eventually retire old system

### Related Patterns
- **Protected by**: Anti-Corruption Layer
- **Replaced via**: Strangler Fig Pattern
- **Contrasts with**: All well-modeled contexts

---

## Strategic Patterns Summary

### Pattern Selection Guide

**When you control both sides:**
- High interdependence + equal power → **Partnership**
- High interdependence + shared model → **Shared Kernel**
- One-way dependency + negotiation → **Customer/Supplier**

**When upstream has power:**
- Need protection → **Anti-Corruption Layer**
- Model is adequate → **Conformist**

**When serving many clients:**
- Multiple consumers → **Open Host Service**
- Standard format needed → **Published Language**

**When integration value is low:**
- Minimal overlap → **Separate Ways**
- Legacy mess → **Big Ball of Mud** (with ACL)

### Integration Complexity Matrix

| Upstream | Downstream | Best Pattern |
|----------|------------|--------------|
| Willing partner | Equal partner | Partnership |
| Willing partner | Needs protection | Customer/Supplier + ACL |
| Unwilling to change | Can accept model | Conformist |
| Unwilling to change | Needs protection | ACL |
| Many clients | Various needs | Open Host Service |
| Legacy mess | Clean context | Big Ball of Mud + ACL |
| No value | No value | Separate Ways |

### Context Mapping in Practice

**Process:**
1. Identify all bounded contexts
2. Map dependencies (who needs whom)
3. Assess power dynamics
4. Choose integration patterns
5. Document in Context Map
6. Review and update regularly

**Documentation:**
- Visual diagram
- Pattern for each relationship
- Rationale for choices
- Team responsibilities
- Update frequency

---

## Complete Strategic Pattern Catalog

### Patterns by Category

**Organizational Patterns:**
1. Bounded Context
2. Subdomain (Core, Supporting, Generic)
3. Context Map
4. Distillation

**Integration Patterns - Cooperative:**
5. Partnership
6. Shared Kernel
7. Customer/Supplier

**Integration Patterns - Upstream:**
8. Open Host Service
9. Published Language

**Integration Patterns - Downstream:**
10. Conformist
11. Anti-Corruption Layer

**No Integration:**
12. Separate Ways
13. Big Ball of Mud

---

## References

- Evans, Eric (2003). "Domain-Driven Design" - Chapters 14-16
- Vernon, Vaughn (2013). "Implementing Domain-Driven Design" - Chapters on Strategic Design
- Fowler, Martin. "Bounded Context" - https://martinfowler.com/bliki/BoundedContext.html
- DDD Community. "Context Mapping" - https://github.com/ddd-crew/context-mapping
- The Open Group. "DDD Strategic Patterns"

---

*Document Status: Strategic patterns complete - ready for tactical patterns research*
