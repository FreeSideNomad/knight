---
version: 0.2.0
status: final
owners: [data-platform-team]
last_updated: 2025-10-09
---

# Quick Reference — Data Engineering Taxonomy & Logical Model

Fast lookup for common commands, UID formats, pattern sequences, and key concepts.

---

## UID Format Reference

All entity IDs follow `{type-code}-{kebab-case-name}` format.

| Entity Type       | Type Code | Pattern              | Example                    | Used For                          |
|-------------------|-----------|----------------------|----------------------------|-----------------------------------|
| System            | `sys-`    | `^sys-[a-z0-9-]+$`   | `sys-order-processing`     | Top-level system definition       |
| Domain            | `dom-`    | `^dom-[a-z0-9-]+$`   | `dom-payments`             | Business domain grouping          |
| Pipeline          | `pip-`    | `^pip-[a-z0-9-]+$`   | `pip-ingest-orders`        | Data pipeline definition          |
| Stage             | `stg-`    | `^stg-[a-z0-9-]+$`   | `stg-cdc-capture`          | Pipeline stage/step               |
| Transform         | `trf-`    | `^trf-[a-z0-9-]+$`   | `trf-dedupe-orders`        | Transformation logic              |
| Dataset           | `ds-`     | `^ds-[a-z0-9-]+$`    | `ds-orders-silver`         | Table, view, or file dataset      |
| Check             | `chk-`    | `^chk-[a-z0-9-]+$`   | `chk-freshness-orders`     | Data quality check                |
| Pattern           | `pat-`    | `^pat-[a-z0-9-]+$`   | `pat-cdc-outbox`           | Taxonomy pattern                  |
| Anti-Pattern      | `apn-`    | `^apn-[a-z0-9-]+$`   | `apn-dual-write`           | Anti-pattern (avoid)              |
| Contract          | `ctr-`    | `^ctr-[a-z0-9-]+$`   | `ctr-orders-v1`            | Data contract specification       |
| Lineage Edge      | `lin-`    | `^lin-[a-z0-9-]+$`   | `lin-orders-flow`          | Lineage relationship              |
| Schedule          | `sch-`    | `^sch-[a-z0-9-]+$`   | `sch-daily-rollup`         | Execution schedule                |
| Conformance Rule  | `rul-`    | `^rul-[a-z0-9-]+$`   | `rul-retention-required`   | Validation rule                   |

**Rules**:
- **Lowercase only**: `pip-ingest-orders` ✅ | `PIP-Ingest-Orders` ❌
- **Hyphens, not underscores**: `pat-scd-type2` ✅ | `pat_scd_type2` ❌
- **Descriptive**: `ds-customer-silver` ✅ | `ds-cust` ❌

---

## Validator Commands Cheat Sheet

### Installation

```bash
cd model/validators
pip install -r requirements.txt
```

### Basic Commands

| Command | Purpose | Exit Code |
|---------|---------|-----------|
| `python validate.py --model model.yaml` | Validate model (basic) | 0=pass, 1=error, 2=warning |
| `python validate.py --model model.yaml --strict` | Fail on warnings | 0=pass, 1=fail |
| `python validate.py --model model.yaml --output json` | JSON output for CI | Same |
| `python validate.py --model model.yaml --output junit` | JUnit XML for reporting | Same |

### Common Workflows

**Validate example model**:
```bash
python model/validators/validate.py \
  --model ../examples/retail/model.example.yaml
```

**Validate with strict enforcement**:
```bash
python model/validators/validate.py \
  --model my-model.yaml \
  --strict
```

**CI integration (JSON output)**:
```bash
python model/validators/validate.py \
  --model model.yaml \
  --output json \
  --strict > validation-report.json
```

**Pre-commit validation**:
```bash
python model/validators/validate.py \
  --model model.yaml \
  --strict || exit 1
```

### Exit Codes

| Code | Meaning | When |
|------|---------|------|
| `0` | Success | All validations passed |
| `1` | Error | Schema errors, broken refs, or warnings (strict mode) |
| `2` | Warning | Best practice violations (non-strict mode only) |

---

## Common Pattern Sequences

### Ingestion Patterns

**Full Refresh**:
```
pat-full-refresh → ds-{entity}-bronze (landing)
```

**CDC (Change Data Capture)**:
```
pat-cdc-log-based → pat-idempotent → ds-{entity}-bronze (append-only)
```

**API Polling**:
```
pat-api-polling → pat-incremental → pat-retryable → ds-{entity}-bronze
```

**File Ingestion**:
```
pat-file-sensor → pat-schema-validation → ds-{entity}-bronze
```

### Transformation Patterns

**Medallion Architecture** (Bronze → Silver → Gold):
```
ds-{entity}-bronze → pat-dedupe → pat-data-quality-checks → ds-{entity}-silver
ds-{entity}-silver → pat-scd-type2 → ds-{entity}-gold
```

**Stream Processing**:
```
pat-streaming → pat-watermarking → pat-windowed-aggregation → ds-{entity}-aggregated
```

**Slowly Changing Dimensions**:
```
ds-{entity}-source → pat-scd-type2 → ds-{entity}-dimension (with effective_from/to)
```

### Storage Patterns

**Lakehouse**:
```
pat-medallion-lakehouse → pat-partitioning → pat-z-ordering
```

**Time-Series**:
```
pat-time-series → pat-partitioning (by date) → pat-compaction
```

### Reliability Patterns

**Exactly-Once Delivery**:
```
pat-exactly-once → pat-idempotent → pat-outbox-cdc → pat-dlq
```

**Error Handling**:
```
pat-dlq → pat-retryable → pat-circuit-breaker
```

### Observability Patterns

**Data Quality**:
```
pat-freshness-check → pat-completeness-check → pat-drift-detection
```

**Monitoring**:
```
pat-slo-monitoring → pat-alerting → pat-anomaly-detection
```

---

## Conformance Rule Severity Levels

| Severity | Exit Code | Meaning | Action Required |
|----------|-----------|---------|-----------------|
| **ERROR** | 1 | Critical violation (broken refs, invalid schema, missing required fields) | **MUST FIX** before merge |
| **WARNING** | 2¹ | Best practice violation, tech debt, missing optional metadata | Should fix; review with team |
| **SUGGESTION** | 0 | Improvement opportunity, optimization | Optional; consider future |
| **INFO** | 0 | Informational, no action needed | No action |

¹ Exit code 2 in non-strict mode; in `--strict` mode, warnings cause exit code 1 (fail)

### Common Rules by Pack

**Reliability** (`/model/checks/rules/reliability.yaml`):
- `rul-idempotent-streaming`: Streaming pipelines must use idempotent patterns
- `rul-dlq-required`: Error-prone stages must have DLQ configuration
- `rul-exactly-once-transactional`: Exactly-once pipelines need transactional patterns

**Governance** (`/model/checks/rules/governance.yaml`):
- `rul-retention-required`: PII datasets must have retention policies
- `rul-pii-classification`: Datasets with PII must be classified
- `rul-access-control-confidential`: Confidential datasets need access controls

**Observability** (`/model/checks/rules/observability.yaml`):
- `rul-freshness-check-sla`: Datasets with SLA must have freshness checks
- `rul-completeness-check`: Critical datasets need completeness checks
- `rul-drift-detection`: Production datasets should monitor drift

**Contracts** (`/model/checks/rules/contracts.yaml`):
- `rul-contract-required`: Published datasets must have contracts
- `rul-sla-check-alignment`: Contract SLAs must have corresponding checks
- `rul-schema-evolution`: Contracts must specify evolution policy

---

## File Structure Overview

```
/
├── taxonomy/                   # Pattern catalog
│   ├── 00-overview.md          # Taxonomy intro
│   ├── 01-glossary.md          # 76+ term definitions
│   ├── 02-taxonomy-index.yaml  # Master pattern catalog
│   ├── patterns/               # Pattern YAML files
│   │   ├── core/               # Foundational patterns
│   │   ├── ingestion/          # Data acquisition
│   │   ├── transformation/     # Data processing
│   │   ├── storage/            # Storage design
│   │   ├── serving/            # Data access
│   │   ├── governance/         # Compliance
│   │   ├── observability/      # Monitoring
│   │   ├── reliability/        # Fault tolerance
│   │   └── cost/               # Cost optimization
│   └── anti-patterns/          # Anti-patterns to avoid
│
├── model/                      # Logical model schema & examples
│   ├── model.schema.yaml       # JSON Schema definition
│   ├── model.example.min.yaml  # Minimal valid example
│   ├── examples/               # Realistic end-to-end models
│   │   ├── retail/             # E-commerce example
│   │   ├── payments/           # Payment processing
│   │   ├── iot/                # IoT streaming
│   │   └── ml-features/        # ML feature engineering
│   ├── checks/                 # Conformance framework
│   │   ├── conformance-rules.md # Rule philosophy
│   │   └── rules/              # Rule packs (YAML)
│   └── validators/             # Validation tooling
│       ├── validate.py         # Main validator CLI
│       └── README.md           # Validator docs
│
├── docs/                       # Documentation
│   ├── 10-vision.md            # Problem, objectives, personas
│   ├── 20-scope-and-nfrs.md    # Scope & assumptions
│   ├── 30-architecture.md      # System design
│   ├── 40-governance-and-rfc.md # UID scheme, versioning, RFC
│   ├── 50-conformance-guide.md # Validation guide
│   ├── 60-how-to-author-patterns.md # Pattern authoring
│   ├── 70-how-to-model-systems.md   # System modeling
│   ├── 80-diagrams.md          # Diagram generation
│   ├── 90-faq.md               # FAQ
│   └── quick-reference.md      # This file
│
├── tests/                      # Test suite
│   ├── golden/                 # Known-good examples
│   ├── data/                   # Test cases (good/bad)
│   ├── schemas/                # Schema validation tests
│   ├── ci.md                   # CI guidance
│   └── README.md               # Test docs
│
├── tools/                      # Utilities
│   ├── scripts/                # Helper scripts
│   └── makefile.md             # Makefile targets
│
├── CHANGELOG.md                # Version history
├── CONTRIBUTING.md             # Contribution guide
├── LICENSE                     # License
└── README.md                   # Main documentation
```

---

## Key Concepts (1-2 Sentences Each)

### Pattern
Reusable solution to a recurring data engineering problem, documenting what, why, when, and how to apply the solution.

### Anti-Pattern
Commonly occurring solution that initially appears beneficial but leads to negative consequences like tech debt, bugs, or performance issues.

### Conformance Rule
Automated check enforcing best practices, governance policies, and operational standards beyond what schema validation provides.

### UID (Unique Identifier)
Globally unique entity ID following `{type-code}-{kebab-name}` format (e.g., `sys-order-processing`, `pat-cdc-outbox`).

### Medallion Architecture
Multi-layer storage pattern (bronze/silver/gold) progressively refining data from raw → curated → aggregated.

### CDC (Change Data Capture)
Pattern for capturing and propagating database changes to downstream systems without polling or dual writes.

### SCD Type 2 (Slowly Changing Dimension)
Dimensional modeling pattern tracking historical changes by creating new rows with effective date ranges.

### Idempotent
Property where processing the same input multiple times produces the same result, enabling safe retries.

### DLQ (Dead Letter Queue)
Error handling pattern routing failed messages to a separate queue for investigation and replay.

### Watermarking
Streaming pattern tracking event-time progress to handle late-arriving data and trigger computations.

### Freshness Check
Data quality check verifying datasets are updated within expected time windows (SLA compliance).

### Data Contract
Formal specification of dataset schema, SLAs, ownership, and evolution policies for producer-consumer alignment.

### Lineage
Tracking data flow from sources through transformations to destinations for impact analysis and debugging.

### Backfill
Process of reprocessing historical data after schema changes, bug fixes, or new transformation logic.

### Exactly-Once
Delivery guarantee ensuring each message is processed once and only once, even with failures and retries.

### Data Quality Dimension
Standard quality measurement dimension (accuracy, completeness, timeliness, consistency) used to assess dataset quality.

### Data Transformation Function
Common transformation type (filter, map, join, aggregate, upsert) applied in pipeline stages.

### Data Validation Rule Type
Category of validation rule (range, format, referential integrity, uniqueness) used in data quality checks.

### Data Monitoring Metric
Pipeline health metric (throughput, latency, error_rate, data_volume) for observability and alerting.

### Data Pipeline Template
Reusable pipeline pattern (CDC ingestion, batch ETL, streaming aggregation) for rapid development.

---

## Pattern Maturity Levels

| Maturity | Description | Complexity | Examples |
|----------|-------------|------------|----------|
| **Foundational** | Core, widely applicable, low complexity | Low | `pat-idempotent`, `pat-retryable`, `pat-partitioning` |
| **Intermediate** | Requires infrastructure or moderate expertise | Medium | `pat-scd-type2`, `pat-cdc-log-based`, `pat-medallion-lakehouse` |
| **Advanced** | Complex, niche, deep understanding required | High | `pat-outbox-cdc`, `pat-lambda-architecture`, `pat-exactly-once` |

---

## Common Validation Errors & Fixes

### Error: Invalid UID Format

**Problem**: `✗ Invalid pipeline ID format: 'ingest_orders'`

**Fix**:
```yaml
# ❌ WRONG
pipelines:
  - id: ingest_orders

# ✅ CORRECT
pipelines:
  - id: pip-ingest-orders
```

### Error: Cross-Reference Not Found

**Problem**: `✗ Stage references non-existent dataset: 'ds-orders-unknown'`

**Fix**:
```yaml
# ❌ WRONG
stages:
  - id: stg-transform
    inputs: [ds-orders-unknown]

# ✅ CORRECT
datasets:
  - id: ds-orders-bronze  # Define first

stages:
  - id: stg-transform
    inputs: [ds-orders-bronze]  # Then reference
```

### Warning: PII Dataset Missing Retention

**Problem**: `⚠ Dataset ds-customer-pii has no retention policy`

**Fix**:
```yaml
# ❌ WRONG
datasets:
  - id: ds-customer-pii
    classification: pii

# ✅ CORRECT
datasets:
  - id: ds-customer-pii
    classification: pii

governance:
  retention:
    - dataset: ds-customer-pii
      policy: delete-after-days
      days: 90
```

### Warning: Streaming Pipeline Not Idempotent

**Problem**: `⚠ Streaming pipeline lacks idempotent patterns`

**Fix**:
```yaml
# ❌ WRONG
pipelines:
  - id: pip-stream-orders
    mode: streaming

# ✅ CORRECT
pipelines:
  - id: pip-stream-orders
    mode: streaming
    stages:
      - id: stg-ingest
        uses_patterns:
          - pat-idempotent
          - pat-exactly-once
```

---

## Useful Commands

### Search Patterns

```bash
# Find pattern by name
grep -r "cdc" taxonomy/02-taxonomy-index.yaml

# Search pattern files
grep -r "exactly-once" taxonomy/patterns/

# Find patterns by tag
yq eval '.patterns[].[] | select(.tags[] | contains("streaming"))' \
  taxonomy/02-taxonomy-index.yaml
```

### Validate Models

```bash
# Validate single model
python model/validators/validate.py --model model.yaml

# Validate all examples
for f in model/examples/*/model.example.yaml; do
  python model/validators/validate.py --model "$f" --strict
done

# Run full test suite
python tools/scripts/run_all_validations.py
```

### Check UID Uniqueness

```bash
# Extract all UIDs
grep -r "^  - id:" model/ taxonomy/ | \
  awk '{print $NF}' | \
  sort | \
  uniq -d

# Find UID references
grep -r "pat-cdc-outbox" model/ taxonomy/
```

### Generate Reports

```bash
# JSON validation report
python model/validators/validate.py \
  --model model.yaml \
  --output json > report.json

# JUnit XML for CI
python model/validators/validate.py \
  --model model.yaml \
  --output junit > results.xml
```

---

## CI/CD Integration Snippets

### GitHub Actions

```yaml
- name: Validate Models
  run: |
    python model/validators/validate.py \
      --model model/examples/retail/model.example.yaml \
      --strict \
      --output json > validation-report.json
```

### GitLab CI

```yaml
validate:
  script:
    - python model/validators/validate.py --model model.yaml --strict --output junit > results.xml
  artifacts:
    reports:
      junit: results.xml
```

### Pre-Commit Hook

```bash
#!/bin/bash
MODELS=$(git diff --cached --name-only | grep 'model.*\.yaml$')
for model in $MODELS; do
  python model/validators/validate.py --model "$model" --strict || exit 1
done
```

---

## Quick Navigation

- **Home**: [README.md](/README.md)
- **Contributing**: [CONTRIBUTING.md](/CONTRIBUTING.md)
- **Vision**: [/docs/10-vision.md](/docs/10-vision.md)
- **Governance**: [/docs/40-governance-and-rfc.md](/docs/40-governance-and-rfc.md)
- **Pattern Authoring**: [/docs/60-how-to-author-patterns.md](/docs/60-how-to-author-patterns.md)
- **System Modeling**: [/docs/70-how-to-model-systems.md](/docs/70-how-to-model-systems.md)
- **Conformance Guide**: [/docs/50-conformance-guide.md](/docs/50-conformance-guide.md)
- **Glossary**: [/taxonomy/01-glossary.md](/taxonomy/01-glossary.md)
- **Test Suite**: [/tests/README.md](/tests/README.md)
- **FAQ**: [/docs/90-faq.md](/docs/90-faq.md)

---

## Version

**Quick Reference Version**: 0.1.0
**Last Updated**: 2025-10-08

---

**Need more details?** See full documentation in [/docs/](/docs/).
