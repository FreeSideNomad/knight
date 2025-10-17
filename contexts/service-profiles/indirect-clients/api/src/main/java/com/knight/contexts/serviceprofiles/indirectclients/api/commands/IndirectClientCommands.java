package com.knight.contexts.serviceprofiles.indirectclients.api.commands;

import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;

public interface IndirectClientCommands {

    IndirectClientId createIndirectClient(CreateIndirectClientCmd cmd);

    void addRelatedPerson(AddRelatedPersonCmd cmd);

    void updateBusinessInfo(UpdateBusinessInfoCmd cmd);

    record CreateIndirectClientCmd(
        ClientId parentClientId,
        String businessName,
        String taxId,
        String createdBy
    ) {}

    record AddRelatedPersonCmd(
        IndirectClientId indirectClientId,
        String personName,
        String role,  // SIGNING_OFFICER, ADMINISTRATOR, DIRECTOR
        String email,
        String phone
    ) {}

    record UpdateBusinessInfoCmd(
        IndirectClientId indirectClientId,
        String businessName,
        String taxId
    ) {}
}
