package com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.repository;

import com.knight.contexts.serviceprofiles.indirectclients.infra.persistence.entity.IndirectClientEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndirectClientRepository extends JpaRepository<IndirectClientEntity, String> {

    Optional<IndirectClientEntity> findByIndirectClientId(String indirectClientId);

    List<IndirectClientEntity> findByParentClientId(String parentClientId);

    List<IndirectClientEntity> findByStatus(IndirectClientEntity.Status status);

    List<IndirectClientEntity> findByParentClientIdAndStatus(String parentClientId, IndirectClientEntity.Status status);

    boolean existsByIndirectClientId(String indirectClientId);
}
