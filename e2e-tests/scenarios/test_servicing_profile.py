"""E2E tests for Servicing Profile Management"""
import pytest
import logging
import time

logger = logging.getLogger(__name__)


@pytest.mark.smoke
class TestServicingProfileE2E:
    """End-to-end tests for servicing profile creation and management"""

    def test_create_servicing_profile_e2e(self, management_api, db, kafka, cleanup_servicing_profiles):
        """
        E2E: Create servicing profile → verify DB → verify outbox → verify Kafka event

        This test covers the complete flow:
        1. POST /commands/service-profiles/servicing/create
        2. Verify profile persisted in spm.servicing_profiles table
        3. Verify outbox entry created in spm.outbox table
        4. Verify Kafka event published (if OutboxPublisher is running)
        """
        # Given
        client_urn = "srf:12345"  # Valid ClientId format: {system}:{client_number}
        created_by = "alice@test.com"
        payload = {
            "clientUrn": client_urn,
            "createdBy": created_by
        }

        # When - Call API to create servicing profile
        logger.info(f"Creating servicing profile for client: {client_urn}")
        response = management_api.post("/commands/service-profiles/servicing/create", json=payload)

        # Then - Assert HTTP 200 response
        assert response.status_code == 200, f"Expected 200, got {response.status_code}: {response.text}"

        # Then - Assert response contains profile URN
        response_data = response.json()
        assert "profileUrn" in response_data, "Response should contain profileUrn"
        profile_urn = response_data["profileUrn"]
        assert profile_urn.startswith("servicing:"), f"Invalid profile URN: {profile_urn}"
        logger.info(f"Created profile: {profile_urn}")

        # Then - Verify profile exists in database
        count = db.count("servicing_profiles", "spm", where="profile_id = %s", params=(profile_urn,))
        assert count == 1, f"Expected 1 profile in DB, found {count}"

        # Then - Verify profile fields in database
        profile = db.query_one(
            "SELECT profile_id, client_id, status, created_by FROM spm.servicing_profiles WHERE profile_id = %s",
            (profile_urn,)
        )
        assert profile is not None, "Profile should exist in database"
        assert profile[0] == profile_urn
        assert profile[1] == client_urn
        assert profile[2] == "PENDING"  # Initial status before service enrollment
        assert profile[3] == created_by
        logger.info(f"Verified profile in database: {profile}")

        # Then - Verify outbox entry exists
        outbox_count = db.count(
            "outbox",
            "spm",
            where="aggregate_id = %s AND event_type = 'ServicingProfileCreated'",
            params=(profile_urn,)
        )
        assert outbox_count == 1, f"Expected 1 outbox entry, found {outbox_count}"
        logger.info(f"Verified outbox entry for profile: {profile_urn}")

        # Optional - Verify Kafka event (if OutboxPublisher is running)
        # Note: This may timeout if OutboxPublisher is not active
        # Uncomment when OutboxPublisher is implemented
        """
        event = kafka.consume_one(
            topic="servicing-profile-events",
            timeout=10,
            filter_fn=lambda msg: msg.get("profileId") == profile_urn
        )
        if event:
            assert event["eventType"] == "ServicingProfileCreated"
            assert event["profileId"] == profile_urn
            logger.info(f"Verified Kafka event: {event}")
        else:
            logger.warning("Kafka event not found (OutboxPublisher may not be running)")
        """

    def test_enroll_service_e2e(self, management_api, db, cleanup_servicing_profiles):
        """
        E2E: Create profile → enroll service → verify DB

        This test covers:
        1. Create servicing profile
        2. POST /commands/service-profiles/servicing/enroll-service
        3. Verify service enrollment persisted
        """
        # Given - Create a servicing profile first
        create_response = management_api.post(
            "/commands/service-profiles/servicing/create",
            json={"clientUrn": "srf:67890", "createdBy": "bob@test.com"}
        )
        assert create_response.status_code == 200
        profile_urn = create_response.json()["profileUrn"]
        logger.info(f"Created profile for enrollment test: {profile_urn}")

        # When - Enroll a service
        enroll_payload = {
            "profileUrn": profile_urn,
            "serviceType": "RECEIVABLES",
            "configurationJson": '{"threshold": 1000, "currency": "CAD"}'
        }
        logger.info(f"Enrolling service for profile: {profile_urn}")
        enroll_response = management_api.post(
            "/commands/service-profiles/servicing/enroll-service",
            json=enroll_payload
        )

        # Then - Assert HTTP 200 response
        assert enroll_response.status_code == 200, f"Expected 200, got {enroll_response.status_code}"

        # Then - Verify service enrollment exists in database
        # Note: Need to wait a moment for async persistence
        time.sleep(0.5)

        enrollment_count = db.count(
            "service_enrollments",
            "spm",
            where="profile_id = %s AND service_type = %s",
            params=(profile_urn, "RECEIVABLES")
        )
        assert enrollment_count == 1, f"Expected 1 service enrollment, found {enrollment_count}"

        # Then - Verify enrollment configuration
        enrollment = db.query_one(
            "SELECT service_type, configuration, status FROM spm.service_enrollments WHERE profile_id = %s",
            (profile_urn,)
        )
        assert enrollment is not None
        assert enrollment[0] == "RECEIVABLES"
        assert '"threshold": 1000' in enrollment[1]  # Check JSON contains expected field
        assert enrollment[2] == "ACTIVE"
        logger.info(f"Verified service enrollment: {enrollment}")

    def test_enroll_account_e2e(self, management_api, db, cleanup_servicing_profiles):
        """
        E2E: Create profile → enroll service → enroll account → verify DB

        This test covers:
        1. Create servicing profile
        2. Enroll service
        3. POST /commands/service-profiles/servicing/enroll-account
        4. Verify account enrollment persisted
        """
        # Given - Create profile and enroll service
        create_response = management_api.post(
            "/commands/service-profiles/servicing/create",
            json={"clientUrn": "srf:99999", "createdBy": "charlie@test.com"}
        )
        profile_urn = create_response.json()["profileUrn"]

        management_api.post(
            "/commands/service-profiles/servicing/enroll-service",
            json={
                "profileUrn": profile_urn,
                "serviceType": "RECEIVABLES",
                "configurationJson": "{}"
            }
        )
        time.sleep(0.5)  # Wait for service enrollment to persist

        # Get service enrollment ID
        service_enrollment = db.query_one(
            "SELECT enrollment_id FROM spm.service_enrollments WHERE profile_id = %s",
            (profile_urn,)
        )
        service_enrollment_id = service_enrollment[0]
        logger.info(f"Service enrollment ID: {service_enrollment_id}")

        # When - Enroll an account
        account_payload = {
            "profileUrn": profile_urn,
            "serviceEnrollmentId": service_enrollment_id,
            "accountId": "DDA-123456"
        }
        logger.info(f"Enrolling account for profile: {profile_urn}")
        account_response = management_api.post(
            "/commands/service-profiles/servicing/enroll-account",
            json=account_payload
        )

        # Then - Assert HTTP 200 response
        assert account_response.status_code == 200, f"Expected 200, got {account_response.status_code}"

        # Then - Verify account enrollment exists in database
        time.sleep(0.5)

        account_enrollment_count = db.count(
            "account_enrollments",
            "spm",
            where="profile_id = %s AND account_id = %s",
            params=(profile_urn, "DDA-123456")
        )
        assert account_enrollment_count == 1, f"Expected 1 account enrollment, found {account_enrollment_count}"

        # Then - Verify enrollment details
        account_enrollment = db.query_one(
            "SELECT account_id, status, service_enrollment_id FROM spm.account_enrollments WHERE profile_id = %s",
            (profile_urn,)
        )
        assert account_enrollment is not None
        assert account_enrollment[0] == "DDA-123456"
        assert account_enrollment[1] == "ACTIVE"
        assert account_enrollment[2] == service_enrollment_id
        logger.info(f"Verified account enrollment: {account_enrollment}")
