package com.knight.contexts.users.users.api.commands;

import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.UserGroupId;
import com.knight.platform.sharedkernel.ServicingProfileId;

/**
 * Command interface for User Group Management.
 * Handles creation and modification of user groups.
 */
public interface UserGroupCommands {

    /**
     * Create a new user group for a profile
     */
    UserGroupId createUserGroup(CreateUserGroupCmd cmd);

    /**
     * Add member to user group
     */
    void addMemberToGroup(AddMemberCmd cmd);

    /**
     * Remove member from user group
     */
    void removeMemberFromGroup(RemoveMemberCmd cmd);

    record CreateUserGroupCmd(
        ServicingProfileId profileId,
        String name,
        String description
    ) {}

    record AddMemberCmd(
        UserGroupId groupId,
        UserId userId
    ) {}

    record RemoveMemberCmd(
        UserGroupId groupId,
        UserId userId
    ) {}
}
