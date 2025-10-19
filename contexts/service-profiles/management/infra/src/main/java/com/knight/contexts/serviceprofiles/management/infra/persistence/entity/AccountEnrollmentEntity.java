package com.knight.contexts.serviceprofiles.management.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "account_enrollments", schema = "spm")
public class AccountEnrollmentEntity {

    @Id
    @Column(name = "enrollment_id", nullable = false)
    private String enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private ServicingProfileEntity servicingProfile;

    @Column(name = "service_enrollment_id", nullable = false)
    private String serviceEnrollmentId;

    @Column(name = "account_id", nullable = false, length = 100)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ServicingProfileEntity.Status status;

    @Column(name = "enrolled_at", nullable = false)
    private Instant enrolledAt;

    public AccountEnrollmentEntity() {
        // JPA requires no-arg constructor
    }

    public AccountEnrollmentEntity(String enrollmentId, ServicingProfileEntity servicingProfile,
                                   String serviceEnrollmentId, String accountId) {
        this.enrollmentId = enrollmentId;
        this.servicingProfile = servicingProfile;
        this.serviceEnrollmentId = serviceEnrollmentId;
        this.accountId = accountId;
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
        if (!(o instanceof AccountEnrollmentEntity)) return false;
        AccountEnrollmentEntity that = (AccountEnrollmentEntity) o;
        return Objects.equals(enrollmentId, that.enrollmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }
}
