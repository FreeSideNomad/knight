package com.knight.contexts.serviceprofiles.management.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA Entity for Service Enrollment.
 */
@Entity
@Table(name = "service_enrollments", schema = "spm")
public class EnrolledServiceJpaEntity {

    @Id
    @Column(name = "enrollment_id")
    private String enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_urn", nullable = false)
    private ServicingProfileJpaEntity profile;

    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;

    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration;

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
    public EnrolledServiceJpaEntity() {}

    public EnrolledServiceJpaEntity(
        String enrollmentId,
        String serviceType,
        String configuration,
        Status status,
        Instant enrolledAt
    ) {
        this.enrollmentId = enrollmentId;
        this.serviceType = serviceType;
        this.configuration = configuration;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
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
