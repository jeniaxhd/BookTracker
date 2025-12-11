package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class AddBookController {

    @FXML
    private BorderPane root;

    // header buttons + icons
    @FXML
    private Button searchButton;

    @FXML
    private Button notificationsButton;

    @FXML
    private ToggleButton themeToggle;

    @FXML
    private ImageView searchIcon;

    @FXML
    private ImageView notificationsIcon;

    @FXML
    private ImageView themeIcon;

    // form fields
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

    @FXML
    private Button uploadCoverButton;

    // stylesheets
    private String lightThemeUrl;
    private String darkThemeUrl;

    // icons
    private Image searchLight;
    private Image searchDark;
    private Image bellLight;
    private Image bellDark;
    private Image moonIconImg;
    private Image sunIconImg;

    @FXML
    private void initialize() {
        lightThemeUrl = getClass().getResource("/css/lightTheme.css").toExternalForm();
        darkThemeUrl  = getClass().getResource("/css/darkTheme.css").toExternalForm();

        searchLight   = load("/img/logoLight/search.png");
        searchDark    = load("/img/logoDark/search.png");
        bellLight     = load("/img/logoLight/bell.png");
        bellDark      = load("/img/logoDark/bell.png");
        moonIconImg   = load("/img/logoLight/moon.png");
        sunIconImg    = load("/img/logoDark/sun.png");

        // when scene is ready – apply light theme
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                switchToLightTheme();
            }
        });

        // example options for combos
        if (categoryBox != null) {
            categoryBox.getItems().addAll(
                    "Fiction", "Non-fiction", "Fantasy", "Sci-Fi",
                    "Self-help", "Biography", "Other"
            );
        }

        if (formatBox != null) {
            formatBox.getItems().addAll(
                    "Paperback", "Hardcover", "E-book", "Audiobook", "Other"
            );
        }

        if (uploadCoverButton != null) {
            uploadCoverButton.setOnAction(e -> onUploadCover());
        }
    }

    private Image load(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }

    // ===== theme toggle =====

    @FXML
    private void onToggleTheme(ActionEvent event) {
        var stylesheets = root.getStylesheets();
        boolean darkActive = stylesheets.contains(darkThemeUrl);

        if (darkActive) {
            switchToLightTheme();
        } else {
            switchToDarkTheme();
        }
    }

    private void switchToLightTheme() {
        var stylesheets = root.getStylesheets();
        stylesheets.remove(darkThemeUrl);
        if (!stylesheets.contains(lightThemeUrl)) {
            stylesheets.add(lightThemeUrl);
        }

        if (searchIcon != null) {
            searchIcon.setImage(searchLight);
        }
        if (notificationsIcon != null) {
            notificationsIcon.setImage(bellLight);
        }
        if (themeIcon != null) {
            themeIcon.setImage(moonIconImg);
        }

        themeToggle.setSelected(false);
    }

    private void switchToDarkTheme() {
        var stylesheets = root.getStylesheets();
        stylesheets.remove(lightThemeUrl);
        if (!stylesheets.contains(darkThemeUrl)) {
            stylesheets.add(darkThemeUrl);
        }

        if (searchIcon != null) {
            searchIcon.setImage(searchDark);
        }
        if (notificationsIcon != null) {
            notificationsIcon.setImage(bellDark);
        }
        if (themeIcon != null) {
            themeIcon.setImage(sunIconImg);
        }

        themeToggle.setSelected(true);
    }

    // ===== header actions =====

    @FXML
    private void onSearch(ActionEvent event) {
        // later: open search dialog
    }

    @FXML
    private void onNotifications(ActionEvent event) {
        // later: open notifications panel
    }

    // ===== cover upload + save book =====

    private void onUploadCover() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upload cover");
        alert.setHeaderText(null);
        alert.setContentText("Cover upload is not implemented yet.");
        alert.showAndWait();
    }

    @FXML
    private void onSaveBook(ActionEvent event) {
        String title  = titleField != null ? titleField.getText().trim() : "";
        String author = authorField != null ? authorField.getText().trim() : "";

        if (title.isEmpty() || author.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing data");
            alert.setHeaderText("Please fill in required fields");
            alert.setContentText("Title and author are required.");
            alert.showAndWait();
            return;
        }

        String category = categoryBox != null ? categoryBox.getValue() : null;
        String format   = formatBox   != null ? formatBox.getValue()   : null;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book saved");
        alert.setHeaderText("Book added to your library");
        alert.setContentText(
                "Title: " + title + "\n" +
                        "Author: " + author + "\n" +
                        (category != null ? "Category: " + category + "\n" : "") +
                        (format != null   ? "Format: " + format   + "\n" : "")
        );
        alert.showAndWait();
    }
}
