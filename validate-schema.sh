#!/bin/bash
# Schema Validation Wrapper
# Activates venv and runs schema validation

set -e

# Get script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Activate venv
if [ -d "$SCRIPT_DIR/venv" ]; then
    source "$SCRIPT_DIR/venv/bin/activate"
else
    echo "Error: venv directory not found at $SCRIPT_DIR/venv"
    exit 1
fi

# Run validation with all arguments passed through
python "$SCRIPT_DIR/validate-schema.py" "$@"
