package com.knight.contexts.serviceprofiles.management.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA Entity for Account Enrollment.
 */
@Entity
@Table(name = "account_enrollments", schema = "spm")
public class EnrolledAccountJpaEntity {

    @Id
    @Column(name = "enrollment_id")
    private String enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_urn", nullable = false)
    private ServicingProfileJpaEntity profile;

    @Column(name = "service_enrollment_id", nullable = false)
    private String serviceEnrollmentId;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status;

    @Column(name = "enrolled_at", nullable = false)
    private Instant enrolledAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public enum Status {
        ACTIVE, SUSPENDED
    }

    // Default constructor for JPA
    public EnrolledAccountJpaEntity() {}

    public EnrolledAccountJpaEntity(
        String enrollmentId,
        String serviceEnrollmentId,
        String accountId,
        Status status,
        Instant enrolledAt
    ) {
        this.enrollmentId = enrollmentId;
        this.serviceEnrollmentId = serviceEnrollmentId;
        this.accountId = accountId;
        this.status = status;
        this.enrolledAt = enrolledAt;
        this.version = 0L;
    }

    // Getters and Setters
    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public ServicingProfileJpaEntity getProfile() {
        return profile;
    }

    public void setProfile(ServicingProfileJpaEntity profile) {
        this.profile = profile;
    }

    public String getServiceEnrollmentId() {
        return serviceEnrollmentId;
    }

    public void setServiceEnrollmentId(String serviceEnrollmentId) {
        this.serviceEnrollmentId = serviceEnrollmentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
