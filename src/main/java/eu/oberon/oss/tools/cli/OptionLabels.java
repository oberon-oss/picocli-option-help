package eu.oberon.oss.tools.cli;

import picocli.CommandLine.Model.OptionSpec;

public final class OptionLabels {
    private OptionLabels() {
    }

    public static String format(OptionSpec option) {
        String optionNames = String.join(", ", option.names());

        if (!requiresParameter(option)) {
            return optionNames;
        }

        if (option.paramLabel() == null || option.paramLabel().isBlank()) {
            return optionNames;
        }

        return optionNames + "=" + option.paramLabel();
    }

    private static boolean requiresParameter(OptionSpec option) {
        return option.arity().min() > 0;
    }
}