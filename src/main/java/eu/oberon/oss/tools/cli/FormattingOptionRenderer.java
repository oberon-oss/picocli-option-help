package eu.oberon.oss.tools.cli;

import picocli.CommandLine.Model.OptionSpec;

import java.util.*;

import static picocli.CommandLine.Help.*;

public class FormattingOptionRenderer implements IOptionRenderer {
    private final IOptionRenderer defaultRenderer;
    private final int descriptionColumnWidth;
    private final Map<String, HelpFormatter> helpFormatters;

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

    private HelpFormatter findHelpFormatter(OptionSpec option) {
        return Arrays.stream(option.names())
                .map(helpFormatters::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}