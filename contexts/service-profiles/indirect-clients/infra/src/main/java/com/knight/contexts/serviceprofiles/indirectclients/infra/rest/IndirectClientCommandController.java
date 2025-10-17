package com.knight.contexts.serviceprofiles.indirectclients.infra.rest;

import com.knight.contexts.serviceprofiles.indirectclients.api.commands.IndirectClientCommands;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

/**
 * REST controller for Indirect Client Management commands.
 */
@Controller("/commands/service-profiles/indirect-clients")
@ExecuteOn(TaskExecutors.BLOCKING)
public class IndirectClientCommandController {

    @Inject
    IndirectClientCommands commands;

    @Post("/create")
    public CreateIndirectClientResult createIndirectClient(@Body CreateIndirectClientRequest req) {
        var parentClientId = ClientId.of(req.parentClientUrn());
        var indirectClientId = commands.createIndirectClient(
            new IndirectClientCommands.CreateIndirectClientCmd(
                parentClientId,
                req.businessName(),
                req.taxId()
            )
        );
        return new CreateIndirectClientResult(indirectClientId.urn());
    }

    @Post("/add-related-person")
    public void addRelatedPerson(@Body AddRelatedPersonRequest req) {
        var indirectClientId = IndirectClientId.fromUrn(req.indirectClientUrn());
        commands.addRelatedPerson(new IndirectClientCommands.AddRelatedPersonCmd(
            indirectClientId,
            req.name(),
            req.role(),
            req.email()
        ));
    }

    @Post("/update-business-info")
    public void updateBusinessInfo(@Body UpdateBusinessInfoRequest req) {
        var indirectClientId = IndirectClientId.fromUrn(req.indirectClientUrn());
        commands.updateBusinessInfo(new IndirectClientCommands.UpdateBusinessInfoCmd(
            indirectClientId,
            req.businessName(),
            req.taxId()
        ));
    }

    public record CreateIndirectClientRequest(String parentClientUrn, String businessName, String taxId) {}
    public record CreateIndirectClientResult(String indirectClientUrn) {}
    public record AddRelatedPersonRequest(String indirectClientUrn, String name, String role, String email) {}
    public record UpdateBusinessInfoRequest(String indirectClientUrn, String businessName, String taxId) {}
}
