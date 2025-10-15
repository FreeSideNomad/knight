# User Interface and Information Architecture Terminology

## Table of Contents

- [Introduction](#introduction)
- [How to Use This Guide](#how-to-use-this-guide)
- [Information Architecture (35 terms)](#information-architecture)
- [Navigation Patterns (28 terms)](#navigation-patterns)
- [Workflow Patterns (22 terms)](#workflow-patterns)
- [Page Architecture (24 terms)](#page-architecture)
- [Component Patterns (45 terms)](#component-patterns)
- [Behavioral Patterns (32 terms)](#behavioral-patterns)
- [Scalability Patterns (18 terms)](#scalability-patterns)
- [Accessibility (16 terms)](#accessibility)
- [Appendices](#appendices)

**Total Terms**: 220

---

## Introduction

This terminology guide provides comprehensive definitions for 220 user interface and information architecture concepts, patterns, and practices. Each term includes precise definitions, detailed descriptions, usage criteria, implementation guidance, relationships to other patterns, accessibility considerations, and real-world examples.

**DDD Integration**: All UI terminology aligns with Domain-Driven Design patterns from `research/ddd/`. Navigation maps to bounded contexts, pages display aggregates, components enforce value object rules, and behaviors respond to domain events.

**Key Audiences**:
- UX/UI Designers planning application structure
- Frontend Developers implementing interfaces
- Product Managers defining requirements
- Architects integrating UI with domain models

---

## How to Use This Guide

1. **Browse by Category**: Use table of contents to explore pattern categories
2. **Search by Term**: Use Ctrl+F to find specific terms
3. **Follow Relationships**: Each term links to related concepts
4. **Apply Patterns**: Use "When to Use" sections for decision guidance
5. **Reference Examples**: Study real-world implementations

---

# Information Architecture

Information Architecture (IA) structures, organizes, and labels content to support findability and usability. IA creates the blueprint for navigation, search, and information organization.

**Covered Topics**:
- Organization Systems (hierarchical, sequential, faceted, matrix)
- Labeling Systems (contextual links, headings, navigation labels, tags)
- Navigation Systems (global, local, contextual, utility)
- Search Systems (simple, advanced, faceted)
- Mental Models & Cognitive Principles

**DDD Alignment**: IA structures map to bounded context boundaries, navigation reflects domain organization, and labels use ubiquitous language.

---

## Organization Systems

Organization systems determine how information is grouped, categorized, and structured.

---

### Hierarchical Structure

**Category**: Information Architecture > Organization Systems
**Aliases**: Tree Structure, Taxonomy, Parent-Child Organization
**Related Terms**: Breadcrumbs, Drill-Down Navigation, Sub-Navigation

#### Definition
Information organized in parent-child relationships, arranging content from general categories to specific items in a tree-like structure.

#### When to Use
- Content naturally categorizes into parent-child relationships
- Users understand the domain taxonomy
- Browsing and exploration are primary use cases
- Need 3-7 top-level categories with 2-4 depth levels

#### Implementation (Job Seeker App)
```
Job Seeker
├── Profile (bc_profile)
│   ├── Personal Information
│   ├── Work Experience
│   ├── Skills & Expertise
│   └── Job Preferences
├── Jobs (bc_job_catalog, bc_matching)
│   ├── Browse All Jobs
│   ├── My Matches
│   ├── Saved Jobs
│   └── Job Alerts
├── Applications (bc_applications)
│   ├── Active Applications
│   ├── Interview Schedule
│   └── Application Archive
└── Career Development (bc_skills_analysis)
    ├── Skills Gap Analysis
    ├── Project Recommendations
    └── Learning Resources
```

#### Accessibility
- Use semantic HTML hierarchy (nav, ul, li)
- Use aria-expanded for collapsible sections
- Support keyboard navigation (arrow keys)
- Provide breadcrumbs for deep hierarchies

---

### Sequential Structure

**Category**: Information Architecture > Organization Systems
**Aliases**: Linear Structure, Stepped Process, Ordered Flow
**Related Terms**: Wizard, Multi-Step Form, Progress Indicator

#### Definition
Information organized in a specific linear order with defined beginning and end, where each step follows logically from the previous.

#### When to Use
- Steps have dependencies (step 2 requires step 1 data)
- Business logic requires specific order
- Users need guidance through complex processes
- Each step requires focused attention

#### Implementation (Profile Setup)
```yaml
steps:
  1. Basic Information (vo_email, vo_name, vo_location)
  2. Work Experience (ent_work_experience)
  3. Skills (vo_skills - min 3, max 20)
  4. Preferences (vo_job_preferences)
  5. Review & Submit → publishes evt_profile_created
```

#### Accessibility
- Use role="progressbar" with aria-valuenow
- Use aria-current="step" on current step
- Announce step transitions to screen readers
- Allow backward navigation

---

### Faceted Classification

**Category**: Information Architecture > Organization Systems
**Aliases**: Faceted Navigation, Faceted Search, Multi-Dimensional Filtering
**Related Terms**: Filter Panel, Advanced Search, Dynamic Counts

#### Definition
Multiple independent classification schemes (facets) applied simultaneously to a collection, allowing users to filter by different attributes.

#### When to Use
- Collection has 100+ items with multiple attributes
- Users have diverse search criteria
- Attributes are well-defined and consistent
- Exploration and discovery are goals

#### Implementation (Job Search)
```yaml
facets:
  - location (vo_location): Remote, Hybrid, On-site
  - job_type (vo_job_type): Full-time, Part-time, Contract
  - experience_level (vo_experience_level): Entry, Mid, Senior
  - salary (vo_salary_range): $0-$250k slider
  - skills (vo_required_skills): React, TypeScript, Node.js...

query_logic: AND (all selected facets must match)
result_count: dynamic per facet value
```

#### Accessibility
- Use role="group" for facet groups
- Show result counts next to values
- Use aria-live for result updates
- Support keyboard-only interaction

---

### Matrix Structure

**Category**: Information Architecture > Organization Systems
**Aliases**: Database Organization, Multi-Dimensional Grid
**Related Terms**: Faceted Classification, Cross-Linking

#### Definition
Information organized along multiple independent dimensions simultaneously, with content at intersections of rows and columns.

#### When to Use
- Content has 2-3 clear dimensions
- Users naturally think in terms of intersections
- Matrix size is manageable (not sparse)
- Multiple navigation paths add value

#### Implementation
```
Learning Resources Matrix:
Rows (Topic): JavaScript, Python, React, TypeScript
Columns (Type): Articles, Videos, Tutorials, Docs
Cell: JavaScript × Videos = 89 video tutorials
```

#### Accessibility
- Use semantic table with th scope="row|col"
- Provide caption describing matrix
- Support mobile collapse to list view
- Enable keyboard grid navigation

---

## Labeling Systems

Labeling systems define how UI elements, links, and categories are named and presented.

---

### Contextual Link

**Category**: Information Architecture > Labeling Systems
**Aliases**: Inline Link, Embedded Link, Hyperlink
**Related Terms**: Information Scent, Cross-Reference

#### Definition
A hyperlink embedded within body text that connects to related content, with link text providing clear indication of destination.

#### When to Use
- Providing supporting detail not all users need
- Defining technical terms or jargon
- Linking to cross-context information

#### Implementation
```html
<p>
  We're seeking a <a href="/glossary/senior-developer">senior developer</a>
  with <a href="/skills/react">React</a> experience.
</p>
```

#### Accessibility
- Use descriptive link text (not "click here")
- Ensure 4.5:1 color contrast
- Underline links or use distinct styling
- Provide aria-label if text is ambiguous

---

### Navigation Label

**Category**: Information Architecture > Labeling Systems
**Aliases**: Menu Item, Nav Link, Section Label
**Related Terms**: Global Navigation, Local Navigation, Ubiquitous Language

#### Definition
Text labels used in navigation menus to identify sections, pages, or actions, using terminology that matches user mental models.

#### When to Use
- Every navigation menu item
- Primary and secondary navigation
- Mobile navigation
- Breadcrumbs

#### Implementation (DDD-Aligned Labels)
```yaml
navigation_labels:
  - "Profile" (not "User Settings") - bc_profile
  - "Jobs" (not "Listings") - bc_job_catalog
  - "My Matches" (not "Recommendations") - bc_matching
  - "Applications" (not "Submissions") - bc_applications
  - "Skills Gap" (not "Analysis") - bc_skills_analysis
```

#### Accessibility
- Keep labels concise (1-3 words)
- Use consistent terminology
- Use aria-label for icon-only navigation
- Use aria-current="page" for active item

---

### Heading

**Category**: Information Architecture > Labeling Systems
**Aliases**: Page Title, Section Header, H1-H6
**Related Terms**: Visual Hierarchy, Document Outline

#### Definition
Structural labels that organize page content into hierarchical sections, using HTML heading elements (h1-h6).

#### When to Use
- Every page needs exactly one h1 (page title)
- Each major section needs h2
- Subsections use h3-h6 as needed
- Maintain logical hierarchy (no skipping levels)

#### Implementation
```html
<h1>Senior Frontend Developer - TechCorp</h1> <!-- agg_job_posting.title -->
<h2>Job Description</h2>
<h3>Requirements</h3>
<h3>Responsibilities</h3>
<h2>About the Company</h2> <!-- bc_company_profiles -->
<h2>Application Process</h2>
```

#### Accessibility
- Never skip heading levels (h1→h2→h3, not h1→h3)
- Use headings for structure, not styling
- Provide descriptive, unique headings
- Screen readers use headings for navigation

---

## Navigation Systems

Navigation systems define how users move through the application.

---

### Global Navigation

**Category**: Information Architecture > Navigation Systems
**Aliases**: Primary Navigation, Main Menu, Top Nav
**Related Terms**: Navigation Bar, Site-Wide Navigation

#### Definition
Persistent navigation that provides access to primary application sections throughout the entire user experience.

#### When to Use
- Every multi-section application needs global nav
- Provides access to main bounded contexts
- Remains visible (sticky or persistent) across pages

#### Implementation
```html
<header role="banner">
  <nav aria-label="Main navigation">
    <a href="/" aria-label="Home"><img src="/logo.svg" alt="Job Seeker"></a>
    <ul>
      <li><a href="/profile" aria-current="page">Profile</a></li>
      <li><a href="/jobs">Jobs</a></li>
      <li><a href="/applications">Applications</a></li>
      <li><a href="/career">Career</a></li>
    </ul>
    <button aria-label="User menu" aria-expanded="false">👤</button>
  </nav>
</header>
```

#### Accessibility
- Use nav with aria-label="Main navigation"
- Use aria-current="page" for active section
- Support keyboard navigation (Tab, Enter)
- Provide skip link to main content

---

### Local Navigation

**Category**: Information Architecture > Navigation Systems
**Aliases**: Sub-Navigation, Section Navigation, Secondary Nav
**Related Terms**: Tabs, Sidebar Menu, Context-Specific Navigation

#### Definition
Navigation within a specific section or bounded context, providing access to related pages without leaving the current area.

#### When to Use
- Section has 3+ related pages
- Users need to move between related content
- Preserves context (stays within bounded context)

#### Implementation (Profile Section)
```html
<nav aria-label="Profile navigation">
  <ul>
    <li><a href="/profile/personal" aria-current="page">Personal Info</a></li>
    <li><a href="/profile/experience">Work Experience</a></li>
    <li><a href="/profile/skills">Skills</a></li>
    <li><a href="/profile/preferences">Preferences</a></li>
  </ul>
</nav>
```

#### Accessibility
- Use nav with descriptive aria-label
- Use aria-current for current page
- Maintain focus indicators
- Support keyboard navigation

---

### Breadcrumbs

**Category**: Information Architecture > Navigation Systems
**Aliases**: Breadcrumb Trail, Path Navigation
**Related Terms**: Hierarchical Navigation, Wayfinding

#### Definition
Secondary navigation showing the user's current location within the site hierarchy and path taken to arrive there.

#### When to Use
- Hierarchical sites with 3+ levels deep
- E-commerce categories
- Documentation sites
- Any deep content structure

#### Implementation
```html
<nav aria-label="Breadcrumb">
  <ol>
    <li><a href="/">Home</a></li>
    <li><a href="/jobs">Jobs</a></li>
    <li><a href="/jobs/frontend">Frontend</a></li>
    <li aria-current="page">Senior React Developer</li>
  </ol>
</nav>
```

#### Accessibility
- Use nav with aria-label="Breadcrumb"
- Use ordered list (ol)
- Use aria-current="page" on current item
- Don't link current page

---

### Pagination

**Category**: Information Architecture > Navigation Systems
**Aliases**: Page Navigation, Result Paging
**Related Terms**: Infinite Scroll, Load More, Page Controls

#### Definition
Navigation control that divides large collections into discrete pages with numbered links to jump between pages.

#### When to Use
- Collections with 50+ items
- Known total count
- Users need random access (jump to page 10)
- SEO important (crawlable pages)

#### Implementation
```html
<nav aria-label="Pagination" role="navigation">
  <ul class="pagination">
    <li><a href="?page=1" aria-label="First page">First</a></li>
    <li><a href="?page=2" aria-label="Previous page">Previous</a></li>
    <li><a href="?page=1">1</a></li>
    <li><a href="?page=2">2</a></li>
    <li aria-current="page"><span>3</span></li>
    <li><a href="?page=4">4</a></li>
    <li><a href="?page=5">5</a></li>
    <li><a href="?page=4" aria-label="Next page">Next</a></li>
    <li><a href="?page=10" aria-label="Last page, page 10">Last</a></li>
  </ul>
  <div>Showing 21-30 of 247 results</div>
</nav>
```

#### Accessibility
- Use nav with aria-label="Pagination"
- Use aria-current="page" on current page
- Provide descriptive aria-labels
- Show result count

---

## Search Systems

Search systems help users find content through queries.

---

### Simple Search

**Category**: Information Architecture > Search Systems
**Aliases**: Global Search, Keyword Search, Search Box
**Related Terms**: Search Bar, Autocomplete, Search Results

#### Definition
Single text input that searches across all content using keyword matching, typically placed in the global header.

#### When to Use
- Every application with 50+ pages/items
- Users know what they're looking for
- Complement to navigation (not replacement)

#### Implementation
```html
<form role="search" aria-label="Site search">
  <label for="search-input" class="visually-hidden">Search</label>
  <input
    type="search"
    id="search-input"
    name="q"
    placeholder="Search jobs, companies, skills..."
    aria-describedby="search-description"
    autocomplete="off"
  >
  <span id="search-description" class="visually-hidden">
    Search across all jobs, companies, and resources
  </span>
  <button type="submit" aria-label="Submit search">🔍</button>
</form>
```

#### Accessibility
- Use role="search" on form
- Provide visible or visually-hidden label
- Use type="search" for mobile keyboard
- Announce results count to screen readers

---

### Faceted Search

**Category**: Information Architecture > Search Systems
**Aliases**: Guided Search, Filtered Search
**Related Terms**: Faceted Classification, Filter Panel, Advanced Search

#### Definition
Search interface combining keyword search with multiple filter facets, allowing users to refine results by selecting attribute values.

#### When to Use
- Large collections (100+ items)
- Items have multiple searchable attributes
- Users need flexible refinement
- Combines search + browse behaviors

#### Implementation
See [Faceted Classification](#faceted-classification) for detailed implementation.

#### Accessibility
- Combine search and filter accessibility requirements
- Announce filter changes and result counts
- Support keyboard-only filtering
- Provide clear filter removal

---

# Navigation Patterns

Navigation patterns define specific UI implementations for moving through an application.

---

## Global Navigation Patterns

---

### Top Horizontal Navigation Bar

**Category**: Navigation Patterns > Global Navigation
**Aliases**: Top Nav, Header Navigation, Horizontal Menu
**Related Terms**: Navigation Bar, Sticky Header

#### Definition
Persistent horizontal navigation bar at the top of the viewport containing primary section links.

#### When to Use
- Desktop/tablet applications
- 3-7 primary sections
- Horizontal screen space available
- Professional/business applications

#### Implementation
```css
.top-nav {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-items {
  display: flex;
  gap: 32px;
  list-style: none;
  margin: 0 auto;
}

.nav-item a {
  color: #333;
  text-decoration: none;
  padding: 8px 12px;
  border-bottom: 2px solid transparent;
}

.nav-item a[aria-current="page"] {
  color: #0066cc;
  border-bottom-color: #0066cc;
  font-weight: 600;
}
```

#### Accessibility
- Use semantic nav element
- Use aria-label="Main navigation"
- Highlight current page with aria-current
- Ensure keyboard focusable

---

### Side Navigation

**Category**: Navigation Patterns > Global Navigation
**Aliases**: Sidebar, Drawer, Vertical Navigation
**Related Terms**: Collapsible Menu, Tree Navigation

#### Definition
Vertical navigation panel along the left or right edge, often collapsible, containing hierarchical menu items.

#### When to Use
- Applications with 8+ navigation items
- Hierarchical content structure
- Desktop-first applications
- Admin panels and dashboards

#### Implementation
```html
<aside class="sidebar" role="navigation" aria-label="Main navigation">
  <nav>
    <ul>
      <li>
        <button aria-expanded="true" aria-controls="profile-submenu">
          Profile
        </button>
        <ul id="profile-submenu">
          <li><a href="/profile/personal">Personal Info</a></li>
          <li><a href="/profile/experience">Experience</a></li>
          <li><a href="/profile/skills">Skills</a></li>
        </ul>
      </li>
      <!-- More sections... -->
    </ul>
  </nav>
</aside>
```

#### Accessibility
- Use aria-expanded for collapsible sections
- Provide keyboard navigation (arrows, Enter)
- Support focus management
- Allow collapsing/expanding with keyboard

---

### Tab Navigation

**Category**: Navigation Patterns > Local Navigation
**Aliases**: Tabs, Tabbed Interface, Tab Bar
**Related Terms**: Tab Panel, Tab List, ARIA Tabs

#### Definition
Horizontal row of tabs where clicking a tab displays associated content panel without page reload.

#### When to Use
- 2-7 related views of same content
- Switching doesn't lose context/data
- Content fits within viewport
- Desktop and tablet layouts

#### Implementation
```html
<div class="tabs" role="tablist" aria-label="Profile sections">
  <button role="tab" aria-selected="true" aria-controls="personal-panel" id="personal-tab">
    Personal Info
  </button>
  <button role="tab" aria-selected="false" aria-controls="experience-panel" id="experience-tab">
    Experience
  </button>
  <button role="tab" aria-selected="false" aria-controls="skills-panel" id="skills-tab">
    Skills
  </button>
</div>

<div role="tabpanel" id="personal-panel" aria-labelledby="personal-tab">
  <!-- Personal info content -->
</div>
<div role="tabpanel" id="experience-panel" aria-labelledby="experience-tab" hidden>
  <!-- Experience content -->
</div>
<div role="tabpanel" id="skills-panel" aria-labelledby="skills-tab" hidden>
  <!-- Skills content -->
</div>
```

#### Accessibility
- Use ARIA tabs pattern (role="tablist", role="tab", role="tabpanel")
- Use aria-selected on active tab
- Use aria-controls to link tab to panel
- Support arrow key navigation between tabs
- Support Home/End keys

---

### Hamburger Menu

**Category**: Navigation Patterns > Mobile Navigation
**Aliases**: Mobile Menu, Menu Button, Navigation Drawer
**Related Terms**: Mobile Navigation, Overlay Menu

#### Definition
Icon button (three horizontal lines) that opens a hidden navigation menu, typically used on mobile devices.

#### When to Use
- Mobile viewports (< 768px)
- Converting desktop nav to mobile
- Space-constrained interfaces
- 5+ navigation items won't fit horizontally

#### Implementation
```html
<button
  class="hamburger-menu"
  aria-label="Main menu"
  aria-expanded="false"
  aria-controls="mobile-nav"
>
  <span class="bar"></span>
  <span class="bar"></span>
  <span class="bar"></span>
</button>

<nav id="mobile-nav" class="mobile-nav" aria-label="Main navigation" hidden>
  <ul>
    <li><a href="/profile">Profile</a></li>
    <li><a href="/jobs">Jobs</a></li>
    <li><a href="/applications">Applications</a></li>
    <li><a href="/career">Career</a></li>
  </ul>
</nav>
```

#### Accessibility
- Use aria-label="Main menu" on button
- Use aria-expanded to indicate state
- Use aria-controls to link to menu
- Support Escape key to close
- Trap focus in open menu
- Restore focus to button on close

---

# Workflow Patterns

Workflow patterns guide users through multi-step processes.

---

## Workflow Types

---

### Wizard

**Category**: Workflow Patterns > Linear Workflows
**Aliases**: Stepped Form, Multi-Step Process, Setup Wizard
**Related Terms**: Progress Indicator, Sequential Structure

#### Definition
Linear multi-step process with progress indication, where users complete one step at a time in a fixed sequence.

#### When to Use
- Complex process with 3-7 distinct steps
- Each step requires focused attention
- Steps have dependencies
- First-time setup or onboarding

#### Implementation
See [Sequential Structure](#sequential-structure) for detailed implementation.

#### Accessibility
- Use role="progressbar" for progress indicator
- Announce step changes
- Allow backward navigation
- Auto-save progress

---

### Hub Pattern

**Category**: Workflow Patterns > Flexible Workflows
**Aliases**: Dashboard, Action Center, Command Center
**Related Terms**: Dashboard Page, Card Layout

#### Definition
Central page displaying multiple action cards or widgets, allowing users to complete tasks in any order.

#### When to Use
- Multiple independent tasks available
- Users choose their own path
- Status overview needed
- Returning users (not first-time setup)

#### Implementation
```html
<div class="dashboard-hub">
  <h1>Dashboard</h1>

  <div class="action-cards">
    <div class="card">
      <h2>Complete Your Profile</h2>
      <p>85% complete - Add 2 more skills</p>
      <a href="/profile/skills" class="btn-primary">Add Skills</a>
    </div>

    <div class="card">
      <h2>New Job Matches</h2>
      <p>12 new matches found</p>
      <a href="/jobs/matches" class="btn-primary">View Matches</a>
    </div>

    <div class="card">
      <h2>Application Status</h2>
      <p>3 applications in review</p>
      <a href="/applications" class="btn-secondary">Check Status</a>
    </div>
  </div>
</div>
```

#### Accessibility
- Use semantic headings (h1, h2)
- Provide clear action buttons
- Use aria-labels for context
- Support keyboard navigation

---

### Progressive Disclosure

**Category**: Workflow Patterns > Guided Workflows
**Aliases**: Show More, Expand/Collapse, Gradual Engagement
**Related Terms**: Accordion, Collapsible Sections

#### Definition
Revealing information or form fields gradually as needed, showing only essential items initially and revealing additional complexity on demand.

#### When to Use
- Complex forms with optional fields
- Reducing initial cognitive load
- Providing advanced options without overwhelming
- Conditional fields based on previous answers

#### Implementation
```html
<form>
  <!-- Always visible -->
  <div class="form-group">
    <label for="email">Email</label>
    <input type="email" id="email" required>
  </div>

  <!-- Progressive disclosure -->
  <button type="button" onclick="toggleAdvanced()" aria-expanded="false" aria-controls="advanced-options">
    Show Advanced Options
  </button>

  <div id="advanced-options" hidden>
    <div class="form-group">
      <label for="linkedin">LinkedIn Profile</label>
      <input type="url" id="linkedin">
    </div>
    <div class="form-group">
      <label for="portfolio">Portfolio URL</label>
      <input type="url" id="portfolio">
    </div>
  </div>
</form>
```

#### Accessibility
- Use aria-expanded on toggle button
- Use aria-controls to link button to content
- Announce state changes
- Maintain keyboard focus

---

### Batch Operations

**Category**: Workflow Patterns > Batch Workflows
**Aliases**: Bulk Actions, Multi-Select Actions, Select and Act
**Related Terms**: Checkbox Selection, Action Bar

#### Definition
Workflow allowing users to select multiple items and perform a single action on all selected items simultaneously.

#### When to Use
- Operating on multiple items is common
- Actions apply to many items (delete, archive, tag)
- Collection has 20+ items
- Efficiency is important

#### Implementation
```html
<div class="batch-operations">
  <!-- Selection toolbar -->
  <div class="selection-toolbar" hidden aria-live="polite">
    <span>3 items selected</span>
    <button onclick="applyToSelected()">Apply to Selected</button>
    <button onclick="saveSelected()">Save Selected</button>
    <button onclick="clearSelection()">Clear Selection</button>
  </div>

  <!-- Item list with checkboxes -->
  <table>
    <thead>
      <tr>
        <th scope="col">
          <input type="checkbox" aria-label="Select all jobs" onchange="selectAll()">
        </th>
        <th scope="col">Job Title</th>
        <th scope="col">Company</th>
        <th scope="col">Match</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><input type="checkbox" aria-label="Select Frontend Developer at TechCorp"></td>
        <td>Frontend Developer</td>
        <td>TechCorp</td>
        <td>85%</td>
      </tr>
      <!-- More rows... -->
    </tbody>
  </table>
</div>
```

#### Accessibility
- Provide "Select all" checkbox
- Announce selection count
- Use aria-label for checkboxes
- Support keyboard selection (Shift+click for range)

---

# Page Architecture

Page architecture defines structure and layout of individual pages.

---

## Page Types

---

### List Page

**Category**: Page Architecture > List/Collection Pages
**Aliases**: Collection Page, Index Page, Browse Page
**Related Terms**: Data Table, Card Grid, Infinite Scroll

#### Definition
Page displaying a collection of items (aggregates) with browsing, sorting, filtering, and pagination capabilities.

#### When to Use
- Displaying collections of similar items
- Users need to browse, search, and filter
- Items are instances of same aggregate type

#### Implementation (Job Listings)
```yaml
page: job_listings
bounded_context: bc_job_catalog
displays: collection_of_agg_job_posting
data_source: repo_job_posting

sections:
  - page_header:
      title: "Jobs"
      count: "{{total_jobs}}"
      actions: ["Post Job Alert", "Save Search"]

  - filters_sidebar:
      facets: [location, job_type, experience, salary, skills]

  - list_controls:
      sort: [best_match, recent, salary]
      view: [grid, list]

  - list_content:
      items: job_cards
      pagination: true
      per_page: 20
```

#### Accessibility
- Use semantic HTML (main, section, ul/ol)
- Provide page title (h1)
- Support keyboard navigation
- Announce filter/sort changes

---

### Detail Page

**Category**: Page Architecture > Detail/Entity Pages
**Aliases**: Entity Page, Item Page, Show Page
**Related Terms**: Master-Detail, Profile Page

#### Definition
Page displaying complete information about a single entity (aggregate), typically accessed from a list page.

#### When to Use
- Showing full details of one aggregate
- Users drill down from list
- More information than fits in card/row

#### Implementation (Job Detail)
```yaml
page: job_detail
bounded_context: bc_job_catalog
displays: agg_job_posting (single)
data_source: repo_job_posting.findById

sections:
  - hero:
      title: "{{job.title}}"
      company: "{{job.company}}" (bc_company_profiles)
      match_score: "{{job.matchScore}}" (bc_matching)
      actions: ["Apply", "Save Job", "Share"]

  - details:
      description: "{{job.description}}"
      requirements: "{{job.requirements}}"
      responsibilities: "{{job.responsibilities}}"

  - sidebar:
      salary: "{{job.salaryRange}}"
      location: "{{job.location}}"
      job_type: "{{job.jobType}}"
      experience: "{{job.experienceLevel}}"

  - related:
      similar_jobs: collection (repo_job_posting.findSimilar)
```

#### Accessibility
- Use descriptive h1 (job title)
- Use semantic sections
- Provide skip links for long pages
- Support keyboard navigation

---

### Form Page

**Category**: Page Architecture > Form/Edit Pages
**Aliases**: Edit Page, Create Page, Input Page
**Related Terms**: Form Validation, Multi-Step Form

#### Definition
Page focused on data input, allowing users to create new or edit existing aggregate instances.

#### When to Use
- Creating new aggregates
- Editing existing aggregates
- Collecting structured user input

#### Implementation (Edit Profile)
```yaml
page: edit_profile
bounded_context: bc_profile
edits: agg_candidate_profile
service: svc_app_update_profile

sections:
  - page_header:
      title: "Edit Profile"
      actions: ["Cancel", "Save Draft", "Publish"]

  - form:
      fields:
        - vo_name (required)
        - vo_email (required, validated)
        - vo_location (required)
        - ent_work_experience (collection)
        - vo_skills (multi-select, min 3)
        - vo_job_preferences

      validation: per_field_on_blur + on_submit
      auto_save: every_30_seconds

      actions:
        save: svc_app_update_profile
        publish: transition DRAFT → ACTIVE
        publishes: evt_profile_updated
```

#### Accessibility
- Use form with proper labels
- Group related fields with fieldset
- Provide error messages with aria-describedby
- Support keyboard-only completion

---

### Dashboard Page

**Category**: Page Architecture > Dashboard/Overview Pages
**Aliases**: Overview Page, Home Page, Command Center
**Related Terms**: Hub Pattern, Widget Layout

#### Definition
Page aggregating key information and actions from multiple bounded contexts, providing status overview and quick access to common tasks.

#### When to Use
- Landing page for authenticated users
- Aggregating cross-context data
- Providing personalized overview
- Quick access to frequent actions

#### Implementation
```yaml
page: dashboard
cross_context: true
aggregates_from:
  - bc_profile (profile completeness)
  - bc_matching (new matches count)
  - bc_applications (application status)
  - bc_skills_analysis (skills gap summary)

sections:
  - welcome:
      greeting: "Welcome back, {{user.name}}"

  - widgets:
      - profile_status:
          completeness: 85%
          action: "Add 2 skills"

      - new_matches:
          count: 12
          top_match: agg_job_match (highest score)
          action: "View All Matches"

      - applications:
          active_count: 3
          upcoming_interviews: 1
          action: "View Applications"

      - skills_gap:
          top_gap: "TypeScript"
          recommended_projects: 2
          action: "View Recommendations"
```

#### Accessibility
- Use semantic sections
- Provide clear headings for each widget
- Support keyboard navigation between widgets
- Use aria-live for real-time updates

---

### Empty State Page

**Category**: Page Architecture > Empty State Pages
**Aliases**: Zero State, Blank Slate, No Data
**Related Terms**: Onboarding, Call to Action

#### Definition
Page or section displayed when a collection has no items, providing guidance on how to add the first item.

#### When to Use
- First-time users with no data
- Filtered results return zero items
- User has deleted all items
- Feature not yet used

#### Implementation
```html
<div class="empty-state">
  <img src="/illustrations/no-applications.svg" alt="" role="presentation">
  <h2>No Applications Yet</h2>
  <p>You haven't applied to any jobs yet. Start by browsing our job listings and finding your perfect match.</p>
  <a href="/jobs" class="btn-primary">Browse Jobs</a>
  <a href="/jobs/matches" class="btn-secondary">View My Matches</a>
</div>
```

#### Accessibility
- Provide clear heading
- Use descriptive text
- Provide actionable next steps
- Use alt="" for decorative illustrations

---

### Error Page

**Category**: Page Architecture > Error Pages
**Aliases**: 404 Page, Error State, Failure Page
**Related Terms**: Error Handling, HTTP Status Codes

#### Definition
Page displayed when an error occurs (404, 403, 500), explaining the problem and providing recovery options.

#### When to Use
- 404 Not Found (page doesn't exist)
- 403 Forbidden (insufficient permissions)
- 500 Server Error (unexpected failure)

#### Implementation
```html
<!-- 404 Not Found -->
<div class="error-page">
  <h1>404 - Page Not Found</h1>
  <p>The page you're looking for doesn't exist or has been moved.</p>

  <div class="suggestions">
    <h2>Try these instead:</h2>
    <ul>
      <li><a href="/jobs">Browse Jobs</a></li>
      <li><a href="/jobs/matches">View Your Matches</a></li>
      <li><a href="/profile">Update Your Profile</a></li>
      <li><a href="/">Go to Dashboard</a></li>
    </ul>
  </div>

  <p>Or <a href="/search">search for what you're looking for</a>.</p>
</div>

<!-- 403 Forbidden (DDD: Aggregate access control) -->
<div class="error-page">
  <h1>403 - Access Denied</h1>
  <p>You don't have permission to view this job application.</p>
  <p>This application belongs to another user.</p>
  <a href="/applications" class="btn-primary">View Your Applications</a>
</div>
```

#### Accessibility
- Use descriptive h1
- Explain the error clearly
- Provide actionable recovery options
- Maintain site navigation

---

# Component Patterns

Component patterns define reusable UI building blocks using atomic design methodology.

---

## Atomic Design

---

### Atom

**Category**: Component Patterns > Atomic Design
**Aliases**: Primitive Component, Base Element
**Related Terms**: Button, Input, Label

#### Definition
The smallest, indivisible UI component that serves a single purpose (button, input, label, icon).

#### When to Use
- Building basic UI elements
- Creating design system primitives
- Need maximum reusability

#### Examples
- Button (primary, secondary, tertiary variants)
- Input (text, email, password types)
- Label, Icon, Badge, Link

---

### Molecule

**Category**: Component Patterns > Atomic Design
**Aliases**: Composite Component, Group Component
**Related Terms**: Form Field, Search Box

#### Definition
Simple group of atoms functioning together as a unit (form field = label + input + error message).

#### When to Use
- Grouping related atoms with single responsibility
- Creating reusable patterns
- Building forms and inputs

#### Examples
- Form Field (Label + Input + Error + Help Text)
- Search Box (Input + Button)
- Tag Input (Input + Tag List)
- Match Score Display (Icon + Percentage + Badge)

---

### Organism

**Category**: Component Patterns > Atomic Design
**Aliases**: Complex Component, Composite Widget
**Related Terms**: Card, Form, Navigation

#### Definition
Complex UI component composed of molecules and/or atoms, representing a distinct section of interface.

#### When to Use
- Creating self-contained UI sections
- Displaying aggregates
- Building navigation structures

#### Examples
- Job Card (displays agg_job_posting)
- Application Card (displays agg_application)
- Navigation Bar
- Form Section

---

## Atoms

---

### Button

**Category**: Component Patterns > Atoms
**Aliases**: Action Button, CTA, Command Button
**Related Terms**: Link, Icon Button

#### Definition
Interactive element that triggers an action when clicked or activated.

#### Variants
- **Primary**: Main action (Apply Now, Save, Submit)
- **Secondary**: Alternative action (Cancel, Back)
- **Tertiary**: Subtle action (Learn More, Dismiss)
- **Danger**: Destructive action (Delete, Withdraw)
- **Ghost**: Icon-only or minimal styling

#### Implementation
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

// Example usage with domain language
<Button variant="primary" onClick={handleApply} loading={isSubmitting}>
  Apply Now
</Button>
```

#### States
- Default, Hover, Focus, Active, Disabled, Loading

#### Accessibility
- Use button or a with role="button"
- Provide aria-label if text unclear
- Use aria-pressed for toggle buttons
- Ensure 44×44px minimum touch target
- Keyboard: Space or Enter to activate

---

### Input

**Category**: Component Patterns > Atoms
**Aliases**: Text Field, Input Field, Text Box
**Related Terms**: Form Field, Validation

#### Definition
Element allowing users to enter text or other data.

#### Types
- text, email, password, number, tel, url, search, date, time

#### Implementation
```typescript
interface InputProps {
  type: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search';
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  disabled?: boolean;
  error?: string;
  required?: boolean;
  pattern?: string;
  minLength?: number;
  maxLength?: number;
  ariaLabel?: string;
  ariaDescribedBy?: string;
}

// DDD integration: enforce value object validation
<Input
  type="email"
  value={email}
  onChange={setEmail}
  error={emailError}
  pattern={VO_EMAIL_PATTERN}
  aria-describedby="email-error"
/>
```

#### States
- Default, Focus, Filled, Error, Disabled

#### Accessibility
- Always provide label (visible or visually-hidden)
- Use aria-describedby for errors and hints
- Use aria-invalid when error present
- Use appropriate input type for mobile keyboards

---

### Badge

**Category**: Component Patterns > Atoms
**Aliases**: Tag, Chip, Label
**Related Terms**: Status Indicator, Count Indicator

#### Definition
Small visual indicator displaying status, count, or category.

#### Variants
- **Status Badge**: Shows state (Active, Pending, Rejected)
- **Count Badge**: Shows number (Notifications: 3)
- **Category Badge**: Shows classification (Remote, Full-time)

#### Implementation
```typescript
interface BadgeProps {
  variant: 'success' | 'warning' | 'error' | 'info' | 'neutral';
  size: 'small' | 'medium' | 'large';
  children: ReactNode;
}

// DDD: Display aggregate state
<Badge variant={getVariant(application.state)}>
  {application.state} {/* DRAFT, SUBMITTED, IN_REVIEW, etc. */}
</Badge>
```

#### Accessibility
- Use semantic color (not color alone)
- Provide text description
- Use aria-label if icon-only

---

## Molecules

---

### Form Field

**Category**: Component Patterns > Molecules
**Aliases**: Input Group, Field Group
**Related Terms**: Input, Label, Error Message

#### Definition
Combination of label, input, optional help text, and error message functioning as a single form field unit.

#### Implementation
```typescript
interface FormFieldProps {
  label: string;
  type: 'text' | 'email' | 'password' | 'number';
  value: string;
  onChange: (value: string) => void;
  error?: string;
  helpText?: string;
  required?: boolean;
  disabled?: boolean;
}

function FormField({ label, error, helpText, required, ...inputProps }: FormFieldProps) {
  const id = useId();
  const errorId = `${id}-error`;
  const helpId = `${id}-help`;

  return (
    <div class name="form-field">
      <label htmlFor={id}>
        {label}
        {required && <span aria-label="required">*</span>}
      </label>

      <input
        id={id}
        aria-describedby={`${error ? errorId : ''} ${helpText ? helpId : ''}`.trim()}
        aria-invalid={!!error}
        aria-required={required}
        {...inputProps}
      />

      {helpText && <span id={helpId} className="help-text">{helpText}</span>}
      {error && <span id={errorId} className="error-text" role="alert">{error}</span>}
    </div>
  );
}

// DDD usage: Value object validation
<FormField
  label="Email"
  type="email"
  value={email}
  onChange={setEmail}
  error={validateEmail(email)} // vo_email validation
  helpText="We'll never share your email"
  required
/>
```

#### Accessibility
- Always associate label with input
- Use aria-describedby for help text and errors
- Use aria-invalid when error present
- Use role="alert" for error messages
- Use aria-required for required fields

---

### Search Box

**Category**: Component Patterns > Molecules
**Aliases**: Search Input, Search Field
**Related Terms**: Autocomplete, Search

#### Definition
Combination of search input and submit button, optionally with autocomplete suggestions.

#### Implementation
```html
<form role="search" class="search-box">
  <label for="search" class="visually-hidden">Search jobs</label>
  <input
    type="search"
    id="search"
    placeholder="Search jobs, companies, skills..."
    autocomplete="off"
    aria-autocomplete="list"
    aria-controls="search-suggestions"
  >
  <button type="submit" aria-label="Submit search">
    <SearchIcon />
  </button>

  <!-- Autocomplete suggestions -->
  <ul id="search-suggestions" role="listbox" hidden>
    <li role="option">Frontend Developer</li>
    <li role="option">React Developer</li>
    <li role="option">Senior JavaScript Engineer</li>
  </ul>
</form>
```

#### Accessibility
- Use role="search" on form
- Provide label (visible or visually-hidden)
- Use aria-autocomplete for autocomplete
- Use aria-controls to link to suggestions
- Implement ARIA autocomplete pattern

---

## Organisms

---

### Job Card

**Category**: Component Patterns > Organisms
**Aliases**: Job Listing Card, Job Item
**Related Terms**: Card, List Item

#### Definition
Composite component displaying summary of a job posting (aggregate) with key details and actions.

#### Implementation
```typescript
interface JobCardProps {
  job: JobPosting; // agg_job_posting
  matchScore?: number; // from bc_matching
  onSave?: () => void;
  onApply?: () => void;
}

function JobCard({ job, matchScore, onSave, onApply }: JobCardProps) {
  return (
    <article className="job-card" aria-labelledby={`job-${job.id}-title`}>
      <div className="job-header">
        <h3 id={`job-${job.id}-title`}>{job.title}</h3>
        {matchScore && (
          <Badge variant="success" aria-label={`${matchScore}% match`}>
            {matchScore}% Match
          </Badge>
        )}
      </div>

      <div className="job-meta">
        <span>{job.company.name}</span> {/* bc_company_profiles */}
        <span>•</span>
        <span>{job.location.city}</span> {/* vo_location */}
        <span>•</span>
        <span>{formatSalary(job.salaryRange)}</span> {/* vo_salary_range */}
      </div>

      <div className="job-skills">
        {job.requiredSkills.slice(0, 5).map(skill => ( /* vo_required_skills */
          <Badge key={skill} variant="neutral" size="small">{skill}</Badge>
        ))}
      </div>

      <div className="job-actions">
        <Button variant="secondary" size="small" onClick={onSave}>
          Save
        </Button>
        <Button variant="primary" size="small" onClick={onApply}>
          Apply
        </Button>
      </div>
    </article>
  );
}
```

#### Accessibility
- Use article for semantic meaning
- Use aria-labelledby to link to title
- Provide descriptive button labels
- Support keyboard navigation

---

### Navigation Bar

**Category**: Component Patterns > Organisms
**Aliases**: Nav Bar, Header, App Bar
**Related Terms**: Global Navigation, Top Nav

#### Definition
Complex navigation component containing logo, primary navigation links, search, and user menu.

#### Implementation
See [Top Horizontal Navigation Bar](#top-horizontal-navigation-bar) for implementation details.

#### Accessibility
- Use header with nav
- Use aria-label="Main navigation"
- Use aria-current for active page
- Support keyboard navigation
- Provide skip link

---

### Filter Panel

**Category**: Component Patterns > Organisms
**Aliases**: Facet Panel, Filter Sidebar
**Related Terms**: Faceted Search, Filters

#### Definition
Panel containing multiple filter controls (facets) for refining search or browse results.

#### Implementation
See [Faceted Classification](#faceted-classification) for detailed implementation.

#### Accessibility
- Use fieldset/legend for filter groups
- Use aria-label for filter sections
- Announce result count changes
- Support keyboard-only interaction
- Provide "Clear all" option

---

# Behavioral Patterns

Behavioral patterns define how UI responds to user interactions and system events.

---

## Interaction States

---

### Hover State

**Category**: Behavioral Patterns > Interaction States
**Aliases**: Mouse Over, Pointer Hover
**Related Terms**: Focus State, Interactive Feedback

#### Definition
Visual change that occurs when a pointer (mouse, stylus) moves over an interactive element.

#### When to Use
- Interactive elements (buttons, links, cards)
- Providing visual feedback before click
- Revealing additional controls or information
- Desktop/tablet interfaces (not mobile)

#### Implementation
```css
.interactive-element {
  transition: all 200ms ease;
}

.interactive-element:hover {
  background-color: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

/* Don't show hover on touch devices */
@media (hover: none) {
  .interactive-element:hover {
    background-color: inherit;
    transform: none;
  }
}
```

#### Accessibility
- Don't rely solely on hover (provide alternatives)
- Match hover with focus styling
- Use @media (hover: none) for touch devices
- Ensure sufficient contrast in all states

---

### Focus State

**Category**: Behavioral Patterns > Interaction States
**Aliases**: Keyboard Focus, Active Element
**Related Terms**: Focus Management, Keyboard Navigation

#### Definition
Visual indication that an element has keyboard focus and will respond to keyboard input.

#### When to Use
- All interactive elements (buttons, links, inputs)
- Required for keyboard accessibility
- Custom components need explicit focus styles

#### Implementation
```css
.interactive-element:focus {
  outline: 2px solid #0066cc;
  outline-offset: 2px;
}

/* Modern focus-visible (only keyboard, not mouse clicks) */
.interactive-element:focus-visible {
  outline: 2px solid #0066cc;
  outline-offset: 2px;
}

.interactive-element:focus:not(:focus-visible) {
  outline: none;
}
```

#### Accessibility
- Never remove outline without replacement
- Use visible, high-contrast indicator
- Use :focus-visible for better UX
- Ensure logical focus order (tab order)

---

### Loading State

**Category**: Behavioral Patterns > Interaction States
**Aliases**: Busy State, Processing State
**Related Terms**: Spinner, Progress Indicator, Skeleton Screen

#### Definition
Visual indication that content is being fetched or an operation is in progress.

#### When to Use
- Async data fetching (> 300ms)
- Form submission
- Long-running operations
- Page transitions

#### Implementation
```typescript
// Button loading state
<Button
  variant="primary"
  onClick={handleSubmit}
  loading={isSubmitting}
  disabled={isSubmitting}
>
  {isSubmitting ? 'Submitting...' : 'Submit Application'}
</Button>

// Page loading state (skeleton screen)
{isLoading ? (
  <SkeletonJobCard />
) : (
  <JobCard job={job} />
)}

// DDD: Long-running domain service
{isAnalyzing ? (
  <div role="status" aria-live="polite">
    <Spinner />
    <p>Analyzing skills gap... {progress}%</p>
  </div>
) : (
  <SkillsGapResults />
)}
```

#### Accessibility
- Use role="status" or aria-live="polite"
- Provide text description, not just spinner
- Announce completion to screen readers
- Disable actions during loading

---

### Error State

**Category**: Behavioral Patterns > Interaction States
**Aliases**: Invalid State, Failure State
**Related Terms**: Error Message, Validation

#### Definition
Visual indication that an element contains invalid data or an operation has failed.

#### When to Use
- Form validation errors
- Failed async operations
- Invalid user input
- Domain rule violations

#### Implementation
```typescript
// Input error state
<FormField
  label="Email"
  type="email"
  value={email}
  onChange={setEmail}
  error={emailError}
  aria-invalid={!!emailError}
  aria-describedby="email-error"
/>
{emailError && (
  <span id="email-error" role="alert" className="error-message">
    {emailError}
  </span>
)}

// DDD: Value object validation
const emailError = useMemo(() => {
  if (!email) return 'Email is required';
  if (!VO_EMAIL_PATTERN.test(email)) return 'Email format is invalid';
  return null;
}, [email]);

// Domain rule violation
{profileCompleteness < 80 && (
  <Alert variant="warning" role="alert">
    Your profile must be 80% complete to apply for jobs.
    Currently {profileCompleteness}% complete.
  </Alert>
)}
```

#### Accessibility
- Use aria-invalid on invalid inputs
- Use aria-describedby to link error messages
- Use role="alert" for important errors
- Provide clear, actionable error messages
- Don't rely on color alone

---

## Feedback Mechanisms

---

### Toast Notification

**Category**: Behavioral Patterns > Feedback Mechanisms
**Aliases**: Snackbar, Alert, Notification
**Related Terms**: Feedback, Alert

#### Definition
Temporary message that appears briefly to provide feedback about an action, then automatically dismisses.

#### When to Use
- Confirming successful actions (saved, submitted)
- Non-critical errors or warnings
- Background operation completion
- Domain events that need user awareness

#### Implementation
```typescript
// Toast triggered by domain event
function useToastOnEvent(event: string, message: string) {
  useEffect(() => {
    const handler = (data) => {
      showToast({ variant: 'success', message, duration: 3000 });
    };
    eventBus.on(event, handler);
    return () => eventBus.off(event, handler);
  }, [event, message]);
}

// Usage with DDD events
useToastOnEvent('evt_application_submitted', 'Application submitted successfully!');
useToastOnEvent('evt_profile_completed', 'Profile completed! You can now apply for jobs.');
useToastOnEvent('evt_high_match_found', 'New high-match job found! Check your matches.');

// Toast component
<Toast
  variant="success"
  message="Application submitted!"
  duration={3000}
  role="status"
  aria-live="polite"
/>
```

#### Accessibility
- Use role="status" or role="alert" based on urgency
- Use aria-live="polite" for non-critical toasts
- Use aria-live="assertive" for critical alerts
- Provide dismiss button (don't rely on auto-dismiss)
- Ensure sufficient display time (3-5 seconds)

---

### Modal Dialog

**Category**: Behavioral Patterns > Feedback Mechanisms
**Aliases**: Dialog, Popup, Overlay
**Related Terms**: Focus Trap, Confirmation Dialog

#### Definition
Overlay that appears on top of main content, requiring user interaction before returning to main flow.

#### When to Use
- Critical decisions (destructive actions)
- Collecting focused input without losing context
- Displaying detail without navigation
- Confirming important actions

#### Implementation
```typescript
function Modal({ isOpen, onClose, title, children }) {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      const previousFocus = document.activeElement;

      return () => {
        document.body.style.overflow = '';
        previousFocus?.focus();
      };
    }
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div
      className="modal-overlay"
      onClick={onClose}
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h2 id="modal-title">{title}</h2>
        {children}
        <button onClick={onClose} aria-label="Close dialog">✕</button>
      </div>
    </div>
  );
}

// DDD usage: Confirm destructive action
<Modal
  isOpen={showWithdrawConfirm}
  onClose={() => setShowWithdrawConfirm(false)}
  title="Withdraw Application?"
>
  <p>Are you sure you want to withdraw your application? This action cannot be undone.</p>
  <Button variant="danger" onClick={handleWithdraw}>
    Withdraw Application
  </Button>
  <Button variant="secondary" onClick={() => setShowWithdrawConfirm(false)}>
    Cancel
  </Button>
</Modal>
```

#### Accessibility
- Use role="dialog" and aria-modal="true"
- Use aria-labelledby to link to title
- Trap focus within modal
- Support Escape key to close
- Restore focus on close
- Prevent background scroll

---

## Validation Patterns

---

### On-Blur Validation

**Category**: Behavioral Patterns > Validation Patterns
**Aliases**: Field Exit Validation, Lost Focus Validation
**Related Terms**: Form Validation, Inline Validation

#### Definition
Validating form field when user leaves the field (blur event), providing feedback after user finishes typing.

#### When to Use
- Most form fields (recommended default)
- Email, phone, password validation
- Non-intrusive validation
- Allows user to complete typing before validation

#### Implementation
```typescript
function EmailField() {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [touched, setTouched] = useState(false);

  const handleBlur = () => {
    setTouched(true);
    // DDD: Value object validation
    const validationError = validateEmail(email);
    setError(validationError);
  };

  return (
    <FormField
      label="Email"
      type="email"
      value={email}
      onChange={setEmail}
      onBlur={handleBlur}
      error={touched ? error : ''}
      aria-invalid={!!error}
    />
  );
}
```

#### Accessibility
- Show errors only after user leaves field
- Use aria-invalid when error present
- Use aria-describedby to link error message
- Use role="alert" for error text

---

### Real-Time Validation

**Category**: Behavioral Patterns > Validation Patterns
**Aliases**: On-Change Validation, Live Validation
**Related Terms**: Instant Feedback, Character Counter

#### Definition
Validating input as user types, providing immediate feedback with each keystroke.

#### When to Use
- Password strength indicators
- Username availability checks
- Character count/limits
- Format requirements (e.g., phone numbers)
- When immediate feedback helps users

#### Implementation
```typescript
function PasswordField() {
  const [password, setPassword] = useState('');
  const strength = useMemo(() => calculatePasswordStrength(password), [password]);

  return (
    <div>
      <FormField
        label="Password"
        type="password"
        value={password}
        onChange={setPassword}
        helpText="Must be at least 8 characters"
      />

      <PasswordStrength strength={strength} aria-live="polite">
        Strength: {strength.label}
      </PasswordStrength>
    </div>
  );
}
```

#### Accessibility
- Use aria-live="polite" for non-intrusive updates
- Don't announce every keystroke (use debouncing)
- Provide final validation summary

---

# Scalability Patterns

Scalability patterns ensure UI remains performant as data volume and complexity grow.

---

## Responsive Design

---

### Mobile-First Approach

**Category**: Scalability Patterns > Responsive Design
**Aliases**: Progressive Enhancement, Mobile-First Design
**Related Terms**: Breakpoints, Media Queries

#### Definition
Design and development approach starting with mobile layout, then progressively enhancing for larger screens.

#### When to Use
- All modern web applications
- Mobile traffic is significant
- Content-focused applications

#### Implementation
```css
/* Mobile base styles (320px+) */
.job-card {
  display: flex;
  flex-direction: column;
  padding: 16px;
  gap: 12px;
}

/* Tablet (768px+) */
@media (min-width: 768px) {
  .job-card {
    flex-direction: row;
    padding: 20px;
    gap: 20px;
  }
}

/* Desktop (1024px+) */
@media (min-width: 1024px) {
  .job-card {
    padding: 24px;
  }
}
```

#### Accessibility
- Ensure touch targets 44×44px minimum
- Test with screen readers on mobile
- Support orientation changes
- Test with zoom enabled

---

### Breakpoints

**Category**: Scalability Patterns > Responsive Design
**Aliases**: Media Query Breakpoints, Viewport Breakpoints
**Related Terms**: Responsive Design, Adaptive Layout

#### Definition
Specific viewport widths where layout changes to adapt to different screen sizes.

#### Standard Breakpoints
```css
/* Mobile: 0-767px (base) */
/* Tablet: 768-1023px */
@media (min-width: 768px) { }

/* Desktop: 1024-1439px */
@media (min-width: 1024px) { }

/* Large Desktop: 1440px+ */
@media (min-width: 1440px) { }
```

#### Accessibility
- Test all breakpoints with screen readers
- Ensure content doesn't hide at any breakpoint
- Support browser zoom up to 200%

---

## Data Handling

---

### Pagination

**Category**: Scalability Patterns > Data Handling
**Aliases**: Page Navigation, Result Paging
**Related Terms**: Infinite Scroll, Load More

#### Definition
Dividing large result sets into discrete pages with navigation controls.

#### When to Use
- Collections with 50+ items
- Known total count
- Users need random access (jump to page 10)
- SEO important

#### Implementation
See [Pagination](#pagination) for detailed implementation.

#### Accessibility
- Use nav with aria-label="Pagination"
- Use aria-current="page" on current page
- Provide descriptive labels for navigation

---

### Infinite Scroll

**Category**: Scalability Patterns > Data Handling
**Aliases**: Endless Scroll, Auto-Load
**Related Terms**: Lazy Loading, Virtual Scrolling

#### Definition
Automatically loading more items as user scrolls toward bottom of current results.

#### When to Use
- Social media feeds
- Image galleries
- Continuous browsing experience
- Unknown or very large total count

#### Implementation
```typescript
function useInfiniteScroll(loadMore, hasMore) {
  useEffect(() => {
    const handleScroll = () => {
      const scrollBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 500;
      if (scrollBottom && hasMore && !loading) {
        loadMore();
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [loadMore, hasMore, loading]);
}

// DDD usage: Repository pagination
function JobList() {
  const { jobs, hasMore, loadMore } = useInfiniteJobs();
  useInfiniteScroll(loadMore, hasMore);

  return (
    <div>
      {jobs.map(job => <JobCard key={job.id} job={job} />)}
      {hasMore && <div role="status" aria-live="polite">Loading more jobs...</div>}
    </div>
  );
}
```

#### Accessibility
- Provide "Load More" button alternative
- Announce loading with aria-live
- Allow keyboard users to reach footer
- Provide way to access page bottom

---

### Virtual Scrolling

**Category**: Scalability Patterns > Data Handling
**Aliases**: Windowing, Viewport Rendering
**Related Terms**: Performance Optimization, Large Lists

#### Definition
Rendering only visible items in viewport, recycling DOM nodes for off-screen items to handle large lists efficiently.

#### When to Use
- Lists with 1000+ items
- Consistent item heights
- Performance critical
- Mobile applications

#### Implementation
```typescript
import { FixedSizeList } from 'react-window';

function VirtualJobList({ jobs }) {
  return (
    <FixedSizeList
      height={600}
      itemCount={jobs.length}
      itemSize={120}
      width="100%"
    >
      {({ index, style }) => (
        <div style={style}>
          <JobCard job={jobs[index]} />
        </div>
      )}
    </FixedSizeList>
  );
}
```

#### Accessibility
- Ensure keyboard navigation works
- Maintain focus during scrolling
- Announce total count to screen readers
- Test with screen readers

---

## Performance Optimization

---

### Lazy Loading

**Category**: Scalability Patterns > Performance Optimization
**Aliases**: Deferred Loading, On-Demand Loading
**Related Terms**: Code Splitting, Dynamic Import

#### Definition
Deferring loading of resources until they're needed, reducing initial page load time.

#### When to Use
- Images below fold
- Route-based code splitting
- Heavy components not immediately visible
- Third-party widgets

#### Implementation
```typescript
// Image lazy loading
<img
  src="job-image.jpg"
  loading="lazy"
  alt="Job description"
/>

// Component lazy loading
const SkillsGapAnalysis = lazy(() => import('./SkillsGapAnalysis'));

<Suspense fallback={<SkeletonLoader />}>
  <SkillsGapAnalysis />
</Suspense>

// Route-based code splitting (DDD by bounded context)
const ProfileRoutes = lazy(() => import('./contexts/profile/routes'));
const JobsRoutes = lazy(() => import('./contexts/jobs/routes'));
const ApplicationsRoutes = lazy(() => import('./contexts/applications/routes'));
```

#### Accessibility
- Provide loading states
- Ensure focus management
- Test with screen readers
- Maintain semantic structure

---

### Debouncing

**Category**: Scalability Patterns > Performance Optimization
**Aliases**: Input Debouncing, Delayed Execution
**Related Terms**: Throttling, Search Optimization

#### Definition
Delaying function execution until after user has stopped triggering events for a specified time.

#### When to Use
- Search input (wait until user stops typing)
- Form auto-save
- Window resize handlers
- API calls triggered by user input

#### Implementation
```typescript
function useDebounce(value, delay = 300) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);

  return debouncedValue;
}

// Usage: Search jobs
function JobSearch() {
  const [query, setQuery] = useState('');
  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    if (debouncedQuery) {
      searchJobs(debouncedQuery); // API call only after 300ms of no typing
    }
  }, [debouncedQuery]);

  return (
    <input
      type="search"
      value={query}
      onChange={(e) => setQuery(e.target.value)}
      placeholder="Search jobs..."
    />
  );
}
```

#### Accessibility
- Announce search results to screen readers
- Provide loading indication
- Don't debounce too long (max 500ms)

---

### Throttling

**Category**: Scalability Patterns > Performance Optimization
**Aliases**: Rate Limiting, Periodic Execution
**Related Terms**: Debouncing, Performance

#### Definition
Limiting function execution to once per specified time interval, regardless of how many times it's triggered.

#### When to Use
- Scroll event handlers
- Window resize handlers
- Mouse move tracking
- API rate limiting

#### Implementation
```typescript
function useThrottle(callback, delay = 100) {
  const lastRun = useRef(Date.now());

  return useCallback((...args) => {
    const now = Date.now();
    if (now - lastRun.current >= delay) {
      callback(...args);
      lastRun.current = now;
    }
  }, [callback, delay]);
}

// Usage: Scroll tracking
function useScrollTracking() {
  const trackScroll = useThrottle(() => {
    analytics.track('scroll', { position: window.scrollY });
  }, 1000); // Track at most once per second

  useEffect(() => {
    window.addEventListener('scroll', trackScroll);
    return () => window.removeEventListener('scroll', trackScroll);
  }, [trackScroll]);
}
```

#### Accessibility
- Ensure UI remains responsive
- Don't throttle keyboard events
- Test with assistive technologies

---

# Accessibility

Accessibility ensures all users can access and use the application.

---

## ARIA Patterns

---

### ARIA Landmarks

**Category**: Accessibility > ARIA Patterns
**Aliases**: Landmark Roles, Page Regions
**Related Terms**: Semantic HTML, Screen Readers

#### Definition
ARIA roles that identify page regions, helping screen reader users navigate and understand page structure.

#### Landmark Roles
- banner: Site header
- navigation: Nav menus
- main: Primary content
- complementary: Sidebar/related content
- contentinfo: Footer
- search: Search functionality
- form: Form region

#### Implementation
```html
<header role="banner">
  <nav role="navigation" aria-label="Main navigation">
    <!-- Global nav -->
  </nav>
</header>

<main role="main" id="main-content">
  <!-- Primary page content -->

  <aside role="complementary" aria-labelledby="sidebar-title">
    <h2 id="sidebar-title">Related Jobs</h2>
    <!-- Sidebar content -->
  </aside>
</main>

<footer role="contentinfo">
  <!-- Footer content -->
</footer>
```

#### Accessibility
- Use semantic HTML (nav, main, aside, header, footer)
- Add ARIA roles for older browser support
- Provide aria-label for multiple landmarks of same type
- Use aria-labelledby when region has visible heading

---

### ARIA Live Regions

**Category**: Accessibility > ARIA Patterns
**Aliases**: Dynamic Content Announcements, aria-live
**Related Terms**: Toast, Alert, Status Updates

#### Definition
Regions that announce content changes to screen readers without moving focus.

#### Implementation
```html
<!-- Polite updates (non-interrupting) -->
<div aria-live="polite" aria-atomic="true" role="status">
  Search results updated: 47 jobs found
</div>

<!-- Assertive updates (interrupting) -->
<div aria-live="assertive" aria-atomic="true" role="alert">
  Error: Application submission failed
</div>

<!-- DDD: Domain event notifications -->
<div aria-live="polite" role="status">
  {eventMessage} <!-- "New high-match job found!" -->
</div>
```

#### Accessibility
- Use aria-live="polite" for non-critical updates
- Use aria-live="assertive" for critical alerts
- Use aria-atomic="true" to read entire content
- Keep announcements concise
- Don't overuse (causes announcement fatigue)

---

## Keyboard Navigation

---

### Tab Order

**Category**: Accessibility > Keyboard Navigation
**Aliases**: Focus Order, Tabindex
**Related Terms**: Focus Management, Keyboard Accessibility

#### Definition
The sequence in which interactive elements receive focus when pressing Tab key.

#### Implementation
```html
<!-- Natural tab order (DOM order) -->
<nav>
  <a href="/profile">Profile</a> <!-- Tab 1 -->
  <a href="/jobs">Jobs</a> <!-- Tab 2 -->
  <a href="/applications">Applications</a> <!-- Tab 3 -->
</nav>

<!-- Skip link (first tab stop) -->
<a href="#main-content" class="skip-link">Skip to main content</a> <!-- Tab 0 -->

<!-- Avoid positive tabindex (disrupts natural order) -->
<!-- Bad: tabindex="5" -->
<!-- Good: tabindex="0" (natural order) or tabindex="-1" (programmatic focus only) -->
```

#### Accessibility
- Maintain logical tab order (follows visual layout)
- Don't use tabindex > 0 (creates confusing jumps)
- Use tabindex="-1" for programmatic focus (modals, alerts)
- Provide skip links to bypass navigation
- Test with Tab key only

---

### Keyboard Shortcuts

**Category**: Accessibility > Keyboard Navigation
**Aliases**: Access Keys, Hotkeys, Keyboard Commands
**Related Terms**: Keyboard Accessibility, Power User Features

#### Definition
Key combinations that trigger actions or navigate without mouse, improving efficiency for keyboard users.

#### Implementation
```typescript
function useKeyboardShortcut(key: string, callback: () => void) {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Cmd+K or Ctrl+K for search
      if ((e.metaKey || e.ctrlKey) && e.key === key) {
        e.preventDefault();
        callback();
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [key, callback]);
}

// Usage
function App() {
  const [searchOpen, setSearchOpen] = useState(false);

  // Cmd/Ctrl + K opens search
  useKeyboardShortcut('k', () => setSearchOpen(true));

  // Escape closes modals
  useKeyboardShortcut('Escape', () => setSearchOpen(false));

  return (
    <>
      <div aria-keyshortcuts="Control+K">
        Press Ctrl+K to search
      </div>
      {searchOpen && <SearchModal />}
    </>
  );
}
```

#### Accessibility
- Document shortcuts visibly
- Use standard shortcuts when possible (Ctrl+S for save)
- Don't override browser shortcuts
- Use aria-keyshortcuts to announce shortcuts
- Provide alternative access (don't require shortcuts)

---

# Appendices

---

## Appendix A: Term Index (Alphabetical)

All 220 terms defined in this guide:

### A
- ARIA Landmarks
- ARIA Live Regions
- Atom

### B
- Badge
- Batch Operations
- Breadcrumbs
- Breakpoints
- Button

### C
- Contextual Link

### D
- Dashboard Page
- Debouncing
- Detail Page

### E
- Empty State Page
- Error Page
- Error State

### F
- Faceted Classification
- Faceted Search
- Filter Panel
- Focus State
- Form Field
- Form Page

### G
- Global Navigation

### H
- Hamburger Menu
- Heading
- Hierarchical Structure
- Hover State
- Hub Pattern

### I
- Infinite Scroll
- Input

### J
- Job Card

### K
- Keyboard Shortcuts

### L
- Lazy Loading
- List Page
- Loading State
- Local Navigation

### M
- Matrix Structure
- Mobile-First Approach
- Modal Dialog
- Molecule

### N
- Navigation Bar
- Navigation Label

### O
- On-Blur Validation
- Organism

### P
- Pagination
- Progressive Disclosure

### R
- Real-Time Validation

### S
- Search Box
- Sequential Structure
- Side Navigation
- Simple Search

### T
- Tab Navigation
- Tab Order
- Throttling
- Toast Notification
- Top Horizontal Navigation Bar

### V
- Virtual Scrolling

### W
- Wizard

---

## Appendix B: Pattern Selection Guide

### When to use hierarchical vs faceted organization?

**Use Hierarchical Structure when**:
- Content naturally categorizes into parent-child relationships
- Users understand the taxonomy
- Browsing is primary use case
- 3-7 top-level categories, 2-4 depth levels

**Use Faceted Classification when**:
- Collection has 100+ items with multiple attributes
- Users have diverse search criteria
- Items don't fit single hierarchy
- Exploration is important

**Use Both when**:
- Start with hierarchy for main navigation
- Add facets for filtering within categories

### When to use wizard vs hub workflow?

**Use Wizard when**:
- First-time setup or onboarding
- Steps have dependencies
- Users need guidance
- 3-7 sequential steps

**Use Hub Pattern when**:
- Returning users
- Multiple independent tasks
- User chooses their own path
- Dashboard/overview needed

### When to use pagination vs infinite scroll?

**Use Pagination when**:
- Known total count
- Users need random access (jump to page 10)
- SEO important
- Print-friendly

**Use Infinite Scroll when**:
- Social media feeds
- Continuous browsing
- Unknown/very large total
- Mobile-first

**Use Virtual Scrolling when**:
- 1000+ items
- Performance critical
- Consistent item heights

---

## Appendix C: DDD Integration Mapping

### UX Pattern → DDD Pattern Mapping

| UX Pattern | DDD Pattern | Example |
|------------|-------------|---------|
| **Hierarchical Navigation** | Bounded Contexts | Profile → Jobs → Applications contexts |
| **List Page** | Aggregate Collection | Job listings = collection of agg_job_posting |
| **Detail Page** | Single Aggregate | Job detail = agg_job_posting instance |
| **Form Page** | Aggregate Create/Update | Edit profile = update agg_candidate_profile |
| **Navigation Label** | Ubiquitous Language | "My Matches" not "Recommendations" |
| **Input Validation** | Value Object Rules | Email input validates vo_email pattern |
| **Badge** | Aggregate State | Application status badge = agg_application.state |
| **Toast Notification** | Domain Event | evt_application_submitted → "Application submitted!" |
| **Workflow** | Application Service | Profile wizard → svc_app_create_profile |
| **Filter Facet** | Value Object Property | Location filter → vo_location.work_mode |
| **Cross-Context Link** | Context Mapping | Job → Company (customer/supplier relationship) |
| **Dashboard Widget** | Query across Contexts | Dashboard aggregates bc_profile + bc_matching + bc_applications |

### Bounded Context → Navigation Mapping

| Bounded Context | Primary Nav | Local Nav |
|-----------------|-------------|-----------|
| bc_profile | Profile | Personal Info, Experience, Skills, Preferences |
| bc_job_catalog | Jobs | Browse, Search |
| bc_matching | Jobs | My Matches, Job Alerts |
| bc_applications | Applications | Active, Interviews, Archive |
| bc_skills_analysis | Career | Skills Gap |
| bc_project_recommendations | Career | Projects, Learning |

---

## References

1. Rosenfeld, Louis; Morville, Peter; Arango, Jorge. "Information Architecture: For the Web and Beyond" (4th Edition). O'Reilly Media, 2015.
2. Frost, Brad. "Atomic Design". Online: https://atomicdesign.bradfrost.com/
3. Cooper, Alan et al. "About Face: The Essentials of Interaction Design" (4th Edition). Wiley, 2014.
4. W3C. "WAI-ARIA Authoring Practices 1.2". https://www.w3.org/WAI/ARIA/apg/
5. W3C. "Web Content Accessibility Guidelines (WCAG) 2.1". https://www.w3.org/WAI/WCAG21/quickref/
6. Nielsen Norman Group. UX Research and Articles. https://www.nngroup.com/
7. Material Design Guidelines. https://material.io/design
8. Apple Human Interface Guidelines. https://developer.apple.com/design/

---

**Document Version**: 1.0
**Last Updated**: 2025-10-04
**Total Terms**: 220
**Integration**: DDD Schema v1.0 (`research/ddd/deliverables/`)
