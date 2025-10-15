---
version: 0.2.0
status: draft
owners: [data-platform-team]
last_updated: 2025-10-09
source_of_truth: /docs/70-how-to-model-systems.md
---

# How to Model Data Systems

This guide walks through the process of creating a formal, machine-readable model of a data engineering system using the logical model schema and taxonomy patterns.

---

## Overview

A **system model** is a YAML document describing:
- **System identity**: UID, name, domains, ownership.
- **Pipelines**: Stages, transforms, and data flow.
- **Datasets**: Sources, sinks, schemas, and contracts.
- **Contracts**: SLAs, schema evolution policies, ownership.
- **Checks**: Freshness, completeness, drift detection.
- **Lineage**: Data provenance and dependencies.
- **Orchestration**: Schedules, triggers, dependencies.
- **Governance**: Retention, classification, access controls.
- **Observability**: Metrics, SLOs, alerts.

Models enable:
- **Automated validation**: Enforce best practices via conformance rules.
- **Code generation**: Generate pipeline code, schemas, and infrastructure.
- **Lineage visualization**: Understand data flow and impact analysis.
- **Governance enforcement**: Audit compliance with retention, PII, and access policies.

---

## Prerequisites

Before modeling a system:
1. Read `/docs/10-vision.md` to understand objectives and personas.
2. Read `/docs/20-scope-and-nfrs.md` for scope and UID conventions.
3. Read `/docs/40-governance-and-rfc.md` for UID scheme rules.
4. Browse `/taxonomy/patterns/` to familiarize yourself with available patterns.
5. Review example models in `/model/examples/` (retail, payments, IoT, ML features).

---

## Step-by-Step Modeling Process

### Step 1: Gather Requirements

Start with a clear understanding of:
- **Business purpose**: What problem does this system solve?
- **Data sources**: Where does data come from? (APIs, databases, files, streams)
- **Transformations**: What processing is required? (cleaning, joins, aggregations, SCD)
- **Outputs**: Who consumes the data? (BI tools, APIs, ML models, downstream systems)
- **Quality & SLA requirements**: Freshness, completeness, uptime.
- **Governance constraints**: Retention, PII, access controls.

**Example**:
> "Build an e-commerce order processing pipeline that ingests orders from a PostgreSQL database via CDC, cleans and deduplicates in a silver layer, and serves dimensional models for BI dashboards. Data must be fresh within 10 minutes, retain for 7 years, and mask PII fields."

---

### Step 2: Identify Applicable Patterns

Map requirements to taxonomy patterns:

| Requirement                          | Pattern(s)                                                                 |
|--------------------------------------|---------------------------------------------------------------------------|
| Ingest from PostgreSQL via CDC       | `pat-cdc-log-based`, `pat-outbox-cdc`                                     |
| Ensure exactly-once delivery         | `pat-exactly-once`, `pat-idempotent`, `pat-deduplication`                 |
| Bronze/Silver/Gold layering          | `pat-medallion-lakehouse`                                                 |
| Handle late-arriving records         | `pat-late-arrival-handling`, `pat-watermarking`                           |
| Slowly changing dimensions           | `pat-scd-type2`                                                           |
| Data contracts                       | `pat-data-contract`, `pat-schema-evolution-policy`                        |
| Freshness monitoring                 | `pat-freshness-check`, `pat-quality-checks-defaults`                      |
| Retention policy                     | `pat-retention-policy`                                                    |
| Dead-letter queue for failures       | `pat-dlq`, `pat-poison-pill-handling`                                     |

---

### Step 3: Design System Structure

Create a high-level diagram or outline:

```
System: E-Commerce Order Processing (sys-order-processing)
  Domain: Orders (dom-orders)
    Pipeline: Ingest Orders (pip-ingest-orders)
      Stage: CDC Capture (stg-cdc-capture)
        → Dataset: orders-raw (ds-orders-raw)
      Stage: Bronze Landing (stg-bronze-landing)
        → Dataset: orders-bronze (ds-orders-bronze)
      Stage: Deduplicate & Clean (stg-dedupe-clean)
        → Dataset: orders-silver (ds-orders-silver)
      Stage: Dimensional Modeling (stg-dim-model)
        → Dataset: dim-orders (ds-dim-orders)
        → Dataset: fact-order-lines (ds-fact-order-lines)
```

---

### Step 4: Create the Model File

Use `/model/model.schema.yaml` as the contract.

#### Start with Front Matter and System Metadata

```yaml
---
version: 0.2.0
status: draft
last_updated: 2025-10-09
---

system:
  id: sys-order-processing
  name: E-Commerce Order Processing
  description: Ingests orders via CDC, processes through medallion layers, serves dimensional models for BI.
  owners:
    - team: data-platform
      contact: data-platform@example.com
  domains:
    - dom-orders
```

#### Define Domains

```yaml
domains:
  - id: dom-orders
    name: Orders Domain
    description: Manages order lifecycle data from creation to fulfillment.
    pipelines:
      - pip-ingest-orders
```

#### Define Pipelines

```yaml
pipelines:
  - id: pip-ingest-orders
    name: Ingest Orders Pipeline
    description: CDC-based ingestion from PostgreSQL to lakehouse.
    mode: streaming  # or batch, micro-batch
    schedule:
      id: sch-continuous
      type: continuous
      triggers:
        - type: cdc-event
          source: postgresql-orders
    stages:
      - id: stg-cdc-capture
        name: CDC Capture
        uses_patterns:
          - pat-cdc-log-based
          - pat-idempotent
        inputs: []
        outputs:
          - ds-orders-raw
        transforms:
          - id: trf-cdc-extract
            type: cdc-extract
            config:
              connector: debezium
              database: orders_db
              table: orders

      - id: stg-bronze-landing
        name: Bronze Landing
        uses_patterns:
          - pat-medallion-lakehouse
        inputs:
          - ds-orders-raw
        outputs:
          - ds-orders-bronze
        transforms:
          - id: trf-append-only
            type: append
            config:
              format: delta
              mode: append

      - id: stg-dedupe-clean
        name: Deduplicate and Clean
        uses_patterns:
          - pat-deduplication
          - pat-merge-upsert
        inputs:
          - ds-orders-bronze
        outputs:
          - ds-orders-silver
        transforms:
          - id: trf-dedupe
            type: deduplication
            dedup_key: [order_id, event_time]
          - id: trf-clean
            type: data-quality
            config:
              drop_nulls: [order_id, customer_id]
              validate_schema: true

      - id: stg-dim-model
        name: Dimensional Modeling
        uses_patterns:
          - pat-dimensional-modeling
          - pat-scd-type2
        inputs:
          - ds-orders-silver
        outputs:
          - ds-dim-orders
          - ds-fact-order-lines
        transforms:
          - id: trf-scd2-orders
            type: scd-type2
            scd_key: order_id
            version_columns: [effective_from, effective_to, is_current]
```

#### Define Datasets

```yaml
datasets:
  - id: ds-orders-raw
    name: Orders Raw
    type: stream
    format: json
    location: s3://datalake/raw/orders/
    schema:
      $ref: schemas/orders-raw.json
    classification: internal
    contains_pii: false

  - id: ds-orders-bronze
    name: Orders Bronze
    type: table
    format: delta
    location: s3://datalake/bronze/orders/
    schema:
      $ref: schemas/orders-bronze.json
    partitioning:
      columns: [date]
      strategy: daily
    classification: internal
    contains_pii: false

  - id: ds-orders-silver
    name: Orders Silver
    type: table
    format: delta
    location: s3://datalake/silver/orders/
    schema:
      $ref: schemas/orders-silver.json
    partitioning:
      columns: [date]
      strategy: daily
    classification: internal
    contains_pii: true
    pii_fields: [customer_email, shipping_address]

  - id: ds-dim-orders
    name: Dimension - Orders
    type: table
    format: delta
    location: s3://datalake/gold/dim_orders/
    schema:
      $ref: schemas/dim-orders.json
    classification: internal
    contains_pii: true

  - id: ds-fact-order-lines
    name: Fact - Order Lines
    type: table
    format: delta
    location: s3://datalake/gold/fact_order_lines/
    schema:
      $ref: schemas/fact-order-lines.json
    classification: internal
    contains_pii: false
```

#### Define Contracts

```yaml
contracts:
  - id: ctr-orders-silver-v1
    name: Orders Silver Contract
    dataset: ds-orders-silver
    version: 1.0.0
    schema:
      $ref: schemas/orders-silver.json
    sla:
      freshness_minutes: 10
      completeness_percent: 99.9
      availability_percent: 99.5
    evolution_policy: backward-compatible
    owners:
      - team: data-platform
        contact: data-platform@example.com
    consumers:
      - team: analytics
        use_case: BI dashboards
```

#### Define Checks

```yaml
checks:
  - id: chk-freshness-orders-silver
    name: Freshness Check - Orders Silver
    type: freshness
    dataset: ds-orders-silver
    threshold:
      max_age_minutes: 10
    severity: critical
    alert:
      channel: pagerduty
      escalation: data-platform-oncall

  - id: chk-completeness-orders-silver
    name: Completeness Check - Orders Silver
    type: completeness
    dataset: ds-orders-silver
    assertions:
      - field: order_id
        not_null: true
      - field: customer_id
        not_null: true
    severity: high

  - id: chk-drift-orders-silver
    name: Drift Detection - Orders Silver
    type: drift
    dataset: ds-orders-silver
    baseline:
      schema_version: 1.0.0
      distribution_baseline: schemas/orders-silver-baseline.json
    severity: medium
```

#### Define Lineage

```yaml
lineage:
  - id: lin-orders-raw-to-bronze
    upstream: ds-orders-raw
    downstream: ds-orders-bronze
    transform: trf-append-only
    relationship: one-to-one

  - id: lin-orders-bronze-to-silver
    upstream: ds-orders-bronze
    downstream: ds-orders-silver
    transform: trf-dedupe
    relationship: many-to-one

  - id: lin-orders-silver-to-dim
    upstream: ds-orders-silver
    downstream: ds-dim-orders
    transform: trf-scd2-orders
    relationship: one-to-many
```

#### Define Governance

```yaml
governance:
  retention:
    - dataset: ds-orders-bronze
      policy: delete-after-days
      days: 90
    - dataset: ds-orders-silver
      policy: archive-after-years
      years: 7
    - dataset: ds-dim-orders
      policy: retain-indefinitely

  access:
    - dataset: ds-orders-silver
      tier: restricted
      roles: [data-engineer, data-analyst]
    - dataset: ds-dim-orders
      tier: general
      roles: [data-analyst, business-user]

  pii_handling:
    - dataset: ds-orders-silver
      masking: [customer_email, shipping_address]
      masking_method: sha256-hash
```

#### Define Observability

```yaml
observability:
  metrics:
    - name: orders_ingested_count
      dataset: ds-orders-raw
      type: counter
      description: Total orders ingested from CDC.

    - name: orders_dedupe_rate
      dataset: ds-orders-silver
      type: gauge
      description: Percentage of duplicate records dropped.

  slos:
    - name: orders-freshness-slo
      target: 99.9
      unit: percent
      window: 30d
      linked_check: chk-freshness-orders-silver

  alerts:
    - name: orders-stale-alert
      condition: freshness > 10 minutes
      severity: critical
      channel: pagerduty
```

---

### Step 5: Validate the Model

Run the validator to check schema compliance and conformance rules:

```bash
python /model/validators/validate.py /model/examples/order-processing/model.example.yaml
```

Expected validations:
- Schema conforms to `/model/model.schema.yaml`.
- All UIDs are unique and properly formatted.
- Cross-references (datasets, patterns, checks) resolve.
- Conformance rules pass (e.g., retention policy present, freshness check defined).

---

### Step 6: Iterate and Refine

- **Add missing checks**: If validator suggests missing freshness or completeness checks, add them.
- **Refine patterns**: Ensure `uses_patterns` accurately reflect the implementation.
- **Document tradeoffs**: Add inline comments explaining design decisions.
- **Link to external schemas**: Use `$ref` for schema files to keep models concise.

---

### Step 7: Review and Finalize

1. **Peer review**: Have architects or senior engineers review for correctness and completeness.
2. **Update status**: Move from `draft` → `review` → `final`.
3. **Add to golden tests**: Copy to `/tests/golden/{system-name}.yaml` for regression testing.
4. **Update CHANGELOG**: Document the new model.

---

## Tips for Effective Modeling

### For LLM Agents

- **Start with requirements**: Parse user inputs into structured requirements before generating YAML.
- **Select patterns first**: Query the taxonomy index to identify applicable patterns based on keywords and tags.
- **Generate incrementally**: Build the model top-down (system → domains → pipelines → stages → datasets).
- **Validate early**: Run validator after each major section to catch errors quickly.
- **Use examples as templates**: Adapt `/model/examples/` to new use cases rather than starting from scratch.
- **Explain tradeoffs**: When presenting the model to engineers, highlight key design decisions and alternatives.

### For Engineers

- **Be explicit**: Don't rely on implicit assumptions; document everything (formats, partitioning, SLAs).
- **Link to patterns**: Use `uses_patterns` generously to make architectural intent clear.
- **Think governance first**: Add retention, classification, and PII handling early, not as an afterthought.
- **Use realistic UIDs**: IDs like `sys-order-processing` are better than `sys-001`.
- **Version schemas**: Use `$ref` to external schema files and version them (e.g., `schemas/orders-v1.json`).

---

## Common Modeling Patterns

### Medallion (Bronze/Silver/Gold)

```yaml
pipelines:
  - id: pip-medallion
    stages:
      - id: stg-bronze
        uses_patterns: [pat-medallion-lakehouse]
        outputs: [ds-data-bronze]
      - id: stg-silver
        uses_patterns: [pat-medallion-lakehouse, pat-deduplication, pat-schema-enforcement]
        inputs: [ds-data-bronze]
        outputs: [ds-data-silver]
      - id: stg-gold
        uses_patterns: [pat-medallion-lakehouse, pat-dimensional-modeling]
        inputs: [ds-data-silver]
        outputs: [ds-dim-table, ds-fact-table]
```

### CDC with Exactly-Once

```yaml
stages:
  - id: stg-cdc-ingest
    uses_patterns:
      - pat-cdc-outbox
      - pat-exactly-once
      - pat-idempotent
      - pat-dlq
    transforms:
      - id: trf-cdc
        type: cdc-extract
```

### Streaming with Watermarks

```yaml
stages:
  - id: stg-windowed-agg
    uses_patterns:
      - pat-watermarking
      - pat-late-arrival-handling
      - pat-windowed-aggregation
    transforms:
      - id: trf-window
        type: tumbling-window
        window_duration: 5m
        allowed_lateness: 1h
```

---

## Pipeline Templates

### data_pipeline_template

Reusable pipeline patterns that can be instantiated for common use cases. Templates provide a starting point with best practices baked in.

**Common Templates:**

1. **CDC Ingestion Template**
   ```yaml
   template_id: tpl-cdc-ingestion
   description: Change Data Capture from OLTP to data lake
   stages:
     - id: stg-cdc-capture
       uses_patterns: [pat-cdc-outbox, pat-idempotent]
       transforms:
         - type: cdc-extract
     - id: stg-dlq-handler
       uses_patterns: [pat-dlq]
   checks:
     - type: freshness
       threshold: 5m
   ```

2. **Batch ETL Template**
   ```yaml
   template_id: tpl-batch-etl
   description: Daily batch extract-transform-load
   schedule:
     type: cron
     expression: "0 2 * * *"
   stages:
     - id: stg-extract
       uses_patterns: [pat-incremental-load]
     - id: stg-transform
       uses_patterns: [pat-idempotent, pat-scd-type2]
     - id: stg-load
       uses_patterns: [pat-upsert]
   ```

3. **Streaming Aggregation Template**
   ```yaml
   template_id: tpl-streaming-agg
   description: Real-time windowed aggregation
   mode: streaming
   stages:
     - id: stg-ingest
       uses_patterns: [pat-exactly-once, pat-watermarking]
     - id: stg-aggregate
       uses_patterns: [pat-windowed-aggregation]
       transforms:
         - type: tumbling-window
           window: 5m
   ```

**How to Use Templates:**

1. Copy template structure to your model
2. Customize IDs, dataset refs, and business logic
3. Add domain-specific transforms
4. Validate with conformance rules
5. Deploy to production

---

## Example: Complete Minimal Model

See `/model/model.example.min.yaml` for a tiny but valid example, and `/model/examples/retail/model.example.yaml` for a realistic end-to-end system.

---

**Next**: See `/docs/50-conformance-guide.md` for interpreting validation results, and `/docs/60-how-to-author-patterns.md` if you need to create custom patterns.
