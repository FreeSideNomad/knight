package com.knight.contexts.approvalworkflows.engine.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "approvals", schema = "approvals")
public class ApprovalEntity {

    @Id
    @Column(name = "approval_id", nullable = false)
    private String approvalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private ApprovalWorkflowEntity approvalWorkflow;

    @Column(name = "approver_user_id", nullable = false, length = 255)
    private String approverUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    private Decision decision;

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "approved_at", nullable = false)
    private Instant approvedAt;

    public enum Decision {
        APPROVE, REJECT
    }

    public ApprovalEntity() {
        // JPA requires no-arg constructor
    }

    public ApprovalEntity(String approvalId, ApprovalWorkflowEntity approvalWorkflow, String approverUserId, Decision decision, String comments) {
        this.approvalId = approvalId;
        this.approvalWorkflow = approvalWorkflow;
        this.approverUserId = approverUserId;
        this.decision = decision;
        this.comments = comments;
        this.approvedAt = Instant.now();
    }

    // Getters and setters
    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }

    public ApprovalWorkflowEntity getApprovalWorkflow() {
        return approvalWorkflow;
    }

    public void setApprovalWorkflow(ApprovalWorkflowEntity approvalWorkflow) {
        this.approvalWorkflow = approvalWorkflow;
    }

    public String getApproverUserId() {
        return approverUserId;
    }

    public void setApproverUserId(String approverUserId) {
        this.approverUserId = approverUserId;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApprovalEntity)) return false;
        ApprovalEntity that = (ApprovalEntity) o;
        return approvalId != null && approvalId.equals(that.approvalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
