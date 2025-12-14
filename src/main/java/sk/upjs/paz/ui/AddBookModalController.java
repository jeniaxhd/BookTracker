package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddBookModalController {

    @FXML private BorderPane root;

    @FXML private Button closeButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Button uploadCoverButton;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private ComboBox<String> formatBox;
    @FXML private TextField languageField;
    @FXML private TextField tagsField;
    @FXML private TextArea notesArea;

    @FXML private FlowPane tagsPane;

    private File selectedCoverFile;

    @FXML
    private void initialize() {
        if (categoryBox != null) {
            categoryBox.getItems().setAll(
                    "Fiction", "Non-fiction", "Fantasy", "Sci-fi",
                    "Biography", "Study", "Other"
            );
        }

        if (formatBox != null) {
            formatBox.getItems().setAll(
                    "Hardcover", "Paperback", "E-book", "Audiobook"
            );
        }

        // Enter у полі tagsField -> додати тег
        if (tagsField != null) {
            tagsField.setOnAction(e -> addTagFromField());
        }
    }

    // ===== TAGS =====

    private void addTagFromField() {
        if (tagsPane == null || tagsField == null) return;

        String text = tagsField.getText();
        if (text == null) return;

        text = text.trim();
        if (text.isEmpty()) return;

        // прибираємо # якщо користувач сам ввів
        if (text.startsWith("#")) text = text.substring(1).trim();
        if (text.isEmpty()) return;

        // (опціонально) не додавати дублікати
        String normalized = text.toLowerCase();
        for (Node n : tagsPane.getChildren()) {
            if (n instanceof Label l) {
                String t = l.getText();
                if (t != null && t.equalsIgnoreCase("#" + normalized)) {
                    tagsField.clear();
                    return;
                }
            }
        }

        Label pill = new Label("#" + text);
        pill.getStyleClass().add("tag-pill");

        // клік по тегу -> видалити
        pill.setOnMouseClicked(e -> tagsPane.getChildren().remove(pill));

        tagsPane.getChildren().add(pill);
        tagsField.clear();
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

        if (!valid) return;

        // TODO: save to DB/service later

        closeWindow();
    }

    @FXML
    private void onUploadCover() {
        Window window = (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;
        if (window == null) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select book cover");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(window);
        if (file != null) {
            selectedCoverFile = file;
            if (uploadCoverButton != null) {
                uploadCoverButton.setText("Cover selected");
            }
        }
    }

    // ===== helpers =====

    private void closeWindow() {
        if (root == null || root.getScene() == null) return;
        Window w = root.getScene().getWindow();
        if (w != null) w.hide();
    }

    private boolean isEmpty(TextField field) {
        if (field == null) return true;
        String t = field.getText();
        return t == null || t.trim().isEmpty();
    }

    private void markError(Control control) {
        if (control == null) return;
        if (!control.getStyleClass().contains("field-error")) {
            control.getStyleClass().add("field-error");
        }
    }

    private void clearFieldErrors() {
        List<Node> nodes = new ArrayList<>();
        if (titleField != null) nodes.add(titleField);
        if (authorField != null) nodes.add(authorField);

        for (Node n : nodes) {
            n.getStyleClass().remove("field-error");
        }
    }
}
