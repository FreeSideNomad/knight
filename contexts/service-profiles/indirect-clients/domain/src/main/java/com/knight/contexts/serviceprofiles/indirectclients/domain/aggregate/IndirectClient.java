package com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * IndirectClient aggregate root.
 * Manages indirect clients (payors) on behalf of direct clients.
 *
 * Invariants:
 * - indirectClientId must be unique
 * - parentClientId cannot change after creation
 * - Type must be BUSINESS (persons are not separate indirect clients in MVP)
 * - At least one related person must exist for BUSINESS type
 */
public class IndirectClient {

    public enum Status { PENDING, ACTIVE, SUSPENDED, CLOSED }
    public enum Type { BUSINESS }  // PERSON deferred to post-MVP

    private final IndirectClientId indirectClientId;
    private final ClientId parentClientId;
    private final Type type;
    private String businessName;
    private String taxId;
    private Status status;
    private final List<RelatedPerson> relatedPersons;
    private final Instant createdAt;
    private Instant updatedAt;
    private final String createdBy;

    private IndirectClient(IndirectClientId indirectClientId, ClientId parentClientId,
                          String businessName, String taxId, String createdBy) {
        if (indirectClientId == null) {
            throw new IllegalArgumentException("indirectClientId cannot be null");
        }
        if (parentClientId == null) {
            throw new IllegalArgumentException("parentClientId cannot be null");
        }
        if (!parentClientId.isSrf()) {
            throw new IllegalArgumentException(
                "IndirectClient requires SRF parent client. Got: " + parentClientId.system()
            );
        }
        if (businessName == null || businessName.isBlank()) {
            throw new IllegalArgumentException("businessName cannot be null or blank");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy cannot be null or blank");
        }

        this.indirectClientId = indirectClientId;
        this.parentClientId = parentClientId;
        this.type = Type.BUSINESS;  // Fixed for MVP
        this.businessName = businessName;
        this.taxId = taxId;
        this.status = Status.PENDING;
        this.relatedPersons = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.createdBy = createdBy;
    }

    /**
     * Factory method to create a new indirect client.
     * Initial status is PENDING until first related person is added.
     */
    public static IndirectClient create(IndirectClientId indirectClientId, ClientId parentClientId,
                                       String businessName, String taxId, String createdBy) {
        return new IndirectClient(indirectClientId, parentClientId, businessName, taxId, createdBy);
    }

    /**
     * Add a related person (signing officer, administrator, director).
     * At least one related person required for BUSINESS type.
     */
    public RelatedPerson addRelatedPerson(String personName, RelatedPerson.Role role,
                                         String email, String phone) {
        if (personName == null || personName.isBlank()) {
            throw new IllegalArgumentException("personName cannot be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }

        String personId = UUID.randomUUID().toString();
        RelatedPerson person = new RelatedPerson(
            personId,
            indirectClientId,
            personName,
            role,
            email,
            phone,
            Instant.now()
        );

        relatedPersons.add(person);
        this.updatedAt = Instant.now();

        // Activate if first related person added
        if (status == Status.PENDING && !relatedPersons.isEmpty()) {
            status = Status.ACTIVE;
        }

        return person;
    }

    /**
     * Update business information.
     */
    public void updateBusinessInfo(String newBusinessName, String newTaxId) {
        if (newBusinessName == null || newBusinessName.isBlank()) {
            throw new IllegalArgumentException("businessName cannot be null or blank");
        }

        this.businessName = newBusinessName;
        this.taxId = newTaxId;
        this.updatedAt = Instant.now();
    }

    /**
     * Suspend indirect client (bank or admin action).
     */
    public void suspend(String reason) {
        if (status == Status.CLOSED) {
            throw new IllegalStateException("Cannot suspend CLOSED indirect client");
        }
        this.status = Status.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    /**
     * Activate indirect client if eligible (has at least one related person).
     */
    public void activateIfEligible() {
        if (status == Status.PENDING && !relatedPersons.isEmpty()) {
            this.status = Status.ACTIVE;
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Close indirect client (terminal state).
     */
    public void close(String reason) {
        this.status = Status.CLOSED;
        this.updatedAt = Instant.now();
    }

    // Getters
    public IndirectClientId indirectClientId() { return indirectClientId; }
    public ClientId parentClientId() { return parentClientId; }
    public Type type() { return type; }
    public String businessName() { return businessName; }
    public String taxId() { return taxId; }
    public Status status() { return status; }
    public List<RelatedPerson> relatedPersons() { return List.copyOf(relatedPersons); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public String createdBy() { return createdBy; }

    /**
     * RelatedPerson entity within IndirectClient aggregate.
     * Represents signing officers, administrators, directors for business indirect clients.
     */
    public static class RelatedPerson {
        public enum Role { SIGNING_OFFICER, ADMINISTRATOR, DIRECTOR }
        public enum Status { ACTIVE, INACTIVE }

        private final String personId;
        private final IndirectClientId indirectClientId;
        private String personName;
        private Role role;
        private String email;
        private String phone;
        private Status status;
        private final Instant addedAt;
        private Instant updatedAt;

        RelatedPerson(String personId, IndirectClientId indirectClientId, String personName,
                     Role role, String email, String phone, Instant addedAt) {
            this.personId = personId;
            this.indirectClientId = indirectClientId;
            this.personName = personName;
            this.role = role;
            this.email = email;
            this.phone = phone;
            this.status = Status.ACTIVE;
            this.addedAt = addedAt;
            this.updatedAt = addedAt;
        }

        public void updateContactInfo(String newEmail, String newPhone) {
            this.email = newEmail;
            this.phone = newPhone;
            this.updatedAt = Instant.now();
        }

        public void changeRole(Role newRole) {
            if (newRole == null) {
                throw new IllegalArgumentException("role cannot be null");
            }
            this.role = newRole;
            this.updatedAt = Instant.now();
        }

        public void deactivate() {
            this.status = Status.INACTIVE;
            this.updatedAt = Instant.now();
        }

        // Getters
        public String personId() { return personId; }
        public IndirectClientId indirectClientId() { return indirectClientId; }
        public String personName() { return personName; }
        public Role role() { return role; }
        public String email() { return email; }
        public String phone() { return phone; }
        public Status status() { return status; }
        public Instant addedAt() { return addedAt; }
        public Instant updatedAt() { return updatedAt; }
    }
}
