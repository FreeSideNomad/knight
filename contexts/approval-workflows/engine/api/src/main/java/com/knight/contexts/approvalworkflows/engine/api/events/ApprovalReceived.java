package com.knight.contexts.approvalworkflows.engine.api.events;

import java.time.Instant;

/**
 * Domain event published when an individual approval is received.
 */
public record ApprovalReceived(
    String workflowId,
    String approverId,
    String decision,            // APPROVED or REJECTED
    String comment,
    Instant approvedAt
) {}
