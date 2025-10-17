package com.knight.contexts.users.policy.domain.aggregate;

import java.time.Instant;
import java.util.UUID;

/**
 * Policy Aggregate Root.
 * Manages permission and approval policies.
 */
public class Policy {

    public enum PolicyType {
        PERMISSION, APPROVAL
    }

    private final String policyId;
    private final PolicyType policyType;
    private final String subject;
    private String action;
    private String resource;
    private Integer approverCount;
    private final Instant createdAt;
    private Instant updatedAt;

    private Policy(String policyId, PolicyType policyType, String subject,
                  String action, String resource, Integer approverCount) {
        this.policyId = policyId;
        this.policyType = policyType;
        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.approverCount = approverCount;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static Policy create(PolicyType policyType, String subject,
                               String action, String resource, Integer approverCount) {
        if (policyType == null) {
            throw new IllegalArgumentException("Policy type cannot be null");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be null or blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Action cannot be null or blank");
        }
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("Resource cannot be null or blank");
        }

        // Approval policies require approver count
        if (policyType == PolicyType.APPROVAL) {
            if (approverCount == null || approverCount <= 0) {
                throw new IllegalArgumentException("Approval policy requires approverCount > 0");
            }
        }

        String policyId = UUID.randomUUID().toString();
        return new Policy(policyId, policyType, subject, action, resource, approverCount);
    }

    public void update(String action, String resource, Integer approverCount) {
        if (action != null && !action.isBlank()) {
            this.action = action;
        }
        if (resource != null && !resource.isBlank()) {
            this.resource = resource;
        }

        // For approval policies, validate approverCount
        if (this.policyType == PolicyType.APPROVAL && approverCount != null) {
            if (approverCount <= 0) {
                throw new IllegalArgumentException("Approval policy requires approverCount > 0");
            }
            this.approverCount = approverCount;
        }

        this.updatedAt = Instant.now();
    }

    // Getters
    public String getPolicyId() { return policyId; }
    public PolicyType getPolicyType() { return policyType; }
    public String getSubject() { return subject; }
    public String getAction() { return action; }
    public String getResource() { return resource; }
    public Integer getApproverCount() { return approverCount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
