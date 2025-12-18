package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class StatisticsController {

    @FXML
    private BorderPane root;

    // Sidebar
    @FXML
    private ToggleButton dashboardNavButton;
    @FXML
    private ToggleButton libraryNavButton;
    @FXML
    private ToggleButton currentlyReadingNavButton;
    @FXML
    private ToggleButton statisticsNavButton;


    // User
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
    private ToggleButton themeToggle;
    @FXML
    private ImageView themeIcon;

    @FXML
    private Button notificationsButton;
    @FXML
    private ImageView notificationsIcon;

    @FXML
    private Button exportButton;


    // Icons
    private Image bellLight;
    private Image bellDark;
    private Image moonIcon;
    private Image sunIcon;

    @FXML
    private void initialize() {
        // Sidebar toggle group
        ToggleGroup navGroup = new ToggleGroup();
        dashboardNavButton.setToggleGroup(navGroup);
        libraryNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setToggleGroup(navGroup);
        statisticsNavButton.setToggleGroup(navGroup);

        statisticsNavButton.setSelected(true);

        // Header
        if (headerTitleLabel != null) headerTitleLabel.setText("Statistics");

        // User name
        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        // Combo
        if (timeRangeCombo != null) {
            timeRangeCombo.getItems().setAll("Last 7 days", "Last 30 days", "This year", "All time");
            timeRangeCombo.getSelectionModel().select("Last 30 days");
        }

        // Icons (safe)
        bellLight = load("/sk/upjs/paz/ui/img/logoLight/bell.png");
        bellDark = load("/sk/upjs/paz/ui/img/logoDark/bell.png");
        moonIcon = load("/sk/upjs/paz/ui/img/logoLight/moon.png");
        sunIcon = load("/sk/upjs/paz/ui/img/logoDark/sun.png");

        // Apply theme when scene is attached + sync icons
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.apply(newScene);
                updateIconsForTheme();
            }
        });

        // In case scene is already present
        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
            updateIconsForTheme();
        }
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    // ===== THEME =====
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
        if (notificationsIcon != null) notificationsIcon.setImage(dark ? bellDark : bellLight);
    }

    // ===== NAVIGATION =====
    @FXML
    private void onDashboardSelected(ActionEvent event) {
        SceneNavigator.showDashboard();
    }

    @FXML
    private void onLibrarySelected(ActionEvent event) {
        SceneNavigator.showLibrary();
    }

    @FXML
    private void onCurrentlyReadingSelected(ActionEvent event) {
        SceneNavigator.showCurrentlyReading();
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        statisticsNavButton.setSelected(true);
    }

    // ===== HEADER ACTIONS =====
    @FXML
    private void onNotifications(ActionEvent event) {
        SceneNavigator.toggleNotifications(notificationsButton);
    }

    @FXML
    private void onExportReport(ActionEvent event) { /* TODO */ }

    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }
}
