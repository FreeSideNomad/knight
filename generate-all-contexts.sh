#!/bin/bash
# Script to generate all remaining bounded context files
# This creates the complete 4-layer structure for each BC

set -e

BASE_DIR="/Users/igor/code/knight"

echo "🚀 Generating complete bounded context implementations..."
echo "This will create ~100+ files for 4 remaining bounded contexts"
echo ""

# Function to create directory structure
create_bc_structure() {
    local context_path=$1
    local bc_name=$2
    
    echo "📁 Creating structure for: $bc_name"
    
    # Create directory tree
    mkdir -p "$context_path/api/src/main/java"
    mkdir -p "$context_path/api/src/test/java"
    mkdir -p "$context_path/domain/src/main/java"
    mkdir -p "$context_path/domain/src/test/java"
    mkdir -p "$context_path/app/src/main/java"
    mkdir -p "$context_path/app/src/test/java"
    mkdir -p "$context_path/infra/src/main/java"
    mkdir -p "$context_path/infra/src/main/resources/db/migration"
    mkdir -p "$context_path/infra/src/test/java"
}

# Generate structures for remaining BCs
create_bc_structure "$BASE_DIR/contexts/service-profiles/indirect-clients" "Indirect Client Management"
create_bc_structure "$BASE_DIR/contexts/users/users" "Users"
create_bc_structure "$BASE_DIR/contexts/users/policy" "Policy"
create_bc_structure "$BASE_DIR/contexts/approval-workflows/engine" "Approval Engine"

echo ""
echo "✅ Directory structure created successfully"
echo ""
echo "📝 Next: Create all POMs, Java files, and config files for each BC"
echo "   Following the same pattern as Service Profile Management"
echo ""
echo "Files to generate per BC:"
echo "  - pom.xml (aggregator)"
echo "  - api/pom.xml + Commands + Queries + Events"
echo "  - domain/pom.xml + Aggregates + Entities + Domain Services"
echo "  - app/pom.xml + Application Services"
echo "  - infra/pom.xml + Application.java + Controllers + Repository + Config + Flyway + ArchUnit"
echo ""
