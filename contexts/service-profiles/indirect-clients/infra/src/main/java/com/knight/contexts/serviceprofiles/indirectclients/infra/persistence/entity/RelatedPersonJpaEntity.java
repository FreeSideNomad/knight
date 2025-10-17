package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA Entity for RelatedPerson child entity.
 * Represents signing officers, administrators, directors for business indirect clients.
 */
@Entity
@Table(name = "related_persons", schema = "icm")
public class RelatedPersonJpaEntity {

    @Id
    @Column(name = "person_id")
    private String personId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indirect_client_id", nullable = false)
    private IndirectClientJpaEntity indirectClient;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column
    private String email;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    // Default constructor for JPA
    public RelatedPersonJpaEntity() {}

    public RelatedPersonJpaEntity(
        String personId,
        String name,
        String role,
        String email,
        Instant addedAt
    ) {
        this.personId = personId;
        this.name = name;
        this.role = role;
        this.email = email;
        this.addedAt = addedAt;
        this.version = 0L;
    }

    // Getters and Setters
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public IndirectClientJpaEntity getIndirectClient() {
        return indirectClient;
    }

    public void setIndirectClient(IndirectClientJpaEntity indirectClient) {
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
