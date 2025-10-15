# Platform Data Eng

*Generated from: platform-data-eng.yaml*

---

# Reference Index

Quick navigation to all identified objects:

### Systems

- [Cash Mgmt Platform System](#sys-cash-mgmt-platform) - Commercial Banking Cash Management Platform - Data Engineering

### Domains

- [Account Data Domain](#dom-account-data) - Account Data Domain
- [Reference Data Domain](#dom-reference-data) - Reference Data Domain
- [User Data Domain](#dom-user-data) - User Data Domain

### Pipelines

- [Account Status Change Pipeline](#pip-account-status-change) - Account Status Change Detection
- [Client Reference Pipeline](#pip-client-reference) - Client Reference Data
- [Express User Sync Pipeline](#pip-express-user-sync) - Express User Synchronization
- [Srf Account Sync Pipeline](#pip-srf-account-sync) - SRF Account Synchronization

### Stages

- [Account Load Stage](#stg-account-load) - Load Account Gold Copy
- [Account Transform Stage](#stg-account-transform) - Transform Account Data
- [Change Detection Stage](#stg-change-detection) - Detect Account Changes
- [Client Extract Stage](#stg-client-extract) - Extract Client Data
- [Client Load Stage](#stg-client-load) - Load Client Gold Copy
- [Client Transform Stage](#stg-client-transform) - Transform Client Data
- [Compare Snapshots Stage](#stg-compare-snapshots) - Compare Account Snapshots
- [Express Consume Stage](#stg-express-consume) - Consume Express User Events
- [Identify Changes Stage](#stg-identify-changes) - Identify New/Closed Accounts
- [Notify Enrollment Stage](#stg-notify-enrollment) - Notify Auto-Enrollment
- [Srf Extract Stage](#stg-srf-extract) - Extract SRF Account Data
- [User Persist Stage](#stg-user-persist) - Persist User Gold Copy
- [User Transform Stage](#stg-user-transform) - Transform User Data

### Datasets

- [Account Changes Dataset](#ds-account-changes) - Account Change Events
- [Account Clean Dataset](#ds-account-clean) - Clean Account Data
- [Account Events Dataset](#ds-account-events) - Account Events for Publishing
- [Account Gold Dataset](#ds-account-gold) - Account Gold Copy
- [Client Clean Dataset](#ds-client-clean) - Clean Client Data
- [Client Gold Dataset](#ds-client-gold) - Client Gold Copy
- [Express User Events Dataset](#ds-express-user-events) - Express User Event Stream
- [Srf Raw Accounts Dataset](#ds-srf-raw-accounts) - SRF Raw Account Data
- [User Clean Dataset](#ds-user-clean) - Clean User Data
- [User Gold Dataset](#ds-user-gold) - User Gold Copy

### Contracts

- [Account Events Contract](#ctr-account-events) - Account Change Events Contract
- [Account Gold Contract](#ctr-account-gold) - Account Gold Copy Contract
- [User Gold Contract](#ctr-user-gold) - User Gold Copy Contract

### Data Products

- [Account Serving Data Product](#dp-account-serving) - Account Data Serving Product

### Other

- [sch-account-status-change](#sch-account-status-change)
- [sch-client-reference](#sch-client-reference)
- [sch-daily-srf-sync](#sch-daily-srf-sync)
- [sch-express-user-sync](#sch-express-user-sync)
- [trx-clean-accounts](#trx-clean-accounts)
- [trx-consume-events](#trx-consume-events)
- [trx-map-users](#trx-map-users)
- [trx-merge-accounts](#trx-merge-accounts)
- [trx-publish-events](#trx-publish-events)
- [trx-srf-extract](#trx-srf-extract)
- [trx-validate-accounts](#trx-validate-accounts)

---

# Table of Contents

- [System](#system)
- [Domains](#domains)
- [Pipelines](#pipelines)
- [Stages](#stages)
- [Datasets](#datasets)
- [Contracts](#contracts)
- [Data Products](#data-products)
- [Metadata](#metadata)

---

<a id="system"></a>
## System

<a id="sys-cash-mgmt-platform"></a>
**ID**: `sys-cash-mgmt-platform` (Cash Mgmt Platform System)

**Name**: Commercial Banking Cash Management Platform - Data Engineering

**Description**: Data engineering model for cash management platform focusing on gold copy creation and serving layer for Service Profile domain

#### Domains

- dom-account-data
- dom-user-data
- dom-reference-data


---

<a id="domains"></a>
## Domains

| Id | Name | Description | Pipelines |
| --- | --- | --- | --- |
| [Account Data Domain](#dom-account-data) | Account Data Domain | Gold copy of account data from SRF and other account systems | *list* |
| [User Data Domain](#dom-user-data) | User Data Domain | User data from Express platform (direct clients) and Okta (indirect clients) | *list* |
| [Reference Data Domain](#dom-reference-data) | Reference Data Domain | Reference data for client demographics, identity mapping | *list* |


---

<a id="pipelines"></a>
## Pipelines

| Id | Name | Description | Mode | Schedule | Stages |
| --- | --- | --- | --- | --- | --- |
| [Srf Account Sync Pipeline](#pip-srf-account-sync) | SRF Account Synchronization | Daily batch sync of client-owned accounts from SRF | batch | *dict* | *list* |
| [Account Status Change Pipeline](#pip-account-status-change) | Account Status Change Detection | Detect new and closed accounts for auto-enrollment | batch | *dict* | *list* |
| [Express User Sync Pipeline](#pip-express-user-sync) | Express User Synchronization | Near real-time sync of direct client users from Express | streaming | *dict* | *list* |
| [Client Reference Pipeline](#pip-client-reference) | Client Reference Data | Daily sync of client demographics from SRF | batch | *dict* | *list* |


---

<a id="stages"></a>
## Stages

| Id | Name | Depends On | Inputs | Outputs | Transforms |
| --- | --- | --- | --- | --- | --- |
| [Srf Extract Stage](#stg-srf-extract) | Extract SRF Account Data |  | *list* | *list* | *list* |
| [Account Transform Stage](#stg-account-transform) | Transform Account Data |  | *list* | *list* | *list* |
| [Account Load Stage](#stg-account-load) | Load Account Gold Copy |  | *list* | *list* | *list* |
| [Change Detection Stage](#stg-change-detection) | Detect Account Changes | *list* | *list* | *list* |  |
| [Compare Snapshots Stage](#stg-compare-snapshots) | Compare Account Snapshots |  | *list* | *list* |  |
| [Identify Changes Stage](#stg-identify-changes) | Identify New/Closed Accounts |  | *list* | *list* |  |
| [Notify Enrollment Stage](#stg-notify-enrollment) | Notify Auto-Enrollment |  | *list* | *list* | *list* |
| [Express Consume Stage](#stg-express-consume) | Consume Express User Events |  | *list* | *list* | *list* |
| [User Transform Stage](#stg-user-transform) | Transform User Data |  | *list* | *list* | *list* |
| [User Persist Stage](#stg-user-persist) | Persist User Gold Copy |  | *list* | *list* |  |
| [Client Extract Stage](#stg-client-extract) | Extract Client Data |  | *list* | *list* |  |
| [Client Transform Stage](#stg-client-transform) | Transform Client Data |  | *list* | *list* |  |
| [Client Load Stage](#stg-client-load) | Load Client Gold Copy |  | *list* | *list* |  |


---

<a id="datasets"></a>
## Datasets

| Id | Name | Type | Classification | Contains Pii | Format | Location | Pii Fields |
| --- | --- | --- | --- | --- | --- | --- | --- |
| [Srf Raw Accounts Dataset](#ds-srf-raw-accounts) | SRF Raw Account Data | file | internal | ✗ | csv | s3://data-lake/bronze/srf/accounts/ |  |
| [Express User Events Dataset](#ds-express-user-events) | Express User Event Stream | stream | restricted | ✓ | json | kafka://express-user-events | *list* |
| [Account Clean Dataset](#ds-account-clean) | Clean Account Data | table | internal |  | delta | s3://data-lake/silver/accounts/ |  |
| [User Clean Dataset](#ds-user-clean) | Clean User Data | table | restricted | ✓ | delta | s3://data-lake/silver/users/ |  |
| [Client Clean Dataset](#ds-client-clean) | Clean Client Data | table | internal | ✓ | delta | s3://data-lake/silver/clients/ |  |
| [Account Gold Dataset](#ds-account-gold) | Account Gold Copy | table | internal | ✗ | delta | s3://data-lake/gold/accounts/ |  |
| [User Gold Dataset](#ds-user-gold) | User Gold Copy | table | restricted | ✓ | delta | s3://data-lake/gold/users/ | *list* |
| [Client Gold Dataset](#ds-client-gold) | Client Gold Copy | table | internal | ✓ | delta | s3://data-lake/gold/clients/ | *list* |
| [Account Changes Dataset](#ds-account-changes) | Account Change Events | table | internal |  | delta | s3://data-lake/events/account-changes/ |  |
| [Account Events Dataset](#ds-account-events) | Account Events for Publishing | stream | internal |  | json | kafka://account-events |  |


---

<a id="contracts"></a>
## Contracts

| Id | Name | Description | Consumers | Dataset | Evolution Policy | Sla | Version |
| --- | --- | --- | --- | --- | --- | --- | --- |
| [Account Gold Contract](#ctr-account-gold) | Account Gold Copy Contract | Contract for Service Profile domain to consume account data | *list* | ds-account-gold | backward-compatible | *dict* | 1.0.0 |
| [User Gold Contract](#ctr-user-gold) | User Gold Copy Contract | Contract for direct client user data | *list* | ds-user-gold | backward-compatible | *dict* | 1.0.0 |
| [Account Events Contract](#ctr-account-events) | Account Change Events Contract | Events for new/closed accounts | *list* | ds-account-events | forward-compatible | *dict* | 1.0.0 |


---

<a id="data-products"></a>
## Data Products

| Id | Name | Description | Access Patterns | Bounded Context Ref | Contracts | Datasets | Owner |
| --- | --- | --- | --- | --- | --- | --- | --- |
| [Account Serving Data Product](#dp-account-serving) | Account Data Serving Product | Read-only serving layer for Service Profile domain | *list* | ddd:BoundedContext:bc_account_data_serving | *list* | *list* | *dict* |


---

<a id="metadata"></a>
## Metadata

**Phase**: phase_2_strategic

**Last Updated**: 2025-10-15

#### Alignment

**Ddd Model**: platform-ddd.yaml

##### Key Mappings

| Data Domains | Data Products | Ddd Context | Ddd Domain | Pipelines |
| --- | --- | --- | --- | --- |
| *list* |  |  | dom_data_engineering |  |
|  |  | bc_account_data_sync |  | *list* |
|  | *list* | bc_account_data_serving |  |  |


---

