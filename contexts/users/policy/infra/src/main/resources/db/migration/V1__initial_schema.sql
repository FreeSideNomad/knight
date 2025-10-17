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
