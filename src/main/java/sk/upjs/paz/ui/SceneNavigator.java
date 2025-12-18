package sk.upjs.paz.ui;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.enums.BookState;
import sk.upjs.paz.service.ServiceFactory;
import sk.upjs.paz.ui.i18n.I18N;

import java.io.IOException;
import java.net.URL;

public final class SceneNavigator {

    private static Stage primaryStage;
    private static BorderPane currentPageRoot;

    private static Popup notificationsPopup;
    private static Parent notificationsRoot;
    private static NotificationsPopoverController notificationsController;

    // === Responsive sizing behavior ===
    private static boolean firstShow = true;
    private static boolean lastWasAuth = false;

    private SceneNavigator() {}

    public static void init(Stage stage) {
        primaryStage = stage;
        showDashboard();
        primaryStage.show();
    }

    // ===== Pages =====

    public static void showDashboard() { setPage("/sk/upjs/paz/ui/dashboard.fxml", tr("window.dashboard", "Dashboard")); }
    public static void showLibrary() { setPage("/sk/upjs/paz/ui/library.fxml", tr("window.library", "Library")); }
    public static void showCurrentlyReading() { setPage("/sk/upjs/paz/ui/currentlyReading.fxml", tr("window.currentlyReading", "Currently Reading")); }
    public static void showStatistics() { setPage("/sk/upjs/paz/ui/statistics.fxml", tr("window.statistics", "Statistics")); }
    public static void showUserProfile() { setPage("/sk/upjs/paz/ui/userProfile.fxml", tr("window.profile", "Profile")); }
    public static void showLogin() { setPage("/sk/upjs/paz/ui/login.fxml", tr("window.login", "Login")); }
    public static void showRegister() { setPage("/sk/upjs/paz/ui/register.fxml", tr("window.register", "Register")); }

    private static void setPage(String fxmlPath, String title) {
        // важливо: якщо popover залишився відкритим — він може перехоплювати кліки
        hideNotificationsIfOpen();

        Parent root = loadRoot(fxmlPath);
        boolean isAuth = isAuthPage(fxmlPath);

        Scene scene;
        if (primaryStage.getScene() == null) {
            scene = new Scene(root);
            primaryStage.setScene(scene);
        } else {
            scene = primaryStage.getScene();
            scene.setRoot(root);
        }

        primaryStage.setTitle(title);
        ThemeManager.apply(scene);

        applyWindowMinSizeFor(fxmlPath);

        if (firstShow || (isAuth != lastWasAuth)) {
            applyInitialWindowSizeFor(fxmlPath);
            primaryStage.centerOnScreen();
        }

        firstShow = false;
        lastWasAuth = isAuth;

        attachSessionBarIfPossible(root);
        syncFloatingOverlaysTheme();
    }

    private static boolean isAuthPage(String fxmlPath) {
        return fxmlPath.endsWith("/login.fxml") || fxmlPath.endsWith("/register.fxml");
    }

    private static void applyWindowMinSizeFor(String fxmlPath) {
        if (primaryStage == null) return;

        boolean login = fxmlPath.endsWith("/login.fxml");
        boolean register = fxmlPath.endsWith("/register.fxml");

        if (login) {
            primaryStage.setMinWidth(820);
            primaryStage.setMinHeight(520);
        } else if (register) {
            primaryStage.setMinWidth(940);
            primaryStage.setMinHeight(640);
        } else {
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
        }
    }

    private static void applyInitialWindowSizeFor(String fxmlPath) {
        if (primaryStage == null) return;

        boolean login = fxmlPath.endsWith("/login.fxml");
        boolean register = fxmlPath.endsWith("/register.fxml");

        if (login) {
            primaryStage.setWidth(860);
            primaryStage.setHeight(540);
        } else if (register) {
            primaryStage.setWidth(980);
            primaryStage.setHeight(720);
        } else {
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
        }
    }

    // ===== Session bar =====

    public static void attachSessionBarIfPossible(Parent pageRootNode) {
        BorderPane bp = findBorderPaneRoot(pageRootNode);
        if (bp != null) {
            currentPageRoot = bp;
            SessionBarHost.attach(bp);
        }
    }

    public static void startSessionBar(long userId, long bookId, long sessionId,
                                       String title, String subtitle, String status) {
        if (currentPageRoot == null) return;
        SessionBarHost.show(currentPageRoot, title, subtitle, status, userId, bookId, sessionId);
    }

    public static void stopSessionBar() {
        SessionBarHost.hide();
    }

    private static BorderPane findBorderPaneRoot(Parent root) {
        if (root instanceof BorderPane bp) return bp;
        var node = root.lookup("#root");
        if (node instanceof BorderPane bp) return bp;
        return null;
    }

    // ===== End session flow =====

    public static void openEndSessionFromBar(int elapsedSeconds) {
        if (primaryStage == null) return;

        long sessionId = SessionBarHost.getActiveSessionId();
        long userId = SessionBarHost.getActiveUserId();
        long bookId = SessionBarHost.getActiveBookId();
        if (sessionId <= 0 || userId <= 0 || bookId <= 0) return;

        var readingSessionService = ServiceFactory.INSTANCE.getReadingSessionService();
        var bookService = ServiceFactory.INSTANCE.getBookService();
        var userHasBookService = ServiceFactory.INSTANCE.getUserHasBookService();

        var sessionOpt = readingSessionService.getById(sessionId);
        if (sessionOpt.isEmpty()) return;

        var session = sessionOpt.get();

        String bookTitle = bookService.getById(bookId).map(Book::getTitle).orElse(tr("book.current", "Current book"));

        String statusText = "READING";
        var dt = session.getStart();
        String startTime = (dt == null) ? "" : dt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        int minutesFromTimer = Math.max(1, elapsedSeconds / 60);
        String durationMinutes = String.valueOf(minutesFromTimer);

        String startPage = String.valueOf(Math.max(0, session.getEndPage()));
        String endPage = "";

        EndSessionController controller = showEndSessionModal(
                primaryStage, bookTitle, "", statusText, "",
                startTime, durationMinutes, startPage, endPage
        );

        if (controller == null) return;
        if (controller.isDiscarded()) return;
        if (!controller.isSessionSaved()) return;

        int endP = controller.getEndPage();
        int durM = controller.getDurationMinutes();

        readingSessionService.finishSession(sessionId, endP, durM, BookState.READING);
        userHasBookService.upsert(userId, bookId, BookState.READING);

        stopSessionBar();
        showCurrentlyReading();
    }

    public static void openEndSessionFromBar() {
        openEndSessionFromBar(0);
    }

    // ===== Modals =====

    public static void showAddBookModal(Window owner) {
        try {
            URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/addBookModal.fxml");
            if (url == null) throw new IllegalStateException("Missing FXML: /sk/upjs/paz/ui/addBookModal.fxml");

            FXMLLoader loader = newLoader(url);
            Parent dialogRoot = loader.load();

            AddBookModalController controller = loader.getController();
            controller.setBookService(ServiceFactory.INSTANCE.getBookService());
            controller.setGenreService(ServiceFactory.INSTANCE.getGenreService());
            controller.setUserHasBookService(ServiceFactory.INSTANCE.getUserHasBookService());

            if (!AppState.isLoggedIn()) throw new IllegalStateException("No logged-in user");
            controller.setCurrentUserId(AppState.getCurrentUser().getId());
            controller.initData();

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(tr("modal.addBook.title", "Add Book"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(true);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Cannot open addBookModal.fxml", e);
        }
    }

    public static void showBookDetailsModal(Window owner, long bookId) {
        try {
            URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/bookDetailsModal.fxml");
            if (url == null) throw new IllegalStateException("Missing FXML: /sk/upjs/paz/ui/bookDetailsModal.fxml");

            FXMLLoader loader = newLoader(url);
            Parent dialogRoot = loader.load();

            BookDetailsModalController controller = loader.getController();

            if (!AppState.isLoggedIn()) throw new IllegalStateException("No logged-in user");

            controller.setServices(
                    ServiceFactory.INSTANCE.getBookService(),
                    ServiceFactory.INSTANCE.getUserHasBookService(),
                    ServiceFactory.INSTANCE.getReadingSessionService(),
                    ServiceFactory.INSTANCE.getReviewService()
            );

            controller.setContext(AppState.getCurrentUser().getId(), bookId);
            controller.load();

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(tr("modal.bookDetails.title", "Book details"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.setScene(dialogScene);

            // ✅ prispôsobivý layout: вікно можна регулювати + гарні розміри
            dialogStage.setResizable(true);
            dialogStage.setWidth(920);
            dialogStage.setHeight(720);
            dialogStage.setMinWidth(720);
            dialogStage.setMinHeight(520);

            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Cannot open bookDetailsModal.fxml", e);
        }
    }


    public static EndSessionController showEndSessionModal(
            Window owner,
            String bookTitle,
            String bookMeta,
            String statusText,
            String currentPageText,
            String startTime,
            String durationMinutes,
            String startPage,
            String endPage
    ) {
        return showEndSessionModal(owner, bookTitle, bookMeta, statusText, currentPageText,
                startTime, durationMinutes, startPage, endPage, null);
    }

    public static EndSessionController showEndSessionModal(
            Window owner,
            String bookTitle,
            String bookMeta,
            String statusText,
            String currentPageText,
            String startTime,
            String durationMinutes,
            String startPage,
            String endPage,
            String coverPath
    ) {
        try {
            URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/endSession.fxml");
            if (url == null) throw new IllegalStateException("Missing FXML: /sk/upjs/paz/ui/endSession.fxml");

            FXMLLoader loader = newLoader(url);
            Parent dialogRoot = loader.load();
            EndSessionController controller = loader.getController();

            controller.setInitialData(
                    bookTitle, bookMeta, statusText, currentPageText,
                    startTime, durationMinutes, startPage, endPage,
                    coverPath
            );

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(tr("modal.endSession.title", "End session"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(true);
            dialogStage.showAndWait();

            return controller;

        } catch (IOException e) {
            throw new RuntimeException("Cannot open endSession.fxml", e);
        }
    }

    // ===== Notifications popover =====

    public static void toggleNotifications(Button anchorButton) {
        if (anchorButton == null || primaryStage == null) return;

        ensureNotificationsLoaded();

        if (notificationsPopup.isShowing()) {
            notificationsPopup.hide();
            return;
        }

        syncFloatingOverlaysTheme();

        Bounds b = anchorButton.localToScreen(anchorButton.getBoundsInLocal());
        if (b == null) return;

        double prefW = 360.0;
        double x = b.getMaxX() - prefW;
        double y = b.getMaxY() + 8;

        notificationsPopup.show(primaryStage, x, y);
    }

    private static void hideNotificationsIfOpen() {
        if (notificationsPopup != null && notificationsPopup.isShowing()) {
            notificationsPopup.hide();
        }
    }

    public static void syncFloatingOverlaysTheme() {
        if (notificationsRoot != null) {
            notificationsRoot.getStylesheets().clear();
            String css = ThemeManager.isDarkMode() ? "/css/darkTheme.css" : "/css/lightTheme.css";
            URL url = SceneNavigator.class.getResource(css);
            if (url != null) notificationsRoot.getStylesheets().add(url.toExternalForm());
        }
    }

    private static void ensureNotificationsLoaded() {
        if (notificationsPopup != null && notificationsRoot != null) return;

        notificationsPopup = new Popup();
        notificationsPopup.setAutoHide(true);

        try {
            URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/notificationsPopover.fxml");
            if (url == null) throw new IllegalStateException("Missing FXML: /sk/upjs/paz/ui/notificationsPopover.fxml");

            FXMLLoader loader = newLoader(url);
            notificationsRoot = loader.load();
            notificationsController = loader.getController();
            notificationsController.setPopup(notificationsPopup);

            notificationsPopup.getContent().clear();
            notificationsPopup.getContent().add(notificationsRoot);

        } catch (IOException e) {
            throw new RuntimeException("Cannot load notificationsPopover.fxml", e);
        }
    }

    // ===== FXML loading with i18n =====

    private static Parent loadRoot(String fxmlPath) {
        try {
            URL url = SceneNavigator.class.getResource(fxmlPath);
            if (url == null) throw new IllegalStateException("Missing FXML: " + fxmlPath);

            FXMLLoader loader = newLoader(url);
            return loader.load();

        } catch (IOException e) {
            throw new RuntimeException("Cannot load: " + fxmlPath, e);
        }
    }

    private static FXMLLoader newLoader(URL url) {
        FXMLLoader loader = new FXMLLoader(url);
        loader.setResources(I18N.getBundle());
        return loader;
    }

    private static String tr(String key, String fallback) {
        try {
            return I18N.getBundle().getString(key);
        } catch (Exception e) {
            return fallback;
        }
    }
}
