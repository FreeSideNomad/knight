package com.knight.contexts.serviceprofiles.indirectclients.app.service;

import com.knight.contexts.serviceprofiles.indirectclients.api.commands.IndirectClientCommands;
import com.knight.contexts.serviceprofiles.indirectclients.api.events.IndirectClientOnboarded;
import com.knight.contexts.serviceprofiles.indirectclients.api.queries.IndirectClientQueries;
import com.knight.contexts.serviceprofiles.indirectclients.app.repository.IndirectClientRepository;
import com.knight.contexts.serviceprofiles.indirectclients.domain.aggregate.IndirectClient;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Application service implementing commands and queries for Indirect Client Management.
 * Orchestrates domain operations, repository access, and event publishing.
 */
@Service
public class IndirectClientApplicationService implements IndirectClientCommands, IndirectClientQueries {

    private final IndirectClientRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicInteger sequenceGenerator = new AtomicInteger(1);

    public IndirectClientApplicationService(IndirectClientRepository repository,
                                           ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public IndirectClientId createIndirectClient(CreateIndirectClientCmd cmd) {
        // Validate parent client exists (would call external data serving layer in full implementation)
        // For now, we'll create the indirect client directly

        // Generate unique sequence for this parent client
        int sequence = generateSequenceForParent(cmd.parentClientId());
        IndirectClientId indirectClientId = IndirectClientId.of(cmd.parentClientId(), sequence);

        // Create aggregate
        IndirectClient indirectClient = IndirectClient.create(
            indirectClientId,
            cmd.parentClientId(),
            cmd.businessName(),
            cmd.taxId(),
            cmd.createdBy()
        );

        // Save
        repository.save(indirectClient);

        // Publish event (after commit in transaction)
        IndirectClientOnboarded event = new IndirectClientOnboarded(
            indirectClientId,
            cmd.parentClientId(),
            cmd.businessName(),
            cmd.createdBy(),
            indirectClient.createdAt()
        );
        eventPublisher.publishEvent(event);

        return indirectClientId;
    }

    @Override
    @Transactional
    public void addRelatedPerson(AddRelatedPersonCmd cmd) {
        IndirectClient indirectClient = repository.findById(cmd.indirectClientId())
            .orElseThrow(() -> new IllegalArgumentException("IndirectClient not found: " + cmd.indirectClientId()));

        // Parse role enum
        IndirectClient.RelatedPerson.Role role = IndirectClient.RelatedPerson.Role.valueOf(cmd.role());

        indirectClient.addRelatedPerson(
            cmd.personName(),
            role,
            cmd.email(),
            cmd.phone()
        );

        repository.save(indirectClient);

        // Publish RelatedPersonAdded event (omitted for brevity)
    }

    @Override
    @Transactional
    public void updateBusinessInfo(UpdateBusinessInfoCmd cmd) {
        IndirectClient indirectClient = repository.findById(cmd.indirectClientId())
            .orElseThrow(() -> new IllegalArgumentException("IndirectClient not found: " + cmd.indirectClientId()));

        indirectClient.updateBusinessInfo(cmd.businessName(), cmd.taxId());

        repository.save(indirectClient);

        // Publish BusinessInfoUpdated event (omitted for brevity)
    }

    @Override
    @Transactional(readOnly = true)
    public IndirectClientSummary getIndirectClient(IndirectClientId id) {
        IndirectClient indirectClient = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("IndirectClient not found: " + id));

        return new IndirectClientSummary(
            indirectClient.indirectClientId().urn(),
            indirectClient.parentClientId().urn(),
            indirectClient.businessName(),
            indirectClient.taxId(),
            indirectClient.status().name(),
            indirectClient.relatedPersons().size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndirectClientSummary> listByParentClient(ClientId parentClientId) {
        List<IndirectClient> indirectClients = repository.findByParentClientId(parentClientId);

        return indirectClients.stream()
            .map(ic -> new IndirectClientSummary(
                ic.indirectClientId().urn(),
                ic.parentClientId().urn(),
                ic.businessName(),
                ic.taxId(),
                ic.status().name(),
                ic.relatedPersons().size()
            ))
            .toList();
    }

    /**
     * Generate unique sequence number for indirect client within parent client.
     * In production, this should be backed by database sequence or distributed ID generator.
     */
    private int generateSequenceForParent(ClientId parentClientId) {
        // Simplified: just increment global counter
        // In production: query max sequence for parent + increment
        return sequenceGenerator.getAndIncrement();
    }
}
