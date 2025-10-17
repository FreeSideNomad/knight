package com.knight.contexts.users.users.api.events;

import com.knight.platform.sharedkernel.UserId;

import java.time.Instant;

/**
 * Domain event published when a user is locked.
 */
public record UserLocked(
    UserId userId,
    String lockedBy,
    String reason,
    Instant lockedAt
) {}
