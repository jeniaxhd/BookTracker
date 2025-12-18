package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.ResourceBundle;

public class DashboardController {

    @FXML private BorderPane root;

    // Sidebar navigation
    @FXML private ToggleButton dashboardNavButton;
    @FXML private ToggleButton libraryNavButton;
    @FXML private ToggleButton currentlyReadingNavButton;
    @FXML private ToggleButton statisticsNavButton;

    // Header actions
    @FXML private ToggleButton themeToggle;
    @FXML private Button addBookButton;
    @FXML private Button notificationsButton;
    @FXML private Button userProfileButton;

    // Header icons
    @FXML private ImageView searchIcon;
    @FXML private ImageView notificationsIcon;
    @FXML private ImageView themeIcon;

    // Header / user labels
    @FXML private Label headerTitleLabel;
    @FXML private Label userNameLabel;

    // Main content
    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentRoot;
    @FXML private FlowPane currentlyReadingContainer;
    @FXML private FlowPane bottomFlow;

    @FXML private VBox queueContainer;
    @FXML private Label booksReadValueLabel;
    @FXML private Label inProgressValueLabel;

    // i18n bundle injected by FXMLLoader
    @FXML private ResourceBundle resources;

    // Icons used on light and dark background
    private Image searchLight;
    private Image searchDark;
    private Image bellLight;
    private Image bellDark;
    private Image moonIcon;
    private Image sunIcon;

    @FXML
    private void initialize() {
        // Defensive: ensure UI is clickable
        root.setDisable(false);
        root.setMouseTransparent(false);

        setupNavToggleGroup();
        setupUserHeader();
        loadIcons();
        setupTheme();
        setupResponsiveWrapping();
        setupDemoStats();
    }

    private void setupNavToggleGroup() {
        ToggleGroup navGroup = new ToggleGroup();
        if (dashboardNavButton != null) dashboardNavButton.setToggleGroup(navGroup);
        if (libraryNavButton != null) libraryNavButton.setToggleGroup(navGroup);
        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setToggleGroup(navGroup);
        if (statisticsNavButton != null) statisticsNavButton.setToggleGroup(navGroup);

        if (dashboardNavButton != null) dashboardNavButton.setSelected(true);
    }

    private void setupUserHeader() {
        // Do NOT override i18n headerTitleLabel here if it's set via FXML (%key).
        if (userNameLabel != null) {
            var user = AppState.getCurrentUser();
            userNameLabel.setText(user != null ? safe(user.getName()) : "");
        }
    }

    private void loadIcons() {
        searchLight = loadImage("/sk/upjs/paz/ui/img/logoLight/search.png");
        searchDark  = loadImage("/sk/upjs/paz/ui/img/logoDark/search.png");
        bellLight   = loadImage("/sk/upjs/paz/ui/img/logoLight/bell.png");
        bellDark    = loadImage("/sk/upjs/paz/ui/img/logoDark/bell.png");
        moonIcon    = loadImage("/sk/upjs/paz/ui/img/logoLight/moon.png");
        sunIcon     = loadImage("/sk/upjs/paz/ui/img/logoDark/sun.png");
    }

    private void setupTheme() {
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.apply(newScene);
                updateIconsForTheme();
            }
        });

        Scene scene = root.getScene();
        if (scene != null) {
            ThemeManager.apply(scene);
            updateIconsForTheme();
        }
    }

    private void setupResponsiveWrapping() {
        if (contentScrollPane == null) return;

        contentScrollPane.viewportBoundsProperty().addListener((obs, oldB, b) -> {
            double w = b.getWidth();
            double wrap = Math.max(320, w - 10);

            if (currentlyReadingContainer != null) currentlyReadingContainer.setPrefWrapLength(wrap);
            if (bottomFlow != null) bottomFlow.setPrefWrapLength(wrap);
        });
    }

    private void setupDemoStats() {
        // Replace later with real values from DB/service.
        if (booksReadValueLabel != null && isBlank(booksReadValueLabel.getText())) {
            booksReadValueLabel.setText("24");
        }
        if (inProgressValueLabel != null && isBlank(inProgressValueLabel.getText())) {
            inProgressValueLabel.setText("4");
        }
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        return (url == null) ? null : new Image(url.toExternalForm());
    }

    private void updateIconsForTheme() {
        boolean dark = ThemeManager.isDarkMode();

        if (searchIcon != null) searchIcon.setImage(dark ? searchDark : searchLight);
        if (notificationsIcon != null) notificationsIcon.setImage(dark ? bellDark : bellLight);
        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);

        if (themeToggle != null) themeToggle.setSelected(dark);
    }

    // ========== THEME TOGGLE ==========

    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        Scene scene = root.getScene();
        if (scene != null) {
            ThemeManager.apply(scene);
            updateIconsForTheme();
        }
    }

    // ========== NAVIGATION HANDLERS ==========

    @FXML
    private void onDashboardSelected(ActionEvent event) {
        // already here
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
        SceneNavigator.showStatistics();
    }

    // ========== HEADER BUTTONS ==========

    @FXML
    private void onSearch(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle(tr("search.title", "Search"));
        dialog.setHeaderText(tr("search.header", "Search books"));
        dialog.setContentText(tr("search.prompt", "Title / author / tag:"));

        dialog.showAndWait();
    }

    @FXML
    private void onNotifications(ActionEvent event) {
        if (notificationsButton != null) {
            SceneNavigator.toggleNotifications(notificationsButton);
        }
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }

    @FXML
    private void onAddBook(ActionEvent event) {
        Window w = (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;

        if (AppState.getCurrentUser() == null) {
            showAlert(Alert.AlertType.WARNING,
                    tr("auth.required.title", "Login required"),
                    tr("auth.required.msg", "Please login first."));
            return;
        }

        SceneNavigator.showAddBookModal(w);
    }

    // ========== Helpers ==========

    private String tr(String key, String fallback) {
        try {
            return (resources != null) ? resources.getString(key) : fallback;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
