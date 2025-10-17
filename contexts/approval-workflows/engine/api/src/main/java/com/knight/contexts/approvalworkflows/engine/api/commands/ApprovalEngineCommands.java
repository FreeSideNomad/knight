package com.knight.contexts.approvalworkflows.engine.api.commands;

import java.math.BigDecimal;

/**
 * Command interface for Approval Engine bounded context.
 * Handles approval workflow orchestration for all services requiring approval.
 */
public interface ApprovalEngineCommands {

    /**
     * Start a new approval workflow for a business action.
     * Returns workflowId for tracking.
     */
    String startApprovalWorkflow(StartApprovalWorkflowCmd cmd);

    /**
     * Approve a pending workflow (called by eligible approver).
     */
    void approveWorkflow(ApproveWorkflowCmd cmd);

    /**
     * Reject a pending workflow (called by eligible approver).
     */
    void rejectWorkflow(RejectWorkflowCmd cmd);

    /**
     * Cancel a workflow (called by requester or admin).
     */
    void cancelWorkflow(CancelWorkflowCmd cmd);

    record StartApprovalWorkflowCmd(
        String statementId,           // Business entity ID (e.g., receivable statement ID)
        String profileId,             // Indirect client profile ID
        String requesterId,           // User initiating the request
        String action,                // Action requiring approval (e.g., "SUBMIT_RECEIVABLE")
        String resource,              // Resource URN being acted upon
        BigDecimal amount             // Amount if applicable (for threshold rules)
    ) {}

    record ApproveWorkflowCmd(
        String workflowId,
        String approverId,
        String comment
    ) {}

    record RejectWorkflowCmd(
        String workflowId,
        String approverId,
        String reason
    ) {}

    record CancelWorkflowCmd(
        String workflowId,
        String cancelledBy,
        String reason
    ) {}
}
