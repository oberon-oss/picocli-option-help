package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.Model.CommandSpec;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtendedCliHelpTest {

    @Test
    void usageHelpShouldContainGeneratedOptionValueHelp() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new DemoCommand());
        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        Help help = new ExtendedCliHelp(
                spec,
                Help.defaultColorScheme(Help.Ansi.OFF),
                formatters
        );

        String usage = help.fullSynopsis()
                + System.lineSeparator()
                + help.optionList();

        assertTrue(usage.contains("--format=FORMAT"));
        assertTrue(usage.contains("Output format."));
        assertTrue(usage.contains("  Valid values:"));
        assertTrue(usage.contains("text"));
        assertTrue(usage.contains("Plain text"));
        assertTrue(usage.contains("json"));
        assertTrue(usage.contains("JSON document"));
        assertTrue(usage.contains("xml"));
        assertTrue(usage.contains("XML document"));
    }

    @Test
    void usageHelpShouldStillRenderNormalOptions() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new DemoCommand());
        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        Help help = new ExtendedCliHelp(
                spec,
                Help.defaultColorScheme(Help.Ansi.OFF),
                formatters
        );

        String usage = help.fullSynopsis()
                + System.lineSeparator()
                + help.optionList();

        assertTrue(usage.contains("--verbose"));
        assertTrue(usage.contains("Enable verbose output."));
    }

    @Test
    void hiddenOptionsShouldNotAppearInUsageHelp() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new DemoCommand());
        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        Help help = new ExtendedCliHelp(
                spec,
                Help.defaultColorScheme(Help.Ansi.OFF),
                formatters
        );

        String usage = help.fullSynopsis()
                + System.lineSeparator()
                + help.optionList();

        assertFalse(usage.contains("--this-is-a-hidden-option-with-a-very-long-name"));
    }

    @CommandLine.Command(
            name = "demo",
            description = "Demo command",
            mixinStandardHelpOptions = true
    )
    private static final class DemoCommand {

        @CommandLine.Option(
                names = {"-f", "--format"},
                description = "Output format.",
                paramLabel = "FORMAT"
        )
        @OptionValueHelp(valuesProvider = FormatValuesProvider.class)
        OutputFormat format;

        @CommandLine.Option(
                names = {"-v", "--verbose"},
                description = "Enable verbose output."
        )
        boolean verbose;

        @CommandLine.Option(
                names = "--this-is-a-hidden-option-with-a-very-long-name",
                hidden = true
        )
        boolean hidden;
    }

    private enum OutputFormat {
        TEXT,
        JSON,
        XML
    }

    public static final class FormatValuesProvider implements OptionValuesProvider<OutputFormat> {

        public FormatValuesProvider() {
            // required
        }

        @Override
        public List<OptionValue<OutputFormat>> values() {
            return List.of(
                    new OptionValue<>(OutputFormat.TEXT, "text", "Plain text"),
                    new OptionValue<>(OutputFormat.JSON, "json", "JSON document"),
                    new OptionValue<>(OutputFormat.XML, "xml", "XML document")
            );
        }
    }
}