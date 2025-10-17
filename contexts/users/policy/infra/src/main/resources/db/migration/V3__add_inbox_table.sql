-- V3: Create inbox table for idempotent event consumption

CREATE TABLE policy.inbox (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message TEXT
);

-- Index for status queries
CREATE INDEX idx_inbox_status ON policy.inbox(status);

-- Index for time-based queries (cleanup old events)
CREATE INDEX idx_inbox_received ON policy.inbox(received_at);

-- Index for event type analytics
CREATE INDEX idx_inbox_event_type ON policy.inbox(event_type);

-- Comments
COMMENT ON TABLE policy.inbox IS 'Inbox pattern for idempotent event consumption from Kafka';
COMMENT ON COLUMN policy.inbox.status IS 'PENDING, PROCESSED, FAILED';
