package com.knight.contexts.approvalworkflows.engine.infra.persistence.repository;

import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.ApprovalWorkflowJpaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

/**
 * JPA repository for ApprovalWorkflow entities.
 */
@Repository
public interface ApprovalWorkflowJpaRepository extends CrudRepository<ApprovalWorkflowJpaEntity, String> {

    /**
     * Find workflows by resource.
     */
    List<ApprovalWorkflowJpaEntity> findByResourceTypeAndResourceId(String resourceType, String resourceId);

    /**
     * Find workflows by status.
     */
    List<ApprovalWorkflowJpaEntity> findByStatus(ApprovalWorkflowJpaEntity.Status status);

    /**
     * Find workflows initiated by a user.
     */
    List<ApprovalWorkflowJpaEntity> findByInitiatedBy(String initiatedBy);
}
