package com.knight.contexts.serviceprofiles.management.infra.rest;

import com.knight.contexts.serviceprofiles.management.api.commands.SpmCommands;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller exposing command endpoints for Service Profile Management.
 */
@RestController
@RequestMapping("/commands/service-profiles/servicing")
public class SpmCommandController {

    private final SpmCommands commands;

    public SpmCommandController(SpmCommands commands) {
        this.commands = commands;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateProfileResult> createServicingProfile(@RequestBody CreateProfileRequest request) {
        ClientId clientId = ClientId.of(request.clientUrn());
        ServicingProfileId profileId = commands.createServicingProfile(clientId, request.createdBy());

        return ResponseEntity.ok(new CreateProfileResult(profileId.urn()));
    }

    @PostMapping("/enroll-service")
    public ResponseEntity<Void> enrollService(@RequestBody EnrollServiceRequest request) {
        ServicingProfileId profileId = ServicingProfileId.fromUrn(request.profileUrn());

        SpmCommands.EnrollServiceCmd cmd = new SpmCommands.EnrollServiceCmd(
            profileId,
            request.serviceType(),
            request.configuration()
        );

        commands.enrollService(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/enroll-account")
    public ResponseEntity<Void> enrollAccount(@RequestBody EnrollAccountRequest request) {
        ServicingProfileId profileId = ServicingProfileId.fromUrn(request.profileUrn());

        SpmCommands.EnrollAccountCmd cmd = new SpmCommands.EnrollAccountCmd(
            profileId,
            request.serviceEnrollmentId(),
            request.accountId()
        );

        commands.enrollAccount(cmd);

        return ResponseEntity.ok().build();
    }

    record CreateProfileRequest(String clientUrn, String createdBy) {}
    record CreateProfileResult(String profileUrn) {}

    record EnrollServiceRequest(
        String profileUrn,
        String serviceType,
        Map<String, Object> configuration
    ) {}

    record EnrollAccountRequest(
        String profileUrn,
        String serviceEnrollmentId,
        String accountId
    ) {}
}
