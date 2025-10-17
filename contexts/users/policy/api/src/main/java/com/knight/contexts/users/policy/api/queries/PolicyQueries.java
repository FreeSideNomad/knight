package com.knight.contexts.users.policy.api.queries;

import com.knight.contexts.users.policy.api.commands.PolicyCommands.Effect;

import java.math.BigDecimal;
import java.util.List;

/**
 * Query interface for Policy bounded context.
 * Provides policy evaluation and statement retrieval.
 */
public interface PolicyQueries {

    /**
     * Evaluate permission for a subject on action + resource
     */
    PermissionResult evaluatePermission(EvaluatePermissionQuery query);

    /**
     * Evaluate approval requirement for a subject on action + resource + amount
     */
    ApprovalRequirement evaluateApprovalRequirement(EvaluateApprovalQuery query);

    /**
     * Get all statements for a profile
     */
    List<StatementSummary> getStatementsForProfile(String profileUrn);

    record EvaluatePermissionQuery(
        String profileUrn,
        String subject,
        String action,
        String resource
    ) {}

    record PermissionResult(
        boolean allowed,
        String reason
    ) {}

    record EvaluateApprovalQuery(
        String profileUrn,
        String subject,
        String action,
        String resource,
        BigDecimal amount
    ) {}

    record ApprovalRequirement(
        boolean approvalRequired,
        int approverCount,
        List<String> approvers,
        String reason
    ) {}

    record StatementSummary(
        String statementId,
        String type,          // "PERMISSION" or "APPROVAL"
        String subject,
        String action,
        String resource,
        Effect effect,
        Integer approverCount,
        BigDecimal amountThreshold
    ) {}
}
