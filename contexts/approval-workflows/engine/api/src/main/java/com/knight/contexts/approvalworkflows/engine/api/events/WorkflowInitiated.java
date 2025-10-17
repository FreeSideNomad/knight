package com.knight.contexts.approvalworkflows.engine.api.events;

import java.time.Instant;

public record WorkflowInitiated(
    String workflowId,
    String resourceType,
    String resourceId,
    int requiredApprovals,
    Instant initiatedAt
) {}
