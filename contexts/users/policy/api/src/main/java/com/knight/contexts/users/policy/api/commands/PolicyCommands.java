package com.knight.contexts.users.policy.api.commands;

public interface PolicyCommands {

    String createPolicy(CreatePolicyCmd cmd);

    record CreatePolicyCmd(
        String policyType,
        String subject,
        String action,
        String resource,
        Integer approverCount
    ) {}

    void updatePolicy(UpdatePolicyCmd cmd);

    record UpdatePolicyCmd(
        String policyId,
        String action,
        String resource,
        Integer approverCount
    ) {}

    void deletePolicy(DeletePolicyCmd cmd);

    record DeletePolicyCmd(String policyId) {}
}
