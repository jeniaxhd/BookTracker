package sk.upjs.paz.ui;

import javafx.scene.Parent;

/**
 * Simple helper to keep theme switching consistent across screens.
 * Controllers can reuse this instead of duplicating stylesheet toggling logic.
 */
public class ThemeManager {

    private final String lightTheme = getClass().getResource("/css/lightTheme.css").toExternalForm();
    private final String darkTheme = getClass().getResource("/css/darkTheme.css").toExternalForm();

    private boolean darkMode;

    public ThemeManager() {
        this(false);
    }

    public ThemeManager(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void applyTheme(Parent root) {
        if (root == null) {
            return;
        }

        var stylesheets = root.getStylesheets();
        stylesheets.clear();
        stylesheets.add(darkMode ? darkTheme : lightTheme);
    }

    public void toggle(Parent root) {
        darkMode = !darkMode;
        applyTheme(root);
    }

    public boolean isDarkMode() {
        return darkMode;
    }
}
