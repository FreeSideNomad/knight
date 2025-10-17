package com.knight.contexts.serviceprofiles.management.app.service;

import com.knight.contexts.serviceprofiles.management.api.commands.SpmCommands;
import com.knight.contexts.serviceprofiles.management.api.events.ServicingProfileCreated;
import com.knight.contexts.serviceprofiles.management.api.queries.SpmQueries;
import com.knight.contexts.serviceprofiles.management.app.repository.ServicingProfileRepository;
import com.knight.contexts.serviceprofiles.management.domain.aggregate.ServicingProfile;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementing commands and queries for Service Profile Management.
 * Orchestrates domain operations, repository access, and event publishing.
 */
@Service
public class SpmApplicationService implements SpmCommands, SpmQueries {

    private final ServicingProfileRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public SpmApplicationService(ServicingProfileRepository repository,
                                ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ServicingProfileId createServicingProfile(ClientId clientId, String createdBy) {
        // Validate client exists (would call external data serving layer in full implementation)
        // For now, we'll create the profile directly

        ServicingProfileId profileId = ServicingProfileId.of(clientId);

        // Check if profile already exists
        repository.findByClientId(clientId).ifPresent(existing -> {
            throw new IllegalArgumentException("ServicingProfile already exists for client: " + clientId);
        });

        // Create aggregate
        ServicingProfile profile = ServicingProfile.create(profileId, clientId, createdBy);

        // Save
        repository.save(profile);

        // Publish event (after commit in transaction)
        ServicingProfileCreated event = new ServicingProfileCreated(
            profileId,
            clientId,
            createdBy,
            profile.createdAt()
        );
        eventPublisher.publishEvent(event);

        return profileId;
    }

    @Override
    @Transactional
    public void enrollService(EnrollServiceCmd cmd) {
        ServicingProfile profile = repository.findById(cmd.profileId())
            .orElseThrow(() -> new IllegalArgumentException("ServicingProfile not found: " + cmd.profileId()));

        profile.enrollService(cmd.serviceType(), cmd.configuration());

        repository.save(profile);

        // Publish ServiceEnrolled event (omitted for brevity)
    }

    @Override
    @Transactional
    public void enrollAccount(EnrollAccountCmd cmd) {
        ServicingProfile profile = repository.findById(cmd.profileId())
            .orElseThrow(() -> new IllegalArgumentException("ServicingProfile not found: " + cmd.profileId()));

        profile.enrollAccount(cmd.serviceEnrollmentId(), cmd.accountId());

        repository.save(profile);

        // Publish AccountEnrolled event (omitted for brevity)
    }

    @Override
    @Transactional
    public void suspendProfile(SuspendProfileCmd cmd) {
        ServicingProfile profile = repository.findById(cmd.profileId())
            .orElseThrow(() -> new IllegalArgumentException("ServicingProfile not found: " + cmd.profileId()));

        profile.suspend(cmd.reason());

        repository.save(profile);

        // Publish ProfileSuspended event (omitted for brevity)
    }

    @Override
    @Transactional(readOnly = true)
    public ServicingProfileSummary getServicingProfileSummary(ServicingProfileId profileId) {
        ServicingProfile profile = repository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("ServicingProfile not found: " + profileId));

        int enrolledServices = profile.serviceEnrollments().size();
        int enrolledAccounts = profile.serviceEnrollments().stream()
            .mapToInt(se -> se.accountEnrollments().size())
            .sum();

        return new ServicingProfileSummary(
            profile.profileId().urn(),
            profile.clientId().urn(),
            profile.status().name(),
            enrolledServices,
            enrolledAccounts
        );
    }
}
