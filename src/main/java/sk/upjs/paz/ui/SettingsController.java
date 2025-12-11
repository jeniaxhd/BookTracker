package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;

public class SettingsController {

    @FXML
    private BorderPane root;

    // ===== SIDEBAR NAVIGATION =====
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

    // ===== HEADER =====
    @FXML
    private Label headerTitleLabel;

    @FXML
    private ToggleButton themeToggle;

    @FXML
    private ImageView themeIcon;

    @FXML
    private Button notificationsButton;

    @FXML
    private ImageView notificationsIcon;

    // ===== CONTENT ROOT / SCROLLPANE =====
    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private VBox contentRoot;

    // ===== READING PREFERENCES =====
    @FXML
    private ToggleButton minutesGoalToggle;

    @FXML
    private ToggleButton pagesGoalToggle;

    @FXML
    private TextField dailyGoalField;

    @FXML
    private TextField defaultDurationField;

    @FXML
    private ComboBox<String> interfaceLanguageCombo;

    @FXML
    private Slider readingSpeedSlider;

    // ===== APPEARANCE (inside card) =====
    @FXML
    private ToggleButton themeLightToggle;

    @FXML
    private ToggleButton themeDarkToggle;

    @FXML
    private ToggleButton densityComfortableToggle;

    @FXML
    private ToggleButton densityCompactToggle;

    // ===== NOTIFICATIONS =====
    @FXML
    private CheckBox reminderCheck;

    @FXML
    private CheckBox goalProgressCheck;

    @FXML
    private CheckBox monthSummaryCheck;

    // ===== STYLESHEETS =====
    private String lightThemeUrl;
    private String darkThemeUrl;

    // ===== ICONS =====
    private Image moonIcon;       // theme toggle icon when app is in light mode
    private Image sunIcon;        // theme toggle icon when app is in dark mode
    private Image bellLight;      // bell icon for light background
    private Image bellDark;       // bell icon for dark background

    @FXML
    private void initialize() {
        // --- nav group ---
        ToggleGroup navGroup = new ToggleGroup();
        if (dashboardNavButton != null) dashboardNavButton.setToggleGroup(navGroup);
        if (libraryNavButton != null)   libraryNavButton.setToggleGroup(navGroup);
        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setToggleGroup(navGroup);
        if (statisticsNavButton != null) statisticsNavButton.setToggleGroup(navGroup);
        if (settingsNavButton != null) {
            settingsNavButton.setToggleGroup(navGroup);
            settingsNavButton.setSelected(true);
        }

        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Settings");
        }

        // --- themes CSS ---
        URL light = getClass().getResource("/css/lightTheme.css");
        URL dark  = getClass().getResource("/css/darkTheme.css");
        if (light != null) lightThemeUrl = light.toExternalForm();
        if (dark  != null) darkThemeUrl  = dark.toExternalForm();

        // --- icons (safe loading) ---
        moonIcon = loadImage("/img/logoLight/moon.png");
        sunIcon  = loadImage("/img/logoDark/sun.png");
        bellLight = loadImage("/img/logoLight/bell.png");
        bellDark  = loadImage("/img/logoDark/bell.png");

        // apply default theme after scene is attached
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                switchToLightTheme();
            }
        });

        // ====== LOGIC INSIDE SETTINGS CARDS ======

        // Daily goal: minutes/pages group
        if (minutesGoalToggle != null && pagesGoalToggle != null) {
            ToggleGroup goalTypeGroup = new ToggleGroup();
            minutesGoalToggle.setToggleGroup(goalTypeGroup);
            pagesGoalToggle.setToggleGroup(goalTypeGroup);
            minutesGoalToggle.setSelected(true);
        }

        // Theme choice in card: light/dark
        if (themeLightToggle != null && themeDarkToggle != null) {
            ToggleGroup themeGroup = new ToggleGroup();
            themeLightToggle.setToggleGroup(themeGroup);
            themeDarkToggle.setToggleGroup(themeGroup);
            themeLightToggle.setSelected(true);
        }

        // Density: comfortable/compact
        if (densityComfortableToggle != null && densityCompactToggle != null) {
            ToggleGroup densityGroup = new ToggleGroup();
            densityComfortableToggle.setToggleGroup(densityGroup);
            densityCompactToggle.setToggleGroup(densityGroup);
            densityComfortableToggle.setSelected(true);
        }

        // Numeric defaults
        if (dailyGoalField != null && dailyGoalField.getText().isEmpty()) {
            dailyGoalField.setText("30");
        }
        if (defaultDurationField != null && defaultDurationField.getText().isEmpty()) {
            defaultDurationField.setText("25");
        }

        // Language default
        if (interfaceLanguageCombo != null &&
                interfaceLanguageCombo.getItems() != null &&
                !interfaceLanguageCombo.getItems().isEmpty()) {
            interfaceLanguageCombo.getSelectionModel().selectFirst();
        }

        // Reading speed slider default
        if (readingSpeedSlider != null && readingSpeedSlider.getValue() == 0.0) {
            readingSpeedSlider.setValue(50.0);
        }

        // Notifications defaults
        if (reminderCheck != null)      reminderCheck.setSelected(true);
        if (goalProgressCheck != null)  goalProgressCheck.setSelected(true);
        if (monthSummaryCheck != null)  monthSummaryCheck.setSelected(false);
    }

    private Image loadImage(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Missing image resource: " + path);
            return null;
        }
        return new Image(url.toExternalForm());
    }

    // ===== THEME TOGGLE IN HEADER =====

    @FXML
    private void onToggleTheme(ActionEvent event) {
        if (darkThemeUrl == null || lightThemeUrl == null) {
            return;
        }
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
        if (darkThemeUrl != null) stylesheets.remove(darkThemeUrl);
        if (lightThemeUrl != null && !stylesheets.contains(lightThemeUrl)) {
            stylesheets.add(lightThemeUrl);
        }

        if (themeToggle != null) {
            themeToggle.setSelected(false);
        }
        if (themeIcon != null && moonIcon != null) {
            themeIcon.setImage(moonIcon);
        }
        if (notificationsIcon != null && bellLight != null) {
            notificationsIcon.setImage(bellLight);
        }
    }

    private void switchToDarkTheme() {
        var stylesheets = root.getStylesheets();
        if (lightThemeUrl != null) stylesheets.remove(lightThemeUrl);
        if (darkThemeUrl != null && !stylesheets.contains(darkThemeUrl)) {
            stylesheets.add(darkThemeUrl);
        }

        if (themeToggle != null) {
            themeToggle.setSelected(true);
        }
        if (themeIcon != null && sunIcon != null) {
            themeIcon.setImage(sunIcon);
        }
        if (notificationsIcon != null && bellDark != null) {
            notificationsIcon.setImage(bellDark);
        }
    }

    // ===== NAVIGATION HANDLERS (поки заглушки) =====

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
        // TODO: SceneNavigator.showCurrentlyReading();
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        // TODO: SceneNavigator.showStatistics();
    }

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        if (settingsNavButton != null) {
            settingsNavButton.setSelected(true);
        }
    }

    // ===== HEADER BUTTONS =====

    @FXML
    private void onNotifications(ActionEvent event) {
        // TODO: відкрити панель нотифікацій
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        // TODO: відкрити профіль
    }
}
