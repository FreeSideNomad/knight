package com.knight.contexts.approvalworkflows.engine.api.events;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain event published when an approval workflow is started.
 */
public record ApprovalWorkflowStarted(
    String workflowId,
    String statementId,
    String profileId,
    String requesterId,
    String action,
    String resource,
    BigDecimal amount,
    int requiredApprovals,
    Instant startedAt
) {}
