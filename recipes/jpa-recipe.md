# Micronaut 4 + JPA Persistence Recipe

## Overview

This recipe documents the **exact configuration and code patterns** required for implementing JPA persistence in Micronaut 4.9.4. This is a validated reference based on a working implementation tested with E2E tests.

---

## Critical Success Factors

### 1. Use `@Serdeable` NOT `@Introspected`

**❌ WRONG** (will cause runtime errors):
```java
import io.micronaut.core.annotation.Introspected;

@Introspected
public record CreateRequest(String name) {}
```

**✅ CORRECT**:
```java
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record CreateRequest(String name) {}
```

**Why**: Micronaut 4 with `micronaut-serde-jackson` requires `@Serdeable` for JSON serialization/deserialization. Using `@Introspected` will result in:
```
No deserializable introspection present for type: CreateRequest
```

---

## Maven Dependencies

### Required Dependencies (pom.xml)

```xml
<parent>
    <groupId>io.micronaut.platform</groupId>
    <artifactId>micronaut-parent</artifactId>
    <version>4.9.4</version>
</parent>

<properties>
    <micronaut.version>4.9.4</micronaut.version>
    <micronaut.runtime>netty</micronaut.runtime>
    <jdk.version>17</jdk.version>
</properties>

<dependencies>
    <!-- HTTP Server -->
    <dependency>
        <groupId>io.micronaut</groupId>
        <artifactId>micronaut-http-server-netty</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- JPA / Hibernate -->
    <dependency>
        <groupId>io.micronaut.data</groupId>
        <artifactId>micronaut-data-hibernate-jpa</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- JSON Serialization (REQUIRED for @Serdeable) -->
    <dependency>
        <groupId>io.micronaut.serde</groupId>
        <artifactId>micronaut-serde-jackson</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- Database Connection Pool -->
    <dependency>
        <groupId>io.micronaut.sql</groupId>
        <artifactId>micronaut-jdbc-hikari</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Logging -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- YAML Configuration Support -->
    <dependency>
        <groupId>org.yaml</groupId>
        <artifactId>snakeyaml</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Annotation Processors (CRITICAL)

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths combine.self="override">
                    <!-- Micronaut Dependency Injection -->
                    <path>
                        <groupId>io.micronaut</groupId>
                        <artifactId>micronaut-inject-java</artifactId>
                        <version>${micronaut.core.version}</version>
                    </path>

                    <!-- Micronaut Data JPA Processor -->
                    <path>
                        <groupId>io.micronaut.data</groupId>
                        <artifactId>micronaut-data-processor</artifactId>
                        <version>${micronaut.data.version}</version>
                        <exclusions>
                            <exclusion>
                                <groupId>io.micronaut</groupId>
                                <artifactId>micronaut-inject</artifactId>
                            </exclusion>
                        </exclusions>
                    </path>

                    <!-- Micronaut Serde Processor (for @Serdeable) -->
                    <path>
                        <groupId>io.micronaut.serde</groupId>
                        <artifactId>micronaut-serde-processor</artifactId>
                        <version>${micronaut.serialization.version}</version>
                        <exclusions>
                            <exclusion>
                                <groupId>io.micronaut</groupId>
                                <artifactId>micronaut-inject</artifactId>
                            </exclusion>
                        </exclusions>
                    </path>

                    <!-- HTTP Validation -->
                    <path>
                        <groupId>io.micronaut</groupId>
                        <artifactId>micronaut-http-validation</artifactId>
                        <version>${micronaut.core.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## Application Configuration

### application.yml

```yaml
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/knight
    driverClassName: org.postgresql.Driver
    username: knight
    password: knight

jpa:
  default:
    properties:
      hibernate:
        hbm2ddl:
          auto: update  # or validate, none (use Flyway for production)
        show_sql: true   # Remove in production
```

**Options for `hibernate.hbm2ddl.auto`**:
- `update` - Auto-create/update schema (dev only)
- `validate` - Validate schema matches entities
- `none` - No auto-DDL (use Flyway/Liquibase)

---

## Code Patterns

### 1. JPA Entity

```java
package example;

import jakarta.persistence.*;

@Entity
@Table(name = "test_entity")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

**Key Points**:
- Use `jakarta.persistence.*` (NOT `javax.persistence.*`)
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` for auto-increment
- Use getters/setters (JPA requires them)

---

### 2. Repository Interface

```java
package example;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface TestRepository extends CrudRepository<TestEntity, Long> {
    // Micronaut Data implements this automatically
    // No code needed for basic CRUD operations
}
```

**What you get for free**:
- `save(entity)` - Create/update
- `findById(id)` - Read
- `findAll()` - List all
- `deleteById(id)` - Delete
- `count()` - Count records

**Custom queries** (if needed):
```java
@Repository
public interface TestRepository extends CrudRepository<TestEntity, Long> {
    List<TestEntity> findByName(String name);
    Optional<TestEntity> findByNameAndActive(String name, boolean active);
}
```

---

### 3. REST Controller with DTOs

```java
package example;

import io.micronaut.http.annotation.*;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.Optional;

@Controller("/test")
public class TestController {

    @Inject
    private TestRepository testRepository;

    @Post
    public CreateResponse create(@Body CreateRequest request) {
        TestEntity entity = new TestEntity();
        entity.setName(request.name());

        TestEntity saved = testRepository.save(entity);

        return new CreateResponse(saved.getId(), saved.getName());
    }

    @Get("/{id}")
    public Optional<GetResponse> get(@PathVariable Long id) {
        return testRepository.findById(id)
                .map(e -> new GetResponse(e.getId(), e.getName()));
    }

    // DTOs with @Serdeable annotation
    @Serdeable
    public record CreateRequest(String name) {}

    @Serdeable
    public record CreateResponse(Long id, String name) {}

    @Serdeable
    public record GetResponse(Long id, String name) {}
}
```

**Critical Rules**:
1. All DTOs MUST have `@Serdeable` annotation
2. Import `io.micronaut.serde.annotation.Serdeable`
3. Use `@Body` for request DTOs
4. Use `@PathVariable` for URL parameters

---

### 4. Application Entry Point

```java
package example;

import io.micronaut.runtime.Micronaut;

public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
```

---

## Common Errors and Solutions

### Error 1: "No deserializable introspection present"

**Error Message**:
```
No deserializable introspection present for type: CreateRequest request.
Consider adding Serdeable.Deserializable annotate to type CreateRequest.
```

**Solution**:
```java
// Change from @Introspected to @Serdeable
@Serdeable
public record CreateRequest(String name) {}
```

**Cause**: Using `@Introspected` instead of `@Serdeable` when `micronaut-serde-jackson` is enabled.

---

### Error 2: "No bean introspection available"

**Error Message**:
```
No bean introspection available for type [class SomeController$SomeRequest]
```

**Solution**:
Add `@Serdeable` annotation to the DTO class.

**Cause**: Missing `@Serdeable` annotation on request/response DTOs.

---

### Error 3: Missing annotation processors

**Symptom**: Repository methods not implemented, DTOs not serializable

**Solution**: Ensure `micronaut-data-processor` and `micronaut-serde-processor` are in `annotationProcessorPaths`.

---

### Error 4: "Unable to start Micronaut server on port"

**Error Message**:
```
Unable to start Micronaut server on *:8080
Caused by: java.net.BindException: Address already in use
```

**Solution**:
```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

---

### Error 5: Database connection fails

**Error Message**:
```
org.postgresql.util.PSQLException: Connection refused
```

**Solution**:
1. Verify PostgreSQL is running: `docker ps`
2. Check connection details in `application.yml`
3. Test connection: `psql -h localhost -p 5432 -U knight -d knight`

---

## Testing Patterns

### E2E Test Script Example

```bash
#!/bin/bash
set -e

# Check service is running
curl -s http://localhost:8080/health > /dev/null || exit 1

# Clean database
docker exec postgres psql -U knight -d knight \
    -c "TRUNCATE TABLE test_entity RESTART IDENTITY CASCADE;"

# Test POST
RESPONSE=$(curl -s -X POST http://localhost:8080/test \
    -H "Content-Type: application/json" \
    -d '{"name":"test-1"}')

echo "$RESPONSE" | grep -q '"id":1' || exit 1

# Test GET
RESPONSE=$(curl -s http://localhost:8080/test/1)
echo "$RESPONSE" | grep -q '"name":"test-1"' || exit 1

# Verify database
COUNT=$(docker exec postgres psql -U knight -d knight -t \
    -c "SELECT COUNT(*) FROM test_entity;" | xargs)
[ "$COUNT" == "1" ] || exit 1

echo "✓ All tests passed"
```

---

## Hibernate Query Logging

**What you'll see in logs**:
```
Hibernate: insert into test_entity (name) values (?)
Hibernate: select te1_0.id,te1_0.name from test_entity te1_0
           where (te1_0.id=?) fetch first ? rows only
```

**To disable** (in production):
```yaml
jpa:
  default:
    properties:
      hibernate:
        show_sql: false  # Disable query logging
```

---

## Migration from Micronaut Data JDBC

If migrating from Micronaut Data JDBC to JPA:

### Changes Required:

1. **Dependencies**:
   - Remove: `micronaut-data-jdbc`
   - Add: `micronaut-data-hibernate-jpa`

2. **Entities**:
   ```java
   // JDBC (old)
   import io.micronaut.data.annotation.*;

   @MappedEntity
   public class Entity {
       @Id
       @GeneratedValue(GeneratedValue.Type.AUTO)
       private Long id;
   }

   // JPA (new)
   import jakarta.persistence.*;

   @Entity
   public class Entity {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
   }
   ```

3. **Configuration**:
   ```yaml
   # JDBC (old)
   datasources:
     default:
       dialect: POSTGRES

   # JPA (new)
   jpa:
     default:
       properties:
         hibernate:
           hbm2ddl:
             auto: update
   ```

---

## Checklist for New JPA Project

- [ ] Parent POM: `micronaut-parent` 4.9.4
- [ ] Dependencies: `micronaut-data-hibernate-jpa`, `micronaut-serde-jackson`, `micronaut-jdbc-hikari`, `postgresql`
- [ ] Annotation processors: `micronaut-data-processor`, `micronaut-serde-processor`
- [ ] `application.yml`: datasources + jpa configuration
- [ ] Entities: `@Entity` with `jakarta.persistence.*`
- [ ] Repositories: `@Repository` extending `CrudRepository`
- [ ] DTOs: `@Serdeable` annotation (NOT `@Introspected`)
- [ ] Controller: `@Controller` with `@Post`, `@Get`, etc.
- [ ] Application: `Micronaut.run()` entry point

---

## Version Compatibility Matrix

| Micronaut | Hibernate | Java | Status |
|-----------|-----------|------|--------|
| 4.9.4 | 6.6.29.Final | 17+ | ✅ Tested |
| 4.9.x | 6.6.x | 17+ | ✅ Should work |
| 4.x | 6.x | 17+ | ⚠️ Verify |

---

## Reference Implementation

**Location**: `/tmp/micronaut4-jpa/example`

**Validated Files**:
- `pom.xml` - Complete dependency configuration
- `src/main/resources/application.yml` - Database config
- `src/main/java/example/TestEntity.java` - JPA entity example
- `src/main/java/example/TestRepository.java` - Repository example
- `src/main/java/example/TestController.java` - Controller with `@Serdeable` DTOs
- `test-e2e.sh` - E2E test script (8/8 assertions passed)

---

## Key Takeaways for LLMs

1. **Always use `@Serdeable`** for DTOs in Micronaut 4 with Serde
2. **Always import** `io.micronaut.serde.annotation.Serdeable`
3. **Always include** `micronaut-serde-processor` in annotation processors
4. **Always use** `jakarta.persistence.*` (not `javax.persistence.*`)
5. **Always validate** with E2E tests before deploying

---

**Last Updated**: 2025-10-18
**Tested With**: Micronaut 4.9.4, Hibernate 6.6.29.Final, PostgreSQL 16.10
**E2E Test Status**: ✅ 8/8 assertions passed
