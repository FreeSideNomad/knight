package com.knight.bff.web.controllers;

import com.knight.contexts.serviceprofiles.management.api.queries.SpmQueries;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

/**
 * BFF Controller for profile summary composition.
 * Depends only on BC API modules (no domain or app).
 */
@Controller("/api/profiles")
@ExecuteOn(TaskExecutors.BLOCKING)
public class ProfileController {

    @Inject
    SpmQueries spmQueries;

    @Get("/servicing/{clientUrn}/summary")
    public ProfileSummaryDto getServicingSummary(@PathVariable String clientUrn) {
        var clientId = ClientId.of(clientUrn);
        var profileId = ServicingProfileId.of(clientId);
        var summary = spmQueries.getServicingProfileSummary(profileId);

        return new ProfileSummaryDto(
            summary.profileUrn(),
            summary.status(),
            summary.enrolledServices(),
            summary.enrolledAccounts()
        );
    }

    public record ProfileSummaryDto(
        String profileUrn,
        String status,
        int enrolledServices,
        int enrolledAccounts
    ) {}
}
