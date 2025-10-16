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

### Aggregates

- [Approval Statement Aggregate](#agg-approval-statement) - ApprovalStatement
- [Indirect Client Aggregate](#agg-indirect-client) - IndirectClient
- [Indirect Profile Aggregate](#agg-indirect-profile) - IndirectProfile
- [Online Profile Aggregate](#agg-online-profile) - OnlineProfile
- [Permission Statement Aggregate](#agg-permission-statement) - PermissionStatement
- [Servicing Profile Aggregate](#agg-servicing-profile) - ServicingProfile
- [User Aggregate](#agg-user) - User
- [User Group Aggregate](#agg-user-group) - UserGroup

### Context Mappings

- [Account Sync To Srf Context Mapping](#cm-account-sync-to-srf)
- [Approval Engine To Policy Context Mapping](#cm-approval-engine-to-policy)
- [Identity Integration To Express Context Mapping](#cm-identity-integration-to-express)
- [Receivable Approval To Approval Engine Context Mapping](#cm-receivable-approval-to-approval-engine)
- [Service Profile To External Data Context Mapping](#cm-service-profile-to-external-data)
- [Service Profile To Indirect Clients Context Mapping](#cm-service-profile-to-indirect-clients)

### Other

- [ent_account_enrollment](#ent-account-enrollment)
- [ent_approval_statement](#ent-approval-statement)
- [ent_indirect_client](#ent-indirect-client)
- [ent_indirect_profile](#ent-indirect-profile)
- [ent_online_profile](#ent-online-profile)
- [ent_permission_statement](#ent-permission-statement)
- [ent_related_person](#ent-related-person)
- [ent_service_enrollment](#ent-service-enrollment)
- [ent_servicing_profile](#ent-servicing-profile)
- [ent_user](#ent-user)
- [ent_user_group](#ent-user-group)
- [ent_user_group_membership](#ent-user-group-membership)
- [evt_approval_completed](#evt-approval-completed)
- [evt_approval_statement_created](#evt-approval-statement-created)
- [evt_approval_statement_deleted](#evt-approval-statement-deleted)
- [evt_approval_statement_updated](#evt-approval-statement-updated)
- [evt_approval_workflow_started](#evt-approval-workflow-started)
- [evt_permission_statement_created](#evt-permission-statement-created)
- [evt_permission_statement_deleted](#evt-permission-statement-deleted)
- [evt_permission_statement_updated](#evt-permission-statement-updated)
- [repo_approval_statement](#repo-approval-statement)
- [repo_indirect_client](#repo-indirect-client)
- [repo_indirect_profile](#repo-indirect-profile)
- [repo_online_profile](#repo-online-profile)
- [repo_permission_statement](#repo-permission-statement)
- [repo_servicing_profile](#repo-servicing-profile)
- [repo_user](#repo-user)
- [repo_user_group](#repo-user-group)
- [svc_app_account_data](#svc-app-account-data)
- [svc_app_client_data](#svc-app-client-data)
- [svc_app_indirect_profile](#svc-app-indirect-profile)
- [svc_app_online_profile](#svc-app-online-profile)
- [svc_app_servicing_profile](#svc-app-servicing-profile)
- [svc_app_user_data](#svc-app-user-data)
- [svc_dom_policy_evaluator](#svc-dom-policy-evaluator)
- [svc_dom_profile_validation](#svc-dom-profile-validation)
- [svc_dom_sequence_generator](#svc-dom-sequence-generator)
- [vo_client_id](#vo-client-id)
- [vo_indirect_client_id](#vo-indirect-client-id)
- [vo_indirect_profile_id](#vo-indirect-profile-id)
- [vo_online_profile_id](#vo-online-profile-id)
- [vo_profile_id_base](#vo-profile-id-base)
- [vo_servicing_profile_id](#vo-servicing-profile-id)

---

# Table of Contents

- [System](#system)
- [Domains](#domains)
- [Bounded Contexts](#bounded-contexts)
- [Context Mappings](#context-mappings)
- [Value Objects](#value-objects)
- [Aggregates](#aggregates)
- [Domain Events](#domain-events)
- [Entities](#entities)
- [Domain Services](#domain-services)
- [Application Services Expanded](#application-services-expanded)
- [Repositories](#repositories)
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

<a id="value-objects"></a>
## Value Objects

#### Vo Client Id

<a id="vo-client-id"></a>
**ID**: `vo_client_id` (Client Id Value Object)

**Name**: ClientId

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Client identifier as URN: {system}:{client_number} where system is srf, gid, or ind. Example: srf:12345, gid:G789, ind:IND001

##### Attributes

| Name | Type | Description | Validation |
| --- | --- | --- | --- |
| system | string | System identifier (srf, gid, ind) | Must be one of: srf, gid, ind |
| client_number | string | Client number within system | Non-empty string |
| urn | string | Full URN representation: {system}:{client_number} |  |

**Immutable**: ✓ Yes

##### Validation Rules

- system must be srf, gid, or ind
- client_number must not be empty
- URN format: {system}:{client_number}

#### Vo Profile Id Base

<a id="vo-profile-id-base"></a>
**ID**: `vo_profile_id_base` (Profile Id Base Value Object)

**Name**: ProfileId

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Base class for profile identifiers. All profiles stored as URN for polymorphic handling. Subclasses: ServicingProfileId, OnlineProfileId, IndirectProfileId.

##### Attributes

| Name | Type | Description |
| --- | --- | --- |
| urn | string | URN representation of profile ID |

**Immutable**: ✓ Yes

**Is Abstract**: ✓ Yes

#### Vo Servicing Profile Id

<a id="vo-servicing-profile-id"></a>
**ID**: `vo_servicing_profile_id` (Servicing Profile Id Value Object)

**Name**: ServicingProfileId

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Servicing profile identifier: ServicingProfileId(clientId). Stored as URN.

**Extends**: [Profile Id Base Value Object](#vo-profile-id-base)

##### Attributes

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| client_id | value_object | Client identifier (SRF or GID) | vo_client_id |

**Immutable**: ✓ Yes

##### Validation Rules

- client_id system must be srf or gid

#### Vo Online Profile Id

<a id="vo-online-profile-id"></a>
**ID**: `vo_online_profile_id` (Online Profile Id Value Object)

**Name**: OnlineProfileId

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Online profile identifier: OnlineProfileId(clientId, sequence). Sequence unique within client. Stored as URN.

**Extends**: [Profile Id Base Value Object](#vo-profile-id-base)

##### Attributes

| Name | Type | Description | Validation | Value Object Ref |
| --- | --- | --- | --- | --- |
| client_id | value_object | Client identifier (SRF) |  | vo_client_id |
| sequence | integer | Sequence number unique within client | Must be positive integer |  |

**Immutable**: ✓ Yes

##### Validation Rules

- client_id system must be srf
- sequence must be positive integer

#### Vo Indirect Profile Id

<a id="vo-indirect-profile-id"></a>
**ID**: `vo_indirect_profile_id` (Indirect Profile Id Value Object)

**Name**: IndirectProfileId

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Indirect profile identifier: IndirectProfileId(clientId, indirectClientId). Stored as URN. IndirectClientId has its own sequence within client.

**Extends**: [Profile Id Base Value Object](#vo-profile-id-base)

##### Attributes

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| client_id | value_object | Direct client identifier (SRF) | vo_client_id |
| indirect_client_id | value_object | Indirect client identifier with sequence | vo_indirect_client_id |

**Immutable**: ✓ Yes

##### Validation Rules

- client_id system must be srf

#### Vo Indirect Client Id

<a id="vo-indirect-client-id"></a>
**ID**: `vo_indirect_client_id` (Indirect Client Id Value Object)

**Name**: IndirectClientId

**Bounded Context Ref**: [Indirect Client Management Bounded Context](#bc-indirect-client-management)

**Description**: 
Indirect client identifier: IndirectClientId(clientId, sequence). Sequence unique within client.

##### Attributes

| Name | Type | Description | Validation | Value Object Ref |
| --- | --- | --- | --- | --- |
| client_id | value_object | Parent direct client identifier |  | vo_client_id |
| sequence | integer | Sequence number unique within direct client | Must be positive integer |  |

**Immutable**: ✓ Yes

##### Validation Rules

- sequence must be positive integer unique within client


---

<a id="aggregates"></a>
## Aggregates

#### Agg Servicing Profile

<a id="agg-servicing-profile"></a>
**ID**: `agg_servicing_profile` (Servicing Profile Aggregate)

**Name**: ServicingProfile

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Root Ref**: [Servicing Profile Entity](#ent-servicing-profile)

##### Entities

- ent_servicing_profile
- ent_service_enrollment
- ent_account_enrollment

##### Value Objects

- vo_servicing_profile_id
- vo_client_id

##### Consistency Rules

- ServicingProfile must be linked to valid SRF or GID client
- Services can only be enrolled if profile is ACTIVE
- Accounts enrolled must belong to the linked client

##### Invariants

- profile_id must be unique
- client_id cannot change after creation
- At least one service must be enrolled for profile to be ACTIVE

##### Lifecycle Hooks

###### On Create

- Validate client exists via svc_app_client_data
- Publish ServicingProfileCreated event

###### On Update

- Publish ServicingProfileUpdated event

**Size Estimate**: medium

#### Agg Online Profile

<a id="agg-online-profile"></a>
**ID**: `agg_online_profile` (Online Profile Aggregate)

**Name**: OnlineProfile

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Root Ref**: [Online Profile Entity](#ent-online-profile)

##### Entities

- ent_online_profile
- ent_service_enrollment
- ent_account_enrollment

##### Value Objects

- vo_online_profile_id
- vo_client_id

##### Consistency Rules

- OnlineProfile must be linked to valid SRF primary client
- Express site-id must exist and link to same client
- Services can only be enrolled if profile is ACTIVE
- Indirect clients can only be onboarded for Receivables service

##### Invariants

- profile_id must be unique
- client_id cannot change after creation
- site_id cannot change after creation
- sequence must be unique within client

##### Lifecycle Hooks

###### On Create

- Validate client exists via svc_app_client_data
- Validate site-id exists via svc_app_client_data
- Generate next sequence number for client
- Publish OnlineProfileCreated event

###### On Update

- Publish OnlineProfileUpdated event

**Size Estimate**: large

#### Agg Indirect Profile

<a id="agg-indirect-profile"></a>
**ID**: `agg_indirect_profile` (Indirect Profile Aggregate)

**Name**: IndirectProfile

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Root Ref**: [Indirect Profile Entity](#ent-indirect-profile)

##### Entities

- ent_indirect_profile
- ent_service_enrollment
- ent_account_enrollment

##### Value Objects

- vo_indirect_profile_id
- vo_indirect_client_id
- vo_client_id

##### Consistency Rules

- IndirectProfile must be linked to valid indirect client
- Indirect client must belong to a valid direct client (online profile)
- Services can only be enrolled if profile is ACTIVE
- Accounts enrolled must be Canadian bank accounts for invoice payments

##### Invariants

- profile_id must be unique
- indirect_client_id cannot change after creation
- parent client_id cannot change after creation

##### Lifecycle Hooks

###### On Create

- Validate indirect client exists
- Validate parent direct client exists
- Publish IndirectProfileCreated event

###### On Update

- Publish IndirectProfileUpdated event

**Size Estimate**: medium

#### Agg Permission Statement

<a id="agg-permission-statement"></a>
**ID**: `agg_permission_statement` (Permission Statement Aggregate)

**Name**: PermissionStatement

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Root Ref**: [Permission Statement Entity](#ent-permission-statement)

##### Entities

- ent_permission_statement

##### Value Objects

- vo_profile_id_base

##### Consistency Rules

- PermissionStatement must be owned by a valid profile
- Subject (user/user group) must exist in bc_users
- Action must be valid URN
- Resource must reference valid profile resource

##### Invariants

- statement_id must be unique
- profile_id (owner) cannot change after creation
- Effect must be ALLOW or DENY

##### Lifecycle Hooks

###### On Create

- Validate profile exists
- Validate subject exists in bc_users
- Publish PermissionStatementCreated event

###### On Update

- Publish PermissionStatementUpdated event

###### On Delete

- Publish PermissionStatementDeleted event

**Size Estimate**: small

#### Agg Approval Statement

<a id="agg-approval-statement"></a>
**ID**: `agg_approval_statement` (Approval Statement Aggregate)

**Name**: ApprovalStatement

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Root Ref**: [Approval Statement Entity](#ent-approval-statement)

##### Entities

- ent_approval_statement

##### Value Objects

- vo_profile_id_base

##### Consistency Rules

- ApprovalStatement extends PermissionStatement with approver requirements
- Must specify approver count (1 or N for parallel approval)
- Amount thresholds must be positive if specified
- Approvers must exist in bc_users

##### Invariants

- statement_id must be unique
- profile_id (owner) cannot change after creation
- approver_count must be >= 1
- If amount_threshold specified, must be positive

##### Lifecycle Hooks

###### On Create

- Validate profile exists
- Validate approvers exist in bc_users
- Publish ApprovalStatementCreated event

###### On Update

- Publish ApprovalStatementUpdated event

###### On Delete

- Publish ApprovalStatementDeleted event

**Size Estimate**: small

#### Agg Indirect Client

<a id="agg-indirect-client"></a>
**ID**: `agg_indirect_client` (Indirect Client Aggregate)

**Name**: IndirectClient

**Bounded Context Ref**: [Indirect Client Management Bounded Context](#bc-indirect-client-management)

**Root Ref**: [Indirect Client Entity](#ent-indirect-client)

##### Entities

- ent_indirect_client
- ent_related_person

##### Value Objects

- vo_indirect_client_id
- vo_client_id

##### Consistency Rules

- IndirectClient must be BUSINESS type (no individuals in MVP)
- Business must have at least one related person
- Related person must have valid role (signing-officer, administrator, director)
- Must be linked to valid direct client (online profile)

##### Invariants

- indirect_client_id must be unique
- parent client_id cannot change after creation
- type must be BUSINESS
- sequence must be unique within parent client

##### Lifecycle Hooks

###### On Create

- Validate parent direct client exists
- Generate next sequence number within client
- Publish IndirectClientOnboarded event

###### On Update

- Publish IndirectClientUpdated event

**Size Estimate**: small

#### Agg User

<a id="agg-user"></a>
**ID**: `agg_user` (User Aggregate)

**Name**: User

**Bounded Context Ref**: [Users Bounded Context](#bc-users)

**Root Ref**: [User Entity](#ent-user)

##### Entities

- ent_user

##### Value Objects

- vo_profile_id_base

##### Consistency Rules

- Direct client users replicated from Express (read-only)
- Indirect client users managed in Okta (full lifecycle)
- Users must be linked to a profile
- Dual admin requirement: at least 2 admins per profile

##### Invariants

- user_id must be unique
- profile_id link cannot change after creation
- source must be EXPRESS or OKTA
- If source=EXPRESS, user is read-only in platform

##### Lifecycle Hooks

###### On Create

- If source=OKTA, create user in Okta via bc_identity_integration
- Publish UserCreated event

###### On Update

- If source=OKTA, update user in Okta via bc_identity_integration
- Publish UserUpdated event

**Size Estimate**: small

#### Agg User Group

<a id="agg-user-group"></a>
**ID**: `agg_user_group` (User Group Aggregate)

**Name**: UserGroup

**Bounded Context Ref**: [Users Bounded Context](#bc-users)

**Root Ref**: [User Group Entity](#ent-user-group)

##### Entities

- ent_user_group
- ent_user_group_membership

##### Value Objects

- vo_profile_id_base

##### Consistency Rules

- UserGroup must be linked to a profile
- Group members must be users from same profile
- User can belong to multiple groups

##### Invariants

- group_id must be unique
- profile_id link cannot change after creation
- All members must belong to same profile

##### Lifecycle Hooks

###### On Create

- Publish UserGroupCreated event

###### On Update

- Publish UserGroupUpdated event

**Size Estimate**: small


---

<a id="domain-events"></a>
## Domain Events

#### Evt Permission Statement Created

<a id="evt-permission-statement-created"></a>
**ID**: `evt_permission_statement_created` (Permission Statement Created Domain Event)

**Name**: PermissionStatementCreated

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Permission Statement Aggregate](#agg-permission-statement)

**Description**: 
Published when a permission statement is created for a service profile. Contains profile_id (owner), subject (user/user group), action URN, resource, and effect (ALLOW/DENY).

##### Payload

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| statement_id | string |  |  |
| profile_id | value_object |  | vo_profile_id_base |
| subject | string | User or user group ID |  |
| action | string | Action URN |  |
| resource | string | Resource identifier |  |
| effect | string | ALLOW or DENY |  |
| created_at | timestamp |  |  |
| created_by | string |  |  |

#### Evt Permission Statement Updated

<a id="evt-permission-statement-updated"></a>
**ID**: `evt_permission_statement_updated` (Permission Statement Updated Domain Event)

**Name**: PermissionStatementUpdated

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Permission Statement Aggregate](#agg-permission-statement)

**Description**: 
Published when a permission statement is updated. May include changes to subject, action, resource, or effect.

##### Payload

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| statement_id | string |  |  |
| profile_id | value_object |  | vo_profile_id_base |
| updated_fields | object | Map of field name to new value |  |
| updated_at | timestamp |  |  |
| updated_by | string |  |  |

#### Evt Permission Statement Deleted

<a id="evt-permission-statement-deleted"></a>
**ID**: `evt_permission_statement_deleted` (Permission Statement Deleted Domain Event)

**Name**: PermissionStatementDeleted

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Permission Statement Aggregate](#agg-permission-statement)

**Description**: 
Published when a permission statement is deleted. Consumers should revoke cached permissions.

##### Payload

| Name | Type | Value Object Ref |
| --- | --- | --- |
| statement_id | string |  |
| profile_id | value_object | vo_profile_id_base |
| deleted_at | timestamp |  |
| deleted_by | string |  |

#### Evt Approval Statement Created

<a id="evt-approval-statement-created"></a>
**ID**: `evt_approval_statement_created` (Approval Statement Created Domain Event)

**Name**: ApprovalStatementCreated

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Approval Statement Aggregate](#agg-approval-statement)

**Description**: 
Published when an approval statement is created for a service profile. Extends permission statement with approver count, amount thresholds, and approver list.

##### Payload

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| statement_id | string |  |  |
| profile_id | value_object |  | vo_profile_id_base |
| subject | string | User or user group ID |  |
| action | string | Action URN requiring approval |  |
| resource | string | Resource identifier |  |
| approver_count | integer | Number of required approvals (parallel) |  |
| approvers | array | List of approver user/user group IDs |  |
| amount_threshold | decimal | Optional amount threshold for approval requirement |  |
| created_at | timestamp |  |  |
| created_by | string |  |  |

#### Evt Approval Statement Updated

<a id="evt-approval-statement-updated"></a>
**ID**: `evt_approval_statement_updated` (Approval Statement Updated Domain Event)

**Name**: ApprovalStatementUpdated

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Approval Statement Aggregate](#agg-approval-statement)

**Description**: 
Published when an approval statement is updated. May include changes to approver count, approvers, or thresholds.

##### Payload

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| statement_id | string |  |  |
| profile_id | value_object |  | vo_profile_id_base |
| updated_fields | object | Map of field name to new value |  |
| updated_at | timestamp |  |  |
| updated_by | string |  |  |

#### Evt Approval Statement Deleted

<a id="evt-approval-statement-deleted"></a>
**ID**: `evt_approval_statement_deleted` (Approval Statement Deleted Domain Event)

**Name**: ApprovalStatementDeleted

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Approval Statement Aggregate](#agg-approval-statement)

**Description**: 
Published when an approval statement is deleted. Consumers should remove approval requirements.

##### Payload

| Name | Type | Value Object Ref |
| --- | --- | --- |
| statement_id | string |  |
| profile_id | value_object | vo_profile_id_base |
| deleted_at | timestamp |  |
| deleted_by | string |  |

#### Evt Approval Workflow Started

<a id="evt-approval-workflow-started"></a>
**ID**: `evt_approval_workflow_started` (Approval Workflow Started Domain Event)

**Name**: ApprovalWorkflowStarted

**Bounded Context Ref**: [Approval Engine Bounded Context](#bc-approval-engine)

**Description**: 
Published when an approval workflow is initiated. Contains workflow ID, approval statement reference, requester, resource being approved, and approver list.

##### Payload

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| workflow_id | string |  |  |
| statement_id | string | Reference to approval statement |  |
| profile_id | value_object |  | vo_profile_id_base |
| requester_id | string | User initiating the action requiring approval |  |
| action | string | Action URN requiring approval |  |
| resource | string | Resource being approved |  |
| amount | decimal | Optional amount for threshold-based approval |  |
| required_approvals | integer | Number of approvals required |  |
| approvers | array | List of eligible approvers |  |
| started_at | timestamp |  |  |

#### Evt Approval Completed

<a id="evt-approval-completed"></a>
**ID**: `evt_approval_completed` (Approval Completed Domain Event)

**Name**: ApprovalCompleted

**Bounded Context Ref**: [Approval Engine Bounded Context](#bc-approval-engine)

**Description**: 
Published when an approval workflow reaches terminal state (approved, rejected, or expired). Contains final workflow state and outcome.

##### Payload

| Name | Type | Description | Value Object Ref |
| --- | --- | --- | --- |
| workflow_id | string |  |  |
| statement_id | string |  |  |
| profile_id | value_object |  | vo_profile_id_base |
| outcome | string | APPROVED, REJECTED, or EXPIRED |  |
| approvals_received | array | List of approver IDs who approved |  |
| rejection_reason | string | Optional reason if rejected |  |
| completed_at | timestamp |  |  |


---

<a id="entities"></a>
## Entities

#### Ent Servicing Profile

<a id="ent-servicing-profile"></a>
**ID**: `ent_servicing_profile` (Servicing Profile Entity)

**Name**: ServicingProfile

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Aggregate Ref**: [Servicing Profile Aggregate](#agg-servicing-profile)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: profile_id

**Identity Generation**: derived

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| profile_id | value_object | Unique profile identifier (clientId) | ✓ | vo_servicing_profile_id |
| client_id | value_object | SRF or GID client reference | ✓ | vo_client_id |
| status | string | Profile status (PENDING, ACTIVE, SUSPENDED, CLOSED) | ✓ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |
| created_by | string | User who created profile | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| enrollService | Enroll a stand-alone service to this profile | *list* | *list* | ServiceEnrollment entity |
| enrollAccount | Enroll account to a service | *list* | *list* | AccountEnrollment entity |
| suspend | Suspend profile (bank or admin action) | *list* | *list* | void |

##### Invariants

- profile_id must be unique
- client_id cannot change after creation
- At least one service must be enrolled for ACTIVE status

##### Lifecycle States

- PENDING
- ACTIVE
- SUSPENDED
- CLOSED

#### Ent Online Profile

<a id="ent-online-profile"></a>
**ID**: `ent_online_profile` (Online Profile Entity)

**Name**: OnlineProfile

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Aggregate Ref**: [Online Profile Aggregate](#agg-online-profile)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: profile_id

**Identity Generation**: derived

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| profile_id | value_object | Unique profile identifier (clientId + sequence) | ✓ | vo_online_profile_id |
| client_id | value_object | SRF primary client reference | ✓ | vo_client_id |
| site_id | string | Express site-id for user synchronization | ✓ |  |
| status | string | Profile status | ✓ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |
| created_by | string |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| enrollService | Enroll online service (Receivables, Interac Send) | *list* | *list* | ServiceEnrollment entity |
| onboardIndirectClient | Onboard indirect client (payor) for Receivables | *list* | *list* | IndirectClient entity reference |
| enrollAccount | Enroll GSAN or other account to service | *list* | *list* | AccountEnrollment entity |

##### Invariants

- profile_id must be unique
- client_id cannot change after creation
- site_id cannot change after creation
- sequence must be unique within client

##### Lifecycle States

- PENDING
- ACTIVE
- SUSPENDED
- CLOSED

#### Ent Indirect Profile

<a id="ent-indirect-profile"></a>
**ID**: `ent_indirect_profile` (Indirect Profile Entity)

**Name**: IndirectProfile

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Aggregate Ref**: [Indirect Profile Aggregate](#agg-indirect-profile)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: profile_id

**Identity Generation**: derived

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| profile_id | value_object | Unique profile identifier (clientId + indirectClientId) | ✓ | vo_indirect_profile_id |
| indirect_client_id | value_object | IND indirect client reference | ✓ | vo_indirect_client_id |
| parent_client_id | value_object | Direct client (SRF) reference | ✓ | vo_client_id |
| status | string |  | ✓ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| enrollService | Enroll indirect service (Receivable-Approval) | *list* | *list* | ServiceEnrollment entity |
| linkPaymentAccount | Link Canadian bank account for invoice payments | *list* | *list* | AccountEnrollment entity |

##### Invariants

- profile_id must be unique
- indirect_client_id cannot change after creation
- parent_client_id cannot change after creation

##### Lifecycle States

- PENDING
- ACTIVE
- SUSPENDED
- CLOSED

#### Ent Service Enrollment

<a id="ent-service-enrollment"></a>
**ID**: `ent_service_enrollment` (Service Enrollment Entity)

**Name**: ServiceEnrollment

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Is Aggregate Root**: ✗ No

**Identity Field**: enrollment_id

**Identity Generation**: auto_generated

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| enrollment_id | string | Unique enrollment identifier (UUID) | ✓ |  |
| profile_id | value_object | Profile owning this enrollment | ✓ | vo_profile_id_base |
| service_type | string | BTR, RECEIVABLES, INTERAC_SEND, RECEIVABLE_APPROVAL, etc. | ✓ |  |
| configuration | object | Service-specific configuration | ✗ |  |
| status | string | ACTIVE, SUSPENDED, CANCELLED | ✓ |  |
| enrolled_at | timestamp |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| updateConfiguration | Update service configuration | *list* | *list* | void |
| suspend | Suspend service enrollment | *list* | *list* | void |

##### Lifecycle States

- ACTIVE
- SUSPENDED
- CANCELLED

#### Ent Account Enrollment

<a id="ent-account-enrollment"></a>
**ID**: `ent_account_enrollment` (Account Enrollment Entity)

**Name**: AccountEnrollment

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Is Aggregate Root**: ✗ No

**Identity Field**: enrollment_id

**Identity Generation**: auto_generated

##### Attributes

| Name | Type | Description | Required |
| --- | --- | --- | --- |
| enrollment_id | string | Unique enrollment identifier (UUID) | ✓ |
| service_enrollment_id | string | Service this account is enrolled to | ✓ |
| account_id | string | Account identifier from SRF | ✓ |
| account_type | string | DDA, FCA, OLB, GSAN, etc. | ✓ |
| status | string | ACTIVE, SUSPENDED, CLOSED | ✓ |
| enrolled_at | timestamp |  | ✓ |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| suspend | Suspend account from service | *list* | *list* | void |

##### Lifecycle States

- ACTIVE
- SUSPENDED
- CLOSED

#### Ent Permission Statement

<a id="ent-permission-statement"></a>
**ID**: `ent_permission_statement` (Permission Statement Entity)

**Name**: PermissionStatement

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Permission Statement Aggregate](#agg-permission-statement)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: statement_id

**Identity Generation**: auto_generated

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| statement_id | string | Unique statement identifier (UUID) | ✓ |  |
| profile_id | value_object | Profile owning this permission (policy owner) | ✓ | vo_profile_id_base |
| subject | string | User ID or user group ID from bc_users | ✓ |  |
| action | string | Action URN (e.g., receivables:invoice:create) | ✓ |  |
| resource | string | Resource identifier (e.g., account:*, service:receivables) | ✓ |  |
| effect | string | ALLOW or DENY | ✓ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| updateEffect | Change effect from ALLOW to DENY or vice versa | *list* | *list* | void |
| updateResource | Update resource scope | *list* | *list* | void |

##### Invariants

- statement_id must be unique
- profile_id cannot change after creation
- effect must be ALLOW or DENY
- subject must exist in bc_users

#### Ent Approval Statement

<a id="ent-approval-statement"></a>
**ID**: `ent_approval_statement` (Approval Statement Entity)

**Name**: ApprovalStatement

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Approval Statement Aggregate](#agg-approval-statement)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: statement_id

**Identity Generation**: auto_generated

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| statement_id | string | Unique statement identifier (UUID) | ✓ |  |
| profile_id | value_object | Profile owning this approval policy | ✓ | vo_profile_id_base |
| subject | string | User ID or user group ID initiating action | ✓ |  |
| action | string | Action URN requiring approval | ✓ |  |
| resource | string | Resource identifier | ✓ |  |
| approver_count | integer | Number of required approvals (parallel) | ✓ |  |
| approvers | array | List of approver user/user group IDs | ✓ |  |
| amount_threshold | decimal | Optional amount threshold for approval requirement | ✗ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| updateApprovers | Update list of approvers | *list* | *list* | void |
| updateThreshold | Update amount threshold | *list* | *list* | void |

##### Invariants

- statement_id must be unique
- profile_id cannot change after creation
- approver_count must be >= 1
- All approvers must exist in bc_users
- If amount_threshold specified, must be positive

#### Ent Indirect Client

<a id="ent-indirect-client"></a>
**ID**: `ent_indirect_client` (Indirect Client Entity)

**Name**: IndirectClient

**Bounded Context Ref**: [Indirect Client Management Bounded Context](#bc-indirect-client-management)

**Aggregate Ref**: [Indirect Client Aggregate](#agg-indirect-client)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: indirect_client_id

**Identity Generation**: derived

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| indirect_client_id | value_object | Unique identifier (clientId + sequence) | ✓ | vo_indirect_client_id |
| parent_client_id | value_object | Direct client (SRF) reference | ✓ | vo_client_id |
| type | string | BUSINESS (MVP only - no individuals) | ✓ |  |
| business_name | string | Business legal name | ✓ |  |
| tax_id | string | Business tax identifier | ✗ |  |
| status | string | ACTIVE, SUSPENDED, CLOSED | ✓ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| addRelatedPerson | Add related person (signing officer, administrator, director) | *list* | *list* | RelatedPerson entity |
| updateBusinessInfo | Update business information | *list* | *list* | void |

##### Invariants

- indirect_client_id must be unique
- parent_client_id cannot change after creation
- type must be BUSINESS (MVP)
- Must have at least one related person

##### Lifecycle States

- ACTIVE
- SUSPENDED
- CLOSED

#### Ent Related Person

<a id="ent-related-person"></a>
**ID**: `ent_related_person` (Related Person Entity)

**Name**: RelatedPerson

**Bounded Context Ref**: [Indirect Client Management Bounded Context](#bc-indirect-client-management)

**Is Aggregate Root**: ✗ No

**Identity Field**: person_id

**Identity Generation**: auto_generated

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| person_id | string | Unique person identifier (UUID) | ✓ |  |
| indirect_client_id | value_object | Indirect client this person belongs to | ✓ | vo_indirect_client_id |
| name | string | Person full name | ✓ |  |
| role | string | SIGNING_OFFICER, ADMINISTRATOR, DIRECTOR | ✓ |  |
| email | string | Contact email | ✗ |  |
| phone | string | Contact phone | ✗ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| updateContactInfo | Update email and phone | *list* | *list* | void |

##### Invariants

- role must be SIGNING_OFFICER, ADMINISTRATOR, or DIRECTOR

#### Ent User

<a id="ent-user"></a>
**ID**: `ent_user` (User Entity)

**Name**: User

**Bounded Context Ref**: [Users Bounded Context](#bc-users)

**Aggregate Ref**: [User Aggregate](#agg-user)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: user_id

**Identity Generation**: external

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| user_id | string | Unique user identifier (from Okta or Express) | ✓ |  |
| profile_id | value_object | Profile user belongs to | ✓ | vo_profile_id_base |
| email | string | User email | ✓ |  |
| first_name | string |  | ✓ |  |
| last_name | string |  | ✓ |  |
| role | string | ADMINISTRATOR, REGULAR_USER | ✓ |  |
| source | string | EXPRESS or OKTA | ✓ |  |
| status | string | PENDING, ACTIVE, LOCKED, DEACTIVATED | ✓ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| lock | Lock user (admin or bank action) | *list* | *list* | void |
| unlock | Unlock user (bank can unlock bank locks, admin can unlock admin locks) | *list* | *list* | void |
| updateRole | Change user role (enforce dual admin rule) | *list* | *list* | void |

##### Invariants

- user_id must be unique
- profile_id cannot change after creation
- source must be EXPRESS or OKTA
- If source=EXPRESS, user is read-only in platform
- Profile must have at least 2 ADMINISTRATOR users (dual admin)

##### Lifecycle States

- PENDING
- ACTIVE
- LOCKED
- DEACTIVATED

#### Ent User Group

<a id="ent-user-group"></a>
**ID**: `ent_user_group` (User Group Entity)

**Name**: UserGroup

**Bounded Context Ref**: [Users Bounded Context](#bc-users)

**Aggregate Ref**: [User Group Aggregate](#agg-user-group)

**Is Aggregate Root**: ✓ Yes

**Identity Field**: group_id

**Identity Generation**: auto_generated

##### Attributes

| Name | Type | Description | Required | Value Object Ref |
| --- | --- | --- | --- | --- |
| group_id | string | Unique group identifier (UUID) | ✓ |  |
| profile_id | value_object | Profile group belongs to | ✓ | vo_profile_id_base |
| name | string | Group name | ✓ |  |
| description | string |  | ✗ |  |
| created_at | timestamp |  | ✓ |  |
| updated_at | timestamp |  | ✓ |  |

##### Business Methods

| Name | Description | Parameters | Publishes Events | Returns |
| --- | --- | --- | --- | --- |
| addMember | Add user to group | *list* | *list* | UserGroupMembership entity |
| removeMember | Remove user from group | *list* | *list* | void |

##### Invariants

- group_id must be unique
- profile_id cannot change after creation
- All members must belong to same profile

#### Ent User Group Membership

<a id="ent-user-group-membership"></a>
**ID**: `ent_user_group_membership` (User Group Membership Entity)

**Name**: UserGroupMembership

**Bounded Context Ref**: [Users Bounded Context](#bc-users)

**Is Aggregate Root**: ✗ No

**Identity Field**: membership_id

**Identity Generation**: auto_generated

##### Attributes

| Name | Type | Description | Required |
| --- | --- | --- | --- |
| membership_id | string | Unique membership identifier (UUID) | ✓ |
| group_id | string | User group reference | ✓ |
| user_id | string | User reference | ✓ |
| added_at | timestamp |  | ✓ |

##### Invariants

- User must belong to same profile as group


---

<a id="domain-services"></a>
## Domain Services

#### Svc Dom Profile Validation

<a id="svc-dom-profile-validation"></a>
**ID**: `svc_dom_profile_validation` (Profile Validation Domain Service)

**Name**: ProfileValidationService

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Validates profile creation rules across contexts. Checks client existence via External Data serving layer, validates business rules, ensures site-id linking.

##### Operations

| Name | Description | Business Logic | Parameters | Returns |
| --- | --- | --- | --- | --- |
| validateOnlineProfileCreation | Validate online profile can be created for client and site-id | 1. Validate client exists in SRF via svc_app_client_data 2. Validate site-id exists in Express via svc_app_user_data 3. Validate site-id links to same client 4. Check no duplicate online profile for same client + site-id
 | *list* | ValidationResult {valid: boolean, errors: array} |
| validateAccountEnrollmentEligibility | Validate account can be enrolled to service | 1. Validate account exists via svc_app_account_data 2. Validate account belongs to profile's client 3. Validate account type is compatible with service type 4. Check account is not already enrolled to service
 | *list* | ValidationResult |

##### Dependencies

| Type | Ref |
| --- | --- |
| domain_service | svc_app_client_data |
| domain_service | svc_app_account_data |
| domain_service | svc_app_user_data |

**Stateless**: ✓ Yes

#### Svc Dom Sequence Generator

<a id="svc-dom-sequence-generator"></a>
**ID**: `svc_dom_sequence_generator` (Sequence Generator Domain Service)

**Name**: SequenceGeneratorService

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Description**: 
Generates unique sequence numbers within parent scope (e.g., online profile sequence within client, indirect client sequence within client).

##### Operations

| Name | Description | Business Logic | Parameters | Returns |
| --- | --- | --- | --- | --- |
| generateOnlineProfileSequence | Generate next sequence number for online profile within client | 1. Query repo_online_profile for max sequence for client 2. Return max + 1 (or 1 if none exist)
 | *list* | integer (next sequence) |
| generateIndirectClientSequence | Generate next sequence number for indirect client within direct client | 1. Query repo_indirect_client for max sequence for client 2. Return max + 1 (or 1 if none exist)
 | *list* | integer (next sequence) |

##### Dependencies

| Type | Ref |
| --- | --- |
| repository | repo_online_profile |
| repository | repo_indirect_client |

**Stateless**: ✓ Yes

#### Svc Dom Policy Evaluator

<a id="svc-dom-policy-evaluator"></a>
**ID**: `svc_dom_policy_evaluator` (Policy Evaluator Domain Service)

**Name**: PolicyEvaluatorService

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Description**: 
Evaluates permission and approval policies for authorization decisions. Determines if user has permission and if approval is required.

##### Operations

| Name | Description | Business Logic | Parameters | Returns |
| --- | --- | --- | --- | --- |
| evaluatePermission | Check if subject has permission for action on resource | 1. Query permission statements for profile 2. Filter by subject (user or user groups containing user) 3. Match action and resource patterns 4. Evaluate DENY first (explicit deny overrides allow) 5. Return allowed=true if any ALLOW matches
 | *list* | PermissionResult {allowed: boolean, statements: array} |
| evaluateApprovalRequirement | Determine if approval is required and who can approve | 1. Query approval statements for profile 2. Filter by action and resource 3. Check amount against thresholds 4. Return approval requirement details
 | *list* | ApprovalRequirement {required: boolean, approver_count: int, approvers: array} |

##### Dependencies

| Type | Ref |
| --- | --- |
| repository | repo_permission_statement |
| repository | repo_approval_statement |

**Stateless**: ✓ Yes


---

<a id="application-services-expanded"></a>
## Application Services Expanded

#### Svc App Servicing Profile Expanded

<a id="svc-app-servicing-profile"></a>
**ID**: `svc_app_servicing_profile` (Servicing Profile Application Service)

**Name**: ServicingProfileApplicationService

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Use Case**: Manage servicing profiles and service enrollment

**Description**: 
Orchestrates servicing profile lifecycle: create, add/modify services, enroll accounts, suspend. Coordinates with validation service, repositories, and event publishing.

##### Methods

| Name | Description | Parameters | Publishes Events | Returns | Transaction | Workflow |
| --- | --- | --- | --- | --- | --- | --- |
| createServicingProfile | Create new servicing profile for SRF/GID client | *list* | *list* | agg_servicing_profile | ✓ | *list* |
| enrollService | Enroll stand-alone service (BTR, ACH Debit Block, etc.) | *list* | *list* | ent_service_enrollment | ✓ | *list* |
| enrollAccount | Enroll account to a service | *list* | *list* | ent_account_enrollment | ✓ | *list* |

##### Orchestrates

| Type | Ref |
| --- | --- |
| aggregate | agg_servicing_profile |
| domain_service | svc_dom_profile_validation |
| repository | repo_servicing_profile |

**Transaction Boundary**: ✓ Yes

#### Svc App Online Profile Expanded

<a id="svc-app-online-profile"></a>
**ID**: `svc_app_online_profile` (Online Profile Application Service)

**Name**: OnlineProfileApplicationService

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Use Case**: Manage online profiles and indirect client onboarding

**Description**: 
Orchestrates online profile lifecycle: create with Express linking, enroll online services, onboard indirect clients, manage accounts. Bank-managed via employee portal (MVP).

##### Methods

| Name | Description | Parameters | Publishes Events | Returns | Transaction | Workflow |
| --- | --- | --- | --- | --- | --- | --- |
| createOnlineProfile | Create new online profile with primary client and Express site-id link | *list* | *list* | agg_online_profile | ✓ | *list* |
| enrollService | Enroll online service (Receivables, Interac Send) | *list* | *list* | ent_service_enrollment | ✓ | *list* |
| onboardIndirectClient | Onboard indirect client (payor) for Receivables service | *list* | *list* | ent_indirect_client | ✓ | *list* |

##### Orchestrates

| Type | Ref |
| --- | --- |
| aggregate | agg_online_profile |
| aggregate | agg_indirect_client |
| domain_service | svc_dom_profile_validation |
| domain_service | svc_dom_sequence_generator |
| repository | repo_online_profile |
| repository | repo_indirect_client |

**Transaction Boundary**: ✓ Yes

#### Svc App Indirect Profile Expanded

<a id="svc-app-indirect-profile"></a>
**ID**: `svc_app_indirect_profile` (Indirect Profile Application Service)

**Name**: IndirectProfileApplicationService

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Use Case**: Manage indirect profiles and self-service policy configuration

**Description**: 
Orchestrates indirect profile lifecycle: create, enroll indirect services, link payment accounts, manage self-service permission/approval policies.

##### Methods

| Name | Description | Parameters | Publishes Events | Returns | Transaction | Workflow |
| --- | --- | --- | --- | --- | --- | --- |
| createIndirectProfile | Create indirect profile for indirect client | *list* | *list* | agg_indirect_profile | ✓ | *list* |
| enrollService | Enroll indirect service (Receivable-Approval) | *list* | *list* | ent_service_enrollment | ✓ | *list* |

##### Orchestrates

| Type | Ref |
| --- | --- |
| aggregate | agg_indirect_profile |
| repository | repo_indirect_profile |
| repository | repo_indirect_client |

**Transaction Boundary**: ✓ Yes


---

<a id="repositories"></a>
## Repositories

#### Repo Servicing Profile

<a id="repo-servicing-profile"></a>
**ID**: `repo_servicing_profile` (Servicing Profile Repository)

**Name**: ServicingProfileRepository

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Aggregate Ref**: [Servicing Profile Aggregate](#agg-servicing-profile)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated servicing profile | *list* |  | void |
| findById | Find servicing profile by ID | *list* | by_id | ServicingProfile (nullable) |
| findByClientId | Find servicing profile for client (should be one only) | *list* | by_criteria | ServicingProfile (nullable) |
| delete | Delete profile (soft delete - set status=CLOSED) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Single table for servicing profile with foreign key to service_enrollments and account_enrollments. Profile ID is derived from client_id (no sequence).

#### Repo Online Profile

<a id="repo-online-profile"></a>
**ID**: `repo_online_profile` (Online Profile Repository)

**Name**: OnlineProfileRepository

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Aggregate Ref**: [Online Profile Aggregate](#agg-online-profile)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated online profile | *list* |  | void |
| findById | Find online profile by ID | *list* | by_id | OnlineProfile (nullable) |
| findByClientId | Find all online profiles for client (paginated) | *list* | by_criteria | Page<OnlineProfile> |
| findBySiteId | Find online profile by Express site-id | *list* | by_criteria | OnlineProfile (nullable) |
| findMaxSequenceForClient | Find max sequence number for client (for sequence generation) | *list* | custom | integer (nullable) |
| delete | Delete profile (soft delete - set status=CLOSED) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Single table for online profile with composite key (client_id, sequence). Foreign keys to service_enrollments and account_enrollments. Index on site_id for Express linking.

#### Repo Indirect Profile

<a id="repo-indirect-profile"></a>
**ID**: `repo_indirect_profile` (Indirect Profile Repository)

**Name**: IndirectProfileRepository

**Bounded Context Ref**: [Service Profile Management Bounded Context](#bc-service-profile-management)

**Aggregate Ref**: [Indirect Profile Aggregate](#agg-indirect-profile)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated indirect profile | *list* |  | void |
| findById | Find indirect profile by ID | *list* | by_id | IndirectProfile (nullable) |
| findByIndirectClientId | Find indirect profile for indirect client | *list* | by_criteria | IndirectProfile (nullable) |
| findByParentClientId | Find all indirect profiles for direct client (paginated) | *list* | by_criteria | Page<IndirectProfile> |
| delete | Delete profile (soft delete - set status=CLOSED) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Single table for indirect profile with composite key (parent_client_id, indirect_client_id).

#### Repo Permission Statement

<a id="repo-permission-statement"></a>
**ID**: `repo_permission_statement` (Permission Statement Repository)

**Name**: PermissionStatementRepository

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Permission Statement Aggregate](#agg-permission-statement)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated permission statement | *list* |  | void |
| findById | Find permission statement by ID | *list* | by_id | PermissionStatement (nullable) |
| findByProfileId | Find all permission statements for profile | *list* | by_criteria | List<PermissionStatement> |
| findByProfileAndSubject | Find permission statements for profile and subject (user or group) | *list* | by_criteria | List<PermissionStatement> |
| delete | Delete permission statement (hard delete) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Table with indexes on profile_id and subject for policy evaluation queries.

#### Repo Approval Statement

<a id="repo-approval-statement"></a>
**ID**: `repo_approval_statement` (Approval Statement Repository)

**Name**: ApprovalStatementRepository

**Bounded Context Ref**: [Policy Bounded Context](#bc-policy)

**Aggregate Ref**: [Approval Statement Aggregate](#agg-approval-statement)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated approval statement | *list* |  | void |
| findById | Find approval statement by ID | *list* | by_id | ApprovalStatement (nullable) |
| findByProfileId | Find all approval statements for profile | *list* | by_criteria | List<ApprovalStatement> |
| findByProfileAndAction | Find approval statements for profile and action | *list* | by_criteria | List<ApprovalStatement> |
| delete | Delete approval statement (hard delete) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Table with indexes on profile_id and action for approval requirement queries.

#### Repo Indirect Client

<a id="repo-indirect-client"></a>
**ID**: `repo_indirect_client` (Indirect Client Repository)

**Name**: IndirectClientRepository

**Bounded Context Ref**: [Indirect Client Management Bounded Context](#bc-indirect-client-management)

**Aggregate Ref**: [Indirect Client Aggregate](#agg-indirect-client)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated indirect client | *list* |  | void |
| findById | Find indirect client by ID | *list* | by_id | IndirectClient (nullable) |
| findByParentClientId | Find all indirect clients for direct client (paginated) | *list* | by_criteria | Page<IndirectClient> |
| findMaxSequenceForClient | Find max sequence number for client (for sequence generation) | *list* | custom | integer (nullable) |
| delete | Delete indirect client (soft delete - set status=CLOSED) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Single table for indirect client with composite key (parent_client_id, sequence). Foreign key to related_persons table.

#### Repo User

<a id="repo-user"></a>
**ID**: `repo_user` (User Repository)

**Name**: UserRepository

**Bounded Context Ref**: [Users Bounded Context](#bc-users)

**Aggregate Ref**: [User Aggregate](#agg-user)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated user | *list* |  | void |
| findById | Find user by ID | *list* | by_id | User (nullable) |
| findByProfileId | Find all users for profile (paginated) | *list* | by_criteria | Page<User> |
| findByEmail | Find user by email | *list* | by_criteria | User (nullable) |
| findAdministratorsByProfileId | Find all administrators for profile (for dual admin check) | *list* | by_criteria | List<User> |
| delete | Delete user (soft delete - set status=DEACTIVATED) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Table with indexes on profile_id and email for user queries.

#### Repo User Group

<a id="repo-user-group"></a>
**ID**: `repo_user_group` (User Group Repository)

**Name**: UserGroupRepository

**Bounded Context Ref**: [Users Bounded Context](#bc-users)

**Aggregate Ref**: [User Group Aggregate](#agg-user-group)

##### Interface Methods

| Name | Description | Parameters | Query Type | Returns |
| --- | --- | --- | --- | --- |
| save | Persist new or updated user group | *list* |  | void |
| findById | Find user group by ID | *list* | by_id | UserGroup (nullable) |
| findByProfileId | Find all user groups for profile (paginated) | *list* | by_criteria | Page<UserGroup> |
| findByUserId | Find all groups user belongs to | *list* | by_criteria | List<UserGroup> |
| delete | Delete user group (hard delete) | *list* |  | void |

**Persistence Strategy**: SQL (PostgreSQL)

**Implementation Notes**: 
Table for user groups with join table for user_group_memberships.


---

<a id="metadata"></a>
## Metadata

**Phase**: phase_4_tactical

**Last Updated**: 2025-10-15

#### Key Changes

- Merged Client Management + Service Management into Service Profiles domain
- Service profiles for clients, not client management itself
- Indirect clients part of Service Profiles (exist only for service profiles)
- Created Data Engineering domain with gold copy and serving layer
- Receivable-approval is separate business service (not part of Receivables)
- Serving layer provides read-only account data to Service Profile domain


---

