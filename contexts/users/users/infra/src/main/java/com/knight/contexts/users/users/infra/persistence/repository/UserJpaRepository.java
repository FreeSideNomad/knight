package com.knight.contexts.users.users.infra.persistence.repository;

import com.knight.contexts.users.users.infra.persistence.entity.UserJpaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for User entities.
 */
@Repository
public interface UserJpaRepository extends CrudRepository<UserJpaEntity, String> {

    /**
     * Find user by email address.
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /**
     * Find all users for a given client.
     */
    List<UserJpaEntity> findByClientId(String clientId);

    /**
     * Find users by status.
     */
    List<UserJpaEntity> findByStatus(UserJpaEntity.Status status);

    /**
     * Find users by type.
     */
    List<UserJpaEntity> findByUserType(UserJpaEntity.UserType userType);

    /**
     * Check if email already exists.
     */
    boolean existsByEmail(String email);
}
