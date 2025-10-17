package com.knight.contexts.serviceprofiles.management.api.queries;

import com.knight.platform.sharedkernel.ServicingProfileId;

/**
 * Query interface for Service Profile Management.
 */
public interface SpmQueries {

    record ServicingProfileSummary(
        String profileUrn,
        String status,
        int enrolledServices,
        int enrolledAccounts
    ) {}

    ServicingProfileSummary getServicingProfileSummary(ServicingProfileId profileId);
}
