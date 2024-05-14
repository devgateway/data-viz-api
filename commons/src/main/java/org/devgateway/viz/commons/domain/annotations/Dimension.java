package org.devgateway.viz.commons.domain.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface Dimension {
    String label()  default "[unassigned]";
    Class type() default Class.class;
    Translation[] translations() default {};
}
