package com.knight.contexts.serviceprofiles.indirectclients.api.queries;

import com.knight.platform.sharedkernel.IndirectClientId;

public interface IndirectClientQueries {

    record IndirectClientSummary(
        String indirectClientUrn,
        String businessName,
        String status,
        int relatedPersonsCount
    ) {}

    IndirectClientSummary getIndirectClientSummary(IndirectClientId id);
}
