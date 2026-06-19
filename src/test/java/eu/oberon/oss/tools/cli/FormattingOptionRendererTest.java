package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.Help.IOptionRenderer;
import picocli.CommandLine.Help.IParamLabelRenderer;
import picocli.CommandLine.Model.OptionSpec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FormattingOptionRendererTest {

    @Test
    void shouldDelegateToDefaultRendererWhenNoFormatterExists() {
        AtomicBoolean defaultRendererCalled = new AtomicBoolean(false);

        Ansi.Text[][] defaultResult = new Ansi.Text[][]{
                {
                        Ansi.AUTO.text("--verbose"),
                        Ansi.AUTO.text("Verbose output")
                }
        };

        IOptionRenderer defaultRenderer = (option, parameterLabelRenderer, scheme) -> {
            defaultRendererCalled.set(true);
            return defaultResult;
        };

        FormattingOptionRenderer renderer = new FormattingOptionRenderer(
                defaultRenderer,
                80,
                Map.of()
        );

        OptionSpec option = OptionSpec.builder("--verbose")
                .description("Verbose output")
                .build();

        Ansi.Text[][] result = renderer.render(
                option,
                parameterLabelRenderer(),
                new ColorScheme.Builder().build()
        );

        assertTrue(defaultRendererCalled.get());
        assertArrayEquals(defaultResult, result);
    }

    @Test
    void shouldUseFormatterWhenLongOptionNameMatches() {
        AtomicBoolean defaultRendererCalled = new AtomicBoolean(false);

        FormattingOptionRenderer renderer = new FormattingOptionRenderer(
                (option, parameterLabelRenderer, scheme) -> {
                    defaultRendererCalled.set(true);
                    return new Ansi.Text[0][];
                },
                80,
                Map.of("--format", fixedFormatter())
        );

        OptionSpec option = OptionSpec.builder("-f", "--format")
                .arity("1")
                .paramLabel("FORMAT")
                .description("Output format.")
                .build();

        Ansi.Text[][] result = renderer.render(
                option,
                parameterLabelRenderer(),
                new ColorScheme.Builder().build()
                // ColorScheme.builder().build()
        );

        assertFalse(defaultRendererCalled.get());

        assertEquals("-f, --format=FORMAT", result[0][0].toString());
        assertEquals("Output format.", result[0][1].toString());

        assertEquals("", result[1][0].toString());
        assertEquals("  Valid values:", result[1][1].toString());

        assertEquals("", result[2][0].toString());
        assertEquals("   json : JSON document", result[2][1].toString());
    }

    @Test
    void shouldUseFormatterWhenShortOptionNameMatches() {
        FormattingOptionRenderer renderer = new FormattingOptionRenderer(
                (option, parameterLabelRenderer, scheme) -> new Ansi.Text[0][],
                80,
                Map.of("-f", fixedFormatter())
        );

        OptionSpec option = OptionSpec.builder("-f", "--format")
                .arity("1")
                .paramLabel("FORMAT")
                .description("Output format.")
                .build();

        Ansi.Text[][] result = renderer.render(
                option,
                parameterLabelRenderer(),
                new ColorScheme.Builder().build()
//                ColorScheme.builder().build()
        );

        assertEquals("-f, --format=FORMAT", result[0][0].toString());
        assertEquals("  Valid values:", result[1][1].toString());
    }

    @Test
    void shouldUseEmptyDescriptionWhenOptionHasNoDescription() {
        FormattingOptionRenderer renderer = new FormattingOptionRenderer(
                (option, parameterLabelRenderer, scheme) -> new Ansi.Text[0][],
                80,
                Map.of("--format", fixedFormatter())
        );

        OptionSpec option = OptionSpec.builder("--format")
                .arity("1")
                .paramLabel("FORMAT")
                .build();

        Ansi.Text[][] result = renderer.render(
                option,
                parameterLabelRenderer(),

                new ColorScheme.Builder().build()
        );

        assertEquals("--format=FORMAT", result[0][0].toString());
        assertEquals("", result[0][1].toString());
    }

    private static HelpFormatter fixedFormatter() {
        return new HelpFormatter() {
            @Override
            public List<String> format(int availableWidth) {
                return List.of(
                        "  Valid values:",
                        "   json : JSON document"
                );
            }

            @Override
            public <T> List<T> format(int availableWidth, java.util.function.Function<String, T> lineMapper) {
                return format(availableWidth).stream()
                        .map(lineMapper)
                        .toList();
            }
        };
    }

    private static IParamLabelRenderer parameterLabelRenderer() {
        return new IParamLabelRenderer() {
            @Override
            public Ansi.Text renderParameterLabel(
                    CommandLine.Model.ArgSpec argSpec,
                    Ansi ansi,
                    List<Ansi.IStyle> styles
            ) {
                throw new AssertionError("Parameter label renderer should not be used by this test");
            }

            @Override
            public String separator() {
                return "=";
            }
        };
    }
}