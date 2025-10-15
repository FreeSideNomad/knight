---
version: 0.2.0
status: draft
owners: [data-platform-team]
last_updated: 2025-10-09
source_of_truth: /docs/40-governance-and-rfc.md
---

# Governance & RFC Process

This document defines the rules and processes for contributing to the taxonomy and logical model, including UID schemes, versioning, deprecation, and the RFC (Request for Comments) workflow.

---

## UID Scheme

All entities in the taxonomy and logical model must have globally unique identifiers (UIDs) following a consistent format.

### Format

```
{type_code}-{kebab-case-name}
```

**Rules**:
- **Type Code**: A 3-4 character prefix indicating the entity type (see table below).
- **Kebab-Case Name**: Lowercase words separated by hyphens, descriptive and concise.
- **Uniqueness**: UIDs must be globally unique within the repository.
- **Stability**: Once assigned, UIDs should NOT change (to preserve cross-references and lineage).

### Type Codes

| Type Code | Entity Type           | Example UID                        |
|-----------|-----------------------|------------------------------------|
| `sys-`    | System                | `sys-order-processing`             |
| `dom-`    | Domain                | `dom-payments`                     |
| `pip-`    | Pipeline              | `pip-ingest-orders`                |
| `stg-`    | Stage                 | `stg-transform-dedupe`             |
| `trf-`    | Transform             | `trf-join-customers`               |
| `ds-`     | Dataset               | `ds-orders-raw`, `ds-orders-silver`|
| `chk-`    | Check                 | `chk-freshness-orders`             |
| `pat-`    | Pattern (Taxonomy)    | `pat-cdc-outbox`, `pat-idempotent` |
| `apn-`    | Anti-Pattern          | `apn-dual-write`, `apn-no-dlq`     |
| `ctr-`    | Contract              | `ctr-orders-v1`                    |
| `lin-`    | Lineage Edge          | `lin-orders-to-enriched`           |
| `sch-`    | Schedule              | `sch-hourly-ingest`                |
| `rul-`    | Conformance Rule Pack | `rul-governance`, `rul-reliability`|

### Examples

- System: `sys-transaction-summary-pipeline`
- Pipeline: `pip-ingest-customer-events`
- Dataset: `ds-customer-silver`
- Pattern: `pat-medallion-lakehouse`
- Anti-Pattern: `apn-silent-drift`
- Check: `chk-completeness-orders`
- Contract: `ctr-payments-api-v2`

### Validation

The validator (`/model/validators/validate.py`) will:
1. Check UID format matches `{type_code}-{kebab-case}` regex.
2. Verify type code matches entity type.
3. Ensure global uniqueness (no duplicate UIDs across all files).

---

## Versioning

We follow **Semantic Versioning (SemVer)** for all artifacts:

```
MAJOR.MINOR.PATCH
```

- **MAJOR**: Breaking changes (e.g., removing required fields, changing UID format).
- **MINOR**: Backward-compatible additions (e.g., new patterns, new optional fields).
- **PATCH**: Backward-compatible fixes (e.g., typos, clarifications, bug fixes in validators).

### Version Fields

Every file includes front-matter with:
```yaml
version: 0.2.0
status: draft|review|final
last_updated: 2025-10-09
```

### Version Bumps

- **Pattern/Schema Changes**: Increment MINOR or MAJOR as appropriate.
- **Documentation Updates**: Increment PATCH.
- **New Examples**: Increment MINOR.

### Compatibility Matrix

| Change Type                         | MAJOR | MINOR | PATCH |
|-------------------------------------|-------|-------|-------|
| Add new pattern                     |       | ✓     |       |
| Add new optional field to schema    |       | ✓     |       |
| Remove or rename required field     | ✓     |       |       |
| Change UID format                   | ✓     |       |       |
| Fix typo in docs                    |       |       | ✓     |
| Add new conformance rule            |       | ✓     |       |
| Deprecate pattern (with migration)  |       | ✓     |       |
| Remove deprecated pattern           | ✓     |       |       |

---

## Deprecation Policy

### Deprecation Lifecycle

1. **Mark as Deprecated** (MINOR version):
   - Add `deprecated: true` field to pattern/schema.
   - Add `deprecation_reason` and `migration_path` fields.
   - Update `status` to `deprecated`.
   - Announce in `CHANGELOG.md`.

2. **Support Window** (1 MAJOR version):
   - Deprecated items remain valid but emit warnings in validator.
   - Documentation updated with "DEPRECATED" notices.

3. **Removal** (Next MAJOR version):
   - Fully remove deprecated items.
   - Validator rejects instances using removed UIDs.
   - Migration guide published in `/docs/41-deprecation-policy.md`.

### Example Deprecation

```yaml
# /taxonomy/patterns/core/old-pattern.yaml
uid: pat-old-pattern
name: Old Pattern
status: deprecated
deprecated: true
deprecation_reason: "Superseded by pat-new-pattern which provides better performance."
migration_path: "Replace uses_patterns: [pat-old-pattern] with [pat-new-pattern]."
deprecated_since: 0.3.0
removal_planned: 1.0.0
```

---

## RFC (Request for Comments) Process

All significant changes to the taxonomy or schema require an RFC.

### When to Use RFC

- Adding a new pattern category.
- Changing the logical model schema (new entity types, required fields).
- Modifying UID scheme rules.
- Introducing breaking changes.
- Deprecating widely-used patterns.

### RFC Workflow

1. **Draft RFC**:
   - Create a new file: `/docs/rfcs/rfc-NNNN-title.md`.
   - Include: problem statement, proposed solution, alternatives considered, impact analysis, migration plan.
   - Assign UID: `rfc-NNNN`.

2. **Discussion**:
   - Open a GitHub Pull Request or discussion thread.
   - Tag relevant stakeholders (architects, governance leads, platform engineers).
   - Allow 1-2 weeks for feedback.

3. **Decision**:
   - **Accepted**: RFC is merged, implementation proceeds.
   - **Rejected**: RFC is closed with rationale documented.
   - **Deferred**: RFC is tabled for future consideration.

4. **Implementation**:
   - Create implementation PR referencing the RFC.
   - Update `CHANGELOG.md` with RFC number and summary.
   - Increment version appropriately.

5. **Announcement**:
   - Communicate changes via team channels, docs, and release notes.

### RFC Template

```markdown
---
rfc: rfc-NNNN
title: Brief Title
status: draft|accepted|rejected|deferred
authors: [name1, name2]
created: YYYY-MM-DD
---

# RFC-NNNN: Title

## Problem Statement
What problem are we solving?

## Proposed Solution
Detailed description of the change.

## Alternatives Considered
What other approaches were evaluated?

## Impact Analysis
- Who is affected?
- Breaking changes?
- Migration effort?

## Migration Plan
Step-by-step guide for users to adapt.

## Open Questions
TBD items for discussion.
```

---

## Contribution Checklist

Before submitting a PR for patterns, schema, or examples:

- [ ] UID follows `{type_code}-{kebab-name}` format.
- [ ] UID is globally unique (checked via `/tools/scripts/check_links.py`).
- [ ] Version incremented appropriately (MAJOR/MINOR/PATCH).
- [ ] `status` field set to `draft` (will move to `review` → `final`).
- [ ] Cross-references (e.g., `uses_patterns`, `related_patterns`) resolve to existing UIDs.
- [ ] File validates against schema (`validate.py` passes).
- [ ] Documentation updated (if adding new pattern category or entity type).
- [ ] `CHANGELOG.md` updated with summary of changes.
- [ ] RFC filed if change is significant.

---

## Review Gates

### Draft → Review
- Pattern file complete with all required fields.
- At least one `known_uses` example.
- Validator passes.

### Review → Final
- Peer review by 2+ team members.
- No unresolved comments.
- Examples tested in realistic scenarios.
- Cross-links verified.

---

## Governance Roles

| Role                    | Responsibilities                                                                 |
|-------------------------|----------------------------------------------------------------------------------|
| **Pattern Author**      | Drafts new patterns, provides examples, responds to review feedback.             |
| **Reviewer**            | Validates correctness, applicability, and clarity of patterns.                   |
| **Schema Maintainer**   | Manages logical model schema, ensures backward compatibility.                    |
| **Governance Lead**     | Approves RFCs, enforces deprecation policy, resolves disputes.                   |
| **Validator Developer** | Maintains validation tooling, implements conformance rules.                      |

---

## Tools for Governance

- **Link Checker**: `/tools/scripts/check_links.py` verifies UID references.
- **Validator**: `/model/validators/validate.py` checks schema compliance and rules.
- **Changelog Generator**: Auto-generates release notes from git commits and tags.

---

**Next**: See `/docs/50-conformance-guide.md` for how to interpret validation outputs and remediate issues.
