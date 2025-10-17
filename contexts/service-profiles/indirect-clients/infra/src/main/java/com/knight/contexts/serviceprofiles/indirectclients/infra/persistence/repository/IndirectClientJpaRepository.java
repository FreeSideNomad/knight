package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.repository;

import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.IndirectClientJpaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Micronaut Data JPA repository for IndirectClient.
 */
@Repository
public interface IndirectClientJpaRepository extends JpaRepository<IndirectClientJpaEntity, String> {

    /**
     * Find indirect clients by parent client ID.
     */
    List<IndirectClientJpaEntity> findByParentClientId(String parentClientId);

    /**
     * Find all indirect clients with specific status.
     */
    List<IndirectClientJpaEntity> findByStatus(IndirectClientJpaEntity.Status status);

    /**
     * Check if indirect client exists for parent.
     */
    boolean existsByParentClientId(String parentClientId);

    /**
     * Count indirect clients by parent.
     */
    long countByParentClientId(String parentClientId);
}
