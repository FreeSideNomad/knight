#!/usr/bin/env python3
"""
YAML to Markdown Converter
Converts YAML files to readable Markdown with:
- Navigation by reference IDs
- Arrays rendered as tables
- Hierarchical headers for embedded attributes
- ID resolution for references
"""

import yaml
import sys
import argparse
from pathlib import Path
from typing import Any, Dict, List, Optional, Set
from collections import defaultdict


class YamlToMarkdownConverter:
    def __init__(self, yaml_file: Path):
        self.yaml_file = yaml_file
        self.data = self._load_yaml()
        self.id_index: Dict[str, Any] = {}
        self.references: Dict[str, List[str]] = defaultdict(list)
        self._build_id_index(self.data)

    def _load_yaml(self) -> Dict[str, Any]:
        """Load YAML file."""
        with open(self.yaml_file, 'r', encoding='utf-8') as f:
            return yaml.safe_load(f)

    def _build_id_index(self, data: Any, path: str = "") -> None:
        """Build index of all objects with 'id' field for reference resolution."""
        if isinstance(data, dict):
            if 'id' in data:
                obj_id = data['id']
                self.id_index[obj_id] = data
            for key, value in data.items():
                self._build_id_index(value, f"{path}.{key}" if path else key)
        elif isinstance(data, list):
            for i, item in enumerate(data):
                self._build_id_index(item, f"{path}[{i}]")

    def _is_reference_id(self, value: str) -> bool:
        """Check if a string value is a reference ID."""
        if not isinstance(value, str):
            return False
        # Common ID patterns
        patterns = ['-', '_']
        return any(p in value for p in patterns) and value in self.id_index

    def _resolve_reference(self, ref_id: str) -> Optional[Dict[str, Any]]:
        """Resolve a reference ID to its object."""
        return self.id_index.get(ref_id)

    def _sanitize_anchor(self, text: str) -> str:
        """Create valid markdown anchor from text."""
        return text.lower().replace(' ', '-').replace('_', '-')

    def _humanize_key(self, key: str) -> str:
        """Convert camelCase, PascalCase, snake_case to human-readable format."""
        # Handle snake_case
        if '_' in key:
            return key.replace('_', ' ').title()

        # Handle camelCase and PascalCase
        import re
        # Insert space before uppercase letters that follow lowercase letters
        spaced = re.sub(r'([a-z])([A-Z])', r'\1 \2', key)
        # Insert space before uppercase letters that are followed by lowercase letters
        spaced = re.sub(r'([A-Z]+)([A-Z][a-z])', r'\1 \2', spaced)
        # Capitalize first letter, lowercase the rest unless it's an acronym
        words = spaced.split()
        result = []
        for word in words:
            if word.isupper() and len(word) > 1:
                result.append(word)  # Keep acronyms uppercase
            else:
                result.append(word.capitalize())
        return ' '.join(result)

    def _get_table_columns(self, items: List[Any]) -> List[str]:
        """Determine key columns for table display."""
        if not items or not isinstance(items[0], dict):
            return []

        # Priority columns (show first if present)
        priority = ['id', 'name', 'title', 'type', 'status', 'description']
        all_keys = set()
        for item in items:
            if isinstance(item, dict):
                all_keys.update(item.keys())

        # Start with priority columns that exist
        columns = [k for k in priority if k in all_keys]

        # Add remaining keys (limit to reasonable number)
        remaining = sorted([k for k in all_keys if k not in columns])
        columns.extend(remaining[:5])  # Max 8 columns total

        return columns

    def _format_cell_value(self, value: Any, max_length: int = 50) -> str:
        """Format a value for table cell display."""
        if value is None:
            return ""

        if isinstance(value, bool):
            return "✓" if value else "✗"

        if isinstance(value, (list, dict)):
            return f"*{type(value).__name__}*"

        str_value = str(value)
        if len(str_value) > max_length:
            return str_value[:max_length-3] + "..."
        return str_value

    def _render_table(self, items: List[Any], level: int = 0) -> str:
        """Render list of objects as markdown table."""
        if not items:
            return "*Empty list*\n\n"

        # If all items are simple strings/numbers, render as bullet list
        if all(not isinstance(item, (dict, list)) for item in items):
            result = []
            for item in items:
                result.append(f"- {item}")
            return "\n".join(result) + "\n\n"

        # If all items are reference IDs, resolve and create table
        if all(isinstance(item, str) and self._is_reference_id(item) for item in items):
            resolved_items = []
            for ref_id in items:
                obj = self._resolve_reference(ref_id)
                if obj:
                    resolved_items.append(obj)
            items = resolved_items if resolved_items else items

        # Render as table
        if not items or not isinstance(items[0], dict):
            # Fallback for non-dict items
            result = []
            for i, item in enumerate(items, 1):
                result.append(f"{i}. {item}")
            return "\n".join(result) + "\n\n"

        columns = self._get_table_columns(items)
        if not columns:
            return "*Complex list - see details below*\n\n"

        # Create table header
        header = "| " + " | ".join(columns) + " |"
        separator = "| " + " | ".join(["---"] * len(columns)) + " |"

        # Create table rows
        rows = []
        for item in items:
            row_values = []
            for col in columns:
                value = item.get(col) if isinstance(item, dict) else ""
                # Create link for id column
                if col == 'id' and isinstance(value, str):
                    anchor = self._sanitize_anchor(value)
                    row_values.append(f"[{value}](#{anchor})")
                else:
                    row_values.append(self._format_cell_value(value))
            rows.append("| " + " | ".join(row_values) + " |")

        return header + "\n" + separator + "\n" + "\n".join(rows) + "\n\n"

    def _render_value(self, key: str, value: Any, level: int = 0) -> str:
        """Render a value (recursive)."""
        indent = "  " * level

        if value is None:
            return f"{indent}*Not specified*\n\n"

        if isinstance(value, bool):
            return f"{indent}{'✓ Yes' if value else '✗ No'}\n\n"

        if isinstance(value, (str, int, float)):
            # Check if it's a reference ID
            if isinstance(value, str) and self._is_reference_id(value):
                anchor = self._sanitize_anchor(value)
                return f"{indent}[{value}](#{anchor})\n\n"

            # Multi-line strings - render on new line, not inline with key
            if isinstance(value, str) and '\n' in value:
                lines = value.strip().split('\n')
                # Don't use blockquote, just render as plain text with blank line before
                return "\n" + '\n'.join(lines) + "\n\n"

            return f"{value}\n\n"

        if isinstance(value, list):
            return self._render_table(value, level)

        if isinstance(value, dict):
            return self._render_dict(value, level)

        return f"{indent}{value}\n\n"

    def _render_dict(self, data: Dict[str, Any], level: int = 0, parent_key: str = "") -> str:
        """Render dictionary as markdown (recursive)."""
        if not data:
            return ""

        output = []

        # Render id field first if present (as anchor)
        if 'id' in data:
            obj_id = data['id']
            anchor = self._sanitize_anchor(obj_id)
            output.append(f"<a id=\"{anchor}\"></a>\n")
            output.append(f"**ID**: `{obj_id}`\n\n")

        for key, value in data.items():
            if key == 'id':
                continue  # Already rendered

            # Create header for nested objects
            if isinstance(value, dict):
                header_level = min(level + 3, 6)  # Cap at h6
                title = self._humanize_key(key)
                output.append(f"{'#' * header_level} {title}\n\n")
                output.append(self._render_dict(value, level + 1, key))

            elif isinstance(value, list):
                header_level = min(level + 3, 6)
                title = self._humanize_key(key)
                output.append(f"{'#' * header_level} {title}\n\n")
                output.append(self._render_table(value, level + 1))

            else:
                # Simple key-value
                key_formatted = self._humanize_key(key)
                output.append(f"**{key_formatted}**: ")
                rendered = self._render_value(key, value, 0)
                output.append(rendered)

        return "".join(output)

    def _generate_toc(self) -> str:
        """Generate table of contents from top-level keys."""
        if not self.data:
            return ""

        toc = ["# Table of Contents\n\n"]

        for key in self.data.keys():
            # Skip $schema
            if key.startswith('$'):
                continue
            anchor = self._sanitize_anchor(key)
            title = self._humanize_key(key)
            toc.append(f"- [{title}](#{anchor})\n")

        toc.append("\n---\n\n")
        return "".join(toc)

    def _generate_reference_index(self) -> str:
        """Generate index of all objects with IDs."""
        if not self.id_index:
            return ""

        output = ["# Reference Index\n\n"]
        output.append("Quick navigation to all identified objects:\n\n")

        # Group by ID prefix
        prefixed: Dict[str, List[str]] = defaultdict(list)
        for obj_id in sorted(self.id_index.keys()):
            prefix = obj_id.split('-')[0] if '-' in obj_id else obj_id.split('_')[0]
            prefixed[prefix].append(obj_id)

        for prefix in sorted(prefixed.keys()):
            output.append(f"### {prefix.upper()}\n\n")
            for obj_id in prefixed[prefix]:
                anchor = self._sanitize_anchor(obj_id)
                obj = self.id_index[obj_id]
                name = obj.get('name', obj.get('title', obj_id))
                output.append(f"- [{obj_id}](#{anchor})")
                if name != obj_id:
                    output.append(f" - {name}")
                output.append("\n")
            output.append("\n")

        output.append("---\n\n")
        return "".join(output)

    def convert(self) -> str:
        """Convert YAML to Markdown."""
        output = []

        # Title
        title = self.yaml_file.stem.replace('-', ' ').replace('_', ' ').title()
        output.append(f"# {title}\n\n")
        output.append(f"*Generated from: {self.yaml_file.name}*\n\n")
        output.append("---\n\n")

        # Reference index if we have IDs
        if len(self.id_index) > 5:
            output.append(self._generate_reference_index())

        # Table of contents
        output.append(self._generate_toc())

        # Main content
        for key, value in self.data.items():
            # Skip $schema and other $ fields
            if key.startswith('$'):
                continue

            anchor = self._sanitize_anchor(key)
            title = self._humanize_key(key)
            output.append(f"<a id=\"{anchor}\"></a>\n")
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
        description="Convert YAML files to Markdown with navigation and tables"
    )
    parser.add_argument(
        "yaml_file",
        type=Path,
        help="Path to YAML file to convert"
    )
    parser.add_argument(
        "-o", "--output",
        type=Path,
        help="Output Markdown file (default: same name with .md extension)"
    )

    args = parser.parse_args()

    if not args.yaml_file.exists():
        print(f"Error: File not found: {args.yaml_file}", file=sys.stderr)
        sys.exit(1)

    # Determine output file
    if args.output:
        output_file = args.output
    else:
        output_file = args.yaml_file.with_suffix('.md')

    # Convert
    print(f"Converting {args.yaml_file} to {output_file}...")
    converter = YamlToMarkdownConverter(args.yaml_file)
    markdown = converter.convert()

    # Write output
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(markdown)

    print(f"✓ Generated {output_file}")
    print(f"  - {len(converter.id_index)} objects with IDs")
    print(f"  - {len(markdown.splitlines())} lines")


if __name__ == "__main__":
    main()
