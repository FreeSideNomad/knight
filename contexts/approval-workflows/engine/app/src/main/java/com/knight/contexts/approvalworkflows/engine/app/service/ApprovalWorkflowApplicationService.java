package com.knight.contexts.approvalworkflows.engine.app.service;

import com.knight.contexts.approvalworkflows.engine.api.commands.ApprovalWorkflowCommands;
import com.knight.contexts.approvalworkflows.engine.api.events.WorkflowInitiated;
import com.knight.contexts.approvalworkflows.engine.api.queries.ApprovalWorkflowQueries;
import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Application service for Approval Workflow Engine.
 * Orchestrates approval workflow operations with transactions and event publishing.
 */
@Singleton
public class ApprovalWorkflowApplicationService implements ApprovalWorkflowCommands, ApprovalWorkflowQueries {

    private final ApprovalWorkflowRepository repository;
    private final ApplicationEventPublisher<Object> eventPublisher;

    public ApprovalWorkflowApplicationService(
        ApprovalWorkflowRepository repository,
        ApplicationEventPublisher<Object> eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public String initiateWorkflow(InitiateWorkflowCmd cmd) {
        // Create aggregate
        ApprovalWorkflow workflow = ApprovalWorkflow.initiate(
            cmd.resourceType(),
            cmd.resourceId(),
            cmd.requiredApprovals(),
            cmd.initiatedBy()
        );

        // Save
        repository.save(workflow);

        // Publish event
        eventPublisher.publishEvent(new WorkflowInitiated(
            workflow.getWorkflowId(),
            cmd.resourceType(),
            cmd.resourceId(),
            cmd.requiredApprovals(),
            Instant.now()
        ));

        return workflow.getWorkflowId();
    }

    @Override
    @Transactional
    public void recordApproval(RecordApprovalCmd cmd) {
        ApprovalWorkflow workflow = repository.findById(cmd.workflowId())
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + cmd.workflowId()));

        // Parse decision
        ApprovalWorkflow.Decision decision = ApprovalWorkflow.Decision.valueOf(cmd.decision());

        workflow.recordApproval(cmd.approverUserId(), decision, cmd.comments());

        repository.save(workflow);

        // Publish event based on new status
        if (workflow.getStatus() == ApprovalWorkflow.Status.APPROVED) {
            eventPublisher.publishEvent(new Object()); // WorkflowApproved event
        } else if (workflow.getStatus() == ApprovalWorkflow.Status.REJECTED) {
            eventPublisher.publishEvent(new Object()); // WorkflowRejected event
        } else {
            eventPublisher.publishEvent(new Object()); // ApprovalRecorded event
        }
    }

    @Override
    @Transactional
    public void expireWorkflow(ExpireWorkflowCmd cmd) {
        ApprovalWorkflow workflow = repository.findById(cmd.workflowId())
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + cmd.workflowId()));

        workflow.expire();

        repository.save(workflow);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // WorkflowExpired event
    }

    @Override
    public ApprovalWorkflowSummary getWorkflowSummary(String workflowId) {
        ApprovalWorkflow workflow = repository.findById(workflowId)
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + workflowId));

        long approveCount = workflow.getReceivedApprovals().stream()
            .filter(a -> a.getDecision() == ApprovalWorkflow.Decision.APPROVE)
            .count();

        return new ApprovalWorkflowSummary(
            workflow.getWorkflowId(),
            workflow.getStatus().name(),
            workflow.getResourceType(),
            workflow.getResourceId(),
            workflow.getRequiredApprovals(),
            (int) approveCount
        );
    }

    // Repository interface (to be implemented in infra layer)
    public interface ApprovalWorkflowRepository {
        void save(ApprovalWorkflow workflow);
        java.util.Optional<ApprovalWorkflow> findById(String workflowId);
    }
}
