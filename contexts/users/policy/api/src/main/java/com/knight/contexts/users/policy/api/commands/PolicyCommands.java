package com.knight.contexts.users.policy.api.commands;

import com.knight.platform.sharedkernel.ProfileId;

import java.math.BigDecimal;
import java.util.List;

/**
 * Command interface for Policy bounded context.
 * Handles creation and modification of permission and approval statements.
 */
public interface PolicyCommands {

    /**
     * Create a new permission statement for a profile
     */
    String createPermissionStatement(CreatePermissionStatementCmd cmd);

    /**
     * Update an existing permission statement
     */
    void updatePermissionStatement(UpdatePermissionStatementCmd cmd);

    /**
     * Delete a permission statement
     */
    void deletePermissionStatement(DeleteStatementCmd cmd);

    /**
     * Create a new approval statement for a profile
     */
    String createApprovalStatement(CreateApprovalStatementCmd cmd);

    /**
     * Update an existing approval statement
     */
    void updateApprovalStatement(UpdateApprovalStatementCmd cmd);

    /**
     * Delete an approval statement
     */
    void deleteApprovalStatement(DeleteStatementCmd cmd);

    record CreatePermissionStatementCmd(
        String profileUrn,
        String subject,       // e.g., "user:john.doe@example.com"
        String action,        // e.g., "receivables:view", "receivables:approve"
        String resource,      // e.g., "urn:knight:receivable:*"
        Effect effect         // ALLOW or DENY
    ) {}

    record UpdatePermissionStatementCmd(
        String statementId,
        String action,
        String resource,
        Effect effect
    ) {}

    record CreateApprovalStatementCmd(
        String profileUrn,
        String subject,
        String action,
        String resource,
        int approverCount,            // 1 for single, 2 for dual
        List<String> approvers,       // List of user URNs
        BigDecimal amountThreshold    // Optional threshold (null means any amount)
    ) {}

    record UpdateApprovalStatementCmd(
        String statementId,
        int approverCount,
        List<String> approvers,
        BigDecimal amountThreshold
    ) {}

    record DeleteStatementCmd(
        String statementId
    ) {}

    enum Effect {
        ALLOW,
        DENY
    }
}
