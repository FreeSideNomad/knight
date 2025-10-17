package com.knight.contexts.users.policy.infra.persistence;

import com.knight.contexts.users.policy.app.repository.PermissionStatementRepository;
import com.knight.contexts.users.policy.domain.aggregate.PermissionStatement;
import com.knight.platform.sharedkernel.ProfileId;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of PermissionStatementRepository for development/testing.
 */
@Repository
public class InMemoryPermissionStatementRepository implements PermissionStatementRepository {

    private final Map<String, PermissionStatement> store = new ConcurrentHashMap<>();

    @Override
    public void save(PermissionStatement statement) {
        store.put(statement.statementId(), statement);
    }

    @Override
    public Optional<PermissionStatement> findById(String statementId) {
        return Optional.ofNullable(store.get(statementId));
    }

    @Override
    public List<PermissionStatement> findByProfileId(ProfileId profileId) {
        return store.values().stream()
            .filter(s -> s.profileId().urn().equals(profileId.urn()))
            .toList();
    }

    @Override
    public void delete(String statementId) {
        store.remove(statementId);
    }
}
