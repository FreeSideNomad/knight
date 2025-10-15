# Workflow Patterns

## Overview

Workflow patterns define how users accomplish multi-step tasks within an application. Effective workflows guide users through complex processes, provide feedback at each step, handle errors gracefully, and align with business domain logic.

**Integration with DDD**: Workflows map directly to DDD application service use cases. Each workflow represents a user-facing implementation of domain operations, often spanning multiple bounded contexts and publishing domain events.

---

## Workflow Pattern Categories

### 1. Linear Workflows (Wizard/Stepped)
### 2. Flexible Workflows (Dashboard/Hub)
### 3. Guided Workflows (Progressive Disclosure)
### 4. Batch Workflows (Multi-Item Actions)
### 5. Conditional Workflows (Branching Logic)
### 6. Long-Running Workflows (Async/Background)

---

## 1. Linear Workflows (Wizard/Stepped)

**Description**: Sequential, step-by-step process with defined beginning and end.

**When to Use**:
- Complex process with clear sequence
- Each step depends on previous step
- Users need guidance
- Data collection across multiple screens

### Pattern: Multi-Step Form Wizard

**Structure**:
```
Step 1 → Step 2 → Step 3 → Step 4 → Complete
[●──────○──────○──────○]
```

**Example: Complete Profile Setup**

**DDD Mapping**:
```yaml
workflow: profile_setup_wizard
application_service: svc_app_create_candidate_profile
bounded_context: bc_profile
aggregate: agg_candidate_profile

steps:
  - step: 1
    label: "Basic Information"
    collects:
      - vo_email
      - vo_name
      - vo_location
    validation: client_side + vo_validation_rules

  - step: 2
    label: "Work Experience"
    collects:
      - ent_work_experience (collection)
    validation: at_least_one_required
    can_skip: false

  - step: 3
    label: "Skills & Expertise"
    collects:
      - vo_skills
      - vo_experience_level
    validation: vo_validation_rules
    can_skip: false

  - step: 4
    label: "Job Preferences"
    collects:
      - vo_job_preferences
    validation: vo_validation_rules
    can_skip: true

  - step: 5
    label: "Review & Submit"
    displays: aggregate_summary
    action: save_candidate_profile
    publishes: evt_profile_created
```

**UI Implementation**:
```
┌─────────────────────────────────────────────────────┐
│ Profile Setup                          Step 2 of 4  │
├─────────────────────────────────────────────────────┤
│ [●────●────○────○] Basic│Experience│Skills│Review  │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Work Experience                                     │
│                                                     │
│ [Job Title__________________________]               │
│ [Company____________________________]               │
│ [From Date____] [To Date____] ☑ Current            │
│ [Description________________________]               │
│ [________________________________]                  │
│                                                     │
│ [+ Add Another Position]                            │
│                                                     │
│ ┌─────────────────────────────────────────────┐   │
│ │ Software Engineer                           │   │
│ │ TechCorp • 2020-2023                        │   │
│ │ [Edit] [Remove]                             │   │
│ └─────────────────────────────────────────────┘   │
│                                                     │
├─────────────────────────────────────────────────────┤
│ [← Previous]                      [Next: Skills →]  │
└─────────────────────────────────────────────────────┘
```

**Navigation Rules**:
- **Next**: Enabled only if current step valid
- **Previous**: Always enabled, doesn't validate
- **Progress saved**: Each step auto-saves (draft state)
- **Exit/Cancel**: Confirm dialog if data entered

**Validation Strategy**:
```yaml
validation:
  client_side:
    - trigger: on_blur  # Field loses focus
    - trigger: on_next_click  # Step navigation
    - display: inline_error_messages

  server_side:
    - trigger: on_step_complete
    - validates: vo_validation_rules (from DDD)
    - returns: validation_errors or success

  final_validation:
    - trigger: on_submit (step 5)
    - validates: entire_aggregate
    - checks: aggregate_invariants
```

---

### Pattern: Checkout/Application Flow

**Example: Submit Job Application**

**DDD Mapping**:
```yaml
workflow: submit_job_application
application_service: svc_app_submit_application
spans_contexts: true  # Crosses multiple bounded contexts

steps:
  - step: 1
    label: "Review Job"
    bounded_context: bc_job_catalog
    aggregate: agg_job_posting
    aggregate_id: from_url_param
    action: display_job_details
    cta: "Apply Now"

  - step: 2
    label: "Review Profile"
    bounded_context: bc_profile
    aggregate: agg_candidate_profile
    action: display_profile_summary
    validation: check_profile_completeness
    error_if: profile_completeness < 80
    cta: "Continue to Application"

  - step: 3
    label: "Cover Letter"
    bounded_context: bc_applications
    creates: agg_application (draft)
    collects:
      - cover_letter_text
      - resume_file (optional)
    cta: "Review Application"

  - step: 4
    label: "Review & Submit"
    bounded_context: bc_applications
    displays:
      - job_summary (from bc_job_catalog)
      - profile_summary (from bc_profile)
      - cover_letter (from draft application)
    action: submit_application
    publishes: evt_application_submitted
    success_redirect: "/applications/{{application_id}}"
```

**UI Implementation**:
```
┌──────────────────────────────────────────────────────┐
│ Apply to Frontend Developer at TechCorp   Step 3/4  │
├──────────────────────────────────────────────────────┤
│ [●────●────●────○] Job│Profile│Letter│Submit        │
├──────────────────────────────────────────────────────┤
│                                                      │
│ Cover Letter (Optional)                              │
│                                                      │
│ ┌──────────────────────────────────────────────┐   │
│ │ Dear Hiring Manager,                         │   │
│ │                                              │   │
│ │ I am excited to apply for...                 │   │
│ │                                              │   │
│ └──────────────────────────────────────────────┘   │
│                                                      │
│ Resume (Optional)                                    │
│ ☑ Use profile resume: john_doe_resume.pdf           │
│ [ ] Upload different resume  [Choose File]          │
│                                                      │
│ Notifications                                        │
│ ☑ Email me when employer reviews application        │
│ ☑ Email me if employer responds                     │
│                                                      │
├──────────────────────────────────────────────────────┤
│ [← Back]              [Review & Submit Application] │
└──────────────────────────────────────────────────────┘
```

**State Management**:
```yaml
application_state:
  status: DRAFT  # DDD: application aggregate status

  data:
    job_id: "12345"  # Reference to bc_job_catalog
    candidate_id: "67890"  # Reference to bc_profile
    cover_letter: "Dear Hiring Manager..."
    resume_ref: "candidate_profile.resume"

  metadata:
    created_at: "2025-10-04T10:30:00Z"
    current_step: 3
    completed_steps: [1, 2, 3]
    can_submit: false  # True when step 4 complete
```

---

## 2. Flexible Workflows (Dashboard/Hub)

**Description**: Non-linear workflow where user chooses next action from a hub.

**When to Use**:
- No required sequence
- Multiple independent tasks
- User-driven exploration
- Complex applications with many features

### Pattern: Dashboard with Action Cards

**Example: Job Seeker Dashboard**

**DDD Mapping**:
```yaml
workflow: job_seeking_dashboard
bounded_contexts: [bc_profile, bc_matching, bc_applications, bc_skills_analysis]
pattern: hub_and_spoke

hub:
  name: "Dashboard"
  url: "/dashboard"
  displays:
    - widget: profile_completeness
      source: bc_profile (agg_candidate_profile)
      action: "Complete Profile" → /profile/edit

    - widget: new_job_matches
      source: bc_matching
      query: unviewed_matches
      action: "View Matches" → /jobs/matches?filter=new

    - widget: active_applications
      source: bc_applications
      query: applications_with_pending_actions
      action: "Manage Applications" → /applications

    - widget: skills_insights
      source: bc_skills_analysis
      displays: top_skill_gap
      action: "View Skills Gap" → /career/skills-gap
```

**UI Implementation**:
```
┌──────────────────────────────────────────────────────┐
│ Welcome back, Marina!                                │
├──────────────────────────────────────────────────────┤
│                                                      │
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │
│ │ Profile      │ │ Job Matches  │ │ Applications │ │
│ │              │ │              │ │              │ │
│ │ 85% Complete │ │ 12 new       │ │ 3 active     │ │
│ │              │ │ matches      │ │ 1 interview  │ │
│ │ [Complete]   │ │ [View All]   │ │ [Manage]     │ │
│ └──────────────┘ └──────────────┘ └──────────────┘ │
│                                                      │
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │
│ │ Skills Gap   │ │ Projects     │ │ Saved Jobs   │ │
│ │              │ │              │ │              │ │
│ │ AWS, Docker  │ │ 2 suggested  │ │ 8 jobs       │ │
│ │ missing      │ │ projects     │ │ saved        │ │
│ │ [Analyze]    │ │ [Explore]    │ │ [Review]     │ │
│ └──────────────┘ └──────────────┘ └──────────────┘ │
│                                                      │
│ Suggested Actions                                    │
│ • Complete work history to improve matches           │
│ • Respond to interview request from TechCorp         │
│ • Start "Build REST API" project to learn AWS        │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**Action Routing**:
```yaml
suggested_actions:
  - condition: profile_completeness < 100
    source: bc_profile
    text: "Complete work history to improve matches"
    url: "/profile/edit#work-history"
    domain_logic: "Profile completeness affects match quality"

  - condition: has_pending_interview_response
    source: bc_applications
    aggregate_id: "app_456"
    text: "Respond to interview request from {{company}}"
    url: "/applications/{{id}}"
    priority: high
    domain_event: evt_interview_scheduled

  - condition: skill_gap_exists
    source: bc_skills_analysis
    aggregate: agg_skills_gap
    text: "Start '{{project_name}}' project to learn {{skill}}"
    url: "/career/projects/{{project_id}}"
    domain_logic: "Project recommendation based on skills gap"
```

---

## 3. Guided Workflows (Progressive Disclosure)

**Description**: Workflow reveals complexity gradually based on user choices.

**When to Use**:
- Complex features with simple defaults
- Expert vs novice users
- Conditional steps

### Pattern: Adaptive Form

**Example: Job Search with Progressive Filters**

**DDD Mapping**:
```yaml
workflow: job_search
bounded_context: bc_job_catalog
service: svc_app_search_jobs

initial_view:
  complexity: simple
  fields:
    - keywords (text input)
    - location (text input with autocomplete)
  action: basic_search

progressive_disclosure:
  - trigger: "Advanced Filters" link clicked
    reveals:
      - job_type (vo_job_type - checkboxes)
      - experience_level (vo_experience_level - dropdown)
      - salary_range (min/max inputs)
      - remote_option (vo_location.remote - toggle)

  - trigger: "More Filters" clicked
    reveals:
      - company_size (faceted filter)
      - benefits (multi-select)
      - technologies (vo_skills - tag input)
      - posting_date (date range)
```

**UI Implementation**:
```
Simple View:
┌──────────────────────────────────────────────────────┐
│ Find Jobs                                            │
├──────────────────────────────────────────────────────┤
│ Keywords: [frontend developer__________] [Search]   │
│ Location: [Remote________________] ▼                 │
│                                                      │
│ [+ Advanced Filters]                                 │
└──────────────────────────────────────────────────────┘

Advanced View (after clicking "Advanced Filters"):
┌──────────────────────────────────────────────────────┐
│ Find Jobs                                            │
├──────────────────────────────────────────────────────┤
│ Keywords: [frontend developer__________] [Search]   │
│ Location: [Remote________________] ▼                 │
│                                                      │
│ ▼ Advanced Filters                                   │
│   Job Type:  ☑ Full-time ☐ Part-time ☐ Contract    │
│   Experience: [Mid-level_______] ▼                   │
│   Salary:    [$80,000___] to [$120,000___]          │
│   Remote:    ● Remote ○ Hybrid ○ On-site            │
│                                                      │
│   [+ More Filters] [Reset Filters]                   │
└──────────────────────────────────────────────────────┘
```

**State Management**:
```yaml
search_state:
  mode: advanced  # simple | advanced | expert
  filters:
    keywords: "frontend developer"
    location: {type: remote}  # vo_location
    job_type: [full_time]  # vo_job_type
    experience_level: mid  # vo_experience_level
    salary_min: 80000
    salary_max: 120000

  ui_state:
    advanced_filters_expanded: true
    more_filters_expanded: false
```

---

## 4. Batch Workflows (Multi-Item Actions)

**Description**: Perform actions on multiple items simultaneously.

**When to Use**:
- Managing collections
- Bulk operations
- Efficiency for power users

### Pattern: Select and Act

**Example: Manage Saved Jobs**

**DDD Mapping**:
```yaml
workflow: manage_saved_jobs
bounded_context: bc_job_catalog
aggregate: agg_job_posting (multiple instances)

actions:
  - action: remove_from_saved
    service: svc_app_unsave_jobs
    input: job_ids[]
    publishes: evt_jobs_unsaved

  - action: apply_to_multiple
    service: svc_app_bulk_apply
    input: job_ids[]
    precondition: profile_complete AND cover_letter_provided
    publishes: evt_application_submitted (multiple events)

  - action: export_to_pdf
    service: generic_export_service
    input: job_ids[]
```

**UI Implementation**:
```
┌──────────────────────────────────────────────────────┐
│ Saved Jobs (8)                     [Select All ▼]   │
├──────────────────────────────────────────────────────┤
│ ☑ Frontend Developer • TechCorp • Remote            │
│   Match: 85% • Saved 2 days ago                      │
│                                                      │
│ ☑ React Developer • StartupCo • Hybrid              │
│   Match: 78% • Saved 1 week ago                      │
│                                                      │
│ ☐ Full Stack Engineer • BigCorp • On-site           │
│   Match: 72% • Saved 2 weeks ago                     │
│                                                      │
│ [2 selected]                                         │
│ [Apply to Selected] [Remove from Saved] [Export]    │
└──────────────────────────────────────────────────────┘
```

**Interaction Flow**:
```yaml
flow:
  - user_action: check_checkbox
    updates: selected_items[]

  - user_action: click_select_all
    updates: selected_items[] = all_job_ids

  - user_action: click_apply_to_selected
    validation:
      - check: selected_items.length > 0
        error: "Please select at least one job"
      - check: profile_completeness >= 80
        error: "Complete your profile to apply"

    if_valid:
      - display: bulk_apply_confirmation_dialog
      - on_confirm: invoke_svc_app_bulk_apply
      - on_success: show_success_message
      - navigate_to: /applications
```

**Confirmation Dialog**:
```
┌──────────────────────────────────────────────────────┐
│ Apply to 2 Jobs?                          [Close ✕] │
├──────────────────────────────────────────────────────┤
│                                                      │
│ You're about to apply to:                            │
│ • Frontend Developer at TechCorp                     │
│ • React Developer at StartupCo                       │
│                                                      │
│ Your profile and standard cover letter will be sent. │
│                                                      │
│ [Cancel]                        [Apply to 2 Jobs →] │
└──────────────────────────────────────────────────────┘
```

---

## 5. Conditional Workflows (Branching Logic)

**Description**: Workflow path changes based on user input or system state.

**When to Use**:
- Different user types or roles
- Conditional requirements
- Context-dependent processes

### Pattern: Branching Wizard

**Example: Application Submission (with variations)**

**DDD Mapping**:
```yaml
workflow: submit_application
application_service: svc_app_submit_application
conditional_logic: true

branches:
  - condition: is_quick_apply_eligible
    criteria:
      - profile_completeness >= 90
      - has_default_resume = true
      - application_type = standard
    flow: quick_apply_flow

  - condition: requires_custom_questions
    criteria:
      - job.has_screening_questions = true
    flow: extended_application_flow

  - condition: default
    flow: standard_application_flow

flows:
  quick_apply_flow:
    steps:
      - confirm_profile_and_submit  # Single step
    skip_steps: [review_profile, cover_letter]

  extended_application_flow:
    steps:
      - review_job
      - review_profile
      - screening_questions  # Additional step
      - cover_letter
      - review_and_submit

  standard_application_flow:
    steps:
      - review_job
      - review_profile
      - cover_letter
      - review_and_submit
```

**UI Decision Point**:
```
Job Detail Page:

┌──────────────────────────────────────────────────────┐
│ Frontend Developer at TechCorp                       │
│ Remote • $100-120k • Posted 2 days ago               │
│                                                      │
│ Your match: 85%                                      │
│ Profile complete: 95% ✓                              │
│                                                      │
│ [Quick Apply ⚡]  [Standard Application →]           │
│  ↓                 ↓                                 │
│  1 step            4 steps                           │
└──────────────────────────────────────────────────────┘

User clicks "Quick Apply":
  → Flow: quick_apply_flow
  → Steps: Single confirmation dialog

User clicks "Standard Application":
  → Flow: standard_application_flow
  → Steps: Multi-step wizard
```

**Quick Apply Confirmation**:
```
┌──────────────────────────────────────────────────────┐
│ Quick Apply to TechCorp?                  [Close ✕] │
├──────────────────────────────────────────────────────┤
│                                                      │
│ Your profile will be sent to TechCorp:               │
│                                                      │
│ ✓ Marina Candidate                                   │
│ ✓ marina@email.com                                   │
│ ✓ Resume: marina_resume.pdf                          │
│ ✓ Skills: React, TypeScript, Node.js                 │
│                                                      │
│ Default cover letter will be included.               │
│                                                      │
│ [ ] Notify me about similar jobs from TechCorp       │
│                                                      │
│ [Cancel]                    [Submit Application →]  │
└──────────────────────────────────────────────────────┘
```

**Branch Logic Implementation**:
```javascript
function determineApplicationFlow(job, candidate) {
  // DDD: Business logic in application service

  const profileComplete = candidate.profile_completeness >= 90;
  const hasResume = candidate.resume !== null;
  const hasScreeningQuestions = job.screening_questions.length > 0;

  if (profileComplete && hasResume && !hasScreeningQuestions) {
    return 'quick_apply_flow';
  } else if (hasScreeningQuestions) {
    return 'extended_application_flow';
  } else {
    return 'standard_application_flow';
  }
}
```

---

## 6. Long-Running Workflows (Async/Background)

**Description**: Workflows that take significant time, running in background.

**When to Use**:
- Data processing/analysis
- External API calls
- Large file uploads
- Complex calculations

### Pattern: Background Job with Progress Tracking

**Example: Generate Skills Gap Analysis**

**DDD Mapping**:
```yaml
workflow: generate_skills_gap_analysis
application_service: svc_app_analyze_skills_gap
bounded_context: bc_skills_analysis
processing_mode: asynchronous

steps:
  - step: initiate
    user_action: click_generate_analysis
    service_action: create_analysis_job
    response: job_id
    status: PENDING

  - step: processing
    runs_in_background: true
    domain_service: svc_domain_calculate_skills_gap
    publishes: evt_analysis_started

    sub_tasks:
      - extract_candidate_skills (from bc_profile)
      - extract_market_requirements (from bc_job_catalog)
      - compare_skills
      - generate_recommendations

  - step: completion
    publishes: evt_analysis_completed
    notification: email + in_app
    status: COMPLETE
    result: agg_skills_gap
```

**UI Implementation - Initiated**:
```
┌──────────────────────────────────────────────────────┐
│ Skills Gap Analysis                                  │
├──────────────────────────────────────────────────────┤
│                                                      │
│ Analyze your skills against market demand            │
│                                                      │
│ We'll compare your profile with:                     │
│ • 1,247 job postings in your field                   │
│ • Industry skill trends                              │
│ • Your saved and matched jobs                        │
│                                                      │
│ This analysis takes about 30 seconds.                │
│                                                      │
│ [Generate Analysis]                                  │
└──────────────────────────────────────────────────────┘
```

**UI Implementation - Processing**:
```
┌──────────────────────────────────────────────────────┐
│ Analyzing Your Skills...                             │
├──────────────────────────────────────────────────────┤
│                                                      │
│ ▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░ 60%                            │
│                                                      │
│ ✓ Extracted your skills                              │
│ ✓ Analyzed 1,247 job postings                        │
│ ⟳ Comparing skills...                                │
│ ○ Generating recommendations                         │
│                                                      │
│ You can leave this page. We'll notify you when done. │
│                                                      │
│ [View in Background] [Cancel Analysis]              │
└──────────────────────────────────────────────────────┘
```

**UI Implementation - Complete (Notification)**:
```
In-App Notification:
┌──────────────────────────────────────────────────────┐
│ 🎉 Your Skills Gap Analysis is ready!                │
│ We found 3 high-demand skills you could learn.       │
│ [View Analysis →]                                    │
└──────────────────────────────────────────────────────┘

Email Notification:
Subject: Your Skills Gap Analysis is Ready
Body:
  Hi Marina,

  We've analyzed your skills against 1,247 job postings.

  Key Findings:
  • 3 high-demand skills you're missing
  • 2 skills you have that are in high demand
  • 85% overall market readiness

  [View Full Analysis]
```

**State Management**:
```yaml
analysis_job:
  job_id: "analysis_789"
  aggregate: agg_skills_gap
  status: COMPLETE  # PENDING | PROCESSING | COMPLETE | FAILED

  metadata:
    created_at: "2025-10-04T10:00:00Z"
    started_at: "2025-10-04T10:00:05Z"
    completed_at: "2025-10-04T10:00:35Z"
    duration_ms: 30000

  progress:
    current_step: 4
    total_steps: 4
    percent_complete: 100

  result:
    skills_gap_id: "gap_123"
    url: "/career/skills-gap/gap_123"
```

---

## Workflow Design Principles

### 1. Clear Entry Points

**Do**:
- Obvious calls-to-action
- Context for what workflow does
- Clear starting point

**Example**:
```
Job Detail:
[Apply Now →]  ← Clear, action-oriented

Dashboard:
"Complete your profile to get better matches"
[Complete Profile →]  ← Clear benefit + action
```

---

### 2. Progress Indicators

**Do**:
- Show current step
- Show total steps
- Indicate completed steps
- Show what's coming next

**Types**:
- **Stepped**: `[●──●──○──○]` Basic | Experience | Skills | Review
- **Percentage**: `[▓▓▓▓▓░░░░░] 50%`
- **Textual**: `Step 2 of 4: Work Experience`

---

### 3. Validation Feedback

**Do**:
- Inline validation (as user types)
- Summary validation (before submit)
- Clear error messages
- Actionable guidance

**Example**:
```
Email field:
[marina@invalid] ✕
│
└─ Please enter a valid email address
   Example: you@example.com
```

**DDD Integration**:
```yaml
validation:
  field: email
  vo_ref: vo_email
  validation_rules:
    - format: email_regex
    - required: true
  error_messages:
    invalid_format: "Please enter a valid email address"
    required: "Email is required"
```

---

### 4. Save Progress

**Do**:
- Auto-save draft state
- Allow resume later
- Indicate when saved

**Example**:
```
Profile Setup - Step 2:
[Work Experience]

Last saved: 2 minutes ago ✓

[← Previous] [Save and Exit] [Next →]
```

**DDD Integration**:
```yaml
draft_state:
  aggregate: agg_candidate_profile
  status: DRAFT  # Domain aggregate can be in DRAFT state
  auto_save: every_30_seconds
  manual_save: save_and_exit_button
```

---

### 5. Error Handling

**Do**:
- Graceful degradation
- Retry options
- Clear error messages
- Don't lose user data

**Example - Server Error**:
```
┌──────────────────────────────────────────────────────┐
│ ⚠ Unable to Submit Application                       │
├──────────────────────────────────────────────────────┤
│                                                      │
│ We couldn't submit your application due to a         │
│ server error. Your information has been saved.       │
│                                                      │
│ [Try Again] [Save and Exit] [Contact Support]       │
└──────────────────────────────────────────────────────┘
```

**DDD Integration**:
```yaml
error_handling:
  error_type: server_error
  user_data_state: saved_as_draft
  aggregate_state: DRAFT (not SUBMITTED)
  domain_event: not_published (evt_application_submitted)

  recovery_actions:
    - retry_submission
    - save_and_exit
    - contact_support
```

---

### 6. Success Confirmation

**Do**:
- Confirm completion
- Show next steps
- Provide relevant actions

**Example - Application Submitted**:
```
┌──────────────────────────────────────────────────────┐
│ ✓ Application Submitted!                             │
├──────────────────────────────────────────────────────┤
│                                                      │
│ Your application to Frontend Developer at TechCorp   │
│ has been submitted successfully.                     │
│                                                      │
│ What's next:                                         │
│ • Employer typically responds in 3-5 days            │
│ • We'll notify you of any updates                    │
│ • Track your application status anytime              │
│                                                      │
│ [View Application] [Apply to Similar Jobs]          │
└──────────────────────────────────────────────────────┘
```

**DDD Integration**:
```yaml
success_state:
  aggregate: agg_application
  status: SUBMITTED  # Final state
  domain_event_published: evt_application_submitted

  next_actions:
    - view_application: /applications/{{id}}
    - find_similar_jobs: /jobs/search?similar_to={{job_id}}
```

---

## Workflow State Persistence

### Pattern: Resume Incomplete Workflow

**DDD Mapping**:
```yaml
workflow_state:
  workflow_id: "profile_setup_wizard"
  user_id: "12345"

  state:
    current_step: 3
    completed_steps: [1, 2]
    data:
      step_1: {name: "Marina", email: "marina@example.com"}
      step_2: {work_experience: [{...}]}
      step_3: {skills: ["React", "TypeScript"]}  # In progress

  stored_in: bc_profile
  aggregate: agg_candidate_profile
  aggregate_status: DRAFT

  resume_url: "/profile/setup?step=3"
```

**UI - Return to Workflow**:
```
Dashboard:
┌──────────────────────────────────────────────────────┐
│ ⚠ You have an incomplete profile setup               │
│                                                      │
│ You're 60% done. Complete your profile to get        │
│ personalized job matches.                            │
│                                                      │
│ [Resume Setup →] [Dismiss]                           │
└──────────────────────────────────────────────────────┘
```

---

## Cross-Context Workflows

### Pattern: Workflow Spanning Multiple Bounded Contexts

**Example: "Find Job, Analyze Fit, Apply" Workflow**

**DDD Mapping**:
```yaml
workflow: complete_job_application_journey
spans_contexts: [bc_job_catalog, bc_matching, bc_skills_analysis, bc_applications]

journey:
  - phase: discovery
    bounded_context: bc_job_catalog
    actions:
      - browse_jobs
      - search_jobs
      - view_job_details

  - phase: evaluation
    bounded_context: bc_matching
    service: svc_domain_calculate_match
    displays:
      - match_score (vo_match_score)
      - skills_overlap

  - phase: gap_analysis
    bounded_context: bc_skills_analysis
    service: svc_app_analyze_skills_gap
    displays:
      - agg_skills_gap
      - missing_skills
      - project_recommendations

  - phase: application
    bounded_context: bc_applications
    service: svc_app_submit_application
    creates: agg_application
    publishes: evt_application_submitted

context_transitions:
  - from: bc_job_catalog
    to: bc_skills_analysis
    trigger: "View Skills Gap for This Job" button
    data_passed: job_id

  - from: bc_skills_analysis
    to: bc_applications
    trigger: "Apply Anyway" button (even with gaps)
    data_passed: job_id, skills_gap_id

  - from: bc_job_catalog
    to: bc_applications
    trigger: "Quick Apply" button
    data_passed: job_id
```

**UI Flow**:
```
1. Job Detail (bc_job_catalog)
   [View Skills Gap] [Apply Now]
          ↓                ↓
   2. Skills Gap    4. Application Flow
      (bc_skills_analysis)  (bc_applications)
          ↓
   3. [Apply Anyway]
          ↓
   4. Application Flow
      (bc_applications)
```

---

## Accessibility in Workflows

### ARIA Live Regions for Progress

```html
<div role="status" aria-live="polite" aria-atomic="true">
  Step 2 of 4 complete. Now on step 3: Skills & Expertise
</div>
```

### Keyboard Navigation

```yaml
keyboard_shortcuts:
  - key: "Tab"
    action: move_to_next_field

  - key: "Shift+Tab"
    action: move_to_previous_field

  - key: "Enter"
    action: submit_current_step (if valid)

  - key: "Escape"
    action: close_dialog_or_cancel
```

### Focus Management

```javascript
// When moving to next step, focus the first input
function goToNextStep(stepNumber) {
  renderStep(stepNumber);

  const firstInput = document.querySelector('input, select, textarea');
  if (firstInput) {
    firstInput.focus();
  }

  // Announce to screen readers
  announceToScreenReader(`Step ${stepNumber}: ${stepTitle}`);
}
```

---

## Performance Considerations

### Lazy Loading Workflow Steps

```yaml
optimization:
  pattern: lazy_load_steps

  initial_load:
    - step_1_component
    - workflow_framework

  on_demand_load:
    - step_2_component (when user clicks "Next" from step 1)
    - step_3_component (when user clicks "Next" from step 2)

  prefetch:
    - step_N+1 (prefetch next step when user is on step N)
```

### Optimistic UI Updates

```yaml
pattern: optimistic_updates

example: save_draft_profile
  user_action: click_save

  ui_update: immediate
    - show_saving_indicator
    - update_last_saved_time

  server_call: async
    - POST /api/profile/draft

  on_success:
    - hide_saving_indicator
    - show_saved_confirmation

  on_error:
    - revert_ui_update
    - show_error_message
    - restore_previous_state
```

---

## Key Takeaways

1. **Workflows = Use Cases**: UI workflows directly implement DDD application service use cases, making business logic visible to users.

2. **Choose Right Pattern**: Linear for sequential tasks, flexible for exploratory tasks, guided for progressive complexity.

3. **State Management**: Workflow state maps to DDD aggregate state (DRAFT, IN_PROGRESS, COMPLETE).

4. **Cross-Context Awareness**: Workflows often span bounded contexts; make transitions clear and respect context mapping relationships.

5. **Progress & Feedback**: Always show where user is, what's next, and when actions complete.

6. **Error Resilience**: Save user data, allow retry, provide clear recovery paths.

7. **Accessibility**: ARIA live regions, keyboard navigation, focus management are requirements.

8. **Performance**: Lazy load steps, optimistic updates, background processing for long tasks.

---

## References

**Primary Sources**:
- Tidwell, Jenifer et al. (2020). "Designing Interfaces" - Workflow patterns
- Nielsen Norman Group - Task completion research
- Material Design - Steppers and progress indicators
- Apple HIG - User flow patterns

**DDD Integration**:
- `research/ddd/deliverables/ddd-schema-example.yaml` - application services and use cases
- `research/ddd/working-docs/03-tactical-patterns.md` - aggregate states, domain events
- `research/ux/UX-DDD-INTEGRATION.md` - workflow mapping to services
- `research/ux/working-docs/01-ia-foundations.md` - task-focused IA
- `research/ux/working-docs/02-navigation-patterns.md` - cross-context navigation

---

*Document created: 2025-10-04*
*Part of UX Research Phase 4: Workflow Patterns*
