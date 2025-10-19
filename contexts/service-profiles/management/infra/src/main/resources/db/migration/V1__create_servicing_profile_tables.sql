-- Service Profile Management Schema
-- V1: Initial tables for servicing profiles, service enrollments, and account enrollments

CREATE SCHEMA IF NOT EXISTS spm;

CREATE TABLE spm.servicing_profiles (
    profile_id VARCHAR(255) PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_servicing_profiles_client ON spm.servicing_profiles(client_id);
CREATE INDEX idx_servicing_profiles_status ON spm.servicing_profiles(status);

CREATE TABLE spm.service_enrollments (
    enrollment_id VARCHAR(255) PRIMARY KEY,
    profile_id VARCHAR(255) NOT NULL REFERENCES spm.servicing_profiles(profile_id),
    service_type VARCHAR(100) NOT NULL,
    configuration TEXT,
    status VARCHAR(20) NOT NULL,
    enrolled_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_service_enrollments_profile ON spm.service_enrollments(profile_id);

CREATE TABLE spm.account_enrollments (
    enrollment_id VARCHAR(255) PRIMARY KEY,
    profile_id VARCHAR(255) NOT NULL REFERENCES spm.servicing_profiles(profile_id),
    service_enrollment_id VARCHAR(255) NOT NULL,
    account_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    enrolled_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_account_enrollments_profile ON spm.account_enrollments(profile_id);
CREATE INDEX idx_account_enrollments_service ON spm.account_enrollments(service_enrollment_id);
CREATE INDEX idx_account_enrollments_account ON spm.account_enrollments(account_id);

-- Outbox table for reliable event publishing
CREATE TABLE spm.outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    correlation_id UUID,
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    error_message TEXT
);

CREATE INDEX idx_outbox_status ON spm.outbox(status);
CREATE INDEX idx_outbox_created_at ON spm.outbox(created_at);

-- Inbox table for idempotent event consumption
CREATE TABLE spm.inbox (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message TEXT
);

CREATE INDEX idx_inbox_status ON spm.inbox(status);
CREATE INDEX idx_inbox_received_at ON spm.inbox(received_at);
