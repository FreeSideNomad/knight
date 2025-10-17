package com.knight.contexts.users.users.api.queries;

import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.util.List;

/**
 * Query interface for User Management bounded context.
 * Provides read models for users and groups.
 */
public interface UserQueries {

    /**
     * Get user details
     */
    UserDetail getUser(UserId userId);

    /**
     * List users by profile
     */
    List<UserSummary> listUsersByProfile(ServicingProfileId profileId);

    /**
     * Get administrators for profile
     */
    List<UserSummary> getAdministratorsForProfile(ServicingProfileId profileId);

    /**
     * Get user groups for user
     */
    List<GroupSummary> getUserGroups(UserId userId);

    record UserDetail(
        String userUrn,
        String profileUrn,
        String email,
        String firstName,
        String lastName,
        String role,
        String source,
        String status
    ) {}

    record UserSummary(
        String userUrn,
        String email,
        String firstName,
        String lastName,
        String role,
        String status
    ) {}

    record GroupSummary(
        String groupUrn,
        String name,
        int memberCount
    ) {}
}
