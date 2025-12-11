package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CurrentlyReadingController {

    @FXML
    private BorderPane root;

    // Sidebar navigation
    @FXML private ToggleButton dashboardNavButton;
    @FXML private ToggleButton libraryNavButton;
    @FXML private ToggleButton currentlyReadingNavButton;
    @FXML private ToggleButton statisticsNavButton;
    @FXML private ToggleButton settingsNavButton;

    // User / header
    @FXML private Button userProfileButton;
    @FXML private Label userNameLabel;
    @FXML private Label headerTitleLabel;

    // Header actions
    @FXML private Button searchButton;
    @FXML private Button settingsButton;
    @FXML private ToggleButton themeToggle;
    @FXML private Button startSessionButton;

    // Header icons
    @FXML private ImageView searchIcon;
    @FXML private ImageView settingsIcon;
    @FXML private ImageView themeIcon;

    // Content
    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentRoot;

    // Stylesheets
    private String lightThemeUrl;
    private String darkThemeUrl;

    // Icons
    private Image searchLight;
    private Image searchDark;
    private Image settingsLight;
    private Image settingsDark;
    private Image moonIcon;
    private Image sunIcon;

    @FXML
    private void initialize() {
        // Navigation group
        ToggleGroup navGroup = new ToggleGroup();
        dashboardNavButton.setToggleGroup(navGroup);
        libraryNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setToggleGroup(navGroup);
        statisticsNavButton.setToggleGroup(navGroup);
        settingsNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setSelected(true);

        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Currently Reading");
        }

        // Stylesheets
        lightThemeUrl = getClass().getResource("/css/lightTheme.css").toExternalForm();
        darkThemeUrl  = getClass().getResource("/css/darkTheme.css").toExternalForm();

        // Load images (make sure these PNG files exist!)
        searchLight   = load("/img/logoLight/search.png");
        searchDark    = load("/img/logoDark/search.png");
        settingsLight = load("/img/logoLight/settings.png");
        settingsDark  = load("/img/logoDark/settings.png");
        moonIcon      = load("/img/logoLight/moon.png");
        sunIcon       = load("/img/logoDark/sun.png");

        // Apply initial theme when scene is ready
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

        if (searchIcon != null)   searchIcon.setImage(searchLight);
        if (settingsIcon != null) settingsIcon.setImage(settingsLight);
        if (themeIcon != null)    themeIcon.setImage(moonIcon);
    }

    private void switchToDarkTheme() {
        var stylesheets = root.getStylesheets();
        stylesheets.remove(lightThemeUrl);
        if (!stylesheets.contains(darkThemeUrl)) {
            stylesheets.add(darkThemeUrl);
        }

        themeToggle.setSelected(true);

        if (searchIcon != null)   searchIcon.setImage(searchDark);
        if (settingsIcon != null) settingsIcon.setImage(settingsDark);
        if (themeIcon != null)    themeIcon.setImage(sunIcon);
    }

    // ===== NAVIGATION =====

    @FXML
    private void onDashboardSelected(ActionEvent event) {
        // TODO: SceneNavigator.showDashboard();
    }

    @FXML
    private void onLibrarySelected(ActionEvent event) {
        // TODO: SceneNavigator.showLibrary();
    }

    @FXML
    private void onCurrentlyReadingSelected(ActionEvent event) {
        currentlyReadingNavButton.setSelected(true);
        // already here
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
    private void onSearch(ActionEvent event) {
        // TODO: open search dialog
    }

    @FXML
    private void onSettings(ActionEvent event) {
        // TODO: open quick settings
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        // TODO: open profile screen
    }

    @FXML
    private void onStartSession(ActionEvent event) {
        // TODO: start reading session
    }
}
