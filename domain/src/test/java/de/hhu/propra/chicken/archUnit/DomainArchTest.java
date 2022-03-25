package de.hhu.propra.chicken.archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import de.hhu.propra.chicken.stereotypes.AggregateRoot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class DomainArchTest {

    private final JavaClasses klassen = new ClassFileImporter()
            .importPackages("de.hhu.propra.chicken.aggregates");

    @Test
    @DisplayName("Nur Aggregate Roots dürfen public sein.")
    void test_1() {
        ArchRule rule = classes()
                .that()
                .areNotAnnotatedWith(AggregateRoot.class)
                .should()
                .notBePublic();

        rule.check(klassen);
    }

    @Test
    @DisplayName("Keine public Attribute in Aggregate Roots, Zugriff nur über Methoden in der Root.")
    void test_2() {
        ArchRule rule = fields()
                .that()
                .areDeclaredInClassesThat()
                .areAnnotatedWith(AggregateRoot.class)
                .should()
                .notBePublic();

        rule.check(klassen);
    }

    @Test
    @DisplayName("Keine Klassen mit @Deprecated.")
    void test_3() {
        ArchRule rule = noClasses()
                .should()
                .beAnnotatedWith(Deprecated.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Keine Methoden mit @Deprecated.")
    void test_4() {
        ArchRule rule = noMethods()
                .should()
                .beAnnotatedWith(Deprecated.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Keine Konstruktoren mit @Deprecated.")
    void test_5() {
        ArchRule rule = noConstructors()
                .should()
                .beAnnotatedWith(Deprecated.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Keine Field Injection.")
    void test_6() {
        ArchRule rule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

        rule.check(klassen);
    }
}
