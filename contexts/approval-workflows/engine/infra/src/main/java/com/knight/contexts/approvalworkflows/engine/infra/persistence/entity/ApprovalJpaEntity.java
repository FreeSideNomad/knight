package com.knight.contexts.approvalworkflows.engine.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity for Approval (child entity of ApprovalWorkflow).
 * Maps Approval value object from domain model.
 */
@Entity
@Table(name = "approvals", schema = "approvals")
public class ApprovalJpaEntity {

    @Id
    @Column(name = "approval_id", nullable = false)
    private String approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private ApprovalWorkflowJpaEntity workflow;

    @Column(name = "approver_user_id", nullable = false)
    private String approverUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    private Decision decision;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "approved_at", nullable = false)
    private Instant approvedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public enum Decision {
        APPROVE, REJECT
    }

    // Default constructor for JPA
    public ApprovalJpaEntity() {}

    public ApprovalJpaEntity(
        String approvalId,
        String approverUserId,
        Decision decision,
        String comments,
        Instant approvedAt
    ) {
        this.approvalId = approvalId;
        this.approverUserId = approverUserId;
        this.decision = decision;
        this.comments = comments;
        this.approvedAt = approvedAt;
        this.version = 0L;
    }

    // Getters and Setters
    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }

    public ApprovalWorkflowJpaEntity getWorkflow() {
        return workflow;
    }

    public void setWorkflow(ApprovalWorkflowJpaEntity workflow) {
        this.workflow = workflow;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
