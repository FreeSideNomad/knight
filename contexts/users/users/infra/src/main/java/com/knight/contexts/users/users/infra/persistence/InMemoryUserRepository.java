package com.knight.contexts.users.users.infra.persistence;

import com.knight.contexts.users.users.app.repository.UserRepository;
import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserRepository.
 * For MVP/testing. Replace with JPA implementation for production.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> store = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        store.put(user.userId().urn(), user);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return Optional.ofNullable(store.get(userId.urn()));
    }

    @Override
    public List<User> findByProfileId(ServicingProfileId profileId) {
        return store.values().stream()
            .filter(u -> u.profileId().equals(profileId))
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findAdministratorsByProfileId(ServicingProfileId profileId) {
        return store.values().stream()
            .filter(u -> u.profileId().equals(profileId))
            .filter(u -> u.role() == User.Role.ADMINISTRATOR)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmailAndProfileId(String email, ServicingProfileId profileId) {
        return store.values().stream()
            .filter(u -> u.profileId().equals(profileId))
            .filter(u -> u.email().equals(email))
            .findFirst();
    }

    @Override
    public void delete(UserId userId) {
        store.remove(userId.urn());
    }
}
