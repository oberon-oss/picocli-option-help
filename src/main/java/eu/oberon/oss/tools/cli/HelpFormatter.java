package eu.oberon.oss.tools.cli;

import java.util.List;
import java.util.function.Function;

public interface HelpFormatter {
    List<String> format(int availableWidth);

    <T> List<T> format(int availableWidth, Function<String, T> lineMapper);
}