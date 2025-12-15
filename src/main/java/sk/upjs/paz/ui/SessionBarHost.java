package sk.upjs.paz.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public final class SessionBarHost {

    private static Parent barRoot;
    private static SessionBarController controller;

    private static BorderPane attachedTo;
    private static boolean visible = false;

    private SessionBarHost() {}

    public static void attach(BorderPane pageRoot) {
        if (pageRoot == null) return;

        ensureLoaded();

        // Detach from previous root if it still contains the bar
        if (attachedTo != null && attachedTo != pageRoot) {
            if (attachedTo.getBottom() == barRoot) {
                attachedTo.setBottom(null);
            }
        }

        attachedTo = pageRoot;

        if (visible) {
            attachedTo.setBottom(barRoot);
        }
    }

    public static void show(BorderPane pageRoot, String title, String subtitle, String status) {
        ensureLoaded();
        attach(pageRoot);

        visible = true;

        if (attachedTo != null) {
            attachedTo.setBottom(barRoot);
        }

        if (controller != null) {
            controller.setBookTitle(title != null ? title : "Current book");
            controller.setBookSubtitle(subtitle != null ? subtitle : "");
            controller.setStatusText(status != null ? status : "");
        }
    }

    public static void hide() {
        visible = false;
        detachFromParent();
        attachedTo = null;
    }

    public static boolean isVisible() {
        return visible;
    }

    public static SessionBarController getController() {
        ensureLoaded();
        return controller;
    }

    private static void detachFromParent() {
        if (barRoot == null) return;

        var parent = barRoot.getParent();

        // Most common case: bar is in BorderPane.bottom
        if (parent instanceof BorderPane bp) {
            if (bp.getBottom() == barRoot) {
                bp.setBottom(null);
            }
            return;
        }

        // Fallback: if someone put it into a Pane
        if (parent instanceof Pane pane) {
            pane.getChildren().remove(barRoot);
        }
    }

    private static void ensureLoaded() {
        if (barRoot != null) return;

        var url = SessionBarHost.class.getResource("/sk/upjs/paz/ui/sessionBar.fxml");
        if (url == null) {
            throw new IllegalStateException("Missing FXML: /fxml/sessionBar.fxml");
        }

        try {
            FXMLLoader loader = new FXMLLoader(url);
            barRoot = loader.load();
            controller = loader.getController();
        } catch (Exception e) {
            throw new RuntimeException("Cannot load sessionBar.fxml", e);
        }
    }
}
