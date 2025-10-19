# E2E Testing Framework

End-to-end testing framework for the Knight Platform using Python, pytest, and simple tooling.

## Architecture

This framework tests the complete flow across all 5 bounded contexts:

1. **service-profiles/management** (port 9500)
2. **service-profiles/indirect-clients** (port 9501)
3. **users/users** (port 9502)
4. **users/policy** (port 9503)
5. **approval-workflows/engine** (port 9504)

## Components

- **lib/api_client.py** - HTTP client with retry logic
- **lib/kafka_client.py** - Kafka consumer for event verification
- **lib/db_client.py** - PostgreSQL client for database assertions
- **scenarios/** - E2E test scenarios
- **conftest.py** - Pytest fixtures and configuration
- **run.sh** - Test runner with infrastructure checks and log capture

## Prerequisites

1. **Infrastructure running:**
   ```bash
   cd /Users/igor/code/knight
   docker-compose up -d
   ```

2. **Python venv setup (first time only):**
   ```bash
   cd e2e-tests
   python3 -m venv venv
   source venv/bin/activate
   pip install -r requirements.txt
   ```

3. **Application services running:**
   - Start all 5 Micronaut services on ports 9500-9504
   - Or use the start-service.sh script

## Running Tests

### Run all tests
```bash
./run.sh
```

### Run specific test file
```bash
./run.sh scenarios/test_servicing_profile.py
```

### Run specific test
```bash
./run.sh scenarios/test_servicing_profile.py::TestServicingProfileE2E::test_create_servicing_profile_e2e
```

### Run with markers
```bash
./run.sh -m smoke        # Only smoke tests
./run.sh -m integration  # Only integration tests
```

### Run in parallel
```bash
./run.sh -n 4  # Run with 4 workers
```

### Run with verbose output
```bash
./run.sh -vv
```

## Test Structure

### Example Test
```python
def test_create_servicing_profile_e2e(self, management_api, db, kafka, cleanup_servicing_profiles):
    """E2E: Create profile → verify DB → verify outbox → verify Kafka event"""

    # Given
    payload = {"clientUrn": "urn:client:CAN-12345", "createdBy": "alice"}

    # When - Call API
    response = management_api.post("/commands/service-profiles/servicing/create", json=payload)

    # Then - Assert HTTP response
    assert response.status_code == 200
    profile_urn = response.json()["profileUrn"]

    # Then - Verify database
    count = db.count("servicing_profiles", "spm", where="profile_id = %s", params=(profile_urn,))
    assert count == 1

    # Then - Verify outbox entry
    outbox_count = db.count("outbox", "spm", where="aggregate_id = %s", params=(profile_urn,))
    assert outbox_count == 1

    # Then - Verify Kafka event (optional)
    event = kafka.consume_one(
        topic="servicing-profile-events",
        timeout=10,
        filter_fn=lambda msg: msg.get("profileId") == profile_urn
    )
    assert event is not None
```

## Available Fixtures

### API Clients
- `management_api` - Service Profile Management API (port 9500)
- `indirect_clients_api` - Indirect Clients API (port 9501)
- `users_api` - Users API (port 9502)
- `policy_api` - Policy API (port 9503)
- `approval_api` - Approval Workflows API (port 9504)

### Infrastructure Clients
- `db` - PostgreSQL database client
- `kafka` - Kafka consumer client

### Cleanup Fixtures
- `cleanup_servicing_profiles` - Auto-cleanup after test
- `cleanup_indirect_clients` - Auto-cleanup after test
- `cleanup_users` - Auto-cleanup after test
- `cleanup_policies` - Auto-cleanup after test
- `cleanup_approval_workflows` - Auto-cleanup after test

## Troubleshooting

### Check infrastructure
```bash
docker ps  # Verify postgres, kafka, zookeeper running
docker logs knight-postgres  # Check PostgreSQL logs
docker logs knight-kafka     # Check Kafka logs
```

### Check application services
```bash
curl http://localhost:9500/health  # Management service
curl http://localhost:9501/health  # Indirect clients service
curl http://localhost:9502/health  # Users service
curl http://localhost:9503/health  # Policy service
curl http://localhost:9504/health  # Approval workflows service
```

### View test logs
```bash
cat logs/knight-postgres.log
cat logs/knight-kafka.log
open logs/report.html  # HTML test report
```

### Database inspection
```bash
# Connect to PostgreSQL
docker exec -it knight-postgres psql -U knight -d knight

# Check schemas
\dn

# Check tables in a schema
\dt spm.*

# Query data
SELECT * FROM spm.servicing_profiles;
SELECT * FROM spm.outbox;
```

### Kafka inspection
```bash
# List topics
docker exec knight-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Consume from topic
docker exec knight-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic servicing-profile-events \
  --from-beginning
```

## Writing New Tests

1. Create new test file in `scenarios/` (e.g., `test_approval_workflow.py`)
2. Import fixtures from conftest.py
3. Use pytest class structure with descriptive test names
4. Follow AAA pattern: Arrange, Act, Assert
5. Use appropriate cleanup fixture
6. Add markers (`@pytest.mark.smoke`, `@pytest.mark.integration`)

## Log Capture

The framework automatically captures logs from:
- PostgreSQL container
- Kafka container
- Application services (on test failure)

Logs are saved to `logs/` directory with timestamps.

## CI/CD Integration

```yaml
# Example GitHub Actions
- name: Run E2E Tests
  run: |
    docker-compose up -d
    cd e2e-tests
    ./run.sh

- name: Upload Test Results
  if: always()
  uses: actions/upload-artifact@v3
  with:
    name: e2e-test-results
    path: e2e-tests/logs/
```

## Test Scenarios Covered

- ✅ **Servicing Profile Management**
  - Create profile → DB → Outbox → Kafka
  - Enroll service → DB
  - Enroll account → DB

- 🚧 **Approval Workflows** (TODO)
  - Initiate workflow → DB → Kafka
  - Record approval → Status change
  - Dual approval completion

- 🚧 **Indirect Client Onboarding** (TODO)
  - Create indirect client → DB → Kafka
  - Add related person → DB

- 🚧 **Cross-Context Integration** (TODO)
  - Policy creation triggers approval workflow
  - Event propagation across contexts
