package com.knight.contexts.users.policy.app.service;

import com.knight.contexts.users.policy.api.commands.PolicyCommands;
import com.knight.contexts.users.policy.api.events.*;
import com.knight.contexts.users.policy.api.queries.PolicyQueries;
import com.knight.contexts.users.policy.app.repository.ApprovalStatementRepository;
import com.knight.contexts.users.policy.app.repository.PermissionStatementRepository;
import com.knight.contexts.users.policy.domain.aggregate.ApprovalStatement;
import com.knight.contexts.users.policy.domain.aggregate.PermissionStatement;
import com.knight.contexts.users.policy.domain.service.PolicyEvaluatorService;
import com.knight.platform.sharedkernel.ProfileId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import com.knight.platform.sharedkernel.OnlineProfileId;
import com.knight.platform.sharedkernel.IndirectProfileId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Application service implementing commands and queries for Policy management.
 * Orchestrates domain operations, repository access, and event publishing.
 */
@Service
public class PolicyApplicationService implements PolicyCommands, PolicyQueries {

    private final PermissionStatementRepository permissionRepository;
    private final ApprovalStatementRepository approvalRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PolicyApplicationService(
        PermissionStatementRepository permissionRepository,
        ApprovalStatementRepository approvalRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.permissionRepository = permissionRepository;
        this.approvalRepository = approvalRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public String createPermissionStatement(CreatePermissionStatementCmd cmd) {
        ProfileId profileId = parseProfileId(cmd.profileUrn());

        PermissionStatement statement = PermissionStatement.create(
            profileId,
            cmd.subject(),
            cmd.action(),
            cmd.resource(),
            cmd.effect()
        );

        permissionRepository.save(statement);

        PermissionStatementCreated event = new PermissionStatementCreated(
            statement.statementId(),
            profileId.urn(),
            cmd.subject(),
            cmd.action(),
            cmd.resource(),
            cmd.effect(),
            statement.createdAt()
        );
        eventPublisher.publishEvent(event);

        return statement.statementId();
    }

    @Override
    @Transactional
    public void updatePermissionStatement(UpdatePermissionStatementCmd cmd) {
        PermissionStatement statement = permissionRepository.findById(cmd.statementId())
            .orElseThrow(() -> new IllegalArgumentException("PermissionStatement not found: " + cmd.statementId()));

        statement.updateAction(cmd.action());
        statement.updateResource(cmd.resource());
        statement.updateEffect(cmd.effect());

        permissionRepository.save(statement);

        PermissionStatementUpdated event = new PermissionStatementUpdated(
            statement.statementId(),
            cmd.action(),
            cmd.resource(),
            cmd.effect(),
            Instant.now()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public void deletePermissionStatement(DeleteStatementCmd cmd) {
        permissionRepository.delete(cmd.statementId());

        PermissionStatementDeleted event = new PermissionStatementDeleted(
            cmd.statementId(),
            Instant.now()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public String createApprovalStatement(CreateApprovalStatementCmd cmd) {
        ProfileId profileId = parseProfileId(cmd.profileUrn());

        ApprovalStatement statement = ApprovalStatement.create(
            profileId,
            cmd.subject(),
            cmd.action(),
            cmd.resource(),
            cmd.approverCount(),
            cmd.approvers(),
            cmd.amountThreshold()
        );

        approvalRepository.save(statement);

        ApprovalStatementCreated event = new ApprovalStatementCreated(
            statement.statementId(),
            profileId.urn(),
            cmd.subject(),
            cmd.action(),
            cmd.resource(),
            cmd.approverCount(),
            cmd.approvers(),
            cmd.amountThreshold(),
            statement.createdAt()
        );
        eventPublisher.publishEvent(event);

        return statement.statementId();
    }

    @Override
    @Transactional
    public void updateApprovalStatement(UpdateApprovalStatementCmd cmd) {
        ApprovalStatement statement = approvalRepository.findById(cmd.statementId())
            .orElseThrow(() -> new IllegalArgumentException("ApprovalStatement not found: " + cmd.statementId()));

        statement.updateApprovers(cmd.approverCount(), cmd.approvers());
        statement.updateThreshold(cmd.amountThreshold());

        approvalRepository.save(statement);

        ApprovalStatementUpdated event = new ApprovalStatementUpdated(
            statement.statementId(),
            cmd.approverCount(),
            cmd.approvers(),
            cmd.amountThreshold(),
            Instant.now()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public void deleteApprovalStatement(DeleteStatementCmd cmd) {
        approvalRepository.delete(cmd.statementId());

        ApprovalStatementDeleted event = new ApprovalStatementDeleted(
            cmd.statementId(),
            Instant.now()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResult evaluatePermission(EvaluatePermissionQuery query) {
        ProfileId profileId = parseProfileId(query.profileUrn());

        List<PermissionStatement> statements = permissionRepository.findByProfileId(profileId);

        PolicyEvaluatorService.PermissionEvaluation eval =
            PolicyEvaluatorService.evaluatePermission(
                statements,
                query.subject(),
                query.action(),
                query.resource()
            );

        return new PermissionResult(eval.allowed(), eval.reason());
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalRequirement evaluateApprovalRequirement(EvaluateApprovalQuery query) {
        ProfileId profileId = parseProfileId(query.profileUrn());

        List<ApprovalStatement> statements = approvalRepository.findByProfileId(profileId);

        PolicyEvaluatorService.ApprovalEvaluation eval =
            PolicyEvaluatorService.evaluateApprovalRequirement(
                statements,
                query.subject(),
                query.action(),
                query.resource(),
                query.amount()
            );

        return new ApprovalRequirement(
            eval.approvalRequired(),
            eval.approverCount(),
            eval.approvers(),
            eval.reason()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatementSummary> getStatementsForProfile(String profileUrn) {
        ProfileId profileId = parseProfileId(profileUrn);

        List<StatementSummary> summaries = new ArrayList<>();

        // Add permission statements
        List<PermissionStatement> permissions = permissionRepository.findByProfileId(profileId);
        for (PermissionStatement p : permissions) {
            summaries.add(new StatementSummary(
                p.statementId(),
                "PERMISSION",
                p.subject(),
                p.action(),
                p.resource(),
                p.effect(),
                null,
                null
            ));
        }

        // Add approval statements
        List<ApprovalStatement> approvals = approvalRepository.findByProfileId(profileId);
        for (ApprovalStatement a : approvals) {
            summaries.add(new StatementSummary(
                a.statementId(),
                "APPROVAL",
                a.subject(),
                a.action(),
                a.resource(),
                null,
                a.approverCount(),
                a.amountThreshold()
            ));
        }

        return summaries;
    }

    private ProfileId parseProfileId(String urn) {
        if (urn.contains(":servicing:")) {
            return ServicingProfileId.fromUrn(urn);
        } else if (urn.contains(":online:")) {
            return OnlineProfileId.fromUrn(urn);
        } else if (urn.contains(":indirect:")) {
            return IndirectProfileId.fromUrn(urn);
        } else {
            throw new IllegalArgumentException("Unknown profile URN format: " + urn);
        }
    }
}
