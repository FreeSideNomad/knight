package com.knight.contexts.users.policy.api.events;

import java.time.Instant;

/**
 * Domain event published when an approval statement is deleted.
 */
public record ApprovalStatementDeleted(
    String statementId,
    Instant deletedAt
) {}
