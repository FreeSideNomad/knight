package com.knight.contexts.serviceprofiles.management.infra.persistence.repository;

import com.knight.contexts.serviceprofiles.management.infra.persistence.entity.ServicingProfileEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicingProfileRepository extends JpaRepository<ServicingProfileEntity, String> {

    @io.micronaut.data.annotation.Query("SELECT DISTINCT sp FROM ServicingProfileEntity sp " +
        "LEFT JOIN FETCH sp.serviceEnrollments " +
        "LEFT JOIN FETCH sp.accountEnrollments " +
        "WHERE sp.profileId = :profileId")
    Optional<ServicingProfileEntity> findByProfileId(String profileId);

    List<ServicingProfileEntity> findByClientId(String clientId);

    List<ServicingProfileEntity> findByStatus(ServicingProfileEntity.Status status);

    boolean existsByProfileId(String profileId);
}
