# Platform Ddd

*Generated from: platform-ddd.yaml*

---

# Reference Index

Quick navigation to all identified objects:

### Systems

- [Cash Mgmt Platform System](#sys-cash-mgmt-platform) - Commercial Banking Cash Management Platform

### Domains

- [Approval Workflows Domain](#dom-approval-workflows) - Approval Workflows
- [External Data Domain](#dom-external-data) - External Data
- [Service Profiles Domain](#dom-service-profiles) - Service Profiles
- [User Management Domain](#dom-user-management) - User Management

### Bounded Contexts

- [Account Data Sync Bounded Context](#bc-account-data-sync) - Account Data Sync
- [Approval Engine Bounded Context](#bc-approval-engine) - Approval Engine
- [External Data Serving Bounded Context](#bc-external-data-serving) - External Data Serving
- [Identity Integration Bounded Context](#bc-identity-integration) - Identity Integration
- [Indirect Client Management Bounded Context](#bc-indirect-client-management) - Indirect Client Management
- [Policy Bounded Context](#bc-policy) - Policy
- [Service Profile Management Bounded Context](#bc-service-profile-management) - Service Profile Management
- [Users Bounded Context](#bc-users) - Users

### Context Mappings

- [Account Sync To Srf Context Mapping](#cm-account-sync-to-srf)
- [Approval Engine To Policy Context Mapping](#cm-approval-engine-to-policy)
- [Identity Integration To Express Context Mapping](#cm-identity-integration-to-express)
- [Receivable Approval To Approval Engine Context Mapping](#cm-receivable-approval-to-approval-engine)
- [Service Profile To External Data Context Mapping](#cm-service-profile-to-external-data)
- [Service Profile To Indirect Clients Context Mapping](#cm-service-profile-to-indirect-clients)

### Other

- [svc_app_account_data](#svc-app-account-data)
- [svc_app_client_data](#svc-app-client-data)
- [svc_app_indirect_profile](#svc-app-indirect-profile)
- [svc_app_online_profile](#svc-app-online-profile)
- [svc_app_servicing_profile](#svc-app-servicing-profile)
- [svc_app_user_data](#svc-app-user-data)

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
- dom_external_data


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

**Type**: core

**Strategic Importance**: critical

**Description**: 
User lifecycle for direct clients (Express sync) and indirect clients (Okta managed). Identity provider integration with dual providers. Permission and approval policy management (AWS IAM-inspired). Policies define WHO can DO WHAT on WHICH resources.

##### Bounded Contexts

- bc_users
- bc_identity_integration
- bc_policy

#### Dom Approval Workflows

<a id="dom-approval-workflows"></a>
**ID**: `dom_approval_workflows` (Approval Workflows Domain)

**Name**: Approval Workflows

**Type**: core

**Strategic Importance**: critical

**Description**: 
Generic approval workflow execution engine. Executes approval workflows based on policies defined in User Management domain. Parallel approval only (MVP). Reusable across all services. Critical for business operations requiring approval controls.

##### Bounded Contexts

- bc_approval_engine

#### Dom External Data

<a id="dom-external-data"></a>
**ID**: `dom_external_data` (External Data Domain)

**Name**: External Data

**Type**: supporting

**Strategic Importance**: important

**Description**: 
Provides SERVING LAYER (read-only) for Service Profile domain to access external data: client demographics (SRF), account data (SRF), and user data (Express). Consumes gold copy data created by data engineering pipelines. Serving layer abstracts external systems from domain model.

##### Bounded Contexts

- bc_external_data_serving


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

- svc_app_servicing_profile
- svc_app_online_profile
- svc_app_indirect_profile

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

#### Svc App Servicing Profile

<a id="svc-app-servicing-profile"></a>
**ID**: `svc_app_servicing_profile` (Servicing Profile Application Service)

**Name**: Servicing Profile Service

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Application service for managing servicing profiles. Creates new servicing profile linked to SRF/GID client. Adds, modifies, and removes services linked to this profile. Enrolls accounts. Manages permission/approval policies owned by servicing profile.

##### Responsibilities

- Create new servicing profile (link to SRF/GID client)
- Add services to servicing profile
- Modify service configurations on profile
- Remove services from profile
- Enroll accounts to services on profile
- Manage permission/approval policies for profile
- Use users and user groups as policy subjects

#### Svc App Online Profile

<a id="svc-app-online-profile"></a>
**ID**: `svc_app_online_profile` (Online Profile Application Service)

**Name**: Online Profile Service

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Application service for managing online profiles (direct client). Creates new online profile with single primary client (MVP). Links to Express via site-id. Adds, modifies, removes services like Receivables. Onboards indirect clients (payors). Manages bank-configured permission/approval policies.

##### Responsibilities

- Create new online profile (link to SRF primary client + Express site-id)
- Add services to online profile (Receivables, etc.)
- Modify service configurations on profile
- Remove services from profile
- Enroll GSAN and other accounts to services
- Onboard indirect clients for Receivables service
- Manage permission/approval policies (bank-configured, MVP)
- Use users and user groups as policy subjects

#### Svc App Indirect Profile

<a id="svc-app-indirect-profile"></a>
**ID**: `svc_app_indirect_profile` (Indirect Profile Application Service)

**Name**: Indirect Profile Service

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Application service for managing indirect profiles (business payors). Creates new indirect profile linked to IND client. Adds, modifies, removes services like Receivable-Approval. Links Canadian bank accounts. Manages self-service permission/approval policies configured by payor.

##### Responsibilities

- Create new indirect profile (link to IND indirect client)
- Add services to indirect profile (Receivable-Approval, etc.)
- Modify service configurations on profile
- Remove services from profile
- Link payor Canadian bank accounts for invoice payments
- Manage permission/approval policies (self-service configured)
- Use users and user groups as policy subjects
- Coordinate invoice approvals via bc_approval_engine

#### Bc Users

<a id="bc-users"></a>
**ID**: `bc_users` (Users Bounded Context)

**Name**: Users

**Domain Ref**: [User Management Domain](#dom-user-management)

**Description**: 
Manages users and user groups. Direct client users (replicated from Express) and indirect client users (managed in Okta). ~1050 indirect users across 700 business payors (MVP). Users and user groups used as subjects in permission/approval policies.

##### Responsibilities

- Replicate direct client users from Express (read-only)
- Manage indirect client users in Okta (full lifecycle)
- Manage user groups for policy subjects
- Track user roles and states
- Enforce dual admin requirement
- Provide users and user groups for policy evaluation

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

#### Bc Policy

<a id="bc-policy"></a>
**ID**: `bc_policy` (Policy Bounded Context)

**Name**: Policy

**Domain Ref**: [User Management Domain](#dom-user-management)

**Description**: 
Permission and approval policy management (AWS IAM-inspired). Policies owned by service profiles. Subject (user/user group from bc_users), action (URN), resource (accounts/services). Approval policy adds approver count and thresholds. Defines WHO can DO WHAT on WHICH resources. Policies consumed by Approval Engine for workflow execution.

##### Responsibilities

- Store permission policies owned by service profiles
- Store approval policies with approver rules owned by profiles
- Use users and user groups from bc_users as policy subjects
- Validate user/user group permissions
- Determine approval requirements for actions
- Provide policy evaluation API for approval workflows

#### Bc Approval Engine

<a id="bc-approval-engine"></a>
**ID**: `bc_approval_engine` (Approval Engine Bounded Context)

**Name**: Approval Engine

**Domain Ref**: [Approval Workflows Domain](#dom-approval-workflows)

**Description**: 
Generic approval workflow execution engine. Executes workflows based on policies from Permission Management. Parallel approval only (MVP). Single or multiple approvers. Amount-based thresholds. States: pending, approved, rejected, expired. Reusable across all services.

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
Data engineering ETL infrastructure. Daily batch from SRF for client-owned accounts. Creates GOLD COPY of account data consumed by serving layer. Detects new/closed accounts. Data quality checks. Infrastructure supporting External Data domain.

##### Responsibilities

- Consume SRF daily batch feed
- Create gold copy of account data
- Detect account status changes (new, closed)
- Data quality and transformation
- Trigger auto-enrollment notifications

#### Bc External Data Serving

<a id="bc-external-data-serving"></a>
**ID**: `bc_external_data_serving` (External Data Serving Bounded Context)

**Name**: External Data Serving

**Domain Ref**: [External Data Domain](#dom-external-data)

**Description**: 
SERVING LAYER providing READ-ONLY access to gold copy external data from SRF and Express. Service Profile domain consumes this for profile creation, account enrollment, and user synchronization. Three application services provide specialized access: client demographics, account data, and user data.

##### Responsibilities

- Provide read-only API to external data gold copies
- Abstract SRF and Express systems from domain model
- Support profile creation and validation workflows
- Support account enrollment workflows
- Support user synchronization for permission/approval enforcement

##### Application Services

- svc_app_client_data
- svc_app_account_data
- svc_app_user_data

#### Svc App Client Data

<a id="svc-app-client-data"></a>
**ID**: `svc_app_client_data` (Client Data Application Service)

**Name**: Client Data Service

**Bounded Context Ref**: [External Data Serving Bounded Context](#bc-external-data-serving)

**Description**: 
Application service providing read-only access to client demographics gold copy from SRF. Service Profile domain consumes this for profile creation and client validation.

##### Responsibilities

- Provide read-only API to client gold copy (SRF demographics)
- Query client demographics by client ID (SRF/GID)
- Validate client existence and status
- Support online and servicing profile creation workflows

#### Svc App Account Data

<a id="svc-app-account-data"></a>
**ID**: `svc_app_account_data` (Account Data Application Service)

**Name**: Account Data Service

**Bounded Context Ref**: [External Data Serving Bounded Context](#bc-external-data-serving)

**Description**: 
Application service providing read-only access to account data gold copy from SRF daily batch. Service Profile domain consumes this for account enrollment and validation.

##### Responsibilities

- Provide read-only API to account gold copy (SRF batch)
- Query account data by client ID
- Validate account existence and status
- Support account enrollment workflows for all profile types

#### Svc App User Data

<a id="svc-app-user-data"></a>
**ID**: `svc_app_user_data` (User Data Application Service)

**Name**: User Data Service

**Bounded Context Ref**: [External Data Serving Bounded Context](#bc-external-data-serving)

**Description**: 
Application service providing read-only access to user data gold copy from Express events. User Management domain (bc_users) consumes this for direct client user synchronization and Policy domain (bc_policy) consumes for permission/approval enforcement.

##### Responsibilities

- Provide read-only API to user gold copy (Express events)
- Query user data by Express site-id
- Validate user existence and status
- Support user synchronization for bc_users
- Support policy evaluation for bc_policy (permission/approval enforcement)


---

<a id="context-mappings"></a>
## Context Mappings

#### Cm Service Profile To External Data

<a id="cm-service-profile-to-external-data"></a>
**ID**: `cm_service_profile_to_external_data` (Service Profile To External Data Context Mapping)

**Upstream Context**: [External Data Serving Bounded Context](#bc-external-data-serving)

**Downstream Context**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Relationship Type**: customer_supplier

**Description**: 
Service Profile Management (downstream) consumes external data from External Data Serving (upstream) for profile creation, account enrollment, and validation. Read-only access to gold copies via three application services: svc_app_client_data (SRF demographics), svc_app_account_data (SRF batch), svc_app_user_data (Express events).

#### Cm Approval Engine To Policy

<a id="cm-approval-engine-to-policy"></a>
**ID**: `cm_approval_engine_to_policy` (Approval Engine To Policy Context Mapping)

**Upstream Context**: [Policy Bounded Context](#bc-policy)

**Downstream Context**: [Approval Engine Bounded Context](#bc-approval-engine)

**Relationship Type**: customer_supplier

**Description**: 
Approval Engine (downstream) consumes permission and approval policies from Policy (upstream). Evaluates policies owned by service profiles to determine approval requirements and enforce rules during workflow execution. Policies use users/user groups from bc_users as subjects.

#### Cm Receivable Approval To Approval Engine

<a id="cm-receivable-approval-to-approval-engine"></a>
**ID**: `cm_receivable_approval_to_approval_engine` (Receivable Approval To Approval Engine Context Mapping)

**Upstream Context**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Downstream Context**: [Approval Engine Bounded Context](#bc-approval-engine)

**Relationship Type**: customer_supplier

**Description**: 
Service Profile Management (via Receivable-Approval Enrollment application service) triggers invoice approvals via generic Approval Engine. Approval Engine provides reusable workflow execution.

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

