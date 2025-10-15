# Behavioral Specifications

## Overview

Behavioral specifications define how UI responds to user interactions, system events, and state changes. Effective behavior provides feedback, guides users, prevents errors, and makes the application feel responsive and intuitive.

**Integration with DDD**: UI behavior responds to domain events, reflects aggregate state transitions, and provides feedback aligned with business logic. Animations and micro-interactions communicate domain changes to users.

---

## Behavior Categories

### 1. Interaction Patterns
### 2. Feedback Mechanisms
### 3. State Transitions
### 4. Animations & Micro-interactions
### 5. Error Handling Behavior
### 6. Loading & Async States
### 7. Domain Event-Triggered Behavior

---

## 1. Interaction Patterns

Standard responses to user actions.

### Click/Tap Behavior

**Button Click**:
```yaml
interaction: button_click

states:
  default:
    - cursor: pointer
    - visual: default_style

  hover:
    - cursor: pointer
    - visual: slight_background_darken
    - transition: 150ms

  active:
    - cursor: pointer
    - visual: background_darken + scale_down(0.98)
    - transition: 100ms

  disabled:
    - cursor: not-allowed
    - visual: opacity(0.5)
    - no_interaction: true

  loading:
    - cursor: wait
    - visual: spinner_icon
    - disabled: true
```

**DDD Example - Apply Button**:
```tsx
<Button
  onClick={handleApply}
  disabled={!canApply}  // Domain rule: profile must be 80% complete
  loading={isSubmitting}
>
  Apply Now
</Button>

// Domain logic determines button state
const canApply = useMemo(() => {
  return profileCompleteness >= 80;  // DDD invariant
}, [profileCompleteness]);
```

---

### Hover Behavior

**Job Card Hover**:
```yaml
interaction: card_hover

default_state:
  - border: 1px solid gray-200
  - shadow: none
  - transition: all 200ms

hover_state:
  - border: 1px solid primary-500
  - shadow: 0 4px 12px rgba(0,0,0,0.1)
  - transform: translateY(-2px)
  - transition: all 200ms

  actions_revealed:
    - show: quick_actions_toolbar
    - animation: fade_in 150ms
```

**Example**:
```css
.job-card {
  border: 1px solid var(--gray-200);
  transition: all 200ms ease;
}

.job-card:hover {
  border-color: var(--primary-500);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.job-card:hover .quick-actions {
  opacity: 1;
  visibility: visible;
}
```

---

### Focus Behavior

**Keyboard Navigation**:
```yaml
interaction: keyboard_focus

requirements:
  - visible_focus_indicator (WCAG 2.4.7)
  - focus_order_follows_dom (WCAG 2.4.3)
  - skip_links_available

focus_visible_style:
  - outline: 2px solid primary-500
  - outline_offset: 2px
  - no_outline_on_mouse_click (use :focus-visible)
```

**Example**:
```css
/* Focus visible for keyboard navigation */
input:focus-visible,
button:focus-visible {
  outline: 2px solid var(--primary-500);
  outline-offset: 2px;
}

/* No outline for mouse clicks */
input:focus:not(:focus-visible),
button:focus:not(:focus-visible) {
  outline: none;
}
```

**DDD Integration - Form Field Focus Order**:
```tsx
// Focus order follows aggregate structure
<form>
  {/* Personal Info (ent_candidate) */}
  <Input name="name" tabIndex={1} />
  <Input name="email" tabIndex={2} />  {/* vo_email */}

  {/* Skills (vo_skills) */}
  <TagInput name="skills" tabIndex={3} />

  {/* Experience (vo_experience_level) */}
  <Select name="experience" tabIndex={4} />

  <Button type="submit" tabIndex={5}>
    Save Profile
  </Button>
</form>
```

---

### Selection Behavior

**Multi-Select (Checkbox List)**:
```yaml
interaction: checkbox_multi_select

behaviors:
  individual_checkbox:
    - click: toggle_selection
    - visual: checkmark + background_color

  select_all:
    - click: select_all_items
    - if all_selected: unselect_all
    - visual: indeterminate_state if some_selected

  bulk_actions:
    - enabled_when: selection_count > 0
    - show: action_bar with selected_count
```

**DDD Example - Bulk Apply to Jobs**:
```tsx
const [selectedJobs, setSelectedJobs] = useState<string[]>([]);

// Domain rule: Can only bulk apply to max 10 jobs
const maxBulkApply = 10;

<div>
  <Checkbox
    checked={selectedJobs.length === jobs.length}
    indeterminate={selectedJobs.length > 0 && selectedJobs.length < jobs.length}
    onChange={handleSelectAll}
  >
    Select All
  </Checkbox>

  {jobs.map(job => (
    <Checkbox
      key={job.id}
      checked={selectedJobs.includes(job.id)}
      onChange={() => handleToggleSelection(job.id)}
      disabled={
        !selectedJobs.includes(job.id) &&
        selectedJobs.length >= maxBulkApply
      }
    >
      {job.title}
    </Checkbox>
  ))}

  {selectedJobs.length > 0 && (
    <ActionBar>
      <span>{selectedJobs.length} jobs selected</span>
      <Button onClick={handleBulkApply}>
        Apply to {selectedJobs.length} Jobs
      </Button>
    </ActionBar>
  )}
</div>
```

---

## 2. Feedback Mechanisms

Confirming user actions and system responses.

### Immediate Feedback

**Form Input Validation**:
```yaml
feedback: inline_validation

triggers:
  - on_blur: validate_field
  - on_change: clear_error (if valid)

display:
  - success: green_border + checkmark_icon
  - error: red_border + error_message + error_icon

timing:
  - show_error: immediately on blur
  - show_success: after first successful validation
```

**DDD Example - Email Validation (vo_email)**:
```tsx
const [email, setEmail] = useState('');
const [emailError, setEmailError] = useState<string | null>(null);

const validateEmail = (value: string): string | null => {
  // Validation from DDD vo_email
  if (!value) return "Email is required";
  if (!emailRegex.test(value)) return "Please enter a valid email";
  return null;
};

<FormField
  label="Email Address"
  type="email"
  value={email}
  onChange={(value) => {
    setEmail(value);
    if (emailError) {
      // Clear error immediately if now valid
      const error = validateEmail(value);
      if (!error) setEmailError(null);
    }
  }}
  onBlur={() => {
    // Validate on blur
    setEmailError(validateEmail(email));
  }}
  error={emailError}
  success={!emailError && email.length > 0}
/>
```

---

### Success Feedback

**Toast Notification**:
```yaml
feedback: success_toast

trigger: domain_event_published
examples:
  - evt_profile_updated
  - evt_application_submitted
  - evt_job_saved

display:
  - position: top_right
  - variant: success (green)
  - icon: checkmark
  - duration: 5000ms
  - dismissible: true
  - animation: slide_in_right
```

**DDD Example - Application Submitted**:
```tsx
// When evt_application_submitted is published
useEffect(() => {
  if (applicationSubmitted) {
    showToast({
      variant: 'success',
      message: 'Application submitted successfully!',
      action: {
        label: 'View Application',
        onClick: () => navigate(`/applications/${applicationId}`)
      },
      duration: 5000
    });
  }
}, [applicationSubmitted]);
```

---

### Confirmation Dialogs

**Destructive Action Confirmation**:
```yaml
feedback: confirmation_modal

trigger: destructive_action_attempt
examples:
  - withdraw_application
  - delete_saved_job
  - discard_draft

display:
  - modal: true
  - title: clear_description of action
  - message: consequences of action
  - actions:
      - cancel (secondary)
      - confirm (danger variant)
```

**DDD Example - Withdraw Application**:
```tsx
const handleWithdrawClick = () => {
  setShowConfirmModal(true);
};

<ConfirmationModal
  isOpen={showConfirmModal}
  onClose={() => setShowConfirmModal(false)}
  title="Withdraw Application?"
  message="Are you sure you want to withdraw your application to TechCorp? This action cannot be undone."
  variant="danger"
  confirmLabel="Withdraw Application"
  onConfirm={async () => {
    // Invokes application service
    await withdrawApplication(applicationId);

    // Domain event: evt_application_withdrawn
    // Aggregate state: WITHDRAWN

    showToast({
      variant: 'info',
      message: 'Application withdrawn'
    });

    navigate('/applications');
  }}
/>
```

---

## 3. State Transitions

Visual representation of state changes.

### Aggregate State Machine Visualization

**Application Status Progression**:
```yaml
state_machine: agg_application

states:
  - DRAFT
  - SUBMITTED
  - IN_REVIEW
  - INTERVIEWING
  - OFFER
  - ACCEPTED
  - REJECTED
  - WITHDRAWN

transitions:
  DRAFT → SUBMITTED:
    trigger: user_action (submit button)
    publishes: evt_application_submitted

  SUBMITTED → IN_REVIEW:
    trigger: employer_action
    publishes: evt_application_viewed

  IN_REVIEW → INTERVIEWING:
    trigger: employer_action
    publishes: evt_interview_scheduled

  INTERVIEWING → OFFER:
    trigger: employer_action
    publishes: evt_offer_extended

  OFFER → ACCEPTED:
    trigger: user_action
    publishes: evt_offer_accepted

visual_feedback:
  - status_badge_color_change
  - timeline_progress_update
  - notification (if user not on page)
```

**UI Implementation**:
```tsx
const ApplicationStatus: React.FC<{application: Application}> = ({application}) => {
  const statusConfig = {
    DRAFT: {variant: 'neutral', label: 'Draft'},
    SUBMITTED: {variant: 'info', label: 'Submitted'},
    IN_REVIEW: {variant: 'info', label: 'In Review'},
    INTERVIEWING: {variant: 'warning', label: 'Interview Scheduled'},
    OFFER: {variant: 'success', label: 'Offer Received'},
    ACCEPTED: {variant: 'success', label: 'Offer Accepted'},
    REJECTED: {variant: 'error', label: 'Not Selected'},
    WITHDRAWN: {variant: 'neutral', label: 'Withdrawn'}
  };

  const config = statusConfig[application.status];

  return (
    <div>
      <Badge variant={config.variant}>
        {config.label}
      </Badge>

      <ApplicationTimeline
        currentState={application.status}
        events={application.events}
      />
    </div>
  );
};
```

---

### Loading States

**Progressive Loading**:
```yaml
loading_states:
  initial_load:
    - show: skeleton_screens
    - duration: until_data_loaded

  partial_load:
    - show: loaded_content + spinner_for_remaining
    - example: job_list_loaded + more_loading

  refresh:
    - show: overlay_spinner on existing_content
    - preserve: current_scroll_position

  inline_action:
    - show: spinner_in_button
    - disable: button
    - label: "Submitting..." instead of "Submit"
```

**DDD Example - Loading Job Matches**:
```tsx
const JobMatchesPage: React.FC = () => {
  const {data: matches, isLoading, error} = useQuery(
    'job-matches',
    () => fetchJobMatches()  // Calls bc_matching service
  );

  if (isLoading) {
    return (
      <div>
        <h1>Your Job Matches</h1>
        {/* Skeleton screens */}
        {Array(5).fill(0).map((_, i) => (
          <JobCardSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (error) {
    return <ErrorState error={error} />;
  }

  return (
    <div>
      <h1>Your Job Matches ({matches.length})</h1>
      {matches.map(match => (
        <JobCard key={match.id} job={match} />
      ))}
    </div>
  );
};
```

---

## 4. Animations & Micro-interactions

Subtle animations that provide feedback and delight.

### Page Transitions

**Navigation Animation**:
```yaml
animation: page_transition

default:
  - enter: fade_in + slide_up(20px)
  - duration: 300ms
  - easing: ease_out

exit:
  - animation: fade_out
  - duration: 200ms

preserve_scroll:
  - save: scroll_position on exit
  - restore: on back_navigation
```

---

### Component Animations

**Dropdown Menu**:
```yaml
animation: dropdown_expand

trigger: click_dropdown_trigger

enter:
  - animation: scale_y(0 → 1) + fade_in
  - origin: top
  - duration: 200ms
  - easing: ease_out

exit:
  - animation: scale_y(1 → 0) + fade_out
  - duration: 150ms
  - easing: ease_in
```

---

### Micro-interactions

**Save Job (Heart Icon)**:
```yaml
micro_interaction: toggle_save_job

trigger: click_save_button

unsaved → saved:
  - icon: outline_heart → filled_heart
  - color: gray → red
  - animation: scale_up(1 → 1.3 → 1)
  - duration: 300ms
  - easing: ease_out

saved → unsaved:
  - icon: filled_heart → outline_heart
  - color: red → gray
  - animation: scale_down(1 → 0.8 → 1)
  - duration: 200ms

ddd_integration:
  - updates: candidate.saved_jobs (bc_profile)
  - publishes: evt_job_saved or evt_job_unsaved
```

**Implementation**:
```tsx
const SaveJobButton: React.FC<{jobId: string, saved: boolean}> = ({jobId, saved}) => {
  const [isSaved, setIsSaved] = useState(saved);
  const [isAnimating, setIsAnimating] = useState(false);

  const handleToggle = async () => {
    setIsAnimating(true);

    if (isSaved) {
      await unsaveJob(jobId);  // Application service
      setIsSaved(false);
      // evt_job_unsaved published
    } else {
      await saveJob(jobId);  // Application service
      setIsSaved(true);
      // evt_job_saved published
    }

    setTimeout(() => setIsAnimating(false), 300);
  };

  return (
    <button
      onClick={handleToggle}
      className={`save-button ${isAnimating ? 'animating' : ''}`}
      aria-label={isSaved ? 'Unsave job' : 'Save job'}
    >
      <Icon
        name={isSaved ? 'heart-filled' : 'heart-outline'}
        color={isSaved ? 'red' : 'gray'}
      />
    </button>
  );
};
```

```css
.save-button.animating {
  animation: save-pulse 300ms ease-out;
}

@keyframes save-pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}
```

---

### Domain Event Animations

**New Match Notification**:
```yaml
animation: new_match_badge

trigger: evt_high_match_found (domain event)

behavior:
  - show: notification_badge on nav_item
  - animation: pulse + scale
  - duration: 600ms
  - repeat: 3_times
  - color: primary_500

user_action:
  - on_click: navigate_to_matches
  - animation: fade_out_badge
  - mark_as_viewed
```

**Implementation**:
```tsx
const JobsNavItem: React.FC = () => {
  const {data: newMatchesCount} = useQuery('new-matches-count');

  // Listen for domain events via websocket
  useEffect(() => {
    const unsubscribe = subscribeToDomainEvent('evt_high_match_found', (event) => {
      // Trigger animation
      setShowBadgeAnimation(true);

      // Update count
      queryClient.invalidateQueries('new-matches-count');
    });

    return unsubscribe;
  }, []);

  return (
    <NavLink to="/jobs/matches">
      Jobs
      {newMatchesCount > 0 && (
        <Badge
          variant="primary"
          className={showBadgeAnimation ? 'pulse-animation' : ''}
        >
          {newMatchesCount}
        </Badge>
      )}
    </NavLink>
  );
};
```

---

## 5. Error Handling Behavior

Graceful error states and recovery.

### Inline Errors

**Form Validation Errors**:
```yaml
error_display: inline_field_error

trigger:
  - client_validation_fail
  - server_validation_fail (vo_validation)

display:
  - position: below_field
  - color: error_red
  - icon: error_icon
  - role: alert (ARIA)

behavior:
  - show: immediately on trigger
  - clear: when field becomes valid
  - focus: error_field (if on submit)
```

**DDD Example - Skills Validation**:
```tsx
const SkillsInput: React.FC = () => {
  const [skills, setSkills] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);

  const validateSkills = (skillsList: string[]): string | null => {
    // vo_skills validation rules
    if (skillsList.length === 0) return "Add at least one skill";
    if (skillsList.length > 20) return "Maximum 20 skills allowed";
    return null;
  };

  const handleAdd = (skill: string) => {
    const newSkills = [...skills, skill];
    const validationError = validateSkills(newSkills);

    if (validationError) {
      setError(validationError);
      return;
    }

    setSkills(newSkills);
    setError(null);
  };

  return (
    <div>
      <TagInput
        value={skills}
        onAdd={handleAdd}
        error={error}
      />
      {error && (
        <span role="alert" className="error-message">
          <Icon name="error" />
          {error}
        </span>
      )}
    </div>
  );
};
```

---

### Global Errors

**Network Error**:
```yaml
error_display: error_banner

trigger: api_request_fail

display:
  - position: top_of_page (below_header)
  - variant: error
  - dismissible: true
  - retry_action: available

message:
  - generic: "Something went wrong. Please try again."
  - specific: include_error_details (if helpful)

ddd_integration:
  - preserve: user_data (draft state)
  - log: error_to_monitoring
  - error_id: for_support_reference
```

---

### Optimistic UI with Rollback

**Save Profile (Optimistic Update)**:
```yaml
pattern: optimistic_update

behavior:
  on_submit:
    - update_ui: immediately
    - show: saving_indicator
    - call_api: async

  on_success:
    - hide: saving_indicator
    - show: success_toast
    - publish: evt_profile_updated

  on_error:
    - revert: ui_changes
    - show: error_message
    - restore: previous_state
```

**Implementation**:
```tsx
const EditProfile: React.FC = () => {
  const [profile, setProfile] = useState<CandidateProfile>(initialProfile);
  const [previousProfile, setPreviousProfile] = useState<CandidateProfile>(initialProfile);

  const handleSave = async (updates: Partial<CandidateProfile>) => {
    // Save current state for rollback
    setPreviousProfile(profile);

    // Optimistic update
    setProfile({...profile, ...updates});
    setIsSaving(true);

    try {
      // Call application service
      await updateCandidateProfile(profile.id, updates);

      // Success
      setIsSaving(false);
      showToast({variant: 'success', message: 'Profile updated'});

      // evt_profile_updated published on backend
    } catch (error) {
      // Rollback to previous state
      setProfile(previousProfile);
      setIsSaving(false);

      showToast({
        variant: 'error',
        message: 'Failed to update profile. Please try again.'
      });
    }
  };

  return (
    <ProfileForm
      profile={profile}
      onSave={handleSave}
      isSaving={isSaving}
    />
  );
};
```

---

## 6. Loading & Async States

Handling asynchronous operations.

### Skeleton Screens

**Job List Loading**:
```yaml
loading_pattern: skeleton_screen

display:
  - placeholder: mimics_final_content_structure
  - animation: shimmer (left to right)
  - duration: until_data_loaded

benefits:
  - perceived_performance: faster than spinner
  - content_awareness: user sees what's coming
```

**Implementation**:
```tsx
const JobCardSkeleton: React.FC = () => {
  return (
    <div className="job-card skeleton">
      <div className="skeleton-header">
        <div className="skeleton-title" />
        <div className="skeleton-icon" />
      </div>
      <div className="skeleton-meta">
        <div className="skeleton-text short" />
        <div className="skeleton-text short" />
        <div className="skeleton-text short" />
      </div>
      <div className="skeleton-badge" />
      <div className="skeleton-skills">
        <div className="skeleton-tag" />
        <div className="skeleton-tag" />
        <div className="skeleton-tag" />
      </div>
    </div>
  );
};
```

```css
.skeleton {
  background: linear-gradient(
    90deg,
    #f0f0f0 25%,
    #e0e0e0 50%,
    #f0f0f0 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
```

---

### Progress Indicators

**Long-Running Operation (Skills Gap Analysis)**:
```yaml
loading_pattern: progress_bar_with_steps

trigger: svc_app_analyze_skills_gap

display:
  - progress_bar: 0-100%
  - current_step: description
  - estimated_time: remaining

steps:
  - step_1: "Extracting your skills..." (20%)
  - step_2: "Analyzing job market..." (40%)
  - step_3: "Comparing skills..." (70%)
  - step_4: "Generating recommendations..." (90%)
  - complete: "Analysis complete!" (100%)

ddd_integration:
  - background_processing: async_job
  - status_polling: every_2_seconds
  - domain_event: evt_analysis_completed
```

---

## 7. Domain Event-Triggered Behavior

UI responds to domain events.

### Real-Time Notifications

**Interview Scheduled (evt_interview_scheduled)**:
```yaml
domain_event: evt_interview_scheduled

published_by: bc_applications (employer action)

ui_behavior:
  if_user_on_applications_page:
    - update: application_card_status
    - show: action_required_banner
    - animation: highlight_card

  if_user_elsewhere:
    - show: toast_notification
    - update: nav_badge_count
    - play: notification_sound (optional)

  always:
    - send: email_notification
    - create: calendar_invite
```

**Implementation**:
```tsx
// WebSocket listener for domain events
useEffect(() => {
  const ws = new WebSocket('wss://api.jobseeker.com/events');

  ws.onmessage = (event) => {
    const domainEvent = JSON.parse(event.data);

    if (domainEvent.type === 'evt_interview_scheduled') {
      // Update application data
      queryClient.invalidateQueries(['application', domainEvent.applicationId]);

      // Show notification
      showToast({
        variant: 'warning',
        message: `Interview scheduled with ${domainEvent.company}`,
        action: {
          label: 'View Details',
          onClick: () => navigate(`/applications/${domainEvent.applicationId}`)
        },
        duration: 10000  // Longer duration for important events
      });

      // Update badge count
      queryClient.invalidateQueries('pending-actions-count');

      // Optional: Play sound
      playNotificationSound();
    }
  };

  return () => ws.close();
}, []);
```

---

### Aggregate State Change Visualization

**Profile Completeness Update (evt_profile_updated)**:
```yaml
domain_event: evt_profile_updated

published_by: bc_profile

ui_behavior:
  - recalculate: profile_completeness_percentage
  - animate: progress_bar (smooth transition)
  - show: success_indicator if milestone_reached

milestones:
  - 50%: "Halfway there!"
  - 80%: "Almost complete! You can now apply to jobs."
  - 100%: "Profile complete! 🎉"

ddd_integration:
  - aggregate: agg_candidate_profile
  - calculation: based on required vs filled attributes
```

**Implementation**:
```tsx
const ProfileCompleteness: React.FC = () => {
  const {data: profile} = useQuery('candidate-profile');
  const [previousCompleteness, setPreviousCompleteness] = useState(0);

  const completeness = useMemo(() => {
    return calculateCompleteness(profile);  // Domain calculation
  }, [profile]);

  // Animate when completeness changes
  useEffect(() => {
    if (completeness > previousCompleteness) {
      animateProgressBar(previousCompleteness, completeness);

      // Show milestone toast
      if (completeness >= 80 && previousCompleteness < 80) {
        showToast({
          variant: 'success',
          message: 'Your profile is now 80% complete! You can apply to jobs.',
          icon: '🎉'
        });
      }
    }

    setPreviousCompleteness(completeness);
  }, [completeness]);

  return (
    <div>
      <h3>Profile Completeness</h3>
      <ProgressBar
        value={completeness}
        variant={completeness >= 80 ? 'success' : 'warning'}
        showPercentage
        animated
      />
    </div>
  );
};
```

---

## Accessibility Behavior

### Keyboard Interactions

```yaml
keyboard_navigation:
  tab:
    - action: move_to_next_focusable
    - order: dom_order

  shift_tab:
    - action: move_to_previous_focusable

  enter:
    - on_button: trigger_click
    - on_link: navigate

  space:
    - on_button: trigger_click
    - on_checkbox: toggle
    - on_scrollable_area: page_down

  escape:
    - on_modal: close_modal
    - on_dropdown: close_dropdown
    - on_form_with_changes: confirm_discard

  arrow_keys:
    - on_radio_group: select_option
    - on_tabs: switch_tab
    - on_dropdown: navigate_options
```

---

### Screen Reader Announcements

```yaml
aria_live_regions:
  polite:
    - use_for: non_critical_updates
    - examples:
        - search_results_count
        - filter_applied
        - item_added_to_list

  assertive:
    - use_for: critical_updates
    - examples:
        - form_error
        - action_failed
        - session_expiring

  off:
    - use_for: non_important_content
```

**Example**:
```tsx
<div role="status" aria-live="polite" aria-atomic="true">
  {jobsCount > 0 ? (
    `Found ${jobsCount} jobs matching your criteria`
  ) : (
    `No jobs found. Try adjusting your filters.`
  )}
</div>

<div role="alert" aria-live="assertive">
  {error && `Error: ${error.message}`}
</div>
```

---

## Performance Considerations

### Debouncing & Throttling

```yaml
optimization: debounce_throttle

debounce:
  use_for: expensive_operations_after_user_stops_typing
  examples:
    - search_input: debounce(300ms)
    - autosave: debounce(2000ms)
    - resize_events: debounce(150ms)

throttle:
  use_for: limit_execution_rate_during_continuous_action
  examples:
    - scroll_events: throttle(100ms)
    - mousemove: throttle(50ms)
    - window_resize: throttle(100ms)
```

**DDD Example - Search Jobs**:
```tsx
const JobSearch: React.FC = () => {
  const [query, setQuery] = useState('');

  // Debounced search function
  const debouncedSearch = useMemo(
    () =>
      debounce((searchQuery: string) => {
        // Call application service
        searchJobs(searchQuery);  // svc_app_search_jobs
      }, 300),
    []
  );

  const handleChange = (value: string) => {
    setQuery(value);  // Update UI immediately
    debouncedSearch(value);  // Debounced API call
  };

  return (
    <SearchInput
      value={query}
      onChange={handleChange}
      placeholder="Search jobs..."
    />
  );
};
```

---

## Key Takeaways

1. **Feedback is Essential**: Every user action should have immediate visual feedback.

2. **Domain Events Drive UI**: UI responds to domain events (evt_application_submitted, evt_high_match_found) with appropriate behavior.

3. **State Transitions Matter**: Visualize aggregate state changes clearly (DRAFT → SUBMITTED → IN_REVIEW).

4. **Micro-interactions Delight**: Small animations (save heart, progress updates) improve perceived quality.

5. **Error Handling is UX**: Graceful errors, optimistic updates with rollback, clear error messages.

6. **Accessibility is Behavior**: Keyboard navigation, focus management, screen reader announcements are behavioral requirements.

7. **Performance Affects Behavior**: Debounce expensive operations, use skeleton screens, optimize animations.

8. **Consistency Builds Trust**: Same interactions produce same results throughout the application.

---

## References

**Primary Sources**:
- Material Design - Motion and interaction guidelines
- Apple HIG - Animation and feedback patterns
- Nielsen Norman Group - Interaction design research
- Framer Motion - Animation library documentation
- WCAG 2.1 - Keyboard and interaction accessibility

**DDD Integration**:
- `research/ddd/deliverables/ddd-schema-example.yaml` - domain events and aggregate states
- `research/ddd/working-docs/03-tactical-patterns.md` - domain events, aggregate lifecycle
- `research/ux/UX-DDD-INTEGRATION.md` - UI event mapping to domain events
- `research/ux/working-docs/05-component-architecture.md` - component interaction patterns

---

*Document created: 2025-10-04*
*Part of UX Research Phase 7: Behavioral Specifications*

---

## 3. Validation Specifications

### validation_config

**Purpose**: Defines validation rules for form fields and user inputs, ensuring data quality and alignment with domain value object invariants.

**Used in**: `behavior.validation`, `workflow.steps[].validation`

**Schema fields**:
- `timing`: on_blur | on_change | on_submit
- `debounce_ms`: Milliseconds to wait before validating (for on_change)
- `rules`: Array of validation rules
  - `type`: required | pattern | range | custom | ddd_value_object
  - `message`: Error message to display
  - `value_object_ref`: Optional DDD value object for validation

**DDD Grounding:**
```
validation_config.rules[].value_object_ref → ddd:value_object
```

Validation rules map to value object invariants, ensuring UI validation matches domain constraints.

**Example:**
```yaml
behavior:
  id: bhv_email_input
  validation_config:
    timing: on_blur
    debounce_ms: 300
    rules:
      - type: required
        message: "Email address is required"
      - type: pattern
        pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        message: "Please enter a valid email address"
      - type: ddd_value_object
        value_object_ref: "ddd:ValueObject:email_address"
        message: "Email address failed domain validation"
```

**Job Seeker Example:**
```yaml
workflow:
  id: wf_profile_setup
  steps:
    - step_id: step_skills
      validation_config:
        timing: on_submit
        rules:
          - type: required
            message: "At least one skill is required"
          - type: range
            min: 1
            max: 20
            message: "Select between 1 and 20 skills"
          - type: ddd_value_object
            value_object_ref: "ddd:ValueObject:skills"
            message: "Selected skills must match our skill taxonomy"
```

**Benefits of DDD Grounding:**
- **Consistency**: UI validation matches backend domain validation
- **Type Safety**: Validation rules reference domain value objects
- **Single Source of Truth**: Domain invariants defined once, validated everywhere
- **Reduced Errors**: Frontend validation prevents invalid submissions

**When to Use:**
- Form field validation
- Multi-step workflow validation
- Real-time input validation
- Domain-driven validation rules

---

