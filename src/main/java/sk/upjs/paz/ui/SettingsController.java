package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SettingsController {

    @FXML private BorderPane root;

    // Sidebar navigation
    @FXML private ToggleButton dashboardNavButton;
    @FXML private ToggleButton libraryNavButton;
    @FXML private ToggleButton currentlyReadingNavButton;
    @FXML private ToggleButton statisticsNavButton;
    @FXML private ToggleButton settingsNavButton;

    // User profile
    @FXML private Button userProfileButton;
    @FXML private Label userNameLabel;

    // Header
    @FXML private Label headerTitleLabel;
    @FXML private ToggleButton themeToggle;
    @FXML private ImageView themeIcon;

    @FXML private Button notificationsButton;
    @FXML private ImageView notificationsIcon;

    // Content
    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentRoot;

    // Reading preferences
    @FXML private ToggleButton minutesGoalToggle;
    @FXML private ToggleButton pagesGoalToggle;
    @FXML private TextField dailyGoalField;
    @FXML private TextField defaultDurationField;
    @FXML private ComboBox<String> interfaceLanguageCombo;
    @FXML private Slider readingSpeedSlider;

    // Appearance (inside card) - MUST BE ToggleButton (pills)
    @FXML private ToggleButton themeLightToggle;
    @FXML private ToggleButton themeDarkToggle;

    @FXML private ToggleButton densityComfortableToggle;
    @FXML private ToggleButton densityCompactToggle;

    // Notifications (inside card)
    @FXML private CheckBox reminderCheck;
    @FXML private CheckBox goalProgressCheck;
    @FXML private CheckBox monthSummaryCheck;

    // Icons
    private Image moonIcon;
    private Image sunIcon;
    private Image bellLight;
    private Image bellDark;

    @FXML
    private void initialize() {
        // ===== Sidebar group =====
        ToggleGroup navGroup = new ToggleGroup();
        if (dashboardNavButton != null) dashboardNavButton.setToggleGroup(navGroup);
        if (libraryNavButton != null) libraryNavButton.setToggleGroup(navGroup);
        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setToggleGroup(navGroup);
        if (statisticsNavButton != null) statisticsNavButton.setToggleGroup(navGroup);
        if (settingsNavButton != null) {
            settingsNavButton.setToggleGroup(navGroup);
            settingsNavButton.setSelected(true);
        }

        if (headerTitleLabel != null) headerTitleLabel.setText("Settings");

        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        // ===== Load icons (safe) =====
        moonIcon = load("/img/logoLight/moon.png");
        sunIcon = load("/img/logoDark/sun.png");
        bellLight = load("/img/logoLight/bell.png");
        bellDark = load("/img/logoDark/bell.png");

        // ===== Apply theme when scene is attached =====
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.apply(newScene);
                syncThemeControls();
                updateHeaderIcons();
            }
        });
        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
            syncThemeControls();
            updateHeaderIcons();
        }

        // ===== Goal type group (minutes/pages) =====
        if (minutesGoalToggle != null && pagesGoalToggle != null) {
            ToggleGroup goalTypeGroup = new ToggleGroup();
            minutesGoalToggle.setToggleGroup(goalTypeGroup);
            pagesGoalToggle.setToggleGroup(goalTypeGroup);

            // prevent unselecting last selected
            goalTypeGroup.selectedToggleProperty().addListener((o, oldT, newT) -> {
                if (newT == null && oldT != null) oldT.setSelected(true);
            });

            // default
            if (goalTypeGroup.getSelectedToggle() == null) {
                minutesGoalToggle.setSelected(true);
            }
        }

        // ===== Theme pills group (Light/Dark) =====
        if (themeLightToggle != null && themeDarkToggle != null) {
            ToggleGroup themeGroup = new ToggleGroup();
            themeLightToggle.setToggleGroup(themeGroup);
            themeDarkToggle.setToggleGroup(themeGroup);

            // initial state from ThemeManager
            if (ThemeManager.isDarkMode()) themeDarkToggle.setSelected(true);
            else themeLightToggle.setSelected(true);

            // prevent unselecting last selected + apply theme
            themeGroup.selectedToggleProperty().addListener((o, oldT, newT) -> {
                if (newT == null) {
                    if (oldT != null) oldT.setSelected(true);
                    return;
                }

                boolean dark = (newT == themeDarkToggle);
                ThemeManager.setDarkMode(dark);
                ThemeManager.apply(root.getScene());
                syncThemeControls();
                updateHeaderIcons();
            });
        }

        // ===== Density group =====
        if (densityComfortableToggle != null && densityCompactToggle != null) {
            ToggleGroup densityGroup = new ToggleGroup();
            densityComfortableToggle.setToggleGroup(densityGroup);
            densityCompactToggle.setToggleGroup(densityGroup);

            densityGroup.selectedToggleProperty().addListener((o, oldT, newT) -> {
                if (newT == null && oldT != null) oldT.setSelected(true);
            });

            if (densityGroup.getSelectedToggle() == null) {
                densityComfortableToggle.setSelected(true);
            }
        }

        // ===== Defaults (optional) =====
        if (dailyGoalField != null && dailyGoalField.getText().isBlank()) dailyGoalField.setText("30");
        if (defaultDurationField != null && defaultDurationField.getText().isBlank()) defaultDurationField.setText("25");

        if (interfaceLanguageCombo != null
                && interfaceLanguageCombo.getItems() != null
                && !interfaceLanguageCombo.getItems().isEmpty()
                && interfaceLanguageCombo.getSelectionModel().isEmpty()) {
            interfaceLanguageCombo.getSelectionModel().selectFirst();
        }

        if (readingSpeedSlider != null && readingSpeedSlider.getValue() == 0.0) {
            readingSpeedSlider.setValue(50.0);
        }
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    // ===== Header theme toggle =====

    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        ThemeManager.apply(root.getScene());
        syncThemeControls();
        updateHeaderIcons();
    }

    private void syncThemeControls() {
        boolean dark = ThemeManager.isDarkMode();

        if (themeToggle != null) themeToggle.setSelected(dark);

        // sync pills too
        if (themeLightToggle != null && themeDarkToggle != null) {
            if (dark) themeDarkToggle.setSelected(true);
            else themeLightToggle.setSelected(true);
        }
    }

    private void updateHeaderIcons() {
        boolean dark = ThemeManager.isDarkMode();

        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);
        if (notificationsIcon != null) notificationsIcon.setImage(dark ? bellDark : bellLight);
    }

    // ===== Navigation =====

    @FXML private void onDashboardSelected(ActionEvent event) { SceneNavigator.showDashboard(); }
    @FXML private void onLibrarySelected(ActionEvent event) { SceneNavigator.showLibrary(); }
    @FXML private void onCurrentlyReadingSelected(ActionEvent event) { SceneNavigator.showCurrentlyReading(); }
    @FXML private void onStatisticsSelected(ActionEvent event) { SceneNavigator.showStatistics(); }

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        if (settingsNavButton != null) settingsNavButton.setSelected(true);
    }

    // ===== Header buttons =====

    @FXML
    private void onNotifications(ActionEvent event) {
        SceneNavigator.toggleNotifications(notificationsButton);
    }


    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }
}
