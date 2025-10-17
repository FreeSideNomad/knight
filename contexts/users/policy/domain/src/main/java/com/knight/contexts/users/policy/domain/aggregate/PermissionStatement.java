package com.knight.contexts.users.policy.domain.aggregate;

import com.knight.contexts.users.policy.api.commands.PolicyCommands.Effect;
import com.knight.platform.sharedkernel.ProfileId;

import java.time.Instant;
import java.util.UUID;

/**
 * PermissionStatement aggregate root.
 * Defines permission policy for a subject on action + resource.
 *
 * Invariants:
 * - statementId must be unique
 * - profileId cannot change after creation
 * - subject, action, resource cannot be null or blank
 * - DENY takes precedence over ALLOW in evaluation
 */
public class PermissionStatement {

    private final String statementId;
    private final ProfileId profileId;
    private final String subject;
    private String action;
    private String resource;
    private Effect effect;
    private final Instant createdAt;
    private Instant updatedAt;

    private PermissionStatement(String statementId, ProfileId profileId, String subject,
                               String action, String resource, Effect effect) {
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
        if (effect == null) {
            throw new IllegalArgumentException("effect cannot be null");
        }

        this.statementId = statementId;
        this.profileId = profileId;
        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.effect = effect;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Factory method to create a new permission statement.
     */
    public static PermissionStatement create(ProfileId profileId, String subject,
                                            String action, String resource, Effect effect) {
        String statementId = UUID.randomUUID().toString();
        return new PermissionStatement(statementId, profileId, subject, action, resource, effect);
    }

    /**
     * Reconstruct from persistence (with existing ID).
     */
    public static PermissionStatement reconstruct(String statementId, ProfileId profileId, String subject,
                                                 String action, String resource, Effect effect,
                                                 Instant createdAt, Instant updatedAt) {
        PermissionStatement statement = new PermissionStatement(statementId, profileId, subject, action, resource, effect);
        // Would need to use reflection or builder to set createdAt/updatedAt in real implementation
        return statement;
    }

    /**
     * Update the action pattern.
     */
    public void updateAction(String newAction) {
        if (newAction == null || newAction.isBlank()) {
            throw new IllegalArgumentException("action cannot be null or blank");
        }
        this.action = newAction;
        this.updatedAt = Instant.now();
    }

    /**
     * Update the resource pattern.
     */
    public void updateResource(String newResource) {
        if (newResource == null || newResource.isBlank()) {
            throw new IllegalArgumentException("resource cannot be null or blank");
        }
        this.resource = newResource;
        this.updatedAt = Instant.now();
    }

    /**
     * Update the effect (ALLOW/DENY).
     */
    public void updateEffect(Effect newEffect) {
        if (newEffect == null) {
            throw new IllegalArgumentException("effect cannot be null");
        }
        this.effect = newEffect;
        this.updatedAt = Instant.now();
    }

    /**
     * Check if this statement matches the given subject, action, resource.
     * Uses simple string matching (could be enhanced with wildcards/patterns).
     */
    public boolean matches(String checkSubject, String checkAction, String checkResource) {
        return matchesPattern(this.subject, checkSubject)
            && matchesPattern(this.action, checkAction)
            && matchesPattern(this.resource, checkResource);
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
    public Effect effect() { return effect; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
