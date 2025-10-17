package com.knight.contexts.approvalworkflows.engine.domain.aggregate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Approval Workflow Aggregate Root.
 * Generic approval workflow execution engine.
 */
public class ApprovalWorkflow {

    public enum Status {
        PENDING, APPROVED, REJECTED, EXPIRED
    }

    public enum Decision {
        APPROVE, REJECT
    }

    public static class Approval {
        private final String approvalId;
        private final String approverUserId;
        private final Decision decision;
        private final String comments;
        private final Instant approvedAt;

        public Approval(String approverUserId, Decision decision, String comments) {
            this.approvalId = UUID.randomUUID().toString();
            this.approverUserId = approverUserId;
            this.decision = decision;
            this.comments = comments;
            this.approvedAt = Instant.now();
        }

        public String getApprovalId() { return approvalId; }
        public String getApproverUserId() { return approverUserId; }
        public Decision getDecision() { return decision; }
        public String getComments() { return comments; }
        public Instant getApprovedAt() { return approvedAt; }
    }

    private final String workflowId;
    private final String resourceType;
    private final String resourceId;
    private final int requiredApprovals;
    private Status status;
    private final List<Approval> receivedApprovals;
    private final Instant initiatedAt;
    private Instant completedAt;
    private final String initiatedBy;

    private ApprovalWorkflow(String workflowId, String resourceType, String resourceId,
                            int requiredApprovals, String initiatedBy) {
        this.workflowId = workflowId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.requiredApprovals = requiredApprovals;
        this.status = Status.PENDING;
        this.receivedApprovals = new ArrayList<>();
        this.initiatedAt = Instant.now();
        this.initiatedBy = initiatedBy;
    }

    public static ApprovalWorkflow initiate(String resourceType, String resourceId,
                                           int requiredApprovals, String initiatedBy) {
        if (resourceType == null || resourceType.isBlank()) {
            throw new IllegalArgumentException("Resource type cannot be null or blank");
        }
        if (resourceId == null || resourceId.isBlank()) {
            throw new IllegalArgumentException("Resource ID cannot be null or blank");
        }
        if (requiredApprovals <= 0) {
            throw new IllegalArgumentException("Required approvals must be greater than 0");
        }
        if (initiatedBy == null || initiatedBy.isBlank()) {
            throw new IllegalArgumentException("Initiated by cannot be null or blank");
        }

        String workflowId = UUID.randomUUID().toString();
        return new ApprovalWorkflow(workflowId, resourceType, resourceId, requiredApprovals, initiatedBy);
    }

    public void recordApproval(String approverUserId, Decision decision, String comments) {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("Cannot record approval for workflow in status: " + this.status);
        }
        if (approverUserId == null || approverUserId.isBlank()) {
            throw new IllegalArgumentException("Approver user ID cannot be null or blank");
        }

        // Check if approver already approved
        boolean alreadyApproved = receivedApprovals.stream()
            .anyMatch(a -> a.getApproverUserId().equals(approverUserId));
        if (alreadyApproved) {
            throw new IllegalStateException("User has already provided approval: " + approverUserId);
        }

        Approval approval = new Approval(approverUserId, decision, comments);
        this.receivedApprovals.add(approval);

        // If rejected, immediately reject the workflow
        if (decision == Decision.REJECT) {
            this.status = Status.REJECTED;
            this.completedAt = Instant.now();
            return;
        }

        // Check if we have enough approvals
        long approveCount = receivedApprovals.stream()
            .filter(a -> a.getDecision() == Decision.APPROVE)
            .count();

        if (approveCount >= requiredApprovals) {
            this.status = Status.APPROVED;
            this.completedAt = Instant.now();
        }
    }

    public void expire() {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("Cannot expire workflow in status: " + this.status);
        }
        this.status = Status.EXPIRED;
        this.completedAt = Instant.now();
    }

    // Getters
    public String getWorkflowId() { return workflowId; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public int getRequiredApprovals() { return requiredApprovals; }
    public Status getStatus() { return status; }
    public List<Approval> getReceivedApprovals() { return List.copyOf(receivedApprovals); }
    public Instant getInitiatedAt() { return initiatedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getInitiatedBy() { return initiatedBy; }
}
