package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    @FXML
    private HBox currentlyReadingContainer;

    @FXML
    private VBox queueContainer;

    @FXML
    private Label booksReadValueLabel;

    @FXML
    private Label inProgressValueLabel;

    @FXML
    private Button userProfileButton;

    // Stylesheet URLs
    private String lightThemeUrl;
    private String darkThemeUrl;

    // Icons used on light and dark background
    private Image searchLight;   // for light theme
    private Image searchDark;    // for dark theme
    private Image bellLight;     // for light theme
    private Image bellDark;      // for dark theme
    private Image moonIcon;      // theme toggle icon in light theme
    private Image sunIcon;       // theme toggle icon in dark theme

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

        // Resolve stylesheet URLs
        lightThemeUrl = getClass().getResource("/css/lightTheme.css").toExternalForm();
        darkThemeUrl = getClass().getResource("/css/darkTheme.css").toExternalForm();

        // Load icon images
        searchLight = load("/img/logoLight/search.png");
        searchDark  = load("/img/logoDark/search.png");
        bellLight   = load("/img/logoLight/bell.png");
        bellDark    = load("/img/logoDark/bell.png");
        moonIcon    = load("/img/logoLight/moon.png");
        sunIcon     = load("/img/logoDark/sun.png");

        // When the scene is attached, apply default (light) theme and icons
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                switchToLightTheme();
            }
        });

        // Example initial values for stats
        if (booksReadValueLabel != null) {
            booksReadValueLabel.setText("24");
        }
        if (inProgressValueLabel != null) {
            inProgressValueLabel.setText("4");
        }
    }

    private Image load(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }

    // ========== THEME TOGGLE ==========

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

        searchIcon.setImage(searchLight);
        notificationsIcon.setImage(bellLight);
        themeIcon.setImage(moonIcon);  // show moon when app is in light mode

        themeToggle.setSelected(false);
    }

    private void switchToDarkTheme() {
        var stylesheets = root.getStylesheets();
        stylesheets.remove(lightThemeUrl);
        if (!stylesheets.contains(darkThemeUrl)) {
            stylesheets.add(darkThemeUrl);
        }

        searchIcon.setImage(searchDark);
        notificationsIcon.setImage(bellDark);
        themeIcon.setImage(sunIcon);   // show sun when app is in dark mode

        themeToggle.setSelected(true);
    }

    // ========== NAVIGATION HANDLERS ==========

    @FXML
    private void onDashboardSelected(ActionEvent event) {
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Dashboard");
        }
        // content stays the same for now
    }

    @FXML
    private void onLibrarySelected(ActionEvent event) {
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Library");
        }
    }

    @FXML
    private void onCurrentlyReadingSelected(ActionEvent event) {
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Currently Reading");
        }
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Statistics");
        }
    }

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Settings");
        }
    }

    // ========== HEADER BUTTONS ==========

    @FXML
    private void onAddBook(ActionEvent event) {
        // TODO: SceneNavigator.showAddBook();
    }

    @FXML
    private void onSearch(ActionEvent event) {
        // TODO: open search UI or dialog
    }

    @FXML
    private void onNotifications(ActionEvent event) {
        // TODO: open notifications panel
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        // TODO: open user profile screen or dialog
    }
}
