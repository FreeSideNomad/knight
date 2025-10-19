package com.knight.contexts.serviceprofiles.management.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "servicing_profiles", schema = "spm")
public class ServicingProfileEntity {

    @Id
    @Column(name = "profile_id", nullable = false)
    private String profileId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @OneToMany(mappedBy = "servicingProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ServiceEnrollmentEntity> serviceEnrollments = new HashSet<>();

    @OneToMany(mappedBy = "servicingProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<AccountEnrollmentEntity> accountEnrollments = new HashSet<>();

    @Version
    @Column(name = "version")
    private Long version;

    public enum Status {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }

    public ServicingProfileEntity() {
        // JPA requires no-arg constructor
    }

    public ServicingProfileEntity(String profileId, String clientId, String createdBy) {
        this.profileId = profileId;
        this.clientId = clientId;
        this.status = Status.PENDING;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Set<ServiceEnrollmentEntity> getServiceEnrollments() {
        return serviceEnrollments;
    }

    public void setServiceEnrollments(Set<ServiceEnrollmentEntity> serviceEnrollments) {
        this.serviceEnrollments = serviceEnrollments;
    }

    public Set<AccountEnrollmentEntity> getAccountEnrollments() {
        return accountEnrollments;
    }

    public void setAccountEnrollments(Set<AccountEnrollmentEntity> accountEnrollments) {
        this.accountEnrollments = accountEnrollments;
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
        if (!(o instanceof ServicingProfileEntity)) return false;
        ServicingProfileEntity that = (ServicingProfileEntity) o;
        return Objects.equals(profileId, that.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId);
    }
}
