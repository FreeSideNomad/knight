package com.knight.contexts.approvalworkflows.engine.app.repository;

import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ApprovalWorkflow aggregate.
 * To be implemented by infra layer.
 */
public interface ApprovalWorkflowRepository {

    void save(ApprovalWorkflow workflow);

    Optional<ApprovalWorkflow> findById(String workflowId);

    List<ApprovalWorkflow> findByStatementId(String statementId);

    List<ApprovalWorkflow> findPendingForApprover(String approverId);

    void delete(String workflowId);
}
