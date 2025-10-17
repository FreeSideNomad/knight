
# prompt.md — Generate a Modular Monolith (DDD) Java/Maven Scaffold

You are an expert software architect and senior Java engineer. Based on the following **product DDD model** and **requirements**, generate a **ready-to-build** Maven multi-module project (Micronaut 4.9, Java 17+) that follows Domain-Driven Design with strict boundaries and a BFF.

## Goals
- **Start as a modular monolith** with clean seams to split into services later.
- Enforce **bounded contexts** as separate Maven modules: `api`, `app`, `domain`, `infra`.
- Provide a **thin BFF** for UI composition & auth (no domain rules).
- Use **in-process domain events** now; enable **Outbox/Inbox + Kafka** later.
- Use **one schema per BC**, Flyway-ready.
- Include **guardrails**: ArchUnit tests for dependency enforcement.

## Inputs you will receive
- A YAML DDD model (see example fragment below). It defines **domains**, **bounded contexts**, **aggregates**, **value objects**, **commands**, **events**, **repositories**, **workflows**.
- The target artifact coordinates: `<groupId>`, `<artifactId>`, `<version>`.

### Example DDD fragment (illustrative)
```yaml
system:
  id: sys_cash_mgmt_platform
  name: Commercial Banking Cash Management Platform

domains:
  dom_service_profiles:
    bounded_contexts: [bc_service_profile_management, bc_indirect_client_management]

bounded_contexts:
  bc_service_profile_management:
    application_services: [svc_app_servicing_profile, svc_app_online_profile, svc_app_indirect_profile]

value_objects:
  vo_client_id: { name: ClientId, rules: ["system in {srf,gid,ind}", "urn format {system}:{client_number}"] }

aggregates:
  agg_servicing_profile:
    root_ref: ent_servicing_profile
    invariants: ["client_id immutable", "at least one service to be ACTIVE"]
```

---

## Deliverables to Generate

### 1) Maven Reactor (root `pom.xml`)
- Packaging `pom`
- `dependencyManagement`: Micronaut BOM (4.9.3)
- Modules list matching BCs: for each BC create `api`, `domain`, `app`, `infra`
- Modules for `platform/shared-kernel`, `platform/common-test`, and `bff/web`

### 2) Shared Kernel (tiny, stable)
- Value objects from YAML (e.g., `ClientId`, `ProfileId` hierarchy).
- Validation and immutability using Jakarta Validation annotations.
- No dependencies on business modules.

### 3) BFF (module: `bff/web`)
- Micronaut HTTP app with reactive support, OIDC client hooks (commented), security enabled.
- **Screen-shaped** endpoints (composition only); depends only on **BC `api`** modules.
- No imports from any `domain` or `app` packages.

### 4) BC modules per bounded context
For each BC (e.g., `service-profiles/management`), generate:

- **`api`**:
  - Command & query interfaces (Java) that other modules call.
  - Domain **event** records in `api.events` package.
  - (Optional) OpenAPI annotations for sync commands/queries.

- **`domain`**:
  - Aggregates/entities/value objects per YAML, with invariants as assertions.
  - Domain services where specified.

- **`app`**:
  - Transactional application services (command handlers, process managers).
  - Publishes in-proc events with `ApplicationEventPublisher` using `@TransactionalEventListener`.

- **`infra`**:
  - Micronaut app for adapters and REST controllers for the command endpoints.
  - Micronaut Data JPA repositories (Postgres), Flyway-ready.
  - Mappers/Adapters (`app` ↔ `infra` JPA), HTTP controllers exposing `/commands/**` & `/queries/**`.
  - Placeholders for **Outbox/Inbox** (Kafka ready).

### 5) Guardrails
- **Java 17** toolchain via Maven Compiler Plugin (`<release>17</release>`).
- **ArchUnit** (1.4.1) tests: forbid cross-BC imports except `..api..`; forbid `bff..` importing `..domain..|..app..`.
- Module dependency validation using ArchUnit layered architecture rules.

### 6) Persistence
- **One schema per BC** (logical or physical), migrations in each `infra` module (`db/migration`).
- Entities annotated with Jakarta Persistence; IDs match VO URNs.
- Indexes aligned to YAML repository queries.

### 7) Events & Integration
- Internal: Micronaut in-proc events (`ApplicationEventPublisher` + `@EventListener`).
- Use `@Async` annotation for asynchronous event processing when needed.
- External (future): Outbox/Inbox entities + scheduled publisher (or Debezium CDC) → Kafka topics.
- Naming pattern: `<bc>.events` (and `<bc>.commands` if choosing async commands).

### 8) Sample Code (representative)
Provide working samples for:
- **Shared Kernel:** `ClientId`, `ServicingProfileId`, `ProfileId` sealed interface.
- **BC (Service Profiles / Management):**
  - API: `SpmCommands`, `SpmQueries`, event `ServicingProfileCreated`.
  - Domain: `ServicingProfile` aggregate with simple invariant & service enrollment.
  - App: `SpmApplicationService` implementing commands/queries + event publish.
  - Infra: JPA entity + Micronaut Data repo + REST controller to create profile.
- **BFF:** one endpoint composing `SpmQueries` into a `ProfileSummaryDto`.

---

## Project Layout (target files & folders)

```
<root>/pom.xml
platform/shared-kernel/pom.xml
bff/web/pom.xml
contexts/<bc>/<sub>/pom.xml               # aggregator for BC
contexts/<bc>/<sub>/api/pom.xml           # + src with command/query interfaces & events
contexts/<bc>/<sub>/domain/pom.xml        # + aggregates/entities/services
contexts/<bc>/<sub>/app/pom.xml           # + application services
contexts/<bc>/<sub>/infra/pom.xml         # + JPA/REST adapters, Flyway, ArchUnit tests
```

---

## Conventions & Rules (must enforce)

1. **Dependency direction**: `infra -> app -> domain -> api` (api is referenced by others; no reverse leaks).
2. **BFF** depends only on `api` modules; never `app` or `domain`.
3. **No shared database** across BCs; schemas per BC (even if on the same instance).
4. **Events are facts; commands are intents**. Use events for "happened", commands for "do".
5. **Idempotency & reliability**: add `commandId`, `correlationId` in contracts; Outbox/Inbox stubs present.
6. **Testing**: generate at least one ArchUnit rule and one basic integration test with `@MicronautTest`.

---

## Minimal Snippets to Include (update package names)

**Root POM (extract):**
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <!-- Micronaut -->
    <micronaut.version>4.9.3</micronaut.version>
    <micronaut.data.version>4.6.0</micronaut.data.version>
    <micronaut.test.version>4.8.0</micronaut.test.version>

    <!-- Dependencies -->
    <lombok.version>1.18.40</lombok.version>
    <archunit.version>1.4.1</archunit.version>
    <mapstruct.version>1.6.3</mapstruct.version>
    <postgresql.version>42.7.5</postgresql.version>
    <flyway.version>10.25.0</flyway.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.micronaut.platform</groupId>
      <artifactId>micronaut-platform</artifactId>
      <version>${micronaut.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <dependency>
      <groupId>io.micronaut.data</groupId>
      <artifactId>micronaut-data-bom</artifactId>
      <version>${micronaut.data.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

**Shared Kernel VO:**
```java
public final class ClientId {
  private static final Pattern P = Pattern.compile("^(srf|gid|ind):[A-Za-z0-9_-]+$");
  private final String urn;
  private ClientId(String urn){
    if(!P.matcher(urn).matches()) throw new IllegalArgumentException("Invalid ClientId format");
    this.urn=urn;
  }
  public static ClientId of(String urn){ return new ClientId(urn); }
  public String urn(){ return urn; }
  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return urn.equals(((ClientId) o).urn);
  }
  @Override public int hashCode() { return urn.hashCode(); }
}
```

**API (commands/queries):**
```java
public interface SpmCommands {
  ServicingProfileId createServicingProfile(ClientId clientId, String createdBy);
  record EnrollServiceCmd(ServicingProfileId id, String serviceType, String configurationJson) {}
  void enrollService(EnrollServiceCmd cmd);
}
public interface SpmQueries {
  record ServicingProfileSummary(String profileUrn, String status, int enrolledServices, int enrolledAccounts){}
  ServicingProfileSummary getServicingProfileSummary(ServicingProfileId id);
}
```

**Domain aggregate:**
```java
public class ServicingProfile {
  public enum Status { PENDING, ACTIVE, SUSPENDED, CLOSED }
  private final ServicingProfileId id;
  private Status status = Status.PENDING;
  public static ServicingProfile create(ServicingProfileId id){ return new ServicingProfile(id); }
  public void activateIfEligible(){ /* set ACTIVE when rules satisfied */ }
}
```

**App service:**
```java
@Singleton
public class SpmApplicationService implements SpmCommands, SpmQueries {
  private final SpmRepository repo;
  private final ApplicationEventPublisher<Object> events;

  @Transactional
  public ServicingProfileId createServicingProfile(ClientId clientId, String createdBy){
    /* create aggregate, save, publish event */
  }
}
```

**Infra controller:**
```java
@Controller
@ExecuteOn(TaskExecutors.BLOCKING)
class SpmCommandController {
  private final SpmCommands commands;

  @Post("/commands/service-profiles/servicing/create")
  public CreateResult create(@Body CreateRequest req){
    var id = commands.createServicingProfile(ClientId.of(req.clientUrn()), req.createdBy());
    return new CreateResult(id.urn());
  }
  record CreateRequest(String clientUrn, String createdBy) {}
  record CreateResult(String profileUrn) {}
}
```

**BFF summary endpoint:**
```java
@Controller("/api/profiles")
class ProfileSummaryController {
  private final SpmQueries queries;

  @Get("/servicing/{clientUrn}/summary")
  ProfileSummaryDto servicing(@PathVariable String clientUrn){
    var id = ServicingProfileId.of(ClientId.of(clientUrn));
    var s = queries.getServicingProfileSummary(id);
    return new ProfileSummaryDto(s.profileUrn(), s.status(), s.enrolledServices(), s.enrolledAccounts());
  }
}
record ProfileSummaryDto(String profileUrn, String status, int enrolledServices, int enrolledAccounts){}
```

**ArchUnit guardrail:**
```java
@AnalyzeClasses(packages = "com.knight")
class DddArchitectureTest {

  @ArchTest
  static final ArchRule bff_no_domain_or_app =
    noClasses().that().resideInAnyPackage("..bff..")
      .should().dependOnClassesThat().resideInAnyPackage("..domain..","..app..");

  @ArchTest
  static final ArchRule layered_architecture =
    layeredArchitecture().consideringAllDependencies()
      .layer("API").definedBy("..api..")
      .layer("Domain").definedBy("..domain..")
      .layer("Application").definedBy("..app..")
      .layer("Infrastructure").definedBy("..infra..")
      .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Infrastructure")
      .whereLayer("Application").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
      .whereLayer("Domain").mayOnlyBeAccessedByLayers("Domain", "Application", "Infrastructure")
      .whereLayer("API").mayOnlyBeAccessedByLayers("API", "Domain", "Application", "Infrastructure");
}
```

**Micronaut Test Example:**
```java
@MicronautTest
@Property(name = "datasources.default.driver-class-name", value = "org.testcontainers.jdbc.ContainerDatabaseDriver")
@Property(name = "datasources.default.url", value = "jdbc:tc:postgresql:16:///db")
class SpmApplicationServiceTest {

  @Inject
  SpmCommands commands;

  @Test
  void shouldCreateServicingProfile() {
    var clientId = ClientId.of("srf:CAN123456");
    var profileId = commands.createServicingProfile(clientId, "admin@bank.com");
    assertNotNull(profileId);
  }
}
```

---

## How to Use This Prompt
1. Provide this **prompt.md** and your YAML DDD model to the LLM.
2. Ask it to **generate the full repo** contents:
   - All POMs and module trees
   - Java packages and sample code per Deliverables
   - Minimal `application.yml` and Flyway stubs
3. Ask it to **explain how to build & run** locally with Postgres (docker) and sample curl calls.

> Keep the output deterministic, consistent with the rules above, and compile-ready.
