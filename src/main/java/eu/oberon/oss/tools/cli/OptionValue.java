package eu.oberon.oss.tools.cli;

/**
 * Represents an option value.
 *
 * @param name        the option value name
 * @param description the option value description
 * @param value       the option value
 * @param <T>         the type of the option value
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record OptionValue<T>(
        T value,
        String name,
        String description
) {
    /**
     * Creates an option value with the given value and description.
     *
     * @param value       the option value
     * @param description the option value description
     *
     * @since 1.0.0
     */
    public OptionValue(T value, String description) {
        this(value, String.valueOf(value), description);
    }
}