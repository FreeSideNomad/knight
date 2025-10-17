package com.knight.contexts.users.policy.domain.aggregate;

import com.knight.platform.sharedkernel.ProfileId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ApprovalStatement aggregate root.
 * Extends permission concept with approval workflow requirements.
 *
 * Invariants:
 * - statementId must be unique
 * - profileId cannot change after creation
 * - approverCount must be > 0
 * - approvers list size must be >= approverCount
 * - amountThreshold can be null (means applies to any amount)
 */
public class ApprovalStatement {

    private final String statementId;
    private final ProfileId profileId;
    private final String subject;
    private final String action;
    private final String resource;
    private int approverCount;
    private List<String> approvers;
    private BigDecimal amountThreshold;
    private final Instant createdAt;
    private Instant updatedAt;

    private ApprovalStatement(String statementId, ProfileId profileId, String subject,
                             String action, String resource, int approverCount,
                             List<String> approvers, BigDecimal amountThreshold) {
        if (statementId == null || statementId.isBlank()) {
            throw new IllegalArgumentException("statementId cannot be null or blank");
        }
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("subject cannot be null or blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action cannot be null or blank");
        }
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("resource cannot be null or blank");
        }
        if (approverCount <= 0) {
            throw new IllegalArgumentException("approverCount must be > 0");
        }
        if (approvers == null || approvers.size() < approverCount) {
            throw new IllegalArgumentException(
                "approvers list size must be >= approverCount. Required: " + approverCount + ", Got: " +
                (approvers == null ? 0 : approvers.size())
            );
        }

        this.statementId = statementId;
        this.profileId = profileId;
        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.approverCount = approverCount;
        this.approvers = new ArrayList<>(approvers);
        this.amountThreshold = amountThreshold;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Factory method to create a new approval statement.
     */
    public static ApprovalStatement create(ProfileId profileId, String subject,
                                          String action, String resource,
                                          int approverCount, List<String> approvers,
                                          BigDecimal amountThreshold) {
        String statementId = UUID.randomUUID().toString();
        return new ApprovalStatement(statementId, profileId, subject, action, resource,
                                    approverCount, approvers, amountThreshold);
    }

    /**
     * Update approver count and list.
     */
    public void updateApprovers(int newApproverCount, List<String> newApprovers) {
        if (newApproverCount <= 0) {
            throw new IllegalArgumentException("approverCount must be > 0");
        }
        if (newApprovers == null || newApprovers.size() < newApproverCount) {
            throw new IllegalArgumentException(
                "approvers list size must be >= approverCount. Required: " + newApproverCount + ", Got: " +
                (newApprovers == null ? 0 : newApprovers.size())
            );
        }

        this.approverCount = newApproverCount;
        this.approvers = new ArrayList<>(newApprovers);
        this.updatedAt = Instant.now();
    }

    /**
     * Update amount threshold.
     */
    public void updateThreshold(BigDecimal newThreshold) {
        this.amountThreshold = newThreshold;
        this.updatedAt = Instant.now();
    }

    /**
     * Check if this statement matches the given subject, action, resource.
     */
    public boolean matches(String checkSubject, String checkAction, String checkResource) {
        return matchesPattern(this.subject, checkSubject)
            && matchesPattern(this.action, checkAction)
            && matchesPattern(this.resource, checkResource);
    }

    /**
     * Check if amount is within threshold (null threshold means any amount).
     */
    public boolean meetsThreshold(BigDecimal amount) {
        if (amountThreshold == null) {
            return true; // No threshold, applies to any amount
        }
        if (amount == null) {
            return false;
        }
        return amount.compareTo(amountThreshold) <= 0;
    }

    private boolean matchesPattern(String pattern, String value) {
        if (pattern.equals("*")) {
            return true;
        }
        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return value.startsWith(prefix);
        }
        return pattern.equals(value);
    }

    // Getters
    public String statementId() { return statementId; }
    public ProfileId profileId() { return profileId; }
    public String subject() { return subject; }
    public String action() { return action; }
    public String resource() { return resource; }
    public int approverCount() { return approverCount; }
    public List<String> approvers() { return List.copyOf(approvers); }
    public BigDecimal amountThreshold() { return amountThreshold; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
