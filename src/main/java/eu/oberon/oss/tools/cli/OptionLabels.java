package eu.oberon.oss.tools.cli;

import picocli.CommandLine.Model.OptionSpec;

/**
 * Provides utility methods for formatting option labels.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class OptionLabels {
    private OptionLabels() {
    }

    /**
     * Formats the option label for the given option.
     *
     * @param option the option to format
     *
     * @return the formatted option label
     *
     * @since 1.0.0
     */
    public static String format(OptionSpec option) {
        String optionNames = String.join(", ", option.names());

        if (!requiresParameter(option)) {
            return optionNames;
        }

        return optionNames + "=" + option.paramLabel();
    }

    /**
     * Determines whether the given option requires a parameter.
     *
     * @param option the option to check
     *
     * @return true if the option requires a parameter, false otherwise
     *
     * @since 1.0.0
     */
    private static boolean requiresParameter(OptionSpec option) {
        return option.arity().min() > 0;
    }
}