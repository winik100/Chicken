package de.hhu.propra.chicken.stereotypes;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Secured({"ROLE_TUTOR", "ROLE_ADMIN"})
public @interface AdminAndTutorOnly {
}