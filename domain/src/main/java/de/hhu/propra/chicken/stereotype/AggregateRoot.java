package de.hhu.propra.chicken.stereotype;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AggregateRoot {
}