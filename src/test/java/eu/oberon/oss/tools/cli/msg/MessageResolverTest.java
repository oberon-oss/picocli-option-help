package eu.oberon.oss.tools.cli.msg;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageResolverTest {

    @Test
    void defaultFormatterShouldFormatMessage() {
        String result = MessageResolver.DEFAULT_FORMATTER.format("Hello, {0}!", Locale.ENGLISH, "World");
        assertEquals("Hello, World!", result);
    }

    @Test
    void defaultFormatterShouldHandleNullArguments() {
        // MessageFormat.format(null) returns the pattern with placeholders unformatted
        String result = MessageResolver.DEFAULT_FORMATTER.format("Hello, {0}!", Locale.ENGLISH, (Object[]) null);
        assertEquals("Hello, {0}!", result);
    }

    @Test
    void defaultFormatterShouldHandleNullPattern() {
        assertThrows(NullPointerException.class, () -> MessageResolver.DEFAULT_FORMATTER.format(null, Locale.ENGLISH, "arg"));
    }

    @Test
    void defaultFormatterShouldHandleNullLocale() {
        // MessageFormat works with null locale (uses default)
        String result = MessageResolver.DEFAULT_FORMATTER.format("Hello, {0}!", null, "World");
        assertEquals("Hello, World!", result);
    }
}
