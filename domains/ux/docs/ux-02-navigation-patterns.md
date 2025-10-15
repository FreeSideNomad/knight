# Navigation Patterns

## Overview

Navigation patterns define how users move through an application. Effective navigation is intuitive, consistent, and supports user mental models while respecting system architecture boundaries (DDD bounded contexts).

**Integration with DDD**: Navigation patterns map to bounded context boundaries and relationships. Primary navigation reflects core vs supporting domains, and cross-context navigation respects context mapping patterns (customer/supplier, partnership, etc.).

---

## Navigation Pattern Categories

### 1. Global Navigation Patterns
### 2. Local Navigation Patterns
### 3. Utility Navigation Patterns
### 4. Contextual Navigation Patterns
### 5. Mobile Navigation Patterns
### 6. Cross-Context Navigation Patterns

---

## 1. Global Navigation Patterns

Global navigation provides access to primary application sections throughout the user experience.

### Pattern: Top Horizontal Navigation Bar

**Description**: Persistent navigation across the top of the viewport.

**When to Use**:
- Desktop/tablet applications
- 3-7 primary sections
- Horizontal space available

**Structure**:
```
┌──────────────────────────────────────────────────────────────┐
│ [Logo]  Profile  Jobs  Applications  Career  [Search] [User▼]│
└──────────────────────────────────────────────────────────────┘
```

**DDD Mapping**:
```yaml
nav_items:
  - label: "Profile"
    bounded_context: bc_profile
    domain: dom_job_matching (core)

  - label: "Jobs"
    bounded_contexts: [bc_job_catalog, bc_matching]
    domain: dom_job_matching (core)
    note: "Combined related contexts with customer/supplier relationship"

  - label: "Applications"
    bounded_context: bc_applications
    domain: dom_application_tracking (supporting)

  - label: "Career"
    bounded_contexts: [bc_skills_analysis, bc_project_recommendations]
    domain: dom_career_development (supporting)
```

**Characteristics**:
- Always visible (persistent)
- Highlights current section
- Desktop-optimized
- Left-to-right importance order

**Example Implementation**:
```html
<header role="banner">
  <nav aria-label="Main navigation">
    <a href="/" aria-label="Home">
      <img src="/logo.svg" alt="Job Seeker" />
    </a>
    <ul>
      <li><a href="/profile" aria-current="page">Profile</a></li>
      <li><a href="/jobs">Jobs</a></li>
      <li><a href="/applications">Applications</a></li>
      <li><a href="/career">Career</a></li>
    </ul>
    <div class="utility-nav">
      <button aria-label="Search" aria-expanded="false">🔍</button>
      <button aria-label="User menu" aria-expanded="false">👤</button>
    </div>
  </nav>
</header>
```

**Visual Indicators**:
- Current section: Bold, underline, or background highlight
- Hover state: Subtle background change
- Badge notifications: "Applications (3)" for pending items

---

### Pattern: Side Navigation (Drawer)

**Description**: Vertical navigation along the left or right edge.

**When to Use**:
- Complex applications with many sections
- Desktop applications with wide viewports
- Deep hierarchies within sections

**Structure**:
```
┌──────────┬─────────────────────────────────┐
│ [Logo]   │ Main Content Area               │
│          │                                 │
│ Profile  │                                 │
│ Jobs     │                                 │
│ Applicat.│                                 │
│ Career   │                                 │
│          │                                 │
│ ─────    │                                 │
│ Settings │                                 │
│ Help     │                                 │
└──────────┴─────────────────────────────────┘
```

**DDD Mapping**:
```yaml
primary_contexts:  # Core domain first
  - bc_profile
  - bc_matching
  - bc_applications

supporting_contexts:  # Supporting domains
  - bc_skills_analysis
  - bc_project_recommendations

generic_contexts:  # Generic/utility
  - bc_notifications (if user-facing)
```

**Variants**:

1. **Always Visible** (Desktop):
   - Width: 200-280px
   - Collapsible to icon-only mode

2. **Collapsible** (Desktop/Tablet):
   - Expands on hover or click
   - Icons visible when collapsed

3. **Overlay** (Mobile):
   - Slides in from edge
   - Overlays content
   - Dismissible

**Example with Nested Navigation**:
```html
<aside aria-label="Main navigation">
  <nav>
    <ul>
      <li>
        <a href="/profile">
          <span class="icon">👤</span>
          Profile
        </a>
      </li>
      <li>
        <button aria-expanded="false" aria-controls="jobs-submenu">
          <span class="icon">💼</span>
          Jobs
          <span class="chevron">▼</span>
        </button>
        <ul id="jobs-submenu" hidden>
          <li><a href="/jobs/browse">Browse All</a></li>
          <li><a href="/jobs/matches">My Matches</a></li>
          <li><a href="/jobs/saved">Saved Jobs</a></li>
        </ul>
      </li>
      <!-- More items -->
    </ul>
  </nav>
</aside>
```

---

### Pattern: Tab Navigation

**Description**: Horizontal tabs for switching between views.

**When to Use**:
- Related content sections within a context
- Peer-level navigation (not hierarchical)
- 2-6 tabs

**Structure**:
```
┌─────────────────────────────────────────────┐
│ Overview | Skills | Work History | Settings │
├─────────────────────────────────────────────┤
│                                             │
│  Content for selected tab                   │
│                                             │
└─────────────────────────────────────────────┘
```

**DDD Mapping**:
```yaml
context: bc_profile
tabs:
  - label: "Overview"
    displays: aggregate_summary  # Summary of agg_candidate_profile

  - label: "Skills"
    displays: entity_attributes  # vo_skills, vo_experience_level

  - label: "Work History"
    displays: entity_collection  # Collection of ent_work_experience

  - label: "Settings"
    displays: preferences  # vo_job_preferences, notification settings
```

**Characteristics**:
- Selected tab highlighted
- Content updates without page reload (SPA pattern)
- Keyboard navigation (arrow keys)
- ARIA roles: `role="tablist"`, `role="tab"`, `role="tabpanel"`

**Example Implementation**:
```html
<div class="tab-container">
  <div role="tablist" aria-label="Profile sections">
    <button role="tab" aria-selected="true" aria-controls="overview-panel" id="overview-tab">
      Overview
    </button>
    <button role="tab" aria-selected="false" aria-controls="skills-panel" id="skills-tab">
      Skills
    </button>
    <button role="tab" aria-selected="false" aria-controls="history-panel" id="history-tab">
      Work History
    </button>
  </div>

  <div role="tabpanel" id="overview-panel" aria-labelledby="overview-tab">
    <!-- Overview content -->
  </div>
  <div role="tabpanel" id="skills-panel" aria-labelledby="skills-tab" hidden>
    <!-- Skills content -->
  </div>
  <div role="tabpanel" id="history-panel" aria-labelledby="history-tab" hidden>
    <!-- History content -->
  </div>
</div>
```

---

## 2. Local Navigation Patterns

Local navigation helps users move within a specific bounded context.

### Pattern: Sub-Navigation Menu

**Description**: Secondary navigation within a primary section.

**When to Use**:
- Complex bounded context with multiple views
- Hierarchical content structure
- Context-specific actions

**Structure**:
```
Global Nav: Profile | Jobs | Applications | Career
              ↓
Local Nav (Jobs context):
┌─────────────────────────────────────┐
│ Browse All | My Matches | Saved     │
└─────────────────────────────────────┘
```

**DDD Mapping**:
```yaml
bounded_context: bc_job_catalog, bc_matching
local_nav:
  - label: "Browse All"
    view: job_listing
    data_source: bc_job_catalog
    service: svc_app_browse_jobs

  - label: "My Matches"
    view: filtered_job_listing
    data_source: bc_matching
    service: svc_app_calculate_matches
    aggregate: agg_job_match

  - label: "Saved"
    view: user_saved_jobs
    data_source: bc_job_catalog (filtered by user)
    entity_attribute: candidate.saved_jobs
```

**Visual Placement**:
- Below global navigation
- Above main content
- Sticky on scroll (optional)

---

### Pattern: Breadcrumbs

**Description**: Hierarchical trail showing user's location.

**When to Use**:
- Deep hierarchies (3+ levels)
- Users need to backtrack easily
- Complex navigation structures

**Structure**:
```
Home > Jobs > Search Results > Frontend Developer at TechCorp
```

**DDD Mapping**:
```yaml
breadcrumb_trail:
  - label: "Home"
    url: "/"
    level: root

  - label: "Jobs"
    url: "/jobs"
    bounded_context: bc_job_catalog
    level: context

  - label: "Search Results"
    url: "/jobs/search?q=frontend"
    view: search_results
    level: view

  - label: "Frontend Developer at TechCorp"
    url: "/jobs/12345"
    aggregate: agg_job_posting
    aggregate_id: "12345"
    level: detail
```

**Characteristics**:
- Each segment is clickable (except current)
- Separator: > or / or →
- Truncate in mobile (show first and last)
- Semantic: `<nav aria-label="Breadcrumb">`

**Example Implementation**:
```html
<nav aria-label="Breadcrumb">
  <ol>
    <li><a href="/">Home</a></li>
    <li><a href="/jobs">Jobs</a></li>
    <li><a href="/jobs/search?q=frontend">Search Results</a></li>
    <li aria-current="page">Frontend Developer at TechCorp</li>
  </ol>
</nav>
```

**DDD Cross-Context Example**:
```
Home > Profile > Skills > Skills Gap Analysis

Crosses contexts:
- "Profile" → bc_profile
- "Skills Gap Analysis" → bc_skills_analysis

Context mapping: customer/supplier relationship allows cross-context navigation
```

---

### Pattern: Pagination

**Description**: Navigate through large result sets.

**When to Use**:
- Large collections (100+ items)
- Performance constraints
- Linear browsing pattern

**Types**:

1. **Numbered Pagination**:
```
← Previous  1  2  3  4  5  ...  42  Next →
```

2. **Load More**:
```
[Showing 20 of 327 jobs]
       [Load More]
```

3. **Infinite Scroll**:
```
[Jobs list]
[Jobs list]
[Jobs list]
← Automatically loads more as user scrolls
```

**DDD Mapping**:
```yaml
context: bc_job_catalog
pagination:
  page_size: 20
  total_items: calculated_by_repository
  repository: repo_job_posting
  query: svc_app_browse_jobs

  # Pagination metadata
  current_page: 1
  total_pages: 17
  has_previous: false
  has_next: true
```

**Example Implementation (Numbered)**:
```html
<nav aria-label="Pagination">
  <ul>
    <li><a href="?page=2" rel="prev">← Previous</a></li>
    <li><a href="?page=1">1</a></li>
    <li><a href="?page=2">2</a></li>
    <li><span aria-current="page">3</span></li>
    <li><a href="?page=4">4</a></li>
    <li><a href="?page=5">5</a></li>
    <li><span>...</span></li>
    <li><a href="?page=17">17</a></li>
    <li><a href="?page=4" rel="next">Next →</a></li>
  </ul>
</nav>
```

---

## 3. Utility Navigation Patterns

Utility navigation provides access to user account, settings, help, and auxiliary functions.

### Pattern: User Menu (Dropdown)

**Description**: Account-related actions accessed via user avatar/name.

**Structure**:
```
┌──────────────────────┐
│ 👤 Marina ▼          │ ← Click to expand
└──────────────────────┘
        ↓
┌──────────────────────┐
│ View Profile         │ → bc_profile
│ Account Settings     │ → bc_profile (settings view)
│ Notifications        │ → bc_notifications
│ ─────────────────    │
│ Help & Support       │
│ Sign Out             │
└──────────────────────┘
```

**DDD Mapping**:
```yaml
user_menu:
  - label: "View Profile"
    bounded_context: bc_profile
    url: "/profile"

  - label: "Account Settings"
    bounded_context: bc_profile
    url: "/profile/settings"
    aggregate: agg_candidate_profile

  - label: "Notifications"
    bounded_context: bc_notifications
    url: "/notifications"

  - label: "Sign Out"
    action: logout
    service: authentication_service  # Infrastructure
```

**Characteristics**:
- Accessible from anywhere (global utility)
- Keyboard accessible (Escape to close)
- Click outside to close
- ARIA: `aria-haspopup="true"`, `aria-expanded`

---

### Pattern: Contextual Action Menu

**Description**: Actions specific to current context or item.

**Structure**:
```
Job Posting Card:
┌──────────────────────────────────┐
│ Frontend Developer               │
│ TechCorp • Remote • $120k        │
│ Match: 85%                    ⋮  │ ← Click for actions
└──────────────────────────────────┘
                                  ↓
                        ┌──────────────────┐
                        │ Save Job         │
                        │ Share            │
                        │ Report           │
                        │ Hide Employer    │
                        └──────────────────┘
```

**DDD Mapping**:
```yaml
context: bc_job_catalog
aggregate: agg_job_posting
aggregate_id: "12345"

actions:
  - label: "Save Job"
    service: svc_app_save_job
    domain_event: evt_job_saved  # Publishes event

  - label: "Share"
    action: share_job
    # Generic action, not domain-specific

  - label: "Report"
    action: report_job
    # Might publish evt_job_reported

  - label: "Hide Employer"
    service: svc_app_hide_employer
    updates: candidate.hidden_employers  # Updates profile aggregate
    cross_context: true  # bc_job_catalog → bc_profile
```

---

## 4. Contextual Navigation Patterns

Contextual navigation emerges from content relationships.

### Pattern: Inline Links

**Description**: Links embedded within text content.

**Example**:
```
Your profile is 80% complete. Add work history to improve match accuracy.
                              ^^^^^^^^^^^^        ^^^^^^^^^^^^^^^
                              DDD: bc_profile    DDD: bc_matching
```

**DDD Mapping**:
```yaml
inline_links:
  - text: "work history"
    bounded_context: bc_profile
    url: "/profile/work-history"
    entity: ent_work_experience

  - text: "match accuracy"
    bounded_context: bc_matching
    url: "/help/matching"
    # Educational content about matching algorithm
```

---

### Pattern: Related Items

**Description**: Links to related content based on domain relationships.

**Example - Job Detail Page**:
```
┌─────────────────────────────────────────┐
│ Frontend Developer at TechCorp          │
│ [Job details...]                        │
│                                         │
│ Related:                                │
│ • 12 similar jobs (same skills)         │ → bc_matching
│ • 5 other jobs from TechCorp            │ → bc_job_catalog
│ • Skills gap for this job               │ → bc_skills_analysis
└─────────────────────────────────────────┘
```

**DDD Mapping**:
```yaml
current_page:
  bounded_context: bc_job_catalog
  aggregate: agg_job_posting
  aggregate_id: "12345"

related_items:
  - label: "Similar jobs"
    bounded_context: bc_matching
    relationship: "same_skills"
    service: svc_domain_calculate_match
    query: "jobs with overlapping vo_skills"

  - label: "Other jobs from TechCorp"
    bounded_context: bc_job_catalog
    relationship: "same_employer"
    query: "jobs where company_id = current.company_id"

  - label: "Skills gap for this job"
    bounded_context: bc_skills_analysis
    relationship: "requirements_analysis"
    aggregate: agg_skills_gap
    cross_context: true
    context_mapping: customer_supplier  # bc_matching → bc_skills_analysis
```

---

### Pattern: Suggested Actions

**Description**: Next steps based on user state and domain logic.

**Example - Dashboard**:
```
┌──────────────────────────────────────────┐
│ Welcome back, Marina!                    │
│                                          │
│ Suggested Actions:                       │
│ • Complete your profile (80% done)       │ → bc_profile
│ • View 12 new job matches                │ → bc_matching
│ • Respond to interview request           │ → bc_applications
│ • Start recommended project              │ → bc_project_recommendations
└──────────────────────────────────────────┘
```

**DDD Mapping**:
```yaml
suggestions:
  - condition: "profile_completeness < 100"
    label: "Complete your profile ({{completeness}}% done)"
    bounded_context: bc_profile
    url: "/profile/edit"
    aggregate: agg_candidate_profile

  - condition: "unviewed_matches_count > 0"
    label: "View {{count}} new job matches"
    bounded_context: bc_matching
    url: "/jobs/matches?filter=unviewed"
    domain_event_trigger: evt_high_match_found  # What caused this suggestion

  - condition: "pending_interview_response"
    label: "Respond to interview request"
    bounded_context: bc_applications
    url: "/applications/{{application_id}}"
    aggregate: agg_application
    priority: high
```

---

## 5. Mobile Navigation Patterns

Mobile requires specialized navigation due to screen size constraints.

### Pattern: Bottom Tab Bar

**Description**: Fixed navigation at bottom of screen.

**When to Use**:
- Mobile apps (iOS/Android pattern)
- 3-5 primary sections
- Thumb-friendly navigation

**Structure**:
```
┌─────────────────────────────────┐
│                                 │
│   Main Content Area             │
│                                 │
│                                 │
├─────────────────────────────────┤
│  👤      💼      📄      📊     │
│ Profile  Jobs  Apps  Career     │
└─────────────────────────────────┘
```

**DDD Mapping**: Same as global navigation, prioritized for mobile:
```yaml
bottom_tabs:
  - label: "Profile"
    icon: "user"
    bounded_context: bc_profile

  - label: "Jobs"
    icon: "briefcase"
    bounded_contexts: [bc_job_catalog, bc_matching]
    badge: new_matches_count

  - label: "Apps"
    icon: "document"
    bounded_context: bc_applications
    badge: pending_actions_count

  - label: "Career"
    icon: "chart"
    bounded_contexts: [bc_skills_analysis, bc_project_recommendations]
```

**Characteristics**:
- Always visible
- Icons + labels
- Selected state clearly indicated
- Safe area insets on iOS

---

### Pattern: Hamburger Menu

**Description**: Collapsible menu accessed via ≡ icon.

**When to Use**:
- Mobile/tablet
- Many navigation options
- Secondary navigation on desktop

**Structure**:
```
┌───┐  ← Click hamburger
│ ≡ │
└───┘
  ↓
┌──────────────────┐
│ [Close ✕]        │
│                  │
│ Profile          │
│ Jobs             │
│ Applications     │
│ Career           │
│ ─────            │
│ Settings         │
│ Help             │
│ Sign Out         │
└──────────────────┘
```

**Behavior**:
- Slides in from left (common) or right
- Overlays content
- Dismissible (close button, swipe, click outside)
- Animates smoothly

---

### Pattern: Swipe Gestures

**Description**: Navigate via swipe actions.

**Types**:

1. **Horizontal Swipe (Tab Navigation)**:
```
← Swipe left/right between tabs
Overview ↔ Skills ↔ Work History
```

2. **Vertical Swipe (Scroll)**:
```
↕ Swipe up/down to scroll content
```

3. **Swipe Actions on Items**:
```
Job Card:
← Swipe left to save/hide
→ Swipe right to apply quickly
```

**DDD Mapping (Swipe Actions)**:
```yaml
swipe_actions:
  left_swipe:
    - label: "Save"
      service: svc_app_save_job
      icon: "bookmark"
    - label: "Hide"
      service: svc_app_hide_job
      icon: "eye-off"

  right_swipe:
    - label: "Quick Apply"
      service: svc_app_submit_application
      icon: "send"
      requires_profile_complete: true
```

---

## 6. Cross-Context Navigation Patterns

Navigation across DDD bounded context boundaries requires special consideration.

### Pattern: Context Transition with Visual Indicator

**Description**: Clear indication when user crosses bounded context.

**Example**:
```
Profile page (bc_profile):
┌──────────────────────────────────────┐
│ Your Skills: React, TypeScript       │
│                                      │
│ Want to improve your skills?         │
│ [View Skills Gap Analysis] ────────┐ │
└─────────────────────────────────────┼─┘
                                      ↓
Skills Gap Analysis page (bc_skills_analysis):
┌─────────────────────────────────────┐
│ ← Back to Profile                   │ ← Breadcrumb shows context switch
│                                     │
│ Skills Gap Analysis                 │
│ ...                                 │
└─────────────────────────────────────┘
```

**DDD Mapping**:
```yaml
transition:
  from_context: bc_profile
  to_context: bc_skills_analysis
  context_mapping: customer_supplier

  navigation_cue:
    breadcrumb: "Profile > Skills Gap Analysis"
    back_button: "← Back to Profile"
    visual_indicator: color/icon change (optional)
```

---

### Pattern: Embedded Context View

**Description**: Display content from another context without full navigation.

**Example - Modal/Drawer**:
```
Job Detail (bc_job_catalog):
┌─────────────────────────────────────────┐
│ Frontend Developer at TechCorp          │
│ ...                                     │
│ [View Skills Gap for This Job] ←────────┼─ Opens modal
└─────────────────────────────────────────┘
                                          ↓
┌─────────────────────────────────────────┐
│ ┌─────────────────────────────────────┐ │
│ │ Skills Gap Analysis         [Close] │ │ ← Modal from bc_skills_analysis
│ │                                     │ │
│ │ Required: React, TypeScript, AWS    │ │
│ │ You have: React, TypeScript         │ │
│ │ Missing: AWS                        │ │
│ │                                     │ │
│ │ [View Full Analysis]                │ │ ← Link to full context
│ └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

**DDD Mapping**:
```yaml
embedded_view:
  host_context: bc_job_catalog
  embedded_context: bc_skills_analysis
  display_mode: modal

  data:
    aggregate: agg_skills_gap
    query_params:
      job_id: current_job.id
      candidate_id: current_user.id

  actions:
    - label: "View Full Analysis"
      url: "/career/skills-gap?job={{job_id}}"
      navigates_to: bc_skills_analysis (full page)
```

**When to Use**:
- Preview data from another context
- Avoid disrupting user flow
- Related but secondary information

---

### Pattern: Workflow Across Contexts

**Description**: Multi-step process spanning multiple bounded contexts.

**Example - Apply to Job**:
```
Step 1: Job Detail (bc_job_catalog)
  ↓ [Apply Now]
Step 2: Review Profile (bc_profile)
  ↓ [Continue]
Step 3: Application Form (bc_applications)
  ↓ [Submit]
Step 4: Confirmation (bc_applications)
```

**DDD Mapping**:
```yaml
workflow: submit_application
steps:
  - step: 1
    label: "Review Job"
    bounded_context: bc_job_catalog
    aggregate: agg_job_posting

  - step: 2
    label: "Review Profile"
    bounded_context: bc_profile
    aggregate: agg_candidate_profile
    context_transition: true

  - step: 3
    label: "Submit Application"
    bounded_context: bc_applications
    service: svc_app_submit_application
    publishes_event: evt_application_submitted
    context_transition: true

  - step: 4
    label: "Confirmation"
    bounded_context: bc_applications
    aggregate: agg_application

context_mappings:
  - bc_job_catalog → bc_profile: reads candidate data
  - bc_profile → bc_applications: provides candidate info
  - bc_job_catalog → bc_applications: provides job info
```

**Navigation Indicators**:
```
Progress Bar:
[●───────────────○───────────○───────────○]
Review Job    Profile    Submit    Done

Context labels:
Step 1/4: Review Job (Jobs)
Step 2/4: Review Profile (Profile)
Step 3/4: Submit Application (Applications)
```

---

## Navigation Best Practices

### 1. Consistency

**Do**:
- Same navigation structure across pages
- Consistent terminology (use DDD ubiquitous language)
- Predictable placement

**Don't**:
- Rearrange navigation items
- Use different terms for same concept
- Hide navigation unexpectedly

---

### 2. Clarity

**Do**:
- Clear labels (domain terms)
- Visual indicators for current location
- Breadcrumbs for deep hierarchies

**Don't**:
- Vague labels ("Stuff", "Things")
- Mystery meat navigation (icons without labels)
- Ambiguous current state

---

### 3. Accessibility

**Do**:
- Semantic HTML (`<nav>`, `<ul>`, `<a>`)
- ARIA landmarks and labels
- Keyboard navigation
- Focus management

**Don't**:
- `<div>` or `<span>` for navigation without ARIA
- Keyboard traps
- Invisible focus indicators

---

### 4. Performance

**Do**:
- Lazy load sub-navigation
- Preload likely next pages
- Cache navigation state

**Don't**:
- Load entire site map upfront
- Re-render navigation unnecessarily
- Block page load with navigation

---

### 5. DDD Alignment

**Do**:
- Map primary navigation to bounded contexts
- Use domain terminology for labels
- Respect context boundaries
- Indicate cross-context transitions

**Don't**:
- Mix implementation details in navigation labels
- Force unrelated contexts into single navigation item
- Hide domain structure from users when it aids understanding

---

## Navigation State Management

### Pattern: Persistent Navigation State

**Description**: Remember user's navigation preferences and state.

**Examples**:
- Last viewed section
- Expanded/collapsed sidebar
- Tab selection within section

**DDD Mapping**:
```yaml
navigation_state:
  user_id: "12345"
  preferences:
    sidebar_collapsed: false
    last_visited_context: bc_matching
    last_visited_url: "/jobs/matches"

  stored_in: bc_profile
  entity: ent_candidate
  attribute: user_preferences  # Value object
```

---

### Pattern: Breadcrumb Trail State

**Description**: Track user's path for "back" navigation.

**Example**:
```
User path:
Home → Jobs → Search → Job Detail → Skills Gap Analysis

Back button should return to: Job Detail (not generic "Jobs")
```

**Implementation**:
```yaml
navigation_stack:
  - url: "/"
    label: "Home"
    context: root

  - url: "/jobs"
    label: "Jobs"
    context: bc_job_catalog

  - url: "/jobs/search?q=react"
    label: "Search Results"
    context: bc_job_catalog

  - url: "/jobs/12345"
    label: "Frontend Developer"
    context: bc_job_catalog
    aggregate: agg_job_posting
    aggregate_id: "12345"

  - url: "/career/skills-gap?job=12345"
    label: "Skills Gap"
    context: bc_skills_analysis  # Current page
```

---

## Responsive Navigation Strategy

### Desktop (≥1024px)

```yaml
primary_nav: top_horizontal_bar
secondary_nav: sub_nav_below_primary
utility_nav: user_menu_top_right
local_nav: sidebar_or_tabs
```

### Tablet (768px - 1023px)

```yaml
primary_nav: top_bar_with_some_items_in_more_menu
secondary_nav: dropdown_or_tabs
utility_nav: user_menu_top_right
local_nav: tabs
```

### Mobile (≤767px)

```yaml
primary_nav: bottom_tab_bar_or_hamburger
secondary_nav: contextual_dropdown
utility_nav: hamburger_menu
local_nav: horizontal_scroll_tabs
```

---

## Navigation Analytics

Track navigation to improve UX:

```yaml
analytics_events:
  - event: "navigation_item_clicked"
    properties:
      label: "Jobs"
      bounded_context: "bc_job_catalog"
      navigation_type: "global"

  - event: "cross_context_navigation"
    properties:
      from_context: "bc_profile"
      to_context: "bc_skills_analysis"
      context_mapping: "customer_supplier"

  - event: "navigation_search_used"
    properties:
      query: "react jobs"
      results_count: 42
      bounded_context: "bc_job_catalog"
```

---

## Key Takeaways

1. **Navigation Reflects Architecture**: Primary navigation should map to DDD bounded contexts, making domain structure visible and understandable.

2. **Consistency is Critical**: Use DDD ubiquitous language in all navigation labels for consistency across team and codebase.

3. **Context Transitions Matter**: Make cross-context navigation clear to users via breadcrumbs, back buttons, or visual indicators.

4. **Accessibility is Non-Negotiable**: Semantic HTML, ARIA, and keyboard support are requirements, not nice-to-haves.

5. **Mobile Needs Adaptation**: Bottom tabs, hamburger menus, and swipe gestures accommodate mobile constraints.

6. **Progressive Disclosure**: Start with primary contexts (global nav), reveal details within context (local nav), and provide shortcuts (contextual nav).

7. **Analytics Inform Improvements**: Track navigation patterns to identify confusion points and optimize structure.

---

## References

**Primary Sources**:
- Nielsen Norman Group - Navigation research and best practices
- Apple Human Interface Guidelines - Navigation patterns
- Material Design - Navigation components
- Inclusive Components - Accessible navigation

**DDD Integration**:
- `research/ddd/deliverables/ddd-schema-example.yaml` - bounded contexts for navigation structure
- `research/ddd/working-docs/02-strategic-patterns.md` - context mapping patterns
- `research/ux/UX-DDD-INTEGRATION.md` - integration guidelines
- `research/ux/working-docs/01-ia-foundations.md` - IA structure

---

*Document created: 2025-10-04*
*Part of UX Research Phase 3: Navigation Patterns*
