---
version: 0.2.0
status: draft
owners: [data-platform-team]
last_updated: 2025-10-09
source_of_truth: /docs/60-how-to-author-patterns.md
---

# How to Author Patterns

This guide provides step-by-step instructions for creating new patterns and anti-patterns in the taxonomy.

---

## Overview

A **pattern** is a reusable solution to a recurring problem in data engineering. Each pattern documents:
- **What** problem it solves.
- **Why** it's the right approach (forces, tradeoffs).
- **How** it works (solution, structure).
- **When** to use it (applicability).
- **What to watch out for** (consequences, metrics).

---

## Prerequisites

Before authoring a pattern:
1. Read `/taxonomy/00-overview.md` to understand taxonomy structure.
2. Read `/docs/40-governance-and-rfc.md` for UID scheme and versioning rules.
3. Familiarize yourself with existing patterns in `/taxonomy/patterns/` to avoid duplication.
4. Identify the appropriate category: `core`, `ingestion`, `transformation`, `storage`, `serving`, `governance`, `observability`, `reliability`, or `cost`.

---

## Step-by-Step Process

### Step 1: Choose a UID

Select a unique identifier following the `pat-{kebab-name}` format.

**Examples**:
- `pat-outbox-cdc`
- `pat-medallion-lakehouse`
- `pat-freshness-check`

**Check uniqueness**: Search `/taxonomy/02-taxonomy-index.yaml` and existing pattern files to ensure no conflicts.

---

### Step 2: Copy the Template

Use `/taxonomy/patterns/_template.yaml` (created in Phase 0) as your starting point:

```bash
cp /taxonomy/patterns/_template.yaml /taxonomy/patterns/{category}/{your-pattern}.yaml
```

---

### Step 3: Fill in Required Fields

#### Front Matter

```yaml
uid: pat-your-pattern
name: Human-Readable Pattern Name
category: core|ingestion|transformation|storage|serving|governance|observability|reliability|cost
version: 0.2.0
status: draft
```

#### Core Content

1. **Intent** (1-2 sentences):
   - What does this pattern achieve?
   - Example: "Enable reliable, exactly-once change data capture from transactional systems to event streams."

2. **Context** (1-2 paragraphs):
   - When and where is this pattern applicable?
   - Example: "In systems that need to propagate database changes to downstream consumers (Kafka, data lake) without dual writes or polling, while ensuring exactly-once delivery..."

3. **Problem** (1-2 paragraphs):
   - What specific challenge does this pattern address?
   - Example: "Directly publishing events from application code leads to dual-write inconsistency..."

4. **Forces** (bullet list):
   - Constraints and considerations that shape the solution.
   - Example:
     ```yaml
     forces:
       - Must maintain transactional consistency between database and event stream.
       - Cannot rely on distributed transactions (2PC) due to complexity/performance.
       - Need to handle failures and retries without duplicates.
     ```

5. **Solution** (2-3 paragraphs):
   - Core mechanism and approach.
   - Example: "The Outbox Pattern writes events to a local 'outbox' table in the same transaction as business entities. A separate poller or CDC connector reads the outbox and publishes to Kafka..."

6. **Structure** (optional, diagram reference or pseudocode):
   - Key components and their relationships.
   - Example:
     ```yaml
     structure:
       - Application writes to `orders` table + `outbox` table in single transaction.
       - CDC connector (Debezium) tails transaction log.
       - Outbox events published to Kafka topic.
       - Outbox rows deleted or archived after publish.
     ```

7. **Consequences**:
   ```yaml
   consequences:
     benefits:
       - Exactly-once semantics via single transaction.
       - No dual-write race conditions.
       - Decouples event publishing from application logic.
     drawbacks:
       - Requires polling or CDC infrastructure.
       - Additional table overhead.
       - Potential lag between commit and event delivery.
   ```

8. **Applicability**:
   ```yaml
   applicability:
     use_when:
       - Transactional system needs to publish events reliably.
       - Dual writes are unacceptable.
       - CDC infrastructure is available (Debezium, etc.).
     avoid_when:
       - Non-transactional source (e.g., file uploads, API logs).
       - Real-time latency requirement < 100ms.
       - System does not support transaction logs.
   ```

9. **Tradeoffs**:
   ```yaml
   tradeoffs:
     - latency: Medium (seconds, not milliseconds).
     - complexity: High (requires CDC connector, schema management).
     - reliability: High (exactly-once guaranteed).
     - cost: Medium (additional storage for outbox table).
   ```

10. **Required Traits** (UIDs of related patterns):
    ```yaml
    required_traits:
      - pat-idempotent
      - pat-retryable
    ```

11. **Must-Have Checks** (UIDs of checks that should be enforced):
    ```yaml
    must_have_checks:
      - chk-freshness-outbox
      - chk-completeness-outbox
    ```

12. **Metrics to Watch**:
    ```yaml
    metrics_to_watch:
      - outbox_lag_seconds
      - outbox_publish_errors
      - outbox_row_count
    ```

13. **Known Uses** (at least one real-world example):
    ```yaml
    known_uses:
      - context: E-commerce order processing system publishing order events to Kafka.
        outcome: Reduced order-event inconsistencies to zero; CDC lag < 5 seconds.
      - context: Payment service with PCI compliance requiring audit trail.
        outcome: Guaranteed event delivery for audit logs; no lost transactions.
    ```

14. **Related Patterns**:
    ```yaml
    related_patterns:
      - uid: pat-cdc-log-based
        relationship: alternative
      - uid: pat-exactly-once
        relationship: complements
      - uid: apn-dual-write
        relationship: solves
    ```

15. **References**:
    ```yaml
    references:
      - title: "Debezium Outbox Pattern"
        url: "https://debezium.io/documentation/reference/transformations/outbox-event-router.html"
        relevance: "Official documentation for implementing outbox with Debezium."
      - title: "Designing Data-Intensive Applications"
        author: "Martin Kleppmann"
        year: 2017
        relevance: "Chapter on distributed transactions and CDC patterns."
    ```

---

### Step 4: Validate

Run the validator to check your pattern file:

```bash
python /model/validators/validate.py /taxonomy/patterns/{category}/{your-pattern}.yaml
```

Expected checks:
- UID format correct.
- UID unique.
- Required fields present.
- Cross-references resolve.

---

### Step 5: Update Taxonomy Index

Add your pattern to `/taxonomy/02-taxonomy-index.yaml`:

```yaml
patterns:
  {category}:
    - uid: pat-your-pattern
      name: Your Pattern Name
      status: draft
      maturity: foundational|intermediate|advanced
      tags: [tag1, tag2, tag3]
      file: patterns/{category}/your-pattern.yaml
```

---

### Step 6: Add Examples (Optional but Recommended)

Create a minimal model instance in `/model/examples/` that uses your pattern:

```yaml
# /model/examples/your-pattern-demo/model.example.yaml
system:
  id: sys-your-pattern-demo
  pipelines:
    - id: pip-demo
      stages:
        - id: stg-demo
          uses_patterns:
            - pat-your-pattern
```

---

### Step 7: Update CHANGELOG

Add an entry to `CHANGELOG.md`:

```markdown
### Added
- Pattern `pat-your-pattern`: {One-sentence description}.
```

---

### Step 8: Submit for Review

1. Create a PR with your pattern file, index update, and changelog entry.
2. Tag reviewers (architects, pattern library maintainers).
3. Address feedback and iterate.
4. Once approved, pattern moves from `status: draft` → `status: review` → `status: final`.

---

## Anti-Pattern Authoring

Anti-patterns follow the same structure but with inverted focus:

- **UID**: `apn-{kebab-name}` (e.g., `apn-dual-write`).
- **Intent**: What harm does this anti-pattern cause?
- **Problem**: Why do engineers fall into this trap?
- **Consequences**: Focus on drawbacks, not benefits.
- **Alternatives**: Reference correct patterns (e.g., "Use `pat-outbox-cdc` instead").

**Template**: `/taxonomy/anti-patterns/_template.yaml`.

---

## Pattern Maturity Levels

Assign a maturity level to help users understand the pattern's complexity:

| Maturity       | Description                                                                 | Example               |
|----------------|-----------------------------------------------------------------------------|-----------------------|
| `foundational` | Core concept, widely applicable, low complexity.                            | `pat-idempotent`      |
| `intermediate` | Requires some infrastructure or expertise.                                  | `pat-scd-type2`       |
| `advanced`     | Complex, niche, or requires deep understanding of distributed systems.      | `pat-outbox-cdc`      |

---

## Tips for Great Patterns

1. **Be Specific**: Avoid vague language like "improves performance." Quantify: "reduces query time by 50%."
2. **Include Tradeoffs**: Honest assessment of costs/complexity builds trust.
3. **Use Real Examples**: `known_uses` should be realistic, not hypothetical.
4. **Link Generously**: Cross-reference related patterns, checks, and anti-patterns.
5. **Keep it Agnostic**: Describe the pattern abstractly; put vendor-specific details in `x-*` extensions or references.
6. **Test Your Pattern**: Validate it against real-world instances before marking `final`.

---

## Common Pitfalls

- **Overgeneralization**: A pattern that solves everything solves nothing. Be narrow and precise.
- **Missing Tradeoffs**: Every pattern has costs. Document them.
- **No Examples**: Without `known_uses`, patterns feel theoretical and untested.
- **Broken Links**: Always verify UID references resolve.
- **Vendor Lock-In**: Avoid embedding Spark/dbt/Airflow-specific syntax in core pattern logic.

---

## Example: Complete Pattern File

```yaml
---
uid: pat-example
name: Example Pattern
category: core
version: 0.2.0
status: draft
---

intent: Demonstrate the structure of a complete pattern file.

context: Used as a reference for pattern authors.

problem: Authors need a concrete example to follow.

forces:
  - Must be clear and concise.
  - Must cover all required fields.
  - Must validate cleanly.

solution: Provide a minimal but complete YAML structure with realistic values.

structure:
  - Component A
  - Component B interacts with A

consequences:
  benefits:
    - Clear reference for authors.
    - Validates successfully.
  drawbacks:
    - Fictional content, not a real pattern.

applicability:
  use_when:
    - Learning to author patterns.
  avoid_when:
    - Looking for production guidance.

tradeoffs:
  - complexity: Low
  - value: Educational only

required_traits:
  - pat-idempotent

must_have_checks:
  - chk-example

metrics_to_watch:
  - example_metric

known_uses:
  - context: Tutorial documentation
    outcome: Helped authors write patterns faster.

related_patterns:
  - uid: pat-other
    relationship: similar

references:
  - title: "Pattern Authoring Guide"
    url: "https://example.com"
    relevance: "This guide itself."
```

---

**Next**: See `/docs/70-how-to-model-systems.md` for guidance on using patterns in full system models, and `/docs/40-governance-and-rfc.md` for the RFC process.
