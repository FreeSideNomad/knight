package com.knight.contexts.approvalworkflows.engine.infra.persistence.mapper;

import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.ApprovalEntity;
import com.knight.contexts.approvalworkflows.engine.infra.persistence.entity.ApprovalWorkflowEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "jsr330")
public interface ApprovalWorkflowMapper {

    // Entity to Domain
    @Mapping(target = "receivedApprovals", source = "receivedApprovals")
    ApprovalWorkflow toDomain(ApprovalWorkflowEntity entity);

    // Domain to Entity
    @Mapping(target = "receivedApprovals", ignore = true)
    @Mapping(target = "version", ignore = true)
    ApprovalWorkflowEntity toEntity(ApprovalWorkflow domain);

    // After mapping - handle bidirectional relationships
    @AfterMapping
    default void linkApprovals(@MappingTarget ApprovalWorkflowEntity entity, ApprovalWorkflow domain) {
        if (domain.getReceivedApprovals() != null) {
            List<ApprovalEntity> approvalEntities = domain.getReceivedApprovals().stream()
                .map(approval -> {
                    ApprovalEntity e = new ApprovalEntity();
                    e.setApprovalId(approval.getApprovalId());
                    e.setApproverUserId(approval.getApproverUserId());
                    e.setDecision(mapDecision(approval.getDecision()));
                    e.setComments(approval.getComments());
                    e.setApprovedAt(approval.getApprovedAt());
                    e.setApprovalWorkflow(entity);
                    return e;
                })
                .collect(Collectors.toList());
            entity.setReceivedApprovals(approvalEntities);
        }
    }

    // Approval mappings
    default ApprovalWorkflow.Approval toApprovalDomain(ApprovalEntity entity) {
        return new ApprovalWorkflow.Approval(
            entity.getApproverUserId(),
            mapDecision(entity.getDecision()),
            entity.getComments()
        );
    }

    // Status enum mappings
    default ApprovalWorkflowEntity.Status mapStatus(ApprovalWorkflow.Status status) {
        if (status == null) return null;
        return ApprovalWorkflowEntity.Status.valueOf(status.name());
    }

    default ApprovalWorkflow.Status mapStatus(ApprovalWorkflowEntity.Status status) {
        if (status == null) return null;
        return ApprovalWorkflow.Status.valueOf(status.name());
    }

    // Decision enum mappings
    default ApprovalEntity.Decision mapDecision(ApprovalWorkflow.Decision decision) {
        if (decision == null) return null;
        return ApprovalEntity.Decision.valueOf(decision.name());
    }

    default ApprovalWorkflow.Decision mapDecision(ApprovalEntity.Decision decision) {
        if (decision == null) return null;
        return ApprovalWorkflow.Decision.valueOf(decision.name());
    }
}
