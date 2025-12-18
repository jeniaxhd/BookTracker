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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController {

    @FXML private BorderPane root;

    @FXML private ToggleButton dashboardNavButton;
    @FXML private ToggleButton libraryNavButton;
    @FXML private ToggleButton currentlyReadingNavButton;
    @FXML private ToggleButton statisticsNavButton;

    @FXML private Button searchButton;
    @FXML private ToggleButton themeToggle;
    @FXML private Button addBookButton;
    @FXML private Button notificationsButton;
    @FXML private Button userProfileButton;

    @FXML private ImageView searchIcon;
    @FXML private ImageView notificationsIcon;
    @FXML private ImageView themeIcon;

    @FXML private Label headerTitleLabel;
    @FXML private Label userNameLabel;

    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentRoot;

    @FXML private FlowPane currentlyReadingContainer;
    @FXML private FlowPane bottomFlow;

    @FXML private VBox queueContainer;
    @FXML private Label booksReadValueLabel;
    @FXML private Label inProgressValueLabel;

    @FXML private ResourceBundle resources;

    private Image searchLight;
    private Image searchDark;
    private Image bellLight;
    private Image bellDark;
    private Image moonIcon;
    private Image sunIcon;

    private final UserHasBookService userHasBookService = ServiceFactory.INSTANCE.getUserHasBookService();

    @FXML
    private void initialize() {
        root.setDisable(false);
        root.setMouseTransparent(false);

        setupNavToggleGroup();
        setupUserHeader();
        loadIcons();
        setupTheme();
        setupResponsiveWrapping();
        setupDemoStats();

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

    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        Scene scene = root.getScene();
        if (scene != null) {
            ThemeManager.apply(scene);
            updateIconsForTheme();
        }
    }

    @FXML
    private void onDashboardSelected(ActionEvent event) {
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

    private void loadCurrentlyReadingCards() {
        if (currentlyReadingContainer == null) return;

        if (AppState.getCurrentUser() == null) {
            currentlyReadingContainer.getChildren().clear();
            if (inProgressValueLabel != null) inProgressValueLabel.setText("0");
            return;
        }

        long userId = AppState.getCurrentUser().getId();

        Task<List<CardVm>> task = new Task<>() {
            @Override
            protected List<CardVm> call() {
                List<UserBookLink> links = userHasBookService.listByUser(userId);

                List<CardVm> result = new ArrayList<>();
                for (UserBookLink link : links) {
                    if (link.state() != BookState.READING) continue;

                    Book book = resolveBookById(link.bookId());
                    if (book == null) continue;

                    result.add(new CardVm(book, link.state(), 0, 0));
                }
                return result;
            }
        };

        task.setOnSucceeded(e -> {
            List<CardVm> cards = task.getValue();
            renderCurrentlyReadingCards(cards);
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
        currentlyReadingContainer.getChildren().clear();

        for (CardVm vm : cards) {
            Node node = loadActiveBookCard(vm);
            if (node != null) currentlyReadingContainer.getChildren().add(node);
        }
    }

    private Node loadActiveBookCard(CardVm vm) {
        var url = getClass().getResource("/sk/upjs/paz/ui/cards/activeDashboardBookCard.fxml");
        if (url == null) {
            System.err.println("activeBookCard.fxml not found: /sk/upjs/paz/ui/cards/activeDashboardBookCard.fxml");
            return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(url, resources);
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller != null) bindCard(controller, vm);

            return node;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void bindCard(Object controller, CardVm vm) {
        invoke(controller, "setData",
                new Class[]{Book.class, BookState.class, int.class, int.class},
                new Object[]{vm.book(), vm.state(), vm.endPage(), vm.totalPages()});

        invoke(controller, "setBook",
                new Class[]{Book.class},
                new Object[]{vm.book()});

        invoke(controller, "setState",
                new Class[]{BookState.class},
                new Object[]{vm.state()});
    }

    private void invoke(Object target, String method, Class<?>[] types, Object[] args) {
        try {
            Method m = target.getClass().getMethod(method, types);
            m.invoke(target, args);
        } catch (Exception ignored) {
        }
    }

    private Book resolveBookById(long bookId) {
        Object bookService;
        try {
            bookService = ServiceFactory.INSTANCE.getBookService();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        if (bookService == null) return null;

        Book b = callBook(bookService, "getById", bookId);
        if (b != null) return b;

        b = callBook(bookService, "get", bookId);
        if (b != null) return b;

        b = callOptionalBook(bookService, "findById", bookId);
        if (b != null) return b;

        return callBook(bookService, "find", bookId);
    }

    private Book callBook(Object service, String method, long id) {
        try {
            Method m = service.getClass().getMethod(method, long.class);
            Object res = m.invoke(service, id);
            return (res instanceof Book book) ? book : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Book callOptionalBook(Object service, String method, long id) {
        try {
            Method m = service.getClass().getMethod(method, long.class);
            Object res = m.invoke(service, id);
            if (res instanceof Optional<?> opt && opt.isPresent() && opt.get() instanceof Book book) {
                return book;
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private record CardVm(Book book, BookState state, int endPage, int totalPages) {}

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
