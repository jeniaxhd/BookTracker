package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CurrentlyReadingController {

    @FXML private BorderPane root;

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

    // Icons
    private Image searchLight, searchDark;
    private Image settingsLight, settingsDark;
    private Image moonIcon, sunIcon;

    @FXML
    private void initialize() {
        ToggleGroup navGroup = new ToggleGroup();
        if (dashboardNavButton != null) dashboardNavButton.setToggleGroup(navGroup);
        if (libraryNavButton != null) libraryNavButton.setToggleGroup(navGroup);
        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setToggleGroup(navGroup);
        if (statisticsNavButton != null) statisticsNavButton.setToggleGroup(navGroup);
        if (settingsNavButton != null) settingsNavButton.setToggleGroup(navGroup);

        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setSelected(true);

        if (headerTitleLabel != null) headerTitleLabel.setText("Currently Reading");

        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        searchLight = load("/img/logoLight/search.png");
        searchDark = load("/img/logoDark/search.png");
        settingsLight = load("/img/logoLight/settings.png");
        settingsDark = load("/img/logoDark/settings.png");
        moonIcon = load("/img/logoLight/moon.png");
        sunIcon = load("/img/logoDark/sun.png");

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

    // ===== Theme =====

    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
        }
        updateIconsForTheme();
    }

    private void updateIconsForTheme() {
        boolean dark = ThemeManager.isDarkMode();

        if (themeToggle != null) themeToggle.setSelected(dark);
        if (searchIcon != null) searchIcon.setImage(dark ? searchDark : searchLight);
        if (settingsIcon != null) settingsIcon.setImage(dark ? settingsDark : settingsLight);
        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);
    }

    // ===== Navigation =====

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
        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setSelected(true);
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        SceneNavigator.showStatistics();
    }

    @FXML
    private void onSettingsSelected(ActionEvent event) {
        SceneNavigator.showSettings();
    }

    // ===== Header actions =====

    @FXML
    private void onSearch(ActionEvent event) {
        // later
    }

    @FXML
    private void onSettings(ActionEvent event) {
        SceneNavigator.showSettings();
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }

    @FXML
    private void onStartSession(ActionEvent event) {
        SceneNavigator.startSessionBar("Atomic Habits", "James Clear · Productivity", "In Progress");

    }

    // ===== Book actions (from FXML buttons) =====

    @FXML
    private void onContinue(ActionEvent event) {
        String title = readUserDataAsString(event);
        if (title == null) title = "Current book";
        startSessionBar(title, "Author · Category", "In Progress");
    }


    @FXML
    private void onViewDetails(ActionEvent event) {
        String title = readUserDataAsString(event);
        if (title == null) title = "Book";
        openBookDetailsModal(title);
    }

    @FXML
    private void onAddAnotherBook(ActionEvent event) {
        openAddBookModal();
    }

    // ===== Session bar =====

    private void startSessionBar(String title, String subtitle, String status) {
        SessionBarHost.show(root, title, subtitle, status);
    }


    // ===== Modals =====

    private void openAddBookModal() {
        openModal("/fxml/addBookModal.fxml", "Add Book");
    }

    private void openBookDetailsModal(String bookTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookDetailsModal.fxml"));
            Parent dialogRoot = loader.load();

            BookDetailsModalController c = loader.getController();
            c.setBookDetails(
                    bookTitle,
                    "Book details and metadata",
                    "2020",
                    "EN",
                    "320",
                    "Productivity",
                    "In Progress",
                    "Description placeholder...",
                    "JC",
                    "James Clear",
                    "United States",
                    "Author bio placeholder..."
            );

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Book details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(root.getScene().getWindow());
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Cannot open bookDetailsModal.fxml", e);
        }
    }

    private void openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dialogRoot = loader.load();

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(root.getScene().getWindow());
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Cannot open modal: " + fxmlPath, e);
        }
    }

    private String readUserDataAsString(ActionEvent event) {
        if (!(event.getSource() instanceof Control c)) return null;
        Object ud = c.getUserData();
        return ud == null ? null : String.valueOf(ud);
    }

}
