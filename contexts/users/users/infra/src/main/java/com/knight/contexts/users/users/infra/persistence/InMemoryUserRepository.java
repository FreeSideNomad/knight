package com.knight.contexts.users.users.infra.persistence;

import com.knight.contexts.users.users.app.service.UserApplicationService;
import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.platform.sharedkernel.UserId;
import io.micronaut.context.annotation.Secondary;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for User (fallback implementation).
 * Marked as @Secondary so JPA implementation takes precedence.
 */
@Singleton
@Secondary
public class InMemoryUserRepository implements UserApplicationService.UserRepository {

    private final Map<String, User> store = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        store.put(user.getUserId().id(), user);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return Optional.ofNullable(store.get(userId.id()));
    }
}
