---
version: 0.2.0
status: final
owners: [data-platform-team]
last_updated: 2025-10-09
---

# Deprecation Policy — Data Engineering Taxonomy & Logical Model

Governance policy for managing changes, deprecations, and migrations across taxonomy patterns, model schema, and conformance rules.

---

## Table of Contents

- [Overview](#overview)
- [Semantic Versioning](#semantic-versioning)
- [Change Classification](#change-classification)
- [Deprecation Workflow](#deprecation-workflow)
- [Migration Guidance](#migration-guidance)
- [Backward Compatibility](#backward-compatibility)
- [Communication Plan](#communication-plan)
- [Tooling Support](#tooling-support)

---

## Overview

### Purpose

This policy ensures that changes to the Data Engineering Taxonomy, Logical Model Schema, and Conformance Rules are:
- **Predictable**: Users know what to expect from version changes
- **Gradual**: Breaking changes are announced with sufficient lead time
- **Documented**: Migration paths are clearly defined
- **Safe**: Production systems are not disrupted by unannounced changes

### Scope

This policy applies to:
- **Taxonomy Patterns** (`/taxonomy/patterns/*.yaml`)
- **Model Schema** (`/model/model.schema.yaml`)
- **Conformance Rules** (`/model/checks/rules/*.yaml`)
- **Glossary** (`/taxonomy/01-glossary.md`)
- **Documentation** (`/docs/*.md`)

### Principles

1. **Backward Compatibility First**: Prioritize non-breaking changes
2. **Deprecate, Don't Delete**: Mark as deprecated before removal
3. **Communicate Early**: Announce deprecations at least one major version in advance
4. **Provide Migration Paths**: Always document how to migrate from deprecated features
5. **Version Everything**: Use semantic versioning for all artifacts

---

## Semantic Versioning

### Version Format

All artifacts follow **Semantic Versioning 2.0.0** (`MAJOR.MINOR.PATCH`):

```
MAJOR.MINOR.PATCH
  │     │     │
  │     │     └─ Bug fixes, documentation updates, non-breaking clarifications
  │     └─────── New patterns, new rules, new features (backward compatible)
  └───────────── Breaking changes (schema changes, removed patterns, incompatible rules)
```

### Version Increments

| Change Type | Version Increment | Examples |
|-------------|-------------------|----------|
| **PATCH** (0.1.0 → 0.1.1) | Bug fixes, typos, clarifications | Fix typo in pattern description, update external reference link |
| **MINOR** (0.1.0 → 0.2.0) | New features, backward compatible | Add new pattern, add new optional field to schema, add new glossary term |
| **MAJOR** (0.1.0 → 1.0.0) | Breaking changes, incompatible | Remove pattern, change required field in schema, change UID format |

### Current Version

**v0.2.0** (as of 2025-10-08)

---

## Change Classification

### Non-Breaking Changes (MINOR or PATCH)

**Allowed without deprecation:**
- Adding new patterns or anti-patterns
- Adding new optional fields to model schema
- Adding new conformance rules (severity: SUGGESTION or WARNING)
- Adding new glossary terms
- Expanding pattern descriptions, examples, or references
- Updating documentation for clarity
- Fixing typos or broken links

**Example:**
```yaml
# v0.1.0
pattern:
  uid: pat-medallion-lakehouse
  name: Medallion Lakehouse Architecture
  # ... existing fields

# v0.2.0 (MINOR bump - new optional field added)
pattern:
  uid: pat-medallion-lakehouse
  name: Medallion Lakehouse Architecture
  related_patterns:  # NEW optional field
    - pat-compaction
    - pat-partitioning-strategies
```

---

### Breaking Changes (MAJOR)

**Require deprecation period:**
- Removing patterns or anti-patterns
- Changing pattern UIDs
- Removing or renaming required fields in model schema
- Changing conformance rule severity from WARNING → ERROR
- Changing UID format conventions
- Removing glossary terms

**Example:**
```yaml
# v0.1.0
pattern:
  uid: pat-old-name
  name: Old Pattern Name
  required_field: value

# v1.0.0 (MAJOR bump - breaking changes)
pattern:
  uid: pat-new-name  # UID changed (breaking!)
  name: New Pattern Name
  required_field_renamed: value  # Field renamed (breaking!)
```

---

## Deprecation Workflow

### Step 1: Mark as Deprecated

Add `deprecated` flag to artifact with deprecation notice:

```yaml
# Pattern deprecation
pattern:
  uid: pat-old-pattern
  name: Old Pattern
  deprecated: true
  deprecated_since: "0.2.0"
  deprecated_message: |
    This pattern is deprecated and will be removed in v1.0.0.
    Use 'pat-new-pattern' instead. Migration guide: /docs/migrations/old-to-new.md
  replacement: pat-new-pattern
```

```yaml
# Schema field deprecation
field:
  old_field_name:
    type: string
    deprecated: true
    description: "[DEPRECATED since v0.2.0] Use 'new_field_name' instead. Will be removed in v1.0.0."
```

### Step 2: Announce Deprecation

Communicate deprecation through:
- **CHANGELOG.md**: Add deprecation notice in release notes
- **GitHub Release Notes**: Highlight deprecated features
- **Documentation**: Update docs with migration examples
- **Validator Warnings**: Emit warnings when deprecated features detected

### Step 3: Maintain During Deprecation Period

During the deprecation period (at least one MAJOR version):
- Keep deprecated artifacts functional
- Validator emits warnings (not errors) for deprecated features
- Documentation clearly marks deprecated features with migration paths

### Step 4: Remove in Next Major Version

After deprecation period:
- Remove deprecated artifacts in next MAJOR version
- Update CHANGELOG with removal notice
- Ensure migration documentation is available

---

## Migration Guidance

### Pattern Migration

**Scenario**: Pattern UID changed from `pat-old-name` to `pat-new-name`

**v0.2.0 (Deprecation Announced):**
```yaml
# Both patterns exist, old one marked deprecated
patterns:
  - uid: pat-old-name  # DEPRECATED
    deprecated: true
    replacement: pat-new-name

  - uid: pat-new-name  # NEW recommended pattern
```

**Migration Steps:**
1. Identify usage: `grep -r "pat-old-name" models/`
2. Update model files to use `pat-new-name`
3. Run validator to ensure no warnings
4. Test model instances

**v1.0.0 (Removal):**
```yaml
# Only new pattern exists
patterns:
  - uid: pat-new-name
```

---

### Schema Field Migration

**Scenario**: Required field renamed from `old_field` to `new_field`

**v0.2.0 (Deprecation Announced):**
```json
{
  "properties": {
    "old_field": {
      "type": "string",
      "deprecated": true,
      "description": "[DEPRECATED] Use 'new_field' instead"
    },
    "new_field": {
      "type": "string",
      "description": "Replacement for 'old_field'"
    }
  },
  "anyOf": [
    {"required": ["old_field"]},
    {"required": ["new_field"]}
  ]
}
```

**Migration Steps:**
1. Update model files to use `new_field` instead of `old_field`
2. Run validator (accepts either field during deprecation period)
3. Verify all instances updated

**v1.0.0 (Removal):**
```json
{
  "properties": {
    "new_field": {
      "type": "string",
      "description": "Replacement for deprecated 'old_field'"
    }
  },
  "required": ["new_field"]
}
```

---

### Conformance Rule Migration

**Scenario**: Rule severity changed from WARNING → ERROR

**v0.2.0 (Deprecation Announced):**
```yaml
rule:
  id: rul-retention-required
  severity: WARNING  # Current severity
  planned_severity_change:
    version: "1.0.0"
    new_severity: ERROR
    reason: "GDPR compliance requirement"
```

**Migration Steps:**
1. Review all models triggering this warning
2. Fix violations before v1.0.0
3. Run validator in `--strict` mode to simulate ERROR behavior
4. Ensure CI passes with strict validation

**v1.0.0 (Breaking Change):**
```yaml
rule:
  id: rul-retention-required
  severity: ERROR  # Now fails validation if violated
```

---

## Backward Compatibility

### Compatibility Matrix

| Version Change | Model v0.1.x | Model v0.2.x | Model v1.0.x |
|----------------|--------------|--------------|--------------|
| **Taxonomy v0.1.x** | ✅ Compatible | ✅ Compatible | ⚠️ May need updates |
| **Taxonomy v0.2.x** | ✅ Compatible | ✅ Compatible | ⚠️ May need updates |
| **Taxonomy v1.0.x** | ❌ Incompatible | ⚠️ May work with warnings | ✅ Compatible |

### Compatibility Guarantees

**Within MINOR versions (0.1.x → 0.2.x):**
- ✅ All existing models remain valid
- ✅ Validator accepts old and new patterns
- ✅ No breaking changes to schema

**Across MAJOR versions (0.x.x → 1.x.x):**
- ⚠️ Deprecated features removed
- ⚠️ Schema may have breaking changes
- ✅ Migration guide provided
- ✅ Validator reports clear errors with migration hints

---

## Communication Plan

### Deprecation Announcement Timeline

| Timeline | Action | Responsible | Deliverable |
|----------|--------|-------------|-------------|
| **T-0** (Deprecation Decision) | Identify deprecated feature | Maintainers | Deprecation proposal |
| **T+1 week** | Review deprecation proposal | Community | Approved deprecation |
| **T+2 weeks** | Add deprecation markers | Maintainers | Updated code with deprecation flags |
| **T+2 weeks** | Publish minor release | Maintainers | Release notes with deprecation notice |
| **T+3 months** | Reminder in release notes | Maintainers | "Reminder: X will be removed in v1.0.0" |
| **T+6 months** (next MAJOR) | Remove deprecated feature | Maintainers | Major release with removal |

### Communication Channels

1. **CHANGELOG.md**: Primary source of truth for all changes
2. **GitHub Releases**: Highlighted deprecation notices
3. **Documentation**: Updated with migration guides
4. **Validator Warnings**: Runtime warnings when deprecated features detected
5. **GitHub Issues**: Announcement issue for major deprecations

### Deprecation Notice Template

```markdown
## Deprecation Notice: [Feature Name]

**Deprecated In**: v0.2.0
**Planned Removal**: v1.0.0
**Replacement**: [New Feature Name]

### Reason
[Why this feature is being deprecated]

### Impact
[Who is affected, what will break]

### Migration Path
1. Step 1
2. Step 2
3. Step 3

### Timeline
- **Now - v0.9.x**: Deprecated, still functional, warnings emitted
- **v1.0.0**: Removed, validator fails if used

### Resources
- Migration Guide: [link]
- Example PR: [link]
- Slack Channel: #data-platform-migrations
```

---

## Tooling Support

### Validator Deprecation Warnings

The validator automatically detects deprecated features:

```bash
python validate.py --model model.yaml
```

**Output:**
```
⚠️  DEPRECATION WARNING:
   Pattern 'pat-old-pattern' is deprecated since v0.2.0.
   Use 'pat-new-pattern' instead.
   This pattern will be removed in v1.0.0.
   Migration guide: /docs/migrations/old-to-new.md
```

### Migration Helper Script

Use the migration helper to automate updates:

```bash
# Find all usage of deprecated patterns
python tools/scripts/find_deprecated.py --model-dir models/

# Automated migration (with review)
python tools/scripts/migrate.py \
  --from pat-old-pattern \
  --to pat-new-pattern \
  --model-dir models/ \
  --dry-run
```

### CI Integration

Fail CI on deprecated features (opt-in):

```yaml
# .github/workflows/validate.yml
- name: Validate (fail on deprecations)
  run: |
    python validate.py --model model.yaml --fail-on-deprecation
```

---

## Examples

### Example 1: Pattern Deprecation

**Scenario**: Renaming `pat-old-cdc` to `pat-cdc-log-based` for clarity.

**v0.2.0 Release (Deprecation):**
```yaml
# /taxonomy/patterns/old-cdc.yaml
uid: pat-old-cdc
name: Old CDC Pattern
deprecated: true
deprecated_since: "0.2.0"
deprecated_message: "Renamed to 'pat-cdc-log-based' for clarity. Will be removed in v1.0.0."
replacement: pat-cdc-log-based

# /taxonomy/patterns/cdc-log-based.yaml
uid: pat-cdc-log-based
name: Log-Based CDC
status: stable
# ... pattern definition
```

**CHANGELOG.md:**
```markdown
## [0.2.0] - 2025-10-08

### Deprecated
- **Pattern `pat-old-cdc` renamed to `pat-cdc-log-based`**
  - Reason: Improve naming clarity and consistency
  - Migration: Replace `pat-old-cdc` with `pat-cdc-log-based` in model files
  - Timeline: `pat-old-cdc` will be removed in v1.0.0
  - See: /docs/migrations/pat-old-cdc-to-cdc-log-based.md
```

**v1.0.0 Release (Removal):**
```yaml
# /taxonomy/patterns/old-cdc.yaml (REMOVED)
# /taxonomy/patterns/cdc-log-based.yaml (RETAINED)
```

---

### Example 2: Schema Field Removal

**Scenario**: Removing deprecated field `old_schedule` in favor of `schedule`.

**v0.2.0 Release (Deprecation):**
```json
{
  "properties": {
    "old_schedule": {
      "type": "string",
      "deprecated": true,
      "description": "[DEPRECATED since v0.2.0] Use 'schedule' instead."
    },
    "schedule": {
      "$ref": "#/$defs/schedule"
    }
  },
  "anyOf": [
    {"required": ["old_schedule"]},
    {"required": ["schedule"]}
  ]
}
```

**v1.0.0 Release (Removal):**
```json
{
  "properties": {
    "schedule": {
      "$ref": "#/$defs/schedule"
    }
  },
  "required": ["schedule"]
}
```

---

### Example 3: Rule Severity Increase

**Scenario**: Changing rule `rul-retention-required` from WARNING to ERROR.

**v0.2.0 Release (Announcement):**
```yaml
rule:
  id: rul-retention-required
  severity: WARNING
  message: |
    PII dataset missing retention policy.

    NOTE: This will become an ERROR in v1.0.0 for GDPR compliance.
    Please add retention policy before upgrading.
```

**v1.0.0 Release (Breaking Change):**
```yaml
rule:
  id: rul-retention-required
  severity: ERROR
  message: "PII dataset missing retention policy (required for GDPR compliance)."
```

---

## Summary

### Key Takeaways

1. **Use Semantic Versioning**: MAJOR.MINOR.PATCH
2. **Deprecate Before Removing**: At least one MAJOR version notice
3. **Communicate Clearly**: CHANGELOG, docs, validator warnings
4. **Provide Migration Paths**: Always document how to upgrade
5. **Test Migrations**: Use validator and CI to catch issues early

### Deprecation Checklist

- [ ] Mark feature as deprecated in code/YAML
- [ ] Add deprecation notice to CHANGELOG.md
- [ ] Update documentation with migration guide
- [ ] Configure validator to emit warnings
- [ ] Create GitHub issue announcing deprecation
- [ ] Wait at least one MAJOR version before removal
- [ ] Remove in next MAJOR version
- [ ] Update CHANGELOG with removal notice

### Getting Help

- **Documentation**: See migration guides in `/docs/migrations/`
- **Questions**: Open GitHub issue or ask in Slack #data-platform
- **Bug Reports**: File issue with "deprecation" label

---

**Version**: 0.2.0
**Status**: Final
**Last Updated**: 2025-10-08
**Maintainers**: Data Platform Team
