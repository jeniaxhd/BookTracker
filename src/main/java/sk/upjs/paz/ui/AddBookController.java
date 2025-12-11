package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddBookController {

    @FXML
    private BorderPane root;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private ComboBox<String> formatBox;

    @FXML
    private TextField languageField;

    @FXML
    private TextField tagsField;

    @FXML
    private TextArea notesArea;

    private final ThemeManager themeManager = new ThemeManager();

    @FXML
    private ToggleButton themeToggle;

    private final ToggleGroup navGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        themeManager.applyTheme(root);
        updateThemeToggleText();
        wireNavigationToggleButtons();

        if (categoryBox != null) {
            categoryBox.getItems().setAll(
                    "Fiction", "Non-fiction", "Fantasy", "Mystery",
                    "Sci-Fi", "Biography", "Self-help"
            );
        }

        if (formatBox != null) {
            formatBox.getItems().setAll("Paperback", "Hardcover", "E-book", "Audiobook");
        }
    }

    @FXML
    private void onToggleTheme() {
        themeManager.toggle(root);
        updateThemeToggleText();
    }

    @FXML
    private void onSaveBook() {
        var validationErrors = new ArrayList<String>();
        if (titleField.getText() == null || titleField.getText().isBlank()) {
            validationErrors.add("Title is required.");
        }
        if (authorField.getText() == null || authorField.getText().isBlank()) {
            validationErrors.add("Author is required.");
        }

        if (!validationErrors.isEmpty()) {
            showValidationAlert(validationErrors);
            return;
        }

        // TODO integrate with persistence/service layer.
        System.out.println("Saving book:");
        System.out.println("Title: " + titleField.getText());
        System.out.println("Author: " + authorField.getText());
        System.out.println("Category: " + categoryBox.getValue());
        System.out.println("Format: " + formatBox.getValue());
        System.out.println("Language: " + languageField.getText());
        System.out.println("Tags: " + tagsField.getText());
        System.out.println("Notes: " + notesArea.getText());

        // TODO navigate back to Library or refresh list after save.
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
    private void onNavigateAddBook() {
        selectNavByText("Add Book");
    }

    private void showValidationAlert(List<String> validationErrors) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Missing information");
        alert.setHeaderText("Please fill in the required fields");
        alert.setContentText(validationErrors.stream().collect(Collectors.joining("\n")));
        alert.showAndWait();
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
