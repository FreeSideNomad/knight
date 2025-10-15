#!/bin/bash
# Convert YAML files to Markdown
# Usage: ./convert-yaml.sh <yaml-file> [output-file]

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VENV_DIR="$SCRIPT_DIR/venv"

echo -e "${BLUE}YAML to Markdown Converter${NC}"
echo "================================"
echo

# Check if venv exists
if [ ! -d "$VENV_DIR" ]; then
    echo "Creating virtual environment..."
    python3 -m venv "$VENV_DIR"
    echo -e "${GREEN}✓${NC} Virtual environment created"
fi

# Activate venv
echo "Activating virtual environment..."
source "$VENV_DIR/bin/activate"

# Install/upgrade dependencies
echo "Installing dependencies..."
pip install --quiet --upgrade pip
pip install --quiet -r "$SCRIPT_DIR/requirements.txt"
echo -e "${GREEN}✓${NC} Dependencies installed"
echo

# Run converter
if [ -z "$1" ]; then
    echo "Usage: $0 <yaml-file> [output-file]"
    echo
    echo "Examples:"
    echo "  $0 model/platform-agile.yaml"
    echo "  $0 model/platform-agile.yaml docs/platform-agile.md"
    exit 1
fi

YAML_FILE="$1"
OUTPUT_FILE="${2:-}"

if [ -n "$OUTPUT_FILE" ]; then
    python3 "$SCRIPT_DIR/yaml2md.py" "$YAML_FILE" -o "$OUTPUT_FILE"
else
    python3 "$SCRIPT_DIR/yaml2md.py" "$YAML_FILE"
fi

echo
echo -e "${GREEN}✓${NC} Conversion complete!"

# Deactivate venv
deactivate
