package com.knight.contexts.serviceprofiles.management.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for ServicingProfile aggregate.
 * Maps domain aggregate to relational database.
 */
@Entity
@Table(name = "servicing_profiles", schema = "spm")
public class ServicingProfileJpaEntity {

    @Id
    @Column(name = "profile_urn")
    private String profileUrn;

    @Column(name = "client_urn", nullable = false)
    private String clientUrn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status;

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<EnrolledServiceJpaEntity> enrolledServices = new ArrayList<>();

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<EnrolledAccountJpaEntity> enrolledAccounts = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Version
    @Column(nullable = false)
    private Long version;

    public enum Status {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }

    // Default constructor for JPA
    public ServicingProfileJpaEntity() {}

    public ServicingProfileJpaEntity(
        String profileUrn,
        String clientUrn,
        Status status,
        Instant createdAt,
        String createdBy
    ) {
        this.profileUrn = profileUrn;
        this.clientUrn = clientUrn;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.createdBy = createdBy;
        this.version = 0L;
    }

    // Getters and Setters
    public String getProfileUrn() {
        return profileUrn;
    }

    public void setProfileUrn(String profileUrn) {
        this.profileUrn = profileUrn;
    }

    public String getClientUrn() {
        return clientUrn;
    }

    public void setClientUrn(String clientUrn) {
        this.clientUrn = clientUrn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<EnrolledServiceJpaEntity> getEnrolledServices() {
        return enrolledServices;
    }

    public void setEnrolledServices(List<EnrolledServiceJpaEntity> enrolledServices) {
        this.enrolledServices = enrolledServices;
    }

    public List<EnrolledAccountJpaEntity> getEnrolledAccounts() {
        return enrolledAccounts;
    }

    public void setEnrolledAccounts(List<EnrolledAccountJpaEntity> enrolledAccounts) {
        this.enrolledAccounts = enrolledAccounts;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Helper methods for bidirectional relationships
    public void addEnrolledService(EnrolledServiceJpaEntity service) {
        enrolledServices.add(service);
        service.setProfile(this);
    }

    public void removeEnrolledService(EnrolledServiceJpaEntity service) {
        enrolledServices.remove(service);
        service.setProfile(null);
    }

    public void addEnrolledAccount(EnrolledAccountJpaEntity account) {
        enrolledAccounts.add(account);
        account.setProfile(this);
    }

    public void removeEnrolledAccount(EnrolledAccountJpaEntity account) {
        enrolledAccounts.remove(account);
        account.setProfile(null);
    }
}
