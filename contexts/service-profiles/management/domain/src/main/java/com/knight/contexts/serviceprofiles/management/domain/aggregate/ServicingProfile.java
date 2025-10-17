package com.knight.contexts.serviceprofiles.management.domain.aggregate;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ServicingProfile aggregate root.
 * Manages service enrollments and account enrollments for servicing clients.
 *
 * Invariants:
 * - profile_id must be unique
 * - client_id cannot change after creation
 * - At least one service must be enrolled for profile to be ACTIVE
 */
public class ServicingProfile {

    public enum Status { PENDING, ACTIVE, SUSPENDED, CLOSED }

    private final ServicingProfileId profileId;
    private final ClientId clientId;
    private Status status;
    private final List<ServiceEnrollment> serviceEnrollments;
    private final Instant createdAt;
    private Instant updatedAt;
    private final String createdBy;

    private ServicingProfile(ServicingProfileId profileId, ClientId clientId, String createdBy) {
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
        if (clientId == null) {
            throw new IllegalArgumentException("clientId cannot be null");
        }
        if (!clientId.isSrf() && !clientId.isGid()) {
            throw new IllegalArgumentException(
                "ServicingProfile requires SRF or GID client. Got: " + clientId.system()
            );
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy cannot be null or blank");
        }

        this.profileId = profileId;
        this.clientId = clientId;
        this.status = Status.PENDING;
        this.serviceEnrollments = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.createdBy = createdBy;
    }

    /**
     * Factory method to create a new servicing profile.
     * Initial status is PENDING until first service is enrolled.
     */
    public static ServicingProfile create(ServicingProfileId profileId, ClientId clientId, String createdBy) {
        return new ServicingProfile(profileId, clientId, createdBy);
    }

    /**
     * Enroll a stand-alone service to this profile.
     * Services: BTR, ACH_DEBIT_BLOCK, ADDITIONAL_DEPOSIT_NARRATIVE
     */
    public ServiceEnrollment enrollService(String serviceType, Object configuration) {
        if (serviceType == null || serviceType.isBlank()) {
            throw new IllegalArgumentException("serviceType cannot be null or blank");
        }

        String enrollmentId = UUID.randomUUID().toString();
        ServiceEnrollment enrollment = new ServiceEnrollment(
            enrollmentId,
            profileId,
            serviceType,
            configuration,
            Instant.now()
        );

        serviceEnrollments.add(enrollment);
        this.updatedAt = Instant.now();

        // Activate profile if first service enrollment
        if (status == Status.PENDING && !serviceEnrollments.isEmpty()) {
            status = Status.ACTIVE;
        }

        return enrollment;
    }

    /**
     * Enroll account to a specific service enrollment.
     */
    public AccountEnrollment enrollAccount(String serviceEnrollmentId, String accountId) {
        if (status != Status.ACTIVE) {
            throw new IllegalStateException("Cannot enroll account. Profile status must be ACTIVE. Current: " + status);
        }

        ServiceEnrollment serviceEnrollment = serviceEnrollments.stream()
            .filter(se -> se.enrollmentId().equals(serviceEnrollmentId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("ServiceEnrollment not found: " + serviceEnrollmentId));

        return serviceEnrollment.enrollAccount(accountId);
    }

    /**
     * Suspend profile (bank or admin action).
     */
    public void suspend(String reason) {
        if (status == Status.CLOSED) {
            throw new IllegalStateException("Cannot suspend CLOSED profile");
        }
        this.status = Status.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    /**
     * Activate profile if eligible (has at least one service).
     */
    public void activateIfEligible() {
        if (status == Status.PENDING && !serviceEnrollments.isEmpty()) {
            this.status = Status.ACTIVE;
            this.updatedAt = Instant.now();
        }
    }

    // Getters
    public ServicingProfileId profileId() { return profileId; }
    public ClientId clientId() { return clientId; }
    public Status status() { return status; }
    public List<ServiceEnrollment> serviceEnrollments() { return List.copyOf(serviceEnrollments); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public String createdBy() { return createdBy; }

    /**
     * ServiceEnrollment entity within ServicingProfile aggregate.
     */
    public static class ServiceEnrollment {
        public enum Status { ACTIVE, SUSPENDED, CANCELLED }

        private final String enrollmentId;
        private final ServicingProfileId profileId;
        private final String serviceType;
        private Object configuration;
        private Status status;
        private final List<AccountEnrollment> accountEnrollments;
        private final Instant enrolledAt;

        ServiceEnrollment(String enrollmentId, ServicingProfileId profileId, String serviceType,
                         Object configuration, Instant enrolledAt) {
            this.enrollmentId = enrollmentId;
            this.profileId = profileId;
            this.serviceType = serviceType;
            this.configuration = configuration;
            this.status = Status.ACTIVE;
            this.accountEnrollments = new ArrayList<>();
            this.enrolledAt = enrolledAt;
        }

        AccountEnrollment enrollAccount(String accountId) {
            if (status != Status.ACTIVE) {
                throw new IllegalStateException("Cannot enroll account. Service status must be ACTIVE");
            }

            String enrollmentId = UUID.randomUUID().toString();
            AccountEnrollment accountEnrollment = new AccountEnrollment(
                enrollmentId,
                this.enrollmentId,
                accountId,
                Instant.now()
            );
            accountEnrollments.add(accountEnrollment);
            return accountEnrollment;
        }

        public void updateConfiguration(Object newConfiguration) {
            this.configuration = newConfiguration;
        }

        public void suspend(String reason) {
            this.status = Status.SUSPENDED;
        }

        // Getters
        public String enrollmentId() { return enrollmentId; }
        public ServicingProfileId profileId() { return profileId; }
        public String serviceType() { return serviceType; }
        public Object configuration() { return configuration; }
        public Status status() { return status; }
        public List<AccountEnrollment> accountEnrollments() { return List.copyOf(accountEnrollments); }
        public Instant enrolledAt() { return enrolledAt; }
    }

    /**
     * AccountEnrollment entity within ServiceEnrollment.
     */
    public static class AccountEnrollment {
        public enum Status { ACTIVE, SUSPENDED, CLOSED }

        private final String enrollmentId;
        private final String serviceEnrollmentId;
        private final String accountId;
        private Status status;
        private final Instant enrolledAt;

        AccountEnrollment(String enrollmentId, String serviceEnrollmentId, String accountId, Instant enrolledAt) {
            this.enrollmentId = enrollmentId;
            this.serviceEnrollmentId = serviceEnrollmentId;
            this.accountId = accountId;
            this.status = Status.ACTIVE;
            this.enrolledAt = enrolledAt;
        }

        public void suspend(String reason) {
            this.status = Status.SUSPENDED;
        }

        // Getters
        public String enrollmentId() { return enrollmentId; }
        public String serviceEnrollmentId() { return serviceEnrollmentId; }
        public String accountId() { return accountId; }
        public Status status() { return status; }
        public Instant enrolledAt() { return enrolledAt; }
    }
}
