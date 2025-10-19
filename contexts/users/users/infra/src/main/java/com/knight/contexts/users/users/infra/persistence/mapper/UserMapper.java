package com.knight.contexts.users.users.infra.persistence.mapper;

import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.contexts.users.users.infra.persistence.entity.UserEntity;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.UserId;
import jakarta.inject.Singleton;

@Singleton
public class UserMapper {

    // Entity to Domain
    public User toDomain(UserEntity entity) {
        return User.create(
            UserId.of(entity.getUserId()),
            entity.getEmail(),
            mapUserType(entity.getUserType()),
            mapIdentityProvider(entity.getIdentityProvider()),
            ClientId.of(entity.getClientId())
        );
    }

    // Domain to Entity
    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity(
            domain.getUserId().id(),
            domain.getEmail(),
            mapUserType(domain.getUserType()),
            mapIdentityProvider(domain.getIdentityProvider()),
            domain.getClientId().urn()
        );

        entity.setStatus(mapStatus(domain.getStatus()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setLockReason(domain.getLockReason());
        entity.setDeactivationReason(domain.getDeactivationReason());

        return entity;
    }

    // Status enum mappings
    private UserEntity.Status mapStatus(User.Status status) {
        if (status == null) return null;
        return UserEntity.Status.valueOf(status.name());
    }

    private User.Status mapStatus(UserEntity.Status status) {
        if (status == null) return null;
        return User.Status.valueOf(status.name());
    }

    // UserType enum mappings
    private UserEntity.UserType mapUserType(User.UserType userType) {
        if (userType == null) return null;
        return UserEntity.UserType.valueOf(userType.name());
    }

    private User.UserType mapUserType(UserEntity.UserType userType) {
        if (userType == null) return null;
        return User.UserType.valueOf(userType.name());
    }

    // IdentityProvider enum mappings
    private UserEntity.IdentityProvider mapIdentityProvider(User.IdentityProvider identityProvider) {
        if (identityProvider == null) return null;
        return UserEntity.IdentityProvider.valueOf(identityProvider.name());
    }

    private User.IdentityProvider mapIdentityProvider(UserEntity.IdentityProvider identityProvider) {
        if (identityProvider == null) return null;
        return User.IdentityProvider.valueOf(identityProvider.name());
    }
}
