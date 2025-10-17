package com.knight.contexts.users.users.app.repository;

import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User aggregate.
 * To be implemented by infra layer.
 */
public interface UserRepository {

    void save(User user);

    Optional<User> findById(UserId userId);

    List<User> findByProfileId(ServicingProfileId profileId);

    List<User> findAdministratorsByProfileId(ServicingProfileId profileId);

    Optional<User> findByEmailAndProfileId(String email, ServicingProfileId profileId);

    void delete(UserId userId);
}
