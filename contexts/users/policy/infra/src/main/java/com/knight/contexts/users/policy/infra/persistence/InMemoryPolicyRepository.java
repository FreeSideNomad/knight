package com.knight.contexts.users.policy.infra.persistence;

import com.knight.contexts.users.policy.app.service.PolicyApplicationService;
import com.knight.contexts.users.policy.domain.aggregate.Policy;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for Policy (MVP - replace with JPA in production).
 */
@Singleton
public class InMemoryPolicyRepository implements PolicyApplicationService.PolicyRepository {

    private final Map<String, Policy> store = new ConcurrentHashMap<>();

    @Override
    public void save(Policy policy) {
        store.put(policy.getPolicyId(), policy);
    }

    @Override
    public Optional<Policy> findById(String policyId) {
        return Optional.ofNullable(store.get(policyId));
    }

    @Override
    public void deleteById(String policyId) {
        store.remove(policyId);
    }
}
