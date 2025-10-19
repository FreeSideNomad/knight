package com.knight.contexts.users.users.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity for User aggregate.
 * Maps User domain model to database table.
 */
@Entity
@Table(name = "users", schema = "users")
public class UserJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_provider", nullable = false, length = 20)
    private IdentityProvider identityProvider;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "lock_reason", columnDefinition = "TEXT")
    private String lockReason;

    @Column(name = "deactivation_reason", columnDefinition = "TEXT")
    private String deactivationReason;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public enum Status {
        PENDING, ACTIVE, LOCKED, DEACTIVATED
    }

    public enum UserType {
        DIRECT, INDIRECT
    }

    public enum IdentityProvider {
        OKTA, A_AND_P
    }

    // Default constructor for JPA
    public UserJpaEntity() {}

    public UserJpaEntity(
        String id,
        String email,
        UserType userType,
        IdentityProvider identityProvider,
        String clientId,
        Status status,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.email = email;
        this.userType = userType;
        this.identityProvider = identityProvider;
        this.clientId = clientId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = 0L;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
