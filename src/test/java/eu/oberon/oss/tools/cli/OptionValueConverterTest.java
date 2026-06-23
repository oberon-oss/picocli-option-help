package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptionValueConverterTest {

    private enum Color { RED, GREEN, BLUE }

    private static class ColorProvider implements OptionValuesProvider<Color> {
        @Override
        public List<OptionValue<Color>> values() {
            return List.of(
                    new OptionValue<>(Color.RED, "red", "The color red"),
                    new OptionValue<>(Color.GREEN, "green", "The color green"),
                    new OptionValue<>(Color.BLUE, "blue", "The color blue")
            );
        }
    }

    @Test
    void shouldConvertValidValues() {
        OptionValueConverter<Color> converter = new OptionValueConverter<>(new ColorProvider());

        assertEquals(Color.RED, converter.convert("red"));
        assertEquals(Color.GREEN, converter.convert("green"));
        assertEquals(Color.BLUE, converter.convert("blue"));
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        OptionValueConverter<Color> converter = new OptionValueConverter<>(new ColorProvider());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> converter.convert("yellow"));
        assertEquals("Invalid value: yellow", exception.getMessage());
    }

    @Test
    void shouldBeCaseSensitive() {
        OptionValueConverter<Color> converter = new OptionValueConverter<>(new ColorProvider());

        // Provider defines "red", so "RED" should fail
        assertThrows(IllegalArgumentException.class, () -> converter.convert("RED"));
    }

    @Test
    void shouldHandleNullInputToConvert() {
        OptionValueConverter<Color> converter = new OptionValueConverter<>(new ColorProvider());

        // Map.get(null) returns null, so it should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> converter.convert(null));
    }

    @Test
    void shouldHandleEmptyProvider() {
        OptionValueConverter<String> converter = new OptionValueConverter<>(Collections::emptyList);

        assertThrows(IllegalArgumentException.class, () -> converter.convert("anything"));
    }

    @Test
    void shouldThrowExceptionOnDuplicateNamesInProvider() {
        OptionValuesProvider<String> provider = () -> List.of(
                new OptionValue<>("v1", "name", "desc1"),
                new OptionValue<>("v2", "name", "desc2")
        );

        // Collectors.toMap throws IllegalStateException on duplicate keys
        assertThrows(IllegalStateException.class, () -> new OptionValueConverter<>(provider));
    }

    @Test
    void shouldThrowExceptionOnNullValueInProvider() {
        OptionValuesProvider<String> provider = () -> List.of(
                new OptionValue<>(null, "null-value", "desc")
        );

        // Collectors.toMap throws NullPointerException on null values
        assertThrows(NullPointerException.class, () -> new OptionValueConverter<>(provider));
    }

    @Test
    void shouldHandleNullNameInProvider() {
        OptionValuesProvider<String> provider = () -> List.of(
                new OptionValue<>("v1", null, "desc")
        );

        OptionValueConverter<String> converter = new OptionValueConverter<>(provider);
        assertEquals("v1", converter.convert(null));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowExceptionOnNullProvider() {
        assertThrows(NullPointerException.class, () -> new OptionValueConverter<>(null));
    }

    @Test
    void shouldHandleIntegerValues() {
        OptionValuesProvider<Integer> provider = () -> List.of(
                new OptionValue<>(1, "one", "The first number"),
                new OptionValue<>(2, "two", "The second number")
        );
        OptionValueConverter<Integer> converter = new OptionValueConverter<>(provider);

        assertEquals(1, converter.convert("one"));
        assertEquals(2, converter.convert("two"));
    }
}
