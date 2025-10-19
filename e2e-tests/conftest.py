"""Pytest configuration and fixtures for E2E tests"""
import pytest
import subprocess
import os
import logging
from lib.api_client import ApiClient
from lib.kafka_client import KafkaTestConsumer
from lib.db_client import PostgresClient

logger = logging.getLogger(__name__)


# Service endpoints
SERVICES = {
    "management": "http://localhost:9500",
    "indirect-clients": "http://localhost:9501",
    "users": "http://localhost:9502",
    "policy": "http://localhost:9503",
    "approval-workflows": "http://localhost:9504"
}


@pytest.fixture(scope="session", autouse=True)
def capture_service_logs(request):
    """Automatically capture docker logs during test session"""
    log_dir = "logs"
    os.makedirs(log_dir, exist_ok=True)

    docker_services = [
        "knight-postgres",
        "knight-kafka",
        "knight-zookeeper"
    ]

    # Start log capture in background
    processes = []
    for service in docker_services:
        try:
            log_file = open(f"{log_dir}/{service}.log", "w")
            proc = subprocess.Popen(
                ["docker", "logs", "-f", service],
                stdout=log_file,
                stderr=subprocess.STDOUT
            )
            processes.append((proc, log_file))
            logger.info(f"Started log capture for {service}")
        except Exception as e:
            logger.warning(f"Could not capture logs for {service}: {e}")

    yield

    # Cleanup on test end
    for proc, log_file in processes:
        proc.kill()
        log_file.close()


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Attach recent docker logs to failed tests"""
    outcome = yield
    report = outcome.get_result()

    if report.when == "call" and report.failed:
        # Capture recent logs on failure
        logger.error(f"Test failed: {item.nodeid}")

        for service_name in ["knight-postgres", "knight-kafka"]:
            try:
                result = subprocess.run(
                    ["docker", "logs", "--tail", "50", service_name],
                    capture_output=True,
                    text=True,
                    timeout=5
                )
                log_section = f"\n=== {service_name} (last 50 lines) ===\n{result.stdout}"
                report.sections.append((f"{service_name} logs", log_section))
            except Exception as e:
                logger.warning(f"Could not capture logs for {service_name}: {e}")


# API Client Fixtures
@pytest.fixture
def management_api():
    """API client for service-profiles/management"""
    return ApiClient(SERVICES["management"])


@pytest.fixture
def indirect_clients_api():
    """API client for service-profiles/indirect-clients"""
    return ApiClient(SERVICES["indirect-clients"])


@pytest.fixture
def users_api():
    """API client for users/users"""
    return ApiClient(SERVICES["users"])


@pytest.fixture
def policy_api():
    """API client for users/policy"""
    return ApiClient(SERVICES["policy"])


@pytest.fixture
def approval_api():
    """API client for approval-workflows/engine"""
    return ApiClient(SERVICES["approval-workflows"])


# Kafka Client Fixture
@pytest.fixture
def kafka():
    """Kafka consumer client"""
    return KafkaTestConsumer("localhost:9093")


# Database Client Fixture
@pytest.fixture
def db():
    """PostgreSQL database client"""
    return PostgresClient(
        host="localhost",
        port=5432,
        database="knight",
        user="knight",
        password="knight"
    )


# Test data cleanup fixtures
@pytest.fixture
def cleanup_servicing_profiles(db):
    """Cleanup servicing profile test data after test"""
    yield
    try:
        db.truncate("servicing_profiles", "spm", cascade=True)
        db.truncate("service_enrollments", "spm", cascade=True)
        db.truncate("account_enrollments", "spm", cascade=True)
        db.truncate("outbox", "spm")
        db.truncate("inbox", "spm")
        logger.info("Cleaned up servicing profile test data")
    except Exception as e:
        logger.warning(f"Cleanup failed: {e}")


@pytest.fixture
def cleanup_indirect_clients(db):
    """Cleanup indirect client test data after test"""
    yield
    try:
        db.truncate("related_persons", "indirect_clients", cascade=True)
        db.truncate("indirect_clients", "indirect_clients", cascade=True)
        db.truncate("outbox", "indirect_clients")
        db.truncate("inbox", "indirect_clients")
        logger.info("Cleaned up indirect client test data")
    except Exception as e:
        logger.warning(f"Cleanup failed: {e}")


@pytest.fixture
def cleanup_users(db):
    """Cleanup user test data after test"""
    yield
    try:
        db.truncate("users", "users", cascade=True)
        db.truncate("outbox", "users")
        db.truncate("inbox", "users")
        logger.info("Cleaned up user test data")
    except Exception as e:
        logger.warning(f"Cleanup failed: {e}")


@pytest.fixture
def cleanup_policies(db):
    """Cleanup policy test data after test"""
    yield
    try:
        db.truncate("policies", "policy", cascade=True)
        db.truncate("outbox", "policy")
        db.truncate("inbox", "policy")
        logger.info("Cleaned up policy test data")
    except Exception as e:
        logger.warning(f"Cleanup failed: {e}")


@pytest.fixture
def cleanup_approval_workflows(db):
    """Cleanup approval workflow test data after test"""
    yield
    try:
        db.truncate("approvals", "approvals", cascade=True)
        db.truncate("approval_workflows", "approvals", cascade=True)
        db.truncate("outbox", "approvals")
        db.truncate("inbox", "approvals")
        logger.info("Cleaned up approval workflow test data")
    except Exception as e:
        logger.warning(f"Cleanup failed: {e}")
