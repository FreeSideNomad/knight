package com.knight.contexts.users.users.api.queries;

import com.knight.platform.sharedkernel.UserId;

public interface UserQueries {

    record UserSummary(
        String userId,
        String email,
        String status,
        String userType,
        String identityProvider
    ) {}

    UserSummary getUserSummary(UserId userId);
}
