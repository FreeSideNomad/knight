# QE YAML Language Guide

## Table of Contents
- [Overview](#overview)
- [Entity Types](#entity-types)
- [Writing Test Specifications](#writing-test-specifications)
- [DDD/UX Integration](#ddux-integration)
- [BDD Scenario Format](#bdd-scenario-format)
- [Complete Examples](#complete-examples)
- [Validation and Tooling](#validation-and-tooling)
- [Integration with Test Management](#integration-with-test-management)

## Overview

### Purpose
The QE YAML Schema provides a formal, structured language for describing test requirements that bridges the gap between domain-driven design (DDD), user experience (UX) design, and quality engineering. It enables:

- **Traceability**: Direct references from tests to DDD constructs (aggregates, value objects, services) and UX elements (pages, workflows, components)
- **Consistency**: Standardized test case structure across unit, integration, UI, and end-to-end tests
- **Automation**: Machine-readable format suitable for test automation frameworks and CI/CD pipelines
- **Documentation**: Self-documenting tests that serve as living documentation of system behavior
- **Quality Metrics**: Built-in support for tracking coverage, quality, and performance metrics

### Key Benefits

1. **Single Source of Truth**: Test specifications reference the same domain model and UX design artifacts used by developers
2. **Complete Coverage**: Ensures tests cover all layers from domain logic to UI interactions
3. **Maintainability**: When domain models or UX designs change, it's clear which tests need updating
4. **Agile-Friendly**: Supports both traditional test steps and BDD Given-When-Then format
5. **Tool-Agnostic**: Can be used with Jest, Playwright, Cypress, Cucumber, or other frameworks

### Schema Version
- **Version**: 1.0.0
- **Date**: 2025-10-04
- **Schema File**: `16-qe-yaml-schema.yaml`

## Entity Types

The schema defines seven primary entity types for organizing and describing tests:

### 1. Test Suite (`test_suite`)
Organizes related test cases by bounded context or feature area.

**Key Properties:**
- `id`: Unique identifier (format: `ts_[context]_[name]`)
- `name`: Human-readable name
- `bounded_context`: Which DDD bounded context this suite tests
- `categories`: Organizes tests by layer (domain, application, UI, end-to-end)
- `owner`: Team or individual responsible
- `priority`: Business criticality (critical, high, medium, low)

**When to Use:**
- Grouping all tests for a bounded context (e.g., `ts_profile_suite`)
- Organizing tests by feature area
- Creating test runs for specific quality characteristics

### 2. Test Case (`test_case`)
Defines a single testable scenario with specific inputs, actions, and expected outcomes.

**Key Properties:**
- `id`: Unique identifier (format: `tc_[level]_[number]_[name]`)
- `test_level`: Type of test (unit, integration, system, acceptance, ui, e2e)
- `test_type`: What aspect is tested (functional, performance, security, usability, accessibility)
- `test_priority`: Execution priority (critical, high, medium, low)
- `ddd_references`: Links to domain model elements
- `ux_references`: Links to UX design elements
- `given`/`when`/`then`: BDD-style test definition
- `test_steps`: Alternative traditional step-by-step format
- `automated`: Whether the test is automated
- `automation_framework`: Tool used for automation (Jest, Playwright, etc.)

**When to Use:**
- Defining specific test scenarios
- Documenting manual or automated tests
- Creating executable specifications

### 3. Test Data (`test_data`)
Defines reusable test data sets that conform to domain model constraints.

**Key Properties:**
- `id`: Unique identifier (format: `td_[name]`)
- `aggregate_type`: Which aggregate this data represents
- `data`: Structured data matching domain model
- `satisfies_invariants`: Whether data satisfies domain invariants
- `reusable`: Whether data can be shared across tests

**When to Use:**
- Creating valid/invalid test data sets
- Sharing data across multiple test cases
- Documenting domain invariants through test examples

### 4. Test Scenario (`test_scenario`)
Behavior-driven test specification focused on user stories and acceptance criteria.

**Key Properties:**
- `id`: Unique identifier (format: `ts_[name]`)
- `feature`: Feature name
- `user_story`: As a [role], I want [goal], So that [value]
- `scenario_name` and `scenario_description`: What is being tested
- `bounded_contexts_involved`: Which contexts participate
- `workflows_involved`: Which UX workflows are exercised
- `given_steps`, `when_steps`, `then_steps`: BDD scenario steps
- `acceptance_criteria`: Success criteria from product owner

**When to Use:**
- Acceptance testing with stakeholders
- BDD-style scenario documentation
- Testing cross-context workflows
- Validating user stories

### 5. Test Automation Configuration (`test_automation`)
Technical configuration for test automation frameworks.

**Key Properties:**
- `framework`: Automation tool (jest, playwright, cypress, cucumber, etc.)
- `page_objects`: Page Object Model definitions
- `test_doubles`: Mocks, stubs, fakes, and spies
- `fixtures`: Test data setup and teardown

**When to Use:**
- Configuring automation frameworks
- Defining page objects for UI tests
- Specifying mocking strategies
- Managing test fixtures

### 6. Test Execution Plan (`test_execution_plan`)
Defines when, where, and how tests should be executed.

**Key Properties:**
- `test_suite_refs` and `test_case_refs`: Which tests to run
- `execution_order`: Sequential, parallel, or priority-based
- `environment`: Where to run (local, CI, staging, production)
- `trigger`: When to run (on_commit, on_pr, scheduled, manual)
- `ci_pipeline_stage`: Integration with CI/CD

**When to Use:**
- Configuring CI/CD pipelines
- Scheduling regression tests
- Defining smoke test suites
- Creating release validation plans

### 7. Defect (`defect`)
Documents bugs found during testing with context and traceability.

**Key Properties:**
- `id`: Unique identifier (format: `BUG-[number]`)
- `severity` and `priority`: Classification
- `ddd_references` and `ux_references`: Where the bug occurs
- `steps_to_reproduce`: How to trigger the bug
- `expected_behavior` vs `actual_behavior`: What should happen vs what does
- `root_cause` and `fix_description`: Analysis and resolution
- `related_test_case`: Which test caught the bug

**When to Use:**
- Reporting bugs found during testing
- Tracking defects to resolution
- Analyzing quality trends
- Linking bugs to test cases

## Writing Test Specifications

### Test Case Structure

Every test case follows a consistent structure:

```yaml
test_case:
  # Identification
  id: "tc_[level]_[number]_[name]"
  name: "Clear, descriptive name"
  description: "What this test validates"

  # Classification
  test_level: unit|integration|system|acceptance|ui|e2e
  test_type: functional|performance|security|usability|accessibility
  test_priority: critical|high|medium|low

  # Context (optional, as applicable)
  ddd_references: {...}
  ux_references: {...}

  # Test Definition (choose one style)
  # Option 1: BDD style
  given: [...]
  when: [...]
  then: [...]

  # Option 2: Traditional steps
  test_steps: [...]

  # Supporting elements
  test_data_refs: [...]
  assertions: [...]
  preconditions: [...]
  postconditions: [...]

  # Metadata
  automated: true|false
  automation_framework: "jest|playwright|cypress|etc"
  author: "team_or_person"
  tags: [...]
```

### Naming Conventions

**Test IDs:**
- Format: `tc_[level]_[number]_[name]`
- Level: `unit`, `int`, `sys`, `acc`, `ui`, `e2e`, `a11y`, `perf`, `sec`
- Number: Zero-padded sequential (001, 002, etc.)
- Name: Lowercase with underscores

Examples:
- `tc_unit_001_vo_email_validation`
- `tc_int_002_update_candidate_profile`
- `tc_e2e_003_submit_application_workflow`
- `tc_a11y_005_profile_page_wcag`

**DDD References:**
- Bounded Context: `bc_[name]` (e.g., `bc_profile`, `bc_applications`)
- Aggregate: `agg_[name]` (e.g., `agg_candidate_profile`)
- Entity: `ent_[name]` (e.g., `ent_candidate`, `ent_job`)
- Value Object: `vo_[name]` (e.g., `vo_email`, `vo_skills`)
- Domain Service: `svc_dom_[name]` (e.g., `svc_dom_skill_matching`)
- Application Service: `svc_app_[name]` (e.g., `svc_app_update_profile`)
- Repository: `repo_[name]` (e.g., `repo_candidate_profile`)
- Domain Event: `evt_[name]` (e.g., `evt_profile_updated`)

**UX References:**
- Page: `page_[name]` (e.g., `page_profile_edit`, `page_job_detail`)
- Workflow: `wf_[name]` (e.g., `wf_submit_application`)
- Component: `comp_[name]` (e.g., `comp_email_input`)
- Atomic Design: `atom_[name]`, `mol_[name]`, `org_[name]`

**Tags:**
- Use lowercase, descriptive tags
- Include layer: `unit`, `integration`, `e2e`, `ui`
- Include context: `bc_profile`, `bc_applications`
- Include type: `functional`, `accessibility`, `performance`
- Include criticality: `critical`, `smoke_test`, `regression`

### Test Levels Explained

**Unit Tests** (`test_level: unit`)
- Test individual domain objects in isolation
- Focus on value objects, entities, domain services
- Fast execution, no external dependencies
- Framework: Jest, JUnit, pytest
- Example: Testing email validation in `vo_email`

**Integration Tests** (`test_level: integration`)
- Test interactions between components
- Focus on application services, repositories, event handlers
- May use test doubles for external systems
- Framework: Jest with real database, Testcontainers
- Example: Testing profile update through application service

**System Tests** (`test_level: system`)
- Test complete backend system
- All layers integrated, real database
- May mock external APIs
- Framework: Jest, Supertest for API testing
- Example: Testing complete API endpoint behavior

**UI Tests** (`test_level: ui`)
- Test user interface in isolation
- Component testing, interaction testing
- Framework: Jest, Testing Library, Storybook
- Example: Testing form validation behavior

**Acceptance Tests** (`test_level: acceptance`)
- Test user stories and acceptance criteria
- Business-focused scenarios
- Often written in BDD style
- Framework: Cucumber, Jest with BDD syntax
- Example: User can submit job application

**End-to-End Tests** (`test_level: e2e`)
- Test complete workflows through UI
- Full stack integration
- Real browser, real backend
- Framework: Playwright, Cypress
- Example: Complete job application submission workflow

### Test Types Explained

**Functional** (`test_type: functional`)
- Validates business logic and requirements
- Most common test type
- Ensures features work as specified

**Performance** (`test_type: performance`)
- Validates response times, throughput
- Load testing, stress testing
- Ensures scalability targets are met

**Security** (`test_type: security`)
- Validates authentication, authorization
- Input validation, SQL injection prevention
- Ensures confidentiality and integrity

**Usability** (`test_type: usability`)
- Validates user experience
- Task completion, time on task
- User satisfaction metrics (SUS)

**Accessibility** (`test_type: accessibility`)
- Validates WCAG compliance
- Screen reader compatibility
- Keyboard navigation, color contrast

**Reliability** (`test_type: reliability`)
- Validates availability, fault tolerance
- Recovery from errors
- Ensures stability under various conditions

**Maintainability** (`test_type: maintainability`)
- Validates code quality
- Test coverage, modularity
- Ensures long-term sustainability

## DDD/UX Integration

### Why DDD/UX References Matter

Traditional test cases often lack context about **what** is being tested. The schema's DDD and UX references provide:

1. **Traceability**: Direct links from tests to design artifacts
2. **Impact Analysis**: When domain model changes, know which tests need updates
3. **Coverage Analysis**: Verify all aggregates, value objects, and pages have tests
4. **Onboarding**: New team members understand system structure through tests
5. **Living Documentation**: Tests document the actual implementation

### DDD References

**When to Use Each Reference:**

```yaml
ddd_references:
  bounded_context: "bc_profile"  # Always include for context

  # For unit tests of domain objects:
  value_object: "vo_email"
  entity: "ent_candidate"

  # For integration tests of aggregates:
  aggregate: "agg_candidate_profile"
  aggregate_root: "ent_candidate"
  repository: "repo_candidate_profile"

  # For application service tests:
  application_service: "svc_app_update_profile"
  domain_service: "svc_dom_skill_matching"  # if involved

  # For event-driven tests:
  domain_event: "evt_profile_updated"

  # For complex object creation:
  factory: "factory_candidate_profile"
```

**Job Seeker Domain Example:**

The Job Seeker application has multiple bounded contexts:

- `bc_profile`: Candidate profile management
- `bc_job_catalog`: Job listings and search
- `bc_applications`: Application submission and tracking
- `bc_matching`: Skills matching algorithms
- `bc_notifications`: Email and in-app notifications

When testing the profile update feature:

```yaml
ddd_references:
  bounded_context: "bc_profile"
  aggregate: "agg_candidate_profile"
  aggregate_root: "ent_candidate"
  application_service: "svc_app_update_profile"
  repository: "repo_candidate_profile"
  domain_event: "evt_candidate_profile_updated"
```

This tells us:
- Tests belong to Profile bounded context
- Tests the CandidateProfile aggregate
- Exercises the UpdateProfile application service
- Verifies repository persistence
- Checks that ProfileUpdated event is published

### UX References

**When to Use Each Reference:**

```yaml
ux_references:
  # For UI and E2E tests:
  page: "page_profile_edit"  # Which page
  workflow: "wf_update_profile"  # Which workflow

  # For component tests:
  component: "comp_email_input"
  component_type: molecule  # atom, molecule, organism, domain_component

  # For interaction tests:
  navigation_pattern: "wizard_stepper"
  interaction_pattern: "form_validation"
  behavior_pattern: "optimistic_update"
```

**Job Seeker UX Example:**

The Job Seeker application follows Atomic Design:

- **Atoms**: `atom_button`, `atom_input`, `atom_label`
- **Molecules**: `mol_email_input`, `mol_skill_chip`, `mol_search_bar`
- **Organisms**: `org_profile_form`, `org_job_card`, `org_application_wizard`
- **Pages**: `page_job_detail`, `page_profile_edit`, `page_applications_list`
- **Workflows**: `wf_submit_application`, `wf_update_profile`, `wf_search_jobs`

When testing the email input component:

```yaml
ux_references:
  component: "mol_email_input"
  component_type: molecule
  interaction_pattern: "real_time_validation"
```

When testing the job application workflow:

```yaml
ux_references:
  workflow: "wf_submit_application"
  pages: ["page_job_detail", "page_application_review", "page_application_confirmation"]
  navigation_pattern: "wizard_stepper"
```

### Linking DDD and UX

The power of the schema comes from linking domain and UX layers:

```yaml
test_case:
  id: "tc_int_010_profile_form_submission"
  name: "Profile Form Submission Updates Aggregate"

  # Links UI component to domain model
  ddd_references:
    bounded_context: "bc_profile"
    aggregate: "agg_candidate_profile"
    application_service: "svc_app_update_profile"

  ux_references:
    page: "page_profile_edit"
    component: "org_profile_form"
    interaction_pattern: "form_submit_with_validation"

  # Test ensures UI correctly calls domain service
  when:
    - action: "Submit profile form with valid data"
      parameters:
        skills: ["Python", "React", "PostgreSQL"]

  then:
    - expectation: "Application service called with form data"
      verification_method: "Mock service verify called with correct DTO"
    - expectation: "UI shows success message"
      verification_method: "Assert toast notification displayed"
```

This test validates the integration between:
- UI layer: `org_profile_form` on `page_profile_edit`
- Application layer: `svc_app_update_profile`
- Domain layer: `agg_candidate_profile`

## BDD Scenario Format

### Given-When-Then Structure

Behavior-Driven Development (BDD) uses natural language to describe tests from a user perspective. The schema supports BDD through the `given`, `when`, `then` format.

**Structure:**

```yaml
given:  # Preconditions - set up the world
  - condition: "Description of initial state"
    setup: "How to establish this state"

when:  # Actions - what the user/system does
  - action: "Description of action"
    parameters:
      key: value

then:  # Expected Results - what should happen
  - expectation: "Description of expected outcome"
    verification_method: "How to verify this outcome"
```

### Writing Effective Given Steps

**Given** establishes the context and preconditions.

**Guidelines:**
- Describe the initial state of the system
- Include all necessary setup (data, authentication, navigation)
- Be specific but not overly technical
- Focus on business-relevant context

**Job Seeker Examples:**

```yaml
# User authentication state
given:
  - condition: "Candidate is logged in"
    setup: "Authenticate with test user credentials (candidate_001)"

# Data state
given:
  - condition: "Candidate has existing profile with 2 skills"
    setup: "Create candidate in database with skills=['Python', 'JavaScript']"
  - condition: "Active job posting exists for React developer"
    setup: "Insert job posting (job_456) requiring React skill"

# Navigation state
given:
  - condition: "User is on job detail page"
    setup: "Navigate to /jobs/job_456"

# Complex state
given:
  - condition: "Candidate has submitted application for job_456"
    setup: "Create application record with status='SUBMITTED'"
  - condition: "Application has been reviewed"
    setup: "Update application status to 'UNDER_REVIEW'"
```

### Writing Effective When Steps

**When** describes the actions taken by the user or system.

**Guidelines:**
- Use active voice
- Focus on one primary action (can have supporting actions)
- Include relevant parameters
- Describe user behavior, not implementation

**Job Seeker Examples:**

```yaml
# User actions
when:
  - action: "User clicks 'Apply for Job' button"
    parameters: {}

when:
  - action: "User enters email address"
    parameters:
      email: "john.doe@example.com"

when:
  - action: "User submits profile form"
    parameters:
      skills: ["Python", "React", "PostgreSQL"]
      experience_years: 5
      location: "New York, NY"

# System actions
when:
  - action: "Application deadline passes"
    parameters:
      current_time: "2025-11-01T00:00:00Z"

# Multi-step actions
when:
  - action: "User navigates through application wizard"
    parameters:
      steps: ["job_info", "profile_review", "confirmation"]
  - action: "User clicks 'Submit Application'"
    parameters: {}
```

### Writing Effective Then Steps

**Then** describes the expected outcomes and how to verify them.

**Guidelines:**
- State observable results
- Include both UI and system changes
- Specify verification method
- Test one concept per expectation (multiple expectations OK)

**Job Seeker Examples:**

```yaml
# UI verification
then:
  - expectation: "Success message displayed"
    verification_method: "Assert toast notification shows 'Profile updated successfully'"
  - expectation: "Form fields are cleared"
    verification_method: "Assert all input values are empty strings"

# Data verification
then:
  - expectation: "Profile saved to database"
    verification_method: "Query repository and assert skills=['Python', 'React', 'PostgreSQL']"
  - expectation: "Updated timestamp changed"
    verification_method: "Assert updated_at > previous value"

# Event verification
then:
  - expectation: "ProfileUpdated event published"
    verification_method: "Assert event bus received evt_candidate_profile_updated with candidate_id=candidate_001"

# Behavior verification
then:
  - expectation: "Confirmation email sent"
    verification_method: "Assert email service called with correct template and recipient"

# Multiple related expectations
then:
  - expectation: "Application status is SUBMITTED"
    verification_method: "Assert application.status == 'SUBMITTED'"
  - expectation: "Application appears in candidate's list"
    verification_method: "Navigate to My Applications page, assert new application visible"
  - expectation: "Email notification sent to candidate"
    verification_method: "Mock email service verify called with candidate email"
```

### BDD vs Traditional Test Steps

The schema supports both formats. Choose based on your team's preference:

**Use BDD (Given-When-Then) when:**
- Writing acceptance tests with product owners
- Testing user stories
- Emphasizing behavior over implementation
- Communicating with non-technical stakeholders

**Use Traditional Steps when:**
- Writing detailed technical tests
- Step-by-step procedures are clearer
- Recording actual vs expected results during execution
- Manual testing where testers need explicit instructions

**Example Comparison:**

BDD Style:
```yaml
given:
  - condition: "User on profile edit page"
    setup: "Navigate to /profile/edit"
when:
  - action: "User enters invalid email"
    parameters:
      email: "not-an-email"
then:
  - expectation: "Validation error displayed"
    verification_method: "Assert error message 'Invalid email format'"
```

Traditional Style:
```yaml
test_steps:
  - step_number: 1
    action: "Navigate to profile edit page (/profile/edit)"
    expected_result: "Page loads, form displayed"
  - step_number: 2
    action: "Enter 'not-an-email' in email field"
    expected_result: "Field accepts input"
  - step_number: 3
    action: "Click Submit button"
    expected_result: "Validation error displayed: 'Invalid email format'"
```

### Test Scenario Entity for BDD

For full BDD scenarios with user stories, use the `test_scenario` entity:

```yaml
test_scenario:
  id: "ts_acc_020_apply_for_job"
  feature: "Job Application Submission"
  user_story: "As a job seeker, I want to submit an application for a job, So that I can be considered for the position"

  scenario_name: "Successful application submission"
  scenario_description: "Candidate with complete profile applies for active job"

  bounded_contexts_involved: ["bc_profile", "bc_job_catalog", "bc_applications"]
  workflows_involved: ["wf_submit_application"]
  pages_involved: ["page_job_detail", "page_application_review", "page_application_confirmation"]

  given_steps:
    - "Candidate is logged in with complete profile"
    - "Active job posting exists for 'Senior React Developer'"
    - "Candidate has not previously applied for this job"

  when_steps:
    - "Candidate navigates to job detail page"
    - "Candidate clicks 'Apply for Job' button"
    - "Candidate reviews profile information in wizard"
    - "Candidate clicks 'Submit Application' button"

  then_steps:
    - "Application is created with status SUBMITTED"
    - "ApplicationSubmitted event published with candidate_id and job_id"
    - "Confirmation page displays with unique application ID"
    - "Confirmation email sent to candidate"
    - "Application appears in candidate's applications list with correct status"

  acceptance_criteria:
    - "Application data persisted to database"
    - "Domain event triggers email notification"
    - "UI provides clear success feedback"
    - "Application is trackable in My Applications page"

  tags: ["acceptance", "bdd", "user_story", "critical"]
  priority: critical
```

## Complete Examples

### Example 1: Unit Test - Value Object Validation

**Scenario**: Test email value object validation in the Profile bounded context.

```yaml
test_case:
  id: "tc_unit_001_vo_email_validation"
  name: "Email Value Object Validates Format"
  description: "Verify that vo_email enforces RFC 5322 email format rules and rejects invalid inputs"

  test_level: unit
  test_type: functional
  test_priority: critical

  ddd_references:
    bounded_context: "bc_profile"
    value_object: "vo_email"

  ux_references:
    component: "mol_email_input"
    component_type: molecule

  given:
    - condition: "Valid and invalid email strings provided"
      setup: "Initialize test data arrays: valid_emails=['john@example.com', 'jane.doe@company.co.uk'] and invalid_emails=['not-an-email', '@example.com', 'user@']"

  when:
    - action: "Create Email value object with each test string"
      parameters:
        test_data_ref: "td_email_validation_cases"

  then:
    - expectation: "Valid emails create Email objects successfully"
      verification_method: "For each valid email, assert no exception thrown and email.value equals input"
    - expectation: "Invalid emails throw ValidationError"
      verification_method: "For each invalid email, assert ValidationError thrown with message 'Invalid email format'"
    - expectation: "Email value object is immutable"
      verification_method: "Assert email.value is read-only, cannot be reassigned"

  test_data_refs: ["td_valid_emails", "td_invalid_emails"]

  assertions:
    - assertion_type: equals
      target: "Email('john@example.com').value"
      expected_value: "john@example.com"
    - assertion_type: throws_exception
      target: "Email('not-an-email')"
      expected_value: "ValidationError"

  automated: true
  automation_framework: "jest"
  author: "qa_team"
  created_date: "2025-10-04"
  tags: ["unit", "value_object", "validation", "bc_profile", "critical"]
```

**What This Tests:**
- Domain layer: `vo_email` enforces business rules
- UI layer: `mol_email_input` component will use this value object
- Validates invariants: email format must be valid
- Critical path: user registration and profile editing depend on this

### Example 2: Integration Test - Application Service

**Scenario**: Test updating candidate profile through application service, verifying aggregate persistence and event publishing.

```yaml
test_case:
  id: "tc_int_002_update_candidate_profile"
  name: "Update Candidate Profile Via Application Service"
  description: "Verify that UpdateProfileService updates aggregate, persists to repository, and publishes ProfileUpdated event"

  test_level: integration
  test_type: functional
  test_priority: high

  ddd_references:
    bounded_context: "bc_profile"
    aggregate: "agg_candidate_profile"
    aggregate_root: "ent_candidate"
    application_service: "svc_app_update_profile"
    repository: "repo_candidate_profile"
    domain_event: "evt_candidate_profile_updated"

  ux_references:
    page: "page_profile_edit"
    workflow: "wf_update_profile"
    component: "org_profile_form"

  given:
    - condition: "Existing candidate profile in database"
      setup: "Insert test candidate with id='candidate_001', email='john@example.com', skills=['Python', 'Java'], experience_years=3"
    - condition: "Updated profile data with additional skill"
      setup: "Prepare UpdateProfileDTO with skills=['Python', 'Java', 'React'], experience_years=3"

  when:
    - action: "Call UpdateProfileService.execute()"
      parameters:
        candidate_id: "candidate_001"
        update_dto:
          skills: ["Python", "Java", "React"]
          experience_years: 3

  then:
    - expectation: "Profile aggregate updated in database"
      verification_method: "Query repo_candidate_profile.findById('candidate_001') and assert skills include 'React'"
    - expectation: "CandidateProfileUpdated event published to event bus"
      verification_method: "Assert event bus received evt_candidate_profile_updated with payload {candidate_id: 'candidate_001', updated_fields: ['skills']}"
    - expectation: "Updated profile returned to caller"
      verification_method: "Assert service returns CandidateProfile with updated skills"
    - expectation: "Updated timestamp changed"
      verification_method: "Assert profile.updated_at is greater than previous value"

  test_data_refs: ["td_candidate_001_initial", "td_candidate_001_updated"]

  preconditions:
    - "Database is running and accessible"
    - "Event bus is initialized"
    - "Test candidate exists in database"

  postconditions:
    - "Database contains updated profile"
    - "Event bus contains published event"

  cleanup_steps:
    - "Delete test candidate from database"
    - "Clear event bus"

  automated: true
  automation_framework: "jest"
  author: "backend_team"
  created_date: "2025-10-04"
  tags: ["integration", "application_service", "aggregate", "bc_profile", "event_driven"]
```

**What This Tests:**
- Application layer: Service orchestrates update correctly
- Domain layer: Aggregate enforces business rules
- Infrastructure layer: Repository persists changes
- Event-driven architecture: Event published for downstream consumers
- UI integration: Profile form calls this service

### Example 3: UI Component Test - Accessibility

**Scenario**: Test email input component for WCAG 2.2 Level AA compliance.

```yaml
test_case:
  id: "tc_ui_003_email_input_accessibility"
  name: "Email Input Component Meets WCAG 2.2 Level AA"
  description: "Verify mol_email_input component is fully accessible with keyboard navigation, screen reader support, and proper ARIA attributes"

  test_level: ui
  test_type: accessibility
  test_priority: high

  ux_references:
    component: "mol_email_input"
    component_type: molecule
    interaction_pattern: "real_time_validation"

  given:
    - condition: "Email input component rendered in test environment"
      setup: "Render <EmailInput label='Email Address' /> with Testing Library"

  when:
    - action: "Run accessibility audit"
      parameters:
        tool: "jest-axe"
        rules: "wcag2aa"

  then:
    - expectation: "Zero critical or serious axe violations"
      verification_method: "Assert axe results have no violations with impact 'critical' or 'serious'"
    - expectation: "Input has associated label"
      verification_method: "Assert input has aria-labelledby or label element with correct for attribute"
    - expectation: "Keyboard navigable"
      verification_method: "Simulate Tab key, assert input receives focus"
    - expectation: "Validation errors announced to screen readers"
      verification_method: "Submit invalid email, assert error has role='alert' or aria-live='polite'"
    - expectation: "Color contrast meets 4.5:1 ratio"
      verification_method: "Measure text/background contrast, assert >= 4.5:1"

  test_steps:
    - step_number: 1
      action: "Render component with label 'Email Address'"
      expected_result: "Component renders, label visible and associated"

    - step_number: 2
      action: "Run axe accessibility audit"
      expected_result: "Zero violations with impact >= serious"

    - step_number: 3
      action: "Test keyboard navigation (Tab to field)"
      expected_result: "Input receives focus, focus indicator visible"

    - step_number: 4
      action: "Enter invalid email 'not-an-email' and trigger validation"
      expected_result: "Error message displayed with role='alert'"

    - step_number: 5
      action: "Test with screen reader (NVDA/JAWS)"
      expected_result: "Label announced, validation error announced"

    - step_number: 6
      action: "Measure color contrast of label and error text"
      expected_result: "Contrast ratio >= 4.5:1 for both"

  assertions:
    - assertion_type: equals
      target: "axe_violations.critical.length"
      expected_value: 0
    - assertion_type: equals
      target: "axe_violations.serious.length"
      expected_value: 0
    - assertion_type: exists
      target: "input[aria-labelledby] or label[for]"
      expected_value: true

  automated: true
  automation_framework: "jest"
  author: "frontend_team"
  created_date: "2025-10-04"
  tags: ["ui", "accessibility", "wcag", "component", "a11y"]
```

**What This Tests:**
- WCAG 2.2 Level AA compliance
- Keyboard navigation
- Screen reader compatibility
- Color contrast
- ARIA attributes
- Error announcement

### Example 4: E2E Test - Complete Workflow

**Scenario**: Test complete job application submission workflow from job detail to confirmation.

```yaml
test_case:
  id: "tc_e2e_004_submit_application_workflow"
  name: "Complete Job Application Submission Workflow"
  description: "User navigates from job detail page, through application wizard, to confirmation. Verifies UI flow, data persistence, and email notification."

  test_level: e2e
  test_type: functional
  test_priority: critical

  ddd_references:
    bounded_context: "bc_applications"
    aggregate: "agg_job_application"
    application_service: "svc_app_submit_application"
    domain_event: "evt_application_submitted"

  ux_references:
    workflow: "wf_submit_application"
    pages: ["page_job_detail", "page_application_wizard", "page_application_confirmation"]
    navigation_pattern: "wizard_stepper"
    interaction_pattern: "multi_step_form"

  test_steps:
    - step_number: 1
      action: "Navigate to job detail page for 'Senior React Developer' position"
      expected_result: "Job details displayed: title, description, requirements. 'Apply for Job' button visible and enabled"

    - step_number: 2
      action: "Click 'Apply for Job' button"
      expected_result: "Application wizard opens. Step 1/3 'Review Job' displays job title and description. 'Next' button enabled"

    - step_number: 3
      action: "Click 'Next' to proceed to Step 2"
      expected_result: "Step 2/3 'Review Profile' displays candidate name, email, skills list, and experience. 'Next' button enabled"

    - step_number: 4
      action: "Verify profile data displayed correctly"
      expected_result: "Name: 'John Doe', Email: 'john@example.com', Skills: ['Python', 'Java', 'React'], Experience: '3 years'"

    - step_number: 5
      action: "Click 'Next' to proceed to Step 3"
      expected_result: "Step 3/3 'Confirmation' displays summary of job and profile. 'Submit Application' button enabled"

    - step_number: 6
      action: "Click 'Submit Application' button"
      expected_result: "Button shows loading spinner. Button text changes to 'Submitting...'"

    - step_number: 7
      action: "Wait for submission to complete"
      expected_result: "Wizard closes. Confirmation page displays with message 'Application submitted successfully!' and application ID (e.g., 'APP-12345')"

    - step_number: 8
      action: "Verify toast notification"
      expected_result: "Success toast appears: 'Your application has been submitted. Good luck!'"

    - step_number: 9
      action: "Navigate to 'My Applications' page"
      expected_result: "New application appears in list with status 'SUBMITTED', job title 'Senior React Developer', submitted date 'Today'"

    - step_number: 10
      action: "Verify email sent (check email service mock)"
      expected_result: "Email service called with template 'application_confirmation', recipient 'john@example.com', subject 'Application Submitted: Senior React Developer'"

  preconditions:
    - "User logged in as candidate_001 (John Doe)"
    - "Job posting job_456 exists and is active"
    - "Candidate has complete profile with skills"
    - "Candidate has not previously applied for job_456"

  postconditions:
    - "Application exists in database with status SUBMITTED"
    - "ApplicationSubmitted event published"
    - "Email confirmation sent"

  cleanup_steps:
    - "Delete test application from database"
    - "Clear email service calls"
    - "Reset candidate application count"

  automated: true
  automation_framework: "playwright"
  author: "qa_team"
  created_date: "2025-10-04"
  tags: ["e2e", "workflow", "bc_applications", "critical_path", "smoke_test"]
```

**What This Tests:**
- Complete user journey from discovery to confirmation
- Multi-step wizard navigation
- Data display from multiple bounded contexts
- Event-driven email notification
- UI state management across pages
- Critical business workflow

### Example 5: BDD Scenario - Acceptance Test

**Scenario**: Acceptance test for job application user story.

```yaml
test_scenario:
  id: "ts_acc_005_apply_for_job"
  feature: "Job Application Submission"
  user_story: "As a job seeker, I want to submit an application for a job posting, So that I can be considered for the position and advance my career"

  scenario_name: "Successful application submission for qualified candidate"
  scenario_description: "A candidate with a complete profile and matching skills applies for an active job posting and receives confirmation"

  bounded_contexts_involved:
    - "bc_profile"
    - "bc_job_catalog"
    - "bc_applications"
    - "bc_notifications"

  workflows_involved:
    - "wf_submit_application"

  pages_involved:
    - "page_job_detail"
    - "page_application_wizard"
    - "page_application_confirmation"
    - "page_my_applications"

  given_steps:
    - "Candidate John Doe is logged in with email 'john@example.com'"
    - "John has a complete profile with skills ['Python', 'React', 'PostgreSQL'] and 3 years experience"
    - "Active job posting exists for 'Senior React Developer' (job_456)"
    - "Job requires skills ['React', 'JavaScript', 'TypeScript']"
    - "John has not previously applied for job_456"

  when_steps:
    - "John navigates to the job detail page for job_456"
    - "John clicks the 'Apply for Job' button"
    - "John reviews the job information in the application wizard Step 1"
    - "John clicks 'Next' to proceed to Step 2"
    - "John reviews his profile information in Step 2"
    - "John clicks 'Next' to proceed to Step 3"
    - "John reviews the application summary in Step 3"
    - "John clicks 'Submit Application' button"

  then_steps:
    - "Application is created in the system with unique ID (e.g., APP-12345)"
    - "Application status is set to 'SUBMITTED'"
    - "Application is linked to candidate_001 (John) and job_456"
    - "ApplicationSubmitted domain event is published with application data"
    - "Confirmation page is displayed showing application ID and success message"
    - "Success toast notification appears: 'Your application has been submitted!'"
    - "Confirmation email is sent to john@example.com with application details"
    - "John navigates to 'My Applications' page and sees the new application"
    - "Application displays with job title 'Senior React Developer', status 'SUBMITTED', and today's date"

  examples:
    - input:
        candidate_id: "candidate_001"
        job_id: "job_456"
        candidate_skills: ["Python", "React", "PostgreSQL"]
        job_required_skills: ["React", "JavaScript", "TypeScript"]
      expected_output:
        application_created: true
        application_status: "SUBMITTED"
        email_sent: true
        event_published: true

  acceptance_criteria:
    - "Application data is persisted to database with correct candidate and job references"
    - "Application status is set to SUBMITTED upon creation"
    - "ApplicationSubmitted domain event is published to event bus"
    - "Event triggers email notification service"
    - "Email is sent to candidate with confirmation and application ID"
    - "UI displays clear success feedback (confirmation page + toast)"
    - "Application is immediately visible in candidate's applications list"
    - "Application can be tracked by application ID"
    - "Skills matching calculation is triggered (separate bounded context)"

  tags: ["acceptance", "bdd", "user_story", "critical", "bc_applications"]
  priority: critical
```

**What This Tests:**
- Acceptance criteria for user story
- Cross-context collaboration (Profile, Job Catalog, Applications, Notifications)
- Complete business workflow
- Event-driven architecture
- User experience across multiple pages
- Business value delivery

### Example 6: Performance Test

**Scenario**: Test job search API response time under load.

```yaml
test_case:
  id: "tc_perf_006_job_search_response_time"
  name: "Job Search API Responds Within 500ms Under Load"
  description: "Verify job search API returns results within performance target (500ms) when handling 100 concurrent requests"

  test_level: system
  test_type: performance
  test_priority: high

  ddd_references:
    bounded_context: "bc_job_catalog"
    application_service: "svc_app_search_jobs"
    repository: "repo_job_catalog"

  ux_references:
    page: "page_job_search"
    component: "org_job_search_results"
    interaction_pattern: "real_time_search"

  given:
    - condition: "Database contains 10,000 active job postings"
      setup: "Seed database with test job data using fixture td_job_catalog_10k"
    - condition: "Search index is warmed up"
      setup: "Execute 10 warm-up search queries"

  when:
    - action: "Execute 100 concurrent search requests"
      parameters:
        query: "React developer"
        location: "New York"
        concurrent_users: 100
        ramp_up_time_seconds: 10

  then:
    - expectation: "95th percentile response time is under 500ms"
      verification_method: "Measure response times, calculate p95, assert p95 < 500ms"
    - expectation: "99th percentile response time is under 1000ms"
      verification_method: "Calculate p99, assert p99 < 1000ms"
    - expectation: "Zero failed requests"
      verification_method: "Assert all requests return HTTP 200"
    - expectation: "All responses return valid results"
      verification_method: "Assert each response has jobs array with length > 0"

  assertions:
    - assertion_type: "less_than"
      target: "response_time_p95"
      expected_value: 500
    - assertion_type: "less_than"
      target: "response_time_p99"
      expected_value: 1000
    - assertion_type: equals
      target: "error_rate"
      expected_value: 0

  automated: true
  automation_framework: "k6"
  author: "performance_team"
  created_date: "2025-10-04"
  tags: ["performance", "load_test", "bc_job_catalog", "api", "non_functional"]
```

**What This Tests:**
- Performance target: 500ms response time
- Scalability under concurrent load
- Search algorithm efficiency
- Database query performance
- Non-functional requirement

### Example 7: Security Test

**Scenario**: Test that users can only view their own applications.

```yaml
test_case:
  id: "tc_sec_007_application_authorization"
  name: "Users Can Only Access Their Own Applications"
  description: "Verify that attempting to access another user's application via API returns 403 Forbidden and does not leak data"

  test_level: system
  test_type: security
  test_priority: critical

  ddd_references:
    bounded_context: "bc_applications"
    application_service: "svc_app_get_application"
    repository: "repo_job_application"

  given:
    - condition: "Two candidates exist in system"
      setup: "Create candidate_001 (John) and candidate_002 (Jane) with authentication tokens"
    - condition: "John has submitted application APP-001"
      setup: "Create application APP-001 owned by candidate_001"
    - condition: "Jane has submitted application APP-002"
      setup: "Create application APP-002 owned by candidate_002"

  when:
    - action: "John attempts to access Jane's application (APP-002)"
      parameters:
        endpoint: "GET /api/applications/APP-002"
        auth_token: "john_token"

  then:
    - expectation: "Request returns HTTP 403 Forbidden"
      verification_method: "Assert response.status == 403"
    - expectation: "Response does not include application data"
      verification_method: "Assert response.body does not contain application details"
    - expectation: "Error message is generic and does not confirm existence"
      verification_method: "Assert response.body.message == 'Access denied' (not 'Application belongs to another user')"
    - expectation: "Security event logged"
      verification_method: "Assert security log contains unauthorized access attempt with user_id and resource_id"

  test_steps:
    - step_number: 1
      action: "Authenticate as John (candidate_001)"
      expected_result: "Receive valid JWT token"

    - step_number: 2
      action: "Send GET request to /api/applications/APP-001 with John's token"
      expected_result: "HTTP 200, application data returned"

    - step_number: 3
      action: "Send GET request to /api/applications/APP-002 with John's token"
      expected_result: "HTTP 403, error message 'Access denied'"

    - step_number: 4
      action: "Verify response body does not leak application data"
      expected_result: "Response does not include job_id, candidate_id, or any application details"

    - step_number: 5
      action: "Check security audit log"
      expected_result: "Log entry created: 'UNAUTHORIZED_ACCESS_ATTEMPT: user=candidate_001, resource=APP-002'"

  automated: true
  automation_framework: "jest"
  author: "security_team"
  created_date: "2025-10-04"
  tags: ["security", "authorization", "bc_applications", "critical", "owasp"]
```

**What This Tests:**
- Authorization enforcement
- Data access control
- Information disclosure prevention
- Security audit logging
- OWASP security best practices

### Example 8: Test Data Definition

**Scenario**: Reusable test data for candidate profiles.

```yaml
test_data:
  id: "td_candidate_profiles_valid"
  name: "Valid Candidate Profile Test Data"
  description: "Collection of valid candidate profiles satisfying all domain invariants for use in integration and E2E tests"

  bounded_context: "bc_profile"
  aggregate_type: "agg_candidate_profile"

  data:
    # Profile 1: Entry-level candidate
    - candidate:
        id: "candidate_001"
        email: "john.doe@example.com"  # vo_email compliant
        first_name: "John"
        last_name: "Doe"
        skills: ["Python", "JavaScript", "SQL"]  # vo_skills compliant
        experience_years: 2  # vo_experience_years compliant (0-50)
        location: "New York, NY"  # vo_location compliant
        education_level: "bachelor"  # enum compliant
        profile_complete: true
        created_at: "2025-01-15T10:00:00Z"
        updated_at: "2025-10-01T14:30:00Z"

    # Profile 2: Senior candidate
    - candidate:
        id: "candidate_002"
        email: "jane.smith@example.com"
        first_name: "Jane"
        last_name: "Smith"
        skills: ["React", "TypeScript", "Node.js", "PostgreSQL", "AWS"]
        experience_years: 8
        location: "San Francisco, CA"
        education_level: "master"
        profile_complete: true
        created_at: "2024-06-20T08:00:00Z"
        updated_at: "2025-09-28T16:45:00Z"

    # Profile 3: Incomplete profile (for negative testing)
    - candidate:
        id: "candidate_003"
        email: "incomplete@example.com"
        first_name: "Incomplete"
        last_name: "Profile"
        skills: []  # Invalid: skills required
        experience_years: 0
        location: ""  # Invalid: location required
        education_level: "bachelor"
        profile_complete: false
        created_at: "2025-10-04T09:00:00Z"
        updated_at: "2025-10-04T09:00:00Z"

  satisfies_invariants: true
  valid_for_contexts: ["bc_profile", "bc_applications", "bc_matching"]

  reusable: true
  tags: ["candidate", "profile", "valid_data", "integration_test"]
```

**What This Provides:**
- Reusable test data across multiple tests
- Data that satisfies domain invariants
- Examples of valid and invalid states
- Documentation of value object constraints

## Validation and Tooling

### Schema Validation Rules

The schema defines validation rules to ensure test specifications are complete and correct:

**Test Case Validation:**
```yaml
validation_rules:
  test_case:
    - rule: "id must follow pattern tc_[level]_[number]_[name]"
      example: "tc_unit_001_email_validation"

    - rule: "test_level must be one of: unit, integration, system, acceptance, ui, e2e"

    - rule: "if ddd_references.value_object specified, must match vo_[name] pattern"
      example: "vo_email, vo_skills"

    - rule: "if ux_references.page specified, must match page_[name] pattern"
      example: "page_profile_edit, page_job_detail"

    - rule: "test_priority must be specified"
      values: ["critical", "high", "medium", "low"]

    - rule: "at least one test step or BDD given-when-then required"
```

**Test Data Validation:**
```yaml
validation_rules:
  test_data:
    - rule: "data must satisfy domain invariants if satisfies_invariants=true"
      validation: "Validate against domain model rules"

    - rule: "data structure must match referenced aggregate structure"
      validation: "Compare data keys to aggregate properties"
```

**Defect Validation:**
```yaml
validation_rules:
  defect:
    - rule: "severity and priority must both be specified"

    - rule: "steps_to_reproduce required"

    - rule: "if status=resolved, fix_description required"
```

### Validation Checklist

Before finalizing a test specification, verify:

- [ ] **ID Format**: Follows naming convention for entity type
- [ ] **Classification**: test_level, test_type, test_priority all specified
- [ ] **References**: DDD/UX references use correct prefixes and exist in design artifacts
- [ ] **Test Definition**: Has either BDD steps OR traditional test steps (not both)
- [ ] **Completeness**: All required fields populated
- [ ] **Traceability**: References link to requirements, user stories, or design docs
- [ ] **Automation**: If automated=true, automation_framework specified
- [ ] **Tags**: Descriptive tags for filtering and reporting
- [ ] **Data**: test_data_refs point to defined test_data entities

### Recommended Tooling

**YAML Validators:**
- **yamllint**: Lint YAML files for syntax errors
  ```bash
  yamllint test-specs/*.yaml
  ```

- **JSON Schema Validator**: Validate against JSON Schema representation of this schema
  ```bash
  ajv validate -s qe-schema.json -d test-case.yaml
  ```

**Custom Validation Scripts:**

Create scripts to validate schema-specific rules:

```typescript
// validate-test-spec.ts
import { loadYaml } from './yaml-loader';
import { validateReferences } from './reference-validator';

function validateTestCase(testCase: TestCase): ValidationResult {
  const errors: string[] = [];

  // Validate ID format
  if (!testCase.id.match(/^tc_[a-z0-9]+_\d{3}_[a-z0-9_]+$/)) {
    errors.push(`Invalid ID format: ${testCase.id}`);
  }

  // Validate DDD references
  if (testCase.ddd_references) {
    const dddErrors = validateReferences(
      testCase.ddd_references,
      dddRegistry
    );
    errors.push(...dddErrors);
  }

  // Validate UX references
  if (testCase.ux_references) {
    const uxErrors = validateReferences(
      testCase.ux_references,
      uxRegistry
    );
    errors.push(...uxErrors);
  }

  // Validate test definition
  const hasGWT = testCase.given && testCase.when && testCase.then;
  const hasSteps = testCase.test_steps && testCase.test_steps.length > 0;

  if (!hasGWT && !hasSteps) {
    errors.push('Test case must have either BDD steps or test steps');
  }

  return {
    valid: errors.length === 0,
    errors
  };
}
```

**Test Coverage Analysis:**

Track which domain objects and UX elements have test coverage:

```typescript
// coverage-analyzer.ts
function analyzeDDDCoverage(testCases: TestCase[]): CoverageReport {
  const allValueObjects = loadDDDRegistry().valueObjects;
  const testedValueObjects = new Set(
    testCases
      .filter(tc => tc.ddd_references?.value_object)
      .map(tc => tc.ddd_references.value_object)
  );

  const untested = allValueObjects.filter(
    vo => !testedValueObjects.has(vo.id)
  );

  return {
    total: allValueObjects.length,
    covered: testedValueObjects.size,
    coverage_percentage: (testedValueObjects.size / allValueObjects.length) * 100,
    untested_items: untested
  };
}
```

**Test Generation:**

Generate test skeletons from domain models:

```typescript
// test-generator.ts
function generateUnitTestForValueObject(vo: ValueObject): TestCase {
  return {
    id: `tc_unit_XXX_${vo.name}_validation`,
    name: `${vo.name} Validation`,
    description: `Verify ${vo.name} enforces business rules`,
    test_level: 'unit',
    test_type: 'functional',
    test_priority: 'high',
    ddd_references: {
      bounded_context: vo.bounded_context,
      value_object: vo.id
    },
    given: [
      { condition: 'Valid and invalid input data', setup: 'TODO: Define test data' }
    ],
    when: [
      { action: `Create ${vo.name} with test data`, parameters: {} }
    ],
    then: [
      { expectation: 'Valid data creates object', verification_method: 'TODO: Define assertion' },
      { expectation: 'Invalid data throws error', verification_method: 'TODO: Define assertion' }
    ],
    automated: false,
    tags: ['unit', 'value_object', vo.bounded_context, 'generated']
  };
}
```

### CI/CD Integration

**Pre-commit Validation:**

```yaml
# .pre-commit-config.yaml
repos:
  - repo: local
    hooks:
      - id: validate-test-specs
        name: Validate Test Specifications
        entry: npm run validate:test-specs
        language: system
        files: test-specs/.*\.yaml$
        pass_filenames: true
```

**GitHub Actions Workflow:**

```yaml
# .github/workflows/validate-tests.yml
name: Validate Test Specifications

on: [pull_request]

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Validate YAML Syntax
        run: yamllint test-specs/

      - name: Validate Schema Compliance
        run: npm run validate:test-specs

      - name: Check DDD/UX References
        run: npm run validate:references

      - name: Generate Coverage Report
        run: npm run test:coverage-report

      - name: Comment on PR
        if: always()
        uses: actions/github-script@v6
        with:
          script: |
            const report = require('./coverage-report.json');
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `## Test Coverage Report\n\nDDD Coverage: ${report.ddd_coverage}%\nUX Coverage: ${report.ux_coverage}%`
            });
```

## Integration with Test Management

### Exporting to Test Management Tools

The YAML schema can be converted to formats accepted by popular test management tools:

**TestRail Integration:**

```typescript
// export-to-testrail.ts
function convertToTestRail(testCase: TestCase): TestRailCase {
  return {
    title: testCase.name,
    section_id: mapBoundedContextToSection(testCase.ddd_references?.bounded_context),
    type_id: mapTestTypeToTestRail(testCase.test_type),
    priority_id: mapPriorityToTestRail(testCase.test_priority),
    custom_fields: {
      custom_test_level: testCase.test_level,
      custom_ddd_context: testCase.ddd_references?.bounded_context,
      custom_automated: testCase.automated,
      custom_tags: testCase.tags?.join(', ')
    },
    custom_steps: testCase.test_steps?.map(step => ({
      content: step.action,
      expected: step.expected_result
    })) || convertGWTToSteps(testCase)
  };
}
```

**Jira Xray Integration:**

```typescript
// export-to-xray.ts
function convertToXray(testCase: TestCase): XrayTest {
  return {
    fields: {
      project: { key: 'JOBSEEKER' },
      issuetype: { name: 'Test' },
      summary: testCase.name,
      description: testCase.description,
      labels: testCase.tags,
      customfield_test_type: testCase.test_type,
      customfield_automated: testCase.automated ? 'Yes' : 'No'
    },
    testSteps: testCase.test_steps || convertGWTToSteps(testCase),
    preconditions: testCase.preconditions?.join('\n'),
    testScript: {
      type: testCase.automated ? 'Cucumber' : 'Manual',
      steps: formatStepsForXray(testCase)
    }
  };
}
```

**Azure DevOps Integration:**

```typescript
// export-to-azure.ts
function convertToAzureTestCase(testCase: TestCase): AzureTestCase {
  return {
    workItemType: 'Test Case',
    fields: {
      'System.Title': testCase.name,
      'System.Description': testCase.description,
      'Microsoft.VSTS.TCM.Steps': formatStepsForAzure(testCase.test_steps),
      'System.Tags': testCase.tags?.join('; '),
      'Custom.TestLevel': testCase.test_level,
      'Custom.Priority': testCase.test_priority,
      'Custom.Automated': testCase.automated
    },
    links: [
      {
        rel: 'System.LinkTypes.TestedBy',
        url: testCase.ux_references?.page ? getPageWorkItemUrl(testCase.ux_references.page) : null
      }
    ]
  };
}
```

### Importing Test Results

After test execution, import results back to YAML for tracking:

```typescript
// import-test-results.ts
function updateTestCaseWithResults(
  testCase: TestCase,
  executionResult: TestExecutionResult
): TestCase {
  return {
    ...testCase,
    status: executionResult.passed ? 'passed' : 'failed',
    execution_time_ms: executionResult.duration,
    failure_message: executionResult.error?.message,
    test_steps: testCase.test_steps?.map((step, i) => ({
      ...step,
      actual_result: executionResult.stepResults[i]?.actual,
      status: executionResult.stepResults[i]?.passed ? 'passed' : 'failed'
    })),
    last_executed: new Date().toISOString()
  };
}
```

### Generating Test Reports

Create comprehensive reports from YAML test specifications:

```typescript
// generate-report.ts
function generateTestReport(testCases: TestCase[]): TestReport {
  return {
    summary: {
      total_tests: testCases.length,
      by_level: countByLevel(testCases),
      by_type: countByType(testCases),
      by_status: countByStatus(testCases),
      automation_rate: calculateAutomationRate(testCases)
    },
    coverage: {
      ddd_coverage: calculateDDDCoverage(testCases),
      ux_coverage: calculateUXCoverage(testCases),
      requirements_coverage: calculateRequirementsCoverage(testCases)
    },
    quality: {
      pass_rate: calculatePassRate(testCases),
      average_execution_time: calculateAvgExecutionTime(testCases),
      flaky_tests: identifyFlakyTests(testCases)
    },
    test_cases_by_context: groupByBoundedContext(testCases),
    test_cases_by_priority: groupByPriority(testCases)
  };
}
```

### Test Execution Plans

Use test execution plans to organize test runs:

```yaml
test_execution_plan:
  id: "tep_release_1_0_smoke_tests"
  name: "Release 1.0 Smoke Test Suite"
  description: "Critical path tests to validate release 1.0 before production deployment"

  test_suite_refs:
    - "ts_profile_critical"
    - "ts_applications_critical"

  test_case_refs:
    - "tc_e2e_004_submit_application_workflow"
    - "tc_int_002_update_candidate_profile"
    - "tc_sec_007_application_authorization"

  execution_order: priority_based
  max_parallel: 5

  environment: staging

  trigger: manual

  ci_pipeline_stage: "pre_production_validation"

  notify_on_failure:
    - "qa-team@example.com"
    - "#releases-channel"

  timeout_minutes: 30
```

### Continuous Testing Workflow

**1. Development Phase:**
- Developer writes code
- Generates test cases from domain models (optional)
- Writes test implementations referencing YAML specs
- Runs unit and integration tests locally

**2. Pull Request:**
- CI validates YAML test specs
- Runs automated tests
- Reports coverage gaps
- Blocks merge if critical tests fail

**3. Pre-deployment:**
- Execute test execution plan for release
- Run E2E tests in staging environment
- Validate accessibility and performance
- Generate test report

**4. Post-deployment:**
- Run smoke tests in production
- Monitor for defects
- Create defect entries in YAML
- Link defects to test cases

**5. Maintenance:**
- Update test specs when requirements change
- Archive obsolete tests
- Analyze test metrics
- Improve test coverage

## Summary

This guide has covered the QE YAML Schema comprehensively:

1. **Schema Overview**: Formal language bridging DDD, UX, and QE
2. **Entity Types**: Test suites, cases, scenarios, data, automation config, execution plans, defects
3. **Writing Specifications**: Structured format with consistent naming and classification
4. **DDD/UX Integration**: Traceability to domain models and UX designs
5. **BDD Format**: Given-When-Then for behavior-driven testing
6. **Complete Examples**: From unit tests to E2E workflows
7. **Validation**: Rules, checklists, and automated validation
8. **Integration**: Export/import to test management tools, CI/CD workflows

### Key Takeaways

- **Traceability**: Every test references what it tests (DDD construct, UX element)
- **Consistency**: Standardized structure across all test levels
- **Automation-Friendly**: Machine-readable format for tools and CI/CD
- **Living Documentation**: Tests document system behavior and design
- **Quality Metrics**: Built-in support for tracking coverage and quality

### Getting Started

1. **Define your first test suite** for a bounded context
2. **Create test cases** for critical value objects and aggregates
3. **Link to DDD and UX references** from your design artifacts
4. **Validate** using schema validation rules
5. **Automate** test execution and reporting
6. **Iterate** based on coverage gaps and quality metrics

### Next Steps

- Review existing tests and migrate to YAML schema
- Set up validation in CI/CD pipeline
- Create test data definitions for reusable fixtures
- Generate coverage reports
- Integrate with your test management tool

The QE YAML Schema provides a solid foundation for comprehensive, maintainable, and traceable test specifications that grow with your system.
