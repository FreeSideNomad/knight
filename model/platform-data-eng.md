# Platform Data Eng

*Generated from: platform-data-eng.yaml*

---

# Reference Index

Quick navigation to all identified objects:

### CTR

- [ctr-account-events](#ctr-account-events) - Account Change Events Contract
- [ctr-account-gold](#ctr-account-gold) - Account Gold Copy Contract
- [ctr-user-gold](#ctr-user-gold) - User Gold Copy Contract

### DOM

- [dom-account-data](#dom-account-data) - Account Data Domain
- [dom-reference-data](#dom-reference-data) - Reference Data Domain
- [dom-user-data](#dom-user-data) - User Data Domain

### DP

- [dp-account-serving](#dp-account-serving) - Account Data Serving Product

### DS

- [ds-account-changes](#ds-account-changes) - Account Change Events
- [ds-account-clean](#ds-account-clean) - Clean Account Data
- [ds-account-events](#ds-account-events) - Account Events for Publishing
- [ds-account-gold](#ds-account-gold) - Account Gold Copy
- [ds-client-clean](#ds-client-clean) - Clean Client Data
- [ds-client-gold](#ds-client-gold) - Client Gold Copy
- [ds-express-user-events](#ds-express-user-events) - Express User Event Stream
- [ds-srf-raw-accounts](#ds-srf-raw-accounts) - SRF Raw Account Data
- [ds-user-clean](#ds-user-clean) - Clean User Data
- [ds-user-gold](#ds-user-gold) - User Gold Copy

### PIP

- [pip-account-status-change](#pip-account-status-change) - Account Status Change Detection
- [pip-client-reference](#pip-client-reference) - Client Reference Data
- [pip-express-user-sync](#pip-express-user-sync) - Express User Synchronization
- [pip-srf-account-sync](#pip-srf-account-sync) - SRF Account Synchronization

### STG

- [stg-account-load](#stg-account-load) - Load Account Gold Copy
- [stg-account-transform](#stg-account-transform) - Transform Account Data
- [stg-change-detection](#stg-change-detection) - Detect Account Changes
- [stg-client-extract](#stg-client-extract) - Extract Client Data
- [stg-client-load](#stg-client-load) - Load Client Gold Copy
- [stg-client-transform](#stg-client-transform) - Transform Client Data
- [stg-compare-snapshots](#stg-compare-snapshots) - Compare Account Snapshots
- [stg-express-consume](#stg-express-consume) - Consume Express User Events
- [stg-identify-changes](#stg-identify-changes) - Identify New/Closed Accounts
- [stg-notify-enrollment](#stg-notify-enrollment) - Notify Auto-Enrollment
- [stg-srf-extract](#stg-srf-extract) - Extract SRF Account Data
- [stg-user-persist](#stg-user-persist) - Persist User Gold Copy
- [stg-user-transform](#stg-user-transform) - Transform User Data

### SYS

- [sys-cash-mgmt-platform](#sys-cash-mgmt-platform) - Commercial Banking Cash Management Platform - Data Engineering

### TRX

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
**ID**: `sys-cash-mgmt-platform`

**Name**: Commercial Banking Cash Management Platform - Data Engineering

**Description**: Data engineering model for cash management platform focusing on gold copy creation and serving layer for Service Profile domain

#### Domains

- dom-account-data
- dom-user-data
- dom-reference-data


---

<a id="domains"></a>
## Domains

| id | name | description | pipelines |
| --- | --- | --- | --- |
| [dom-account-data](#dom-account-data) | Account Data Domain | Gold copy of account data from SRF and other ac... | *list* |
| [dom-user-data](#dom-user-data) | User Data Domain | User data from Express platform (direct clients... | *list* |
| [dom-reference-data](#dom-reference-data) | Reference Data Domain | Reference data for client demographics, identit... | *list* |


---

<a id="pipelines"></a>
## Pipelines

| id | name | description | mode | schedule | stages |
| --- | --- | --- | --- | --- | --- |
| [pip-srf-account-sync](#pip-srf-account-sync) | SRF Account Synchronization | Daily batch sync of client-owned accounts from SRF | batch | *dict* | *list* |
| [pip-account-status-change](#pip-account-status-change) | Account Status Change Detection | Detect new and closed accounts for auto-enrollment | batch | *dict* | *list* |
| [pip-express-user-sync](#pip-express-user-sync) | Express User Synchronization | Near real-time sync of direct client users from... | streaming | *dict* | *list* |
| [pip-client-reference](#pip-client-reference) | Client Reference Data | Daily sync of client demographics from SRF | batch | *dict* | *list* |


---

<a id="stages"></a>
## Stages

| id | name | depends_on | inputs | outputs | transforms |
| --- | --- | --- | --- | --- | --- |
| [stg-srf-extract](#stg-srf-extract) | Extract SRF Account Data |  | *list* | *list* | *list* |
| [stg-account-transform](#stg-account-transform) | Transform Account Data |  | *list* | *list* | *list* |
| [stg-account-load](#stg-account-load) | Load Account Gold Copy |  | *list* | *list* | *list* |
| [stg-change-detection](#stg-change-detection) | Detect Account Changes | *list* | *list* | *list* |  |
| [stg-compare-snapshots](#stg-compare-snapshots) | Compare Account Snapshots |  | *list* | *list* |  |
| [stg-identify-changes](#stg-identify-changes) | Identify New/Closed Accounts |  | *list* | *list* |  |
| [stg-notify-enrollment](#stg-notify-enrollment) | Notify Auto-Enrollment |  | *list* | *list* | *list* |
| [stg-express-consume](#stg-express-consume) | Consume Express User Events |  | *list* | *list* | *list* |
| [stg-user-transform](#stg-user-transform) | Transform User Data |  | *list* | *list* | *list* |
| [stg-user-persist](#stg-user-persist) | Persist User Gold Copy |  | *list* | *list* |  |
| [stg-client-extract](#stg-client-extract) | Extract Client Data |  | *list* | *list* |  |
| [stg-client-transform](#stg-client-transform) | Transform Client Data |  | *list* | *list* |  |
| [stg-client-load](#stg-client-load) | Load Client Gold Copy |  | *list* | *list* |  |


---

<a id="datasets"></a>
## Datasets

| id | name | type | classification | contains_pii | format | location | pii_fields |
| --- | --- | --- | --- | --- | --- | --- | --- |
| [ds-srf-raw-accounts](#ds-srf-raw-accounts) | SRF Raw Account Data | file | internal | ✗ | csv | s3://data-lake/bronze/srf/accounts/ |  |
| [ds-express-user-events](#ds-express-user-events) | Express User Event Stream | stream | restricted | ✓ | json | kafka://express-user-events | *list* |
| [ds-account-clean](#ds-account-clean) | Clean Account Data | table | internal |  | delta | s3://data-lake/silver/accounts/ |  |
| [ds-user-clean](#ds-user-clean) | Clean User Data | table | restricted | ✓ | delta | s3://data-lake/silver/users/ |  |
| [ds-client-clean](#ds-client-clean) | Clean Client Data | table | internal | ✓ | delta | s3://data-lake/silver/clients/ |  |
| [ds-account-gold](#ds-account-gold) | Account Gold Copy | table | internal | ✗ | delta | s3://data-lake/gold/accounts/ |  |
| [ds-user-gold](#ds-user-gold) | User Gold Copy | table | restricted | ✓ | delta | s3://data-lake/gold/users/ | *list* |
| [ds-client-gold](#ds-client-gold) | Client Gold Copy | table | internal | ✓ | delta | s3://data-lake/gold/clients/ | *list* |
| [ds-account-changes](#ds-account-changes) | Account Change Events | table | internal |  | delta | s3://data-lake/events/account-changes/ |  |
| [ds-account-events](#ds-account-events) | Account Events for Publishing | stream | internal |  | json | kafka://account-events |  |


---

<a id="contracts"></a>
## Contracts

| id | name | description | consumers | dataset | evolution_policy | sla | version |
| --- | --- | --- | --- | --- | --- | --- | --- |
| [ctr-account-gold](#ctr-account-gold) | Account Gold Copy Contract | Contract for Service Profile domain to consume ... | *list* | ds-account-gold | backward-compatible | *dict* | 1.0.0 |
| [ctr-user-gold](#ctr-user-gold) | User Gold Copy Contract | Contract for direct client user data | *list* | ds-user-gold | backward-compatible | *dict* | 1.0.0 |
| [ctr-account-events](#ctr-account-events) | Account Change Events Contract | Events for new/closed accounts | *list* | ds-account-events | forward-compatible | *dict* | 1.0.0 |


---

<a id="data-products"></a>
## Data Products

| id | name | description | access_patterns | bounded_context_ref | contracts | datasets |
| --- | --- | --- | --- | --- | --- | --- |
| [dp-account-serving](#dp-account-serving) | Account Data Serving Product | Read-only serving layer for Service Profile domain | *list* | ddd:BoundedContext:bc_account_data_serving | *list* | *list* |


---

<a id="metadata"></a>
## Metadata

**Phase**: phase_2_strategic

**Last Updated**: 2025-10-15

#### Alignment

**Ddd Model**: platform-ddd.yaml

##### Key Mappings

| data_domains | data_products | ddd_context | ddd_domain | pipelines |
| --- | --- | --- | --- | --- |
| *list* |  |  | dom_data_engineering |  |
|  |  | bc_account_data_sync |  | *list* |
|  | *list* | bc_account_data_serving |  |  |


---

