# Domain I: Quality Engineering Standards and Bodies of Knowledge

**Research Domain**: Standards and Bodies of Knowledge
**Status**: Complete
**Last Updated**: 2025-10-04

---

## Overview

This document provides comprehensive coverage of the major quality engineering standards and bodies of knowledge that form the foundation of modern software testing practice. These standards provide authoritative definitions, methodologies, and best practices recognized globally across industries.

**Covered Standards**:
1. ISTQB (International Software Testing Qualifications Board)
2. ISO/IEC Standards (9001, 25010, 5055)
3. ASQ Certified Quality Engineer (CQE) Body of Knowledge
4. TMAP (Test Management Approach)
5. IEEE Standards (829, 29119)

---

## 1. ISTQB (International Software Testing Qualifications Board)

### 1.1 Overview

**ISTQB** is the world's leading software testing certification body, providing standardized qualifications for software testers at various levels of expertise. The ISTQB certification scheme provides a common language and understanding of software testing worldwide.

**Website**: https://www.istqb.org

### 1.2 Foundation Level Syllabus v4.0

The ISTQB Certified Tester Foundation Level (CTFL) v4.0 was released in 2023 and represents the latest comprehensive foundation for software testing knowledge.

#### Structure

The syllabus is organized into **6 chapters**:

1. **Fundamentals of Testing**
   - What is testing?
   - Why is testing necessary?
   - Testing principles
   - Test process
   - Test work products
   - Roles in testing

2. **Testing Throughout the Software Development Lifecycle**
   - Software development lifecycle models
   - Test levels and test types
   - Maintenance testing

3. **Static Testing**
   - Static testing basics
   - Feedback and review process
   - Work product review process

4. **Test Analysis and Design**
   - Test techniques overview
   - Black-box test techniques
   - White-box test techniques
   - Experience-based test techniques

5. **Managing the Test Activities**
   - Test planning
   - Risk management
   - Test monitoring, control and completion
   - Configuration management
   - Defect management

6. **Test Tools**
   - Tool support for testing
   - Benefits and risks of test automation

### 1.3 Seven Testing Principles

The ISTQB Foundation Level v4.0 identifies **seven fundamental testing principles** that guide effective testing:

#### 1. Testing Shows the Presence, Not the Absence of Defects
Testing can show that defects are present in the test object, but cannot prove that there are no defects. Testing reduces the probability of defects remaining undiscovered, but even if no defects are found, testing cannot prove software correctness.

**DDD Integration Example**:
```yaml
# Testing vo_email (Value Object)
test: Email validation detects invalid formats
result: Found defects (rejects "invalid@", "@example.com")
conclusion: Cannot prove all valid emails are accepted
           Can only prove invalid emails are rejected
```

#### 2. Exhaustive Testing Is Impossible
Testing everything (all combinations of inputs and preconditions) is not feasible except in trivial cases. Rather than attempting to test exhaustively, test effort should focus on risk analysis, test techniques, and priorities.

**Job Seeker Example**:
```yaml
# bc_applications: Application submission
impossible_exhaustive_testing:
  - All combinations of profile data (infinite)
  - All possible job postings (thousands)
  - All network conditions
  - All browser/device combinations

solution: Risk-based testing
  - Focus on critical paths (first application, reapplication)
  - Use boundary value analysis on key fields
  - Test on representative browser/device matrix
```

#### 3. Early Testing Saves Time and Money
Testing activities should start as early as possible in the software development lifecycle and should be focused on defined objectives. Defects found early are much cheaper to fix than defects found later.

**DDD Integration**: Shift-left testing
- Test Value Objects during domain modeling (vo_email validation rules)
- Test Aggregates during design (agg_candidate_profile invariants)
- Test Use Cases before UI implementation

#### 4. Defects Cluster Together
A small number of modules usually contains most of the defects discovered during pre-release testing or shows the most operational failures. Predicted defect clusters and actual observed defect clusters are an important input for risk-based testing.

**Job Seeker Risk Areas** (Defect Clustering):
```yaml
high_risk_areas:
  - bc_applications.svc_app_submit_application
    reason: Complex workflow, cross-context integration
    defect_density: High (historical data)
    test_effort: 30% of testing resources

  - bc_matching.svc_dom_matching
    reason: Complex algorithm, performance-sensitive
    defect_density: Medium-high
    test_effort: 20% of testing resources

  - page_application_wizard (wf_submit_application)
    reason: Multi-step UI, validation across steps
    defect_density: Medium-high
    test_effort: 15% of testing resources
```

#### 5. Tests Wear Out (Pesticide Paradox)
If the same tests are repeated over and over again, eventually they will no longer find new defects. To detect new defects, existing tests and test data may need to be changed, and new tests may need to be written.

**Mitigation Strategy**:
```yaml
test_maintenance:
  - Regular test review (quarterly)
  - Update tests when requirements change
  - Add new test scenarios based on production defects
  - Exploratory testing to find edge cases
  - Mutation testing to verify test effectiveness
```

#### 6. Testing Is Context Dependent
Testing is done differently in different contexts. For example, safety-critical software is tested differently from an e-commerce site, and testing in an Agile project is done differently than testing in a waterfall project.

**Job Seeker Context**:
```yaml
context:
  domain: Job search and application platform
  safety_critical: No (not life-critical)
  compliance_requirements:
    - GDPR (user data protection)
    - WCAG 2.2 Level AA (accessibility)
    - EU Accessibility Act (June 2025)

methodology: Agile/DevOps
  - Short iterations (2-week sprints)
  - Continuous integration
  - Automated testing emphasis

test_focus:
  - Functional correctness (job search, applications)
  - Usability (user experience)
  - Accessibility (WCAG 2.2 AA compliance)
  - Performance (response time, concurrent users)
  - Data privacy and security
```

#### 7. Absence-of-Defects Fallacy
It is a fallacy to expect that just finding and fixing a large number of defects will ensure the success of a system. Thoroughly testing all specified requirements and fixing all defects found could still produce a system that is difficult to use, does not fulfill the user's needs and expectations, or is inferior compared to other competing systems.

**Quality vs. Testing**:
```yaml
beyond_defect_detection:
  - Usability testing (SUS score >75)
  - User acceptance testing (real user feedback)
  - A/B testing (feature effectiveness)
  - Accessibility testing (inclusive design)
  - Performance benchmarking (vs. competitors)
  - User satisfaction metrics (NPS, CSAT)
```

### 1.4 Advanced Level Certifications

ISTQB offers specialized advanced-level certifications:

- **Test Analyst** - Black-box testing techniques
- **Technical Test Analyst** - White-box testing, code-level testing
- **Test Manager** - Test management, planning, estimation
- **Agile Technical Tester** - Testing in Agile environments
- **AI Testing** (New) - Testing AI/ML systems

### 1.5 ISTQB Glossary

The **ISTQB Glossary of Testing Terms** provides standardized terminology used globally. Key terms referenced in this research:

- **Test Level**: A specific instantiation of a test process (unit, integration, system, acceptance)
- **Test Type**: A group of test activities based on specific test objectives (functional, performance, security)
- **Test Technique**: A procedure used to define test conditions, design test cases, and specify test data
- **Aggregate**: (Not in ISTQB; from DDD) A cluster of associated objects treated as a single unit

**Integration with DDD/UX Terminology**: See `QE-DDD-UX-INTEGRATION.md` for cross-reference tables

### 1.6 Application to Job Seeker

The ISTQB framework provides the testing foundation for the Job Seeker application:

```yaml
istqb_application:
  test_levels:
    - Component/Unit: vo_email, ent_job_posting
    - Integration: agg_candidate_profile, repo_job_posting
    - System: bc_applications workflow
    - Acceptance: User stories (BDD scenarios)

  test_types:
    functional:
      - Job search functionality
      - Application submission
      - Profile management

    non_functional:
      - Performance: <200ms response time
      - Security: Authentication, authorization
      - Usability: SUS score >75
      - Accessibility: WCAG 2.2 Level AA

  test_techniques:
    black_box:
      - Equivalence Partitioning (vo_experience_years)
      - Boundary Value Analysis (pagination limits)
      - Decision Table (matching algorithm rules)

    white_box:
      - Statement coverage (domain logic)
      - Branch coverage (conditional logic)
```

---

## 2. ISO/IEC Standards

### 2.1 ISO 9001:2015 - Quality Management Systems

**ISO 9001:2015** is the international standard for quality management systems (QMS). It provides a framework for organizations to ensure they meet customer and regulatory requirements while continually improving.

#### Key Principles

1. **Customer Focus**
2. **Leadership**
3. **Engagement of People**
4. **Process Approach**
5. **Improvement**
6. **Evidence-based Decision Making**
7. **Relationship Management**

#### Relevance to Software Testing

ISO 9001 emphasizes:
- Process documentation
- Continuous improvement (PDCA cycle: Plan-Do-Check-Act)
- Risk-based thinking
- Measurement and analysis
- Defect management and corrective action

**Job Seeker Application**:
```yaml
iso_9001_alignment:
  quality_objectives:
    - >95% test coverage
    - <0.1% defect escape rate
    - 100% WCAG 2.2 AA compliance

  process_approach:
    - Documented test process
    - Test planning and design
    - Test execution and reporting
    - Defect management workflow

  continuous_improvement:
    - Sprint retrospectives
    - Test metrics analysis
    - Process refinement
```

### 2.2 ISO/IEC 25010 - Software Product Quality Model

**ISO/IEC 25010** defines a quality model for software products, part of the SQuaRE (Systems and software Quality Requirements and Evaluation) series.

#### 2023 Update: 9 Quality Characteristics

As of November 2023, ISO 25010 includes **9 characteristics** (updated from 8 in the 2011 version):

1. **Functional Suitability** (NEW NAME: Functional Correctness)
2. **Performance Efficiency**
3. **Compatibility**
4. **Interaction Capability** (was Usability)
5. **Reliability**
6. **Security**
7. **Maintainability**
8. **Flexibility** (was Portability)
9. **Safety** (NEW in 2023)

#### Original 2011 Version: 8 Characteristics

For reference, the widely-used ISO 25010:2011 includes:

1. **Functional Suitability**
2. **Performance Efficiency**
3. **Compatibility**
4. **Usability**
5. **Reliability**
6. **Security**
7. **Maintainability**
8. **Portability**

Each characteristic has sub-characteristics (31 total in 2011 version).

#### Complete Quality Model (ISO 25010:2011)

**1. Functional Suitability**
- Functional completeness
- Functional correctness
- Functional appropriateness

**2. Performance Efficiency**
- Time behavior (response time, processing time)
- Resource utilization (CPU, memory, network)
- Capacity (maximum limits)

**3. Compatibility**
- Co-existence (with other software)
- Interoperability (data exchange)

**4. Usability**
- Appropriateness recognizability (understand if suitable)
- Learnability
- Operability (easy to operate and control)
- User error protection
- User interface aesthetics
- Accessibility

**5. Reliability**
- Maturity (low failure rate)
- Availability
- Fault tolerance
- Recoverability

**6. Security**
- Confidentiality
- Integrity
- Non-repudiation
- Accountability
- Authenticity

**7. Maintainability**
- Modularity
- Reusability
- Analyzability
- Modifiability
- Testability

**8. Portability**
- Adaptability
- Installability
- Replaceability

#### Mapping to Job Seeker Testing

```yaml
iso_25010_job_seeker_testing:

  functional_suitability:
    tests:
      - Job search returns relevant results
      - Application submission creates agg_application
      - Profile updates persist correctly
    coverage: >95%

  performance_efficiency:
    time_behavior:
      - Search response <200ms (95th percentile)
      - Page load <1s
    resource_utilization:
      - API memory usage <500MB
      - Database query optimization
    capacity:
      - Support 1000 concurrent users
      - Handle 100,000 job postings

  compatibility:
    co_existence:
      - Works alongside browser extensions
    interoperability:
      - REST API for job data
      - OAuth integration

  usability:
    learnability:
      - First-time user can submit application in <10 min
    operability:
      - Keyboard navigation 100% functional
    accessibility:
      - WCAG 2.2 Level AA: 100% compliance
      - Screen reader compatible
      - Color contrast ratio ≥4.5:1

  reliability:
    availability:
      - 99.9% uptime target
    fault_tolerance:
      - Graceful degradation if matching service fails
    recoverability:
      - Auto-save form data every 30s

  security:
    confidentiality:
      - Encrypted user data (AES-256)
    authenticity:
      - JWT authentication
    authorization:
      - Role-based access control

  maintainability:
    modularity:
      - Bounded contexts (9 contexts)
      - Microservices architecture
    testability:
      - Unit test coverage >90% (domain)
      - Integration test coverage >85% (application)

  portability:
    adaptability:
      - Responsive design (mobile, tablet, desktop)
    installability:
      - Docker containerization
      - One-command deployment
```

### 2.3 ISO 5055:2021 - Code Quality Standards

**ISO 5055:2021** defines measures for automated source code quality. It specifies measures for four critical software quality characteristics based on weaknesses that lead to operational software failures.

#### Four Critical Characteristics

1. **Reliability** - Software weaknesses that lead to system failures
2. **Security** - Vulnerabilities exploitable by attackers
3. **Performance Efficiency** - Code that causes performance degradation
4. **Maintainability** - Code that is difficult to understand or change

**Job Seeker Application**:
```yaml
iso_5055_metrics:
  reliability:
    - Null pointer dereferences: 0 critical
    - Resource leaks: 0 critical
    - Uncontrolled format strings: 0

  security:
    - SQL injection vulnerabilities: 0
    - Cross-site scripting (XSS): 0
    - Hardcoded credentials: 0

  performance:
    - Inefficient database queries: <5
    - N+1 query problems: 0 critical

  maintainability:
    - Cyclomatic complexity: <10 per function
    - Code duplication: <3%
    - Technical debt ratio: <5%

tools:
  - SonarQube (static analysis)
  - ESLint (JavaScript/TypeScript)
  - Bandit (Python security)
```

---

## 3. ASQ Certified Quality Engineer (CQE) Body of Knowledge

### 3.1 Overview

The **American Society for Quality (ASQ)** Certified Quality Engineer certification represents comprehensive knowledge in quality engineering principles and practices. The 2022 updated Body of Knowledge organizes QE knowledge into **seven pillars**.

**Website**: https://www.asq.org/cert/quality-engineer

### 3.2 The Seven Pillars of CQE (2022 BoK)

#### Pillar 1: Management and Leadership (15% - 17 questions)
- Quality philosophy and principles
- Organizational structures
- Team dynamics and facilitation
- Leadership principles and techniques
- Customer focus and satisfaction

**Job Seeker Application**:
```yaml
management_leadership:
  quality_philosophy:
    - Test-first mindset (TDD for Value Objects)
    - Shift-left testing (early defect detection)
    - Whole team approach (QA embedded in teams)

  team_structure:
    - Cross-functional teams (Dev, QA, UX, Product)
    - Quality champions in each bounded context

  customer_focus:
    - User acceptance testing
    - Usability testing sessions
    - Accessibility compliance (user inclusivity)
```

#### Pillar 2: The Quality System (15%)
- Quality system development
- Quality standards and guidelines
- Documentation systems
- Quality audits
- Training and development

**Application**:
```yaml
quality_system:
  documentation:
    - Test strategy document
    - Test plans per bounded context
    - QE-DDD-UX integration guide

  standards:
    - ISTQB test process
    - ISO 25010 quality characteristics
    - WCAG 2.2 accessibility

  training:
    - DDD fundamentals for QA
    - Accessibility testing workshop
    - Test automation best practices
```

#### Pillar 3: Product, Process, and Service Design
- Design for X (DfX): Design for Testability, Reliability, Manufacturability
- Failure Mode and Effects Analysis (FMEA)
- Reliability and maintainability engineering
- Design verification and validation

**DDD Integration**:
```yaml
design_for_testability:
  value_objects:
    - Pure functions (deterministic)
    - No side effects (easy to test)
    - Validation in constructor

  aggregates:
    - Clear boundaries (isolated testing)
    - Domain events (observable behavior)
    - Repository interfaces (mockable)

  application_services:
    - Single responsibility (focused tests)
    - Dependency injection (testable)
```

#### Pillar 4: Product and Process Control
- Statistical Process Control (SPC)
- Control charts
- Process capability analysis
- Measurement system analysis

**Testing Metrics**:
```yaml
process_control:
  test_metrics:
    - Test pass rate (control chart)
    - Defect detection rate
    - Test execution time (trend analysis)

  capability:
    - Test coverage capability (target >80%)
    - Defect removal efficiency (>95%)
```

#### Pillar 5: Continuous Improvement (17% - 27 questions)
- PDCA (Plan-Do-Check-Act) cycle
- Root cause analysis
- Corrective and preventive action (CAPA)
- Kaizen and lean principles
- Six Sigma methodologies

**Job Seeker CI**:
```yaml
continuous_improvement:
  sprint_retrospectives:
    - What went well (testing perspective)
    - What to improve
    - Action items

  root_cause_analysis:
    - Production defects (5 Whys)
    - Test gaps identification

  preventive_actions:
    - Add tests for defect classes
    - Update test data for edge cases
    - Improve test documentation
```

#### Pillar 6: Quantitative Methods and Statistics (25 questions - largest section)
- Descriptive statistics
- Probability distributions
- Hypothesis testing
- Regression and correlation analysis
- Design of experiments (DOE)
- Analysis of variance (ANOVA)

**Testing Application**:
```yaml
statistical_methods:
  defect_analysis:
    - Defect density distribution
    - Correlation: code complexity vs defects

  performance_testing:
    - Response time distribution (percentiles)
    - Load test result analysis (regression)

  test_effectiveness:
    - Code coverage vs defect detection (correlation)
    - A/B testing (hypothesis testing)
```

#### Pillar 7: Risk Management (13% - 21 questions)
- Risk identification and assessment
- Risk prioritization
- Risk mitigation and control
- Risk monitoring

**Risk-Based Testing**:
```yaml
risk_based_testing_job_seeker:
  high_risk_areas:
    - bc_applications.submit_application
      likelihood: Medium
      impact: High (business critical)
      mitigation: 30% test effort, >95% coverage

    - bc_matching.calculate_score
      likelihood: Medium
      impact: High (user experience)
      mitigation: Performance testing, algorithm validation

    - security.authentication
      likelihood: Low
      impact: Critical (data breach)
      mitigation: Security testing, penetration testing

  medium_risk:
    - bc_profile.update_profile
    - bc_job_catalog.search_jobs

  low_risk:
    - Static content pages
    - Help documentation
```

### 3.3 CQE Exam Structure

- **Total Questions**: 175 (160 scored, 15 unscored)
- **Time**: 5.5 hours total appointment
- **Exam Time**: 5 hours 18 minutes
- **Format**: Multiple choice
- **Language**: English only

---

## 4. TMAP (Test Management Approach)

### 4.1 Overview

**TMAP** is a body of knowledge for quality engineering and testing in IT delivery, fully aligned with Agile and DevOps methodologies. Originally developed in the Netherlands, TMAP has evolved from traditional waterfall approaches to modern high-performance IT delivery.

**Website**: https://www.tmap.net

### 4.2 Evolution

- **TMap NEXT** (2006): Traditional waterfall/V-model approaches
- **TMap for Agile/DevOps** (2020): "Quality for DevOps Teams" book published
- Focus on **quality at speed**

### 4.3 Quality at Speed - Three Pillars

TMAP's modern approach focuses on three pillars:

#### 1. Shift-Left
Move testing activities earlier in the development lifecycle:
- Test planning during requirements
- Test design during architecture
- Unit testing during coding
- Early defect detection

**Job Seeker Shift-Left**:
```yaml
shift_left:
  requirements_phase:
    - Define acceptance criteria (BDD scenarios)
    - Identify testability requirements

  design_phase:
    - Review DDD aggregates for testability
    - Design for dependency injection
    - Plan integration test boundaries

  coding_phase:
    - TDD for Value Objects
    - Unit tests for Entities
    - Component tests for UI atoms/molecules
```

#### 2. Continuous Testing
Automated testing integrated into CI/CD pipeline:
- Every commit triggers automated tests
- Fast feedback loops
- Test parallelization

**CI/CD Integration**:
```yaml
continuous_testing_pipeline:
  commit_stage:
    - Unit tests (domain layer)
    - Linting and static analysis
    - <5 min execution

  acceptance_stage:
    - Integration tests (application layer)
    - Component tests (UI layer)
    - <15 min execution

  deployment_stage:
    - E2E tests (critical paths)
    - Smoke tests
    - <30 min execution
```

#### 3. Continuous Feedback Loop
Ongoing monitoring and learning:
- Production monitoring
- User feedback integration
- Metrics-driven improvement

**Feedback Mechanisms**:
```yaml
feedback_loops:
  production_monitoring:
    - Error tracking (Sentry)
    - Performance monitoring (New Relic)
    - User behavior analytics

  user_feedback:
    - Usability testing sessions
    - User surveys (NPS, CSAT)
    - Accessibility audits

  team_feedback:
    - Sprint retrospectives
    - Test metrics dashboards
    - Defect trend analysis
```

### 4.4 TMAP Certification

Based on "Quality for DevOps teams" book, the certification scheme covers:
- Agile and DevOps testing
- Test automation
- Continuous testing strategies
- Quality in high-performance IT delivery

### 4.5 TMAP Building Blocks

TMAP provides modular "building blocks" for constructing a testing approach:

- **Test Strategy**: Defining the test approach
- **Test Process**: Activities and tasks
- **Test Techniques**: Structured methods for test design
- **Test Organization**: Roles and responsibilities
- **Test Infrastructure**: Tools and environments
- **Test Automation**: Automation strategy and frameworks

**Job Seeker Test Strategy**:
```yaml
tmap_building_blocks:
  test_strategy:
    - Risk-based testing (RBT)
    - Test pyramid (60-30-10)
    - Shift-left emphasis

  test_techniques:
    - BVA for Value Objects
    - Decision tables for complex rules
    - State transition for workflows

  test_organization:
    - QA embedded in Scrum teams
    - Quality champions per bounded context

  test_infrastructure:
    - Jest (unit/integration)
    - Playwright (E2E)
    - Docker (test environments)

  test_automation:
    - >70% automation target
    - CI/CD integration
    - Page Object Model for UI
```

---

## 5. IEEE Standards

### 5.1 IEEE 829 (Historical) - Software Test Documentation

**IEEE 829** was the original standard for software test documentation, specifying the form and content of test documents.

**Status**: Superseded by **ISO/IEC/IEEE 29119-3:2013**

#### Eight Document Types (IEEE 829-2008)

1. **Test Plan** - Overall test strategy and planning
2. **Test Design Specification** - Detailed test conditions and features
3. **Test Case Specification** - Detailed test cases with inputs and expected results
4. **Test Procedure Specification** - Step-by-step test execution procedures
5. **Test Item Transmittal Report** - Status of test items
6. **Test Log** - Chronological record of test execution
7. **Test Incident Report** - Defects found during testing
8. **Test Summary Report** - Summary of test results

**Job Seeker Application** (IEEE 829 inspired structure):
```yaml
test_documentation:
  test_plan:
    file: test-strategy-bc-applications.md
    scope: bc_applications bounded context
    approach: Risk-based, automated emphasis

  test_cases:
    file: 16-qe-yaml-schema.yaml
    format: YAML with DDD/UX references
    examples: 5 complete test specifications

  test_execution_logs:
    tool: Jest/Playwright reporters
    format: HTML, JSON, Allure

  defect_reports:
    tool: GitHub Issues
    template: Bug report template with DDD references

  test_summary:
    file: 18-executive-summary.md
    metrics: Coverage, pass rate, defect density
```

### 5.2 ISO/IEC/IEEE 29119 - Software Testing Standards (Current)

**ISO/IEC/IEEE 29119** is the current international standard for software testing, replacing IEEE 829.

#### Five-Part Standard

1. **Part 1: Concepts and Definitions** - Testing vocabulary and concepts
2. **Part 2: Test Processes** - Test process model and activities
3. **Part 3: Test Documentation** - Test document templates (replaces IEEE 829)
4. **Part 4: Test Techniques** - Specification-based, structure-based, experience-based
5. **Part 5: Keyword-Driven Testing** - Keyword-driven test automation

#### Test Process Model (29119-2)

Three process groups:
- **Organizational Test Process**: Test policy, strategy, organization-wide
- **Test Management Process**: Test planning, monitoring, control, completion
- **Dynamic Test Process**: Test design, implementation, execution, reporting

**Job Seeker Alignment**:
```yaml
iso_29119_alignment:
  organizational_level:
    - QE strategy document (15-qe-knowledge-base.md)
    - Test policy (quality gates, coverage targets)

  management_level:
    - Test plans per bounded context
    - Sprint test planning
    - Test monitoring (metrics dashboard)

  dynamic_level:
    - Test design (YAML schema)
    - Test implementation (Jest, Playwright)
    - Test execution (CI/CD pipeline)
    - Test reporting (test summary reports)
```

### 5.3 Other Relevant IEEE Standards

- **IEEE 1012** - Software Verification and Validation
- **IEEE 730** - Software Quality Assurance Processes
- **IEEE 1028** - Software Reviews and Audits
- **IEEE 2675** - DevOps: Building Reliable and Secure Systems

---

## 6. Standards Integration Summary

### 6.1 Cross-Reference Table

| Standard | Focus | Application to Job Seeker |
|----------|-------|---------------------------|
| **ISTQB CTFL v4.0** | Testing fundamentals, principles, techniques | Foundation for test strategy, levels, types |
| **ISO 25010** | Software quality characteristics | NFR testing (performance, security, usability, accessibility) |
| **ISO 5055** | Code quality measures | Static analysis, code metrics, technical debt |
| **ASQ CQE** | Quality engineering body of knowledge | Quality management, continuous improvement, risk-based testing |
| **TMAP** | Agile/DevOps testing | Shift-left, continuous testing, quality at speed |
| **ISO/IEC/IEEE 29119** | Software testing process and documentation | Test process structure, test documentation templates |

### 6.2 Unified Terminology

All standards use consistent core concepts:

- **Test Level**: Unit, Integration, System, Acceptance (ISTQB, ISO 29119)
- **Test Type**: Functional, Non-functional (ISTQB, ISO 25010)
- **Quality Characteristics**: ISO 25010's 8 characteristics
- **Test Design Techniques**: Black-box, White-box, Experience-based (ISTQB, ISO 29119)
- **Risk-Based Testing**: ASQ CQE, TMAP

### 6.3 DDD/UX Integration

These standards are integrated with DDD and UX patterns:

```yaml
standards_ddd_ux_integration:
  istqb_test_levels:
    - Unit: vo_email, ent_job_posting, atom_button
    - Integration: agg_candidate_profile, repo_job_posting, page_profile_edit
    - System: bc_applications, wf_submit_application
    - Acceptance: User stories, user journeys

  iso_25010_characteristics:
    usability.accessibility:
      - WCAG 2.2 Level AA compliance
      - UX patterns: keyboard navigation, screen reader support

    maintainability.modularity:
      - DDD bounded contexts (9 contexts)
      - UX atomic design (atoms → organisms)

  asq_cqe_risk_based_testing:
    high_risk: bc_applications (complex aggregate, cross-context)
    medium_risk: bc_matching (algorithm complexity)
    low_risk: Static pages

  tmap_shift_left:
    - TDD for Value Objects
    - Integration tests for Aggregates
    - Component tests for UI atoms/molecules
```

---

## 7. Best Practices from Standards

### 7.1 Common Themes

1. **Early Testing** (ISTQB Principle 3, TMAP Shift-Left)
2. **Risk-Based Approach** (ISTQB, ASQ CQE, ISO 29119)
3. **Continuous Improvement** (ASQ CQE, ISO 9001, TMAP Feedback Loop)
4. **Documented Process** (ISO 9001, IEEE 829, ISO 29119)
5. **Automation Where Appropriate** (TMAP, ISO 29119 Part 5)
6. **Quality Characteristics** (ISO 25010, ASQ CQE)

### 7.2 Anti-Patterns to Avoid

From standards analysis:

1. **Testing Only at the End** (violates Shift-Left principle)
2. **Ignoring Risk Assessment** (violates Risk-Based Testing)
3. **No Test Documentation** (violates IEEE 829, ISO 29119)
4. **Testing Without Defined Quality Criteria** (violates ISO 25010)
5. **Exhaustive Testing Attempts** (violates ISTQB Principle 2)
6. **No Continuous Improvement** (violates ASQ CQE Pillar 5)

### 7.3 Standards Compliance Checklist

```yaml
job_seeker_compliance:
  istqb:
    - ✅ Seven testing principles applied
    - ✅ Four test levels defined
    - ✅ Test types identified (functional, non-functional)
    - ✅ Test techniques selected (BVA, EP, Decision Table)

  iso_25010:
    - ✅ All 8 quality characteristics mapped
    - ✅ Test cases for 31 sub-characteristics
    - ✅ Quality metrics defined

  asq_cqe:
    - ✅ Risk-based testing approach
    - ✅ Continuous improvement (sprint retros)
    - ✅ Quality metrics and KPIs

  tmap:
    - ✅ Shift-left testing (TDD, early testing)
    - ✅ Continuous testing (CI/CD integration)
    - ✅ Continuous feedback (monitoring, metrics)

  iso_29119:
    - ✅ Test process defined
    - ✅ Test documentation templates (YAML schema)
    - ✅ Test management process
```

---

## 8. References and Further Reading

### Official Standards Documents

1. **ISTQB Foundation Level Syllabus v4.0** - https://istqb.org/certifications/certified-tester-foundation-level-ctfl-v4-0/
2. **ISO 25010:2023** - https://www.iso.org/standard/78176.html
3. **ISO 5055:2021** - https://www.iso.org/standard/80623.html
4. **ASQ CQE Body of Knowledge 2022** - https://www.asq.org/cert/resource/pdf/certification/2022-CQE-BoK.pdf
5. **TMAP** - https://www.tmap.net/
6. **ISO/IEC/IEEE 29119** - https://www.iso.org/standard/81291.html

### Books

- "Quality for DevOps Teams" - Rik Marselis et al. (TMAP)
- "The ASQ Certified Quality Engineer Handbook, Fifth Edition"
- "ISTQB Certified Tester Foundation Level Study Guide"

### Related Job Seeker Documents

- `01-foundation-standards.md` - Comprehensive standards analysis
- `QE-DDD-UX-INTEGRATION.md` - Integration framework
- `15-qe-knowledge-base.md` - Complete QE taxonomy
- `16-qe-yaml-schema.yaml` - Test specification language

---

**Document Status**: Complete
**Next Document**: `03-domain-II-ontologies.md` (Testing Ontologies and Taxonomies)
