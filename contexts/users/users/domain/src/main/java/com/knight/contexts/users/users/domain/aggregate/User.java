package com.knight.contexts.users.users.domain.aggregate;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.UserId;

import java.time.Instant;

/**
 * User Aggregate Root.
 * Manages user lifecycle for direct and indirect clients.
 */
public class User {

    public enum Status {
        PENDING, ACTIVE, LOCKED, DEACTIVATED
    }

    public enum UserType {
        DIRECT, INDIRECT
    }

    public enum IdentityProvider {
        OKTA, A_AND_P
    }

    private final UserId userId;
    private final String email;
    private final UserType userType;
    private final IdentityProvider identityProvider;
    private final ClientId clientId;
    private Status status;
    private final Instant createdAt;
    private Instant updatedAt;
    private String lockReason;
    private String deactivationReason;

    private User(UserId userId, String email, UserType userType,
                IdentityProvider identityProvider, ClientId clientId) {
        this.userId = userId;
        this.email = email;
        this.userType = userType;
        this.identityProvider = identityProvider;
        this.clientId = clientId;
        this.status = Status.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static User create(UserId userId, String email, UserType userType,
                             IdentityProvider identityProvider, ClientId clientId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (userType == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }
        if (identityProvider == null) {
            throw new IllegalArgumentException("Identity provider cannot be null");
        }
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }

        return new User(userId, email, userType, identityProvider, clientId);
    }

    public void activate() {
        if (this.status == Status.LOCKED) {
            throw new IllegalStateException("Cannot activate locked user. Unlock first.");
        }
        if (this.status == Status.ACTIVE) {
            return; // Already active
        }
        this.status = Status.ACTIVE;
        this.updatedAt = Instant.now();
        this.deactivationReason = null;
    }

    public void deactivate(String reason) {
        if (this.status == Status.DEACTIVATED) {
            return; // Already deactivated
        }
        this.status = Status.DEACTIVATED;
        this.deactivationReason = reason;
        this.updatedAt = Instant.now();
    }

    public void lock(String reason) {
        if (this.status == Status.LOCKED) {
            return; // Already locked
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Lock reason is required");
        }
        this.status = Status.LOCKED;
        this.lockReason = reason;
        this.updatedAt = Instant.now();
    }

    public void unlock() {
        if (this.status != Status.LOCKED) {
            throw new IllegalStateException("User is not locked");
        }
        this.status = Status.ACTIVE;
        this.lockReason = null;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UserId getUserId() { return userId; }
    public String getEmail() { return email; }
    public UserType getUserType() { return userType; }
    public IdentityProvider getIdentityProvider() { return identityProvider; }
    public ClientId getClientId() { return clientId; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getLockReason() { return lockReason; }
    public String getDeactivationReason() { return deactivationReason; }
}
