
# prompt.md — Generate a Modular Monolith (DDD) Java/Maven Scaffold

You are an expert software architect and senior Java engineer. Based on the following **product DDD model** and **requirements**, generate a **ready-to-build** Maven multi-module project (Spring Boot 3.3, Java 17+) that follows Domain-Driven Design with strict boundaries and a BFF.

## Goals
- **Start as a modular monolith** with clean seams to split into services later.
- Enforce **bounded contexts** as separate Maven modules: `api`, `app`, `domain`, `infra`.
- Provide a **thin BFF** for UI composition & auth (no domain rules).
- Use **in-process domain events** now; enable **Outbox/Inbox + Kafka** later.
- Use **one schema per BC**, Flyway-ready.
- Include **guardrails**: Spring Modulith + ArchUnit tests.

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
- `dependencyManagement`: Spring Boot BOM (3.3.x) & Spring Modulith BOM
- Modules list matching BCs: for each BC create `api`, `domain`, `app`, `infra`
- Modules for `platform/shared-kernel`, `platform/common-test`, and `bff/web`

### 2) Shared Kernel (tiny, stable)
- Value objects from YAML (e.g., `ClientId`, `ProfileId` hierarchy).
- Validation and immutability.
- No dependencies on business modules.

### 3) BFF (module: `bff/web`)
- Spring Boot app (WebFlux or MVC), OIDC client hooks (commented), CSRF enabled.
- **Screen-shaped** endpoints (composition only); depends only on **BC `api`** modules.
- No imports from any `domain` or `app` packages.

### 4) BC modules per bounded context
For each BC (e.g., `service-profiles/management`), generate:

- **`api`**:
  - Command & query interfaces (Java) that other modules call.
  - Domain **event** records in `api.events` package.
  - (Optional) OpenAPI stubs for sync commands/queries.

- **`domain`**:
  - Aggregates/entities/value objects per YAML, with invariants as assertions.
  - Domain services where specified.

- **`app`**:
  - Transactional application services (command handlers, process managers).
  - Publishes in-proc events with `ApplicationEventPublisher` (AFTER_COMMIT).

- **`infra`**:
  - Spring Boot app for adapters and REST controllers for the command endpoints.
  - Spring Data JPA repositories (Postgres), Flyway-ready.
  - Mappers Adapters (`app` ↔ `infra` JPA), HTTP controllers exposing `/commands/**` & `/queries/**`.
  - Placeholders for **Outbox/Inbox** (Kafka ready).

### 5) Guardrails
- **Java 17** toolchain via Maven Compiler Plugin (`<release>17</release>`).
- **ArchUnit** tests: forbid cross-BC imports except `..api..`; forbid `bff..` importing `..domain..|..app..`.
- **Spring Modulith**: dependency checks + scenario tests (basic).

### 6) Persistence
- **One schema per BC** (logical or physical), migrations in each `infra` module (`db/migration`).
- Entities annotated minimally; IDs match VO URNs.
- Indexes aligned to YAML repository queries.

### 7) Events & Integration
- Internal: Spring in-proc events (`@TransactionalEventListener(AFTER_COMMIT)`).
- External (future): Outbox/Inbox entities + scheduled publisher (or Debezium CDC) → Kafka topics.
- Naming pattern: `<bc>.events` (and `<bc>.commands` if choosing async commands).

### 8) Sample Code (representative)
Provide working samples for:
- **Shared Kernel:** `ClientId`, `ServicingProfileId`, `ProfileId` sealed interface.
- **BC (Service Profiles / Management):**
  - API: `SpmCommands`, `SpmQueries`, event `ServicingProfileCreated`.
  - Domain: `ServicingProfile` aggregate with simple invariant & service enrollment.
  - App: `SpmApplicationService` implementing commands/queries + event publish.
  - Infra: JPA entity + Spring Data repo + REST controller to create profile.
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
4. **Events are facts; commands are intents**. Use events for “happened”, commands for “do”. 
5. **Idempotency & reliability**: add `commandId`, `correlationId` in contracts; Outbox/Inbox stubs present.
6. **Testing**: generate at least one ArchUnit rule and one basic spring-modulith test.

---

## Minimal Snippets to Include (update package names)

**Root POM (extract):**
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>3.3.4</version>
      <type>pom</type><scope>import</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.modulith</groupId>
      <artifactId>spring-modulith-bom</artifactId>
      <version>1.2.4</version>
      <type>pom</type><scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

**Shared Kernel VO:**
```java
public final class ClientId {
  private static final Pattern P = Pattern.compile("^(srf|gid|ind):[A-Za-z0-9_-]+$");
  private final String urn;
  private ClientId(String urn){ if(!P.matcher(urn).matches()) throw new IllegalArgumentException("bad"); this.urn=urn; }
  public static ClientId of(String urn){ return new ClientId(urn); }
  public String urn(){ return urn; }
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
@Service
public class SpmApplicationService implements SpmCommands, SpmQueries {
  private final SpmRepository repo; private final ApplicationEventPublisher events;
  @Transactional public ServicingProfileId createServicingProfile(ClientId clientId, String createdBy){ /* ... */ }
}
```

**Infra controller:**
```java
@RestController
@RequestMapping("/commands/service-profiles/servicing")
class SpmCommandController {
  private final SpmCommands commands;
  @PostMapping("/create") public CreateResult create(@RequestBody CreateRequest req){
    var id = commands.createServicingProfile(ClientId.of(req.clientUrn()), req.createdBy());
    return new CreateResult(id.urn());
  }
  record CreateRequest(String clientUrn, String createdBy) {}
  record CreateResult(String profileUrn) {}
}
```

**BFF summary endpoint:**
```java
@RestController @RequestMapping("/api/profiles")
class ProfileSummaryController {
  private final SpmQueries queries;
  @GetMapping("/servicing/{clientUrn}/summary")
  ProfileSummaryDto servicing(@PathVariable String clientUrn){
    var id = ServicingProfileId.of(ClientId.of(clientUrn));
    var s = queries.getServicingProfileSummary(id);
    return new ProfileSummaryDto(s.profileUrn(), s.status(), s.enrolledServices(), s.enrolledAccounts());
  }
}
record ProfileSummaryDto(String profileUrn, String status, int enrolledServices, int enrolledAccounts){}
```

**ArchUnit guardrail (idea):**
```java
@AnalyzeClasses(packages = "com.knight")
class DependencyRules {
  @ArchTest static final ArchRule bff_no_domain =
    noClasses().that().resideInAnyPackage("..bff..")
      .should().dependOnClassesThat().resideInAnyPackage("..domain..","..app..");
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
