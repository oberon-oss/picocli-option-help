package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OptionHelpFormattersTest {

    @Test
    void shouldCreateFormatterForAnnotatedOptionField() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new CommandWithAnnotatedOption());

        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        assertTrue(formatters.containsKey("--format"));

        HelpFormatter formatter = formatters.get("--format");
        List<String> lines = formatter.format(80);

        assertTrue(lines.contains("  Valid values:"));
        assertTrue(lines.contains("   text : Plain text"));
        assertTrue(lines.contains("   json : JSON document"));
        assertTrue(lines.contains("   xml  : XML document"));
    }

    @Test
    void shouldIgnoreOptionsWithoutOptionValueHelpAnnotation() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new CommandWithoutAnnotatedOption());

        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        assertFalse(formatters.containsKey("--verbose"));
        assertTrue(formatters.isEmpty());
    }

    @Test
    void shouldUseLongestOptionNameAsMapKey() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new CommandWithShortAndLongOption());

        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        assertTrue(formatters.containsKey("--format"));
        assertFalse(formatters.containsKey("-f"));
    }

    @Test
    void shouldRespectAnnotationAttributes() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new CommandWithCustomAnnotationAttributes());

        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        List<String> lines = formatters.get("--format").format(80);

        assertEquals(List.of(
                "Formats:",
                "     text - Plain text",
                "     json - JSON document",
                "     xml  - XML document"
        ), lines);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenProviderCannotBeInstantiated() {
        CommandSpec spec = CommandSpec.forAnnotatedObject(new CommandWithBrokenProvider());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> OptionHelpFormatters.from(spec)
        );

        assertTrue(exception.getMessage().contains("Could not instantiate option values provider"));
        assertInstanceOf(ReflectiveOperationException.class, exception.getCause());
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

        @SuppressWarnings("ClassEscapesDefinedScope")
        @Override
        public List<OptionValue<OutputFormat>> values() {
            return List.of(
                    new OptionValue<>(OutputFormat.TEXT, "text", "Plain text"),
                    new OptionValue<>(OutputFormat.JSON, "json", "JSON document"),
                    new OptionValue<>(OutputFormat.XML, "xml", "XML document")
            );
        }
    }

    private static final class BrokenValuesProvider implements OptionValuesProvider<String> {

        @SuppressWarnings("unused")
        private BrokenValuesProvider(String ignored) {
        }

        @Override
        public List<OptionValue<String>> values() {
            return List.of();
        }
    }

    private static final class CommandWithAnnotatedOption {

        @CommandLine.Option(names = "--format")
        @OptionValueHelp(valuesProvider = FormatValuesProvider.class)
        OutputFormat format;
    }

    private static final class CommandWithoutAnnotatedOption {

        @SuppressWarnings("unused")
        @CommandLine.Option(names = "--verbose")
        boolean verbose;
    }

    private static final class CommandWithShortAndLongOption {

        @CommandLine.Option(names = {"-f", "--format"})
        @OptionValueHelp(valuesProvider = FormatValuesProvider.class)
        OutputFormat format;
    }

    private static final class CommandWithCustomAnnotationAttributes {

        @CommandLine.Option(names = "--format")
        @OptionValueHelp(
                valuesProvider = FormatValuesProvider.class,
                heading = "Formats:",
                indent = 5,
                separator = " - "
        )
        OutputFormat format;
    }

    private static final class CommandWithBrokenProvider {

        @CommandLine.Option(names = "--broken")
        @OptionValueHelp(valuesProvider = BrokenValuesProvider.class)
        String broken;
    }
}