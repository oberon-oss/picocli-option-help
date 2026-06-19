package eu.oberon.oss.tools.cli;

import java.util.Arrays;
import java.util.List;

public abstract class EnumOptionValuesProvider<E extends Enum<E>> implements OptionValuesProvider<E> {
    private final Class<E> enumType;

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

    protected String name(E value) {
        return value.name();
    }

    protected abstract String description(E value);
}