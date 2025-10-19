package com.knight.contexts.approvalworkflows.engine.infra.persistence.repository;

import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.ApprovalWorkflowEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflowEntity, String> {

    Optional<ApprovalWorkflowEntity> findByWorkflowId(String workflowId);

    List<ApprovalWorkflowEntity> findByResourceTypeAndResourceId(String resourceType, String resourceId);

    List<ApprovalWorkflowEntity> findByStatus(ApprovalWorkflowEntity.Status status);

    List<ApprovalWorkflowEntity> findByInitiatedBy(String initiatedBy);

    List<ApprovalWorkflowEntity> findByResourceType(String resourceType);

    boolean existsByWorkflowId(String workflowId);
}
