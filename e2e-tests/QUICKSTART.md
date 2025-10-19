# E2E Tests - Quick Start Guide

## Setup (One-Time)

```bash
cd /Users/igor/code/knight/e2e-tests

# Create venv (if not exists)
python3 -m venv venv

# Activate venv
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

## Running Tests

### 1. Start Infrastructure

```bash
cd /Users/igor/code/knight
docker-compose up -d
```

### 2. Start Application Services

You need at least the service-profiles/management service running on port 9500 for the tests to work.

```bash
# Option 1: Use start-service.sh
./start-service.sh  # Select service-profiles/management

# Option 2: Run manually
cd contexts/service-profiles/management/infra
mvn mn:run
```

### 3. Run E2E Tests

```bash
cd /Users/igor/code/knight/e2e-tests
./run.sh
```

That's it! The `run.sh` script will:
- ✅ Check infrastructure (postgres, kafka)
- ✅ Check application services
- ✅ Run all E2E tests
- ✅ Capture logs automatically
- ✅ Generate HTML report

## Test Results

- **Console Output**: See pass/fail in terminal
- **HTML Report**: `e2e-tests/logs/report.html`
- **Docker Logs**: `e2e-tests/logs/knight-*.log`

## Common Commands

```bash
# Run specific test
./run.sh scenarios/test_servicing_profile.py::TestServicingProfileE2E::test_create_servicing_profile_e2e

# Run with verbose output
./run.sh -vv

# Run only smoke tests
./run.sh -m smoke

# Run in parallel (4 workers)
./run.sh -n 4
```

## Troubleshooting

### Tests fail with connection error

**Check PostgreSQL:**
```bash
docker ps | grep postgres
docker exec knight-postgres pg_isready -U knight
```

**Check Kafka:**
```bash
docker ps | grep kafka
docker exec knight-kafka kafka-broker-api-versions --bootstrap-server localhost:9092
```

**Check Application Service:**
```bash
curl http://localhost:9500/health
```

### View logs after test failure

```bash
# View captured logs
ls -lh logs/
cat logs/knight-postgres.log
cat logs/knight-kafka.log

# View HTML report
open logs/report.html
```

### Database inspection

```bash
# Connect to PostgreSQL
docker exec -it knight-postgres psql -U knight -d knight

# Check what's in database
\dt spm.*
SELECT * FROM spm.servicing_profiles;
SELECT * FROM spm.outbox;
```

### Clean up test data

```bash
# Truncate all test tables
docker exec knight-postgres psql -U knight -d knight -c "
  TRUNCATE TABLE spm.servicing_profiles CASCADE;
  TRUNCATE TABLE spm.outbox;
  TRUNCATE TABLE spm.inbox;
"
```

## Architecture

```
e2e-tests/
├── lib/                         # Reusable test libraries
│   ├── api_client.py           # HTTP client with retry
│   ├── kafka_client.py         # Kafka consumer (docker exec)
│   └── db_client.py            # PostgreSQL client
├── scenarios/                   # Test scenarios
│   └── test_servicing_profile.py
├── conftest.py                  # Pytest fixtures
├── pytest.ini                   # Pytest configuration
├── run.sh                       # Test runner script
└── logs/                        # Auto-generated logs
```

## What's Tested

**Current:**
- ✅ Create servicing profile → DB → Outbox
- ✅ Enroll service → DB
- ✅ Enroll account → DB

**Coming Soon:**
- 🚧 Approval workflows
- 🚧 Indirect client onboarding
- 🚧 Kafka event verification
- 🚧 Cross-context integration
