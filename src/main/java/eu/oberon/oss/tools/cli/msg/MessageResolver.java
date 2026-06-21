package eu.oberon.oss.tools.cli.msg;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Defines the contract to allow users to resolve messages, based on a key.
 * <p>
 * The way the key is resolved is implementation-specific. Most commonly, the key is used to look up a message in a resource bundle, but if the user decides to
 * use a different approach, they can implement their own {@link MessageResolver}.
 *
 * @author TigerLilly
 * @since 1.1.0
 */
public interface MessageResolver {

    /**
     * The default message formatter used by this resolver.
     *
     * @since 1.1.0
     */
    MessageFormatter DEFAULT_FORMATTER = (pattern, locale, arguments) -> new MessageFormat(pattern, locale).format(arguments);

    /**
     * Resolves a message using the default formatter.
     *
     * @param key The key of the message to resolve.
     *
     * @return The resolved message.
     *
     * @since 1.1.0
     */
    String resolve(String key);

    /**
     * Resolves a message using the {@link #DEFAULT_FORMATTER}.
     *
     * @param key       The key of the message to resolve.
     * @param arguments The arguments to format the message with.
     *
     * @return The resolved message.
     *
     * @since 1.1.0
     */
    default String resolve(String key, Object... arguments) {
        return resolve(key, DEFAULT_FORMATTER, arguments);
    }

    /**
     * Resolves a message using the provided formatter.
     *
     * @param key       The key of the message to resolve.
     * @param formatter The formatter to use to format the message.
     * @param arguments The arguments to format the message with.
     *
     * @return The resolved message.
     *
     * @since 1.1.0
     */
    default String resolve(String key, MessageFormatter formatter, Object... arguments) {
        return formatter.format(resolve(key), locale(), arguments);
    }

    /**
     * Returns the locale used by this resolver.
     *
     * @return The locale used by this resolver.
     *
     * @since 1.1.0
     */
    Locale locale();
}