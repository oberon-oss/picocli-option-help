package eu.oberon.oss.tools.cli.msg;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceBundleMessageResolverTest {

    @Test
    void shouldResolveSimpleMessage() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        assertEquals("Simple message", resolver.resolve("simple"));
        assertEquals(Locale.ENGLISH, resolver.locale());
    }

    @Test
    void shouldResolveMessageWithArguments() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        assertEquals("Hello, World!", resolver.resolve("hello", "World"));
    }

    @Test
    void shouldResolveMessageWithDifferentLocale() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.FRENCH);

        assertEquals("Message simple", resolver.resolve("simple"));
        assertEquals("Bonjour, tout le monde !", resolver.resolve("hello", "tout le monde"));
        assertEquals(Locale.FRENCH, resolver.locale());
    }

    @Test
    void shouldWorkWithProvidedResourceBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle("test-messages", Locale.ENGLISH);
        MessageResolver resolver = new ResourceBundleMessageResolver(bundle);

        assertEquals("Simple message", resolver.resolve("simple"));
        assertEquals(Locale.ENGLISH, resolver.locale());
    }

    @Test
    void shouldResolveWithCustomFormatter() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);
        MessageFormatter customFormatter = (pattern, locale, arguments) -> "PREFIX: " + pattern + " [" + arguments[0] + "]";

        assertEquals("PREFIX: Hello, {0}! [World]", resolver.resolve("hello", customFormatter, "World"));
    }

    @Test
    void shouldThrowExceptionWhenKeyIsMissing() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        org.junit.jupiter.api.Assertions.assertThrows(
                java.util.MissingResourceException.class,
                () -> resolver.resolve("missing.key")
        );
    }

    @Test
    void shouldThrowExceptionWhenKeyIsNull() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> resolver.resolve(null)
        );
    }

    @Test
    void shouldHandleNullArgumentsInResolve() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        // MessageFormat.format(null) does not throw NPE, it returns the pattern as is
        assertEquals("Hello, {0}!", resolver.resolve("hello", (Object[]) null));
    }

    @Test
    void shouldHandleNullArgumentInResolve() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        // One of the arguments is null
        assertEquals("Hello, null!", resolver.resolve("hello", (Object) null));
    }

    @Test
    void shouldThrowExceptionWhenFormatterIsNull() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> resolver.resolve("simple", null, "arg")
        );
    }

    @Test
    void shouldThrowExceptionWhenBundleNameIsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> new ResourceBundleMessageResolver(null, Locale.ENGLISH)
        );
    }

    @Test
    void shouldThrowExceptionWhenLocaleIsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> new ResourceBundleMessageResolver("test-messages", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenResourceBundleIsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> new ResourceBundleMessageResolver(null)
        );
    }

    @Test
    void shouldHandleEmptyKey() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        org.junit.jupiter.api.Assertions.assertThrows(
                java.util.MissingResourceException.class,
                () -> resolver.resolve("")
        );
    }
}
