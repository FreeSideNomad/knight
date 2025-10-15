---
version: 0.2.0
status: review
owners: [data-platform-team]
last_updated: 2025-10-09
---

# Frequently Asked Questions (FAQ)

Common questions about the Data Engineering Taxonomy & Logical Model project, with practical answers and examples.

---

## Table of Contents

- [Getting Started](#getting-started)
- [Patterns & Taxonomy](#patterns--taxonomy)
- [Modeling Systems](#modeling-systems)
- [Validation & Conformance](#validation--conformance)
- [Technology Choices](#technology-choices)
- [Implementation & Operations](#implementation--operations)
- [Troubleshooting](#troubleshooting)

---

## Getting Started

### Q: What is this project and who is it for?

**A:** This project provides:

1. **Taxonomy**: A catalog of 65+ proven data engineering patterns and 13 anti-patterns
2. **Logical Model Schema**: A formal YAML/JSON-Schema specification for describing data systems
3. **Conformance Framework**: Automated validator with 55+ rules to enforce best practices
4. **Documentation**: Authoring guides, examples, and governance policies

**Target Personas:**
- **Data Engineers**: Design guidance, formal documentation, anti-pattern detection
- **Data Architects**: Standards enforcement, ADR references, technology selection
- **Governance Leads**: Automated compliance, audit trails, retention enforcement
- **SREs**: SLA codification, pattern-based monitoring
- **LLM Agents**: Code generation, pattern selection, validation

**Key Benefit**: Shift-left quality enforcement through automated validation in CI/CD.

---

### Q: How do I get started in 5 minutes?

**A:** Quick start:

```bash
# 1. Clone/navigate to repository
cd /path/to/data-eng-schema

# 2. Browse patterns
cat taxonomy/02-taxonomy-index.yaml
ls taxonomy/patterns/ingestion/

# 3. View example model
cat model/examples/retail/model.example.yaml

# 4. Install validator
cd model/validators
pip install -r requirements.txt

# 5. Validate example
python validate.py --model ../examples/retail/model.example.yaml

# Expected output:
# ✅ Schema validation: PASS
# ✅ UID validation: PASS
# ✅ Cross-reference validation: PASS
# ✅ Conformance rules: PASS
```

**Next Steps**:
- Read `/docs/10-vision.md` for project objectives
- Read `/docs/70-how-to-model-systems.md` for modeling walkthrough
- Copy `/model/model.example.min.yaml` to create your first model

---

## Patterns & Taxonomy

### Q: How do I choose between patterns?

**A:** Use this decision framework:

**1. Identify Your Category:**
- **Ingestion**: How do I bring data in? (CDC, batch, streaming, webhooks)
- **Transformation**: How do I process data? (joins, aggregations, SCD)
- **Storage**: How do I optimize data at rest? (compaction, partitioning, tiering)
- **Serving**: How do I expose data? (BI, API, feature store, search index)
- **Cross-cutting**: Governance, observability, reliability, cost

**2. Filter by Maturity:**
- **Foundational**: Start here (e.g., `pat-idempotent`, `pat-medallion-lakehouse`)
- **Intermediate**: After foundational patterns are stable (e.g., `pat-scd-type2`)
- **Advanced**: For specialized use cases (e.g., `pat-watermarking`, `pat-outbox-cdc`)

**3. Check Pattern Metadata:**

```yaml
# /taxonomy/patterns/core/idempotent.yaml
uid: pat-idempotent
maturity: foundational
applicability:
  - All pipelines requiring retry safety
  - Backfill and replay scenarios
  - Exactly-once semantics
related_patterns:
  - pat-retryable
  - pat-exactly-once
conflicts_with:
  - ant-dual-write
```

**4. Search by Tags:**

```bash
# Find all streaming patterns
grep -r "tags:.*streaming" taxonomy/patterns/

# Find all patterns with exactly-once tag
grep -r "exactly-once" taxonomy/patterns/
```

**Example Decision: "How do I ingest from a database?"**

1. Category: Ingestion
2. Options: `pat-cdc-log-based`, `pat-cdc-change-tables`, `pat-outbox-cdc`, `pat-batch-extract`
3. Decision factors:
   - Real-time? → `pat-cdc-log-based` or `pat-outbox-cdc`
   - Batch? → `pat-batch-extract`
   - Transactional consistency required? → `pat-outbox-cdc`
   - Low latency? → `pat-cdc-log-based`

---

### Q: What's the difference between bronze/silver/gold?

**A:** The medallion architecture (bronze/silver/gold) organizes data by quality and refinement:

| Layer | Purpose | Characteristics | Example |
|-------|---------|----------------|---------|
| **Bronze** | Raw landing zone | - Append-only<br>- Schema-on-read<br>- Minimal validation<br>- Preserves original format | Raw JSON from API dumps stored in S3 |
| **Silver** | Cleaned, validated | - Schema-enforced<br>- Deduplicated<br>- Business keys<br>- Standardized formats | Parsed, validated orders with standardized timestamps |
| **Gold** | Business-ready | - Aggregated<br>- Dimensional models<br>- Curated feature sets<br>- Optimized for consumption | Daily sales aggregates by region for BI dashboards |

**When to use each:**

- **Bronze**: Always land raw data first (enables replay, audit, debugging)
- **Silver**: Required for downstream consumption (enforces quality gates)
- **Gold**: For specific use cases (BI, ML features, reporting)

**Pattern Reference**: `pat-medallion-lakehouse`

---

### Q: When should I use Delta vs. Iceberg vs. Hudi?

**A:** Choose based on workload characteristics:

| Feature | Delta Lake | Iceberg | Hudi |
|---------|-----------|---------|------|
| **Best For** | Databricks ecosystem, general lakehouse | Multi-engine analytics, large tables | CDC-heavy workloads, upserts |
| **ACID Support** | ✅ Full | ✅ Full | ✅ Full |
| **Time-Travel** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Schema Evolution** | ✅ Good | ✅ Excellent (hidden partitioning) | ✅ Good |
| **Multi-Engine** | Moderate | ✅ Excellent (Spark, Trino, Flink, Presto) | Moderate |
| **Streaming Writes** | ✅ Excellent | Good | ✅ Excellent |
| **Upsert Performance** | Good | Good | ✅ Excellent (MOR table type) |
| **Ecosystem** | Databricks-native | Vendor-neutral | Netflix, Uber workloads |

**Decision Matrix:**

```
Use Delta if:
  - Running on Databricks
  - Need tight Spark integration
  - Want Z-order clustering

Use Iceberg if:
  - Need multi-engine access (Spark + Trino + Flink)
  - Have very large tables (petabytes)
  - Want vendor-neutral format
  - Need partition evolution without rewrites

Use Hudi if:
  - Heavy CDC workloads (many upserts)
  - Need incremental query (read only changed records)
  - Want compaction control (MOR vs. COW table types)
```

**Pattern References**: `pat-delta-lake-maintenance`, `pat-iceberg-maintenance`, `pat-hudi-maintenance`

---

## Modeling Systems

### Q: How do I model a new system?

**A:** Follow this step-by-step workflow:

**Step 1: Start with Template**

```bash
cp model/model.example.min.yaml my-system.yaml
```

**Step 2: Define System Metadata**

```yaml
system:
  uid: sys-my-data-platform
  name: My Data Platform
  version: 1.0.0
  description: End-to-end data platform for analytics
  owners: [data-platform-team]
  domains: [dom-analytics]
```

**Step 3: Define Domains**

```yaml
domains:
  - uid: dom-analytics
    name: Analytics Domain
    description: Business intelligence and reporting
    pipelines: [pip-daily-rollup]
```

**Step 4: Define Datasets**

```yaml
datasets:
  - uid: ds-orders-bronze
    name: Orders Bronze
    type: landing
    format: json
    location: s3://bronze/orders/
    layer: bronze

  - uid: ds-orders-silver
    name: Orders Silver
    type: curated
    format: delta
    location: s3://silver/orders/
    layer: silver
    schema_ref: schemas/orders-v1.avsc
```

**Step 5: Define Pipelines**

```yaml
pipelines:
  - uid: pip-ingest-orders
    name: Ingest Orders
    mode: batch
    stages:
      - uid: stg-land
        type: ingestion
        inputs: []
        outputs: [ds-orders-bronze]
        uses_patterns: [pat-idempotent]

      - uid: stg-clean
        type: transformation
        inputs: [ds-orders-bronze]
        outputs: [ds-orders-silver]
        uses_patterns: [pat-deduplication, pat-schema-enforcement]
```

**Step 6: Add Checks**

```yaml
checks:
  - uid: chk-orders-freshness
    type: freshness
    dataset: ds-orders-silver
    params:
      max_lag_minutes: 30
    severity: high
    on_failure: alert
```

**Step 7: Validate**

```bash
python model/validators/validate.py --model my-system.yaml --strict
```

**See**: `/docs/70-how-to-model-systems.md` for detailed walkthrough

---

### Q: How do I add a custom conformance rule?

**A:** Create a rule in `/model/checks/rules/custom.yaml`:

```yaml
rules:
  - id: rul-gold-requires-contract
    name: Gold datasets require contracts
    description: All gold-layer datasets must have a formal data contract
    severity: ERROR
    enabled: true
    selector: $.datasets[?(@.layer == 'gold')]
    conditions:
      - type: required_field
        field: contract_id
    message: |
      Gold dataset '{id}' is missing a contract.
      Add a contract_id field referencing a formal data contract.
```

**Rule Components:**

- **id**: Unique rule identifier (`rul-*`)
- **selector**: JSONPath expression to find matching entities
- **conditions**: Validation checks (required_field, pattern, enum, etc.)
- **severity**: ERROR | WARNING | SUGGESTION
- **message**: Actionable guidance with template variables

**Test Rule:**

```bash
python model/validators/validate.py --model my-model.yaml
# Rule automatically loaded from /model/checks/rules/*.yaml
```

**See**: `/docs/50-conformance-guide.md` → Custom Rules section

---

## Technology Choices

### Q: How do I implement SCD Type 2?

**A:** SCD Type 2 tracks full history by creating new rows for each change:

**Schema:**

```sql
CREATE TABLE customers_silver (
  customer_id STRING,          -- Business key
  name STRING,
  email STRING,
  tier STRING,                 -- Value that changes

  -- SCD Type 2 columns:
  effective_from TIMESTAMP,    -- When this version became active
  effective_to TIMESTAMP,      -- When this version expired (NULL = current)
  is_current BOOLEAN,          -- Flag for current version
  version INT,                 -- Optional: version number

  PRIMARY KEY (customer_id, effective_from)
)
```

**Transformation Logic:**

```python
# Pseudo-code (Spark/dbt-like syntax)
def scd_type2_upsert(target, updates):
    # 1. Find changed records
    changed = updates.join(target.filter("is_current = true"),
                           on="customer_id",
                           how="inner") \
                     .where("updates.tier != target.tier")

    # 2. Expire old versions
    target.update(
        condition="customer_id IN (changed.customer_id)",
        set={
            "effective_to": current_timestamp(),
            "is_current": False
        }
    )

    # 3. Insert new versions
    changed.select(
        "customer_id", "name", "email", "tier",
        current_timestamp().alias("effective_from"),
        lit(None).alias("effective_to"),
        lit(True).alias("is_current"),
        (max("version") + 1).alias("version")
    ).write.append(target)
```

**Model YAML:**

```yaml
stages:
  - uid: stg-customer-scd2
    type: transformation
    inputs: [ds-customers-updates]
    outputs: [ds-customers-silver]
    uses_patterns: [pat-scd-type2, pat-merge-upsert]
    transforms:
      - uid: trf-scd2-customer
        type: scd2
        params:
          business_key: customer_id
          effective_date_column: effective_from
          expiry_date_column: effective_to
          current_flag_column: is_current
```

**Pattern Reference**: Implied in transformation patterns (no dedicated `pat-scd-type2.yaml` yet)

**See Also**: `pat-merge-upsert`, `pat-temporal-joins`

---

### Q: What's the difference between idempotent and retryable?

**A:** Related but distinct concepts:

**Idempotent:**
- **Definition**: Running operation N times produces same result as running once
- **Ensures**: Safe to replay/retry without duplicates or side effects
- **Examples**:
  - SQL `MERGE`/`UPSERT` with business key
  - REST `PUT /resource/123` (overwrites)
  - Writing to S3 with deterministic filename

**Retryable:**
- **Definition**: Operation can be safely retried after failure
- **Ensures**: Failure doesn't leave system in inconsistent state
- **Examples**:
  - Database transaction (rolled back on failure)
  - API call with exponential backoff
  - Kafka consumer with offset management

**Relationship:**

```
Idempotent ⊂ Retryable

All idempotent operations are retryable.
Not all retryable operations are idempotent.

Example:
  - UPSERT: Idempotent AND retryable (safe to retry, no duplicates)
  - INSERT with retry + dedup: Retryable but NOT idempotent (needs dedup step)
  - Append-only INSERT: Retryable but NOT idempotent (creates duplicates)
```

**Model Usage:**

```yaml
stages:
  - uid: stg-idempotent-stage
    traits: [idempotent, retryable]  # Both traits
    uses_patterns: [pat-idempotent, pat-retryable]
    retry_config:
      max_attempts: 3
      backoff_strategy: exponential
```

**Pattern References**: `pat-idempotent`, `pat-retryable`

---

### Q: How do I handle late-arriving data?

**A:** Use watermarking + late arrival handling:

**Concept:**

```
Event Time:      08:00    09:00    10:00    11:00
                   │        │        │        │
Processing Time:   │        │        │        │
                   ├────────┼────────┼────────┤
Window:         [08:00-09:00] (1-hour tumbling window)
Watermark:         08:55 (5-min lag)
Allowed Lateness:  10 min

Timeline:
  09:05: Window closes (watermark reaches 09:00)
  09:10: Late data with event_time=08:45 arrives
         → Within allowed lateness → included in window
  09:16: Late data with event_time=08:30 arrives
         → Beyond allowed lateness → routed to late data table
```

**Configuration:**

```yaml
stages:
  - uid: stg-windowed-aggregation
    type: transformation
    mode: streaming
    uses_patterns: [pat-watermarking, pat-late-arrival-handling]

    watermark_config:
      column: event_time
      delay_minutes: 5           # Watermark trails max event time by 5 min

    late_arrival_config:
      allowed_lateness_minutes: 10  # Accept late data up to 10 min after watermark
      late_data_dataset: ds-late-arrivals  # Sink for data beyond allowed lateness

    window_config:
      type: tumbling
      size_minutes: 60
```

**Handling Late Data:**

```sql
-- Option 1: Ignore late data (data loss)
-- Option 2: Route to late data table (for investigation/replay)
-- Option 3: Re-emit window results (update downstream aggregates)
```

**Pattern References**: `pat-watermarking`, `pat-late-arrival-handling`

---

### Q: When should I use streaming vs. batch?

**A:** Decision matrix:

| Factor | Streaming | Batch | Micro-Batch |
|--------|-----------|-------|-------------|
| **Latency SLA** | <1 second | Hours to days | Seconds to minutes |
| **Data Volume** | Continuous, unbounded | Fixed snapshots | Small batches |
| **Cost** | Higher (always-on) | Lower (scheduled) | Moderate |
| **Complexity** | High (watermarks, state) | Low | Moderate |
| **Use Cases** | Fraud detection, real-time alerts | Daily reports, backfills | Near-real-time dashboards |
| **Failure Handling** | Checkpoints, exactly-once | Retry entire batch | Retry small batch |

**Decision Tree:**

```
Is latency SLA < 15 minutes?
  ├─ YES: Is latency SLA < 1 minute?
  │       ├─ YES: Use Streaming (pat-streaming-ingest)
  │       └─ NO:  Use Micro-Batch (pat-file-drop-microbatch)
  └─ NO:  Use Batch (pat-batch-extract)

Can you tolerate always-on costs?
  ├─ YES: Streaming or Micro-Batch
  └─ NO:  Batch (scheduled, auto-stop clusters)

Do you need exactly-once guarantees?
  ├─ YES: Streaming with checkpoints (pat-exactly-once)
  └─ NO:  Batch with idempotent transforms (pat-idempotent)
```

**Pattern References**: `pat-streaming-ingest`, `pat-batch-microbatch-continuum`, `pat-file-drop-microbatch`

---

## Implementation & Operations

### Q: How do I debug pipeline failures?

**A:** Systematic debugging checklist:

**Step 1: Check Observability**

```yaml
# Ensure model has observability configured
checks:
  - uid: chk-freshness
    type: freshness
    dataset: ds-orders-silver
    severity: high
    on_failure: alert

  - uid: chk-completeness
    type: completeness
    dataset: ds-orders-silver
    params:
      expected_row_count_min: 1000
```

**Step 2: Validate Model**

```bash
# Run validator to check for anti-patterns
python model/validators/validate.py --model my-model.yaml --strict

# Common issues caught:
# - Missing DLQ (ant-no-dlq)
# - No retention policy (rul-retention-required)
# - Broken cross-references
```

**Step 3: Check Logs**

```bash
# Look for validation errors in pipeline logs
grep -i "schema validation failed" pipeline.log
grep -i "null pointer" pipeline.log

# Check for resource issues
grep -i "out of memory" pipeline.log
grep -i "timeout" pipeline.log
```

**Step 4: Replay with Debug Sampling**

```yaml
# Add debug sampling to isolate issue
stages:
  - uid: stg-debug
    type: transformation
    uses_patterns: [pat-debug-sampling]
    transforms:
      - type: sample
        params:
          rate: 0.01          # Sample 1% of data
          strategy: deterministic
          seed: 42
```

**Step 5: Check DLQ**

```bash
# Inspect dead-letter queue for poison pills
aws s3 ls s3://bucket/dlq/orders/

# Analyze failed records
cat dlq-record.json | jq .
```

**Pattern References**: `pat-debug-sampling`, `pat-dlq-pattern`, `pat-poison-pill-handling`

---

### Q: How do I migrate between table formats?

**A:** Use incremental migration strategy:

**Phase 1: Dual Write (Temporary)**

```yaml
stages:
  - uid: stg-migrate
    type: transformation
    inputs: [ds-source]
    outputs: [ds-target-delta, ds-target-iceberg]  # Write to both
    uses_patterns: [pat-format-migration]
```

**Phase 2: Validation**

```yaml
checks:
  - uid: chk-migration-count
    type: completeness
    dataset: ds-target-iceberg
    params:
      compare_dataset: ds-target-delta  # Ensure row counts match
      tolerance: 0.01
```

**Phase 3: Switch Consumers**

```yaml
# Update downstream consumers to read from new format
pipelines:
  - uid: pip-analytics
    stages:
      - inputs: [ds-target-iceberg]  # Changed from ds-target-delta
```

**Phase 4: Deprecate Old Format**

```yaml
datasets:
  - uid: ds-target-delta
    deprecated: true
    deprecation_date: 2025-11-01
    replacement: ds-target-iceberg
```

**Pattern Reference**: `pat-format-migration`

---

## Troubleshooting

### Q: Validator reports "UID format invalid" but it looks correct

**A:** Common UID pitfalls:

**❌ Wrong:**

```yaml
pipelines:
  - id: Ingest_Orders       # Capital letters not allowed
  - id: pip_ingest_orders   # Underscores not allowed
  - id: ingest-orders       # Missing prefix
  - id: pip-Ingest-Orders   # Capital letters not allowed
```

**✅ Correct:**

```yaml
pipelines:
  - id: pip-ingest-orders   # Lowercase, kebab-case, with prefix
```

**UID Format Rules:**

```
Pattern: ^{type-code}-[a-z0-9-]+$

Type Codes:
  sys-  → System
  dom-  → Domain
  pip-  → Pipeline
  stg-  → Stage
  trf-  → Transform
  ds-   → Dataset
  chk-  → Check
  ctr-  → Contract
  lin-  → Lineage
  sch-  → Schedule
  pat-  → Pattern (taxonomy only)

Allowed Characters: a-z, 0-9, - (hyphen)
NOT Allowed: A-Z, _, spaces
```

---

### Q: How do I suppress a specific validation warning?

**A:** Three options:

**Option 1: Disable Rule Globally**

```yaml
# /model/checks/rules/custom.yaml
rules:
  - id: rul-problematic-rule
    enabled: false  # Disable for all models
```

**Option 2: Lower Severity**

```yaml
# /model/checks/rules/custom.yaml
rules:
  - id: rul-retention-required
    severity: SUGGESTION  # Changed from WARNING
```

**Option 3: Add Exception Comment (Future Feature)**

```yaml
# In model.yaml (planned for future release)
datasets:
  - uid: ds-legacy-system
    # conformance-ignore: rul-retention-required
    # reason: Legacy system exempt from retention policy
```

**Temporary Workaround:**

```bash
# Run without strict mode (warnings don't fail)
python validate.py --model model.yaml  # Exit code 2 (warning) is non-fatal
```

---

### Q: Where do I get help?

**A:** Resources:

1. **Documentation**:
   - `/docs/10-vision.md` — Project overview
   - `/docs/70-how-to-model-systems.md` — Modeling walkthrough
   - `/docs/50-conformance-guide.md` — Validation guide
   - `/taxonomy/01-glossary.md` — 76 term definitions

2. **Examples**:
   - `/model/examples/retail/` — Retail e-commerce example
   - `/model/examples/payments/` — Payments processing example
   - `/model/examples/iot/` — IoT streaming example
   - `/model/examples/ml-features/` — ML feature engineering example

3. **Community**:
   - GitHub Issues: [github.com/your-org/data-eng-schema/issues](https://github.com/your-org/data-eng-schema/issues) (Placeholder)
   - GitHub Discussions: [github.com/your-org/data-eng-schema/discussions](https://github.com/your-org/data-eng-schema/discussions) (Placeholder)
   - Slack: #data-platform (Internal)

4. **Maintainers**:
   - Data Platform Team (data-platform-team@example.com)

---

## Quick Reference

### Common Commands

```bash
# Validate model
python model/validators/validate.py --model my-model.yaml

# Strict mode (warnings → errors)
python model/validators/validate.py --model my-model.yaml --strict

# JSON output for CI
python model/validators/validate.py --model my-model.yaml --output json

# Browse patterns
cat taxonomy/02-taxonomy-index.yaml
ls taxonomy/patterns/ingestion/

# Search glossary
grep -i "scd type" taxonomy/01-glossary.md
```

### Key File Paths

```
/taxonomy/01-glossary.md           — 76 term definitions
/taxonomy/02-taxonomy-index.yaml   — Pattern catalog
/taxonomy/patterns/                — Pattern YAML files
/model/model.schema.yaml           — JSON Schema for models
/model/validators/validate.py      — Validator CLI
/model/checks/rules/               — Conformance rules
/docs/70-how-to-model-systems.md   — Modeling guide
/docs/50-conformance-guide.md      — Validation guide
```

---

## Summary of FAQs

| Question | Category | Key Takeaway |
|----------|----------|--------------|
| What is this project? | Getting Started | Taxonomy + Model Schema + Validator for data engineering systems |
| How to get started? | Getting Started | Browse patterns → View examples → Validate → Model your system |
| How to choose patterns? | Patterns | Filter by category, maturity, tags; check pattern metadata |
| Bronze/Silver/Gold? | Patterns | Raw → Validated → Business-ready layers |
| Delta vs. Iceberg vs. Hudi? | Technology | Delta=Databricks, Iceberg=Multi-engine, Hudi=CDC-heavy |
| How to model a system? | Modeling | Template → Metadata → Datasets → Pipelines → Checks → Validate |
| How to add custom rule? | Validation | Create rule YAML with selector + conditions + message |
| How to implement SCD Type 2? | Technology | Track history with effective_from/to + is_current flag |
| Idempotent vs. Retryable? | Technology | Idempotent=same result; Retryable=safe to retry |
| Handle late-arriving data? | Technology | Watermarking + allowed lateness + late data table |
| Streaming vs. Batch? | Technology | Streaming=<1min SLA; Batch=daily; Micro-batch=5-15min |
| Debug pipeline failures? | Operations | Validator → Logs → Debug sampling → DLQ analysis |
| Migrate table formats? | Operations | Dual write → Validate → Switch consumers → Deprecate old |
| UID format invalid? | Troubleshooting | Use lowercase, kebab-case, correct prefix (e.g., pip-*) |
| Suppress validation warning? | Troubleshooting | Disable rule, lower severity, or run non-strict mode |

---

**Version**: 0.2.0
**Last Updated**: 2025-10-08
**Maintainers**: Data Platform Team
