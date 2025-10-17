package com.knight.contexts.users.users.app.service;

import com.knight.contexts.users.users.api.commands.UserCommands;
import com.knight.contexts.users.users.api.events.UserCreated;
import com.knight.contexts.users.users.api.queries.UserQueries;
import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.platform.sharedkernel.UserId;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Application service for User Management.
 * Orchestrates user lifecycle operations with transactions and event publishing.
 */
@Singleton
public class UserApplicationService implements UserCommands, UserQueries {

    private final UserRepository repository;
    private final ApplicationEventPublisher<Object> eventPublisher;

    public UserApplicationService(
        UserRepository repository,
        ApplicationEventPublisher<Object> eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public UserId createUser(CreateUserCmd cmd) {
        UserId userId = UserId.of(java.util.UUID.randomUUID().toString());

        // Parse enums
        User.UserType userType = User.UserType.valueOf(cmd.userType());
        User.IdentityProvider identityProvider = User.IdentityProvider.valueOf(cmd.identityProvider());

        // Create aggregate
        User user = User.create(userId, cmd.email(), userType, identityProvider, cmd.clientId());

        // Save
        repository.save(user);

        // Publish event
        eventPublisher.publishEvent(new UserCreated(
            userId.id(),
            cmd.email(),
            cmd.userType(),
            cmd.identityProvider(),
            Instant.now()
        ));

        return userId;
    }

    @Override
    @Transactional
    public void activateUser(ActivateUserCmd cmd) {
        User user = repository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId().id()));

        user.activate();

        repository.save(user);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // UserActivated event
    }

    @Override
    @Transactional
    public void deactivateUser(DeactivateUserCmd cmd) {
        User user = repository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId().id()));

        user.deactivate(cmd.reason());

        repository.save(user);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // UserDeactivated event
    }

    @Override
    @Transactional
    public void lockUser(LockUserCmd cmd) {
        User user = repository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId().id()));

        user.lock(cmd.reason());

        repository.save(user);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // UserLocked event
    }

    @Override
    @Transactional
    public void unlockUser(UnlockUserCmd cmd) {
        User user = repository.findById(cmd.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + cmd.userId().id()));

        user.unlock();

        repository.save(user);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // UserUnlocked event
    }

    @Override
    public UserSummary getUserSummary(UserId userId) {
        User user = repository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId.id()));

        return new UserSummary(
            user.getUserId().id(),
            user.getEmail(),
            user.getStatus().name(),
            user.getUserType().name(),
            user.getIdentityProvider().name()
        );
    }

    // Repository interface (to be implemented in infra layer)
    public interface UserRepository {
        void save(User user);
        java.util.Optional<User> findById(UserId userId);
    }
}
