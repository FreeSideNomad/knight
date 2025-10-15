# UX Ontological Taxonomy

## Overview

This document provides a complete hierarchy and classification of all UX patterns researched, their relationships, dependencies, and decision frameworks for when to apply each pattern. It serves as a quick reference and pattern selection guide.

**Integration with DDD**: UX patterns map to DDD patterns at every level—system organization aligns with bounded contexts, pages map to aggregates, components enforce value object rules, and behaviors respond to domain events.

---

## Complete Pattern Hierarchy

```
UX System
│
├── Information Architecture (System Level)
│   ├── Organization Systems
│   │   ├── Hierarchical
│   │   ├── Sequential
│   │   ├── Faceted
│   │   └── Matrix/Database
│   ├── Labeling Systems
│   │   ├── Contextual Links
│   │   ├── Headings
│   │   ├── Navigation Labels
│   │   └── Index Terms/Tags
│   ├── Navigation Systems
│   │   ├── Global Navigation
│   │   ├── Local Navigation
│   │   ├── Contextual Navigation
│   │   └── Utility Navigation
│   └── Search Systems
│       ├── Simple Search
│       ├── Advanced Search
│       └── Faceted Search
│
├── Navigation Patterns
│   ├── Global Navigation
│   │   ├── Top Horizontal Bar
│   │   ├── Side Navigation (Drawer)
│   │   └── Tab Navigation
│   ├── Local Navigation
│   │   ├── Sub-Navigation Menu
│   │   ├── Breadcrumbs
│   │   └── Pagination
│   ├── Utility Navigation
│   │   ├── User Menu (Dropdown)
│   │   └── Contextual Action Menu
│   ├── Contextual Navigation
│   │   ├── Inline Links
│   │   ├── Related Items
│   │   └── Suggested Actions
│   ├── Mobile Navigation
│   │   ├── Bottom Tab Bar
│   │   ├── Hamburger Menu
│   │   └── Swipe Gestures
│   └── Cross-Context Navigation
│       ├── Context Transition with Visual Indicator
│       ├── Embedded Context View
│       └── Workflow Across Contexts
│
├── Workflow Patterns
│   ├── Linear Workflows (Wizard/Stepped)
│   │   ├── Multi-Step Form Wizard
│   │   └── Checkout/Application Flow
│   ├── Flexible Workflows (Dashboard/Hub)
│   │   └── Dashboard with Action Cards
│   ├── Guided Workflows (Progressive Disclosure)
│   │   └── Adaptive Form
│   ├── Batch Workflows
│   │   └── Select and Act
│   ├── Conditional Workflows (Branching Logic)
│   │   └── Branching Wizard
│   └── Long-Running Workflows (Async/Background)
│       └── Background Job with Progress Tracking
│
├── Page Architecture
│   ├── List/Collection Pages
│   │   ├── Job Listings
│   │   └── Applications List
│   ├── Detail/Entity Pages
│   │   ├── Job Detail
│   │   └── Application Detail
│   ├── Form/Edit Pages
│   │   └── Edit Profile
│   ├── Dashboard/Overview Pages
│   │   └── Job Seeker Dashboard
│   ├── Search/Filter Pages
│   │   └── Advanced Job Search
│   ├── Empty State Pages
│   │   └── No Applications Yet
│   └── Error Pages
│       ├── 404 Not Found
│       ├── 403 Forbidden
│       └── 500 Server Error
│
├── Component Architecture (Atomic Design)
│   ├── Atoms (Primitives)
│   │   ├── Button
│   │   ├── Input
│   │   ├── Label
│   │   ├── Badge
│   │   └── Icon
│   ├── Molecules (Simple Composites)
│   │   ├── Form Field
│   │   ├── Tag Input
│   │   ├── Search Input
│   │   ├── Progress Bar
│   │   └── Match Score Display
│   ├── Organisms (Complex Composites)
│   │   ├── Job Card
│   │   ├── Application Card
│   │   ├── Navigation Bar
│   │   └── Filter Panel
│   ├── Domain Components (DDD-Specific)
│   │   ├── Skills Display
│   │   ├── Skills Gap Widget
│   │   └── Application Timeline
│   ├── Layout Components
│   │   ├── Container
│   │   ├── Grid
│   │   ├── Stack
│   │   └── Card
│   └── Utility Components
│       ├── Modal
│       └── Toast/Notification
│
├── Behavioral Specifications
│   ├── Interaction Patterns
│   │   ├── Click/Tap Behavior
│   │   ├── Hover Behavior
│   │   ├── Focus Behavior
│   │   └── Selection Behavior
│   ├── Feedback Mechanisms
│   │   ├── Immediate Feedback
│   │   ├── Success Feedback
│   │   └── Confirmation Dialogs
│   ├── State Transitions
│   │   ├── Aggregate State Machine Visualization
│   │   └── Loading States
│   ├── Animations & Micro-interactions
│   │   ├── Page Transitions
│   │   ├── Component Animations
│   │   ├── Micro-interactions
│   │   └── Domain Event Animations
│   ├── Error Handling Behavior
│   │   ├── Inline Errors
│   │   ├── Global Errors
│   │   └── Optimistic UI with Rollback
│   ├── Loading & Async States
│   │   ├── Skeleton Screens
│   │   └── Progress Indicators
│   └── Domain Event-Triggered Behavior
│       ├── Real-Time Notifications
│       └── Aggregate State Change Visualization
│
└── Scalability Patterns
    ├── Responsive & Adaptive Design
    │   ├── Mobile-First Approach
    │   ├── Adaptive Layouts
    │   └── Responsive Navigation
    ├── Data Scalability
    │   ├── Pagination
    │   ├── Infinite Scroll
    │   ├── Virtual Scrolling
    │   └── Data Caching
    ├── Performance Optimization
    │   ├── Code Splitting
    │   ├── Component-Level Splitting
    │   ├── Image Optimization
    │   ├── Memoization
    │   └── Debouncing & Throttling
    ├── Progressive Enhancement
    │   ├── Baseline Functionality
    │   ├── Feature Detection
    │   └── Offline Support
    └── Code Scalability
        ├── Module Organization by Domain
        ├── Component Composition
        └── Type Safety (TypeScript)
```

---

## Pattern Categories Summary

### Strategic (System-Level) - 4 categories, 18 patterns
**Purpose**: Organize the entire UX system structure

1. **Information Architecture** (4 patterns)
2. **Navigation Patterns** (6 subcategories, 18 patterns)
3. **Workflow Patterns** (6 patterns)
4. **Scalability Patterns** (5 subcategories, 15 patterns)

### Tactical (Implementation-Level) - 3 categories, 40+ patterns
**Purpose**: Build specific UI elements

1. **Page Architecture** (7 page types)
2. **Component Architecture** (6 categories, 25+ components)
3. **Behavioral Specifications** (7 categories, 20+ behaviors)

**Total**: ~80+ documented UX patterns

---

## Pattern Dependencies

### Hierarchical Dependencies

```yaml
dependencies:
  information_architecture:
    depends_on: []
    enables:
      - navigation_patterns
      - page_architecture

  navigation_patterns:
    depends_on:
      - information_architecture
    enables:
      - workflow_patterns
      - page_architecture

  page_architecture:
    depends_on:
      - information_architecture
      - navigation_patterns
    enables:
      - component_architecture

  component_architecture:
    depends_on:
      - page_architecture
    enables:
      - behavioral_specifications

  workflow_patterns:
    depends_on:
      - navigation_patterns
      - page_architecture
    enables:
      - behavioral_specifications

  behavioral_specifications:
    depends_on:
      - component_architecture
      - workflow_patterns
    enables: []

  scalability_patterns:
    depends_on: [all_above]
    applies_across: all_patterns
```

---

## DDD Pattern Mapping

### IA → DDD Strategic Patterns

```yaml
mapping:
  hierarchical_organization:
    maps_to: bounded_context_hierarchy
    example: "Profile (bc_profile) > Skills > Expertise"

  faceted_classification:
    maps_to: value_object_attributes
    example: "vo_location, vo_job_type, vo_experience_level"

  navigation_sections:
    maps_to: bounded_contexts
    example: "Profile → bc_profile, Jobs → bc_job_catalog"
```

---

### Pages → DDD Aggregates

```yaml
mapping:
  list_page:
    maps_to: aggregate_collection
    query: repository.get_all()
    example: "Job Listings = repo_job_posting.get_active_jobs()"

  detail_page:
    maps_to: single_aggregate
    query: repository.get_by_id(id)
    example: "Job Detail = repo_job_posting.get_by_id(job_id)"

  form_page:
    maps_to: aggregate_create_or_update
    service: application_service
    example: "Edit Profile = svc_app_update_candidate_profile"

  dashboard:
    maps_to: multi_context_aggregation
    query: multiple_repositories
    example: "Dashboard queries bc_profile, bc_matching, bc_applications"
```

---

### Components → DDD Value Objects

```yaml
mapping:
  input_component:
    maps_to: value_object_validation
    example: "EmailInput enforces vo_email validation rules"

  display_component:
    maps_to: value_object_formatting
    example: "MatchScore displays vo_match_score"

  domain_component:
    maps_to: aggregate_or_entity
    example: "SkillsGap displays agg_skills_gap"
```

---

### Behaviors → DDD Events & States

```yaml
mapping:
  state_transition:
    maps_to: aggregate_state_machine
    example: "Application status badge reflects DRAFT → SUBMITTED → IN_REVIEW"

  domain_event_trigger:
    maps_to: domain_event_published
    example: "Toast notification on evt_application_submitted"

  validation_behavior:
    maps_to: value_object_invariants
    example: "Inline error on vo_skills validation failure"
```

---

## Decision Frameworks

### When to Use Which Navigation Pattern?

```yaml
decision_tree:
  question: "How many primary sections?"

  if: 3-7_sections
    use: top_horizontal_nav (desktop) + bottom_tab_bar (mobile)

  if: 8+_sections
    use: side_navigation_drawer (desktop) + hamburger_menu (mobile)

  question: "Is this within a specific bounded context?"

  if: yes
    use: local_navigation (tabs, sub-nav)

  if: crossing_contexts
    use: cross_context_navigation (breadcrumbs, context_transitions)
```

---

### When to Use Which Workflow Pattern?

```yaml
decision_tree:
  question: "Is there a required sequence of steps?"

  if: yes
    question: "Can user skip steps?"
    if: no
      use: linear_wizard
    if: yes
      use: guided_workflow (progressive_disclosure)

  if: no
    question: "Are there multiple independent tasks?"
    if: yes
      use: flexible_workflow (dashboard/hub)

  question: "Does workflow span multiple bounded contexts?"

  if: yes
    use: workflow_across_contexts
    ensure: context_transitions_are_clear

  question: "Will workflow take > 5 seconds to complete?"

  if: yes
    use: long_running_workflow (background_job with_progress)
```

---

### When to Use Which Page Type?

```yaml
decision_tree:
  question: "What is the primary purpose?"

  if: browse_collection
    use: list_page
    ddd: displays aggregate collection

  if: view_single_item
    use: detail_page
    ddd: displays single aggregate

  if: create_or_edit
    use: form_page
    ddd: invokes application service

  if: overview_of_multiple_contexts
    use: dashboard_page
    ddd: aggregates data from multiple bounded contexts

  if: find_specific_item
    use: search_page
    ddd: repository query with filters
```

---

### When to Use Which Component Pattern?

```yaml
decision_tree:
  question: "What level of complexity?"

  if: basic_ui_element (button, input)
    use: atom

  if: grouped_atoms with_single_responsibility
    use: molecule (form_field, search_input)

  if: complex_multi_part_component
    use: organism (job_card, nav_bar)

  question: "Is this domain-specific?"

  if: yes
    question: "Does it display an aggregate?"
    if: yes
      use: domain_component_organism
      example: ApplicationTimeline (agg_application)

    question: "Does it enforce VO validation?"
    if: yes
      use: domain_input_component
      example: SkillsInput (vo_skills)

  if: no
    use: shared_component (atoms, molecules, layouts)
```

---

### When to Use Which Data Scalability Pattern?

```yaml
decision_tree:
  question: "How many items in collection?"

  if: < 100_items
    use: simple_pagination or load_all

  if: 100-500_items
    use: server_side_pagination

  if: 500-5000_items
    question: "Is this mobile?"
    if: yes
      use: infinite_scroll
    if: no
      use: pagination or virtual_scrolling

  if: > 5000_items
    use: virtual_scrolling + pagination

  ddd_integration:
    - repository: must_support_pagination
    - query: page_size and cursor_or_offset
```

---

## Anti-Patterns (What NOT to Do)

### IA Anti-Patterns

```yaml
anti_patterns:
  - mystery_meat_navigation:
      problem: icons_without_labels
      fix: always_include_labels (or aria_labels)

  - too_deep_hierarchy:
      problem: > 4_levels_deep
      fix: flatten_structure or use_faceted_classification

  - inconsistent_terminology:
      problem: "Jobs" vs "Postings" vs "Opportunities"
      fix: use_ubiquitous_language_from_ddd

  - ignoring_context_boundaries:
      problem: mixing_profile_and_jobs_navigation
      fix: respect_bounded_context_separation
```

---

### Component Anti-Patterns

```yaml
anti_patterns:
  - god_component:
      problem: component_does_too_much
      fix: split_into_smaller_components (atomic_design)

  - prop_drilling:
      problem: passing_props_through_5+_levels
      fix: context_api or state_management

  - inline_validation_logic:
      problem: validation_rules_in_component
      fix: extract_to_value_object_validation

  - ignoring_domain_state:
      problem: local_state_diverges_from_aggregate_state
      fix: single_source_of_truth (aggregate_state)
```

---

### Workflow Anti-Patterns

```yaml
anti_patterns:
  - unclear_progress:
      problem: user_doesn't_know_where_they_are
      fix: always_show_progress_indicator

  - losing_user_data:
      problem: no_draft_saving in_multi_step_form
      fix: auto_save_draft_state

  - no_error_recovery:
      problem: form_fails_user_loses_all_data
      fix: optimistic_updates_with_rollback

  - ignoring_domain_events:
      problem: ui_doesn't_respond_to_evt_application_submitted
      fix: subscribe_to_domain_events
```

---

## Pattern Selection Matrix

### By Complexity Level

```yaml
simple_application:
  use:
    - ia: hierarchical_organization
    - nav: top_nav_bar
    - pages: list + detail + form
    - components: atoms + molecules
    - workflows: linear_wizard
    - scalability: basic_responsive

medium_application:
  use:
    - ia: hierarchical + faceted
    - nav: top_nav + local_nav + breadcrumbs
    - pages: all_7_types
    - components: atoms + molecules + organisms
    - workflows: linear + flexible + conditional
    - scalability: pagination + code_splitting + caching

complex_application (Job Seeker):
  use:
    - ia: all_organization_systems
    - nav: all_navigation_patterns (context-aware)
    - pages: all_types + cross_context_compositions
    - components: full_atomic_design + domain_components
    - workflows: all_patterns (context-spanning)
    - scalability: all_patterns + domain_aware_optimization
```

---

### By DDD Complexity

```yaml
single_bounded_context:
  use:
    - nav: local_navigation_only
    - pages: within_context
    - components: context_specific
    - workflows: intra_context

multiple_bounded_contexts (Job Seeker: 9 contexts):
  use:
    - nav: global + local + cross_context
    - pages: context_specific + cross_context_dashboards
    - components: shared + context_specific + domain_components
    - workflows: intra_context + cross_context
    - scalability: code_split_by_context
```

---

## Evolution Paths

### Simple → Complex Evolution

```yaml
evolution:
  phase_1_mvp:
    - ia: simple_hierarchy
    - nav: basic_top_nav
    - pages: list + detail
    - components: atoms + basic_molecules
    - workflows: simple_forms

  phase_2_growth:
    - ia: add_faceted_search
    - nav: add_breadcrumbs + local_nav
    - pages: add_dashboard + search
    - components: add_organisms
    - workflows: add_wizards

  phase_3_scale:
    - ia: optimize_labeling
    - nav: add_mobile_patterns + context_transitions
    - pages: add_empty_states + advanced_error_handling
    - components: add_domain_components
    - workflows: add_conditional + async
    - scalability: add_all_optimization_patterns
```

---

## Integration with Other Practices

### UX + DDD

```yaml
integration:
  strategic_level:
    - ux_ia → ddd_bounded_contexts
    - ux_navigation → ddd_context_map

  tactical_level:
    - ux_pages → ddd_aggregates
    - ux_components → ddd_value_objects
    - ux_validation → ddd_vo_invariants

  behavioral_level:
    - ux_state_transitions → ddd_aggregate_state_machine
    - ux_notifications → ddd_domain_events
```

---

### UX + CQRS/Event Sourcing

```yaml
integration:
  commands:
    - ux_forms_submit → cqrs_commands
    - ux_buttons_trigger → command_handlers

  queries:
    - ux_pages_display → cqrs_queries
    - ux_read_models → query_optimized_views

  events:
    - ux_notifications → event_stream
    - ux_real_time_updates → event_subscriptions
```

---

### UX + Microservices

```yaml
integration:
  - each_bounded_context → potential_microservice
  - cross_context_workflows → saga_pattern
  - ui_composition → micro_frontends (per_context)
```

---

## Quick Reference: Pattern → DDD Mapping

| UX Pattern | DDD Pattern | Example |
|------------|-------------|---------|
| Navigation Section | Bounded Context | Jobs → bc_job_catalog |
| Facet Filter | Value Object | Location filter → vo_location |
| List Page | Aggregate Collection | Job Listings → [agg_job_posting] |
| Detail Page | Single Aggregate | Job Detail → agg_job_posting(id) |
| Form Page | Application Service | Edit Profile → svc_app_update_profile |
| Dashboard Widget | Repository Query | Match Count → repo_job_match.count() |
| Input Component | VO Validation | EmailInput → vo_email.validate() |
| Display Component | VO Formatting | MatchScore → vo_match_score.display() |
| Domain Component | Aggregate Display | ApplicationTimeline → agg_application |
| State Badge | Aggregate State | Status: Submitted → application.status |
| Toast Notification | Domain Event | "Application submitted!" → evt_application_submitted |
| Workflow | Use Case | Submit Application → svc_app_submit_application |
| Breadcrumb | Context Transition | Jobs > Job Detail > Skills Gap (bc_job_catalog → bc_skills_analysis) |
| Paginated List | Repository Pagination | Jobs page 1 → repo.get_jobs(page: 1, size: 20) |

---

## Key Takeaways

1. **80+ Patterns**: Complete UX system from IA to components to behaviors.

2. **Hierarchical Organization**: Strategic (system-level) → Tactical (implementation-level).

3. **DDD Alignment**: Every UX pattern maps to a DDD pattern for consistency.

4. **Decision Frameworks**: Clear guidance on when to use which pattern.

5. **Dependencies**: Understand which patterns enable others (IA enables navigation, pages enable components).

6. **Evolution Path**: Start simple (MVP), grow systematically (add patterns as needed).

7. **Anti-Patterns**: Know what NOT to do to avoid common pitfalls.

8. **Integration**: UX + DDD + CQRS + Microservices work together seamlessly.

---

## References

**All UX Research Documents**:
- `01-ia-foundations.md` - Information architecture patterns
- `02-navigation-patterns.md` - Navigation hierarchy
- `03-workflow-patterns.md` - Workflow classifications
- `04-page-architecture.md` - Page types
- `05-component-architecture.md` - Atomic design hierarchy
- `06-behavior-specifications.md` - Interaction and behavior patterns
- `07-scalability-patterns.md` - Optimization patterns

**DDD Research**:
- `research/ddd/working-docs/06-ontological-taxonomy.md` - DDD pattern hierarchy
- `research/ddd/deliverables/ddd-schema-example.yaml` - Domain model reference

---

*Document created: 2025-10-04*
*Part of UX Research Phase 9: Synthesis & Ontological Taxonomy*
