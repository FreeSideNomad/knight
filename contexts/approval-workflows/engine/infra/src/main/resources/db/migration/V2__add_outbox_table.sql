-- V2: Create outbox table for reliable event publishing

CREATE TABLE approvals.outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    correlation_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    error_message TEXT
);

-- Index for polling pending events
CREATE INDEX idx_outbox_status_created ON approvals.outbox(status, created_at);

-- Index for aggregate queries (debugging)
CREATE INDEX idx_outbox_aggregate ON approvals.outbox(aggregate_type, aggregate_id);

-- Index for correlation tracking
CREATE INDEX idx_outbox_correlation ON approvals.outbox(correlation_id) WHERE correlation_id IS NOT NULL;

-- Comments
COMMENT ON TABLE approvals.outbox IS 'Outbox pattern for reliable event publishing to Kafka';
COMMENT ON COLUMN approvals.outbox.status IS 'PENDING, PUBLISHED, FAILED';
