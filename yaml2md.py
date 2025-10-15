#!/usr/bin/env python3
"""
YAML to Markdown Converter v2
Enhanced with schema-aware ID humanization
"""

import yaml
import sys
import argparse
from pathlib import Path
from typing import Any, Dict, List, Optional
from collections import defaultdict
import re


class YamlToMarkdownConverter:
    def __init__(self, yaml_file: Path):
        self.yaml_file = yaml_file
        self.data = self._load_yaml()
        self.id_index: Dict[str, Any] = {}

        # Schema-aware ID type mappings
        self.id_type_map = {
            # DDD Schema patterns (underscore)
            'sys_': 'System',
            'dom_': 'Domain',
            'bc_': 'Bounded Context',
            'agg_': 'Aggregate',
            'ent_': 'Entity',
            'vo_': 'Value Object',
            'repo_': 'Repository',
            'svc_dom_': 'Domain Service',
            'svc_app_': 'Application Service',
            'factory_': 'Factory',
            'evt_': 'Domain Event',
            'spec_': 'Specification',
            'cm_': 'Context Mapping',

            # Data-Eng Schema patterns (kebab-case)
            'sys-': 'System',
            'dom-': 'Domain',
            'pip-': 'Pipeline',
            'stg-': 'Stage',
            'trx-': 'Transform',
            'ds-': 'Dataset',
            'ctr-': 'Contract',
            'dp-': 'Data Product',

            # Agile Schema patterns
            'EPIC-': 'Epic',
            'FEAT-': 'Feature',
            'US-': 'User Story',
            'REL-': 'Release',
            'PI-': 'Program Increment',
            'SPRINT-': 'Sprint',
        }

        self._build_id_index(self.data)

    def _load_yaml(self) -> Dict[str, Any]:
        """Load YAML file."""
        with open(self.yaml_file, 'r', encoding='utf-8') as f:
            return yaml.safe_load(f)

    def _build_id_index(self, data: Any, path: str = "") -> None:
        """Build index of all objects with 'id' field for reference resolution."""
        if isinstance(data, dict):
            if 'id' in data:
                self.id_index[data['id']] = data
            for key, value in data.items():
                self._build_id_index(value, f"{path}.{key}" if path else key)
        elif isinstance(data, list):
            for i, item in enumerate(data):
                self._build_id_index(item, f"{path}[{i}]")

    def _humanize_id_with_type(self, obj_id: str) -> str:
        """Convert ID to human-readable format with proper type suffix."""
        if not isinstance(obj_id, str):
            return str(obj_id)

        # Find matching prefix and type
        id_type = ""
        name_part = obj_id

        for prefix, type_name in self.id_type_map.items():
            if obj_id.startswith(prefix):
                id_type = type_name
                name_part = obj_id[len(prefix):]
                break

        # Convert name part to human readable
        # Handle underscores and hyphens
        name_part = name_part.replace('_', ' ').replace('-', ' ')

        # Handle camelCase
        name_part = re.sub(r'([a-z])([A-Z])', r'\1 \2', name_part)

        # Capitalize words properly
        words = name_part.split()
        human_name = ' '.join([w.capitalize() for w in words if w])

        # Return with type suffix
        if id_type:
            return f"{human_name} {id_type}"
        return human_name

    def _humanize_key(self, key: str) -> str:
        """Convert camelCase, PascalCase, snake_case to human-readable format."""
        # Handle snake_case
        if '_' in key:
            return key.replace('_', ' ').title()

        # Handle camelCase and PascalCase
        spaced = re.sub(r'([a-z])([A-Z])', r'\1 \2', key)
        spaced = re.sub(r'([A-Z]+)([A-Z][a-z])', r'\1 \2', spaced)

        words = spaced.split()
        result = []
        for word in words:
            if word.isupper() and len(word) > 1:
                result.append(word)  # Keep acronyms
            else:
                result.append(word.capitalize())
        return ' '.join(result)

    def _sanitize_anchor(self, text: str) -> str:
        """Create valid markdown anchor from text."""
        return text.lower().replace(' ', '-').replace('_', '-')

    def _is_reference_id(self, value: str) -> bool:
        """Check if a string value is a reference ID."""
        if not isinstance(value, str):
            return False
        return value in self.id_index

    def _format_cell_value(self, value: Any) -> str:
        """Format a value for table cell display."""
        if value is None:
            return ""
        if isinstance(value, bool):
            return "✓" if value else "✗"
        if isinstance(value, (list, dict)):
            return f"*{type(value).__name__}*"

        return str(value)

    def _get_table_columns(self, items: List[Any]) -> List[str]:
        """Determine key columns for table display."""
        if not items or not isinstance(items[0], dict):
            return []

        priority = ['id', 'name', 'title', 'type', 'status', 'description']
        all_keys = set()
        for item in items:
            if isinstance(item, dict):
                all_keys.update(item.keys())

        columns = [k for k in priority if k in all_keys]
        remaining = sorted([k for k in all_keys if k not in columns])
        columns.extend(remaining[:5])

        return columns

    def _render_table(self, items: List[Any], level: int = 0) -> str:
        """Render list of objects as markdown table."""
        if not items:
            return "*Empty list*\n\n"

        # Simple items as bullet list
        if all(not isinstance(item, (dict, list)) for item in items):
            return "\n".join([f"- {item}" for item in items]) + "\n\n"

        # If all are reference IDs, resolve them
        if all(isinstance(item, str) and self._is_reference_id(item) for item in items):
            resolved = []
            for ref_id in items:
                obj = self.id_index.get(ref_id)
                if obj:
                    resolved.append(obj)
            items = resolved if resolved else items

        # Non-dict items fallback
        if not items or not isinstance(items[0], dict):
            return "\n".join([f"{i}. {item}" for i, item in enumerate(items, 1)]) + "\n\n"

        columns = self._get_table_columns(items)
        if not columns:
            return "*Complex list*\n\n"

        # Create table
        header = "| " + " | ".join([self._humanize_key(col) for col in columns]) + " |"
        separator = "| " + " | ".join(["---"] * len(columns)) + " |"

        rows = []
        for item in items:
            row_values = []
            for col in columns:
                value = item.get(col, "")
                # Special handling for ID column
                if col == 'id' and isinstance(value, str):
                    anchor = self._sanitize_anchor(value)
                    human_text = self._humanize_id_with_type(value)
                    row_values.append(f"[{human_text}](#{anchor})")
                else:
                    row_values.append(self._format_cell_value(value))
            rows.append("| " + " | ".join(row_values) + " |")

        return header + "\n" + separator + "\n" + "\n".join(rows) + "\n\n"

    def _render_value(self, key: str, value: Any, level: int = 0) -> str:
        """Render a value."""
        if value is None:
            return "*Not specified*\n\n"

        if isinstance(value, bool):
            return f"{'✓ Yes' if value else '✗ No'}\n\n"

        if isinstance(value, (str, int, float)):
            # Check if it's a reference ID
            if isinstance(value, str) and self._is_reference_id(value):
                anchor = self._sanitize_anchor(value)
                human_text = self._humanize_id_with_type(value)
                return f"[{human_text}](#{anchor})\n\n"

            # Multi-line strings
            if isinstance(value, str) and '\n' in value:
                lines = value.strip().split('\n')
                return "\n" + '\n'.join(lines) + "\n\n"

            return f"{value}\n\n"

        if isinstance(value, list):
            return self._render_table(value, level)

        if isinstance(value, dict):
            return self._render_dict(value, level)

        return f"{value}\n\n"

    def _render_dict(self, data: Dict[str, Any], level: int = 0, parent_key: str = "") -> str:
        """Render dictionary as markdown."""
        if not data:
            return ""

        output = []

        # Render ID first with proper humanization
        if 'id' in data:
            obj_id = data['id']
            anchor = self._sanitize_anchor(obj_id)
            human_id = self._humanize_id_with_type(obj_id)
            output.append(f'<a id="{anchor}"></a>\n')
            output.append(f"**ID**: `{obj_id}` ({human_id})\n\n")

        for key, value in data.items():
            if key == 'id':
                continue

            # Nested objects/lists get headers
            if isinstance(value, dict):
                header_level = min(level + 3, 6)
                title = self._humanize_key(key)
                output.append(f"{'#' * header_level} {title}\n\n")
                output.append(self._render_dict(value, level + 1, key))
            elif isinstance(value, list):
                header_level = min(level + 3, 6)
                title = self._humanize_key(key)
                output.append(f"{'#' * header_level} {title}\n\n")
                output.append(self._render_table(value, level + 1))
            else:
                key_formatted = self._humanize_key(key)
                output.append(f"**{key_formatted}**: ")
                output.append(self._render_value(key, value, 0))

        return "".join(output)

    def _generate_reference_index(self) -> str:
        """Generate hierarchical reference index."""
        if not self.id_index:
            return ""

        output = ["# Reference Index\n\n"]
        output.append("Quick navigation to all identified objects:\n\n")

        # Define hierarchy order
        hierarchy = [
            ('sys_', 'Systems'),
            ('sys-', 'Systems'),
            ('dom_', 'Domains'),
            ('dom-', 'Domains'),
            ('bc_', 'Bounded Contexts'),
            ('pip-', 'Pipelines'),
            ('agg_', 'Aggregates'),
            ('stg-', 'Stages'),
            ('ds-', 'Datasets'),
            ('ctr-', 'Contracts'),
            ('dp-', 'Data Products'),
            ('cm_', 'Context Mappings'),
        ]

        # Group IDs by prefix
        grouped = defaultdict(list)
        for obj_id in sorted(self.id_index.keys()):
            for prefix, _ in hierarchy:
                if obj_id.startswith(prefix):
                    grouped[prefix].append(obj_id)
                    break
            else:
                grouped['other'].append(obj_id)

        # Output in hierarchical order
        for prefix, plural_name in hierarchy:
            if prefix in grouped and grouped[prefix]:
                output.append(f"### {plural_name}\n\n")
                for obj_id in grouped[prefix]:
                    anchor = self._sanitize_anchor(obj_id)
                    obj = self.id_index[obj_id]
                    name = obj.get('name', '')
                    human_id = self._humanize_id_with_type(obj_id)
                    output.append(f"- [{human_id}](#{anchor})")
                    if name:
                        output.append(f" - {name}")
                    output.append("\n")
                output.append("\n")

        if 'other' in grouped:
            output.append("### Other\n\n")
            for obj_id in grouped['other']:
                anchor = self._sanitize_anchor(obj_id)
                output.append(f"- [{obj_id}](#{anchor})\n")
            output.append("\n")

        output.append("---\n\n")
        return "".join(output)

    def _generate_toc(self) -> str:
        """Generate table of contents."""
        if not self.data:
            return ""

        toc = ["# Table of Contents\n\n"]

        for key in self.data.keys():
            if key.startswith('$'):
                continue
            anchor = self._sanitize_anchor(key)
            title = self._humanize_key(key)
            toc.append(f"- [{title}](#{anchor})\n")

        toc.append("\n---\n\n")
        return "".join(toc)

    def convert(self) -> str:
        """Convert YAML to Markdown."""
        output = []

        # Title
        title = self.yaml_file.stem.replace('-', ' ').replace('_', ' ').title()
        output.append(f"# {title}\n\n")
        output.append(f"*Generated from: {self.yaml_file.name}*\n\n")
        output.append("---\n\n")

        # Reference index for files with many IDs
        if len(self.id_index) > 5:
            output.append(self._generate_reference_index())

        # Table of contents
        output.append(self._generate_toc())

        # Main content
        for key, value in self.data.items():
            if key.startswith('$'):
                continue

            anchor = self._sanitize_anchor(key)
            title = self._humanize_key(key)
            output.append(f'<a id="{anchor}"></a>\n')
            output.append(f"## {title}\n\n")

            if isinstance(value, dict):
                output.append(self._render_dict(value, level=1))
            elif isinstance(value, list):
                output.append(self._render_table(value))
            else:
                output.append(self._render_value(key, value))

            output.append("\n---\n\n")

        return "".join(output)


def main():
    parser = argparse.ArgumentParser(
        description="Convert YAML files to Markdown with proper ID humanization"
    )
    parser.add_argument("yaml_file", type=Path, help="YAML file to convert")
    parser.add_argument("-o", "--output", type=Path, help="Output file")

    args = parser.parse_args()

    if not args.yaml_file.exists():
        print(f"Error: {args.yaml_file} not found", file=sys.stderr)
        sys.exit(1)

    output_file = args.output or args.yaml_file.with_suffix('.md')

    print(f"Converting {args.yaml_file} to {output_file}...")
    converter = YamlToMarkdownConverter(args.yaml_file)
    markdown = converter.convert()

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(markdown)

    print(f"✓ Generated {output_file}")
    print(f"  - {len(converter.id_index)} objects with IDs")
    print(f"  - {len(markdown.splitlines())} lines")


if __name__ == "__main__":
    main()