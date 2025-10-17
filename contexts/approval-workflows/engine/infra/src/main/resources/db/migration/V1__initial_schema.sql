-- Approval Workflow Engine Schema
-- V1: Create schema and initial tables for workflows and approvals

CREATE SCHEMA IF NOT EXISTS approvals;

CREATE TABLE approvals.approval_workflows (
    workflow_id VARCHAR(255) PRIMARY KEY,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(255) NOT NULL,
    required_approvals INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    initiated_by VARCHAR(255) NOT NULL,
    initiated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_workflows_resource ON approvals.approval_workflows(resource_type, resource_id);
CREATE INDEX idx_workflows_status ON approvals.approval_workflows(status);
CREATE INDEX idx_workflows_initiated_by ON approvals.approval_workflows(initiated_by);

CREATE TABLE approvals.approvals (
    approval_id VARCHAR(255) PRIMARY KEY,
    workflow_id VARCHAR(255) NOT NULL REFERENCES approvals.approval_workflows(workflow_id),
    approver_user_id VARCHAR(255) NOT NULL,
    decision VARCHAR(20) NOT NULL,
    comments TEXT,
    approved_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_approvals_workflow ON approvals.approvals(workflow_id);
CREATE INDEX idx_approvals_approver ON approvals.approvals(approver_user_id);

-- Comments
COMMENT ON TABLE approvals.approval_workflows IS 'ApprovalWorkflow aggregate root - generic workflow execution engine';
COMMENT ON COLUMN approvals.approval_workflows.status IS 'PENDING, APPROVED, REJECTED, EXPIRED';
COMMENT ON TABLE approvals.approvals IS 'Approval entity - individual approvals within a workflow';
COMMENT ON COLUMN approvals.approvals.decision IS 'APPROVE, REJECT';
