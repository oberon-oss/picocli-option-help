# Picocli Option Help

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_picocli-option-help&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oberon-oss_picocli-option-help)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_picocli-option-help&metric=coverage)](https://sonarcloud.io/summary/new_code?id=oberon-oss_picocli-option-help)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_picocli-option-help&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_picocli-option-help)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_picocli-option-help&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_picocli-option-help)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_picocli-option-help&metric=bugs)](https://sonarcloud.io/summary/new_code?id=oberon-oss_picocli-option-help)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_picocli-option-help&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=oberon-oss_picocli-option-help)
[![MvnRepository](https://badges.mvnrepository.com/badge/eu.oberon-oss/picocli-option-help/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/eu.oberon-oss/picocli-option-help)


Picocli Option Help is a Java library designed to enhance [picocli](https://picocli.info/) by providing detailed listings of valid values for command-line options. It allows developers to specify custom providers for option values and descriptions, which are then rendered as part of the command's usage help.

## Features

- **Custom Value Providers**: Define exactly which values should be listed for an option.
- **Detailed Descriptions**: Add descriptions for each valid value.
- **Enhanced Usage Help**: Automatically integrate these listings into picocli's standard help output.
- **Flexible Formatting**: Configure indentation, headings, and separators for value listings.
- **Option Value Conversion**: Ensure accepted command-line values match the customized help descriptions.
- **Internationalization (i18n)**: Support for localized messages and headings using resource bundles.

## Requirements

- **Java**: 25 or higher
- **Maven**: 3.9.x or higher
- **Dependencies**: picocli, slf4j-api, lombok (compile only)

## Setup

Add the library to your Maven project:

```xml
<dependency>
    <groupId>eu.oberon-oss</groupId>
    <artifactId>picocli-option-help</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

### 1. Define an Option Value Provider

Implement the `OptionValuesProvider` interface to return a list of `OptionValue` objects.

```java
public class OutputFormatProvider implements OptionValuesProvider<Format> {
    @Override
    public List<OptionValue<Format>> values() {
        return List.of(
            new OptionValue<>(Format.TEXT, "text", "Plain text output"),
            new OptionValue<>(Format.JSON, "json", "JSON formatted output")
        );
    }
}
```

### 2. Annotate Your Command Option

Use `@OptionValueHelp` on your picocli `@Option` fields.

```java
@CommandLine.Command(name = "my-app")
public class MyApp {
    @CommandLine.Option(names = {"-f", "--format"}, description = "Output format")
    @OptionValueHelp(valuesProvider = OutputFormatProvider.class)
    Format format;
}
```

### 3. Use ExtendedCliHelp

When creating your `CommandLine` instance, use `ExtendedCliHelp` to render the usage message.

```java
import eu.oberon.oss.tools.cli.ExtendedCliHelp;
import eu.oberon.oss.tools.cli.HelpFormatter;
import eu.oberon.oss.tools.cli.OptionHelpFormatters;
import picocli.CommandLine.Help;
import picocli.CommandLine.Model.CommandSpec;
import java.util.Map;

public final class HelpExample {
    private HelpExample() {
    }

    public static String usage(Object command) {
        CommandSpec spec = CommandSpec.forAnnotatedObject(command);
        Map<String, HelpFormatter> formatters = OptionHelpFormatters.from(spec);

        Help help = new ExtendedCliHelp(
                spec,
                Help.defaultColorScheme(Help.Ansi.AUTO),
                formatters
        );

        return help.fullSynopsis()
                + System.lineSeparator()
                + help.optionList();
    }
}
```

### 4. Option Value Conversion

By default, picocli might not recognize the custom names defined in your `OptionValuesProvider` (for example, if you use lowercase or kebab-case for enums). You can use `OptionValueConverter` to ensure that picocli accepts exactly the values shown in the help.

```java
import eu.oberon.oss.tools.cli.OptionValueConverter;
import picocli.CommandLine;

public final class ConversionExample {
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new MyApp());

        commandLine.registerConverter(
            ExecutionMode.class,
            new OptionValueConverter<>(new KebabModeProvider())
        );

        commandLine.execute(args);
    }
}
```

### Examples

#### String Options

```java
public static class EnvProvider implements OptionValuesProvider<String> {
    @Override
    public List<OptionValue<String>> values() {
        return List.of(
            new OptionValue<>("dev", "dev", "Development environment"),
            new OptionValue<>("test", "test", "Testing environment"),
            new OptionValue<>("prod", "prod", "Production environment")
        );
    }
}
```

Rendered help:
```text
-e, --env=<env>  Target environment
                   Valid values:
                    dev  : Development environment
                    test : Testing environment
                    prod : Production environment
```

#### Enum Options

You can extend `EnumOptionValuesProvider` for a more convenient way to provide enum values.

```java
public enum LogLevel { INFO, WARN, ERROR }

public static class LevelProvider extends EnumOptionValuesProvider<LogLevel> {
    public LevelProvider() { super(LogLevel.class); }
    @Override
    protected String description(LogLevel value) {
        return switch (value) {
            case INFO -> "Informational messages";
            case WARN -> "Warning messages";
            case ERROR -> "Error messages";
        };
    }
}
```

Rendered help:
```text
-l, --level=<level>  Log level
                       Valid values:
                        INFO  : Informational messages
                        WARN  : Warning messages
                        ERROR : Error messages
```

##### Customizing Displayed Names for Enum Values

You can override the `name()` method to customize how enum values are displayed in the help message. For example, to use `kebab-case` instead of the default `UPPER_SNAKE_CASE`:

```java
public enum ExecutionMode { FAST_TRACK, SAFE_MODE, DRY_RUN }

public static class KebabModeProvider extends EnumOptionValuesProvider<ExecutionMode> {
    public KebabModeProvider() { super(ExecutionMode.class); }
    
    @Override
    protected String name(ExecutionMode value) {
        return value.name().toLowerCase().replace('_', '-');
    }

    @Override
    protected String description(ExecutionMode value) {
        return switch (value) {
            case FAST_TRACK -> "Fast execution path";
            case SAFE_MODE -> "Safe execution with extra checks";
            case DRY_RUN -> "Simulate execution without changes";
        };
    }
}
```

This changes the displayed names in the rendered help:

```text
-m, --mode=<mode>  Execution mode
                     Valid values:
                      fast-track : Fast execution path
                      safe-mode  : Safe execution with extra checks
                      dry-run    : Simulate execution without changes
```

#### Integer Options

```java
public static class PortProvider implements OptionValuesProvider<Integer> {
    @Override
    public List<OptionValue<Integer>> values() {
        return List.of(
            new OptionValue<>(80, "80", "HTTP port"),
            new OptionValue<>(443, "443", "HTTPS port"),
            new OptionValue<>(8080, "8080", "Alternative HTTP port")
        );
    }
}
```

Rendered help:
```text
-p, --port=<port>  Server port
                     Valid values:
                      80   : HTTP port
                      443  : HTTPS port
                      8080 : Alternative HTTP port
```

### Internationalization (i18n)

The library provides support for localizing messages and headings using the `MessageResolver` interface.

#### 1. Using ResourceBundleMessageResolver

You can use the built-in `ResourceBundleMessageResolver` to resolve messages from a `ResourceBundle`.

```java
MessageResolver resolver = new ResourceBundleMessageResolver("my-messages", Locale.ENGLISH);
String heading = resolver.resolve("my.heading.key");
String description = resolver.resolve("my.value.description.key", "OptionalArgument");
```

#### 2. Localizing Option Headings

By default, the `@OptionValueHelp` annotation uses a message key `eu.oberon.oss.tools.cli.option.values.heading` for its heading. This key is automatically resolved if you don't provide a custom heading.

```java
@OptionValueHelp(valuesProvider = MyProvider.class) // Uses default localized heading
private String someValue;
```

To provide your own localized heading, you can resolve it before passing it to the annotation if it's constant, or more dynamically when building your `HelpFormatter`.

#### 3. Localizing Value Descriptions in Providers

You can use a `MessageResolver` inside your `OptionValuesProvider` to return localized descriptions.

```java
public class LocalizedProvider implements OptionValuesProvider<String> {
    private final MessageResolver resolver;

    public LocalizedProvider(MessageResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public List<OptionValue<String>> values() {
        return List.of(
            new OptionValue<>("v1", "v1", resolver.resolve("value.v1.description"))
        );
    }
}
```

## Project Structure

- `src/main/java`: Core logic and annotations.
    - `eu.oberon.oss.tools.cli.ExtendedCliHelp`: The main class for generating extended help.
    - `eu.oberon.oss.tools.cli.OptionValueHelp`: Annotation for options.
    - `eu.oberon.oss.tools.cli.OptionValuesProvider`: Interface for value providers.
    - `eu.oberon.oss.tools.cli.OptionValueConverter`: A Picocli converter that uses an `OptionValuesProvider`.
    - `eu.oberon.oss.tools.cli.msg.MessageResolver`: Interface for message resolution.
    - `eu.oberon.oss.tools.cli.msg.ResourceBundleMessageResolver`: Implementation using resource bundles.
- `src/test/java`: Unit tests and usage examples.

## Scripts

Use standard Maven commands:

- **Build**: `mvn clean install`
- **Test**: `mvn test`
- **Checkstyle**: `mvn checkstyle:check` (if configured)

## Environment Variables

- No specific environment variables are required for this library.

## License

This project is licensed under the [MIT License](LICENSE).
