package eu.oberon.oss.tools.cli;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface OptionValueHelp {
    Class<? extends OptionValuesProvider<?>> valuesProvider();

    String heading() default "  Valid values:";

    int indent() default 3;

    String separator() default " : ";
}