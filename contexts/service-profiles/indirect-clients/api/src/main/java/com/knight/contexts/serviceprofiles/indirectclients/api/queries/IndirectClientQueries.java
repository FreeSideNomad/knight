package com.knight.contexts.serviceprofiles.indirectclients.api.queries;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;

import java.util.List;

public interface IndirectClientQueries {

    IndirectClientSummary getIndirectClient(IndirectClientId id);

    List<IndirectClientSummary> listByParentClient(ClientId parentClientId);

    record IndirectClientSummary(
        String indirectClientUrn,
        String parentClientUrn,
        String businessName,
        String taxId,
        String status,
        int relatedPersonsCount
    ) {}
}
