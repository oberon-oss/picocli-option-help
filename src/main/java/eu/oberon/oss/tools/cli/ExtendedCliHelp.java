package eu.oberon.oss.tools.cli;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extended version of the picocli CommandLine.Help class that provides additional functionality for listing of values for command line options that have a
 * restricted number of all possible values.
 *
 * @since 1.0.0
 */
public class ExtendedCliHelp extends CommandLine.Help {
    private static final int OPTION_COLUMN_PADDING = 2;
    private static final int MIN_DESCRIPTION_COLUMN_WIDTH = 20;

    private final ColorScheme colorScheme;
    private final FormattingOptionRenderer optionRenderer;
    private final int optionColumnWidth;
    private final int descriptionColumnWidth;

    /**
     * Creates a new instance of the ExtendedCliHelp class.
     *
     * @param commandSpec    The command specification.
     * @param colorScheme    The color scheme.
     * @param helpFormatters The help formatters.
     *
     * @since 1.0.0
     */
    public ExtendedCliHelp(
            CommandSpec commandSpec,
            ColorScheme colorScheme,
            Map<String, HelpFormatter> helpFormatters
    ) {
        super(commandSpec, colorScheme);
        this.colorScheme = colorScheme;
        this.optionColumnWidth = calculateOptionColumnWidth(commandSpec.options());
        this.descriptionColumnWidth = calculateDescriptionColumnWidth(
                commandSpec.usageMessage().width(),
                optionColumnWidth
        );
        this.optionRenderer = new FormattingOptionRenderer(
                createTwoColumnOptionRenderer(),
                descriptionColumnWidth,
                helpFormatters
        );
    }

    @Override
    public Layout createDefaultLayout() {
        return new Layout(
                colorScheme,
                TextTable.forColumnWidths(colorScheme, optionColumnWidth, descriptionColumnWidth),
                optionRenderer,
                createDefaultParameterRenderer()
        );
    }

    /**
     * Creates a two-column option renderer.
     *
     * @return The two-column option renderer.
     *
     * @since 1.0.0
     */
    private IOptionRenderer createTwoColumnOptionRenderer() {
        return (option, parameterLabelRenderer, scheme) -> {
            List<Ansi.Text[]> result = new ArrayList<>();

            String[] descriptions = option.description();

            result.add(new Ansi.Text[]{
                    scheme.optionText(OptionLabels.format(option)),
                    scheme.text(descriptions.length == 0 ? "" : descriptions[0])
            });

            for (int index = 1; index < descriptions.length; index++) {
                result.add(new Ansi.Text[]{
                        scheme.text(""),
                        scheme.text(descriptions[index])
                });
            }

            return result.toArray(Ansi.Text[][]::new);
        };
    }

    /**
     * Calculates the width of the description column.
     *
     * @param usageWidth        The width of the usage column.
     * @param optionColumnWidth The width of the option column.
     *
     * @return The width of the description column.
     *
     * @since 1.0.0
     */
    private static int calculateDescriptionColumnWidth(int usageWidth, int optionColumnWidth) {
        return Math.max(
                MIN_DESCRIPTION_COLUMN_WIDTH,
                usageWidth - optionColumnWidth
        );
    }

    private static int calculateOptionColumnWidth(Iterable<OptionSpec> options) {
        int maxOptionLabelLength = 0;

        for (OptionSpec option : options) {
            if (option.hidden()) {
                continue;
            }

            maxOptionLabelLength = Math.max(maxOptionLabelLength, OptionLabels.format(option).length());
        }

        return maxOptionLabelLength + OPTION_COLUMN_PADDING;
    }
}