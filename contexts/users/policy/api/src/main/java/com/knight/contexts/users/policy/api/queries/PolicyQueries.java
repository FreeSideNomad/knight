package com.knight.contexts.users.policy.api.queries;

public interface PolicyQueries {

    record PolicySummary(
        String policyId,
        String policyType,
        String subject,
        String action,
        String resource,
        Integer approverCount
    ) {}

    PolicySummary getPolicySummary(String policyId);
}
