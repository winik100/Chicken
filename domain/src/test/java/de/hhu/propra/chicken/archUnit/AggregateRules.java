package de.hhu.propra.chicken.archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import de.hhu.propra.chicken.stereotype.AggregateRoot;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class AggregateRules {

    private final JavaClasses klassen = new ClassFileImporter()
            .importPackages("de.hhu.propra.chicken.aggregates");

    @Test
    @Disabled
    @DisplayName("Nur Aggregate Roots dürfen public sein.")
    void test_1() throws Exception {
        ArchRule rule = classes()
                .that()
                .areNotAnnotatedWith(AggregateRoot.class)
                .should()
                .notBePublic();

        rule.check(klassen);
    }


}
