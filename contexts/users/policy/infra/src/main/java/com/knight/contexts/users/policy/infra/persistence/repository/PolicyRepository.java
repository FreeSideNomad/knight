package com.knight.contexts.users.policy.infra.persistence.repository;

import com.knight.contexts.users.policy.infra.persistence.entity.PolicyEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, String> {

    Optional<PolicyEntity> findByPolicyId(String policyId);

    List<PolicyEntity> findByPolicyType(PolicyEntity.PolicyType policyType);

    List<PolicyEntity> findBySubject(String subject);

    List<PolicyEntity> findBySubjectAndAction(String subject, String action);

    List<PolicyEntity> findBySubjectAndActionAndResource(String subject, String action, String resource);

    boolean existsByPolicyId(String policyId);
}
