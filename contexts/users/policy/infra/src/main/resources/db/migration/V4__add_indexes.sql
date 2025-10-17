-- V4: Add additional performance indexes

-- Composite index for policy queries by subject and type
CREATE INDEX idx_policies_subject_type ON policy.policies(subject, policy_type);

-- Index for policy queries by action (permission enforcement)
CREATE INDEX idx_policies_action ON policy.policies(action);

-- Comments
COMMENT ON INDEX policy.idx_policies_subject_type IS 'Composite index for efficient subject-type queries';
COMMENT ON INDEX policy.idx_policies_action IS 'Index for permission enforcement queries';
