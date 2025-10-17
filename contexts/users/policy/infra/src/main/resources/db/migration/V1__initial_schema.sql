-- Policy Management Schema
-- V1: Initial tables for policies

CREATE TABLE policies (
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

CREATE INDEX idx_policies_type ON policies(policy_type);
CREATE INDEX idx_policies_subject ON policies(subject);
CREATE INDEX idx_policies_resource ON policies(resource);
