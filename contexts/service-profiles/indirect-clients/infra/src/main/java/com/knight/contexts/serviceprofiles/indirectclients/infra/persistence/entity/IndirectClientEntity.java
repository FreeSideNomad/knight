package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "indirect_clients", schema = "indirect_clients")
public class IndirectClientEntity {

    @Id
    @Column(name = "indirect_client_id", nullable = false)
    private String indirectClientId;

    @Column(name = "parent_client_id", nullable = false)
    private String parentClientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 20)
    private ClientType clientType;

    @Column(name = "business_name", length = 255)
    private String businessName;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "indirectClient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RelatedPersonEntity> relatedPersons = new ArrayList<>();

    @Version
    @Column(name = "version")
    private Long version;

    public enum Status {
        PENDING, ACTIVE, SUSPENDED
    }

    public enum ClientType {
        PERSON, BUSINESS
    }

    public IndirectClientEntity() {
        // JPA requires no-arg constructor
    }

    public IndirectClientEntity(String indirectClientId, String parentClientId, ClientType clientType, String businessName, String taxId) {
        this.indirectClientId = indirectClientId;
        this.parentClientId = parentClientId;
        this.clientType = clientType;
        this.businessName = businessName;
        this.taxId = taxId;
        this.status = Status.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Getters and setters
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

    public List<RelatedPersonEntity> getRelatedPersons() {
        return relatedPersons;
    }

    public void setRelatedPersons(List<RelatedPersonEntity> relatedPersons) {
        this.relatedPersons = relatedPersons;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndirectClientEntity)) return false;
        IndirectClientEntity that = (IndirectClientEntity) o;
        return indirectClientId != null && indirectClientId.equals(that.indirectClientId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
