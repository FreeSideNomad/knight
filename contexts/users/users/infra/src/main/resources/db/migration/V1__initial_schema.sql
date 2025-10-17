-- User Management Schema
-- V1: Create schema and initial users table

CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE users.users (
    id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    user_type VARCHAR(20) NOT NULL,
    identity_provider VARCHAR(20) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    lock_reason TEXT,
    deactivation_reason TEXT,
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
