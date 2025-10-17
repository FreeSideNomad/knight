package com.knight.contexts.approvalworkflows.engine.api.queries;

public interface ApprovalWorkflowQueries {

    record ApprovalWorkflowSummary(
        String workflowId,
        String status,
        String resourceType,
        String resourceId,
        int requiredApprovals,
        int receivedApprovals
    ) {}

    ApprovalWorkflowSummary getWorkflowSummary(String workflowId);
}
