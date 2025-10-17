package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for IndirectClient aggregate.
 * Maps domain aggregate to relational database.
 */
@Entity
@Table(name = "indirect_clients", schema = "icm")
public class IndirectClientJpaEntity {

    @Id
    @Column(name = "indirect_client_id")
    private String indirectClientId;

    @Column(name = "parent_client_id", nullable = false)
    private String parentClientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 50)
    private ClientType clientType;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "tax_id")
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status;

    @OneToMany(
        mappedBy = "indirectClient",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<RelatedPersonJpaEntity> relatedPersons = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public enum Status {
        PENDING, ACTIVE, SUSPENDED
    }

    public enum ClientType {
        PERSON, BUSINESS
    }

    // Default constructor for JPA
    public IndirectClientJpaEntity() {}

    public IndirectClientJpaEntity(
        String indirectClientId,
        String parentClientId,
        ClientType clientType,
        String businessName,
        String taxId,
        Status status,
        Instant createdAt
    ) {
        this.indirectClientId = indirectClientId;
        this.parentClientId = parentClientId;
        this.clientType = clientType;
        this.businessName = businessName;
        this.taxId = taxId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.version = 0L;
    }

    // Getters and Setters
    public String getIndirectClientId() {
        return indirectClientId;
    }

    public void setIndirectClientId(String indirectClientId) {
        this.indirectClientId = indirectClientId;
    }

    public String getParentClientId() {
        return parentClientId;
    }

    public void setParentClientId(String parentClientId) {
        this.parentClientId = parentClientId;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<RelatedPersonJpaEntity> getRelatedPersons() {
        return relatedPersons;
    }

    public void setRelatedPersons(List<RelatedPersonJpaEntity> relatedPersons) {
        this.relatedPersons = relatedPersons;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Helper methods for bidirectional relationships
    public void addRelatedPerson(RelatedPersonJpaEntity person) {
        relatedPersons.add(person);
        person.setIndirectClient(this);
    }

    public void removeRelatedPerson(RelatedPersonJpaEntity person) {
        relatedPersons.remove(person);
        person.setIndirectClient(null);
    }
}
