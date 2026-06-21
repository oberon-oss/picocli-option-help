package eu.oberon.oss.tools.cli.msg;

import eu.oberon.oss.tools.cli.OptionValue;
import eu.oberon.oss.tools.cli.OptionValueListFormatter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageResolverUsageTest {

    @Test
    void shouldResolveOptionValueDescriptions() {
        MessageResolver resolver = new ResourceBundleMessageResolver("test-messages", Locale.ENGLISH);

        List<OptionValue<String>> values = List.of(
                new OptionValue<>("v1", "simple"),
                new OptionValue<>("v2", "hello")
        );

        OptionValueListFormatter<OptionValue<String>> formatter = new OptionValueListFormatter<>(
                values,
                OptionValue::name,
                v -> {
                    if (v.description().equals("hello")) {
                        return resolver.resolve(v.description(), "User");
                    }
                    return resolver.resolve(v.description());
                },
                resolver.resolve("eu.oberon.oss.tools.cli.option.values.heading"),
                2,
                " : "
        );

        List<String> formatted = formatter.format(80);

        assertEquals("Allowed values:", formatted.get(0));
        assertEquals("  v1 : Simple message", formatted.get(1));
        assertEquals("  v2 : Hello, User!", formatted.get(2));
    }
}
