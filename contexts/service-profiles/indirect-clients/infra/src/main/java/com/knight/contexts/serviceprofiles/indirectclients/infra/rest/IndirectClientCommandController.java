package com.knight.contexts.serviceprofiles.indirectclients.infra.rest;

import com.knight.contexts.serviceprofiles.indirectclients.api.commands.IndirectClientCommands;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.IndirectClientId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing command endpoints for Indirect Client Management.
 */
@RestController
@RequestMapping("/commands/indirect-clients")
public class IndirectClientCommandController {

    private final IndirectClientCommands commands;

    public IndirectClientCommandController(IndirectClientCommands commands) {
        this.commands = commands;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateIndirectClientResult> createIndirectClient(@RequestBody CreateIndirectClientRequest request) {
        ClientId parentClientId = ClientId.of(request.parentClientUrn());

        IndirectClientCommands.CreateIndirectClientCmd cmd = new IndirectClientCommands.CreateIndirectClientCmd(
            parentClientId,
            request.businessName(),
            request.taxId(),
            request.createdBy()
        );

        IndirectClientId indirectClientId = commands.createIndirectClient(cmd);

        return ResponseEntity.ok(new CreateIndirectClientResult(indirectClientId.urn()));
    }

    @PostMapping("/add-related-person")
    public ResponseEntity<Void> addRelatedPerson(@RequestBody AddRelatedPersonRequest request) {
        IndirectClientId indirectClientId = IndirectClientId.fromUrn(request.indirectClientUrn());

        IndirectClientCommands.AddRelatedPersonCmd cmd = new IndirectClientCommands.AddRelatedPersonCmd(
            indirectClientId,
            request.personName(),
            request.role(),
            request.email(),
            request.phone()
        );

        commands.addRelatedPerson(cmd);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-business-info")
    public ResponseEntity<Void> updateBusinessInfo(@RequestBody UpdateBusinessInfoRequest request) {
        IndirectClientId indirectClientId = IndirectClientId.fromUrn(request.indirectClientUrn());

        IndirectClientCommands.UpdateBusinessInfoCmd cmd = new IndirectClientCommands.UpdateBusinessInfoCmd(
            indirectClientId,
            request.businessName(),
            request.taxId()
        );

        commands.updateBusinessInfo(cmd);

        return ResponseEntity.ok().build();
    }

    record CreateIndirectClientRequest(
        String parentClientUrn,
        String businessName,
        String taxId,
        String createdBy
    ) {}

    record CreateIndirectClientResult(String indirectClientUrn) {}

    record AddRelatedPersonRequest(
        String indirectClientUrn,
        String personName,
        String role,  // SIGNING_OFFICER, ADMINISTRATOR, DIRECTOR
        String email,
        String phone
    ) {}

    record UpdateBusinessInfoRequest(
        String indirectClientUrn,
        String businessName,
        String taxId
    ) {}
}
