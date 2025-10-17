package com.knight.contexts.users.policy.infra.rest;

import com.knight.contexts.users.policy.api.queries.PolicyQueries;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller exposing query endpoints for Policy management.
 */
@RestController
@RequestMapping("/queries/users/policy")
public class PolicyQueryController {

    private final PolicyQueries queries;

    public PolicyQueryController(PolicyQueries queries) {
        this.queries = queries;
    }

    @PostMapping("/evaluate-permission")
    public ResponseEntity<PolicyQueries.PermissionResult> evaluatePermission(
        @RequestBody EvaluatePermissionRequest request
    ) {
        PolicyQueries.EvaluatePermissionQuery query = new PolicyQueries.EvaluatePermissionQuery(
            request.profileUrn(),
            request.subject(),
            request.action(),
            request.resource()
        );

        PolicyQueries.PermissionResult result = queries.evaluatePermission(query);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/evaluate-approval")
    public ResponseEntity<PolicyQueries.ApprovalRequirement> evaluateApproval(
        @RequestBody EvaluateApprovalRequest request
    ) {
        PolicyQueries.EvaluateApprovalQuery query = new PolicyQueries.EvaluateApprovalQuery(
            request.profileUrn(),
            request.subject(),
            request.action(),
            request.resource(),
            request.amount()
        );

        PolicyQueries.ApprovalRequirement result = queries.evaluateApprovalRequirement(query);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/profiles/{profileUrn}/statements")
    public ResponseEntity<List<PolicyQueries.StatementSummary>> getStatementsForProfile(
        @PathVariable String profileUrn
    ) {
        List<PolicyQueries.StatementSummary> statements = queries.getStatementsForProfile(profileUrn);

        return ResponseEntity.ok(statements);
    }

    record EvaluatePermissionRequest(
        String profileUrn,
        String subject,
        String action,
        String resource
    ) {}

    record EvaluateApprovalRequest(
        String profileUrn,
        String subject,
        String action,
        String resource,
        BigDecimal amount
    ) {}
}
