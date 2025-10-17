package com.knight.contexts.users.policy.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity for Policy aggregate.
 * Maps Policy domain model to database table.
 */
@Entity
@Table(name = "policies", schema = "policy")
public class PolicyJpaEntity {

    @Id
    @Column(name = "policy_id", nullable = false)
    private String policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 20)
    private PolicyType policyType;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "resource", nullable = false)
    private String resource;

    @Column(name = "approver_count")
    private Integer approverCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public enum PolicyType {
        PERMISSION, APPROVAL
    }

    // Default constructor for JPA
    protected PolicyJpaEntity() {}

    public PolicyJpaEntity(
        String policyId,
        PolicyType policyType,
        String subject,
        String action,
        String resource,
        Integer approverCount,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.policyId = policyId;
        this.policyType = policyType;
        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.approverCount = approverCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = 0L;
    }

    // Getters and Setters
    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Integer getApproverCount() {
        return approverCount;
    }

    public void setApproverCount(Integer approverCount) {
        this.approverCount = approverCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
