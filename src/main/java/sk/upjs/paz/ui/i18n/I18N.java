package sk.upjs.paz.ui.i18n;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

public final class I18N {
    private I18N() {}

    private static final String BASE = "i18n.messages";

    // Мінімум 2 мови: SK + EN (додай uk/cs/de якщо хочеш)
    private static final List<Locale> SUPPORTED = List.of(
            new Locale("sk"),
            Locale.ENGLISH,
            new Locale("sk")
    );

    private static final ObjectProperty<Locale> locale =
            new SimpleObjectProperty<>(resolve(Locale.getDefault()));

    public static ObjectProperty<Locale> localeProperty() { return locale; }
    public static Locale getLocale() { return locale.get(); }

    public static void setLocale(Locale newLocale) {
        locale.set(resolve(newLocale));
    }

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BASE, getLocale(), new UTF8Control());
    }

    public static String tr(String key, Object... args) {
        String pattern = getBundle().getString(key);
        return MessageFormat.format(pattern, args);
    }

    private static Locale resolve(Locale candidate) {
        if (candidate != null) {
            String lang = candidate.getLanguage();
            for (Locale l : SUPPORTED) {
                if (l.getLanguage().equalsIgnoreCase(lang)) return l;
            }
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
            try (InputStream stream = loader.getResourceAsStream(resourceName)) {
                if (stream == null) return null;
                try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    return new PropertyResourceBundle(reader);
                }
            }
        }
    }
}
