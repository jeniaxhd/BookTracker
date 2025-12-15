package sk.upjs.paz.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddBookModalController {

    @FXML private BorderPane root;

    @FXML private TextField titleField;
    @FXML private TextField authorField;

    @FXML private ComboBox<String> categoryBox;
    @FXML private ComboBox<String> formatBox;

    @FXML private TextField pagesField;
    @FXML private TextField languageField;

    @FXML private TextField tagsField;
    @FXML private FlowPane tagsPane;

    @FXML private TextArea notesArea;

    @FXML private ImageView coverPreview;
    @FXML private Label coverPlaceholderLabel;
    @FXML private Button uploadCoverButton;

    private final Set<String> tags = new LinkedHashSet<>();
    private File selectedCoverFile;

    @FXML
    private void initialize() {
        // Categories / formats - replace with your real ones if you want
        if (categoryBox != null) {
            categoryBox.setItems(FXCollections.observableArrayList(
                    "Fiction", "Non-fiction", "Fantasy", "Sci-fi", "Mystery", "Romance", "Self-help", "Other"
            ));
        }
        if (formatBox != null) {
            formatBox.setItems(FXCollections.observableArrayList(
                    "Paperback", "Hardcover", "E-book", "Audiobook"
            ));
        }

        // Pages: digits only
        if (pagesField != null) {
            pagesField.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                return newText.matches("\\d*") ? change : null;
            }));
        }

        // Tags: Enter adds tag, Backspace on empty removes last tag
        if (tagsField != null) {
            tagsField.setOnAction(e -> addTagFromField());

            tagsField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.BACK_SPACE) {
                    String text = tagsField.getText();
                    if (text == null || text.isEmpty()) {
                        removeLastTag();
                    }
                }
            });
        }

        // Initial cover state
        if (coverPreview != null) {
            coverPreview.setImage(null);
        }
        if (coverPlaceholderLabel != null) {
            coverPlaceholderLabel.setVisible(true);
            coverPlaceholderLabel.setManaged(true);
        }
    }

    @FXML
    private void onUploadCover() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose cover image");
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(root.getScene().getWindow());
        if (file == null) return;

        // Load synchronously to immediately know if it fails
        Image img = new Image(file.toURI().toString(), false);

        if (img.isError()) {
            showError("Image can't be loaded. Please choose a PNG/JPG file.");
            return;
        }

        selectedCoverFile = file;

        coverPreview.setImage(img);

        coverPlaceholderLabel.setVisible(false);
        coverPlaceholderLabel.setManaged(false);

        if (uploadCoverButton != null) {
            uploadCoverButton.setText("Cover selected");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSaveBook() {
        String title = safeTrim(titleField.getText());
        String author = safeTrim(authorField.getText());
        String pagesText = safeTrim(pagesField.getText());

        if (title.isEmpty()) {
            showError("Book title is required.");
            return;
        }
        if (author.isEmpty()) {
            showError("Author is required.");
            return;
        }
        if (pagesText.isEmpty()) {
            showError("Pages is required.");
            return;
        }

        int pages;
        try {
            pages = Integer.parseInt(pagesText);
        } catch (NumberFormatException ex) {
            showError("Pages must be a number.");
            return;
        }
        if (pages <= 0) {
            showError("Pages must be greater than 0.");
            return;
        }

        String category = categoryBox != null ? categoryBox.getValue() : null;
        String format = formatBox != null ? formatBox.getValue() : null;
        String language = safeTrim(languageField.getText());
        String notes = notesArea != null ? safeTrim(notesArea.getText()) : "";

        List<String> tagsList = tags.stream().toList();

        // TODO: replace with your real save logic (DAO/service)
        // Example:
        // bookService.createBook(title, author, pages, category, format, language, tagsList, notes, selectedCoverFile);

        // Close modal after saving
        onCancel();
    }

    // ---------- Tags helpers ----------

    private void addTagFromField() {
        String raw = safeTrim(tagsField.getText());
        if (raw.isEmpty()) return;

        // Allow typing "a, b, c" and adding multiple at once
        String[] parts = raw.split("[,;]");
        boolean addedAny = false;

        for (String p : parts) {
            String tag = safeTrim(p);
            if (tag.isEmpty()) continue;

            // Avoid duplicates
            if (tags.add(tag)) {
                tagsPane.getChildren().add(createTagChip(tag));
                addedAny = true;
            }
        }

        if (addedAny) {
            tagsField.clear();
        }
    }

    private void removeLastTag() {
        if (tags.isEmpty()) return;

        String last = tags.stream().reduce((a, b) -> b).orElse(null);
        if (last == null) return;

        tags.remove(last);

        // Remove last chip node (chips are added in the same order)
        int n = tagsPane.getChildren().size();
        if (n > 0) {
            tagsPane.getChildren().remove(n - 1);
        }
    }

    private HBox createTagChip(String tag) {
        HBox chip = new HBox(6);
        chip.getStyleClass().add("tag-chip");

        Text label = new Text(tag);
        label.getStyleClass().add("tag-chip-text");

        Button remove = new Button("×");
        remove.getStyleClass().add("tag-chip-remove");
        remove.setFocusTraversable(false);
        remove.setOnAction(e -> {
            tags.remove(tag);
            tagsPane.getChildren().remove(chip);
        });

        chip.getChildren().addAll(label, remove);
        return chip;
    }

    // ---------- Utils ----------

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.initOwner(root.getScene().getWindow());
        alert.showAndWait();
    }
}
