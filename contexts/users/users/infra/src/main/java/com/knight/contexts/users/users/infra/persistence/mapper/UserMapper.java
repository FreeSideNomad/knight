package com.knight.contexts.users.users.infra.persistence.mapper;

import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.contexts.users.users.infra.persistence.entity.UserJpaEntity;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.UserId;
import jakarta.inject.Singleton;

/**
 * Manual mapper between User domain aggregate and UserJpaEntity.
 * Cannot use MapStruct due to private constructor in domain aggregate.
 */
@Singleton
public class UserMapper {

    /**
     * Map domain User to JPA entity.
     */
    public UserJpaEntity toEntity(User user) {
        var entity = new UserJpaEntity(
            user.getUserId().id(),
            user.getEmail(),
            mapUserType(user.getUserType()),
            mapIdentityProvider(user.getIdentityProvider()),
            user.getClientId().urn(),
            mapStatus(user.getStatus()),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );

        entity.setLockReason(user.getLockReason());
        entity.setDeactivationReason(user.getDeactivationReason());

        return entity;
    }

    /**
     * Map JPA entity to domain User.
     * Uses User.create() factory method to reconstruct the aggregate.
     */
    public User toDomain(UserJpaEntity entity) {
        // Use factory method to create User
        User user = User.create(
            UserId.of(entity.getId()),
            entity.getEmail(),
            mapUserType(entity.getUserType()),
            mapIdentityProvider(entity.getIdentityProvider()),
            ClientId.of(entity.getClientId())
        );

        // Apply state changes to match persisted state
        User.Status domainStatus = mapStatus(entity.getStatus());

        if (domainStatus == User.Status.ACTIVE) {
            user.activate();
        } else if (domainStatus == User.Status.LOCKED && entity.getLockReason() != null) {
            user.lock(entity.getLockReason());
        } else if (domainStatus == User.Status.DEACTIVATED && entity.getDeactivationReason() != null) {
            user.deactivate(entity.getDeactivationReason());
        }

        return user;
    }

    // Enum mapping helpers
    private UserJpaEntity.UserType mapUserType(User.UserType domainType) {
        return UserJpaEntity.UserType.valueOf(domainType.name());
    }

    private User.UserType mapUserType(UserJpaEntity.UserType entityType) {
        return User.UserType.valueOf(entityType.name());
    }

    private UserJpaEntity.IdentityProvider mapIdentityProvider(User.IdentityProvider domainProvider) {
        return UserJpaEntity.IdentityProvider.valueOf(domainProvider.name());
    }

    private User.IdentityProvider mapIdentityProvider(UserJpaEntity.IdentityProvider entityProvider) {
        return User.IdentityProvider.valueOf(entityProvider.name());
    }

    private UserJpaEntity.Status mapStatus(User.Status domainStatus) {
        return UserJpaEntity.Status.valueOf(domainStatus.name());
    }

    private User.Status mapStatus(UserJpaEntity.Status entityStatus) {
        return User.Status.valueOf(entityStatus.name());
    }
}
