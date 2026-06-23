package eu.oberon.oss.tools.cli.msg;

import java.util.Locale;

/**
 * Defines the contract to allow users to provided custom message formatting.
 *
 * @author TigerLilly
 * @since 1.1.0
 */
@FunctionalInterface
public interface MessageFormatter {
    /**
     * Formats a message pattern using the provided locale and arguments.
     *
     * @param pattern   The message pattern to format.
     * @param locale    The locale to use for formatting.
     * @param arguments The arguments to use for formatting.
     *
     * @return The formatted message.
     * @since 1.1.0
     */
    String format(String pattern, Locale locale, Object... arguments);
}