package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class NotificationsPopoverController {

    @FXML private VBox root;
    @FXML private VBox itemsBox;

    @FXML private Button markAllReadButton;
    @FXML private Button viewAllButton;
    @FXML private Button closeButton;

    private Popup popup;

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    private void initialize() {
        // Nothing required
    }

    @FXML
    private void onMarkAllRead() {
        if (itemsBox == null) return;
        itemsBox.getChildren().clear();

        Label empty = new Label("No new notifications");
        empty.getStyleClass().add("book-meta-text");
        itemsBox.getChildren().add(empty);
    }

    @FXML
    private void onViewAll() {
        // Placeholder: open Statistics (or create a Notifications page later)
        SceneNavigator.showStatistics();
        hide();
    }

    @FXML
    private void onClose() {
        hide();
    }

    private void hide() {
        if (popup != null) popup.hide();
    }
}
