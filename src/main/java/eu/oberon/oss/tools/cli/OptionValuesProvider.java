package eu.oberon.oss.tools.cli;

import java.util.List;

/**
 * Provides a list of option values.
 *
 * @param <T> the type of the option values
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface OptionValuesProvider<T> {
    /**
     * Returns a list of option value to be rendered as part of the command's usage help.
     *
     * @return a list of option values
     * @since 1.0.0
     */
    List<OptionValue<T>> values();
}