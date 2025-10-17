package com.knight.contexts.serviceprofiles.management.api.queries;

import com.knight.platform.sharedkernel.ServicingProfileId;

/**
 * Query interface for Service Profile Management bounded context.
 * Provides read models for servicing profiles.
 */
public interface SpmQueries {

    /**
     * Get servicing profile summary with enrollment counts
     */
    ServicingProfileSummary getServicingProfileSummary(ServicingProfileId profileId);

    record ServicingProfileSummary(
        String profileUrn,
        String clientUrn,
        String status,
        int enrolledServices,
        int enrolledAccounts
    ) {}
}
