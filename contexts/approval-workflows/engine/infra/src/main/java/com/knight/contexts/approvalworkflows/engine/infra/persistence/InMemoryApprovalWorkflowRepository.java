package com.knight.contexts.approvalworkflows.engine.infra.persistence;

import com.knight.contexts.approvalworkflows.engine.app.service.ApprovalWorkflowApplicationService;
import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
import io.micronaut.context.annotation.Secondary;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for ApprovalWorkflow (MVP - replace with JPA in production).
 * Marked as @Secondary so JPA implementation takes precedence.
 */
@Singleton
@Secondary
public class InMemoryApprovalWorkflowRepository implements ApprovalWorkflowApplicationService.ApprovalWorkflowRepository {

    private final Map<String, ApprovalWorkflow> store = new ConcurrentHashMap<>();

    @Override
    public void save(ApprovalWorkflow workflow) {
        store.put(workflow.getWorkflowId(), workflow);
    }

    @Override
    public Optional<ApprovalWorkflow> findById(String workflowId) {
        return Optional.ofNullable(store.get(workflowId));
    }
}
