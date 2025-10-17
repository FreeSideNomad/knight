package com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Indirect Client Aggregate Root.
 * Manages indirect clients (payors) associated with a parent client.
 */
public class IndirectClient {

    public enum Status {
        PENDING, ACTIVE, SUSPENDED
    }

    public enum ClientType {
        PERSON, BUSINESS
    }

    public static class RelatedPerson {
        private final String personId;
        private final String name;
        private final String role;
        private final String email;
        private final Instant addedAt;

        public RelatedPerson(String name, String role, String email) {
            this.personId = java.util.UUID.randomUUID().toString();
            this.name = name;
            this.role = role;
            this.email = email;
            this.addedAt = Instant.now();
        }

        public String getPersonId() { return personId; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public String getEmail() { return email; }
        public Instant getAddedAt() { return addedAt; }
    }

    private final IndirectClientId indirectClientId;
    private final ClientId parentClientId;
    private ClientType clientType;
    private String businessName;
    private String taxId;
    private Status status;
    private final List<RelatedPerson> relatedPersons;
    private final Instant createdAt;
    private Instant updatedAt;

    private IndirectClient(IndirectClientId indirectClientId, ClientId parentClientId,
                          ClientType clientType, String businessName, String taxId) {
        this.indirectClientId = indirectClientId;
        this.parentClientId = parentClientId;
        this.clientType = clientType;
        this.businessName = businessName;
        this.taxId = taxId;
        this.status = Status.PENDING;
        this.relatedPersons = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static IndirectClient create(IndirectClientId indirectClientId, ClientId parentClientId,
                                       String businessName, String taxId) {
        if (indirectClientId == null) {
            throw new IllegalArgumentException("Indirect client ID cannot be null");
        }
        if (parentClientId == null) {
            throw new IllegalArgumentException("Parent client ID cannot be null");
        }
        if (businessName == null || businessName.isBlank()) {
            throw new IllegalArgumentException("Business name cannot be null or blank");
        }

        return new IndirectClient(indirectClientId, parentClientId, ClientType.BUSINESS,
                                 businessName, taxId);
    }

    public void addRelatedPerson(String name, String role, String email) {
        if (this.status == Status.SUSPENDED) {
            throw new IllegalStateException("Cannot add related person to suspended indirect client");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Related person name cannot be null or blank");
        }

        RelatedPerson person = new RelatedPerson(name, role, email);
        this.relatedPersons.add(person);
        this.updatedAt = Instant.now();

        // Activate if pending and has at least one related person for business type
        if (this.status == Status.PENDING && this.clientType == ClientType.BUSINESS &&
            !this.relatedPersons.isEmpty()) {
            this.status = Status.ACTIVE;
        }
    }

    public void updateBusinessInfo(String businessName, String taxId) {
        if (this.status == Status.SUSPENDED) {
            throw new IllegalStateException("Cannot update suspended indirect client");
        }
        if (this.clientType != ClientType.BUSINESS) {
            throw new IllegalStateException("Can only update business info for business type clients");
        }
        if (businessName != null && !businessName.isBlank()) {
            this.businessName = businessName;
        }
        this.taxId = taxId;
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        if (this.status == Status.SUSPENDED) {
            throw new IllegalStateException("Indirect client is already suspended");
        }
        this.status = Status.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == Status.ACTIVE) {
            return;
        }
        // Business type must have at least one related person
        if (this.clientType == ClientType.BUSINESS && this.relatedPersons.isEmpty()) {
            throw new IllegalStateException("Business type client must have at least one related person");
        }
        this.status = Status.ACTIVE;
        this.updatedAt = Instant.now();
    }

    // Getters
    public IndirectClientId getIndirectClientId() { return indirectClientId; }
    public ClientId getParentClientId() { return parentClientId; }
    public ClientType getClientType() { return clientType; }
    public String getBusinessName() { return businessName; }
    public String getTaxId() { return taxId; }
    public Status getStatus() { return status; }
    public List<RelatedPerson> getRelatedPersons() { return List.copyOf(relatedPersons); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
