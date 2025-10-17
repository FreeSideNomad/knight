package com.knight.contexts.approvalworkflows.engine.infra.rest;

import com.knight.contexts.approvalworkflows.engine.api.commands.ApprovalWorkflowCommands;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

/**
 * REST controller for Approval Workflow Engine commands.
 */
@Controller("/commands/approval-workflows")
@ExecuteOn(TaskExecutors.BLOCKING)
public class ApprovalWorkflowCommandController {

    @Inject
    ApprovalWorkflowCommands commands;

    @Post("/initiate")
    public InitiateWorkflowResult initiateWorkflow(@Body InitiateWorkflowRequest req) {
        var workflowId = commands.initiateWorkflow(new ApprovalWorkflowCommands.InitiateWorkflowCmd(
            req.resourceType(),
            req.resourceId(),
            req.requiredApprovals(),
            req.initiatedBy()
        ));
        return new InitiateWorkflowResult(workflowId);
    }

    @Post("/record-approval")
    public void recordApproval(@Body RecordApprovalRequest req) {
        commands.recordApproval(new ApprovalWorkflowCommands.RecordApprovalCmd(
            req.workflowId(),
            req.approverUserId(),
            req.decision(),
            req.comments()
        ));
    }

    @Post("/expire")
    public void expireWorkflow(@Body ExpireWorkflowRequest req) {
        commands.expireWorkflow(new ApprovalWorkflowCommands.ExpireWorkflowCmd(req.workflowId()));
    }

    public record InitiateWorkflowRequest(String resourceType, String resourceId, int requiredApprovals, String initiatedBy) {}
    public record InitiateWorkflowResult(String workflowId) {}
    public record RecordApprovalRequest(String workflowId, String approverUserId, String decision, String comments) {}
    public record ExpireWorkflowRequest(String workflowId) {}
}
