package com.knight.contexts.approvalworkflows.engine.domain.aggregate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * ApprovalWorkflow aggregate root.
 * Manages the lifecycle of an approval workflow for business actions requiring approval.
 *
 * Invariants:
 * - workflowId must be unique
 * - requester cannot approve their own workflow
 * - workflow must be PENDING to receive approvals
 * - workflow completes when requiredApprovals count is reached (parallel approval)
 * - any rejection immediately completes workflow with REJECTED status
 */
public class ApprovalWorkflow {

    public enum Status { PENDING, APPROVED, REJECTED, EXPIRED, CANCELLED }

    private final String workflowId;
    private final String statementId;       // Business entity ID
    private final String profileId;         // Indirect client profile ID
    private final String requesterId;       // User who initiated the request
    private final String action;            // Action requiring approval
    private final String resource;          // Resource URN
    private final BigDecimal amount;        // Amount if applicable
    private final int requiredApprovals;    // Number of approvals needed
    private final List<String> eligibleApprovers; // Who can approve
    private final List<Approval> receivedApprovals;
    private Status status;
    private final Instant createdAt;
    private Instant completedAt;

    private ApprovalWorkflow(String workflowId, String statementId, String profileId,
                            String requesterId, String action, String resource,
                            BigDecimal amount, int requiredApprovals,
                            List<String> eligibleApprovers) {
        if (workflowId == null || workflowId.isBlank()) {
            throw new IllegalArgumentException("workflowId cannot be null or blank");
        }
        if (statementId == null || statementId.isBlank()) {
            throw new IllegalArgumentException("statementId cannot be null or blank");
        }
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("profileId cannot be null or blank");
        }
        if (requesterId == null || requesterId.isBlank()) {
            throw new IllegalArgumentException("requesterId cannot be null or blank");
        }
        if (requiredApprovals < 1) {
            throw new IllegalArgumentException("requiredApprovals must be at least 1");
        }
        if (eligibleApprovers == null || eligibleApprovers.isEmpty()) {
            throw new IllegalArgumentException("eligibleApprovers cannot be null or empty");
        }
        // Critical invariant: requester cannot be in eligible approvers list
        if (eligibleApprovers.contains(requesterId)) {
            throw new IllegalArgumentException(
                "Requester " + requesterId + " cannot be in eligible approvers list"
            );
        }

        this.workflowId = workflowId;
        this.statementId = statementId;
        this.profileId = profileId;
        this.requesterId = requesterId;
        this.action = action;
        this.resource = resource;
        this.amount = amount;
        this.requiredApprovals = requiredApprovals;
        this.eligibleApprovers = new ArrayList<>(eligibleApprovers);
        this.receivedApprovals = new ArrayList<>();
        this.status = Status.PENDING;
        this.createdAt = Instant.now();
        this.completedAt = null;
    }

    /**
     * Factory method to create a new approval workflow.
     */
    public static ApprovalWorkflow start(String workflowId, String statementId, String profileId,
                                        String requesterId, String action, String resource,
                                        BigDecimal amount, int requiredApprovals,
                                        List<String> eligibleApprovers) {
        return new ApprovalWorkflow(workflowId, statementId, profileId, requesterId,
                                   action, resource, amount, requiredApprovals, eligibleApprovers);
    }

    /**
     * Approve the workflow by an eligible approver.
     * Enforces invariant: approver must be eligible and cannot be the requester.
     */
    public void approve(String approverId, String comment) {
        validateCanReceiveApproval(approverId);

        Approval approval = new Approval(approverId, "APPROVED", comment, Instant.now());
        receivedApprovals.add(approval);

        checkCompletion();
    }

    /**
     * Reject the workflow by an eligible approver.
     * Any rejection immediately completes the workflow with REJECTED status.
     */
    public void reject(String approverId, String reason) {
        validateCanReceiveApproval(approverId);

        Approval approval = new Approval(approverId, "REJECTED", reason, Instant.now());
        receivedApprovals.add(approval);

        // Any rejection completes the workflow immediately
        this.status = Status.REJECTED;
        this.completedAt = Instant.now();
    }

    /**
     * Cancel the workflow (by requester or admin).
     */
    public void cancel(String cancelledBy, String reason) {
        if (status != Status.PENDING) {
            throw new IllegalStateException(
                "Cannot cancel workflow. Workflow must be PENDING. Current status: " + status
            );
        }

        this.status = Status.CANCELLED;
        this.completedAt = Instant.now();
    }

    /**
     * Validate that an approver can approve this workflow.
     */
    private void validateCanReceiveApproval(String approverId) {
        if (status != Status.PENDING) {
            throw new IllegalStateException(
                "Cannot approve workflow. Workflow must be PENDING. Current status: " + status
            );
        }

        if (!eligibleApprovers.contains(approverId)) {
            throw new IllegalArgumentException(
                "Approver " + approverId + " is not eligible to approve this workflow"
            );
        }

        if (approverId.equals(requesterId)) {
            throw new IllegalArgumentException(
                "Requester " + requesterId + " cannot approve their own workflow"
            );
        }

        // Check if approver already approved (parallel approval - each approver votes once)
        boolean alreadyApproved = receivedApprovals.stream()
            .anyMatch(a -> a.approverId().equals(approverId));

        if (alreadyApproved) {
            throw new IllegalArgumentException(
                "Approver " + approverId + " has already provided a decision for this workflow"
            );
        }
    }

    /**
     * Check if workflow has received enough approvals to complete.
     * MVP: Parallel approval only - all approvers vote, workflow completes when threshold is reached.
     */
    private void checkCompletion() {
        long approvalCount = receivedApprovals.stream()
            .filter(a -> "APPROVED".equals(a.decision()))
            .count();

        if (approvalCount >= requiredApprovals) {
            this.status = Status.APPROVED;
            this.completedAt = Instant.now();
        }
    }

    // Getters
    public String workflowId() { return workflowId; }
    public String statementId() { return statementId; }
    public String profileId() { return profileId; }
    public String requesterId() { return requesterId; }
    public String action() { return action; }
    public String resource() { return resource; }
    public BigDecimal amount() { return amount; }
    public int requiredApprovals() { return requiredApprovals; }
    public List<String> eligibleApprovers() { return List.copyOf(eligibleApprovers); }
    public List<Approval> receivedApprovals() { return List.copyOf(receivedApprovals); }
    public Status status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant completedAt() { return completedAt; }

    /**
     * Approval entity within ApprovalWorkflow aggregate.
     * Represents a single approval decision.
     */
    public static class Approval {
        private final String approverId;
        private final String decision;      // APPROVED or REJECTED
        private final String comment;
        private final Instant approvedAt;

        public Approval(String approverId, String decision, String comment, Instant approvedAt) {
            this.approverId = approverId;
            this.decision = decision;
            this.comment = comment;
            this.approvedAt = approvedAt;
        }

        public String approverId() { return approverId; }
        public String decision() { return decision; }
        public String comment() { return comment; }
        public Instant approvedAt() { return approvedAt; }
    }
}
