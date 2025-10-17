package com.knight.contexts.users.users.api.commands;

import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;

/**
 * Command interface for User Management bounded context.
 * Handles creation and modification of users.
 */
public interface UserCommands {

    /**
     * Create a new user for a profile
     */
    UserId createUser(CreateUserCmd cmd);

    /**
     * Lock a user (by admin or bank)
     */
    void lockUser(LockUserCmd cmd);

    /**
     * Unlock a user
     */
    void unlockUser(UnlockUserCmd cmd);

    /**
     * Update user role
     */
    void updateUserRole(UpdateUserRoleCmd cmd);

    /**
     * Deactivate a user
     */
    void deactivateUser(DeactivateUserCmd cmd);

    record CreateUserCmd(
        ServicingProfileId profileId,
        String email,
        String firstName,
        String lastName,
        String role,
        String source
    ) {}

    record LockUserCmd(
        UserId userId,
        String lockedBy,
        String reason
    ) {}

    record UnlockUserCmd(
        UserId userId
    ) {}

    record UpdateUserRoleCmd(
        UserId userId,
        String newRole
    ) {}

    record DeactivateUserCmd(
        UserId userId
    ) {}
}
