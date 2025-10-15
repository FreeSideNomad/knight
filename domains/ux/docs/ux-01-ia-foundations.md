# Information Architecture Foundations

## Overview

Information Architecture (IA) is the structural design of shared information environments. It involves organizing, structuring, and labeling content to support usability and findability. IA creates the blueprint for how users navigate, search, and understand information within a system.

**Integration with DDD**: This IA framework aligns with the DDD bounded contexts defined in `research/ddd/deliverables/ddd-schema-example.yaml`. Navigation and organization structures will map to domain boundaries, and labeling will use the ubiquitous language from DDD contexts.

---

## Core IA Components

### 1. Organization Systems

Organization systems determine how information is grouped and categorized.

#### Hierarchical (Tree) Structure

**Definition**: Information organized in parent-child relationships, from general to specific.

**Characteristics**:
- Clear hierarchy of importance
- Familiar mental model (like file systems)
- Supports browsing and drilling down
- Works well for predictable content

**Job Seeker Application Example**:
```
Job Seeker (Root)
├── Profile Management (bc_profile)
│   ├── Personal Information
│   ├── Skills & Expertise
│   ├── Work History
│   └── Preferences
├── Job Search (bc_job_catalog, bc_matching)
│   ├── Browse Jobs
│   ├── My Matches
│   ├── Saved Jobs
│   └── Search Results
├── Applications (bc_applications)
│   ├── Active Applications
│   ├── Interviews Scheduled
│   └── Application History
└── Career Development (bc_skills_analysis, bc_project_recommendations)
    ├── Skills Gap Analysis
    ├── Recommended Projects
    └── Learning Paths
```

**DDD Integration**: Top-level categories map to DDD bounded contexts, preserving domain boundaries.

---

#### Sequential (Linear) Structure

**Definition**: Information organized in a specific order, with a defined beginning, middle, and end.

**Characteristics**:
- Step-by-step progression
- Enforces specific order
- Common in processes and workflows
- Reduces cognitive load for complex tasks

**Job Seeker Application Example**:
```
Profile Setup Wizard:
1. Welcome & Account Creation
2. Basic Information (Name, Email, Location)
3. Work Experience
4. Skills & Expertise
5. Job Preferences
6. Review & Submit
```

**DDD Integration**: Sequential workflows map to DDD application service use cases (e.g., `svc_app_update_profile`).

---

#### Faceted Classification

**Definition**: Multiple independent classification schemes applied simultaneously, allowing filtering by different attributes.

**Characteristics**:
- Supports multiple access paths
- Flexible and powerful for complex content
- Allows user-driven exploration
- Requires well-defined taxonomy

**Job Seeker Application Example**:
```
Job Search Facets:
├── Location (DDD: vo_location)
│   ├── Remote
│   ├── Hybrid
│   └── On-site (by city)
├── Job Type (DDD: vo_job_type)
│   ├── Full-time
│   ├── Part-time
│   ├── Contract
│   └── Freelance
├── Experience Level (DDD: vo_experience_level)
│   ├── Entry
│   ├── Mid
│   ├── Senior
│   └── Lead/Principal
├── Skills Required (DDD: vo_skills)
│   ├── Programming Languages
│   ├── Frameworks
│   └── Tools
├── Company Size (DDD: vo_company_info)
│   ├── Startup (1-50)
│   ├── Small (51-200)
│   ├── Medium (201-1000)
│   └── Enterprise (1000+)
└── Match Score (DDD: vo_match_score)
    ├── Excellent (90-100%)
    ├── Great (75-89%)
    ├── Good (60-74%)
    └── Fair (50-59%)
```

**DDD Integration**: Each facet references a DDD value object, ensuring validation and business logic consistency.

---

#### Database/Matrix Organization

**Definition**: Content organized in multiple dimensions, allowing cross-referencing and complex queries.

**Characteristics**:
- Multiple access dimensions
- Supports complex relationships
- Good for research and comparison
- Requires robust search/filter capabilities

**Job Seeker Application Example**:
```
Skills Matrix:
          | JavaScript | Python | Java   | Go     | Rust
----------|------------|--------|--------|--------|------
Frontend  | React      | -      | -      | -      | -
Backend   | Node.js    | Django | Spring | Gin    | Actix
Mobile    | RN         | -      | Android| -      | -
DevOps    | -          | Ansible| -      | Docker | -
```

**DDD Integration**: Matrix data sourced from DDD aggregates (`agg_candidate_profile`, `agg_job_posting`).

---

### 2. Labeling Systems

Labeling systems define how we represent information to users.

#### Principles of Effective Labels

1. **Use Domain Language** (DDD Integration)
   - Labels must use terms from DDD ubiquitous language
   - Example: "Candidate Profile" not "User Profile"
   - Example: "Job Match" not "Job Recommendation"

2. **Be Consistent**
   - Same term for same concept everywhere
   - DDD glossaries ensure consistency

3. **Be Clear and Concise**
   - Avoid jargon unless domain-specific
   - Example: "Skills" not "Competency Inventory"

4. **Context-Sensitive**
   - Labels change based on user context
   - Example: "My Profile" (user context) vs "Candidate Profile" (admin context)

#### Label Types

**Contextual Links**:
```
"View full profile" - action-oriented
"Edit skills" - verb + noun (DDD term)
"See matching jobs" - clear outcome
```

**Headings**:
```
"Your Job Matches" - personal, clear
"Application Status" - domain term
"Skills Gap Analysis" - DDD bounded context name
```

**Navigation Labels**:
```
Main Nav: Profile | Jobs | Applications | Career
Matches DDD contexts: bc_profile | bc_job_catalog, bc_matching | bc_applications | bc_skills_analysis
```

**Index Terms/Tags**:
```
Skills: "React", "TypeScript", "Node.js" (DDD: vo_skills)
Job Types: "Remote", "Full-time" (DDD: vo_job_type)
```

#### DDD Terminology Mapping

| UI Label             | DDD Source                 | Context             |
|----------------------|----------------------------|---------------------|
| "Profile"            | bc_profile                 | Navigation          |
| "Candidate"          | ent_candidate              | Entity reference    |
| "Skills"             | vo_skills                  | Value object        |
| "Job Match"          | agg_job_match              | Aggregate           |
| "Match Score"        | vo_match_score             | Value object        |
| "Submit Application" | svc_app_submit_application | Application service |
| "High Match Found"   | evt_high_match_found       | Domain event        |

---

### 2.3 Hierarchical Navigation

#### hierarchy_node

A **hierarchy_node** represents a node in a tree-structured information architecture. Each node defines a location in the navigation hierarchy with its own identity, label, and optional bounded context alignment.

**Schema Properties:**
- `id`: Unique node identifier (format: `node_<name>`)
- `label`: Display text shown to users
- `bounded_context_ref`: Optional DDD context alignment (grounding)
- `url`: Navigation target URL
- `children`: Array of child node references
- `parent`: Reference to parent node (optional)
- `level`: Depth in hierarchy (1 = top level)

**Example:**
```yaml
hierarchy_node:
  id: node_products
  label: Products
  bounded_context_ref: ddd:BoundedContext:product-catalog
  url: /products
  level: 1
  children:
    - node_electronics
    - node_clothing
    - node_home

hierarchy_node:
  id: node_electronics
  label: Electronics
  bounded_context_ref: ddd:BoundedContext:product-catalog
  url: /products/electronics
  level: 2
  parent: node_products
  children:
    - node_laptops
    - node_phones
    - node_accessories
```

**DDD Grounding:**
```
hierarchy_node.bounded_context_ref → ddd:bounded_context
```

Hierarchy nodes map to bounded context boundaries, ensuring navigation structure aligns with domain boundaries. This enables:
- **Domain-aligned IA**: Navigation mirrors domain model
- **Clear ownership**: Each nav section has a domain owner
- **Consistent language**: Labels use ubiquitous language from DDD
- **Scalable structure**: Hierarchy grows with domain boundaries

**When to Use:**
- Multi-level navigation structures
- Category/subcategory organization
- Tree-based browsing experiences
- Domain-aligned information architecture

---

### 2.4 Faceted Classification

#### facet_value

**facet_value** instances represent individual filter values within a facet. Each value includes display metadata and optional domain grounding.

**Schema Properties:**
- `value_id`: Unique identifier for this facet value
- `label`: Display text shown to users
- `count`: Number of items matching this value (for display)
- `ddd_value_object_ref`: Optional grounding to DDD value object
- `selected`: Boolean indicating if currently selected
- `disabled`: Boolean indicating if unavailable in current context

**Example Facet with Values:**
```yaml
facet:
  facet_id: facet_brand
  label: Brand
  type: multiselect
  ddd_value_object_refs:
    - ddd:ValueObject:product_brand
  values:
    - facet_value:
        value_id: brand_apple
        label: Apple
        count: 245
        ddd_value_object_ref: ddd:ValueObject:product_brand:apple
        selected: false
    - facet_value:
        value_id: brand_dell
        label: Dell
        count: 189
        ddd_value_object_ref: ddd:ValueObject:product_brand:dell
        selected: true
    - facet_value:
        value_id: brand_hp
        label: HP
        count: 156
        ddd_value_object_ref: ddd:ValueObject:product_brand:hp
        selected: false
```

**DDD Grounding:**
```
facet.ddd_value_object_refs → ddd:value_object
facet_value.ddd_value_object_ref → ddd:value_object
```

Facet values map to domain value objects, ensuring:
- **Validated options**: Filter values match domain constraints
- **Business rules**: Value availability follows domain logic
- **Consistent terminology**: Labels use ubiquitous language
- **Type safety**: Values are validated against domain types

**Job Seeker Example:**
```yaml
facet:
  facet_id: facet_experience
  label: Experience Level
  type: single_select
  values:
    - facet_value:
        value_id: exp_entry
        label: Entry Level (0-2 years)
        count: 342
        ddd_value_object_ref: ddd:ValueObject:experience_level:entry
    - facet_value:
        value_id: exp_mid
        label: Mid Level (3-5 years)
        count: 567
        ddd_value_object_ref: ddd:ValueObject:experience_level:mid
    - facet_value:
        value_id: exp_senior
        label: Senior (6+ years)
        count: 234
        ddd_value_object_ref: ddd:ValueObject:experience_level:senior
```

**When to Use:**
- Search and filter interfaces
- Product catalog faceted navigation
- Multi-dimensional data exploration
- User-driven content discovery

---

### 3. Navigation Systems

Navigation systems help users understand where they are, where they've been, and where they can go.

#### Global Navigation

**Definition**: Persistent navigation available throughout the application.

**Purpose**:
- Provides access to primary sections
- Establishes context
- Supports wayfinding

**Job Seeker Application Example**:
```
Top Navigation Bar:
┌─────────────────────────────────────────────────────────┐
│ [Logo] Profile | Jobs | Applications | Career | [User▼] │
└─────────────────────────────────────────────────────────┘

Maps to DDD Contexts:
- Profile → bc_profile
- Jobs → bc_job_catalog, bc_matching
- Applications → bc_applications
- Career → bc_skills_analysis, bc_project_recommendations
```

**Design Principles**:
- Limit to 5-7 primary items (Miller's Law)
- Use DDD bounded context names
- Maintain consistent order
- Highlight current section

---

#### Local Navigation

**Definition**: Navigation within a specific section or bounded context.

**Job Seeker Application Example**:
```
Profile Section (bc_profile):
├── Overview
├── Personal Info
├── Skills & Expertise
├── Work History
├── Education
├── Preferences
└── Settings

Jobs Section (bc_job_catalog, bc_matching):
├── Browse All Jobs
├── My Matches (filtered by match score)
├── Saved Jobs
├── Recent Searches
└── Job Alerts
```

**DDD Integration**: Local navigation reflects aggregate structure within the bounded context.

---

#### Contextual Navigation

**Definition**: Links embedded within content, leading to related information.

**Types**:

1. **Inline Links**:
   ```
   "Your profile is 80% complete. Add work history to improve match accuracy."
   [work history] → bc_profile/work_history
   [match accuracy] → help/matching_algorithm
   ```

2. **Related Items**:
   ```
   Job Posting Detail:
   Related:
   - Similar jobs (same skills)
   - Jobs from this company
   - Jobs with similar match scores
   ```

3. **Breadcrumbs**:
   ```
   Home > Jobs > Search Results > Frontend Developer at TechCorp

   Maps to:
   Root > bc_job_catalog > search_results > job_posting_detail
   ```

4. **Cross-Context Links**:
   ```
   Job Match Detail:
   "Your skills match 85% of requirements. View skills gap analysis."

   Links from: bc_matching (Job Match)
   Links to: bc_skills_analysis (Skills Gap Analysis)

   DDD: Cross-bounded-context navigation via context mapping (customer/supplier relationship)
   ```

---

#### Navigation Patterns

**Hub and Spoke**:
```
Dashboard (Hub) → Detailed Views (Spokes)

Dashboard shows:
- Match summary → bc_matching
- Recent applications → bc_applications
- Skills progress → bc_skills_analysis
- Profile completeness → bc_profile

Each spoke returns to dashboard
```

**Stepped Navigation (Wizard)**:
```
Application Submission (bc_applications):
Step 1: Review Job → Step 2: Review Profile → Step 3: Cover Letter → Step 4: Submit

Maps to DDD workflow: svc_app_submit_application
```

**Faceted Navigation**:
```
Job Search (bc_job_catalog):
Filters (left sidebar) ⇄ Results (main) ⇄ Detail (overlay/page)

Facets reference DDD value objects (vo_location, vo_skills, etc.)
```

---

### 4. Search Systems

Search helps users find specific information when browsing isn't efficient.

#### Search Types

**Simple Search**:
```
Job Search:
┌────────────────────────────────────┐
│ 🔍 Search jobs...                  │
└────────────────────────────────────┘

Searches: job title, company, skills
```

**Advanced Search**:
```
┌─────────────────────────────────────────┐
│ Keywords: [_____________________]       │
│ Location: [_____________________]       │
│ Skills:   [_____________________]       │
│ Job Type: [ ] Remote [ ] Hybrid [ ] On-site │
│ Experience: [Entry ▼]                   │
│                                         │
│ [Search] [Reset]                        │
└─────────────────────────────────────────┘

Maps to DDD value objects for validation
```

**Faceted Search**:
```
Search + Filters (combined):
Results update dynamically as filters applied
Facets = DDD value objects (as defined in faceted classification)
```

#### Search Features

**Autocomplete/Suggestions**:
```
User types: "reac"
Suggestions:
- React (skill)
- React Native (skill)
- React Developer (job title)
- Reactive Programming (skill)

Source: DDD vo_skills, aggregated job titles
```

**Scoped Search**:
```
Search within context:
- Search My Applications (bc_applications only)
- Search Jobs (bc_job_catalog only)
- Search Profile (bc_profile only)

DDD: Search scoped to bounded context
```

**Search Results Display**:
```
Jobs matching "React Developer":

[Job Title] - [Company]
Match Score: 85% (DDD: vo_match_score)
Location: Remote (DDD: vo_location)
Skills: React, TypeScript, Node.js (DDD: vo_skills)
Posted: 2 days ago

Sorting options:
- Best Match (match score desc)
- Newest
- Company A-Z
- Location
```

**No Results Handling**:
```
No jobs found for "obscure-framework"

Suggestions:
- Check spelling
- Try broader terms: "JavaScript" instead of "obscure-framework"
- Expand location filter
- Lower experience level requirement

DDD: Use vo_skills taxonomy to suggest related skills
```

---

### 5. Mental Models

Understanding user mental models helps create intuitive IA.

#### User Mental Model vs System Model

**User Mental Model** (Job Seeker):
```
"I want to find jobs that match my skills"
"I need to track my applications"
"I should improve skills where I'm weak"
```

**System Model** (DDD):
```
bc_matching: Calculate match scores based on candidate profile and job requirements
bc_applications: Track application lifecycle and state
bc_skills_analysis: Compare candidate skills vs market demand
```

**IA Bridge**: Navigation and labeling translate system model into user mental model
- "My Matches" instead of "Match Calculation Results"
- "Applications" instead of "Application State Tracker"
- "Skills Gap" instead of "Skills Analysis Delta"

#### Progressive Disclosure

Reveal complexity gradually:

**Level 1** (Simple):
```
Dashboard:
- You have 12 new job matches
- 3 applications in progress
- Skills: 85% market-ready
```

**Level 2** (Intermediate):
```
Job Matches (click "12 new job matches"):
- Filter by match score
- View by date posted
- See all facets
```

**Level 3** (Advanced):
```
Match Detail (click specific job):
- Detailed match breakdown
- Skills coverage analysis
- Company insights
- Application workflow
```

**DDD Integration**: Each level reveals more aggregate detail, from summary to full entity/VO attributes.

---

## IA Design Process

### 1. Content Inventory

**Task**: Catalog all content types in the system.

**Job Seeker Application**:
```
DDD Aggregates as Content Types:
- Candidate Profiles (agg_candidate_profile)
- Job Postings (agg_job_posting)
- Job Matches (agg_job_match)
- Applications (agg_application)
- Skills Gaps (agg_skills_gap)
- Projects (agg_project)
- Requirements Analysis (agg_requirements_analysis)

Each aggregate becomes a content type with specific attributes (entities, VOs)
```

### 2. Content Audit

**Task**: Evaluate existing content for quality, relevance, and gaps.

**Evaluation Criteria**:
- Accuracy (validated by DDD value object rules)
- Completeness (all required entity attributes present)
- Consistency (ubiquitous language used)
- Redundancy (aggregates don't duplicate)

### 3. User Research

**Task**: Understand user needs, goals, and behaviors.

**Methods**:
- User interviews (discover mental models)
- Card sorting (validate organization schemes)
- Tree testing (validate hierarchy)
- Task analysis (map to DDD use cases)

**Example**:
```
User Goal: "Find remote jobs matching my React skills"

Maps to:
1. Navigate to Jobs (bc_job_catalog)
2. Apply filters (vo_location: Remote, vo_skills: React)
3. View results sorted by match score (vo_match_score)
4. Access job detail

DDD Services Involved:
- svc_domain_calculate_match (domain service)
- svc_app_search_jobs (application service)
```

### 4. Define IA Structure

**Hierarchical Outline**:
```
Job Seeker Application
│
├── Profile (bc_profile)
│   ├── Overview (aggregate summary)
│   ├── Personal Info (entities: Candidate)
│   ├── Skills (VOs: vo_skills, vo_experience_level)
│   ├── Work History (entities: WorkExperience)
│   └── Preferences (VOs: vo_job_preferences)
│
├── Jobs (bc_job_catalog, bc_matching)
│   ├── Browse (all job postings)
│   ├── Matches (filtered by match score)
│   │   ├── Excellent Matches (90%+)
│   │   ├── Great Matches (75-89%)
│   │   └── Good Matches (60-74%)
│   ├── Saved (user-flagged jobs)
│   └── Search (faceted search)
│
├── Applications (bc_applications)
│   ├── Active (status: SUBMITTED, IN_REVIEW, INTERVIEWING)
│   ├── Scheduled Interviews
│   ├── Offers
│   └── Archive (REJECTED, WITHDRAWN, ACCEPTED)
│
└── Career (bc_skills_analysis, bc_project_recommendations)
    ├── Skills Gap (agg_skills_gap)
    ├── Recommended Projects (agg_project)
    └── Learning Resources
```

**DDD Alignment**: Each section maps to bounded contexts, subsections to aggregates, pages to entities/VOs.

### 5. Validate and Iterate

**Methods**:
- Tree testing: Can users find "Skills Gap Analysis"?
- Card sorting: Do users group "Job Matches" with "Browse Jobs"?
- First-click testing: Where do users click to "Submit Application"?
- Navigation audit: Are DDD context boundaries clear?

---

## Information Architecture Patterns

### Pattern 1: Entity-Focused IA

**Structure**: Organize around DDD entities and aggregates.

```
Candidate Profile (agg_candidate_profile)
├── Candidate Info (ent_candidate)
├── Skills (vo_skills)
├── Work History (ent_work_experience)
└── Education (ent_education)
```

**When to Use**: When entities are the primary user focus.

---

### Pattern 2: Task-Focused IA

**Structure**: Organize around user tasks (DDD use cases).

```
Find Jobs
├── Search for Jobs (svc_app_search_jobs)
├── Browse Recommendations (svc_app_calculate_matches)
└── Save Jobs (svc_app_save_job)

Apply to Jobs
├── Review Job (read aggregate)
├── Prepare Application (svc_app_prepare_application)
└── Submit Application (svc_app_submit_application)
```

**When to Use**: When users have clear task goals.

---

### Pattern 3: Workflow-Focused IA

**Structure**: Organize around processes (DDD workflows).

```
Application Process
├── Pre-Application
│   ├── Find Job
│   ├── Assess Match
│   └── Prepare Materials
├── Application
│   ├── Fill Form
│   ├── Attach Documents
│   └── Submit
└── Post-Application
    ├── Track Status
    ├── Prepare for Interview
    └── Accept/Decline Offer
```

**When to Use**: When linear processes dominate user experience.

---

### Pattern 4: Hybrid IA

**Structure**: Combine approaches based on context.

```
Global Nav (Entity): Profile | Jobs | Applications | Career

Within Jobs (Task + Faceted):
├── Browse (task: explore)
├── Search (task: find specific)
└── Matches (entity: agg_job_match)

Within Applications (Workflow):
Draft → Submitted → In Review → Interview → Offer
```

**When to Use**: Complex applications with diverse user needs (recommended for Job Seeker app).

---

## IA and DDD Bounded Contexts

### Mapping Strategy

**One-to-One Mapping** (Preferred):
```
Navigation Section = Bounded Context
"Profile" → bc_profile
"Jobs" → bc_job_catalog + bc_matching (related contexts)
"Applications" → bc_applications
"Career" → bc_skills_analysis + bc_project_recommendations
```

**Benefits**:
- Clear mental model alignment
- Respects domain boundaries
- Easier context switching
- Consistent terminology

**When Contexts Must Combine**:
```
"Jobs" section combines:
- bc_job_catalog (browse/search jobs)
- bc_matching (view matches)

Reason: User mental model sees these as one task ("find jobs")
DDD: Contexts have customer/supplier relationship, safe to combine in UI
```

**When Contexts Must Separate**:
```
bc_scrapers (job aggregation from external sources)
↓
Should NOT appear in main navigation

Reason: Internal/admin context, not user-facing
DDD: Generic subdomain, supporting infrastructure
```

---

## Accessibility in IA

### WCAG Principles Applied to IA

**Perceivable**:
- Clear headings (semantic HTML)
- Descriptive link text (not "click here")
- Breadcrumbs for orientation

**Operable**:
- Keyboard navigation through all IA elements
- Skip links to main content/navigation
- Consistent navigation order

**Understandable**:
- Clear labels (DDD ubiquitous language)
- Predictable navigation behavior
- Consistent terminology

**Robust**:
- Semantic HTML (nav, main, aside)
- ARIA landmarks
- Valid markup

### Example: Accessible Navigation

```html
<nav aria-label="Main navigation">
  <ul>
    <li><a href="/profile" aria-current="page">Profile</a></li>
    <li><a href="/jobs">Jobs</a></li>
    <li><a href="/applications">Applications</a></li>
    <li><a href="/career">Career</a></li>
  </ul>
</nav>

<nav aria-label="Breadcrumb">
  <ol>
    <li><a href="/">Home</a></li>
    <li><a href="/jobs">Jobs</a></li>
    <li aria-current="page">Frontend Developer at TechCorp</li>
  </ol>
</nav>
```

**DDD Integration**: ARIA labels use domain terminology for clarity.

---

## Responsive IA

### Mobile-First IA Considerations

**Progressive Enhancement**:
```
Mobile (Priority):
- Hamburger menu (global nav)
- Bottom tab bar (primary contexts)
- Contextual actions (floating action button)

Tablet:
- Side drawer navigation
- Increased information density

Desktop:
- Persistent top navigation
- Multi-column layouts
- Advanced filters always visible
```

**Information Prioritization**:
```
Mobile Job Detail:
1. Job Title & Company (primary info)
2. Match Score (key decision factor)
3. Key Skills (DDD: vo_skills - top 3)
4. "Quick Apply" CTA
5. [Expand for full details]

Desktop Job Detail:
- All info visible simultaneously
- Side panel for related jobs
- Skills comparison matrix
```

---

## IA Documentation Deliverables

### 1. Site Map

Visual representation of IA structure.

```
[Root]
├── [Profile] (bc_profile)
│   ├── Overview
│   ├── Personal Info
│   ├── Skills
│   ├── Work History
│   └── Preferences
├── [Jobs] (bc_job_catalog, bc_matching)
│   ├── Browse
│   ├── Matches
│   ├── Saved
│   └── Search
├── [Applications] (bc_applications)
│   ├── Active
│   ├── Scheduled
│   └── Archive
└── [Career] (bc_skills_analysis, bc_project_recommendations)
    ├── Skills Gap
    ├── Projects
    └── Resources
```

### 2. Navigation Specification

Detailed navigation behavior:

```yaml
global_navigation:
  items:
    - label: "Profile"  # DDD: bc_profile
      url: "/profile"
      icon: "user"
      auth_required: true

    - label: "Jobs"  # DDD: bc_job_catalog, bc_matching
      url: "/jobs"
      icon: "briefcase"
      children:
        - label: "Browse All"
          url: "/jobs/browse"
        - label: "My Matches"
          url: "/jobs/matches"
          badge: "new_matches_count"  # DDD: count of unviewed matches
```

### 3. Labeling Glossary

All labels mapped to DDD terms:

```markdown
| UI Label | DDD Term | Type | Context |
|----------|----------|------|---------|
| Profile | bc_profile | Bounded Context | Navigation |
| Skills | vo_skills | Value Object | Form field, filter |
| Match Score | vo_match_score | Value Object | Display, sort |
| Submit Application | svc_app_submit_application | Application Service | Button action |
```

---

## Key Takeaways

1. **IA Creates Structure**: Organization, labeling, navigation, and search work together to create usable information environments.

2. **DDD Provides Foundation**: Bounded contexts define primary navigation, aggregates define content structure, value objects define filters/facets, and ubiquitous language defines labels.

3. **User-Centered**: IA translates technical DDD structure into user-friendly navigation and organization.

4. **Consistency is Critical**: Use DDD terminology consistently across all IA elements.

5. **Multiple Access Paths**: Support browsing (hierarchy), searching (query), and filtering (facets) to accommodate different user preferences.

6. **Context Matters**: Local navigation and contextual links help users navigate within bounded contexts and across context boundaries.

7. **Accessibility Built-In**: Semantic structure, clear labels, and predictable navigation support all users.

8. **Document Everything**: Site maps, navigation specs, and labeling glossaries ensure consistent implementation.

---

## References

**Primary Sources**:
- Rosenfeld, Louis; Morville, Peter; Arango, Jorge (2015). "Information Architecture: For the Web and Beyond" (4th Edition)
- Covert, Abby (2014). "How to Make Sense of Any Mess"
- Nielsen Norman Group - IA articles and research

**DDD Integration**:
- `research/ddd/deliverables/ddd-schema-example.yaml` - bounded contexts and aggregates
- `research/ddd/working-docs/04-ubiquitous-language.md` - terminology standards
- `research/ux/UX-DDD-INTEGRATION.md` - integration guidelines

---

*Document created: 2025-10-04*
*Part of UX Research Phase 2: Information Architecture Foundations*
