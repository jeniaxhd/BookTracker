package sk.upjs.paz.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public final class SessionBarHost {

    private static BorderPane attachedRoot;
    private static Node barNode;
    private static SessionBarController controller;

    private static long activeUserId;
    private static long activeBookId;
    private static long activeSessionId;

    private static String activeTitle;
    private static String activeSubtitle;
    private static String activeStatus;

    private static String activeCoverPath;
    private static int activeTotalPages;
    private static int activeStartPage;

    private SessionBarHost() {
    }

    public static void attach(BorderPane pageRoot) {
        attachedRoot = pageRoot;
    }

    public static void show(BorderPane pageRoot,
                            String title,
                            String subtitle,
                            String status,
                            long userId,
                            long bookId,
                            long sessionId,
                            String coverPath,
                            int totalPages,
                            int startPage) {

        attachedRoot = pageRoot;

        activeTitle = title;
        activeSubtitle = subtitle;
        activeStatus = status;

        activeUserId = userId;
        activeBookId = bookId;
        activeSessionId = sessionId;

        activeCoverPath = coverPath;
        activeTotalPages = totalPages;
        activeStartPage = startPage;

        ensureLoaded();

        if (attachedRoot != null) {
            attachedRoot.setBottom(barNode);
        }

        if (controller != null) {
            controller.startSession(title, subtitle, status);
        }
    }

    public static void hide() {
        if (attachedRoot != null) attachedRoot.setBottom(null);
        if (controller != null) controller.stopAndReset();

        activeUserId = 0;
        activeBookId = 0;
        activeSessionId = 0;
        activeCoverPath = null;
        activeTotalPages = 0;
        activeStartPage = 0;
    }

    private static void ensureLoaded() {
        if (barNode != null && controller != null) return;

        try {
            var url = SessionBarHost.class.getResource("/sk/upjs/paz/ui/sessionBar.fxml");
            if (url == null) throw new IllegalStateException("Missing FXML: /sk/upjs/paz/ui/sessionBar.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            barNode = loader.load();
            controller = loader.getController();

        } catch (IOException e) {
            throw new RuntimeException("Cannot load sessionBar.fxml", e);
        }
    }

    public static long getActiveUserId() {
        return activeUserId;
    }

    public static long getActiveBookId() {
        return activeBookId;
    }

    public static long getActiveSessionId() {
        return activeSessionId;
    }

    public static String getActiveTitle() {
        return activeTitle;
    }

    public static String getActiveSubtitle() {
        return activeSubtitle;
    }

    public static String getActiveStatus() {
        return activeStatus;
    }

    public static String getActiveCoverPath() {
        return activeCoverPath;
    }

    public static int getActiveTotalPages() {
        return activeTotalPages;
    }

    public static int getActiveStartPage() {
        return activeStartPage;
    }

    // Зручний overload без cover/сторінок (дефолти)
    public static void show(BorderPane pageRoot,
                            String title,
                            String subtitle,
                            String status,
                            long userId,
                            long bookId,
                            long sessionId) {
        show(pageRoot, title, subtitle, status, userId, bookId, sessionId, null, 0, 0);
    }

}
