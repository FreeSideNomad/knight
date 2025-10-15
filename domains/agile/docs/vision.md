# Vision: Agile Schema & Taxonomy Project

## Future State Description

This project will become the **definitive machine-readable taxonomy and schema for Agile and Scaled Agile Framework (SAFe) methodologies**. It will provide a formal, structured representation of agile concepts, patterns, and practices that can be:

- **Programmatically validated** for conformance to agile best practices
- **Consumed by tools** for project management, analysis, and automation
- **Used by AI agents and LLMs** for reasoning about agile projects
- **Referenced by practitioners** as a shared vocabulary and knowledge base
- **Extended and customized** for organization-specific needs

This matters because agile methodologies, while widely adopted, lack a standardized, formal representation that enables automation, validation, and machine reasoning. By creating this schema, we enable a new generation of agile tools and intelligent systems.

## Customer & Stakeholder Needs

### Target Audiences

1. **Agile Practitioners**
   - Product Owners, Scrum Masters, Agile Coaches
   - Need: Shared vocabulary, pattern library, best practices reference

2. **Tool Developers**
   - Agile project management tool creators
   - Need: Standard schema for representing agile artifacts and processes

3. **Engineering Teams**
   - Development teams practicing Scrum, XP, Kanban, SAFe
   - Need: Validation of practices against established patterns

4. **AI/LLM Developers**
   - Building AI agents for project management and coaching
   - Need: Formal ontology for reasoning about agile concepts

5. **Organizations Scaling Agile**
   - Enterprises implementing SAFe or other scaling frameworks
   - Need: Structured approach to modeling complex agile organizations

### Problems Being Solved

- **Inconsistent Terminology:** Different teams use different terms for the same concepts
- **Lack of Validation:** No automated way to check if practices conform to agile principles
- **Tool Fragmentation:** Each tool invents its own data model
- **Knowledge Gaps:** No comprehensive, structured reference for agile patterns
- **Scaling Complexity:** Difficult to model and validate enterprise-scale agile implementations

### Value Proposition

A formal, extensible schema that serves as:
- **Foundation for tooling** - Standard data model for agile project management tools
- **Validation framework** - Automated conformance checking against best practices
- **Knowledge repository** - Comprehensive pattern library with anti-patterns
- **Integration layer** - Common language for tool interoperability
- **AI enablement** - Structured knowledge for LLM-based agile agents

## Solution Intent

### Proposed Solution Approach

Build a **multi-layered taxonomy and schema system** consisting of:

1. **Glossary Layer** - Definitions of all agile terms and concepts
2. **Taxonomy Layer** - Hierarchical organization of concepts, patterns, and practices
3. **Schema Layer** - JSON Schema for modeling agile artifacts (Product, Vision, Epics, Features, Stories, Sprints, PIs)
4. **Validation Layer** - Rules and checks for conformance to best practices
5. **Pattern Library** - Documented patterns and anti-patterns with examples
6. **Documentation Layer** - Educational guides and references

### Key Capabilities and Features

- **Comprehensive Coverage:** Scrum, XP, Kanban, Lean, SAFe (all levels)
- **Product-Centric Model:** Product at the top of the hierarchy with rich Vision structure
- **Validation Rules:** INVEST criteria for stories, PI planning guidelines, ceremony checklists
- **Pattern Library:** 50+ documented patterns across planning, execution, scaling, and quality
- **Example Library:** Real-world examples of well-formed agile artifacts
- **Extensibility:** YAML-based format allowing organization-specific extensions

### Technical Direction

- **Format:** YAML for human readability and editability
- **Schema Language:** JSON Schema for validation
- **Validation:** Python-based validator with pluggable rules
- **Version Control:** Git-based, semantic versioning
- **Documentation:** Markdown with diagrams (Mermaid)
- **Tooling:** CLI tools for validation, pattern search, and conformance checking

## Context & Boundaries

### Scope - What's Included

- Agile methodologies: Scrum, XP, Kanban, Lean
- Scaled Agile Framework (SAFe): Team, Program, Large Solution, Portfolio levels
- Agile project structure: Product, Vision, Backlogs, Releases, PIs
- Team practices: Ceremonies, artifacts, roles
- Technical practices: TDD, CI/CD, pair programming
- Planning practices: Story writing, estimation, prioritization
- Scaling patterns: ARTs, value streams, portfolio management

### Scope - What's Excluded

- Specific tool implementations (Jira, Rally, etc.)
- Organization-specific workflows (covered by extensions)
- Non-agile methodologies (Waterfall, etc.)
- Project management details unrelated to agile practices
- Human resource management and personnel details
- Financial/budgeting systems (except high-level concepts like WSJF)

### Constraints and Assumptions

**Constraints:**
- Must remain methodology-agnostic where possible (support multiple agile flavors)
- Must be human-readable and editable
- Must be machine-readable and validatable
- Must be extensible without breaking existing usage

**Assumptions:**
- Users have basic agile knowledge
- Primary language is English (internationalization is future work)
- YAML and JSON Schema are acceptable technical choices
- Git/GitHub is the distribution mechanism

### Dependencies and Relationships

- **Depends on:** Agile Manifesto principles, Scrum Guide, SAFe framework documentation
- **Relates to:** Existing agile knowledge bases (Atlassian guides, Scrum.org, Scaled Agile, Inc.)
- **Influenced by:** Domain modeling best practices, ontology design patterns

## Strategic Alignment

### Business Objectives

1. **Enable Automation:** Make agile practices machine-readable and validatable
2. **Improve Consistency:** Establish shared vocabulary across teams and organizations
3. **Facilitate Learning:** Provide comprehensive educational resource
4. **Support Tool Development:** Offer standard schema for tool builders
5. **Enable AI Integration:** Provide structured knowledge for AI-powered agile coaching

### Strategic Themes

- **Formalization:** Bring rigor to agile practice representation
- **Interoperability:** Enable tools and systems to share agile data
- **Quality:** Support validation and conformance checking
- **Education:** Serve as learning resource for agile practitioners
- **Innovation:** Enable new classes of AI-powered agile tools

### Success Metrics and Outcomes

**Metrics:**
- Number of patterns documented (target: 50+ by v1.0)
- Number of validation rules implemented (target: 30+ by v1.0)
- Number of example artifacts (target: 20+ by v1.0)
- Schema coverage (target: 100% of core agile concepts by v1.0)
- Community adoption (GitHub stars, forks, issues, contributions)

**Outcomes:**
- At least 3 tools integrate the schema
- Used in educational contexts (courses, certifications)
- Referenced by agile practitioners as authoritative source
- Enables new AI-powered agile coaching tools
- Reduces ambiguity in agile terminology across organizations

## Motivational Narrative

Agile methodologies have transformed how we build software, yet we lack a common, formal language to describe what we do. Every tool invents its own model. Every team uses terms differently. AI agents struggle to reason about agile concepts without structured knowledge.

**This project changes that.**

We're building the foundation for the next generation of agile tools - tools that can validate your practices, coach your teams, and reason about complex scaled implementations. We're creating a shared vocabulary that bridges teams, organizations, and tools.

By formalizing agile knowledge, we're not constraining it - we're amplifying it. We're making it accessible to machines so they can help humans practice agile better. We're creating a knowledge commons that any tool, any team, any AI can build upon.

**This is the Rosetta Stone for agile methodologies.**

It matters because when we have a common language, we can build better tools, teach more effectively, and practice more consistently. When AI agents understand agile formally, they can provide intelligent guidance. When teams share vocabulary, they collaborate more smoothly.

This project makes agile machine-readable, validatable, and accessible. It transforms tribal knowledge into structured wisdom. It enables innovation we haven't imagined yet.

## Decision-Making Framework

### Guiding Principles

1. **Fidelity to Source:** Align with official frameworks (Scrum Guide, SAFe documentation)
2. **Practical Over Theoretical:** Focus on real-world practices over academic purity
3. **Extensibility:** Design for customization without forking
4. **Simplicity:** Make simple cases simple, complex cases possible
5. **Validation Friendly:** Structure for automated checking
6. **Human & Machine:** Readable by practitioners, parseable by tools
7. **Pattern-Oriented:** Capture proven practices, document anti-patterns
8. **Incremental:** Build iteratively, release early and often

### Trade-off Criteria

**When choosing between alternatives, prefer:**
- Clarity over brevity
- Practical utility over theoretical completeness
- Established conventions over innovation
- Backward compatibility over breaking changes
- Documentation over code comments
- Examples over abstract descriptions
- Validation over flexibility (where safety matters)

### Alignment Mechanisms

- **Quarterly reviews** of schema against latest framework updates (SAFe, Scrum Guide)
- **Community feedback** through GitHub issues and discussions
- **Pattern validation** through practitioner review
- **Example validation** through real-world usage
- **Rule validation** through expert review

## Roadmap Horizon

### Phase 0 (Foundation)
- Glossary, taxonomy structure, educational guide
- Timeline: Weeks 1-2

### Phase 1 (Patterns)
- Pattern and anti-pattern library
- Timeline: Weeks 3-4

### Phase 2 (Schema)
- JSON Schema for core artifacts
- Timeline: Weeks 5-6

### Phase 3 (Validation)
- Rule engine and conformance checks
- Timeline: Weeks 7-8

### Phase 4 (Expansion)
- Cross-cutting concerns, advanced patterns
- Timeline: Weeks 9-10

### Phase 5 (Polish)
- Diagrams, indexes, documentation
- Timeline: Weeks 11-12

### Phase 6 (Release)
- v1.0.0 release with full test coverage
- Timeline: Week 13

### Future Phases
- v2.0: Extended scaling frameworks (LeSS, Nexus, Disciplined Agile)
- v3.0: Integration patterns with CI/CD, DevOps
- v4.0: Metrics and analytics schemas
- v5.0: AI agent integration patterns
