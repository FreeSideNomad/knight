package com.knight.contexts.approvalworkflows.engine.infra.persistence.repository;

import com.knight.contexts.approvalworkflows.engine.app.service.ApprovalWorkflowApplicationService;
import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.ApprovalWorkflowJpaEntity;
import com.knight.contexts.approvalworkflows.engine.infra.persistence.mapper.ApprovalWorkflowMapper;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Optional;

/**
 * JPA-based implementation of ApprovalWorkflowRepository.
 * Adapts domain repository interface to JPA infrastructure.
 */
@Singleton
public class ApprovalWorkflowRepositoryImpl implements ApprovalWorkflowApplicationService.ApprovalWorkflowRepository {

    private final ApprovalWorkflowJpaRepository jpaRepository;
    private final ApprovalWorkflowMapper mapper;

    public ApprovalWorkflowRepositoryImpl(
        ApprovalWorkflowJpaRepository jpaRepository,
        ApprovalWorkflowMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void save(ApprovalWorkflow workflow) {
        ApprovalWorkflowJpaEntity entity = mapper.toEntity(workflow);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<ApprovalWorkflow> findById(String workflowId) {
        return jpaRepository.findById(workflowId)
            .map(mapper::toDomain);
    }
}
