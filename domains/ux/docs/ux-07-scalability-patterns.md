# Scalability Patterns

## Overview

Scalability patterns ensure the UI remains performant and usable as data volume, user base, and feature complexity grow. These patterns address responsive design, performance optimization, progressive enhancement, and graceful degradation.

**Integration with DDD**: Scalability aligns with aggregate boundaries, repository pagination, and domain service caching strategies. UI scalability respects domain architecture constraints.

---

## Scalability Categories

### 1. Responsive & Adaptive Design
### 2. Data Scalability
### 3. Performance Optimization
### 4. Progressive Enhancement
### 5. Code Scalability

---

## 1. Responsive & Adaptive Design

Adapting to different devices and screen sizes.

### Mobile-First Approach

**Breakpoints**:
```yaml
breakpoints:
  mobile: 0-767px (base styles)
  tablet: 768-1023px
  desktop: 1024-1439px
  large_desktop: 1440px+

strategy: mobile_first
  - define: mobile_styles_first
  - enhance: tablet, desktop, large_desktop
```

**Example - Job Card**:
```css
/* Mobile (base) */
.job-card {
  display: flex;
  flex-direction: column;
  padding: 16px;
  gap: 12px;
}

.job-card .actions {
  flex-direction: column;
  width: 100%;
}

/* Tablet */
@media (min-width: 768px) {
  .job-card {
    flex-direction: row;
    padding: 20px;
    gap: 20px;
  }

  .job-card .actions {
    flex-direction: row;
    width: auto;
  }
}

/* Desktop */
@media (min-width: 1024px) {
  .job-card {
    padding: 24px;
  }
}
```

---

### Adaptive Layouts

**Grid Columns by Breakpoint**:
```yaml
layout: job_listings_grid

columns:
  mobile: 1
  tablet: 2
  desktop: 3
  large_desktop: 4

ddd_context: bc_job_catalog
displays: collection_of_agg_job_posting
```

```tsx
<Grid
  columns={{
    mobile: 1,
    tablet: 2,
    desktop: 3,
    largeDesktop: 4
  }}
  gap="md"
>
  {jobs.map(job => (
    <JobCard key={job.id} job={job} />
  ))}
</Grid>
```

---

### Responsive Navigation

**Pattern Switching by Breakpoint**:
```yaml
navigation_patterns:
  mobile:
    primary: bottom_tab_bar
    secondary: hamburger_menu

  tablet:
    primary: top_nav_bar
    secondary: side_drawer

  desktop:
    primary: top_nav_bar (expanded)
    secondary: always_visible_sidebar
```

---

## 2. Data Scalability

Handling large datasets efficiently.

### Pagination

**Server-Side Pagination**:
```yaml
pattern: server_side_pagination

ddd_integration:
  repository: repo_job_posting
  query: get_jobs_paginated(page, page_size)

implementation:
  - page_size: 20 items
  - total_pages: calculated_from_total_count
  - navigation: numbered_pagination
```

**Example**:
```tsx
const JobListings: React.FC = () => {
  const [page, setPage] = useState(1);
  const pageSize = 20;

  const {data, isLoading} = useQuery(
    ['jobs', page],
    () => fetchJobs({page, pageSize})  // Calls repo_job_posting
  );

  return (
    <div>
      {isLoading ? (
        <SkeletonGrid count={pageSize} />
      ) : (
        <>
          <Grid>
            {data.jobs.map(job => (
              <JobCard key={job.id} job={job} />
            ))}
          </Grid>

          <Pagination
            currentPage={page}
            totalPages={Math.ceil(data.totalCount / pageSize)}
            onPageChange={setPage}
          />
        </>
      )}
    </div>
  );
};
```

---

### Infinite Scroll

**Progressive Loading**:
```yaml
pattern: infinite_scroll

use_when:
  - mobile_optimized_views
  - continuous_browsing_experience
  - social_feed_style_content

implementation:
  - trigger: user_scrolls_to_80%_of_page
  - load: next_page_in_background
  - append: new_items_to_list

ddd_integration:
  - repository: paginated_query
  - cursor_based: more_scalable_than_offset
```

**Example**:
```tsx
const InfiniteJobList: React.FC = () => {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage
  } = useInfiniteQuery(
    'jobs-infinite',
    ({pageParam = 0}) => fetchJobs({cursor: pageParam, limit: 20}),
    {
      getNextPageParam: (lastPage) => lastPage.nextCursor
    }
  );

  // Intersection Observer for infinite scroll
  const {ref} = useInView({
    onChange: (inView) => {
      if (inView && hasNextPage && !isFetchingNextPage) {
        fetchNextPage();
      }
    }
  });

  return (
    <div>
      {data?.pages.map((page, i) => (
        <React.Fragment key={i}>
          {page.jobs.map(job => (
            <JobCard key={job.id} job={job} />
          ))}
        </React.Fragment>
      ))}

      {/* Trigger element */}
      <div ref={ref}>
        {isFetchingNextPage && <Spinner />}
      </div>
    </div>
  );
};
```

---

### Virtual Scrolling

**For Very Large Lists (1000+ items)**:
```yaml
pattern: virtual_scrolling

use_when:
  - list_items: > 500
  - example: all_jobs_in_catalog (10,000+)

benefits:
  - renders: only_visible_items
  - dom_nodes: ~20 instead of 10,000
  - performance: constant regardless of list size

ddd_integration:
  - works_with: paginated_repository_queries
  - combine: virtual_scroll + pagination
```

**Example**:
```tsx
import {FixedSizeList} from 'react-window';

const VirtualJobList: React.FC<{jobs: JobPosting[]}> = ({jobs}) => {
  return (
    <FixedSizeList
      height={800}
      itemCount={jobs.length}
      itemSize={200}  // Height of each JobCard
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

### Data Caching

**Client-Side Caching Strategy**:
```yaml
caching:
  aggregates:
    - agg_job_posting:
        cache_time: 5_minutes
        stale_time: 1_minute

    - agg_candidate_profile:
        cache_time: infinity (until_mutated)
        stale_time: 0

    - agg_job_match:
        cache_time: 10_minutes
        stale_time: 5_minutes

  invalidation:
    - on_mutation: invalidate_affected_queries
    - on_domain_event: invalidate_related_data
```

**Example**:
```tsx
// Cache job posting for 5 minutes
const {data: job} = useQuery(
  ['job', jobId],
  () => fetchJob(jobId),
  {
    cacheTime: 5 * 60 * 1000,  // 5 minutes
    staleTime: 1 * 60 * 1000   // 1 minute
  }
);

// Invalidate on domain event
useEffect(() => {
  const unsubscribe = subscribeToDomainEvent('evt_job_updated', (event) => {
    queryClient.invalidateQueries(['job', event.jobId]);
  });
  return unsubscribe;
}, []);
```

---

## 3. Performance Optimization

Ensuring fast load times and smooth interactions.

### Code Splitting

**Route-Based Splitting**:
```yaml
pattern: lazy_load_routes

strategy:
  - split_by: bounded_context
  - initial_load: authentication + landing
  - on_demand: context_specific_bundles

bundles:
  - auth.js: authentication flows
  - profile.js: bc_profile (lazy loaded)
  - jobs.js: bc_job_catalog, bc_matching (lazy loaded)
  - applications.js: bc_applications (lazy loaded)
  - career.js: bc_skills_analysis (lazy loaded)
```

**Example**:
```tsx
import {lazy, Suspense} from 'react';

const ProfilePage = lazy(() => import('./pages/ProfilePage'));
const JobsPage = lazy(() => import('./pages/JobsPage'));
const ApplicationsPage = lazy(() => import('./pages/ApplicationsPage'));

function App() {
  return (
    <Router>
      <Suspense fallback={<PageLoader />}>
        <Routes>
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/jobs" element={<JobsPage />} />
          <Route path="/applications" element={<ApplicationsPage />} />
        </Routes>
      </Suspense>
    </Router>
  );
}
```

---

### Component-Level Splitting

```yaml
pattern: lazy_load_heavy_components

examples:
  - rich_text_editor: lazy_load_on_edit_click
  - chart_library: lazy_load_on_analytics_view
  - pdf_viewer: lazy_load_on_resume_view
```

---

### Image Optimization

**Lazy Loading Images**:
```yaml
pattern: lazy_load_images

strategy:
  - above_fold: load_immediately
  - below_fold: lazy_load_on_scroll
  - placeholder: blur_up_technique

ddd_example:
  - company_logos: lazy_load (vo_company)
  - candidate_photos: lazy_load (ent_candidate)
```

**Example**:
```tsx
<img
  src={job.company.logoUrl}
  alt={job.company.name}
  loading="lazy"  // Native lazy loading
  decoding="async"
/>
```

---

### Memoization

**Expensive Calculations**:
```yaml
pattern: memoize_calculations

use_for:
  - domain_calculations:
      - match_score_calculation (svc_domain_calculate_match)
      - skills_gap_analysis
      - profile_completeness

  - ui_transformations:
      - filtered_lists
      - sorted_data
      - formatted_values
```

**Example**:
```tsx
// Memoize domain calculation
const matchScore = useMemo(() => {
  return calculateMatchScore(
    candidateProfile.skills,
    jobPosting.requirements
  );  // Expensive domain calculation
}, [candidateProfile.skills, jobPosting.requirements]);

// Memoize component to prevent re-renders
const JobCard = React.memo<JobCardProps>(({job, matchScore}) => {
  // Component implementation
}, (prevProps, nextProps) => {
  return prevProps.job.id === nextProps.job.id &&
         prevProps.matchScore === nextProps.matchScore;
});
```

---

### Debouncing & Throttling

**Search Input (Debounce)**:
```yaml
pattern: debounce_search

delay: 300ms
benefit: reduces_api_calls
example: job_search_input

ddd_integration:
  - service: svc_app_search_jobs
  - prevents: excessive_repository_queries
```

**Scroll Events (Throttle)**:
```yaml
pattern: throttle_scroll

interval: 100ms
benefit: limits_event_handler_calls
example: infinite_scroll_trigger
```

---

## 4. Progressive Enhancement

Starting with core functionality, enhancing for capable browsers.

### Baseline Functionality

**Core Features (Work Everywhere)**:
```yaml
baseline:
  - view_job_listings: server_rendered_html
  - read_job_details: basic_html
  - submit_application: standard_form_submission

enhanced:
  - client_side_routing: SPA experience
  - optimistic_updates: immediate_feedback
  - real_time_notifications: websocket_updates
```

---

### Feature Detection

**Progressive WebSocket Support**:
```tsx
// Baseline: Polling for updates
let updateStrategy: UpdateStrategy;

if ('WebSocket' in window) {
  // Enhanced: Real-time via WebSocket
  updateStrategy = new WebSocketUpdates();
} else {
  // Fallback: Polling every 30 seconds
  updateStrategy = new PollingUpdates(30000);
}

updateStrategy.subscribe('evt_application_updated', handleUpdate);
```

---

### Offline Support

**Service Worker for Offline Caching**:
```yaml
offline_strategy:
  cache_first:
    - static_assets: js, css, fonts
    - images: company_logos

  network_first:
    - api_calls: job_search, profile_data
    - fallback: cached_data or offline_page

  cache_then_network:
    - job_listings: show_cached + fetch_fresh + update
```

---

## 5. Code Scalability

Organizing code for growth.

### Module Organization by Domain

**Directory Structure**:
```yaml
src/
  contexts/  # Bounded contexts
    profile/
      components/  # bc_profile components
      services/    # Application services
      types/       # Aggregates, VOs (TypeScript)
      hooks/       # React hooks for this context

    jobs/
      components/  # bc_job_catalog, bc_matching
      services/
      types/
      hooks/

    applications/
      components/  # bc_applications
      services/
      types/
      hooks/

  shared/
    components/  # Atoms, molecules (domain-agnostic)
    utils/
    types/

  app/
    routes.tsx
    App.tsx
```

**Benefits**:
- Bounded contexts align with DDD
- Easy to find context-specific code
- Facilitates code splitting by context
- Team can own specific contexts

---

### Component Composition

**Composing Domain Components**:
```yaml
composition_strategy:
  - primitives: shared/components (atoms, molecules)
  - domain_organisms: context/components
  - pages: compose_organisms + primitives

example:
  ProfilePage:
    - uses: ProfileHeader (domain organism)
    - uses: SkillsSection (domain organism)
    - uses: Button (shared primitive)
    - uses: Grid (shared layout)
```

---

### Type Safety (TypeScript)

**Domain Types from DDD**:
```typescript
// Value Objects as TypeScript types
type Email = string & {readonly __brand: 'Email'};
type Skills = string[] & {readonly __brand: 'Skills'};

// Aggregates as interfaces
interface CandidateProfile {
  id: string;
  email: Email;
  skills: Skills;
  // ... other VO and entity attributes
}

// Domain events
interface ProfileUpdatedEvent {
  type: 'evt_profile_updated';
  aggregateId: string;
  timestamp: Date;
  payload: Partial<CandidateProfile>;
}
```

**Type Guards**:
```typescript
function isEmail(value: string): value is Email {
  return emailRegex.test(value);  // VO validation
}

function createEmail(value: string): Email {
  if (!isEmail(value)) {
    throw new Error('Invalid email format');
  }
  return value as Email;
}
```

---

## Accessibility at Scale

### Design System Accessibility

**Accessible by Default**:
```yaml
design_system_principles:
  - all_components: wcag_aa_compliant
  - keyboard_nav: built_in
  - screen_reader: aria_labels_included
  - color_contrast: meets_4.5:1_ratio

benefits:
  - developers: get_accessibility_free
  - consistency: across_entire_app
  - maintenance: centralized_fixes
```

---

### Automated Testing

**Accessibility Tests**:
```typescript
import {axe, toHaveNoViolations} from 'jest-axe';

expect.extend(toHaveNoViolations);

describe('JobCard', () => {
  it('has no accessibility violations', async () => {
    const {container} = render(<JobCard job={mockJob} />);
    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });
});
```

---

## Monitoring & Analytics

### Performance Monitoring

**Web Vitals**:
```yaml
metrics:
  - LCP (Largest Contentful Paint): < 2.5s
  - FID (First Input Delay): < 100ms
  - CLS (Cumulative Layout Shift): < 0.1

monitoring:
  - tool: Google Analytics, Sentry
  - track: per_page, per_bounded_context
  - alert: when_thresholds_exceeded
```

---

### User Analytics

**DDD-Aware Analytics**:
```yaml
events_to_track:
  - aggregate_created:
      - evt_profile_created
      - evt_application_submitted

  - aggregate_updated:
      - evt_profile_updated
      - evt_skills_changed

  - domain_service_calls:
      - svc_domain_calculate_match
      - svc_app_search_jobs

  - user_flows:
      - job_search_to_application
      - profile_edit_workflow
```

**Example**:
```tsx
// Track domain events
useEffect(() => {
  const unsubscribe = subscribeToDomainEvent('evt_application_submitted', (event) => {
    analytics.track('Application Submitted', {
      jobId: event.jobId,
      candidateId: event.candidateId,
      applicationId: event.applicationId,
      context: 'bc_applications'
    });
  });
  return unsubscribe;
}, []);
```

---

## Key Takeaways

1. **Mobile-First**: Start with mobile constraints, enhance for larger screens.

2. **Data Pagination**: Always paginate large collections at repository level.

3. **Code Splitting**: Split by bounded context for optimal bundle sizes.

4. **Performance Budget**: Monitor Web Vitals, set thresholds per context.

5. **Progressive Enhancement**: Core functionality works everywhere, enhancements for modern browsers.

6. **Virtual Scrolling**: For lists > 500 items, consider virtual scrolling.

7. **Memoization**: Cache expensive domain calculations.

8. **Accessibility**: Build into design system, test automatically.

9. **DDD Alignment**: Organize code by bounded context, cache by aggregate.

10. **Monitor Domain Events**: Track aggregate lifecycle for analytics and debugging.

---

## References

**Primary Sources**:
- Google Web Vitals - Performance metrics
- Web.dev - Performance optimization guides
- React documentation - Performance optimization
- A11Y Project - Accessibility at scale
- Martin Fowler - Patterns of Enterprise Application Architecture (caching, pagination)

**DDD Integration**:
- `research/ddd/deliverables/ddd-schema-example.yaml` - bounded contexts for code organization
- `research/ddd/working-docs/03-tactical-patterns.md` - repositories, pagination strategies
- `research/ux/working-docs/04-page-architecture.md` - responsive layouts
- `research/ux/working-docs/05-component-architecture.md` - component optimization

---

*Document created: 2025-10-04*
*Part of UX Research Phase 8: Scalability Patterns*
