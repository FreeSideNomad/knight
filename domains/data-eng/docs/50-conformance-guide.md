---
version: 0.2.0
status: draft
owners: [data-platform-team]
last_updated: 2025-10-09
---

# Conformance Guide — Data Engineering Model Validation

Complete guide to understanding, running, and responding to model validation results.

---

## Table of Contents

- [Overview](#overview)
- [Philosophy](#philosophy)
- [Severity Levels](#severity-levels)
- [Running the Validator](#running-the-validator)
- [Interpreting Output](#interpreting-output)
- [Common Violations & Remediation](#common-violations--remediation)
- [Custom Rules](#custom-rules)
- [CI/CD Integration](#cicd-integration)
- [Troubleshooting](#troubleshooting)

---

## Overview

The **Data Engineering Model Validator** ensures that system models conform to:

1. **JSON Schema** (`/model/model.schema.yaml`) — Structural correctness
2. **UID Conventions** — Naming and uniqueness rules
3. **Cross-References** — Dataset, pipeline, stage, contract integrity
4. **Conformance Rules** (`/model/checks/rules/*.yaml`) — Best practices and anti-pattern detection

**Goal:** Catch errors and anti-patterns before they reach production, promoting consistency, maintainability, and governance compliance.

---

## Philosophy

### Why Conformance Checking?

**Without validation:**
- Broken references (pipelines referencing non-existent datasets)
- Missing governance metadata (retention policies, PII classification)
- Anti-patterns proliferate (no DLQs, silent drift, schema-on-read abuse)
- Inconsistent naming across teams

**With validation:**
- **Shift-left quality**: Detect issues in development/PR phase
- **Consistency**: Enforce organizational standards
- **Governance**: Ensure retention, SLAs, contracts are specified
- **Collaboration**: Shared vocabulary via UIDs and patterns

### Guiding Principles

1. **Fail-Fast**: Block deployment on errors, warn on smells
2. **Actionable Feedback**: Every violation includes remediation guidance
3. **Extensible**: Add custom rules without modifying validator code
4. **CI-Friendly**: Exit codes and output formats (text, JSON, JUnit) for automation

---

## Severity Levels

| Severity     | Exit Code | Meaning                                                                 | Action Required           |
|--------------|-----------|-------------------------------------------------------------------------|---------------------------|
| **ERROR**    | 1         | Critical violation (broken references, schema invalid, missing required fields) | **MUST FIX** before merge |
| **WARNING**  | 2¹        | Best practice violation, potential tech debt, missing optional metadata | Should fix; review with team |
| **SUGGESTION** | 0       | Improvement opportunity, optimization hint                               | Optional; consider for future |
| **INFO**     | 0         | Informational message, no action needed                                  | No action required        |

¹ Exit code 2 in non-strict mode; in `--strict` mode, warnings cause exit code 1 (fail)

---

## Running the Validator

### Installation

```bash
cd model/validators
pip install -r requirements.txt
```

**Dependencies:**
- `pyyaml` (required)
- `jsonschema` (required for schema validation)
- `jsonpath-ng` (optional, for advanced rule selectors)

### Basic Usage

```bash
# Validate a model file
python model/validators/validate.py --model model/examples/retail/model.example.yaml

# Strict mode (treat warnings as errors)
python model/validators/validate.py --model my-model.yaml --strict

# JSON output (for CI parsing)
python model/validators/validate.py --model my-model.yaml --output json

# JUnit XML output (for CI test reporting)
python model/validators/validate.py --model my-model.yaml --output junit > results.xml
```

### Options

| Flag          | Description                                      | Default                     |
|---------------|--------------------------------------------------|-----------------------------|
| `--model`     | Path to model YAML file (required)               | —                           |
| `--schema`    | Path to JSON schema file                         | `model/model.schema.yaml`   |
| `--rules`     | Path to conformance rules directory              | `model/checks/rules/`       |
| `--strict`    | Treat warnings as errors (fail on warnings)      | False                       |
| `--output`    | Output format: `text`, `json`, `junit`           | `text`                      |

---

## Interpreting Output

### Text Output Format

```
================================================================================
Validation Report: model/examples/retail/model.example.yaml
Timestamp: 2025-10-08 14:23:45
================================================================================

PASSED CHECKS:
  ✓ JSON Schema validation passed
  ✓ UID format validation passed for 23 entities
  ✓ All UIDs are unique (23 total)
  ✓ All cross-references resolve correctly
  ✓ Required fields validation passed
  ✓ Evaluated 47 conformance rules

SUGGESTIONS:
  💡 Dataset ds-orders-bronze: Consider adding freshness check for bronze datasets

WARNINGS:
  ⚠  Dataset ds-customer-pii (rul-retention-required): PII dataset missing retention policy
  ⚠  Pipeline pip-daily-rollup: No DLQ configured for stage stg-aggregate

ERRORS:
  ✗ Pipeline pip-ingest-orders: Invalid pipeline ID format: 'ingest_orders'. Expected pattern: pip-{kebab-name}
  ✗ Stage stg-transform: References non-existent input dataset: 'ds-orders-unknown'

================================================================================
SUMMARY: 6 passed, 1 suggestions, 2 warnings, 2 errors
================================================================================
Result: FAILED
```

### Understanding Output

**Passes:** Validations that succeeded. Confirms what's working.

**Suggestions:** Non-critical improvements (e.g., add optional metadata, consider alternative patterns).

**Warnings:** Best practice violations that should be addressed but don't block deployment (unless `--strict`).

**Errors:** Critical issues that **must** be fixed before deployment.

### JSON Output Format

```json
{
  "model_file": "model/examples/retail/model.example.yaml",
  "timestamp": "2025-10-08T14:23:45.123Z",
  "summary": {
    "errors": 2,
    "warnings": 2,
    "suggestions": 1,
    "passes": 6
  },
  "errors": [
    {
      "severity": "ERROR",
      "message": "Invalid pipeline ID format: 'ingest_orders'. Expected pattern: pip-{kebab-name}",
      "rule_id": "uid-format",
      "entity_type": "pipeline",
      "entity_id": "ingest_orders",
      "file_path": "model/examples/retail/model.example.yaml",
      "line_number": null
    }
  ],
  "warnings": [...],
  "suggestions": [...],
  "passes": [...]
  "result": "FAILED"
}
```

**Use case:** Parse in CI for custom reporting, metrics, dashboards.

---

## Common Violations & Remediation

### 1. Invalid UID Format

**Error:**
```
✗ Pipeline pip-ingest-orders: Invalid pipeline ID format: 'ingest_orders'. Expected pattern: pip-{kebab-name}
```

**Cause:** UID doesn't follow `{type-code}-{kebab-name}` convention.

**Remediation:**
```yaml
# ❌ WRONG
pipelines:
  - id: ingest_orders  # underscore not allowed
    name: Ingest Orders

# ✅ CORRECT
pipelines:
  - id: pip-ingest-orders  # kebab-case with prefix
    name: Ingest Orders
```

**UID Type Codes:**
- `sys-*` — System
- `dom-*` — Domain
- `pip-*` — Pipeline
- `stg-*` — Stage
- `trf-*` — Transform
- `ds-*` — Dataset
- `chk-*` — Check
- `ctr-*` — Contract
- `lin-*` — Lineage

---

### 2. Duplicate UIDs

**Error:**
```
✗ Duplicate UID 'ds-orders-silver' found in 2 entities: dataset, dataset
```

**Cause:** Same UID used for multiple entities.

**Remediation:**
```yaml
# ❌ WRONG
datasets:
  - id: ds-orders-silver
    name: Orders Silver (Cleaned)
  - id: ds-orders-silver  # Duplicate!
    name: Orders Silver (Deduplicated)

# ✅ CORRECT
datasets:
  - id: ds-orders-silver-cleaned
    name: Orders Silver (Cleaned)
  - id: ds-orders-silver-deduplicated
    name: Orders Silver (Deduplicated)
```

---

### 3. Broken Cross-Reference

**Error:**
```
✗ Stage stg-transform: References non-existent input dataset: 'ds-orders-unknown'
```

**Cause:** Stage references a dataset ID that doesn't exist in the model.

**Remediation:**
```yaml
# ❌ WRONG
datasets:
  - id: ds-orders-bronze  # Correct name
    name: Orders Bronze

pipelines:
  - id: pip-transform
    stages:
      - id: stg-clean
        inputs: [ds-orders-unknown]  # Typo! Should be ds-orders-bronze

# ✅ CORRECT
datasets:
  - id: ds-orders-bronze
    name: Orders Bronze

pipelines:
  - id: pip-transform
    stages:
      - id: stg-clean
        inputs: [ds-orders-bronze]  # Fixed reference
```

**Prevention Tip:** Use IDE autocomplete with YAML schema, or maintain a central UID registry.

---

### 4. Missing Retention Policy (Governance Rule)

**Warning:**
```
⚠ Dataset ds-customer-pii (rul-retention-required): PII dataset missing retention policy
```

**Cause:** Dataset containing PII lacks required `governance.retention_policy`.

**Remediation:**
```yaml
# ❌ WRONG
datasets:
  - id: ds-customer-pii
    name: Customer PII
    classification: pii
    # No retention policy!

# ✅ CORRECT
datasets:
  - id: ds-customer-pii
    name: Customer PII
    classification: pii
    governance:
      retention_policy:
        retain_days: 90  # GDPR requirement
        archive_after_days: 30
      access_tier: restricted
```

**Rule Source:** `/model/checks/rules/governance.yaml`

**Why It Matters:** GDPR/CCPA compliance requires documented retention policies for PII.

---

### 5. Missing Dead-Letter Queue (Reliability Rule)

**Warning:**
```
⚠ Pipeline pip-ingest-orders: No DLQ configured for stage stg-kafka-ingest (rul-streaming-dlq-required)
```

**Cause:** Streaming ingestion stage lacks DLQ for poison pill handling.

**Remediation:**
```yaml
# ❌ WRONG
stages:
  - id: stg-kafka-ingest
    type: ingestion
    mode: streaming
    source:
      type: kafka
      topic: orders-raw
    # No DLQ!

# ✅ CORRECT
stages:
  - id: stg-kafka-ingest
    type: ingestion
    mode: streaming
    source:
      type: kafka
      topic: orders-raw
    dlq_config:
      destination: s3://bucket/dlq/orders/
      max_retries: 3
      on_failure: dlq
```

**Rule Source:** `/model/checks/rules/reliability.yaml`

**Why It Matters:** Without DLQ, poison pills block entire pipeline; DLQ enables isolation and replay.

---

### 6. Missing Freshness Check (Observability Rule)

**Suggestion:**
```
💡 Dataset ds-orders-silver: Consider adding freshness check for near-real-time datasets
```

**Cause:** Dataset lacks freshness SLA monitoring.

**Remediation:**
```yaml
# ❌ SUBOPTIMAL
datasets:
  - id: ds-orders-silver
    name: Orders Silver
    # No freshness check

# ✅ IMPROVED
datasets:
  - id: ds-orders-silver
    name: Orders Silver
checks:
  - id: chk-orders-freshness
    type: freshness
    dataset: ds-orders-silver
    params:
      max_lag_minutes: 15
    severity: high
    on_failure: alert
```

**Rule Source:** `/model/checks/rules/observability.yaml`

**Why It Matters:** Freshness SLAs enable proactive alerting before downstream consumers notice delays.

---

### 7. Schema Validation Error

**Error:**
```
✗ Schema validation failed at 'pipelines.0.mode': 'real-time' is not one of ['batch', 'streaming', 'micro-batch', 'continuous']
```

**Cause:** Field value doesn't match JSON Schema enum.

**Remediation:**
```yaml
# ❌ WRONG
pipelines:
  - id: pip-streaming-ingest
    name: Streaming Ingest
    mode: real-time  # Invalid enum value

# ✅ CORRECT
pipelines:
  - id: pip-streaming-ingest
    name: Streaming Ingest
    mode: streaming  # Valid enum: batch | streaming | micro-batch | continuous
```

**Schema Reference:** `/model/model.schema.yaml`

---

### 8. Missing Required Field

**Error:**
```
✗ Dataset ds-orders-bronze missing required field: 'format'
```

**Cause:** Required field per JSON Schema is missing.

**Remediation:**
```yaml
# ❌ WRONG
datasets:
  - id: ds-orders-bronze
    name: Orders Bronze
    type: landing
    # Missing 'format' field

# ✅ CORRECT
datasets:
  - id: ds-orders-bronze
    name: Orders Bronze
    type: landing
    format: json  # Required field
    location: s3://bronze/orders/
```

---

## Custom Rules

### Rule Anatomy

Rules are defined in `/model/checks/rules/*.yaml`:

```yaml
rules:
  - id: rul-my-custom-rule
    name: My Custom Rule
    description: Ensures all gold datasets have a contract
    severity: WARNING  # ERROR | WARNING | SUGGESTION | INFO
    enabled: true
    selector: $.datasets[?(@.layer == 'gold')]  # JSONPath selector
    conditions:
      - type: required_field
        field: contract_id
    message: "Gold dataset '{name}' is missing a contract (field: contract_id)"
```

### Condition Types

| Type              | Description                                     | Example                                                  |
|-------------------|-------------------------------------------------|----------------------------------------------------------|
| `required_field`  | Field must exist and be non-null               | `{type: required_field, field: retention_policy}`        |
| `pattern`         | Field value must match regex                   | `{type: pattern, field: id, pattern: ^ds-.*$}`           |
| `enum`            | Field value must be in allowed list            | `{type: enum, field: mode, allowed: [batch, streaming]}` |
| `min_length`      | Field (string/array/dict) minimum length       | `{type: min_length, field: stages, min: 1}`              |
| `custom`          | Python expression (use sparingly)              | `{type: custom, expression: "len(target.get('checks', [])) > 0"}` |

### Adding a Custom Rule

1. Create `/model/checks/rules/custom.yaml`:

```yaml
rules:
  - id: rul-no-test-data-in-prod
    name: No Test Data in Production
    description: Prevents test/sandbox datasets from being tagged as production
    severity: ERROR
    enabled: true
    selector: $.datasets[?(@.environment == 'production')]
    conditions:
      - type: pattern
        field: name
        pattern: ^(?!.*test|.*sandbox|.*dev).*$  # Name must NOT contain test/sandbox/dev
    message: "Production dataset '{name}' has test-like naming. Remove 'test', 'sandbox', or 'dev' from name."
```

2. Run validator (rules loaded automatically from `/model/checks/rules/`):

```bash
python model/validators/validate.py --model my-model.yaml
```

3. Result:

```
ERRORS:
  ✗ Dataset ds-orders-test-prod (rul-no-test-data-in-prod): Production dataset 'Orders Test Dataset' has test-like naming.
```

### Rule Best Practices

- **Specific selectors:** Use JSONPath to target exact entities (e.g., `$.datasets[?(@.classification == 'pii')]`)
- **Actionable messages:** Include what's wrong AND how to fix (use template variables like `{name}`, `{id}`)
- **Right severity:** ERROR for broken systems, WARNING for tech debt, SUGGESTION for optimization
- **Document rules:** Add description explaining the "why" (compliance, reliability, cost, etc.)

---

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Validate Data Engineering Models

on:
  pull_request:
    paths:
      - 'models/**/*.yaml'

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'

      - name: Install dependencies
        run: |
          pip install -r model/validators/requirements.txt

      - name: Validate models (strict mode)
        run: |
          for model in models/**/*.yaml; do
            python model/validators/validate.py --model "$model" --strict --output text
          done

      - name: Generate validation report
        if: failure()
        run: |
          python model/validators/validate.py --model models/prod/model.yaml --output json > validation-report.json

      - name: Upload report artifact
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: validation-report
          path: validation-report.json
```

### GitLab CI Example

```yaml
validate-models:
  stage: test
  image: python:3.10
  before_script:
    - pip install -r model/validators/requirements.txt
  script:
    - |
      for model in models/**/*.yaml; do
        python model/validators/validate.py --model "$model" --strict --output junit > "report-$(basename $model).xml"
      done
  artifacts:
    reports:
      junit: report-*.xml
    when: always
```

### Pre-Commit Hook

Create `.git/hooks/pre-commit`:

```bash
#!/bin/bash
# Validate changed model files before commit

MODELS=$(git diff --cached --name-only --diff-filter=ACM | grep 'models/.*\.yaml$')

if [ -z "$MODELS" ]; then
  exit 0  # No model files changed
fi

echo "Validating model files..."

for model in $MODELS; do
  python model/validators/validate.py --model "$model" --strict --output text
  if [ $? -ne 0 ]; then
    echo "❌ Validation failed for $model"
    echo "Fix issues or use 'git commit --no-verify' to bypass (not recommended)"
    exit 1
  fi
done

echo "✅ All models validated successfully"
exit 0
```

Make executable:
```bash
chmod +x .git/hooks/pre-commit
```

---

## Troubleshooting

### Issue: "jsonschema library not installed"

**Solution:**
```bash
pip install jsonschema
```

Without `jsonschema`, JSON Schema validation is skipped (only custom rules run).

---

### Issue: "jsonpath-ng library not installed"

**Solution:**
```bash
pip install jsonpath-ng
```

Without `jsonpath-ng`, rule selectors fall back to root-level evaluation only (advanced JSONPath selectors won't work).

---

### Issue: Validator reports errors but model looks correct

**Diagnosis:**
1. Check YAML syntax: `python -c "import yaml; yaml.safe_load(open('model.yaml'))"`
2. Check schema version: Ensure `model.schema.yaml` is up-to-date
3. Check rule files: Ensure `/model/checks/rules/*.yaml` are valid

**Debug:**
```bash
# Run with JSON output to see full error details
python model/validators/validate.py --model model.yaml --output json | jq .
```

---

### Issue: Custom rule not triggering

**Checklist:**
- [ ] Rule file is in `/model/checks/rules/` directory
- [ ] Rule file has `.yaml` extension
- [ ] Rule `enabled: true`
- [ ] Selector matches target entities (test with standalone JSONPath tool)
- [ ] Condition type is correct (e.g., `required_field` vs. `pattern`)

**Test selector:**
```bash
pip install jsonpath-ng
python -c "
import yaml
from jsonpath_ng import parse

model = yaml.safe_load(open('model.yaml'))
selector = parse('$.datasets[?(@.classification == \"pii\")]')
matches = [m.value for m in selector.find(model)]
print(f'Matched {len(matches)} entities: {[m.get(\"id\") for m in matches]}')
"
```

---

### Issue: False positives/negatives

**Tune severity:**
- If too many warnings block progress → lower severity to `SUGGESTION`
- If critical issues slip through → raise severity to `ERROR`

**Disable specific rule temporarily:**
```yaml
rules:
  - id: rul-problematic-rule
    enabled: false  # Temporarily disable while investigating
    ...
```

---

## Best Practices

### For Model Authors

1. **Validate early:** Run validator locally before committing
2. **Fix errors first:** Don't accumulate technical debt
3. **Address warnings:** Schedule time to resolve warnings in sprint planning
4. **Use suggestions:** Treat as improvement backlog

### For Platform Teams

1. **Update rules incrementally:** Don't introduce 50 new rules at once
2. **Socialize changes:** Announce new rules in team channels before enforcement
3. **Provide examples:** Include "before/after" examples for each rule
4. **Monitor metrics:** Track error/warning trends over time

### For CI/CD

1. **Block on errors:** Always fail builds for ERROR-level issues
2. **Warn on warnings:** Report warnings but allow merge (or use `--strict`)
3. **Archive reports:** Store validation reports as CI artifacts for auditing
4. **Trending:** Track validation metrics (errors/warnings per PR, resolution time)

---

## Summary

| Action                        | Command                                                                 | Exit Code        |
|-------------------------------|-------------------------------------------------------------------------|------------------|
| Validate model (basic)        | `python validate.py --model model.yaml`                                 | 0=pass, 1=error  |
| Strict mode (warnings→errors) | `python validate.py --model model.yaml --strict`                        | 0=pass, 1=error  |
| JSON output for CI            | `python validate.py --model model.yaml --output json`                   | 0=pass, 1=error  |
| JUnit XML for test reporting  | `python validate.py --model model.yaml --output junit > results.xml`    | 0=pass, 1=error  |

**Exit Codes:**
- `0` — Success (no errors)
- `1` — Errors found (or warnings in strict mode)
- `2` — Warnings found (non-strict mode only)

---

## Next Steps

- **Read**: [`/docs/70-how-to-model-systems.md`](/docs/70-how-to-model-systems.md) for modeling walkthrough
- **Explore**: [`/model/checks/rules/`](/model/checks/rules/) for all conformance rules
- **Customize**: Add custom rules for your organization's standards
- **Automate**: Integrate validator into CI/CD pipelines

---

**Version**: 0.1.0
**Last Updated**: 2025-10-08
**Maintainers**: Data Platform Team
