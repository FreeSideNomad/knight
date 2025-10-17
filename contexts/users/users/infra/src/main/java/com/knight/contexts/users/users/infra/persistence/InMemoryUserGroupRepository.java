package com.knight.contexts.users.users.infra.persistence;

import com.knight.contexts.users.users.app.repository.UserGroupRepository;
import com.knight.contexts.users.users.domain.aggregate.UserGroup;
import com.knight.platform.sharedkernel.UserGroupId;
import com.knight.platform.sharedkernel.UserId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserGroupRepository.
 * For MVP/testing. Replace with JPA implementation for production.
 */
@Repository
public class InMemoryUserGroupRepository implements UserGroupRepository {

    private final Map<String, UserGroup> store = new ConcurrentHashMap<>();

    @Override
    public void save(UserGroup group) {
        store.put(group.groupId().urn(), group);
    }

    @Override
    public Optional<UserGroup> findById(UserGroupId groupId) {
        return Optional.ofNullable(store.get(groupId.urn()));
    }

    @Override
    public List<UserGroup> findByProfileId(ServicingProfileId profileId) {
        return store.values().stream()
            .filter(g -> g.profileId().equals(profileId))
            .collect(Collectors.toList());
    }

    @Override
    public List<UserGroup> findByUserId(UserId userId) {
        return store.values().stream()
            .filter(g -> g.members().stream()
                .anyMatch(m -> m.userId().equals(userId)))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UserGroupId groupId) {
        store.remove(groupId.urn());
    }
}
