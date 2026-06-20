package eu.oberon.oss.tools.cli;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies the help information for an option value.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface OptionValueHelp {
    /**
     * Specifies the option values provider class.
     *
     * @return the option values provider class
     *
     * @since 1.0.0
     */
    Class<? extends OptionValuesProvider<?>> valuesProvider();

    /**
     * Specifies the heading for the option values.
     *
     * @return the heading for the option values
     *
     * @since 1.0.0
     */
    String heading() default "  Valid values:";

    /**
     * Specifies the indentation for the option values.
     *
     * @return the indentation for the option values
     *
     * @since 1.0.0
     */
    int indent() default 3;

    /**
     * Specifies the separator for the option values.
     *
     * @return the separator for the option values, which will be used to separate the option values from their descriptions
     *
     * @since 1.0.0
     */
    String separator() default " : ";
}