package eu.oberon.oss.tools.cli;

import picocli.CommandLine.ITypeConverter;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts option values that are in a different case representation to their corresponding actual value.
 * <p>
 * The created class is intended for use in {@link picocli.CommandLine.Command}
 * <p>
 * For example, assuming there is a class '<b>Action</b>' representing a limited set of allowed values, then the converter for the contained values in
 * '<b>Action</b>' can be registered as follows:
 *
 * <pre>{@code
 * {
 * ...
 * CommandLine commandLine = new CommandLine(new SomeCommand());
 *
 * commandLine.registerConverter(
 *         Action.class,
 *         new OptionValueConverter<>(new ActionValuesProvider())
 * );
 *
 * commandLine.execute(args);
 * ...
 * }
 * }</pre>
 *
 * @param <T> the type of the option value
 *
 * @author TigerLilly64
 * @since 1.1.0
 */
public final class OptionValueConverter<T> implements ITypeConverter<T> {
    private final Map<String, T> valuesByName;

    /**
     * Creates a new {@link OptionValueConverter} object.
     *
     * @param valuesProvider The option value provider that handles the option values to be converted.
     *
     * @since 1.1.0
     */
    public OptionValueConverter(OptionValuesProvider<T> valuesProvider) {
        this.valuesByName = valuesProvider.values().stream()
                .collect(Collectors.toMap(
                        OptionValue::name,
                        OptionValue::value
                ));
    }

    @Override
    public T convert(String value) {
        T result = valuesByName.get(value);

        if (result == null) {
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        return result;
    }
}