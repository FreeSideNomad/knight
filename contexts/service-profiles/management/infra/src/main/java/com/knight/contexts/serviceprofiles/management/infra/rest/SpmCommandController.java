package com.knight.contexts.serviceprofiles.management.infra.rest;

import com.knight.contexts.serviceprofiles.management.api.commands.SpmCommands;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

/**
 * REST controller for Service Profile Management commands.
 */
@Controller("/commands/service-profiles/servicing")
@ExecuteOn(TaskExecutors.BLOCKING)
public class SpmCommandController {

    @Inject
    SpmCommands commands;

    @Post("/create")
    public CreateProfileResult createProfile(@Body CreateProfileRequest req) {
        var clientId = ClientId.of(req.clientUrn());
        var profileId = commands.createServicingProfile(clientId, req.createdBy());
        return new CreateProfileResult(profileId.urn());
    }

    @Post("/enroll-service")
    public void enrollService(@Body EnrollServiceRequest req) {
        var profileId = ServicingProfileId.fromUrn(req.profileUrn());
        commands.enrollService(new SpmCommands.EnrollServiceCmd(
            profileId,
            req.serviceType(),
            req.configurationJson()
        ));
    }

    @Post("/enroll-account")
    public void enrollAccount(@Body EnrollAccountRequest req) {
        var profileId = ServicingProfileId.fromUrn(req.profileUrn());
        commands.enrollAccount(new SpmCommands.EnrollAccountCmd(
            profileId,
            req.serviceEnrollmentId(),
            req.accountId()
        ));
    }

    @Serdeable
    public record CreateProfileRequest(String clientUrn, String createdBy) {}

    @Serdeable
    public record CreateProfileResult(String profileUrn) {}

    @Serdeable
    public record EnrollServiceRequest(String profileUrn, String serviceType, String configurationJson) {}

    @Serdeable
    public record EnrollAccountRequest(String profileUrn, String serviceEnrollmentId, String accountId) {}
}
