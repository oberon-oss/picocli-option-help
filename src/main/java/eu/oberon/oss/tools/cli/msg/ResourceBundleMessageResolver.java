package eu.oberon.oss.tools.cli.msg;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A {@link MessageResolver} that uses a {@link ResourceBundle} to resolve messages.
 *
 * @author TigerLilly64
 * @since 1.1.0
 */
public final class ResourceBundleMessageResolver implements MessageResolver {

    private final Locale locale;
    private final ResourceBundle resourceBundle;

    /**
     * Creates a new {@link ResourceBundleMessageResolver} with the given {@link ResourceBundle}.
     *
     * @param resourceBundle The {@link ResourceBundle} to use.
     *
     * @since 1.1.0
     */
    public ResourceBundleMessageResolver(ResourceBundle resourceBundle) {
        this.locale = resourceBundle.getLocale();
        this.resourceBundle = resourceBundle;
    }

    /**
     * Creates a new {@link ResourceBundleMessageResolver} with the given bundle name and locale.
     *
     * @param bundleName The name of the bundle to use.
     * @param locale     The locale to use.
     *
     * @since 1.1.0
     */
    public ResourceBundleMessageResolver(String bundleName, Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle(bundleName, locale);
    }

    @Override
    public String resolve(String key) {
        return resourceBundle.getString(key);
    }

    @Override
    public Locale locale() {
        return locale;
    }
}