package com.knight.contexts.serviceprofiles.indirectclients.api.commands;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;

public interface IndirectClientCommands {

    IndirectClientId createIndirectClient(CreateIndirectClientCmd cmd);

    record CreateIndirectClientCmd(
        ClientId parentClientId,
        String businessName,
        String taxId
    ) {}

    void addRelatedPerson(AddRelatedPersonCmd cmd);

    record AddRelatedPersonCmd(
        IndirectClientId indirectClientId,
        String name,
        String role,
        String email
    ) {}

    void updateBusinessInfo(UpdateBusinessInfoCmd cmd);

    record UpdateBusinessInfoCmd(
        IndirectClientId indirectClientId,
        String businessName,
        String taxId
    ) {}
}
