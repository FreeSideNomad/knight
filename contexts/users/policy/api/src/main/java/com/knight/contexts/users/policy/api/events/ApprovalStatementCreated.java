package com.knight.contexts.users.policy.api.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Domain event published when an approval statement is created.
 */
public record ApprovalStatementCreated(
    String statementId,
    String profileUrn,
    String subject,
    String action,
    String resource,
    int approverCount,
    List<String> approvers,
    BigDecimal amountThreshold,
    Instant createdAt
) {}
