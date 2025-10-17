package com.knight.contexts.users.users.api.events;

import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.UserGroupId;

import java.time.Instant;

/**
 * Domain event published when a user is added to a group.
 */
public record UserAddedToGroup(
    UserGroupId groupId,
    UserId userId,
    Instant addedAt
) {}
