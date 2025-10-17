package com.knight.contexts.approvalworkflows.engine.infra.rest;

import com.knight.contexts.approvalworkflows.engine.api.queries.ApprovalEngineQueries;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing query endpoints for Approval Engine.
 */
@RestController
@RequestMapping("/queries/approval-workflows/engine")
public class ApprovalEngineQueryController {

    private final ApprovalEngineQueries queries;

    public ApprovalEngineQueryController(ApprovalEngineQueries queries) {
        this.queries = queries;
    }

    @GetMapping("/status/{workflowId}")
    public ResponseEntity<ApprovalEngineQueries.WorkflowStatus> getWorkflowStatus(
        @PathVariable String workflowId) {

        ApprovalEngineQueries.WorkflowStatus status = queries.getWorkflowStatus(workflowId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/pending/{approverId}")
    public ResponseEntity<List<ApprovalEngineQueries.PendingApprovalSummary>> getPendingApprovals(
        @PathVariable String approverId) {

        List<ApprovalEngineQueries.PendingApprovalSummary> pending = queries.getPendingApprovals(approverId);
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/history/{workflowId}")
    public ResponseEntity<ApprovalEngineQueries.WorkflowHistory> getWorkflowHistory(
        @PathVariable String workflowId) {

        ApprovalEngineQueries.WorkflowHistory history = queries.getWorkflowHistory(workflowId);
        return ResponseEntity.ok(history);
    }
}
