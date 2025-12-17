package sk.upjs.paz.ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.upjs.paz.ui.dto.ActiveBookCard;
import sk.upjs.paz.service.CurrentlyReadingService;
import sk.upjs.paz.ui.i18n.I18N;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    // IMPORTANT: container for cards (add this in FXML)
    @FXML private VBox cardsBox;

    private CurrentlyReadingService currentlyReadingService;
    private List<ActiveBookCard> cachedCards = new ArrayList<>();

    // Icons
    private Image searchLight, searchDark;
    private Image settingsLight, settingsDark;
    private Image moonIcon, sunIcon;

    public void setCurrentlyReadingService(CurrentlyReadingService s) {
        this.currentlyReadingService = s;
        refresh(); // safe call after injection
    }

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

    public void refresh() {
        if (cardsBox == null) return;
        if (currentlyReadingService == null) return;

        if (!AppState.isLoggedIn()) {
            cardsBox.getChildren().clear();
            cachedCards = new ArrayList<>();
            return;
        }

        long userId = AppState.getCurrentUser().getId();

        Task<List<ActiveBookCard>> task = new Task<>() {
            @Override
            protected List<ActiveBookCard> call() {
                return currentlyReadingService.listActiveBooks(userId);
            }
        };

        task.setOnSucceeded(e -> {
            cachedCards = task.getValue() != null ? task.getValue() : new ArrayList<>();
            renderCards(cachedCards);
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderCards(List<ActiveBookCard> cards) {
        cardsBox.getChildren().clear();
        for (ActiveBookCard card : cards) {
            cardsBox.getChildren().add(loadCard(card));
        }
    }

    private Node loadCard(ActiveBookCard card) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/upjs/paz/ui/activeBookCard.fxml"),  I18N.getBundle());
            Parent root = loader.load();

            ActiveBookCardController c = loader.getController();
            c.setData(card);
            c.setOnContinue(this::startSessionForCard);
            c.setOnDetails(this::openBookDetailsForCard);

            return root;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load activeBookCard.fxml", ex);
        }
    }

    private void startSessionForCard(ActiveBookCard card) {
        String subtitle = card.authorsText() + " · " + card.genreName();
        SessionBarHost.show(root, card.title(), subtitle, "In Progress");
    }

    private void openBookDetailsForCard(ActiveBookCard card) {
        openBookDetailsModal(card.title());
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
        if (cachedCards == null || cachedCards.isEmpty()) return;
        startSessionForCard(cachedCards.get(0));
    }

    @FXML
    private void onAddAnotherBook(ActionEvent event) {
        openAddBookModal();
    }

    // ===== Modals =====

    private void openAddBookModal() {
        openModal("/sk/upjs/paz/ui/addBookModal.fxml", "Add Book");
    }

    private void openBookDetailsModal(String bookTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/upjs/paz/ui/bookDetailsModal.fxml"), I18N.getBundle());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), I18N.getBundle());
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
}
