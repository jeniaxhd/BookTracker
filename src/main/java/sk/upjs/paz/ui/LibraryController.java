package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryController {

    @FXML
    private BorderPane root;

    // Sidebar navigation
    @FXML
    private ToggleButton dashboardNavButton;

    @FXML
    private ToggleButton libraryNavButton;

    @FXML
    private ToggleButton currentlyReadingNavButton;

    @FXML
    private ToggleButton statisticsNavButton;

    @FXML
    private ToggleButton settingsNavButton;

    // User profile
    @FXML
    private Button userProfileButton;

    @FXML
    private Label userNameLabel;

    // Header
    @FXML
    private Label headerTitleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button filterButton;

    @FXML
    private ImageView filterIcon;

    @FXML
    private ToggleButton themeToggle;

    @FXML
    private ImageView themeIcon;

    @FXML
    private Button addBookButton;

    // Content
    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private VBox contentRoot;

    // Stylesheets
    private String lightThemeUrl;
    private String darkThemeUrl;

    // Theme icons
    private Image moonIcon;   // icon shown in light theme
    private Image sunIcon;    // icon shown in dark theme

    // Filter (settings) icons
    private Image filterLight; // for light background
    private Image filterDark;  // for dark background

    @FXML
    private void initialize() {
        // Group navigation buttons so only one is selected at a time
        ToggleGroup navGroup = new ToggleGroup();
        dashboardNavButton.setToggleGroup(navGroup);
        libraryNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setToggleGroup(navGroup);
        statisticsNavButton.setToggleGroup(navGroup);
        settingsNavButton.setToggleGroup(navGroup);
        libraryNavButton.setSelected(true);

        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Library");
        }

        // Resolve stylesheet URLs
        lightThemeUrl = getClass().getResource("/css/lightTheme.css").toExternalForm();
        darkThemeUrl = getClass().getResource("/css/darkTheme.css").toExternalForm();

        // Load icons
        moonIcon = load("/img/logoLight/moon.png");
        sunIcon = load("/img/logoDark/sun.png");
        filterLight = load("/img/logoLight/settings.png");
        filterDark = load("/img/logoDark/settings.png");

        // Apply default theme when the scene is attached
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                switchToLightTheme();
            }
        });
    }

    private Image load(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }

    // ===== THEME TOGGLE =====

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

        themeToggle.setSelected(false);
        themeToggle.setText(null);

        if (themeIcon != null) {
            themeIcon.setImage(moonIcon); // moon in light mode
        }
        if (filterIcon != null) {
            filterIcon.setImage(filterLight); // light settings icon
        }
    }

    private void switchToDarkTheme() {
        var stylesheets = root.getStylesheets();
        stylesheets.remove(lightThemeUrl);
        if (!stylesheets.contains(darkThemeUrl)) {
            stylesheets.add(darkThemeUrl);
        }

        themeToggle.setSelected(true);
        themeToggle.setText(null);

        if (themeIcon != null) {
            themeIcon.setImage(sunIcon); // sun in dark mode
        }
        if (filterIcon != null) {
            filterIcon.setImage(filterDark); // dark settings icon
        }
    }

    // ===== NAVIGATION =====

    @FXML
    private void onDashboardSelected(ActionEvent event) {
        // TODO: SceneNavigator.showDashboard();
    }

    @FXML
    private void onLibrarySelected(ActionEvent event) {
        libraryNavButton.setSelected(true);
        // Already on Library screen; later you can refresh data here
    }

    @FXML
    private void onCurrentlyReadingSelected(ActionEvent event) {
        // TODO: SceneNavigator.showCurrentlyReading();
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        // TODO: SceneNavigator.showStatistics();
    }

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        // TODO: SceneNavigator.showSettings();
    }

    // ===== HEADER ACTIONS =====

    @FXML
    private void onAddBook(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/addBookModal.fxml"));
            Parent dialogRoot = loader.load();

            Scene dialogScene = new Scene(dialogRoot);

            // Inherit current styles (light or dark theme)
            dialogScene.getStylesheets().addAll(
                    root.getScene().getStylesheets()
            );

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Book");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(root.getScene().getWindow());
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        // TODO: open user profile
    }
}
