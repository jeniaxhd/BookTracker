package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

public class LibraryController {

    @FXML
    private BorderPane root;

    @FXML
    private TextField searchField;

    private final ThemeManager themeManager = new ThemeManager();
    private final ToggleGroup navGroup = new ToggleGroup();
    private final ToggleGroup filterGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        themeManager.applyTheme(root);
        wireNavigationToggleButtons();
        wireFilters();
    }

    @FXML
    private void onNavigateDashboard() {
        selectNavByText("Dashboard");
    }

    @FXML
    private void onNavigateLibrary() {
        selectNavByText("Library");
    }

    @FXML
    private void onNavigateCurrentlyReading() {
        selectNavByText("Currently Reading");
    }

    @FXML
    private void onNavigateStatistics() {
        selectNavByText("Statistics");
    }

    @FXML
    private void onNavigateSettings() {
        selectNavByText("Settings");
    }

    @FXML
    private void onSearchChanged() {
        if (searchField != null) {
            // TODO hook up to search/filter service
            System.out.println("Searching for: " + searchField.getText());
        }
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
    }

    private void wireFilters() {
        if (root == null) {
            return;
        }

        root.lookupAll(".filter-chip").forEach(node -> {
            if (node instanceof ToggleButton toggle) {
                toggle.setToggleGroup(filterGroup);
                if (toggle.isSelected()) {
                    filterGroup.selectToggle(toggle);
                }
            }
        });
    }

    private void selectNavByText(String text) {
        navGroup.getToggles().stream()
                .filter(toggle -> toggle instanceof ToggleButton)
                .map(toggle -> (ToggleButton) toggle)
                .filter(button -> text.equals(button.getText()))
                .findFirst()
                .ifPresent(navGroup::selectToggle);
    }
}
