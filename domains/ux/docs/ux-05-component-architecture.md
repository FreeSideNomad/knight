# Component Architecture

## Overview

Component architecture defines reusable UI building blocks organized by complexity and responsibility. Using atomic design methodology, components range from simple atoms (buttons, inputs) to complex organisms (cards, forms) that compose into templates and pages.

**Integration with DDD**: Components map to domain concepts—input components enforce value object validation rules, display components format value objects, and composite components represent entities or aggregates. Component APIs reflect ubiquitous language.

---

## Atomic Design Methodology

### Hierarchy (Brad Frost)

```
Atoms → Molecules → Organisms → Templates → Pages
```

**Atoms**: Basic building blocks (button, input, label)
**Molecules**: Simple groups of atoms (form field = label + input + error)
**Organisms**: Complex components (job card, navigation bar)
**Templates**: Page layouts without real data
**Pages**: Templates with real content

---

## Component Categories

### 1. Atoms (Primitives)
### 2. Molecules (Simple Composites)
### 3. Organisms (Complex Composites)
### 4. Domain Components (DDD-Specific)
### 5. Layout Components
### 6. Utility Components

---

## 1. Atoms (Primitives)

Basic, indivisible UI elements.

### Button

**Variants**:
```yaml
button_variants:
  - primary: main actions (Apply, Submit, Save)
  - secondary: alternative actions (Cancel, Back)
  - tertiary: subtle actions (Learn More, Dismiss)
  - danger: destructive actions (Delete, Withdraw)
  - ghost: minimal actions (icon buttons)
```

**Props API**:
```typescript
interface ButtonProps {
  variant: 'primary' | 'secondary' | 'tertiary' | 'danger' | 'ghost';
  size: 'small' | 'medium' | 'large';
  disabled?: boolean;
  loading?: boolean;
  icon?: ReactNode;
  iconPosition?: 'left' | 'right';
  onClick: () => void;
  children: ReactNode;
  ariaLabel?: string;
}
```

**Example Usage**:
```tsx
<Button
  variant="primary"
  size="medium"
  onClick={handleApply}
  loading={isSubmitting}
>
  Apply Now
</Button>
```

**DDD Integration**:
```yaml
button_labels_use_domain_language:
  - "Apply Now" (not "Submit Form")
  - "Save Profile" (not "Update")
  - "View Matches" (domain term: matches)
  - "Withdraw Application" (domain verb)
```

---

### Input

**Types**:
```yaml
input_types:
  - text: general text input
  - email: validates email format (vo_email)
  - number: numeric values
  - tel: phone numbers
  - url: URLs
  - password: sensitive data
  - search: search queries
```

**Props API**:
```typescript
interface InputProps {
  type: 'text' | 'email' | 'number' | 'tel' | 'url' | 'password' | 'search';
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  disabled?: boolean;
  required?: boolean;
  error?: string;
  label?: string;
  hint?: string;
  maxLength?: number;
  pattern?: string;  // For VO validation
  ariaDescribedBy?: string;
}
```

**DDD Integration - Value Object Validation**:
```typescript
// Email Input (vo_email)
<Input
  type="email"
  value={email}
  onChange={setEmail}
  pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
  error={emailError}
  label="Email Address"
  required
/>

// Validation from DDD vo_email
const validateEmail = (value: string): string | null => {
  // Validation rules from DDD value object
  if (!value) return "Email is required";
  if (!emailRegex.test(value)) return "Please enter a valid email";
  return null;
};
```

---

### Label

**Props API**:
```typescript
interface LabelProps {
  htmlFor: string;
  required?: boolean;
  children: ReactNode;
}
```

**DDD Integration**:
```tsx
<Label htmlFor="skills" required>
  Skills  {/* Domain term from vo_skills */}
</Label>
```

---

### Badge

**Purpose**: Display status, counts, or categories.

**Variants**:
```yaml
badge_variants:
  - status: success, warning, error, info
  - count: numerical indicators
  - category: skill tags, job types
```

**Props API**:
```typescript
interface BadgeProps {
  variant: 'success' | 'warning' | 'error' | 'info' | 'neutral';
  size?: 'small' | 'medium';
  children: ReactNode;
}
```

**DDD Integration**:
```tsx
// Application Status (aggregate state)
<Badge variant="success">Submitted</Badge>
<Badge variant="warning">Interview Scheduled</Badge>
<Badge variant="error">Rejected</Badge>

// Match Tier (vo_match_tier)
<Badge variant="success">Excellent Match</Badge>
<Badge variant="info">Good Match</Badge>

// Skill Tag (vo_skills)
<Badge variant="neutral">React</Badge>
<Badge variant="neutral">TypeScript</Badge>
```

---

### Icon

**Props API**:
```typescript
interface IconProps {
  name: string;
  size?: number;
  color?: string;
  ariaLabel?: string;
  ariaHidden?: boolean;
}
```

**Icon Library Organization**:
```yaml
icons_by_domain:
  profile:
    - user, user-circle, id-card

  jobs:
    - briefcase, building, map-pin

  applications:
    - document, send, check-circle

  skills:
    - code, tool, award

  actions:
    - edit, delete, save, search, filter
```

---

## 2. Molecules (Simple Composites)

Combinations of atoms forming functional units.

### Form Field

**Composition**: Label + Input + Error Message + Hint

**Props API**:
```typescript
interface FormFieldProps {
  label: string;
  name: string;
  type: InputProps['type'];
  value: string;
  onChange: (value: string) => void;
  error?: string;
  hint?: string;
  required?: boolean;
  disabled?: boolean;
}
```

**Component Structure**:
```tsx
const FormField: React.FC<FormFieldProps> = ({
  label,
  name,
  type,
  value,
  onChange,
  error,
  hint,
  required,
  disabled
}) => {
  const fieldId = `field-${name}`;
  const errorId = `${fieldId}-error`;
  const hintId = `${fieldId}-hint`;

  return (
    <div className="form-field">
      <Label htmlFor={fieldId} required={required}>
        {label}
      </Label>

      {hint && (
        <span id={hintId} className="hint">
          {hint}
        </span>
      )}

      <Input
        id={fieldId}
        type={type}
        value={value}
        onChange={onChange}
        required={required}
        disabled={disabled}
        error={error}
        ariaDescribedBy={error ? errorId : hint ? hintId : undefined}
      />

      {error && (
        <span id={errorId} className="error" role="alert">
          {error}
        </span>
      )}
    </div>
  );
};
```

**DDD Integration**:
```tsx
// Email field with vo_email validation
<FormField
  label="Email Address"  // Domain term
  name="email"
  type="email"
  value={profile.email}
  onChange={(value) => updateProfile({email: value})}
  error={validationErrors.email}  // From vo_email validation
  hint="We'll use this for job alerts"
  required
/>
```

---

### Tag Input

**Purpose**: Multi-select input for collections (skills, tags).

**Composition**: Input + Tag List + Add/Remove Logic

**Props API**:
```typescript
interface TagInputProps {
  label: string;
  value: string[];
  onChange: (tags: string[]) => void;
  placeholder?: string;
  suggestions?: string[];
  maxTags?: number;
  error?: string;
}
```

**DDD Integration - Skills (vo_skills)**:
```tsx
<TagInput
  label="Technical Skills"  // Domain term
  value={profile.skills}  // vo_skills
  onChange={(skills) => updateProfile({skills})}
  suggestions={popularSkills}  // From domain service
  maxTags={20}
  error={validationErrors.skills}
  placeholder="Add a skill (e.g., React, Python)"
/>

// Renders:
// Skills: [React ×] [TypeScript ×] [Node.js ×]
// [Add skill: ____________]
```

**Validation from DDD**:
```typescript
// vo_skills validation rules
const validateSkills = (skills: string[]): string | null => {
  if (skills.length === 0) return "Add at least one skill";
  if (skills.length > 20) return "Maximum 20 skills allowed";
  return null;
};
```

---

### Search Input

**Composition**: Input + Icon + Clear Button

**Props API**:
```typescript
interface SearchInputProps {
  value: string;
  onChange: (value: string) => void;
  onClear: () => void;
  placeholder?: string;
  suggestions?: string[];
  onSearch?: (query: string) => void;
}
```

**Example**:
```tsx
<SearchInput
  value={searchQuery}
  onChange={setSearchQuery}
  onClear={() => setSearchQuery('')}
  placeholder="Search jobs by title, company, or skill"
  onSearch={handleSearch}
/>
```

---

### Progress Bar

**Props API**:
```typescript
interface ProgressBarProps {
  value: number;  // 0-100
  max?: number;
  label?: string;
  variant?: 'default' | 'success' | 'warning' | 'error';
  showPercentage?: boolean;
}
```

**DDD Integration - Profile Completeness**:
```tsx
<ProgressBar
  value={profileCompleteness}  // Calculated from aggregate
  label="Profile Completeness"
  variant={profileCompleteness >= 80 ? 'success' : 'warning'}
  showPercentage
/>
// Renders: Profile Completeness: ████████░░ 85%
```

---

### Match Score Display

**Purpose**: Show job match percentage (vo_match_score).

**Props API**:
```typescript
interface MatchScoreProps {
  score: number;  // 0-100
  tier?: 'excellent' | 'great' | 'good' | 'fair';
  showLabel?: boolean;
  size?: 'small' | 'medium' | 'large';
}
```

**DDD Integration**:
```tsx
<MatchScore
  score={85}  // vo_match_score from bc_matching
  tier="excellent"  // vo_match_tier
  showLabel
  size="medium"
/>
// Renders: Match: 85% ██████████░░ Excellent
```

**Tier Calculation (Domain Logic)**:
```typescript
// From DDD bc_matching domain service
const calculateMatchTier = (score: number): MatchTier => {
  if (score >= 90) return 'excellent';
  if (score >= 75) return 'great';
  if (score >= 60) return 'good';
  return 'fair';
};
```

---

## 3. Organisms (Complex Composites)

Multi-molecule components with significant functionality.

### Job Card

**Purpose**: Display job posting summary in lists.

**Composition**: Multiple molecules + atoms

**Props API**:
```typescript
interface JobCardProps {
  job: JobPosting;  // DDD: agg_job_posting
  matchScore?: number;  // From bc_matching
  onSave?: (jobId: string) => void;
  onApply?: (jobId: string) => void;
  onView?: (jobId: string) => void;
  saved?: boolean;
}
```

**Component Structure**:
```tsx
const JobCard: React.FC<JobCardProps> = ({
  job,
  matchScore,
  onSave,
  onApply,
  onView,
  saved
}) => {
  return (
    <article className="job-card">
      <header>
        <h3>{job.title}</h3>  {/* Entity attribute */}
        <Button variant="ghost" onClick={() => onSave(job.id)}>
          <Icon name={saved ? 'bookmark-filled' : 'bookmark'} />
        </Button>
      </header>

      <div className="job-meta">
        <span>
          <Icon name="building" />
          {job.company.name}  {/* vo_company */}
        </span>
        <span>
          <Icon name="map-pin" />
          {job.location.display()}  {/* vo_location */}
        </span>
        <span>
          <Icon name="currency" />
          {job.salary.display()}  {/* vo_salary */}
        </span>
      </div>

      {matchScore && (
        <MatchScore score={matchScore} tier={calculateTier(matchScore)} />
      )}

      <div className="job-skills">
        {job.skills.slice(0, 5).map(skill => (
          <Badge key={skill} variant="neutral">{skill}</Badge>
        ))}
        {job.skills.length > 5 && <span>+{job.skills.length - 5} more</span>}
      </div>

      <footer>
        <span className="posted-date">Posted {formatDate(job.postedDate)}</span>
        <div className="actions">
          <Button variant="secondary" onClick={() => onView(job.id)}>
            View
          </Button>
          <Button variant="primary" onClick={() => onApply(job.id)}>
            Apply
          </Button>
        </div>
      </footer>
    </article>
  );
};
```

**DDD Mapping**:
```yaml
job_card:
  displays: agg_job_posting
  aggregate_id: job.id

  data_sources:
    primary: bc_job_catalog (agg_job_posting)
    secondary: bc_matching (match_score)

  value_objects_displayed:
    - vo_company (company name)
    - vo_location (location display)
    - vo_salary (salary range)
    - vo_skills (skill tags)
    - vo_match_score (if available)

  actions:
    - save_job: updates candidate.saved_jobs (bc_profile)
    - apply: navigates to application workflow (bc_applications)
    - view: navigates to job detail page
```

---

### Application Card

**Props API**:
```typescript
interface ApplicationCardProps {
  application: Application;  // DDD: agg_application
  onView: (id: string) => void;
  onWithdraw?: (id: string) => void;
}
```

**Component Structure**:
```tsx
const ApplicationCard: React.FC<ApplicationCardProps> = ({
  application,
  onView,
  onWithdraw
}) => {
  const statusVariant = getStatusVariant(application.status);
  const hasAction = application.status === 'INTERVIEWING';

  return (
    <article className="application-card">
      {hasAction && (
        <Banner variant="warning">
          <Icon name="alert" />
          Action Required: Confirm interview time
        </Banner>
      )}

      <header>
        <h3>{application.job.title}</h3>
        <Badge variant={statusVariant}>
          {formatStatus(application.status)}
        </Badge>
      </header>

      <div className="application-meta">
        <span>
          <Icon name="building" />
          {application.job.company}
        </span>
        <span>
          <Icon name="calendar" />
          Applied {formatDate(application.submittedDate)}
        </span>
        <span>
          <Icon name="clock" />
          Updated {formatRelativeDate(application.lastUpdated)}
        </span>
      </div>

      <footer>
        <Button variant="secondary" onClick={() => onView(application.id)}>
          View Details →
        </Button>
        {onWithdraw && application.canWithdraw && (
          <Button variant="tertiary" onClick={() => onWithdraw(application.id)}>
            Withdraw
          </Button>
        )}
      </footer>
    </article>
  );
};
```

**DDD Mapping**:
```yaml
application_card:
  displays: agg_application
  aggregate_id: application.id

  aggregate_state_display:
    status: SUBMITTED | IN_REVIEW | INTERVIEWING | OFFER | REJECTED | WITHDRAWN

  status_badges:
    SUBMITTED: info
    IN_REVIEW: info
    INTERVIEWING: warning
    OFFER: success
    REJECTED: error
    WITHDRAWN: neutral

  conditional_rendering:
    - if status == INTERVIEWING:
        show: action_required_banner
    - if canWithdraw (domain rule):
        show: withdraw_button
```

---

### Navigation Bar

**Props API**:
```typescript
interface NavBarProps {
  currentSection: string;
  user: {
    name: string;
    avatar?: string;
  };
  notifications?: number;
  onNavigate: (section: string) => void;
  onLogout: () => void;
}
```

**DDD Integration - Bounded Contexts as Nav Items**:
```tsx
const navItems = [
  {label: 'Profile', href: '/profile', context: 'bc_profile'},
  {label: 'Jobs', href: '/jobs', context: 'bc_job_catalog'},
  {label: 'Applications', href: '/applications', context: 'bc_applications'},
  {label: 'Career', href: '/career', context: 'bc_skills_analysis'}
];
```

---

### Filter Panel

**Purpose**: Faceted filtering for search/browse pages.

**Props API**:
```typescript
interface FilterPanelProps {
  filters: FilterGroup[];
  activeFilters: FilterState;
  onChange: (filters: FilterState) => void;
  onClear: () => void;
}

interface FilterGroup {
  id: string;
  label: string;
  type: 'checkbox' | 'radio' | 'range' | 'select';
  options: FilterOption[];
  voRef?: string;  // DDD value object reference
}
```

**DDD Integration**:
```tsx
const jobFilters: FilterGroup[] = [
  {
    id: 'location',
    label: 'Location',
    type: 'radio',
    voRef: 'vo_location',  // References DDD value object
    options: [
      {value: 'remote', label: 'Remote'},
      {value: 'hybrid', label: 'Hybrid'},
      {value: 'onsite', label: 'On-site'}
    ]
  },
  {
    id: 'job_type',
    label: 'Job Type',
    type: 'checkbox',
    voRef: 'vo_job_type',
    options: [
      {value: 'full_time', label: 'Full-time'},
      {value: 'part_time', label: 'Part-time'},
      {value: 'contract', label: 'Contract'}
    ]
  },
  {
    id: 'experience_level',
    label: 'Experience',
    type: 'radio',
    voRef: 'vo_experience_level',
    options: [
      {value: 'entry', label: 'Entry'},
      {value: 'mid', label: 'Mid-level'},
      {value: 'senior', label: 'Senior'}
    ]
  }
];

<FilterPanel
  filters={jobFilters}
  activeFilters={currentFilters}
  onChange={handleFilterChange}
  onClear={clearFilters}
/>
```

---

## 4. Domain Components (DDD-Specific)

Components that directly represent domain concepts.

### Skills Display

**Purpose**: Show candidate skills with proficiency.

**Props API**:
```typescript
interface SkillsDisplayProps {
  skills: Skill[];  // vo_skills
  variant: 'tags' | 'list' | 'matrix';
  editable?: boolean;
  onEdit?: (skills: Skill[]) => void;
}

interface Skill {
  name: string;
  proficiency?: 'beginner' | 'intermediate' | 'advanced' | 'expert';
  yearsExperience?: number;
}
```

**Variants**:
```tsx
// Tags variant (simple)
<SkillsDisplay skills={skills} variant="tags" />
// Renders: [React] [TypeScript] [Node.js] [Python]

// List variant (with proficiency)
<SkillsDisplay skills={skills} variant="list" />
// Renders:
// • React - Expert (5 years)
// • TypeScript - Advanced (3 years)
// • Node.js - Intermediate (2 years)

// Matrix variant (compare against requirements)
<SkillsDisplay skills={candidateSkills} variant="matrix" />
```

**DDD Integration**:
```yaml
skills_display:
  value_object: vo_skills
  bounded_context: bc_profile

  validation:
    - at_least_one_skill_required
    - max_20_skills
    - skill_names_from_approved_taxonomy
```

---

### Skills Gap Widget

**Purpose**: Show missing skills for a job.

**Props API**:
```typescript
interface SkillsGapProps {
  candidateSkills: string[];  // vo_skills from bc_profile
  requiredSkills: string[];   // vo_skills from bc_job_catalog
  onImprove?: (skill: string) => void;
}
```

**Component**:
```tsx
const SkillsGap: React.FC<SkillsGapProps> = ({
  candidateSkills,
  requiredSkills,
  onImprove
}) => {
  const matching = requiredSkills.filter(s => candidateSkills.includes(s));
  const missing = requiredSkills.filter(s => !candidateSkills.includes(s));
  const coverage = (matching.length / requiredSkills.length) * 100;

  return (
    <div className="skills-gap">
      <h3>Skills Match</h3>
      <ProgressBar value={coverage} variant={coverage >= 80 ? 'success' : 'warning'} />

      <div className="matching-skills">
        <h4>You have ({matching.length})</h4>
        {matching.map(skill => (
          <Badge key={skill} variant="success">✓ {skill}</Badge>
        ))}
      </div>

      {missing.length > 0 && (
        <div className="missing-skills">
          <h4>Missing ({missing.length})</h4>
          {missing.map(skill => (
            <div key={skill}>
              <Badge variant="neutral">✗ {skill}</Badge>
              {onImprove && (
                <Button variant="tertiary" size="small" onClick={() => onImprove(skill)}>
                  Learn
                </Button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
```

**DDD Mapping**:
```yaml
skills_gap:
  aggregate: agg_skills_gap (bc_skills_analysis)

  data_sources:
    - candidate_skills: vo_skills from agg_candidate_profile
    - required_skills: vo_skills from agg_job_posting

  domain_service: svc_domain_calculate_skills_gap

  domain_logic:
    - coverage = matching_skills / total_required_skills
    - tier = calculate_coverage_tier(coverage)
```

---

### Application Timeline

**Purpose**: Show application status progression.

**Props API**:
```typescript
interface ApplicationTimelineProps {
  application: Application;  // agg_application
  events: DomainEvent[];     // Domain events history
}
```

**Component**:
```tsx
const ApplicationTimeline: React.FC<ApplicationTimelineProps> = ({
  application,
  events
}) => {
  const stages = [
    {key: 'SUBMITTED', label: 'Submitted', icon: 'send'},
    {key: 'IN_REVIEW', label: 'In Review', icon: 'eye'},
    {key: 'INTERVIEWING', label: 'Interview', icon: 'calendar'},
    {key: 'OFFER', label: 'Offer', icon: 'gift'},
    {key: 'ACCEPTED', label: 'Accepted', icon: 'check'}
  ];

  const currentStageIndex = stages.findIndex(s => s.key === application.status);

  return (
    <div className="timeline">
      {stages.map((stage, index) => (
        <div
          key={stage.key}
          className={`timeline-stage ${index <= currentStageIndex ? 'completed' : 'pending'}`}
        >
          <Icon name={stage.icon} />
          <span>{stage.label}</span>
        </div>
      ))}

      <div className="timeline-events">
        <h4>Activity History</h4>
        {events.map(event => (
          <div key={event.id} className="event">
            <time>{formatDate(event.timestamp)}</time>
            <span>{formatEventName(event.type)}</span>
          </div>
        ))}
      </div>
    </div>
  );
};
```

**DDD Integration**:
```yaml
application_timeline:
  aggregate: agg_application
  aggregate_state: application.status

  domain_events_displayed:
    - evt_application_submitted
    - evt_application_viewed
    - evt_interview_scheduled
    - evt_offer_extended
    - evt_offer_accepted

  state_progression:
    SUBMITTED → IN_REVIEW → INTERVIEWING → OFFER → ACCEPTED
```

---

## 5. Layout Components

Structural components for page organization.

### Container

**Props API**:
```typescript
interface ContainerProps {
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
  padding?: boolean;
  children: ReactNode;
}
```

---

### Grid

**Props API**:
```typescript
interface GridProps {
  columns?: number | {sm: number, md: number, lg: number};
  gap?: 'sm' | 'md' | 'lg';
  children: ReactNode;
}
```

**Example**:
```tsx
<Grid columns={{sm: 1, md: 2, lg: 3}} gap="md">
  <JobCard job={job1} />
  <JobCard job={job2} />
  <JobCard job={job3} />
</Grid>
```

---

### Stack

**Props API**:
```typescript
interface StackProps {
  direction?: 'vertical' | 'horizontal';
  spacing?: 'sm' | 'md' | 'lg';
  align?: 'start' | 'center' | 'end';
  children: ReactNode;
}
```

---

### Card

**Props API**:
```typescript
interface CardProps {
  variant?: 'default' | 'outlined' | 'elevated';
  padding?: 'sm' | 'md' | 'lg';
  clickable?: boolean;
  onClick?: () => void;
  children: ReactNode;
}
```

---

## 6. Utility Components

### Modal

**Props API**:
```typescript
interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  size?: 'sm' | 'md' | 'lg';
  children: ReactNode;
  actions?: ReactNode;
}
```

**DDD Example - Confirm Application**:
```tsx
<Modal
  isOpen={showConfirmModal}
  onClose={() => setShowConfirmModal(false)}
  title="Apply to TechCorp?"
  size="md"
  actions={
    <>
      <Button variant="secondary" onClick={() => setShowConfirmModal(false)}>
        Cancel
      </Button>
      <Button variant="primary" onClick={handleSubmitApplication}>
        Submit Application
      </Button>
    </>
  }
>
  <p>Your profile will be sent to TechCorp.</p>
  <SkillsGap
    candidateSkills={profile.skills}
    requiredSkills={job.skills}
  />
</Modal>
```

---

### Toast/Notification

**Props API**:
```typescript
interface ToastProps {
  variant: 'success' | 'error' | 'warning' | 'info';
  message: string;
  action?: {label: string, onClick: () => void};
  duration?: number;
  onDismiss: () => void;
}
```

**DDD Integration - Domain Events as Notifications**:
```tsx
// When evt_application_submitted is published
<Toast
  variant="success"
  message="Application submitted successfully!"
  action={{
    label: "View Application",
    onClick: () => navigate(`/applications/${applicationId}`)
  }}
  duration={5000}
  onDismiss={dismissToast}
/>

// When evt_high_match_found is published
<Toast
  variant="info"
  message="New high-quality job match found!"
  action={{
    label: "View Match",
    onClick: () => navigate(`/jobs/${jobId}`)
  }}
/>
```

---

## Component Naming Conventions

### DDD-Aligned Naming

```yaml
naming_principles:
  - use_domain_terms: "JobCard" not "PostingCard"
  - use_ubiquitous_language: "MatchScore" not "Percentage"
  - context_prefixes_optional: "ProfileSkillsDisplay" when ambiguous

examples:
  good:
    - JobCard (agg_job_posting)
    - ApplicationTimeline (agg_application)
    - SkillsInput (vo_skills)
    - MatchScore (vo_match_score)
    - CandidateProfile (agg_candidate_profile)

  avoid:
    - PostingCard (not domain term)
    - PercentageDisplay (not domain-specific)
    - TagsInput (too generic, use SkillsInput)
```

---

## Component Validation Patterns

### Value Object Validation

```typescript
// Component receives VO validation rules
interface SkillsInputProps {
  value: string[];
  onChange: (skills: string[]) => void;
  validation: SkillsValidationRules;  // From DDD vo_skills
  error?: string;
}

// Validation rules from DDD
interface SkillsValidationRules {
  minSkills: number;
  maxSkills: number;
  allowedSkills?: string[];  // Taxonomy
}

const SkillsInput: React.FC<SkillsInputProps> = ({
  value,
  onChange,
  validation,
  error
}) => {
  const handleAdd = (skill: string) => {
    if (value.length >= validation.maxSkills) {
      setError(`Maximum ${validation.maxSkills} skills allowed`);
      return;
    }

    if (validation.allowedSkills && !validation.allowedSkills.includes(skill)) {
      setError(`${skill} is not a recognized skill`);
      return;
    }

    onChange([...value, skill]);
  };

  // ... rest of component
};
```

---

## Component State Management

### Local State vs Domain State

```typescript
// Local UI state (not domain state)
const [isExpanded, setIsExpanded] = useState(false);
const [showDropdown, setShowDropdown] = useState(false);

// Domain state (from aggregate)
const [application, setApplication] = useState<Application>({
  status: 'DRAFT',  // Aggregate state
  // ... other aggregate attributes
});

// Updating domain state triggers domain logic
const handleSubmit = async () => {
  // Invokes application service
  const result = await submitApplication(application);

  // Updates aggregate state
  setApplication({...application, status: 'SUBMITTED'});

  // Domain event published: evt_application_submitted
};
```

---

## Accessibility Requirements

### ARIA Patterns

```tsx
// Form Field
<div role="group" aria-labelledby="skills-label">
  <label id="skills-label">Skills</label>
  <input
    aria-describedby="skills-hint skills-error"
    aria-invalid={!!error}
    aria-required="true"
  />
  <span id="skills-hint">Add your technical skills</span>
  {error && (
    <span id="skills-error" role="alert">
      {error}
    </span>
  )}
</div>

// Button with loading state
<button
  disabled={isLoading}
  aria-busy={isLoading}
  aria-label={isLoading ? "Submitting application..." : "Submit application"}
>
  {isLoading ? "Submitting..." : "Submit"}
</button>

// Modal
<div
  role="dialog"
  aria-modal="true"
  aria-labelledby="modal-title"
  aria-describedby="modal-description"
>
  <h2 id="modal-title">Confirm Application</h2>
  <div id="modal-description">...</div>
</div>
```

---

## Component Testing Strategy

### Testing DDD Components

```typescript
describe('SkillsInput', () => {
  it('enforces vo_skills validation rules', () => {
    const validation = {
      minSkills: 1,
      maxSkills: 20
    };

    const {getByRole, getByText} = render(
      <SkillsInput
        value={Array(20).fill('Skill')}
        onChange={jest.fn()}
        validation={validation}
      />
    );

    // Try to add 21st skill
    const input = getByRole('textbox');
    fireEvent.change(input, {target: {value: 'NewSkill'}});
    fireEvent.click(getByText('Add'));

    // Should show error from VO validation
    expect(getByText('Maximum 20 skills allowed')).toBeInTheDocument();
  });

  it('displays skills from vo_skills', () => {
    const skills = ['React', 'TypeScript', 'Node.js'];

    const {getByText} = render(
      <SkillsInput value={skills} onChange={jest.fn()} />
    );

    skills.forEach(skill => {
      expect(getByText(skill)).toBeInTheDocument();
    });
  });
});

describe('JobCard', () => {
  it('displays aggregate attributes correctly', () => {
    const job: JobPosting = {
      id: '123',
      title: 'Frontend Developer',
      company: {name: 'TechCorp'},
      location: {type: 'remote', display: () => 'Remote'},
      salary: {min: 100000, max: 120000, display: () => '$100-120k'},
      skills: ['React', 'TypeScript']
    };

    const {getByText} = render(<JobCard job={job} />);

    expect(getByText('Frontend Developer')).toBeInTheDocument();
    expect(getByText('TechCorp')).toBeInTheDocument();
    expect(getByText('Remote')).toBeInTheDocument();
    expect(getByText('$100-120k')).toBeInTheDocument();
  });
});
```

---

## Performance Optimization

### Memoization

```typescript
// Memoize expensive domain calculations
const MatchScore: React.FC<MatchScoreProps> = React.memo(({score, tier}) => {
  // Component logic
}, (prevProps, nextProps) => {
  return prevProps.score === nextProps.score;
});

// Memoize domain service calls
const memoizedMatchCalculation = useMemo(() => {
  return calculateMatchScore(candidateSkills, jobRequirements);
}, [candidateSkills, jobRequirements]);
```

### Virtual Scrolling for Large Lists

```typescript
// For large collections (e.g., 1000+ jobs)
import {FixedSizeList} from 'react-window';

const JobsList: React.FC<{jobs: JobPosting[]}> = ({jobs}) => {
  return (
    <FixedSizeList
      height={600}
      itemCount={jobs.length}
      itemSize={200}
      width="100%"
    >
      {({index, style}) => (
        <div style={style}>
          <JobCard job={jobs[index]} />
        </div>
      )}
    </FixedSizeList>
  );
};
```

---

## Key Takeaways

1. **Atomic Design**: Organize components from simple atoms to complex organisms, templates, and pages.

2. **Domain Alignment**: Components represent domain concepts—input components enforce VO validation, display components format VOs, cards display aggregates.

3. **Props Mirror VOs**: Component props align with value object structures and validation rules.

4. **Domain Language**: Component names, props, and labels use ubiquitous language from DDD.

5. **Validation Integration**: Client-side validation mirrors VO validation rules from domain layer.

6. **State Separation**: Distinguish UI state (local) from domain state (aggregate state).

7. **Reusability**: Build reusable components with domain-agnostic atoms/molecules, domain-specific organisms.

8. **Accessibility**: ARIA patterns, semantic HTML, keyboard navigation are requirements, not nice-to-haves.

9. **Testing**: Test components against DDD validation rules and aggregate behavior.

10. **Performance**: Memoize domain calculations, virtual scrolling for large aggregate collections.

---

## References

**Primary Sources**:
- Frost, Brad (2016). "Atomic Design" - https://atomicdesign.bradfrost.com/
- Material Design - Component documentation
- Radix UI - Accessible component primitives
- Ant Design - Enterprise component library
- Chakra UI - Composable component system

**DDD Integration**:
- `research/ddd/deliverables/ddd-schema-example.yaml` - value objects and aggregates
- `research/ddd/working-docs/03-tactical-patterns.md` - value object patterns
- `research/ux/UX-DDD-INTEGRATION.md` - component-to-VO mapping
- `research/ux/working-docs/04-page-architecture.md` - component composition into pages

---

*Document created: 2025-10-04*
*Part of UX Research Phase 6: Component Architecture*

---

## 4. Responsive Design

### responsive_config

**Purpose**: Configuration for responsive behavior across different device breakpoints.

**Used in**: `component.responsive_config`, `page.responsive_config`, `page_section.responsive_config`

**Schema fields**:
- `mobile`: Configuration for mobile devices (< 768px)
- `tablet`: Configuration for tablet devices (768px - 1024px)
- `desktop`: Configuration for desktop devices (> 1024px)
- `breakpoints`: Custom breakpoint definitions

**Example**:
```yaml
component:
  id: comp_product_card
  responsive_config:
    mobile:
      columns: 1
      font_size: 14px
      image_size: small
    tablet:
      columns: 2
      font_size: 16px
      image_size: medium
    desktop:
      columns: 4
      font_size: 16px
      image_size: large
```

---

## 5. Design System

### design_tokens

**Purpose**: Atomic design values (colors, spacing, typography) used consistently across the application.

**Used in**: Component styling, theme configuration

**Schema fields**:
- `colors`: Color palette (primary, secondary, semantic colors)
- `spacing`: Spacing scale (margins, padding)
- `typography`: Font families, sizes, weights
- `breakpoints`: Responsive breakpoints

**Example**:
```yaml
design_tokens:
  colors:
    primary: "#007bff"
    secondary: "#6c757d"
    success: "#28a745"
    error: "#dc3545"
  spacing:
    xs: 4px
    sm: 8px
    md: 16px
    lg: 24px
    xl: 32px
  typography:
    font_family: "Inter, sans-serif"
    sizes:
      small: 12px
      base: 14px
      large: 16px
      h1: 32px
      h2: 24px
  breakpoints:
    mobile: 768px
    tablet: 1024px
    desktop: 1440px
```

---

## 6. Page Structure

### page_section

**Purpose**: Logical sections within a page (header, main content, sidebar, footer).

**Used in**: `page.sections`

**Schema fields**:
- `section_id`: Unique identifier
- `type`: header | main | sidebar | footer | nav
- `components`: Array of component references
- `layout`: Grid or flex layout configuration

**Example**:
```yaml
page:
  id: page_product_detail
  sections:
    - page_section:
        section_id: sec_header
        type: header
        components: [comp_nav, comp_breadcrumb]
    - page_section:
        section_id: sec_main
        type: main
        components: [comp_product_gallery, comp_product_info, comp_add_to_cart]
    - page_section:
        section_id: sec_sidebar
        type: sidebar
        components: [comp_recommendations, comp_reviews_summary]
```

---

## 7. Data Presentation

### pagination_config

**Purpose**: Configuration for paginated data display.

**Used in**: `component.pagination`, `page.pagination`

**Schema fields**:
- `page_size`: Items per page
- `total_items`: Total number of items
- `current_page`: Active page number
- `show_page_numbers`: Boolean
- `show_prev_next`: Boolean

**Example**:
```yaml
component:
  id: comp_product_list
  pagination_config:
    page_size: 20
    total_items: 500
    current_page: 1
    show_page_numbers: true
    show_prev_next: true
    max_visible_pages: 5
```

### caching_config

**Purpose**: Client-side caching strategy for performance optimization.

**Used in**: `page.caching`, `component.caching`

**Schema fields**:
- `strategy`: cache_first | network_first | stale_while_revalidate
- `ttl`: Time-to-live in seconds
- `invalidation_keys`: Keys that trigger cache invalidation

**Example**:
```yaml
page:
  id: page_product_catalog
  caching_config:
    strategy: stale_while_revalidate
    ttl: 300
    invalidation_keys: [products_updated, inventory_changed]
```

### accessibility_spec

**Purpose**: Accessibility requirements and WCAG compliance specifications.

**Used in**: `component.accessibility`, `page.accessibility`

**Schema fields**:
- `wcag_level`: A | AA | AAA
- `aria_labels`: Required ARIA attributes
- `keyboard_navigation`: Keyboard shortcuts and tab order
- `screen_reader_text`: Alternative text for screen readers

**Example**:
```yaml
component:
  id: comp_modal_dialog
  accessibility_spec:
    wcag_level: AA
    aria_labels:
      role: dialog
      aria_labelledby: modal_title
      aria_describedby: modal_description
    keyboard_navigation:
      esc_closes: true
      trap_focus: true
      focus_first_element: true
    screen_reader_text: "Modal dialog opened. Press Escape to close."
```

