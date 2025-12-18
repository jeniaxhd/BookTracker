package sk.upjs.paz.ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.UserBookLink;
import sk.upjs.paz.enums.BookState;
import sk.upjs.paz.service.ServiceFactory;
import sk.upjs.paz.service.UserHasBookService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController {

    @FXML private BorderPane root;

    // Sidebar navigation
    @FXML private ToggleButton dashboardNavButton;
    @FXML private ToggleButton libraryNavButton;
    @FXML private ToggleButton currentlyReadingNavButton;
    @FXML private ToggleButton statisticsNavButton;

    // Header actions
    @FXML private Button searchButton;
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

    // IMPORTANT: must exist in FXML
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

    // Services
    private final UserHasBookService userHasBookService = ServiceFactory.INSTANCE.getUserHasBookService();

    @FXML
    private void initialize() {
        // English comments only:
        // Defensive: ensure UI is clickable
        root.setDisable(false);
        root.setMouseTransparent(false);

        setupNavToggleGroup();
        setupUserHeader();
        loadIcons();
        setupTheme();
        setupResponsiveWrapping();
        setupDemoStats();

        // English comments only:
        // Load real cards from DB
        loadCurrentlyReadingCards();
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
        // English comments only:
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
        // English comments only:
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
        // English comments only:
        // Already here
        loadCurrentlyReadingCards();
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

    // ========== DB LOADING: Currently Reading cards ==========

    private void loadCurrentlyReadingCards() {
        if (currentlyReadingContainer == null) return;

        if (AppState.getCurrentUser() == null) {
            // English comments only:
            // Not logged in -> clear cards
            currentlyReadingContainer.getChildren().clear();
            if (inProgressValueLabel != null) inProgressValueLabel.setText("0");
            return;
        }

        long userId = AppState.getCurrentUser().getId();

        Task<List<CardVm>> task = new Task<>() {
            @Override
            protected List<CardVm> call() {
                // English comments only:
                // Load links for the user and filter READING
                List<UserBookLink> links = userHasBookService.listByUser(userId);

                List<CardVm> result = new ArrayList<>();
                for (UserBookLink link : links) {
                    if (link.state() != BookState.READING) continue;

                    // English comments only:
                    // Resolve Book using reflection to avoid compile break on different APIs.
                    Book book = resolveBookById(link.bookId());
                    if (book == null) continue;

                    // English comments only:
                    // Progress can be wired later (e.g., from ReadingSession).
                    int endPage = 0;
                    int totalPages = 0;

                    result.add(new CardVm(book, link.state(), endPage, totalPages));
                }
                return result;
            }
        };

        task.setOnSucceeded(e -> {
            List<CardVm> cards = task.getValue();
            renderCurrentlyReadingCards(cards);

            // English comments only:
            // Update in-progress counter using loaded cards count
            if (inProgressValueLabel != null) inProgressValueLabel.setText(String.valueOf(cards.size()));
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderCurrentlyReadingCards(List<CardVm> cards) {
        if (currentlyReadingContainer == null) return;

        currentlyReadingContainer.getChildren().clear();

        for (CardVm vm : cards) {
            Node cardNode = loadActiveBookCard(vm);
            if (cardNode != null) {
                currentlyReadingContainer.getChildren().add(cardNode);
            }
        }
    }

    private Node loadActiveBookCard(CardVm vm) {
        // English comments only:
        // Loads existing card FXML and binds data via reflection to avoid coupling.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/upjs/paz/ui/cards/activeBookCard.fxml"));
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller != null) {
                bindToCardController(controller, vm);
            }
            return node;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void bindToCardController(Object controller, CardVm vm) {
        // English comments only:
        // Try common method signatures in order. Ignore if not found.
        invokeIfExists(controller, "setData",
                new Class[]{Book.class, BookState.class, int.class, int.class},
                new Object[]{vm.book(), vm.state(), vm.endPage(), vm.totalPages()});

        invokeIfExists(controller, "setBook",
                new Class[]{Book.class},
                new Object[]{vm.book()});

        invokeIfExists(controller, "setState",
                new Class[]{BookState.class},
                new Object[]{vm.state()});

        invokeIfExists(controller, "setProgress",
                new Class[]{int.class, int.class},
                new Object[]{vm.endPage(), vm.totalPages()});

        invokeIfExists(controller, "setEndPage",
                new Class[]{int.class},
                new Object[]{vm.endPage()});

        invokeIfExists(controller, "setTotalPages",
                new Class[]{int.class},
                new Object[]{vm.totalPages()});
    }

    private void invokeIfExists(Object target, String methodName, Class<?>[] paramTypes, Object[] args) {
        try {
            Method m = target.getClass().getMethod(methodName, paramTypes);
            m.invoke(target, args);
        } catch (Exception ignored) {
            // English comments only:
            // Method not present or invocation failed -> safely ignore.
        }
    }

    private Book resolveBookById(long bookId) {
        // English comments only:
        // Uses reflection against BookService instance to avoid compile-time dependency on method names.
        Object bookService = null;
        try {
            bookService = ServiceFactory.INSTANCE.getBookService();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        if (bookService == null) return null;

        // Try: Book getById(long)
        Book b = tryCallBookMethod(bookService, "getById", bookId);
        if (b != null) return b;

        // Try: Book get(long)
        b = tryCallBookMethod(bookService, "get", bookId);
        if (b != null) return b;

        // Try: Optional<Book> findById(long)
        b = tryCallOptionalBookMethod(bookService, "findById", bookId);
        if (b != null) return b;

        // Try: Optional<Book> getByIdOptional(long)
        b = tryCallOptionalBookMethod(bookService, "getByIdOptional", bookId);
        if (b != null) return b;

        // Try: Book find(long)
        b = tryCallBookMethod(bookService, "find", bookId);
        return b;
    }

    private Book tryCallBookMethod(Object service, String methodName, long bookId) {
        try {
            Method m = service.getClass().getMethod(methodName, long.class);
            Object res = m.invoke(service, bookId);
            if (res instanceof Book book) {
                return book;
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Book tryCallOptionalBookMethod(Object service, String methodName, long bookId) {
        try {
            Method m = service.getClass().getMethod(methodName, long.class);
            Object res = m.invoke(service, bookId);
            if (res instanceof Optional<?> opt && opt.isPresent() && opt.get() instanceof Book book) {
                return book;
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private record CardVm(Book book, BookState state, int endPage, int totalPages) {}

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
