package de.hhu.propra.chicken.archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import de.hhu.propra.chicken.stereotypes.AdminAndTutorOnly;
import de.hhu.propra.chicken.stereotypes.AdminOnly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ControllerArchTest {

    private final JavaClasses klassen = new ClassFileImporter()
            .importPackages("de.hhu.propra.chicken.web.controllers");

    @Test
    @DisplayName("Der AdminController muss mit @AdminOnly annotiert sein, um Zugriff nur für Admins zu erlauben.")
    void test_1() {
        ArchRule rule = classes()
                .that()
                .haveSimpleNameStartingWith("Admin")
                .should()
                .beAnnotatedWith(AdminOnly.class)
                .andShould()
                .notBeAnnotatedWith(AdminAndTutorOnly.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Der TutorController muss mit @TutorOnly annotiert sein, um Zugriff nur für Admins " +
            "und Tutoren zu erlauben.")
    void test_2() {
        ArchRule rule = classes()
                .that()
                .haveSimpleNameStartingWith("Tutor")
                .should()
                .beAnnotatedWith(AdminAndTutorOnly.class)
                .andShould()
                .notBeAnnotatedWith(AdminOnly.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Der StudentController darf weder mit @TutorOnly noch mit @AdminOnly annotiert sein.")
    void test_3() {
        ArchRule rule = classes()
                .that()
                .haveSimpleNameStartingWith("Student")
                .should()
                .notBeAnnotatedWith(AdminOnly.class)
                .andShould()
                .notBeAnnotatedWith(AdminAndTutorOnly.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Jede Klasse im package de.hhu.propra.chicken.web.controllers muss mit @Controller annotiert sein.")
    void test_4() {
        ArchRule rule = classes()
                .should()
                .beAnnotatedWith(Controller.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Jeder Controller muss mit @Secured meta-annotiert sein.")
    void test_5() {
        ArchRule rule = classes()
                .that()
                .areAnnotatedWith(Controller.class)
                .should()
                .beMetaAnnotatedWith(Secured.class);

        rule.check(klassen);
    }
}
