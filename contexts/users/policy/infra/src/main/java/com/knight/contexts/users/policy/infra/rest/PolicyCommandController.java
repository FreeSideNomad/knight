package com.knight.contexts.users.policy.infra.rest;

import com.knight.contexts.users.policy.api.commands.PolicyCommands;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller exposing command endpoints for Policy management.
 */
@RestController
@RequestMapping("/commands/users/policy")
public class PolicyCommandController {

    private final PolicyCommands commands;

    public PolicyCommandController(PolicyCommands commands) {
        this.commands = commands;
    }

    @PostMapping("/permission-statements")
    public ResponseEntity<CreateStatementResult> createPermissionStatement(
        @RequestBody CreatePermissionRequest request
    ) {
        PolicyCommands.CreatePermissionStatementCmd cmd = new PolicyCommands.CreatePermissionStatementCmd(
            request.profileUrn(),
            request.subject(),
            request.action(),
            request.resource(),
            request.effect()
        );

        String statementId = commands.createPermissionStatement(cmd);

        return ResponseEntity.ok(new CreateStatementResult(statementId));
    }

    @PutMapping("/permission-statements/{statementId}")
    public ResponseEntity<Void> updatePermissionStatement(
        @PathVariable String statementId,
        @RequestBody UpdatePermissionRequest request
    ) {
        PolicyCommands.UpdatePermissionStatementCmd cmd = new PolicyCommands.UpdatePermissionStatementCmd(
            statementId,
            request.action(),
            request.resource(),
            request.effect()
        );

        commands.updatePermissionStatement(cmd);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/permission-statements/{statementId}")
    public ResponseEntity<Void> deletePermissionStatement(@PathVariable String statementId) {
        PolicyCommands.DeleteStatementCmd cmd = new PolicyCommands.DeleteStatementCmd(statementId);

        commands.deletePermissionStatement(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/approval-statements")
    public ResponseEntity<CreateStatementResult> createApprovalStatement(
        @RequestBody CreateApprovalRequest request
    ) {
        PolicyCommands.CreateApprovalStatementCmd cmd = new PolicyCommands.CreateApprovalStatementCmd(
            request.profileUrn(),
            request.subject(),
            request.action(),
            request.resource(),
            request.approverCount(),
            request.approvers(),
            request.amountThreshold()
        );

        String statementId = commands.createApprovalStatement(cmd);

        return ResponseEntity.ok(new CreateStatementResult(statementId));
    }

    @PutMapping("/approval-statements/{statementId}")
    public ResponseEntity<Void> updateApprovalStatement(
        @PathVariable String statementId,
        @RequestBody UpdateApprovalRequest request
    ) {
        PolicyCommands.UpdateApprovalStatementCmd cmd = new PolicyCommands.UpdateApprovalStatementCmd(
            statementId,
            request.approverCount(),
            request.approvers(),
            request.amountThreshold()
        );

        commands.updateApprovalStatement(cmd);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/approval-statements/{statementId}")
    public ResponseEntity<Void> deleteApprovalStatement(@PathVariable String statementId) {
        PolicyCommands.DeleteStatementCmd cmd = new PolicyCommands.DeleteStatementCmd(statementId);

        commands.deleteApprovalStatement(cmd);

        return ResponseEntity.ok().build();
    }

    record CreatePermissionRequest(
        String profileUrn,
        String subject,
        String action,
        String resource,
        PolicyCommands.Effect effect
    ) {}

    record UpdatePermissionRequest(
        String action,
        String resource,
        PolicyCommands.Effect effect
    ) {}

    record CreateApprovalRequest(
        String profileUrn,
        String subject,
        String action,
        String resource,
        int approverCount,
        List<String> approvers,
        BigDecimal amountThreshold
    ) {}

    record UpdateApprovalRequest(
        int approverCount,
        List<String> approvers,
        BigDecimal amountThreshold
    ) {}

    record CreateStatementResult(String statementId) {}
}
