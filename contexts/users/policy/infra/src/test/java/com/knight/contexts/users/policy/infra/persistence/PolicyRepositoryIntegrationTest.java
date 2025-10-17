package com.knight.contexts.users.policy.infra.persistence;

import com.knight.contexts.users.policy.app.service.PolicyApplicationService;
import com.knight.contexts.users.policy.domain.aggregate.Policy;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for Policy repository with Testcontainers.
 */
@MicronautTest(
    transactional = false,
    packages = "com.knight.contexts.users.policy"
)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyRepositoryIntegrationTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword()
        );
    }

    @Inject
    PolicyApplicationService.PolicyRepository repository;

    @Test
    void shouldSaveAndRetrievePermissionPolicy() {
        // Given
        Policy policy = Policy.create(
            Policy.PolicyType.PERMISSION,
            "user:12345",
            "service:receivables:read",
            "account:CAN123456",
            null
        );

        // When
        repository.save(policy);
        var retrieved = repository.findById(policy.getPolicyId());

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getPolicyType()).isEqualTo(Policy.PolicyType.PERMISSION);
        assertThat(retrieved.get().getSubject()).isEqualTo("user:12345");
        assertThat(retrieved.get().getAction()).isEqualTo("service:receivables:read");
        assertThat(retrieved.get().getResource()).isEqualTo("account:CAN123456");
        assertThat(retrieved.get().getApproverCount()).isNull();
    }

    @Test
    void shouldSaveAndRetrieveApprovalPolicy() {
        // Given
        Policy policy = Policy.create(
            Policy.PolicyType.APPROVAL,
            "client:CORP001",
            "service:receivables:approve",
            "invoice:*",
            2 // Dual approval required
        );

        // When
        repository.save(policy);
        var retrieved = repository.findById(policy.getPolicyId());

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getPolicyType()).isEqualTo(Policy.PolicyType.APPROVAL);
        assertThat(retrieved.get().getSubject()).isEqualTo("client:CORP001");
        assertThat(retrieved.get().getAction()).isEqualTo("service:receivables:approve");
        assertThat(retrieved.get().getResource()).isEqualTo("invoice:*");
        assertThat(retrieved.get().getApproverCount()).isEqualTo(2);
    }

    @Test
    void shouldUpdateExistingPolicy() {
        // Given
        Policy policy = Policy.create(
            Policy.PolicyType.PERMISSION,
            "user:67890",
            "service:receivables:write",
            "account:CAN789012",
            null
        );
        repository.save(policy);

        // When
        policy.update("service:receivables:admin", "account:*", null);
        repository.save(policy);

        // Then
        var retrieved = repository.findById(policy.getPolicyId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getAction()).isEqualTo("service:receivables:admin");
        assertThat(retrieved.get().getResource()).isEqualTo("account:*");
    }

    @Test
    void shouldDeletePolicy() {
        // Given
        Policy policy = Policy.create(
            Policy.PolicyType.PERMISSION,
            "user:99999",
            "service:test:delete",
            "resource:test",
            null
        );
        repository.save(policy);

        // When
        repository.deleteById(policy.getPolicyId());

        // Then
        var retrieved = repository.findById(policy.getPolicyId());
        assertThat(retrieved).isEmpty();
    }

    @Test
    void shouldHandleApprovalPolicyUpdates() {
        // Given
        Policy policy = Policy.create(
            Policy.PolicyType.APPROVAL,
            "client:CORP999",
            "service:receivables:approve",
            "invoice:large",
            1 // Single approver
        );
        repository.save(policy);

        // When - Increase to dual approval
        policy.update(null, null, 2);
        repository.save(policy);

        // Then
        var retrieved = repository.findById(policy.getPolicyId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getApproverCount()).isEqualTo(2);
    }
}
