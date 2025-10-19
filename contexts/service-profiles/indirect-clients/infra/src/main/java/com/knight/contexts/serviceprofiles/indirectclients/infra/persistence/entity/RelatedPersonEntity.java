package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "related_persons", schema = "indirect_clients")
public class RelatedPersonEntity {

    @Id
    @Column(name = "person_id", nullable = false)
    private String personId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "indirect_client_id", nullable = false)
    private IndirectClientEntity indirectClient;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    public RelatedPersonEntity() {
        // JPA requires no-arg constructor
    }

    public RelatedPersonEntity(String personId, IndirectClientEntity indirectClient, String name, String role, String email) {
        this.personId = personId;
        this.indirectClient = indirectClient;
        this.name = name;
        this.role = role;
        this.email = email;
        this.addedAt = Instant.now();
    }

    // Getters and setters
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public IndirectClientEntity getIndirectClient() {
        return indirectClient;
    }

    public void setIndirectClient(IndirectClientEntity indirectClient) {
        this.indirectClient = indirectClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedPersonEntity)) return false;
        RelatedPersonEntity that = (RelatedPersonEntity) o;
        return personId != null && personId.equals(that.personId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
