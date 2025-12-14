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

    // Content (do NOT clear this - it contains your whole layout from FXML)
    @FXML
    private ScrollPane contentScrollPane;
    @FXML
    private VBox contentRoot;

    // Icons
    private Image moonIcon;
    private Image sunIcon;

    private Image filterLight;
    private Image filterDark;

    @FXML
    private void initialize() {
        // Sidebar group
        ToggleGroup navGroup = new ToggleGroup();
        dashboardNavButton.setToggleGroup(navGroup);
        libraryNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setToggleGroup(navGroup);
        statisticsNavButton.setToggleGroup(navGroup);
        settingsNavButton.setToggleGroup(navGroup);
        libraryNavButton.setSelected(true);

        if (headerTitleLabel != null) headerTitleLabel.setText("Library");

        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        // Load icons (safe)
        moonIcon = load("/img/logoLight/moon.png");
        sunIcon = load("/img/logoDark/sun.png");

        filterLight = load("/img/logoLight/settings.png");
        filterDark = load("/img/logoDark/settings.png");

        // Apply theme + icons when scene is attached
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.apply(newScene);
                updateIconsForTheme();
            }
        });
        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
            updateIconsForTheme();
        }
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    // ===== THEME TOGGLE =====

    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        ThemeManager.apply(root.getScene());
        updateIconsForTheme();
    }

    private void updateIconsForTheme() {
        boolean dark = ThemeManager.isDarkMode();

        if (themeToggle != null) themeToggle.setSelected(dark);
        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);

        if (filterIcon != null) filterIcon.setImage(dark ? filterDark : filterLight);
    }

    // ===== NAVIGATION =====

    @FXML
    private void onDashboardSelected(ActionEvent event) {
        SceneNavigator.showDashboard();
    }

    @FXML
    private void onLibrarySelected(ActionEvent event) {
        // already here
        libraryNavButton.setSelected(true);
    }

    @FXML
    private void onCurrentlyReadingSelected(ActionEvent event) {
        SceneNavigator.showCurrentlyReading();
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        SceneNavigator.showStatistics();
    }

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        SceneNavigator.showSettings();
    }

    // ===== HEADER ACTIONS =====

    @FXML
    private void onAddBook(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addBookModal.fxml"));
            Parent dialogRoot = loader.load();

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Book");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(root.getScene().getWindow());
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Cannot open addBookModal.fxml", e);
        }
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }

    @FXML
    private void onTopSettings(ActionEvent event) {
        SceneNavigator.showSettings();
    }

}
