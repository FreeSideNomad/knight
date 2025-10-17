package com.knight.contexts.users.users.api.events;

import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.UserGroupId;

import java.time.Instant;

/**
 * Domain event published when a user is removed from a group.
 */
public record UserRemovedFromGroup(
    UserGroupId groupId,
    UserId userId,
    Instant removedAt
) {}
