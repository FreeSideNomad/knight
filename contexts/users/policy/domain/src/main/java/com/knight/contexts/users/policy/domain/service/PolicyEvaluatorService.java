package com.knight.contexts.users.policy.domain.service;

import com.knight.contexts.users.policy.api.commands.PolicyCommands.Effect;
import com.knight.contexts.users.policy.domain.aggregate.ApprovalStatement;
import com.knight.contexts.users.policy.domain.aggregate.PermissionStatement;

import java.math.BigDecimal;
import java.util.List;

/**
 * Domain service for evaluating permission and approval policies.
 * Implements AWS IAM-like evaluation logic: DENY overrides ALLOW.
 */
public class PolicyEvaluatorService {

    /**
     * Evaluate permission for subject on action + resource.
     * Logic: If any DENY matches, permission is denied.
     *        If any ALLOW matches, permission is allowed.
     *        Otherwise, permission is denied (default deny).
     */
    public static PermissionEvaluation evaluatePermission(
        List<PermissionStatement> statements,
        String subject,
        String action,
        String resource
    ) {
        // Check for DENY first
        for (PermissionStatement statement : statements) {
            if (statement.matches(subject, action, resource) && statement.effect() == Effect.DENY) {
                return new PermissionEvaluation(
                    false,
                    "Permission denied by explicit DENY statement: " + statement.statementId()
                );
            }
        }

        // Check for ALLOW
        for (PermissionStatement statement : statements) {
            if (statement.matches(subject, action, resource) && statement.effect() == Effect.ALLOW) {
                return new PermissionEvaluation(
                    true,
                    "Permission allowed by statement: " + statement.statementId()
                );
            }
        }

        // Default deny
        return new PermissionEvaluation(false, "No matching ALLOW statement (default deny)");
    }

    /**
     * Evaluate approval requirement for subject on action + resource + amount.
     * Returns the most specific matching approval statement.
     */
    public static ApprovalEvaluation evaluateApprovalRequirement(
        List<ApprovalStatement> statements,
        String subject,
        String action,
        String resource,
        BigDecimal amount
    ) {
        // Find all matching statements
        List<ApprovalStatement> matching = statements.stream()
            .filter(s -> s.matches(subject, action, resource))
            .filter(s -> s.meetsThreshold(amount))
            .toList();

        if (matching.isEmpty()) {
            return new ApprovalEvaluation(
                false,
                0,
                List.of(),
                "No approval requirement for this operation"
            );
        }

        // Take the first matching statement (in real impl, could prioritize by specificity)
        ApprovalStatement statement = matching.get(0);

        return new ApprovalEvaluation(
            true,
            statement.approverCount(),
            statement.approvers(),
            "Approval required by statement: " + statement.statementId()
        );
    }

    public record PermissionEvaluation(
        boolean allowed,
        String reason
    ) {}

    public record ApprovalEvaluation(
        boolean approvalRequired,
        int approverCount,
        List<String> approvers,
        String reason
    ) {}
}
