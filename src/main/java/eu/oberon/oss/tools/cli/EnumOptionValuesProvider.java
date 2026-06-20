package eu.oberon.oss.tools.cli;

import java.util.Arrays;
import java.util.List;

/**
 * Provides a list of enum values as option values.
 * <p>
 * This functionality is kept out of enum types to avoid coupling between enums and the picocli-option-help library
 *
 * @param <E> The enum for which values are provided
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public abstract class EnumOptionValuesProvider<E extends Enum<E>> implements OptionValuesProvider<E> {
    private final Class<E> enumType;

    /**
     * Constructor to initialize the enum type for which values are provided.
     *
     * @param enumType The enum type for which values are provided
     *
     * @since 1.0.0
     */
    protected EnumOptionValuesProvider(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public List<OptionValue<E>> values() {
        return Arrays.stream(enumType.getEnumConstants())
                .map(value -> new OptionValue<>(
                        value,
                        name(value),
                        description(value)
                ))
                .toList();
    }

    /**
     * Allows access to the enum entry name.
     *
     * @param value The enum entry for which to return the name.
     *
     * @return The enum entry name.
     *
     * @since 1.0.0
     */
    protected String name(E value) {
        return value.name();
    }

    /**
     * Returns the description of the enum entry.
     *
     * @param value The enum entry for which to return the description.
     *
     * @return The enum entry description.
     *
     * @since 1.0.0
     */
    protected abstract String description(E value);
}