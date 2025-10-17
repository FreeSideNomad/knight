#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Banner
echo -e "${CYAN}"
cat << "EOF"
╔═══════════════════════════════════════════════════════════╗
║                                                           ║
║     Knight - Commercial Banking Platform                 ║
║     Micronaut + PostgreSQL + Kafka                       ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝
EOF
echo -e "${NC}"

# Function to check if docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}✗ Docker is not running!${NC}"
        echo "Please start Docker Desktop and try again."
        exit 1
    fi
    echo -e "${GREEN}✓ Docker is running${NC}"
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

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1

    echo -n "Waiting for $service_name to be ready"
    while [ $attempt -le $max_attempts ]; do
        if check_port $port; then
            echo -e " ${GREEN}✓${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    echo -e " ${RED}✗ Timeout${NC}"
    return 1
}

# Function to wait for HTTP endpoint
wait_for_http() {
    local url=$1
    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if curl -s -f -o /dev/null "$url" 2>/dev/null; then
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    return 1
}

# Check Docker
echo -e "\n${BLUE}[1/4] Checking Prerequisites${NC}"
check_docker

# Start infrastructure
echo -e "\n${BLUE}[2/4] Starting Infrastructure (PostgreSQL + Kafka)${NC}"
if docker-compose ps | grep -q "Up"; then
    echo -e "${YELLOW}Infrastructure is already running${NC}"
else
    echo "Starting docker-compose..."
    docker-compose up -d
fi

# Wait for infrastructure
echo -e "\n${BLUE}[3/4] Waiting for Infrastructure${NC}"
wait_for_service "PostgreSQL" 5432
wait_for_service "Kafka" 9092
wait_for_service "AKHQ" 8080
wait_for_service "pgAdmin" 5050

# Build applications
echo -e "\n${BLUE}[4/4] Building Applications${NC}"
echo "Running Maven build..."
if mvn clean package -DskipTests -q; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi

# Display information
echo -e "\n${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}║                                                         ║${NC}"
echo -e "${GREEN}║            Infrastructure Started Successfully         ║${NC}"
echo -e "${GREEN}║                                                         ║${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"

echo -e "\n${CYAN}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║                  INFRASTRUCTURE ACCESS                    ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════════╝${NC}"

echo -e "\n${YELLOW}PostgreSQL Database:${NC}"
echo "  URL:      postgresql://localhost:5432/knight"
echo "  Username: knight"
echo "  Password: knight"
echo "  Schemas:  spm, icm, users, policy, approvals"

echo -e "\n${YELLOW}pgAdmin (PostgreSQL UI):${NC}"
echo "  URL:      http://localhost:5050"
echo "  Email:    admin@knight.com"
echo "  Password: admin"
echo -e "  ${CYAN}→ Server 'Knight Platform' is pre-configured${NC}"

echo -e "\n${YELLOW}Apache Kafka:${NC}"
echo "  Bootstrap: localhost:9092"
echo "  Topics:    spm.*, icm.*, users.*, policy.*, approvals.*"

echo -e "\n${YELLOW}AKHQ (Kafka UI):${NC}"
echo "  URL:      http://localhost:8080"
echo -e "  ${CYAN}→ No authentication required${NC}"

echo -e "\n${YELLOW}Schema Registry:${NC}"
echo "  URL:      http://localhost:8081"

echo -e "\n${CYAN}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║                  BOUNDED CONTEXTS (APIs)                  ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════════╝${NC}"

echo -e "\n${YELLOW}Service Profile Management:${NC}"
echo "  Port:     8081"
echo "  Health:   http://localhost:8081/health"
echo "  Metrics:  http://localhost:8081/metrics"
echo "  Schema:   spm"
echo "  Start:    cd contexts/service-profiles/management/infra && mvn mn:run"

echo -e "\n${YELLOW}Indirect Client Management:${NC}"
echo "  Port:     8082"
echo "  Health:   http://localhost:8082/health"
echo "  Metrics:  http://localhost:8082/metrics"
echo "  Schema:   icm"
echo "  Start:    cd contexts/service-profiles/indirect-clients/infra && mvn mn:run"

echo -e "\n${YELLOW}User Management:${NC}"
echo "  Port:     8083"
echo "  Health:   http://localhost:8083/health"
echo "  Metrics:  http://localhost:8083/metrics"
echo "  Schema:   users"
echo "  Start:    cd contexts/users/users/infra && mvn mn:run"

echo -e "\n${YELLOW}Policy Management:${NC}"
echo "  Port:     8084"
echo "  Health:   http://localhost:8084/health"
echo "  Metrics:  http://localhost:8084/metrics"
echo "  Schema:   policy"
echo "  Start:    cd contexts/users/policy/infra && mvn mn:run"

echo -e "\n${YELLOW}Approval Workflow Engine:${NC}"
echo "  Port:     8085"
echo "  Health:   http://localhost:8085/health"
echo "  Metrics:  http://localhost:8085/metrics"
echo "  Schema:   approvals"
echo "  Start:    cd contexts/approval-workflows/engine/infra && mvn mn:run"

echo -e "\n${CYAN}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║                     QUICK COMMANDS                        ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════════╝${NC}"

echo -e "\n${YELLOW}Start a service:${NC}"
echo "  ./start-service.sh <service-name>"
echo "  Example: ./start-service.sh spm"
echo ""
echo "  Available services:"
echo "    spm      - Service Profile Management (port 8081)"
echo "    icm      - Indirect Client Management (port 8082)"
echo "    users    - User Management (port 8083)"
echo "    policy   - Policy Management (port 8084)"
echo "    approvals - Approval Workflow Engine (port 8085)"

echo -e "\n${YELLOW}View logs:${NC}"
echo "  docker-compose logs -f                    # All services"
echo "  docker-compose logs -f postgres           # PostgreSQL"
echo "  docker-compose logs -f kafka              # Kafka"

echo -e "\n${YELLOW}Stop infrastructure:${NC}"
echo "  docker-compose stop                       # Stop (keep data)"
echo "  docker-compose down                       # Stop and remove"
echo "  docker-compose down -v                    # Stop, remove, and delete data"

echo -e "\n${YELLOW}Run tests:${NC}"
echo "  mvn test                                  # All tests"
echo "  mvn test -pl contexts/users/users/infra   # Specific module"

echo -e "\n${YELLOW}Database access:${NC}"
echo "  psql postgresql://knight:knight@localhost:5432/knight"
echo "  \\dn                                       # List schemas"
echo "  \\dt spm.*                                 # List tables in spm schema"

echo -e "\n${CYAN}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║                  USEFUL LINKS                             ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════════╝${NC}"

echo ""
echo "  📊 AKHQ (Kafka):     http://localhost:8080"
echo "  🗄️  pgAdmin:          http://localhost:5050"
echo "  📈 Metrics:          http://localhost:808[1-5]/metrics"
echo "  💚 Health Checks:    http://localhost:808[1-5]/health"
echo "  📚 Documentation:    contexts/service-profiles/management/infra/IMPLEMENTATION_NOTES.md"

echo -e "\n${GREEN}Ready to start services!${NC} 🚀"
echo -e "Run ${CYAN}./start-service.sh <service>${NC} to start a bounded context.\n"
