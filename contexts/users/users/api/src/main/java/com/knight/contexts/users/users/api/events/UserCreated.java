package com.knight.contexts.users.users.api.events;

import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.time.Instant;

/**
 * Domain event published when a user is created.
 */
public record UserCreated(
    UserId userId,
    ServicingProfileId profileId,
    String email,
    String firstName,
    String lastName,
    String role,
    String source,
    Instant createdAt
) {}
