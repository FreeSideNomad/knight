package com.knight.contexts.approvalworkflows.engine.infra.persistence;

import com.knight.contexts.approvalworkflows.engine.app.repository.ApprovalWorkflowRepository;
import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ApprovalWorkflowRepository.
 * For MVP/testing. Replace with JPA implementation for production.
 */
@Repository
public class InMemoryApprovalWorkflowRepository implements ApprovalWorkflowRepository {

    private final Map<String, ApprovalWorkflow> store = new ConcurrentHashMap<>();

    @Override
    public void save(ApprovalWorkflow workflow) {
        store.put(workflow.workflowId(), workflow);
    }

    @Override
    public Optional<ApprovalWorkflow> findById(String workflowId) {
        return Optional.ofNullable(store.get(workflowId));
    }

    @Override
    public List<ApprovalWorkflow> findByStatementId(String statementId) {
        return store.values().stream()
            .filter(w -> w.statementId().equals(statementId))
            .collect(Collectors.toList());
    }

    @Override
    public List<ApprovalWorkflow> findPendingForApprover(String approverId) {
        return store.values().stream()
            .filter(w -> w.status() == ApprovalWorkflow.Status.PENDING)
            .filter(w -> w.eligibleApprovers().contains(approverId))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(String workflowId) {
        store.remove(workflowId);
    }
}
