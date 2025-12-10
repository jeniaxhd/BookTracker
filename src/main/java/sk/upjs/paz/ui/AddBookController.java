package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

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

    private boolean dark = false;

    @FXML
    private void initialize() {
        // приклад початкових значень
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
        var stylesheets = root.getStylesheets();
        stylesheets.clear();

        if (dark) {
            // назад на світлу
            stylesheets.add(
                    getClass().getResource("/css/lightTheme.css").toExternalForm()
            );
        } else {
            // темна
            stylesheets.add(
                    getClass().getResource("/css/darkTheme.css").toExternalForm()
            );
        }

        dark = !dark;
    }

    @FXML
    private void onSaveBook() {
        // тут потім додаси збереження в БД / сервіс
        System.out.println("Saving book:");
        System.out.println("Title: " + titleField.getText());
        System.out.println("Author: " + authorField.getText());
        System.out.println("Category: " + categoryBox.getValue());
        System.out.println("Format: " + formatBox.getValue());
        System.out.println("Language: " + languageField.getText());
        System.out.println("Tags: " + tagsField.getText());
        System.out.println("Notes: " + notesArea.getText());
    }
}
