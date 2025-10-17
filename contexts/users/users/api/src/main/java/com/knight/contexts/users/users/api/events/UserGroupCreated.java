package com.knight.contexts.users.users.api.events;

import com.knight.platform.sharedkernel.UserGroupId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.time.Instant;

/**
 * Domain event published when a user group is created.
 */
public record UserGroupCreated(
    UserGroupId groupId,
    ServicingProfileId profileId,
    String name,
    String description,
    Instant createdAt
) {}
