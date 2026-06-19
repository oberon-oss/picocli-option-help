package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionValueListFormatterTest {

    @Test
    void shouldRenderHeadingFirst() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>("json", "json", "JSON output")
        ));

        List<String> lines = formatter.format(80);

        assertEquals("  Valid values:", lines.getFirst());
    }

    @Test
    void shouldAlignNamesBasedOnLongestName() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>("json", "json", "JSON output"),
                new OptionValue<>("xml", "xml", "XML output"),
                new OptionValue<>("text", "text", "Plain text output")
        ));

        List<String> lines = formatter.format(80);

        assertEquals(List.of(
                "  Valid values:",
                "   json : JSON output",
                "   xml  : XML output",
                "   text : Plain text output"
        ), lines);
    }

    @Test
    void shouldRespectCustomIndentAndSeparator() {
        OptionValueListFormatter<OptionValue<String>> formatter = new OptionValueListFormatter<>(
                List.of(new OptionValue<>("json", "json", "JSON output")),
                OptionValue::name,
                OptionValue::description,
                "Formats:",
                5,
                " - "
        );

        List<String> lines = formatter.format(80);

        assertEquals(List.of(
                "Formats:",
                "     json - JSON output"
        ), lines);
    }

    @Test
    void shouldWrapLongDescriptionsAndAlignContinuationLines() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>(
                        "json",
                        "json",
                        "JSON output with a fairly long description"
                )
        ));

        List<String> lines = formatter.format(24);

        assertEquals("  Valid values:", lines.get(0));
        assertEquals("   json : JSON output", lines.get(1));
        assertEquals("          with a fairly", lines.get(2));
        assertEquals("          long", lines.get(3));
        assertEquals("          description", lines.get(4));
    }

    @Test
    void shouldHandleBlankDescription() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>("json", "json", "")
        ));

        List<String> lines = formatter.format(80);

        assertEquals(List.of(
                "  Valid values:",
                "   json : "
        ), lines);
    }

    @Test
    void shouldHandleNullDescription() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>("json", "json", null)
        ));

        List<String> lines = formatter.format(80);

        assertEquals(List.of(
                "  Valid values:",
                "   json : "
        ), lines);
    }

    @Test
    void shouldHandleEmptyValuesList() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of());

        List<String> lines = formatter.format(80);

        assertEquals(List.of("  Valid values:"), lines);
    }

    @Test
    void shouldApplyLineMapperToEveryLine() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>("json", "json", "JSON output")
        ));

        List<Integer> lineLengths = formatter.format(80, String::length);

        assertEquals(List.of(
                "  Valid values:".length(),
                "   json : JSON output".length()
        ), lineLengths);
    }

    @Test
    void shouldNotFailForVeryNarrowWidth() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>("json", "json", "JSON output")
        ));

        List<String> lines = formatter.format(5);

        assertEquals("  Valid values:", lines.getFirst());
        assertTrue(lines.size() > 1);
    }

    @Test
    void shouldNormalizeRepeatedWhitespaceWhenWrappingDescriptions() {
        OptionValueListFormatter<OptionValue<String>> formatter = formatter(List.of(
                new OptionValue<>("json", "json", "JSON     formatted    output")
        ));

        List<String> lines = formatter.format(80);

        assertEquals(List.of(
                "  Valid values:",
                "   json : JSON formatted output"
        ), lines);
    }

    private static OptionValueListFormatter<OptionValue<String>> formatter(
            List<OptionValue<String>> values
    ) {
        return new OptionValueListFormatter<>(
                values,
                OptionValue::name,
                OptionValue::description,
                "  Valid values:",
                3,
                " : "
        );
    }
}