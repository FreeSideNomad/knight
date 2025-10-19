-- User Management Schema
-- V1: Create schema and initial users table

CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE users.users (
    user_id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    user_type VARCHAR(20) NOT NULL,
    identity_provider VARCHAR(20) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    lock_reason VARCHAR(500),
    deactivation_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX idx_users_email ON users.users(email);
CREATE INDEX idx_users_client ON users.users(client_id);
CREATE INDEX idx_users_status ON users.users(status);
CREATE INDEX idx_users_user_type ON users.users(user_type);

-- Comments
COMMENT ON TABLE users.users IS 'User aggregate root - manages user lifecycle for direct and indirect clients';
COMMENT ON COLUMN users.users.status IS 'PENDING, ACTIVE, LOCKED, DEACTIVATED';
COMMENT ON COLUMN users.users.user_type IS 'DIRECT, INDIRECT';
COMMENT ON COLUMN users.users.identity_provider IS 'OKTA, A_AND_P';

-- Outbox table for reliable event publishing
CREATE TABLE users.outbox (
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

CREATE INDEX idx_outbox_status ON users.outbox(status);
CREATE INDEX idx_outbox_created_at ON users.outbox(created_at);

-- Inbox table for idempotent event consumption
CREATE TABLE users.inbox (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message TEXT
);

CREATE INDEX idx_inbox_status ON users.inbox(status);
CREATE INDEX idx_inbox_received_at ON users.inbox(received_at);
