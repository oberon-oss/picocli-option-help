package eu.oberon.oss.tools.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine.Model.OptionSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionLabelsTest {

    @Test
    void shouldFormatFlagOptionWithoutParameterLabel() {
        OptionSpec option = OptionSpec.builder("-h", "--help")
                .arity("0")
                .build();

        String result = OptionLabels.format(option);

        assertEquals("-h, --help", result);
    }

    @Test
    void shouldAppendParameterLabelForRequiredValueOption() {
        OptionSpec option = OptionSpec.builder("-f", "--format")
                .arity("1")
                .paramLabel("FORMAT")
                .build();

        String result = OptionLabels.format(option);

        assertEquals("-f, --format=FORMAT", result);
    }

    @Test
    void shouldUsePicocliDefaultParameterLabelWhenBlankParameterLabelIsConfigured() {
        OptionSpec option = OptionSpec.builder("--format")
                .arity("1")
                .paramLabel("   ")
                .build();

        String result = OptionLabels.format(option);

        assertEquals("--format=PARAM", result);
    }

    @Test
    void shouldFormatSingleNameOption() {
        OptionSpec option = OptionSpec.builder("--format")
                .arity("1")
                .paramLabel("FORMAT")
                .build();

        String result = OptionLabels.format(option);

        assertEquals("--format=FORMAT", result);
    }

    @Test
    void shouldNotAppendParameterLabelForOptionalBooleanStyleFlag() {
        OptionSpec option = OptionSpec.builder("--verbose")
                .arity("0")
                .paramLabel("LEVEL")
                .build();

        String result = OptionLabels.format(option);

        assertEquals("--verbose", result);
    }
}