-- Indirect Client Management Schema
-- V1: Initial tables for indirect clients and related persons

CREATE TABLE indirect_clients (
    indirect_client_id VARCHAR(255) PRIMARY KEY,
    parent_client_id VARCHAR(255) NOT NULL,
    client_type VARCHAR(50) NOT NULL,
    business_name VARCHAR(500),
    tax_id VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_indirect_clients_parent ON indirect_clients(parent_client_id);
CREATE INDEX idx_indirect_clients_status ON indirect_clients(status);
CREATE INDEX idx_indirect_clients_type ON indirect_clients(client_type);

CREATE TABLE related_persons (
    person_id VARCHAR(255) PRIMARY KEY,
    indirect_client_id VARCHAR(255) NOT NULL REFERENCES indirect_clients(indirect_client_id),
    name VARCHAR(500) NOT NULL,
    role VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    added_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_related_persons_client ON related_persons(indirect_client_id);
CREATE INDEX idx_related_persons_role ON related_persons(role);
