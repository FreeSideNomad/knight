# Page Architecture

## Overview

Page architecture defines the structure, layout, and content organization of individual pages within an application. Effective page architecture balances information density, visual hierarchy, user task completion, and system performance.

**Integration with DDD**: Pages are organized by bounded context and display aggregates. Each page type has specific responsibilities aligned with domain structure: list pages display aggregate collections, detail pages display single aggregates, and form pages create/edit aggregates.

---

## Page Type Classification

### 1. List/Collection Pages
### 2. Detail/Entity Pages
### 3. Form/Edit Pages
### 4. Dashboard/Overview Pages
### 5. Search/Filter Pages
### 6. Empty State Pages
### 7. Error Pages

---

## 1. List/Collection Pages

**Purpose**: Display collections of items (aggregates) with browsing, sorting, and filtering.

### Pattern: Job Listings Page

**DDD Mapping**:
```yaml
page_type: list_collection
bounded_context: bc_job_catalog
displays: collection_of_agg_job_posting
data_source: repo_job_posting
default_query: get_active_jobs
```

**Page Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ Page Title & Actions                                         │
│ Jobs (1,247)                              [Post Alert] [⋮]   │
├──────────────────────────────────────────────────────────────┤
│ Filters        │ List Content                                │
│ (Sidebar)      │                                             │
│                │ Sort: [Best Match ▼]      [Grid] [List]    │
│ Location       │                                             │
│ ☑ Remote       │ ┌─────────────────────────────────────┐   │
│ ☐ Hybrid       │ │ Frontend Developer                   │   │
│ ☐ On-site      │ │ TechCorp • Remote • $100-120k       │   │
│                │ │ Match: 85%  Posted: 2 days ago      │   │
│ Job Type       │ │ React TypeScript Node.js            │   │
│ ☑ Full-time    │ │ [Save] [Apply]                      │   │
│ ☐ Part-time    │ └─────────────────────────────────────┘   │
│ ☐ Contract     │                                             │
│                │ ┌─────────────────────────────────────┐   │
│ Experience     │ │ React Developer                      │   │
│ ● All          │ │ StartupCo • Hybrid • $90-110k       │   │
│ ○ Entry        │ │ Match: 78%  Posted: 1 week ago      │   │
│ ○ Mid          │ │ React Redux GraphQL                 │   │
│ ○ Senior       │ │ [Save] [Apply]                      │   │
│                │ └─────────────────────────────────────┘   │
│ [Clear All]    │                                             │
│                │ [Load More Jobs]                            │
└────────────────┴─────────────────────────────────────────────┘
│ Footer                                                       │
└──────────────────────────────────────────────────────────────┘
```

**Anatomy**:
```yaml
page_sections:
  - section: header
    type: global_navigation
    sticky: true

  - section: page_header
    contains:
      - page_title: "Jobs"
      - item_count: "{{total_jobs}}"
      - primary_action: "Post Alert"
      - utility_menu: "⋮"

  - section: filters_sidebar
    width: 280px
    position: left
    collapsible: true_on_mobile
    contains:
      - filter_groups (faceted)
    ddd_mapping: filters_map_to_value_objects

  - section: list_controls
    contains:
      - sort_dropdown
      - view_toggle (grid/list)

  - section: list_content
    contains:
      - item_cards (collection)
    pagination: load_more_button
    ddd_mapping: each_card_is_agg_job_posting

  - section: footer
    type: global_footer
```

**DDD Integration - Each List Item**:
```yaml
list_item:
  aggregate: agg_job_posting
  aggregate_id: "{{job_id}}"

  displayed_data:
    - job_title (entity attribute)
    - company_name (vo_company)
    - location (vo_location)
    - salary_range (vo_salary)
    - match_score (from bc_matching via context mapping)
    - posted_date (entity attribute)
    - skills_required (vo_skills - top 3)

  actions:
    - save_job (updates candidate.saved_jobs in bc_profile)
    - apply_to_job (navigates to bc_applications workflow)
```

**Responsive Behavior**:
```yaml
breakpoints:
  desktop: (≥1024px)
    layout: sidebar_left + list_right
    filters: always_visible

  tablet: (768-1023px)
    layout: collapsible_drawer + list
    filters: drawer_overlay

  mobile: (≤767px)
    layout: stacked
    filters: bottom_sheet
    list_view: single_column
```

---

### Pattern: My Applications Page

**DDD Mapping**:
```yaml
page_type: list_collection
bounded_context: bc_applications
displays: collection_of_agg_application
data_source: repo_application
query: get_applications_for_candidate(candidate_id)
```

**Page Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ My Applications (12)                                         │
│ [Active] [Interviews] [Offers] [Archive]  ← Status tabs      │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ ⚡ Action Required                                    │   │
│ │ Interview scheduled with TechCorp - Confirm time     │   │
│ │ [Respond →]                                          │   │
│ └──────────────────────────────────────────────────────┘   │
│                                                              │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ Frontend Developer                                    │   │
│ │ TechCorp                                             │   │
│ │ Status: Interview Scheduled                          │   │
│ │ Applied: Oct 1, 2025 • Updated: Oct 3, 2025         │   │
│ │ [View Details →]                                     │   │
│ └──────────────────────────────────────────────────────┘   │
│                                                              │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ React Developer                                       │   │
│ │ StartupCo                                            │   │
│ │ Status: Submitted                                    │   │
│ │ Applied: Sep 28, 2025 • No response yet             │   │
│ │ [View Details →]                                     │   │
│ └──────────────────────────────────────────────────────┘   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

**DDD Integration**:
```yaml
list_item:
  aggregate: agg_application
  aggregate_id: "{{application_id}}"

  displayed_data:
    - job_title (from referenced agg_job_posting)
    - company_name (from job posting)
    - status (aggregate state: SUBMITTED, IN_REVIEW, INTERVIEWING, OFFER, REJECTED)
    - applied_date (aggregate creation timestamp)
    - last_updated (aggregate last modified)

  conditional_display:
    - if status == INTERVIEWING:
        show: action_required_banner
        text: "Interview scheduled - Confirm time"
        action: respond_to_interview_request

  actions:
    - view_details: navigate_to_detail_page
```

**Status-Based Filtering (Tabs)**:
```yaml
tabs:
  - label: "Active"
    filter: status IN [SUBMITTED, IN_REVIEW, INTERVIEWING]
    count: 8

  - label: "Interviews"
    filter: status == INTERVIEWING
    count: 1

  - label: "Offers"
    filter: status == OFFER
    count: 0

  - label: "Archive"
    filter: status IN [REJECTED, WITHDRAWN, ACCEPTED]
    count: 3
```

---

## 2. Detail/Entity Pages

**Purpose**: Display complete information about a single aggregate with related actions.

### Pattern: Job Detail Page

**DDD Mapping**:
```yaml
page_type: detail_entity
bounded_context: bc_job_catalog
displays: single_agg_job_posting
aggregate_id: from_url_param
data_source: repo_job_posting.get_by_id(job_id)

cross_context_data:
  - match_score: from bc_matching
  - skills_gap: from bc_skills_analysis (optional)
  - application_status: from bc_applications (if applied)
```

**Page Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ ← Back to Jobs                                               │
├──────────────────────────────────────────────────────────────┤
│ Frontend Developer                                           │
│ TechCorp • Remote • $100-120k                                │
│                                                              │
│ Your Match: 85% ██████████░░ Excellent                      │
│                                                              │
│ [Apply Now] [Save Job] [Share] [⋮]                          │
├──────────────────────────────────────────────────────────────┤
│ Content Area                                                 │
│ ┌─────────────────────┬──────────────────────────────────┐ │
│ │ Main Content        │ Sidebar                          │ │
│ │                     │                                  │ │
│ │ About the Role      │ ┌──────────────────────────────┐ │ │
│ │ [Full description]  │ │ Company: TechCorp            │ │ │
│ │                     │ │ Location: Remote             │ │ │
│ │ Requirements        │ │ Type: Full-time              │ │ │
│ │ • 3+ years React    │ │ Experience: Mid-level        │ │ │
│ │ • TypeScript exp    │ │ Posted: 2 days ago           │ │ │
│ │ • Node.js backend   │ └──────────────────────────────┘ │ │
│ │                     │                                  │ │
│ │ Skills Required     │ ┌──────────────────────────────┐ │ │
│ │ ✓ React (you have)  │ │ Your Skills Match            │ │ │
│ │ ✓ TypeScript (✓)    │ │ ██████████░░ 85%            │ │ │
│ │ ✗ AWS (missing)     │ │                              │ │ │
│ │                     │ │ You have: 9/10 skills       │ │ │
│ │ [View Skills Gap →] │ │ Missing: AWS                 │ │ │
│ │                     │ │ [Improve Skills →]           │ │ │
│ │ Benefits            │ └──────────────────────────────┘ │ │
│ │ • Health insurance  │                                  │ │
│ │ • 401k match        │ ┌──────────────────────────────┐ │ │
│ │ • Remote work       │ │ Similar Jobs                 │ │ │
│ │                     │ │ • Senior React Dev - BigCo   │ │ │
│ │                     │ │ • Frontend Eng - StartupX    │ │ │
│ │                     │ └──────────────────────────────┘ │ │
│ └─────────────────────┴──────────────────────────────────┘ │
│                                                              │
│ [Apply Now] [Save Job]                                       │
└──────────────────────────────────────────────────────────────┘
```

**Anatomy**:
```yaml
page_sections:
  - section: breadcrumb_nav
    content: "← Back to Jobs"
    action: navigate_back

  - section: page_header
    contains:
      - job_title (h1)
      - company_name (from vo_company)
      - location (vo_location)
      - salary_range (vo_salary)
      - match_score (from bc_matching - cross-context)
    ddd: displays_aggregate_primary_attributes

  - section: primary_actions
    contains:
      - apply_now (primary CTA)
      - save_job (secondary action)
      - share (utility)
      - more_menu (utility)

  - section: main_content
    width: 65%
    contains:
      - job_description (rich text)
      - requirements_list
      - skills_required (with match indicators)
      - benefits_list
    ddd: entity_attributes_and_value_objects

  - section: sidebar
    width: 35%
    contains:
      - job_metadata_card
      - skills_match_widget (from bc_matching)
      - similar_jobs_widget (from bc_job_catalog)
    ddd: cross_context_data_and_related_aggregates

  - section: sticky_footer
    contains:
      - apply_now (repeated CTA)
      - save_job (repeated action)
```

**DDD Integration - Data Sources**:
```yaml
data_composition:
  primary_aggregate: agg_job_posting (bc_job_catalog)
    attributes:
      - job_id
      - job_title
      - company_name (vo_company)
      - location (vo_location)
      - salary_range (vo_salary)
      - job_type (vo_job_type)
      - experience_level (vo_experience_level)
      - description
      - requirements
      - benefits
      - posted_date

  cross_context_data:
    - source: bc_matching
      service: svc_domain_calculate_match
      data: match_score (vo_match_score), skills_overlap
      relationship: customer_supplier

    - source: bc_skills_analysis
      service: svc_app_analyze_skills_gap (triggered on demand)
      data: agg_skills_gap
      relationship: customer_supplier

    - source: bc_applications
      query: check_if_already_applied(candidate_id, job_id)
      data: application_status
      conditional: show_if_applied

  related_data:
    - source: bc_job_catalog
      query: find_similar_jobs(job_id, limit: 5)
      data: collection_of_agg_job_posting
```

**Conditional Rendering**:
```yaml
conditional_ui:
  - condition: already_applied
    show:
      - application_status_banner
      - "You applied on {{date}}"
      - [View Application] button
    hide:
      - [Apply Now] button

  - condition: match_score < 60
    show:
      - low_match_warning
      - "This job may not be a great fit"
      - [View Skills Gap] prominent

  - condition: has_skills_gap
    show:
      - skills_gap_inline_widget
      - missing_skills_list
      - [Improve Skills] action
```

---

### Pattern: Application Detail Page

**DDD Mapping**:
```yaml
page_type: detail_entity
bounded_context: bc_applications
displays: single_agg_application
aggregate_id: from_url_param

cross_context_data:
  - job_details: from bc_job_catalog
  - profile_snapshot: from bc_profile (version at application time)
```

**Page Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ ← Back to Applications                                       │
├──────────────────────────────────────────────────────────────┤
│ Application to TechCorp                                      │
│ Frontend Developer                                           │
│                                                              │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ Status: Interview Scheduled ●                         │   │
│ │ Applied: Oct 1, 2025 • Last updated: Oct 3, 2025     │   │
│ └──────────────────────────────────────────────────────┘   │
├──────────────────────────────────────────────────────────────┤
│ ┌──────────────────────────────────────────────────────┐   │
│ │ ⚡ Action Required                                    │   │
│ │ Interview scheduled for Oct 5, 2025 at 2:00 PM       │   │
│ │ [Confirm] [Request Reschedule]                       │   │
│ └──────────────────────────────────────────────────────┘   │
│                                                              │
│ Timeline                                                     │
│ ○───●───●───●───○                                           │
│ Submit Review Interview Offer Decision                      │
│                                                              │
│ Activity History                                             │
│ • Oct 3: Interview scheduled                                 │
│ • Oct 2: Application viewed by employer                      │
│ • Oct 1: Application submitted                               │
│                                                              │
│ Your Application                                             │
│ ┌───────────────────┬─────────────────────────────────┐    │
│ │ Cover Letter      │ Resume                          │    │
│ │ [View/Edit]       │ john_doe_resume.pdf [Download]  │    │
│ └───────────────────┴─────────────────────────────────┘    │
│                                                              │
│ Job Details                                                  │
│ [View Full Job Posting →]                                    │
│                                                              │
│ [Withdraw Application]                                       │
└──────────────────────────────────────────────────────────────┘
```

**DDD Integration**:
```yaml
aggregate_display: agg_application
  attributes:
    - application_id
    - status (aggregate state enum)
    - submitted_date
    - last_updated
    - cover_letter
    - resume_ref
    - job_ref (aggregate reference by ID)
    - candidate_ref (aggregate reference by ID)

  domain_events_history:
    - evt_application_submitted
    - evt_application_viewed
    - evt_interview_scheduled

  state_machine:
    current_state: INTERVIEWING
    possible_transitions:
      - INTERVIEWING → OFFER (employer extends offer)
      - INTERVIEWING → REJECTED (employer rejects)
      - INTERVIEWING → WITHDRAWN (candidate withdraws)

  actions_available:
    - if status == INTERVIEWING:
        - confirm_interview
        - request_reschedule
        - withdraw_application

    - if status == OFFER:
        - accept_offer
        - decline_offer
        - negotiate_offer
```

---

## 3. Form/Edit Pages

**Purpose**: Create or edit aggregates with validation and submission.

### Pattern: Edit Profile Page

**DDD Mapping**:
```yaml
page_type: form_edit
bounded_context: bc_profile
aggregate: agg_candidate_profile
mode: edit  # vs create
data_source: repo_candidate_profile.get_by_id(candidate_id)
service: svc_app_update_candidate_profile
publishes: evt_profile_updated
```

**Page Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ Edit Profile                          Last saved: 2 min ago  │
├──────────────────────────────────────────────────────────────┤
│ [Overview] [Personal Info] [Skills] [Work History] [Prefs]  │ ← Tabs
├──────────────────────────────────────────────────────────────┤
│                                                              │
│ Skills & Expertise                                           │
│                                                              │
│ Technical Skills                                             │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ React TypeScript Node.js                        [×]  │   │
│ │ Add skill: [___________________] [+ Add]            │   │
│ └──────────────────────────────────────────────────────┘   │
│                                                              │
│ Experience Level                                             │
│ ○ Entry  ● Mid-level  ○ Senior  ○ Lead                     │
│                                                              │
│ Years of Experience                                          │
│ [5_______] years                                             │
│                                                              │
│ Certifications (Optional)                                    │
│ [Add certification_____________________] [+ Add]            │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│ [Cancel]  [Save Draft]                  [Save & Continue →] │
└──────────────────────────────────────────────────────────────┘
```

**Anatomy**:
```yaml
page_sections:
  - section: page_header
    contains:
      - page_title: "Edit Profile"
      - autosave_indicator: "Last saved: {{time}}"

  - section: tab_navigation
    tabs:
      - overview (aggregate summary)
      - personal_info (vo_name, vo_email, vo_location)
      - skills (vo_skills, vo_experience_level)
      - work_history (collection of ent_work_experience)
      - preferences (vo_job_preferences)
    ddd_mapping: tabs_map_to_aggregate_structure

  - section: form_content
    current_tab: skills
    fields:
      - technical_skills (vo_skills - tag input)
      - experience_level (vo_experience_level - radio buttons)
      - years_experience (number input)
      - certifications (optional collection)

  - section: form_actions
    contains:
      - cancel (discard changes, confirm if unsaved)
      - save_draft (save without validation)
      - save_and_continue (validate + save + next tab)
```

**Validation Strategy**:
```yaml
validation:
  client_side:
    - field: technical_skills
      vo_ref: vo_skills
      rules:
        - at_least_one_required
      error_message: "Add at least one technical skill"

    - field: experience_level
      vo_ref: vo_experience_level
      rules:
        - required
      error_message: "Select your experience level"

  server_side:
    - on: save_and_continue
      validates: entire_aggregate
      checks: aggregate_invariants
      service: svc_app_update_candidate_profile

  aggregate_invariants:
    - if years_experience < 2 AND experience_level == SENIOR:
        error: "Senior level requires 5+ years experience"
```

**Auto-Save**:
```yaml
autosave:
  enabled: true
  trigger: after_field_blur
  debounce: 2000ms  # Wait 2s after last change
  endpoint: PATCH /api/profile/draft
  status_indicator: "Last saved: {{time}}"
  on_error: show_error_inline
```

**State Management**:
```yaml
form_state:
  aggregate: agg_candidate_profile
  aggregate_status: DRAFT  # While editing

  dirty: true  # Has unsaved changes
  valid: false  # Current tab invalid

  data:
    skills: ["React", "TypeScript", "Node.js"]
    experience_level: "MID"
    years_experience: 5

  on_navigate_away:
    if dirty:
      - show_confirmation: "You have unsaved changes. Leave anyway?"
      - options: [Save, Discard, Cancel]
```

---

## 4. Dashboard/Overview Pages

**Purpose**: Aggregate information from multiple contexts for at-a-glance insights.

### Pattern: Job Seeker Dashboard

**DDD Mapping**:
```yaml
page_type: dashboard_overview
spans_contexts: true
aggregates_data_from:
  - bc_profile
  - bc_matching
  - bc_applications
  - bc_skills_analysis
  - bc_project_recommendations
```

**Page Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ Welcome back, Marina!                                        │
│ "Your next great opportunity is waiting"                     │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│ ┌────────────────┐ ┌────────────────┐ ┌────────────────┐   │
│ │ Profile        │ │ Job Matches    │ │ Applications   │   │
│ │ 85% Complete   │ │ 12 new         │ │ 3 active       │   │
│ │ [Complete →]   │ │ [View All →]   │ │ [Manage →]     │   │
│ └────────────────┘ └────────────────┘ └────────────────┘   │
│                                                              │
│ Top Job Matches                                              │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ Frontend Developer • TechCorp                         │   │
│ │ Match: 85%  Remote  $100-120k                        │   │
│ │ [View] [Apply]                                       │   │
│ └──────────────────────────────────────────────────────┘   │
│ [View all 12 matches →]                                      │
│                                                              │
│ Skills Insights                                              │
│ Top skills in demand: AWS, Docker, Kubernetes                │
│ You're missing: AWS                                          │
│ [Analyze Skills Gap →]                                       │
│                                                              │
│ Suggested Actions                                            │
│ • Complete work history to improve match quality             │
│ • Respond to interview request from TechCorp                 │
│ • Start "Build REST API" project to learn AWS                │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

**Widget Composition**:
```yaml
widgets:
  - widget: profile_completeness
    source: bc_profile
    query: get_candidate_profile(candidate_id)
    calculates: completeness_percentage
    data:
      - completeness: 85
      - missing_sections: ["work_history"]
    action: navigate_to_profile_edit

  - widget: job_matches_summary
    source: bc_matching
    query: get_unviewed_matches(candidate_id)
    data:
      - new_matches_count: 12
      - top_match_score: 85
    action: navigate_to_matches_page

  - widget: applications_summary
    source: bc_applications
    query: get_active_applications(candidate_id)
    data:
      - active_count: 3
      - pending_actions_count: 1
    action: navigate_to_applications_page

  - widget: top_job_matches
    source: bc_matching
    query: get_top_matches(candidate_id, limit: 3)
    displays: collection_of_agg_job_match
    actions_per_item:
      - view_job_detail
      - apply_to_job

  - widget: skills_insights
    source: bc_skills_analysis
    query: get_skills_gap_summary(candidate_id)
    data:
      - top_demand_skills: ["AWS", "Docker", "Kubernetes"]
      - missing_skills: ["AWS"]
    action: navigate_to_skills_gap_analysis

  - widget: suggested_actions
    source: multiple_contexts
    logic: aggregate_suggestions_from_domain_events
    displays: prioritized_action_list
```

**Cross-Context Data Flow**:
```yaml
dashboard_data_flow:
  - context: bc_profile
    event: evt_profile_updated
    triggers: recalculate_completeness

  - context: bc_matching
    event: evt_high_match_found
    triggers: increment_new_matches_count

  - context: bc_applications
    event: evt_interview_scheduled
    triggers: add_suggested_action("Respond to interview")

  - refresh_strategy:
      - on_page_load: fetch_all_widgets
      - polling: every_30_seconds (new matches, applications)
      - real_time: websocket_for_critical_updates
```

---

## 5. Search/Filter Pages

**Purpose**: Help users find specific items through query and filtering.

### Pattern: Advanced Job Search

**DDD Mapping**:
```yaml
page_type: search_filter
bounded_context: bc_job_catalog
service: svc_app_search_jobs
data_source: repo_job_posting.search(query, filters)
```

**Page Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ Job Search                                                   │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ 🔍 [frontend developer_________________] [Search]   │   │
│ └──────────────────────────────────────────────────────┘   │
├──────────────────────────────────────────────────────────────┤
│ Filters        │ Results (127)                Sort: [Best Match ▼] │
│                │                                             │
│ Location       │ ┌─────────────────────────────────────┐   │
│ ● Remote       │ │ Frontend Developer                   │   │
│ ○ Hybrid       │ │ TechCorp • Remote • $100-120k       │   │
│ ○ On-site      │ │ Match: 85%                          │   │
│ ○ Any          │ │ [Save] [Apply]                      │   │
│                │ └─────────────────────────────────────┘   │
│ Job Type       │ ┌─────────────────────────────────────┐   │
│ ☑ Full-time    │ │ Senior Frontend Engineer             │   │
│ ☐ Part-time    │ │ BigCorp • Hybrid • $130-150k        │   │
│ ☐ Contract     │ │ Match: 72%                          │   │
│                │ │ [Save] [Apply]                      │   │
│ Salary         │ └─────────────────────────────────────┘   │
│ Min: [$80k__]  │                                             │
│ Max: [$150k_]  │ [Load More]                                 │
│                │                                             │
│ Experience     │                                             │
│ ☑ Entry        │                                             │
│ ☑ Mid          │                                             │
│ ☐ Senior       │                                             │
│                │                                             │
│ [Clear All]    │                                             │
│ [Save Search]  │                                             │
└────────────────┴─────────────────────────────────────────────┘
```

**Search State**:
```yaml
search_state:
  query_params:
    keywords: "frontend developer"
    location: "remote"
    job_type: ["full_time"]
    salary_min: 80000
    salary_max: 150000
    experience_level: ["entry", "mid"]

  results:
    total_count: 127
    current_page: 1
    results_per_page: 20
    sort_by: "best_match"  # Options: best_match, newest, salary_high, salary_low

  url_encoding: /jobs/search?q=frontend+developer&loc=remote&type=full_time&exp=entry,mid
```

**DDD Validation**:
```yaml
search_validation:
  - param: location
    vo_ref: vo_location
    valid_values: [remote, hybrid, onsite, any]

  - param: job_type
    vo_ref: vo_job_type
    valid_values: [full_time, part_time, contract, freelance]

  - param: experience_level
    vo_ref: vo_experience_level
    valid_values: [entry, mid, senior, lead]

  - param: salary_min, salary_max
    vo_ref: vo_salary
    validation: min <= max
```

**Save Search Feature**:
```yaml
save_search:
  action: save_current_search
  service: svc_app_save_search_criteria
  stores_in: bc_profile
  entity_attribute: candidate.saved_searches

  saved_search_data:
    name: "Remote Frontend Jobs"
    query_params: {all current params}
    notifications:
      - email_on_new_results: true
      - frequency: daily

  publishes: evt_search_saved
```

---

## 6. Empty State Pages

**Purpose**: Guide users when no data exists yet.

### Pattern: No Applications Yet

**Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│ My Applications                                              │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│                      [Illustration]                          │
│                                                              │
│             You haven't applied to any jobs yet              │
│                                                              │
│  Find your perfect match and start applying to build         │
│  your career.                                                │
│                                                              │
│                    [Browse Jobs →]                           │
│                    [View My Matches →]                       │
│                                                              │
│                                                              │
│  Tips for successful applications:                           │
│  • Complete your profile for better matches                  │
│  • Tailor your cover letter to each job                      │
│  • Apply within 3 days of job posting                        │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

**DDD Context**:
```yaml
empty_state: no_applications
  bounded_context: bc_applications
  condition: repo_application.count(candidate_id) == 0

  content:
    - illustration: "empty_applications.svg"
    - heading: "You haven't applied to any jobs yet"
    - description: "Find your perfect match and start applying"
    - primary_action:
        label: "Browse Jobs"
        navigates_to: bc_job_catalog
    - secondary_action:
        label: "View My Matches"
        navigates_to: bc_matching

  helpful_content:
    - tips_list
    - help_link
```

---

## 7. Error Pages

**Purpose**: Handle error states gracefully.

### Pattern: 404 Not Found

**Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│                         404                                  │
│                   Page Not Found                             │
│                                                              │
│  The page you're looking for doesn't exist or has been moved.│
│                                                              │
│                    [Go to Dashboard]                         │
│                    [Browse Jobs]                             │
│                    [View Applications]                       │
│                                                              │
│  Need help? [Contact Support →]                              │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Pattern: 403 Forbidden (Aggregate Access)

**DDD Context**:
```yaml
error: aggregate_access_denied
  http_status: 403
  bounded_context: bc_applications
  aggregate: agg_application
  aggregate_id: "12345"
  reason: application_belongs_to_different_candidate

  message: "You don't have permission to view this application."
  actions:
    - go_to_my_applications
    - contact_support
```

### Pattern: 500 Server Error

**Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ Header (Global Nav)                                          │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│                   Something Went Wrong                       │
│                                                              │
│  We encountered an error. Your data is safe, and we've been  │
│  notified. Please try again in a few moments.                │
│                                                              │
│                    [Try Again]                               │
│                    [Go to Dashboard]                         │
│                                                              │
│  Error ID: ERR-2025-10-04-12345  ← For support reference    │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## Page Architecture Principles

### 1. Single Responsibility

Each page should have one primary purpose aligned with a DDD use case:
- List page: Browse aggregates
- Detail page: View single aggregate
- Form page: Create/edit aggregate
- Dashboard: Overview of multiple contexts

### 2. Aggregate-Centric Design

Pages organized around aggregates:
```yaml
page_focus: agg_job_posting
  - list_page: displays collection
  - detail_page: displays single instance
  - no_edit_page: job postings are read-only for candidates
```

### 3. Context Awareness

Make bounded context transitions visible:
```yaml
page_breadcrumb:
  - Home
  - Jobs (bc_job_catalog) ← context indicator
  - Frontend Developer

cross_context_link:
  - from: Job Detail (bc_job_catalog)
  - to: Skills Gap (bc_skills_analysis)
  - indicator: breadcrumb, visual change
```

### 4. Responsive Layout Strategy

```yaml
layout_approach: mobile_first

breakpoints:
  mobile: (≤767px)
    - single_column
    - collapsible_filters
    - stacked_cards

  tablet: (768-1023px)
    - two_column (60/40 split)
    - drawer_filters
    - grid_or_list_toggle

  desktop: (≥1024px)
    - multi_column (sidebar + main + detail)
    - persistent_filters
    - grid_view_default
```

### 5. Performance

```yaml
optimization:
  - lazy_load_content: below_fold
  - paginate_lists: 20_items_per_page
  - cache_static_content: job_descriptions
  - prefetch: likely_next_page
  - virtual_scrolling: for_very_long_lists
```

### 6. Accessibility

```yaml
wcag_compliance:
  - semantic_html: <main>, <article>, <aside>
  - headings_hierarchy: h1 → h2 → h3
  - landmark_regions: role="main", role="navigation"
  - skip_links: skip_to_main_content
  - keyboard_navigation: all_interactive_elements
  - focus_indicators: visible_on_all_elements
```

---

## Key Takeaways

1. **Pages Map to Aggregates**: List pages show aggregate collections, detail pages show single aggregates, form pages create/edit aggregates.

2. **Bounded Context Alignment**: Pages belong to specific bounded contexts, with clear indicators when crossing context boundaries.

3. **Consistent Patterns**: Use established patterns (list, detail, form, dashboard, search, empty, error) for predictability.

4. **Cross-Context Composition**: Dashboard and detail pages can aggregate data from multiple contexts via context mapping relationships.

5. **State Reflects Domain**: Page state (loading, editing, submitted) mirrors aggregate state and lifecycle.

6. **Responsive by Design**: Mobile-first approach with progressive enhancement for larger screens.

7. **Performance Matters**: Lazy loading, pagination, caching aligned with aggregate boundaries.

---

## References

**Primary Sources**:
- Tidwell, Jenifer et al. (2020). "Designing Interfaces" - Page patterns
- Material Design - Layout and structure guidelines
- Apple HIG - Page architecture patterns
- Nielsen Norman Group - Page layout research

**DDD Integration**:
- `research/ddd/deliverables/ddd-schema-example.yaml` - aggregates and bounded contexts
- `research/ddd/working-docs/03-tactical-patterns.md` - aggregate design, repositories
- `research/ux/UX-DDD-INTEGRATION.md` - page-to-aggregate mapping
- `research/ux/working-docs/01-ia-foundations.md` - page organization
- `research/ux/working-docs/02-navigation-patterns.md` - cross-page navigation
- `research/ux/working-docs/03-workflow-patterns.md` - multi-page workflows

---

*Document created: 2025-10-04*
*Part of UX Research Phase 5: Page Architecture*
