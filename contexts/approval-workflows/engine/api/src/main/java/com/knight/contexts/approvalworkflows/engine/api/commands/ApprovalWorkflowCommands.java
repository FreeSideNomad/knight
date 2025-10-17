package com.knight.contexts.approvalworkflows.engine.api.commands;

public interface ApprovalWorkflowCommands {

    String initiateWorkflow(InitiateWorkflowCmd cmd);

    record InitiateWorkflowCmd(
        String resourceType,
        String resourceId,
        int requiredApprovals,
        String initiatedBy
    ) {}

    void recordApproval(RecordApprovalCmd cmd);

    record RecordApprovalCmd(
        String workflowId,
        String approverUserId,
        String decision,
        String comments
    ) {}

    void expireWorkflow(ExpireWorkflowCmd cmd);

    record ExpireWorkflowCmd(String workflowId) {}
}
