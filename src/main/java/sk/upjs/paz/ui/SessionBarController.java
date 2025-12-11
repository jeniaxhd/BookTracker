package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SessionBarController {

    @FXML
    private HBox root;

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Label bookSubtitleLabel;

    @FXML
    private Label statusPillLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label timerCaptionLabel;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button moreButton;

    private boolean running = true;

    @FXML
    private void initialize() {
        // Default demo values – you can override them from outside
        if (bookTitleLabel != null && bookTitleLabel.getText() == null) {
            bookTitleLabel.setText("Current book");
        }
        if (bookSubtitleLabel != null && bookSubtitleLabel.getText() == null) {
            bookSubtitleLabel.setText("Author · Category");
        }
        if (timerLabel != null && timerLabel.getText() == null) {
            timerLabel.setText("00:00");
        }
        if (timerCaptionLabel != null && timerCaptionLabel.getText() == null) {
            timerCaptionLabel.setText("Ready to read");
        }

        setPauseIcon(); // start as “running”
    }

    @FXML
    private void handlePlayPause() {
        running = !running;

        if (running) {
            setPauseIcon();
            timerCaptionLabel.setText("Reading now");
            // TODO: resume timer logic
        } else {
            setPlayIcon();
            timerCaptionLabel.setText("Paused");
            // TODO: pause timer logic
        }
    }

    @FXML
    private void handleStop() {
        running = false;
        setPlayIcon();
        timerLabel.setText("00:00");
        timerCaptionLabel.setText("Stopped");
        // TODO: end session, open EndSession dialog if needed
    }

    @FXML
    private void handleMore() {
        // TODO: open menu / book details / options
    }

    // ===== helpers =====

    private void setPlayIcon() {
        if (playPauseButton != null && playPauseButton.getGraphic() instanceof Label label) {
            label.setText("▶");
        } else if (playPauseButton != null) {
            playPauseButton.setText("▶");
        }
    }

    private void setPauseIcon() {
        if (playPauseButton != null && playPauseButton.getGraphic() instanceof Label label) {
            label.setText("⏸");
        } else if (playPauseButton != null) {
            playPauseButton.setText("⏸");
        }
    }

    // Optional setters, if you want to pass data from other controllers

    public void setBookTitle(String title) {
        bookTitleLabel.setText(title);
    }

    public void setBookSubtitle(String subtitle) {
        bookSubtitleLabel.setText(subtitle);
    }

    public void setStatusText(String status) {
        statusPillLabel.setText(status);
    }

    public void setTimerText(String text) {
        timerLabel.setText(text);
    }
}
