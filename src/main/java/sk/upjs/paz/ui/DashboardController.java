package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DashboardController {

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

    // Header actions
    @FXML
    private ToggleButton themeToggle;

    @FXML
    private Button addBookButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button notificationsButton;

    // Header icons (PNG)
    @FXML
    private ImageView searchIcon;

    @FXML
    private ImageView notificationsIcon;

    @FXML
    private ImageView themeIcon;

    // Header / user labels
    @FXML
    private Label headerTitleLabel;

    @FXML
    private Label userNameLabel;

    // Main content
    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private VBox contentRoot;

    // CHANGED: HBox -> FlowPane (wrap on resize)
    @FXML
    private FlowPane currentlyReadingContainer;

    // CHANGED: add bottomFlow to wrap Queue + Statistics when window is narrow
    @FXML
    private FlowPane bottomFlow;

    @FXML
    private VBox queueContainer;

    @FXML
    private Label booksReadValueLabel;

    @FXML
    private Label inProgressValueLabel;

    @FXML
    private Button userProfileButton;

    // Icons used on light and dark background
    private Image searchLight;
    private Image searchDark;
    private Image bellLight;
    private Image bellDark;
    private Image moonIcon;
    private Image sunIcon;

    @FXML
    private void initialize() {
        // Group sidebar buttons so only one can be active at a time
        ToggleGroup navGroup = new ToggleGroup();
        dashboardNavButton.setToggleGroup(navGroup);
        libraryNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setToggleGroup(navGroup);
        statisticsNavButton.setToggleGroup(navGroup);
        settingsNavButton.setToggleGroup(navGroup);
        dashboardNavButton.setSelected(true);

        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Dashboard");
        }
        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        // Load icon images
        searchLight = load("/img/logoLight/search.png");
        searchDark = load("/img/logoDark/search.png");
        bellLight = load("/img/logoLight/bell.png");
        bellDark = load("/img/logoDark/bell.png");
        moonIcon = load("/img/logoLight/moon.png");
        sunIcon = load("/img/logoDark/sun.png");

        // Apply theme when scene becomes available
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

        // Responsive wrapping for FlowPanes based on ScrollPane viewport width
        if (contentScrollPane != null) {
            contentScrollPane.viewportBoundsProperty().addListener((obs, oldB, b) -> {
                double w = b.getWidth();
                double wrap = Math.max(320, w - 10);

                if (currentlyReadingContainer != null) {
                    currentlyReadingContainer.setPrefWrapLength(wrap);
                }
                if (bottomFlow != null) {
                    bottomFlow.setPrefWrapLength(wrap);
                }
            });
        }

        // Example initial values for stats
        if (booksReadValueLabel != null) {
            booksReadValueLabel.setText("24");
        }
        if (inProgressValueLabel != null) {
            inProgressValueLabel.setText("4");
        }
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    // ========== THEME TOGGLE ==========

    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        var scene = root.getScene();
        ThemeManager.apply(scene);
        updateIconsForTheme();
    }

    private void updateIconsForTheme() {
        boolean dark = ThemeManager.isDarkMode();

        if (searchIcon != null) searchIcon.setImage(dark ? searchDark : searchLight);
        if (notificationsIcon != null) notificationsIcon.setImage(dark ? bellDark : bellLight);
        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);

        if (themeToggle != null) themeToggle.setSelected(dark);
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

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        SceneNavigator.showSettings();
    }

    // ========== HEADER BUTTONS ==========

    @FXML
    private void onSearch(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search");
        dialog.setHeaderText("Search books");
        dialog.setContentText("Title / author / tag:");

//        dialog.showAndWait().ifPresent(q -> {
//            String query = q == null ? "" : q.trim();
//            if (!query.isBlank()) {
//                AppState.setPendingSearchQuery(query);
//                SceneNavigator.showLibrary();
//            }
//        });
    }

    @FXML
    private void onNotifications(ActionEvent event) {
        SceneNavigator.toggleNotifications(notificationsButton);
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }

    @FXML
    private void onAddBook(ActionEvent event) {
        SceneNavigator.showAddBookModal(root.getScene().getWindow());
    }
}
