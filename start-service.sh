#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to get service info
get_service_info() {
    local service_key=$1
    case "$service_key" in
        spm)
            echo "contexts/service-profiles/management/infra:9500:Service Profile Management"
            ;;
        icm)
            echo "contexts/service-profiles/indirect-clients/infra:9501:Indirect Client Management"
            ;;
        users)
            echo "contexts/users/users/infra:9502:User Management"
            ;;
        policy)
            echo "contexts/users/policy/infra:9503:Policy Management"
            ;;
        approvals)
            echo "contexts/approval-workflows/engine/infra:9504:Approval Workflow Engine"
            ;;
        *)
            echo ""
            ;;
    esac
}

# Function to display usage
usage() {
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║          Knight Platform - Service Launcher              ║${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${YELLOW}Usage:${NC}"
    echo "  ./start-service.sh [service-name]"
    echo ""
    echo -e "${YELLOW}Available Services:${NC}"
    echo "  spm       - Service Profile Management (port 9500)"
    echo "  icm       - Indirect Client Management (port 9501)"
    echo "  users     - User Management (port 9502)"
    echo "  policy    - Policy Management (port 9503)"
    echo "  approvals - Approval Workflow Engine (port 9504)"
    echo "  all       - Start all services in background (default)"
    echo ""
    echo -e "${YELLOW}Examples:${NC}"
    echo "  ./start-service.sh          # Starts all services"
    echo "  ./start-service.sh spm      # Starts only SPM"
    echo "  ./start-service.sh users    # Starts only Users"
    echo ""
    exit 1
}

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Function to start a single service
start_service() {
    local service_key=$1
    local service_info=$(get_service_info "$service_key")

    if [ -z "$service_info" ]; then
        echo -e "${RED}✗ Unknown service: $service_key${NC}"
        echo ""
        usage
    fi

    IFS=':' read -r path port name <<< "$service_info"

    echo -e "${CYAN}╔═══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║  Starting: $name${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════════╝${NC}"
    echo ""

    # Check if port is already in use
    if check_port $port; then
        echo -e "${YELLOW}⚠ Port $port is already in use${NC}"
        echo "The service may already be running. Stop it first or use a different port."
        exit 1
    fi

    # Check if directory exists
    if [ ! -d "$path" ]; then
        echo -e "${RED}✗ Directory not found: $path${NC}"
        exit 1
    fi

    # Start the service
    echo -e "${BLUE}Starting service on port $port...${NC}"
    echo -e "${YELLOW}Press Ctrl+C to stop${NC}"
    echo ""

    cd "$path" && mvn mn:run
}

# Function to start all services in background
start_all_services() {
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║          Starting All Services in Background             ║${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════════╝${NC}"
    echo ""

    # Create logs directory
    mkdir -p logs

    for service_key in spm icm users policy approvals; do
        service_info=$(get_service_info "$service_key")
        IFS=':' read -r path port name <<< "$service_info"

        # Check if already running
        if check_port $port; then
            echo -e "${YELLOW}⊙ $name (port $port) - already running${NC}"
            continue
        fi

        # Start in background
        echo -e "${BLUE}▶ Starting $name (port $port)...${NC}"
        (cd "$path" && mvn mn:run > "../../../../logs/${service_key}.log" 2>&1 &)

        # Wait a bit before starting next service
        sleep 2
    done

    echo ""
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}║         All Services Started in Background               ║${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo ""
    echo -e "${YELLOW}View logs:${NC}"
    echo "  tail -f logs/spm.log"
    echo "  tail -f logs/users.log"
    echo "  tail -f logs/approvals.log"
    echo ""
    echo -e "${YELLOW}Check service health:${NC}"
    echo "  curl http://localhost:8081/health  # SPM"
    echo "  curl http://localhost:8083/health  # Users"
    echo "  curl http://localhost:8085/health  # Approvals"
    echo ""
    echo -e "${YELLOW}Stop all services:${NC}"
    echo "  pkill -f 'mn:run'"
    echo ""
}

# Main script
SERVICE=${1:-all}

if [ "$SERVICE" = "all" ]; then
    start_all_services
else
    start_service "$SERVICE"
fi
