# Domain IX: Test Data and Environment Management

## Overview

Test Data and Environment Management are critical components of a comprehensive quality engineering strategy. Effective management of test data ensures realistic testing scenarios while maintaining data privacy and security. Robust environment management provides stable, reproducible testing platforms that mirror production conditions. This document explores industry best practices, tools, and strategies aligned with ISTQB principles and modern DevOps practices.

## 1. Test Data Management

### 1.1 Types of Test Data

**Classification by Source:**

1. **Production Data**
   - Real data from production systems
   - Highest fidelity to real-world scenarios
   - Requires masking/anonymization for privacy compliance
   - Risk: Contains sensitive PII, business confidential information

2. **Synthetic Data**
   - Artificially generated data that mimics production patterns
   - No privacy concerns
   - Full control over edge cases and boundary conditions
   - Challenge: May miss real-world complexity and correlation patterns

3. **Masked/Obfuscated Data**
   - Production data with sensitive fields transformed
   - Maintains referential integrity and data relationships
   - Balances realism with privacy compliance
   - Common techniques: substitution, shuffling, encryption, nulling

4. **Subset Data**
   - Carefully selected portions of production data
   - Reduced volume for faster test execution
   - Must maintain data relationships and referential integrity
   - Useful for component and integration testing

**Classification by Usage:**

```yaml
test_data_types:
  valid_data:
    description: "Data that should be accepted by the system"
    purpose: "Positive testing scenarios"
    examples:
      - "Valid email format: user@example.com"
      - "Valid phone: +1-555-123-4567"
      - "Valid date of birth within acceptable range"

  invalid_data:
    description: "Data that should be rejected by the system"
    purpose: "Negative testing scenarios"
    examples:
      - "Invalid email: user@invalid"
      - "SQL injection attempts: '; DROP TABLE users--"
      - "XSS attempts: <script>alert('xss')</script>"

  boundary_data:
    description: "Data at the edges of valid ranges"
    purpose: "Boundary value analysis"
    examples:
      - "Minimum age: 18 years"
      - "Maximum resume size: 5MB"
      - "Character limits: 255 chars for job title"

  edge_case_data:
    description: "Unusual but valid data scenarios"
    purpose: "Edge case and robustness testing"
    examples:
      - "Special characters in names: O'Brien, José"
      - "Very long valid inputs"
      - "Concurrent operations on same data"
```

### 1.2 Test Data Generation Strategies

**ISTQB Test Data Preparation Principles:**
- Test data should be prepared before test execution begins
- Data should support both positive and negative test scenarios
- Data must be maintained and version controlled
- Data should be environment-specific but consistent across test runs

**Job Seeker Application - Candidate Profile Data Generation:**

```javascript
// Using Faker.js for synthetic candidate data
const { faker } = require('@faker-js/faker');

class CandidateDataGenerator {
  /**
   * Generate realistic candidate profile with controlled randomness
   * Ensures data variations while maintaining referential integrity
   */
  static generateCandidate(options = {}) {
    const firstName = options.firstName || faker.person.firstName();
    const lastName = options.lastName || faker.person.lastName();
    const email = options.email ||
      `${firstName.toLowerCase()}.${lastName.toLowerCase()}@${faker.internet.domainName()}`;

    return {
      // Personal Information
      personalInfo: {
        firstName,
        lastName,
        email,
        phone: faker.phone.number('+1-###-###-####'),
        dateOfBirth: faker.date.birthdate({ min: 18, max: 65, mode: 'age' }),
        location: {
          city: faker.location.city(),
          state: faker.location.state({ abbreviated: true }),
          zipCode: faker.location.zipCode(),
          country: 'USA'
        }
      },

      // Professional Information
      professionalInfo: {
        currentTitle: faker.person.jobTitle(),
        yearsExperience: faker.number.int({ min: 0, max: 40 }),
        desiredSalary: faker.number.int({ min: 40000, max: 200000, multipleOf: 5000 }),
        willingToRelocate: faker.datatype.boolean(),

        skills: this.generateSkills(options.skillLevel || 'intermediate'),

        experience: this.generateWorkHistory(
          faker.number.int({ min: 1, max: 5 })
        ),

        education: this.generateEducation()
      },

      // Application Metadata
      metadata: {
        profileCompleteness: faker.number.int({ min: 40, max: 100 }),
        registrationDate: faker.date.past({ years: 2 }),
        lastLoginDate: faker.date.recent({ days: 30 }),
        profileStatus: faker.helpers.arrayElement(['active', 'inactive', 'suspended'])
      }
    };
  }

  static generateSkills(level) {
    const skillCategories = {
      technical: ['JavaScript', 'Python', 'Java', 'SQL', 'React', 'Node.js', 'AWS', 'Docker'],
      soft: ['Communication', 'Leadership', 'Problem Solving', 'Team Collaboration'],
      domain: ['Agile', 'Project Management', 'Data Analysis', 'Quality Assurance']
    };

    const skillCount = level === 'senior' ? 12 : level === 'intermediate' ? 8 : 5;
    const skills = [];

    Object.entries(skillCategories).forEach(([category, skillList]) => {
      const categorySkills = faker.helpers.arrayElements(
        skillList,
        faker.number.int({ min: 1, max: 4 })
      );

      categorySkills.forEach(skill => {
        skills.push({
          name: skill,
          category,
          proficiencyLevel: faker.helpers.arrayElement(['beginner', 'intermediate', 'advanced', 'expert']),
          yearsOfExperience: faker.number.int({ min: 0, max: 15 })
        });
      });
    });

    return skills.slice(0, skillCount);
  }

  static generateWorkHistory(count) {
    const experiences = [];
    let currentDate = new Date();

    for (let i = 0; i < count; i++) {
      const startDate = faker.date.past({ years: 10, refDate: currentDate });
      const endDate = i === 0 ? null : // Current job
        faker.date.between({ from: startDate, to: currentDate });

      experiences.push({
        companyName: faker.company.name(),
        jobTitle: faker.person.jobTitle(),
        startDate,
        endDate,
        isCurrent: i === 0,
        responsibilities: faker.helpers.arrayElements(
          [
            'Led development team of 5-10 engineers',
            'Implemented CI/CD pipelines',
            'Reduced bug count by 40%',
            'Mentored junior developers',
            'Architected microservices infrastructure'
          ],
          faker.number.int({ min: 2, max: 5 })
        ),
        achievements: faker.lorem.sentences(2)
      });

      currentDate = startDate;
    }

    return experiences;
  }

  static generateEducation() {
    return [
      {
        institution: faker.company.name() + ' University',
        degree: faker.helpers.arrayElement([
          'Bachelor of Science',
          'Master of Science',
          'Bachelor of Arts',
          'Associate Degree'
        ]),
        major: faker.helpers.arrayElement([
          'Computer Science',
          'Software Engineering',
          'Information Technology',
          'Business Administration'
        ]),
        graduationYear: faker.date.past({ years: 20 }).getFullYear(),
        gpa: faker.number.float({ min: 2.5, max: 4.0, precision: 0.01 })
      }
    ];
  }

  /**
   * Generate edge case candidates for boundary testing
   */
  static generateEdgeCaseCandidate(edgeCase) {
    const baseCandiddate = this.generateCandidate();

    const edgeCases = {
      minimumAge: {
        ...baseCandiddate,
        personalInfo: {
          ...baseCandiddate.personalInfo,
          dateOfBirth: new Date(new Date().setFullYear(new Date().getFullYear() - 18))
        }
      },

      maximumExperience: {
        ...baseCandiddate,
        professionalInfo: {
          ...baseCandiddate.professionalInfo,
          yearsExperience: 50,
          experience: this.generateWorkHistory(10)
        }
      },

      specialCharactersName: {
        ...baseCandiddate,
        personalInfo: {
          ...baseCandiddate.personalInfo,
          firstName: "François-José",
          lastName: "O'Brien-González"
        }
      },

      maximumSkills: {
        ...baseCandiddate,
        professionalInfo: {
          ...baseCandiddate.professionalInfo,
          skills: this.generateSkills('expert').concat(this.generateSkills('expert'))
        }
      },

      incompleteProfile: {
        personalInfo: {
          firstName: faker.person.firstName(),
          lastName: faker.person.lastName(),
          email: faker.internet.email()
        },
        metadata: {
          profileCompleteness: 15
        }
      }
    };

    return edgeCases[edgeCase] || baseCandiddate;
  }
}

module.exports = CandidateDataGenerator;
```

**Job Seeker Application - Job Posting Data Generation:**

```javascript
class JobPostingDataGenerator {
  static generateJobPosting(options = {}) {
    const title = options.title || faker.person.jobTitle();
    const companyName = options.companyName || faker.company.name();

    return {
      jobDetails: {
        title,
        companyName,
        companyId: options.companyId || faker.string.uuid(),
        department: faker.commerce.department(),
        employmentType: faker.helpers.arrayElement([
          'Full-time',
          'Part-time',
          'Contract',
          'Temporary',
          'Internship'
        ]),
        experienceLevel: faker.helpers.arrayElement([
          'Entry Level',
          'Mid Level',
          'Senior Level',
          'Executive'
        ]),

        location: {
          city: faker.location.city(),
          state: faker.location.state({ abbreviated: true }),
          remote: faker.datatype.boolean(),
          hybrid: faker.datatype.boolean()
        },

        compensation: {
          salaryMin: faker.number.int({ min: 40000, max: 100000, multipleOf: 5000 }),
          salaryMax: faker.number.int({ min: 100000, max: 250000, multipleOf: 5000 }),
          currency: 'USD',
          period: 'annually',
          benefits: faker.helpers.arrayElements([
            'Health Insurance',
            '401(k) Matching',
            'Flexible PTO',
            'Remote Work',
            'Professional Development',
            'Stock Options'
          ], faker.number.int({ min: 2, max: 6 }))
        }
      },

      requirements: {
        description: faker.lorem.paragraphs(3),
        responsibilities: Array.from(
          { length: faker.number.int({ min: 5, max: 10 }) },
          () => faker.lorem.sentence()
        ),
        requiredSkills: this.generateRequiredSkills(),
        preferredSkills: this.generatePreferredSkills(),
        educationLevel: faker.helpers.arrayElement([
          'High School',
          'Associate Degree',
          'Bachelor\'s Degree',
          'Master\'s Degree',
          'PhD'
        ]),
        yearsExperienceMin: faker.number.int({ min: 0, max: 3 }),
        yearsExperienceMax: faker.number.int({ min: 3, max: 15 })
      },

      applicationSettings: {
        applicationDeadline: faker.date.future({ years: 0.25 }),
        maxApplications: faker.number.int({ min: 50, max: 500 }),
        currentApplicationCount: 0,
        acceptingApplications: true,
        requireCoverLetter: faker.datatype.boolean(),
        requireResume: true,
        screeningQuestions: this.generateScreeningQuestions()
      },

      metadata: {
        postingId: faker.string.uuid(),
        postedDate: faker.date.recent({ days: 30 }),
        status: faker.helpers.arrayElement(['draft', 'active', 'closed', 'filled']),
        views: faker.number.int({ min: 0, max: 5000 }),
        applications: faker.number.int({ min: 0, max: 200 })
      }
    };
  }

  static generateRequiredSkills() {
    return Array.from(
      { length: faker.number.int({ min: 3, max: 8 }) },
      () => ({
        skill: faker.helpers.arrayElement([
          'JavaScript', 'Python', 'Java', 'SQL', 'React', 'Node.js',
          'AWS', 'Docker', 'Kubernetes', 'Git', 'REST APIs', 'Agile'
        ]),
        minimumYears: faker.number.int({ min: 1, max: 5 }),
        required: true
      })
    );
  }

  static generatePreferredSkills() {
    return Array.from(
      { length: faker.number.int({ min: 2, max: 5 }) },
      () => ({
        skill: faker.helpers.arrayElement([
          'GraphQL', 'TypeScript', 'MongoDB', 'Redis', 'CI/CD',
          'Microservices', 'Machine Learning', 'Data Analysis'
        ]),
        required: false
      })
    );
  }

  static generateScreeningQuestions() {
    return [
      {
        question: 'How many years of experience do you have with the required technologies?',
        type: 'number',
        required: true,
        minValue: 0,
        maxValue: 50
      },
      {
        question: 'Are you authorized to work in the United States?',
        type: 'boolean',
        required: true
      },
      {
        question: 'What is your expected salary range?',
        type: 'text',
        required: false
      }
    ];
  }
}

module.exports = JobPostingDataGenerator;
```

**Job Application Data Generation:**

```javascript
class ApplicationDataGenerator {
  /**
   * Generate complete application with candidate-job relationships
   */
  static generateApplication(candidateId, jobPostingId, options = {}) {
    const applicationDate = options.applicationDate || faker.date.recent({ days: 30 });

    return {
      applicationId: faker.string.uuid(),
      candidateId,
      jobPostingId,

      applicationData: {
        submittedDate: applicationDate,
        lastUpdated: faker.date.between({
          from: applicationDate,
          to: new Date()
        }),

        status: options.status || faker.helpers.arrayElement([
          'submitted',
          'under_review',
          'interview_scheduled',
          'interviewed',
          'offer_extended',
          'accepted',
          'rejected',
          'withdrawn'
        ]),

        documents: {
          resume: {
            fileName: `resume_${faker.string.alphanumeric(8)}.pdf`,
            uploadDate: applicationDate,
            fileSize: faker.number.int({ min: 50000, max: 5000000 }), // bytes
            virusScanStatus: 'clean',
            parsed: true
          },
          coverLetter: faker.datatype.boolean() ? {
            fileName: `cover_letter_${faker.string.alphanumeric(8)}.pdf`,
            uploadDate: applicationDate,
            fileSize: faker.number.int({ min: 20000, max: 1000000 })
          } : null,
          portfolio: faker.datatype.boolean(0.3) ? {
            url: faker.internet.url()
          } : null
        },

        screeningAnswers: this.generateScreeningAnswers(),

        matchScore: options.matchScore || faker.number.int({ min: 40, max: 100 }),

        timeline: this.generateApplicationTimeline(applicationDate)
      },

      recruiterNotes: options.includeNotes ? Array.from(
        { length: faker.number.int({ min: 0, max: 5 }) },
        () => ({
          note: faker.lorem.sentences(2),
          author: faker.person.fullName(),
          timestamp: faker.date.between({ from: applicationDate, to: new Date() })
        })
      ) : []
    };
  }

  static generateScreeningAnswers() {
    return [
      {
        question: 'How many years of experience do you have with the required technologies?',
        answer: faker.number.int({ min: 0, max: 20 })
      },
      {
        question: 'Are you authorized to work in the United States?',
        answer: faker.datatype.boolean(0.9) // 90% yes
      },
      {
        question: 'What is your expected salary range?',
        answer: `$${faker.number.int({ min: 60000, max: 180000, multipleOf: 5000 })}`
      }
    ];
  }

  static generateApplicationTimeline(startDate) {
    return [
      {
        event: 'application_submitted',
        timestamp: startDate,
        details: 'Application successfully submitted'
      },
      {
        event: 'application_viewed',
        timestamp: faker.date.between({ from: startDate, to: new Date() }),
        details: 'Application viewed by recruiter'
      }
    ];
  }

  /**
   * Generate bulk applications for load testing
   */
  static generateBulkApplications(jobPostingId, count = 100) {
    return Array.from({ length: count }, () => {
      const candidateId = faker.string.uuid();
      return this.generateApplication(candidateId, jobPostingId);
    });
  }
}

module.exports = ApplicationDataGenerator;
```

### 1.3 Data Masking and Privacy

**GDPR and Privacy Compliance Considerations:**

According to GDPR Article 5, personal data must be:
- Processed lawfully, fairly, and transparently
- Collected for specified, explicit, and legitimate purposes
- Adequate, relevant, and limited to what is necessary
- Accurate and kept up to date
- Kept in a form that permits identification for no longer than necessary
- Processed in a manner that ensures appropriate security

**Data Classification for Job Seeker Application:**

```javascript
const dataClassification = {
  // PII - Personally Identifiable Information (GDPR Protected)
  pii: [
    'email',
    'phone',
    'firstName',
    'lastName',
    'dateOfBirth',
    'address',
    'socialSecurityNumber',
    'governmentId'
  ],

  // Sensitive Personal Data (GDPR Article 9 - Special Categories)
  sensitiveData: [
    'ethnicity',
    'gender',
    'religion',
    'healthInformation',
    'criminalHistory'
  ],

  // Business Confidential
  confidential: [
    'salaryExpectations',
    'currentSalary',
    'companyName', // in work history
    'negotiationDetails'
  ],

  // Public/Non-sensitive
  public: [
    'skills',
    'jobTitle',
    'yearsOfExperience',
    'educationLevel',
    'certifications'
  ]
};
```

**Data Masking Strategies:**

```javascript
const crypto = require('crypto');

class DataMasker {
  /**
   * Deterministic masking - same input always produces same output
   * Preserves referential integrity across related tables
   */
  static deterministicHash(value, salt = 'test-env-salt') {
    return crypto
      .createHash('sha256')
      .update(value + salt)
      .digest('hex')
      .substring(0, 16);
  }

  /**
   * Email masking - preserves format and domain for testing
   */
  static maskEmail(email) {
    const [localPart, domain] = email.split('@');
    const hashedLocal = this.deterministicHash(localPart);

    // Option 1: Hash local part, keep domain
    return `masked_${hashedLocal.substring(0, 8)}@${domain}`;

    // Option 2: Use generic test domain
    // return `user_${hashedLocal.substring(0, 8)}@test.example.com`;
  }

  /**
   * Phone number masking - preserves format
   */
  static maskPhone(phone) {
    // Keep country code and format, mask middle digits
    const cleaned = phone.replace(/\D/g, '');
    const countryCode = cleaned.substring(0, 1);
    const areaCode = cleaned.substring(1, 4);
    const masked = 'XXX-XXXX';

    return `+${countryCode}-${areaCode}-${masked}`;
  }

  /**
   * Name masking - preserves first letter and length
   */
  static maskName(name) {
    if (!name || name.length === 0) return name;
    return name[0] + '*'.repeat(name.length - 1);
  }

  /**
   * Date masking - preserves year for age calculations
   */
  static maskDateOfBirth(date) {
    const d = new Date(date);
    return new Date(d.getFullYear(), 0, 1); // Jan 1st of birth year
  }

  /**
   * Salary masking - preserves range
   */
  static maskSalary(salary) {
    // Round to nearest 10k
    return Math.round(salary / 10000) * 10000;
  }

  /**
   * Complete candidate profile masking
   */
  static maskCandidateProfile(candidate) {
    return {
      ...candidate,
      personalInfo: {
        ...candidate.personalInfo,
        firstName: this.maskName(candidate.personalInfo.firstName),
        lastName: this.maskName(candidate.personalInfo.lastName),
        email: this.maskEmail(candidate.personalInfo.email),
        phone: this.maskPhone(candidate.personalInfo.phone),
        dateOfBirth: this.maskDateOfBirth(candidate.personalInfo.dateOfBirth),
        location: {
          ...candidate.personalInfo.location,
          // Keep city/state for location-based testing
          zipCode: 'XXXXX'
        }
      },
      professionalInfo: {
        ...candidate.professionalInfo,
        desiredSalary: this.maskSalary(candidate.professionalInfo.desiredSalary),
        experience: candidate.professionalInfo.experience.map(exp => ({
          ...exp,
          companyName: `Company_${this.deterministicHash(exp.companyName).substring(0, 6)}`
        }))
      }
    };
  }

  /**
   * Null/redact sensitive fields - for environments with strict compliance
   */
  static redactSensitiveData(candidate) {
    return {
      ...candidate,
      personalInfo: {
        ...candidate.personalInfo,
        phone: null,
        dateOfBirth: null,
        email: `user_${candidate.metadata?.candidateId?.substring(0, 8)}@test.example.com`
      },
      professionalInfo: {
        ...candidate.professionalInfo,
        desiredSalary: null,
        experience: candidate.professionalInfo.experience.map(exp => ({
          ...exp,
          companyName: '[REDACTED]'
        }))
      }
    };
  }
}

module.exports = DataMasker;
```

**Production Data Sanitization Pipeline:**

```javascript
/**
 * Automated pipeline for creating test data from production snapshots
 * Run nightly or on-demand to refresh test environments
 */
class ProductionDataSanitizer {
  constructor(sourceDb, targetDb, config) {
    this.sourceDb = sourceDb;
    this.targetDb = targetDb;
    this.config = config;
  }

  async sanitizeAndCopy() {
    console.log('Starting data sanitization pipeline...');

    try {
      // 1. Extract subset of production data
      const candidates = await this.extractCandidateSubset();
      const jobPostings = await this.extractJobPostingSubset();
      const applications = await this.extractApplicationSubset();

      // 2. Apply masking rules
      const maskedCandidates = candidates.map(c => DataMasker.maskCandidateProfile(c));
      const maskedApplications = applications.map(a => this.maskApplication(a));

      // 3. Validate referential integrity
      await this.validateReferentialIntegrity(
        maskedCandidates,
        jobPostings,
        maskedApplications
      );

      // 4. Load into test environment
      await this.loadIntoTestEnvironment({
        candidates: maskedCandidates,
        jobPostings,
        applications: maskedApplications
      });

      // 5. Verify data quality
      await this.verifyDataQuality();

      console.log('Data sanitization completed successfully');

    } catch (error) {
      console.error('Data sanitization failed:', error);
      throw error;
    }
  }

  async extractCandidateSubset() {
    // Extract recent, active candidates with varied profiles
    return await this.sourceDb.candidates.find({
      registrationDate: { $gte: new Date(Date.now() - 90 * 24 * 60 * 60 * 1000) },
      profileStatus: 'active'
    }).limit(this.config.candidateLimit || 1000).toArray();
  }

  async extractJobPostingSubset() {
    // Extract active and recent job postings
    return await this.sourceDb.jobPostings.find({
      status: { $in: ['active', 'filled'] },
      postedDate: { $gte: new Date(Date.now() - 60 * 24 * 60 * 60 * 1000) }
    }).limit(this.config.jobPostingLimit || 200).toArray();
  }

  async extractApplicationSubset() {
    // Extract applications related to selected candidates and jobs
    return await this.sourceDb.applications.find({
      submittedDate: { $gte: new Date(Date.now() - 60 * 24 * 60 * 60 * 1000) }
    }).limit(this.config.applicationLimit || 5000).toArray();
  }

  maskApplication(application) {
    return {
      ...application,
      documents: {
        ...application.documents,
        resume: {
          ...application.documents.resume,
          // Replace with sanitized test document
          fileName: `sanitized_resume_${application.applicationId}.pdf`,
          content: null // Remove actual content
        },
        coverLetter: application.documents.coverLetter ? {
          ...application.documents.coverLetter,
          fileName: `sanitized_cover_${application.applicationId}.pdf`,
          content: null
        } : null
      },
      recruiterNotes: [] // Remove all notes containing potential PII
    };
  }

  async validateReferentialIntegrity(candidates, jobPostings, applications) {
    const candidateIds = new Set(candidates.map(c => c.metadata.candidateId));
    const jobPostingIds = new Set(jobPostings.map(j => j.metadata.postingId));

    // Ensure all applications reference valid candidates and jobs
    const orphanedApplications = applications.filter(app =>
      !candidateIds.has(app.candidateId) || !jobPostingIds.has(app.jobPostingId)
    );

    if (orphanedApplications.length > 0) {
      throw new Error(
        `Found ${orphanedApplications.length} applications with invalid references`
      );
    }
  }

  async loadIntoTestEnvironment(data) {
    // Clear existing test data
    await this.targetDb.candidates.deleteMany({});
    await this.targetDb.jobPostings.deleteMany({});
    await this.targetDb.applications.deleteMany({});

    // Insert sanitized data
    await this.targetDb.candidates.insertMany(data.candidates);
    await this.targetDb.jobPostings.insertMany(data.jobPostings);
    await this.targetDb.applications.insertMany(data.applications);
  }

  async verifyDataQuality() {
    // Verify counts
    const candidateCount = await this.targetDb.candidates.countDocuments();
    const jobCount = await this.targetDb.jobPostings.countDocuments();
    const appCount = await this.targetDb.applications.countDocuments();

    console.log(`Loaded: ${candidateCount} candidates, ${jobCount} jobs, ${appCount} applications`);

    // Verify no PII leaked
    const unmaskedEmails = await this.targetDb.candidates.find({
      'personalInfo.email': { $not: /^(masked_|user_)/ }
    }).toArray();

    if (unmaskedEmails.length > 0) {
      throw new Error('Found unmasked email addresses in test data!');
    }
  }
}

module.exports = ProductionDataSanitizer;
```

### 1.4 Test Data Lifecycle

**ISTQB Test Data Management Process:**

1. **Planning**: Identify data requirements based on test design
2. **Preparation**: Generate, extract, or mask data
3. **Storage**: Securely store and version control test data
4. **Provisioning**: Load data into test environments
5. **Maintenance**: Update data as application evolves
6. **Retirement**: Securely delete obsolete test data

**Test Data Lifecycle Implementation:**

```yaml
test_data_lifecycle:
  planning:
    activities:
      - "Analyze test scenarios and identify data needs"
      - "Determine data volumes for performance testing"
      - "Identify edge cases and boundary conditions"
      - "Plan data relationships and dependencies"
    outputs:
      - "Test data requirements specification"
      - "Data generation strategy"
      - "Privacy compliance checklist"

  preparation:
    synthetic_generation:
      tools: ["Faker.js", "Chance.js", "TestDataBuilder pattern"]
      advantages:
        - "No privacy concerns"
        - "Unlimited volume"
        - "Full control over variations"
      disadvantages:
        - "May miss real-world complexity"
        - "Requires maintenance as schema changes"

    production_masking:
      tools: ["DataMasker", "AWS Data Pipeline", "Delphix"]
      advantages:
        - "Realistic data patterns"
        - "Real-world correlations"
        - "Complex edge cases included"
      disadvantages:
        - "Privacy compliance overhead"
        - "Masking can break functionality"
        - "Requires production access"

  storage:
    version_control:
      location: "test-data/ directory in repository"
      formats: ["JSON", "YAML", "SQL seed files"]
      versioning: "Tagged with application version"

    security:
      - "Encrypt test data at rest"
      - "No production data in repositories"
      - "Access control for masked production data"
      - "Audit trail for data access"

  provisioning:
    approaches:
      - "Database seeding scripts"
      - "API-based data creation"
      - "Database snapshots/cloning"
      - "Container initialization"

    timing:
      - "Before test suite execution"
      - "Per test class/suite setup"
      - "Per test case (isolated data)"

  maintenance:
    triggers:
      - "Schema changes in application"
      - "New test scenarios added"
      - "Data quality issues discovered"
      - "Compliance requirements updated"

    activities:
      - "Update data generators"
      - "Refresh masked production data"
      - "Add new edge case data"
      - "Remove obsolete data sets"

  retirement:
    criteria:
      - "Test scenarios deprecated"
      - "Application features removed"
      - "Data schema significantly changed"
      - "Compliance retention period exceeded"

    process:
      - "Archive data for audit purposes"
      - "Securely delete from active environments"
      - "Document deletion for compliance"
      - "Update test data catalog"
```

## 2. Database Seeding Strategies

### 2.1 Seeding Approaches

**Sequential vs. Random Seeding:**

```javascript
// Sequential seeding - deterministic, reproducible
class SequentialSeeder {
  static async seed(database) {
    console.log('Starting sequential database seeding...');

    // 1. Seed lookup/reference data first
    await this.seedReferenceData(database);

    // 2. Seed companies
    const companies = await this.seedCompanies(database, 50);

    // 3. Seed job postings (dependent on companies)
    const jobPostings = await this.seedJobPostings(database, companies, 200);

    // 4. Seed candidates
    const candidates = await this.seedCandidates(database, 1000);

    // 5. Seed applications (dependent on candidates and jobs)
    await this.seedApplications(database, candidates, jobPostings, 3000);

    console.log('Sequential seeding completed');
  }

  static async seedReferenceData(database) {
    const skills = [
      'JavaScript', 'Python', 'Java', 'C#', 'SQL', 'React', 'Angular',
      'Node.js', 'Docker', 'Kubernetes', 'AWS', 'Azure', 'Git'
    ].map(name => ({ name, category: 'technical' }));

    const industries = [
      'Technology', 'Finance', 'Healthcare', 'Education', 'Retail',
      'Manufacturing', 'Consulting', 'Entertainment'
    ].map(name => ({ name }));

    await database.skills.insertMany(skills);
    await database.industries.insertMany(industries);
  }

  static async seedCompanies(database, count) {
    const companies = Array.from({ length: count }, (_, i) => ({
      companyId: `company_${String(i + 1).padStart(5, '0')}`,
      name: `Company ${i + 1}`,
      industry: faker.helpers.arrayElement(['Technology', 'Finance', 'Healthcare']),
      size: faker.helpers.arrayElement(['1-10', '11-50', '51-200', '201-500', '500+']),
      createdAt: new Date()
    }));

    await database.companies.insertMany(companies);
    return companies;
  }

  static async seedJobPostings(database, companies, count) {
    const jobPostings = Array.from({ length: count }, (_, i) => {
      const company = companies[i % companies.length];
      return JobPostingDataGenerator.generateJobPosting({
        companyId: company.companyId,
        companyName: company.name
      });
    });

    await database.jobPostings.insertMany(jobPostings);
    return jobPostings;
  }

  static async seedCandidates(database, count) {
    const candidates = Array.from({ length: count }, () =>
      CandidateDataGenerator.generateCandidate()
    );

    await database.candidates.insertMany(candidates);
    return candidates;
  }

  static async seedApplications(database, candidates, jobPostings, count) {
    const applications = Array.from({ length: count }, () => {
      const candidate = faker.helpers.arrayElement(candidates);
      const jobPosting = faker.helpers.arrayElement(jobPostings);

      return ApplicationDataGenerator.generateApplication(
        candidate.personalInfo.email,
        jobPosting.metadata.postingId
      );
    });

    await database.applications.insertMany(applications);
  }
}
```

**Factory Pattern for Test Data:**

```javascript
/**
 * Builder pattern for creating test data with fluent API
 * Allows flexible, readable test data creation
 */
class CandidateBuilder {
  constructor() {
    this.candidate = CandidateDataGenerator.generateCandidate();
  }

  withName(firstName, lastName) {
    this.candidate.personalInfo.firstName = firstName;
    this.candidate.personalInfo.lastName = lastName;
    this.candidate.personalInfo.email =
      `${firstName.toLowerCase()}.${lastName.toLowerCase()}@example.com`;
    return this;
  }

  withEmail(email) {
    this.candidate.personalInfo.email = email;
    return this;
  }

  withExperience(years) {
    this.candidate.professionalInfo.yearsExperience = years;
    return this;
  }

  withSkills(skills) {
    this.candidate.professionalInfo.skills = skills.map(skill => ({
      name: skill,
      category: 'technical',
      proficiencyLevel: 'intermediate',
      yearsOfExperience: 3
    }));
    return this;
  }

  withIncompleteProfile() {
    this.candidate.metadata.profileCompleteness = 25;
    this.candidate.professionalInfo.experience = [];
    return this;
  }

  withStatus(status) {
    this.candidate.metadata.profileStatus = status;
    return this;
  }

  build() {
    return this.candidate;
  }
}

// Usage in tests
describe('Candidate Profile Tests', () => {
  test('should validate senior engineer profile', () => {
    const candidate = new CandidateBuilder()
      .withName('Alice', 'Engineer')
      .withExperience(10)
      .withSkills(['JavaScript', 'React', 'Node.js', 'AWS'])
      .build();

    expect(candidate.professionalInfo.yearsExperience).toBe(10);
    expect(candidate.professionalInfo.skills).toHaveLength(4);
  });

  test('should reject incomplete profiles', () => {
    const candidate = new CandidateBuilder()
      .withIncompleteProfile()
      .build();

    expect(candidate.metadata.profileCompleteness).toBeLessThan(30);
  });
});
```

### 2.2 Data Fixtures and Snapshots

**Fixed Test Data for Regression Testing:**

```javascript
// test-fixtures/candidates.json
const candidateFixtures = {
  seniorEngineer: {
    personalInfo: {
      firstName: "Alice",
      lastName: "Senior",
      email: "alice.senior@example.com",
      phone: "+1-555-100-0001",
      dateOfBirth: "1985-05-15",
      location: {
        city: "San Francisco",
        state: "CA",
        zipCode: "94102",
        country: "USA"
      }
    },
    professionalInfo: {
      currentTitle: "Senior Software Engineer",
      yearsExperience: 10,
      desiredSalary: 150000,
      skills: [
        { name: "JavaScript", proficiencyLevel: "expert", yearsOfExperience: 10 },
        { name: "React", proficiencyLevel: "expert", yearsOfExperience: 6 },
        { name: "Node.js", proficiencyLevel: "advanced", yearsOfExperience: 8 }
      ]
    },
    metadata: {
      profileCompleteness: 100,
      profileStatus: "active"
    }
  },

  juniorDeveloper: {
    personalInfo: {
      firstName: "Bob",
      lastName: "Junior",
      email: "bob.junior@example.com",
      phone: "+1-555-100-0002",
      dateOfBirth: "1998-08-20",
      location: {
        city: "Austin",
        state: "TX",
        zipCode: "78701",
        country: "USA"
      }
    },
    professionalInfo: {
      currentTitle: "Junior Developer",
      yearsExperience: 2,
      desiredSalary: 75000,
      skills: [
        { name: "JavaScript", proficiencyLevel: "intermediate", yearsOfExperience: 2 },
        { name: "HTML", proficiencyLevel: "advanced", yearsOfExperience: 2 }
      ]
    },
    metadata: {
      profileCompleteness: 85,
      profileStatus: "active"
    }
  },

  incompleteProfile: {
    personalInfo: {
      firstName: "Charlie",
      lastName: "Incomplete",
      email: "charlie.incomplete@example.com"
    },
    metadata: {
      profileCompleteness: 20,
      profileStatus: "active"
    }
  }
};

module.exports = candidateFixtures;
```

**Database Snapshot Management:**

```javascript
const { exec } = require('child_process');
const util = require('util');
const execPromise = util.promisify(exec);

class DatabaseSnapshotManager {
  constructor(dbConfig) {
    this.dbConfig = dbConfig;
    this.snapshotDir = './test-snapshots';
  }

  /**
   * Create a snapshot of current database state
   */
  async createSnapshot(snapshotName) {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const filename = `${this.snapshotDir}/${snapshotName}_${timestamp}.sql`;

    // MongoDB example
    const cmd = `mongodump --uri="${this.dbConfig.uri}" --out="${filename}"`;

    try {
      await execPromise(cmd);
      console.log(`Snapshot created: ${filename}`);
      return filename;
    } catch (error) {
      console.error('Snapshot creation failed:', error);
      throw error;
    }
  }

  /**
   * Restore database from snapshot
   */
  async restoreSnapshot(snapshotPath) {
    const cmd = `mongorestore --uri="${this.dbConfig.uri}" --drop "${snapshotPath}"`;

    try {
      await execPromise(cmd);
      console.log(`Snapshot restored: ${snapshotPath}`);
    } catch (error) {
      console.error('Snapshot restore failed:', error);
      throw error;
    }
  }

  /**
   * Create a baseline snapshot for test suite
   */
  async createBaselineSnapshot() {
    // Seed database with known good state
    await SequentialSeeder.seed(this.dbConfig.db);

    // Create snapshot
    return await this.createSnapshot('baseline');
  }

  /**
   * Reset database to baseline before each test suite
   */
  async resetToBaseline() {
    const baselineSnapshot = `${this.snapshotDir}/baseline_latest.sql`;
    await this.restoreSnapshot(baselineSnapshot);
  }
}

// Usage in test setup
beforeAll(async () => {
  const snapshotManager = new DatabaseSnapshotManager(dbConfig);
  await snapshotManager.createBaselineSnapshot();
});

beforeEach(async () => {
  const snapshotManager = new DatabaseSnapshotManager(dbConfig);
  await snapshotManager.resetToBaseline();
});
```

## 3. Test Environment Management

### 3.1 Environment Types and Purposes

**ISTQB Test Environment Levels:**

```yaml
test_environments:
  local_development:
    purpose: "Individual developer testing and debugging"
    characteristics:
      - "Runs on developer workstation"
      - "Isolated data and configuration"
      - "Fast feedback loops"
      - "May use mocked external dependencies"
    infrastructure:
      - "Docker Compose for services"
      - "Local database instances"
      - "In-memory caches"
    data_strategy: "Synthetic data, minimal dataset"

  continuous_integration:
    purpose: "Automated testing on every commit"
    characteristics:
      - "Ephemeral environments"
      - "Parallel execution support"
      - "Consistent, reproducible state"
      - "Fast provisioning and teardown"
    infrastructure:
      - "Containerized services"
      - "In-memory databases when possible"
      - "Mocked external APIs"
    data_strategy: "Synthetic data, fixtures, database snapshots"

  integration_testing:
    purpose: "Test integration between components and services"
    characteristics:
      - "Longer-lived than CI environments"
      - "Real external service integrations"
      - "Shared test data"
      - "Mirrors production architecture"
    infrastructure:
      - "Kubernetes cluster"
      - "Real database instances"
      - "Message queues, caches"
      - "External service sandboxes"
    data_strategy: "Masked production data, synthetic data supplements"

  qa_staging:
    purpose: "Manual exploratory testing and UAT"
    characteristics:
      - "Production-like configuration"
      - "Stable for extended testing periods"
      - "Supports multiple test teams"
      - "Pre-release validation"
    infrastructure:
      - "Production-equivalent infrastructure"
      - "Full service mesh"
      - "Load balancers, CDN"
      - "Monitoring and logging"
    data_strategy: "Masked production data, refreshed weekly"

  performance_testing:
    purpose: "Load, stress, and scalability testing"
    characteristics:
      - "Matches production capacity"
      - "Isolated from other test environments"
      - "Supports high data volumes"
      - "Comprehensive monitoring"
    infrastructure:
      - "Production-scale infrastructure"
      - "Distributed load generators"
      - "APM tools installed"
      - "Network simulation capabilities"
    data_strategy: "Large-scale synthetic data, production data subset"

  pre_production:
    purpose: "Final validation before production deployment"
    characteristics:
      - "Identical to production"
      - "Blue/green deployment testing"
      - "Smoke and sanity testing"
      - "Deployment rehearsal"
    infrastructure:
      - "Exact production mirror"
      - "Same infrastructure as code"
      - "Same monitoring stack"
      - "Same security controls"
    data_strategy: "Recent masked production data"
```

### 3.2 Environment Provisioning and Configuration

**Infrastructure as Code with Docker:**

```dockerfile
# Dockerfile for Job Seeker Application Test Environment
FROM node:18-alpine

# Install dependencies
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

# Copy application code
COPY . .

# Environment-specific configuration
ARG ENV=test
ENV NODE_ENV=${ENV}

# Expose application port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD node healthcheck.js

# Start application
CMD ["npm", "start"]
```

**Docker Compose for Local Test Environment:**

```yaml
# docker-compose.test.yml
version: '3.8'

services:
  # Job Seeker Application
  job-seeker-app:
    build:
      context: .
      dockerfile: Dockerfile
      args:
        ENV: test
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=test
      - DATABASE_URL=postgresql://testuser:testpass@postgres:5432/jobseeker_test
      - REDIS_URL=redis://redis:6379
      - ELASTICSEARCH_URL=http://elasticsearch:9200
      - LOG_LEVEL=debug
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
      elasticsearch:
        condition: service_healthy
    volumes:
      - ./test-data:/app/test-data
    networks:
      - test-network

  # PostgreSQL Database
  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_USER=testuser
      - POSTGRES_PASSWORD=testpass
      - POSTGRES_DB=jobseeker_test
    ports:
      - "5432:5432"
    volumes:
      - postgres-test-data:/var/lib/postgresql/data
      - ./test-data/init-db.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U testuser"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - test-network

  # Redis Cache
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - test-network

  # Elasticsearch for Job Search
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.8.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch-test-data:/usr/share/elasticsearch/data
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5
    networks:
      - test-network

  # Email Service Mock (MailHog)
  mailhog:
    image: mailhog/mailhog:latest
    ports:
      - "1025:1025"  # SMTP
      - "8025:8025"  # Web UI
    networks:
      - test-network

  # AWS LocalStack for S3/SQS testing
  localstack:
    image: localstack/localstack:latest
    ports:
      - "4566:4566"  # LocalStack Gateway
    environment:
      - SERVICES=s3,sqs,sns
      - DEBUG=1
      - DATA_DIR=/tmp/localstack/data
    volumes:
      - localstack-data:/tmp/localstack
      - ./test-data/aws-init.sh:/docker-entrypoint-initaws.d/init.sh
    networks:
      - test-network

volumes:
  postgres-test-data:
  elasticsearch-test-data:
  localstack-data:

networks:
  test-network:
    driver: bridge
```

**TestContainers for Integration Tests:**

```javascript
const { GenericContainer, Wait } = require('testcontainers');
const { Client } = require('pg');

describe('Candidate Repository Integration Tests', () => {
  let postgresContainer;
  let postgresClient;

  beforeAll(async () => {
    // Start PostgreSQL container
    postgresContainer = await new GenericContainer('postgres:15-alpine')
      .withEnvironment({
        POSTGRES_USER: 'testuser',
        POSTGRES_PASSWORD: 'testpass',
        POSTGRES_DB: 'jobseeker_test'
      })
      .withExposedPorts(5432)
      .withWaitStrategy(Wait.forLogMessage(/database system is ready to accept connections/))
      .withStartupTimeout(120000)
      .start();

    // Connect to database
    const port = postgresContainer.getMappedPort(5432);
    postgresClient = new Client({
      host: 'localhost',
      port,
      user: 'testuser',
      password: 'testpass',
      database: 'jobseeker_test'
    });

    await postgresClient.connect();

    // Initialize schema
    await initializeSchema(postgresClient);

    // Seed test data
    await seedTestData(postgresClient);
  });

  afterAll(async () => {
    await postgresClient.end();
    await postgresContainer.stop();
  });

  afterEach(async () => {
    // Clean up data after each test
    await postgresClient.query('TRUNCATE candidates, applications CASCADE');
    await seedTestData(postgresClient);
  });

  test('should create candidate with complete profile', async () => {
    const candidate = new CandidateBuilder()
      .withName('Test', 'User')
      .withEmail('test.user@example.com')
      .build();

    const result = await candidateRepository.create(candidate);

    expect(result.personalInfo.email).toBe('test.user@example.com');
    expect(result.metadata.profileStatus).toBe('active');
  });

  test('should find candidates by skill', async () => {
    const candidates = await candidateRepository.findBySkill('JavaScript');

    expect(candidates.length).toBeGreaterThan(0);
    candidates.forEach(candidate => {
      const hasSkill = candidate.professionalInfo.skills.some(
        skill => skill.name === 'JavaScript'
      );
      expect(hasSkill).toBe(true);
    });
  });
});

async function initializeSchema(client) {
  await client.query(`
    CREATE TABLE IF NOT EXISTS candidates (
      id SERIAL PRIMARY KEY,
      email VARCHAR(255) UNIQUE NOT NULL,
      first_name VARCHAR(100),
      last_name VARCHAR(100),
      profile_data JSONB,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS applications (
      id SERIAL PRIMARY KEY,
      candidate_id INTEGER REFERENCES candidates(id),
      job_posting_id UUID NOT NULL,
      status VARCHAR(50),
      submitted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      application_data JSONB
    );

    CREATE INDEX idx_candidates_email ON candidates(email);
    CREATE INDEX idx_applications_candidate ON applications(candidate_id);
    CREATE INDEX idx_applications_status ON applications(status);
  `);
}

async function seedTestData(client) {
  const candidates = [
    new CandidateBuilder()
      .withName('Alice', 'Senior')
      .withSkills(['JavaScript', 'React', 'Node.js'])
      .build(),
    new CandidateBuilder()
      .withName('Bob', 'Junior')
      .withSkills(['JavaScript', 'HTML', 'CSS'])
      .build()
  ];

  for (const candidate of candidates) {
    await client.query(
      'INSERT INTO candidates (email, first_name, last_name, profile_data) VALUES ($1, $2, $3, $4)',
      [
        candidate.personalInfo.email,
        candidate.personalInfo.firstName,
        candidate.personalInfo.lastName,
        JSON.stringify(candidate)
      ]
    );
  }
}
```

### 3.3 Environment Isolation and Cleanup

**Test Isolation Strategies:**

```javascript
/**
 * Database isolation using transactions
 * Each test runs in a transaction that is rolled back
 */
class TransactionalTestRunner {
  async runTest(testFn) {
    const client = await database.connect();

    try {
      // Start transaction
      await client.query('BEGIN');

      // Run test
      await testFn(client);

      // Rollback transaction (discard all changes)
      await client.query('ROLLBACK');
    } catch (error) {
      await client.query('ROLLBACK');
      throw error;
    } finally {
      client.release();
    }
  }
}

// Usage
describe('Candidate Tests with Transaction Isolation', () => {
  const testRunner = new TransactionalTestRunner();

  test('create candidate does not affect other tests', async () => {
    await testRunner.runTest(async (client) => {
      const candidate = new CandidateBuilder().build();
      await candidateRepository.create(candidate, client);

      const count = await candidateRepository.count(client);
      expect(count).toBe(1);

      // Changes will be rolled back after test
    });
  });
});
```

**Schema-based Isolation:**

```javascript
/**
 * Each test gets its own database schema
 * Provides complete isolation without affecting other tests
 */
class SchemaIsolatedTestRunner {
  async setup() {
    this.schemaName = `test_schema_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;

    await database.query(`CREATE SCHEMA ${this.schemaName}`);
    await database.query(`SET search_path TO ${this.schemaName}`);

    // Initialize schema
    await this.initializeSchema();
    await this.seedTestData();
  }

  async teardown() {
    await database.query(`DROP SCHEMA ${this.schemaName} CASCADE`);
  }

  async initializeSchema() {
    // Run migration scripts in isolated schema
    await runMigrations(this.schemaName);
  }

  async seedTestData() {
    // Seed data in isolated schema
    await SequentialSeeder.seed(database, this.schemaName);
  }
}

// Usage with Jest
describe('Candidate Tests with Schema Isolation', () => {
  let testRunner;

  beforeEach(async () => {
    testRunner = new SchemaIsolatedTestRunner();
    await testRunner.setup();
  });

  afterEach(async () => {
    await testRunner.teardown();
  });

  test('isolated test with dedicated schema', async () => {
    // Test has complete database isolation
    const candidate = new CandidateBuilder().build();
    await candidateRepository.create(candidate);

    expect(await candidateRepository.count()).toBe(1);
  });
});
```

**Container-based Isolation:**

```javascript
/**
 * Each test suite gets a fresh container
 * Ultimate isolation but slower
 */
const { DockerComposeEnvironment, Wait } = require('testcontainers');

describe('Full Stack Integration Tests', () => {
  let environment;
  let apiUrl;

  beforeAll(async () => {
    environment = await new DockerComposeEnvironment('.', 'docker-compose.test.yml')
      .withWaitStrategy('job-seeker-app', Wait.forHealthCheck())
      .withBuild()
      .up();

    const appContainer = environment.getContainer('job-seeker-app');
    const port = appContainer.getMappedPort(3000);
    apiUrl = `http://localhost:${port}`;
  }, 300000); // 5 minute timeout

  afterAll(async () => {
    await environment.down();
  });

  test('end-to-end candidate registration flow', async () => {
    const response = await fetch(`${apiUrl}/api/candidates`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(new CandidateBuilder().build())
    });

    expect(response.status).toBe(201);
    const candidate = await response.json();
    expect(candidate.personalInfo.email).toBeDefined();
  });
});
```

**Cleanup Strategies:**

```javascript
/**
 * Automated cleanup utilities
 */
class TestDataCleanup {
  /**
   * Delete all test data created during a test
   */
  static async cleanupTestData(testId) {
    // Delete in reverse dependency order
    await database.applications.deleteMany({ testId });
    await database.jobPostings.deleteMany({ testId });
    await database.candidates.deleteMany({ testId });
  }

  /**
   * Delete test data older than specified age
   */
  static async cleanupOldTestData(daysOld = 7) {
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - daysOld);

    const result = await database.candidates.deleteMany({
      'metadata.createdAt': { $lt: cutoffDate },
      'metadata.isTestData': true
    });

    console.log(`Deleted ${result.deletedCount} old test records`);
  }

  /**
   * Reset environment to baseline state
   */
  static async resetEnvironment() {
    // Drop all test data
    await database.applications.deleteMany({});
    await database.jobPostings.deleteMany({});
    await database.candidates.deleteMany({});

    // Reseed baseline data
    await SequentialSeeder.seed(database);

    // Clear caches
    await redis.flushdb();

    // Reset Elasticsearch indices
    await elasticsearch.indices.delete({ index: 'jobs' });
    await elasticsearch.indices.create({ index: 'jobs' });
  }

  /**
   * Cleanup external resources (files, S3 objects, etc.)
   */
  static async cleanupExternalResources(testId) {
    // Delete uploaded files
    const uploadsDir = `./uploads/test/${testId}`;
    await fs.promises.rm(uploadsDir, { recursive: true, force: true });

    // Delete S3 test objects
    await s3.deleteObjects({
      Bucket: 'test-bucket',
      Delete: {
        Objects: await this.listTestS3Objects(testId)
      }
    });
  }

  static async listTestS3Objects(testId) {
    const result = await s3.listObjectsV2({
      Bucket: 'test-bucket',
      Prefix: `test/${testId}/`
    });

    return result.Contents.map(obj => ({ Key: obj.Key }));
  }
}

// Usage in test lifecycle
afterEach(async () => {
  await TestDataCleanup.cleanupTestData(currentTestId);
});

afterAll(async () => {
  await TestDataCleanup.cleanupExternalResources(currentTestId);
});

// Scheduled cleanup job
cron.schedule('0 2 * * *', async () => {
  console.log('Running scheduled test data cleanup...');
  await TestDataCleanup.cleanupOldTestData(7);
});
```

## 4. Synthetic Data Generation

### 4.1 Advanced Synthetic Data Patterns

**Realistic Data Correlations:**

```javascript
/**
 * Generate correlated data that mimics real-world patterns
 */
class RealisticDataGenerator {
  /**
   * Generate salary based on experience and location
   * Ensures realistic correlations
   */
  static calculateRealisticSalary(yearsExperience, location, role) {
    const baseSalaries = {
      'Junior Developer': 60000,
      'Software Engineer': 90000,
      'Senior Software Engineer': 130000,
      'Staff Engineer': 170000,
      'Principal Engineer': 200000
    };

    const locationMultipliers = {
      'San Francisco': 1.4,
      'New York': 1.35,
      'Seattle': 1.25,
      'Austin': 1.1,
      'Denver': 1.05,
      'Remote': 1.0
    };

    const baseSalary = baseSalaries[role] || 90000;
    const locationMultiplier = locationMultipliers[location.city] || 1.0;
    const experienceBonus = yearsExperience * 3000;

    const salary = (baseSalary + experienceBonus) * locationMultiplier;

    // Add some randomness (+/- 10%)
    const variance = salary * 0.1;
    return Math.round(
      (salary + (Math.random() * variance * 2 - variance)) / 1000
    ) * 1000;
  }

  /**
   * Generate skills that correlate with role and experience
   */
  static generateRoleAppropriateSkills(role, yearsExperience) {
    const skillSets = {
      'Frontend Developer': {
        core: ['JavaScript', 'HTML', 'CSS', 'React'],
        advanced: ['TypeScript', 'Next.js', 'GraphQL', 'Webpack'],
        senior: ['Architecture', 'Performance Optimization', 'Accessibility', 'Mentoring']
      },
      'Backend Developer': {
        core: ['Python', 'SQL', 'REST APIs', 'Git'],
        advanced: ['PostgreSQL', 'Redis', 'Docker', 'Kubernetes'],
        senior: ['System Design', 'Microservices', 'Cloud Architecture', 'Team Leadership']
      },
      'Full Stack Developer': {
        core: ['JavaScript', 'Node.js', 'React', 'SQL'],
        advanced: ['TypeScript', 'AWS', 'CI/CD', 'Testing'],
        senior: ['Architecture', 'DevOps', 'Security', 'Project Management']
      }
    };

    const roleSkills = skillSets[role] || skillSets['Full Stack Developer'];
    const skills = [];

    // All candidates get core skills
    skills.push(...roleSkills.core);

    // More experienced candidates have advanced skills
    if (yearsExperience >= 3) {
      const advancedCount = Math.min(
        roleSkills.advanced.length,
        Math.floor(yearsExperience / 2)
      );
      skills.push(...faker.helpers.arrayElements(roleSkills.advanced, advancedCount));
    }

    // Senior candidates have leadership skills
    if (yearsExperience >= 7) {
      const seniorCount = Math.min(
        roleSkills.senior.length,
        Math.floor((yearsExperience - 7) / 2) + 1
      );
      skills.push(...faker.helpers.arrayElements(roleSkills.senior, seniorCount));
    }

    return [...new Set(skills)]; // Remove duplicates
  }

  /**
   * Generate work history that makes chronological sense
   */
  static generateChronologicalWorkHistory(yearsExperience) {
    const experiences = [];
    let remainingYears = yearsExperience;
    let currentDate = new Date();
    let previousRole = 'Junior Developer';

    while (remainingYears > 0) {
      const tenureYears = Math.min(
        remainingYears,
        faker.number.int({ min: 1, max: 5 })
      );

      const startDate = new Date(currentDate);
      startDate.setFullYear(startDate.getFullYear() - tenureYears);

      // Career progression
      const role = this.getNextRole(previousRole, tenureYears);

      experiences.push({
        companyName: faker.company.name(),
        jobTitle: role,
        startDate,
        endDate: experiences.length === 0 ? null : currentDate, // Current job has no end date
        isCurrent: experiences.length === 0,
        responsibilities: this.generateRoleResponsibilities(role),
        achievements: faker.lorem.sentences(2)
      });

      currentDate = startDate;
      remainingYears -= tenureYears;
      previousRole = role;
    }

    return experiences;
  }

  static getNextRole(currentRole, yearsInRole) {
    const careerProgression = [
      'Junior Developer',
      'Software Engineer',
      'Senior Software Engineer',
      'Staff Engineer',
      'Principal Engineer'
    ];

    const currentIndex = careerProgression.indexOf(currentRole);

    // Promote after 2-3 years typically
    if (yearsInRole >= 2 && currentIndex < careerProgression.length - 1) {
      return careerProgression[currentIndex + 1];
    }

    return currentRole;
  }

  static generateRoleResponsibilities(role) {
    const responsibilityMap = {
      'Junior Developer': [
        'Implemented features under senior guidance',
        'Fixed bugs and wrote unit tests',
        'Participated in code reviews',
        'Contributed to documentation'
      ],
      'Software Engineer': [
        'Designed and implemented new features',
        'Collaborated with product managers',
        'Mentored junior developers',
        'Improved code quality and test coverage'
      ],
      'Senior Software Engineer': [
        'Led development of major features',
        'Architected scalable solutions',
        'Mentored team members',
        'Drove technical decisions'
      ],
      'Staff Engineer': [
        'Defined technical strategy',
        'Led cross-team initiatives',
        'Established engineering standards',
        'Influenced product roadmap'
      ]
    };

    return faker.helpers.arrayElements(
      responsibilityMap[role] || responsibilityMap['Software Engineer'],
      faker.number.int({ min: 2, max: 4 })
    );
  }
}
```

**Statistical Distribution Matching:**

```javascript
/**
 * Generate data that matches statistical distributions from production
 */
class StatisticalDataGenerator {
  /**
   * Generate experience distribution that matches production data
   *
   * Production analysis shows:
   * - 30% junior (0-3 years)
   * - 45% mid-level (4-7 years)
   * - 20% senior (8-12 years)
   * - 5% staff+ (13+ years)
   */
  static generateExperienceWithDistribution() {
    const random = Math.random();

    if (random < 0.30) {
      return faker.number.int({ min: 0, max: 3 });
    } else if (random < 0.75) {
      return faker.number.int({ min: 4, max: 7 });
    } else if (random < 0.95) {
      return faker.number.int({ min: 8, max: 12 });
    } else {
      return faker.number.int({ min: 13, max: 25 });
    }
  }

  /**
   * Generate application status distribution
   *
   * Production data:
   * - 50% submitted (not yet reviewed)
   * - 25% under_review
   * - 15% interviewed
   * - 5% offer_extended
   * - 3% accepted
   * - 2% rejected
   */
  static generateApplicationStatus() {
    const random = Math.random();

    if (random < 0.50) return 'submitted';
    if (random < 0.75) return 'under_review';
    if (random < 0.90) return 'interviewed';
    if (random < 0.95) return 'offer_extended';
    if (random < 0.98) return 'accepted';
    return 'rejected';
  }

  /**
   * Generate profile completeness with realistic distribution
   * Most users have medium completeness (40-80%)
   */
  static generateProfileCompleteness() {
    // Normal distribution centered around 65%
    const mean = 65;
    const stdDev = 20;

    // Box-Muller transform for normal distribution
    const u1 = Math.random();
    const u2 = Math.random();
    const z = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);

    const completeness = Math.round(mean + stdDev * z);

    // Clamp to 0-100
    return Math.max(0, Math.min(100, completeness));
  }
}
```

### 4.2 Performance Test Data Generation

**Large-Scale Data Generation:**

```javascript
/**
 * Generate large volumes of test data for performance testing
 */
class PerformanceTestDataGenerator {
  /**
   * Generate millions of candidate records efficiently
   */
  static async generateCandidatesInBatches(totalCount, batchSize = 1000) {
    console.log(`Generating ${totalCount} candidates in batches of ${batchSize}...`);

    const startTime = Date.now();
    let generated = 0;

    while (generated < totalCount) {
      const batch = [];
      const batchCount = Math.min(batchSize, totalCount - generated);

      for (let i = 0; i < batchCount; i++) {
        const yearsExperience = StatisticalDataGenerator.generateExperienceWithDistribution();
        const location = faker.location.city();
        const role = this.getRoleForExperience(yearsExperience);

        batch.push({
          personalInfo: {
            firstName: faker.person.firstName(),
            lastName: faker.person.lastName(),
            email: `candidate_${generated + i}@example.com`,
            phone: faker.phone.number('+1-###-###-####'),
            location: {
              city: location,
              state: faker.location.state({ abbreviated: true }),
              country: 'USA'
            }
          },
          professionalInfo: {
            currentTitle: role,
            yearsExperience,
            desiredSalary: RealisticDataGenerator.calculateRealisticSalary(
              yearsExperience,
              { city: location },
              role
            ),
            skills: RealisticDataGenerator.generateRoleAppropriateSkills(role, yearsExperience)
          },
          metadata: {
            profileCompleteness: StatisticalDataGenerator.generateProfileCompleteness(),
            profileStatus: 'active',
            registrationDate: faker.date.past({ years: 2 })
          }
        });
      }

      // Bulk insert batch
      await database.candidates.insertMany(batch, { ordered: false });

      generated += batchCount;

      if (generated % 10000 === 0) {
        const elapsed = (Date.now() - startTime) / 1000;
        const rate = Math.round(generated / elapsed);
        console.log(`Generated ${generated}/${totalCount} (${rate} records/sec)`);
      }
    }

    const elapsed = (Date.now() - startTime) / 1000;
    console.log(`Completed in ${elapsed}s (${Math.round(totalCount / elapsed)} records/sec)`);
  }

  static getRoleForExperience(years) {
    if (years <= 3) return 'Junior Developer';
    if (years <= 7) return 'Software Engineer';
    if (years <= 12) return 'Senior Software Engineer';
    return 'Staff Engineer';
  }

  /**
   * Generate realistic application load for stress testing
   */
  static async generateApplicationLoad(jobPostingId, applicationsPerMinute, durationMinutes) {
    console.log(`Generating ${applicationsPerMinute} applications/min for ${durationMinutes} minutes`);

    const delayMs = (60 * 1000) / applicationsPerMinute;
    const totalApplications = applicationsPerMinute * durationMinutes;
    let generated = 0;

    const startTime = Date.now();

    while (generated < totalApplications) {
      // Generate application
      const candidateId = `perf_test_candidate_${generated}`;
      const application = ApplicationDataGenerator.generateApplication(
        candidateId,
        jobPostingId
      );

      // Submit application (async, don't wait)
      this.submitApplication(application).catch(err =>
        console.error('Application submission failed:', err)
      );

      generated++;

      if (generated % 100 === 0) {
        const elapsed = (Date.now() - startTime) / 1000;
        const rate = Math.round(generated / elapsed * 60);
        console.log(`Submitted ${generated}/${totalApplications} (${rate}/min)`);
      }

      // Throttle to maintain desired rate
      await new Promise(resolve => setTimeout(resolve, delayMs));
    }

    console.log(`Load generation complete: ${generated} applications`);
  }

  static async submitApplication(application) {
    const response = await fetch('http://localhost:3000/api/applications', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(application)
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${await response.text()}`);
    }

    return response.json();
  }
}
```

## 5. Compliance and Best Practices

### 5.1 GDPR Compliance in Test Environments

**GDPR Requirements for Test Data:**

```yaml
gdpr_compliance:
  data_minimization:
    principle: "Use minimum necessary data for testing"
    practices:
      - "Synthetic data preferred over production data"
      - "Subset only necessary records if using production data"
      - "Delete test data when no longer needed"
      - "Avoid collecting unnecessary PII in test scenarios"

  purpose_limitation:
    principle: "Test data used only for testing purposes"
    practices:
      - "Clear data classification (test vs. production)"
      - "Separate test and production databases"
      - "No production data in non-production environments without justification"
      - "Document purpose and retention for each test data set"

  storage_limitation:
    principle: "Retain test data only as long as necessary"
    practices:
      - "Automated cleanup of ephemeral test data"
      - "Maximum retention periods for test environments"
      - "Regular audits of test data stores"
      - "Deletion of test data after test completion"

    retention_periods:
      ci_environment: "24 hours"
      integration_environment: "30 days"
      qa_staging: "90 days"
      archived_test_data: "1 year"

  security:
    principle: "Protect test data with appropriate security measures"
    practices:
      - "Encrypt test data at rest and in transit"
      - "Access controls for test environments"
      - "Audit logging for test data access"
      - "No production credentials in test environments"
      - "Secure disposal of test data"

  accountability:
    principle: "Document and demonstrate compliance"
    practices:
      - "Maintain inventory of test data sources"
      - "Document masking and anonymization procedures"
      - "Regular compliance reviews"
      - "Training for engineers on test data handling"
```

**Compliance Checklist:**

```javascript
/**
 * Automated compliance checks for test environments
 */
class ComplianceChecker {
  static async runComplianceChecks(environment) {
    console.log(`Running compliance checks for ${environment.name}...`);

    const results = {
      passed: [],
      failed: [],
      warnings: []
    };

    // Check 1: No production database connections
    if (await this.checkProductionDatabaseAccess(environment)) {
      results.failed.push({
        check: 'Production Database Access',
        message: 'Test environment has production database credentials'
      });
    } else {
      results.passed.push('Production Database Access');
    }

    // Check 2: All PII fields are masked
    const unmaskedPII = await this.checkPIIMasking(environment);
    if (unmaskedPII.length > 0) {
      results.failed.push({
        check: 'PII Masking',
        message: `Found ${unmaskedPII.length} unmasked PII fields`,
        details: unmaskedPII
      });
    } else {
      results.passed.push('PII Masking');
    }

    // Check 3: Test data age within retention policy
    const oldData = await this.checkDataRetention(environment);
    if (oldData.count > 0) {
      results.warnings.push({
        check: 'Data Retention',
        message: `${oldData.count} records exceed retention period`,
        action: 'Schedule cleanup'
      });
    } else {
      results.passed.push('Data Retention');
    }

    // Check 4: Encryption enabled
    if (!await this.checkEncryption(environment)) {
      results.failed.push({
        check: 'Encryption',
        message: 'Test database encryption not enabled'
      });
    } else {
      results.passed.push('Encryption');
    }

    // Check 5: Access controls configured
    if (!await this.checkAccessControls(environment)) {
      results.failed.push({
        check: 'Access Controls',
        message: 'Insufficient access controls on test environment'
      });
    } else {
      results.passed.push('Access Controls');
    }

    this.reportResults(results);

    return results.failed.length === 0;
  }

  static async checkProductionDatabaseAccess(environment) {
    // Check if any configuration points to production
    const config = environment.getDatabaseConfig();
    const productionHosts = ['prod-db.example.com', 'production.rds.amazonaws.com'];

    return productionHosts.some(host => config.host.includes(host));
  }

  static async checkPIIMasking(environment) {
    const unmasked = [];

    // Sample records and check for unmasked PII
    const candidates = await environment.database.candidates.find().limit(100).toArray();

    candidates.forEach((candidate, index) => {
      // Check email masking
      if (!candidate.personalInfo.email.startsWith('masked_') &&
          !candidate.personalInfo.email.startsWith('user_')) {
        unmasked.push({
          record: index,
          field: 'email',
          value: candidate.personalInfo.email
        });
      }

      // Check phone masking
      if (candidate.personalInfo.phone &&
          !candidate.personalInfo.phone.includes('XXX')) {
        unmasked.push({
          record: index,
          field: 'phone',
          value: candidate.personalInfo.phone
        });
      }
    });

    return unmasked;
  }

  static async checkDataRetention(environment) {
    const retentionPeriods = {
      ci: 1, // days
      integration: 30,
      qa: 90
    };

    const maxAgeDays = retentionPeriods[environment.type] || 90;
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - maxAgeDays);

    const oldRecordCount = await environment.database.candidates.countDocuments({
      'metadata.createdAt': { $lt: cutoffDate }
    });

    return { count: oldRecordCount, cutoffDate };
  }

  static async checkEncryption(environment) {
    // Check if database has encryption enabled
    const dbInfo = await environment.database.admin().serverStatus();
    return dbInfo.security?.encryptionEnabled || false;
  }

  static async checkAccessControls(environment) {
    // Verify RBAC is configured
    const users = await environment.database.admin().command({ usersInfo: 1 });

    // Check that default users are removed
    const hasDefaultUsers = users.users.some(u =>
      u.user === 'admin' || u.user === 'root'
    );

    return !hasDefaultUsers && users.users.length > 0;
  }

  static reportResults(results) {
    console.log('\n=== Compliance Check Results ===');
    console.log(`Passed: ${results.passed.length}`);
    console.log(`Failed: ${results.failed.length}`);
    console.log(`Warnings: ${results.warnings.length}`);

    if (results.failed.length > 0) {
      console.log('\nFailed Checks:');
      results.failed.forEach(failure => {
        console.log(`  ❌ ${failure.check}: ${failure.message}`);
        if (failure.details) {
          console.log(`     Details: ${JSON.stringify(failure.details, null, 2)}`);
        }
      });
    }

    if (results.warnings.length > 0) {
      console.log('\nWarnings:');
      results.warnings.forEach(warning => {
        console.log(`  ⚠️  ${warning.check}: ${warning.message}`);
        console.log(`     Action: ${warning.action}`);
      });
    }
  }
}

// Run compliance checks as part of CI/CD
// In deployment pipeline:
const compliancePass = await ComplianceChecker.runComplianceChecks(testEnvironment);
if (!compliancePass) {
  throw new Error('Compliance checks failed - deployment blocked');
}
```

## 6. Tools and Technologies

### 6.1 Test Data Tools

```yaml
test_data_tools:
  data_generation:
    faker_js:
      purpose: "Generate realistic fake data"
      language: "JavaScript"
      capabilities:
        - "Person data (names, emails, phones)"
        - "Company data"
        - "Addresses and locations"
        - "Dates and times"
        - "Lorem ipsum text"
        - "Internet data (URLs, IPs, user agents)"
      example: "const email = faker.internet.email();"
      url: "https://fakerjs.dev/"

    chance_js:
      purpose: "Random data generator with utilities"
      capabilities:
        - "Similar to Faker but different API"
        - "Credit cards, GUIDs, hashes"
        - "Weighted random selection"

    factory_bot:
      purpose: "Ruby test data factory"
      language: "Ruby"
      capabilities:
        - "Define data factories"
        - "Associations and sequences"
        - "Traits and inheritance"

    fishery:
      purpose: "TypeScript factory library"
      language: "TypeScript"
      capabilities:
        - "Type-safe factories"
        - "Traits and transient params"
        - "Integration with Faker"

  data_masking:
    delphix:
      type: "Commercial"
      capabilities:
        - "Production data masking"
        - "Data subsetting"
        - "Data virtualization"
        - "Compliance management"

    anonimatron:
      type: "Open Source"
      capabilities:
        - "Configuration-based masking"
        - "Consistent anonymization"
        - "Multiple database support"

    aws_data_pipeline:
      type: "Cloud Service"
      capabilities:
        - "ETL with masking"
        - "Scheduled data refresh"
        - "Integration with RDS"

  database_tools:
    testcontainers:
      purpose: "Disposable container instances for testing"
      languages: ["Java", "JavaScript", "Python", "Go", ".NET"]
      capabilities:
        - "Database containers"
        - "Message queue containers"
        - "Custom containers"
        - "Docker Compose support"
      url: "https://testcontainers.com/"

    dbmate:
      purpose: "Database migration tool"
      capabilities:
        - "Schema versioning"
        - "Rollback support"
        - "Multiple database support"

    pg_dump_restore:
      purpose: "PostgreSQL backup and restore"
      capabilities:
        - "Full database dumps"
        - "Selective table dumps"
        - "Point-in-time recovery"
```

### 6.2 Environment Management Tools

```yaml
environment_tools:
  containerization:
    docker:
      purpose: "Application containerization"
      use_cases:
        - "Isolated test environments"
        - "Consistent dependencies"
        - "Multi-service orchestration"

    docker_compose:
      purpose: "Multi-container orchestration"
      use_cases:
        - "Local development environments"
        - "Integration testing setups"
        - "Service dependency management"

    kubernetes:
      purpose: "Container orchestration platform"
      use_cases:
        - "Staging and production-like environments"
        - "Auto-scaling test environments"
        - "Namespace isolation"

  infrastructure_as_code:
    terraform:
      purpose: "Infrastructure provisioning"
      capabilities:
        - "Cloud resource management"
        - "Version-controlled infrastructure"
        - "Multi-cloud support"

    ansible:
      purpose: "Configuration management"
      capabilities:
        - "Server configuration"
        - "Application deployment"
        - "Idempotent operations"

    pulumi:
      purpose: "Infrastructure as code with programming languages"
      languages: ["TypeScript", "Python", "Go", "C#"]
      capabilities:
        - "Type-safe infrastructure"
        - "Integration with existing code"

  cloud_environments:
    aws_services:
      rds: "Managed relational databases"
      elasticache: "Managed Redis/Memcached"
      s3: "Object storage for test files"
      cloudformation: "Infrastructure as code"
      ecs_fargate: "Serverless containers"

    azure_services:
      azure_devtest_labs: "Managed test environments"
      azure_sql: "Managed SQL databases"
      azure_container_instances: "Serverless containers"

    gcp_services:
      cloud_sql: "Managed databases"
      cloud_run: "Serverless containers"
      gke: "Kubernetes engine"
```

## 7. Best Practices Summary

### 7.1 Test Data Management Best Practices

```yaml
best_practices:
  data_generation:
    - practice: "Prefer synthetic data for most testing scenarios"
      rationale: "Eliminates privacy concerns and provides unlimited variations"

    - practice: "Use production data only when necessary"
      rationale: "Captures real-world complexity but requires masking overhead"

    - practice: "Implement builder patterns for test data creation"
      rationale: "Readable, maintainable, and flexible test data creation"

    - practice: "Version control test data fixtures"
      rationale: "Reproducible tests across time and environments"

    - practice: "Generate edge cases systematically"
      rationale: "Ensures boundary conditions are tested consistently"

  data_security:
    - practice: "Never commit production data to version control"
      rationale: "Prevents accidental exposure of sensitive information"

    - practice: "Use deterministic masking for referential integrity"
      rationale: "Maintains data relationships while protecting PII"

    - practice: "Implement automated compliance checks"
      rationale: "Catch compliance issues before deployment"

    - practice: "Encrypt test data at rest"
      rationale: "Defense in depth for data protection"

    - practice: "Regular security audits of test environments"
      rationale: "Identify and remediate security gaps"

  environment_management:
    - practice: "Treat test environments as code"
      rationale: "Reproducible, version-controlled infrastructure"

    - practice: "Isolate test runs from each other"
      rationale: "Prevents test interference and flakiness"

    - practice: "Automate environment provisioning and teardown"
      rationale: "Reduces manual effort and ensures consistency"

    - practice: "Use containers for local development"
      rationale: "Consistent environment across team members"

    - practice: "Implement environment health checks"
      rationale: "Detect infrastructure issues before testing"

  data_lifecycle:
    - practice: "Implement automated cleanup of test data"
      rationale: "Prevents data accumulation and ensures fresh starts"

    - practice: "Document data retention policies"
      rationale: "Compliance and storage cost management"

    - practice: "Separate ephemeral and persistent test data"
      rationale: "Different lifecycle requirements"

    - practice: "Regular refresh of masked production data"
      rationale: "Keep test data representative of production"
```

## 8. References and Resources

### 8.1 ISTQB References

- **ISTQB Foundation Level Syllabus v4.0** - Section 5.3: Test Data Preparation
- **ISTQB Advanced Test Manager** - Chapter on Test Environment and Data Management
- **ISTQB Test Automation Engineer** - Environment setup and test data strategies

### 8.2 Industry Standards

- **ISO/IEC 29119-1:2022** - Software Testing - Test Environments
- **IEEE 829-2008** - Standard for Software Test Documentation
- **GDPR (EU) 2016/679** - Data protection and privacy requirements
- **CCPA** - California Consumer Privacy Act

### 8.3 Books and Publications

- "The Art of Software Testing" by Glenford Myers - Test data design principles
- "Continuous Delivery" by Jez Humble - Environment management in CD pipelines
- "Test Data Management" by John Slootweg - Comprehensive guide to TDM
- "Database Reliability Engineering" by Charity Majors - Production data handling

### 8.4 Online Resources

- **TestContainers Documentation**: https://testcontainers.com/
- **Faker.js Documentation**: https://fakerjs.dev/
- **Docker Best Practices**: https://docs.docker.com/develop/dev-best-practices/
- **OWASP Testing Guide - Data Privacy**: https://owasp.org/www-project-web-security-testing-guide/

### 8.5 Tools Documentation

- **Docker Compose**: https://docs.docker.com/compose/
- **Kubernetes**: https://kubernetes.io/docs/
- **Terraform**: https://www.terraform.io/docs/
- **AWS Test Environments**: https://aws.amazon.com/devops/continuous-testing/

---

**Document Status:** Complete
**Version:** 1.0
**Last Updated:** 2025-10-04
**Next Review:** 2025-11-04
