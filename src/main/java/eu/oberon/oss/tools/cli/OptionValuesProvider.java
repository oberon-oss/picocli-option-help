package eu.oberon.oss.tools.cli;

import java.util.List;

public interface OptionValuesProvider<T> {
    List<OptionValue<T>> values();
}