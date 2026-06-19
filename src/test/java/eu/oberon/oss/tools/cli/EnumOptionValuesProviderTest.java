package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumOptionValuesProviderTest {

    @Test
    void shouldExposeAllEnumConstantsInDeclarationOrder() {
        OutputFormatValuesProvider provider = new OutputFormatValuesProvider();

        List<OptionValue<OutputFormat>> values = provider.values();

        assertEquals(3, values.size());

        assertEquals(OutputFormat.TEXT, values.getFirst().value());
        assertEquals("TEXT", values.get(0).name());
        assertEquals("Plain text", values.get(0).description());

        assertEquals(OutputFormat.JSON, values.get(1).value());
        assertEquals("JSON", values.get(1).name());
        assertEquals("JSON document", values.get(1).description());

        assertEquals(OutputFormat.XML, values.get(2).value());
        assertEquals("XML", values.get(2).name());
        assertEquals("XML document", values.get(2).description());
    }

    @Test
    void shouldAllowOverridingDisplayedNames() {
        LowerCaseOutputFormatValuesProvider provider = new LowerCaseOutputFormatValuesProvider();

        List<OptionValue<OutputFormat>> values = provider.values();

        assertEquals("text", values.get(0).name());
        assertEquals("json", values.get(1).name());
        assertEquals("xml", values.get(2).name());
    }

    private enum OutputFormat {
        TEXT,
        JSON,
        XML
    }

    private static class OutputFormatValuesProvider extends EnumOptionValuesProvider<OutputFormat> {

        private OutputFormatValuesProvider() {
            super(OutputFormat.class);
        }

        @Override
        protected String description(OutputFormat value) {
            return switch (value) {
                case TEXT -> "Plain text";
                case JSON -> "JSON document";
                case XML -> "XML document";
            };
        }
    }

    private static final class LowerCaseOutputFormatValuesProvider extends OutputFormatValuesProvider {

        @Override
        protected String name(OutputFormat value) {
            return value.name().toLowerCase(Locale.ROOT);
        }
    }
}