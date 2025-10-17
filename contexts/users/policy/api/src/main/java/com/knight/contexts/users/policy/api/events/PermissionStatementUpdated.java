package com.knight.contexts.users.policy.api.events;

import com.knight.contexts.users.policy.api.commands.PolicyCommands.Effect;

import java.time.Instant;

/**
 * Domain event published when a permission statement is updated.
 */
public record PermissionStatementUpdated(
    String statementId,
    String action,
    String resource,
    Effect effect,
    Instant updatedAt
) {}
