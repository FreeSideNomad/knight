# Platform Ddd

*Generated from: platform-ddd.yaml*

---

# Reference Index

Quick navigation to all identified objects:

### Systems

- [Cash Mgmt Platform System](#sys-cash-mgmt-platform) - Commercial Banking Cash Management Platform

### Domains

- [Approval Workflows Domain](#dom-approval-workflows) - Approval Workflows
- [Client Account Integration Domain](#dom-client-account-integration) - Client and Account Integration
- [Service Profiles Domain](#dom-service-profiles) - Service Profiles
- [User Management Domain](#dom-user-management) - User Management

### Bounded Contexts

- [Account Data Serving Bounded Context](#bc-account-data-serving) - Account Data Serving
- [Account Data Sync Bounded Context](#bc-account-data-sync) - Account Data Sync
- [Approval Engine Bounded Context](#bc-approval-engine) - Approval Engine
- [Client Data Serving Bounded Context](#bc-client-data-serving) - Client Data Serving
- [Identity Integration Bounded Context](#bc-identity-integration) - Identity Integration
- [Indirect Client Management Bounded Context](#bc-indirect-client-management) - Indirect Client Management
- [Permission Management Bounded Context](#bc-permission-management) - Permission Management
- [Service Profile Management Bounded Context](#bc-service-profile-management) - Service Profile Management
- [User Lifecycle Bounded Context](#bc-user-lifecycle) - User Lifecycle

### Context Mappings

- [Account Sync To Srf Context Mapping](#cm-account-sync-to-srf)
- [Identity Integration To Express Context Mapping](#cm-identity-integration-to-express)
- [Receivable Approval To Approval Engine Context Mapping](#cm-receivable-approval-to-approval-engine)
- [Service Profile To Account Serving Context Mapping](#cm-service-profile-to-account-serving)
- [Service Profile To Client Serving Context Mapping](#cm-service-profile-to-client-serving)
- [Service Profile To Indirect Clients Context Mapping](#cm-service-profile-to-indirect-clients)

### Other

- [svc_app_receivable_approval_enrollment](#svc-app-receivable-approval-enrollment)
- [svc_app_receivables_enrollment](#svc-app-receivables-enrollment)

---

# Table of Contents

- [System](#system)
- [Domains](#domains)
- [Bounded Contexts](#bounded-contexts)
- [Context Mappings](#context-mappings)
- [Metadata](#metadata)

---

<a id="system"></a>
## System

<a id="sys-cash-mgmt-platform"></a>
**ID**: `sys_cash_mgmt_platform` (Cash Mgmt Platform System)

**Name**: Commercial Banking Cash Management Platform

**Version**: 0.2.0

#### Domains

- dom_service_profiles
- dom_user_management
- dom_approval_workflows
- dom_client_account_integration


---

<a id="domains"></a>
## Domains

#### Dom Service Profiles

<a id="dom-service-profiles"></a>
**ID**: `dom_service_profiles` (Service Profiles Domain)

**Name**: Service Profiles

**Type**: core

**Strategic Importance**: critical

**Description**: 
Core domain managing SERVICE PROFILES for clients (not clients themselves - client data from SRF/GID). Three profile types: servicing, online (direct client), indirect (payor). Service enrollment managed through application services (stand-alone like BTR, online like Receivables, indirect like receivable-approval). Indirect client management is part of this domain since indirect clients exist only to serve indirect service profiles.

##### Bounded Contexts

- bc_service_profile_management
- bc_indirect_client_management

#### Dom User Management

<a id="dom-user-management"></a>
**ID**: `dom_user_management` (User Management Domain)

**Name**: User Management

**Type**: supporting

**Strategic Importance**: important

**Description**: 
User lifecycle for direct clients (Express sync) and indirect clients (Okta managed). Identity provider integration with dual providers.

##### Bounded Contexts

- bc_user_lifecycle
- bc_identity_integration

#### Dom Approval Workflows

<a id="dom-approval-workflows"></a>
**ID**: `dom_approval_workflows` (Approval Workflows Domain)

**Name**: Approval Workflows

**Type**: core

**Strategic Importance**: critical

**Description**: 
Generic approval workflow engine. AWS IAM-inspired permission model. Parallel approval only (MVP). Reusable across all services.

##### Bounded Contexts

- bc_permission_management
- bc_approval_engine

#### Dom Client Account Integration

<a id="dom-client-account-integration"></a>
**ID**: `dom_client_account_integration` (Client Account Integration Domain)

**Name**: Client and Account Integration

**Type**: supporting

**Strategic Importance**: important

**Description**: 
Provides SERVING LAYER (read-only) for Service Profile domain to access client demographics, account data, and user information for profile creation and account linkage. Consumes gold copy data created by data engineering pipelines.

##### Bounded Contexts

- bc_account_data_serving
- bc_client_data_serving


---

<a id="bounded-contexts"></a>
## Bounded Contexts

#### Bc Service Profile Management

<a id="bc-service-profile-management"></a>
**ID**: `bc_service_profile_management` (Service Profile Management Bounded Context)

**Name**: Service Profile Management

**Domain Ref**: [Service Profiles Domain](#dom-service-profiles)

**Description**: 
Manages SERVICE PROFILES (not clients). Links to client data (SRF/GID/IND). Three types: servicing, online, indirect. Enrolls services and accounts through application services. Consumes account and client data from serving layers (read-only).

##### Responsibilities

- Create/manage service profiles (servicing, online, indirect)
- Link profile to SRF/GID client data (external)
- Enroll services to profiles through application services
- Enroll accounts to profiles (via serving layer)
- Link online profile to Express (site-id)
- Store permission/approval policies (owned by profile)

##### Application Services

- svc_app_receivables_enrollment
- svc_app_receivable_approval_enrollment

#### Bc Indirect Client Management

<a id="bc-indirect-client-management"></a>
**ID**: `bc_indirect_client_management` (Indirect Client Management Bounded Context)

**Name**: Indirect Client Management

**Domain Ref**: [Service Profiles Domain](#dom-service-profiles)

**Description**: 
Manages indirect clients (BUSINESS payors only, MVP). Part of Service Profiles domain since indirect clients exist only for indirect service profiles. IND identification, business info, related persons. Self-service user/permission/approval management.

##### Responsibilities

- Create/manage indirect client profiles (IND ID)
- Store business info and related persons
- Link to direct client service profile
- Support self-service management

#### Svc App Receivables Enrollment

<a id="svc-app-receivables-enrollment"></a>
**ID**: `svc_app_receivables_enrollment` (Receivables Enrollment Application Service)

**Name**: Receivables Enrollment Service

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Application service for enrolling Receivables (online) service to direct client profiles. Manages GSAN account linkage and indirect client (payor) onboarding for receivables. ONE OF MANY service enrollment types.

##### Responsibilities

- Enroll receivables service for online profiles
- Link GSAN accounts to service
- Onboard indirect clients for direct client
- Configure receivables settings

#### Svc App Receivable Approval Enrollment

<a id="svc-app-receivable-approval-enrollment"></a>
**ID**: `svc_app_receivable_approval_enrollment` (Receivable Approval Enrollment Application Service)

**Name**: Receivable-Approval Enrollment Service

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Application service for enrolling Receivable-Approval (indirect) service to indirect client profiles. Manages approval rule configuration and payor DDA account linkage. Coordinates with Approval Engine for invoice approval workflows.

##### Responsibilities

- Enroll receivable-approval service for indirect profiles
- Configure approval rules (via bc_approval_engine)
- Link payor DDA accounts for payments
- Coordinate invoice approvals (via bc_approval_engine)

#### Bc User Lifecycle

<a id="bc-user-lifecycle"></a>
**ID**: `bc_user_lifecycle` (User Lifecycle Bounded Context)

**Name**: User Lifecycle

**Domain Ref**: [User Management Domain](#dom-user-management)

**Description**: 
Direct client users (replicated from Express) and indirect client users (managed in Okta). ~1050 indirect users across 700 business payors (MVP).

##### Responsibilities

- Replicate direct client users from Express (read-only)
- Manage indirect client users in Okta (full lifecycle)
- Track user roles and states
- Enforce dual admin requirement

#### Bc Identity Integration

<a id="bc-identity-integration"></a>
**ID**: `bc_identity_integration` (Identity Integration Bounded Context)

**Name**: Identity Integration

**Domain Ref**: [User Management Domain](#dom-user-management)

**Description**: 
Anti-corruption layer for dual identity providers. Consumes Express user events (add, update) via streaming. Integrates with Okta APIs for indirect client users.

##### Responsibilities

- Consume Express user events (anti-corruption layer)
- Integrate with Okta APIs for indirect users
- Abstract identity provider differences
- Translate to platform user model

#### Bc Permission Management

<a id="bc-permission-management"></a>
**ID**: `bc_permission_management` (Permission Management Bounded Context)

**Name**: Permission Management

**Domain Ref**: [Approval Workflows Domain](#dom-approval-workflows)

**Description**: 
AWS IAM-inspired permission/approval policies. Subject (user), action (URN), resource (accounts). Approval policy adds approver count and thresholds.

##### Responsibilities

- Store permission policies (profiles and indirect clients)
- Store approval policies with approver rules
- Validate user permissions
- Determine approval requirements

#### Bc Approval Engine

<a id="bc-approval-engine"></a>
**ID**: `bc_approval_engine` (Approval Engine Bounded Context)

**Name**: Approval Engine

**Domain Ref**: [Approval Workflows Domain](#dom-approval-workflows)

**Description**: 
Generic approval workflow engine. Parallel approval only (MVP). Single or multiple approvers. Amount-based thresholds. States: pending, approved, rejected, expired. Reusable across all services.

##### Responsibilities

- Create approval workflow instances
- Enforce approval rules (parallel approval)
- Track approval progress
- Update workflow state
- Notify participants

#### Bc Account Data Sync

<a id="bc-account-data-sync"></a>
**ID**: `bc_account_data_sync` (Account Data Sync Bounded Context)

**Name**: Account Data Sync

**Description**: 
Data engineering ETL infrastructure. Daily batch from SRF for client-owned accounts. Creates GOLD COPY of account data consumed by serving layer. Detects new/closed accounts. Data quality checks. Infrastructure supporting Client and Account Integration domain.

##### Responsibilities

- Consume SRF daily batch feed
- Create gold copy of account data
- Detect account status changes (new, closed)
- Data quality and transformation
- Trigger auto-enrollment notifications

#### Bc Account Data Serving

<a id="bc-account-data-serving"></a>
**ID**: `bc_account_data_serving` (Account Data Serving Bounded Context)

**Name**: Account Data Serving

**Domain Ref**: [Client Account Integration Domain](#dom-client-account-integration)

**Description**: 
SERVING LAYER providing READ-ONLY access to gold copy account data. Service Profile domain consumes this for account enrollment and validation.

##### Responsibilities

- Provide read-only API to account gold copy
- Query account data by client ID
- Validate account existence and status
- Support account enrollment workflows

#### Bc Client Data Serving

<a id="bc-client-data-serving"></a>
**ID**: `bc_client_data_serving` (Client Data Serving Bounded Context)

**Name**: Client Data Serving

**Domain Ref**: [Client Account Integration Domain](#dom-client-account-integration)

**Description**: 
SERVING LAYER providing READ-ONLY access to gold copy client demographics and user data. Service Profile domain consumes this for client validation and user synchronization.

##### Responsibilities

- Provide read-only API to client gold copy
- Query client demographics by client ID
- Validate client existence and status
- Provide user data for permission and approval enforcement
- Support profile creation and validation workflows


---

<a id="context-mappings"></a>
## Context Mappings

#### Cm Service Profile To Account Serving

<a id="cm-service-profile-to-account-serving"></a>
**ID**: `cm_service_profile_to_account_serving` (Service Profile To Account Serving Context Mapping)

**Upstream Context**: [Account Data Serving Bounded Context](#bc-account-data-serving)

**Downstream Context**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Relationship Type**: customer_supplier

**Description**: 
Service Profile Management (downstream) consumes account data from Account Data Serving (upstream) for account enrollment. Read-only access to gold copy.

#### Cm Service Profile To Client Serving

<a id="cm-service-profile-to-client-serving"></a>
**ID**: `cm_service_profile_to_client_serving` (Service Profile To Client Serving Context Mapping)

**Upstream Context**: [Client Data Serving Bounded Context](#bc-client-data-serving)

**Downstream Context**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Relationship Type**: customer_supplier

**Description**: 
Service Profile Management (downstream) consumes client demographics and user data from Client Data Serving (upstream) for profile creation and validation. Read-only access to gold copy.

#### Cm Receivable Approval To Approval Engine

<a id="cm-receivable-approval-to-approval-engine"></a>
**ID**: `cm_receivable_approval_to_approval_engine` (Receivable Approval To Approval Engine Context Mapping)

**Upstream Context**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Downstream Context**: [Approval Engine Bounded Context](#bc-approval-engine)

**Relationship Type**: customer_supplier

**Description**: 
Service Profile Management (via Receivable-Approval Enrollment application service) triggers invoice approvals via generic Approval Engine. Approval Engine provides reusable workflow logic.

#### Cm Service Profile To Indirect Clients

<a id="cm-service-profile-to-indirect-clients"></a>
**ID**: `cm_service_profile_to_indirect_clients` (Service Profile To Indirect Clients Context Mapping)

**Upstream Context**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Downstream Context**: [Indirect Client Management Bounded Context](#bc-indirect-client-management)

**Relationship Type**: customer_supplier

**Description**: 
Direct client service profile onboards indirect clients. One-to-many (1 profile → 700 indirect clients, MVP).

#### Cm Identity Integration To Express

<a id="cm-identity-integration-to-express"></a>
**ID**: `cm_identity_integration_to_express` (Identity Integration To Express Context Mapping)

**Upstream Context**: express_platform

**Downstream Context**: [Identity Integration Bounded Context](#bc-identity-integration)

**Relationship Type**: anti_corruption_layer

**Description**: 
Express publishes user events. Identity Integration consumes via ACL to protect domain from Express complexity (big ball of mud). Unidirectional: Express → Platform.

#### Cm Account Sync To Srf

<a id="cm-account-sync-to-srf"></a>
**ID**: `cm_account_sync_to_srf` (Account Sync To Srf Context Mapping)

**Upstream Context**: srf_system

**Downstream Context**: [Account Data Sync Bounded Context](#bc-account-data-sync)

**Relationship Type**: anti_corruption_layer

**Description**: 
SRF provides daily batch. Account Data Sync consumes via ACL to protect domain from SRF complexity. Creates gold copy.


---

<a id="metadata"></a>
## Metadata

**Phase**: phase_2_strategic

**Last Updated**: 2025-10-15

#### Key Changes

- Merged Client Management + Service Management into Service Profiles domain
- Service profiles for clients, not client management itself
- Indirect clients part of Service Profiles (exist only for service profiles)
- Created Data Engineering domain with gold copy and serving layer
- Receivable-approval is separate business service (not part of Receivables)
- Serving layer provides read-only account data to Service Profile domain


---

