package com.knight.contexts.approvalworkflows.engine.api.events;

import java.time.Instant;

/**
 * Domain event published when an approval workflow completes.
 * Outcome can be APPROVED, REJECTED, EXPIRED, or CANCELLED.
 */
public record ApprovalWorkflowCompleted(
    String workflowId,
    String statementId,
    String profileId,
    String outcome,             // APPROVED, REJECTED, EXPIRED, CANCELLED
    String completedBy,         // User who completed it (approver or canceller)
    Instant completedAt
) {}
