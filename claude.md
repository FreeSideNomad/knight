# Commercial Banking Platform - Model Foundation Workflow

## Workflow Overview
This document tracks the progress of creating domain models for a commercial banking cash management platform. The workflow is defined in `prompts/workflow-prompt.md`.

## Current Phase
**Phase 1: COMPLETED** ✅ - Vision & Technology Foundation

**Ready to begin Phase 2**: Initial Domain Models (Strategic Focus)

## Workflow Phases

### Phase 1: Vision & Technology Foundation ✅
**Status**: COMPLETED
**Goal**: Establish platform vision and technology direction
**Outputs**:
- ✅ `model/platform-agile.yaml` with comprehensive Product Vision

**Key Decisions Made**:
- **MVP Scope**: One large Canadian commercial client + 700 indirect clients (payors)
- **Launch Date**: June 2026
- **Core Focus**: Payedge Receivables initiative (NEW service - greenfield)
- **Onboarding**: Employee portal (bank-managed, not self-service for direct client)
- **Identity Strategy**: Dual provider - Okta (indirect clients, MVP) + A-and-P (direct clients via Express)
- **Okta Integration**: Full user lifecycle for 700 indirect client users (create, update, activate, deactivate, lock, unlock)
- **Express Integration**: Anti-corruption layer - unidirectional event replication (Express → Platform)
- **Express Context**: Legacy big ball of mud - cannot modify, has self-serve user management with A-and-P
- **User Ownership**: Direct client users (Express + A-and-P); indirect client users (platform + Okta)
- **Dual Portals**: Employee portal (bank operations) + self-service portal (payors with Okta auth)
- **Approval Workflow**: Generic approval engine with configurable rules (single/dual approver, thresholds)
- **MVP Client Structure**: Primary client only (secondary client linking deferred to post-MVP)
- **Geographic Scope**: Canada (MVP), architecture supports US/UK expansion
- **Success Metrics**: 1 direct client + 700 payors + 700 Okta users + 700 approval workflows by June 2026

**Next Steps**: Begin Phase 2 - Define DDD and Data-Eng domain models

---

### Phase 2: Initial Domain Models (Strategic Focus) 📋
**Status**: Not Started
**Goal**: Create high-level domain structure
**Outputs**:
- `model/platform-ddd.yaml` with strategic patterns (Domains, Bounded Contexts, Context Mappings)
- `model/platform-data-eng.yaml` with Systems, Domains, high-level Pipelines

---

### Phase 3: MVP Release Planning 📋
**Status**: Not Started
**Goal**: Define minimum viable product scope
**Outputs**:
- `model/platform-agile.yaml` with MVP Release section

---

### Phase 4: Tactical DDD Model Refinement 📋
**Status**: Not Started
**Goal**: Detail domain model with tactical patterns
**Outputs**:
- `model/platform-ddd.yaml` with complete tactical patterns (Aggregates, Entities, Value Objects, etc.)

---

### Phase 5: Cross-Model Enrichment 📋
**Status**: Not Started
**Goal**: Update UX, Data-Eng, QE, and Agile models based on refined DDD
**Outputs**:
- `model/platform-ux.yaml` - UI/UX specification
- `model/platform-data-eng.yaml` - Refined with detailed pipelines
- `model/platform-qe.yaml` - Test strategy and cases
- `model/platform-agile.yaml` - Sprint planning and stories

---

## Key Decisions & Assumptions

### MVP Scope (Phase 1 - Completed)
- **Target**: One large Canadian commercial client with ~700 indirect clients (payors)
- **Timeline**: Launch by June 2026
- **Primary Initiative**: Payedge Receivables (NEW service launch - greenfield, not replacing existing)
- **Critical Context**: No existing receivables service to digitize - building new digital service from scratch
- **Key MVP Features**:
  - Employee portal for bank operations
  - Self-service portal for 700 payors (Okta authentication)
  - **Okta integration** for 700 indirect client users (full lifecycle management)
  - **Generic approval workflow engine** (configurable rules: single/dual approver, thresholds)
  - Primary client only (no secondary client support in MVP)

### Onboarding Approach & Portal Strategy
- **Direct Client Onboarding**: Via employee portal (bank-managed, NOT client self-service for MVP)
- **Employee Portal Purpose**:
  - Enroll Canadian online profiles
  - View direct client + indirect client relationships
  - Monitor corporate structures (primary + secondary clients)
  - Provide unified operational view for bank staff
- **Indirect Client Management**: Self-service portal (payors manage own users, permissions, approvals)
- **Rationale**:
  - Employee portal is PRIMARY tool for bank operations (not just onboarding)
  - Large client requires bank-managed setup; payors need independence
  - Corporate structures require visibility into primary + secondary client relationships

### Identity Provider Strategy (Dual Provider Approach)

**Critical Decision**: Platform uses TWO identity providers based on user type:

#### **Okta (MVP - Indirect Client Users)**
- **Purpose**: Authentication and lifecycle management for indirect client (payor) users
- **Ownership**: Platform fully owns user lifecycle
- **Lifecycle Operations**: create, update, activate, deactivate, lock, unlock
- **Integration**: Platform integrates with Okta APIs for all user operations
- **Target**: 700 indirect client users in Okta for MVP
- **Target Latency**: <2 seconds from platform operation to Okta provisioning
- **Rationale**: Modern identity platform, supports commercial banking MFA/security requirements

#### **A-and-P (Existing - Direct Client Users)**
- **Purpose**: Authentication for direct client users (via Express integration)
- **Ownership**: Express owns user lifecycle (existing self-serve UI)
- **Platform Role**: Replicate users from Express for permission/approval enforcement only
- **Integration**: Indirect via Express event streaming (Express → Platform)
- **Rationale**: Cannot modify Express; avoid disrupting existing A-and-P integration

### Express Platform Integration (Legacy System - Big Ball of Mud)
- **Critical Context**: Express is legacy application - cannot be modified or extended
- **Express Current State**: Has self-serve user management for direct clients with A-and-P authentication
- **Express Limitation**: No concept of indirect clients (payors) - cannot support new receivables model
- **Integration Pattern**: Anti-corruption layer - consume events without modifying Express
- **Integration Type**: Unidirectional event streaming (Express → Platform)
- **Events Consumed**: User add/update events only (near real-time, <5 minutes latency)
- **Linking Mechanism**: Online profile stores site-id reference to Express profile
- **Ownership Model**:
  - **Direct client users**: Express + A-and-P own, platform replicates (read-only from platform perspective)
  - **Indirect client users**: Platform + Okta own, full lifecycle management
- **Rationale**: Separate concerns - avoid polluting platform domain with Express complexity

### Dual-Portal Architecture
- **Employee Portal** (bank staff):
  - Manage Canadian online profile enrollment
  - View direct clients + their indirect client (payor) relationships
  - Monitor corporate structures and account aggregations
  - Primary operational tool for MVP
- **Self-Service Portal** (indirect clients/payors):
  - Manage own users, permissions, and approval rules
  - Process invoice approvals
  - Independent from employee portal

### Client Profile Types
- **Online Profile** (direct client):
  - Managed by bank via employee portal
  - Linked to Express via site-id for user replication
  - **Primary client**: Single SRF client (MVP - no secondary client support)
  - **Deferred (Post-MVP)**: Secondary clients for corporate structures (head office/subsidiary, holding/child)
- **Indirect Profile** (payors):
  - Can be person (name) OR business (business name + related persons)
  - Managed via self-service portal by payors themselves
- **Related Persons**: Signing-officer, administrator, director roles for business indirect clients

### Permission & Approval Management
- **Direct Client**: Bank-managed (MVP); platform stores and enforces rules
- **Indirect Clients**: Self-service (payors create their own approval rules)
- **Approval Workflow Engine** (MVP - Core):
  - Generic, reusable across services
  - Configurable rules: single approver, dual approver, approval thresholds (amount-based)
  - Workflow states: pending → approved/rejected/expired
  - Approval sequences: parallel or sequential
  - Supports receivable-approval service for indirect clients
- **Future**: Self-service for direct clients (post-MVP)

### Domain Classification (Preliminary)
- **Core** (MVP):
  - Receivables Service Management
  - Indirect Client Management
  - **Approval Workflows** (generic approval engine with configurable rules)
- **Supporting** (MVP):
  - Direct Client Onboarding
  - User Management (dual path: Express sync for direct, Okta lifecycle for indirect)
  - Account Enrollment (primary client only)
  - Employee Portal Operations
  - **Identity Management** (Okta integration for indirect client users)
- **Generic**:
  - Authentication (abstraction over Okta + A-and-P)
  - Audit
- **Deferred (Post-MVP)**:
  - Corporate Structure Management (secondary client linking)
  - Unified identity (migrate direct client users from A-and-P to Okta)

## Domain Schemas Available
- **DDD Schema**: `domains/ddd/model-schema.yaml` ✅ (Custom YAML format, strategic + tactical patterns)
- **Data-Eng Schema**: `domains/data-eng/model-schema.yaml` ✅ (JSON Schema format, pipelines + datasets + contracts)
- **UX Schema**: `domains/ux/model-schema.yaml` ✅ (Custom YAML format, pages + workflows + components)
- **QE Schema**: `domains/qe/model-schema.yaml` ✅ (Custom YAML format, test strategies + cases)
- **Agile Schema**: `domains/agile/model-schema.yaml` ✅ (JSON Schema format, product + vision + releases + features)
- **Interdomain Map**: `domains/interdomain-map.yaml` ✅ (28 groundings between 5 canonical models)

## Platform Context (Quick Reference)

### Client Types
- Small business (sole proprietor)
- Mid-market commercial
- Large commercial clients

### Geographic Coverage
- **North-South**: Canada (primary) + US
- **East-West**: US East + US West banks
- **Future**: UK expansion

### Client Identification Systems
- **SRF**: Canadian banking platform (demographics, accounts)
- **GID**: Global ID for Capital Markets (multi-jurisdiction)
- **IND**: Indirect Clients (managed on behalf of direct clients)

### Core Domains (Preliminary)
1. **Client Management** (Core) - Client profiles, enrollment
2. **Service Management** (Core) - Service enrollment, configuration
3. **User Management** (Supporting) - Users, identity providers, roles
4. **Permission & Approval** (Supporting) - Permission policies, approval rules
5. **Account Integration** (Supporting) - Account enrollment, status tracking

### Key Account Systems
- **Canadian Deposits**: DDA (CAD/USD), FCA (multi-currency)
- **Canadian Lending**: OLB (lines/loans), MTG (mortgages), GIC (investments)
- **Payments**: ACH/GSAN (linked to DDA)
- **Credit Cards**: TSYS (individual), Commercial Cards (pool)
- **US Accounts**: US:DDA

### Service Categories
- **Stand-alone**: BTR (Balance/Transaction Reporting), ACH Debit Block, Additional Deposit Narrative
- **Online**: Interac Send, Receivables
- **Indirect**: Receivable-Approval Service

### User Management Features
- Dual admin requirement (2 administrators)
- Identity providers: A-and-P (current), Okta (planned)
- User locking (by admin or bank)
- Permission policies (AWS IAM-like: subject, action URN, resource)
- Approval policies (extends permission with approver count)

## Resume Instructions

To resume work, ask the user:
1. "Would you like to continue with the current phase or start a different phase?"
2. Review this file for context
3. Check `model/` directory for existing model files
4. Refer to `prompts/workflow-prompt.md` for phase details

## Terminology Reminder

- **Define**: Create initial version → Ask 3-5 clarifying questions → Incorporate feedback → Check ripple effects
- **Refine**: Enhance through iteration → Ask 3-5 questions → Loop until user says "done for this turn"
- User can redirect at any time

---

*Last Updated*: 2025-10-15
*Workflow Reference*: `prompts/workflow-prompt.md`
*Platform Description*: `prompts/egg.md`
