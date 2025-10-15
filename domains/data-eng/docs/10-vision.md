---
version: 0.2.0
status: draft
owners: [data-platform-team]
last_updated: 2025-10-09
source_of_truth: /docs/10-vision.md
---

# Vision — Data Engineering Taxonomy & Logical Model

## Problem Statement

Modern data engineering organizations face several critical challenges:

1. **Fragmentation**: Teams build pipelines using disparate patterns without a shared vocabulary, leading to reinvention, inconsistency, and knowledge silos.

2. **Lack of Formalism**: Data systems are often documented informally (diagrams, wikis, tribal knowledge), making it difficult to enforce standards, automate validation, or understand system-wide dependencies.

3. **Tool Lock-In**: Architecture decisions become tightly coupled to specific vendor solutions (Spark, Airflow, dbt, etc.), making it hard to reason about patterns in a tool-agnostic way or migrate between technologies.

4. **Quality & Governance Gaps**: Without machine-readable contracts and conformance rules, quality checks, governance policies, and observability practices are inconsistently applied or entirely missing.

5. **Onboarding Friction**: New engineers struggle to understand the "why" behind existing patterns, leading to anti-patterns, redundant work, and technical debt.

## Objectives

This project delivers:

1. **Comprehensive Taxonomy**: A curated catalog of 45+ data engineering patterns and anti-patterns across ingestion, transformation, storage, serving, governance, observability, reliability, and cost optimization—each with precise definitions, tradeoffs, applicability criteria, and cross-references.

2. **Logical Model Schema**: A vendor-neutral, machine-readable schema (YAML/JSON-Schema) for describing data systems holistically—capturing pipelines, datasets, contracts, lineage, checks, and orchestration in a consistent, evolvable format.

3. **Conformance Framework**: A set of validation rules and checks that enforce best practices (idempotency, data contracts, retention policies, freshness SLAs, etc.) and flag anti-patterns automatically.

4. **Executable Documentation**: Examples, tests, and tooling that make the taxonomy and model immediately usable in CI/CD pipelines, architecture reviews, and day-to-day engineering workflows.

## Personas & Use Cases

### 1. Data Platform Engineer
- **Needs**: Reusable patterns for common problems (CDC, SCD2, backfill, DLQ handling).
- **Uses**: Taxonomy as reference when designing new pipelines; model schema to document systems formally.

### 2. Data Architect
- **Needs**: Tool-agnostic design language to compare solutions, enforce standards, and guide technology selection.
- **Uses**: Taxonomy for architecture decision records (ADRs); conformance rules in design reviews.

### 3. Data Governance Lead
- **Needs**: Automated enforcement of retention, PII classification, access controls, and schema evolution policies.
- **Uses**: Model schema + conformance rules to audit compliance; validator in CI gates.

### 4. SRE / Observability Engineer
- **Needs**: Standard metrics, alerting patterns, and SLAs for data freshness, completeness, and drift.
- **Uses**: Observability patterns in taxonomy; checks section of model schema to codify SLOs.

### 5. New Hire / Junior Engineer
- **Needs**: Clear, teachable explanations of why systems are built a certain way; templates to follow.
- **Uses**: Glossary, pattern docs, and example instances to learn conventions quickly.

### 6. LLM Agent (Code Generation & Collaboration)
- **Needs**: Structured, machine-readable specifications to generate correct, conformant data engineering code; precise requirements to collaborate effectively with data engineers.
- **Uses**:
  - Logical model schema as a contract for generating pipeline definitions, dataset specs, and conformance checks.
  - Taxonomy to select appropriate patterns based on stated requirements (e.g., "implement CDC with exactly-once semantics" → `pat-cdc-outbox` + `pat-exactly-once` + `pat-idempotent`).
  - Conformance rules to validate generated artifacts before presenting to engineers, ensuring compliance with governance, observability, and reliability requirements.
  - Examples and glossary to ground natural language requirements into formal YAML models.
  - UID scheme to generate consistent, traceable identifiers for systems, pipelines, and datasets.
- **Workflow**: Engineer provides high-level requirements → LLM queries taxonomy for applicable patterns → LLM generates model YAML → LLM validates against schema and rules → Engineer reviews and refines → LLM iterates based on feedback.

## Success Criteria

1. **Coverage**: Taxonomy includes ≥45 patterns and ≥8 anti-patterns covering the full data lifecycle.

2. **Formalism**: Logical model schema is JSON-Schema compliant, versioned, and supports 4+ realistic end-to-end examples.

3. **Validation**: Conformance validator runs successfully in CI, catching 10+ common anti-patterns and missing governance requirements.

4. **Adoption-Ready**: Documentation is actionable, with quickstart commands, authoring guides, and FAQ. All examples validate cleanly.

5. **Extensibility**: Schema supports `x-*` extensions and trait composition, allowing teams to add vendor-specific or domain-specific elements without breaking the core model.

6. **Community**: Governance policy and RFC process defined, enabling contributions, deprecation, and evolution over time.

## Vision Statement

> We envision a world where data engineering patterns are as well-understood and rigorously modeled as software design patterns—where teams share a common language, systems are self-describing, and quality is enforced by design rather than retrofitted through manual audits.

---

**Next**: See `/docs/20-scope-and-nfrs.md` for detailed scope boundaries and non-functional requirements.
