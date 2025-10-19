-- Indirect Client Management Schema
-- V1: Initial tables for indirect clients and related persons

CREATE SCHEMA IF NOT EXISTS indirect_clients;

CREATE TABLE indirect_clients.indirect_clients (
    indirect_client_id VARCHAR(255) PRIMARY KEY,
    parent_client_id VARCHAR(255) NOT NULL,
    client_type VARCHAR(20) NOT NULL,
    business_name VARCHAR(255),
    tax_id VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_indirect_clients_parent ON indirect_clients.indirect_clients(parent_client_id);
CREATE INDEX idx_indirect_clients_status ON indirect_clients.indirect_clients(status);
CREATE INDEX idx_indirect_clients_type ON indirect_clients.indirect_clients(client_type);

CREATE TABLE indirect_clients.related_persons (
    person_id VARCHAR(255) PRIMARY KEY,
    indirect_client_id VARCHAR(255) NOT NULL REFERENCES indirect_clients.indirect_clients(indirect_client_id),
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100),
    email VARCHAR(255),
    added_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_related_persons_client ON indirect_clients.related_persons(indirect_client_id);
CREATE INDEX idx_related_persons_role ON indirect_clients.related_persons(role);

-- Outbox table for reliable event publishing
CREATE TABLE indirect_clients.outbox (
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

CREATE INDEX idx_outbox_status ON indirect_clients.outbox(status);
CREATE INDEX idx_outbox_created_at ON indirect_clients.outbox(created_at);

-- Inbox table for idempotent event consumption
CREATE TABLE indirect_clients.inbox (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    error_message TEXT
);

CREATE INDEX idx_inbox_status ON indirect_clients.inbox(status);
CREATE INDEX idx_inbox_received_at ON indirect_clients.inbox(received_at);
