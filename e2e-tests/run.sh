#!/bin/bash
# E2E Test Runner with infrastructure checks and log capture

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Knight Platform E2E Tests ===${NC}"

# Change to e2e-tests directory
cd "$(dirname "$0")"

# Check if docker-compose services are running
echo -e "\n${YELLOW}Checking infrastructure...${NC}"

check_service() {
    local service=$1
    if docker ps --format '{{.Names}}' | grep -q "^${service}$"; then
        echo -e "${GREEN}✓${NC} $service is running"
        return 0
    else
        echo -e "${RED}✗${NC} $service is NOT running"
        return 1
    fi
}

# Check required services
ALL_RUNNING=true
check_service "knight-postgres" || ALL_RUNNING=false
check_service "knight-kafka" || ALL_RUNNING=false
check_service "knight-zookeeper" || ALL_RUNNING=false

if [ "$ALL_RUNNING" = false ]; then
    echo -e "\n${RED}ERROR: Required services are not running${NC}"
    echo -e "${YELLOW}Please start services first:${NC}"
    echo -e "  cd /Users/igor/code/knight && docker-compose up -d"
    exit 1
fi

# Wait for services to be healthy
echo -e "\n${YELLOW}Waiting for services to be healthy...${NC}"

wait_for_postgres() {
    local retries=30
    local count=0
    while [ $count -lt $retries ]; do
        if docker exec knight-postgres pg_isready -U knight -d knight > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC} PostgreSQL is ready"
            return 0
        fi
        count=$((count + 1))
        sleep 1
    done
    echo -e "${RED}✗${NC} PostgreSQL failed to become ready"
    return 1
}

wait_for_kafka() {
    local retries=30
    local count=0
    while [ $count -lt $retries ]; do
        if docker exec knight-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC} Kafka is ready"
            return 0
        fi
        count=$((count + 1))
        sleep 1
    done
    echo -e "${RED}✗${NC} Kafka failed to become ready"
    return 1
}

wait_for_postgres || exit 1
wait_for_kafka || exit 1

# Check application services (optional - warn if not running)
echo -e "\n${YELLOW}Checking application services...${NC}"
APP_SERVICES=(
    "localhost:9500:service-profiles/management"
    "localhost:9501:service-profiles/indirect-clients"
    "localhost:9502:users/users"
    "localhost:9503:users/policy"
    "localhost:9504:approval-workflows/engine"
)

for service_info in "${APP_SERVICES[@]}"; do
    IFS=':' read -r host port name <<< "$service_info"
    if curl -s -f "http://${host}:${port}/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $name ($host:$port)"
    else
        echo -e "${YELLOW}⚠${NC} $name ($host:$port) - not responding (tests may fail)"
    fi
done

# Create logs directory
mkdir -p logs

# Activate virtual environment
if [ ! -d "venv" ]; then
    echo -e "\n${RED}ERROR: venv not found${NC}"
    echo -e "${YELLOW}Please create venv first:${NC}"
    echo -e "  cd e2e-tests && python3 -m venv venv && source venv/bin/activate && pip install -r requirements.txt"
    exit 1
fi

source venv/bin/activate

# Parse command line arguments
PYTEST_ARGS="${PYTEST_ARGS:-}"
if [ $# -gt 0 ]; then
    PYTEST_ARGS="$@"
fi

# Run tests
echo -e "\n${GREEN}=== Running E2E Tests ===${NC}"
echo -e "${YELLOW}Command:${NC} pytest scenarios/ ${PYTEST_ARGS}"
echo ""

# Run pytest with HTML report
pytest scenarios/ \
    --verbose \
    --html=logs/report.html \
    --self-contained-html \
    --tb=short \
    --log-cli-level=INFO \
    ${PYTEST_ARGS}

TEST_EXIT_CODE=$?

# Print summary
echo -e "\n${GREEN}=== Test Summary ===${NC}"
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed${NC}"
    echo -e "\nHTML Report: ${GREEN}e2e-tests/logs/report.html${NC}"
else
    echo -e "${RED}✗ Some tests failed${NC}"
    echo -e "\nHTML Report: ${YELLOW}e2e-tests/logs/report.html${NC}"
    echo -e "\n${YELLOW}=== Recent Application Logs ===${NC}"

    # Show recent logs from docker services
    echo -e "\n${YELLOW}--- PostgreSQL (last 30 lines) ---${NC}"
    docker logs --tail 30 knight-postgres 2>&1 | tail -20

    echo -e "\n${YELLOW}--- Kafka (last 30 lines) ---${NC}"
    docker logs --tail 30 knight-kafka 2>&1 | tail -20
fi

echo -e "\n${YELLOW}Logs captured in:${NC} e2e-tests/logs/"
ls -lh logs/

exit $TEST_EXIT_CODE
