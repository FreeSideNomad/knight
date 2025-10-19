-- Policy Management Schema
-- V1: Create schema and initial policies table

CREATE SCHEMA IF NOT EXISTS policy;

CREATE TABLE policy.policies (
    policy_id VARCHAR(255) PRIMARY KEY,
    policy_type VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    resource VARCHAR(500) NOT NULL,
    approver_count INT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX idx_policies_type ON policy.policies(policy_type);
CREATE INDEX idx_policies_subject ON policy.policies(subject);
CREATE INDEX idx_policies_resource ON policy.policies(resource);

-- Comments
COMMENT ON TABLE policy.policies IS 'Policy aggregate root - manages permission and approval policies';
COMMENT ON COLUMN policy.policies.policy_type IS 'PERMISSION, APPROVAL';

-- Outbox table for reliable event publishing
CREATE TABLE policy.outbox (
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

CREATE INDEX idx_outbox_status ON policy.outbox(status);
CREATE INDEX idx_outbox_created_at ON policy.outbox(created_at);

-- Inbox table for idempotent event consumption
CREATE TABLE policy.inbox (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message TEXT
);

CREATE INDEX idx_inbox_status ON policy.inbox(status);
CREATE INDEX idx_inbox_received_at ON policy.inbox(received_at);
