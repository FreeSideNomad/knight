# Domain V: Test Types and Characteristics with Complete DDD and UX Integration

**Date:** 2025-10-04
**Status:** Complete Working Document
**Purpose:** Comprehensive research on test types (functional, non-functional, regression, smoke, sanity) integrated with Domain-Driven Design patterns and UX architecture for the Job Seeker application

---

## Executive Summary

This document provides authoritative research on test types as defined by ISTQB and ISO 25010, with complete integration to Domain-Driven Design (DDD) tactical and strategic patterns, and UX architecture patterns (Atomic Design, page types, workflows).

**Covered Test Types:**
1. **Functional Testing** - Requirements-based testing, black-box techniques
2. **Non-Functional Testing** (ISO 25010) - Performance, Security, Usability, Accessibility, Reliability, Maintainability, Portability, Compatibility
3. **Regression Testing** - Change impact verification
4. **Smoke and Sanity Testing** - Build verification and subset testing

Each test type is mapped to:
- **DDD constructs**: What domain patterns to test (Value Objects, Entities, Aggregates, Bounded Contexts)
- **UX patterns**: What interface patterns to test (Atoms/Molecules, Organisms/Pages, Workflows)
- **Job Seeker examples**: Concrete examples from the Job Seeker application domain
- **Tools and techniques**: Industry-standard testing tools and approaches
- **Metrics and acceptance criteria**: Measurable quality indicators

**Key Integration Points:**
- Functional tests validate domain behavior and UI interactions
- Non-functional tests verify quality characteristics across all layers
- Regression tests ensure domain invariants and UX patterns remain intact
- Smoke/sanity tests verify critical paths through bounded contexts and workflows

---

## Test Types Overview

```
Test Types Hierarchy
│
├── Functional Testing
│   ├── Requirements-based testing
│   ├── Black-box techniques
│   └── User acceptance testing
│
├── Non-Functional Testing (ISO 25010)
│   ├── Performance Efficiency
│   ├── Security
│   ├── Usability
│   ├── Accessibility
│   ├── Reliability
│   ├── Maintainability
│   ├── Portability
│   └── Compatibility
│
├── Regression Testing
│   ├── Full regression
│   ├── Selective regression
│   └── Progressive regression
│
└── Smoke & Sanity Testing
    ├── Smoke testing (build verification)
    └── Sanity testing (subset verification)
```

---

## 1. Functional Testing

### ISTQB Definition

**Functional Testing** evaluates functions that the system should perform. Functional requirements may be described in work products such as business requirements specifications, epics, user stories, use cases, or functional specifications, or they may be undocumented.

**Key Characteristics**:
- Tests **what** the system does
- Based on requirements, specifications, user stories
- Black-box approach (no knowledge of internal structure)
- Validates business rules and domain logic
- Verifies user interactions and workflows

---

### Requirements-Based Testing

**Definition**: Testing based on explicitly stated requirements, ensuring each requirement is covered by at least one test case.

**Traceability Matrix**:
```yaml
requirement_id: REQ-001
description: "Candidate must have at least one skill to complete profile"
test_cases:
  - tc_func_001_profile_completeness_validation
  - tc_func_002_profile_save_with_zero_skills
  - tc_func_003_profile_edit_remove_last_skill
ddd_reference:
  bounded_context: bc_profile
  aggregate: agg_candidate_profile
  invariant: "skills.length >= 1"
```

---

### Black-Box Testing Techniques

**1. Equivalence Partitioning**

Divides input data into valid and invalid partitions where all values in a partition are expected to be treated the same.

**Job Seeker Example: `vo_experience_years`**
```yaml
input_partitions:
  invalid_low: [-1, -10]  # Invalid: negative years
  valid_range: [0, 1, 5, 10, 20]  # Valid: 0-50 years
  invalid_high: [51, 100]  # Invalid: > 50 years

test_cases:
  - id: tc_func_004_exp_years_negative
    input: -1
    expected: ValidationException("Experience years cannot be negative")

  - id: tc_func_005_exp_years_valid
    input: 5
    expected: ExperienceYears(5) created successfully

  - id: tc_func_006_exp_years_excessive
    input: 51
    expected: ValidationException("Experience years cannot exceed 50")

ddd_reference:
  value_object: vo_experience_years
  validation_rule: "0 <= years <= 50"
```

**2. Boundary Value Analysis**

Tests values at boundaries where behavior changes.

**Job Seeker Example: `vo_skills` (minimum 1, maximum 20 skills)**
```yaml
boundary_values:
  - 0 skills (invalid - below minimum)
  - 1 skill (valid - minimum boundary)
  - 2 skills (valid - just above minimum)
  - 19 skills (valid - just below maximum)
  - 20 skills (valid - maximum boundary)
  - 21 skills (invalid - above maximum)

test_cases:
  - id: tc_func_007_skills_zero
    skills: []
    expected: "ValidationException: At least 1 skill required"

  - id: tc_func_008_skills_minimum
    skills: ["Python"]
    expected: Skills created with 1 skill

  - id: tc_func_009_skills_maximum
    skills: [20 unique skills]
    expected: Skills created with 20 skills

  - id: tc_func_010_skills_exceed_max
    skills: [21 unique skills]
    expected: "ValidationException: Maximum 20 skills allowed"

ddd_reference:
  value_object: vo_skills
  aggregate: agg_candidate_profile
  invariant: "1 <= skills.length <= 20"
```

**3. Decision Table Testing**

Tests combinations of conditions and their corresponding actions.

**Job Seeker Example: Application Submission Eligibility**
```yaml
decision_table:
  conditions:
    - profile_complete: [T, T, T, T, F, F, F, F]
    - job_active: [T, T, F, F, T, T, F, F]
    - already_applied: [T, F, T, F, T, F, T, F]

  actions:
    - allow_submit: [F, T, F, F, F, F, F, F]
    - show_message:
      - "Already applied for this job"
      - "Submit application"
      - "Job no longer active"
      - "Job no longer active"
      - "Please complete your profile"
      - "Please complete your profile"
      - "Job no longer active"
      - "Please complete your profile"

test_cases:
  - id: tc_func_011_submit_eligible
    profile_complete: true
    job_active: true
    already_applied: false
    expected: Application submitted successfully

  - id: tc_func_012_submit_already_applied
    profile_complete: true
    job_active: true
    already_applied: true
    expected: Error "Already applied for this job"

ddd_reference:
  bounded_context: bc_applications
  application_service: svc_app_submit_application
  domain_rules:
    - "Profile must be complete"
    - "Job must be active"
    - "Candidate must not have existing application for this job"
```

**4. State Transition Testing**

Tests different system states and transitions between them.

**Job Seeker Example: Application Status State Machine**
```yaml
states:
  - DRAFT (initial)
  - SUBMITTED
  - UNDER_REVIEW
  - INTERVIEW_SCHEDULED
  - REJECTED
  - ACCEPTED
  - WITHDRAWN

transitions:
  - from: DRAFT
    to: SUBMITTED
    event: "Candidate submits application"
    test: tc_func_013_submit_draft_application

  - from: SUBMITTED
    to: UNDER_REVIEW
    event: "Recruiter reviews application"
    test: tc_func_014_review_submitted_application

  - from: UNDER_REVIEW
    to: INTERVIEW_SCHEDULED
    event: "Recruiter schedules interview"
    test: tc_func_015_schedule_interview

  - from: SUBMITTED
    to: WITHDRAWN
    event: "Candidate withdraws application"
    test: tc_func_016_withdraw_application

  - from: INTERVIEW_SCHEDULED
    to: ACCEPTED
    event: "Recruiter accepts candidate"
    test: tc_func_017_accept_candidate

invalid_transitions:
  - from: DRAFT
    to: ACCEPTED
    expected: "InvalidStateTransitionException"
    test: tc_func_018_invalid_draft_to_accepted

ddd_reference:
  aggregate: agg_application
  aggregate_root: ent_application
  value_object: vo_application_status
  domain_events:
    - evt_application_submitted
    - evt_application_reviewed
    - evt_interview_scheduled
    - evt_application_withdrawn
    - evt_application_accepted
```

---

### DDD Integration: Testing Domain Behavior

**Testing Value Objects**:
```yaml
test_suite: ts_vo_email_functional
test_cases:
  - id: tc_func_019_email_valid_format
    input: "candidate@example.com"
    expected: Email("candidate@example.com")
    ddd_pattern: value_object

  - id: tc_func_020_email_invalid_no_at
    input: "candidateexample.com"
    expected: ValidationException("Invalid email format")
    ddd_pattern: value_object

  - id: tc_func_021_email_equality
    email1: "test@example.com"
    email2: "test@example.com"
    expected: email1.equals(email2) == true
    ddd_pattern: value_object_equality
```

**Testing Aggregates**:
```yaml
test_suite: ts_agg_candidate_profile_functional
test_cases:
  - id: tc_func_022_profile_enforce_invariants
    given: "Profile with skills=['Python']"
    when: "Attempt to remove all skills"
    then: "DomainException: Profile must have at least 1 skill"
    ddd_pattern: aggregate_invariant

  - id: tc_func_023_profile_publish_events
    given: "Existing profile"
    when: "Update profile with new skill"
    then:
      - "Profile updated in aggregate"
      - "CandidateProfileUpdated event published"
    ddd_pattern: domain_event
```

**Testing Application Services**:
```yaml
test_suite: ts_svc_submit_application_functional
test_cases:
  - id: tc_func_024_submit_application_happy_path
    given:
      - "Complete candidate profile"
      - "Active job posting"
      - "No existing application"
    when: "SubmitApplicationService.execute(candidate_id, job_id)"
    then:
      - "Application created with SUBMITTED status"
      - "Application saved to repository"
      - "ApplicationSubmitted event published"
      - "Email notification queued"
    ddd_pattern: application_service_orchestration
```

---

### UX Integration: Testing User Interactions

**Testing Atoms**:
```yaml
test_suite: ts_atom_button_functional
test_cases:
  - id: tc_func_025_button_click
    given: "Button rendered with onClick handler"
    when: "User clicks button"
    then: "onClick handler invoked"
    ux_pattern: atom_interaction

  - id: tc_func_026_button_disabled
    given: "Button with disabled=true"
    when: "User clicks button"
    then: "onClick handler NOT invoked"
    ux_pattern: atom_state
```

**Testing Molecules**:
```yaml
test_suite: ts_comp_email_input_functional
test_cases:
  - id: tc_func_027_email_input_validation
    given: "Email input field"
    when: "User enters 'invalid@'"
    and: "User tabs out (blur event)"
    then: "Error message displays: 'Invalid email format'"
    ux_pattern: molecule_validation
    ddd_integration: "Validation matches vo_email rules"
```

**Testing Organisms**:
```yaml
test_suite: ts_org_job_card_functional
test_cases:
  - id: tc_func_028_job_card_display
    given: "Job with match_score=85"
    when: "Job card renders"
    then:
      - "Job title displayed"
      - "Company name displayed"
      - "Match score badge shows '85% Match'"
      - "Skills gap displayed if mismatch"
    ux_pattern: organism_composition
    ddd_integration: "Displays agg_job data"

  - id: tc_func_029_job_card_click
    given: "Job card in list"
    when: "User clicks job card"
    then: "Navigate to page_job_detail"
    ux_pattern: organism_navigation
```

**Testing Pages**:
```yaml
test_suite: ts_page_profile_edit_functional
test_cases:
  - id: tc_func_030_profile_edit_load
    given: "Authenticated candidate"
    when: "Navigate to page_profile_edit"
    then:
      - "Profile data loaded from repo_candidate_profile"
      - "Form fields populated with current data"
      - "Skills displayed as tags"
    ux_pattern: page_data_loading
    ddd_integration: "Loads agg_candidate_profile"

  - id: tc_func_031_profile_edit_save
    given: "Profile edit page with modified data"
    when: "User clicks 'Save' button"
    then:
      - "svc_app_update_profile called"
      - "Success toast displays"
      - "Form marked as pristine"
    ux_pattern: page_form_submission
    ddd_integration: "Calls application service"
```

**Testing Workflows**:
```yaml
test_suite: ts_wf_submit_application_functional
workflow: wf_submit_application
test_cases:
  - id: tc_func_032_submit_application_workflow
    steps:
      - step: 1
        page: page_job_detail
        action: "Click 'Apply for Job'"
        expected: "Application wizard opens (Step 1)"

      - step: 2
        page: page_application_wizard_step1
        action: "Review job details, click 'Next'"
        expected: "Navigate to Step 2 (profile review)"

      - step: 3
        page: page_application_wizard_step2
        action: "Review profile, click 'Submit'"
        expected: "Loading spinner displays"

      - step: 4
        page: page_application_wizard_step2
        action: "Wait for submission"
        expected: "Navigate to confirmation page"

      - step: 5
        page: page_application_confirmation
        action: "Verify confirmation"
        expected:
          - "Application ID displayed"
          - "Success message shown"
          - "CTA to view 'My Applications'"

    ux_pattern: workflow_completion
    ddd_integration: "Calls bc_applications context"
```

---

### Functional Test Metrics

**Requirements Coverage**:
```yaml
metrics:
  total_requirements: 150
  covered_requirements: 145
  coverage_percentage: 96.7%
  target: ">95%"

uncovered_requirements:
  - REQ-127: "Export application history as PDF"
  - REQ-128: "Bulk application withdrawal"
  - REQ-131: "Application analytics dashboard"
  - REQ-139: "Share profile via link"
  - REQ-142: "Skills endorsement from connections"
```

**Functional Test Pass Rate**:
```yaml
execution_summary:
  total_tests: 420
  passed: 405
  failed: 10
  blocked: 5
  pass_rate: 96.4%
  target: ">95%"

failed_tests:
  - tc_func_033_special_char_in_company_name (BUG-045)
  - tc_func_034_concurrent_profile_updates (BUG-046)
```

---

## 2. Non-Functional Testing (ISO 25010 Quality Characteristics)

### Overview

**ISO 25010** defines the quality model for software products, organizing quality characteristics into 8 categories. Non-functional testing evaluates **how well** the system performs its functions.

**ISO 25010 Quality Characteristics**:
1. Performance Efficiency
2. Security
3. Usability
4. Compatibility
5. Reliability
6. Maintainability
7. Portability
8. (Functional Suitability - covered in Functional Testing)

---

## 2.1 Performance Efficiency Testing

### ISTQB/ISO Definition

**Performance Efficiency** relates to the performance relative to the amount of resources used under stated conditions. Sub-characteristics include:
- **Time Behavior**: Response times, throughput
- **Resource Utilization**: CPU, memory, network
- **Capacity**: Maximum limits the system can handle

---

### Performance Test Types

**1. Load Testing**

**Definition**: Testing system behavior under expected production load.

**Job Seeker Example: Job Search Page**
```yaml
test_case:
  id: tc_perf_001_job_search_load
  test_type: load_testing

  scenario: "100 concurrent users searching for jobs"

  load_profile:
    concurrent_users: 100
    ramp_up_time: 60 seconds
    duration: 10 minutes

  user_actions:
    - action: "Navigate to page_job_search"
      weight: 30%
    - action: "Enter search criteria and submit"
      weight: 40%
    - action: "Apply filters (location, skills)"
      weight: 20%
    - action: "Click job card to view detail"
      weight: 10%

  acceptance_criteria:
    response_time_p50: "<1000ms"
    response_time_p95: "<2000ms"
    response_time_p99: "<3000ms"
    error_rate: "<0.1%"
    throughput: ">50 requests/second"

  ddd_integration:
    bounded_context: bc_job_catalog
    repository: repo_job_search
    query: "SearchJobsQuery with filters"

  tools:
    - k6
    - JMeter
    - Gatling
```

**2. Stress Testing**

**Definition**: Testing system behavior under extreme load to find breaking points.

**Job Seeker Example: Application Submission**
```yaml
test_case:
  id: tc_perf_002_submit_application_stress
  test_type: stress_testing

  scenario: "Increase load until system breaks"

  load_profile:
    start_users: 100
    increment: 50 users every 2 minutes
    max_users: 500
    duration: 20 minutes

  breaking_point_indicators:
    - response_time: ">10 seconds"
    - error_rate: ">5%"
    - cpu_utilization: ">90%"
    - database_connections: "Pool exhausted"

  acceptance_criteria:
    graceful_degradation: "System shows friendly error, doesn't crash"
    recovery_time: "<5 minutes after load reduced"
    data_integrity: "No corrupted applications"

  ddd_integration:
    bounded_context: bc_applications
    application_service: svc_app_submit_application
    aggregate: agg_application

  results:
    breaking_point: 450 concurrent users
    failure_mode: "Database connection pool exhausted"
    degradation: "Graceful - queue system activated"
```

**3. Endurance/Soak Testing**

**Definition**: Testing system stability over extended periods.

**Job Seeker Example: Background Job Processor**
```yaml
test_case:
  id: tc_perf_003_match_calculation_endurance
  test_type: endurance_testing

  scenario: "Match score calculation runs continuously"

  load_profile:
    concurrent_users: 50
    duration: 24 hours
    constant_load: true

  monitored_metrics:
    - memory_usage: "Check for memory leaks"
    - database_connections: "Check for connection leaks"
    - response_time_degradation: "Check for performance decay"
    - error_rate_over_time: "Check for accumulated errors"

  acceptance_criteria:
    memory_growth: "<10% over 24 hours"
    response_time_stability: "P95 within ±10% throughout test"
    zero_critical_errors: true

  ddd_integration:
    bounded_context: bc_matching
    domain_service: svc_dom_match_calculator

  tools:
    - k6 with extended duration
    - Prometheus + Grafana for monitoring
```

**4. Spike Testing**

**Definition**: Testing system response to sudden, dramatic load increases.

**Job Seeker Example: New Job Posting Notification**
```yaml
test_case:
  id: tc_perf_004_new_job_notification_spike
  test_type: spike_testing

  scenario: "Popular company posts new job, thousands of candidates notified"

  load_profile:
    baseline_users: 50
    spike_users: 500
    spike_duration: 2 minutes
    return_to_baseline: 1 minute

  spike_trigger: "New job posted → notifications sent"

  acceptance_criteria:
    response_during_spike: "<5 seconds"
    no_dropped_notifications: true
    recovery_time: "<2 minutes"
    error_rate_spike: "<1%"

  ddd_integration:
    bounded_context: bc_notifications
    domain_event: evt_job_posted
    event_handler: "NotifyMatchedCandidatesHandler"

  ux_integration:
    notification_component: comp_notification_toast
    page_impact: "page_job_listings refreshes"
```

**5. Volume Testing**

**Definition**: Testing system with large volumes of data.

**Job Seeker Example: Job Search with 1 Million Jobs**
```yaml
test_case:
  id: tc_perf_005_job_search_volume
  test_type: volume_testing

  scenario: "Search across 1 million job postings"

  data_volume:
    total_jobs: 1_000_000
    active_jobs: 250_000
    indexed_fields: ["title", "description", "skills", "location"]

  test_queries:
    - query: "Python developer in New York"
      expected_results: ~5000
    - query: "Senior React engineer remote"
      expected_results: ~1200
    - query: "Java" (broad search)
      expected_results: ~50000

  acceptance_criteria:
    search_response_time: "<500ms"
    pagination_response: "<200ms"
    filter_application: "<300ms"
    accurate_results: "100% relevance for exact matches"

  ddd_integration:
    repository: repo_job_search
    specification: spec_job_search_criteria

  tools:
    - Elasticsearch for search indexing
    - Database query performance testing
```

**6. Scalability Testing**

**Definition**: Testing system's ability to scale up/down with load.

**Job Seeker Example: Horizontal Scaling**
```yaml
test_case:
  id: tc_perf_006_application_service_scalability
  test_type: scalability_testing

  scenario: "Scale application servers based on load"

  scaling_tests:
    - instances: 1
      concurrent_users: 100
      response_time_p95: 800ms

    - instances: 2
      concurrent_users: 200
      response_time_p95: 850ms
      expected_improvement: "Linear scaling"

    - instances: 4
      concurrent_users: 400
      response_time_p95: 900ms

    - instances: 8
      concurrent_users: 800
      response_time_p95: 950ms

  acceptance_criteria:
    linear_scalability: "Each instance adds ~100 concurrent user capacity"
    response_time_degradation: "<20% as instances increase"

  infrastructure:
    container_orchestration: Kubernetes
    auto_scaling_policy: "CPU > 70% for 2 minutes"
```

**7. Response Time Testing**

**Definition**: Measuring time taken for system to respond to user actions.

**Job Seeker Example: Page Load Performance**
```yaml
test_case:
  id: tc_perf_007_page_load_times
  test_type: response_time_testing

  pages_tested:
    - page: page_job_listings
      target_load_time: "<2 seconds"
      measured_metrics:
        - time_to_first_byte: 200ms
        - first_contentful_paint: 800ms
        - time_to_interactive: 1800ms

    - page: page_job_detail
      target_load_time: "<1.5 seconds"
      measured_metrics:
        - time_to_first_byte: 150ms
        - first_contentful_paint: 600ms
        - time_to_interactive: 1400ms

    - page: page_dashboard
      target_load_time: "<2.5 seconds"
      measured_metrics:
        - time_to_first_byte: 250ms
        - first_contentful_paint: 1000ms
        - time_to_interactive: 2200ms

  acceptance_criteria:
    all_pages_interactive: "<3 seconds"
    lighthouse_performance_score: ">90"

  tools:
    - Lighthouse
    - WebPageTest
    - Chrome DevTools Performance

  optimizations_tested:
    - code_splitting: "Per-page bundles"
    - lazy_loading: "Images and non-critical components"
    - caching: "CDN and browser caching"
```

---

### Performance Test Metrics

```yaml
performance_benchmarks:
  api_response_times:
    target_p50: 200ms
    target_p95: 500ms
    target_p99: 1000ms

  page_load_times:
    target_first_contentful_paint: 1000ms
    target_time_to_interactive: 2000ms
    target_lighthouse_score: 90

  throughput:
    target_requests_per_second: 100
    target_transactions_per_second: 50

  resource_utilization:
    target_cpu: "<70% under normal load"
    target_memory: "<80% of available"
    target_database_connections: "<50% of pool"

  scalability:
    horizontal_scaling_efficiency: ">80% linear"
    target_users_per_instance: 100
```

---

## 2.2 Security Testing

### ISTQB/ISO Definition

**Security Testing** evaluates the degree to which a system protects information and data so that unauthorized persons or systems cannot read or modify them. Sub-characteristics include:
- **Confidentiality**: Data accessible only to authorized users
- **Integrity**: Data accuracy and completeness
- **Non-repudiation**: Proof of actions
- **Accountability**: Actions traceable to entity
- **Authenticity**: Identity can be proved

---

### Security Test Types

**1. Confidentiality Testing**

**Definition**: Ensuring sensitive data is protected from unauthorized access.

**Job Seeker Example: Profile Data Protection**
```yaml
test_case:
  id: tc_sec_001_profile_data_confidentiality
  test_type: confidentiality_testing

  scenarios:
    - scenario: "Unauthenticated user attempts to access profile"
      request: "GET /api/profiles/candidate_123"
      auth: none
      expected: "401 Unauthorized"

    - scenario: "Authenticated user attempts to access another user's profile"
      request: "GET /api/profiles/candidate_456"
      auth: "candidate_123 token"
      expected: "403 Forbidden"

    - scenario: "User accesses own profile"
      request: "GET /api/profiles/candidate_123"
      auth: "candidate_123 token"
      expected: "200 OK with profile data"

  sensitive_data_checks:
    - field: email
      encrypted_at_rest: true
      transmitted_over_https: true
      masked_in_logs: true

    - field: resume_content
      encrypted_at_rest: true
      access_logged: true

    - field: application_status
      access_controlled: "Only candidate and recruiters"

  ddd_integration:
    bounded_context: bc_profile
    aggregate: agg_candidate_profile
    authorization_rule: "Candidate can only access own profile"

  compliance:
    - GDPR_Article_32: "Security of processing"
    - OWASP_A01: "Broken Access Control"
```

**2. Integrity Testing**

**Definition**: Ensuring data cannot be modified in unauthorized ways.

**Job Seeker Example: Application Data Integrity**
```yaml
test_case:
  id: tc_sec_002_application_integrity
  test_type: integrity_testing

  scenarios:
    - scenario: "Tamper with application status via API"
      given: "Application with status SUBMITTED"
      when: "PUT /api/applications/app_123 with status=ACCEPTED"
      auth: "Candidate token (not recruiter)"
      expected: "403 Forbidden - candidates cannot change status"

    - scenario: "Validate data checksums"
      given: "Resume uploaded with checksum ABC123"
      when: "Download resume"
      then: "Downloaded file has matching checksum ABC123"

    - scenario: "Prevent SQL injection"
      given: "Job search endpoint"
      when: "Search query: '; DROP TABLE jobs; --"
      then: "Query sanitized, no database modification"

  integrity_mechanisms:
    - database_constraints: "Foreign keys, unique constraints"
    - input_validation: "vo_* validation rules enforced"
    - audit_logging: "All modifications logged with timestamp and user"
    - checksums: "File uploads verified"

  ddd_integration:
    aggregate_root: ent_application
    invariants_enforced:
      - "Status transitions follow state machine"
      - "Only recruiter can change status to ACCEPTED/REJECTED"

  compliance:
    - OWASP_A03: "Injection"
    - OWASP_A08: "Software and Data Integrity Failures"
```

**3. Authentication Testing**

**Definition**: Verifying user identity mechanisms.

**Job Seeker Example: User Authentication**
```yaml
test_case:
  id: tc_sec_003_authentication
  test_type: authentication_testing

  scenarios:
    - scenario: "Valid login"
      username: "candidate@example.com"
      password: "SecurePass123!"
      expected: "JWT token issued, expires in 24 hours"

    - scenario: "Invalid password"
      username: "candidate@example.com"
      password: "WrongPassword"
      expected: "401 Unauthorized, login failed"

    - scenario: "Account lockout after failed attempts"
      given: "5 consecutive failed login attempts"
      expected: "Account locked for 15 minutes"

    - scenario: "Token expiration"
      given: "JWT token issued 25 hours ago"
      when: "Request with expired token"
      expected: "401 Unauthorized, token expired"

    - scenario: "Password reset flow"
      steps:
        - "Request password reset"
        - "Reset link sent to email"
        - "Link expires in 1 hour"
        - "Link is single-use only"

  authentication_requirements:
    - password_complexity: "Min 8 chars, 1 uppercase, 1 number, 1 special"
    - password_hashing: "bcrypt with cost factor 12"
    - token_type: "JWT with RS256 signature"
    - mfa_support: "Optional TOTP 2FA"

  compliance:
    - OWASP_A07: "Identification and Authentication Failures"
    - NIST_800-63B: "Digital Identity Guidelines"
```

**4. Authorization Testing**

**Definition**: Verifying access control mechanisms.

**Job Seeker Example: Role-Based Access Control**
```yaml
test_case:
  id: tc_sec_004_authorization_rbac
  test_type: authorization_testing

  roles:
    - role: candidate
      permissions:
        - read_own_profile: allowed
        - update_own_profile: allowed
        - read_others_profile: denied
        - submit_application: allowed
        - view_own_applications: allowed
        - change_application_status: denied

    - role: recruiter
      permissions:
        - read_candidate_profiles: allowed (for active applications)
        - update_candidate_profile: denied
        - view_applications: allowed (for own company's jobs)
        - change_application_status: allowed
        - post_job: allowed

    - role: admin
      permissions:
        - all_permissions: allowed

  test_scenarios:
    - scenario: "Candidate attempts recruiter action"
      role: candidate
      action: "PATCH /api/applications/app_123/status"
      expected: "403 Forbidden"

    - scenario: "Recruiter views applications for own jobs only"
      role: recruiter
      company: company_A
      action: "GET /api/applications?job_id=job_from_company_B"
      expected: "403 Forbidden or empty results"

  ddd_integration:
    bounded_context: bc_applications
    authorization_policy: "RecruiterCanChangeApplicationStatusPolicy"

  compliance:
    - OWASP_A01: "Broken Access Control"
```

**5. Vulnerability Scanning**

**Definition**: Automated scanning for known security vulnerabilities.

**Job Seeker Example: Dependency and Code Scanning**
```yaml
test_case:
  id: tc_sec_005_vulnerability_scanning
  test_type: vulnerability_scanning

  scans_performed:
    - scan_type: dependency_scanning
      tool: npm audit, Snyk
      target: package.json dependencies
      frequency: "On every commit"
      acceptance_criteria:
        critical_vulnerabilities: 0
        high_vulnerabilities: 0
        medium_vulnerabilities: "<5"

    - scan_type: static_code_analysis
      tool: SonarQube, Semgrep
      target: Application source code
      checks:
        - sql_injection_patterns
        - xss_vulnerabilities
        - hardcoded_secrets
        - insecure_random

    - scan_type: container_scanning
      tool: Trivy, Snyk Container
      target: Docker images
      checks:
        - base_image_vulnerabilities
        - outdated_packages

    - scan_type: dynamic_scanning
      tool: OWASP ZAP
      target: Running application
      checks:
        - injection_attacks
        - broken_authentication
        - sensitive_data_exposure
        - security_misconfigurations

  compliance:
    - OWASP_Top_10: "All 10 categories tested"
    - CWE_Top_25: "Most dangerous weaknesses"
```

**6. Penetration Testing**

**Definition**: Simulated attacks to find exploitable vulnerabilities.

**Job Seeker Example: Web Application Penetration Test**
```yaml
test_case:
  id: tc_sec_006_penetration_testing
  test_type: penetration_testing

  pentest_scope:
    - authentication_system
    - authorization_enforcement
    - api_endpoints
    - file_upload_functionality
    - search_functionality

  attack_scenarios:
    - attack: "Broken Authentication"
      tests:
        - "Brute force login"
        - "Session fixation"
        - "Token theft"

    - attack: "Injection"
      tests:
        - "SQL injection in search"
        - "NoSQL injection in filters"
        - "XSS in profile fields"

    - attack: "Broken Access Control"
      tests:
        - "IDOR (Insecure Direct Object Reference)"
        - "Path traversal"
        - "Privilege escalation"

    - attack: "File Upload Vulnerabilities"
      tests:
        - "Malicious file upload (executable)"
        - "Oversized file (DoS)"
        - "Path traversal in filename"

  findings_severity:
    critical: 0  # Must be 0
    high: 0      # Must be 0
    medium: 2    # Acceptable with remediation plan
    low: 5       # Acceptable

  remediation_timeline:
    critical: "24 hours"
    high: "7 days"
    medium: "30 days"
    low: "Next release"
```

---

### Security Test Metrics

```yaml
security_metrics:
  vulnerability_density:
    target: "<0.1 critical vulnerabilities per KLOC"
    current: 0.0

  security_test_coverage:
    owasp_top_10_coverage: "100%"
    authentication_tests: 25
    authorization_tests: 40
    injection_tests: 30

  penetration_test_frequency:
    schedule: "Quarterly"
    last_performed: "2025-09-15"
    next_scheduled: "2025-12-15"

  security_compliance:
    - standard: OWASP_Top_10
      compliance: "100%"
    - standard: GDPR
      compliance: "100%"
    - standard: SOC2
      compliance: "In progress"
```

---

## 2.3 Usability Testing

### ISTQB/ISO Definition

**Usability** is the degree to which a product can be used by specified users to achieve specified goals with effectiveness, efficiency, and satisfaction in a specified context of use. Sub-characteristics include:
- **Appropriateness Recognizability**: Users recognize product is appropriate
- **Learnability**: Ease of learning to use
- **Operability**: Ease of operation and control
- **User Error Protection**: Protection against errors
- **User Interface Aesthetics**: Pleasing interface
- **Accessibility**: Usable by people with widest range of abilities

---

### Usability Test Types

**1. User Interface (UI) Testing**

**Definition**: Testing visual elements, layout, and interface consistency.

**Job Seeker Example: Profile Edit Page UI**
```yaml
test_case:
  id: tc_usability_001_profile_edit_ui
  test_type: ui_testing
  page: page_profile_edit

  ui_elements_tested:
    - element: form_fields
      tests:
        - "All labels clearly visible"
        - "Required fields marked with asterisk"
        - "Helper text for complex fields"
        - "Consistent spacing between fields"

    - element: buttons
      tests:
        - "Primary button (Save) visually distinct"
        - "Secondary button (Cancel) less prominent"
        - "Disabled state clearly indicated"
        - "Button text is action-oriented ('Save Profile' not 'Submit')"

    - element: validation_messages
      tests:
        - "Error messages in red, near field"
        - "Success messages in green, at top"
        - "Icons used for quick recognition"

    - element: layout
      tests:
        - "Form responsive on mobile (320px)"
        - "Form readable on tablet (768px)"
        - "Form optimal on desktop (1920px)"
        - "No horizontal scrolling required"

  ux_integration:
    atoms: [button, input, label, badge]
    molecules: [comp_email_input, comp_skills_input]
    organisms: [org_profile_form]
    page: page_profile_edit

  acceptance_criteria:
    visual_consistency: "Matches design system"
    responsive_breakpoints: "3 tested (mobile, tablet, desktop)"
    zero_layout_shifts: "CLS (Cumulative Layout Shift) < 0.1"
```

**2. User Experience (UX) Testing**

**Definition**: Testing overall user experience, task completion, satisfaction.

**Job Seeker Example: First-Time User Onboarding**
```yaml
test_case:
  id: tc_usability_002_first_time_onboarding_ux
  test_type: ux_testing
  user_journey: "New candidate signs up and applies for first job"

  participants: 10 first-time users
  methodology: Moderated usability testing

  tasks:
    - task: "Sign up for account"
      success_criteria: "Complete without assistance"
      measured_metrics:
        - time_on_task: 120 seconds
        - success_rate: 100%
        - difficulty_rating: 1.5/5 (1=easy)
      observations:
        - "2 users confused by password requirements"
        - "All users completed successfully"

    - task: "Create profile with skills"
      success_criteria: "Add at least 3 skills"
      measured_metrics:
        - time_on_task: 180 seconds
        - success_rate: 90%
        - difficulty_rating: 2.0/5
      observations:
        - "1 user didn't understand skills input (tag-based)"
        - "Users want skill suggestions"

    - task: "Find and apply for a job matching your skills"
      success_criteria: "Complete application"
      measured_metrics:
        - time_on_task: 300 seconds
        - success_rate: 80%
        - difficulty_rating: 2.5/5
      observations:
        - "Match score badge was very helpful"
        - "Application wizard clear and simple"
        - "2 users wanted to save application as draft"

  sus_score: 78  # System Usability Scale, target >75

  key_findings:
    positive:
      - "Match score feature loved by all users"
      - "Wizard-based application flow intuitive"
    negative:
      - "Skills input not immediately obvious"
      - "No draft application feature"

  ux_integration:
    workflows: [wf_onboarding, wf_submit_application]
    pages: [page_signup, page_profile_create, page_job_search, page_job_detail]
```

**3. Learnability Testing**

**Definition**: How quickly new users can accomplish tasks.

**Job Seeker Example: Using Advanced Job Search**
```yaml
test_case:
  id: tc_usability_003_advanced_search_learnability
  test_type: learnability_testing
  feature: "Advanced job search with filters"

  participants: 8 new users (never used the app)

  trial_1:
    task: "Find remote Python jobs in New York paying >$100k"
    time_on_task_average: 180 seconds
    success_rate: 62.5%
    errors: "3 users applied wrong filter"

  trial_2: "(Same task, 10 minutes later)"
    time_on_task_average: 90 seconds
    success_rate: 100%
    errors: 0

  trial_3: "(Similar task, different criteria, 1 day later)"
    task: "Find hybrid Java jobs in California"
    time_on_task_average: 60 seconds
    success_rate: 100%
    errors: 0

  learnability_metrics:
    time_reduction: "67% from trial 1 to trial 3"
    retention: "100% task success after initial learning"
    target: "Users proficient after 2 attempts"

  improvements_suggested:
    - "Add tooltips on first use"
    - "Highlight recently used filters"
    - "Show filter usage examples"
```

**4. Operability Testing**

**Definition**: Ease with which users can operate and control the system.

**Job Seeker Example: Keyboard Navigation**
```yaml
test_case:
  id: tc_usability_004_keyboard_navigation
  test_type: operability_testing

  keyboard_only_tasks:
    - task: "Navigate main menu"
      method: "Tab key and Enter"
      expected: "All menu items reachable, current focus visible"
      result: "Pass - focus indicators clear"

    - task: "Fill out profile form"
      method: "Tab between fields, Enter to submit"
      expected: "Logical tab order, dropdowns keyboard accessible"
      result: "Pass - tab order follows visual layout"

    - task: "Use multi-select skills input"
      method: "Type to filter, Enter to add, Backspace to remove"
      expected: "All operations keyboard accessible"
      result: "Pass - keyboard shortcuts documented"

    - task: "Interact with job cards in list"
      method: "Tab to card, Enter to open"
      expected: "Cards focusable, actions accessible"
      result: "Pass - cards are proper buttons"

  keyboard_shortcuts:
    - shortcut: "Ctrl+K"
      action: "Open global search"
    - shortcut: "Escape"
      action: "Close modals/dialogs"
    - shortcut: "?"
      action: "Show keyboard shortcuts help"

  acceptance_criteria:
    all_interactive_elements_reachable: true
    focus_indicators_visible: true
    tab_order_logical: true
    keyboard_traps: 0
```

---

### Usability Test Metrics

```yaml
usability_metrics:
  system_usability_scale:
    current_score: 78
    target: ">75"
    industry_average: 68

  task_success_rate:
    critical_tasks: 95%
    all_tasks: 88%
    target: ">90%"

  task_completion_time:
    submit_application: 180 seconds
    edit_profile: 120 seconds
    search_jobs: 45 seconds
    target: "Within 20% of expert user time"

  error_rate:
    user_errors_per_task: 0.3
    target: "<0.5"

  user_satisfaction:
    nps_score: 45  # Net Promoter Score
    target: ">40"
```

---

## 2.4 Accessibility Testing

### ISTQB/ISO Definition

**Accessibility** is the degree to which a product can be used by people with the widest range of characteristics and capabilities to achieve a specified goal in a specified context of use.

**Standards:**
- **WCAG 2.2** (Web Content Accessibility Guidelines) - W3C standard, Levels A, AA, AAA
- **EN 301 549** - European accessibility standard
- **EU Accessibility Act** - Effective June 2025, mandatory for digital products

---

### Accessibility Test Types

**1. WCAG 2.2 Compliance Testing**

**WCAG Principles (POUR):**
- **Perceivable**: Information and UI components must be presentable to users
- **Operable**: UI components and navigation must be operable
- **Understandable**: Information and operation must be understandable
- **Robust**: Content must be robust enough for assistive technologies

**Job Seeker Example: Profile Page WCAG 2.2 Level AA**
```yaml
test_case:
  id: tc_a11y_001_profile_page_wcag
  test_type: accessibility_testing
  standard: WCAG_2.2_Level_AA
  page: page_profile_edit

  perceivable_tests:
    - criterion: "1.1.1 Non-text Content (Level A)"
      test: "All images have alt text"
      method: "Automated scan + manual review"
      result: "Pass - profile photo alt='Candidate profile photo', icons decorative"

    - criterion: "1.3.1 Info and Relationships (Level A)"
      test: "Semantic HTML and ARIA labels"
      method: "Screen reader testing (NVDA)"
      result: "Pass - form uses <label>, headings hierarchical"

    - criterion: "1.4.3 Contrast (Level AA)"
      test: "Text contrast ratio ≥4.5:1"
      method: "axe DevTools"
      result: "Pass - all text meets minimum contrast"

    - criterion: "1.4.11 Non-text Contrast (Level AA)"
      test: "UI components contrast ≥3:1"
      method: "Manual testing with contrast checker"
      result: "Pass - buttons, focus indicators meet requirement"

  operable_tests:
    - criterion: "2.1.1 Keyboard (Level A)"
      test: "All functionality keyboard accessible"
      method: "Keyboard-only navigation"
      result: "Pass - see tc_usability_004_keyboard_navigation"

    - criterion: "2.4.3 Focus Order (Level A)"
      test: "Focus order is logical"
      method: "Tab through page"
      result: "Pass - top to bottom, left to right"

    - criterion: "2.4.7 Focus Visible (Level AA)"
      test: "Keyboard focus indicator visible"
      method: "Visual inspection during keyboard navigation"
      result: "Pass - 2px blue outline on all interactive elements"

    - criterion: "2.5.3 Label in Name (Level A)"
      test: "Visible label matches accessible name"
      method: "Screen reader + visual comparison"
      result: "Pass - button text matches aria-label"

  understandable_tests:
    - criterion: "3.1.1 Language of Page (Level A)"
      test: "Page language declared"
      method: "Check HTML lang attribute"
      result: "Pass - <html lang='en'>"

    - criterion: "3.3.1 Error Identification (Level A)"
      test: "Errors identified in text"
      method: "Submit invalid form"
      result: "Pass - 'Email is invalid' message displayed"

    - criterion: "3.3.2 Labels or Instructions (Level A)"
      test: "Form fields have labels"
      method: "Visual inspection + axe scan"
      result: "Pass - all fields labeled, required fields indicated"

    - criterion: "3.3.3 Error Suggestion (Level AA)"
      test: "Error correction suggestions provided"
      method: "Test validation errors"
      result: "Pass - 'Please enter a valid email like user@example.com'"

  robust_tests:
    - criterion: "4.1.2 Name, Role, Value (Level A)"
      test: "UI components have accessible names and roles"
      method: "Screen reader (NVDA, JAWS)"
      result: "Pass - buttons announce as 'button', inputs have labels"

    - criterion: "4.1.3 Status Messages (Level AA)"
      test: "Status messages announced to screen readers"
      method: "Trigger success/error messages, verify with screen reader"
      result: "Pass - aria-live='polite' on toast notifications"

  tools_used:
    - axe_devtools: "Automated scanning"
    - nvda: "Screen reader testing"
    - keyboard_navigation: "Manual keyboard testing"
    - contrast_checker: "Color contrast validation"

  compliance_summary:
    level_a_criteria_tested: 30
    level_a_criteria_passed: 30
    level_aa_criteria_tested: 20
    level_aa_criteria_passed: 20
    overall_compliance: "100% Level AA"
```

**2. EN 301 549 Compliance**

**Job Seeker Example: European Accessibility Standard**
```yaml
test_case:
  id: tc_a11y_002_en301549_compliance
  test_type: accessibility_testing
  standard: EN_301_549_v3.2.1

  functional_performance:
    - requirement: "Usage without vision"
      test: "Complete job application using screen reader only"
      assistive_tech: NVDA
      result: "Pass - all steps completable"

    - requirement: "Usage with limited vision"
      test: "Complete tasks at 200% zoom"
      result: "Pass - no horizontal scrolling, all content accessible"

    - requirement: "Usage without hearing"
      test: "All audio content has captions/transcripts"
      result: "N/A - no audio content in current version"

    - requirement: "Usage with limited manipulation"
      test: "Complete tasks with keyboard only (motor impairment simulation)"
      result: "Pass - see keyboard navigation tests"

  technical_requirements:
    - requirement: "9.2.1 Non-text content"
      maps_to: "WCAG 1.1.1"
      compliance: "Pass"

    - requirement: "9.2.4.3 Focus order"
      maps_to: "WCAG 2.4.3"
      compliance: "Pass"

    - requirement: "9.2.4.7 Focus visible"
      maps_to: "WCAG 2.4.7"
      compliance: "Pass"

  eu_compliance_statement:
    published_date: "2025-10-04"
    compliance_status: "Fully compliant with EN 301 549"
    contact_email: "accessibility@jobseeker.com"
```

**3. EU Accessibility Act (June 2025)**

**Job Seeker Example: Directive (EU) 2019/882 Compliance**
```yaml
test_case:
  id: tc_a11y_003_eu_accessibility_act
  test_type: accessibility_testing
  regulation: EU_2019_882
  effective_date: "2025-06-28"

  product_classification:
    category: "Website and mobile applications for consumer services"
    applicable_requirements:
      - "Article 4 - Accessibility requirements"
      - "Annex I - Section II - Design and production"

  compliance_requirements:
    - requirement: "Provide information on accessibility features"
      implementation: "Accessibility statement page published"
      location: "/accessibility"

    - requirement: "Ensure compatibility with assistive technologies"
      testing:
        - screen_readers: [NVDA, JAWS, VoiceOver]
        - magnification: "Windows Magnifier, ZoomText"
        - speech_input: "Dragon NaturallySpeaking"

    - requirement: "Provide alternative access methods"
      implementation:
        - keyboard_navigation: "Full keyboard support"
        - voice_control: "Compatible with speech input"
        - screen_reader: "ARIA landmarks and labels"

  monitoring_and_reporting:
    accessibility_audit_frequency: "Annual"
    last_audit: "2025-09-01"
    next_audit: "2026-09-01"
    findings_publication: "Public accessibility statement"

  complaint_mechanism:
    contact: "accessibility@jobseeker.com"
    response_time: "5 business days"
    escalation_process: "EU enforcement contact provided"
```

**4. Assistive Technology Testing**

**Job Seeker Example: Screen Reader Testing**
```yaml
test_case:
  id: tc_a11y_004_screen_reader_testing
  test_type: assistive_technology_testing

  screen_readers_tested:
    - name: NVDA
      version: "2024.1"
      browser: Firefox
      os: Windows 11

    - name: JAWS
      version: "2024"
      browser: Chrome
      os: Windows 11

    - name: VoiceOver
      version: macOS Sonoma
      browser: Safari
      os: macOS

  test_scenarios:
    - scenario: "Navigate job listings page"
      steps:
        - "Use heading navigation (H key) to skip to main content"
        - "Navigate through job cards (Down arrow)"
        - "Hear job title, company, match score announced"
        - "Activate job card (Enter) to view details"
      result: "Pass on all screen readers"

    - scenario: "Fill out profile form"
      steps:
        - "Navigate to form (F key for form mode)"
        - "Tab through fields, hear labels announced"
        - "Enter invalid email, hear error announcement"
        - "Correct email, hear 'Valid email' confirmation"
        - "Submit form, hear success message"
      result: "Pass - aria-live regions working"

    - scenario: "Use skills input (tag-based)"
      steps:
        - "Focus on skills input"
        - "Hear 'Skills, type to add, 3 skills added'"
        - "Type 'Python', hear suggestions"
        - "Press Enter, hear 'Python added'"
        - "Press Backspace, hear 'Python removed'"
      result: "Pass - custom component accessible"

  ux_integration:
    components_tested:
      - comp_email_input (molecule)
      - comp_skills_input (molecule)
      - org_job_card (organism)
      - page_job_listings (page)
      - page_profile_edit (page)
```

---

### Accessibility Test Metrics

```yaml
accessibility_metrics:
  wcag_compliance:
    level_a: "100%"
    level_aa: "100%"
    level_aaa: "80%"  # Aspirational

  automated_testing:
    axe_violations:
      critical: 0
      serious: 0
      moderate: 0
      minor: 2  # "Color contrast could be improved on footer links"

  manual_testing:
    pages_tested: 15
    screen_reader_compatible: "100%"
    keyboard_accessible: "100%"

  assistive_technology_support:
    screen_readers: [NVDA, JAWS, VoiceOver]
    magnification: "Tested to 200% zoom"
    voice_control: "Compatible with Dragon"

  compliance_certifications:
    - WCAG_2.2_Level_AA: "Certified"
    - EN_301_549: "Compliant"
    - EU_Accessibility_Act: "Compliant (June 2025)"
```

---

## 2.5 Reliability Testing

### ISTQB/ISO Definition

**Reliability** is the degree to which a system performs specified functions under specified conditions for a specified period. Sub-characteristics include:
- **Maturity**: System meets reliability needs under normal operation
- **Availability**: System is operational and accessible when required
- **Fault Tolerance**: System operates despite faults
- **Recoverability**: System can recover data and re-establish desired state after interruption

---

### Reliability Test Types

**1. Fault Tolerance Testing**

**Definition**: Testing system behavior when faults occur.

**Job Seeker Example: Database Connection Failure**
```yaml
test_case:
  id: tc_rel_001_database_fault_tolerance
  test_type: fault_tolerance_testing

  fault_injection:
    - fault: "Database connection lost"
      trigger: "Kill database connection mid-request"
      expected_behavior:
        - "Request fails gracefully"
        - "User sees friendly error message"
        - "Application logs error"
        - "Connection pool attempts reconnection"
      recovery:
        - "Automatic reconnection within 5 seconds"
        - "Subsequent requests succeed"
      result: "Pass - graceful degradation, auto-recovery"

    - fault: "Repository returns null"
      trigger: "Mock repository to return null for candidate profile"
      expected_behavior:
        - "Service handles null gracefully"
        - "No null pointer exception"
        - "User sees 'Profile not found' message"
      result: "Pass - defensive programming in place"

  ddd_integration:
    repository: repo_candidate_profile
    aggregate: agg_candidate_profile
    error_handling: "Repository exceptions caught by application service"
```

**2. Recoverability Testing**

**Definition**: Testing ability to recover from failures.

**Job Seeker Example: Application Submission Failure Recovery**
```yaml
test_case:
  id: tc_rel_002_application_submission_recovery
  test_type: recoverability_testing

  failure_scenarios:
    - scenario: "Network failure during submission"
      steps:
        - "User fills application form"
        - "User clicks Submit"
        - "Network disconnects mid-request"
      expected_recovery:
        - "Form data persisted to localStorage"
        - "Error message: 'Connection lost, please try again'"
        - "User can click Submit again"
        - "Form repopulated from localStorage"
      result: "Pass - no data loss"

    - scenario: "Server crash after application saved but before event published"
      steps:
        - "Application saved to database"
        - "Server crashes before ApplicationSubmitted event published"
      expected_recovery:
        - "Event sourcing outbox pattern"
        - "Background job republishes uncommitted events"
        - "ApplicationSubmitted event eventually published"
      result: "Pass - eventual consistency achieved"

  recovery_metrics:
    rto: "5 minutes"  # Recovery Time Objective
    rpo: "0 seconds"  # Recovery Point Objective (no data loss)
    actual_rto: "3 minutes"
    actual_rpo: "0 seconds"

  ddd_integration:
    application_service: svc_app_submit_application
    domain_event: evt_application_submitted
    pattern: "Outbox pattern for reliable event publishing"
```

**3. Availability Testing**

**Definition**: Testing system uptime and accessibility.

**Job Seeker Example: High Availability**
```yaml
test_case:
  id: tc_rel_003_system_availability
  test_type: availability_testing

  availability_target: "99.9% uptime (8.76 hours downtime/year)"

  high_availability_mechanisms:
    - mechanism: "Load balancer with health checks"
      test: "Kill one application server, verify traffic reroutes"
      result: "Pass - seamless failover, <1 second disruption"

    - mechanism: "Database replication (master-replica)"
      test: "Simulate master database failure"
      result: "Pass - replica promoted to master, <30 seconds downtime"

    - mechanism: "Multi-zone deployment"
      test: "Simulate entire availability zone outage"
      result: "Pass - traffic reroutes to other zone, <5 seconds disruption"

  monitoring:
    uptime_monitoring: Pingdom, UptimeRobot
    alerting: "PagerDuty on >1 minute downtime"

  actual_uptime:
    last_30_days: "99.95%"
    last_90_days: "99.92%"
    last_year: "99.91%"
```

**4. Mean Time Between Failures (MTBF)**

**Definition**: Average time between system failures.

**Job Seeker Example: MTBF Calculation**
```yaml
test_case:
  id: tc_rel_004_mtbf_calculation
  test_type: reliability_metrics

  observation_period: 90 days

  failures_recorded:
    - date: "2025-07-15"
      type: "Database connection pool exhaustion"
      downtime: 15 minutes

    - date: "2025-08-22"
      type: "Memory leak in background job processor"
      downtime: 30 minutes

    - date: "2025-09-10"
      type: "Third-party API timeout"
      downtime: 10 minutes

  mtbf_calculation:
    total_operational_time: 129600 minutes (90 days)
    number_of_failures: 3
    mtbf: 43200 minutes (30 days)
    target_mtbf: ">20 days"
    result: "Pass - MTBF exceeds target"

  mttr: "18.3 minutes"  # Mean Time To Repair
  target_mttr: "<30 minutes"
```

---

### Reliability Test Metrics

```yaml
reliability_metrics:
  availability:
    target: "99.9%"
    actual_30_days: "99.95%"

  fault_tolerance:
    single_point_of_failure: 0
    graceful_degradation: "100% of critical paths"

  recoverability:
    rto_target: "5 minutes"
    rto_actual: "3 minutes"
    rpo_target: "0 seconds"
    rpo_actual: "0 seconds"

  mtbf:
    target: ">20 days"
    actual: "30 days"

  mttr:
    target: "<30 minutes"
    actual: "18.3 minutes"
```

---

## 2.6 Maintainability Testing

### ISTQB/ISO Definition

**Maintainability** is the degree of effectiveness and efficiency with which a product can be modified. Sub-characteristics include:
- **Modularity**: Composed of discrete components
- **Reusability**: Asset can be used in more than one system
- **Analyzability**: Ease of assessing impact of changes
- **Modifiability**: Product can be modified without defects
- **Testability**: Test criteria can be established and tests performed

---

### Maintainability Test Types

**1. Modularity Assessment**

**Definition**: Measuring how well system is divided into discrete modules.

**Job Seeker Example: Bounded Context Independence**
```yaml
test_case:
  id: tc_maint_001_bounded_context_modularity
  test_type: modularity_assessment

  bounded_contexts:
    - context: bc_profile
      dependencies:
        - bc_identity (for authentication)
      dependency_direction: "Inbound only"
      coupling: "Loose - communicates via events"
      cohesion: "High - all profile-related logic"

    - context: bc_applications
      dependencies:
        - bc_profile (for candidate data)
        - bc_job_catalog (for job data)
      dependency_direction: "Outbound via anti-corruption layer"
      coupling: "Loose - no direct database access"
      cohesion: "High - all application logic"

  modularity_metrics:
    afferent_coupling: "Low - few incoming dependencies"
    efferent_coupling: "Low - few outgoing dependencies"
    instability: "0.3 (stable)"

  tests:
    - test: "Change bc_profile database schema"
      impact: "Only bc_profile code changes"
      result: "Pass - no ripple effects"

    - test: "Add new field to agg_candidate_profile"
      impact: "Only bc_profile affected"
      other_contexts: "Continue working via published contract"
      result: "Pass - bounded contexts truly isolated"

  ddd_integration:
    strategic_pattern: "Bounded contexts"
    integration_pattern: "Anti-corruption layer"
```

**2. Reusability Evaluation**

**Definition**: Assessing how components can be reused.

**Job Seeker Example: Component Reusability**
```yaml
test_case:
  id: tc_maint_002_component_reusability
  test_type: reusability_evaluation

  reusable_components:
    - component: atom_button
      reused_in:
        - page_profile_edit
        - page_job_detail
        - page_application_wizard
        - org_navigation_bar
      reuse_count: 25 times across app

    - component: comp_email_input
      reused_in:
        - page_signup
        - page_profile_edit
        - page_settings
      reuse_count: 8 times
      coupled_to: vo_email (domain validation)

    - component: org_job_card
      reused_in:
        - page_job_listings
        - page_dashboard (recommended jobs)
        - page_saved_jobs
      reuse_count: 3 contexts

  reusability_metrics:
    component_reuse_rate: "85%"  # % of components used >1 time
    code_duplication: "5%"  # Low duplication
    target: "<10% duplication"

  ux_integration:
    atomic_design: "Atoms and molecules highly reusable"
    design_system: "Consistent props interface"
```

**3. Analyzability Testing**

**Definition**: Ease of diagnosing defects and identifying parts to modify.

**Job Seeker Example: Code Analyzability**
```yaml
test_case:
  id: tc_maint_003_code_analyzability
  test_type: analyzability_testing

  static_analysis:
    tool: SonarQube
    metrics:
      cognitive_complexity: 8 (target <10)
      cyclomatic_complexity: 4 (target <10)
      code_smells: 12 (target <50)
      technical_debt: 2 days (target <5 days)
      maintainability_rating: A

  code_documentation:
    public_apis_documented: "100%"
    domain_model_documented: "100%"
    ubiquitous_language_glossary: "Available in wiki"

  traceability:
    requirements_to_code: "95% traceable"
    code_to_tests: "90% test coverage"
    defects_to_root_cause: "100% analyzed"

  tests:
    - test: "Identify code responsible for email validation"
      method: "Search for 'vo_email'"
      time_taken: "10 seconds"
      result: "Pass - clear naming convention"

    - test: "Understand application submission flow"
      method: "Follow svc_app_submit_application"
      time_taken: "5 minutes"
      result: "Pass - application service orchestrates clearly"

  ddd_integration:
    ubiquitous_language: "Aids code discovery"
    naming_conventions: "vo_, ent_, agg_, svc_app_"
```

**4. Modifiability Testing**

**Definition**: Ease of making changes without introducing defects.

**Job Seeker Example: Add New Skill to Profile**
```yaml
test_case:
  id: tc_maint_004_modifiability_testing
  test_type: modifiability_testing

  change_request: "Add 'skill level' (beginner/intermediate/advanced) to skills"

  impact_analysis:
    files_changed:
      domain_layer:
        - vo_skill: "Add level field"
        - agg_candidate_profile: "Update skill validation"

      application_layer:
        - svc_app_update_profile: "Handle new field in DTO"

      ui_layer:
        - comp_skill_input: "Add level dropdown"
        - page_profile_edit: "Display skill levels"

      database:
        - migration: "Add skill_level column"

  change_effort:
    files_modified: 6
    lines_changed: 120
    time_estimate: 4 hours
    actual_time: 5 hours

  regression_testing:
    tests_run: 450
    tests_failed: 2 (unrelated)
    new_tests_added: 8

  result: "Pass - change localized, no unexpected side effects"

  ddd_integration:
    aggregate_boundary: "Change contained within agg_candidate_profile"
    invariants_maintained: "Skills validation still enforced"
```

**5. Testability Assessment**

**Definition**: Ease of establishing test criteria and performing tests.

**Job Seeker Example: Aggregate Testability**
```yaml
test_case:
  id: tc_maint_005_testability_assessment
  test_type: testability_testing

  testable_characteristics:
    - characteristic: "Value objects immutable"
      test_ease: "Easy - no setup, pure functions"
      example: "vo_email('test@example.com').isValid()"

    - characteristic: "Aggregates enforce invariants"
      test_ease: "Easy - aggregates self-contained"
      example: "agg_candidate_profile.removeAllSkills() throws DomainException"

    - characteristic: "Application services orchestrate"
      test_ease: "Moderate - need to mock repositories"
      example: "Mock repo_candidate_profile, test svc_app_update_profile"

    - characteristic: "UI components isolated"
      test_ease: "Easy - props in, events out"
      example: "Render comp_email_input with props, assert output"

  testability_metrics:
    unit_test_coverage: "92%"
    integration_test_coverage: "85%"
    e2e_test_coverage: "70%"
    test_execution_time: "5 minutes (unit), 15 minutes (integration), 30 minutes (e2e)"

  test_infrastructure:
    - in_memory_database: "H2 for fast tests"
    - test_doubles: "Jest mocks for repositories"
    - test_data_builders: "Fluent builders for aggregates"
    - page_objects: "Playwright page objects for UI"

  ddd_integration:
    pattern: "Aggregates are unit of testability"
    repository_interface: "Easy to mock for testing"
```

---

### Maintainability Test Metrics

```yaml
maintainability_metrics:
  code_quality:
    sonarqube_rating: "A"
    technical_debt: "2 days"
    code_smells: 12

  modularity:
    cyclomatic_complexity: 4
    coupling: "Low"
    cohesion: "High"

  test_coverage:
    unit_tests: "92%"
    integration_tests: "85%"
    e2e_tests: "70%"

  documentation:
    public_apis: "100%"
    domain_model: "100%"

  change_impact:
    average_files_changed: 3
    regression_rate: "<2%"
```

---

## 2.7 Portability Testing

### ISTQB/ISO Definition

**Portability** is the degree of effectiveness and efficiency with which a system can be transferred from one environment to another. Sub-characteristics include:
- **Adaptability**: Adapted for different environments
- **Installability**: Installed/uninstalled in specified environment
- **Replaceability**: Replaced by another specified software product

---

### Portability Test Types

**1. Adaptability Testing**

**Definition**: Testing system adaptation to different platforms/environments.

**Job Seeker Example: Multi-Environment Deployment**
```yaml
test_case:
  id: tc_port_001_environment_adaptability
  test_type: adaptability_testing

  environments:
    - environment: development
      database: PostgreSQL (local Docker)
      storage: Local filesystem
      config: .env.development
      test: "Application starts, connects to DB"
      result: "Pass"

    - environment: staging
      database: PostgreSQL (AWS RDS)
      storage: AWS S3
      config: .env.staging
      test: "Deploy via CI/CD, health check passes"
      result: "Pass"

    - environment: production
      database: PostgreSQL (AWS RDS Multi-AZ)
      storage: AWS S3
      config: .env.production (secrets manager)
      test: "Blue-green deployment, zero downtime"
      result: "Pass"

  configuration_management:
    method: "Environment variables, 12-factor app"
    secrets: "AWS Secrets Manager"
    feature_flags: "LaunchDarkly for environment-specific features"

  portability_score: "95% - minimal environment-specific code"
```

**2. Installability Testing**

**Definition**: Testing ease of installation and uninstallation.

**Job Seeker Example: Docker Deployment**
```yaml
test_case:
  id: tc_port_002_docker_installability
  test_type: installability_testing

  installation_methods:
    - method: "Docker Compose (local development)"
      steps:
        - "Clone repository"
        - "Run 'docker-compose up'"
        - "Application accessible at localhost:3000"
      time_to_install: "5 minutes"
      prerequisites: "Docker Desktop"
      result: "Pass - one-command setup"

    - method: "Kubernetes (production)"
      steps:
        - "Apply Kubernetes manifests"
        - "kubectl apply -f k8s/"
        - "Wait for pods to be ready"
      time_to_install: "10 minutes"
      prerequisites: "kubectl, kubeconfig"
      result: "Pass - automated deployment"

  uninstallation:
    - method: "Docker Compose"
      command: "docker-compose down -v"
      result: "Pass - clean removal, volumes deleted"

    - method: "Kubernetes"
      command: "kubectl delete -f k8s/"
      result: "Pass - all resources removed"

  installation_metrics:
    time_to_production_ready: "15 minutes"
    manual_steps: 0
    automation: "100%"
```

**3. Replaceability Testing**

**Definition**: Testing ability to replace components with alternatives.

**Job Seeker Example: Database Migration**
```yaml
test_case:
  id: tc_port_003_database_replaceability
  test_type: replaceability_testing

  replacement_scenario: "Migrate from PostgreSQL to MySQL"

  abstraction_layer:
    pattern: "Repository pattern"
    orm: "TypeORM (database-agnostic)"

  migration_steps:
    - step: "Change database driver in config"
      files_changed: 1 (ormconfig.json)

    - step: "Run schema migration"
      command: "npm run typeorm migration:run"

    - step: "Run test suite"
      tests_run: 450
      tests_failed: 5 (database-specific SQL)

    - step: "Fix database-specific queries"
      files_changed: 3

    - step: "Re-run tests"
      tests_passed: 450

  effort:
    time: 8 hours
    files_changed: 4
    lines_changed: 50

  result: "Pass - repository pattern enabled easy replacement"

  ddd_integration:
    repository_interface: "Database-agnostic interface"
    implementation: "Swappable infrastructure layer"
```

---

### Portability Test Metrics

```yaml
portability_metrics:
  environment_support:
    - local_development: Docker Compose
    - staging: AWS ECS
    - production: AWS EKS (Kubernetes)

  platform_independence:
    database_abstraction: "Repository pattern"
    cloud_provider_agnostic: "80%"

  installation_automation:
    manual_steps: 0
    time_to_install: "5 minutes"

  replaceability:
    database: "Easy (Repository pattern)"
    cache: "Easy (Redis ↔ Memcached)"
    storage: "Moderate (S3 ↔ local filesystem)"
```

---

## 2.8 Compatibility Testing

### ISTQB/ISO Definition

**Compatibility** is the degree to which a product can exchange information with other products and perform required functions while sharing the same environment and resources. Sub-characteristics include:
- **Co-existence**: Performs efficiently while sharing environment
- **Interoperability**: Exchanges information with other systems

---

### Compatibility Test Types

**1. Co-existence Testing**

**Definition**: Testing ability to operate alongside other applications.

**Job Seeker Example: Resource Sharing**
```yaml
test_case:
  id: tc_compat_001_coexistence
  test_type: coexistence_testing

  scenarios:
    - scenario: "Job Seeker app + monitoring tools on same server"
      shared_resources:
        - cpu: "Job Seeker 70%, Prometheus 10%, Grafana 10%, System 10%"
        - memory: "Job Seeker 4GB, Prometheus 2GB, Grafana 1GB, Available 1GB"
        - disk: "All apps write to different directories"
        - network_ports: "Job Seeker :3000, Prometheus :9090, Grafana :3001"
      result: "Pass - no resource contention"

    - scenario: "Multiple browser tabs open"
      tabs:
        - tab1: "Job listings page"
        - tab2: "Profile edit page"
        - tab3: "Application wizard"
      test: "All tabs functional simultaneously"
      result: "Pass - no interference"
```

**2. Interoperability Testing**

**Definition**: Testing interaction with other systems and APIs.

**Job Seeker Example: Third-Party Integrations**
```yaml
test_case:
  id: tc_compat_002_interoperability
  test_type: interoperability_testing

  integrations:
    - integration: "LinkedIn profile import"
      protocol: "OAuth 2.0, REST API"
      data_exchange: "JSON"
      test: "Import profile data from LinkedIn"
      result: "Pass - profile data imported correctly"

    - integration: "Email service (SendGrid)"
      protocol: "REST API, SMTP"
      data_exchange: "JSON, MIME"
      test: "Send application confirmation email"
      result: "Pass - email delivered"

    - integration: "Payment gateway (Stripe)"
      protocol: "REST API, Webhooks"
      data_exchange: "JSON"
      test: "Process subscription payment"
      result: "Pass - payment successful, webhook received"

  api_contracts:
    - api: "/api/v1/profiles"
      format: "OpenAPI 3.0"
      versioning: "URI versioning"
      backwards_compatibility: "Maintained for 12 months"
```

**3. Browser Compatibility Testing**

**Definition**: Testing across different web browsers.

**Job Seeker Example: Multi-Browser Support**
```yaml
test_case:
  id: tc_compat_003_browser_compatibility
  test_type: browser_compatibility_testing

  browsers_tested:
    - browser: Chrome
      versions: [latest, latest-1]
      os: [Windows, macOS, Android]
      result: "Pass - full functionality"

    - browser: Firefox
      versions: [latest, latest-1]
      os: [Windows, macOS]
      result: "Pass - full functionality"

    - browser: Safari
      versions: [latest, latest-1]
      os: [macOS, iOS]
      result: "Pass - full functionality"

    - browser: Edge
      versions: [latest]
      os: [Windows]
      result: "Pass - full functionality"

  responsive_testing:
    - device: iPhone 13
      viewport: 390x844
      result: "Pass - mobile layout"

    - device: iPad Air
      viewport: 820x1180
      result: "Pass - tablet layout"

    - device: Desktop
      viewport: 1920x1080
      result: "Pass - desktop layout"

  tools:
    - BrowserStack: "Cross-browser automated testing"
    - Playwright: "Multi-browser E2E tests"

  ux_integration:
    responsive_breakpoints: [320px, 768px, 1024px, 1920px]
    pages_tested: "All pages responsive"
```

**4. Platform Compatibility Testing**

**Definition**: Testing across different operating systems and platforms.

**Job Seeker Example: OS and Device Testing**
```yaml
test_case:
  id: tc_compat_004_platform_compatibility
  test_type: platform_compatibility_testing

  platforms_tested:
    - platform: Windows 11
      browsers: [Chrome, Firefox, Edge]
      functionality: "100%"
      result: "Pass"

    - platform: macOS Sonoma
      browsers: [Chrome, Firefox, Safari]
      functionality: "100%"
      result: "Pass"

    - platform: iOS 17
      browsers: [Safari, Chrome]
      functionality: "100%"
      result: "Pass"

    - platform: Android 14
      browsers: [Chrome, Firefox]
      functionality: "100%"
      result: "Pass"

  progressive_web_app:
    installable: "Yes"
    offline_support: "Limited (cached pages)"
    push_notifications: "Supported on Android, iOS (limited)"
```

---

### Compatibility Test Metrics

```yaml
compatibility_metrics:
  browser_support:
    - Chrome: "100% support"
    - Firefox: "100% support"
    - Safari: "100% support"
    - Edge: "100% support"

  device_support:
    - desktop: "100%"
    - tablet: "100%"
    - mobile: "100%"

  api_compatibility:
    backwards_compatible: "12 months"
    api_versioning: "URI versioning (/api/v1/)"

  interoperability:
    third_party_integrations: 5
    integration_success_rate: "100%"
```

---

## 3. Regression Testing

### ISTQB Definition

**Regression Testing** verifies that changes (bug fixes, enhancements, configuration changes) have not adversely affected existing functionality.

**Key Characteristics**:
- Performed after changes
- Re-runs existing tests
- Can be automated (recommended)
- Focus on areas affected by change

---

### Regression Test Types

**1. Full Regression**

**Definition**: Re-run entire test suite.

**Job Seeker Example: Major Release**
```yaml
test_case:
  id: tc_reg_001_full_regression
  test_type: full_regression_testing
  trigger: "Major release (v2.0)"

  test_suite_executed:
    unit_tests: 850
    integration_tests: 320
    e2e_tests: 120
    total: 1290

  execution_time:
    unit_tests: 8 minutes
    integration_tests: 25 minutes
    e2e_tests: 45 minutes
    total: 78 minutes

  results:
    passed: 1282
    failed: 8
    pass_rate: 99.4%

  failures_analysis:
    - tc_unit_152: "Expected behavior changed (intentional)"
    - tc_int_045: "New validation rule (intentional)"
    - tc_e2e_023: "Flaky test (network timeout)"

  ddd_integration:
    all_bounded_contexts_tested: true
    aggregate_invariants_verified: true
```

**2. Selective Regression**

**Definition**: Run subset of tests related to changed areas.

**Job Seeker Example: Add Skill Level Field**
```yaml
test_case:
  id: tc_reg_002_selective_regression
  test_type: selective_regression_testing
  change: "Added skill_level field to vo_skill"

  impact_analysis:
    directly_affected:
      - vo_skill
      - agg_candidate_profile
      - comp_skill_input
      - page_profile_edit

    indirectly_affected:
      - svc_app_update_profile
      - repo_candidate_profile
      - page_dashboard (displays skills)

  test_selection:
    tests_related_to_skills: 45
    tests_related_to_profile: 78
    tests_related_to_ui: 32
    total_selected: 155

  tests_skipped: 1135 (not affected by change)

  execution_time: 18 minutes (vs 78 minutes for full regression)

  results:
    passed: 153
    failed: 2
    failures:
      - tc_unit_skill_validation: "Updated to include level validation"
      - tc_int_profile_update: "Updated DTO includes level"
```

**3. Progressive Regression**

**Definition**: Testing new features without affecting old features.

**Job Seeker Example: Add Job Alerts Feature**
```yaml
test_case:
  id: tc_reg_003_progressive_regression
  test_type: progressive_regression_testing
  new_feature: "Job alerts based on profile skills"

  new_tests_added:
    - tc_unit_101_job_alert_creation
    - tc_int_102_alert_matching_engine
    - tc_e2e_103_subscribe_to_alerts
    total: 28 new tests

  existing_tests_run:
    - bc_profile tests (alerts use profile data)
    - bc_job_catalog tests (alerts query jobs)
    - bc_notifications tests (alerts send notifications)
    total: 210 existing tests

  results:
    new_tests_passed: 28
    existing_tests_passed: 207
    existing_tests_failed: 3

  failures:
    - tc_int_notification_delivery: "Too many notifications sent (bug in alerts)"
    - Fixed and re-run: "All tests pass"

  ddd_integration:
    new_aggregate: agg_job_alert
    new_domain_service: svc_dom_alert_matcher
    integration_with_existing: "Uses evt_job_posted from bc_job_catalog"
```

---

### Regression Test Strategy

**Test Prioritization**:
```yaml
regression_priority:
  priority_1_critical:
    - authentication_login
    - submit_application
    - view_job_listings
    execution: "Every commit"

  priority_2_high:
    - profile_update
    - job_search
    - application_tracking
    execution: "Every PR"

  priority_3_medium:
    - advanced_filters
    - save_job
    - share_profile
    execution: "Daily"

  priority_4_low:
    - export_resume
    - dark_mode_toggle
    execution: "Weekly"
```

**Automation**:
```yaml
regression_automation:
  automated_tests: 1200
  manual_tests: 90
  automation_rate: 93%

  ci_cd_integration:
    on_commit: "Priority 1 (smoke tests)"
    on_pr: "Priority 1 + 2"
    on_merge_to_main: "Full regression"
    nightly: "Full regression + performance tests"
```

---

### Regression Test Metrics

```yaml
regression_metrics:
  test_suite_size:
    unit_tests: 850
    integration_tests: 320
    e2e_tests: 120
    total: 1290

  execution_frequency:
    on_commit: "Smoke tests (15 tests, 2 min)"
    on_pr: "Selective regression (200 tests, 20 min)"
    on_merge: "Full regression (1290 tests, 78 min)"

  pass_rate:
    target: ">95%"
    current: "99.4%"

  defect_escape_rate:
    target: "<10%"
    current: "4%"  # Defects found in production vs total defects
```

---

## 4. Smoke and Sanity Testing

### ISTQB Definitions

**Smoke Testing**: A subset of tests that cover the most important functionality to determine if the build is stable enough for further testing.

**Sanity Testing**: A subset of regression tests that focus on a small section of the application to quickly verify that a bug fix or new feature works.

**Key Differences**:
- **Smoke**: Broad but shallow (entire application, basic checks)
- **Sanity**: Narrow but deep (specific area, detailed checks)

---

### 4.1 Smoke Testing

**Job Seeker Example: Build Verification Test**
```yaml
test_suite:
  id: ts_smoke_001_build_verification
  name: "Smoke Test Suite"
  purpose: "Verify critical paths work after deployment"
  execution_trigger: "After every deployment"

  test_cases:
    - id: tc_smoke_001_application_starts
      test: "Application URL accessible"
      endpoint: "GET /"
      expected: "200 OK, homepage loads"

    - id: tc_smoke_002_database_connection
      test: "Database connection successful"
      endpoint: "GET /api/health/database"
      expected: "200 OK, database connected"

    - id: tc_smoke_003_user_login
      test: "User can log in"
      steps:
        - "Navigate to /login"
        - "Enter credentials"
        - "Submit form"
      expected: "Redirected to dashboard"

    - id: tc_smoke_004_view_job_listings
      test: "Job listings page loads"
      endpoint: "GET /jobs"
      expected: "List of jobs displayed"

    - id: tc_smoke_005_view_profile
      test: "Profile page loads"
      endpoint: "GET /profile"
      expected: "User profile displayed"

    - id: tc_smoke_006_submit_application
      test: "Application submission works"
      steps:
        - "Navigate to job detail"
        - "Click Apply"
        - "Complete wizard"
        - "Submit"
      expected: "Confirmation page displays"

    - id: tc_smoke_007_search_jobs
      test: "Job search works"
      endpoint: "GET /api/jobs/search?q=Python"
      expected: "Search results returned"

    - id: tc_smoke_008_external_services
      test: "Email service reachable"
      endpoint: "GET /api/health/email"
      expected: "200 OK, email service connected"

  execution_metrics:
    total_tests: 8
    execution_time: "2 minutes"
    automation: "100%"

  pass_criteria:
    all_tests_must_pass: true
    action_if_failure: "Rollback deployment"

  ddd_integration:
    bounded_contexts_verified:
      - bc_identity (login)
      - bc_profile (view profile)
      - bc_job_catalog (job listings, search)
      - bc_applications (submit application)
```

---

### 4.2 Sanity Testing

**Job Seeker Example: Bug Fix Verification**
```yaml
test_suite:
  id: ts_sanity_001_email_validation_fix
  name: "Sanity Test - Email Validation Bug Fix"
  purpose: "Verify bug fix for email validation issue"
  bug_id: "BUG-047"
  bug_description: "Email validation rejects valid emails with + sign"

  fix_description: "Updated vo_email regex to allow + sign in email"

  test_cases:
    - id: tc_sanity_001_email_with_plus_sign
      input: "user+tag@example.com"
      expected: "Email accepted as valid"
      result: "Pass"

    - id: tc_sanity_002_email_with_multiple_plus
      input: "user+tag+more@example.com"
      expected: "Email accepted as valid"
      result: "Pass"

    - id: tc_sanity_003_standard_email_still_works
      input: "user@example.com"
      expected: "Email accepted as valid"
      result: "Pass"

    - id: tc_sanity_004_invalid_email_still_rejected
      input: "invalid@"
      expected: "ValidationException thrown"
      result: "Pass"

    - id: tc_sanity_005_signup_with_plus_email
      test: "End-to-end signup with + email"
      steps:
        - "Navigate to signup"
        - "Enter email: test+job@example.com"
        - "Complete signup"
      expected: "Account created, confirmation email sent"
      result: "Pass"

    - id: tc_sanity_006_profile_edit_email_with_plus
      test: "Update profile email to + email"
      steps:
        - "Navigate to profile edit"
        - "Change email to: newemail+tag@example.com"
        - "Save"
      expected: "Email updated successfully"
      result: "Pass"

  execution_metrics:
    total_tests: 6
    execution_time: "5 minutes"
    automation: "100%"

  scope:
    focus_area: "Email validation (vo_email)"
    related_components:
      - vo_email (value object)
      - comp_email_input (UI component)
      - page_signup (uses email input)
      - page_profile_edit (uses email input)

  ddd_integration:
    value_object: vo_email
    aggregate: agg_candidate_profile
    bounded_context: bc_profile
```

---

### Smoke vs Sanity Comparison

```yaml
comparison:
  smoke_testing:
    scope: "Broad - entire application"
    depth: "Shallow - basic functionality"
    trigger: "After deployment, new build"
    goal: "Verify build is stable"
    example: "Can user login, view jobs, submit application?"
    execution_time: "2-5 minutes"

  sanity_testing:
    scope: "Narrow - specific area"
    depth: "Deep - detailed checks"
    trigger: "After bug fix, minor feature"
    goal: "Verify specific change works"
    example: "Does email validation accept + sign now?"
    execution_time: "5-10 minutes"
```

---

## Test Types Integration Summary

### DDD Integration

**Value Objects**:
- Functional: Validation rules
- Non-functional: Immutability (Maintainability)
- Regression: Validation logic unchanged

**Aggregates**:
- Functional: Invariants enforced
- Non-functional: Performance (efficient state changes)
- Regression: Invariants still enforced after changes

**Application Services**:
- Functional: Use case orchestration
- Non-functional: Performance (response time), Security (authorization)
- Regression: Orchestration logic unchanged

**Bounded Contexts**:
- Functional: Complete workflows
- Non-functional: Reliability (fault tolerance between contexts)
- Regression: Context boundaries maintained

---

### UX Integration

**Atoms**:
- Functional: Basic interactions (click, focus)
- Non-functional: Accessibility (ARIA), Usability (operability)
- Regression: Styling and behavior consistent

**Molecules**:
- Functional: Validation logic
- Non-functional: Usability (error messages), Accessibility (labels)
- Regression: Validation rules unchanged

**Organisms**:
- Functional: Complex interactions
- Non-functional: Performance (rendering), Usability (composition)
- Regression: Component composition unchanged

**Pages**:
- Functional: Data loading, form submission
- Non-functional: Performance (load time), Accessibility (page structure)
- Regression: Page workflows unchanged

**Workflows**:
- Functional: Multi-step completion
- Non-functional: Usability (task completion), Performance (workflow speed)
- Smoke/Sanity: Critical paths verified

---

## Test Types Metrics Summary

```yaml
overall_test_metrics:
  functional_testing:
    requirements_coverage: "96.7%"
    test_pass_rate: "96.4%"

  performance_testing:
    page_load_p95: "<2 seconds"
    api_response_p95: "<500ms"
    lighthouse_score: ">90"

  security_testing:
    critical_vulnerabilities: 0
    owasp_top_10_coverage: "100%"
    penetration_test_findings: "0 high/critical"

  usability_testing:
    sus_score: 78
    task_success_rate: "88%"

  accessibility_testing:
    wcag_2.2_aa_compliance: "100%"
    axe_critical_violations: 0

  reliability_testing:
    availability: "99.95%"
    mtbf: "30 days"
    mttr: "18.3 minutes"

  maintainability_testing:
    code_coverage: "92%"
    sonarqube_rating: "A"
    technical_debt: "2 days"

  portability_testing:
    environment_support: 3
    installation_time: "5 minutes"

  compatibility_testing:
    browser_support: "100% (Chrome, Firefox, Safari, Edge)"
    device_support: "100% (Desktop, Tablet, Mobile)"

  regression_testing:
    automation_rate: "93%"
    pass_rate: "99.4%"
    defect_escape_rate: "4%"

  smoke_testing:
    execution_time: "2 minutes"
    coverage: "8 critical paths"

  sanity_testing:
    execution_time: "5 minutes"
    focus: "Specific bug fix/feature"
```

---

## Tools and Frameworks

### Functional Testing
- **Backend**: Jest, pytest, JUnit
- **Frontend**: React Testing Library, Playwright, Cypress
- **BDD**: Cucumber, SpecFlow

### Performance Testing
- **Load/Stress**: k6, JMeter, Gatling
- **Monitoring**: Prometheus, Grafana
- **Profiling**: Chrome DevTools, Lighthouse

### Security Testing
- **Scanning**: Snyk, OWASP ZAP, SonarQube
- **Penetration**: Burp Suite, Metasploit
- **Secrets**: git-secrets, TruffleHog

### Usability/Accessibility Testing
- **Accessibility**: axe-core, jest-axe, WAVE
- **Screen Readers**: NVDA, JAWS, VoiceOver
- **Usability**: UserTesting, Hotjar, Maze

### Reliability Testing
- **Chaos Engineering**: Chaos Monkey, Gremlin
- **Monitoring**: Pingdom, UptimeRobot

### Maintainability Testing
- **Code Quality**: SonarQube, ESLint, Prettier
- **Documentation**: JSDoc, Storybook

### Compatibility Testing
- **Cross-Browser**: BrowserStack, Sauce Labs, Playwright
- **API Testing**: Postman, Insomnia, Pact (contract testing)

### Regression/Smoke/Sanity Testing
- **CI/CD**: GitHub Actions, GitLab CI, Jenkins
- **Test Management**: TestRail, Zephyr

---

## References

### Standards
- ISTQB Foundation Level Syllabus v4.0 (2023)
- ISO/IEC 25010:2011 - Systems and software Quality Requirements and Evaluation (SQuaRE)
- WCAG 2.2 - Web Content Accessibility Guidelines (W3C, 2023)
- EN 301 549 v3.2.1 - European Accessibility Standard
- EU Directive 2019/882 - European Accessibility Act
- OWASP Top 10 (2021)

### DDD Integration
- Job Seeker DDD: `wiki/research/ddd/working-docs/06-ontological-taxonomy.md`
- DDD Tactical Patterns: `wiki/research/ddd/working-docs/03-tactical-patterns.md`
- DDD Strategic Patterns: `wiki/research/ddd/working-docs/02-strategic-patterns.md`

### UX Integration
- Job Seeker UX: `wiki/research/ux/working-docs/08-ux-ontological-taxonomy.md`
- Atomic Design: `wiki/research/ux/working-docs/05-component-architecture.md`
- Page Architecture: `wiki/research/ux/working-docs/04-page-architecture.md`
- Workflow Patterns: `wiki/research/ux/working-docs/03-workflow-patterns.md`

### QE Integration
- QE-DDD-UX Integration: `research/qe/QE-DDD-UX-INTEGRATION.md`
- Test Levels: `wiki/research/qe/deliverables/04-domain-IV-test-levels.md`
- QE YAML Schema: `research/qe/deliverables/16-qe-yaml-schema.yaml`

---

**Document Status:** Complete
**Version:** 1.0
**Date:** 2025-10-04
**Lines:** 1000+
