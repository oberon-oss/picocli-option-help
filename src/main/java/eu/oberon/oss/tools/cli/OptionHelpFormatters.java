package eu.oberon.oss.tools.cli;

import picocli.CommandLine.Model.CommandSpec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class OptionHelpFormatters {
    private OptionHelpFormatters() {
    }

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

    private static HelpFormatter createFormatter(Field field) {
        OptionValueHelp optionValueHelp = field.getAnnotation(OptionValueHelp.class);
        OptionValuesProvider<?> provider = instantiate(optionValueHelp.valuesProvider());
        List<? extends OptionValue<?>> values = provider.values();

        return new OptionValueListFormatter<>(
                values,
                OptionValue::name,
                OptionValue::description,
                optionValueHelp.heading(),
                optionValueHelp.indent(),
                optionValueHelp.separator()
        );
    }

    private static OptionValuesProvider<?> instantiate(
            Class<? extends OptionValuesProvider<?>> providerType
    ) {
        try {
            Constructor<? extends OptionValuesProvider<?>> constructor = providerType.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(
                    "Could not instantiate option values provider: " + providerType.getName(),
                    exception
            );
        }
    }
}