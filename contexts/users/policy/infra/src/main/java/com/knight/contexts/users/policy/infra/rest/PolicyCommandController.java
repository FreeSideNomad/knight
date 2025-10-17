package com.knight.contexts.users.policy.infra.rest;

import com.knight.contexts.users.policy.api.commands.PolicyCommands;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

/**
 * REST controller for Policy Management commands.
 */
@Controller("/commands/users/policy")
@ExecuteOn(TaskExecutors.BLOCKING)
public class PolicyCommandController {

    @Inject
    PolicyCommands commands;

    @Post("/create")
    public CreatePolicyResult createPolicy(@Body CreatePolicyRequest req) {
        var policyId = commands.createPolicy(new PolicyCommands.CreatePolicyCmd(
            req.policyType(),
            req.subject(),
            req.action(),
            req.resource(),
            req.approverCount()
        ));
        return new CreatePolicyResult(policyId);
    }

    @Post("/update")
    public void updatePolicy(@Body UpdatePolicyRequest req) {
        commands.updatePolicy(new PolicyCommands.UpdatePolicyCmd(
            req.policyId(),
            req.action(),
            req.resource(),
            req.approverCount()
        ));
    }

    @Post("/delete")
    public void deletePolicy(@Body DeletePolicyRequest req) {
        commands.deletePolicy(new PolicyCommands.DeletePolicyCmd(req.policyId()));
    }

    public record CreatePolicyRequest(String policyType, String subject, String action, String resource, Integer approverCount) {}
    public record CreatePolicyResult(String policyId) {}
    public record UpdatePolicyRequest(String policyId, String action, String resource, Integer approverCount) {}
    public record DeletePolicyRequest(String policyId) {}
}
