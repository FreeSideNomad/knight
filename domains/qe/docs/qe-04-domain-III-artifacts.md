# Domain III: Test Artifacts and Documentation

**Research Domain**: Test Artifacts and Documentation
**Status**: Complete
**Last Updated**: 2025-10-04

---

## Overview

This document provides comprehensive coverage of test artifacts and documentation in software quality engineering. Test artifacts are the tangible work products created throughout the testing process—from high-level strategies to detailed test cases and results. Proper documentation ensures traceability, repeatability, and effective communication with stakeholders.

**Covered Topics**:
1. Test Strategy
2. Test Plans
3. Test Scenarios
4. Test Cases
5. Test Execution and Results
6. Test Reports

**Purpose**: Understanding these artifacts enables systematic test design, execution tracking, and quality assessment aligned with ISTQB standards, DDD domain models, and UX patterns.

---

## 1. Test Strategy

### 1.1 Definition

**ISTQB Glossary Definition**:
> Test Strategy: A high-level description of the test levels to be performed and the testing within those levels for an organization or programme (one or more projects).

**ISO/IEC/IEEE 29119-2 Definition**:
> Test Strategy: Defines the overall approach to testing, including the test levels to be undertaken, the testing techniques to be applied, and the organizational test process to be adopted.

A test strategy is a high-level document that defines the overall approach to testing for an organization or project. It provides a blueprint for how testing will be conducted across all initiatives.

### 1.2 Organizational vs Project Test Strategy

#### Organizational Test Strategy

**Scope**: Organization-wide testing approach
**Longevity**: Long-term (years), updated periodically
**Audience**: All QA staff, development teams, management
**Authority**: Established by QA leadership, approved by executives

**Contents**:
- Organizational testing principles (e.g., shift-left, continuous testing)
- Standard test levels and types across projects
- Required tools and technologies
- Quality gates and acceptance criteria
- Compliance requirements (WCAG 2.2, GDPR, etc.)
- Risk management approach
- Roles and responsibilities

**Job Seeker Organizational Test Strategy Example**:
```yaml
organizational_test_strategy:
  organization: Job Seeker Platform
  version: 1.0
  effective_date: 2025-01-01

  testing_principles:
    - Shift-left: Testing begins at requirements phase
    - Continuous testing: Automated tests in CI/CD pipeline
    - Risk-based: Allocate effort based on risk assessment
    - Quality at speed: Fast feedback without compromising quality

  test_levels:
    - Unit Testing: >90% coverage for domain layer
    - Integration Testing: >85% coverage for application layer
    - System Testing: Critical paths and workflows
    - Acceptance Testing: BDD scenarios, user validation

  test_types:
    functional:
      - Requirements-based testing
      - User acceptance testing

    non_functional:
      - Performance: <200ms API response time
      - Security: OWASP Top 10 compliance
      - Usability: SUS score >75
      - Accessibility: WCAG 2.2 Level AA (100% compliance)

  compliance_requirements:
    - WCAG 2.2 Level AA (Accessibility)
    - EU Accessibility Act (June 28, 2025)
    - GDPR (Data protection)

  standard_tools:
    unit_integration: Jest, React Testing Library
    e2e: Playwright
    accessibility: axe-core, NVDA, JAWS
    performance: Lighthouse, k6
    ci_cd: GitHub Actions
    quality_gates: SonarQube

  quality_gates:
    - Code coverage >80% (domain >90%, application >85%, UI >70%)
    - All critical and high severity defects resolved
    - Zero accessibility violations (critical/serious)
    - Performance benchmarks met
```

#### Project Test Strategy

**Scope**: Specific project or feature
**Longevity**: Duration of project (weeks to months)
**Audience**: Project team members
**Authority**: Established by Test Manager/QA Lead, approved by Project Manager

**Contents**:
- Test scope (in-scope and out-of-scope)
- Test approach specific to project
- Entry and exit criteria
- Test deliverables
- Test schedule and milestones
- Project-specific risks
- Dependencies

**Job Seeker Project Test Strategy Example (bc_applications)**:
```yaml
project_test_strategy:
  project: Application Tracking System (bc_applications)
  bounded_context_ref: bc_applications
  version: 1.0
  duration: 2025-Q1 (3 months)

  test_scope:
    in_scope:
      - agg_application aggregate testing
      - svc_app_submit_application use case
      - svc_app_track_application use case
      - page_application_wizard UI testing
      - wf_submit_application workflow testing
      - Cross-context integration (bc_profile, bc_job_catalog, bc_matching)

    out_of_scope:
      - bc_matching algorithm testing (separate bounded context)
      - Payment processing (not in MVP)

  test_approach:
    domain_layer:
      - TDD for Value Objects (vo_application_status, vo_submission_date)
      - Unit tests for ent_application entity
      - Integration tests for agg_application aggregate

    application_layer:
      - Integration tests for svc_app_submit_application
      - Event handling tests (evt_application_submitted, evt_application_status_changed)
      - Cross-context integration tests

    ui_layer:
      - Component tests for application wizard steps
      - E2E tests for wf_submit_application
      - Accessibility tests (WCAG 2.2 AA)
      - Usability testing sessions (5 users)

  risk_assessment:
    high_risk:
      - Cross-context integration complexity
        mitigation: Integration test suite, contract testing

      - Multi-step application wizard state management
        mitigation: State transition testing, E2E coverage

    medium_risk:
      - Application status transition rules
        mitigation: Decision table testing, state diagram validation

  entry_criteria:
    - DDD domain model finalized
    - UX wireframes approved
    - Test environment provisioned (Docker containers)

  exit_criteria:
    - >90% coverage for domain layer
    - >85% coverage for application layer
    - All critical/high defects resolved
    - 100% WCAG 2.2 AA compliance
    - Usability testing complete (SUS >75)

  deliverables:
    - Test plan document
    - Test case specifications (YAML)
    - Test execution reports
    - Defect reports
    - Test summary report
```

### 1.3 When to Create Test Strategies

**Create Organizational Test Strategy when**:
- Establishing QA function in new organization
- Standardizing testing practices across teams
- Adopting new methodologies (Agile, DevOps)
- Regulatory compliance requirements change
- New technology stack adopted organization-wide

**Create Project Test Strategy when**:
- Starting significant new project (3+ months)
- Project has unique testing requirements
- High-risk project requiring special attention
- Multiple teams collaborating on project
- Contractual testing obligations

**Skip Project Strategy when**:
- Small project (<1 month)
- Routine maintenance work
- Standard organizational practices sufficient
- Embedded QA in Agile team handles testing

### 1.4 Components and Structure

#### Essential Components

1. **Introduction**
   - Purpose and scope
   - References (standards, organizational policies)
   - Definitions and acronyms

2. **Test Approach**
   - Test levels (unit, integration, system, acceptance)
   - Test types (functional, non-functional)
   - Test techniques (BVA, EP, state transition, etc.)

3. **Test Organization**
   - Roles and responsibilities
   - Team structure
   - Training requirements

4. **Test Process**
   - Test planning process
   - Test design and implementation
   - Test execution and reporting
   - Defect management

5. **Test Environment**
   - Infrastructure requirements
   - Tools and technologies
   - Test data management

6. **Quality Goals and Metrics**
   - Coverage targets
   - Quality gates
   - KPIs and metrics

7. **Risk Management**
   - Risk identification approach
   - Risk-based testing allocation
   - Mitigation strategies

8. **Compliance and Standards**
   - Industry standards (ISTQB, ISO, IEEE)
   - Regulatory compliance (WCAG, GDPR)
   - Organizational policies

### 1.5 Alignment with Quality Strategy

Test strategy is a subset of the broader **Quality Strategy**, which encompasses:
- Quality management principles (ISO 9001)
- Quality assurance processes
- Quality control activities (including testing)
- Continuous improvement

**Relationship**:
```yaml
quality_strategy:
  quality_planning:
    - Quality objectives
    - Quality standards
    - Quality metrics

  quality_assurance:
    - Process audits
    - Reviews and inspections
    - Training and mentoring

  quality_control:
    - Testing (test strategy covers this)
    - Defect management
    - Verification and validation

  continuous_improvement:
    - Root cause analysis
    - Process improvement
    - Lessons learned
```

### 1.6 Best Practices and Frameworks

#### Dan Ashby's Continuous Testing Model

Focus on integrating testing throughout the delivery pipeline:

1. **Shift-Left**: Testing earlier in lifecycle
2. **Automated Fast Feedback**: CI/CD integration
3. **Production Monitoring**: Shift-right testing
4. **Data-Driven Decisions**: Metrics and analytics

**Job Seeker Application**:
```yaml
continuous_testing_job_seeker:
  shift_left:
    - TDD for Value Objects and Entities
    - BDD scenarios defined during sprint planning
    - Test case design during story refinement

  automated_feedback:
    - Unit tests run on every commit (<5 min)
    - Integration tests run on PR (<15 min)
    - E2E tests run on merge to main (<30 min)

  production_monitoring:
    - Error tracking (Sentry)
    - Performance monitoring (Lighthouse CI)
    - User analytics (session recordings)

  data_driven:
    - Test coverage dashboard (SonarQube)
    - Defect density by bounded context
    - Test execution trends
```

#### Janet Gregory's Holistic Testing Model

Four testing quadrants based on business/technology facing and supporting/critiquing:

**Quadrant 1**: Technology-facing, Supporting (Unit, Component)
**Quadrant 2**: Business-facing, Supporting (Functional, User stories)
**Quadrant 3**: Business-facing, Critiquing (Exploratory, Usability, UAT)
**Quadrant 4**: Technology-facing, Critiquing (Performance, Security)

**Job Seeker Mapping**:
```yaml
holistic_testing_quadrants:
  q1_technology_supporting:
    - Unit tests for vo_email, ent_candidate
    - Component tests for atom_button, comp_email_input
    - TDD during development

  q2_business_supporting:
    - BDD scenarios for user stories
    - Functional tests for wf_submit_application
    - Integration tests for svc_app_submit_application

  q3_business_critiquing:
    - Exploratory testing of workflows
    - Usability testing sessions
    - User acceptance testing
    - Accessibility testing (WCAG 2.2)

  q4_technology_critiquing:
    - Performance testing (load, stress)
    - Security testing (OWASP Top 10)
    - Reliability testing
    - Scalability testing
```

### 1.7 DDD Integration

Test strategy must align with DDD bounded contexts:

```yaml
test_strategy_ddd_integration:
  strategic_alignment:
    bounded_context_mapping:
      - Each bounded context has test plan
      - Cross-context integration tests defined
      - Shared kernel testing coordinated

    test_organization_by_context:
      bc_profile:
        qa_owner: QA_Engineer_1
        focus: Profile management, Value Object validation
        risk_level: Medium

      bc_applications:
        qa_owner: QA_Engineer_2
        focus: Application workflow, cross-context integration
        risk_level: High

      bc_job_catalog:
        qa_owner: QA_Engineer_3
        focus: Job search, filtering, pagination
        risk_level: Medium

      bc_matching:
        qa_owner: QA_Engineer_4
        focus: Matching algorithm, performance
        risk_level: High

  tactical_pattern_testing:
    value_objects:
      test_level: Unit
      coverage_target: ">95%"
      test_focus: Validation rules, immutability, equality

    entities:
      test_level: Unit
      coverage_target: ">90%"
      test_focus: Identity, lifecycle, business logic

    aggregates:
      test_level: Integration
      coverage_target: ">90%"
      test_focus: Consistency boundary, invariants, transactional integrity

    repositories:
      test_level: Integration
      coverage_target: ">85%"
      test_focus: CRUD operations, query correctness

    domain_services:
      test_level: Integration
      coverage_target: ">90%"
      test_focus: Complex business logic, cross-aggregate operations

    application_services:
      test_level: System
      coverage_target: ">85%"
      test_focus: Use case orchestration, transaction management
```

### 1.8 UX Integration

Test strategy must address UX patterns (Atomic Design):

```yaml
test_strategy_ux_integration:
  atomic_design_testing:
    atoms:
      examples: atom_button, atom_input, atom_label
      test_level: Unit
      test_focus: Rendering, props, basic interaction
      coverage_target: ">80%"

    molecules:
      examples: comp_email_input, comp_skills_input
      test_level: Unit/Component
      test_focus: Composition, validation feedback, state management
      coverage_target: ">80%"

    organisms:
      examples: org_job_card, org_profile_summary
      test_level: Integration
      test_focus: Complex behavior, data integration
      coverage_target: ">75%"

    pages:
      examples: page_profile_edit, page_job_listings
      test_level: Integration
      test_focus: Layout, data loading, error handling
      coverage_target: ">70%"

    workflows:
      examples: wf_submit_application, wf_create_profile
      test_level: E2E
      test_focus: User journey, multi-step navigation, state persistence
      coverage_target: "100% critical paths"

  accessibility_strategy:
    standard: WCAG 2.2 Level AA
    compliance_target: 100%

    automated_testing:
      tool: axe-core (jest-axe)
      frequency: Every CI/CD run
      scope: All components and pages

    manual_testing:
      tools: NVDA, JAWS screen readers
      frequency: Quarterly audits
      scope: All workflows and critical pages

    keyboard_navigation:
      requirement: 100% keyboard accessible
      test_frequency: Every sprint
      verification: Manual exploratory testing
```

---

## 2. Test Plans

### 2.1 Definition

**ISTQB Glossary Definition**:
> Test Plan: Documentation describing the test objectives to be achieved and the means and the schedule for achieving them, organized to coordinate testing activities.

**IEEE 829 (Historical)**:
> Test Plan: A document describing the scope, approach, resources, and schedule of intended test activities. It identifies test items, features to be tested, testing tasks, who will do each task, and any risks requiring contingency planning.

A test plan is more detailed and tactical than a test strategy, focusing on how testing will be executed for a specific project, release, or bounded context.

### 2.2 Differences from Test Strategy

| Aspect | Test Strategy | Test Plan |
|--------|---------------|-----------|
| **Scope** | Organization or program-wide | Specific project or release |
| **Level** | High-level approach | Detailed execution plan |
| **Longevity** | Long-term (years) | Short-term (weeks to months) |
| **Focus** | What and why | How, when, who |
| **Audience** | Executives, all QA | Project team, testers |
| **Detail** | General principles | Specific tasks and schedules |
| **Updates** | Infrequent (annually) | Frequent (per sprint/release) |

**Analogy**:
- **Test Strategy** = War strategy (overall approach to winning)
- **Test Plan** = Battle plan (specific tactics for this engagement)

### 2.3 Components (IEEE 829 Standard)

The IEEE 829 standard (now superseded by ISO/IEC/IEEE 29119-3) specified these components:

#### 1. Test Plan Identifier
Unique identifier and version number

#### 2. Introduction
Purpose, scope, references, overview

#### 3. Test Items
What will be tested (features, modules, components)

**Job Seeker Example**:
```yaml
test_items_bc_applications:
  domain_layer:
    - vo_application_status
    - vo_submission_date
    - ent_application
    - agg_application

  application_layer:
    - svc_app_submit_application
    - svc_app_track_application
    - evt_application_submitted
    - evt_application_status_changed

  ui_layer:
    - page_application_wizard
    - comp_application_form_step1
    - comp_application_form_step2
    - wf_submit_application
```

#### 4. Features to be Tested
Specific functionality to test

**Job Seeker Example**:
```yaml
features_to_test:
  - Create new application (draft state)
  - Submit application (draft → submitted transition)
  - Track application status
  - Withdraw application
  - Application auto-save
  - Application form validation
  - Cross-context data integration (profile, job posting)
  - Application submission confirmation
```

#### 5. Features NOT to be Tested
Explicitly state what's out of scope

**Job Seeker Example**:
```yaml
features_not_tested:
  - Job matching algorithm (bc_matching, separate test plan)
  - Payment processing (not in MVP)
  - Email notifications (handled by bc_notifications)
  - Admin panel features (separate test plan)
```

#### 6. Test Approach
High-level testing methodology

**Job Seeker Example**:
```yaml
test_approach_bc_applications:
  methodology: Agile testing, continuous integration

  test_levels:
    unit:
      scope: Value Objects, Entities
      tool: Jest
      coverage_target: ">95%"
      execution: Every commit

    integration:
      scope: Aggregates, Application Services, Repositories
      tool: Jest with test database
      coverage_target: ">85%"
      execution: Every PR

    system:
      scope: Complete workflows
      tool: Playwright
      coverage_target: "100% critical paths"
      execution: Merge to main

  test_types:
    functional:
      - Requirements-based testing
      - BDD scenarios (Cucumber syntax)
      - User acceptance testing

    non_functional:
      - Performance: Application submission <500ms
      - Security: Authorization checks, CSRF protection
      - Usability: Multi-step wizard UX
      - Accessibility: WCAG 2.2 Level AA

  test_techniques:
    - State Transition Testing (application status states)
    - Decision Table Testing (submission validation rules)
    - Equivalence Partitioning (form field inputs)
    - Boundary Value Analysis (file upload size limits)
```

#### 7. Item Pass/Fail Criteria
When is a test item considered passed?

**Job Seeker Example**:
```yaml
pass_fail_criteria:
  unit_tests:
    pass: All tests pass, >95% coverage, no critical SonarQube issues
    fail: Any test fails OR coverage <95% OR critical issues exist

  integration_tests:
    pass: All tests pass, >85% coverage, cross-context integration verified
    fail: Any test fails OR integration failures

  e2e_tests:
    pass: All critical workflows complete successfully, <3s execution time
    fail: Any workflow fails OR timeout exceeded

  accessibility:
    pass: Zero critical/serious axe-core violations, screen reader compatible
    fail: Any critical/serious violation exists

  performance:
    pass: API <200ms (95th percentile), page load <1s
    fail: Performance benchmarks not met
```

#### 8. Suspension Criteria and Resumption Requirements
When to stop testing and when to resume

**Job Seeker Example**:
```yaml
suspension_criteria:
  - Critical defect blocking all testing (e.g., database connection failure)
  - Test environment unavailable for >4 hours
  - Build failure rate >50% (unstable codebase)
  - Major requirement changes invalidating >30% of test cases

resumption_requirements:
  - Critical defect resolved and verified
  - Test environment restored and stable
  - Build stabilized (failure rate <10%)
  - Updated requirements reviewed and test cases updated
```

#### 9. Test Deliverables
What artifacts will be produced?

**Job Seeker Example**:
```yaml
test_deliverables:
  planning_phase:
    - Test plan document (this document)
    - Test case specifications (YAML format)
    - Test data specifications

  execution_phase:
    - Test scripts (Jest, Playwright)
    - Test execution logs
    - Defect reports (GitHub Issues)
    - Daily test status reports

  completion_phase:
    - Test summary report
    - Coverage reports (Istanbul, SonarQube)
    - Metrics dashboard
    - Lessons learned document
```

#### 10. Testing Tasks
Specific tasks and activities

**Job Seeker Example**:
```yaml
testing_tasks:
  sprint_planning:
    - Review user stories
    - Define BDD acceptance criteria
    - Estimate test effort
    - Identify test data needs

  sprint_execution:
    - Implement unit tests (TDD)
    - Implement integration tests
    - Implement E2E tests for completed features
    - Execute regression test suite
    - Perform exploratory testing
    - Log and track defects

  sprint_review:
    - Demo with automated test execution
    - Gather stakeholder feedback
    - Update test documentation

  sprint_retrospective:
    - Review test metrics
    - Identify process improvements
    - Update test strategy if needed
```

#### 11. Environmental Needs
Infrastructure, tools, software, hardware

**Job Seeker Example**:
```yaml
environmental_needs:
  test_environments:
    local_dev:
      purpose: Developer testing
      setup: Docker Compose
      components: PostgreSQL, Redis, Node.js

    ci_cd:
      purpose: Automated testing
      platform: GitHub Actions
      runners: Ubuntu latest
      parallelization: 4 concurrent jobs

    staging:
      purpose: E2E testing, UAT
      infrastructure: Cloud-hosted (similar to production)
      data: Anonymized production data subset

  tools:
    test_frameworks:
      - Jest (unit, integration)
      - React Testing Library (component)
      - Playwright (E2E)

    test_management:
      - GitHub Projects (test planning)
      - GitHub Issues (defect tracking)

    quality_tools:
      - SonarQube (code quality)
      - axe-core (accessibility)
      - Lighthouse (performance)

  test_data:
    source: Mock data factory, fixtures
    storage: test/fixtures/*.json
    refresh_strategy: Recreate before each test run
```

#### 12. Responsibilities
Who does what?

**Job Seeker Example**:
```yaml
responsibilities:
  test_manager:
    - Define test strategy
    - Allocate resources
    - Monitor progress and quality metrics
    - Report to stakeholders

  qa_engineer_bc_applications:
    - Create test plan and test cases
    - Implement integration and E2E tests
    - Execute manual exploratory tests
    - Log and verify defects
    - Maintain test documentation

  developers:
    - Implement unit tests (TDD)
    - Fix defects
    - Support integration test development
    - Code review of test code

  ux_designer:
    - Define usability test scenarios
    - Participate in usability testing
    - Validate accessibility compliance

  product_owner:
    - Define acceptance criteria
    - Participate in UAT
    - Approve test deliverables
```

#### 13. Staffing and Training Needs
Resource requirements

**Job Seeker Example**:
```yaml
staffing:
  qa_engineers: 2 FTE
    - QA Engineer 1: bc_applications, bc_profile
    - QA Engineer 2: bc_matching, bc_job_catalog

  test_automation_engineer: 0.5 FTE
    - Maintain test framework
    - CI/CD pipeline optimization

training_needs:
  - DDD fundamentals for QA (2-day workshop)
  - Playwright advanced features (1-day workshop)
  - WCAG 2.2 accessibility testing (half-day workshop)
  - GitHub Actions CI/CD (self-paced online)
```

#### 14. Schedule
Test milestones and timeline

**Job Seeker Example**:
```yaml
schedule:
  sprint_1:
    week_1:
      - Test plan creation
      - Test environment setup
      - Unit test implementation (TDD)

    week_2:
      - Integration test implementation
      - Defect fixing
      - E2E test implementation

  sprint_2:
    week_3:
      - Regression testing
      - Accessibility testing
      - Performance testing
      - Usability testing session 1

    week_4:
      - Defect fixing and verification
      - UAT
      - Test summary report

  milestones:
    - 2025-01-15: Test plan approved
    - 2025-01-31: All unit tests complete (Sprint 1)
    - 2025-02-15: Integration tests complete (Sprint 2)
    - 2025-02-28: UAT complete, release approved
```

#### 15. Risks and Contingencies
Potential issues and mitigation

**Job Seeker Example**:
```yaml
risks:
  - risk: Cross-context integration complexity
    probability: Medium
    impact: High
    mitigation: Early integration testing, contract testing, frequent integration
    contingency: Extend sprint if integration issues found

  - risk: Test data availability
    probability: Low
    impact: Medium
    mitigation: Mock data factory, automated data generation
    contingency: Manual test data creation if needed

  - risk: Performance testing expertise gap
    probability: Medium
    impact: Medium
    mitigation: Training on k6, external consultant if needed
    contingency: Simplified performance tests, monitoring in production

  - risk: Accessibility compliance issues late in cycle
    probability: Low
    impact: High
    mitigation: Automated axe-core tests from day 1, early manual audits
    contingency: Dedicated accessibility sprint if major issues found
```

#### 16. Approvals
Who approves the test plan?

**Job Seeker Example**:
```yaml
approvals:
  prepared_by:
    name: QA Engineer
    date: 2025-01-10

  reviewed_by:
    name: Test Manager
    date: 2025-01-12

  approved_by:
    - name: Product Owner
      date: 2025-01-15
    - name: Development Lead
      date: 2025-01-15
```

### 2.4 Agile/DevOps Approach to Test Plans

In Agile environments, traditional test plans are often replaced by:

**1. Living Documentation**
- Test plan as wiki page (constantly updated)
- BDD scenarios as executable documentation
- Test automation as documentation

**2. Lightweight Test Plans**
- One-page test approach document
- Focus on what's different from standard practices
- Reference organizational test strategy for common elements

**3. Iteration-based Planning**
- Test planning per sprint
- Test strategy at epic/feature level
- Continuous refinement

**Job Seeker Agile Test Plan Template**:
```yaml
agile_test_plan_bc_applications:
  epic: Application Tracking System
  bounded_context: bc_applications
  sprint_duration: 2 weeks
  team: Team Alpha

  # What's different from organizational test strategy?
  unique_approach:
    - Heavy focus on state transition testing (application status)
    - Cross-context integration with bc_profile, bc_job_catalog, bc_matching
    - Multi-step wizard requiring E2E coverage

  # What are we testing this sprint?
  sprint_scope:
    user_stories:
      - US-101: As a candidate, I can create a draft application
      - US-102: As a candidate, I can submit an application
      - US-103: As a candidate, I can view application status

    acceptance_criteria: (see BDD scenarios in code)

  # How will we test?
  test_approach:
    - TDD for Value Objects (vo_application_status)
    - Integration tests for agg_application
    - E2E tests for wf_submit_application
    - Accessibility testing (WCAG 2.2 AA)

  # Definition of Done
  dod:
    - All acceptance tests pass
    - >90% code coverage (domain layer)
    - Zero critical accessibility violations
    - Code reviewed and approved
    - Deployed to staging and verified

  # Risks this sprint
  risks:
    - Cross-context integration untested
      mitigation: Integration test suite, contract testing

  # Resources
  team:
    - 2 developers
    - 1 QA engineer (embedded)
    - 1 UX designer (part-time)
```

### 2.5 Test Planning in Continuous Delivery

In CD environments, test planning focuses on:

**Pipeline Design**:
```yaml
cd_test_pipeline:
  commit_stage:
    trigger: Every commit
    tests: Unit tests, linting
    duration: <5 min
    failure_action: Block PR

  acceptance_stage:
    trigger: PR merge
    tests: Integration tests, component tests
    duration: <15 min
    failure_action: Block deployment

  deployment_stage:
    trigger: Deploy to staging
    tests: E2E tests, smoke tests
    duration: <30 min
    failure_action: Rollback deployment

  production_monitoring:
    trigger: Deploy to production
    tests: Smoke tests, synthetic monitoring
    duration: Continuous
    failure_action: Alert, potential rollback
```

### 2.6 Risk Assessment in Test Planning

Risk-based testing allocates effort based on risk (likelihood × impact):

**Job Seeker Risk Assessment**:
```yaml
risk_assessment_bc_applications:
  high_risk:
    - area: svc_app_submit_application (cross-context integration)
      likelihood: Medium
      impact: High
      risk_score: 8
      test_allocation: 30%
      coverage_target: ">95%"
      test_techniques:
        - Integration testing
        - Contract testing
        - Error scenario testing

    - area: Application status state transitions
      likelihood: Medium
      impact: High
      risk_score: 8
      test_allocation: 20%
      coverage_target: ">95%"
      test_techniques:
        - State transition testing
        - Decision table testing
        - Invalid transition testing

  medium_risk:
    - area: Application form validation
      likelihood: Low
      impact: Medium
      risk_score: 4
      test_allocation: 15%
      coverage_target: ">85%"
      test_techniques:
        - Boundary value analysis
        - Equivalence partitioning

    - area: Application auto-save
      likelihood: Medium
      impact: Medium
      risk_score: 6
      test_allocation: 10%
      coverage_target: ">80%"
      test_techniques:
        - Timer testing
        - Network interruption simulation

  low_risk:
    - area: Application list display
      likelihood: Low
      impact: Low
      risk_score: 2
      test_allocation: 5%
      coverage_target: ">70%"
      test_techniques:
        - Basic functional testing
        - Visual regression testing
```

**Risk-Based Test Allocation** (70-20-10 rule):
- 70% effort on high-risk areas
- 20% effort on medium-risk areas
- 10% effort on low-risk areas

### 2.7 DDD Integration Example

Test plan organized by DDD layers:

```yaml
test_plan_ddd_structure:
  bounded_context: bc_applications
  version: 1.0

  domain_layer_testing:
    value_objects:
      - vo_application_status:
          test_focus: Valid transitions, validation
          test_level: Unit
          test_count: 15
          coverage_target: ">95%"

      - vo_submission_date:
          test_focus: Date validation, formatting
          test_level: Unit
          test_count: 10
          coverage_target: ">95%"

    entities:
      - ent_application:
          test_focus: Identity, lifecycle, business logic
          test_level: Unit
          test_count: 25
          coverage_target: ">90%"

    aggregates:
      - agg_application:
          test_focus: Consistency boundary, invariants
          test_level: Integration
          test_count: 40
          coverage_target: ">90%"
          cross_context_tests: 10

  application_layer_testing:
    application_services:
      - svc_app_submit_application:
          test_focus: Use case orchestration
          test_level: Integration/System
          test_count: 30
          scenarios: 15

      - svc_app_track_application:
          test_focus: Query and filtering
          test_level: Integration
          test_count: 20
          scenarios: 10

    domain_events:
      - evt_application_submitted:
          test_focus: Event publication, handling
          test_level: Integration
          test_count: 8

  infrastructure_layer_testing:
    repositories:
      - repo_application:
          test_focus: CRUD, queries, transactions
          test_level: Integration
          test_count: 25

  ui_layer_testing:
    pages:
      - page_application_wizard:
          test_focus: Multi-step navigation, validation
          test_level: E2E
          test_count: 20
          accessibility_tests: 15

    workflows:
      - wf_submit_application:
          test_focus: Complete user journey
          test_level: E2E
          test_count: 10
          performance_tests: 5
```

---

## 3. Test Scenarios

### 3.1 Definition

**ISTQB Glossary Definition**:
> Test Scenario: A sequence of test cases or test procedures, or both, that together form a test for an overall requirement or function.

**Alternative Definition**:
> Test Scenario: A high-level description of what to test, often from a user's perspective, without specifying the exact steps or test data.

A test scenario is a narrative description of what will be tested. It's more abstract than a test case, focusing on the "what" rather than the "how."

**Characteristics**:
- High-level (less detailed than test case)
- User-centric (describes user goals)
- Can map to multiple test cases
- Often written in business language
- Used for test planning and estimation

### 3.2 How to Write Effective Test Scenarios

#### Format 1: User Story Format
```
As a [role], I want to [action], so that [benefit]
```

**Job Seeker Examples**:
```yaml
test_scenarios_user_story_format:
  - scenario: "As a job seeker, I want to submit an application for a job posting, so that I can be considered for the position"

  - scenario: "As a job seeker, I want to track my application status, so that I know the progress of my application"

  - scenario: "As a job seeker, I want to withdraw my application, so that I'm no longer considered for the position"

  - scenario: "As a job seeker, I want my application to auto-save, so that I don't lose my progress if I navigate away"
```

#### Format 2: Scenario Name + Description
```
Scenario: [Clear, descriptive name]
Description: [What will be tested and expected outcome]
```

**Job Seeker Examples**:
```yaml
test_scenarios_descriptive:
  - scenario: "Successful Application Submission"
    description: "Verify that a candidate can create, complete, and submit an application, resulting in confirmation and status update to 'Submitted'"

  - scenario: "Application Form Validation"
    description: "Verify that the application form validates all required fields and displays appropriate error messages for invalid inputs"

  - scenario: "Application Status Transitions"
    description: "Verify that application status correctly transitions through states: Draft → Submitted → Under Review → Accepted/Rejected"

  - scenario: "Cross-Context Data Integration"
    description: "Verify that application correctly pulls candidate profile data and job posting details"
```

#### Format 3: BDD Gherkin Format (Executable)
```gherkin
Scenario: [Name]
  Given [preconditions]
  When [action]
  Then [expected result]
```

**Job Seeker Examples**:
```gherkin
Scenario: Submit Application for Job Posting
  Given I am a logged-in candidate
  And I have a complete profile
  And I am viewing a job posting I haven't applied to
  When I click "Apply Now"
  And I complete the application wizard
  And I click "Submit Application"
  Then I see a confirmation message
  And the application status is "Submitted"
  And I receive a confirmation email
  And the evt_application_submitted event is published

Scenario: Application Form Auto-Save
  Given I am on the application wizard
  And I have filled in some fields
  When 30 seconds elapse
  Then the application is auto-saved as a draft
  And I see a "Draft saved" notification

Scenario: Prevent Duplicate Application
  Given I am a logged-in candidate
  And I have already submitted an application for job posting "Software Engineer"
  When I try to apply again for the same job posting
  Then I see a message "You have already applied for this position"
  And the Apply button is disabled
```

### 3.3 Relationship to Requirements and User Stories

**Traceability Mapping**:
```yaml
requirements_to_scenarios_mapping:
  requirement: REQ-APP-001
  title: "Candidate can submit job application"

  user_story: US-102
  story: "As a candidate, I can submit an application for a job posting"

  test_scenarios:
    - TS-102-01: Successful application submission
    - TS-102-02: Application submission with missing required fields
    - TS-102-03: Application submission for already applied job
    - TS-102-04: Application submission when not logged in
    - TS-102-05: Application submission with file attachments

  test_cases:
    TS-102-01:
      - TC-102-01-01: Submit application with all required fields
      - TC-102-01-02: Submit application with optional fields
      - TC-102-01-03: Verify evt_application_submitted event
      - TC-102-01-04: Verify confirmation email sent

    TS-102-02:
      - TC-102-02-01: Submit with missing cover letter
      - TC-102-02-02: Submit with missing resume
      - TC-102-02-03: Verify validation error messages
```

**Requirements Traceability Matrix (RTM)**:
```yaml
rtm_bc_applications:
  - requirement_id: REQ-APP-001
    title: Submit application
    test_scenarios: [TS-102-01, TS-102-02, TS-102-03]
    test_cases: [TC-102-01-01, TC-102-01-02, TC-102-02-01]
    status: Verified
    coverage: 100%

  - requirement_id: REQ-APP-002
    title: Track application status
    test_scenarios: [TS-103-01, TS-103-02]
    test_cases: [TC-103-01-01, TC-103-01-02]
    status: In Progress
    coverage: 80%

  - requirement_id: REQ-APP-003
    title: Withdraw application
    test_scenarios: [TS-104-01]
    test_cases: [TC-104-01-01, TC-104-01-02]
    status: Not Started
    coverage: 0%
```

### 3.4 Scenario-Based Testing Approaches

#### Use Case Testing

Map test scenarios to use cases:

```yaml
use_case_testing_application_submission:
  use_case: UC-APP-001 Submit Application

  primary_actor: Job Seeker (Candidate)

  preconditions:
    - User is authenticated
    - User has a complete profile
    - Job posting is active and accepting applications

  main_success_scenario:
    1: User navigates to job posting detail page
    2: User clicks "Apply Now" button
    3: System displays application wizard (Step 1: Review Profile)
    4: User reviews and confirms profile information
    5: User proceeds to Step 2 (Cover Letter and Resume)
    6: User uploads/edits cover letter and resume
    7: User proceeds to Step 3 (Review and Submit)
    8: User reviews application summary
    9: User clicks "Submit Application"
    10: System validates application
    11: System creates agg_application in "Submitted" status
    12: System publishes evt_application_submitted event
    13: System displays confirmation page
    14: System sends confirmation email

  test_scenarios_mapping:
    - TS-UC-001-01: Happy path (main success scenario)
    - TS-UC-001-02: Alternative flow - user edits profile during application
    - TS-UC-001-03: Alternative flow - user saves draft and completes later
    - TS-UC-001-04: Exception flow - validation errors
    - TS-UC-001-05: Exception flow - duplicate application attempt
    - TS-UC-001-06: Exception flow - job posting closed during application
```

#### Context-Scenario-Action Pattern

Organize scenarios by context:

```yaml
context_scenario_action:
  context: Application Submission Workflow

  scenarios:
    - scenario: First-time application
      actions:
        - Navigate to job posting
        - Click Apply
        - Complete wizard steps
        - Submit application
      expected: Application created, confirmation shown

    - scenario: Resume incomplete application draft
      actions:
        - Navigate to My Applications
        - Click "Continue Application" on draft
        - Complete remaining steps
        - Submit application
      expected: Draft updated to Submitted, confirmation shown

    - scenario: Application with custom cover letter
      actions:
        - Start application
        - Upload custom cover letter file
        - Provide additional notes
        - Submit application
      expected: Cover letter attached, visible in application summary
```

### 3.5 Best Practices for Test Scenarios

1. **User-Centric Language**: Write from user's perspective
2. **Clear and Concise**: One clear goal per scenario
3. **Traceable**: Link to requirements/user stories
4. **Independent**: Each scenario should stand alone
5. **Testable**: Should be verifiable with clear pass/fail
6. **Complete**: Cover happy path and alternative/exception flows
7. **Realistic**: Represent actual user behavior

**Anti-Patterns to Avoid**:
- Too vague ("Test the application feature")
- Too detailed (becomes a test case)
- Testing implementation instead of behavior
- No clear expected outcome
- Mixing multiple unrelated scenarios

### 3.6 DDD Integration: Scenarios by Bounded Context

```yaml
test_scenarios_by_bounded_context:
  bc_applications:
    aggregate: agg_application
    scenarios:
      - Create draft application
      - Submit application
      - Track application status
      - Withdraw application
      - Update submitted application (if allowed)
      - Application status transitions (state machine)

  bc_profile:
    aggregate: agg_candidate_profile
    scenarios:
      - Create candidate profile
      - Update profile information
      - Add/remove skills
      - Upload resume
      - Set job preferences

  bc_job_catalog:
    aggregate: agg_job_posting
    scenarios:
      - Search jobs by keyword
      - Filter jobs by location
      - Filter jobs by experience level
      - View job posting details
      - Save job posting to favorites

  bc_matching:
    aggregate: agg_match_score
    scenarios:
      - Calculate match score for candidate-job pair
      - Retrieve top matches for candidate
      - Retrieve top candidates for job posting
      - Update match score when profile changes

  # Cross-Context Scenarios
  cross_context:
    - scenario: Submit application (bc_applications + bc_profile + bc_job_catalog)
      description: Application pulls data from Profile and Job Catalog

    - scenario: Match score updates on profile change (bc_profile + bc_matching)
      description: Profile update triggers match score recalculation
```

### 3.7 UX Integration: Scenarios by Workflow

```yaml
test_scenarios_by_workflow:
  wf_submit_application:
    description: Complete application submission workflow
    pages: [page_job_detail, page_application_wizard, page_application_confirmation]

    scenarios:
      - TS-WF-APP-01: Happy path application submission
        steps:
          - View job posting
          - Click Apply
          - Navigate wizard (3 steps)
          - Submit application
          - View confirmation

      - TS-WF-APP-02: Application with validation errors
        steps:
          - Start application
          - Skip required fields
          - Attempt to proceed
          - See validation errors
          - Correct errors
          - Submit successfully

      - TS-WF-APP-03: Application save as draft
        steps:
          - Start application
          - Fill partial information
          - Click "Save Draft"
          - Navigate away
          - Return later
          - Continue from saved draft

      - TS-WF-APP-04: Application auto-save
        steps:
          - Start application
          - Fill some fields
          - Wait 30 seconds
          - See auto-save notification
          - Verify draft saved (refresh page)

  wf_create_profile:
    description: New candidate profile creation
    pages: [page_signup, page_profile_wizard, page_profile_summary]

    scenarios:
      - TS-WF-PROF-01: Complete profile creation
      - TS-WF-PROF-02: Profile creation with validation errors
      - TS-WF-PROF-03: Profile creation with resume upload

  wf_job_search:
    description: Job search and filtering
    pages: [page_job_search, page_job_detail]

    scenarios:
      - TS-WF-SEARCH-01: Search by keyword
      - TS-WF-SEARCH-02: Filter by location
      - TS-WF-SEARCH-03: Filter by experience level
      - TS-WF-SEARCH-04: Save job to favorites
```

---

## 4. Test Cases

### 4.1 Definition

**ISTQB Glossary Definition**:
> Test Case: A set of preconditions, inputs, actions (where applicable), expected results and postconditions, developed based on test conditions.

**ISO/IEC/IEEE 29119-1 Definition**:
> Test Case: A set of test case preconditions, inputs (including actions, where applicable), and expected results, developed to drive the execution of a test item to meet test objectives, including correct implementation, error identification, checking quality, and other valued information.

A test case is a detailed, step-by-step specification of how to test a specific aspect of the system.

**Characteristics**:
- Detailed and specific
- Includes preconditions, steps, test data, expected results
- Executable (manual or automated)
- Repeatable
- One clear objective per test case

### 4.2 Differences from Test Scenarios

| Aspect | Test Scenario | Test Case |
|--------|---------------|-----------|
| **Abstraction** | High-level | Low-level, detailed |
| **Focus** | What to test | How to test |
| **Steps** | General description | Specific step-by-step |
| **Test Data** | Not specified | Specific test data |
| **Granularity** | One scenario = many test cases | One test case = one test |
| **Audience** | Stakeholders, testers | Testers, automation |
| **Example** | "Submit application" | "Submit application with valid data" |

**Relationship**:
- 1 Test Scenario → Multiple Test Cases
- Test Scenario = "What to test"
- Test Case = "How to test it"

### 4.3 Structure and Components

#### Essential Components

1. **Test Case ID**: Unique identifier
2. **Test Case Name**: Descriptive title
3. **Test Scenario**: Link to parent scenario
4. **Preconditions**: State before test execution
5. **Test Steps**: Detailed actions
6. **Test Data**: Specific input values
7. **Expected Results**: What should happen
8. **Actual Results**: What actually happened (filled during execution)
9. **Status**: Pass/Fail/Blocked/Not Run
10. **Priority**: Critical/High/Medium/Low
11. **Postconditions**: State after test execution

#### Optional Components

- **Test Type**: Functional, Performance, Security, etc.
- **Test Level**: Unit, Integration, System, Acceptance
- **Test Technique**: BVA, EP, State Transition, etc.
- **Automation Status**: Automated/Manual/To Be Automated
- **References**: Requirements, user stories, defects
- **Estimated Time**: Expected execution duration
- **Environment**: Specific environment requirements
- **Tags/Labels**: For organization and filtering

### 4.4 Test Case Example (Traditional Format)

```yaml
test_case_traditional:
  test_case_id: TC-APP-102-01
  test_case_name: Submit Application with Valid Data
  test_scenario: TS-102-01 Successful Application Submission

  references:
    requirement: REQ-APP-001
    user_story: US-102
    bounded_context: bc_applications
    aggregate: agg_application
    workflow: wf_submit_application

  metadata:
    priority: Critical
    test_type: Functional
    test_level: System
    test_technique: Use Case Testing
    automation_status: Automated
    estimated_time: 5 minutes
    author: QA Engineer
    created_date: 2025-01-15

  preconditions:
    - User is logged in as candidate "john.doe@example.com"
    - Candidate profile is complete (>80% completion)
    - Job posting "Software Engineer" (id: job-123) exists and is active
    - User has NOT already applied for this job posting

  test_steps:
    - step: 1
      action: Navigate to job posting detail page for job-123
      expected_result: Job posting details displayed, "Apply Now" button visible and enabled

    - step: 2
      action: Click "Apply Now" button
      expected_result: Application wizard opens, Step 1 "Review Profile" displayed

    - step: 3
      action: Review profile information, click "Next"
      expected_result: Step 2 "Cover Letter and Resume" displayed

    - step: 4
      action: |
        Enter cover letter: "I am excited to apply for this position..."
        Upload resume file: "john_doe_resume.pdf"
        Click "Next"
      expected_result: Step 3 "Review and Submit" displayed with summary

    - step: 5
      action: Review application summary, click "Submit Application"
      expected_result: |
        - Confirmation page displayed with message "Application submitted successfully"
        - Application ID displayed (e.g., "APP-2025-001")
        - Application status is "Submitted"
        - "View My Applications" button visible

    - step: 6
      action: Navigate to "My Applications" page
      expected_result: |
        - New application appears in the list
        - Status shows "Submitted"
        - Job title is "Software Engineer"
        - Submitted date is today's date

  test_data:
    user:
      email: john.doe@example.com
      password: Test@1234
      profile_id: cand-001

    job_posting:
      id: job-123
      title: Software Engineer
      company: Tech Corp
      status: active

    cover_letter: |
      I am excited to apply for the Software Engineer position at Tech Corp.
      With 5 years of experience in full-stack development, I believe I am
      well-suited for this role.

    resume_file:
      name: john_doe_resume.pdf
      path: test/fixtures/resumes/john_doe_resume.pdf
      size: 245 KB

  postconditions:
    - agg_application created with status "Submitted"
    - evt_application_submitted event published
    - Confirmation email sent to john.doe@example.com
    - Application visible in candidate's application list
    - Application count for job-123 incremented by 1

  # Filled during execution
  actual_results: (To be filled by tester)
  status: Not Run
  executed_by: null
  execution_date: null
  notes: null
```

### 4.5 Test Case Example (BDD Format - Automated)

```gherkin
Feature: Application Submission
  As a job seeker
  I want to submit applications for job postings
  So that I can be considered for positions

  Background:
    Given I am logged in as candidate "john.doe@example.com"
    And my profile is complete
    And job posting "Software Engineer" with id "job-123" exists

  @critical @automated
  Scenario: Submit application with valid data
    Given I have not applied for job "job-123"
    When I navigate to job posting "job-123"
    Then I should see the "Apply Now" button enabled

    When I click "Apply Now"
    Then I should see the application wizard step "Review Profile"

    When I click "Next"
    Then I should see the application wizard step "Cover Letter and Resume"

    When I enter cover letter "I am excited to apply for this position..."
    And I upload resume file "john_doe_resume.pdf"
    And I click "Next"
    Then I should see the application wizard step "Review and Submit"
    And I should see profile information summary
    And I should see cover letter preview
    And I should see resume file name "john_doe_resume.pdf"

    When I click "Submit Application"
    Then I should see confirmation message "Application submitted successfully"
    And I should see application ID
    And the evt_application_submitted event should be published with:
      | candidate_id | job_posting_id | status    |
      | cand-001     | job-123        | Submitted |

    When I navigate to "My Applications"
    Then I should see application for "Software Engineer" with status "Submitted"
    And the application submitted date should be today

  @high @automated
  Scenario: Submit application with missing required fields
    Given I have not applied for job "job-123"
    When I navigate to job posting "job-123"
    And I click "Apply Now"
    And I navigate through steps without filling required fields
    And I click "Submit Application"
    Then I should see validation error "Cover letter is required"
    And I should see validation error "Resume is required"
    And the application should NOT be submitted
    And no evt_application_submitted event should be published

  @high @automated
  Scenario: Attempt to apply for already applied job
    Given I have already submitted application for job "job-123"
    When I navigate to job posting "job-123"
    Then I should see message "You have already applied for this position"
    And the "Apply Now" button should be disabled
```

### 4.6 Test Case Design Best Practices

#### 1. Follow the Single Responsibility Principle
Each test case should test ONE thing:

**Good**:
```yaml
- TC-001: Submit application with valid data
- TC-002: Submit application with missing cover letter
- TC-003: Submit application with invalid resume format
```

**Bad**:
```yaml
- TC-001: Submit application and track status and withdraw
  (Tests multiple features - should be separate test cases)
```

#### 2. Use Clear, Descriptive Names
Test case name should clearly state what is being tested:

**Good**:
- "Submit application with all required fields populated"
- "Verify validation error when email is missing"
- "Verify application status transitions from Draft to Submitted"

**Bad**:
- "Test 1"
- "Check application"
- "Validation test"

#### 3. Make Tests Independent
Each test should run independently without relying on others:

**Good**:
```yaml
test_case_1:
  preconditions:
    - User logged in
    - Fresh test data created
  cleanup:
    - Delete test data after execution

test_case_2:
  preconditions:
    - User logged in
    - Fresh test data created
  cleanup:
    - Delete test data after execution
```

**Bad**:
```yaml
test_case_1:
  action: Create application (app-001)

test_case_2:
  precondition: Test case 1 has run
  action: Update application (app-001)
```

#### 4. Use Equivalence Partitioning and Boundary Value Analysis

**Equivalence Partitioning Example (vo_experience_years)**:
```yaml
test_cases_experience_years:
  valid_partitions:
    - TC-EXP-001: Experience = 0 years (entry level)
    - TC-EXP-002: Experience = 3 years (mid-level)
    - TC-EXP-003: Experience = 10 years (senior)

  invalid_partitions:
    - TC-EXP-004: Experience = -1 (negative, invalid)
    - TC-EXP-005: Experience = "abc" (non-numeric, invalid)
    - TC-EXP-006: Experience = 101 (unrealistic, invalid)
```

**Boundary Value Analysis Example (file upload size)**:
```yaml
test_cases_file_upload_bva:
  boundary: Maximum file size = 5 MB = 5242880 bytes

  test_cases:
    - TC-FILE-001: Upload file with size = 0 bytes (minimum - 1, invalid)
    - TC-FILE-002: Upload file with size = 1 byte (minimum, valid)
    - TC-FILE-003: Upload file with size = 2621440 bytes (typical, valid)
    - TC-FILE-004: Upload file with size = 5242880 bytes (maximum, valid)
    - TC-FILE-005: Upload file with size = 5242881 bytes (maximum + 1, invalid)
```

#### 5. Cover Positive and Negative Scenarios

**Positive (Happy Path)**:
```yaml
- TC-POS-001: Submit application with valid data → Success
- TC-POS-002: Update profile with valid email → Updated
```

**Negative (Error Cases)**:
```yaml
- TC-NEG-001: Submit application without cover letter → Validation error
- TC-NEG-002: Update profile with invalid email format → Error message
- TC-NEG-003: Submit application for closed job posting → Error "Job posting no longer accepting applications"
```

#### 6. Use Decision Tables for Complex Logic

**Example: Application Eligibility Rules**
```yaml
decision_table_application_eligibility:
  conditions:
    - User authenticated: [Y, Y, Y, Y, N, N, N, N]
    - Profile complete: [Y, Y, N, N, Y, Y, N, N]
    - Job active: [Y, N, Y, N, Y, N, Y, N]

  actions:
    - Allow application: [Y, N, N, N, N, N, N, N]
    - Show error: [N, Y, Y, Y, Y, Y, Y, Y]
    - Error message:
        - null
        - "Job posting is no longer active"
        - "Please complete your profile first"
        - "Job posting is no longer active"
        - "Please log in to apply"
        - "Please log in to apply"
        - "Please log in to apply"
        - "Please log in to apply"

test_cases_from_decision_table:
  - TC-DT-001: [Y, Y, Y] → Allow application
  - TC-DT-002: [Y, Y, N] → Show error "Job posting is no longer active"
  - TC-DT-003: [Y, N, Y] → Show error "Please complete your profile first"
  - TC-DT-004: [Y, N, N] → Show error "Job posting is no longer active"
  - TC-DT-005: [N, Y, Y] → Show error "Please log in to apply"
  - TC-DT-006: [N, Y, N] → Show error "Please log in to apply"
  - TC-DT-007: [N, N, Y] → Show error "Please log in to apply"
  - TC-DT-008: [N, N, N] → Show error "Please log in to apply"
```

#### 7. Use State Transition Testing for State Machines

**Application Status State Machine**:
```yaml
state_transition_application_status:
  states: [Draft, Submitted, Under Review, Interviewing, Offered, Accepted, Rejected, Withdrawn]

  valid_transitions:
    Draft:
      - [submit] → Submitted
      - [delete] → (deleted)

    Submitted:
      - [review] → Under Review
      - [withdraw] → Withdrawn

    Under Review:
      - [schedule_interview] → Interviewing
      - [reject] → Rejected

    Interviewing:
      - [make_offer] → Offered
      - [reject] → Rejected

    Offered:
      - [accept] → Accepted
      - [reject] → Rejected

    Accepted: (terminal state)
    Rejected: (terminal state)
    Withdrawn: (terminal state)

  test_cases_valid_transitions:
    - TC-ST-001: Draft → submit → Submitted
    - TC-ST-002: Submitted → review → Under Review
    - TC-ST-003: Under Review → schedule_interview → Interviewing
    - TC-ST-004: Interviewing → make_offer → Offered
    - TC-ST-005: Offered → accept → Accepted

  test_cases_invalid_transitions:
    - TC-ST-INV-001: Draft → make_offer → (should fail)
    - TC-ST-INV-002: Accepted → withdraw → (should fail, terminal state)
    - TC-ST-INV-003: Rejected → review → (should fail, terminal state)
```

### 4.7 Test Case Management

#### Organization Strategies

**By Bounded Context**:
```
test-cases/
  bc_profile/
    value-objects/
      tc_vo_email_validation.yaml
      tc_vo_skills_validation.yaml
    aggregates/
      tc_agg_candidate_profile.yaml
  bc_applications/
    value-objects/
      tc_vo_application_status.yaml
    aggregates/
      tc_agg_application.yaml
    workflows/
      tc_wf_submit_application.yaml
```

**By Test Level**:
```
test-cases/
  unit/
    domain/
      tc_unit_vo_email.yaml
      tc_unit_ent_candidate.yaml
    ui/
      tc_unit_atom_button.yaml
  integration/
    tc_integration_agg_application.yaml
    tc_integration_cross_context.yaml
  e2e/
    tc_e2e_submit_application.yaml
```

**By Feature/Epic**:
```
test-cases/
  epic_application_tracking/
    tc_submit_application_*.yaml
    tc_track_application_*.yaml
    tc_withdraw_application_*.yaml
```

#### Traceability Matrix

```yaml
traceability_matrix:
  requirement: REQ-APP-001
  title: Submit Application

  user_stories:
    - US-102: Submit application for job posting

  test_scenarios:
    - TS-102-01: Successful application submission
    - TS-102-02: Application validation errors
    - TS-102-03: Duplicate application prevention

  test_cases:
    TS-102-01:
      - TC-102-01-01: Submit with valid data
      - TC-102-01-02: Submit with optional fields
      - TC-102-01-03: Verify event publication
      status: Passed
      coverage: 100%

    TS-102-02:
      - TC-102-02-01: Missing cover letter
      - TC-102-02-02: Missing resume
      - TC-102-02-03: Invalid file format
      status: Passed
      coverage: 100%

    TS-102-03:
      - TC-102-03-01: Apply twice for same job
      - TC-102-03-02: Verify Apply button disabled
      status: Passed
      coverage: 100%

  defects:
    - DEF-123: Validation error message not displayed (fixed)
    - DEF-456: Event not published on submit (fixed)

  overall_coverage: 100%
  status: Verified
```

### 4.8 Reusability and Maintainability

#### Reusable Test Steps
```yaml
reusable_steps:
  login_as_candidate:
    steps:
      - Navigate to login page
      - Enter email: {email}
      - Enter password: {password}
      - Click "Login" button
    expected: Dashboard displayed, welcome message shown

  navigate_to_job_posting:
    steps:
      - Navigate to job search page
      - Search for job: {job_title}
      - Click on job posting: {job_id}
    expected: Job posting detail page displayed

  submit_application:
    steps:
      - Click "Apply Now"
      - Complete application wizard
      - Click "Submit Application"
    expected: Confirmation page displayed

# Test case using reusable steps
test_case_using_reusable_steps:
  test_case_id: TC-APP-103
  name: Submit application as returning user

  steps:
    - use: login_as_candidate
      params:
        email: john.doe@example.com
        password: Test@1234

    - use: navigate_to_job_posting
      params:
        job_title: Software Engineer
        job_id: job-123

    - use: submit_application

    - step: Verify application submitted
      expected: Application appears in "My Applications" with status "Submitted"
```

#### Parameterized Test Cases (Data-Driven)
```yaml
parameterized_test_case:
  test_case_id: TC-VAL-EMAIL
  name: Email validation

  test_steps:
    - Enter email: {email}
    - Click "Save"
    - Verify result: {expected_result}

  test_data:
    - email: valid@example.com
      expected_result: Accepted

    - email: invalid@
      expected_result: Error "Invalid email format"

    - email: "@example.com"
      expected_result: Error "Invalid email format"

    - email: "no-at-sign.com"
      expected_result: Error "Invalid email format"

    - email: "valid.email+tag@example.co.uk"
      expected_result: Accepted
```

### 4.9 DDD Integration Example

```yaml
test_case_ddd_integration:
  test_case_id: TC-AGG-APP-001
  test_case_name: Create Application Aggregate with Valid Data

  # DDD References
  bounded_context_ref: bc_applications
  aggregate_ref: agg_application
  aggregate_root: Application

  value_objects_under_test:
    - vo_application_status
    - vo_submission_date
    - vo_cover_letter

  entities_under_test:
    - ent_application

  domain_events_expected:
    - evt_application_created
    - evt_application_submitted

  # Test metadata
  test_level: Integration
  test_type: Functional
  test_technique: Aggregate Testing

  preconditions:
    - Candidate profile exists (agg_candidate_profile, id: cand-001)
    - Job posting exists (agg_job_posting, id: job-123)
    - No existing application for this candidate-job pair

  test_steps:
    - step: 1
      action: Create agg_application with valid data
      code: |
        const application = Application.create({
          candidateId: new CandidateId('cand-001'),
          jobPostingId: new JobPostingId('job-123'),
          coverLetter: new CoverLetter('I am excited to apply...'),
          resumeUrl: new ResumeUrl('https://storage/resumes/cand-001.pdf')
        });
      expected_result: |
        - agg_application created with status Draft
        - evt_application_created event published
        - Application has unique ApplicationId

    - step: 2
      action: Submit application
      code: |
        application.submit();
      expected_result: |
        - Application status transitions to Submitted
        - vo_submission_date set to current date/time
        - evt_application_submitted event published

    - step: 3
      action: Verify aggregate invariants
      code: |
        expect(application.status).toBe(ApplicationStatus.Submitted);
        expect(application.submissionDate).toBeDefined();
        expect(application.isSubmitted()).toBe(true);
      expected_result: All assertions pass

  postconditions:
    - agg_application persisted in database
    - Domain events stored in event store
    - Application visible to candidate

  invariants_validated:
    - Application must have candidateId
    - Application must have jobPostingId
    - Application cannot be submitted twice
    - Submitted application has submissionDate
```

### 4.10 UX Integration Example

```yaml
test_case_ux_integration:
  test_case_id: TC-UI-WF-APP-001
  test_case_name: Submit Application Workflow - Happy Path

  # UX References
  workflow_ref: wf_submit_application
  pages_under_test:
    - page_job_detail
    - page_application_wizard
    - page_application_confirmation

  components_under_test:
    - comp_application_step1_profile_review
    - comp_application_step2_documents
    - comp_application_step3_review
    - comp_submit_button

  # Test metadata
  test_level: E2E
  test_type: Functional, Usability
  automation: Playwright

  preconditions:
    - User logged in
    - User on page_job_detail for job-123

  test_steps:
    - step: 1
      action: Click "Apply Now" button (comp_apply_button)
      playwright_code: |
        await page.locator('[data-testid="apply-now-button"]').click();
      expected_result: |
        - page_application_wizard opens
        - URL is /jobs/job-123/apply
        - Step 1 "Review Profile" is active
        - Progress indicator shows "Step 1 of 3"

    - step: 2
      action: Review profile information, click "Next"
      playwright_code: |
        await expect(page.locator('[data-testid="profile-summary"]')).toBeVisible();
        await page.locator('[data-testid="next-button"]').click();
      expected_result: |
        - Step 2 "Cover Letter and Resume" displayed
        - Progress indicator shows "Step 2 of 3"
        - comp_cover_letter_editor visible
        - comp_resume_upload visible

    - step: 3
      action: Enter cover letter and upload resume
      playwright_code: |
        await page.locator('[data-testid="cover-letter-editor"]').fill('I am excited to apply...');
        await page.locator('[data-testid="resume-upload"]').setInputFiles('test/fixtures/resume.pdf');
        await page.locator('[data-testid="next-button"]').click();
      expected_result: |
        - Step 3 "Review and Submit" displayed
        - Progress indicator shows "Step 3 of 3"
        - Application summary visible with all entered data
        - comp_submit_button enabled

    - step: 4
      action: Click "Submit Application"
      playwright_code: |
        await page.locator('[data-testid="submit-button"]').click();
      expected_result: |
        - page_application_confirmation displayed
        - Confirmation message "Application submitted successfully" visible
        - Application ID displayed
        - "View My Applications" button visible

    - step: 5
      action: Verify accessibility (WCAG 2.2 AA)
      playwright_code: |
        const accessibilityScanResults = await new AxeBuilder({ page }).analyze();
        expect(accessibilityScanResults.violations).toEqual([]);
      expected_result: Zero accessibility violations

  postconditions:
    - Application submitted
    - User on page_application_confirmation
    - Application visible in candidate's application list

  usability_criteria:
    - Workflow completion time <10 minutes (first-time user)
    - All steps clearly labeled and explained
    - Validation errors displayed inline
    - Progress indicator always visible
    - "Save Draft" option available at each step
```

---

## 5. Test Execution and Results

### 5.1 Test Execution Process

**ISTQB Test Execution Activities**:
1. Prepare test environment
2. Prepare test data
3. Execute test cases
4. Compare actual results with expected results
5. Log test results
6. Report defects
7. Record test execution status

### 5.2 Test Execution Workflow

```yaml
test_execution_workflow:
  pre_execution:
    - Verify test environment ready
      checklist:
        - Database accessible and seeded with test data
        - Application deployed and running
        - Test tools configured (Jest, Playwright)
        - Browser/device matrix available

    - Review test cases to execute
      - Test suite: bc_applications_integration_tests
      - Total test cases: 85
      - Estimated time: 2 hours

    - Prepare test data
      - Generate candidate profiles (10 test users)
      - Create job postings (20 test jobs)
      - Clear existing applications

  execution:
    automated_tests:
      - Run unit tests (npm run test:unit)
        duration: 5 minutes
        coverage: domain layer

      - Run integration tests (npm run test:integration)
        duration: 15 minutes
        coverage: application layer

      - Run E2E tests (npm run test:e2e)
        duration: 30 minutes
        coverage: critical workflows

    manual_tests:
      - Exploratory testing (application wizard)
        duration: 1 hour
        focus: Edge cases, usability issues

      - Accessibility testing (WCAG 2.2 AA)
        duration: 1 hour
        tools: NVDA, JAWS, axe DevTools

      - Usability testing (5 test users)
        duration: 3 hours
        focus: User experience, task completion

  post_execution:
    - Review test results
      - Pass rate: 95%
      - Failed tests: 4
      - Blocked tests: 1

    - Log defects for failed tests
      - DEF-789: Application submission fails with special characters
      - DEF-790: Auto-save not triggering after 30 seconds
      - DEF-791: Accessibility: Missing ARIA labels
      - DEF-792: Validation error not cleared after correction

    - Update test execution report
      - Generate HTML report (Allure)
      - Update metrics dashboard
      - Notify team via Slack

    - Cleanup
      - Delete test data
      - Reset test environment
      - Archive test logs
```

### 5.3 Test Result Documentation

#### Test Result Components

```yaml
test_result:
  test_case_id: TC-APP-102-01
  test_case_name: Submit Application with Valid Data

  execution_metadata:
    executed_by: QA Engineer (John Smith)
    execution_date: 2025-01-20T14:30:00Z
    execution_environment: Staging
    build_version: 1.2.3
    execution_type: Automated (Playwright)

  status: Passed

  actual_results:
    step_1:
      action: Navigate to job posting detail
      actual: Job posting displayed correctly, "Apply Now" button enabled
      status: Passed

    step_2:
      action: Click "Apply Now"
      actual: Application wizard opened, Step 1 displayed
      status: Passed

    step_3:
      action: Complete wizard and submit
      actual: Application submitted successfully, confirmation displayed
      status: Passed

    step_4:
      action: Verify in application list
      actual: Application visible with status "Submitted"
      status: Passed

  execution_time: 45 seconds

  screenshots:
    - step_2_wizard_opened.png
    - step_4_confirmation.png

  video_recording: test_executions/TC-APP-102-01_2025-01-20.mp4

  logs:
    - console.log (browser console output)
    - network.log (network requests)

  notes: "All steps executed successfully. No issues found."
```

#### Failed Test Result Example

```yaml
test_result_failed:
  test_case_id: TC-APP-102-05
  test_case_name: Submit Application with Special Characters in Cover Letter

  status: Failed

  actual_results:
    step_1:
      action: Navigate to job posting
      actual: Job posting displayed
      status: Passed

    step_2:
      action: Enter cover letter with special characters (é, ñ, ü)
      actual: Characters not displayed correctly (mojibake)
      expected: Characters displayed correctly with UTF-8 encoding
      status: Failed

    step_3:
      action: Submit application
      actual: Application submitted, but cover letter corrupted in database
      status: Failed

  defect_logged:
    defect_id: DEF-789
    title: Special characters not handled correctly in cover letter
    severity: High
    priority: High
    description: |
      When submitting an application with special characters (é, ñ, ü) in the
      cover letter, the characters are not stored correctly in the database.
      UTF-8 encoding issue suspected.

  screenshots:
    - step_2_character_corruption.png
    - database_query_result.png

  notes: |
    Issue occurs with non-ASCII characters. ASCII characters work fine.
    Suspect database column charset is not UTF-8.
```

### 5.4 Pass/Fail Criteria

#### Test Case Level

**Pass Criteria**:
- All steps executed successfully
- Actual results match expected results
- No unexpected errors or exceptions
- Performance within acceptable limits
- No accessibility violations (for UI tests)

**Fail Criteria**:
- Any step produces unexpected result
- Actual result differs from expected result
- Exceptions or errors thrown
- Performance degradation beyond threshold
- Accessibility violations found

#### Test Suite Level

**Pass Criteria**:
```yaml
test_suite_pass_criteria:
  unit_tests:
    pass_rate: ">=95%"
    coverage: ">=90% (domain layer)"
    execution_time: "<=5 minutes"
    critical_failures: 0

  integration_tests:
    pass_rate: ">=90%"
    coverage: ">=85% (application layer)"
    execution_time: "<=15 minutes"
    critical_failures: 0

  e2e_tests:
    pass_rate: ">=95%"
    critical_path_coverage: "100%"
    execution_time: "<=30 minutes"
    critical_failures: 0

  accessibility_tests:
    critical_violations: 0
    serious_violations: 0
    moderate_violations: "<=5"
    wcag_level: "AA"
```

**Fail Criteria**:
- Pass rate below threshold
- Any critical test failure
- Coverage below threshold
- Execution time exceeds limit
- Critical/serious accessibility violations

### 5.5 Actual vs Expected Results

#### Comparison Strategies

**Exact Match**:
```yaml
expected: "Application submitted successfully"
actual: "Application submitted successfully"
result: Pass
```

**Pattern Match**:
```yaml
expected: "Application ID: APP-XXXX-YYY"
actual: "Application ID: APP-2025-001"
result: Pass (matches pattern)
```

**Range Match**:
```yaml
expected: Response time < 200ms
actual: Response time 185ms
result: Pass
```

**Structural Match**:
```yaml
expected:
  status: "Submitted"
  candidateId: "cand-001"
  jobPostingId: "job-123"

actual:
  status: "Submitted"
  candidateId: "cand-001"
  jobPostingId: "job-123"
  submissionDate: "2025-01-20T14:30:00Z"

result: Pass (all expected fields match, additional fields acceptable)
```

### 5.6 Test Execution Logs

#### Automated Test Execution Log (Jest)

```
PASS  src/domain/applications/aggregates/__tests__/Application.test.ts
  Application Aggregate
    ✓ should create application in Draft status (5 ms)
    ✓ should submit application and transition to Submitted (3 ms)
    ✓ should publish evt_application_submitted when submitted (4 ms)
    ✓ should not allow submitting twice (2 ms)
    ✓ should allow withdrawing submitted application (3 ms)
    ✓ should not allow withdrawing accepted application (2 ms)

Test Suites: 1 passed, 1 total
Tests:       6 passed, 6 total
Snapshots:   0 total
Time:        1.234 s
Coverage:    92.5% (Statements 185/200)
```

#### E2E Test Execution Log (Playwright)

```
Running 10 tests using 4 workers

  ✓ [chromium] › submit-application.spec.ts:12:5 › Submit application with valid data (5.2s)
  ✓ [firefox] › submit-application.spec.ts:12:5 › Submit application with valid data (6.1s)
  ✓ [webkit] › submit-application.spec.ts:12:5 › Submit application with valid data (5.8s)
  ✓ [chromium] › submit-application.spec.ts:45:5 › Submit application with missing fields (3.2s)
  ✓ [chromium] › submit-application.spec.ts:78:5 › Application auto-save (8.5s)
  ✓ [chromium] › submit-application.spec.ts:102:5 › Prevent duplicate application (2.8s)
  ✗ [chromium] › submit-application.spec.ts:125:5 › Submit with special characters (4.1s)

    Error: expect(received).toBe(expected)

    Expected: "I am excited to apply for this position with résumé..."
    Received: "I am excited to apply for this position with r�sum�..."

    Screenshot: test-results/submit-special-chars-chromium/screenshot-failure.png

  10 passed, 1 failed (35.2s)
```

### 5.7 CI/CD Integration

```yaml
ci_cd_test_execution:
  pipeline: GitHub Actions

  stages:
    commit_stage:
      trigger: Every commit to feature branch
      tests:
        - Unit tests (Jest)
        - Linting (ESLint)
        - Type checking (TypeScript)
      duration: <5 minutes
      failure_action: Block PR, notify developer

    pr_stage:
      trigger: Pull request created/updated
      tests:
        - Integration tests
        - Component tests (React Testing Library)
        - Code coverage check (>80%)
        - SonarQube quality gate
      duration: <15 minutes
      failure_action: Block PR merge

    merge_stage:
      trigger: Merge to main branch
      tests:
        - E2E tests (critical paths)
        - Accessibility tests (axe-core)
        - Performance tests (Lighthouse)
      duration: <30 minutes
      failure_action: Rollback merge, create incident

    deployment_stage:
      trigger: Deploy to staging/production
      tests:
        - Smoke tests
        - Synthetic monitoring
        - Health checks
      duration: <5 minutes
      failure_action: Rollback deployment

  test_results_reporting:
    - GitHub Actions summary
    - Allure report (HTML)
    - SonarQube dashboard
    - Slack notification
    - Email to QA team
```

---

## 6. Test Reports

### 6.1 Test Summary Reports

**ISTQB Definition**:
> Test Summary Report: A document summarizing testing activities and results. It also contains an evaluation of the corresponding test items against exit criteria.

#### Purpose
- Communicate test results to stakeholders
- Provide metrics and quality assessment
- Support go/no-go decision for release
- Document lessons learned

#### Audience
- Product Owner
- Project Manager
- Development Lead
- QA Manager
- Executives (high-level summary)

### 6.2 Test Summary Report Structure (IEEE 829)

```yaml
test_summary_report:
  report_id: TSR-BC-APP-2025-Q1
  title: Test Summary Report - Application Tracking System (bc_applications)
  reporting_period: 2025-01-01 to 2025-01-31
  prepared_by: QA Lead
  date: 2025-02-01

  # 1. Executive Summary
  executive_summary: |
    Testing for the Application Tracking System (bc_applications bounded context) was
    completed on January 31, 2025. All critical and high-priority test cases passed
    successfully. Overall test pass rate: 96.5% (165 passed, 6 failed, 0 blocked).

    Key findings:
    - All critical workflows (submit application, track status) working correctly
    - 4 medium-severity defects found and fixed
    - 2 low-severity defects deferred to next sprint
    - WCAG 2.2 Level AA compliance: 100% (zero violations)
    - Performance targets met: API <200ms, page load <1s

    Recommendation: APPROVE for production release.

  # 2. Test Scope
  test_scope:
    in_scope:
      - agg_application aggregate
      - svc_app_submit_application use case
      - svc_app_track_application use case
      - page_application_wizard UI
      - wf_submit_application workflow
      - Cross-context integration

    out_of_scope:
      - bc_matching algorithm (separate release)
      - Admin panel features

  # 3. Test Approach
  test_approach:
    test_levels:
      - Unit: 95% coverage (domain layer)
      - Integration: 88% coverage (application layer)
      - E2E: 100% critical paths

    test_types:
      - Functional
      - Performance
      - Security
      - Usability
      - Accessibility (WCAG 2.2 AA)

  # 4. Test Metrics
  test_metrics:
    test_execution_summary:
      total_test_cases: 171
      executed: 171
      passed: 165
      failed: 6
      blocked: 0
      not_run: 0
      pass_rate: 96.5%

    coverage:
      code_coverage:
        domain_layer: 95.2%
        application_layer: 88.3%
        ui_layer: 72.1%
        overall: 85.2%

      requirements_coverage:
        total_requirements: 25
        covered: 25
        coverage_percentage: 100%

    defect_metrics:
      total_defects: 8
      critical: 0
      high: 2 (fixed)
      medium: 4 (2 fixed, 2 deferred)
      low: 2 (deferred)
      defect_density: 0.32 defects/KLOC
      defect_removal_efficiency: 75% (6 fixed, 2 deferred)

    test_execution_time:
      unit_tests: 4.5 minutes
      integration_tests: 12.3 minutes
      e2e_tests: 28.7 minutes
      total_automated: 45.5 minutes
      manual_exploratory: 8 hours
      manual_accessibility: 6 hours

  # 5. Test Results by Priority
  test_results_by_priority:
    critical:
      total: 35
      passed: 35
      failed: 0
      pass_rate: 100%

    high:
      total: 60
      passed: 58
      failed: 2
      pass_rate: 96.7%

    medium:
      total: 50
      passed: 48
      failed: 2
      pass_rate: 96%

    low:
      total: 26
      passed: 24
      failed: 2
      pass_rate: 92.3%

  # 6. Test Results by Type
  test_results_by_type:
    functional:
      total: 120
      passed: 116
      failed: 4
      pass_rate: 96.7%

    performance:
      total: 15
      passed: 15
      failed: 0
      pass_rate: 100%
      notes: All APIs <200ms, page load <1s

    security:
      total: 10
      passed: 10
      failed: 0
      pass_rate: 100%
      notes: No OWASP Top 10 vulnerabilities found

    accessibility:
      total: 20
      passed: 20
      failed: 0
      pass_rate: 100%
      notes: WCAG 2.2 Level AA - zero violations

    usability:
      total: 6
      passed: 4
      failed: 2
      pass_rate: 66.7%
      notes: 2 usability improvements recommended

  # 7. Defect Summary
  defects_summary:
    - defect_id: DEF-789
      title: Special characters not handled in cover letter
      severity: High
      status: Fixed
      test_case: TC-APP-102-05

    - defect_id: DEF-790
      title: Auto-save delay inconsistent
      severity: Medium
      status: Fixed
      test_case: TC-APP-103-02

    - defect_id: DEF-791
      title: Missing ARIA label on submit button
      severity: High
      status: Fixed
      test_case: TC-ACC-001-05

    - defect_id: DEF-792
      title: Validation error not cleared after correction
      severity: Medium
      status: Fixed
      test_case: TC-VAL-003-02

    - defect_id: DEF-793
      title: Application list pagination slow with >100 items
      severity: Medium
      status: Deferred to Sprint 3
      test_case: TC-PERF-002-01

    - defect_id: DEF-794
      title: Cover letter character count off by 1
      severity: Low
      status: Deferred to Sprint 3
      test_case: TC-UI-005-03

  # 8. Risk Assessment
  risks:
    - risk: Cross-context integration complexity
      status: Mitigated
      notes: Integration test suite verified all cross-context calls

    - risk: Performance degradation with high data volume
      status: Monitored
      notes: Performance tests passed, but monitor in production

  # 9. Exit Criteria Assessment
  exit_criteria_assessment:
    - criteria: All critical test cases pass
      status: Met
      actual: 35/35 passed

    - criteria: Pass rate >90%
      status: Met
      actual: 96.5%

    - criteria: Zero critical/high defects open
      status: Met
      actual: 0 critical, 0 high defects open

    - criteria: Code coverage >80%
      status: Met
      actual: 85.2%

    - criteria: WCAG 2.2 AA compliance 100%
      status: Met
      actual: 100%, zero violations

  # 10. Recommendations
  recommendations:
    release_recommendation: APPROVE for production release

    conditions:
      - Monitor performance in production (pagination with high data volumes)
      - Address deferred medium/low defects in Sprint 3
      - Continue accessibility audits quarterly

    improvements:
      - Increase UI layer test coverage to >80%
      - Automate more usability test scenarios
      - Add performance regression tests to CI/CD

  # 11. Lessons Learned
  lessons_learned:
    what_went_well:
      - Early TDD for Value Objects prevented many defects
      - BDD scenarios improved communication with Product Owner
      - Accessibility-first approach resulted in zero violations
      - CI/CD integration provided fast feedback

    areas_for_improvement:
      - Need more exploratory testing earlier in sprint
      - Performance testing should start earlier
      - Test data management could be automated further

    action_items:
      - Schedule exploratory testing sessions mid-sprint
      - Include performance tests in acceptance stage of CI/CD
      - Investigate test data generation tools
```

### 6.3 Defect Reports

**ISTQB Definition**:
> Defect Report: Documentation of the occurrence, nature, and status of a defect (also known as incident report, bug report, or problem report).

#### Defect Report Template

```yaml
defect_report:
  defect_id: DEF-789
  title: Special Characters Not Handled Correctly in Cover Letter

  # Classification
  severity: High
    # Critical: System crash, data loss, security breach
    # High: Major feature not working, workaround exists
    # Medium: Feature partially working, minor impact
    # Low: Cosmetic, minor inconvenience

  priority: High
    # Critical: Fix immediately, block release
    # High: Fix before release
    # Medium: Fix in next release
    # Low: Fix when time permits

  status: Fixed
    # New → Assigned → In Progress → Fixed → Verified → Closed
    # Or: New → Rejected/Duplicate/Won't Fix

  # Identification
  reported_by: QA Engineer (John Smith)
  reported_date: 2025-01-22T10:15:00Z
  assigned_to: Developer (Jane Doe)

  # Context
  test_case_ref: TC-APP-102-05
  environment: Staging
  build_version: 1.2.3
  browser: Chrome 120.0.6099.109
  operating_system: Windows 11

  # DDD/UX References
  bounded_context: bc_applications
  aggregate: agg_application
  value_object: vo_cover_letter
  component: comp_cover_letter_editor
  page: page_application_wizard

  # Description
  summary: |
    When submitting an application with special characters (é, ñ, ü, ç, etc.) in the
    cover letter field, the characters are not stored or displayed correctly. They
    appear as mojibake (� or other incorrect characters).

  steps_to_reproduce:
    - step: 1
      action: Log in as candidate
    - step: 2
      action: Navigate to job posting and click "Apply Now"
    - step: 3
      action: In application wizard Step 2, enter cover letter with text "Résumé for café position"
    - step: 4
      action: Complete wizard and submit application
    - step: 5
      action: View submitted application or check database

  expected_result: |
    Cover letter displays correctly: "Résumé for café position"
    Characters é and é are preserved.

  actual_result: |
    Cover letter displays incorrectly: "R�sum� for caf� position"
    Special characters replaced with � (replacement character).

  screenshots:
    - defect-789-mojibake-ui.png
    - defect-789-database-query.png

  attachments:
    - browser-console-log.txt
    - network-request-response.json

  # Analysis
  root_cause: |
    Database column `applications.cover_letter` is using latin1 charset instead of
    utf8mb4. Special characters outside ASCII range are corrupted on insert.

  proposed_fix: |
    ALTER TABLE applications MODIFY cover_letter TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    Ensure all text columns use utf8mb4 for full Unicode support.

  # Resolution
  resolution_date: 2025-01-23T15:30:00Z
  fixed_in_build: 1.2.4
  verified_by: QA Engineer (John Smith)
  verification_date: 2025-01-24T09:00:00Z

  regression_test:
    test_case_id: TC-APP-102-05-REGRESSION
    added_to_regression_suite: Yes

  notes: |
    Also checked other text columns - all converted to utf8mb4.
    Verified with various Unicode characters (emoji, Chinese, Arabic) - all working.
```

#### Defect Severity vs Priority

**Severity** = Technical impact (how bad is the bug?)
**Priority** = Business impact (how urgently must it be fixed?)

Examples:
```yaml
defect_classification_examples:
  - scenario: Login page crashes (no one can log in)
    severity: Critical
    priority: Critical
    reason: Blocks all users, must fix immediately

  - scenario: Special characters corrupted in cover letter
    severity: High
    priority: High
    reason: Major feature broken, affects user data

  - scenario: Spelling error in help text
    severity: Low
    priority: Low
    reason: Cosmetic, no functional impact

  - scenario: Critical security vulnerability in admin panel (only 2 admins)
    severity: Critical
    priority: High
    reason: Severe technical issue, but limited user impact

  - scenario: CEO's favorite feature has minor cosmetic issue
    severity: Low
    priority: High
    reason: Low technical impact, but high business priority
```

### 6.4 Test Metrics and KPIs

#### Key Performance Indicators

**Test Effectiveness**:
```yaml
test_effectiveness_metrics:
  defect_detection_rate:
    formula: (Defects found by testing) / (Total defects)
    target: ">80%"
    actual: "85%"
    interpretation: Testing found 85% of defects before production

  defect_removal_efficiency:
    formula: (Defects fixed) / (Defects found) * 100
    target: ">95%"
    actual: "96.5%"
    interpretation: 96.5% of found defects were fixed

  test_coverage:
    code_coverage: "85.2%"
    requirements_coverage: "100%"
    risk_coverage: "100% high-risk areas"

  test_pass_rate:
    formula: (Passed tests) / (Total tests) * 100
    target: ">95%"
    actual: "96.5%"
```

**Test Efficiency**:
```yaml
test_efficiency_metrics:
  test_automation_rate:
    formula: (Automated tests) / (Total tests) * 100
    target: ">70%"
    actual: "75%"
    interpretation: 75% of tests are automated

  test_execution_time:
    unit_tests: "4.5 minutes"
    integration_tests: "12.3 minutes"
    e2e_tests: "28.7 minutes"
    total_automated: "45.5 minutes"
    target: "<60 minutes"

  test_case_productivity:
    formula: (Test cases executed) / (Testing effort in hours)
    actual: "171 test cases / 14 hours = 12.2 test cases/hour"

  defect_density:
    formula: (Defects found) / (KLOC)
    actual: "8 defects / 25 KLOC = 0.32 defects/KLOC"
    industry_average: "0.5-1.0 defects/KLOC"
    interpretation: Below industry average (good)
```

**Quality Metrics**:
```yaml
quality_metrics:
  defect_by_severity:
    critical: 0
    high: 2
    medium: 4
    low: 2

  defect_by_status:
    fixed: 6
    deferred: 2
    open: 0

  defect_aging:
    average_time_to_fix: "1.5 days"
    oldest_open_defect: "0 days (all closed or deferred)"

  defect_leakage:
    formula: (Defects found in production) / (Total defects) * 100
    target: "<5%"
    actual: "0%"
    interpretation: No defects escaped to production
```

### 6.5 Stakeholder Communication

#### Daily Test Status Report (Agile)

```yaml
daily_test_status:
  date: 2025-01-25
  sprint: Sprint 2
  bounded_context: bc_applications

  today_summary:
    - Executed 25 integration tests (all passed)
    - Completed E2E tests for wf_submit_application (20 passed, 1 failed)
    - Found 1 new defect (DEF-795, Medium severity)
    - Fixed and verified DEF-789 (special characters issue)

  metrics:
    tests_executed_today: 45
    tests_passed: 44
    tests_failed: 1
    new_defects: 1
    defects_fixed: 1

  blockers:
    - None

  risks:
    - E2E test suite execution time increasing (now 32 min, target <30 min)
      mitigation: Investigate test parallelization options

  tomorrow_plan:
    - Complete accessibility testing (WCAG 2.2 AA audit)
    - Execute performance tests
    - Regression testing after DEF-789 fix
```

#### Weekly Test Progress Report

```yaml
weekly_test_progress:
  week_ending: 2025-01-26
  sprint: Sprint 2, Week 2

  test_execution_progress:
    planned_tests: 171
    executed_tests: 145
    remaining_tests: 26
    completion_percentage: 84.8%

  defect_summary:
    opened_this_week: 3
    fixed_this_week: 4
    currently_open: 2

  risks_and_issues:
    - Test environment downtime (4 hours on Tuesday)
    - Performance test execution slower than expected

  next_week_plan:
    - Complete remaining 26 test cases
    - Re-test all fixed defects
    - Prepare test summary report
    - UAT with Product Owner
```

#### Executive Dashboard (Metrics Visualization)

```yaml
executive_dashboard:
  project: Application Tracking System (bc_applications)
  status: On Track

  health_indicators:
    overall_health: Green
    test_execution: Green (96.5% pass rate)
    defect_status: Yellow (2 defects deferred)
    test_coverage: Green (85.2%)
    schedule: Green (on track for release)

  key_metrics:
    - metric: Test Pass Rate
      value: 96.5%
      target: ">95%"
      trend: "↑ (up from 94% last week)"

    - metric: Code Coverage
      value: 85.2%
      target: ">80%"
      trend: "↑ (up from 82% last week)"

    - metric: Open Defects
      critical: 0
      high: 0
      medium: 2
      low: 0
      trend: "↓ (down from 4 medium)"

    - metric: Release Readiness
      value: 95%
      criteria_met: "9 of 10"
      remaining: "Address 2 deferred defects"

  release_recommendation:
    status: APPROVE
    confidence: High
    notes: All critical criteria met, 2 low-priority items deferred to next release
```

### 6.6 YAML Test Report Schema

```yaml
test_report_schema:
  report_type: Test Summary Report
  version: 1.0

  metadata:
    report_id: required
    title: required
    reporting_period_start: required (ISO 8601 date)
    reporting_period_end: required (ISO 8601 date)
    prepared_by: required
    date: required (ISO 8601 date)
    project: required
    bounded_context: optional (for DDD projects)

  executive_summary:
    type: string (markdown)
    required: true

  test_scope:
    in_scope: array[string]
    out_of_scope: array[string]

  test_approach:
    test_levels: array[object]
    test_types: array[object]
    test_techniques: array[object]

  test_metrics:
    test_execution_summary:
      total_test_cases: integer
      executed: integer
      passed: integer
      failed: integer
      blocked: integer
      not_run: integer
      pass_rate: percentage

    coverage:
      code_coverage: object
      requirements_coverage: object

    defect_metrics:
      total_defects: integer
      by_severity: object
      defect_density: float
      defect_removal_efficiency: percentage

  test_results:
    by_priority: object
    by_type: object
    by_test_level: object

  defects:
    type: array[defect_report]

  risks:
    type: array[risk_item]

  exit_criteria_assessment:
    type: array[exit_criterion]

  recommendations:
    release_recommendation: enum[APPROVE, CONDITIONAL_APPROVE, REJECT]
    conditions: array[string]
    improvements: array[string]

  lessons_learned:
    what_went_well: array[string]
    areas_for_improvement: array[string]
    action_items: array[string]
```

---

## 7. Integration with DDD and UX

### 7.1 Test Artifacts by Bounded Context

```yaml
test_artifacts_organization:
  by_bounded_context:
    bc_profile:
      test_strategy: test-strategy-bc-profile.md
      test_plan: test-plan-bc-profile-2025-q1.yaml
      test_scenarios: scenarios/bc-profile/*.yaml
      test_cases: test-cases/bc-profile/**/*.yaml
      test_results: test-results/bc-profile/2025-01/*.json
      test_reports: reports/bc-profile/test-summary-2025-01.md

    bc_applications:
      test_strategy: test-strategy-bc-applications.md
      test_plan: test-plan-bc-applications-2025-q1.yaml
      test_scenarios: scenarios/bc-applications/*.yaml
      test_cases: test-cases/bc-applications/**/*.yaml
      test_results: test-results/bc-applications/2025-01/*.json
      test_reports: reports/bc-applications/test-summary-2025-01.md

    bc_matching:
      test_strategy: test-strategy-bc-matching.md
      test_plan: test-plan-bc-matching-2025-q1.yaml
      test_scenarios: scenarios/bc-matching/*.yaml
      test_cases: test-cases/bc-matching/**/*.yaml
      test_results: test-results/bc-matching/2025-01/*.json
      test_reports: reports/bc-matching/test-summary-2025-01.md
```

### 7.2 Traceability: Requirements → Tests → Defects

```yaml
full_traceability_example:
  requirement:
    id: REQ-APP-001
    title: Candidate can submit job application
    bounded_context: bc_applications
    user_story: US-102

  test_scenarios:
    - id: TS-102-01
      title: Successful application submission
      test_cases: [TC-102-01-01, TC-102-01-02, TC-102-01-03]

    - id: TS-102-02
      title: Application validation errors
      test_cases: [TC-102-02-01, TC-102-02-02, TC-102-02-03]

  test_cases:
    - id: TC-102-01-01
      title: Submit with valid data
      status: Passed
      execution_date: 2025-01-20

    - id: TC-102-01-02
      title: Submit with optional fields
      status: Passed
      execution_date: 2025-01-20

    - id: TC-102-02-01
      title: Missing cover letter
      status: Failed
      execution_date: 2025-01-20
      defect: DEF-792

  defects:
    - id: DEF-792
      title: Validation error not cleared after correction
      severity: Medium
      status: Fixed
      test_case: TC-102-02-01
      regression_test: TC-102-02-01-REGRESSION

  test_results:
    overall_status: Verified
    coverage: 100%
    all_defects_fixed: true
```

---

## 8. Standards Alignment

### 8.1 ISTQB Test Documentation

All artifacts align with ISTQB Foundation Level v4.0 concepts:
- Test work products (test plan, test case, test report)
- Test monitoring and control
- Test completion activities

### 8.2 IEEE 829 / ISO/IEC/IEEE 29119-3

Document templates follow ISO/IEC/IEEE 29119-3 (which supersedes IEEE 829):
- Test Plan structure (16 components)
- Test Case specification
- Test Incident Report (defect report)
- Test Summary Report

### 8.3 ISO 25010 Quality Characteristics

Test artifacts reference ISO 25010 quality characteristics:
- Functional suitability (functional tests)
- Performance efficiency (performance tests)
- Usability (usability tests, accessibility tests)
- Security (security tests)

---

## 9. Best Practices Summary

### Test Strategy
1. Create organizational strategy for consistency
2. Create project strategy for unique needs
3. Align with DDD bounded contexts
4. Include shift-left and continuous testing
5. Define quality gates and metrics

### Test Plans
1. Follow IEEE 829 / ISO 29119-3 structure
2. Be specific and detailed
3. Include risk assessment
4. Define entry/exit criteria
5. Keep lightweight in Agile environments

### Test Scenarios
1. Write from user perspective
2. Use BDD format for clarity
3. Map to requirements/user stories
4. Cover happy path and edge cases
5. Organize by workflow or bounded context

### Test Cases
1. One test case = one test objective
2. Use clear, descriptive names
3. Make tests independent and repeatable
4. Apply test design techniques (BVA, EP, etc.)
5. Automate where ROI is positive

### Test Execution
1. Prepare environment and data
2. Log all results (pass/fail, actual results)
3. Capture evidence (screenshots, logs)
4. Report defects immediately
5. Integrate with CI/CD for continuous execution

### Test Reports
1. Tailor to audience (executives vs testers)
2. Include metrics and trends
3. Provide clear recommendations
4. Document lessons learned
5. Maintain traceability (requirements → tests → defects)

---

## 10. References and Further Reading

### Standards Documents
1. **ISTQB CTFL v4.0** - Test process and work products
2. **ISO/IEC/IEEE 29119-3:2013** - Test documentation
3. **IEEE 829-2008** (Historical) - Test documentation templates
4. **ISO 25010** - Quality characteristics for testing

### Books
- "Agile Testing" - Lisa Crispin, Janet Gregory (test planning in Agile)
- "Quality for DevOps Teams" - Rik Marselis et al. (TMAP, continuous testing)
- "The Art of Software Testing" - Glenford Myers (test case design)

### Related Job Seeker Documents
1. `02-domain-I-standards.md` - Quality engineering standards
2. `03-domain-II-ontologies.md` - Testing ontologies (ROoST, TestTDO, STOWS)
3. `QE-DDD-UX-INTEGRATION.md` - Integration framework
4. `16-qe-yaml-schema.yaml` - Test specification language with examples

---

**Document Status**: Complete
**Next Document**: Domain IV (Test Levels) or Domain V (Test Types)
