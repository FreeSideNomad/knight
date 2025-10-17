package com.knight.contexts.approvalworkflows.engine.api.queries;

import java.time.Instant;
import java.util.List;

/**
 * Query interface for Approval Engine bounded context.
 * Provides read models for approval workflows.
 */
public interface ApprovalEngineQueries {

    /**
     * Get current status of a workflow.
     */
    WorkflowStatus getWorkflowStatus(String workflowId);

    /**
     * Get pending approvals for a specific approver.
     */
    List<PendingApprovalSummary> getPendingApprovals(String approverId);

    /**
     * Get complete workflow history with all approval decisions.
     */
    WorkflowHistory getWorkflowHistory(String workflowId);

    record WorkflowStatus(
        String workflowId,
        String statementId,
        String profileId,
        String status,              // PENDING, APPROVED, REJECTED, EXPIRED, CANCELLED
        int requiredApprovals,
        int receivedApprovals,
        Instant createdAt,
        Instant completedAt
    ) {}

    record PendingApprovalSummary(
        String workflowId,
        String statementId,
        String profileId,
        String requesterId,
        String action,
        String resource,
        Instant requestedAt,
        int requiredApprovals,
        int receivedApprovals
    ) {}

    record WorkflowHistory(
        String workflowId,
        String statementId,
        String status,
        List<ApprovalDecision> decisions,
        Instant createdAt,
        Instant completedAt
    ) {}

    record ApprovalDecision(
        String approverId,
        String decision,            // APPROVED, REJECTED
        String comment,
        Instant decidedAt
    ) {}
}
