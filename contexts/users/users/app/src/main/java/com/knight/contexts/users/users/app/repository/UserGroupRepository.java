package com.knight.contexts.users.users.app.repository;

import com.knight.contexts.users.users.domain.aggregate.UserGroup;
import com.knight.platform.sharedkernel.UserGroupId;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserGroup aggregate.
 * To be implemented by infra layer.
 */
public interface UserGroupRepository {

    void save(UserGroup group);

    Optional<UserGroup> findById(UserGroupId groupId);

    List<UserGroup> findByProfileId(ServicingProfileId profileId);

    List<UserGroup> findByUserId(UserId userId);

    void delete(UserGroupId groupId);
}
