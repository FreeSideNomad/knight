-- V5: Additional performance indexes for outbox/inbox

-- Composite index for outbox cleanup (find old published events)
CREATE INDEX idx_outbox_cleanup
ON outbox(status, published_at)
WHERE published_at IS NOT NULL;

-- Composite index for failed event analysis
CREATE INDEX idx_outbox_failures
ON outbox(status, retry_count)
WHERE status = 'FAILED';

-- Inbox cleanup index (find old processed events)
CREATE INDEX idx_inbox_cleanup
ON inbox(status, processed_at)
WHERE processed_at IS NOT NULL;

-- Partial index for pending events (most common query)
CREATE INDEX idx_outbox_pending
ON outbox(created_at)
WHERE status = 'PENDING';

-- Statistics for query planner
ANALYZE outbox;
ANALYZE inbox;
