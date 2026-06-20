package eu.oberon.oss.tools.cli;

import java.util.List;
import java.util.function.Function;

/**
 * Describes how the help text for a command line option will be formatted.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface HelpFormatter {
    /**
     * Formats the help text.
     *
     * @param availableWidth The available width for the help text.
     *
     * @return The formatted help text.
     *
     * @since 1.0.0
     */
    List<String> format(int availableWidth);

    /**
     * Formats the help text using the provided line mapper.
     *
     * @param availableWidth The available width for the help text.
     * @param lineMapper     The function to map each line of the help text.
     * @param <T>            the type of the formatted help text
     *
     * @return The formatted help text.
     *
     * @since 1.0.0
     */
    <T> List<T> format(int availableWidth, Function<String, T> lineMapper);
}