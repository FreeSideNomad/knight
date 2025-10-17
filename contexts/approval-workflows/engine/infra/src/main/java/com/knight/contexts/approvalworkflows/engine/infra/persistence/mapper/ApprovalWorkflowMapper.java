package com.knight.contexts.approvalworkflows.engine.infra.persistence.mapper;

import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.ApprovalJpaEntity;
import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.ApprovalWorkflowJpaEntity;
import jakarta.inject.Singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual mapper between ApprovalWorkflow domain aggregate and ApprovalWorkflowJpaEntity.
 * Uses reflection to reconstruct domain aggregate from JPA entity due to private constructor.
 */
@Singleton
public class ApprovalWorkflowMapper {

    /**
     * Map domain ApprovalWorkflow to JPA entity.
     */
    public ApprovalWorkflowJpaEntity toEntity(ApprovalWorkflow workflow) {
        ApprovalWorkflowJpaEntity entity = new ApprovalWorkflowJpaEntity(
            workflow.getWorkflowId(),
            workflow.getResourceType(),
            workflow.getResourceId(),
            workflow.getRequiredApprovals(),
            mapStatus(workflow.getStatus()),
            workflow.getInitiatedBy(),
            workflow.getInitiatedAt(),
            workflow.getCompletedAt()
        );

        // Map approvals
        for (ApprovalWorkflow.Approval approval : workflow.getReceivedApprovals()) {
            ApprovalJpaEntity approvalEntity = new ApprovalJpaEntity(
                approval.getApprovalId(),
                approval.getApproverUserId(),
                mapDecision(approval.getDecision()),
                approval.getComments(),
                approval.getApprovedAt()
            );
            entity.addApproval(approvalEntity);
        }

        return entity;
    }

    /**
     * Map JPA entity to domain ApprovalWorkflow.
     * Uses reflection to reconstruct the aggregate since it has a private constructor.
     */
    public ApprovalWorkflow toDomain(ApprovalWorkflowJpaEntity entity) {
        try {
            // Create instance using reflection (private constructor bypass)
            Constructor<ApprovalWorkflow> constructor = ApprovalWorkflow.class.getDeclaredConstructor(
                String.class, String.class, String.class, int.class, String.class
            );
            constructor.setAccessible(true);

            ApprovalWorkflow workflow = constructor.newInstance(
                entity.getWorkflowId(),
                entity.getResourceType(),
                entity.getResourceId(),
                entity.getRequiredApprovals(),
                entity.getInitiatedBy()
            );

            // Set status using reflection
            Field statusField = ApprovalWorkflow.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(workflow, mapStatus(entity.getStatus()));

            // Set initiatedAt using reflection
            Field initiatedAtField = ApprovalWorkflow.class.getDeclaredField("initiatedAt");
            initiatedAtField.setAccessible(true);
            initiatedAtField.set(workflow, entity.getInitiatedAt());

            // Set completedAt using reflection
            Field completedAtField = ApprovalWorkflow.class.getDeclaredField("completedAt");
            completedAtField.setAccessible(true);
            completedAtField.set(workflow, entity.getCompletedAt());

            // Reconstruct approvals list using reflection
            Field approvalsField = ApprovalWorkflow.class.getDeclaredField("receivedApprovals");
            approvalsField.setAccessible(true);
            List<ApprovalWorkflow.Approval> approvals = new ArrayList<>();

            for (ApprovalJpaEntity approvalEntity : entity.getReceivedApprovals()) {
                ApprovalWorkflow.Approval approval = reconstructApproval(
                    approvalEntity.getApprovalId(),
                    approvalEntity.getApproverUserId(),
                    mapDecision(approvalEntity.getDecision()),
                    approvalEntity.getComments(),
                    approvalEntity.getApprovedAt()
                );
                approvals.add(approval);
            }
            approvalsField.set(workflow, approvals);

            return workflow;

        } catch (Exception e) {
            throw new RuntimeException("Failed to reconstruct ApprovalWorkflow from JPA entity", e);
        }
    }

    /**
     * Reconstruct Approval value object using reflection.
     */
    private ApprovalWorkflow.Approval reconstructApproval(
        String approvalId,
        String approverUserId,
        ApprovalWorkflow.Decision decision,
        String comments,
        java.time.Instant approvedAt
    ) {
        try {
            Constructor<ApprovalWorkflow.Approval> constructor =
                ApprovalWorkflow.Approval.class.getDeclaredConstructor(
                    String.class, ApprovalWorkflow.Decision.class, String.class
                );
            constructor.setAccessible(true);

            ApprovalWorkflow.Approval approval = constructor.newInstance(
                approverUserId, decision, comments
            );

            // Override the auto-generated approvalId with persisted one
            Field approvalIdField = ApprovalWorkflow.Approval.class.getDeclaredField("approvalId");
            approvalIdField.setAccessible(true);
            approvalIdField.set(approval, approvalId);

            // Override the auto-generated approvedAt with persisted one
            Field approvedAtField = ApprovalWorkflow.Approval.class.getDeclaredField("approvedAt");
            approvedAtField.setAccessible(true);
            approvedAtField.set(approval, approvedAt);

            return approval;

        } catch (Exception e) {
            throw new RuntimeException("Failed to reconstruct Approval", e);
        }
    }

    // Enum mapping helpers
    private ApprovalWorkflowJpaEntity.Status mapStatus(ApprovalWorkflow.Status domainStatus) {
        return ApprovalWorkflowJpaEntity.Status.valueOf(domainStatus.name());
    }

    private ApprovalWorkflow.Status mapStatus(ApprovalWorkflowJpaEntity.Status entityStatus) {
        return ApprovalWorkflow.Status.valueOf(entityStatus.name());
    }

    private ApprovalJpaEntity.Decision mapDecision(ApprovalWorkflow.Decision domainDecision) {
        return ApprovalJpaEntity.Decision.valueOf(domainDecision.name());
    }

    private ApprovalWorkflow.Decision mapDecision(ApprovalJpaEntity.Decision entityDecision) {
        return ApprovalWorkflow.Decision.valueOf(entityDecision.name());
    }
}
