package com.knight.contexts.serviceprofiles.indirectclients.infra.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * ArchUnit tests to enforce DDD architecture rules.
 */
public class DddArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .importPackages("com.knight.contexts.serviceprofiles.indirectclients");

    @Test
    void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("API").definedBy("..api..")
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..app..")
            .layer("Infrastructure").definedBy("..infra..")
            .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Domain", "Application", "Infrastructure")
            .whereLayer("API").mayOnlyBeAccessedByLayers("API", "Domain", "Application", "Infrastructure");

        rule.check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infra..");

        rule.check(importedClasses);
    }

    @Test
    void apiShouldNotDependOnDomainOrAppOrInfra() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAnyPackage("..domain..", "..app..", "..infra..");

        rule.check(importedClasses);
    }
}
