package com.knight.contexts.users.users.infra.persistence.repository;

import com.knight.contexts.users.users.infra.persistence.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByClientId(String clientId);

    List<UserEntity> findByStatus(UserEntity.Status status);

    List<UserEntity> findByUserType(UserEntity.UserType userType);

    List<UserEntity> findByClientIdAndUserType(String clientId, UserEntity.UserType userType);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
}
