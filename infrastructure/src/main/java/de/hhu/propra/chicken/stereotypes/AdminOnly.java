package de.hhu.propra.chicken.stereotypes;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Secured({"ROLE_ADMIN"})
public @interface AdminOnly {
}