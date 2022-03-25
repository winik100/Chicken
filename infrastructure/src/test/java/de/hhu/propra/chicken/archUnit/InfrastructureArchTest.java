package de.hhu.propra.chicken.archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class InfrastructureArchTest {

    private final JavaClasses klassen = new ClassFileImporter()
            .importPackages("de.hhu.propra.chicken");

    @Test
    @DisplayName("Keine Klassen mit @Deprecated.")
    void test_1() {
        ArchRule rule = noClasses()
                .should()
                .beAnnotatedWith(Deprecated.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Keine Methoden mit @Deprecated.")
    void test_2() {
        ArchRule rule = noMethods()
                .should()
                .beAnnotatedWith(Deprecated.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Keine Konstruktoren mit @Deprecated.")
    void test_3() {
        ArchRule rule = noConstructors()
                .should()
                .beAnnotatedWith(Deprecated.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Repository Interfaces und Implementierungen müssen mit @Repository annotiert sein.")
    void test_4() {
        ArchRule rule = classes()
                .that()
                .haveSimpleNameEndingWith("Repo")
                .or()
                .haveSimpleNameEndingWith("RepoImpl")
                .should()
                .beAnnotatedWith(Repository.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Configurations müssen mit @Configuratoin annotiert sein.")
    void test_5() {
        ArchRule rule = classes()
                .that()
                .haveSimpleNameEndingWith("Configuration")
                .should()
                .beAnnotatedWith(Configuration.class);

        rule.check(klassen);
    }
}
