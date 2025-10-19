package com.knight.contexts.serviceprofiles.management.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "service_enrollments", schema = "spm")
public class ServiceEnrollmentEntity {

    @Id
    @Column(name = "enrollment_id", nullable = false)
    private String enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private ServicingProfileEntity servicingProfile;

    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;

    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ServicingProfileEntity.Status status;

    @Column(name = "enrolled_at", nullable = false)
    private Instant enrolledAt;

    public ServiceEnrollmentEntity() {
        // JPA requires no-arg constructor
    }

    public ServiceEnrollmentEntity(String enrollmentId, ServicingProfileEntity servicingProfile,
                                   String serviceType, String configuration) {
        this.enrollmentId = enrollmentId;
        this.servicingProfile = servicingProfile;
        this.serviceType = serviceType;
        this.configuration = configuration;
        this.status = ServicingProfileEntity.Status.ACTIVE;
        this.enrolledAt = Instant.now();
    }

    // Getters and Setters
    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public ServicingProfileEntity getServicingProfile() {
        return servicingProfile;
    }

    public void setServicingProfile(ServicingProfileEntity servicingProfile) {
        this.servicingProfile = servicingProfile;
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

    public ServicingProfileEntity.Status getStatus() {
        return status;
    }

    public void setStatus(ServicingProfileEntity.Status status) {
        this.status = status;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceEnrollmentEntity)) return false;
        ServiceEnrollmentEntity that = (ServiceEnrollmentEntity) o;
        return Objects.equals(enrollmentId, that.enrollmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }
}
