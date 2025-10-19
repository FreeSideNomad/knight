package com.knight.contexts.serviceprofiles.management.domain.aggregate;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Servicing Profile Aggregate Root.
 * Manages service and account enrollments for a client.
 */
public class ServicingProfile {

    public enum Status {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }

    public static class ServiceEnrollment {
        private final String enrollmentId;
        private final String serviceType;
        private final String configuration;
        private Status status;
        private final Instant enrolledAt;

        public ServiceEnrollment(String serviceType, String configuration) {
            this.enrollmentId = UUID.randomUUID().toString();
            this.serviceType = serviceType;
            this.configuration = configuration;
            this.status = Status.ACTIVE;
            this.enrolledAt = Instant.now();
        }

        // Factory method for reconstruction from persistence
        private ServiceEnrollment(String enrollmentId, String serviceType, String configuration, Status status, Instant enrolledAt) {
            this.enrollmentId = enrollmentId;
            this.serviceType = serviceType;
            this.configuration = configuration;
            this.status = status;
            this.enrolledAt = enrolledAt;
        }

        public static ServiceEnrollment reconstitute(String enrollmentId, String serviceType, String configuration, Status status, Instant enrolledAt) {
            return new ServiceEnrollment(enrollmentId, serviceType, configuration, status, enrolledAt);
        }

        public String getEnrollmentId() { return enrollmentId; }
        public String getServiceType() { return serviceType; }
        public String getConfiguration() { return configuration; }
        public Status getStatus() { return status; }
        public Instant getEnrolledAt() { return enrolledAt; }

        public void suspend() {
            this.status = Status.SUSPENDED;
        }
    }

    public static class AccountEnrollment {
        private final String enrollmentId;
        private final String serviceEnrollmentId;
        private final String accountId;
        private Status status;
        private final Instant enrolledAt;

        public AccountEnrollment(String serviceEnrollmentId, String accountId) {
            this.enrollmentId = UUID.randomUUID().toString();
            this.serviceEnrollmentId = serviceEnrollmentId;
            this.accountId = accountId;
            this.status = Status.ACTIVE;
            this.enrolledAt = Instant.now();
        }

        // Factory method for reconstruction from persistence
        private AccountEnrollment(String enrollmentId, String serviceEnrollmentId, String accountId, Status status, Instant enrolledAt) {
            this.enrollmentId = enrollmentId;
            this.serviceEnrollmentId = serviceEnrollmentId;
            this.accountId = accountId;
            this.status = status;
            this.enrolledAt = enrolledAt;
        }

        public static AccountEnrollment reconstitute(String enrollmentId, String serviceEnrollmentId, String accountId, Status status, Instant enrolledAt) {
            return new AccountEnrollment(enrollmentId, serviceEnrollmentId, accountId, status, enrolledAt);
        }

        public String getEnrollmentId() { return enrollmentId; }
        public String getServiceEnrollmentId() { return serviceEnrollmentId; }
        public String getAccountId() { return accountId; }
        public Status getStatus() { return status; }
        public Instant getEnrolledAt() { return enrolledAt; }

        public void suspend() {
            this.status = Status.SUSPENDED;
        }
    }

    private final ServicingProfileId profileId;
    private final ClientId clientId;
    private Status status;
    private final List<ServiceEnrollment> serviceEnrollments;
    private final List<AccountEnrollment> accountEnrollments;
    private final Instant createdAt;
    private Instant updatedAt;
    private final String createdBy;

    private ServicingProfile(ServicingProfileId profileId, ClientId clientId, String createdBy) {
        this.profileId = profileId;
        this.clientId = clientId;
        this.status = Status.PENDING;
        this.serviceEnrollments = new ArrayList<>();
        this.accountEnrollments = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.createdBy = createdBy;
    }

    public static ServicingProfile create(ServicingProfileId profileId, ClientId clientId, String createdBy) {
        if (profileId == null) throw new IllegalArgumentException("Profile ID cannot be null");
        if (clientId == null) throw new IllegalArgumentException("Client ID cannot be null");
        if (createdBy == null || createdBy.isBlank()) throw new IllegalArgumentException("Created by cannot be null");

        return new ServicingProfile(profileId, clientId, createdBy);
    }

    public ServiceEnrollment enrollService(String serviceType, String configuration) {
        if (this.status != Status.ACTIVE && this.status != Status.PENDING) {
            throw new IllegalStateException("Cannot enroll service to profile in status: " + this.status);
        }

        ServiceEnrollment enrollment = new ServiceEnrollment(serviceType, configuration);
        this.serviceEnrollments.add(enrollment);
        this.updatedAt = Instant.now();

        // Activate profile if it was pending and now has services
        if (this.status == Status.PENDING && !this.serviceEnrollments.isEmpty()) {
            this.status = Status.ACTIVE;
        }

        return enrollment;
    }

    public AccountEnrollment enrollAccount(String serviceEnrollmentId, String accountId) {
        if (this.status != Status.ACTIVE) {
            throw new IllegalStateException("Cannot enroll account to profile in status: " + this.status);
        }

        // Verify service enrollment exists
        boolean serviceExists = serviceEnrollments.stream()
            .anyMatch(se -> se.getEnrollmentId().equals(serviceEnrollmentId));
        if (!serviceExists) {
            throw new IllegalArgumentException("Service enrollment not found: " + serviceEnrollmentId);
        }

        AccountEnrollment enrollment = new AccountEnrollment(serviceEnrollmentId, accountId);
        this.accountEnrollments.add(enrollment);
        this.updatedAt = Instant.now();

        return enrollment;
    }

    public void suspend(String reason) {
        if (this.status == Status.CLOSED) {
            throw new IllegalStateException("Cannot suspend a closed profile");
        }
        this.status = Status.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    // Public methods for reconstruction from persistence (used by infrastructure mapper)
    // Note: These should only be called during aggregate reconstitution from database
    public void addExistingServiceEnrollment(ServiceEnrollment enrollment) {
        this.serviceEnrollments.add(enrollment);
    }

    public void addExistingAccountEnrollment(AccountEnrollment enrollment) {
        this.accountEnrollments.add(enrollment);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Getters
    public ServicingProfileId getProfileId() { return profileId; }
    public ClientId getClientId() { return clientId; }
    public Status getStatus() { return status; }
    public List<ServiceEnrollment> getServiceEnrollments() { return List.copyOf(serviceEnrollments); }
    public List<AccountEnrollment> getAccountEnrollments() { return List.copyOf(accountEnrollments); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
}
