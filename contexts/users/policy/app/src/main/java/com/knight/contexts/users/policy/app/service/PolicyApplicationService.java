package com.knight.contexts.users.policy.app.service;

import com.knight.contexts.users.policy.api.commands.PolicyCommands;
import com.knight.contexts.users.policy.api.events.PolicyCreated;
import com.knight.contexts.users.policy.api.queries.PolicyQueries;
import com.knight.contexts.users.policy.domain.aggregate.Policy;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Application service for Policy Management.
 * Orchestrates policy operations with transactions and event publishing.
 */
@Singleton
public class PolicyApplicationService implements PolicyCommands, PolicyQueries {

    private final PolicyRepository repository;
    private final ApplicationEventPublisher<Object> eventPublisher;

    public PolicyApplicationService(
        PolicyRepository repository,
        ApplicationEventPublisher<Object> eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public String createPolicy(CreatePolicyCmd cmd) {
        // Parse policy type
        Policy.PolicyType policyType = Policy.PolicyType.valueOf(cmd.policyType());

        // Create aggregate
        Policy policy = Policy.create(
            policyType,
            cmd.subject(),
            cmd.action(),
            cmd.resource(),
            cmd.approverCount()
        );

        // Save
        repository.save(policy);

        // Publish event
        eventPublisher.publishEvent(new PolicyCreated(
            policy.getPolicyId(),
            cmd.policyType(),
            cmd.subject(),
            Instant.now()
        ));

        return policy.getPolicyId();
    }

    @Override
    @Transactional
    public void updatePolicy(UpdatePolicyCmd cmd) {
        Policy policy = repository.findById(cmd.policyId())
            .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + cmd.policyId()));

        policy.update(cmd.action(), cmd.resource(), cmd.approverCount());

        repository.save(policy);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // PolicyUpdated event
    }

    @Override
    @Transactional
    public void deletePolicy(DeletePolicyCmd cmd) {
        repository.deleteById(cmd.policyId());

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // PolicyDeleted event
    }

    @Override
    public PolicySummary getPolicySummary(String policyId) {
        Policy policy = repository.findById(policyId)
            .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + policyId));

        return new PolicySummary(
            policy.getPolicyId(),
            policy.getPolicyType().name(),
            policy.getSubject(),
            policy.getAction(),
            policy.getResource(),
            policy.getApproverCount()
        );
    }

    // Repository interface (to be implemented in infra layer)
    public interface PolicyRepository {
        void save(Policy policy);
        java.util.Optional<Policy> findById(String policyId);
        void deleteById(String policyId);
    }
}
