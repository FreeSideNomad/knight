---
version: 0.2.0
status: draft
owners: [data-platform-team]
last_updated: 2025-10-09
source_of_truth: /docs/20-scope-and-nfrs.md
---

# Scope & Non-Functional Requirements

## In Scope

### Taxonomy
- **Data Engineering Patterns**: Ingestion (batch, streaming, CDC), transformation (windowing, joins, SCD), storage (medallion, lakehouse, partitioning), serving (marts, feature stores, CQRS).
- **Cross-Cutting Concerns**: Governance (contracts, retention, classification), observability (freshness, drift, lineage), reliability (idempotency, exactly-once, backfill), cost optimization (tiering, sampling, autoscaling).
- **Anti-Patterns**: Dual writes, schema-on-read abuse, missing DLQs, silent drift, global mutable tables.
- **Vendor-Neutral Language**: Patterns described abstractly; vendor specifics in `x-*` extensions.

### Logical Model
- **Core Entities**: System, Domain, Pipeline, Stage, Transform, Dataset, Contract, Check, Lineage, Schedule, Orchestration, Runtime, Governance, Observability.
- **Extensibility**: Traits, tags, `x-*` vendor extensions.
- **Format**: YAML with JSON-Schema (2020-12) validation.
- **ID Convention**: `{type_code}-{kebab-name}` (e.g., `sys-transaction-summary`, `pip-ingest-orders`).

### Conformance
- **Rule Packs**: Reliability, governance, observability, contracts, patterns.
- **Validator**: Python script that validates instances against schema and rules; CI-ready with exit codes.
- **Negative Test Coverage**: Examples of common violations with expected diagnostics.

### Documentation
- **Authoring Guides**: How to define new patterns; how to model systems from scratch.
- **Governance**: UID scheme, versioning, deprecation policy, RFC process.
- **Conformance Guide**: Interpreting validation outputs, remediation steps.
- **Diagrams**: How to generate PlantUML/Graphviz from model YAML.

### Tooling
- **Validator**: `validate.py` for schema and rule checking.
- **Scripts**: Link checker, diagram generator stubs, CI wrapper.
- **Linters**: `.editorconfig` for consistency.

## Out of Scope

- **Implementation Code**: This project does NOT provide executable pipeline code (Spark jobs, SQL scripts, dbt models). It provides the *model* and *patterns* to describe them.
- **Vendor-Specific Solutions**: No Databricks-specific, Snowflake-specific, or dbt-specific constructs in core schema (use `x-*` extensions for these).
- **Operational Tooling**: No monitoring dashboards, alerting systems, or orchestration engines—only the schema to describe them.
- **Data Quality Execution**: The schema models *checks* and *rules*, but does not execute them (integrate with Great Expectations, Soda, etc. externally).
- **Machine Learning Pipelines**: ML-specific patterns (training, inference, drift detection) are mentioned where they intersect with data engineering (e.g., feature store materialization) but are not the primary focus.
- **Real-Time Stream Processing Internals**: Windowing and watermarking are covered at a pattern level, but low-level stream processing mechanics (Flink state backends, Kafka Streams internals) are out of scope.

## Assumptions

1. **Target Audience**: Intermediate to senior data engineers and architects familiar with data pipeline concepts.
2. **Technology Landscape**: Users work with modern data stacks (cloud storage, lakehouse formats, streaming engines, orchestrators).
3. **YAML/JSON Familiarity**: Users can read and author YAML; JSON-Schema is a plus but not required.
4. **Version Control**: All artifacts live in Git; changes follow PR-based review.
5. **CI/CD Integration**: Teams have CI pipelines where validation can run (GitHub Actions, GitLab CI, Jenkins, etc.).

## Non-Functional Requirements

### NFR-1: Consistency
- **UID Uniqueness**: All `uid` fields must be globally unique within the repository.
- **Schema Compliance**: All YAML files must validate against their respective schemas.
- **Cross-Reference Integrity**: References by UID (e.g., `uses_patterns`, `lineage.upstream_refs`) must resolve to existing entries.

### NFR-2: Evolvability
- **Versioning**: Schema and taxonomy follow semantic versioning (MAJOR.MINOR.PATCH).
- **Backward Compatibility**: MINOR/PATCH changes must not break existing valid instances.
- **Deprecation Policy**: Deprecated fields/patterns remain supported for one MAJOR version with warnings.
- **Extension Points**: `x-*` fields and `traits` allow customization without schema changes.

### NFR-3: Tool-Agnostic
- **Neutral Vocabulary**: Patterns describe concepts (e.g., "upsert/merge") abstractly, not tied to Spark, Flink, or dbt.
- **Vendor Extensions**: Vendor-specific details live under `x-databricks`, `x-snowflake`, `x-dbt`, etc.
- **Portability**: A model instance should be translatable across execution engines (with tool-specific adapters).

### NFR-4: Human & Machine Readable
- **YAML Format**: Primary format is YAML (indentation-based, comments supported).
- **JSON-Schema**: Machine validation via JSON-Schema (2020-12).
- **Documentation as Code**: Patterns and examples are self-documenting; code and docs stay in sync.

### NFR-5: Composability
- **Small Files**: Prefer many focused files over monolithic documents.
- **Modular Patterns**: Patterns can be composed (e.g., "CDC + exactly-once + DLQ").
- **Reusable Definitions**: `$defs` in schema enable reuse (e.g., `datasetRef`, `check`).

### NFR-6: Testability
- **Golden Examples**: Known-good instances serve as regression tests.
- **Negative Tests**: Known-bad instances validate error messages and diagnostics.
- **CI Integration**: Validator returns exit codes (0 = pass, non-zero = fail).

### NFR-7: Accessibility
- **Onboarding**: Glossary, FAQ, and authoring guides reduce time-to-productivity.
- **Examples**: 4+ end-to-end realistic examples (retail, payments, IoT, ML features).
- **Quickstart**: Copy-paste commands in README to validate examples immediately.

## Portability Matrix (Non-Normative)

The following table shows conceptual mappings between patterns and execution engines. These are **informational only** and not part of the formal schema.

| Pattern                 | Spark                  | Flink                    | dbt                     | DuckDB              |
|-------------------------|------------------------|--------------------------|-------------------------|---------------------|
| Micro-batch ingestion   | `foreachBatch`         | Bounded source           | Incremental models      | COPY + schedule     |
| Windowed aggregation    | `groupBy` + `window`   | `TumblingWindow`         | N/A (batch)             | `window` functions  |
| SCD Type 2              | Merge + rank           | Temporal joins           | Snapshots + macros      | Merge + window      |
| Upsert/Merge            | `MERGE INTO` (Delta)   | `INSERT ... ON CONFLICT` | `{{ dbt_utils.merge }}` | `INSERT OR REPLACE` |
| Exactly-once            | Idempotent writes      | Checkpointing            | Run-level dedup         | Transaction         |
| Compaction              | `OPTIMIZE` (Delta)     | Manual or async          | N/A                     | `VACUUM`            |

**Note**: Vendor-specific implementations belong in `x-*` extensions or separate adapter docs.

---

**Next**: See `/docs/30-architecture.md` for high-level design and `/taxonomy/00-overview.md` for taxonomy structure.
