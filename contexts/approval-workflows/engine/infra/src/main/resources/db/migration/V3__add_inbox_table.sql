-- V3: Create inbox table for idempotent event consumption

CREATE TABLE approvals.inbox (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message TEXT
);

-- Index for status queries
CREATE INDEX idx_inbox_status ON approvals.inbox(status);

-- Index for time-based queries (cleanup old events)
CREATE INDEX idx_inbox_received ON approvals.inbox(received_at);

-- Index for event type analytics
CREATE INDEX idx_inbox_event_type ON approvals.inbox(event_type);

-- Comments
COMMENT ON TABLE approvals.inbox IS 'Inbox pattern for idempotent event consumption from Kafka';
COMMENT ON COLUMN approvals.inbox.status IS 'PENDING, PROCESSED, FAILED';
