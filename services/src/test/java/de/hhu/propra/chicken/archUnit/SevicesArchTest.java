package de.hhu.propra.chicken.archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class SevicesArchTest {

    private final JavaClasses klassen = new ClassFileImporter()
            .importPackages("de.hhu.propra.chicken.aggregates");

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
    @DisplayName("Keine Field Injection.")
    void test_4() {
        ArchRule rule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

        rule.check(klassen);
    }
}
