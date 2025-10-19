package com.knight.contexts.approvalworkflows.engine.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for ApprovalWorkflow aggregate.
 * Maps ApprovalWorkflow domain model to database table.
 */
@Entity
@Table(name = "approval_workflows", schema = "approvals")
public class ApprovalWorkflowJpaEntity {

    @Id
    @Column(name = "workflow_id", nullable = false)
    private String workflowId;

    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "required_approvals", nullable = false)
    private int requiredApprovals;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "initiated_by", nullable = false)
    private String initiatedBy;

    @Column(name = "initiated_at", nullable = false)
    private Instant initiatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ApprovalJpaEntity> receivedApprovals = new ArrayList<>();

    public enum Status {
        PENDING, APPROVED, REJECTED, EXPIRED
    }

    // Default constructor for JPA
    public ApprovalWorkflowJpaEntity() {}

    public ApprovalWorkflowJpaEntity(
        String workflowId,
        String resourceType,
        String resourceId,
        int requiredApprovals,
        Status status,
        String initiatedBy,
        Instant initiatedAt,
        Instant completedAt
    ) {
        this.workflowId = workflowId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.requiredApprovals = requiredApprovals;
        this.status = status;
        this.initiatedBy = initiatedBy;
        this.initiatedAt = initiatedAt;
        this.completedAt = completedAt;
        this.version = 0L;
    }

    // Getters and Setters
    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getRequiredApprovals() {
        return requiredApprovals;
    }

    public void setRequiredApprovals(int requiredApprovals) {
        this.requiredApprovals = requiredApprovals;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public Instant getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(Instant initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<ApprovalJpaEntity> getReceivedApprovals() {
        return receivedApprovals;
    }

    public void setReceivedApprovals(List<ApprovalJpaEntity> receivedApprovals) {
        this.receivedApprovals = receivedApprovals;
    }

    public void addApproval(ApprovalJpaEntity approval) {
        receivedApprovals.add(approval);
        approval.setWorkflow(this);
    }

    public void removeApproval(ApprovalJpaEntity approval) {
        receivedApprovals.remove(approval);
        approval.setWorkflow(null);
    }
}
