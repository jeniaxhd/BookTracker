package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import sk.upjs.paz.service.ServiceFactory;
import sk.upjs.paz.ui.i18n.I18N;

import java.io.IOException;

public class LibraryController {

    @FXML
    private BorderPane root;

    // Sidebar navigation
    @FXML
    private FlowPane allBooksContainer;
    @FXML
    private ToggleButton dashboardNavButton;
    @FXML
    private ToggleButton libraryNavButton;
    @FXML
    private ToggleButton currentlyReadingNavButton;
    @FXML
    private ToggleButton statisticsNavButton;


    // User profile
    @FXML
    private Button userProfileButton;
    @FXML
    private Label userNameLabel;

    // Header
    @FXML
    private Label headerTitleLabel;
    @FXML
    private TextField searchField;

    @FXML
    private Button filterButton;
    @FXML
    private ImageView filterIcon;

    @FXML
    private ToggleButton themeToggle;
    @FXML
    private ImageView themeIcon;

    @FXML
    private Button addBookButton;

    // Content (do NOT clear this - it contains your whole layout from FXML)
    @FXML
    private ScrollPane contentScrollPane;
    @FXML
    private VBox contentRoot;

    // Icons
    private Image moonIcon;
    private Image sunIcon;

    private Image filterLight;
    private Image filterDark;

    @FXML
    private void initialize() {
        // Sidebar toggle group
        ToggleGroup navGroup = new ToggleGroup();
        dashboardNavButton.setToggleGroup(navGroup);
        libraryNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setToggleGroup(navGroup);
        statisticsNavButton.setToggleGroup(navGroup);
        libraryNavButton.setSelected(true);

        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Library");
        }

        if (userNameLabel != null && AppState.getCurrentUser() != null) {
            userNameLabel.setText(AppState.getCurrentUser().getName());
        }

        // Load icons (safe)
        moonIcon = load("/sk/upjs/paz/ui/img/logoLight/moon.png");
        sunIcon = load("/sk/upjs/paz/ui/img/logoDark/sun.png");


        // Apply theme when scene is attached
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

        loadBooks();
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    // ===== THEME TOGGLE =====

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

        if (filterIcon != null) filterIcon.setImage(dark ? filterDark : filterLight);
    }

    // ===== NAVIGATION =====

    @FXML
    private void onDashboardSelected(ActionEvent event) {
        SceneNavigator.showDashboard();
    }

    @FXML
    private void onLibrarySelected(ActionEvent event) {
        libraryNavButton.setSelected(true);
    }

    @FXML
    private void onCurrentlyReadingSelected(ActionEvent event) {
        SceneNavigator.showCurrentlyReading();
    }

    @FXML
    private void onStatisticsSelected(ActionEvent event) {
        SceneNavigator.showStatistics();
    }

    // ===== HEADER ACTIONS =====

    @FXML
    private void onAddBook(ActionEvent event) {
        SceneNavigator.showAddBookModal(root.getScene().getWindow()); // Use navigator to inject services
        loadBooks(); // Refresh after modal closes
    }

    @FXML
    private void onUserProfile(ActionEvent event) {
        SceneNavigator.showUserProfile();
    }


    private void loadBooks() {
        if (AppState.getCurrentUser() == null) {
            return; // User is not logged in
        }

        long userId = AppState.getCurrentUser().getId();

        var userHasBookService = ServiceFactory.INSTANCE.getUserHasBookService();
        var bookService = ServiceFactory.INSTANCE.getBookService();

        var links = userHasBookService.listByUser(userId);

        allBooksContainer.getChildren().clear();

        for (var link : links) {
            var bookOpt = bookService.getById(link.bookId());
            if (bookOpt.isEmpty()) {
                continue; // Skip missing book records
            }

            var book = bookOpt.get();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/upjs/paz/ui/cards/bookCardLibrary.fxml"), I18N.getBundle());
            try {
                Parent cardRoot = loader.load();
                BookCardLibraryController controller = loader.getController();
                controller.setData(book, link.state());
                allBooksContainer.getChildren().add(cardRoot);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
