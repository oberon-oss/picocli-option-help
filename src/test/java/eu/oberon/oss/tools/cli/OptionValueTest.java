package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OptionValueTest {

    @Test
    void constructorWithExplicitNameShouldStoreAllValues() {
        OptionValue<String> value = new OptionValue<>(
                "json",
                "JSON",
                "JSON output"
        );

        assertEquals("json", value.value());
        assertEquals("JSON", value.name());
        assertEquals("JSON output", value.description());
    }

    @Test
    void constructorWithoutExplicitNameShouldUseStringValueOfValue() {
        OptionValue<Integer> value = new OptionValue<>(
                42,
                "The answer"
        );

        assertEquals(42, value.value());
        assertEquals("42", value.name());
        assertEquals("The answer", value.description());
    }

    @Test
    void constructorWithoutExplicitNameShouldHandleNullValue() {
        OptionValue<Object> value = new OptionValue<>(
                null,
                "No value"
        );

        assertNull(value.value());
        assertEquals("null", value.name());
        assertEquals("No value", value.description());
    }
}