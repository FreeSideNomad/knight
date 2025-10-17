package com.knight.contexts.serviceprofiles.management.infra.persistence.repository;

import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.ServicingProfileJpaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Micronaut Data JPA repository for ServicingProfile.
 */
@Repository
public interface ServicingProfileJpaRepository extends JpaRepository<ServicingProfileJpaEntity, String> {

    /**
     * Find servicing profile by client URN.
     */
    Optional<ServicingProfileJpaEntity> findByClientUrn(String clientUrn);

    /**
     * Find all profiles with specific status.
     */
    List<ServicingProfileJpaEntity> findByStatus(ServicingProfileJpaEntity.Status status);

    /**
     * Check if profile exists for client.
     */
    boolean existsByClientUrn(String clientUrn);
}
