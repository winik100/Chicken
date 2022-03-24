package de.hhu.propra.chicken.archUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;


public class ArchUnitTests {

    private final JavaClasses klassen = new ClassFileImporter()
            .importPackages("de.hhu.propra.chicken.web");

    @Test
    @DisplayName("Jeder Controller MUSS mit @Secured annotiert sein.")
    void test_1(){
        ArchRule rule = classes()
                .that()
                .areAnnotatedWith(Controller.class)
                .should()
                .beAnnotatedWith(Secured.class);

        rule.check(klassen);
    }

    @Test
    @DisplayName("Jede Klasse im package de.hhu.propra.chicken.web.controllers MUSS mit @Controller annotiert sein.")
    void test_2(){
        ArchRule rule = classes()
                .that()
                .resideInAPackage("..controllers..")
                .should().beAnnotatedWith(Controller.class);

        rule.check(klassen);
    }

}
