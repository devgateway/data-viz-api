package org.devgateway.viz.commons.domain.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates if the field can be used to calculate the prevalence
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Measure {

    String name();

    String label() default "[unassigned]";

    String expression() default "";

    Class delegate() default void.class;

    String group() default "Default";

    int position() default 0;

    String color() default "#555";

    Translation[] translations() default {};
}
