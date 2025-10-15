---
version: 0.2.0
status: review
owners: [data-platform-team]
last_updated: 2025-10-09
source_of_truth: /docs/30-architecture.md
---

# Architecture Overview

This document describes the high-level architecture of the Data Engineering Taxonomy & Logical Model project, including the relationships between taxonomy, model schema, conformance rules, and validation tooling.

---

## System Components

The project consists of four primary components:

```
┌────────────────────────────────────────────────────────────────┐
│                         Repository                             │
│                                                                │
│  ┌─────────────────┐  ┌──────────────────┐  ┌──────────────┐   │
│  │   Taxonomy      │  │  Logical Model   │  │  Conformance |   │
│  │   (Patterns)    │◄─┤     Schema       │─►│     Rules    │   │
│  │                 │  │   (JSON-Schema)  │  │              │   │
│  └────────┬────────┘  └────────┬─────────┘  └───────┬──────┘   │
│           │                    │                    │          │
│           │                    │                    │          │
│           └────────────────────┼────────────────────┘          │
│                                │                               │
│                                ▼                               │
│                    ┌───────────────────────┐                   │
│                    │      Validator        │                   │
│                    │  (validate.py + CI)   │                   │
│                    └───────────────────────┘                   │
│                                |                               │
│                                ▼                               │
│                    ┌───────────────────────┐                   │
│                    │   Model Instances     │                   │
│                    │  (Examples, Systems)  │                   │
│                    └───────────────────────┘                   │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 1. Taxonomy (Patterns & Anti-Patterns)

**Location**: `/taxonomy/`

**Purpose**: A catalog of reusable data engineering patterns and anti-patterns.

**Contents**:
- **Patterns** (`/patterns/{category}/*.yaml`): 45+ proven solutions across 9 categories.
- **Anti-Patterns** (`/anti-patterns/*.yaml`): 8+ documented pitfalls to avoid.
- **Index** (`02-taxonomy-index.yaml`): Master registry with UIDs, tags, maturity levels.
- **Glossary** (`01-glossary.md`): 50+ precise term definitions.
- **References** (`references.md`): Citations and external sources.

**Key Features**:
- **UID-Based**: Every pattern has a unique ID (e.g., `pat-cdc-outbox`).
- **Composable**: Patterns reference each other via `related_patterns` and `required_traits`.
- **Metadata-Rich**: Tags, maturity levels, applicability criteria, known uses.
- **Machine-Readable**: YAML format enables programmatic querying.

**Use Cases**:
- Engineers browse patterns to solve design problems.
- LLM agents query patterns by tag/category to suggest solutions.
- Conformance rules reference patterns to enforce best practices.

---

## 2. Logical Model Schema

**Location**: `/model/model.schema.yaml`

**Purpose**: A JSON-Schema (2020-12) specification for describing data systems holistically.

**Entities**:
- **System**: Top-level container with domains, ownership, metadata.
- **Domain**: Logical grouping of pipelines (e.g., `dom-payments`, `dom-inventory`).
- **Pipeline**: Series of stages processing data from sources to sinks.
- **Stage**: Discrete step with inputs, outputs, transforms, and `uses_patterns` links.
- **Transform**: Specific operation (e.g., `filter`, `join`, `upsert`, `scd-type2`).
- **Dataset**: Source or sink with schema, format, location, classification.
- **Contract**: SLA, schema evolution policy, ownership, consumer agreements.
- **Check**: Validation rule (freshness, completeness, drift, anomaly detection).
- **Lineage**: Data flow graph (upstream → downstream relationships).
- **Schedule**: Orchestration triggers (cron, event-driven, continuous).
- **Governance**: Retention policies, access tiers, PII handling.
- **Observability**: Metrics, SLOs, alerts.

**Extensibility**:
- **Traits**: Reusable properties (e.g., `idempotent`, `exactly-once`).
- **Tags**: Free-form labels for categorization.
- **`x-*` Extensions**: Vendor-specific fields (e.g., `x-databricks`, `x-dbt`).

**Validation**:
- All instances must validate against `/model/model.schema.yaml`.
- UID format enforced: `{type_code}-{kebab-name}`.
- Cross-references (datasets, patterns, checks) must resolve.

---

## 3. Conformance Rules

**Location**: `/model/checks/rules/*.yaml`

**Purpose**: Machine-executable rules that enforce best practices and flag violations.

**Rule Packs**:
- **reliability.yaml**: Idempotency, exactly-once, DLQ presence.
- **governance.yaml**: Retention policies, PII classification, contract presence.
- **observability.yaml**: Freshness checks, volume monitoring, drift detection.
- **contracts.yaml**: Schema evolution policies, SLA definitions.
- **patterns.yaml**: Pattern-specific requirements (e.g., if `pat-exactly-once` is used, require `pat-idempotent` and `chk-freshness`).

**Rule Structure**:
```yaml
- rule_id: rul-freshness-required
  description: All datasets must have a freshness check.
  selector:
    entity_type: dataset
    classification: [internal, public]
  assertions:
    - has_check:
        type: freshness
  severity: high
  message: "Dataset {dataset.id} is missing a freshness check. Add a check with type: freshness."
```

**Evaluation**:
- Validator (`validate.py`) loads all rule packs.
- Rules are evaluated over model instances.
- Violations emit diagnostics with severity, message, and remediation hints.

---

## 4. Validator

**Location**: `/model/validators/validate.py`

**Purpose**: Command-line tool to validate model instances against schema and conformance rules.

**Features**:
- **Schema Validation**: Checks JSON-Schema compliance.
- **UID Validation**: Ensures format and uniqueness.
- **Cross-Reference Validation**: Verifies all UIDs resolve.
- **Conformance Rules**: Evaluates rule packs and emits diagnostics.
- **CI Integration**: Returns exit codes (0 = pass, non-zero = fail).
- **Extensible**: Plugins for custom rules and checks.

**Usage**:
```bash
# Validate a single model instance
python /model/validators/validate.py /model/examples/retail/model.example.yaml

# Validate all examples
python /model/validators/validate.py /model/examples/**/*.yaml

# Run in CI
make validate  # Exit code 0 if all pass
```

**Output**:
```
✅ Schema validation: PASS
✅ UID validation: PASS
✅ Cross-reference validation: PASS
⚠️  Conformance rule rul-freshness-required: WARNING
    Dataset ds-orders-silver is missing a freshness check.
    Add a check with type: freshness.
❌ Conformance rule rul-retention-required: FAIL
    Dataset ds-customer-pii has no retention policy.
    Severity: critical

Summary: 1 error, 1 warning, 12 passes
Exit code: 1
```

---

## Data Flow

### Authoring Workflow

```
1. Engineer defines requirements
   ↓
2. Consults taxonomy for applicable patterns
   ↓
3. Authors model instance (YAML)
   ↓
4. Runs validator
   ↓
5. Fixes violations and warnings
   ↓
6. Submits for review
   ↓
7. Model moves to production (status: final)
```

### LLM Agent Workflow

```
1. User provides natural language requirements
   ↓
2. LLM queries taxonomy index by tags/keywords
   ↓
3. LLM selects applicable patterns
   ↓
4. LLM generates model YAML incrementally
   ↓
5. LLM validates via validator API
   ↓
6. LLM presents model + diagnostics to user
   ↓
7. User reviews and refines
   ↓
8. LLM iterates based on feedback
```

---

## Cross-Linking Between Components

### Taxonomy → Model

Patterns are referenced in model instances via `uses_patterns`:

```yaml
# /model/examples/retail/model.example.yaml
stages:
  - id: stg-ingest-orders
    uses_patterns:
      - pat-cdc-outbox
      - pat-idempotent
      - pat-dlq
```

### Model → Conformance Rules

Models are evaluated by rules. Rules reference pattern UIDs to enforce constraints:

```yaml
# /model/checks/rules/patterns.yaml
- rule_id: rul-cdc-requires-idempotent
  selector:
    uses_patterns: [pat-cdc-outbox]
  assertions:
    - uses_patterns_includes: pat-idempotent
  message: "CDC patterns require idempotency. Add pat-idempotent to uses_patterns."
```

### Taxonomy → Conformance Rules

Patterns specify `must_have_checks`:

```yaml
# /taxonomy/patterns/observability/freshness-check.yaml
uid: pat-freshness-check
must_have_checks:
  - chk-freshness
```

Conformance rules enforce this:

```yaml
# /model/checks/rules/observability.yaml
- rule_id: rul-freshness-pattern-requires-check
  selector:
    uses_patterns: [pat-freshness-check]
  assertions:
    - has_check:
        type: freshness
```

---

## Versioning & Evolution

### Schema Versioning

- Follows SemVer (MAJOR.MINOR.PATCH).
- Breaking changes (removing required fields, changing UID format) → MAJOR bump.
- Backward-compatible additions (new entities, optional fields) → MINOR bump.
- Documentation fixes → PATCH bump.

### Deprecation

- Patterns/schema elements marked `deprecated: true` remain valid for 1 MAJOR version.
- Validator emits warnings for deprecated usage.
- Migration guides provided in `/docs/41-deprecation-policy.md`.

### Extensibility

- `x-*` fields allow vendor-specific extensions without breaking core schema.
- Example:
  ```yaml
  dataset:
    id: ds-orders
    format: delta
    x-databricks:
      optimize_write: true
      auto_compaction: true
  ```

---

## Deployment & CI Integration

### CI Pipeline

```yaml
# .github/workflows/validate.yml
name: Validate Models
on: [push, pull_request]
jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Install dependencies
        run: pip install -r model/validators/requirements.txt
      - name: Run validator
        run: python model/validators/validate.py model/examples/**/*.yaml
      - name: Check links
        run: python tools/scripts/check_links.py
```

### Makefile Targets

```makefile
# /tools/makefile.md (pseudo-code)
validate:
  python model/validators/validate.py model/examples/**/*.yaml

test:
  pytest tests/

build-docs:
  mkdocs build

lint:
  yamllint taxonomy/ model/

check-links:
  python tools/scripts/check_links.py
```

---

## Validator Flow: Taxonomy → Model → Rules → Validation

### Overview

The validator provides automated quality gates for model instances, ensuring they conform to:
1. **Structural correctness** (JSON Schema)
2. **Naming conventions** (UID format)
3. **Reference integrity** (cross-references)
4. **Best practices** (conformance rules)

### Validation Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     VALIDATION PIPELINE                          │
└─────────────────────────────────────────────────────────────────┘

INPUT: model.yaml
    │
    ├─► [1] Load & Parse YAML
    │       └─► Syntax validation
    │
    ├─► [2] JSON Schema Validation
    │       ├─► Load /model/model.schema.yaml
    │       ├─► Validate structure, types, required fields
    │       └─► Validate enum values, patterns
    │
    ├─► [3] UID Format Validation
    │       ├─► Extract all entity UIDs (sys-, dom-, pip-, ds-, etc.)
    │       ├─► Validate format: ^{type-code}-[a-z0-9-]+$
    │       └─► Check uniqueness (no duplicates)
    │
    ├─► [4] Cross-Reference Resolution
    │       ├─► Build UID registry (all entities)
    │       ├─► Resolve stage.inputs → dataset UIDs
    │       ├─► Resolve stage.outputs → dataset UIDs
    │       ├─► Resolve lineage.upstream/downstream → dataset UIDs
    │       ├─► Resolve contract.dataset → dataset UID
    │       └─► Resolve check.dataset → dataset UID
    │
    ├─► [5] Load Conformance Rules
    │       ├─► Scan /model/checks/rules/*.yaml
    │       ├─► Load all rule packs (reliability, governance, etc.)
    │       └─► Filter enabled rules
    │
    ├─► [6] Evaluate Conformance Rules
    │       │
    │       ├─► FOR EACH RULE:
    │       │       ├─► Apply selector (JSONPath) → find matching entities
    │       │       ├─► FOR EACH MATCHED ENTITY:
    │       │       │       ├─► Evaluate conditions
    │       │       │       ├─► If condition fails → record violation
    │       │       │       └─► Severity: ERROR | WARNING | SUGGESTION
    │       │       └─► Collect all violations
    │       │
    │       └─► Examples:
    │           ├─► rul-retention-required:
    │           │       Selector: $.datasets[?(@.classification == 'pii')]
    │           │       Condition: required_field(governance.retention_policy)
    │           │       Message: "PII dataset {id} missing retention policy"
    │           │
    │           └─► rul-streaming-dlq-required:
    │                   Selector: $.stages[?(@.mode == 'streaming')]
    │                   Condition: required_field(dlq_config)
    │                   Message: "Streaming stage {id} missing DLQ config"
    │
    ├─► [7] Aggregate Results
    │       ├─► Count: errors, warnings, suggestions, passes
    │       ├─► Determine exit code:
    │       │       └─► 0=success, 1=errors, 2=warnings (non-strict)
    │       └─► Format output: text | json | junit
    │
    └─► [8] Output Results
            ├─► Text: Human-readable report
            ├─► JSON: Machine-parseable for CI
            └─► JUnit XML: Test result format

EXIT CODE: 0 (pass) | 1 (fail) | 2 (warnings)
```

### How Rules Reference Taxonomy by UID

Conformance rules enforce pattern-specific requirements by referencing pattern UIDs from the taxonomy:

**Example 1: Pattern-Specific Rule**

```yaml
# /model/checks/rules/patterns.yaml
- rule_id: rul-cdc-requires-idempotent
  description: CDC patterns require idempotent transformations
  severity: ERROR
  enabled: true
  selector: $.stages[?(@.uses_patterns contains 'pat-cdc-outbox')]
  conditions:
    - type: array_contains
      field: uses_patterns
      value: pat-idempotent
  message: |
    Stage '{id}' uses CDC pattern but is missing pat-idempotent.
    CDC ingestion must be idempotent to handle replay safely.
    Add 'pat-idempotent' to uses_patterns.
```

**Flow:**
1. Taxonomy defines `pat-cdc-outbox` with metadata: `requires: [pat-idempotent]`
2. Rule references pattern UID `pat-cdc-outbox` in selector
3. Validator finds all stages using this pattern
4. Rule checks if `pat-idempotent` is also present
5. If not → ERROR with actionable message

**Example 2: Multi-Pattern Dependency**

```yaml
# /model/checks/rules/patterns.yaml
- rule_id: rul-exactly-once-requires-checkpoint
  description: Exactly-once patterns require checkpoint configuration
  severity: ERROR
  selector: $.stages[?(@.uses_patterns contains 'pat-exactly-once')]
  conditions:
    - type: required_field
      field: checkpoint_config
  message: |
    Stage '{id}' claims exactly-once guarantee but has no checkpoint_config.
    Add checkpoint_config with location and interval.
```

**UID Cross-Reference Map:**

```
Taxonomy UIDs              Model Instance UIDs          Conformance Rules
─────────────────────────  ───────────────────────────  ──────────────────────
pat-cdc-outbox        ───► stage.uses_patterns    ◄─── rul-cdc-requires-idempotent
pat-idempotent        ───► stage.uses_patterns    ◄─── (required by above rule)
pat-exactly-once      ───► stage.uses_patterns    ◄─── rul-exactly-once-requires-checkpoint
pat-dlq               ───► stage.uses_patterns    ◄─── rul-streaming-dlq-required
pat-freshness-check   ───► check.type             ◄─── rul-freshness-required
```

### Validation in CI/CD Pipeline

#### GitHub Actions Example

```yaml
name: Validate Data Models
on:
  pull_request:
    paths:
      - 'model/**/*.yaml'

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'

      - name: Install validator dependencies
        run: |
          cd model/validators
          pip install -r requirements.txt

      - name: Validate all models (strict mode)
        run: |
          python model/validators/validate.py \
            --model model/examples/retail/model.example.yaml \
            --strict \
            --output json > validation-report.json

      - name: Check validation result
        run: |
          exit_code=$?
          if [ $exit_code -eq 0 ]; then
            echo "✅ Validation passed"
          else
            echo "❌ Validation failed with exit code $exit_code"
            cat validation-report.json | jq .
            exit 1
          fi

      - name: Upload validation report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: validation-report
          path: validation-report.json
```

#### Pre-Commit Hook

```bash
#!/bin/bash
# .git/hooks/pre-commit

# Find changed model files
MODELS=$(git diff --cached --name-only --diff-filter=ACM | grep 'model/.*\.yaml$')

if [ -z "$MODELS" ]; then
  echo "No model files changed, skipping validation"
  exit 0
fi

echo "Validating changed model files..."

for model in $MODELS; do
  echo "Validating: $model"
  python model/validators/validate.py --model "$model" --strict --output text

  if [ $? -ne 0 ]; then
    echo "❌ Validation failed for $model"
    echo "Fix errors or use 'git commit --no-verify' to bypass (not recommended)"
    exit 1
  fi
done

echo "✅ All models validated successfully"
exit 0
```

#### Makefile Integration

```makefile
# /tools/makefile.md (pseudo-code)

.PHONY: validate validate-strict validate-all

validate:
	@echo "Validating models..."
	@python model/validators/validate.py \
		--model model/examples/retail/model.example.yaml

validate-strict:
	@echo "Validating models (strict mode)..."
	@python model/validators/validate.py \
		--model model/examples/retail/model.example.yaml \
		--strict

validate-all:
	@echo "Validating all model files..."
	@find model/examples -name "*.yaml" -exec \
		python model/validators/validate.py --model {} --strict \;

ci: validate-strict
	@echo "Running full CI validation suite..."
	@python tools/scripts/run_all_validations.py --strict
```

### Validator Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    validate.py                               │
│                                                              │
│  ┌────────────────┐  ┌─────────────────┐  ┌──────────────┐   │
│  │ Schema         │  │ UID             │  │ Cross-Ref    │   │
│  │ Validator      │  │ Validator       │  │ Validator    │   │
│  └────────┬───────┘  └────────┬────────┘  └──────┬───────┘   │
│           │                   │                  │           │
│           └───────────────────┼──────────────────┘           │
│                               │                              │
│  ┌────────────────────────────▼──────────────────────────┐   │
│  │         Conformance Rules Engine                      │   │
│  │                                                       │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐   │   │
│  │  │ Rule Loader  │  │ JSONPath     │  │ Condition  │   │   │
│  │  │              │  │ Evaluator    │  │ Checker    │   │   │
│  │  └──────────────┘  └──────────────┘  └────────────┘   │   │
│  │                                                       │   │
│  │  Inputs:                                              │   │
│  │    - /model/checks/rules/*.yaml (rule packs)          │   │
│  │    - Model instance (parsed YAML)                     │   │
│  │    - Taxonomy UIDs (for pattern-specific rules)       │   │
│  │                                                       │   │
│  │  Outputs:                                             │   │
│  │    - Violations (ERROR | WARNING | SUGGESTION)        │   │
│  │    - Messages with entity context                     │   │
│  └───────────────────────────────────────────────────────┘   │
│                               │                              │
│  ┌────────────────────────────▼───────────────────────────┐  │
│  │         Report Generator                               │  │
│  │                                                        │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────────┐      │  │
│  │  │   Text   │  │   JSON   │  │   JUnit XML      │      │  │
│  │  │ Formatter│  │ Formatter│  │   Formatter      │      │  │
│  │  └──────────┘  └──────────┘  └──────────────────┘      │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Key Features

1. **Taxonomy-Driven Validation**: Rules reference pattern UIDs from taxonomy
2. **Extensible**: Add new rule packs without modifying validator code
3. **Actionable Feedback**: Each violation includes remediation guidance
4. **CI-Ready**: Multiple output formats and proper exit codes
5. **Incremental Adoption**: Severity levels allow gradual enforcement

---

## Future Enhancements

1. **Code Generation**: Generate Spark/dbt/Airflow code from model instances.
2. **Lineage Visualization**: Render lineage graphs via PlantUML/Graphviz.
3. **Diff Tool**: Compare model versions and highlight changes.
4. **Migration Assistant**: Auto-generate migration code when schemas evolve.
5. **Pattern Recommendation Engine**: Suggest patterns based on model context (ML-powered).
6. **Web UI**: Browse taxonomy and models via interactive dashboard.

---

## 3. Data Products

### What is a Data Product?

A **data_product** is a self-contained, discoverable data asset designed for specific analytical or operational purposes. Data products represent a shift from pipeline-centric thinking to product-centric data delivery.

**Core Properties:**
- **product_id**: Unique identifier (format: `dp-<name>`)
- **name**: Human-readable product name
- **description**: Purpose and value proposition
- **owner**: Accountable team or person (references `owner` entity)
- **datasets**: Constituent datasets that comprise the product
- **bounded_context_ref**: Optional DDD alignment (grounding to `ddd:bounded_context`)
- **sla**: Service-level agreements (freshness, availability, quality)
- **consumers**: Teams or systems consuming this product
- **status**: draft | active | deprecated

**Schema Pattern:**
```yaml
data_product:
  product_id: dp-customer-360
  name: Customer 360 View
  description: Unified customer profile combining CRM, transactions, and behavioral data
  owner: customer-analytics-team
  datasets:
    - ds-customer-profile-gold
    - ds-customer-transactions-gold
    - ds-customer-behavior-gold
  bounded_context_ref: ddd:BoundedContext:customer-management
  sla:
    freshness: 24h
    availability: 99.9%
    quality_target: 95%
  consumers:
    - team: marketing-analytics
      use_case: Customer segmentation
    - team: customer-success
      use_case: Health scoring
  status: active
```

**DDD Grounding:**
```
data_product.bounded_context_ref → ddd:bounded_context
```

Data products map to bounded context boundaries, ensuring data ownership aligns with domain boundaries. This grounding enables domain-driven data mesh architectures.

**Why Data Products Matter:**
- **Ownership**: Clear accountability for data quality and availability
- **Discoverability**: Products are cataloged and searchable
- **SLA-Driven**: Explicit contracts with consumers
- **Domain Alignment**: Maps to DDD bounded contexts
- **Consumer-Focused**: Designed for specific use cases

---

## 4. Access Patterns

### data_access_pattern

**Purpose**: Describes how datasets and pipelines are accessed (read-heavy, write-heavy, mixed, batch, streaming).

**Used in**: `dataset.access_pattern`, `pipeline.access_pattern`

**Schema fields**:
- `type`: read_heavy | write_heavy | mixed | batch | streaming
- `peak_throughput`: Requests/second at peak
- `typical_latency`: p95 response time

**Example**:
```yaml
dataset:
  id: ds-orders-gold
  access_pattern:
    type: read_heavy
    peak_throughput: 5000
    typical_latency: 50ms
```

---

## 5. Cataloging

### data_catalog_entry

**Purpose**: Metadata about datasets for data catalog systems (searchability, tagging, business glossary).

**Used in**: `dataset.catalog_entry`

**Schema fields**:
- `catalog_id`: Unique identifier in catalog system
- `business_name`: Business-friendly name
- `tags`: Searchable keywords
- `glossary_terms`: References to business glossary

**Example**:
```yaml
dataset:
  id: ds-customer-profile
  catalog_entry:
    catalog_id: cat-cust-profile-001
    business_name: Customer Master Profile
    tags: [customer, pii, gold-tier]
    glossary_terms: [customer, profile, demographic]
```

---

## 6. Partitioning

### data_partition_strategy

**Purpose**: Defines how datasets are partitioned for performance and manageability.

**Used in**: `dataset.partition_strategy`

**Schema fields**:
- `type`: time_based | hash_based | range_based | list_based
- `partition_keys`: Fields used for partitioning
- `partition_count`: Number of partitions (for hash/range)

**Example**:
```yaml
dataset:
  id: ds-transactions
  partition_strategy:
    type: time_based
    partition_keys: [transaction_date]
    granularity: daily
```

---

## 7. Replication

### data_replication_config

**Purpose**: Configuration for data replication across regions or systems.

**Used in**: `dataset.replication`, `pipeline.replication`

**Schema fields**:
- `replication_type`: sync | async | active_active
- `target_regions`: List of target regions
- `consistency_model`: strong | eventual | causal

**Example**:
```yaml
dataset:
  id: ds-orders
  replication:
    replication_type: async
    target_regions: [us-west-2, eu-central-1]
    consistency_model: eventual
    lag_tolerance: 5min
```

---

## 8. Retention

### data_retention_tier

**Purpose**: Defines storage tiers for data lifecycle management (hot, warm, cold, archive).

**Used in**: `dataset.retention_policy.tiers`, `governance.retention_tiers`

**Schema fields**:
- `tier_name`: hot | warm | cold | archive
- `duration`: How long data stays in this tier
- `storage_class`: Storage technology (S3 Standard, Glacier, etc.)

**Example**:
```yaml
dataset:
  id: ds-logs
  governance:
    retention_policy:
      tiers:
        - tier_name: hot
          duration: 30d
          storage_class: S3_Standard
        - tier_name: warm
          duration: 90d
          storage_class: S3_IA
        - tier_name: cold
          duration: 365d
          storage_class: S3_Glacier
```

---

**Next**: See `/docs/80-diagrams.md` for how to generate visual diagrams from models, and `/docs/40-governance-and-rfc.md` for contribution and evolution processes.
