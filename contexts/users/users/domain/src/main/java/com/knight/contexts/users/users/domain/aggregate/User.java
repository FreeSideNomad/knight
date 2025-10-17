package com.knight.contexts.users.users.domain.aggregate;

import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.time.Instant;

/**
 * User aggregate root.
 * Manages user lifecycle, roles, and status.
 *
 * Invariants:
 * - user_id must be unique
 * - profile must have at least 2 ADMINISTRATOR users (dual admin rule)
 * - email must be unique within profile
 */
public class User {

    public enum Role { ADMINISTRATOR, REGULAR_USER }
    public enum Source { EXPRESS, OKTA }
    public enum Status { PENDING, ACTIVE, LOCKED, DEACTIVATED }

    private final UserId userId;
    private final ServicingProfileId profileId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private Role role;
    private final Source source;
    private Status status;
    private String lockReason;
    private String lockedBy;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(UserId userId, ServicingProfileId profileId, String email,
                String firstName, String lastName, Role role, Source source) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or blank");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName cannot be null or blank");
        }

        this.userId = userId;
        this.profileId = profileId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.source = source;
        this.status = Status.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Factory method to create a new user.
     * Initial status is PENDING until activated.
     */
    public static User create(UserId userId, ServicingProfileId profileId, String email,
                             String firstName, String lastName, Role role, Source source) {
        return new User(userId, profileId, email, firstName, lastName, role, source);
    }

    /**
     * Lock user (by admin or bank).
     */
    public void lock(String reason, String lockedBy) {
        if (status == Status.DEACTIVATED) {
            throw new IllegalStateException("Cannot lock DEACTIVATED user");
        }
        if (status == Status.LOCKED) {
            throw new IllegalStateException("User is already locked");
        }

        this.status = Status.LOCKED;
        this.lockReason = reason;
        this.lockedBy = lockedBy;
        this.updatedAt = Instant.now();
    }

    /**
     * Unlock user.
     */
    public void unlock() {
        if (status != Status.LOCKED) {
            throw new IllegalStateException("Can only unlock LOCKED users. Current status: " + status);
        }

        this.status = Status.ACTIVE;
        this.lockReason = null;
        this.lockedBy = null;
        this.updatedAt = Instant.now();
    }

    /**
     * Update user role.
     * Note: Dual admin validation must be enforced at application service level.
     */
    public void updateRole(Role newRole) {
        if (status == Status.DEACTIVATED) {
            throw new IllegalStateException("Cannot update role for DEACTIVATED user");
        }
        if (this.role == newRole) {
            throw new IllegalArgumentException("Role is already " + newRole);
        }

        this.role = newRole;
        this.updatedAt = Instant.now();
    }

    /**
     * Deactivate user.
     * Note: Dual admin validation must be enforced at application service level.
     */
    public void deactivate() {
        if (status == Status.DEACTIVATED) {
            throw new IllegalStateException("User is already deactivated");
        }

        this.status = Status.DEACTIVATED;
        this.updatedAt = Instant.now();
    }

    /**
     * Activate user (transition from PENDING to ACTIVE).
     */
    public void activate() {
        if (status != Status.PENDING) {
            throw new IllegalStateException("Can only activate PENDING users. Current status: " + status);
        }

        this.status = Status.ACTIVE;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UserId userId() { return userId; }
    public ServicingProfileId profileId() { return profileId; }
    public String email() { return email; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public Role role() { return role; }
    public Source source() { return source; }
    public Status status() { return status; }
    public String lockReason() { return lockReason; }
    public String lockedBy() { return lockedBy; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
