package sk.upjs.paz.ui.i18n;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18N {
    private static final List<Locale> SUPPORTED = List.of(new Locale("sk"), Locale.ENGLISH);
    private static final String BASE_NAME = "i18n.messages";

    private static final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(resolveLocale(Locale.getDefault()));

    private I18N() {}

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale newLocale) {
        locale.set(resolveLocale(newLocale));
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BASE_NAME, getLocale(), new UTF8Control());
    }

    public static String tr(String key, Object... args) {
        String value = getBundle().containsKey(key) ? getBundle().getString(key) : "!" + key + "!";
        return MessageFormat.format(value, args);
    }

    private static Locale resolveLocale(Locale candidate) {
        if (candidate == null) return Locale.ENGLISH;
        for (Locale l : SUPPORTED) {
            if (l.getLanguage().equalsIgnoreCase(candidate.getLanguage())) return l;
        }
        return Locale.ENGLISH;
    }

    private static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                        ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            try (var stream = loader.getResourceAsStream(resourceName)) {
                if (stream == null) return null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    return new java.util.PropertyResourceBundle(reader);
                }
            }
        }
    }
}
