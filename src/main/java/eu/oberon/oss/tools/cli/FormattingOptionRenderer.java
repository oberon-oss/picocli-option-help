package eu.oberon.oss.tools.cli;

import picocli.CommandLine.Model.OptionSpec;

import java.util.*;

import static picocli.CommandLine.Help.*;

/**
 * Formatting option renderer that uses a custom help formatter to format the option description.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class FormattingOptionRenderer implements IOptionRenderer {
    private final IOptionRenderer defaultRenderer;
    private final int descriptionColumnWidth;
    private final Map<String, HelpFormatter> helpFormatters;

    /**
     * Creates a new instance of the FormattingOptionRenderer class.
     *
     * @param defaultRenderer        The default option renderer. This will be returned when no custom help formatter is found for the option.
     * @param descriptionColumnWidth The width of the description column.
     * @param helpFormatters         The help formatters.
     *
     * @since 1.0.0
     */
    public FormattingOptionRenderer(
            IOptionRenderer defaultRenderer,
            int descriptionColumnWidth,
            Map<String, HelpFormatter> helpFormatters
    ) {
        this.defaultRenderer = defaultRenderer;
        this.descriptionColumnWidth = descriptionColumnWidth;
        this.helpFormatters = Map.copyOf(helpFormatters);
    }

    @Override
    public Ansi.Text[][] render(OptionSpec option, IParamLabelRenderer parameterLabelRenderer, ColorScheme scheme) {
        HelpFormatter helpFormatter = findHelpFormatter(option);

        if (helpFormatter == null) {
            return defaultRenderer.render(option, parameterLabelRenderer, scheme);
        }

        List<Ansi.Text[]> result = new ArrayList<>();

        result.add(new Ansi.Text[]{
                scheme.optionText(OptionLabels.format(option)),
                scheme.text(option.description().length == 0 ? "" : option.description()[0])
        });

        result.addAll(helpFormatter.format(
                descriptionColumnWidth,
                line -> new Ansi.Text[]{
                        scheme.text(""),
                        scheme.text(line)
                }
        ));

        return result.toArray(Ansi.Text[][]::new);
    }

    /**
     * Finds the help formatter for the given option.
     *
     * @param option The option for which to find the help formatter.
     *
     * @return The help formatter, or null if no custom help formatter is found.
     *
     * @since 1.0.0
     */
    private HelpFormatter findHelpFormatter(OptionSpec option) {
        return Arrays.stream(option.names())
                .map(helpFormatters::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}