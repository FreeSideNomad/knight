-- Approval Workflow Engine Schema
-- V1: Initial tables for workflows and approvals

CREATE TABLE approval_workflows (
    workflow_id VARCHAR(255) PRIMARY KEY,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(255) NOT NULL,
    required_approvals INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    initiated_by VARCHAR(255) NOT NULL,
    initiated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_workflows_resource ON approval_workflows(resource_type, resource_id);
CREATE INDEX idx_workflows_status ON approval_workflows(status);

CREATE TABLE approvals (
    approval_id VARCHAR(255) PRIMARY KEY,
    workflow_id VARCHAR(255) NOT NULL REFERENCES approval_workflows(workflow_id),
    approver_user_id VARCHAR(255) NOT NULL,
    decision VARCHAR(50) NOT NULL,
    comments TEXT,
    approved_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_approvals_workflow ON approvals(workflow_id);
CREATE INDEX idx_approvals_approver ON approvals(approver_user_id);
