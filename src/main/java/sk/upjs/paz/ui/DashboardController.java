package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class DashboardController {

    @FXML
    private BorderPane root; // це fx:id="root" з FXML

    @FXML
    private ToggleButton themeToggle;

    private final ThemeManager themeManager = new ThemeManager();
    private final ToggleGroup navGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        themeManager.applyTheme(root);
        updateThemeToggleText();
        wireNavigationToggleButtons();
    }

    @FXML
    private void onToggleTheme() {
        themeManager.toggle(root);
        updateThemeToggleText();
    }

    @FXML
    private void onNavigateDashboard() {
        // already here; keep selected state consistent
        selectNavByText("Dashboard");
    }

    @FXML
    private void onNavigateLibrary() {
        selectNavByText("Library");
        // TODO navigate to Library scene
    }

    @FXML
    private void onNavigateCurrentlyReading() {
        selectNavByText("Currently Reading");
        // TODO navigate to Currently Reading scene
    }

    @FXML
    private void onNavigateStatistics() {
        selectNavByText("Statistics");
        // TODO navigate to Statistics scene
    }

    @FXML
    private void onNavigateSettings() {
        selectNavByText("Settings");
        // TODO open settings view once implemented
    }

    private void wireNavigationToggleButtons() {
        if (root == null) {
            return;
        }

        root.lookupAll(".sidebar-nav-button").forEach(node -> {
            if (node instanceof ToggleButton toggle) {
                toggle.setToggleGroup(navGroup);
                if (toggle.isSelected()) {
                    navGroup.selectToggle(toggle);
                }
            }
        });

        if (navGroup.getSelectedToggle() == null && !navGroup.getToggles().isEmpty()) {
            navGroup.selectToggle(navGroup.getToggles().getFirst());
        }
    }

    private void selectNavByText(String text) {
        navGroup.getToggles().stream()
                .filter(toggle -> toggle instanceof ToggleButton)
                .map(toggle -> (ToggleButton) toggle)
                .filter(button -> text.equals(button.getText()))
                .findFirst()
                .ifPresent(navGroup::selectToggle);
    }

    private void updateThemeToggleText() {
        if (themeToggle != null) {
            themeToggle.setText(themeManager.isDarkMode() ? "🌙" : "☀");
        }
    }
}
