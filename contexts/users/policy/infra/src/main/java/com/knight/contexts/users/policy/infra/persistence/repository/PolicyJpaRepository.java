package com.knight.contexts.users.policy.infra.persistence.repository;

import com.knight.contexts.users.policy.infra.persistence.entity.PolicyJpaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

/**
 * JPA repository for Policy entities.
 */
@Repository
public interface PolicyJpaRepository extends CrudRepository<PolicyJpaEntity, String> {

    /**
     * Find policies by subject.
     */
    List<PolicyJpaEntity> findBySubject(String subject);

    /**
     * Find policies by type.
     */
    List<PolicyJpaEntity> findByPolicyType(PolicyJpaEntity.PolicyType policyType);

    /**
     * Find policies by subject and type.
     */
    List<PolicyJpaEntity> findBySubjectAndPolicyType(String subject, PolicyJpaEntity.PolicyType policyType);
}
