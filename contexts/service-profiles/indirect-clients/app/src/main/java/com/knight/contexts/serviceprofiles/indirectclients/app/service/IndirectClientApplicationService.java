package com.knight.contexts.serviceprofiles.indirectclients.app.service;

import com.knight.contexts.serviceprofiles.indirectclients.api.commands.IndirectClientCommands;
import com.knight.contexts.serviceprofiles.indirectclients.api.events.IndirectClientOnboarded;
import com.knight.contexts.serviceprofiles.indirectclients.api.queries.IndirectClientQueries;
import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Application service for Indirect Client Management.
 * Orchestrates indirect client operations with transactions and event publishing.
 */
@Singleton
public class IndirectClientApplicationService implements IndirectClientCommands, IndirectClientQueries {

    private final IndirectClientRepository repository;
    private final ApplicationEventPublisher<Object> eventPublisher;

    public IndirectClientApplicationService(
        IndirectClientRepository repository,
        ApplicationEventPublisher<Object> eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public IndirectClientId createIndirectClient(CreateIndirectClientCmd cmd) {
        // Generate next sequence for this parent client
        int nextSequence = repository.getNextSequenceForClient(cmd.parentClientId());
        IndirectClientId indirectClientId = IndirectClientId.of(cmd.parentClientId(), nextSequence);

        // Create aggregate
        IndirectClient client = IndirectClient.create(
            indirectClientId,
            cmd.parentClientId(),
            cmd.businessName(),
            cmd.taxId()
        );

        // Save
        repository.save(client);

        // Publish event
        eventPublisher.publishEvent(new IndirectClientOnboarded(
            indirectClientId.urn(),
            cmd.parentClientId().urn(),
            cmd.businessName(),
            Instant.now()
        ));

        return indirectClientId;
    }

    @Override
    @Transactional
    public void addRelatedPerson(AddRelatedPersonCmd cmd) {
        IndirectClient client = repository.findById(cmd.indirectClientId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Indirect client not found: " + cmd.indirectClientId().urn()));

        client.addRelatedPerson(cmd.name(), cmd.role(), cmd.email());

        repository.save(client);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // RelatedPersonAdded event
    }

    @Override
    @Transactional
    public void updateBusinessInfo(UpdateBusinessInfoCmd cmd) {
        IndirectClient client = repository.findById(cmd.indirectClientId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Indirect client not found: " + cmd.indirectClientId().urn()));

        client.updateBusinessInfo(cmd.businessName(), cmd.taxId());

        repository.save(client);

        // Publish event (simplified)
        eventPublisher.publishEvent(new Object()); // BusinessInfoUpdated event
    }

    @Override
    public IndirectClientSummary getIndirectClientSummary(IndirectClientId id) {
        IndirectClient client = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Indirect client not found: " + id.urn()));

        return new IndirectClientSummary(
            client.getIndirectClientId().urn(),
            client.getBusinessName(),
            client.getStatus().name(),
            client.getRelatedPersons().size()
        );
    }

    // Repository interface (to be implemented in infra layer)
    public interface IndirectClientRepository {
        void save(IndirectClient client);
        java.util.Optional<IndirectClient> findById(IndirectClientId id);
        int getNextSequenceForClient(ClientId parentClientId);
    }
}
