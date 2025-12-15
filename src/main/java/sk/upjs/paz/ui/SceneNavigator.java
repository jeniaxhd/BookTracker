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

import java.io.IOException;
import java.net.URL;

public final class SceneNavigator {

    private static Stage primaryStage;

    // Remember last shown page root (for session bar attach)
    private static BorderPane currentPageRoot;

    // Notifications popup (reused)
    private static Popup notificationsPopup;
    private static Parent notificationsRoot;
    private static NotificationsPopoverController notificationsController;

    private SceneNavigator() {}

    public static void init(Stage stage) {
        primaryStage = stage;
        showDashboard();
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Window getWindow() {
        return primaryStage;
    }

    // ===== Pages =====

    public static void showDashboard() { setPage("/sk/upjs/paz/ui/dashboard.fxml", "Dashboard"); }
    public static void showLibrary() { setPage("/sk/upjs/paz/ui/library.fxml", "Library"); }
    public static void showCurrentlyReading() { setPage("/sk/upjs/paz/ui/currentlyReading.fxml", "Currently Reading"); }
    public static void showStatistics() { setPage("/sk/upjs/paz/ui/statistics.fxml", "Statistics"); }
    public static void showSettings() { setPage("/sk/upjs/paz/ui/settings.fxml", "Settings"); }
    public static void showUserProfile() { setPage("/sk/upjs/paz/ui/userProfile.fxml", "Profile"); }

    public static void showLogin() { setPage("/sk/upjs/paz/ui/login.fxml", "Login"); }
    public static void showRegister() { setPage("/sk/upjs/paz/ui/register.fxml", "Register"); }

    private static void setPage(String fxmlPath, String title) {
        Loaded<Parent> loaded = loadRoot(fxmlPath);
        Parent root = loaded.root();

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

        // Session bar attach
        attachSessionBarIfPossible(root);

        // Keep popup theme synced if open
        syncFloatingOverlaysTheme();
    }

    // ===== Session Bar =====

    public static void attachSessionBarIfPossible(Parent pageRootNode) {
        BorderPane bp = findBorderPaneRoot(pageRootNode);
        if (bp != null) {
            currentPageRoot = bp;
            SessionBarHost.attach(bp);
        }
    }

    public static void startSessionBar(String title, String subtitle, String status) {
        if (currentPageRoot == null) return;
        SessionBarHost.show(currentPageRoot, title, subtitle, status);
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

    // ===== Notifications Popover =====

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

    public static void syncFloatingOverlaysTheme() {
        // Make popup follow current theme (it is NOT in the main Scene stylesheets)
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
            if (url == null) throw new IllegalStateException("Missing FXML: /fxml/notificationsPopover.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            notificationsRoot = loader.load();
            notificationsController = loader.getController();
            notificationsController.setPopup(notificationsPopup);

            notificationsPopup.getContent().clear();
            notificationsPopup.getContent().add(notificationsRoot);

        } catch (IOException e) {
            throw new RuntimeException("Cannot load notificationsPopover.fxml", e);
        }
    }

    // ===== Modals =====

    public static void showAddBookModal(Window owner) {
        openModal("/sk/upjs/paz/ui/addBookModal.fxml", "Add Book", owner);
    }

    public static BookDetailsModalController showBookDetailsModal(Window owner, String title) {
        try {
            URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/bookDetailsModal.fxml");
            if (url == null) throw new IllegalStateException("Missing FXML: /fxml/bookDetailsModal.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Parent dialogRoot = loader.load();
            BookDetailsModalController controller = loader.getController();

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title != null ? title : "Book details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

            return controller;
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
        try {
            URL url = SceneNavigator.class.getResource("/sk/upjs/paz/ui/endSession.fxml");
            if (url == null) throw new IllegalStateException("Missing FXML: /fxml/endSession.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Parent dialogRoot = loader.load();
            EndSessionController controller = loader.getController();

            controller.setInitialData(
                    bookTitle,
                    bookMeta,
                    statusText,
                    currentPageText,
                    startTime,
                    durationMinutes,
                    startPage,
                    endPage
            );

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("End session");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

            return controller;
        } catch (IOException e) {
            throw new RuntimeException("Cannot open endSession.fxml", e);
        }
    }

    private static void openModal(String fxmlPath, String title, Window owner) {
        try {
            URL url = SceneNavigator.class.getResource(fxmlPath);
            if (url == null) throw new IllegalStateException("Missing FXML: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(url);
            Parent dialogRoot = loader.load();

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Cannot open modal: " + fxmlPath, e);
        }
    }

    private static Loaded<Parent> loadRoot(String fxmlPath) {
        try {
            URL url = SceneNavigator.class.getResource(fxmlPath);
            if (url == null) throw new IllegalStateException("Missing FXML: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            return new Loaded<>(root);

        } catch (IOException e) {
            throw new RuntimeException("Cannot load: " + fxmlPath, e);
        }
    }

    private record Loaded<T>(T root) {}
}
