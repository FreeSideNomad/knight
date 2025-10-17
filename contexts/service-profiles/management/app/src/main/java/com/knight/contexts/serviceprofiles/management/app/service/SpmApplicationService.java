package com.knight.contexts.serviceprofiles.management.app.service;

import com.knight.contexts.serviceprofiles.management.api.commands.SpmCommands;
import com.knight.contexts.serviceprofiles.management.api.events.ServicingProfileCreated;
import com.knight.contexts.serviceprofiles.management.api.queries.SpmQueries;
import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Application service for Service Profile Management.
 * Updated to use Outbox pattern for event publishing.
 */
@Singleton
public class SpmApplicationService implements SpmCommands, SpmQueries {

    private final SpmRepository repository;
    private final ServicingProfileEventProducer eventProducer;

    public SpmApplicationService(
        SpmRepository repository,
        ServicingProfileEventProducer eventProducer
    ) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    // Inner interface for event producer (implemented in infra layer)
    public interface ServicingProfileEventProducer {
        void publishServicingProfileCreated(ServicingProfileCreated event);
    }

    @Override
    @Transactional
    public ServicingProfileId createServicingProfile(ClientId clientId, String createdBy) {
        ServicingProfileId profileId = ServicingProfileId.of(clientId);

        // 1. Create aggregate
        ServicingProfile profile = ServicingProfile.create(profileId, clientId, createdBy);

        // 2. Save aggregate
        repository.save(profile);

        // 3. Save event to outbox (SAME TRANSACTION)
        ServicingProfileCreated event = new ServicingProfileCreated(
            profileId.urn(),
            clientId.urn(),
            profile.getStatus().name(),
            createdBy,
            Instant.now()
        );
        eventProducer.publishServicingProfileCreated(event);

        return profileId;
    }

    @Override
    @Transactional
    public void enrollService(EnrollServiceCmd cmd) {
        ServicingProfile profile = repository.findById(cmd.profileId())
            .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + cmd.profileId().urn()));

        profile.enrollService(cmd.serviceType(), cmd.configurationJson());

        repository.save(profile);

        // Publish event to outbox
        // eventProducer.publishServiceEnrolled(...);
    }

    @Override
    @Transactional
    public void enrollAccount(EnrollAccountCmd cmd) {
        ServicingProfile profile = repository.findById(cmd.profileId())
            .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + cmd.profileId().urn()));

        profile.enrollAccount(cmd.serviceEnrollmentId(), cmd.accountId());

        repository.save(profile);

        // Publish event to outbox
        // eventProducer.publishAccountEnrolled(...);
    }

    @Override
    @Transactional
    public void suspendProfile(SuspendProfileCmd cmd) {
        ServicingProfile profile = repository.findById(cmd.profileId())
            .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + cmd.profileId().urn()));

        profile.suspend(cmd.reason());

        repository.save(profile);

        // Publish event to outbox
        // eventProducer.publishProfileSuspended(...);
    }

    @Override
    public ServicingProfileSummary getServicingProfileSummary(ServicingProfileId profileId) {
        ServicingProfile profile = repository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + profileId.urn()));

        return new ServicingProfileSummary(
            profile.getProfileId().urn(),
            profile.getStatus().name(),
            profile.getServiceEnrollments().size(),
            profile.getAccountEnrollments().size()
        );
    }

    // Repository interface (to be implemented in infra layer)
    public interface SpmRepository {
        void save(ServicingProfile profile);
        java.util.Optional<ServicingProfile> findById(ServicingProfileId profileId);
    }
}
