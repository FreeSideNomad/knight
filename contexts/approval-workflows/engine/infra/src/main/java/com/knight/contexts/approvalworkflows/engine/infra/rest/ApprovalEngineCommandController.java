package com.knight.contexts.approvalworkflows.engine.infra.rest;

import com.knight.contexts.approvalworkflows.engine.api.commands.ApprovalEngineCommands;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST controller exposing command endpoints for Approval Engine.
 */
@RestController
@RequestMapping("/commands/approval-workflows/engine")
public class ApprovalEngineCommandController {

    private final ApprovalEngineCommands commands;

    public ApprovalEngineCommandController(ApprovalEngineCommands commands) {
        this.commands = commands;
    }

    @PostMapping("/start")
    public ResponseEntity<StartWorkflowResult> startApprovalWorkflow(@RequestBody StartWorkflowRequest request) {
        ApprovalEngineCommands.StartApprovalWorkflowCmd cmd = new ApprovalEngineCommands.StartApprovalWorkflowCmd(
            request.statementId(),
            request.profileId(),
            request.requesterId(),
            request.action(),
            request.resource(),
            request.amount()
        );

        String workflowId = commands.startApprovalWorkflow(cmd);

        return ResponseEntity.ok(new StartWorkflowResult(workflowId));
    }

    @PostMapping("/approve")
    public ResponseEntity<Void> approveWorkflow(@RequestBody ApproveWorkflowRequest request) {
        ApprovalEngineCommands.ApproveWorkflowCmd cmd = new ApprovalEngineCommands.ApproveWorkflowCmd(
            request.workflowId(),
            request.approverId(),
            request.comment()
        );

        commands.approveWorkflow(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> rejectWorkflow(@RequestBody RejectWorkflowRequest request) {
        ApprovalEngineCommands.RejectWorkflowCmd cmd = new ApprovalEngineCommands.RejectWorkflowCmd(
            request.workflowId(),
            request.approverId(),
            request.reason()
        );

        commands.rejectWorkflow(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelWorkflow(@RequestBody CancelWorkflowRequest request) {
        ApprovalEngineCommands.CancelWorkflowCmd cmd = new ApprovalEngineCommands.CancelWorkflowCmd(
            request.workflowId(),
            request.cancelledBy(),
            request.reason()
        );

        commands.cancelWorkflow(cmd);

        return ResponseEntity.ok().build();
    }

    record StartWorkflowRequest(
        String statementId,
        String profileId,
        String requesterId,
        String action,
        String resource,
        BigDecimal amount
    ) {}

    record StartWorkflowResult(String workflowId) {}

    record ApproveWorkflowRequest(
        String workflowId,
        String approverId,
        String comment
    ) {}

    record RejectWorkflowRequest(
        String workflowId,
        String approverId,
        String reason
    ) {}

    record CancelWorkflowRequest(
        String workflowId,
        String cancelledBy,
        String reason
    ) {}
}
