package com.knight.contexts.serviceprofiles.management.api.commands;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;

import java.util.Map;

/**
 * Command interface for Service Profile Management bounded context.
 * Handles creation and modification of servicing profiles.
 */
public interface SpmCommands {

    /**
     * Create a new servicing profile for SRF or GID client
     */
    ServicingProfileId createServicingProfile(ClientId clientId, String createdBy);

    /**
     * Enroll a stand-alone service to servicing profile
     */
    void enrollService(EnrollServiceCmd cmd);

    /**
     * Enroll account to a service
     */
    void enrollAccount(EnrollAccountCmd cmd);

    /**
     * Suspend servicing profile
     */
    void suspendProfile(SuspendProfileCmd cmd);

    record EnrollServiceCmd(
        ServicingProfileId profileId,
        String serviceType,
        Map<String, Object> configuration
    ) {}

    record EnrollAccountCmd(
        ServicingProfileId profileId,
        String serviceEnrollmentId,
        String accountId
    ) {}

    record SuspendProfileCmd(
        ServicingProfileId profileId,
        String reason,
        String suspendedBy
    ) {}
}
