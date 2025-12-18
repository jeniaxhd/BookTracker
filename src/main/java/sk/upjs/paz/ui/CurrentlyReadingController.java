package sk.upjs.paz.ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sk.upjs.paz.service.CurrentlyReadingService;
import sk.upjs.paz.ui.dto.ActiveBookCard;
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


    // User / header
    @FXML private Button userProfileButton;
    @FXML private Label userNameLabel;
    @FXML private Label headerTitleLabel;

    // Header actions

    @FXML private ToggleButton themeToggle;
    @FXML private Button startSessionButton;

    // Header icons
    @FXML private ImageView searchIcon;

    @FXML private ImageView themeIcon;

    // Content
    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentRoot;

    // Container for cards
    @FXML private VBox cardsBox;

    private CurrentlyReadingService currentlyReadingService;
    private List<ActiveBookCard> cachedCards = new ArrayList<>();

    // Icons
    private Image searchLight, searchDark;

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


        if (currentlyReadingNavButton != null) currentlyReadingNavButton.setSelected(true);
        if (headerTitleLabel != null) headerTitleLabel.setText("Currently Reading");

        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        searchLight = load("/sk/upjs/paz/ui/img/logoLight/search.png");
        searchDark = load("/sk/upjs/paz/ui/img/logoDark/search.png");
        moonIcon = load("/sk/upjs/paz/ui/img/logoLight/moon.png");
        sunIcon = load("/sk/upjs/paz/ui/img/logoDark/sun.png");

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

        // If service was not injected via SceneNavigator, try lazy fallback
        if (currentlyReadingService == null) {
            try {
                currentlyReadingService = sk.upjs.paz.service.ServiceFactory.INSTANCE.getCurrentlyReadingService();
            } catch (Exception ignored) {
                // Service may not exist yet; controller can still work if injected later
            }
        }

        refresh();
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

        task.setOnFailed(e -> {
            if (task.getException() != null) task.getException().printStackTrace();
        });

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/upjs/paz/ui/cards/activeBookCard.fxml"), I18N.getBundle());
            Node node = loader.load();

            ActiveBookCardController c = loader.getController();
            c.setData(card);
            c.setOnContinue(this::startSessionForCard);
            c.setOnDetails(this::openBookDetailsForCard);

            return node;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load activeBookCard.fxml", ex);
        }
    }

    private void startSessionForCard(ActiveBookCard card) {
        if (card == null) return;
        if (!AppState.isLoggedIn()) return;

        long userId = AppState.getCurrentUser().getId();

        var readingSessionService = sk.upjs.paz.service.ServiceFactory.INSTANCE.getReadingSessionService();
        var userHasBookService = sk.upjs.paz.service.ServiceFactory.INSTANCE.getUserHasBookService();

        int startPage = Math.max(0, card.currentPage());

        // Start session in DB and get generated session id
        var session = readingSessionService.startNewSession(userId, card.bookId(), startPage);
        long sessionId = session.getId();

        // Ensure user-book state is READING
        userHasBookService.upsert(userId, card.bookId(), sk.upjs.paz.enums.BookState.READING);

        String subtitle = card.authorsText() + " · " + card.genreName();

        // Show session bar with context so end-session can update DB
        SceneNavigator.startSessionBar(userId, card.bookId(), sessionId, card.title(), subtitle, "READING");

        // Optional: refresh cards so the state/progress updates immediately
        refresh();
    }


    private void openBookDetailsForCard(ActiveBookCard card) {
        if (root == null || root.getScene() == null) return;

        // Open the details modal by bookId (new API)
        SceneNavigator.showBookDetailsModal(root.getScene().getWindow(), card.bookId());
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

    // ===== Header actions =====

    @FXML
    private void onSearch(ActionEvent event) {
        // later
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
        // Use navigator so services (BookService, GenreService, UserHasBookService) are injected correctly
        if (root != null && root.getScene() != null) {
            SceneNavigator.showAddBookModal(root.getScene().getWindow());
            refresh(); // reload cards after modal closes
        }
    }

}
