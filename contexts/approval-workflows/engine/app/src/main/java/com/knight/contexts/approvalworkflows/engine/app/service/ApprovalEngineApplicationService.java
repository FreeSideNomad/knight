package com.knight.contexts.approvalworkflows.engine.app.service;

import com.knight.contexts.approvalworkflows.engine.api.commands.ApprovalEngineCommands;
import com.knight.contexts.approvalworkflows.engine.api.events.ApprovalReceived;
import com.knight.contexts.approvalworkflows.engine.api.events.ApprovalWorkflowCompleted;
import com.knight.contexts.approvalworkflows.engine.api.events.ApprovalWorkflowStarted;
import com.knight.contexts.approvalworkflows.engine.api.queries.ApprovalEngineQueries;
import com.knight.contexts.approvalworkflows.engine.app.repository.ApprovalWorkflowRepository;
import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service implementing commands and queries for Approval Engine.
 * Orchestrates domain operations, repository access, and event publishing.
 */
@Service
public class ApprovalEngineApplicationService implements ApprovalEngineCommands, ApprovalEngineQueries {

    private final ApprovalWorkflowRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ApprovalEngineApplicationService(ApprovalWorkflowRepository repository,
                                           ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public String startApprovalWorkflow(StartApprovalWorkflowCmd cmd) {
        // Generate workflow ID
        String workflowId = UUID.randomUUID().toString();

        // TODO: Fetch approval policy from Policy BC to determine:
        // - requiredApprovals count
        // - eligibleApprovers list
        // For MVP, we'll use hardcoded values
        int requiredApprovals = 1;  // Single approver for MVP
        List<String> eligibleApprovers = List.of("approver-1", "approver-2");

        // Create aggregate
        ApprovalWorkflow workflow = ApprovalWorkflow.start(
            workflowId,
            cmd.statementId(),
            cmd.profileId(),
            cmd.requesterId(),
            cmd.action(),
            cmd.resource(),
            cmd.amount(),
            requiredApprovals,
            eligibleApprovers
        );

        // Save
        repository.save(workflow);

        // Publish event
        ApprovalWorkflowStarted event = new ApprovalWorkflowStarted(
            workflowId,
            cmd.statementId(),
            cmd.profileId(),
            cmd.requesterId(),
            cmd.action(),
            cmd.resource(),
            cmd.amount(),
            requiredApprovals,
            workflow.createdAt()
        );
        eventPublisher.publishEvent(event);

        return workflowId;
    }

    @Override
    @Transactional
    public void approveWorkflow(ApproveWorkflowCmd cmd) {
        ApprovalWorkflow workflow = repository.findById(cmd.workflowId())
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + cmd.workflowId()));

        ApprovalWorkflow.Status previousStatus = workflow.status();

        workflow.approve(cmd.approverId(), cmd.comment());

        repository.save(workflow);

        // Publish approval received event
        ApprovalReceived approvalEvent = new ApprovalReceived(
            cmd.workflowId(),
            cmd.approverId(),
            "APPROVED",
            cmd.comment(),
            workflow.receivedApprovals().get(workflow.receivedApprovals().size() - 1).approvedAt()
        );
        eventPublisher.publishEvent(approvalEvent);

        // If workflow completed, publish completion event
        if (previousStatus == ApprovalWorkflow.Status.PENDING &&
            workflow.status() == ApprovalWorkflow.Status.APPROVED) {
            ApprovalWorkflowCompleted completedEvent = new ApprovalWorkflowCompleted(
                workflow.workflowId(),
                workflow.statementId(),
                workflow.profileId(),
                "APPROVED",
                cmd.approverId(),
                workflow.completedAt()
            );
            eventPublisher.publishEvent(completedEvent);
        }
    }

    @Override
    @Transactional
    public void rejectWorkflow(RejectWorkflowCmd cmd) {
        ApprovalWorkflow workflow = repository.findById(cmd.workflowId())
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + cmd.workflowId()));

        workflow.reject(cmd.approverId(), cmd.reason());

        repository.save(workflow);

        // Publish rejection event
        ApprovalReceived rejectionEvent = new ApprovalReceived(
            cmd.workflowId(),
            cmd.approverId(),
            "REJECTED",
            cmd.reason(),
            workflow.receivedApprovals().get(workflow.receivedApprovals().size() - 1).approvedAt()
        );
        eventPublisher.publishEvent(rejectionEvent);

        // Publish workflow completed event (rejection completes immediately)
        ApprovalWorkflowCompleted completedEvent = new ApprovalWorkflowCompleted(
            workflow.workflowId(),
            workflow.statementId(),
            workflow.profileId(),
            "REJECTED",
            cmd.approverId(),
            workflow.completedAt()
        );
        eventPublisher.publishEvent(completedEvent);
    }

    @Override
    @Transactional
    public void cancelWorkflow(CancelWorkflowCmd cmd) {
        ApprovalWorkflow workflow = repository.findById(cmd.workflowId())
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + cmd.workflowId()));

        workflow.cancel(cmd.cancelledBy(), cmd.reason());

        repository.save(workflow);

        // Publish workflow completed event
        ApprovalWorkflowCompleted completedEvent = new ApprovalWorkflowCompleted(
            workflow.workflowId(),
            workflow.statementId(),
            workflow.profileId(),
            "CANCELLED",
            cmd.cancelledBy(),
            workflow.completedAt()
        );
        eventPublisher.publishEvent(completedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowStatus getWorkflowStatus(String workflowId) {
        ApprovalWorkflow workflow = repository.findById(workflowId)
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + workflowId));

        long approvalCount = workflow.receivedApprovals().stream()
            .filter(a -> "APPROVED".equals(a.decision()))
            .count();

        return new WorkflowStatus(
            workflow.workflowId(),
            workflow.statementId(),
            workflow.profileId(),
            workflow.status().name(),
            workflow.requiredApprovals(),
            (int) approvalCount,
            workflow.createdAt(),
            workflow.completedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingApprovalSummary> getPendingApprovals(String approverId) {
        List<ApprovalWorkflow> workflows = repository.findPendingForApprover(approverId);

        return workflows.stream()
            .filter(w -> w.status() == ApprovalWorkflow.Status.PENDING)
            .filter(w -> w.eligibleApprovers().contains(approverId))
            .filter(w -> !w.requesterId().equals(approverId)) // Can't approve own request
            .map(w -> {
                long approvalCount = w.receivedApprovals().stream()
                    .filter(a -> "APPROVED".equals(a.decision()))
                    .count();

                return new PendingApprovalSummary(
                    w.workflowId(),
                    w.statementId(),
                    w.profileId(),
                    w.requesterId(),
                    w.action(),
                    w.resource(),
                    w.createdAt(),
                    w.requiredApprovals(),
                    (int) approvalCount
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowHistory getWorkflowHistory(String workflowId) {
        ApprovalWorkflow workflow = repository.findById(workflowId)
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + workflowId));

        List<ApprovalDecision> decisions = workflow.receivedApprovals().stream()
            .map(a -> new ApprovalDecision(
                a.approverId(),
                a.decision(),
                a.comment(),
                a.approvedAt()
            ))
            .collect(Collectors.toList());

        return new WorkflowHistory(
            workflow.workflowId(),
            workflow.statementId(),
            workflow.status().name(),
            decisions,
            workflow.createdAt(),
            workflow.completedAt()
        );
    }
}
