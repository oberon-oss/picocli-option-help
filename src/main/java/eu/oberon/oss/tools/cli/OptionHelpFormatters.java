package eu.oberon.oss.tools.cli;

import picocli.CommandLine.Model.CommandSpec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides formatters for option help.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class OptionHelpFormatters {

    // Final class should NOT be extended.
    private OptionHelpFormatters() {
    }

    /**
     * Creates a map of option help formatters from the given command specification for any option annotated with {@link OptionValueHelp}.
     *
     * @param commandSpec the command specification
     *
     * @return a map of option help formatters
     */
    public static Map<String, HelpFormatter> from(CommandSpec commandSpec) {
        return commandSpec.options().stream()
                .filter(option -> option.userObject() instanceof Field)
                .map(option -> Map.entry(option, (Field) option.userObject()))
                .filter(entry -> entry.getValue().isAnnotationPresent(OptionValueHelp.class))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().longestName(),
                        entry -> createFormatter(entry.getValue())
                ));
    }

    /**
     * Creates a help formatter for an option annotated with {@link OptionValueHelp}.
     *
     * @param field the field annotated with {@link OptionValueHelp}
     *
     * @return the help formatter
     *
     * @since 1.0.0
     */
    private static HelpFormatter createFormatter(Field field) {
        OptionValueHelp optionValueHelp = field.getAnnotation(OptionValueHelp.class);
        OptionValuesProvider<?> provider = instantiate(optionValueHelp.valuesProvider());

        return new OptionValueListFormatter<>(
                provider.values(),
                OptionValue::name,
                OptionValue::description,
                optionValueHelp.heading(),
                optionValueHelp.indent(),
                optionValueHelp.separator()
        );
    }

    /**
     * Instantiates an option values provider.
     * Note: the option values provider must have a <b>public</b> no-argument constructor.
     *
     * @param providerType the option values provider type
     *
     * @return the option values provider
     *
     * @since 1.0.0
     */
    private static OptionValuesProvider<?> instantiate(
            Class<? extends OptionValuesProvider<?>> providerType
    ) {
        try {
            Constructor<? extends OptionValuesProvider<?>> constructor = providerType.getConstructor();
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(
                    "Could not instantiate option values provider: " + providerType.getName(),
                    exception
            );
        }
    }
}