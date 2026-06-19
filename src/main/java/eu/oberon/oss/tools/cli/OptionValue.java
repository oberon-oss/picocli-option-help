package eu.oberon.oss.tools.cli;

public record OptionValue<T>(
        T value,
        String name,
        String description
) {
    public OptionValue(T value, String description) {
        this(value, String.valueOf(value), description);
    }
}