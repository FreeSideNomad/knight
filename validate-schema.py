#!/usr/bin/env python3
"""
Schema Validator for Domain Models
Validates YAML model files against their corresponding schemas
"""

import yaml
import json
import sys
import argparse
from pathlib import Path
from typing import Dict, Any, List, Tuple
import jsonschema
from jsonschema import Draft7Validator, ValidationError


class SchemaValidator:
    """Validates YAML models against their schemas."""

    # Map model files to their schema files
    MODEL_SCHEMA_MAP = {
        'model/platform-ddd.yaml': 'domains/ddd/model-schema.yaml',
        'model/platform-data-eng.yaml': 'domains/data-eng/model-schema.yaml',
        'model/platform-agile.yaml': 'domains/agile/model-schema.yaml',
        'model/platform-ux.yaml': 'domains/ux/model-schema.yaml',
        'model/platform-qe.yaml': 'domains/qe/model-schema.yaml',
    }

    def __init__(self, base_path: Path = Path('.')):
        self.base_path = base_path

    def load_yaml(self, file_path: Path) -> Dict[str, Any]:
        """Load YAML file, handling multiple documents."""
        with open(file_path, 'r', encoding='utf-8') as f:
            docs = list(yaml.safe_load_all(f))

            # If multiple documents exist, skip the first one (it's the schema definition for DDD)
            if len(docs) > 1:
                # Skip first document (schema metadata), return second (actual content)
                return docs[1] if docs[1] is not None else docs[0]

            # Single document - return it
            return docs[0] if docs else {}

    def validate_json_schema(self, model: Dict[str, Any], schema: Dict[str, Any]) -> Tuple[bool, List[str]]:
        """Validate model against JSON Schema."""
        errors = []

        try:
            validator = Draft7Validator(schema)
            validation_errors = sorted(validator.iter_errors(model), key=lambda e: e.path)

            for error in validation_errors:
                path = '.'.join(str(p) for p in error.path) if error.path else 'root'
                errors.append(f"  Path: {path}")
                errors.append(f"  Error: {error.message}")
                if error.validator:
                    errors.append(f"  Validator: {error.validator}")
                errors.append("")

        except Exception as e:
            errors.append(f"  Validation exception: {str(e)}")

        return len(errors) == 0, errors

    def validate_custom_yaml_schema(self, model: Dict[str, Any], schema: Dict[str, Any]) -> Tuple[bool, List[str]]:
        """Validate model against custom YAML schema (for DDD, UX, QE)."""
        errors = []

        # Check if schema has a $schema field indicating JSON Schema format
        if '$schema' in schema:
            return self.validate_json_schema(model, schema)

        # Custom validation for YAML-based schemas
        # DDD schema validation
        if 'system' in schema:
            errors.extend(self._validate_ddd_schema(model, schema))

        return len(errors) == 0, errors

    def _validate_ddd_schema(self, model: Dict[str, Any], schema: Dict[str, Any]) -> List[str]:
        """Validate DDD model structure."""
        errors = []

        # Check required top-level keys
        if 'system' not in model:
            errors.append("  Missing required field: 'system'")
        else:
            system = model['system']
            if 'id' not in system:
                errors.append("  system: Missing required field 'id'")
            elif not system['id'].startswith('sys_'):
                errors.append(f"  system.id: Must start with 'sys_' prefix, got: {system['id']}")

            if 'name' not in system:
                errors.append("  system: Missing required field 'name'")

        # Check domains
        if 'domains' in model:
            for domain_key, domain in model['domains'].items():
                if not isinstance(domain, dict):
                    errors.append(f"  domains.{domain_key}: Must be a dictionary")
                    continue

                if 'id' not in domain:
                    errors.append(f"  domains.{domain_key}: Missing required field 'id'")
                elif not domain['id'].startswith('dom_'):
                    errors.append(f"  domains.{domain_key}.id: Must start with 'dom_' prefix, got: {domain['id']}")

                if 'name' not in domain:
                    errors.append(f"  domains.{domain_key}: Missing required field 'name'")

        # Check bounded contexts
        if 'boundedContexts' in model:
            for bc_key, bc in model['boundedContexts'].items():
                if not isinstance(bc, dict):
                    errors.append(f"  boundedContexts.{bc_key}: Must be a dictionary")
                    continue

                if 'id' not in bc:
                    errors.append(f"  boundedContexts.{bc_key}: Missing required field 'id'")
                elif not bc['id'].startswith('bc_'):
                    errors.append(f"  boundedContexts.{bc_key}.id: Must start with 'bc_' prefix, got: {bc['id']}")

                if 'name' not in bc:
                    errors.append(f"  boundedContexts.{bc_key}: Missing required field 'name'")

                if 'domainRef' not in bc:
                    errors.append(f"  boundedContexts.{bc_key}: Missing required field 'domainRef'")

        # Check context mappings
        if 'contextMappings' in model:
            for cm_key, cm in model['contextMappings'].items():
                if not isinstance(cm, dict):
                    errors.append(f"  contextMappings.{cm_key}: Must be a dictionary")
                    continue

                if 'id' not in cm:
                    errors.append(f"  contextMappings.{cm_key}: Missing required field 'id'")
                elif not cm['id'].startswith('cm_'):
                    errors.append(f"  contextMappings.{cm_key}.id: Must start with 'cm_' prefix, got: {cm['id']}")

        return errors

    def validate_model(self, model_path: str) -> Tuple[bool, List[str]]:
        """Validate a single model file."""
        model_file = self.base_path / model_path
        schema_path = self.MODEL_SCHEMA_MAP.get(model_path)

        if not schema_path:
            return False, [f"No schema mapping found for {model_path}"]

        schema_file = self.base_path / schema_path

        if not model_file.exists():
            return False, [f"Model file not found: {model_file}"]

        if not schema_file.exists():
            return False, [f"Schema file not found: {schema_file}"]

        # Load model and schema
        try:
            model = self.load_yaml(model_file)
            schema = self.load_yaml(schema_file)
        except Exception as e:
            return False, [f"Failed to load files: {str(e)}"]

        # Determine validation type based on schema format
        if '$schema' in schema:
            # JSON Schema format (data-eng, agile)
            return self.validate_json_schema(model, schema)
        else:
            # Custom YAML schema format (ddd, ux, qe)
            return self.validate_custom_yaml_schema(model, schema)

    def validate_all(self) -> Dict[str, Tuple[bool, List[str]]]:
        """Validate all known models."""
        results = {}

        for model_path in self.MODEL_SCHEMA_MAP.keys():
            model_file = self.base_path / model_path
            if model_file.exists():
                results[model_path] = self.validate_model(model_path)
            else:
                results[model_path] = (None, [f"File not found: {model_file}"])

        return results


def print_validation_results(results: Dict[str, Tuple[bool, List[str]]]) -> int:
    """Print validation results and return exit code."""
    total = len(results)
    passed = sum(1 for valid, _ in results.values() if valid is True)
    failed = sum(1 for valid, _ in results.values() if valid is False)
    skipped = sum(1 for valid, _ in results.values() if valid is None)

    print("\n" + "=" * 80)
    print("SCHEMA VALIDATION RESULTS")
    print("=" * 80 + "\n")

    for model_path, (valid, errors) in results.items():
        if valid is True:
            print(f"✓ {model_path}")
            print(f"  Status: VALID")
        elif valid is False:
            print(f"✗ {model_path}")
            print(f"  Status: INVALID")
            if errors:
                print(f"  Errors ({len(errors)} issues):")
                for error in errors:
                    print(f"    {error}")
        else:
            print(f"⊘ {model_path}")
            print(f"  Status: SKIPPED")
            if errors:
                for error in errors:
                    print(f"    {error}")
        print()

    print("=" * 80)
    print(f"Summary: {passed} passed, {failed} failed, {skipped} skipped (out of {total})")
    print("=" * 80 + "\n")

    return 0 if failed == 0 else 1


def main():
    parser = argparse.ArgumentParser(
        description="Validate domain model YAML files against their schemas"
    )
    parser.add_argument(
        "model",
        nargs="?",
        help="Specific model to validate (e.g., model/platform-ddd.yaml). If not specified, validates all models."
    )
    parser.add_argument(
        "--base-path",
        type=Path,
        default=Path('.'),
        help="Base path for the project (default: current directory)"
    )

    args = parser.parse_args()

    validator = SchemaValidator(args.base_path)

    if args.model:
        # Validate single model
        valid, errors = validator.validate_model(args.model)

        if valid is True:
            print(f"✓ {args.model} is VALID")
            return 0
        elif valid is False:
            print(f"✗ {args.model} is INVALID")
            if errors:
                print("\nErrors:")
                for error in errors:
                    print(error)
            return 1
        else:
            print(f"⊘ {args.model} - SKIPPED")
            if errors:
                for error in errors:
                    print(error)
            return 1
    else:
        # Validate all models
        results = validator.validate_all()
        return print_validation_results(results)


if __name__ == "__main__":
    sys.exit(main())
