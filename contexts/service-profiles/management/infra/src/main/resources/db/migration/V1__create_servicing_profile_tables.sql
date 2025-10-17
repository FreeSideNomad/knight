-- Service Profile Management Schema
-- V1: Initial tables for servicing profiles, service enrollments, and account enrollments

CREATE TABLE servicing_profiles (
    profile_urn VARCHAR(255) PRIMARY KEY,
    client_urn VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_servicing_profiles_client ON servicing_profiles(client_urn);
CREATE INDEX idx_servicing_profiles_status ON servicing_profiles(status);

CREATE TABLE service_enrollments (
    enrollment_id VARCHAR(255) PRIMARY KEY,
    profile_urn VARCHAR(255) NOT NULL REFERENCES servicing_profiles(profile_urn),
    service_type VARCHAR(100) NOT NULL,
    configuration TEXT,
    status VARCHAR(50) NOT NULL,
    enrolled_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_service_enrollments_profile ON service_enrollments(profile_urn);

CREATE TABLE account_enrollments (
    enrollment_id VARCHAR(255) PRIMARY KEY,
    service_enrollment_id VARCHAR(255) NOT NULL REFERENCES service_enrollments(enrollment_id),
    account_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    enrolled_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_account_enrollments_service ON account_enrollments(service_enrollment_id);
CREATE INDEX idx_account_enrollments_account ON account_enrollments(account_id);
