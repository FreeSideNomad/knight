package com.knight.contexts.users.policy.api.events;

import java.time.Instant;

/**
 * Domain event published when a permission statement is deleted.
 */
public record PermissionStatementDeleted(
    String statementId,
    Instant deletedAt
) {}
