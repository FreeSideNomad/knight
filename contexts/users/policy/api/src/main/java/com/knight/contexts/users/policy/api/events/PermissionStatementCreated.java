package com.knight.contexts.users.policy.api.events;

import com.knight.contexts.users.policy.api.commands.PolicyCommands.Effect;

import java.time.Instant;

/**
 * Domain event published when a permission statement is created.
 */
public record PermissionStatementCreated(
    String statementId,
    String profileUrn,
    String subject,
    String action,
    String resource,
    Effect effect,
    Instant createdAt
) {}
