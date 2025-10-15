# Domain XI: Testing in Different Contexts

## Overview

Modern software testing occurs across diverse organizational and technical contexts, each requiring specialized approaches, tools, and strategies. Testing practices must adapt to methodology (Agile, DevOps), architecture (microservices, monoliths), technology (APIs, mobile), and organizational culture. Understanding context-specific challenges and solutions enables QE professionals to deliver effective testing strategies that align with project constraints and business objectives.

## 1. Agile Testing

### Core Principles

**Definition**: Testing approach integrated throughout iterative development cycles, emphasizing collaboration, continuous feedback, and delivering working software incrementally.

**Lisa Crispin's Agile Testing Quadrants**:

```
Technology-Facing          |          Business-Facing
---------------------------|---------------------------
Q1: Unit Tests             |  Q2: Functional Tests
    Component Tests        |      Story Tests
    (Support Development)  |      Prototypes/Simulations
                          |      (Guide Development)
---------------------------|---------------------------
Q3: Performance Tests      |  Q4: Exploratory Tests
    Security Tests         |      Usability Tests
    Load Tests            |      Acceptance Tests
    (Critique Product)     |      (Critique Product)

Automated <-----------> Manual
```

**Quadrant Breakdown**:

- **Q1 (Technology-Facing, Automated)**: Unit and component tests that support development
- **Q2 (Business-Facing, Automated)**: Functional tests that guide development
- **Q3 (Technology-Facing, Mixed)**: Non-functional tests that critique the product
- **Q4 (Business-Facing, Manual)**: Exploratory and usability tests that critique the product

### Sprint Testing Process

**Sprint Planning**:
- Define acceptance criteria with three amigos (developer, tester, product owner)
- Identify testability requirements
- Estimate testing effort
- Plan test automation strategy

**During Sprint**:
- Test story implementation as developed
- Pair with developers on test design
- Automate acceptance tests
- Conduct exploratory testing sessions
- Provide continuous feedback

**Sprint End**:
- Verify Definition of Done
- Regression testing
- Demo preparation
- Retrospective participation

### Continuous Feedback Mechanisms

**Real-Time Communication**:
- Daily standups with testing updates
- Instant messaging for quick questions
- Pair programming/testing sessions
- Visual information radiators (test dashboards)

**Automated Feedback**:
- CI/CD pipeline test results
- Test coverage reports
- Code quality metrics
- Build status notifications

**Periodic Reviews**:
- Sprint demos with stakeholder feedback
- Retrospectives for process improvement
- Test metrics review
- Quality trend analysis

### Job Seeker Examples

**Q1 - Unit Tests**:
```typescript
// ProfileService unit tests
describe('ProfileService', () => {
  it('should validate required profile fields', () => {
    const profile = { name: '', email: 'test@example.com' };
    expect(ProfileService.validate(profile)).toBe(false);
  });

  it('should calculate profile completeness percentage', () => {
    const profile = createPartialProfile();
    expect(ProfileService.getCompleteness(profile)).toBe(60);
  });
});
```

**Q2 - Functional Tests**:
```gherkin
Feature: Job Application Submission
  Scenario: Submit application with resume
    Given I am logged in as a job seeker
    And I have uploaded my resume
    When I apply to "Senior QE Engineer" position
    Then my application should be submitted successfully
    And I should receive a confirmation email
```

**Q3 - Performance Tests**:
```typescript
// Load test for job search API
export default function() {
  const response = http.get('https://jobseeker.com/api/jobs?q=engineer');
  check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
    'results returned': (r) => JSON.parse(r.body).jobs.length > 0
  });
}
```

**Q4 - Exploratory Testing**:
```
Charter: Explore resume builder functionality for usability issues
Duration: 90 minutes
Areas: Template selection, content editing, preview, export

Findings:
- Template preview doesn't update in real-time
- Export to PDF loses formatting for bullet points
- No auto-save warning when navigating away
- Mobile view cuts off long job titles
```

### Agile Testing Challenges

**Challenge**: Late involvement in story refinement
**Solution**: Embed testers in refinement sessions, create testability checklists

**Challenge**: Insufficient time for exploratory testing
**Solution**: Time-box exploratory sessions, rotate team members, use charter-based testing

**Challenge**: Test automation debt accumulation
**Solution**: Include automation tasks in story estimates, refactor tests regularly, apply SOLID principles

**Challenge**: Balancing speed with quality
**Solution**: Risk-based testing, automated regression suites, Definition of Done enforcement

### Tools and Practices

**Test Management**: Jira, Azure DevOps, TestRail
**Automation Frameworks**: Playwright, Cypress, Jest
**Collaboration**: Miro (three amigos sessions), Confluence (documentation)
**Continuous Integration**: GitHub Actions, Jenkins, CircleCI

---

## 2. DevOps Testing

### Core Principles

**Definition**: Testing integrated into continuous delivery pipelines, emphasizing automation, infrastructure testing, and shared responsibility for quality across development and operations.

**Key Concepts**:
- **Shift-Left**: Testing earlier in development
- **Shift-Right**: Testing in production
- **Continuous Testing**: Automated tests at every pipeline stage
- **Infrastructure as Code**: Testable, version-controlled infrastructure

### CI/CD Pipeline Testing

**Pipeline Stages**:

```
Code Commit → Build → Unit Tests → Integration Tests →
→ Security Scan → Deploy to Staging → E2E Tests →
→ Performance Tests → Deploy to Production → Monitoring
```

**Stage-Specific Testing**:

**Build Stage**:
- Compilation verification
- Dependency scanning
- Code linting and formatting checks

**Unit Test Stage**:
- Fast, isolated tests
- Code coverage measurement
- Fail fast on regression

**Integration Test Stage**:
- API contract tests
- Database integration tests
- Service interaction validation

**Security Scan Stage**:
- SAST (Static Application Security Testing)
- Dependency vulnerability scanning
- Secret detection

**Staging Deployment**:
- Smoke tests
- End-to-end scenarios
- Visual regression tests

**Performance Testing**:
- Load tests
- Stress tests
- Scalability verification

**Production Deployment**:
- Canary releases with monitoring
- Feature flag validation
- Synthetic monitoring

### Infrastructure as Code Testing

**Configuration Testing**:
```yaml
# Example: Terratest for infrastructure validation
func TestJobSeekerInfrastructure(t *testing.T) {
    terraformOptions := &terraform.Options{
        TerraformDir: "../terraform",
    }

    defer terraform.Destroy(t, terraformOptions)
    terraform.InitAndApply(t, terraformOptions)

    // Validate database instance
    dbEndpoint := terraform.Output(t, terraformOptions, "db_endpoint")
    assert.NotEmpty(t, dbEndpoint)

    // Validate load balancer
    lbURL := terraform.Output(t, terraformOptions, "lb_url")
    validateLoadBalancer(t, lbURL)
}
```

**Container Testing**:
```dockerfile
# Dockerfile for testable containers
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
HEALTHCHECK --interval=30s --timeout=3s \
  CMD node healthcheck.js || exit 1
```

**Configuration Validation**:
```yaml
# Job Seeker Kubernetes manifest testing
apiVersion: v1
kind: Pod
metadata:
  name: job-seeker-api
spec:
  containers:
  - name: api
    image: jobseeker/api:latest
    livenessProbe:
      httpGet:
        path: /health
        port: 8080
      initialDelaySeconds: 30
      periodSeconds: 10
    readinessProbe:
      httpGet:
        path: /ready
        port: 8080
      initialDelaySeconds: 5
      periodSeconds: 5
```

### Monitoring and Observability

**Synthetic Monitoring**:
```typescript
// Datadog synthetic test for Job Seeker
{
  "name": "Job Search Critical Path",
  "type": "browser",
  "config": {
    "request": {
      "url": "https://jobseeker.com"
    },
    "assertions": [
      {
        "type": "responseTime",
        "operator": "lessThan",
        "target": 2000
      }
    ]
  },
  "message": "Job search flow is broken",
  "options": {
    "device_ids": ["laptop_large", "mobile_small"],
    "locations": ["aws:us-east-1", "aws:eu-west-1"],
    "min_failure_duration": 180,
    "retry": {
      "count": 2
    }
  }
}
```

**Production Testing Strategies**:
- **Canary Releases**: Deploy to small user subset, monitor metrics
- **Blue-Green Deployment**: Parallel environments with instant rollback
- **Feature Flags**: Enable/disable features without deployment
- **Chaos Engineering**: Inject failures to test resilience

**Observability Pillars**:
1. **Logs**: Structured logging for debugging
2. **Metrics**: Performance and business metrics
3. **Traces**: Distributed request tracing
4. **Alerts**: Proactive issue detection

### Job Seeker Examples

**CI/CD Pipeline Configuration**:
```yaml
# GitHub Actions workflow
name: Job Seeker CI/CD
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'

      - name: Install dependencies
        run: npm ci

      - name: Run unit tests
        run: npm run test:unit

      - name: Run integration tests
        run: npm run test:integration

      - name: Security scan
        run: npm audit --audit-level=high

      - name: Build application
        run: npm run build

      - name: E2E tests
        run: npm run test:e2e
        env:
          TEST_URL: ${{ secrets.STAGING_URL }}

      - name: Performance tests
        run: npm run test:perf
        if: github.ref == 'refs/heads/main'

      - name: Deploy to staging
        if: github.ref == 'refs/heads/main'
        run: ./deploy-staging.sh

      - name: Smoke tests
        if: github.ref == 'refs/heads/main'
        run: npm run test:smoke
```

**Infrastructure Testing**:
```python
# Test database backup configuration
def test_database_backup_enabled():
    db = get_rds_instance('jobseeker-prod-db')
    assert db.backup_retention_period >= 7
    assert db.automated_backup_enabled == True

# Test auto-scaling configuration
def test_autoscaling_policies():
    asg = get_autoscaling_group('jobseeker-api')
    assert asg.min_size >= 2
    assert asg.max_size >= 10
    assert asg.desired_capacity >= 2
```

**Monitoring and Alerts**:
```javascript
// Application performance monitoring
const apm = require('elastic-apm-node').start({
  serviceName: 'job-seeker-api',
  environment: process.env.NODE_ENV
});

// Custom metrics for business monitoring
app.post('/api/applications', async (req, res) => {
  const span = apm.startSpan('application.submit');
  try {
    await submitApplication(req.body);
    metrics.increment('applications.submitted');
    span.setOutcome('success');
  } catch (error) {
    metrics.increment('applications.failed');
    span.setOutcome('failure');
    throw error;
  } finally {
    span.end();
  }
});
```

### DevOps Testing Challenges

**Challenge**: Test data management across environments
**Solution**: Containerized test databases, data masking tools, synthetic data generation

**Challenge**: Flaky tests breaking pipelines
**Solution**: Test quarantine, retry mechanisms, parallel execution, deterministic test design

**Challenge**: Long-running tests delaying deployments
**Solution**: Test parallelization, selective test execution, risk-based test selection

**Challenge**: Environment drift between staging and production
**Solution**: Infrastructure as Code, container consistency, environment parity checks

### Tools

**CI/CD**: GitHub Actions, Jenkins, GitLab CI, CircleCI, Azure Pipelines
**Infrastructure Testing**: Terratest, Testinfra, InSpec, Kitchen
**Container Testing**: Docker Compose, Testcontainers, Hadolint
**Monitoring**: Datadog, New Relic, Grafana, Prometheus, ELK Stack
**Security**: SonarQube, Snyk, OWASP ZAP, Trivy

---

## 3. Microservices Testing

### Core Principles

**Definition**: Testing distributed systems composed of independently deployable services, requiring strategies for service isolation, integration, and system-level verification.

**Testing Pyramid for Microservices**:

```
           /\
          /  \    E2E Tests (Few)
         /----\
        /      \  Contract Tests (More)
       /--------\
      /          \ Integration Tests (Many)
     /------------\
    /              \ Unit Tests (Most)
   /----------------\
```

**Key Challenges**:
- Service dependencies
- Network unreliability
- Data consistency across services
- Distributed tracing complexity
- Version compatibility

### Contract Testing

**Consumer-Driven Contracts**: Consumers define expectations; providers verify compliance.

**Pact Example - Job Seeker**:

**Consumer Side (Frontend)**:
```javascript
// pact-consumer.spec.js
import { PactV3 } from '@pact-foundation/pact';

const provider = new PactV3({
  consumer: 'JobSeekerUI',
  provider: 'JobListingService'
});

describe('Job Listing Service Contract', () => {
  it('returns job listings for search query', () => {
    provider
      .given('jobs exist for "QA Engineer"')
      .uponReceiving('a request for QA Engineer jobs')
      .withRequest({
        method: 'GET',
        path: '/api/jobs',
        query: { q: 'QA Engineer', location: 'San Francisco' }
      })
      .willRespondWith({
        status: 200,
        headers: { 'Content-Type': 'application/json' },
        body: {
          jobs: eachLike({
            id: like('job-123'),
            title: like('Senior QA Engineer'),
            company: like('Tech Corp'),
            location: like('San Francisco, CA'),
            salary: like({ min: 120000, max: 160000 })
          })
        }
      });

    return provider.executeTest(async (mockServer) => {
      const client = new JobListingClient(mockServer.url);
      const jobs = await client.searchJobs('QA Engineer', 'San Francisco');
      expect(jobs.length).toBeGreaterThan(0);
    });
  });
});
```

**Provider Side (Backend)**:
```javascript
// pact-provider.spec.js
const { Verifier } = require('@pact-foundation/pact');

describe('Job Listing Service Provider', () => {
  it('validates contracts from consumers', async () => {
    const opts = {
      provider: 'JobListingService',
      providerBaseUrl: 'http://localhost:3000',
      pactUrls: ['./pacts/jobseekerui-joblistingservice.json'],
      providerStatesSetupUrl: 'http://localhost:3000/pact-states',
      publishVerificationResult: true,
      providerVersion: process.env.GIT_COMMIT
    };

    await new Verifier(opts).verifyProvider();
  });
});
```

**Benefits**:
- Independent service deployment
- Early integration issue detection
- Living documentation of service interactions
- Version compatibility verification

### Service Virtualization

**Mock Services for Isolated Testing**:

```javascript
// WireMock service stub for Job Seeker
const { WireMock } = require('wiremock-captain');

describe('Application Service with Mocked Dependencies', () => {
  beforeAll(async () => {
    // Mock Resume Service
    await WireMock.stubFor({
      request: {
        method: 'GET',
        urlPathPattern: '/api/resumes/.*'
      },
      response: {
        status: 200,
        jsonBody: {
          id: '${request.pathSegments.[2]}',
          name: 'Jane Doe',
          skills: ['JavaScript', 'Testing', 'Automation']
        },
        headers: { 'Content-Type': 'application/json' }
      }
    });

    // Mock Notification Service
    await WireMock.stubFor({
      request: {
        method: 'POST',
        url: '/api/notifications/email'
      },
      response: {
        status: 202,
        fixedDelayMilliseconds: 100
      }
    });
  });

  it('submits application with mocked dependencies', async () => {
    const application = await ApplicationService.submit({
      jobId: 'job-123',
      resumeId: 'resume-456'
    });

    expect(application.status).toBe('submitted');
  });
});
```

**Service Mesh Testing**:
```yaml
# Istio virtual service for testing
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: job-listing-service
spec:
  hosts:
  - job-listing-service
  http:
  - match:
    - headers:
        x-test-scenario:
          exact: "slow-response"
    fault:
      delay:
        percentage:
          value: 100
        fixedDelay: 5s
    route:
    - destination:
        host: job-listing-service
  - route:
    - destination:
        host: job-listing-service
```

### Chaos Engineering

**Principles**:
1. Define steady-state behavior
2. Hypothesize steady state continues in experiment and control groups
3. Introduce real-world variables (server crashes, network failures)
4. Disprove hypothesis by looking for differences

**Job Seeker Chaos Experiments**:

```javascript
// Chaos Toolkit experiment
{
  "title": "Job Search Service Resilience",
  "description": "Verify job search works when recommendation service fails",
  "steady-state-hypothesis": {
    "title": "Job search returns results",
    "probes": [
      {
        "type": "probe",
        "name": "job-search-available",
        "tolerance": {
          "type": "status",
          "status": 200
        },
        "provider": {
          "type": "http",
          "url": "https://jobseeker.com/api/jobs?q=engineer",
          "timeout": 3
        }
      }
    ]
  },
  "method": [
    {
      "type": "action",
      "name": "terminate-recommendation-service",
      "provider": {
        "type": "python",
        "module": "chaosk8s.pod.actions",
        "func": "terminate_pods",
        "arguments": {
          "label_selector": "app=recommendation-service",
          "rand": true
        }
      }
    }
  ],
  "rollbacks": [
    {
      "type": "action",
      "name": "restart-recommendation-service",
      "provider": {
        "type": "python",
        "module": "chaosk8s.deployment.actions",
        "func": "scale_deployment",
        "arguments": {
          "name": "recommendation-service",
          "replicas": 3
        }
      }
    }
  ]
}
```

**Netflix Simian Army Patterns**:
- **Chaos Monkey**: Random instance termination
- **Latency Monkey**: Introduce artificial delays
- **Chaos Gorilla**: Availability zone failure

### Integration Testing Strategies

**Component Integration Tests**:
```typescript
// Test Job Application Service with real database
describe('ApplicationService Integration', () => {
  let db: Database;

  beforeAll(async () => {
    // Testcontainers for PostgreSQL
    const container = await new PostgreSQLContainer()
      .withDatabase('jobseeker_test')
      .withUsername('test')
      .withPassword('test')
      .start();

    db = await connectDatabase(container.getConnectionUri());
    await runMigrations(db);
  });

  afterAll(async () => {
    await db.close();
  });

  it('saves application to database', async () => {
    const application = await ApplicationService.create({
      jobId: 'job-123',
      userId: 'user-456',
      resumeId: 'resume-789'
    });

    const saved = await db.applications.findById(application.id);
    expect(saved).toMatchObject(application);
  });
});
```

**End-to-End Flow Testing**:
```typescript
// Test complete job application flow across services
describe('Complete Job Application Flow', () => {
  it('applies to job with resume and receives confirmation', async () => {
    // 1. User uploads resume (Resume Service)
    const resume = await uploadResume({
      file: 'test-resume.pdf',
      userId: 'user-123'
    });

    // 2. Search for jobs (Job Listing Service)
    const jobs = await searchJobs({ query: 'QA Engineer' });
    expect(jobs.length).toBeGreaterThan(0);

    // 3. Submit application (Application Service)
    const application = await submitApplication({
      jobId: jobs[0].id,
      resumeId: resume.id,
      userId: 'user-123'
    });
    expect(application.status).toBe('submitted');

    // 4. Verify notification sent (Notification Service)
    await waitFor(async () => {
      const notifications = await getNotifications('user-123');
      expect(notifications).toContainEqual(
        expect.objectContaining({
          type: 'application_confirmation',
          applicationId: application.id
        })
      );
    });
  });
});
```

### Microservices Testing Challenges

**Challenge**: Managing test data across services
**Solution**: Service-specific test data builders, contract-defined fixtures, database per service

**Challenge**: Complex distributed debugging
**Solution**: Distributed tracing (Jaeger, Zipkin), correlation IDs, centralized logging

**Challenge**: Service version compatibility
**Solution**: Contract testing, semantic versioning, consumer-driven contracts

**Challenge**: Test environment orchestration
**Solution**: Docker Compose, Kubernetes test namespaces, service virtualization

### Tools

**Contract Testing**: Pact, Spring Cloud Contract, Dredd
**Service Virtualization**: WireMock, Mountebank, Hoverfly
**Chaos Engineering**: Chaos Toolkit, Gremlin, Litmus
**Integration Testing**: Testcontainers, Docker Compose, LocalStack
**Observability**: Jaeger, Zipkin, OpenTelemetry, Prometheus

---

## 4. API Testing

### Core Principles

**Definition**: Verification of application programming interfaces for functionality, reliability, performance, and security, focusing on request/response validation, data integrity, and contract compliance.

**API Testing Levels**:
1. **Unit**: Individual endpoint logic
2. **Integration**: Service interactions
3. **Contract**: Provider-consumer agreements
4. **End-to-End**: Complete workflows
5. **Security**: Authentication, authorization, injection
6. **Performance**: Load, stress, scalability

### REST API Testing

**Test Categories**:

**Functional Testing**:
```javascript
// Postman/Newman test for Job Seeker API
pm.test("GET /api/jobs returns 200 OK", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains job listings array", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('jobs');
    pm.expect(jsonData.jobs).to.be.an('array');
    pm.expect(jsonData.jobs.length).to.be.greaterThan(0);
});

pm.test("Job object has required fields", function () {
    const jsonData = pm.response.json();
    const job = jsonData.jobs[0];
    pm.expect(job).to.have.property('id');
    pm.expect(job).to.have.property('title');
    pm.expect(job).to.have.property('company');
    pm.expect(job).to.have.property('location');
    pm.expect(job).to.have.property('postedDate');
});

pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

**Schema Validation**:
```javascript
// JSON Schema validation
const schema = {
  type: "object",
  required: ["jobs", "pagination"],
  properties: {
    jobs: {
      type: "array",
      items: {
        type: "object",
        required: ["id", "title", "company"],
        properties: {
          id: { type: "string", pattern: "^job-[0-9]+$" },
          title: { type: "string", minLength: 1 },
          company: { type: "string", minLength: 1 },
          location: { type: "string" },
          salary: {
            type: "object",
            properties: {
              min: { type: "number", minimum: 0 },
              max: { type: "number", minimum: 0 }
            }
          },
          postedDate: { type: "string", format: "date-time" }
        }
      }
    },
    pagination: {
      type: "object",
      required: ["page", "pageSize", "total"],
      properties: {
        page: { type: "number", minimum: 1 },
        pageSize: { type: "number", minimum: 1 },
        total: { type: "number", minimum: 0 }
      }
    }
  }
};

pm.test("Schema is valid", function() {
    pm.response.to.have.jsonSchema(schema);
});
```

**Playwright API Testing**:
```typescript
// Job Seeker API tests with Playwright
import { test, expect } from '@playwright/test';

const API_URL = 'https://api.jobseeker.com';

test.describe('Job Listing API', () => {
  let apiContext;

  test.beforeAll(async ({ playwright }) => {
    apiContext = await playwright.request.newContext({
      baseURL: API_URL,
      extraHTTPHeaders: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${process.env.API_TOKEN}`
      }
    });
  });

  test.afterAll(async () => {
    await apiContext.dispose();
  });

  test('GET /jobs - search for QA Engineer positions', async () => {
    const response = await apiContext.get('/api/jobs', {
      params: {
        q: 'QA Engineer',
        location: 'San Francisco',
        page: 1,
        pageSize: 20
      }
    });

    expect(response.ok()).toBeTruthy();
    expect(response.status()).toBe(200);

    const data = await response.json();
    expect(data.jobs).toBeInstanceOf(Array);
    expect(data.jobs.length).toBeGreaterThan(0);
    expect(data.jobs.length).toBeLessThanOrEqual(20);

    // Verify relevance
    data.jobs.forEach(job => {
      expect(job.title.toLowerCase()).toContain('qa');
    });
  });

  test('POST /applications - submit job application', async () => {
    const application = {
      jobId: 'job-12345',
      resumeId: 'resume-67890',
      coverLetter: 'I am interested in this position...'
    };

    const response = await apiContext.post('/api/applications', {
      data: application
    });

    expect(response.status()).toBe(201);

    const data = await response.json();
    expect(data).toHaveProperty('id');
    expect(data).toHaveProperty('status', 'submitted');
    expect(data).toHaveProperty('createdAt');

    // Verify location header
    const location = response.headers()['location'];
    expect(location).toContain(`/api/applications/${data.id}`);
  });

  test('PUT /profile - update user profile', async () => {
    const updates = {
      headline: 'Senior QA Engineer',
      skills: ['JavaScript', 'Playwright', 'API Testing'],
      experience: 8
    };

    const response = await apiContext.put('/api/profile', {
      data: updates
    });

    expect(response.ok()).toBeTruthy();

    const profile = await response.json();
    expect(profile.headline).toBe(updates.headline);
    expect(profile.skills).toEqual(updates.skills);
  });

  test('DELETE /applications/:id - withdraw application', async () => {
    // First create application
    const createResponse = await apiContext.post('/api/applications', {
      data: { jobId: 'job-123', resumeId: 'resume-456' }
    });
    const { id } = await createResponse.json();

    // Then delete it
    const deleteResponse = await apiContext.delete(`/api/applications/${id}`);
    expect(deleteResponse.status()).toBe(204);

    // Verify it's gone
    const getResponse = await apiContext.get(`/api/applications/${id}`);
    expect(getResponse.status()).toBe(404);
  });

  test('Error handling - invalid job ID', async () => {
    const response = await apiContext.get('/api/jobs/invalid-id');

    expect(response.status()).toBe(404);

    const error = await response.json();
    expect(error).toHaveProperty('error');
    expect(error.error).toHaveProperty('message');
    expect(error.error).toHaveProperty('code', 'JOB_NOT_FOUND');
  });

  test('Pagination - navigate through results', async () => {
    const page1 = await apiContext.get('/api/jobs?page=1&pageSize=10');
    const data1 = await page1.json();

    const page2 = await apiContext.get('/api/jobs?page=2&pageSize=10');
    const data2 = await page2.json();

    // Verify different results
    expect(data1.jobs[0].id).not.toBe(data2.jobs[0].id);

    // Verify pagination metadata
    expect(data1.pagination.page).toBe(1);
    expect(data2.pagination.page).toBe(2);
    expect(data1.pagination.total).toBe(data2.pagination.total);
  });
});
```

### GraphQL Testing

**Query Testing**:
```typescript
// GraphQL query tests for Job Seeker
import { test, expect } from '@playwright/test';

const GRAPHQL_URL = 'https://api.jobseeker.com/graphql';

test.describe('Job Seeker GraphQL API', () => {
  test('Query jobs with filters', async ({ request }) => {
    const query = `
      query SearchJobs($query: String!, $location: String, $salaryMin: Int) {
        jobs(query: $query, location: $location, salaryMin: $salaryMin) {
          edges {
            node {
              id
              title
              company {
                name
                logo
              }
              location
              salary {
                min
                max
                currency
              }
              postedDate
              tags
            }
          }
          pageInfo {
            hasNextPage
            endCursor
          }
          totalCount
        }
      }
    `;

    const response = await request.post(GRAPHQL_URL, {
      data: {
        query,
        variables: {
          query: 'QA Engineer',
          location: 'San Francisco',
          salaryMin: 120000
        }
      }
    });

    expect(response.ok()).toBeTruthy();

    const { data, errors } = await response.json();
    expect(errors).toBeUndefined();
    expect(data.jobs.edges.length).toBeGreaterThan(0);

    // Verify salary filter applied
    data.jobs.edges.forEach(({ node }) => {
      expect(node.salary.min).toBeGreaterThanOrEqual(120000);
    });
  });

  test('Mutation - submit application', async ({ request }) => {
    const mutation = `
      mutation SubmitApplication($input: ApplicationInput!) {
        submitApplication(input: $input) {
          application {
            id
            status
            job {
              title
              company {
                name
              }
            }
            submittedAt
          }
          errors {
            field
            message
          }
        }
      }
    `;

    const response = await request.post(GRAPHQL_URL, {
      data: {
        query: mutation,
        variables: {
          input: {
            jobId: 'job-123',
            resumeId: 'resume-456',
            coverLetter: 'I am very interested...'
          }
        }
      },
      headers: {
        'Authorization': `Bearer ${process.env.USER_TOKEN}`
      }
    });

    const { data } = await response.json();
    expect(data.submitApplication.errors).toHaveLength(0);
    expect(data.submitApplication.application.status).toBe('SUBMITTED');
  });

  test('Error handling - invalid query', async ({ request }) => {
    const query = `
      query {
        jobs(invalidField: "test") {
          id
        }
      }
    `;

    const response = await request.post(GRAPHQL_URL, {
      data: { query }
    });

    const { errors } = await response.json();
    expect(errors).toBeDefined();
    expect(errors[0].message).toContain('Unknown argument');
  });

  test('Fragment usage and nested queries', async ({ request }) => {
    const query = `
      fragment JobDetails on Job {
        id
        title
        description
        requirements
        benefits
      }

      fragment CompanyInfo on Company {
        name
        logo
        description
        employeeCount
        industry
      }

      query GetJobWithDetails($id: ID!) {
        job(id: $id) {
          ...JobDetails
          company {
            ...CompanyInfo
          }
          similarJobs {
            ...JobDetails
          }
        }
      }
    `;

    const response = await request.post(GRAPHQL_URL, {
      data: {
        query,
        variables: { id: 'job-123' }
      }
    });

    const { data } = await response.json();
    expect(data.job).toHaveProperty('title');
    expect(data.job.company).toHaveProperty('name');
    expect(data.job.similarJobs).toBeInstanceOf(Array);
  });
});
```

### API Contract Testing

**OpenAPI/Swagger Validation**:
```javascript
// Validate API responses against OpenAPI spec
const SwaggerParser = require('@apidevtools/swagger-parser');
const Ajv = require('ajv');

describe('Job Seeker API Contract Compliance', () => {
  let apiSpec;
  let ajv;

  beforeAll(async () => {
    apiSpec = await SwaggerParser.validate('./openapi.yaml');
    ajv = new Ajv();
  });

  it('GET /jobs response matches schema', async () => {
    const response = await fetch('https://api.jobseeker.com/api/jobs');
    const data = await response.json();

    const schema = apiSpec.paths['/api/jobs'].get.responses['200'].content['application/json'].schema;
    const validate = ajv.compile(schema);
    const valid = validate(data);

    expect(valid).toBe(true);
    if (!valid) {
      console.log(validate.errors);
    }
  });

  it('POST /applications request validation', async () => {
    const requestSchema = apiSpec.paths['/api/applications'].post.requestBody.content['application/json'].schema;

    const invalidRequest = {
      jobId: 'job-123'
      // Missing required resumeId
    };

    const validate = ajv.compile(requestSchema);
    const valid = validate(invalidRequest);

    expect(valid).toBe(false);
    expect(validate.errors[0].message).toContain('required');
  });
});
```

### API Security Testing

**Authentication Testing**:
```typescript
test.describe('API Authentication', () => {
  test('Rejects unauthenticated requests', async ({ request }) => {
    const response = await request.get('/api/profile');
    expect(response.status()).toBe(401);
  });

  test('Accepts valid JWT token', async ({ request }) => {
    const response = await request.get('/api/profile', {
      headers: {
        'Authorization': `Bearer ${validToken}`
      }
    });
    expect(response.ok()).toBeTruthy();
  });

  test('Rejects expired token', async ({ request }) => {
    const response = await request.get('/api/profile', {
      headers: {
        'Authorization': `Bearer ${expiredToken}`
      }
    });
    expect(response.status()).toBe(401);
    const error = await response.json();
    expect(error.code).toBe('TOKEN_EXPIRED');
  });

  test('Rate limiting enforced', async ({ request }) => {
    const requests = Array(101).fill(null).map(() =>
      request.get('/api/jobs')
    );

    const responses = await Promise.all(requests);
    const rateLimited = responses.filter(r => r.status() === 429);

    expect(rateLimited.length).toBeGreaterThan(0);
  });
});
```

**Injection Testing**:
```typescript
test.describe('API Security - Injection Prevention', () => {
  test('SQL injection protection', async ({ request }) => {
    const maliciousQuery = "'; DROP TABLE jobs; --";

    const response = await request.get('/api/jobs', {
      params: { q: maliciousQuery }
    });

    // Should return safely, not error
    expect(response.ok()).toBeTruthy();

    // Verify database not affected
    const verifyResponse = await request.get('/api/jobs');
    expect(verifyResponse.ok()).toBeTruthy();
  });

  test('XSS prevention in API responses', async ({ request }) => {
    const xssPayload = '<script>alert("XSS")</script>';

    const response = await request.post('/api/profile', {
      data: { headline: xssPayload }
    });

    const data = await response.json();
    // Should be sanitized or encoded
    expect(data.headline).not.toContain('<script>');
  });
});
```

### API Testing Challenges

**Challenge**: Managing authentication tokens across tests
**Solution**: Token refresh mechanisms, fixture management, environment variables

**Challenge**: Testing rate limiting and throttling
**Solution**: Time-controlled test execution, mock time, test environment exemptions

**Challenge**: Validating complex nested responses
**Solution**: JSON Schema validation, snapshot testing, custom matchers

**Challenge**: API versioning compatibility
**Solution**: Version-specific test suites, contract testing, deprecation warnings

### Tools

**REST API Testing**: Postman, Insomnia, Playwright, REST Assured, Supertest
**GraphQL Testing**: Apollo Client, GraphQL Playground, Insomnia
**Contract Testing**: Pact, Dredd, Prism, Spectral
**Security Testing**: OWASP ZAP, Burp Suite, Postman
**Documentation**: Swagger/OpenAPI, GraphQL Schema, API Blueprint

---

## 5. Mobile Testing

### Core Principles

**Definition**: Testing mobile applications across devices, operating systems, screen sizes, and network conditions, addressing touch interactions, platform-specific behaviors, and mobile-specific constraints.

**Mobile Testing Dimensions**:
1. **Functional**: Feature correctness
2. **Usability**: Touch interactions, gestures
3. **Compatibility**: OS versions, devices
4. **Performance**: Battery, memory, network
5. **Security**: Data storage, permissions
6. **Accessibility**: Screen readers, voice control

### Responsive Design Testing

**Viewport Testing**:
```typescript
// Playwright mobile viewport tests
import { test, devices } from '@playwright/test';

const mobileDevices = [
  devices['iPhone 13 Pro'],
  devices['Pixel 5'],
  devices['iPad Pro'],
  devices['Galaxy S21']
];

for (const device of mobileDevices) {
  test.describe(`Job Seeker on ${device.name}`, () => {
    test.use(device);

    test('Job search displays correctly', async ({ page }) => {
      await page.goto('https://jobseeker.com');

      // Verify mobile layout
      const searchBar = page.locator('[data-testid="job-search"]');
      await expect(searchBar).toBeVisible();

      // Check responsive breakpoints
      const viewport = page.viewportSize();
      if (viewport.width < 768) {
        // Mobile view - hamburger menu
        await expect(page.locator('[data-testid="menu-toggle"]')).toBeVisible();
      } else {
        // Tablet/Desktop - full nav
        await expect(page.locator('[data-testid="nav-menu"]')).toBeVisible();
      }
    });

    test('Job cards stack vertically on mobile', async ({ page }) => {
      await page.goto('https://jobseeker.com/jobs');

      const jobCards = page.locator('[data-testid="job-card"]');
      const count = await jobCards.count();

      // Get positions of first two cards
      const box1 = await jobCards.nth(0).boundingBox();
      const box2 = await jobCards.nth(1).boundingBox();

      // Verify vertical stacking (second card below first)
      expect(box2.y).toBeGreaterThan(box1.y + box1.height);
    });
  });
}

test.describe('Orientation changes', () => {
  test('Handles portrait to landscape rotation', async ({ page, context }) => {
    await context.setViewportSize({ width: 375, height: 667 }); // Portrait
    await page.goto('https://jobseeker.com/jobs/job-123');

    // Verify portrait layout
    const sidebar = page.locator('[data-testid="job-sidebar"]');
    await expect(sidebar).toBeVisible();

    // Rotate to landscape
    await context.setViewportSize({ width: 667, height: 375 });

    // Verify landscape layout adaptation
    await expect(sidebar).toBeVisible();
    const sidebarBox = await sidebar.boundingBox();
    expect(sidebarBox.width).toBeLessThan(400); // Narrower in landscape
  });
});
```

**CSS Media Query Verification**:
```typescript
test('Responsive breakpoints applied correctly', async ({ page }) => {
  const breakpoints = [
    { width: 320, name: 'mobile-sm' },
    { width: 375, name: 'mobile' },
    { width: 768, name: 'tablet' },
    { width: 1024, name: 'desktop' },
    { width: 1440, name: 'desktop-lg' }
  ];

  for (const { width, name } of breakpoints) {
    await page.setViewportSize({ width, height: 800 });
    await page.goto('https://jobseeker.com');

    // Check applied styles match breakpoint
    const container = page.locator('.container');
    const computedPadding = await container.evaluate(el =>
      window.getComputedStyle(el).padding
    );

    // Verify expected padding for breakpoint
    const expectedPadding = {
      'mobile-sm': '16px',
      'mobile': '16px',
      'tablet': '24px',
      'desktop': '32px',
      'desktop-lg': '48px'
    };

    expect(computedPadding).toContain(expectedPadding[name]);
  }
});
```

### Touch Interactions and Gestures

**Touch Event Testing**:
```typescript
test.describe('Mobile Touch Interactions', () => {
  test.use(devices['iPhone 13']);

  test('Swipe to delete application', async ({ page }) => {
    await page.goto('https://jobseeker.com/applications');

    const application = page.locator('[data-testid="application-1"]');
    const box = await application.boundingBox();

    // Swipe left gesture
    await page.touchscreen.tap(box.x + box.width - 10, box.y + box.height / 2);
    await page.touchscreen.swipe(
      { x: box.x + box.width - 10, y: box.y + box.height / 2 },
      { x: box.x + 10, y: box.y + box.height / 2 }
    );

    // Verify delete button appears
    await expect(page.locator('[data-testid="delete-button"]')).toBeVisible();
  });

  test('Pull-to-refresh updates job list', async ({ page }) => {
    await page.goto('https://jobseeker.com/jobs');

    const jobList = page.locator('[data-testid="job-list"]');
    const initialCount = await jobList.locator('.job-card').count();

    // Pull-to-refresh gesture
    await page.touchscreen.swipe(
      { x: 200, y: 100 },
      { x: 200, y: 400 }
    );

    // Wait for refresh
    await page.waitForTimeout(1000);

    // Verify list updated (spinner shown, then new content)
    await expect(page.locator('[data-testid="loading-spinner"]')).toBeVisible();
    await expect(page.locator('[data-testid="loading-spinner"]')).toBeHidden();
  });

  test('Pinch-to-zoom on job description image', async ({ page }) => {
    await page.goto('https://jobseeker.com/jobs/job-123');

    const image = page.locator('[data-testid="company-image"]');
    const initialBox = await image.boundingBox();

    // Pinch-to-zoom gesture
    await page.touchscreen.tap(initialBox.x + initialBox.width / 2, initialBox.y + initialBox.height / 2);
    // Note: Playwright doesn't fully support pinch gestures, would need native testing
  });

  test('Long press to save job', async ({ page }) => {
    await page.goto('https://jobseeker.com/jobs');

    const jobCard = page.locator('[data-testid="job-card"]').first();
    const box = await jobCard.boundingBox();

    // Long press
    await page.touchscreen.tap(box.x + box.width / 2, box.y + box.height / 2);
    await page.waitForTimeout(800); // Hold for 800ms

    // Verify context menu appears
    await expect(page.locator('[data-testid="job-context-menu"]')).toBeVisible();
    await expect(page.locator('text=Save Job')).toBeVisible();
  });
});
```

**Gesture Recognition**:
```typescript
test('Horizontal swipe navigation between applications', async ({ page }) => {
  test.use(devices['Pixel 5']);

  await page.goto('https://jobseeker.com/applications/app-1');

  const content = page.locator('[data-testid="application-content"]');

  // Swipe right to next application
  await content.swipe({ x: -200, y: 0 });
  await expect(page).toHaveURL(/app-2/);

  // Swipe left to previous
  await content.swipe({ x: 200, y: 0 });
  await expect(page).toHaveURL(/app-1/);
});
```

### Device Fragmentation Testing

**Platform-Specific Behaviors**:
```typescript
test.describe('Platform-specific features', () => {
  test('iOS - Date picker native behavior', async ({ page }) => {
    test.use(devices['iPhone 13']);

    await page.goto('https://jobseeker.com/profile/availability');

    const datePicker = page.locator('input[type="date"]');
    await datePicker.click();

    // iOS shows native date picker wheel
    // Verify it's the native control, not custom
    const isNative = await datePicker.evaluate(input =>
      input.type === 'date' && !input.dataset.customPicker
    );
    expect(isNative).toBe(true);
  });

  test('Android - Share sheet integration', async ({ page }) => {
    test.use(devices['Pixel 5']);

    await page.goto('https://jobseeker.com/jobs/job-123');

    const shareButton = page.locator('[data-testid="share-job"]');
    await shareButton.click();

    // Android should trigger native share sheet
    // In real device testing, this would open system share dialog
    await expect(page.locator('[data-testid="share-options"]')).toBeVisible();
  });
});
```

**OS Version Compatibility**:
```typescript
// BrowserStack integration for multi-OS testing
const browserstackCapabilities = [
  { device: 'iPhone 13', os: 'iOS', osVersion: '15' },
  { device: 'iPhone 13', os: 'iOS', osVersion: '16' },
  { device: 'Samsung Galaxy S21', os: 'Android', osVersion: '11' },
  { device: 'Samsung Galaxy S21', os: 'Android', osVersion: '12' },
  { device: 'iPad Pro 12.9', os: 'iOS', osVersion: '16' }
];

for (const cap of browserstackCapabilities) {
  test(`${cap.device} on ${cap.os} ${cap.osVersion}`, async ({ page }) => {
    // BrowserStack provides real devices
    await page.goto('https://jobseeker.com');

    // Test core functionality
    await page.fill('[data-testid="job-search"]', 'QA Engineer');
    await page.click('[data-testid="search-button"]');

    await expect(page.locator('[data-testid="job-results"]')).toBeVisible();
  });
}
```

### Performance Testing on Mobile

**Network Conditions**:
```typescript
test.describe('Mobile Network Performance', () => {
  test('App loads on 3G network', async ({ page, context }) => {
    // Simulate 3G network
    await context.route('**/*', route => {
      setTimeout(() => route.continue(), 100); // Add latency
    });

    await page.goto('https://jobseeker.com', {
      waitUntil: 'networkidle'
    });

    // Verify core content visible
    await expect(page.locator('[data-testid="job-search"]')).toBeVisible({ timeout: 5000 });
  });

  test('Offline mode with service worker', async ({ page, context }) => {
    await page.goto('https://jobseeker.com/jobs');

    // Wait for service worker registration
    await page.waitForFunction(() => navigator.serviceWorker.ready);

    // Go offline
    await context.setOffline(true);

    // Navigate to cached page
    await page.goto('https://jobseeker.com/jobs/job-123');

    // Verify cached content loads
    await expect(page.locator('[data-testid="job-title"]')).toBeVisible();
    await expect(page.locator('[data-testid="offline-indicator"]')).toBeVisible();
  });

  test('Image lazy loading on slow connection', async ({ page }) => {
    // Throttle network to slow 3G
    const client = await page.context().newCDPSession(page);
    await client.send('Network.emulateNetworkConditions', {
      offline: false,
      downloadThroughput: 500 * 1024 / 8, // 500kb/s
      uploadThroughput: 500 * 1024 / 8,
      latency: 400
    });

    await page.goto('https://jobseeker.com/jobs');

    // Verify images use lazy loading
    const images = page.locator('img[loading="lazy"]');
    expect(await images.count()).toBeGreaterThan(0);

    // Verify placeholder shown while loading
    await expect(page.locator('.image-placeholder').first()).toBeVisible();
  });
});
```

**Battery and Memory**:
```typescript
// Use Chrome DevTools Protocol for performance metrics
test('Monitor memory usage during scroll', async ({ page }) => {
  const client = await page.context().newCDPSession(page);
  await client.send('Performance.enable');

  await page.goto('https://jobseeker.com/jobs');

  // Get initial memory
  const initialMetrics = await client.send('Performance.getMetrics');
  const initialMemory = initialMetrics.metrics.find(m => m.name === 'JSHeapUsedSize').value;

  // Scroll through long list
  for (let i = 0; i < 10; i++) {
    await page.mouse.wheel(0, 1000);
    await page.waitForTimeout(500);
  }

  // Get final memory
  const finalMetrics = await client.send('Performance.getMetrics');
  const finalMemory = finalMetrics.metrics.find(m => m.name === 'JSHeapUsedSize').value;

  // Verify no significant memory leak
  const memoryIncrease = finalMemory - initialMemory;
  expect(memoryIncrease).toBeLessThan(10 * 1024 * 1024); // Less than 10MB increase
});
```

### Mobile-Specific Challenges

**Challenge**: Touch target size and spacing
**Solution**: Minimum 44x44px touch targets, adequate spacing, accessibility guidelines

**Challenge**: Viewport inconsistencies across devices
**Solution**: Meta viewport tags, flexible layouts, extensive device testing

**Challenge**: Platform-specific behaviors (date pickers, keyboards)
**Solution**: Progressive enhancement, native component detection, platform-specific tests

**Challenge**: Network variability and offline scenarios
**Solution**: Service workers, caching strategies, network condition testing

**Challenge**: Performance on lower-end devices
**Solution**: Performance budgets, code splitting, lazy loading, device-specific profiling

### Mobile Testing Tools

**Real Device Testing**:
- **BrowserStack**: Cloud-based real device testing
- **Sauce Labs**: Automated and manual mobile testing
- **Firebase Test Lab**: Android device testing on Google infrastructure
- **AWS Device Farm**: Real devices for iOS and Android

**Emulators/Simulators**:
- **Android Studio Emulator**: Android virtual devices
- **Xcode Simulator**: iOS simulation
- **Playwright**: Built-in device emulation
- **Chrome DevTools**: Mobile device emulation

**Framework-Specific**:
- **Appium**: Native, hybrid, and mobile web automation
- **Detox**: Gray-box testing for React Native
- **XCUITest**: iOS native testing
- **Espresso**: Android native testing

**Performance**:
- **Chrome Lighthouse**: Mobile performance audits
- **WebPageTest**: Mobile network testing
- **Firebase Performance Monitoring**: Real user monitoring

---

## Summary

Testing in different contexts requires adapting strategies, tools, and practices to specific organizational, architectural, and technological environments:

**Agile Testing** emphasizes collaboration, continuous feedback, and balancing manual/automated testing across Lisa Crispin's four quadrants. Job Seeker examples include unit tests for profile validation, BDD scenarios for application submission, performance testing of search APIs, and exploratory testing charters.

**DevOps Testing** integrates testing throughout CI/CD pipelines, from unit tests through production monitoring. Infrastructure as Code, container testing, and observability practices ensure quality at every deployment stage. Challenges include flaky tests, test data management, and environment parity.

**Microservices Testing** addresses distributed system complexity through contract testing (Pact), service virtualization (WireMock), and chaos engineering. Integration strategies balance isolated component tests with end-to-end flow validation across services.

**API Testing** verifies REST, GraphQL, and other API contracts through functional, schema, security, and performance testing. Playwright provides comprehensive API testing capabilities, while Postman and contract testing tools ensure provider-consumer compatibility.

**Mobile Testing** tackles responsive design, touch interactions, device fragmentation, and mobile-specific constraints. Tools like BrowserStack provide real device testing, while Playwright offers robust mobile emulation for viewport, gesture, and performance testing.

Success across contexts requires understanding domain-specific challenges, selecting appropriate tools, and maintaining quality while delivering at speed.

---

## References

1. Crispin, L., & Gregory, J. (2009). *Agile Testing: A Practical Guide for Testers and Agile Teams*. Addison-Wesley.
2. Crispin, L., & Gregory, J. (2014). *More Agile Testing: Learning Journeys for the Whole Team*. Addison-Wesley.
3. Kim, G., Humble, J., Debois, P., & Willis, J. (2016). *The DevOps Handbook*. IT Revolution Press.
4. Newman, S. (2021). *Building Microservices* (2nd ed.). O'Reilly Media.
5. Richardson, C. (2018). *Microservices Patterns*. Manning Publications.
6. Pact Foundation. (2024). *Pact Documentation*. https://docs.pact.io/
7. Playwright Documentation. (2024). *API Testing*. https://playwright.dev/docs/api-testing
8. Postman Learning Center. (2024). *API Testing and Development*. https://learning.postman.com/
9. Netflix Technology Blog. (2011). *The Netflix Simian Army*. https://netflixtechblog.com/
10. Basiri, A., et al. (2016). *Chaos Engineering*. IEEE Software, 33(3), 35-41.
11. Principles of Chaos Engineering. (2024). https://principlesofchaos.org/
12. OpenAPI Initiative. (2024). *OpenAPI Specification*. https://spec.openapis.org/
13. GraphQL Foundation. (2024). *GraphQL Specification*. https://spec.graphql.org/
14. Google Web Fundamentals. (2024). *Mobile Web Development*. https://developers.google.com/web
15. W3C Mobile Web Best Practices. (2024). https://www.w3.org/TR/mobile-bp/
