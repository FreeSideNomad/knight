package com.knight.contexts.users.policy.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "policies", schema = "policy")
public class PolicyEntity {

    @Id
    @Column(name = "policy_id", nullable = false)
    private String policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 20)
    private PolicyType policyType;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "action", nullable = false, length = 255)
    private String action;

    @Column(name = "resource", nullable = false, length = 500)
    private String resource;

    @Column(name = "approver_count")
    private Integer approverCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public enum PolicyType {
        PERMISSION, APPROVAL
    }

    public PolicyEntity() {
        // JPA requires no-arg constructor
    }

    public PolicyEntity(String policyId, PolicyType policyType, String subject, String action, String resource, Integer approverCount) {
        this.policyId = policyId;
        this.policyType = policyType;
        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.approverCount = approverCount;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Getters and setters
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyEntity)) return false;
        PolicyEntity that = (PolicyEntity) o;
        return policyId != null && policyId.equals(that.policyId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
