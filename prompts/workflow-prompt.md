# Commercial Banking Platform Model Foundation Workflow

## Overview
This workflow guides the creation and refinement of domain models for a commercial banking platform serving commercial customers (small business to large commercial clients) across Canada and US markets. The platform focuses on cash management capabilities including service enrolment, user management, permission/approval rule management, and generic approval workflows.

## Domain Foundation
The platform supports:
- **Geographic Coverage**: Canada and US (North-South), with East-West US division, and future UK expansion
- **Client Types**: Small business (sole proprietor), mid-market, and large commercial clients
- **Client Identification**: SRF (Canadian platform), GID (Global ID - Capital Markets), IND (Indirect Clients)
- **Core Capabilities**: Service enrolment, user management, permission/approval policies, workflow management

## Domain Schemas & Relationships
The platform uses 5 domain model schemas:
1. **DDD (Domain-Driven Design)** - Strategic and tactical domain patterns (foundation layer)
2. **Data-Eng (Data Engineering)** - Data pipelines, quality, and governance (foundation layer)
3. **UX (User Experience)** - UI structure, navigation, workflows, components (derived layer)
4. **QE (Quality Engineering)** - Testing and quality assurance patterns (derived layer)
5. **Agile/SAFe** - Work organization and coordination patterns (meta layer)

Relationships defined in `domains/interdomain-map.yaml` show 28 groundings between models, with DDD as the central hub.

## Workflow Terminology

### "Define"
When you see "define" in this workflow, it means:
1. Create an initial version of the domain model based on schema and context
2. **VALIDATE model against schema** (e.g., validate platform-ddd.yaml against domains/ddd/model-schema.yaml)
3. Fix any validation errors and re-validate until compliant
4. **IMMEDIATELY convert YAML to MD using `./convert-yaml.sh`** before asking questions
5. Ask 3-5 meaningful clarifying questions based on the model schema and business context
6. Accept user feedback and modify models accordingly
7. Re-validate, fix if needed, and convert updated YAML to MD again after modifications
8. When changes are made to one model, check if ripple effects require updates to other models

**CRITICAL**:
- ALWAYS validate YAML models against their schemas BEFORE converting to MD
- Schema files are in `domains/{domain-name}/model-schema.yaml`
- Model validation mapping:
  - `platform-ddd.yaml` → `domains/ddd/model-schema.yaml`
  - `platform-data-eng.yaml` → `domains/data-eng/model-schema.yaml`
  - `platform-agile.yaml` → `domains/agile/model-schema.yaml`
  - `platform-ux.yaml` → `domains/ux/model-schema.yaml`
  - `platform-qe.yaml` → `domains/qe/model-schema.yaml`
- Always create .md files BEFORE asking for review, not at the end of the process

### "Refine"
When you see "refine" in this workflow, it means:
1. Further enhance the model through iterative questioning
2. Ask 3-5 focused questions to deepen understanding
3. Accept user feedback and iterate
4. Continue the question/feedback loop until user says "we are done for this turn"

### User Autonomy
At any point, the user may decide not to answer questions and change direction. Adapt accordingly.

## Workflow Phases

### Phase 1: Vision & Technology Foundation
**Objective**: Establish platform vision and technology direction

**Actions**:
1. **Define Product Vision** in `model/platform-agile.yaml`

   Create a comprehensive `product` object with a `vision` section following the agile schema:

   **Required Vision Components** (per agile schema):
   - `futureStateDescription` (min 50 chars) - Clear description of intended future state
   - `customerNeeds`:
     - `targetCustomers[]` - Customer segments (small business, mid-market, large commercial)
     - `problemsBeingSolved[]` - Key problems the platform addresses
     - `valueProposition` - Core value proposition

   **Recommended Vision Components**:
   - `solutionIntent`:
     - `approach` - High-level solution approach
     - `keyCapabilities[]` - Key capabilities (service enrolment, user management, permissions, approvals)
     - `technicalDirection` - Overall technical direction
     - `differentiators[]` - What makes this solution different

   - `boundaries`:
     - `inScope[]` - What is included (geography, services, client types)
     - `outOfScope[]` - What is explicitly excluded
     - `constraints[]` - Known constraints (regulatory, technical, business)
     - `assumptions[]` - Key assumptions
     - `dependencies[]` - External dependencies (SRF, GID, identity providers, account systems)

   - `strategicAlignment`:
     - `businessObjectives[]` - Business objectives this supports
     - `strategicThemes[]` - Strategic themes (e.g., North American expansion, digital transformation)
     - `successMetrics[]` - How success will be measured (metric objects with name, target, current, unit)
     - `outcomes[]` - Desired business outcomes

   - `motivationalNarrative` (min 100 chars) - Inspiring description for team engagement

   - `decisionFramework`:
     - `guidingPrinciples[]` - Principles that guide decisions
     - `tradeoffCriteria[]` - Criteria for making trade-offs
     - `alignmentMechanisms[]` - How alignment is maintained

2. **Define Technology Vision** (within `solutionIntent.technicalDirection`)
   - Document architecture principles and patterns
   - Identify key technology choices (identity providers: Okta/A-and-P, data persistence, integration patterns)
   - Define integration strategy with external systems (batch vs. real-time, API vs. feed)
   - Outline multi-tenancy and geographic distribution approach

**Clarifying Questions Focus**:
- **Strategic Domains**: Which domains are core (competitive advantage) vs. supporting vs. generic?
- **Customer Priorities**: Which customer segments are MVP priority? (small business vs. mid-market vs. large?)
- **Geographic Scope**: MVP Canada-only or include US? East-West considerations?
- **Technology Constraints**: Regulatory requirements, existing technology investments, integration patterns
- **Success Metrics**: What metrics define platform success? (user adoption, transaction volume, SLA targets)
- **Key Differentiators**: What makes this platform unique vs. competitors?
- **Dependencies**: Critical dependencies on external systems (SRF, GID, account systems)

**Outputs**:
- `model/platform-agile.yaml` with complete `product` object and comprehensive `vision`
- Foundation for strategic DDD model
- Clear understanding of boundaries, constraints, and success criteria

---

### Phase 2: Initial Domain Models (Strategic Focus)
**Objective**: Create high-level domain structure

**Actions**:
1. **Define DDD Model** in `model/platform-ddd.yaml` (Strategic focus)

   Following the DDD schema, create:

   **System Definition**:
   - `id`, `name`, `description`, `version`
   - `domains[]` - Array of domain references
   - `bounded_contexts[]` - Array of all bounded contexts
   - `context_mappings[]` - Relationships between contexts

   **Domain Definitions** (pattern: `dom_<name>`):
   - `type`: core | supporting | generic
     - **Core**: Client Management, Service Management (competitive advantage)
     - **Supporting**: User Management, Approval Workflows, Account Integration
     - **Generic**: Authentication, Notifications, Audit
   - `strategic_importance`: critical | important | standard | low
   - `investment_strategy`: best team | adequate | minimal | outsource
   - `bounded_contexts[]` - References to contexts in this domain

   **Bounded Context Definitions** (pattern: `bc_<name>`):
   - `domain_ref` - Parent domain reference
   - `team_ownership` - Team responsible
   - `ubiquitous_language.glossary[]` - Term, definition, examples
   - List aggregates, repositories, services (high-level only for Phase 2)

   **Context Mapping Definitions** (pattern: `cm_<source>_to_<target>`):
   - `upstream_context`, `downstream_context`
   - `relationship_type`: partnership | shared_kernel | customer_supplier | conformist | anti_corruption_layer | open_host_service | published_language | separate_ways
   - `integration_pattern` - How integration is implemented
   - `translation_map` - How concepts translate between contexts

2. **Define Data-Eng Model** in `model/platform-data-eng.yaml`

   Following the data-eng schema, create:

   **System Definition**:
   - `id` (pattern: `sys-<name>`), `name`, `description`
   - `domains[]` - References to data domain IDs

   **Domain Definitions** (pattern: `dom-<name>`):
   - Align with DDD domains (use same names)
   - `pipelines[]` - References to pipeline IDs in this domain

   **Pipeline Definitions** (pattern: `pip-<name>`):
   - `mode`: batch | streaming | micro-batch | continuous
   - `schedule` object:
     - `type`: cron | interval | event | continuous | manual
     - `cron_expression` or `interval_minutes`
   - `stages[]` - Array of stage definitions

   **Stage Definitions** (pattern: `stg-<name>`):
   - `inputs[]` - Dataset IDs consumed
   - `outputs[]` - Dataset IDs produced
   - `transforms[]` - Array of transform definitions
   - `depends_on[]` - Stage dependencies

   **Dataset Definitions** (pattern: `ds-<name>`):
   - `type`: table | view | stream | file | api
   - `format`: delta | parquet | json | csv | avro | custom
   - `location` - URI or path
   - `schema.fields[]` - Field definitions (name, type, nullable, description, pii)
   - `classification`: public | internal | restricted | confidential
   - `contains_pii`: boolean
   - `pii_fields[]` - List of PII field names

   **Contract Definitions** (pattern: `ctr-<name>`):
   - `dataset` - Dataset reference
   - `version` - Semantic version (MAJOR.MINOR.PATCH)
   - `sla`:
     - `freshness_minutes` - Max data age
     - `completeness_percent` - Min completeness
     - `availability_percent` - Min availability
   - `evolution_policy`: backward-compatible | forward-compatible | full-compatible | breaking-allowed
   - `consumers[]` - Array of {team, use_case}

**Clarifying Questions Focus**:
- **Bounded Context Boundaries**: How should client profile vs. service enrolment vs. user management be separated?
- **Team Ownership**: Which teams will own which bounded contexts?
- **Context Relationships**: Is client data shared kernel or customer-supplier? How do contexts integrate?
- **Data Ownership**: Who owns truth for client data, account data, user data?
- **Data Freshness**: What are SLA requirements for account feeds? (daily batch sufficient?)
- **Integration Patterns**: API-first or batch-first for external systems? When to use CDC vs. batch?
- **Data Classification**: Which datasets contain PII? What's the classification scheme?

**Cross-Model Validation**:
- Ensure data-eng domains align with DDD domains (same naming)
- Verify each DDD context mapping has corresponding data contract
- Check that data pipelines map to bounded context data needs
- Confirm dataset classification aligns with domain importance

**Outputs**:
- `model/platform-ddd.yaml` with strategic patterns (System, Domains, Bounded Contexts, Context Mappings)
- `model/platform-data-eng.yaml` with System, Domains, Pipelines, Datasets, Contracts

---

### Phase 3: MVP Release Planning
**Objective**: Define minimum viable product scope

**Actions**:
1. **Define MVP Release** in `model/platform-agile.yaml`

   Create a `release` object (pattern: `REL-<number>`) with detailed planning:

   **Release Basics** (required):
   - `id` (pattern: `REL-<number>`), `name`, `version` (semantic versioning)
   - `startDate`, `endDate` (format: date)
   - `productId` - Reference to parent product
   - `status`: planning | development | hardening | deployed | closed
   - `releaseType`: major | minor | patch | hotfix

   **Release Vision** (required - `release_vision` object):
   - `releaseGoal` (min 50 chars) - Clear statement of what this release achieves
   - `targetCustomers[]` - Customer segments for this release (small business? mid-market? large?)
   - `keyCapabilities[]` - Top 3-5 capabilities (e.g., "Online profile management", "BTR service", "Basic user management")
   - `customerValue` (min 50 chars) - Value articulation for customers
   - `businessValue` - Expected business value
   - `themes[]` - Strategic themes (e.g., "Foundation", "Core Cash Management")
   - `boundaries`:
     - `inScope[]` - What's included in MVP
     - `outOfScope[]` - What's explicitly deferred
     - `deferredToFuture[]` - Features for future releases
   - `successCriteria[]` - Metric objects defining success
   - `risks[]` - Key release risks (risk objects with id, description, impact, probability, roam, mitigation)
   - `alignmentToProductVision` (min 50 chars) - How this advances product vision
   - `motivationalNarrative` (min 100 chars) - Inspiring story about the release

   **Release Planning**:
   - `programIncrements[]` - PI IDs for this release (1-3 PIs typical)
   - `features[]` - Feature IDs planned for release
   - `releaseObjectives[]` - High-level objectives with business value (1-10) and target PI
   - `milestones[]` - Key milestones (name, date, type: planning|development|testing|deployment|marketing, status)
   - `deploymentStrategy`: big-bang | phased | canary | blue-green | feature-flags
   - `stakeholders[]` - Release stakeholders (id, name, role, influence, interest)
   - `metrics[]` - Success metrics (name, description, target, current, unit)

   **Roadmap** (optional but recommended):
   - `level`: release
   - `timeHorizon` - e.g., "3 months", "1 PI"
   - `items[]` - Roadmap items by timeframe with themes, objectives, features, confidence level

2. **Define MVP Features** in `model/platform-agile.yaml`

   Create `feature` objects (pattern: `FEAT-<number>`) for MVP scope:

   **Feature Basics** (required):
   - `id` (pattern: `FEAT-<number>`), `title`, `type`: business | enabler
   - `bounded_context_ref` (pattern: `ddd:BoundedContext:<name>`) - **Required grounding to DDD**
   - `description`, `benefitHypothesis`
   - `acceptanceCriteria[]` - Conditions for acceptance
   - `status`: backlog | planned | in-progress | done | accepted

   **Feature Prioritization**:
   - `wsjf` object for prioritization:
     - `userBusinessValue` (1-20)
     - `timeCriticality` (1-20)
     - `riskReductionOpportunity` (1-20)
     - `jobSize` (1-20)
     - `wsjfScore` - Calculated: (UBV + TC + RR) / JobSize
   - `piId` - Target program increment
   - `teamId` - Responsible team

   **Feature Traceability**:
   - `epicId` - Parent epic (if applicable)
   - `nfrRefs[]` - Non-functional requirement IDs
   - `enablerRefs[]` - Enabler IDs supporting this feature
   - `stories[]` - User story IDs (will be defined in Phase 5)

**Clarifying Questions Focus**:
- **Client Profile Scope**: MVP support for servicing-only, online, or both profile types?
- **Service Prioritization**: Which services are MVP critical? (BTR? Interac Send? Receivables?)
- **Geographic Scope**: Canada-only MVP or include US from day 1?
- **User Management Depth**: Basic user CRUD or full dual-admin, permission policies, approval rules?
- **Account Types**: Which account types in MVP? (DDA only? Include FCA, OLB, ACH?)
- **Client ID Systems**: Which client ID types in MVP? (SRF only? Include GID? IND?)
- **Indirect Client Support**: MVP include indirect clients and receivable-approval service?
- **Identity Provider**: Start with A-and-P only or include Okta integration in MVP?
- **Data Integration**: MVP use batch feeds or require real-time API integration?
- **Release Timeline**: What's the target date? How many PIs/sprints?
- **Success Metrics**: How will MVP success be measured? (user adoption targets, transaction volume, uptime SLA)

**Outputs**:
- `model/platform-agile.yaml` with MVP `release` object, comprehensive `release_vision`, and prioritized `features[]`
- Clear scope for tactical DDD refinement
- Foundation for sprint/iteration planning

---

### Phase 4: Tactical DDD Model Refinement
**Objective**: Detail domain model with tactical patterns

**Actions**:

1. **Define Value Objects** (pattern: `vo_<name>`)
   - Immutable objects representing domain concepts
   - Attributes with types, descriptions, and validation rules
   - Validation rules (format, range, dependencies)
   - Equality based on value, not identity
   - Examples: ClientId (URN-based), ProfileId hierarchy, Email, AccountNumber

2. **Define Aggregates** (pattern: `agg_<name>`)
   - Aggregate root entity reference
   - Child entities within aggregate boundary
   - Value objects used by aggregate
   - Consistency rules (invariants across entities)
   - Invariants (must-hold conditions)
   - Lifecycle hooks (on_create, on_update, on_delete)
   - Size estimate (small/medium/large)
   - Examples: ServicingProfile, OnlineProfile, IndirectProfile, User, PermissionStatement

3. **Define Entities** (pattern: `ent_<name>`)
   - Entity attributes with types and descriptions
   - Methods (behavior) with parameters and return types
   - Lifecycle states (if applicable)
   - State transition rules
   - Identity generation strategy
   - Validation rules
   - Examples: ent_online_profile, ent_user, ent_service_enrollment, ent_permission_statement

4. **Define Domain Events** (pattern: `evt_<name>`)
   - Event payload (attributes with types)
   - Publishing aggregate reference
   - Bounded context reference
   - Timestamp and causation tracking
   - Examples: ProfileCreated, UserCreated, PermissionStatementCreated, ApprovalWorkflowStarted

5. **Define Domain Services** (pattern: `svc_dom_<name>`)
   - Stateless operations spanning multiple aggregates
   - Methods with clear input/output contracts
   - When to use: Operations that don't belong to single aggregate
   - Examples: Profile validation across contexts, account enrollment eligibility checks

6. **Define Application Services** (pattern: `svc_app_<name>`)
   - Orchestration of domain logic
   - Methods implementing use cases
   - Transaction boundaries
   - Integration with repositories
   - Event publishing
   - Examples: svc_app_online_profile (create, add service, enroll accounts)

7. **Define Repositories** (pattern: `repo_<name>`)
   - Aggregate reference
   - Operations: save, findById, findBy<criteria>, delete, update
   - Query methods with filters and pagination
   - Persistence technology hints (SQL, NoSQL, etc.)
   - Examples: repo_online_profile, repo_user, repo_permission_statement

8. **Define Factories** (pattern: `factory_<name>`) - Optional
   - Complex object construction
   - Validation during creation
   - When to use: Multi-step construction, validation logic
   - Examples: ProfileFactory (validates client exists, generates sequence)

**Detailed Definition Guidelines**:

**Entities**:
```yaml
entities:
  ent_online_profile:
    id: ent_online_profile
    name: OnlineProfile
    bounded_context_ref: bc_service_profile_management
    is_aggregate_root: true
    attributes:
      - name: profile_id
        type: value_object
        value_object_ref: vo_online_profile_id
        description: "Unique profile identifier"
      - name: client_id
        type: value_object
        value_object_ref: vo_client_id
        description: "SRF client reference"
      - name: site_id
        type: string
        description: "Express site-id link"
      - name: status
        type: enum
        enum_values: [PENDING, ACTIVE, SUSPENDED, CLOSED]
      - name: created_at
        type: timestamp
      - name: updated_at
        type: timestamp
    methods:
      - name: enrollService
        description: "Enroll a service to this profile"
        parameters:
          - name: service_type
            type: enum
            enum_values: [RECEIVABLES, BTR, INTERAC_SEND]
          - name: configuration
            type: object
        returns:
          type: entity
          entity_ref: ent_service_enrollment
        validation:
          - "Profile must be ACTIVE"
          - "Service not already enrolled"
      - name: enrollAccount
        description: "Enroll account to a service"
        parameters:
          - name: service_enrollment_id
            type: string
          - name: account_id
            type: string
        validation:
          - "Service must be enrolled"
          - "Account must belong to client"
    lifecycle_states:
      - PENDING: "Created but not activated"
      - ACTIVE: "Active and can enroll services"
      - SUSPENDED: "Temporarily suspended"
      - CLOSED: "Permanently closed"
    state_transitions:
      - from: PENDING
        to: ACTIVE
        trigger: "Bank approval"
      - from: ACTIVE
        to: SUSPENDED
        trigger: "Admin or bank suspension"
      - from: SUSPENDED
        to: ACTIVE
        trigger: "Reinstatement"
    identity_generation: "Composite: clientId + sequence (unique within client)"
```

**Domain Services**:
```yaml
domain_services:
  svc_dom_profile_validation:
    id: svc_dom_profile_validation
    name: ProfileValidationService
    bounded_context_ref: bc_service_profile_management
    description: >
      Validates profile creation rules across contexts. Checks client existence
      via External Data serving layer, validates business rules.
    methods:
      - name: validateOnlineProfileCreation
        description: "Validate online profile can be created"
        parameters:
          - name: client_id
            type: value_object
            value_object_ref: vo_client_id
          - name: site_id
            type: string
        returns:
          type: object
          properties:
            valid: boolean
            errors: array
        validation:
          - "Client exists in SRF via svc_app_client_data"
          - "Site-id exists in Express via svc_app_user_data"
          - "Site-id links to same client"
```

**Application Services**:
```yaml
application_services:
  svc_app_online_profile:
    id: svc_app_online_profile
    name: OnlineProfileApplicationService
    bounded_context_ref: bc_service_profile_management
    description: >
      Orchestrates online profile use cases. Creates profiles, enrolls services,
      manages accounts. Coordinates with repositories and domain services.
    methods:
      - name: createOnlineProfile
        description: "Create new online profile with primary client"
        parameters:
          - name: client_id
            type: value_object
            value_object_ref: vo_client_id
          - name: site_id
            type: string
        returns:
          type: aggregate
          aggregate_ref: agg_online_profile
        transaction: true
        publishes_events:
          - evt_online_profile_created
        workflow:
          - "Validate via ProfileValidationService"
          - "Generate sequence via SequenceGeneratorService"
          - "Create OnlineProfile aggregate"
          - "Save via repo_online_profile"
          - "Publish OnlineProfileCreated event"
      - name: enrollService
        parameters:
          - name: profile_id
            type: value_object
            value_object_ref: vo_online_profile_id
          - name: service_type
            type: enum
          - name: configuration
            type: object
        returns:
          type: entity
          entity_ref: ent_service_enrollment
        transaction: true
        publishes_events:
          - evt_service_enrolled
```

**Repositories**:
```yaml
repositories:
  repo_online_profile:
    id: repo_online_profile
    name: OnlineProfileRepository
    bounded_context_ref: bc_service_profile_management
    aggregate_ref: agg_online_profile
    description: "Repository for OnlineProfile aggregate persistence"
    operations:
      - name: save
        description: "Persist new or updated profile"
        parameters:
          - name: profile
            type: aggregate
            aggregate_ref: agg_online_profile
        returns:
          type: void
      - name: findById
        description: "Find profile by ID"
        parameters:
          - name: profile_id
            type: value_object
            value_object_ref: vo_online_profile_id
        returns:
          type: aggregate
          aggregate_ref: agg_online_profile
          nullable: true
      - name: findByClientId
        description: "Find all profiles for a client"
        parameters:
          - name: client_id
            type: value_object
            value_object_ref: vo_client_id
          - name: page_number
            type: integer
          - name: page_size
            type: integer
        returns:
          type: array
          items:
            type: aggregate
            aggregate_ref: agg_online_profile
      - name: findBySiteId
        description: "Find profile by Express site-id"
        parameters:
          - name: site_id
            type: string
        returns:
          type: aggregate
          aggregate_ref: agg_online_profile
          nullable: true
      - name: delete
        description: "Delete profile (soft delete - status=CLOSED)"
        parameters:
          - name: profile_id
            type: value_object
            value_object_ref: vo_online_profile_id
        returns:
          type: void
    persistence_technology: "SQL (PostgreSQL recommended)"
    table_strategy: "Single table per aggregate root"
```

**Clarifying Questions Focus**:
- **Aggregate Boundaries**: What entities belong inside aggregate vs. separate aggregates?
- **Entity Attributes**: What data needs to be persisted? What's computed?
- **Entity Methods**: What business logic belongs on entities vs. domain services?
- **Value Object Validation**: What format/range rules? (e.g., account number format per region)
- **Domain Events**: What events are critical for cross-context communication?
- **Identity Generation**: How are IDs generated? (UUID, sequence, composite?)
- **Lifecycle States**: What states exist? What transitions are allowed?
- **Repository Queries**: What query patterns are needed? Pagination requirements?
- **Application Service Methods**: What use cases? Transaction boundaries?
- **Domain Service Scope**: What operations span multiple aggregates?

**Key Domain Concepts to Model**:
- **Profile Aggregates**: ServicingProfile, OnlineProfile, IndirectProfile
- **Policy Aggregates**: PermissionStatement, ApprovalStatement
- **User Aggregates**: User, UserGroup
- **Client Aggregates**: IndirectClient
- **Service Entities**: ServiceEnrollment, AccountEnrollment
- **Value Objects**: ClientId (URN), ProfileId hierarchy, Email, PhoneNumber
- **Domain Events**: Profile lifecycle, service enrollment, user management, policy changes, approval workflows

**Outputs**:
- `model/platform-ddd.yaml` with complete tactical patterns:
  - Value Objects with validation rules
  - Aggregates with consistency rules and invariants
  - Entities with attributes, methods, and lifecycle states
  - Domain Events with payloads
  - Domain Services with methods
  - Application Services with use case orchestration
  - Repositories with operations

---

### Phase 5: Cross-Model Enrichment
**Objective**: Update UX, Data-Eng, QE, and Agile models based on refined DDD

**Actions**:
1. **Update UX Model** in `model/platform-ux.yaml`
   - Map Pages to Bounded Contexts
   - Define Workflows referencing DDD Aggregates
   - Create Components with DDD Value Object validation rules
   - Design Navigation aligned with domain structure
   - Document Behaviors responding to Domain Events

2. **Update Data-Eng Model** in `model/platform-data-eng.yaml`
   - Refine Pipelines to support aggregates
   - Add detailed Transforms and Stages
   - Define Dataset schemas aligned with aggregates
   - Create data quality Checks and Contracts
   - Document Lineage tracking

3. **Update QE Model** in `model/platform-qe.yaml`
   - Define Test Strategies for bounded contexts
   - Create Test Cases validating DDD Invariants
   - Design Contract Tests for data contracts
   - Plan UI Tests for UX Workflows
   - Set Coverage Targets per aggregate

4. **Update Agile Model** in `model/platform-agile.yaml`
   - Map Features to Bounded Contexts
   - Create User Stories implementing UX Workflows
   - Define Definition of Done with QE validation criteria
   - Plan Sprints and Program Increments
   - Track Technical Debt by bounded context

**Clarifying Questions Focus**:
- User workflows and page navigation patterns
- Data quality requirements and SLAs
- Testing coverage expectations (unit, integration, E2E)
- Sprint capacity and team structure
- Cross-context dependencies and integration points

**Cross-Model Validation**:
- Verify UX validation rules enforce DDD value object invariants
- Ensure data-eng contracts align with DDD context mappings
- Confirm QE test cases cover critical DDD invariants
- Check agile stories reference UX workflows and DDD aggregates

**Outputs**:
- `model/platform-ux.yaml` with complete UI/UX specification
- `model/platform-data-eng.yaml` with detailed pipelines and contracts
- `model/platform-qe.yaml` with comprehensive test strategy
- `model/platform-agile.yaml` with sprint planning and stories

---

## Model Storage Convention

All models are stored in the `model/` directory:
- `model/platform-agile.yaml` - Vision, releases, features, stories, sprints
- `model/platform-ddd.yaml` - Strategic and tactical DDD patterns
- `model/platform-data-eng.yaml` - Systems, pipelines, datasets, contracts
- `model/platform-ux.yaml` - Pages, workflows, components, navigation (created in Phase 5)
- `model/platform-qe.yaml` - Test strategies, cases, coverage (created in Phase 5)

**IMPORTANT: YAML to Markdown Conversion**

ALWAYS run `./convert-yaml.sh` after modifying any `platform-*.yaml` file:

```bash
# After editing platform-agile.yaml
./convert-yaml.sh model/platform-agile.yaml

# After editing platform-ddd.yaml
./convert-yaml.sh model/platform-ddd.yaml

# And so on for all platform-*.yaml files
```

This generates readable `.md` versions with:
- Humanized keys (camelCase → Title Case)
- Arrays as tables
- Multi-line values properly formatted
- Navigation by reference IDs

## Schema References

Each model file should reference its schema:
- **DDD**: `domains/ddd/model-schema.yaml` - Domain-Driven Design patterns (strategic and tactical)
- **Data-Eng**: `domains/data-eng/model-schema.yaml` - Data engineering (JSON Schema format)
- **UX**: `domains/ux/model-schema.yaml` - User experience patterns
- **QE**: `domains/qe/model-schema.yaml` - Quality engineering and testing
- **Agile**: `domains/agile/model-schema.yaml` - Agile/SAFe methodology (JSON Schema format)

### Schema Format Notes

**Agile Schema**: Uses JSON Schema draft 2020-12 format with `$defs` section for all types:
- Access definitions via `$ref: '#/$defs/<type>'` (e.g., `$ref: '#/$defs/product'`)
- Pattern-based IDs (EPIC-[0-9]+, FEAT-[0-9]+, US-[0-9]+, REL-[0-9]+, etc.)
- Strong grounding to DDD (bounded_context_ref required for features)
- Strong grounding to UX (ux_artifact_refs for user stories)
- Strong grounding to QE (definitionOfDone with test_criteria_refs)

**Data-Eng Schema**: Uses JSON Schema draft 2020-12 format with `$defs` section:
- Access definitions via `$ref: '#/$defs/<type>'` (e.g., `$ref: '#/$defs/pipeline'`)
- Kebab-case ID patterns (sys-<name>, dom-<name>, pip-<name>, ds-<name>, etc.)
- Hierarchical structure: System → Domains → Pipelines → Stages → Transforms
- Datasets are cross-cutting with references from stages
- Contracts formalize dataset SLAs and evolution policies
- Grounding to DDD via data_product.bounded_context_ref

**DDD Schema**: Uses custom YAML schema format with type definitions:
- Documented in schema with `$defs` style but using custom format
- Snake_case ID patterns (dom_<name>, bc_<name>, agg_<name>, etc.)
- Hierarchical: System → Domains → BoundedContexts → Aggregates → Entities/ValueObjects

**UX & QE Schemas**: Use custom YAML schema formats with detailed type definitions

## Workflow State Tracking

The workflow state and next steps are tracked in `claude.md`. This file will:
1. Record which phase is currently in progress
2. Track completed phases
3. Document key decisions and assumptions
4. Provide context for resuming work
5. List next steps and pending questions

## Resuming Workflow

When resuming work:
1. Check `claude.md` for current phase and context
2. Review completed model files in `model/` directory
3. Verify cross-model consistency using `domains/interdomain-map.yaml`
4. Ask if user wants to continue current phase or change direction

## Success Criteria

Each phase is complete when:
1. Model file(s) are created/updated with required sections
2. Clarifying questions are answered or user redirects
3. Cross-model relationships are validated
4. User confirms phase completion

The workflow is complete when all 5 phases are finished and all model files are created with appropriate detail level.

## Platform Context Summary

### Client & Account Structure
- **Client Profiles**: Can be servicing-only or online (with users)
- **Client Identification**: SRF (Canadian), GID (Capital Markets), IND (Indirect - managed on behalf)
- **Account Types**:
  - CA deposits: DDA (CAD/USD), FCA (EUR, GBP, JPY, AUD, NZD)
  - CA lending: OLB (operating lines, term loans), MTG (mortgages), GIC (investments)
  - CA payments: ACH (GSAN accounts linked to DDA)
  - Business credit cards: TSYS (individual plastic), Commercial cards (pool management)
  - US accounts: US:DDA

### Service Categories
- **Stand-alone**: Additional deposit narrative, ACH debit block, BTR (Balance & Transaction Reporting)
- **Online**: Interac send, Receivables (with indirect clients as payors)
- **Indirect**: Receivable-approval service (indirect clients managing their users, permissions, approvals)

### User Management
- **Profile Types**: Servicing (no users), Online (requires users)
- **Administrator Requirement**: Dual admin (2 users with admin role) for critical functions
- **User Roles**: Administrator, Regular User
- **Identity Providers**: A-and-P (current), Okta (planned)
- **User Locking**: By administrators or bank (bank locks only removable by bank)

### Permission & Approval Model
- **Permission Policies**: AWS IAM-like (subject=user, action=URN, resource=accounts)
- **Approval Policies**: Extends permissions with approver count requirements
- **Ownership**: Policies owned by online profile

### Geographic & Multi-Tenancy
- **North-South**: Canada vs. US
- **East-West**: Within US (east vs. west banks)
- **Future**: UK expansion
- **Design**: Global servicing platform capability

### Data Integration
- **External Systems**: SRF, GID, DDA, OLB, MTG, GIC, TSYS, RIBS, ACH/GSAN
- **Velocity**: Low velocity - batch feasible
- **Pattern**: Daily feeds to identify new/closed accounts for auto-enrollment
- **API Access**: Currently available, but batch alternative considered
