package com.knight.contexts.users.policy.infra.persistence;

import com.knight.contexts.users.policy.app.repository.ApprovalStatementRepository;
import com.knight.contexts.users.policy.domain.aggregate.ApprovalStatement;
import com.knight.platform.sharedkernel.ProfileId;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ApprovalStatementRepository for development/testing.
 */
@Repository
public class InMemoryApprovalStatementRepository implements ApprovalStatementRepository {

    private final Map<String, ApprovalStatement> store = new ConcurrentHashMap<>();

    @Override
    public void save(ApprovalStatement statement) {
        store.put(statement.statementId(), statement);
    }

    @Override
    public Optional<ApprovalStatement> findById(String statementId) {
        return Optional.ofNullable(store.get(statementId));
    }

    @Override
    public List<ApprovalStatement> findByProfileId(ProfileId profileId) {
        return store.values().stream()
            .filter(s -> s.profileId().urn().equals(profileId.urn()))
            .toList();
    }

    @Override
    public void delete(String statementId) {
        store.remove(statementId);
    }
}
