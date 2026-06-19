package eu.oberon.oss.tools.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OptionValueListFormatter<T> implements HelpFormatter {
    private final List<T> values;
    private final Function<T, String> nameProvider;
    private final Function<T, String> descriptionProvider;
    private final String heading;
    private final int indent;
    private final String separator;
    private final int maxEntrySize;

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

    public List<String> format(int availableWidth) {
        return format(availableWidth, Function.identity());
    }

    public <T> List<T> format(int availableWidth, Function<String, T> lineMapper) {
        List<T> result = new ArrayList<>();
        result.add(lineMapper.apply(heading));

        values.stream()
                .flatMap(value -> formatValue(value, availableWidth).stream())
                .map(lineMapper)
                .forEach(result::add);

        return result;
    }

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

    private static <T> int calculateMaxEntrySize(List<T> values, Function<T, String> nameProvider) {
        return values.stream()
                .map(nameProvider)
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

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
