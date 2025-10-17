-- V4: Add additional performance indexes

-- Composite index for workflow queries by resource and status
CREATE INDEX idx_workflows_resource_status ON approvals.approval_workflows(resource_type, resource_id, status);

-- Index for time-based workflow queries
CREATE INDEX idx_workflows_initiated_at ON approvals.approval_workflows(initiated_at);

-- Index for completed workflows
CREATE INDEX idx_workflows_completed_at ON approvals.approval_workflows(completed_at) WHERE completed_at IS NOT NULL;

-- Comments
COMMENT ON INDEX approvals.idx_workflows_resource_status IS 'Composite index for efficient resource-status queries';
COMMENT ON INDEX approvals.idx_workflows_initiated_at IS 'Index for time-based workflow queries';
COMMENT ON INDEX approvals.idx_workflows_completed_at IS 'Partial index for completed workflows only';
