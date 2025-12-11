package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class StatisticsController {

    // Root
    @FXML
    private BorderPane root;

    // Sidebar navigation (optional – may be null if fx:id missing)
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

    // User profile (optional)
    @FXML
    private Button userProfileButton;

    @FXML
    private Label userNameLabel;

    // Header
    @FXML
    private Label headerTitleLabel;

    @FXML
    private ComboBox<String> timeRangeCombo;

    @FXML
    private ToggleButton themeToggle;      // if you change it to Button in FXML, this will stay null

    @FXML
    private ImageView themeIcon;           // optional – only if defined in FXML

    @FXML
    private Button notificationsButton;    // optional

    @FXML
    private ImageView notificationsIcon;   // optional

    @FXML
    private Button exportButton;           // optional

    // Scrollable content
    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private VBox contentRoot;

    // Stylesheets
    private String lightThemeUrl;
    private String darkThemeUrl;

    // Icons
    private Image bellLight;
    private Image bellDark;
    private Image moonIcon;
    private Image sunIcon;

    @FXML
    private void initialize() {
        // Navigation group – use only buttons that are actually injected
        ToggleGroup navGroup = new ToggleGroup();
        if (dashboardNavButton != null) {
            dashboardNavButton.setToggleGroup(navGroup);
        }
        if (libraryNavButton != null) {
            libraryNavButton.setToggleGroup(navGroup);
        }
        if (currentlyReadingNavButton != null) {
            currentlyReadingNavButton.setToggleGroup(navGroup);
        }
        if (statisticsNavButton != null) {
            statisticsNavButton.setToggleGroup(navGroup);
            statisticsNavButton.setSelected(true);
        }
        if (settingsNavButton != null) {
            settingsNavButton.setToggleGroup(navGroup);
        }

        // Header title
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Statistics");
        }

        // Time range combo
        if (timeRangeCombo != null) {
            timeRangeCombo.getItems().setAll(
                    "Last 7 days",
                    "Last 30 days",
                    "This year",
                    "All time"
            );
            timeRangeCombo.getSelectionModel().select("Last 30 days");
        }

        // Stylesheet URLs
        lightThemeUrl = getClass().getResource("/css/lightTheme.css").toExternalForm();
        darkThemeUrl  = getClass().getResource("/css/darkTheme.css").toExternalForm();

        // Icons (only used if corresponding ImageView is present)
        bellLight = load("/img/logoLight/bell.png");
        bellDark  = load("/img/logoDark/bell.png");
        moonIcon  = load("/img/logoLight/moon.png");
        sunIcon   = load("/img/logoDark/sun.png");

        // Apply default theme when scene is attached
        if (root != null) {
            root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    switchToLightTheme();
                }
            });
        }
    }

    private Image load(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }

    // ========= THEME TOGGLE =========

    @FXML
    private void onToggleTheme(ActionEvent event) {
        if (root == null) {
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
        if (root == null) return;

        var stylesheets = root.getStylesheets();
        stylesheets.remove(darkThemeUrl);
        if (!stylesheets.contains(lightThemeUrl)) {
            stylesheets.add(lightThemeUrl);
        }

        // Icons for light theme
        if (notificationsIcon != null) {
            notificationsIcon.setImage(bellLight);
        }
        if (themeIcon != null) {
            themeIcon.setImage(moonIcon);
        }
        if (themeToggle != null) {
            themeToggle.setSelected(false);
        }
    }

    private void switchToDarkTheme() {
        if (root == null) return;

        var stylesheets = root.getStylesheets();
        stylesheets.remove(lightThemeUrl);
        if (!stylesheets.contains(darkThemeUrl)) {
            stylesheets.add(darkThemeUrl);
        }

        // Icons for dark theme
        if (notificationsIcon != null) {
            notificationsIcon.setImage(bellDark);
        }
        if (themeIcon != null) {
            themeIcon.setImage(sunIcon);
        }
        if (themeToggle != null) {
            themeToggle.setSelected(true);
        }
    }

    // ========= NAVIGATION HANDLERS =========

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
        if (statisticsNavButton != null) {
            statisticsNavButton.setSelected(true);
        }
        // TODO: maybe refresh statistics later
    }

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        // TODO: SceneNavigator.showSettings();
    }

    // ========= HEADER ACTIONS =========

    @FXML
    private void onNotifications(ActionEvent event) {
        // TODO: open notifications panel
    }

    @FXML
    private void onExportReport(ActionEvent event) {
        // TODO: export report
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        // TODO: open user profile
    }
}
