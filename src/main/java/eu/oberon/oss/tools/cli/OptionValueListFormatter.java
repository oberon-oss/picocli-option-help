package eu.oberon.oss.tools.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Provides a formatter for a list of option values.
 *
 * @param <T> the type of the option values
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class OptionValueListFormatter<T> implements HelpFormatter {
    private final List<T> values;
    private final Function<T, String> nameProvider;
    private final Function<T, String> descriptionProvider;
    private final String heading;
    private final int indent;
    private final String separator;
    private final int maxEntrySize;

    /**
     * Creates a new OptionValueListFormatter with the given values, name provider, description provider, heading, indent, and separator.
     *
     * @param values              the list of option values
     * @param nameProvider        a function that provides the name for each option value
     * @param descriptionProvider a function that provides the description for each option value
     * @param heading             the heading for the list of option values
     * @param indent              the number of spaces to indent each line
     * @param separator           the separator to use between the option values and their descriptions
     *
     * @since 1.0.0
     */
    public OptionValueListFormatter(
            List<T> values,
            Function<T, String> nameProvider,
            Function<T, String> descriptionProvider,
            String heading,
            int indent,
            String separator
    ) {
        this.values = List.copyOf(values);
        this.nameProvider = nameProvider;
        this.descriptionProvider = descriptionProvider;
        this.heading = heading;
        this.indent = indent;
        this.separator = separator;
        this.maxEntrySize = calculateMaxEntrySize(values, nameProvider);
    }

    /**
     * Formats the list of option values using the specified available width.
     *
     * @param availableWidth the available width for formatting
     *
     * @return a list of formatted strings representing the option values
     *
     * @since 1.0.0
     */
    public List<String> format(int availableWidth) {
        return format(availableWidth, Function.identity());
    }

    /**
     * Formats the list of option values using the specified available width and line mapper.
     *
     * @param availableWidth the available width for formatting
     * @param lineMapper     a function that maps each formatted string to a different type
     * @param <O>            The type of objects that will be present in the returned list
     *
     * @return a list of formatted values of the specified type
     *
     * @since 1.0.0
     */
    public <O> List<O> format(int availableWidth, Function<String, O> lineMapper) {
        List<O> result = new ArrayList<>();
        result.add(lineMapper.apply(heading));

        values.stream()
                .flatMap(value -> formatValue(value, availableWidth).stream())
                .map(lineMapper)
                .forEach(result::add);

        return result;
    }

    /**
     * Formats a single option value using the specified available width.
     *
     * @param value          the option value to format
     * @param availableWidth the available width for formatting
     *
     * @return a list of formatted strings representing the option value
     *
     * @since 1.0.0
     */
    private List<String> formatValue(T value, int availableWidth) {
        String nameFormatter = "%-" + maxEntrySize + "s";
        String prefix = " ".repeat(indent)
                + String.format(nameFormatter, nameProvider.apply(value))
                + separator;

        String continuationPrefix = " ".repeat(prefix.length());

        int firstLineWidth = availableWidth - prefix.length();
        int continuationLineWidth = availableWidth - continuationPrefix.length();

        List<String> wrappedDescription = wrapWords(
                descriptionProvider.apply(value),
                firstLineWidth,
                continuationLineWidth
        );

        List<String> result = new ArrayList<>();

        if (wrappedDescription.isEmpty()) {
            result.add(prefix);
            return result;
        }

        result.add(prefix + wrappedDescription.getFirst());

        wrappedDescription.stream()
                .skip(1)
                .map(line -> continuationPrefix + line)
                .forEach(result::add);

        return result;
    }

    /**
     * Calculates the maximum entry size for a list of values using the specified name provider.
     *
     * @param values       the list of values to calculate the maximum entry size for
     * @param nameProvider a function that provides the name for each value
     *
     * @return the maximum entry size
     *
     * @since 1.0.0
     */
    private static <T> int calculateMaxEntrySize(List<T> values, Function<T, String> nameProvider) {
        return values.stream()
                .map(nameProvider)
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

    /**
     * Wraps a given text into multiple lines based on the specified first line width and continuation line width.
     *
     * @param text                  the text to wrap
     * @param firstLineWidth        the width of the first line
     * @param continuationLineWidth the width of the continuation lines
     *
     * @return a list of wrapped lines
     *
     * @since 1.0.0
     */
    private static List<String> wrapWords(String text, int firstLineWidth, int continuationLineWidth) {
        List<String> lines = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return lines;
        }

        String[] words = text.trim().split("\\s+");
        int currentLineWidth = Math.max(1, firstLineWidth);

        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.isEmpty()) {
                currentLine.append(word);
            } else if (currentLine.length() + 1 + word.length() <= currentLineWidth) {
                currentLine.append(' ').append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
                currentLineWidth = Math.max(1, continuationLineWidth);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}
