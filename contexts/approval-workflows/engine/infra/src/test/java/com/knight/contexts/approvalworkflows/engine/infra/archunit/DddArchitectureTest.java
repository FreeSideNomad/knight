package com.knight.contexts.approvalworkflows.engine.infra.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * ArchUnit tests enforcing DDD architecture rules and dependency constraints.
 */
class DddArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.knight.contexts.approvalworkflows.engine");
    }

    @Test
    void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()

            .layer("API").definedBy("..api..")
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..app..")
            .layer("Infrastructure").definedBy("..infra..")

            .whereLayer("API").mayOnlyBeAccessedByLayers("Domain", "Application", "Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

        rule.check(classes);
    }

    @Test
    void domainShouldNotDependOnApplicationOrInfrastructure() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..app..", "..infra..");

        rule.check(classes);
    }

    @Test
    void applicationShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..app..")
            .should().dependOnClassesThat().resideInAnyPackage("..infra..");

        rule.check(classes);
    }

    @Test
    void domainShouldNotDependOnSpring() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence.."
            );

        rule.check(classes);
    }
}
