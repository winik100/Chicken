package archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stereotype.AggregateRoot;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class AggregateRules {

    private final JavaClasses klassen = new ClassFileImporter()
            .importPackages("aggregates");

    @Test
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
