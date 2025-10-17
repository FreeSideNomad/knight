package com.knight.contexts.users.policy.api.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Domain event published when an approval statement is updated.
 */
public record ApprovalStatementUpdated(
    String statementId,
    int approverCount,
    List<String> approvers,
    BigDecimal amountThreshold,
    Instant updatedAt
) {}
