package sk.upjs.paz.ui;

import javafx.scene.Scene;

public final class  ThemeManager {

    private static final String LIGHT = "/sk/upjs/paz/ui/css/lightTheme.css";
    private static final String DARK = "/sk/upjs/paz/ui/css/darkTheme.css";

    private static boolean darkMode = false;

    private ThemeManager() {
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean dark) {
        darkMode = dark;
    }

    public static void toggle() {
        darkMode = !darkMode;
    }

    public static void apply(Scene scene) {
        if (scene == null) return;

        scene.getStylesheets().remove(ThemeManager.class.getResource(LIGHT).toExternalForm());
        scene.getStylesheets().remove(ThemeManager.class.getResource(DARK).toExternalForm());

        String css = ThemeManager.class.getResource(darkMode ? DARK : LIGHT).toExternalForm();
        scene.getStylesheets().add(css);
    }
}
