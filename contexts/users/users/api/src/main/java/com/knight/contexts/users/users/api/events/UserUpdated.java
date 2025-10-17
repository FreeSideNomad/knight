package com.knight.contexts.users.users.api.events;

import com.knight.platform.sharedkernel.UserId;

import java.time.Instant;

/**
 * Domain event published when a user is updated.
 */
public record UserUpdated(
    UserId userId,
    String updatedField,
    String newValue,
    Instant updatedAt
) {}
