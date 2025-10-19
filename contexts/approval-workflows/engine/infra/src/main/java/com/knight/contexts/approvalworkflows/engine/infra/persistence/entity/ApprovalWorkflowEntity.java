package com.knight.contexts.approvalworkflows.engine.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_workflows", schema = "approvals")
public class ApprovalWorkflowEntity {

    @Id
    @Column(name = "workflow_id", nullable = false)
    private String workflowId;

    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;

    @Column(name = "resource_id", nullable = false, length = 255)
    private String resourceId;

    @Column(name = "required_approvals", nullable = false)
    private int requiredApprovals;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "initiated_at", nullable = false)
    private Instant initiatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "initiated_by", nullable = false, length = 255)
    private String initiatedBy;

    @OneToMany(mappedBy = "approvalWorkflow", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApprovalEntity> receivedApprovals = new ArrayList<>();

    @Version
    @Column(name = "version")
    private Long version;

    public enum Status {
        PENDING, APPROVED, REJECTED, EXPIRED
    }

    public ApprovalWorkflowEntity() {
        // JPA requires no-arg constructor
    }

    public ApprovalWorkflowEntity(String workflowId, String resourceType, String resourceId, int requiredApprovals, String initiatedBy) {
        this.workflowId = workflowId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.requiredApprovals = requiredApprovals;
        this.status = Status.PENDING;
        this.initiatedAt = Instant.now();
        this.initiatedBy = initiatedBy;
    }

    // Getters and setters
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

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public List<ApprovalEntity> getReceivedApprovals() {
        return receivedApprovals;
    }

    public void setReceivedApprovals(List<ApprovalEntity> receivedApprovals) {
        this.receivedApprovals = receivedApprovals;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApprovalWorkflowEntity)) return false;
        ApprovalWorkflowEntity that = (ApprovalWorkflowEntity) o;
        return workflowId != null && workflowId.equals(that.workflowId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
