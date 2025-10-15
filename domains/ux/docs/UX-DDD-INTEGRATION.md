# UX Research Integration with DDD

## Overview

This UX research builds upon and integrates with the completed DDD research. Where terminology overlaps, **DDD terminology takes precedence** to ensure consistency across architecture and user interface design.

---

## Key Integration Points

### 1. Terminology Synchronization

**DDD Terms Used in UX:**
- **Bounded Context** → UI contexts/sections organized by bounded context
- **Aggregate** → Form/view organized around aggregate root
- **Domain Event** → UI state changes trigger/respond to domain events
- **Ubiquitous Language** → UI labels, buttons, messages use same language as domain

**Example:**
```yaml
# DDD defines:
bounded_context: "Profile Management"
aggregate: "Candidate Profile"
domain_event: "ProfileUpdated"

# UX follows:
page_name: "Profile Management"  # Same as bounded context
form_entity: "Candidate Profile"  # Same as aggregate
ui_event: "ProfileUpdated"  # Same as domain event
button_label: "Update Profile"  # Uses domain verb "update"
```

### 2. Schema Alignment

**UX Schema References DDD Schema:**
```yaml
# UX Schema
page:
  id: "page_profile_edit"
  bounded_context_ref: "bc_profile"  # Reference to DDD bounded context
  aggregate_ref: "agg_candidate_profile"  # Reference to DDD aggregate

  sections:
    - id: "sec_skills"
      displays_value_object: "vo_skills"  # Reference to DDD value object
```

### 3. Workflow Mapping to Use Cases

**DDD Application Services → UX Workflows:**
```yaml
# DDD Application Service:
application_service:
  id: "svc_app_update_profile"
  use_case: "Candidate updates their profile"

# UX Workflow:
workflow:
  id: "wf_update_profile"
  application_service_ref: "svc_app_update_profile"  # Direct mapping
  pages: [...]
```

### 4. Component Naming

**UI Components Use Domain Language:**
```yaml
# DDD Entity:
entity: "Candidate"
  attributes:
    - skills: "Skills"  # Value Object
    - email: "Email"  # Value Object

# UX Components:
component:
  id: "comp_skills_input"  # Uses domain term "skills"
  label: "Skills"  # Domain term

component:
  id: "comp_email_input"
  label: "Email Address"  # Domain-friendly term
  validation_ref: "vo_email"  # References DDD value object validation
```

---

## Cross-Reference Requirements

### UX Schema Must Reference:

1. **DDD Bounded Contexts**
   - Each page/workflow indicates which bounded context it belongs to
   - Navigation structure aligns with context boundaries

2. **DDD Aggregates**
   - Forms/views organized around aggregate roots
   - CRUD operations respect aggregate boundaries

3. **DDD Value Objects**
   - Input components reference value object validation rules
   - Display components use value object formatting

4. **DDD Domain Events**
   - UI events that trigger domain events are documented
   - Event handlers update UI state

5. **DDD Application Services**
   - Workflows map to application service use cases
   - User actions invoke application services

---

## Terminology Precedence Rules

### When DDD Defines It, UX Follows:

| Concept | DDD Term | UX Uses | Why |
|---------|----------|---------|-----|
| User entity | "Candidate" | "Candidate" (not "User") | Domain precision |
| Submit action | "submit" | "Submit Order" button | Domain verb |
| Profile section | "Profile Management" | "Profile Management" page | Bounded context name |
| Status | "OrderStatus.SUBMITTED" | "Submitted" label | Domain state |
| Skills | "Skills" (value object) | "Skills" section | Domain concept |

### When UX Defines It Independently:

| Concept | UX Term | Why Independent |
|---------|---------|-----------------|
| Navigation pattern | "Side Navigation" | UI implementation detail |
| Layout | "Grid Layout" | Visual organization |
| Interaction | "Click" | User action type |
| Animation | "Fade In" | Visual feedback |
| Responsive | "Mobile Breakpoint" | Device adaptation |

---

## Shared Glossary Maintenance

### Process:

1. **DDD glossary is source of truth** for domain concepts
2. **UX glossary extends** with UI-specific terms
3. **Cross-reference** terms that appear in both
4. **Flag conflicts** for resolution (DDD wins)

### Glossary Structure:

```markdown
## Term: Candidate

**Source**: DDD (Bounded Context: Profile Management)
**Category**: Domain Entity
**DDD Definition**: A job seeker using the system

**UX Usage**:
- Page titles: "Candidate Profile"
- Form labels: "Candidate Information"
- Navigation: "Candidate" section
- Messages: "Candidate profile updated"

**NOT Used in UX**:
- ❌ "User profile" (too generic)
- ❌ "Job seeker profile" (verbose)
- ✓ "Candidate profile" (matches domain)
```

---

## Validation Rules

### UX Schema Validation Against DDD:

1. **bounded_context_exists**: UX page references valid DDD bounded context
2. **aggregate_exists**: UX form references valid DDD aggregate
3. **value_object_validation_matches**: Input validation aligns with DDD value object rules
4. **domain_event_names_match**: UI events use same names as domain events
5. **ubiquitous_language_compliance**: UI labels/messages use domain terminology

---

## Updated UX Research Phases

### Phase 0: DDD Integration (NEW - Day 1)

**Task 0.1: Review DDD Outputs**
- Read `research/ddd/README.md`
- Study `research/ddd/deliverables/ddd-schema-example.yaml`
- Extract ubiquitous language from DDD contexts
- Create DDD→UX term mapping

**Deliverable**: `ux-ddd-term-mapping.md`

**Task 0.2: Establish Cross-Reference System**
- Define how UX schema references DDD schema
- Set up validation rules
- Create shared glossary template

**Deliverable**: `ux-ddd-integration-rules.md`

---

## Example Integration

### DDD Model (From ddd-schema-example.yaml):

```yaml
bounded_context:
  id: "bc_profile"
  name: "Profile Management"

aggregate:
  id: "agg_candidate_profile"
  name: "Candidate Profile"

entity:
  id: "ent_candidate"
  name: "Candidate"
  attributes:
    - name: "skills"
      type: "Skills"
      value_object_ref: "vo_skills"

value_object:
  id: "vo_skills"
  name: "Skills"
  validation_rules:
    - "Technical skills must not be empty"
```

### UX Model (Will be created):

```yaml
page:
  id: "page_profile_edit"
  name: "Edit Profile"
  bounded_context_ref: "bc_profile"  # ← DDD reference
  aggregate_ref: "agg_candidate_profile"  # ← DDD reference

  sections:
    - id: "sec_skills"
      name: "Skills"  # ← DDD term
      components:
        - component_ref: "comp_skills_input"

component:
  id: "comp_skills_input"
  name: "Skills Input"
  type: "multi_select"
  label: "Skills"  # ← DDD term
  value_object_ref: "vo_skills"  # ← DDD reference
  validation:
    client_side:
      - "At least one skill required"  # ← Matches DDD rule
    server_side_ref: "vo_skills.validation_rules"  # ← DDD validation

user_action:
  id: "action_update_skills"
  trigger: "click"
  target: "btn_save_skills"
  invokes_service: "svc_app_update_profile"  # ← DDD service
  publishes_event: "evt_skills_changed"  # ← DDD event
```

---

## Benefits of Integration

1. **Consistency**: Same terminology across backend and frontend
2. **Validation Alignment**: UI validation matches domain rules
3. **Traceability**: UI elements trace to domain concepts
4. **Team Communication**: Everyone uses same language
5. **Reduced Errors**: No terminology translation needed
6. **Living Documentation**: UI documentation references domain documentation

---

## References

- **DDD Research**: `research/ddd/README.md`
- **DDD Schema**: `research/ddd/deliverables/ddd-schema-definition.yaml`
- **DDD Example**: `research/ddd/deliverables/ddd-schema-example.yaml`
- **DDD Ubiquitous Language**: `research/ddd/working-docs/04-ubiquitous-language.md`
- **DDD Glossaries**: In each bounded context definition

---

*Integration guidelines established: 2025-10-04*
*DDD terminology takes precedence for all domain concepts*
