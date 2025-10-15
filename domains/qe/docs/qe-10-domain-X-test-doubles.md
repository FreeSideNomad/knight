# Domain X: Test Doubles

## Overview

Test doubles are simulated objects or components that replace real dependencies during testing, enabling isolated, fast, and reliable unit and integration tests. Coined by Gerard Meszaros in "xUnit Test Patterns: Refactoring Test Code," the term encompasses five distinct patterns: Dummy, Stub, Fake, Mock, and Spy. Each serves specific testing needs and offers different trade-offs between simplicity, realism, and verification capabilities.

This document explores each test double pattern in depth, providing practical implementation examples using Jest (JavaScript) and Mockito (Java), along with Job Seeker application-specific scenarios. Understanding when and how to use each pattern is critical for creating maintainable, effective test suites.

---

## 1. Dummy Objects

### 1.1 Definition

**Dummy objects** are placeholder objects passed to satisfy method signatures or constructor parameters but never actually used in the test scenario. They exist solely to fulfill compilation or runtime requirements when the actual behavior of the dependency is irrelevant to the test.

**Gerard Meszaros Definition**: "Objects are passed around but never actually used. Usually they are just used to fill parameter lists."

**Key Characteristics**:
- Never invoked or accessed during test execution
- Simplest form of test double
- No behavior implementation required
- Used purely for syntactic completeness
- Should cause test failure if accidentally used

### 1.2 When to Use

**Ideal Scenarios**:
- Required constructor parameters that aren't relevant to the specific test
- Method parameters mandated by interface but not used in test path
- Dependency injection contexts where dependency isn't exercised
- Satisfying type requirements in strongly-typed languages
- Reducing noise in test setup when behavior doesn't matter

**Anti-patterns**:
- Don't use when the parameter is actually accessed (use Stub instead)
- Avoid if it masks poor design (too many constructor parameters)
- Don't use as substitute for proper null object pattern in production code

### 1.3 Implementation Examples

#### Jest (JavaScript/TypeScript)

```javascript
// Job Seeker - User Profile Service
class ProfileService {
  constructor(
    private emailService: EmailService,
    private auditLogger: AuditLogger,
    private metricsCollector: MetricsCollector
  ) {}

  updateName(userId: string, newName: string): void {
    // Only uses emailService, not auditLogger or metricsCollector
    const profile = this.getProfile(userId);
    profile.name = newName;
    this.emailService.sendNotification(userId, 'Profile updated');
  }
}

// Test using Dummy objects
describe('ProfileService.updateName', () => {
  test('sends notification after name update', () => {
    // Dummies - never used in this test path
    const dummyAuditLogger = {} as AuditLogger;
    const dummyMetricsCollector = {} as MetricsCollector;

    // Real test double - Stub with verification
    const emailServiceStub = {
      sendNotification: jest.fn()
    };

    const service = new ProfileService(
      emailServiceStub,
      dummyAuditLogger,      // DUMMY
      dummyMetricsCollector  // DUMMY
    );

    service.updateName('user-123', 'John Doe');

    expect(emailServiceStub.sendNotification)
      .toHaveBeenCalledWith('user-123', 'Profile updated');
  });
});
```

#### Mockito (Java)

```java
// Job Seeker - Job Application Service
public class JobApplicationService {
    private final EmailService emailService;
    private final NotificationQueue notificationQueue;
    private final AnalyticsTracker analyticsTracker;

    public JobApplicationService(
        EmailService emailService,
        NotificationQueue notificationQueue,
        AnalyticsTracker analyticsTracker
    ) {
        this.emailService = emailService;
        this.notificationQueue = notificationQueue;
        this.analyticsTracker = analyticsTracker;
    }

    public void submitApplication(Application application) {
        // Only uses emailService in this code path
        validateApplication(application);
        emailService.sendConfirmation(application.getUserId());
    }
}

// Test using Dummy objects
@Test
public void testSubmitApplication_SendsConfirmationEmail() {
    // Dummies - never invoked
    NotificationQueue dummyQueue = mock(NotificationQueue.class);
    AnalyticsTracker dummyTracker = mock(NotificationQueue.class);

    // Real test double - Mock with verification
    EmailService emailMock = mock(EmailService.class);

    JobApplicationService service = new JobApplicationService(
        emailMock,
        dummyQueue,      // DUMMY
        dummyTracker     // DUMMY
    );

    Application app = new Application("user-123", "job-456");
    service.submitApplication(app);

    verify(emailMock).sendConfirmation("user-123");

    // Verify dummies were never touched
    verifyNoInteractions(dummyQueue);
    verifyNoInteractions(dummyTracker);
}
```

### 1.4 Job Seeker Application Examples

#### Example 1: Job Matching Service Dependencies

```typescript
// Scenario: Testing job search ranking without recommendation engine
interface RecommendationEngine {
  calculateAffinityScore(userId: string, jobId: string): number;
}

interface CacheService {
  get(key: string): any;
  set(key: string, value: any): void;
}

class JobSearchService {
  constructor(
    private jobRepository: JobRepository,
    private recommendationEngine: RecommendationEngine,
    private cache: CacheService
  ) {}

  // Simple search doesn't use recommendation engine or cache
  searchByKeyword(keyword: string): Job[] {
    return this.jobRepository.findByKeyword(keyword);
  }
}

// Test with Dummies
test('searchByKeyword returns jobs from repository', () => {
  const dummyRecommendationEngine = {} as RecommendationEngine;
  const dummyCache = {} as CacheService;

  const mockRepository = {
    findByKeyword: jest.fn().mockReturnValue([
      { id: 'job-1', title: 'Software Engineer' },
      { id: 'job-2', title: 'Senior Engineer' }
    ])
  };

  const service = new JobSearchService(
    mockRepository,
    dummyRecommendationEngine,  // DUMMY
    dummyCache                  // DUMMY
  );

  const results = service.searchByKeyword('Engineer');

  expect(results).toHaveLength(2);
  expect(mockRepository.findByKeyword).toHaveBeenCalledWith('Engineer');
});
```

#### Example 2: Email Service with Multiple Formatters

```java
// Scenario: Testing email sending without using all formatters
public class EmailService {
    private final SmtpClient smtpClient;
    private final HtmlFormatter htmlFormatter;
    private final PdfFormatter pdfFormatter;
    private final TemplateEngine templateEngine;

    public void sendPlainTextNotification(String recipient, String message) {
        // Only uses smtpClient - other formatters are dummies
        smtpClient.send(recipient, message);
    }
}

@Test
public void testSendPlainTextNotification() {
    SmtpClient clientMock = mock(SmtpClient.class);

    // Dummies - never used for plain text emails
    HtmlFormatter dummyHtmlFormatter = mock(HtmlFormatter.class);
    PdfFormatter dummyPdfFormatter = mock(PdfFormatter.class);
    TemplateEngine dummyTemplateEngine = mock(TemplateEngine.class);

    EmailService service = new EmailService(
        clientMock,
        dummyHtmlFormatter,     // DUMMY
        dummyPdfFormatter,      // DUMMY
        dummyTemplateEngine     // DUMMY
    );

    service.sendPlainTextNotification("user@example.com", "Hello");

    verify(clientMock).send("user@example.com", "Hello");
    verifyNoInteractions(dummyHtmlFormatter, dummyPdfFormatter, dummyTemplateEngine);
}
```

### 1.5 Best Practices

1. **Make Dummies Obvious**: Name variables clearly (e.g., `dummyLogger`, `unusedCache`)
2. **Use Type-Safe Nulls**: Prefer empty objects over `null` to avoid NullPointerExceptions
3. **Verify Non-Use**: Add assertions to confirm dummies weren't called
4. **Consider Null Object Pattern**: For production code, use proper null objects
5. **Review Constructor Bloat**: Too many dummies may indicate design issues (SRP violation)

---

## 2. Stubs

### 2.1 Definition

**Stubs** provide canned (predetermined) responses to calls made during testing, effectively controlling the indirect inputs to the system under test. Unlike dummies, stubs are actually invoked, but they return hard-coded values rather than executing real logic.

**Gerard Meszaros Definition**: "Stubs provide canned answers to calls made during the test, usually not responding at all to anything outside what's programmed in for the test."

**Martin Fowler's Perspective**: "Stubs provide answers to queries but never record or verify how they were called."

**Key Characteristics**:
- Replaces real dependency with simplified implementation
- Returns predetermined values (hard-coded responses)
- No verification of interactions (state-based testing)
- Minimal logic - just enough to support test scenario
- Focuses on providing correct outputs, not tracking invocations

### 2.2 When to Use

**Ideal Scenarios**:
- Simulating responses from external systems (APIs, databases)
- Providing test data without complex setup
- Controlling random or time-dependent behaviors
- Avoiding slow operations (network calls, disk I/O)
- Creating specific data conditions for edge cases
- Testing error handling with predetermined failures

**State-Based vs Behavior-Based Testing**:
- **Stubs**: State-based testing - verify final state of SUT
- **Mocks**: Behavior-based testing - verify method invocations

### 2.3 Implementation Examples

#### Jest (JavaScript/TypeScript)

```javascript
// Job Seeker - Job Repository Stub
interface JobRepository {
  findById(jobId: string): Job | null;
  findAll(): Job[];
  save(job: Job): void;
}

class JobApplicationValidator {
  constructor(private jobRepository: JobRepository) {}

  isJobAvailable(jobId: string): boolean {
    const job = this.jobRepository.findById(jobId);
    return job !== null && job.status === 'OPEN';
  }
}

// Test using Stub
describe('JobApplicationValidator.isJobAvailable', () => {
  test('returns true when job exists and is open', () => {
    // Stub with canned response
    const jobRepositoryStub: JobRepository = {
      findById: (jobId: string) => ({
        id: jobId,
        title: 'Software Engineer',
        status: 'OPEN',
        company: 'TechCorp'
      }),
      findAll: () => [], // Not used in this test
      save: () => {}     // Not used in this test
    };

    const validator = new JobApplicationValidator(jobRepositoryStub);
    const result = validator.isJobAvailable('job-123');

    expect(result).toBe(true); // State-based assertion
  });

  test('returns false when job does not exist', () => {
    // Stub returning null
    const jobRepositoryStub: JobRepository = {
      findById: () => null,  // Canned response: job not found
      findAll: () => [],
      save: () => {}
    };

    const validator = new JobApplicationValidator(jobRepositoryStub);
    const result = validator.isJobAvailable('nonexistent-job');

    expect(result).toBe(false); // State-based assertion
  });

  test('returns false when job is closed', () => {
    // Stub with closed job
    const jobRepositoryStub: JobRepository = {
      findById: () => ({
        id: 'job-456',
        title: 'Senior Engineer',
        status: 'CLOSED',  // Closed status
        company: 'StartupXYZ'
      }),
      findAll: () => [],
      save: () => {}
    };

    const validator = new JobApplicationValidator(jobRepositoryStub);
    const result = validator.isJobAvailable('job-456');

    expect(result).toBe(false);
  });
});

// Using Jest's built-in stubbing
describe('JobApplicationValidator with Jest mocks', () => {
  test('handles repository errors gracefully', () => {
    const jobRepositoryStub = {
      findById: jest.fn().mockReturnValue(null),
      findAll: jest.fn(),
      save: jest.fn()
    };

    const validator = new JobApplicationValidator(jobRepositoryStub);
    const result = validator.isJobAvailable('job-789');

    expect(result).toBe(false);
    // Note: Not verifying call count - that would be mock behavior
  });
});
```

#### Mockito (Java)

```java
// Job Seeker - Email Service Stub
public interface EmailService {
    boolean sendEmail(String recipient, String subject, String body);
    EmailStatus checkStatus(String messageId);
}

public class ApplicationNotifier {
    private final EmailService emailService;

    public ApplicationNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    public boolean notifyApplicant(String email, String jobTitle) {
        String subject = "Application Received";
        String body = "Thank you for applying to " + jobTitle;
        return emailService.sendEmail(email, subject, body);
    }
}

// Test using Stub
@Test
public void testNotifyApplicant_WhenEmailSucceeds() {
    // Create stub with canned response
    EmailService emailStub = mock(EmailService.class);
    when(emailStub.sendEmail(anyString(), anyString(), anyString()))
        .thenReturn(true);  // Stub always returns success

    ApplicationNotifier notifier = new ApplicationNotifier(emailStub);

    boolean result = notifier.notifyApplicant(
        "applicant@example.com",
        "Software Engineer"
    );

    assertTrue(result);  // State-based assertion
    // No verification of how many times called - that's mock behavior
}

@Test
public void testNotifyApplicant_WhenEmailFails() {
    // Stub returning failure
    EmailService emailStub = mock(EmailService.class);
    when(emailStub.sendEmail(anyString(), anyString(), anyString()))
        .thenReturn(false);  // Stub always returns failure

    ApplicationNotifier notifier = new ApplicationNotifier(emailStub);

    boolean result = notifier.notifyApplicant(
        "applicant@example.com",
        "Senior Developer"
    );

    assertFalse(result);  // State-based assertion
}

// Stub with conditional responses
@Test
public void testNotifyApplicant_WithConditionalStub() {
    EmailService emailStub = mock(EmailService.class);

    // Stub with specific behavior for different inputs
    when(emailStub.sendEmail(eq("valid@example.com"), anyString(), anyString()))
        .thenReturn(true);
    when(emailStub.sendEmail(eq("invalid@example.com"), anyString(), anyString()))
        .thenReturn(false);

    ApplicationNotifier notifier = new ApplicationNotifier(emailStub);

    assertTrue(notifier.notifyApplicant("valid@example.com", "Engineer"));
    assertFalse(notifier.notifyApplicant("invalid@example.com", "Engineer"));
}
```

### 2.4 Job Seeker Application Examples

#### Example 1: Job Matching Service with Scoring Stub

```typescript
// Scenario: Testing job recommendations with predetermined scores
interface ScoringEngine {
  calculateMatchScore(userProfile: UserProfile, job: Job): number;
}

class JobMatchingService {
  constructor(private scoringEngine: ScoringEngine) {}

  findTopMatches(userProfile: UserProfile, jobs: Job[]): Job[] {
    return jobs
      .map(job => ({
        job,
        score: this.scoringEngine.calculateMatchScore(userProfile, job)
      }))
      .filter(match => match.score >= 70)
      .sort((a, b) => b.score - a.score)
      .map(match => match.job);
  }
}

// Test with Stub
test('findTopMatches returns jobs with score >= 70', () => {
  const userProfile = { skills: ['JavaScript', 'React'] };
  const jobs = [
    { id: '1', title: 'React Developer' },
    { id: '2', title: 'Java Developer' },
    { id: '3', title: 'Senior React Engineer' }
  ];

  // Stub with predetermined scores
  const scoringStub: ScoringEngine = {
    calculateMatchScore: (profile, job) => {
      // Canned responses based on job ID
      const scores = { '1': 85, '2': 45, '3': 92 };
      return scores[job.id] || 0;
    }
  };

  const service = new JobMatchingService(scoringStub);
  const matches = service.findTopMatches(userProfile, jobs);

  // State-based assertions
  expect(matches).toHaveLength(2);
  expect(matches[0].id).toBe('3'); // Highest score (92)
  expect(matches[1].id).toBe('1'); // Second highest (85)
  // Job '2' excluded (score 45 < 70)
});
```

#### Example 2: External API Integration Stub

```java
// Scenario: Testing job import from external API
public interface ExternalJobApiClient {
    List<ExternalJob> fetchJobs(String apiKey, int page);
    ApiQuota checkQuota(String apiKey);
}

public class JobImportService {
    private final ExternalJobApiClient apiClient;

    public JobImportService(ExternalJobApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<Job> importJobs(String apiKey) {
        List<ExternalJob> externalJobs = apiClient.fetchJobs(apiKey, 1);
        return externalJobs.stream()
            .map(this::convertToJob)
            .collect(Collectors.toList());
    }

    private Job convertToJob(ExternalJob external) {
        return new Job(external.getId(), external.getTitle());
    }
}

@Test
public void testImportJobs_ReturnsConvertedJobs() {
    // Stub with canned API response
    ExternalJobApiClient apiStub = mock(ExternalJobApiClient.class);

    List<ExternalJob> stubbedResponse = Arrays.asList(
        new ExternalJob("ext-1", "Backend Engineer"),
        new ExternalJob("ext-2", "Frontend Developer")
    );

    when(apiStub.fetchJobs("test-api-key", 1))
        .thenReturn(stubbedResponse);  // Canned response

    JobImportService service = new JobImportService(apiStub);
    List<Job> importedJobs = service.importJobs("test-api-key");

    // State-based assertions
    assertEquals(2, importedJobs.size());
    assertEquals("ext-1", importedJobs.get(0).getId());
    assertEquals("Backend Engineer", importedJobs.get(0).getTitle());
}

@Test
public void testImportJobs_HandlesEmptyResponse() {
    ExternalJobApiClient apiStub = mock(ExternalJobApiClient.class);
    when(apiStub.fetchJobs(anyString(), anyInt()))
        .thenReturn(Collections.emptyList());  // Empty canned response

    JobImportService service = new JobImportService(apiStub);
    List<Job> importedJobs = service.importJobs("test-api-key");

    assertTrue(importedJobs.isEmpty());
}
```

### 2.5 Best Practices

1. **Keep Stubs Simple**: Only implement logic necessary for test scenario
2. **Use Descriptive Names**: `emailServiceSuccessStub`, `repositoryEmptyStub`
3. **Avoid Over-Stubbing**: Don't stub methods that aren't called
4. **Prefer State Verification**: Check outcomes, not interactions
5. **Reuse Common Stubs**: Create factory methods for frequently used stubs
6. **Document Canned Responses**: Make stub behavior obvious in test

---

## 3. Fakes

### 3.1 Definition

**Fakes** are working implementations that provide realistic behavior but use shortcuts unsuitable for production. Unlike stubs (which return canned responses) or mocks (which verify interactions), fakes contain actual business logic—just simplified or optimized for testing.

**Gerard Meszaros Definition**: "Fake objects have working implementations, but usually take some shortcut which makes them not suitable for production."

**Common Examples**:
- In-memory database instead of production database
- Local file system instead of cloud storage
- Synchronous implementation instead of asynchronous
- Simple algorithm instead of complex production version

**Key Characteristics**:
- Contains real, working logic
- Simpler/faster than production implementation
- Maintains correctness for test scenarios
- Can be reused across multiple tests
- Often stateful (maintains internal state)
- More complex than stubs, simpler than real implementation

### 3.2 When to Use

**Ideal Scenarios**:
- Replacing slow external dependencies (databases, APIs, file systems)
- Integration tests requiring realistic behavior without infrastructure
- Testing complex interaction patterns that stubs can't handle well
- Scenarios needing stateful behavior across multiple operations
- Development environments where real services are unavailable
- Contract testing where behavior must match real service

**Trade-offs**:
- **Pros**: Fast, reliable, no external dependencies, realistic behavior
- **Cons**: Maintenance overhead, potential drift from real implementation, setup complexity

**When NOT to Use**:
- Unit tests where stubs are sufficient (fakes are overkill)
- When fake behavior significantly differs from production
- If maintenance burden exceeds benefits
- Testing production-specific edge cases (connection timeouts, network errors)

### 3.3 Implementation Examples

#### Jest (JavaScript/TypeScript)

```javascript
// Job Seeker - In-Memory Job Repository Fake
interface JobRepository {
  save(job: Job): Promise<string>;
  findById(jobId: string): Promise<Job | null>;
  findAll(): Promise<Job[]>;
  delete(jobId: string): Promise<boolean>;
  findByCompany(companyId: string): Promise<Job[]>;
}

// Real implementation uses database
class PostgresJobRepository implements JobRepository {
  async save(job: Job): Promise<string> {
    // Complex SQL operations, connection pooling, transactions
    const result = await this.db.query('INSERT INTO jobs...');
    return result.insertId;
  }
  // ... other methods with real DB operations
}

// Fake implementation for testing
class InMemoryJobRepository implements JobRepository {
  private jobs: Map<string, Job> = new Map();
  private idCounter = 1;

  async save(job: Job): Promise<string> {
    // Simplified: just store in memory
    const id = job.id || `job-${this.idCounter++}`;
    const jobWithId = { ...job, id };
    this.jobs.set(id, jobWithId);
    return id;
  }

  async findById(jobId: string): Promise<Job | null> {
    return this.jobs.get(jobId) || null;
  }

  async findAll(): Promise<Job[]> {
    return Array.from(this.jobs.values());
  }

  async delete(jobId: string): Promise<boolean> {
    return this.jobs.delete(jobId);
  }

  async findByCompany(companyId: string): Promise<Job[]> {
    return Array.from(this.jobs.values())
      .filter(job => job.companyId === companyId);
  }

  // Test helper - not in production interface
  clear(): void {
    this.jobs.clear();
    this.idCounter = 1;
  }
}

// Using the Fake in tests
class JobService {
  constructor(private repository: JobRepository) {}

  async createJob(jobData: Partial<Job>): Promise<string> {
    const job: Job = {
      id: '',
      title: jobData.title!,
      companyId: jobData.companyId!,
      status: 'OPEN',
      createdAt: new Date()
    };
    return this.repository.save(job);
  }

  async getCompanyJobs(companyId: string): Promise<Job[]> {
    return this.repository.findByCompany(companyId);
  }
}

describe('JobService with Fake Repository', () => {
  let fakeRepository: InMemoryJobRepository;
  let jobService: JobService;

  beforeEach(() => {
    fakeRepository = new InMemoryJobRepository();
    jobService = new JobService(fakeRepository);
  });

  test('createJob stores job and returns ID', async () => {
    const jobId = await jobService.createJob({
      title: 'Software Engineer',
      companyId: 'company-1'
    });

    expect(jobId).toBeDefined();

    // Verify using fake's stateful behavior
    const savedJob = await fakeRepository.findById(jobId);
    expect(savedJob).toMatchObject({
      title: 'Software Engineer',
      companyId: 'company-1',
      status: 'OPEN'
    });
  });

  test('getCompanyJobs returns all jobs for company', async () => {
    // Create multiple jobs using the fake
    await jobService.createJob({
      title: 'Engineer',
      companyId: 'company-1'
    });
    await jobService.createJob({
      title: 'Designer',
      companyId: 'company-1'
    });
    await jobService.createJob({
      title: 'Manager',
      companyId: 'company-2'
    });

    const company1Jobs = await jobService.getCompanyJobs('company-1');

    expect(company1Jobs).toHaveLength(2);
    expect(company1Jobs.map(j => j.title)).toEqual(
      expect.arrayContaining(['Engineer', 'Designer'])
    );
  });

  test('handles complex workflow across multiple operations', async () => {
    // Fake enables testing complex stateful scenarios
    const jobId = await jobService.createJob({
      title: 'Senior Engineer',
      companyId: 'company-3'
    });

    const job = await fakeRepository.findById(jobId);
    expect(job).toBeTruthy();

    await fakeRepository.delete(jobId);

    const deletedJob = await fakeRepository.findById(jobId);
    expect(deletedJob).toBeNull();
  });
});
```

#### Mockito (Java)

```java
// Job Seeker - In-Memory Email Service Fake
public interface EmailService {
    void sendEmail(String recipient, String subject, String body);
    List<Email> getSentEmails(String recipient);
    void clearHistory();
}

// Real implementation uses SMTP
public class SmtpEmailService implements EmailService {
    public void sendEmail(String recipient, String subject, String body) {
        // Complex SMTP operations: authentication, TLS, retries, etc.
        // ...
    }

    public List<Email> getSentEmails(String recipient) {
        throw new UnsupportedOperationException("Not supported by SMTP");
    }
}

// Fake implementation for testing
public class InMemoryEmailService implements EmailService {
    private final List<Email> sentEmails = new ArrayList<>();

    @Override
    public void sendEmail(String recipient, String subject, String body) {
        // Simplified: just store in memory
        Email email = new Email(
            UUID.randomUUID().toString(),
            recipient,
            subject,
            body,
            LocalDateTime.now()
        );
        sentEmails.add(email);
    }

    @Override
    public List<Email> getSentEmails(String recipient) {
        return sentEmails.stream()
            .filter(email -> email.getRecipient().equals(recipient))
            .collect(Collectors.toList());
    }

    @Override
    public void clearHistory() {
        sentEmails.clear();
    }

    // Additional test helpers
    public int getTotalEmailsSent() {
        return sentEmails.size();
    }

    public boolean wasEmailSentTo(String recipient) {
        return sentEmails.stream()
            .anyMatch(email -> email.getRecipient().equals(recipient));
    }
}

// Using the Fake in tests
public class ApplicationNotificationService {
    private final EmailService emailService;

    public ApplicationNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void notifyApplicationReceived(String applicantEmail, String jobTitle) {
        emailService.sendEmail(
            applicantEmail,
            "Application Received",
            "Thank you for applying to " + jobTitle
        );
    }

    public void notifyApplicationStatusChange(
        String applicantEmail,
        String jobTitle,
        String newStatus
    ) {
        emailService.sendEmail(
            applicantEmail,
            "Application Status Update",
            "Your application for " + jobTitle + " is now " + newStatus
        );
    }
}

@Test
public void testApplicationNotifications_WithFakeEmailService() {
    // Use fake instead of real SMTP service
    InMemoryEmailService fakeEmailService = new InMemoryEmailService();
    ApplicationNotificationService notificationService =
        new ApplicationNotificationService(fakeEmailService);

    String applicantEmail = "applicant@example.com";
    String jobTitle = "Software Engineer";

    // Send initial notification
    notificationService.notifyApplicationReceived(applicantEmail, jobTitle);

    // Verify using fake's stateful behavior
    assertEquals(1, fakeEmailService.getTotalEmailsSent());
    assertTrue(fakeEmailService.wasEmailSentTo(applicantEmail));

    List<Email> sentEmails = fakeEmailService.getSentEmails(applicantEmail);
    assertEquals(1, sentEmails.size());
    assertEquals("Application Received", sentEmails.get(0).getSubject());

    // Send status update
    notificationService.notifyApplicationStatusChange(
        applicantEmail,
        jobTitle,
        "Under Review"
    );

    // Fake maintains state across operations
    assertEquals(2, fakeEmailService.getTotalEmailsSent());

    sentEmails = fakeEmailService.getSentEmails(applicantEmail);
    assertEquals(2, sentEmails.size());
    assertEquals("Application Status Update", sentEmails.get(1).getSubject());
}

@Test
public void testMultipleApplicants_WithFakeEmailService() {
    InMemoryEmailService fakeEmailService = new InMemoryEmailService();
    ApplicationNotificationService notificationService =
        new ApplicationNotificationService(fakeEmailService);

    // Send to multiple applicants
    notificationService.notifyApplicationReceived("alice@example.com", "Engineer");
    notificationService.notifyApplicationReceived("bob@example.com", "Designer");
    notificationService.notifyApplicationReceived("alice@example.com", "Manager");

    // Fake tracks all state correctly
    assertEquals(3, fakeEmailService.getTotalEmailsSent());
    assertEquals(2, fakeEmailService.getSentEmails("alice@example.com").size());
    assertEquals(1, fakeEmailService.getSentEmails("bob@example.com").size());
}
```

### 3.4 Job Seeker Application Examples

#### Example 1: In-Memory Cache Fake

```typescript
// Scenario: Testing caching behavior without Redis
interface CacheService {
  get<T>(key: string): Promise<T | null>;
  set<T>(key: string, value: T, ttlSeconds?: number): Promise<void>;
  delete(key: string): Promise<boolean>;
  clear(): Promise<void>;
}

// Real implementation uses Redis
class RedisCache implements CacheService {
  async get<T>(key: string): Promise<T | null> {
    // Redis client operations, connection handling, serialization
    const value = await this.redisClient.get(key);
    return value ? JSON.parse(value) : null;
  }
  // ... complex Redis operations
}

// Fake implementation for testing
class InMemoryCache implements CacheService {
  private cache = new Map<string, { value: any; expiresAt: number | null }>();

  async get<T>(key: string): Promise<T | null> {
    const entry = this.cache.get(key);
    if (!entry) return null;

    // Simulate TTL expiration
    if (entry.expiresAt && Date.now() > entry.expiresAt) {
      this.cache.delete(key);
      return null;
    }

    return entry.value as T;
  }

  async set<T>(key: string, value: T, ttlSeconds?: number): Promise<void> {
    const expiresAt = ttlSeconds
      ? Date.now() + (ttlSeconds * 1000)
      : null;

    this.cache.set(key, { value, expiresAt });
  }

  async delete(key: string): Promise<boolean> {
    return this.cache.delete(key);
  }

  async clear(): Promise<void> {
    this.cache.clear();
  }

  // Test helpers
  size(): number {
    return this.cache.size;
  }
}

// Service using cache
class JobRecommendationService {
  constructor(
    private cache: CacheService,
    private recommendationEngine: RecommendationEngine
  ) {}

  async getRecommendations(userId: string): Promise<Job[]> {
    const cacheKey = `recommendations:${userId}`;

    // Try cache first
    const cached = await this.cache.get<Job[]>(cacheKey);
    if (cached) return cached;

    // Compute recommendations
    const recommendations = await this.recommendationEngine.compute(userId);

    // Cache for 1 hour
    await this.cache.set(cacheKey, recommendations, 3600);

    return recommendations;
  }
}

// Tests using fake cache
describe('JobRecommendationService with Fake Cache', () => {
  let fakeCache: InMemoryCache;
  let mockEngine: RecommendationEngine;
  let service: JobRecommendationService;

  beforeEach(() => {
    fakeCache = new InMemoryCache();
    mockEngine = {
      compute: jest.fn().mockResolvedValue([
        { id: 'job-1', title: 'Engineer' }
      ])
    };
    service = new JobRecommendationService(fakeCache, mockEngine);
  });

  test('caches recommendations after first call', async () => {
    const userId = 'user-123';

    // First call - cache miss
    await service.getRecommendations(userId);
    expect(mockEngine.compute).toHaveBeenCalledTimes(1);

    // Second call - cache hit (fake maintains state)
    await service.getRecommendations(userId);
    expect(mockEngine.compute).toHaveBeenCalledTimes(1); // Not called again

    expect(fakeCache.size()).toBe(1);
  });

  test('respects TTL expiration', async () => {
    // Set short TTL for testing
    await fakeCache.set('test-key', 'value', 1); // 1 second TTL

    const immediate = await fakeCache.get('test-key');
    expect(immediate).toBe('value');

    // Wait for expiration
    await new Promise(resolve => setTimeout(resolve, 1100));

    const afterExpiration = await fakeCache.get('test-key');
    expect(afterExpiration).toBeNull(); // Fake simulates TTL correctly
  });
});
```

#### Example 2: File Storage Fake

```java
// Scenario: Testing resume upload without actual file system or S3
public interface FileStorageService {
    String uploadFile(String fileName, byte[] content);
    byte[] downloadFile(String fileId);
    boolean deleteFile(String fileId);
    List<String> listFiles(String prefix);
}

// Real implementation uses AWS S3
public class S3FileStorageService implements FileStorageService {
    public String uploadFile(String fileName, byte[] content) {
        // Complex S3 operations: multipart upload, encryption, etc.
        // ...
    }
}

// Fake implementation for testing
public class InMemoryFileStorage implements FileStorageService {
    private final Map<String, FileEntry> files = new HashMap<>();
    private int idCounter = 1;

    @Override
    public String uploadFile(String fileName, byte[] content) {
        String fileId = "file-" + idCounter++;
        files.put(fileId, new FileEntry(fileName, content, LocalDateTime.now()));
        return fileId;
    }

    @Override
    public byte[] downloadFile(String fileId) {
        FileEntry entry = files.get(fileId);
        return entry != null ? entry.getContent() : null;
    }

    @Override
    public boolean deleteFile(String fileId) {
        return files.remove(fileId) != null;
    }

    @Override
    public List<String> listFiles(String prefix) {
        return files.entrySet().stream()
            .filter(e -> e.getValue().getFileName().startsWith(prefix))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    // Test helpers
    public int getFileCount() {
        return files.size();
    }

    public void clear() {
        files.clear();
    }

    private static class FileEntry {
        private final String fileName;
        private final byte[] content;
        private final LocalDateTime uploadedAt;

        public FileEntry(String fileName, byte[] content, LocalDateTime uploadedAt) {
            this.fileName = fileName;
            this.content = content;
            this.uploadedAt = uploadedAt;
        }

        public String getFileName() { return fileName; }
        public byte[] getContent() { return content; }
        public LocalDateTime getUploadedAt() { return uploadedAt; }
    }
}

// Service using file storage
public class ResumeService {
    private final FileStorageService fileStorage;

    public ResumeService(FileStorageService fileStorage) {
        this.fileStorage = fileStorage;
    }

    public String uploadResume(String userId, String fileName, byte[] content) {
        String prefixedFileName = userId + "/" + fileName;
        return fileStorage.uploadFile(prefixedFileName, content);
    }

    public List<String> getUserResumes(String userId) {
        return fileStorage.listFiles(userId + "/");
    }
}

@Test
public void testResumeUploadAndRetrieval_WithFakeStorage() {
    InMemoryFileStorage fakeStorage = new InMemoryFileStorage();
    ResumeService resumeService = new ResumeService(fakeStorage);

    String userId = "user-123";
    byte[] resumeContent = "PDF content".getBytes();

    // Upload resume
    String fileId = resumeService.uploadResume(userId, "resume.pdf", resumeContent);

    assertNotNull(fileId);
    assertEquals(1, fakeStorage.getFileCount());

    // Download and verify
    byte[] downloaded = fakeStorage.downloadFile(fileId);
    assertArrayEquals(resumeContent, downloaded);

    // List user's files
    List<String> userFiles = resumeService.getUserResumes(userId);
    assertEquals(1, userFiles.size());
    assertEquals(fileId, userFiles.get(0));
}

@Test
public void testMultipleUsersResumes_WithFakeStorage() {
    InMemoryFileStorage fakeStorage = new InMemoryFileStorage();
    ResumeService resumeService = new ResumeService(fakeStorage);

    // Upload for different users
    resumeService.uploadResume("user-1", "resume1.pdf", "content1".getBytes());
    resumeService.uploadResume("user-1", "resume2.pdf", "content2".getBytes());
    resumeService.uploadResume("user-2", "resume.pdf", "content3".getBytes());

    // Fake correctly isolates by prefix
    assertEquals(2, resumeService.getUserResumes("user-1").size());
    assertEquals(1, resumeService.getUserResumes("user-2").size());
    assertEquals(3, fakeStorage.getFileCount());
}
```

### 3.5 Best Practices

1. **Maintain Behavioral Equivalence**: Fake should behave like real implementation for test scenarios
2. **Keep Fakes Simple**: Use shortcuts appropriate for testing (in-memory vs. persistent)
3. **Document Limitations**: Clearly state what fake doesn't support
4. **Reuse Across Tests**: Create shared fake implementations
5. **Consider Contract Tests**: Ensure fake and real implementation satisfy same contract
6. **Provide Test Helpers**: Add methods useful for test verification (not in production interface)
7. **Avoid Business Logic**: Fakes should mimic infrastructure, not domain logic

---

## 4. Mocks

### 4.1 Definition

**Mocks** are test doubles that record interactions and verify that the system under test called them correctly. Unlike stubs (which focus on providing responses) or fakes (which provide working implementations), mocks are primarily concerned with **behavior verification**—ensuring specific methods were called with expected parameters, in the correct order, and the right number of times.

**Gerard Meszaros Definition**: "Mocks are pre-programmed with expectations which form a specification of the calls they are expected to receive."

**Martin Fowler's Distinction**:
- **State Verification** (Stubs): Assert on the state of the system after execution
- **Behavior Verification** (Mocks): Assert on the interactions that occurred during execution

**Key Characteristics**:
- Records all method invocations
- Verifies calls were made with correct arguments
- Can verify call order and frequency
- Fails test if expectations not met
- Primarily used for commands (void methods with side effects)
- Enables testing in isolation from dependencies

### 4.2 When to Use

**Ideal Scenarios**:
- **Testing Commands**: Methods with side effects (sending emails, saving to database)
- **Verifying Interactions**: Ensuring dependency is called correctly
- **Protocol Verification**: Checking correct sequence of operations
- **Boundary Testing**: Verifying communication with external systems
- **Integration Points**: Testing how components interact
- **Void Methods**: When there's no return value to assert on

**Queries vs Commands (CQS Principle)**:
- **Queries**: Return data, no side effects → Use **Stubs**
- **Commands**: Perform actions, side effects → Use **Mocks**

**Warning Signs of Mock Overuse**:
- Tests become brittle (break with refactoring)
- Testing implementation details instead of behavior
- Mocking everything leads to meaningless tests
- "Mock hell" - complex mock setup obscures test intent

### 4.3 Implementation Examples

#### Jest (JavaScript/TypeScript)

```javascript
// Job Seeker - Email Service Mock
interface EmailService {
  sendApplicationConfirmation(email: string, jobTitle: string): void;
  sendStatusUpdate(email: string, jobTitle: string, status: string): void;
}

class ApplicationService {
  constructor(
    private emailService: EmailService,
    private repository: ApplicationRepository
  ) {}

  submitApplication(application: Application): void {
    // Save application (command with side effect)
    this.repository.save(application);

    // Send confirmation email (command with side effect)
    this.emailService.sendApplicationConfirmation(
      application.applicantEmail,
      application.jobTitle
    );
  }

  approveApplication(applicationId: string): void {
    const app = this.repository.findById(applicationId);
    if (!app) throw new Error('Application not found');

    app.status = 'APPROVED';
    this.repository.update(app);

    // Send notification (command)
    this.emailService.sendStatusUpdate(
      app.applicantEmail,
      app.jobTitle,
      'APPROVED'
    );
  }
}

// Tests using Mocks for behavior verification
describe('ApplicationService - Behavior Verification', () => {
  test('submitApplication sends confirmation email with correct parameters', () => {
    // Create mocks
    const emailServiceMock = {
      sendApplicationConfirmation: jest.fn(),
      sendStatusUpdate: jest.fn()
    };
    const repositoryMock = {
      save: jest.fn(),
      findById: jest.fn(),
      update: jest.fn()
    };

    const service = new ApplicationService(emailServiceMock, repositoryMock);

    const application = {
      id: 'app-123',
      applicantEmail: 'john@example.com',
      jobTitle: 'Software Engineer',
      status: 'PENDING'
    };

    // Execute
    service.submitApplication(application);

    // Behavior verification - did we call the email service correctly?
    expect(emailServiceMock.sendApplicationConfirmation)
      .toHaveBeenCalledWith('john@example.com', 'Software Engineer');

    expect(emailServiceMock.sendApplicationConfirmation)
      .toHaveBeenCalledTimes(1); // Exactly once

    // Verify repository interaction
    expect(repositoryMock.save).toHaveBeenCalledWith(application);
  });

  test('approveApplication sends status update notification', () => {
    const emailServiceMock = {
      sendApplicationConfirmation: jest.fn(),
      sendStatusUpdate: jest.fn()
    };
    const repositoryMock = {
      save: jest.fn(),
      findById: jest.fn().mockReturnValue({
        id: 'app-456',
        applicantEmail: 'jane@example.com',
        jobTitle: 'Senior Developer',
        status: 'PENDING'
      }),
      update: jest.fn()
    };

    const service = new ApplicationService(emailServiceMock, repositoryMock);

    service.approveApplication('app-456');

    // Verify the status update email was sent with correct status
    expect(emailServiceMock.sendStatusUpdate)
      .toHaveBeenCalledWith('jane@example.com', 'Senior Developer', 'APPROVED');

    // Verify repository was updated
    expect(repositoryMock.update).toHaveBeenCalledWith(
      expect.objectContaining({ status: 'APPROVED' })
    );
  });

  test('verifies call order when multiple operations occur', () => {
    const emailServiceMock = {
      sendApplicationConfirmation: jest.fn(),
      sendStatusUpdate: jest.fn()
    };
    const repositoryMock = {
      save: jest.fn(),
      findById: jest.fn(),
      update: jest.fn()
    };

    const service = new ApplicationService(emailServiceMock, repositoryMock);

    const application = {
      id: 'app-789',
      applicantEmail: 'test@example.com',
      jobTitle: 'Engineer',
      status: 'PENDING'
    };

    service.submitApplication(application);

    // Verify save was called before email
    const saveMock = repositoryMock.save.mock;
    const emailMock = emailServiceMock.sendApplicationConfirmation.mock;

    expect(saveMock.invocationCallOrder[0])
      .toBeLessThan(emailMock.invocationCallOrder[0]);
  });

  test('does not send email if repository save fails', () => {
    const emailServiceMock = {
      sendApplicationConfirmation: jest.fn(),
      sendStatusUpdate: jest.fn()
    };
    const repositoryMock = {
      save: jest.fn().mockImplementation(() => {
        throw new Error('Database error');
      }),
      findById: jest.fn(),
      update: jest.fn()
    };

    const service = new ApplicationService(emailServiceMock, repositoryMock);

    const application = {
      id: 'app-error',
      applicantEmail: 'error@example.com',
      jobTitle: 'Test Job',
      status: 'PENDING'
    };

    expect(() => service.submitApplication(application)).toThrow('Database error');

    // Verify email was NOT sent due to exception
    expect(emailServiceMock.sendApplicationConfirmation)
      .not.toHaveBeenCalled();
  });
});
```

#### Mockito (Java)

```java
// Job Seeker - Notification Service Mock
public interface NotificationService {
    void sendPushNotification(String userId, String message);
    void sendEmail(String email, String subject, String body);
}

public interface ApplicationRepository {
    void save(Application application);
    Application findById(String id);
    void update(Application application);
}

public class ApplicationService {
    private final NotificationService notificationService;
    private final ApplicationRepository repository;

    public ApplicationService(
        NotificationService notificationService,
        ApplicationRepository repository
    ) {
        this.notificationService = notificationService;
        this.repository = repository;
    }

    public void submitApplication(Application application) {
        // Command: save to repository
        repository.save(application);

        // Command: send notifications
        notificationService.sendEmail(
            application.getApplicantEmail(),
            "Application Received",
            "Your application has been submitted"
        );

        notificationService.sendPushNotification(
            application.getUserId(),
            "Application submitted successfully"
        );
    }

    public void withdrawApplication(String applicationId) {
        Application app = repository.findById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found");
        }

        app.setStatus(ApplicationStatus.WITHDRAWN);
        repository.update(app);

        notificationService.sendEmail(
            app.getApplicantEmail(),
            "Application Withdrawn",
            "You have withdrawn your application"
        );
    }
}

// Tests using Mocks with Mockito
public class ApplicationServiceTest {

    @Test
    public void testSubmitApplication_SendsNotifications() {
        // Create mocks
        NotificationService notificationMock = mock(NotificationService.class);
        ApplicationRepository repositoryMock = mock(ApplicationRepository.class);

        ApplicationService service = new ApplicationService(
            notificationMock,
            repositoryMock
        );

        Application application = new Application(
            "app-123",
            "user-456",
            "john@example.com",
            "Software Engineer"
        );

        // Execute
        service.submitApplication(application);

        // Behavior verification - verify interactions occurred
        verify(repositoryMock).save(application);

        verify(notificationMock).sendEmail(
            eq("john@example.com"),
            eq("Application Received"),
            anyString()
        );

        verify(notificationMock).sendPushNotification(
            eq("user-456"),
            eq("Application submitted successfully")
        );
    }

    @Test
    public void testSubmitApplication_VerifiesCallOrder() {
        NotificationService notificationMock = mock(NotificationService.class);
        ApplicationRepository repositoryMock = mock(ApplicationRepository.class);

        ApplicationService service = new ApplicationService(
            notificationMock,
            repositoryMock
        );

        Application application = new Application(
            "app-789",
            "user-123",
            "test@example.com",
            "Engineer"
        );

        service.submitApplication(application);

        // Create InOrder verifier
        InOrder inOrder = inOrder(repositoryMock, notificationMock);

        // Verify save happens before notifications
        inOrder.verify(repositoryMock).save(application);
        inOrder.verify(notificationMock).sendEmail(anyString(), anyString(), anyString());
        inOrder.verify(notificationMock).sendPushNotification(anyString(), anyString());
    }

    @Test
    public void testWithdrawApplication_UpdatesAndNotifies() {
        NotificationService notificationMock = mock(NotificationService.class);
        ApplicationRepository repositoryMock = mock(ApplicationRepository.class);

        Application existingApp = new Application(
            "app-999",
            "user-111",
            "applicant@example.com",
            "Developer"
        );
        existingApp.setStatus(ApplicationStatus.PENDING);

        // Stub the query (findById returns value)
        when(repositoryMock.findById("app-999")).thenReturn(existingApp);

        ApplicationService service = new ApplicationService(
            notificationMock,
            repositoryMock
        );

        service.withdrawApplication("app-999");

        // Verify command interactions (mock behavior)
        verify(repositoryMock).update(argThat(app ->
            app.getStatus() == ApplicationStatus.WITHDRAWN
        ));

        verify(notificationMock).sendEmail(
            eq("applicant@example.com"),
            eq("Application Withdrawn"),
            anyString()
        );
    }

    @Test
    public void testSubmitApplication_VerifiesExactNumberOfCalls() {
        NotificationService notificationMock = mock(NotificationService.class);
        ApplicationRepository repositoryMock = mock(ApplicationRepository.class);

        ApplicationService service = new ApplicationService(
            notificationMock,
            repositoryMock
        );

        Application app = new Application(
            "app-555",
            "user-777",
            "user@example.com",
            "Job Title"
        );

        service.submitApplication(app);

        // Verify exact number of invocations
        verify(repositoryMock, times(1)).save(app);
        verify(notificationMock, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(notificationMock, times(1)).sendPushNotification(anyString(), anyString());

        // Verify no other interactions
        verifyNoMoreInteractions(notificationMock);
    }

    @Test
    public void testSubmitApplication_DoesNotNotifyOnRepositoryFailure() {
        NotificationService notificationMock = mock(NotificationService.class);
        ApplicationRepository repositoryMock = mock(ApplicationRepository.class);

        // Mock repository to throw exception
        doThrow(new RuntimeException("Database error"))
            .when(repositoryMock).save(any(Application.class));

        ApplicationService service = new ApplicationService(
            notificationMock,
            repositoryMock
        );

        Application app = new Application(
            "app-error",
            "user-error",
            "error@example.com",
            "Test Job"
        );

        assertThrows(RuntimeException.class, () -> {
            service.submitApplication(app);
        });

        // Verify notifications were NOT sent due to exception
        verify(notificationMock, never()).sendEmail(anyString(), anyString(), anyString());
        verify(notificationMock, never()).sendPushNotification(anyString(), anyString());
    }
}
```

### 4.4 Job Seeker Application Examples

#### Example 1: Job Matching Service Integration

```typescript
// Scenario: Verify job matching service calls analytics tracker
interface AnalyticsTracker {
  trackJobView(userId: string, jobId: string, timestamp: Date): void;
  trackJobMatch(userId: string, jobId: string, score: number): void;
}

interface JobRepository {
  findById(jobId: string): Job | null;
}

class JobMatchingService {
  constructor(
    private repository: JobRepository,
    private analyticsTracker: AnalyticsTracker
  ) {}

  viewJob(userId: string, jobId: string): Job | null {
    const job = this.repository.findById(jobId);

    if (job) {
      // Track analytics (command with side effect)
      this.analyticsTracker.trackJobView(userId, jobId, new Date());
    }

    return job;
  }

  recordMatch(userId: string, jobId: string, score: number): void {
    // Track the match event (command)
    this.analyticsTracker.trackJobMatch(userId, jobId, score);
  }
}

// Test with Mock for behavior verification
describe('JobMatchingService - Analytics Integration', () => {
  test('viewJob tracks analytics when job exists', () => {
    const repositoryStub = {
      findById: jest.fn().mockReturnValue({
        id: 'job-123',
        title: 'Engineer'
      })
    };

    const analyticsTrackerMock = {
      trackJobView: jest.fn(),
      trackJobMatch: jest.fn()
    };

    const service = new JobMatchingService(repositoryStub, analyticsTrackerMock);

    const result = service.viewJob('user-456', 'job-123');

    // Verify analytics tracking occurred
    expect(analyticsTrackerMock.trackJobView).toHaveBeenCalledWith(
      'user-456',
      'job-123',
      expect.any(Date)
    );
    expect(analyticsTrackerMock.trackJobView).toHaveBeenCalledTimes(1);
  });

  test('viewJob does not track analytics when job not found', () => {
    const repositoryStub = {
      findById: jest.fn().mockReturnValue(null)
    };

    const analyticsTrackerMock = {
      trackJobView: jest.fn(),
      trackJobMatch: jest.fn()
    };

    const service = new JobMatchingService(repositoryStub, analyticsTrackerMock);

    const result = service.viewJob('user-456', 'nonexistent');

    // Verify analytics was NOT called
    expect(analyticsTrackerMock.trackJobView).not.toHaveBeenCalled();
    expect(result).toBeNull();
  });

  test('recordMatch tracks with correct score', () => {
    const repositoryStub = { findById: jest.fn() };
    const analyticsTrackerMock = {
      trackJobView: jest.fn(),
      trackJobMatch: jest.fn()
    };

    const service = new JobMatchingService(repositoryStub, analyticsTrackerMock);

    service.recordMatch('user-789', 'job-999', 85.5);

    // Verify exact parameters
    expect(analyticsTrackerMock.trackJobMatch).toHaveBeenCalledWith(
      'user-789',
      'job-999',
      85.5
    );
  });
});
```

#### Example 2: Application Workflow with Multiple Commands

```java
// Scenario: Verify application approval workflow executes all commands
public interface ApplicationRepository {
    Application findById(String id);
    void update(Application application);
}

public interface NotificationService {
    void notifyApplicant(String email, String message);
    void notifyRecruiter(String recruiterId, String message);
}

public interface AuditLog {
    void logStatusChange(String applicationId, String oldStatus, String newStatus, String actor);
}

public class ApplicationApprovalService {
    private final ApplicationRepository repository;
    private final NotificationService notificationService;
    private final AuditLog auditLog;

    public ApplicationApprovalService(
        ApplicationRepository repository,
        NotificationService notificationService,
        AuditLog auditLog
    ) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.auditLog = auditLog;
    }

    public void approveApplication(String applicationId, String approverId) {
        Application app = repository.findById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found");
        }

        String oldStatus = app.getStatus();
        app.setStatus("APPROVED");
        app.setApprovedBy(approverId);

        // Command 1: Update repository
        repository.update(app);

        // Command 2: Notify applicant
        notificationService.notifyApplicant(
            app.getApplicantEmail(),
            "Your application has been approved!"
        );

        // Command 3: Notify recruiter
        notificationService.notifyRecruiter(
            app.getRecruiterId(),
            "Application " + applicationId + " approved"
        );

        // Command 4: Audit log
        auditLog.logStatusChange(applicationId, oldStatus, "APPROVED", approverId);
    }
}

@Test
public void testApproveApplication_ExecutesAllCommands() {
    // Create mocks for all command dependencies
    ApplicationRepository repositoryMock = mock(ApplicationRepository.class);
    NotificationService notificationMock = mock(NotificationService.class);
    AuditLog auditLogMock = mock(AuditLog.class);

    Application existingApp = new Application();
    existingApp.setId("app-123");
    existingApp.setStatus("PENDING");
    existingApp.setApplicantEmail("applicant@example.com");
    existingApp.setRecruiterId("recruiter-456");

    when(repositoryMock.findById("app-123")).thenReturn(existingApp);

    ApplicationApprovalService service = new ApplicationApprovalService(
        repositoryMock,
        notificationMock,
        auditLogMock
    );

    // Execute approval
    service.approveApplication("app-123", "admin-789");

    // Verify all commands were executed
    verify(repositoryMock).update(argThat(app ->
        "APPROVED".equals(app.getStatus()) &&
        "admin-789".equals(app.getApprovedBy())
    ));

    verify(notificationMock).notifyApplicant(
        eq("applicant@example.com"),
        contains("approved")
    );

    verify(notificationMock).notifyRecruiter(
        eq("recruiter-456"),
        contains("app-123")
    );

    verify(auditLogMock).logStatusChange(
        eq("app-123"),
        eq("PENDING"),
        eq("APPROVED"),
        eq("admin-789")
    );
}

@Test
public void testApproveApplication_VerifiesCommandOrder() {
    ApplicationRepository repositoryMock = mock(ApplicationRepository.class);
    NotificationService notificationMock = mock(NotificationService.class);
    AuditLog auditLogMock = mock(AuditLog.class);

    Application app = new Application();
    app.setId("app-456");
    app.setStatus("PENDING");
    app.setApplicantEmail("test@example.com");
    app.setRecruiterId("recruiter-123");

    when(repositoryMock.findById("app-456")).thenReturn(app);

    ApplicationApprovalService service = new ApplicationApprovalService(
        repositoryMock,
        notificationMock,
        auditLogMock
    );

    service.approveApplication("app-456", "admin-999");

    // Verify commands executed in correct order
    InOrder inOrder = inOrder(repositoryMock, notificationMock, auditLogMock);

    inOrder.verify(repositoryMock).update(any(Application.class));
    inOrder.verify(notificationMock).notifyApplicant(anyString(), anyString());
    inOrder.verify(notificationMock).notifyRecruiter(anyString(), anyString());
    inOrder.verify(auditLogMock).logStatusChange(
        anyString(), anyString(), anyString(), anyString()
    );
}
```

### 4.5 Best Practices

1. **Mock Commands, Stub Queries**: Use mocks for side effects, stubs for data retrieval
2. **Verify Behavior, Not Implementation**: Focus on what happens, not how
3. **Avoid Over-Mocking**: Don't mock everything; test real collaborations when possible
4. **Use Argument Matchers Wisely**: `anyString()` vs exact values - balance specificity and fragility
5. **Verify Call Order When It Matters**: Use `InOrder` for sequence-dependent operations
6. **Test Negative Paths**: Verify what DOESN'T happen (e.g., no notification on error)
7. **Keep Tests Readable**: Complex mock setups may indicate design issues

---

## 5. Spies

### 5.1 Definition

**Spies** are test doubles that wrap real objects and record information about interactions while delegating to the actual implementation. They combine characteristics of real objects (executing actual code) with mocks (recording calls for verification). Spies are often called "partial mocks" because they allow selective stubbing of specific methods while calling through to real implementations for others.

**Key Characteristics**:
- Wraps a real object instance
- Records all method invocations (like mocks)
- Delegates to real implementation by default
- Allows selective stubbing of specific methods
- Useful for testing legacy code or complex dependencies
- Enables verification of calls to real objects

**Spies vs Mocks**:
- **Mocks**: Completely simulated, no real code execution
- **Spies**: Real object wrapped, actual code executes unless stubbed

**When Real Meets Test**: Spies blur the line between integration and unit tests by using real implementations while maintaining verification capabilities.

### 5.2 When to Use

**Ideal Scenarios**:
- **Partial Stubbing**: Need most real behavior but override specific methods
- **Legacy Code Testing**: Testing existing code without extensive refactoring
- **Verification on Real Objects**: Track calls to actual implementations
- **Side Effect Monitoring**: Observe real behavior while recording interactions
- **Gradual Test Migration**: Transitioning from integration to unit tests
- **Complex Dependencies**: When creating full mocks is prohibitively complex

**Anti-patterns and Cautions**:
- **Over-reliance on Spies**: May indicate poor separation of concerns
- **Testing Implementation Details**: Spies make it easier to couple tests to implementation
- **Obscured Test Intent**: Mixing real and stubbed behavior can confuse readers
- **Integration Test Disguise**: Spies can mask what should be integration tests

**Trade-offs**:
- **Pros**: Real behavior, easier setup for complex objects, gradual testing approach
- **Cons**: Slower (real code executes), potential side effects, less isolation

### 5.3 Implementation Examples

#### Jest (JavaScript/TypeScript)

```javascript
// Job Seeker - User Profile Service
class UserProfileService {
  constructor(private database: Database) {}

  getProfile(userId: string): UserProfile | null {
    return this.database.query(`SELECT * FROM profiles WHERE id = ?`, [userId]);
  }

  updateEmail(userId: string, newEmail: string): boolean {
    const profile = this.getProfile(userId); // Calls internal method

    if (!profile) {
      return false;
    }

    this.validateEmail(newEmail);

    const result = this.database.execute(
      `UPDATE profiles SET email = ? WHERE id = ?`,
      [newEmail, userId]
    );

    return result.affectedRows > 0;
  }

  validateEmail(email: string): void {
    if (!email.includes('@')) {
      throw new Error('Invalid email');
    }
  }

  private logActivity(userId: string, action: string): void {
    console.log(`User ${userId} performed: ${action}`);
  }
}

// Using Spy to test with partial real behavior
describe('UserProfileService with Spies', () => {
  test('updateEmail calls getProfile and validateEmail', () => {
    const mockDatabase = {
      query: jest.fn().mockReturnValue({
        id: 'user-123',
        email: 'old@example.com'
      }),
      execute: jest.fn().mockReturnValue({ affectedRows: 1 })
    };

    const service = new UserProfileService(mockDatabase);

    // Create spy - wraps real service instance
    const serviceSpy = jest.spyOn(service, 'getProfile');
    const validateSpy = jest.spyOn(service, 'validateEmail');

    // Execute - calls REAL updateEmail, but we can verify internal calls
    const result = service.updateEmail('user-123', 'new@example.com');

    // Verify internal method calls
    expect(serviceSpy).toHaveBeenCalledWith('user-123');
    expect(validateSpy).toHaveBeenCalledWith('new@example.com');
    expect(result).toBe(true);

    // Cleanup
    serviceSpy.mockRestore();
    validateSpy.mockRestore();
  });

  test('spy with partial stubbing - override specific method', () => {
    const mockDatabase = {
      query: jest.fn(),
      execute: jest.fn().mockReturnValue({ affectedRows: 1 })
    };

    const service = new UserProfileService(mockDatabase);

    // Spy on getProfile and stub it to return specific value
    const profileSpy = jest.spyOn(service, 'getProfile')
      .mockReturnValue({ id: 'user-456', email: 'stubbed@example.com' });

    // validateEmail uses REAL implementation
    // getProfile uses STUBBED implementation

    const result = service.updateEmail('user-456', 'valid@example.com');

    expect(profileSpy).toHaveBeenCalled();
    expect(result).toBe(true);

    profileSpy.mockRestore();
  });

  test('spy on real object to verify side effects', () => {
    const realDatabase = {
      data: new Map<string, any>(),

      query: function(sql: string, params: any[]) {
        return this.data.get(params[0]) || null;
      },

      execute: function(sql: string, params: any[]) {
        this.data.set(params[1], { email: params[0] });
        return { affectedRows: 1 };
      }
    };

    // Pre-populate database
    realDatabase.data.set('user-789', { id: 'user-789', email: 'original@example.com' });

    const service = new UserProfileService(realDatabase);

    // Spy on database methods while using REAL implementation
    const querySpy = jest.spyOn(realDatabase, 'query');
    const executeSpy = jest.spyOn(realDatabase, 'execute');

    service.updateEmail('user-789', 'updated@example.com');

    // Verify calls to real database
    expect(querySpy).toHaveBeenCalledWith(
      expect.stringContaining('SELECT'),
      ['user-789']
    );

    expect(executeSpy).toHaveBeenCalledWith(
      expect.stringContaining('UPDATE'),
      ['updated@example.com', 'user-789']
    );

    // Verify real side effect occurred
    const updatedProfile = realDatabase.data.get('user-789');
    expect(updatedProfile.email).toBe('updated@example.com');
  });
});

// Job Seeker - Spy on class instance methods
class JobSearchService {
  private searchHistory: string[] = [];

  search(query: string): Job[] {
    this.recordSearch(query); // Internal call
    return this.executeSearch(query);
  }

  private recordSearch(query: string): void {
    this.searchHistory.push(query);
  }

  private executeSearch(query: string): Job[] {
    // Real search logic
    return [{ id: '1', title: 'Job matching ' + query }];
  }

  getSearchHistory(): string[] {
    return [...this.searchHistory];
  }
}

describe('JobSearchService with Instance Spy', () => {
  test('search records history internally', () => {
    const service = new JobSearchService();

    // Spy on private method (use with caution!)
    const recordSpy = jest.spyOn(service as any, 'recordSearch');

    service.search('JavaScript');
    service.search('React');

    // Verify internal method was called
    expect(recordSpy).toHaveBeenCalledTimes(2);
    expect(recordSpy).toHaveBeenNthCalledWith(1, 'JavaScript');
    expect(recordSpy).toHaveBeenNthCalledWith(2, 'React');

    // Verify real behavior still worked
    expect(service.getSearchHistory()).toEqual(['JavaScript', 'React']);
  });
});
```

#### Mockito (Java)

```java
// Job Seeker - Application Service
public class ApplicationService {
    private final ApplicationRepository repository;
    private final NotificationService notificationService;

    public ApplicationService(
        ApplicationRepository repository,
        NotificationService notificationService
    ) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public boolean submitApplication(Application application) {
        // Real logic
        if (!isValid(application)) {
            return false;
        }

        repository.save(application);
        sendNotifications(application);
        return true;
    }

    protected boolean isValid(Application application) {
        return application.getApplicantEmail() != null
            && application.getJobId() != null;
    }

    protected void sendNotifications(Application application) {
        notificationService.sendEmail(
            application.getApplicantEmail(),
            "Application Submitted"
        );
    }

    public Application getApplication(String id) {
        return repository.findById(id);
    }
}

// Tests using Mockito Spy
@Test
public void testSubmitApplication_WithSpy() {
    ApplicationRepository mockRepository = mock(ApplicationRepository.class);
    NotificationService mockNotification = mock(NotificationService.class);

    // Create REAL service instance
    ApplicationService realService = new ApplicationService(
        mockRepository,
        mockNotification
    );

    // Create SPY wrapping the real instance
    ApplicationService serviceSpy = spy(realService);

    Application application = new Application(
        "app-123",
        "user-456",
        "applicant@example.com",
        "job-789"
    );

    // Execute - calls REAL submitApplication logic
    boolean result = serviceSpy.submitApplication(application);

    // Verify internal method calls using spy
    verify(serviceSpy).isValid(application);
    verify(serviceSpy).sendNotifications(application);

    // Verify dependencies were called
    verify(mockRepository).save(application);
    verify(mockNotification).sendEmail(
        eq("applicant@example.com"),
        eq("Application Submitted")
    );

    assertTrue(result);
}

@Test
public void testPartialStubbing_WithSpy() {
    ApplicationRepository mockRepository = mock(ApplicationRepository.class);
    NotificationService mockNotification = mock(NotificationService.class);

    ApplicationService realService = new ApplicationService(
        mockRepository,
        mockNotification
    );

    ApplicationService serviceSpy = spy(realService);

    // Stub ONLY the isValid method, rest uses real implementation
    doReturn(false).when(serviceSpy).isValid(any(Application.class));

    Application application = new Application(
        "app-invalid",
        "user-999",
        "invalid@example.com",
        "job-111"
    );

    // isValid is stubbed to return false
    // Rest of submitApplication logic is REAL
    boolean result = serviceSpy.submitApplication(application);

    assertFalse(result);

    // Verify repository.save was NOT called because validation failed
    verify(mockRepository, never()).save(any(Application.class));
    verify(serviceSpy, never()).sendNotifications(any(Application.class));
}

@Test
public void testSpyOnRealCollaborations() {
    ApplicationRepository mockRepository = mock(ApplicationRepository.class);
    NotificationService mockNotification = mock(NotificationService.class);

    ApplicationService service = new ApplicationService(
        mockRepository,
        mockNotification
    );

    // Use spy to monitor real service behavior
    ApplicationService serviceSpy = spy(service);

    Application app1 = new Application("app-1", "user-1", "a@example.com", "job-1");
    Application app2 = new Application("app-2", "user-2", "b@example.com", "job-2");

    serviceSpy.submitApplication(app1);
    serviceSpy.submitApplication(app2);

    // Verify real logic executed multiple times
    verify(serviceSpy, times(2)).isValid(any(Application.class));
    verify(serviceSpy, times(2)).sendNotifications(any(Application.class));
}

// Spy on real object with state
@Test
public void testSpyWithRealState() {
    // Real list with state
    List<String> realList = new ArrayList<>();
    realList.add("item1");
    realList.add("item2");

    // Spy wraps real list
    List<String> spyList = spy(realList);

    // Real behavior
    assertEquals(2, spyList.size());
    assertTrue(spyList.contains("item1"));

    // Verify real method was called
    verify(spyList).contains("item1");

    // Stub specific method while keeping rest real
    when(spyList.size()).thenReturn(100);

    assertEquals(100, spyList.size()); // Stubbed
    assertTrue(spyList.contains("item2")); // Real
}
```

### 5.4 Job Seeker Application Examples

#### Example 1: Testing Service with Logging

```typescript
// Scenario: Verify logging occurs in real service execution
class JobApplicationService {
  constructor(
    private repository: ApplicationRepository,
    private logger: Logger
  ) {}

  submitApplication(application: Application): string {
    this.logAttempt(application);

    const validationErrors = this.validate(application);
    if (validationErrors.length > 0) {
      this.logValidationFailure(application, validationErrors);
      throw new Error('Validation failed');
    }

    const id = this.repository.save(application);
    this.logSuccess(application, id);

    return id;
  }

  private logAttempt(application: Application): void {
    this.logger.info(`Submitting application for user ${application.userId}`);
  }

  private validate(application: Application): string[] {
    const errors: string[] = [];
    if (!application.resume) errors.push('Resume required');
    if (!application.coverLetter) errors.push('Cover letter required');
    return errors;
  }

  private logValidationFailure(application: Application, errors: string[]): void {
    this.logger.warn(`Validation failed: ${errors.join(', ')}`);
  }

  private logSuccess(application: Application, id: string): void {
    this.logger.info(`Application ${id} submitted successfully`);
  }
}

// Test with Spy
describe('JobApplicationService Logging', () => {
  test('logs all steps during successful submission', () => {
    const mockRepository = {
      save: jest.fn().mockReturnValue('app-123')
    };

    const mockLogger = {
      info: jest.fn(),
      warn: jest.fn(),
      error: jest.fn()
    };

    const service = new JobApplicationService(mockRepository, mockLogger);

    // Spy on internal logging methods
    const logAttemptSpy = jest.spyOn(service as any, 'logAttempt');
    const logSuccessSpy = jest.spyOn(service as any, 'logSuccess');

    const application = {
      userId: 'user-456',
      jobId: 'job-789',
      resume: 'resume.pdf',
      coverLetter: 'cover.pdf'
    };

    const result = service.submitApplication(application);

    // Verify logging calls
    expect(logAttemptSpy).toHaveBeenCalledWith(application);
    expect(logSuccessSpy).toHaveBeenCalledWith(application, 'app-123');

    // Verify actual logger was called
    expect(mockLogger.info).toHaveBeenCalledWith(
      expect.stringContaining('Submitting application')
    );
    expect(mockLogger.info).toHaveBeenCalledWith(
      expect.stringContaining('submitted successfully')
    );

    expect(result).toBe('app-123');
  });

  test('logs validation failures', () => {
    const mockRepository = { save: jest.fn() };
    const mockLogger = {
      info: jest.fn(),
      warn: jest.fn(),
      error: jest.fn()
    };

    const service = new JobApplicationService(mockRepository, mockLogger);

    const logValidationFailureSpy = jest.spyOn(
      service as any,
      'logValidationFailure'
    );

    const invalidApplication = {
      userId: 'user-999',
      jobId: 'job-111',
      resume: null, // Missing
      coverLetter: null // Missing
    };

    expect(() => service.submitApplication(invalidApplication)).toThrow();

    // Verify validation failure was logged
    expect(logValidationFailureSpy).toHaveBeenCalledWith(
      invalidApplication,
      expect.arrayContaining(['Resume required', 'Cover letter required'])
    );

    expect(mockLogger.warn).toHaveBeenCalled();
  });
});
```

#### Example 2: Cache Service with Real State

```java
// Scenario: Test cache service with real caching logic
public class CachedJobService {
    private final JobRepository repository;
    private final Map<String, Job> cache;

    public CachedJobService(JobRepository repository) {
        this.repository = repository;
        this.cache = new HashMap<>();
    }

    public Job getJob(String jobId) {
        if (cache.containsKey(jobId)) {
            return getFromCache(jobId);
        }

        Job job = repository.findById(jobId);
        if (job != null) {
            putInCache(jobId, job);
        }

        return job;
    }

    protected Job getFromCache(String jobId) {
        return cache.get(jobId);
    }

    protected void putInCache(String jobId, Job job) {
        cache.put(jobId, job);
    }

    public void clearCache() {
        cache.clear();
    }

    public int getCacheSize() {
        return cache.size();
    }
}

@Test
public void testCaching_WithSpyOnRealService() {
    JobRepository mockRepository = mock(JobRepository.class);
    Job testJob = new Job("job-123", "Software Engineer");
    when(mockRepository.findById("job-123")).thenReturn(testJob);

    // Create REAL service with actual cache
    CachedJobService realService = new CachedJobService(mockRepository);

    // Spy on real service to verify caching behavior
    CachedJobService serviceSpy = spy(realService);

    // First call - cache miss
    Job result1 = serviceSpy.getJob("job-123");

    verify(serviceSpy).getFromCache("job-123");
    verify(serviceSpy).putInCache("job-123", testJob);
    verify(mockRepository, times(1)).findById("job-123");
    assertEquals(testJob, result1);

    // Second call - cache hit (uses REAL cache state)
    Job result2 = serviceSpy.getJob("job-123");

    verify(serviceSpy, times(2)).getFromCache("job-123");
    verify(mockRepository, times(1)).findById("job-123"); // Still only called once
    assertEquals(testJob, result2);

    // Verify real cache state
    assertEquals(1, serviceSpy.getCacheSize());
}

@Test
public void testPartialStubbing_OverrideCacheCheck() {
    JobRepository mockRepository = mock(JobRepository.class);
    Job testJob = new Job("job-456", "Designer");
    when(mockRepository.findById("job-456")).thenReturn(testJob);

    CachedJobService realService = new CachedJobService(mockRepository);
    CachedJobService serviceSpy = spy(realService);

    // Stub getFromCache to simulate cache hit (even though cache is empty)
    doReturn(testJob).when(serviceSpy).getFromCache("job-456");

    Job result = serviceSpy.getJob("job-456");

    // Repository was never called because stubbed cache returned value
    verify(mockRepository, never()).findById(anyString());
    verify(serviceSpy, never()).putInCache(anyString(), any(Job.class));
    assertEquals(testJob, result);
}
```

### 5.5 Best Practices

1. **Use Spies Sparingly**: Prefer mocks/stubs for cleaner unit tests
2. **Avoid Testing Private Methods**: Spying on private methods couples tests to implementation
3. **Document Real vs Stubbed**: Clearly indicate which methods use real vs stubbed behavior
4. **Watch for Side Effects**: Real code execution may have unintended consequences
5. **Consider Refactoring Instead**: If spies are needed frequently, redesign may be better
6. **Use for Legacy Code**: Spies are particularly useful when refactoring is impractical
7. **Prefer Stub/Mock When Possible**: Only use spy when real behavior is genuinely needed

---

## 6. Test Double Selection Decision Tree

### 6.1 Quick Reference Guide

```
START: What type of dependency do I need to replace?

├─ Does the dependency need to be passed but never used?
│  └─ YES → Use DUMMY
│     Example: Constructor parameter required but not accessed in test
│
├─ Does the test verify interactions/calls to the dependency?
│  └─ YES → Use MOCK or SPY
│     ├─ Do I need the real implementation?
│     │  └─ YES → Use SPY (partial mock)
│     └─ NO → Use MOCK (pure behavior verification)
│
├─ Does the dependency return data/values?
│  └─ YES → Consider:
│     ├─ Is the logic simple (canned responses)?
│     │  └─ YES → Use STUB
│     │     Example: Return fixed value, simulate error condition
│     │
│     └─ Does it need complex, stateful behavior?
│        └─ YES → Use FAKE
│           Example: In-memory database, file system simulation
│
└─ Not sure? → Start with STUB, upgrade to FAKE if needed
```

### 6.2 Comparison Matrix

| **Characteristic** | **Dummy** | **Stub** | **Fake** | **Mock** | **Spy** |
|-------------------|-----------|----------|----------|----------|---------|
| **Executes real code** | No | No | Yes (simplified) | No | Yes (wrapped) |
| **Returns values** | No | Yes (canned) | Yes (computed) | Optional | Yes (real) |
| **Verifies interactions** | No | No | No | **Yes** | **Yes** |
| **Maintains state** | No | No | **Yes** | No | **Yes** |
| **Complexity** | Minimal | Low | Medium-High | Low-Medium | Medium |
| **Reusability** | Low | Medium | **High** | Medium | Low |
| **Test speed** | Fastest | Fast | Medium | Fast | Slowest |
| **Primary use** | Satisfy signatures | Provide responses | Realistic behavior | Verify calls | Monitor real objects |

### 6.3 Decision Framework by Scenario

#### Scenario 1: External Service Call

```typescript
// Question: How to test this?
class OrderService {
  constructor(private paymentGateway: PaymentGateway) {}

  processOrder(order: Order): boolean {
    return this.paymentGateway.charge(order.amount, order.cardToken);
  }
}

// Decision:
// - Need to verify charge was called? → MOCK
// - Just need success/failure response? → STUB
// - Testing retry logic with state? → FAKE
```

**Recommended**: **MOCK** (verifying command execution)

```typescript
test('processOrder calls payment gateway with correct amount', () => {
  const mockGateway = {
    charge: jest.fn().mockReturnValue(true)
  };

  const service = new OrderService(mockGateway);
  service.processOrder({ amount: 100, cardToken: 'tok_123' });

  expect(mockGateway.charge).toHaveBeenCalledWith(100, 'tok_123');
});
```

#### Scenario 2: Data Repository Query

```java
// Question: How to test this?
public class UserService {
    private final UserRepository repository;

    public User getUser(String userId) {
        return repository.findById(userId);
    }
}

// Decision:
// - Just need user object returned? → STUB
// - Testing complex queries across multiple calls? → FAKE
// - Verifying findById was called? → MOCK (but query, so STUB better)
```

**Recommended**: **STUB** (query returns data, no verification needed)

```java
@Test
public void testGetUser() {
    UserRepository stubRepository = mock(UserRepository.class);
    when(stubRepository.findById("user-123"))
        .thenReturn(new User("user-123", "John"));

    UserService service = new UserService(stubRepository);
    User user = service.getUser("user-123");

    assertEquals("John", user.getName());
    // No verify() - state-based assertion
}
```

#### Scenario 3: Email Notification

```typescript
// Question: How to test this?
class RegistrationService {
  constructor(private emailService: EmailService) {}

  register(user: User): void {
    this.saveUser(user);
    this.emailService.sendWelcomeEmail(user.email);
  }
}

// Decision:
// - Need to verify email was sent? → MOCK
// - Need to inspect email content? → FAKE (if complex)
// - Just checking flow works? → STUB
```

**Recommended**: **MOCK** (command with side effect, verify it was called)

```typescript
test('register sends welcome email', () => {
  const emailMock = {
    sendWelcomeEmail: jest.fn()
  };

  const service = new RegistrationService(emailMock);
  service.register({ email: 'user@example.com', name: 'Alice' });

  expect(emailMock.sendWelcomeEmail)
    .toHaveBeenCalledWith('user@example.com');
});
```

#### Scenario 4: Complex Workflow Testing

```java
// Question: How to test multi-step job application workflow?
public class ApplicationWorkflow {
    private final ApplicationRepository repository;
    private final EmailService emailService;
    private final NotificationQueue queue;

    public void submitApplication(Application app) {
        repository.save(app);
        emailService.sendConfirmation(app.getEmail());
        queue.enqueue(new ReviewTask(app.getId()));
    }
}

// Decision:
// - Need to verify all steps executed? → MOCK all dependencies
// - Need realistic repository behavior? → FAKE repository, MOCK others
// - Just testing save/send/enqueue? → MOCK all
```

**Recommended**: **MOCK** for commands, **FAKE** if stateful queries needed

```java
@Test
public void testApplicationWorkflow() {
    // FAKE for repository if we need to query saved data later
    InMemoryApplicationRepository fakeRepo = new InMemoryApplicationRepository();

    // MOCK for commands
    EmailService emailMock = mock(EmailService.class);
    NotificationQueue queueMock = mock(NotificationQueue.class);

    ApplicationWorkflow workflow = new ApplicationWorkflow(
        fakeRepo,
        emailMock,
        queueMock
    );

    Application app = new Application("user@example.com", "job-123");
    workflow.submitApplication(app);

    // Verify commands
    verify(emailMock).sendConfirmation("user@example.com");
    verify(queueMock).enqueue(any(ReviewTask.class));

    // Verify state (using fake)
    assertEquals(1, fakeRepo.count());
}
```

### 6.4 CQS-Based Selection

**Command-Query Separation Principle**:
- **Commands**: Modify state, return void → Use **MOCK**
- **Queries**: Return data, no side effects → Use **STUB** or **FAKE**

```typescript
interface JobRepository {
  // QUERIES (use STUB/FAKE)
  findById(id: string): Job | null;
  findAll(): Job[];
  count(): number;

  // COMMANDS (use MOCK)
  save(job: Job): void;
  delete(id: string): void;
  update(job: Job): void;
}
```

### 6.5 Job Seeker Application Decision Examples

#### Example 1: Job Matching Service

```typescript
class JobMatchingService {
  constructor(
    private scoringEngine: ScoringEngine,      // Query → STUB
    private userRepository: UserRepository,     // Query → STUB/FAKE
    private analyticsTracker: AnalyticsTracker  // Command → MOCK
  ) {}

  findMatches(userId: string): Job[] {
    const user = this.userRepository.findById(userId); // Query
    const jobs = this.getAllJobs();

    const matches = jobs
      .map(job => ({
        job,
        score: this.scoringEngine.calculateScore(user, job) // Query
      }))
      .filter(m => m.score > 70);

    this.analyticsTracker.trackMatchingOperation(userId, matches.length); // Command

    return matches.map(m => m.job);
  }
}

// Test Double Selection:
// - scoringEngine: STUB (returns canned scores)
// - userRepository: STUB (returns test user)
// - analyticsTracker: MOCK (verify tracking called)
```

#### Example 2: Resume Upload Service

```java
public class ResumeUploadService {
    private final FileStorage fileStorage;        // Command → MOCK or FAKE
    private final VirusScanner virusScanner;      // Query → STUB
    private final NotificationService notifier;   // Command → MOCK

    public String uploadResume(String userId, byte[] file) {
        ScanResult scanResult = virusScanner.scan(file); // Query
        if (!scanResult.isClean()) {
            throw new SecurityException("Virus detected");
        }

        String fileId = fileStorage.save(userId, file); // Command
        notifier.notifyUploadSuccess(userId);           // Command

        return fileId;
    }
}

// Test Double Selection:
// - virusScanner: STUB (return clean/infected result)
// - fileStorage: MOCK (verify save called) or FAKE (test multiple uploads)
// - notifier: MOCK (verify notification sent)
```

---

## 7. References and Further Reading

### 7.1 Foundational Texts

**"xUnit Test Patterns: Refactoring Test Code" by Gerard Meszaros**
- Definitive source for test double patterns
- Detailed catalog of dummy, stub, fake, mock, spy patterns
- Test smell identification and refactoring strategies
- Available: https://xunitpatterns.com/

**"Mocks Aren't Stubs" by Martin Fowler**
- Classic article distinguishing state vs behavior verification
- Explains classical vs mockist TDD
- Available: https://martinfowler.com/articles/mocksArentStubs.html

**"Growing Object-Oriented Software, Guided by Tests" by Steve Freeman and Nat Pryce**
- Mock objects in practice
- Integration of mocking into TDD workflow
- Outside-in development with mocks

### 7.2 Tool Documentation

**Jest (JavaScript/TypeScript)**:
- Mock Functions: https://jestjs.io/docs/mock-functions
- Manual Mocks: https://jestjs.io/docs/manual-mocks
- ES6 Class Mocks: https://jestjs.io/docs/es6-class-mocks

**Mockito (Java)**:
- Official Documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- Mockito Tutorial: https://site.mockito.org/
- Mockito Best Practices: https://github.com/mockito/mockito/wiki

### 7.3 Testing Standards

**ISTQB Test Automation Engineer**:
- Test double strategies in automation frameworks
- Certification: https://www.istqb.org/certifications/test-automation-engineer

**ISTQB Advanced Test Automation Engineer**:
- Advanced mocking and stubbing techniques
- Test isolation strategies

### 7.4 Community Resources

**Test Double Blog**:
- https://blog.testdouble.com/
- Modern testing practices and patterns

**Stack Overflow**:
- Jest Mocking: https://stackoverflow.com/questions/tagged/jestjs+mocking
- Mockito: https://stackoverflow.com/questions/tagged/mockito

---

## 8. Summary

### 8.1 Key Takeaways

**Test Double Hierarchy**:
1. **Dummy**: Simplest - never used, just fills parameter lists
2. **Stub**: Simple - returns canned responses
3. **Fake**: Complex - working implementation with shortcuts
4. **Mock**: Verification - records and verifies interactions
5. **Spy**: Hybrid - wraps real objects with verification

**Selection Principles**:
- Use **Dummies** for unused parameters
- Use **Stubs** for queries (data retrieval)
- Use **Fakes** for complex stateful dependencies
- Use **Mocks** for commands (side effects)
- Use **Spies** for partial mocking (use sparingly)

**Best Practices**:
- Prefer stubs over mocks for simpler, less brittle tests
- Mock commands, stub queries (CQS principle)
- Avoid over-mocking (don't mock everything)
- Fakes should maintain behavioral equivalence
- Keep test doubles simple and focused
- Document when mixing real and test double behavior

### 8.2 Job Seeker Application Patterns

**Common Patterns in Job Seeker Domain**:

| **Component** | **Type** | **Recommended Double** | **Reason** |
|--------------|----------|----------------------|------------|
| Email Service | Command | Mock | Verify notifications sent |
| Job Repository | Query/Command | Stub (queries), Mock (saves) | CQS separation |
| Scoring Engine | Query | Stub | Returns calculated scores |
| File Storage | Command | Fake or Mock | Stateful vs verification |
| Analytics Tracker | Command | Mock | Verify tracking calls |
| Cache Service | Query/Command | Fake | Complex stateful behavior |
| External API | Query | Stub | Controlled responses |
| Notification Queue | Command | Mock | Verify enqueue operations |

### 8.3 Final Recommendations

**For Unit Tests**:
- Start with stubs and mocks
- Use fakes sparingly (integration test territory)
- Avoid spies unless testing legacy code
- Keep tests focused on single responsibility

**For Integration Tests**:
- Prefer fakes over mocks for realistic behavior
- Use test containers for databases when possible
- Mock external services (third-party APIs)

**For Test Maintainability**:
- Create reusable test double factories
- Document test double behavior clearly
- Refactor tests when doubles become too complex
- Consider if complex mocking indicates design issues

**Remember**: The goal of test doubles is to enable fast, reliable, isolated tests. Choose the simplest double that meets your testing needs.

---

*Document Version: 1.0*
*Last Updated: 2025-10-04*
*Domain: Quality Engineering - Test Doubles*
