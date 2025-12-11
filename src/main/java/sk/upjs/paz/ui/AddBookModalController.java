package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

public class AddBookModalController {

    @FXML
    private BorderPane root;

    @FXML
    private Button closeButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button uploadCoverButton;

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

    private File selectedCoverFile;

    @FXML
    private void initialize() {
        categoryBox.getItems().setAll(
                "Fiction",
                "Non-fiction",
                "Fantasy",
                "Sci-fi",
                "Biography",
                "Study",
                "Other"
        );

        formatBox.getItems().setAll(
                "Hardcover",
                "Paperback",
                "E-book",
                "Audiobook"
        );
    }

    // ===== actions =====

    @FXML
    private void onCancel() {
        closeWindow();
    }

    @FXML
    private void onSaveBook() {
        clearFieldErrors();

        boolean valid = true;

        if (isEmpty(titleField)) {
            markError(titleField);
            valid = false;
        }
        if (isEmpty(authorField)) {
            markError(authorField);
            valid = false;
        }

        if (!valid) {
            // TODO: show info / tooltip if потрібно
            return;
        }

        // TODO: create Book object and save

        closeWindow();
    }

    @FXML
    private void onUploadCover() {
        Window window = root.getScene() != null ? root.getScene().getWindow() : null;
        if (window == null) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select book cover");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(window);
        if (file != null) {
            selectedCoverFile = file;
            uploadCoverButton.setText("Cover selected");
        }
    }

    // ===== helpers =====

    private void closeWindow() {
        if (root != null && root.getScene() != null) {
            Window w = root.getScene().getWindow();
            if (w != null) {
                w.hide();
            }
        }
    }

    private boolean isEmpty(TextField field) {
        String t = field.getText();
        return t == null || t.trim().isEmpty();
    }

    private void markError(Control control) {
        if (!control.getStyleClass().contains("field-error")) {
            control.getStyleClass().add("field-error");
        }
    }

    private void clearFieldErrors() {
        List<Node> nodes = List.of(titleField, authorField);
        for (Node n : nodes) {
            n.getStyleClass().remove("field-error");
        }
    }
}
