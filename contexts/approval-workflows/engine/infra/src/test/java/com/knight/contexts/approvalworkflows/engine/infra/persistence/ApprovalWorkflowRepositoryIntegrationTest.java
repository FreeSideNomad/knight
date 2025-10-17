package com.knight.contexts.approvalworkflows.engine.infra.persistence;

import com.knight.contexts.approvalworkflows.engine.app.service.ApprovalWorkflowApplicationService;
import com.knight.contexts.approvalworkflows.engine.domain.aggregate.ApprovalWorkflow;
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
 * Integration test for ApprovalWorkflow repository with Testcontainers.
 */
@MicronautTest(
    transactional = false,
    packages = "com.knight.contexts.approvalworkflows.engine"
)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApprovalWorkflowRepositoryIntegrationTest implements TestPropertyProvider {

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
    ApprovalWorkflowApplicationService.ApprovalWorkflowRepository repository;

    @Test
    void shouldSaveAndRetrieveWorkflow() {
        // Given
        ApprovalWorkflow workflow = ApprovalWorkflow.initiate(
            "Invoice",
            "INV-12345",
            2, // Dual approval
            "user:alice"
        );

        // When
        repository.save(workflow);
        var retrieved = repository.findById(workflow.getWorkflowId());

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getResourceType()).isEqualTo("Invoice");
        assertThat(retrieved.get().getResourceId()).isEqualTo("INV-12345");
        assertThat(retrieved.get().getRequiredApprovals()).isEqualTo(2);
        assertThat(retrieved.get().getStatus()).isEqualTo(ApprovalWorkflow.Status.PENDING);
        assertThat(retrieved.get().getInitiatedBy()).isEqualTo("user:alice");
        assertThat(retrieved.get().getReceivedApprovals()).isEmpty();
    }

    @Test
    void shouldSaveWorkflowWithApprovals() {
        // Given
        ApprovalWorkflow workflow = ApprovalWorkflow.initiate(
            "Payment",
            "PAY-67890",
            2,
            "user:bob"
        );

        // When - Record first approval
        workflow.recordApproval("user:charlie", ApprovalWorkflow.Decision.APPROVE, "Looks good");
        repository.save(workflow);

        // Then
        var retrieved = repository.findById(workflow.getWorkflowId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(ApprovalWorkflow.Status.PENDING);
        assertThat(retrieved.get().getReceivedApprovals()).hasSize(1);
        assertThat(retrieved.get().getReceivedApprovals().get(0).getApproverUserId()).isEqualTo("user:charlie");
        assertThat(retrieved.get().getReceivedApprovals().get(0).getDecision()).isEqualTo(ApprovalWorkflow.Decision.APPROVE);
    }

    @Test
    void shouldCompleteWorkflowWhenFullyApproved() {
        // Given
        ApprovalWorkflow workflow = ApprovalWorkflow.initiate(
            "Invoice",
            "INV-99999",
            2,
            "user:alice"
        );

        // When - Record two approvals
        workflow.recordApproval("user:bob", ApprovalWorkflow.Decision.APPROVE, "Approved");
        workflow.recordApproval("user:charlie", ApprovalWorkflow.Decision.APPROVE, "Also approved");
        repository.save(workflow);

        // Then
        var retrieved = repository.findById(workflow.getWorkflowId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(ApprovalWorkflow.Status.APPROVED);
        assertThat(retrieved.get().getReceivedApprovals()).hasSize(2);
        assertThat(retrieved.get().getCompletedAt()).isNotNull();
    }

    @Test
    void shouldRejectWorkflowOnFirstRejection() {
        // Given
        ApprovalWorkflow workflow = ApprovalWorkflow.initiate(
            "Invoice",
            "INV-88888",
            2,
            "user:alice"
        );

        // When - Record one rejection
        workflow.recordApproval("user:bob", ApprovalWorkflow.Decision.REJECT, "Invalid invoice");
        repository.save(workflow);

        // Then
        var retrieved = repository.findById(workflow.getWorkflowId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(ApprovalWorkflow.Status.REJECTED);
        assertThat(retrieved.get().getReceivedApprovals()).hasSize(1);
        assertThat(retrieved.get().getCompletedAt()).isNotNull();
    }

    @Test
    void shouldExpireWorkflow() {
        // Given
        ApprovalWorkflow workflow = ApprovalWorkflow.initiate(
            "Invoice",
            "INV-77777",
            1,
            "user:alice"
        );
        repository.save(workflow);

        // When - Expire the workflow
        var retrieved = repository.findById(workflow.getWorkflowId()).orElseThrow();
        retrieved.expire();
        repository.save(retrieved);

        // Then
        var expired = repository.findById(workflow.getWorkflowId());
        assertThat(expired).isPresent();
        assertThat(expired.get().getStatus()).isEqualTo(ApprovalWorkflow.Status.EXPIRED);
        assertThat(expired.get().getCompletedAt()).isNotNull();
    }
}
