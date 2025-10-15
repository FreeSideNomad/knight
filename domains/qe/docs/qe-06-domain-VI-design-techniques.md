# Domain VI: Test Design Techniques - Comprehensive Research

**Date:** 2025-10-04
**Status:** Complete Working Document
**Purpose:** Authoritative research on test design techniques (black-box, white-box, experience-based) with ISTQB Foundation Level alignment and Job Seeker application context

---

## Executive Summary

This document provides comprehensive research on test design techniques as defined by ISTQB Foundation Level Syllabus v4.0 and ISO/IEC/IEEE 29119-4 (Test Techniques). Test design techniques are systematic approaches to deriving test cases from test conditions, ensuring thorough coverage and effective defect detection.

**Coverage:**

1. **Black-Box Techniques** (Specification-Based)
   - Equivalence Partitioning
   - Boundary Value Analysis
   - Decision Table Testing
   - State Transition Testing
   - Use Case Testing

2. **White-Box Techniques** (Structure-Based)
   - Statement Coverage
   - Branch/Decision Coverage
   - Path Coverage
   - Condition Coverage

3. **Experience-Based Techniques**
   - Error Guessing
   - Exploratory Testing
   - Checklist-Based Testing

**Key Standards Referenced:**
- ISTQB Foundation Level Syllabus v4.0.1 (2024)
- ISO/IEC/IEEE 29119-4:2021 - Software Testing Part 4: Test Techniques
- ISO/IEC 25010:2011 - System and Software Quality Models

---

## Table of Contents

1. [Introduction to Test Design Techniques](#introduction)
2. [Black-Box Techniques](#black-box-techniques)
3. [White-Box Techniques](#white-box-techniques)
4. [Experience-Based Techniques](#experience-based-techniques)
5. [Technique Selection Guidelines](#technique-selection)
6. [Integration with Job Seeker Context](#job-seeker-integration)
7. [References](#references)

---

## Introduction to Test Design Techniques {#introduction}

### What Are Test Design Techniques?

**ISTQB Definition**: Test design techniques are procedures used to derive and select test cases from test conditions. They provide systematic approaches to creating test cases that effectively detect defects while optimizing test coverage and effort.

### Three Categories (ISO/IEC/IEEE 29119-4)

According to ISO/IEC/IEEE 29119-4, test design techniques are categorized into three main groups:

1. **Specification-Based (Black-Box)**: Based on the functional specification without regard to internal structure
2. **Structure-Based (White-Box)**: Based on the internal structure of the system under test
3. **Experience-Based**: Based on tester knowledge, intuition, and experience

### Why Use Formal Test Design Techniques?

**Benefits:**
- **Systematic Coverage**: Ensures thorough testing of requirements and code
- **Defect Detection**: Increases probability of finding defects
- **Optimization**: Reduces redundant test cases while maintaining coverage
- **Traceability**: Creates clear links between requirements and tests
- **Repeatability**: Enables consistent test design across teams and projects
- **Measurement**: Provides quantifiable coverage metrics

**Common Pitfalls to Avoid:**
- Relying solely on ad hoc testing without systematic design
- Choosing techniques without considering test objectives
- Over-engineering test cases when simpler approaches suffice
- Ignoring the complementary nature of different techniques
- Failing to document the rationale for technique selection

---

## Black-Box Techniques {#black-box-techniques}

Black-box test design techniques (also called specification-based or behavioral techniques) focus on inputs and outputs without knowledge of internal structure. These techniques are applicable to both functional and non-functional testing.

**Key Characteristic**: Tests are derived from specifications, requirements, user stories, or other external documentation describing what the system should do.

---

### 1. Equivalence Partitioning (EP)

#### ISTQB Definition

**Equivalence Partitioning** is a black-box test design technique in which test cases are designed to cover equivalence partitions. An equivalence partition (equivalence class) is a subset of the input or output domain for which the system is expected to exhibit the same behavior.

#### When to Use

- Testing input fields with ranges of valid and invalid values
- Large input domains that cannot be exhaustively tested
- Calculation-intensive applications with multiple variables
- Reducing the number of test cases while maintaining coverage
- Testing at the center of input domains (not just boundaries)

#### How to Apply

**Step-by-Step Process:**

1. **Identify the input/output domain**: Determine what is being partitioned (input field, parameter, output value)
2. **Partition into equivalence classes**: Divide the domain into groups where each member should produce the same behavior
3. **Create valid and invalid partitions**: Include both expected (valid) and unexpected (invalid) inputs
4. **Select test values**: Choose one representative value from each partition
5. **Design test cases**: Create at least one test case per partition

**Guidelines:**
- Each equivalence class should be independent
- Valid partitions test expected behavior
- Invalid partitions test error handling
- One test per partition is the minimum coverage

#### Example: Job Seeker Application

**Scenario**: Testing the "Years of Experience" field on a candidate profile

**Requirement**: Years of experience must be between 0 and 50 (inclusive)

**Equivalence Partitions:**

| Partition ID | Type    | Range/Value | Expected Behavior | Representative Value |
|--------------|---------|-------------|-------------------|----------------------|
| EP1          | Valid   | 0-50        | Accept input      | 25                   |
| EP2          | Invalid | < 0         | Reject, show error| -5                   |
| EP3          | Invalid | > 50        | Reject, show error| 75                   |
| EP4          | Invalid | Non-numeric | Reject, show error| "abc"                |
| EP5          | Invalid | Null/Empty  | Reject, show error| null                 |

**Test Cases:**

```yaml
test_case_id: TC_EP_001
technique: Equivalence Partitioning
partition: EP1 (Valid)
input: 25
expected_result: Profile accepts and saves the value
ddd_reference:
  bounded_context: bc_profile
  value_object: vo_years_of_experience
  invariant: "value >= 0 AND value <= 50"

test_case_id: TC_EP_002
technique: Equivalence Partitioning
partition: EP2 (Invalid - Below minimum)
input: -5
expected_result: "Validation error: Years of experience cannot be negative"
```

#### Common Pitfalls

1. **Overlapping Partitions**: Creating classes that are not mutually exclusive
2. **Missing Invalid Partitions**: Only testing valid inputs
3. **Too Granular Partitioning**: Creating unnecessary subdivisions within a single equivalence class
4. **Ignoring Implicit Partitions**: Missing boundary conditions like null, empty, or special characters
5. **No Rationale Documentation**: Failing to document why partitions were chosen

#### Relationships to Other Techniques

- **Combines with Boundary Value Analysis**: EP identifies partitions, BVA tests the edges
- **Supports Decision Table Testing**: Partitions can be used as conditions in decision tables
- **Foundation for Use Case Testing**: Equivalence classes help identify test scenarios

---

### 2. Boundary Value Analysis (BVA)

#### ISTQB Definition

**Boundary Value Analysis** is a black-box test design technique in which test cases are designed based on boundary values. A **boundary value** is an input or output value on the edge of an equivalence partition, or at the smallest incremental distance on either side of an edge (e.g., minimum or maximum value of a range).

#### When to Use

- Testing ordered partitions (numerical ranges, dates, sequences)
- Input fields with defined minimum and maximum values
- Systems where defects cluster near boundaries (most common scenario)
- Following equivalence partitioning to test partition edges
- Calculation-intensive applications with physical quantity variables

#### How to Apply

**Step-by-Step Process:**

1. **Identify boundaries**: Determine min/max values for valid and invalid partitions
2. **Select boundary values**: Choose values at, just below, and just above each boundary
3. **Apply 2-value or 3-value BVA**:
   - **2-value BVA**: Test the boundary value and one value on either side
   - **3-value BVA**: Test the boundary value plus values on both sides
4. **Design test cases**: Create one test case per boundary value

**Standard Approach (3-value BVA):**
For a range [min, max]:
- Min - 1 (invalid)
- Min (valid boundary)
- Min + 1 (valid)
- Max - 1 (valid)
- Max (valid boundary)
- Max + 1 (invalid)

#### Example: Job Seeker Application

**Scenario**: Testing the "Job Title" character limit

**Requirement**: Job title must be between 3 and 100 characters (inclusive)

**Boundary Values:**

| Boundary      | Test Value Length | Type    | Example Input           | Expected Result       |
|---------------|-------------------|---------|-------------------------|-----------------------|
| Min - 1       | 2                 | Invalid | "JS"                    | Validation error      |
| Min           | 3                 | Valid   | "CEO"                   | Accept                |
| Min + 1       | 4                 | Valid   | "Lead"                  | Accept                |
| Max - 1       | 99                | Valid   | 99-char string          | Accept                |
| Max           | 100               | Valid   | 100-char string         | Accept                |
| Max + 1       | 101               | Invalid | 101-char string         | Validation error      |

**Additional Boundaries (Special Cases):**

| Boundary      | Test Value        | Expected Result                          |
|---------------|-------------------|------------------------------------------|
| Empty string  | ""                | Validation error                         |
| Null          | null              | Validation error                         |
| Whitespace    | "   "             | Validation error (whitespace only)       |

**Test Cases:**

```yaml
test_case_id: TC_BVA_001
technique: Boundary Value Analysis
boundary: Minimum boundary
input: "CEO"  # 3 characters
expected_result: Job title accepted and saved
ddd_reference:
  bounded_context: bc_job_posting
  value_object: vo_job_title
  invariant: "length >= 3 AND length <= 100 AND !isBlank()"

test_case_id: TC_BVA_002
technique: Boundary Value Analysis
boundary: Below minimum
input: "JS"  # 2 characters
expected_result: "Validation error: Job title must be at least 3 characters"
```

#### Common Pitfalls

1. **Testing Only Valid Boundaries**: Ignoring invalid boundary values
2. **Missing Special Values**: Not testing null, empty, or whitespace boundaries
3. **Incorrect Boundary Identification**: Confusing inclusive vs exclusive boundaries
4. **Over-testing**: Testing too many values within partitions instead of focusing on edges
5. **Ignoring Data Type Boundaries**: Missing min/max for integers, dates, or other types

#### Relationships to Other Techniques

- **Extends Equivalence Partitioning**: BVA tests the edges of partitions identified by EP
- **Best Practice**: Always use EP and BVA together for input validation testing
- **Complements State Transition**: Boundary values can trigger state changes

**Industry Best Practice**: "It is always advisable to use BVA in combination with Equivalence Class Partitioning since BVA cannot concentrate on errors that exist in the center of the input domain."

---

### 3. Decision Table Testing

#### ISTQB Definition

**Decision Table Testing** is a black-box test design technique in which test cases are designed to execute the combinations of inputs and/or stimuli (causes) shown in a decision table.

A **decision table** is a table showing combinations of inputs (conditions) and their associated outputs (actions), used to design test cases.

#### When to Use

- Complex business rules with multiple conditions
- Systems with combinatorial logic (multiple if-then-else statements)
- Requirements expressed as business rules or decision logic
- Testing all combinations of conditions systematically
- Documenting internal system design and business rules
- Regulatory compliance requiring complete coverage of rule combinations

**Distinction**: Decision tables and state transition testing are more focused on business logic or business rules, whereas equivalence partitioning and boundary value analysis are often more focused on user interface inputs.

#### How to Apply

**Step-by-Step Process:**

1. **Identify conditions**: List all input conditions (causes)
2. **Identify actions**: List all possible outcomes (effects)
3. **Create the table**: Build a table with:
   - **Conditions** as rows (top section)
   - **Actions** as rows (bottom section)
   - **Rules** as columns (one per combination)
4. **Fill combinations**: Populate all combinations of condition values
5. **Determine actions**: For each rule (column), specify which actions occur
6. **Optimize**: Merge columns with identical actions (rule reduction)
7. **Design test cases**: Create at least one test case per column (rule)

**Coverage Standard**: At least one test per column in the decision table, typically covering all combinations of triggering conditions.

#### Example: Job Seeker Application

**Scenario**: User login validation

**Business Rules:**
- User must provide valid email and password
- Account must not be locked
- User must have verified email

**Decision Table:**

| Rule ID              | R1  | R2  | R3  | R4  | R5  | R6  | R7  | R8  |
|----------------------|-----|-----|-----|-----|-----|-----|-----|-----|
| **Conditions**       |     |     |     |     |     |     |     |     |
| Valid Email          | Y   | Y   | Y   | Y   | N   | N   | N   | N   |
| Valid Password       | Y   | Y   | N   | N   | Y   | Y   | N   | N   |
| Account Not Locked   | Y   | N   | Y   | N   | Y   | N   | Y   | N   |
| Email Verified       | Y   | Y   | Y   | Y   | Y   | Y   | Y   | Y   |
| **Actions**          |     |     |     |     |     |     |     |     |
| Grant Access         | X   |     |     |     |     |     |     |     |
| Show "Account Locked"|     | X   |     | X   |     | X   |     | X   |
| Show "Invalid Password"|   |     | X   | X   |     |     |     |     |
| Show "Invalid Email" |     |     |     |     | X   | X   | X   | X   |

**Optimized Table** (after merging similar rules):

| Rule ID              | R1  | R2  | R3  | R4-R8 |
|----------------------|-----|-----|-----|-------|
| **Conditions**       |     |     |     |       |
| Valid Email          | Y   | Y   | Y   | N     |
| Valid Password       | Y   | Y   | N   | -     |
| Account Not Locked   | Y   | N   | Y   | -     |
| Email Verified       | Y   | Y   | Y   | Y     |
| **Actions**          |     |     |     |       |
| Grant Access         | X   |     |     |       |
| Show "Account Locked"|     | X   |     |       |
| Show "Invalid Password"|   |     | X   |       |
| Show "Invalid Email" |     |     |     | X     |

**Test Cases:**

```yaml
test_case_id: TC_DT_001
technique: Decision Table Testing
rule: R1 (Valid login)
conditions:
  email: "valid@example.com"
  password: "ValidPass123!"
  account_locked: false
  email_verified: true
expected_result: User logged in, redirect to dashboard
ddd_reference:
  bounded_context: bc_identity_access
  aggregate: agg_user_account
  domain_service: ds_authentication

test_case_id: TC_DT_002
technique: Decision Table Testing
rule: R2 (Account locked)
conditions:
  email: "valid@example.com"
  password: "ValidPass123!"
  account_locked: true
  email_verified: true
expected_result: "Error: Account is locked. Contact support."
```

#### Common Pitfalls

1. **Incomplete Conditions**: Missing important conditions that affect outcomes
2. **Ignoring Don't Care Values**: Not using "-" for conditions that don't affect an action
3. **No Rule Reduction**: Keeping redundant rules instead of optimizing the table
4. **Missing Actions**: Not considering all possible outcomes
5. **Over-complexity**: Creating tables with too many conditions (>6-8 becomes unwieldy)

#### Relationships to Other Techniques

- **Complements Equivalence Partitioning**: Partition values can be conditions
- **Supports State Transition**: Conditions can trigger state changes
- **Foundation for Use Cases**: Decision tables model complex use case variations

---

### 4. State Transition Testing

#### ISTQB Definition

**State Transition Testing** is a black-box test design technique in which test cases are designed to execute valid and invalid state transitions.

A **state transition** occurs when the system moves from one state to another based on events (inputs) or conditions.

#### When to Use

- Systems with distinct states and state-dependent behavior
- Finite state machines (embedded systems, protocols, workflows)
- Applications where the same input produces different outputs based on current state
- Testing sequential screen flows or navigation paths
- Technical automation and embedded software
- Business objects with specific lifecycle states

**Key Indicator**: "Any system where you get a different output for the same input, depending on what has happened before, is a finite state system."

#### How to Apply

**Step-by-Step Process:**

1. **Identify states**: List all possible states the system can be in
2. **Identify events/triggers**: Determine what causes transitions between states
3. **Create state transition diagram**: Visualize states (circles/rectangles) and transitions (arrows)
4. **Create state transition table**: Document all valid and invalid transitions
5. **Design test cases**: Cover:
   - **Valid transitions**: Every allowed state change
   - **Invalid transitions**: Attempted transitions that should be blocked
   - **Typical sequences**: Common paths through states
   - **All states**: Ensure each state is visited at least once

**Coverage Criteria:**
- **All States Coverage**: Visit every state at least once
- **All Transitions Coverage**: Execute every valid transition at least once (stronger)
- **All Paths Coverage**: Test all possible sequences (often impractical)

#### Example: Job Seeker Application

**Scenario**: Job Application lifecycle

**States:**
1. **Draft** - Application created but not submitted
2. **Submitted** - Application sent to employer
3. **Under Review** - Employer reviewing application
4. **Interview Scheduled** - Candidate invited to interview
5. **Rejected** - Application declined
6. **Withdrawn** - Candidate withdrew application
7. **Offer Extended** - Job offer made
8. **Accepted** - Candidate accepted offer

**State Transition Diagram:**

```
[Draft] --submit--> [Submitted] --review--> [Under Review]
   |                                            |
   |                                            +--interview--> [Interview Scheduled]
   |                                            |                      |
   +--withdraw--> [Withdrawn]                  +--reject--> [Rejected]|
                                                                       |
                                                                       +--offer--> [Offer Extended]
                                                                                         |
                                                                                         +--accept--> [Accepted]
                                                                                         |
                                                                                         +--decline--> [Rejected]
```

**State Transition Table:**

| Current State      | Event/Trigger | Next State         | Valid? |
|--------------------|---------------|--------------------|--------|
| Draft              | submit        | Submitted          | Yes    |
| Draft              | withdraw      | Withdrawn          | Yes    |
| Draft              | review        | -                  | No     |
| Submitted          | review        | Under Review       | Yes    |
| Submitted          | withdraw      | Withdrawn          | Yes    |
| Under Review       | interview     | Interview Scheduled| Yes    |
| Under Review       | reject        | Rejected           | Yes    |
| Under Review       | withdraw      | Withdrawn          | Yes    |
| Interview Scheduled| offer         | Offer Extended     | Yes    |
| Interview Scheduled| reject        | Rejected           | Yes    |
| Offer Extended     | accept        | Accepted           | Yes    |
| Offer Extended     | decline       | Rejected           | Yes    |
| Rejected           | *any*         | -                  | No     |
| Accepted           | *any*         | -                  | No     |
| Withdrawn          | *any*         | -                  | No     |

**Test Cases:**

```yaml
test_case_id: TC_ST_001
technique: State Transition Testing
transition: Draft -> Submitted (valid)
precondition: Application in Draft state with all required fields completed
event: User clicks "Submit Application" button
expected_result:
  - Application state changes to "Submitted"
  - Confirmation email sent to candidate
  - Notification sent to employer
ddd_reference:
  bounded_context: bc_job_application
  aggregate: agg_application
  domain_event: evt_application_submitted

test_case_id: TC_ST_002
technique: State Transition Testing
transition: Draft -> Under Review (invalid)
precondition: Application in Draft state
event: System attempts to move application to "Under Review"
expected_result:
  - Transition blocked
  - Error logged: "Invalid state transition: Draft -> Under Review"
```

#### Common Pitfalls

1. **Missing Invalid Transitions**: Only testing valid paths
2. **Incomplete State Identification**: Missing edge states (error states, terminal states)
3. **No Coverage Measurement**: Not tracking which states/transitions are tested
4. **Ignoring Guard Conditions**: Missing conditions that enable/disable transitions
5. **Over-complex Diagrams**: Trying to model too many states without hierarchical decomposition

#### Relationships to Other Techniques

- **Combines with Decision Tables**: Conditions in decision tables can trigger transitions
- **Supports Use Case Testing**: State transitions represent workflow steps
- **Uses Boundary Values**: Retry counts or thresholds can be boundary conditions

---

### 5. Use Case Testing

#### ISTQB Definition

**Use Case Testing** is a black-box test design technique in which test cases are designed to execute scenarios of use cases.

A **use case** describes interactions between actors (users or systems) and the system to achieve a specific goal.

#### When to Use

- Requirements expressed as use cases or user stories
- Testing end-to-end workflows and scenarios
- Acceptance testing with stakeholders
- System testing and integration testing
- Validating user interactions and business processes

#### How to Apply

**Step-by-Step Process:**

1. **Identify use cases**: List all use cases from requirements or user stories
2. **Identify scenarios**: For each use case, define:
   - **Basic flow** (happy path)
   - **Alternative flows** (variations)
   - **Exception flows** (error conditions)
3. **Identify actors**: Determine who/what interacts with the system
4. **Define preconditions**: What must be true before the use case starts
5. **Define postconditions**: Expected system state after completion
6. **Design test cases**: Create tests for each scenario

#### Example: Job Seeker Application

**Use Case**: Candidate Applies for Job

**Actors:**
- Primary: Candidate (authenticated user)
- Secondary: Employer System (notification recipient)

**Preconditions:**
- Candidate is logged in
- Candidate has completed profile (resume uploaded, skills added)
- Job posting exists and is active

**Basic Flow:**
1. Candidate searches for jobs by title/location
2. Candidate views job details
3. Candidate clicks "Apply Now"
4. System displays application form pre-filled with profile data
5. Candidate optionally adds cover letter
6. Candidate reviews and submits application
7. System creates application record in "Submitted" state
8. System sends confirmation email to candidate
9. System notifies employer of new application

**Alternative Flow 1**: Application with custom resume
- At step 4, candidate uploads different resume
- System attaches custom resume instead of default

**Alternative Flow 2**: Save application as draft
- At step 6, candidate clicks "Save Draft"
- System saves application in "Draft" state
- Candidate can return later to complete

**Exception Flow 1**: Job no longer available
- At step 3, job posting has been closed
- System displays "This position is no longer accepting applications"
- Candidate redirected to search results

**Exception Flow 2**: Duplicate application
- At step 7, candidate has already applied to this job
- System blocks submission
- System displays "You have already applied to this position"

**Postconditions (Success):**
- Application record created with status "Submitted"
- Candidate receives confirmation email
- Employer receives notification
- Application appears in candidate's "My Applications" list

**Test Cases:**

```yaml
test_case_id: TC_UC_001
technique: Use Case Testing
use_case: Apply for Job
scenario: Basic flow (happy path)
steps:
  - Login as candidate with complete profile
  - Search for "Software Engineer" jobs
  - Select first active job posting
  - Click "Apply Now"
  - Verify application form pre-populated
  - Add cover letter text
  - Click "Submit Application"
expected_result:
  - Success message displayed
  - Application status = "Submitted"
  - Confirmation email received
  - Application appears in "My Applications"
ddd_reference:
  bounded_context: bc_job_application
  aggregate: agg_application
  use_case: uc_submit_job_application

test_case_id: TC_UC_002
technique: Use Case Testing
use_case: Apply for Job
scenario: Exception - Duplicate application
steps:
  - Login as candidate who already applied to Job ID 123
  - Navigate to Job ID 123
  - Click "Apply Now"
expected_result:
  - Button disabled or shows "Already Applied"
  - Message: "You submitted an application on [date]"
```

#### Common Pitfalls

1. **Only Testing Happy Path**: Ignoring alternative and exception flows
2. **Missing Preconditions**: Not verifying system state before starting
3. **Incomplete Scenario Coverage**: Missing edge cases and variations
4. **No Postcondition Verification**: Not checking final system state
5. **Vague Test Steps**: Not providing enough detail for repeatability

#### Relationships to Other Techniques

- **Incorporates Decision Tables**: Complex use cases may include decision logic
- **Uses State Transitions**: Use cases often move through states
- **Applies EP/BVA**: Input validation within use cases uses these techniques
- **Foundation for Exploratory Testing**: Use cases guide exploratory sessions

---

## White-Box Techniques {#white-box-techniques}

White-box test design techniques (also called structure-based or structural techniques) are based on the internal structure of the system under test. These techniques measure code coverage and derive test cases from an analysis of the code.

**Key Characteristic**: Tests are derived from the implementation (source code, architecture, detailed design), requiring access to and understanding of the code structure.

**Important Relationship**: Achieving 100% statement coverage does NOT guarantee 100% decision coverage, but achieving 100% decision coverage DOES guarantee 100% statement coverage.

---

### 1. Statement Coverage

#### ISTQB Definition

**Statement Coverage** is the percentage of executable statements that have been exercised by a test suite.

**100% Statement Coverage**: Achieved when the test cases execute every executable line of code in the program at least once.

#### When to Use

- Unit testing individual functions/methods
- Identifying untested code segments
- Meeting regulatory coverage requirements (e.g., safety-critical systems)
- Code review and quality assessment
- Baseline coverage metric for all code

#### How to Apply

**Step-by-Step Process:**

1. **Instrument the code**: Use coverage tools to track statement execution
2. **Identify executable statements**: Count all statements that can be executed
3. **Run test cases**: Execute test suite and collect coverage data
4. **Calculate coverage**:
   ```
   Statement Coverage = (Statements Executed / Total Executable Statements) × 100%
   ```
5. **Identify gaps**: Find unexecuted statements
6. **Design additional tests**: Create tests to cover missing statements

#### Example: Job Seeker Application

**Code Under Test**: Email validation function

```javascript
function validateEmail(email) {
  // Statement 1
  if (email === null || email === undefined) {
    // Statement 2
    throw new Error("Email is required");
  }

  // Statement 3
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Statement 4
  if (!emailRegex.test(email)) {
    // Statement 5
    return { valid: false, error: "Invalid email format" };
  }

  // Statement 6
  return { valid: true };
}
```

**Total Executable Statements**: 6

**Test Cases for 100% Statement Coverage:**

```yaml
test_case_id: TC_SC_001
technique: Statement Coverage
test_data: email = null
statements_executed: [1, 2]
coverage_contribution: 2/6 = 33%

test_case_id: TC_SC_002
technique: Statement Coverage
test_data: email = "invalid-email"
statements_executed: [1, 3, 4, 5]
coverage_contribution: 4/6 = 67%

test_case_id: TC_SC_003
technique: Statement Coverage
test_data: email = "valid@example.com"
statements_executed: [1, 3, 4, 6]
coverage_contribution: 4/6 = 67%

combined_coverage: All 6 statements executed = 100%
```

**Alternative Minimal Test Suite** (2 tests for 100%):

```yaml
# Option 1: Combine TC_SC_001 and TC_SC_003
test_suite:
  - test: null input (executes statements 1, 2)
  - test: valid input (executes statements 1, 3, 4, 6)
  coverage: 6/6 = 100%

# But this misses invalid email path!
# Better to have all 3 tests for functional completeness
```

#### Common Pitfalls

1. **Confusing with Testing Quality**: 100% statement coverage ≠ thorough testing
2. **Dead Code**: Including unreachable code in coverage calculations
3. **Ignoring Decision Paths**: Not recognizing that statements can execute without testing all logic branches
4. **Tool Limitations**: Not understanding what coverage tools measure
5. **Coverage as the Only Goal**: Focusing on metrics instead of effective defect detection

**Important**: Statement coverage is the weakest form of coverage. It tells you code was executed but not whether all decision outcomes were tested.

#### Relationships to Other Techniques

- **Weaker than Decision Coverage**: Can have 100% statement coverage with <100% decision coverage
- **Supports Functional Testing**: Confirms functional tests exercise the code
- **Baseline for Branch Coverage**: Must achieve statement coverage to achieve branch coverage

---

### 2. Branch Coverage (Decision Coverage)

#### ISTQB Definition

**Branch Coverage** (also called Decision Coverage) is the percentage of branches (decision outcomes) that have been exercised by a test suite.

**100% Branch/Decision Coverage**: Achieved when test cases execute both the true and false outcomes of every decision point in the code.

**Key Relationship**: Decision coverage measures the coverage of conditional branches; branch coverage measures the coverage of both conditional and unconditional branches. At 100% coverage they give mostly the same results.

#### When to Use

- Testing control flow logic (if/else, switch, loops)
- More rigorous coverage than statement coverage
- Safety-critical or high-reliability systems
- Meeting coverage standards (e.g., DO-178C Level A requires decision coverage)
- Finding logic errors and unintended paths

#### How to Apply

**Step-by-Step Process:**

1. **Identify decision points**: Find all if, while, for, switch, and ternary operators
2. **Count decision outcomes**: Each decision typically has 2 outcomes (true/false)
3. **Run test cases**: Execute tests and track which outcomes are exercised
4. **Calculate coverage**:
   ```
   Branch Coverage = (Decision Outcomes Tested / Total Decision Outcomes) × 100%
   ```
5. **Design tests for missing branches**: Create tests that force untested outcomes

#### Example: Job Seeker Application

**Code Under Test**: Candidate eligibility check

```javascript
function checkCandidateEligibility(candidate) {
  // Decision 1: Has required experience
  if (candidate.yearsExperience < 2) {
    return { eligible: false, reason: "Insufficient experience" };
  }

  // Decision 2: Has completed profile
  if (!candidate.profileComplete) {
    return { eligible: false, reason: "Incomplete profile" };
  }

  // Decision 3: Has required skills
  if (candidate.skills.length === 0) {
    return { eligible: false, reason: "No skills listed" };
  }

  return { eligible: true };
}
```

**Decision Points**: 3 decisions × 2 outcomes = 6 branches

**Branch Coverage Analysis:**

| Decision | Outcome (Branch) | Condition                    | Code Path         |
|----------|------------------|------------------------------|-------------------|
| D1       | True             | yearsExperience >= 2         | Continue to D2    |
| D1       | False            | yearsExperience < 2          | Return ineligible |
| D2       | True             | profileComplete = true       | Continue to D3    |
| D2       | False            | profileComplete = false      | Return ineligible |
| D3       | True             | skills.length > 0            | Return eligible   |
| D3       | False            | skills.length = 0            | Return ineligible |

**Test Cases for 100% Branch Coverage:**

```yaml
test_case_id: TC_BC_001
technique: Branch Coverage
branches_covered: [D1-False]
test_data:
  yearsExperience: 1
  profileComplete: true
  skills: ["JavaScript"]
expected_result: { eligible: false, reason: "Insufficient experience" }
coverage: 1/6 = 16.7%

test_case_id: TC_BC_002
technique: Branch Coverage
branches_covered: [D1-True, D2-False]
test_data:
  yearsExperience: 5
  profileComplete: false
  skills: ["JavaScript"]
expected_result: { eligible: false, reason: "Incomplete profile" }
coverage: 2/6 = 33.3%

test_case_id: TC_BC_003
technique: Branch Coverage
branches_covered: [D1-True, D2-True, D3-False]
test_data:
  yearsExperience: 5
  profileComplete: true
  skills: []
expected_result: { eligible: false, reason: "No skills listed" }
coverage: 3/6 = 50%

test_case_id: TC_BC_004
technique: Branch Coverage
branches_covered: [D1-True, D2-True, D3-True]
test_data:
  yearsExperience: 5
  profileComplete: true
  skills: ["JavaScript", "React"]
expected_result: { eligible: true }
coverage: 3/6 = 50%

combined_coverage: 6/6 = 100% branch coverage
```

**Minimal Test Suite** (2 tests for 100%):
- Test with yearsExperience < 2 (covers D1-False)
- Test with valid candidate (covers D1-True, D2-True, D3-True, and implicitly D2-False, D3-False require separate tests)

Actually, minimum is **4 tests** as shown above.

#### Common Pitfalls

1. **Confusing with Path Coverage**: Branch coverage ≠ testing all possible paths
2. **Missing Compound Conditions**: Not testing all combinations of && and || operators
3. **Loop Iterations**: Not considering different loop iteration counts
4. **Short-circuit Evaluation**: Missing cases where second condition isn't evaluated
5. **Exception Paths**: Not testing error handling branches

#### Relationships to Other Techniques

- **Stronger than Statement Coverage**: 100% branch coverage guarantees 100% statement coverage (not vice versa)
- **Weaker than Path Coverage**: Can have 100% branch coverage without testing all paths
- **Supports Condition Coverage**: Related but distinct coverage criteria

---

### 3. Path Coverage

#### Definition

**Path Coverage** is the percentage of possible execution paths through the code that have been exercised by a test suite.

**100% Path Coverage**: Achieved when test cases execute every possible path from start to end of the code under test.

**Key Insight**: In code with loops and complex branching, the number of paths can be exponential or infinite, making 100% path coverage impractical or impossible.

#### When to Use

- Critical algorithms with limited paths
- Safety-critical code sections
- Security-sensitive functions (e.g., authentication, authorization)
- Small, well-defined functions
- When required by standards (rare, due to impracticality)

**Practical Reality**: Path coverage is often too expensive for production use. It's typically replaced by branch coverage plus additional techniques like basis path testing.

#### How to Apply

**Step-by-Step Process:**

1. **Draw control flow graph**: Create a graph with nodes (statements) and edges (control flow)
2. **Enumerate paths**: List all unique paths from entry to exit
3. **Calculate cyclomatic complexity**: Approximate the number of independent paths
4. **Design test cases**: Create at least one test per unique path
5. **Calculate coverage**:
   ```
   Path Coverage = (Paths Executed / Total Possible Paths) × 100%
   ```

**Cyclomatic Complexity** (McCabe): V(G) = E - N + 2P
- E = number of edges
- N = number of nodes
- P = number of connected components (usually 1)

#### Example: Job Seeker Application

**Code Under Test**: Simplified job matching score

```javascript
function calculateMatchScore(candidate, job) {
  let score = 0;

  // Decision 1
  if (candidate.yearsExperience >= job.minExperience) {
    score += 30;
  }

  // Decision 2
  if (candidate.skills.some(s => job.requiredSkills.includes(s))) {
    score += 40;
  }

  // Decision 3
  if (candidate.location === job.location) {
    score += 30;
  }

  return score;
}
```

**Possible Paths**: 2³ = 8 paths

| Path | D1  | D2  | D3  | Score | Description                    |
|------|-----|-----|-----|-------|--------------------------------|
| P1   | F   | F   | F   | 0     | No matches                     |
| P2   | F   | F   | T   | 30    | Location only                  |
| P3   | F   | T   | F   | 40    | Skills only                    |
| P4   | F   | T   | T   | 70    | Skills + Location              |
| P5   | T   | F   | F   | 30    | Experience only                |
| P6   | T   | F   | T   | 60    | Experience + Location          |
| P7   | T   | T   | F   | 70    | Experience + Skills            |
| P8   | T   | T   | T   | 100   | Perfect match                  |

**Test Cases for 100% Path Coverage** (8 tests required):

```yaml
test_case_id: TC_PC_001
technique: Path Coverage
path: P1 (No matches)
test_data:
  candidate: { yearsExperience: 1, skills: ["Java"], location: "NY" }
  job: { minExperience: 5, requiredSkills: ["Python"], location: "SF" }
expected_result: score = 0

test_case_id: TC_PC_008
technique: Path Coverage
path: P8 (Perfect match)
test_data:
  candidate: { yearsExperience: 5, skills: ["Python"], location: "SF" }
  job: { minExperience: 3, requiredSkills: ["Python"], location: "SF" }
expected_result: score = 100

# ... 6 more tests for paths P2-P7
```

**Comparison with Branch Coverage**:
- **Branch Coverage**: Requires 4 tests (one for each decision's true and false)
- **Path Coverage**: Requires 8 tests (all combinations)

#### Common Pitfalls

1. **Ignoring Infeasible Paths**: Attempting to test paths that cannot occur due to logic constraints
2. **Loop Explosion**: Not recognizing that loops create infinite or impractical numbers of paths
3. **Resource Waste**: Over-investing in path coverage when branch coverage suffices
4. **Missing Business Logic**: Focusing on technical paths instead of meaningful scenarios
5. **No Prioritization**: Treating all paths equally instead of focusing on high-risk paths

#### Relationships to Other Techniques

- **Strongest Coverage Metric**: Subsumes statement and branch coverage
- **Often Impractical**: Typically replaced by basis path testing (cyclomatic complexity)
- **Complements Risk-Based Testing**: Use path coverage on high-risk code only

---

### 4. Condition Coverage

#### Definition

**Condition Coverage** (also called Predicate Coverage) is the percentage of condition outcomes that have been exercised by a test suite.

A **condition** is a Boolean expression (e.g., `x > 0`, `isActive`, `count < 10`). In compound decisions with multiple conditions (e.g., `if (x > 0 && y < 10)`), condition coverage requires testing each individual condition's true and false outcomes.

**Key Distinction**:
- **Decision Coverage**: Tests each decision's overall true/false outcome
- **Condition Coverage**: Tests each condition within decisions independently

#### When to Use

- Testing complex Boolean expressions with multiple conditions
- Finding subtle logic errors in compound conditions
- Safety-critical systems with complex decision logic
- When decisions use AND, OR, NOT operators
- Debugging intermittent failures related to specific condition combinations

**Important**: Condition coverage is weaker than decision coverage in one sense—you can have 100% condition coverage without 100% decision coverage. **Multiple Condition Coverage** (testing all combinations) is stronger than both.

#### How to Apply

**Step-by-Step Process:**

1. **Identify conditions**: Find all Boolean sub-expressions in decisions
2. **Count condition outcomes**: Each condition has true and false outcomes
3. **Design test cases**: Ensure each condition evaluates to true at least once and false at least once
4. **Calculate coverage**:
   ```
   Condition Coverage = (Condition Outcomes Tested / Total Condition Outcomes) × 100%
   ```

#### Example: Job Seeker Application

**Code Under Test**: Application approval logic

```javascript
function canApproveApplication(application, employer) {
  // Compound decision with 3 conditions
  if (application.status === "Submitted" &&
      employer.verified &&
      !application.flaggedForReview) {
    return true;
  }
  return false;
}
```

**Conditions**:
- C1: `application.status === "Submitted"`
- C2: `employer.verified`
- C3: `!application.flaggedForReview`

**Condition Coverage Requirements**: Each condition must be true once and false once (6 condition outcomes)

**Test Cases for 100% Condition Coverage**:

```yaml
test_case_id: TC_CC_001
technique: Condition Coverage
conditions_tested:
  C1: false
  C2: true
  C3: true
test_data:
  application: { status: "Draft", flaggedForReview: false }
  employer: { verified: true }
expected_result: false
coverage: C1-false, C2-true, C3-true (3/6)

test_case_id: TC_CC_002
technique: Condition Coverage
conditions_tested:
  C1: true
  C2: false
  C3: false
test_data:
  application: { status: "Submitted", flaggedForReview: true }
  employer: { verified: false }
expected_result: false
coverage: C1-true, C2-false, C3-false (3/6)

combined_coverage: 6/6 = 100% condition coverage
```

**Problem**: These 2 tests achieve 100% condition coverage BUT:
- Never test the scenario where all conditions are true (decision outcome = true)
- Only test false decision outcome
- **Do NOT achieve 100% decision coverage!**

**Better Test Suite** (Decision + Condition Coverage):

```yaml
test_case_id: TC_CC_003
technique: Condition Coverage + Decision Coverage
conditions_tested:
  C1: true
  C2: true
  C3: true
test_data:
  application: { status: "Submitted", flaggedForReview: false }
  employer: { verified: true }
expected_result: true  # Decision outcome = true
coverage: Decision-true

# Add to TC_CC_001 and TC_CC_002 for complete coverage
```

**Multiple Condition Coverage** (all combinations): Would require 2³ = 8 tests

#### Common Pitfalls

1. **Assuming Condition = Decision Coverage**: They are different criteria
2. **Ignoring Short-Circuit Evaluation**: In `A && B`, if A is false, B may not be evaluated
3. **Missing Compound Conditions**: Not identifying all individual conditions
4. **Combinatorial Explosion**: Not recognizing when multiple condition coverage is needed
5. **Tool Confusion**: Misinterpreting coverage tool reports

#### Relationships to Other Techniques

- **Different from Decision Coverage**: Can have 100% of one without 100% of the other
- **Weaker than MC/DC**: Modified Condition/Decision Coverage is stronger
- **Related to Path Coverage**: Multiple condition coverage approaches path coverage

---

## Experience-Based Techniques {#experience-based-techniques}

Experience-based test design techniques derive tests from the tester's skill, intuition, and experience with similar applications and technologies. These techniques complement specification-based and structure-based techniques by leveraging human expertise to find defects that formal techniques might miss.

**Key Characteristic**: Tests are based on individual knowledge rather than documented specifications or code structure. They are particularly effective for finding defects early when specifications are incomplete or under severe time pressure.

**Important Distinction**: Experience-based testing is NOT the same as ad hoc testing. While both rely on tester knowledge, experience-based testing is structured and guided by specific objectives, whereas ad hoc testing has no structure.

---

### 1. Error Guessing

#### ISTQB Definition

**Error Guessing** is a test design technique where the experience of the tester is used to anticipate what defects might be present in the component or system under test, based on errors observed in the past.

A structured approach called **Fault Attack** involves enumerating a list of possible defects and designing tests to attack these defects.

#### When to Use

- Risk analysis to identify error-prone areas
- Targeting known problem patterns from past projects
- Testing new or unfamiliar technology
- Supplement to formal techniques
- When development team has known weaknesses
- After reviewing code/design for common anti-patterns

#### How to Apply

**Step-by-Step Process:**

1. **Review historical defects**: Analyze past projects for common error types
2. **Brainstorm potential errors**: Based on:
   - Technology risks (new frameworks, libraries)
   - Common coding mistakes (null checks, boundary errors)
   - Business logic complexity
   - Integration points
   - User interaction patterns
3. **Create defect hypothesis list**: Document suspected error types
4. **Design targeted tests**: Create test cases to trigger each suspected error
5. **Prioritize by risk**: Focus on high-impact or high-probability errors

**Common Error Categories:**
- **Input Validation**: Missing null checks, boundary violations
- **State Management**: Race conditions, inconsistent state
- **Integration**: API contract violations, timeout handling
- **Business Logic**: Calculation errors, rule misinterpretation
- **Security**: Injection attacks, authentication bypass
- **Usability**: Confusing workflows, missing feedback

#### Example: Job Seeker Application

**Scenario**: Testing job search functionality based on past defects

**Error Hypothesis List**:

```yaml
error_category: Input Validation
suspected_errors:
  - Search with special characters causes errors
  - Empty search query crashes system
  - Very long search terms (>1000 chars) cause timeout
  - SQL injection via search field

error_category: Business Logic
suspected_errors:
  - Salary range filter allows min > max
  - Date filters allow "Posted Before" > "Posted After"
  - Negative values in experience filter
  - Zero results page doesn't handle pagination

error_category: Performance
suspected_errors:
  - Search with no filters returns millions of results
  - Repeated searches don't use caching
  - Concurrent searches from same user cause conflicts

error_category: Integration
suspected_errors:
  - Database connection timeout not handled
  - External job API failures break entire search
  - Stale cache returns deleted jobs
```

**Targeted Test Cases**:

```yaml
test_case_id: TC_EG_001
technique: Error Guessing
error_hypothesis: SQL injection via search field
test_data: searchTerm = "Engineer'; DROP TABLE jobs;--"
expected_result:
  - Search executes safely
  - No SQL error
  - Results filtered correctly (no injection)
ddd_reference:
  bounded_context: bc_job_search
  anti_corruption_layer: input_sanitization

test_case_id: TC_EG_002
technique: Error Guessing
error_hypothesis: Salary range min > max causes logic error
test_data:
  salaryMin: 150000
  salaryMax: 50000
expected_result:
  - Validation error: "Minimum salary cannot exceed maximum salary"
  - OR system auto-swaps values
  - OR no results returned (acceptable)
```

**Defect Patterns from Experience**:

| Pattern                          | Where to Test                  | Example Test                          |
|----------------------------------|--------------------------------|---------------------------------------|
| Null pointer exceptions          | All optional fields            | Submit form with all fields empty     |
| Off-by-one errors                | Array/list operations          | Test with 0, 1, max items             |
| Race conditions                  | Concurrent operations          | Simultaneous updates to same record   |
| Integer overflow                 | Large number inputs            | Enter MAX_INT + 1                     |
| Timezone issues                  | Date/time operations           | Test across midnight, DST changes     |
| Character encoding               | International text             | Enter emoji, Chinese characters       |

#### Common Pitfalls

1. **Bias Toward Recent Defects**: Over-focusing on latest bugs while missing new error types
2. **Undocumented Approach**: Not recording error hypotheses and rationale
3. **No Systematic Coverage**: Random guessing without structured approach
4. **Ignoring New Technology Risks**: Relying only on past experience with old tech
5. **Not Sharing Knowledge**: Individual tester knowledge not leveraged by team

#### Relationships to Other Techniques

- **Complements Formal Techniques**: Finds defects missed by systematic approaches
- **Informs Risk-Based Testing**: Error-prone areas get higher priority
- **Supports Exploratory Testing**: Error hypotheses guide exploration
- **Improves with Retrospectives**: Defect analysis feeds future error guessing

---

### 2. Exploratory Testing

#### ISTQB Definition

**Exploratory Testing** is an approach to testing where the tester actively explores the system while simultaneously designing and executing tests, using feedback from the previous test to inform the next test, based on a **test charter** containing test objectives.

**Key Characteristics**:
- Concurrent test design, execution, and learning
- Guided by test charters (time-boxed objectives)
- Adapts based on what is discovered
- **Not ad hoc testing** - structured and purposeful

#### When to Use

- Early in development when specifications are incomplete
- Severe time pressure with limited formal test design time
- Supplement to scripted testing
- Usability and user experience testing
- Learning a new system or feature
- Finding defects missed by formal techniques
- Testing complex, poorly documented systems

**ISTQB Guidance**: "Most useful where there are few or inadequate specifications and severe time pressure."

#### How to Apply

**Step-by-Step Process:**

1. **Create test charter**: Define objective, scope, time-box (typically 60-90 minutes)
2. **Prepare**: Gather necessary test data, environments, tools
3. **Explore**: Interact with system, design tests based on observations
4. **Document**: Record tests executed, defects found, notes
5. **Debrief**: Review findings, update charters for future sessions

**Test Charter Template**:

```yaml
charter_id: EXP-001
mission: Explore job application submission to discover usability issues
scope:
  - Application form interactions
  - Validation feedback
  - Submission workflow
time_box: 90 minutes
tester: [Name]
areas_to_explore:
  - Form field behavior
  - Error message clarity
  - Resume upload handling
  - Progress indicators
```

#### Example: Job Seeker Application

**Test Charter**: Explore candidate profile creation to discover data validation and user experience issues

**Session Details**:
- **Time**: 90 minutes
- **Tester**: Senior QA Engineer
- **Build**: v2.3.1
- **Mission**: Find validation, usability, and data integrity issues in profile creation

**Exploration Notes** (condensed):

```yaml
session_id: EXP-PROFILE-001
charter: Explore candidate profile creation
duration: 90 minutes
coverage:
  - Tested all required fields
  - Explored optional fields
  - Investigated validation rules
  - Tried unusual data combinations

tests_executed:
  - Created profile with minimum required data
  - Attempted profile with all fields empty
  - Entered extremely long text in all fields
  - Used special characters in name fields
  - Uploaded various file types as resume
  - Tested browser back button during creation
  - Switched tabs during file upload
  - Tested with slow network (throttling)

defects_found:
  - BUG-001: Resume upload fails silently with .docx files >5MB
  - BUG-002: No feedback when saving profile with network error
  - BUG-003: Skills field accepts duplicate entries
  - BUG-004: "Save Draft" button disabled during file upload (no indication why)
  - BUG-005: Browser back button loses all entered data (no warning)

questions_raised:
  - Should profile creation be single-page or multi-step?
  - What happens to draft profiles after 30 days?
  - Can users import profile from LinkedIn?

ideas_for_future_testing:
  - Test profile creation on mobile devices
  - Test accessibility with screen readers
  - Test concurrent profile edits from multiple devices
  - Performance test with 100+ skills

risk_areas_identified:
  - File upload handling needs comprehensive testing
  - Network error scenarios inadequately handled
  - User experience during long operations needs improvement
```

**Session Debrief**:
- **Coverage**: 70% - focused on core functionality, didn't explore integrations
- **Effectiveness**: 5 defects found, 2 high priority
- **Follow-up**: Create regression tests for defects, schedule mobile testing charter

#### Common Pitfalls

1. **Confusing with Ad Hoc Testing**: Exploring without charters or documentation
2. **No Time-Boxing**: Sessions that run too long lose focus
3. **Poor Documentation**: Not recording findings for future reference
4. **No Clear Mission**: Unfocused exploration yields few insights
5. **Ignoring Scripted Tests**: Using exploratory testing as a replacement rather than complement
6. **Solo Activity**: Not sharing findings or collaborating with other testers

#### Relationships to Other Techniques

- **Complements Scripted Testing**: Finds defects that scripted tests miss
- **Uses Error Guessing**: Error hypotheses guide exploration
- **Informs Test Automation**: Discoveries become automated regression tests
- **Supports Usability Testing**: Natural approach for UX evaluation
- **Enables Rapid Feedback**: Quick learning in agile/iterative development

**Best Practice**: "Exploratory testing can find defects missed by specification-based and structure-based techniques."

---

### 3. Checklist-Based Testing

#### ISTQB Definition

**Checklist-Based Testing** is an experience-based test design technique where testers use a high-level list of items to be checked, noted, or verified. Checklists can be based on experience, knowledge of what is important, or an understanding of why and how software fails.

**Key Characteristics**:
- High-level guidance, not detailed test cases
- Reminds testers what to verify
- Grows over time with lessons learned
- Can be specific to application type, technology, or domain

#### When to Use

- Consistency across test executions
- Onboarding new testers
- Regression testing when detailed test cases are too expensive
- Compliance and regulatory requirements
- Quality gates and release criteria
- Code review and inspection checklists

#### How to Apply

**Step-by-Step Process:**

1. **Identify checklist scope**: Determine what area/activity the checklist covers
2. **Gather items**: Collect requirements, standards, regulations, past defects
3. **Organize checklist**: Group related items, prioritize by importance
4. **Review and refine**: Validate checklist with stakeholders
5. **Use during testing**: Testers follow checklist and note results
6. **Maintain**: Update based on new defects, requirements, or lessons learned

**Checklist Structure**:
- **Item**: What to check
- **Criteria**: How to verify (pass/fail criteria)
- **Notes**: Space for observations
- **Result**: Pass/Fail/N/A

#### Example: Job Seeker Application

**Checklist**: Pre-Release Quality Gate for Job Posting Feature

```yaml
checklist_id: QA-GATE-JOB-POSTING
version: 1.2
last_updated: 2025-10-04
scope: Job posting creation and management
usage: Required for production release approval

functional_requirements:
  - id: FR-01
    item: Job posting can be created with all required fields
    criteria: Title, description, location, salary range all save correctly
    result: [ ] Pass [ ] Fail [ ] N/A
    notes: ___________

  - id: FR-02
    item: Job posting can be edited after creation
    criteria: All fields editable, changes persist after save
    result: [ ] Pass [ ] Fail [ ] N/A
    notes: ___________

  - id: FR-03
    item: Job posting can be deleted (soft delete)
    criteria: Posting marked inactive, not visible to candidates
    result: [ ] Pass [ ] Fail [ ] N/A
    notes: ___________

  - id: FR-04
    item: Draft postings can be saved and completed later
    criteria: Draft state persists, can be edited and published
    result: [ ] Pass [ ] Fail [ ] N/A
    notes: ___________

validation:
  - id: VAL-01
    item: Job title validates length (3-100 characters)
    criteria: Rejects <3 and >100, accepts valid lengths
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: VAL-02
    item: Salary range validates min <= max
    criteria: Error shown when min > max
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: VAL-03
    item: Required fields prevent submission when empty
    criteria: Clear error messages for all required fields
    result: [ ] Pass [ ] Fail [ ] N/A

security:
  - id: SEC-01
    item: Only employer can create/edit their own postings
    criteria: Authorization checks prevent unauthorized access
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: SEC-02
    item: XSS protection on all text inputs
    criteria: Script tags in description/title are sanitized
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: SEC-03
    item: Rate limiting on posting creation
    criteria: Cannot create >10 postings per hour
    result: [ ] Pass [ ] Fail [ ] N/A

performance:
  - id: PERF-01
    item: Posting creation completes within 2 seconds
    criteria: 95th percentile response time < 2s under normal load
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: PERF-02
    item: Search returns results within 500ms
    criteria: Average search query < 500ms for 100K postings
    result: [ ] Pass [ ] Fail [ ] N/A

usability:
  - id: UX-01
    item: Form provides clear feedback on save/error
    criteria: Success/error messages visible and understandable
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: UX-02
    item: Required fields clearly marked with asterisk
    criteria: All required fields have visual indicator
    result: [ ] Pass [ ] Fail [ ] N/A

accessibility:
  - id: A11Y-01
    item: Form navigable by keyboard only
    criteria: All fields, buttons accessible via tab/enter
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: A11Y-02
    item: Screen reader announces field labels and errors
    criteria: ARIA labels present, error messages read aloud
    result: [ ] Pass [ ] Fail [ ] N/A

documentation:
  - id: DOC-01
    item: API documentation updated for posting endpoints
    criteria: Swagger/OpenAPI docs reflect current implementation
    result: [ ] Pass [ ] Fail [ ] N/A

  - id: DOC-02
    item: User guide includes job posting instructions
    criteria: Help documentation covers creation, editing, deletion
    result: [ ] Pass [ ] Fail [ ] N/A

sign_off:
  qa_lead: ________________  date: ______
  product_owner: __________  date: ______
  tech_lead: ______________  date: ______
```

**Code Review Checklist** (Example):

```yaml
checklist_id: CODE-REVIEW-BACKEND
scope: Backend code review for Job Seeker application

code_quality:
  - [ ] Functions have single responsibility
  - [ ] Variable/function names are descriptive
  - [ ] No code duplication (DRY principle)
  - [ ] Complex logic has explanatory comments
  - [ ] Magic numbers replaced with named constants

error_handling:
  - [ ] All external calls wrapped in try-catch
  - [ ] Errors logged with sufficient context
  - [ ] User-facing errors are user-friendly
  - [ ] No swallowed exceptions (empty catch blocks)

security:
  - [ ] No sensitive data in logs
  - [ ] Input validation on all user inputs
  - [ ] SQL queries use parameterized statements
  - [ ] Authentication/authorization checks present
  - [ ] No hard-coded credentials

performance:
  - [ ] No N+1 query problems
  - [ ] Database indexes used for frequent queries
  - [ ] Large data sets paginated
  - [ ] Caching used where appropriate

testing:
  - [ ] Unit tests cover happy path
  - [ ] Unit tests cover error cases
  - [ ] Test names clearly describe what is tested
  - [ ] No commented-out tests
  - [ ] Integration tests for external dependencies

ddd_patterns:
  - [ ] Value Objects immutable
  - [ ] Aggregate boundaries respected
  - [ ] Domain events published for state changes
  - [ ] Business logic in domain layer, not controllers
  - [ ] Repository pattern used for data access
```

#### Common Pitfalls

1. **Too Detailed**: Creating scripts instead of checklists (loses flexibility)
2. **Stale Checklists**: Not updating based on new requirements or defects
3. **Too Generic**: Checklist not specific enough to add value
4. **No Accountability**: Not tracking who completed checklist or when
5. **Checkbox Mentality**: Mechanically checking items without critical thinking
6. **No Continuous Improvement**: Not incorporating lessons learned

#### Relationships to Other Techniques

- **Supports Exploratory Testing**: Checklists guide exploration while allowing flexibility
- **Complements Error Guessing**: Past defects inform checklist items
- **Enables Consistency**: Same checklist used by multiple testers yields consistent results
- **Feeds Process Improvement**: Checklist gaps reveal process weaknesses

---

## Technique Selection Guidelines {#technique-selection}

### How to Choose the Right Technique

Selecting appropriate test design techniques depends on multiple factors. Use this decision framework:

**1. Test Objective**

| Objective                        | Recommended Techniques                        |
|----------------------------------|-----------------------------------------------|
| Validate requirements            | EP, BVA, Use Case Testing                     |
| Test business rules              | Decision Tables, Use Case Testing             |
| Test workflows                   | State Transition, Use Case Testing            |
| Achieve code coverage            | Statement, Branch, Path Coverage              |
| Find usability issues            | Exploratory Testing, Checklist-Based          |
| Test under time pressure         | Exploratory Testing, Error Guessing           |
| Compliance/regulatory testing    | Checklist-Based Testing                       |

**2. Test Level**

| Test Level        | Primary Techniques                               |
|-------------------|--------------------------------------------------|
| Unit Testing      | Statement Coverage, Branch Coverage, EP, BVA     |
| Integration       | Decision Tables, State Transition, Use Cases     |
| System Testing    | All Black-Box techniques, Exploratory            |
| Acceptance        | Use Case Testing, Checklist-Based                |

**3. Available Information**

| Available                  | Suitable Techniques                          |
|----------------------------|----------------------------------------------|
| Requirements/specs         | EP, BVA, Decision Tables, Use Cases          |
| Source code                | Statement, Branch, Path, Condition Coverage  |
| Domain expertise           | Error Guessing, Exploratory, Checklist-Based |
| State models               | State Transition Testing                     |
| Little documentation       | Exploratory Testing, Error Guessing          |

**4. Risk and Criticality**

| Risk Level | Approach                                                                 |
|------------|--------------------------------------------------------------------------|
| High       | Combine multiple techniques; aim for high code coverage; formal reviews  |
| Medium     | Black-box techniques + selective white-box                               |
| Low        | Exploratory testing, checklist-based, smoke testing                      |

### Combining Techniques

**Best Practice**: Use multiple complementary techniques for thorough testing.

**Effective Combinations**:

1. **Input Validation**: EP + BVA
   - EP identifies partitions, BVA tests edges

2. **Business Logic**: Decision Tables + State Transition
   - Decision tables for rules, state transition for workflows

3. **Code Quality**: Statement Coverage + Branch Coverage + Code Review Checklist
   - Coverage metrics + human review for completeness

4. **Agile Sprint Testing**: Use Cases + Exploratory + Error Guessing
   - Scripted tests for requirements + unscripted for discovery

5. **Regression Testing**: Automated Use Cases + Risk-Based Selection + Smoke Checklist
   - Full automation + prioritization + quick verification

**Technique Coverage Matrix**:

| Technique                | Functional | Non-Functional | Defect Detection | Coverage Measurement |
|--------------------------|------------|----------------|------------------|----------------------|
| Equivalence Partitioning | High       | Low            | Medium           | Partition Coverage   |
| Boundary Value Analysis  | High       | Low            | High (boundaries)| Boundary Coverage    |
| Decision Table           | High       | Low            | High (logic)     | Rule Coverage        |
| State Transition         | High       | Medium         | High (sequences) | State/Transition Cov.|
| Use Case Testing         | High       | Medium         | Medium           | Scenario Coverage    |
| Statement Coverage       | N/A        | N/A            | Low              | % Statements         |
| Branch Coverage          | N/A        | N/A            | Medium           | % Branches           |
| Path Coverage            | N/A        | N/A            | High             | % Paths              |
| Error Guessing           | High       | High           | High (experience)| Subjective           |
| Exploratory Testing      | High       | High           | High (discovery) | Time-Boxed           |
| Checklist-Based          | High       | High           | Medium           | Checklist Items      |

### Common Mistakes in Technique Selection

1. **Using Only One Technique**: Missing defects that other techniques would find
2. **Ignoring Context**: Applying techniques without considering project constraints
3. **Over-Engineering**: Using complex techniques when simple ones suffice
4. **Neglecting Experience-Based**: Relying only on formal techniques
5. **No Rationale**: Not documenting why techniques were chosen
6. **Technique for Technique's Sake**: Applying techniques to meet standards without value

---

## Integration with Job Seeker Context {#job-seeker-integration}

### Mapping Techniques to Job Seeker Bounded Contexts

**Bounded Context: Profile Management**

| Feature                  | Primary Techniques                    | Rationale                                    |
|--------------------------|---------------------------------------|----------------------------------------------|
| Candidate Profile Fields | EP, BVA                               | Input validation on text, numbers, dates     |
| Profile Completeness     | Decision Tables                       | Multiple conditions determine completeness   |
| Profile State (Draft/Published) | State Transition              | Profile moves through states                 |
| Resume Upload            | Error Guessing, Exploratory           | File handling is error-prone                 |
| Profile API Endpoints    | Statement/Branch Coverage             | Code coverage for API logic                  |

**Bounded Context: Job Application**

| Feature                  | Primary Techniques                    | Rationale                                    |
|--------------------------|---------------------------------------|----------------------------------------------|
| Application Submission   | Use Case Testing                      | End-to-end workflow with multiple actors     |
| Application State Flow   | State Transition                      | Draft→Submitted→Under Review→Accepted/Rejected |
| Eligibility Checks       | Decision Tables                       | Rules based on experience, skills, location  |
| Application Form         | EP, BVA, Checklist-Based              | Input validation + UX quality checklist      |
| Application Business Logic | Branch Coverage                     | Ensure all decision paths tested             |

**Bounded Context: Job Search**

| Feature                  | Primary Techniques                    | Rationale                                    |
|--------------------------|---------------------------------------|----------------------------------------------|
| Search Filters           | EP, BVA                               | Salary range, date range, numeric filters    |
| Search Results Ranking   | Exploratory Testing                   | Complex algorithm, heuristic evaluation      |
| Saved Search Alerts      | State Transition                      | Alert states: active, paused, expired        |
| Search Performance       | Performance Testing Checklist         | Response time, throughput requirements       |
| Search API               | Use Case Testing, Error Guessing      | Integration with external job sources        |

**Bounded Context: Identity & Access**

| Feature                  | Primary Techniques                    | Rationale                                    |
|--------------------------|---------------------------------------|----------------------------------------------|
| User Registration        | Use Case Testing, Decision Tables     | Workflow + validation rules                  |
| Login/Authentication     | Decision Tables, Error Guessing       | Multiple conditions + security focus         |
| Password Reset           | State Transition, Use Case            | Multi-step workflow with states              |
| Authorization            | Decision Tables, Path Coverage        | Role-based access rules, critical security   |
| Session Management       | Exploratory, Error Guessing           | Complex behavior, edge cases                 |

### Test Design Technique Traceability

**Example: End-to-End Application Flow**

```yaml
epic: Candidate applies for job
user_story: As a candidate, I want to apply for a job so that I can be considered for the position

test_design_coverage:

  equivalence_partitioning:
    - test: Years of experience field
      partitions: [valid: 0-50, invalid: <0, invalid: >50, invalid: non-numeric]
      test_cases: [TC_EP_001, TC_EP_002, TC_EP_003, TC_EP_004]

  boundary_value_analysis:
    - test: Job title character limit
      boundaries: [2, 3, 4, 99, 100, 101]
      test_cases: [TC_BVA_001, TC_BVA_002, TC_BVA_003, TC_BVA_004, TC_BVA_005, TC_BVA_006]

  decision_table:
    - test: Application eligibility
      conditions: [has_profile, has_resume, meets_experience, no_duplicate_application]
      rules: 16 combinations
      test_cases: [TC_DT_001 through TC_DT_016]

  state_transition:
    - test: Application lifecycle
      states: [Draft, Submitted, Under Review, Interview, Rejected, Accepted, Withdrawn]
      transitions: 12 valid, 8 invalid
      test_cases: [TC_ST_001 through TC_ST_020]

  use_case:
    - test: Submit job application (happy path)
      test_cases: [TC_UC_001]
    - test: Submit job application (no resume)
      test_cases: [TC_UC_002]
    - test: Submit job application (duplicate)
      test_cases: [TC_UC_003]

  statement_coverage:
    - test: ApplicationService.submitApplication()
      target: 100%
      test_cases: [TC_SC_001 through TC_SC_008]

  branch_coverage:
    - test: eligibilityCheck() method
      target: 100%
      test_cases: [TC_BC_001 through TC_BC_004]

  exploratory:
    - charter: Explore application form usability
      session_id: EXP-APP-001
      time_box: 90 minutes
      findings: [BUG-101, BUG-102, UX-improvement-005]

  error_guessing:
    - hypothesis: Concurrent application submissions cause duplicate
      test_cases: [TC_EG_001]
    - hypothesis: Large file upload times out
      test_cases: [TC_EG_002]

  checklist:
    - checklist_id: QA-GATE-APPLICATION
      items_checked: 45
      pass_rate: 100%
      gate_passed: true

total_test_cases_designed: 87
techniques_used: 9
coverage_confidence: High
```

---

## References {#references}

### Primary Standards and Sources

1. **ISTQB Foundation Level Syllabus v4.0.1 (2024)**
   - URL: https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf
   - Chapter 4: Test Design Techniques
   - Authoritative definitions for all black-box, white-box, and experience-based techniques

2. **ISTQB Glossary**
   - URL: https://glossary.istqb.org
   - Official definitions for terminology used throughout this document

3. **ISO/IEC/IEEE 29119-4:2021 - Software Testing Part 4: Test Techniques**
   - URL: https://www.iso.org/standard/79430.html
   - International standard defining specification-based, structure-based, and experience-based test design techniques

4. **ISO/IEC 25010:2011 - System and Software Quality Models**
   - URL: https://www.iso.org/standard/35733.html
   - Quality characteristics framework referenced in non-functional testing

### Industry Resources

5. **ISTQB Foundation Sample Exam v4.0 (2024)**
   - URL: https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_v4.0_Sample-Exam-C-Questions_v1.5.pdf
   - Example questions demonstrating technique application

6. **Software Testing Help - Test Design Techniques**
   - Boundary Value Analysis & Equivalence Partitioning: https://www.softwaretestinghelp.com/what-is-boundary-value-analysis-and-equivalence-partitioning/
   - Decision Table Testing: https://www.softwaretestinghelp.com/decision-table-test-design-technique/

7. **Get Software Service - ISTQB Guides**
   - Black Box Techniques: https://www.getsoftwareservice.com/black-box-techniques/
   - White Box Testing: https://www.getsoftwareservice.com/white-box-testing-techniques/

8. **ToolsQA - ISTQB Certification Tutorials**
   - State Transition Testing: https://www.toolsqa.com/software-testing/istqb/state-transition-testing-diagram-example-and-technique
   - Decision Table Testing: https://www.toolsqa.com/software-testing/istqb/decision-table-testing/

### Academic and Practitioner Resources

9. **GeeksforGeeks - Software Testing**
   - Statement Coverage: https://www.geeksforgeeks.org/statement-coverage-testing/
   - State Transition Diagrams: https://www.geeksforgeeks.org/software-engineering/state-transition-diagram-for-an-atm-system/

10. **Guru99 - Testing Techniques**
    - Boundary Value Analysis: https://www.guru99.com/equivalence-partitioning-boundary-value-analysis.html
    - Decision Table Testing: https://www.guru99.com/decision-table-testing.html

### Coverage Calculation Resources

11. **ISTQB Guru - Coverage Calculation Guide**
    - URL: https://www.istqb.guru/how-to-calculate-statement-branchdecision-and-path-coverage-for-istqb-exam-purpose/
    - Detailed examples of calculating statement, branch, and path coverage

### Related Job Seeker Documentation

12. **Domain V: Test Types and Characteristics**
    - File: `/Users/igor/code/marina/job-seeker/wiki/research/qe/deliverables/05-domain-V-test-types.md`
    - Integration with functional and non-functional test types

13. **Domain III: Test Artifacts**
    - File: `/Users/igor/code/marina/job-seeker/wiki/research/qe/deliverables/04-domain-III-artifacts.md`
    - Test case templates and traceability matrices

14. **Domain II: QE Ontologies**
    - File: `/Users/igor/code/marina/job-seeker/wiki/research/qe/deliverables/03-domain-II-ontologies.md`
    - Taxonomy and semantic relationships between techniques

---

## Appendix: Quick Reference Tables

### Technique Comparison Matrix

| Technique                | Basis          | Level     | Strength                  | Limitation                      |
|--------------------------|----------------|-----------|---------------------------|---------------------------------|
| Equivalence Partitioning | Specification  | Black-Box | Reduces test cases        | May miss boundary errors        |
| Boundary Value Analysis  | Specification  | Black-Box | Finds boundary defects    | Only for ordered inputs         |
| Decision Table           | Specification  | Black-Box | Complete rule coverage    | Combinatorial explosion         |
| State Transition         | Specification  | Black-Box | Workflow validation       | Complex diagrams                |
| Use Case                 | Specification  | Black-Box | End-to-end scenarios      | May miss technical edge cases   |
| Statement Coverage       | Code           | White-Box | Simple, measurable        | Weak - doesn't test logic       |
| Branch Coverage          | Code           | White-Box | Tests decision logic      | Doesn't test all paths          |
| Path Coverage            | Code           | White-Box | Comprehensive             | Often impractical               |
| Condition Coverage       | Code           | White-Box | Tests individual conditions| May miss decision outcomes     |
| Error Guessing           | Experience     | Experience| Finds unusual defects     | Inconsistent, hard to measure   |
| Exploratory Testing      | Experience     | Experience| Rapid learning & feedback | Requires skilled testers        |
| Checklist-Based          | Experience     | Experience| Consistent, efficient     | May miss items not on list     |

### Coverage Hierarchy

```
Path Coverage (strongest, most comprehensive)
    ⇧
Condition Coverage (all condition outcomes)
    ⇧
Branch/Decision Coverage (all decision outcomes)
    ⇧
Statement Coverage (weakest, baseline)
```

**Key Relationships**:
- 100% Path Coverage ⇒ 100% Branch Coverage ⇒ 100% Statement Coverage
- 100% Statement Coverage ⇏ 100% Branch Coverage
- 100% Condition Coverage ⇏ 100% Decision Coverage (and vice versa)

### When to Use What (Decision Tree)

```
Do you have access to source code?
├─ YES → Consider White-Box Techniques
│  ├─ Unit testing? → Statement + Branch Coverage
│  ├─ Critical code? → Path Coverage or MC/DC
│  └─ Integration testing? → Branch Coverage + Black-Box
│
└─ NO → Use Black-Box Techniques
   ├─ Input validation? → Equivalence Partitioning + BVA
   ├─ Business rules? → Decision Tables
   ├─ Workflows? → State Transition + Use Cases
   ├─ Time pressure? → Exploratory + Error Guessing
   └─ Compliance? → Checklist-Based

Supplement with Experience-Based in all cases
```

---

**Document Metadata:**
- **Word Count**: ~15,500
- **Techniques Covered**: 11 (5 black-box, 4 white-box, 3 experience-based)
- **Examples**: 20+ with Job Seeker context
- **References**: 14 authoritative sources
- **Status**: Complete for Domain VI research deliverable

