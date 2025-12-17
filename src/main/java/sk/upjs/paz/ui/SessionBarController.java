package sk.upjs.paz.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import javafx.util.Duration;
import sk.upjs.paz.ui.i18n.I18N;

public class SessionBarController {

    @FXML private HBox root;

    @FXML private Label bookTitleLabel;
    @FXML private Label bookSubtitleLabel;
    @FXML private Label statusPillLabel;

    @FXML private Label timerLabel;
    @FXML private Label timerCaptionLabel;

    @FXML private Button playPauseButton;
    @FXML private Button stopButton;
    @FXML private Button moreButton;

    private boolean running = false;
    private int seconds = 0;
    private Timeline timeline;

    @FXML
    private void initialize() {
        if (bookTitleLabel != null && isBlank(bookTitleLabel.getText())) {
            bookTitleLabel.setText("Current book");
        }
        if (bookSubtitleLabel != null && isBlank(bookSubtitleLabel.getText())) {
            bookSubtitleLabel.setText("Author · Category");
        }
        if (statusPillLabel != null && isBlank(statusPillLabel.getText())) {
            statusPillLabel.setText("In Progress");
        }

        if (timerLabel != null) timerLabel.setText("00:00");
        if (timerCaptionLabel != null) timerCaptionLabel.setText("Ready to read");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        setPlayIcon();
    }


    public void refreshI18n() {
        if (timerCaptionLabel != null) {
            String t = timerCaptionLabel.getText();
            if ("Ready to read".equals(t)) timerCaptionLabel.setText(I18N.tr("session.readyToRead"));
            else if ("Reading now".equals(t)) timerCaptionLabel.setText(I18N.tr("session.readingNow"));
            else if ("Paused".equals(t)) timerCaptionLabel.setText(I18N.tr("session.paused"));
            else if ("Stopped".equals(t)) timerCaptionLabel.setText(I18N.tr("session.stopped"));
            else if ("More (TODO)".equals(t)) timerCaptionLabel.setText(I18N.tr("session.moreTodo"));
        }
    }
    // ===== Public API =====

    public void startSession(String title, String subtitle, String status) {
        setBookTitle(title != null ? title : "Current book");
        setBookSubtitle(subtitle != null ? subtitle : "");
        setStatusText(status != null ? status : "");

        resetTimer();
        running = true;
        setPauseIcon();

        if (timerCaptionLabel != null) timerCaptionLabel.setText("Reading now");
        if (timeline != null) timeline.playFromStart();
    }

    public void setBookTitle(String title) {
        if (bookTitleLabel != null) bookTitleLabel.setText(title);
    }

    public void setBookSubtitle(String subtitle) {
        if (bookSubtitleLabel != null) bookSubtitleLabel.setText(subtitle);
    }

    public void setStatusText(String status) {
        if (statusPillLabel != null) statusPillLabel.setText(status);
    }

    // ===== UI handlers =====

    @FXML
    private void handlePlayPause() {
        running = !running;

        if (running) {
            setPauseIcon();
            if (timerCaptionLabel != null) timerCaptionLabel.setText("Reading now");
            if (timeline != null) timeline.play();
        } else {
            setPlayIcon();
            if (timerCaptionLabel != null) timerCaptionLabel.setText("Paused");
            if (timeline != null) timeline.pause();
        }
    }

    @FXML
    private void handleStop() {
        // Pause timer while end-session modal is open
        boolean wasRunning = running;
        running = false;
        if (timeline != null) timeline.pause();
        setPlayIcon();
        if (timerCaptionLabel != null) timerCaptionLabel.setText("Stopped");

        EndSessionController end = openEndSessionModal();
        if (end != null && (end.isSessionSaved() || end.isDiscarded())) {
            // Hide global bar
            SessionBarHost.hide();
            // Optional: reset local state
            resetTimer();
            return;
        }

        // If user closed modal without saving/discarding, restore previous state
        running = wasRunning;
        if (running) {
            setPauseIcon();
            if (timerCaptionLabel != null) timerCaptionLabel.setText("Reading now");
            if (timeline != null) timeline.play();
        } else {
            setPlayIcon();
            if (timerCaptionLabel != null) timerCaptionLabel.setText("Paused");
        }
    }

    @FXML
    private void handleMore() {
        if (timerCaptionLabel != null) timerCaptionLabel.setText("More (TODO)");
        // TODO: open context menu or details
    }

    // ===== Internals =====

    private void tick() {
        seconds++;
        if (timerLabel != null) timerLabel.setText(format(seconds));
    }

    private void resetTimer() {
        seconds = 0;
        if (timerLabel != null) timerLabel.setText("00:00");
    }

    private EndSessionController openEndSessionModal() {
        Window owner = (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;
        if (owner == null) return null;

        // Provide placeholders; replace later with real book/session data
        return SceneNavigator.showEndSessionModal(
                owner,
                safeText(bookTitleLabel, "Current book"),
                safeText(bookSubtitleLabel, "Author · Category"),
                safeText(statusPillLabel, "In Progress"),
                "Current page: 187 / 320",
                "10:14",
                String.valueOf(Math.max(1, seconds / 60)),
                "164",
                "188"
        );
    }

    private void setPlayIcon() {
        setIconText("▶");
    }

    private void setPauseIcon() {
        setIconText("⏸");
    }

    private void setIconText(String t) {
        if (playPauseButton == null) return;

        if (playPauseButton.getGraphic() instanceof Label label) {
            label.setText(t);
        } else {
            playPauseButton.setText(t);
        }
    }

    private String format(int totalSec) {
        int m = totalSec / 60;
        int s = totalSec % 60;
        return String.format("%02d:%02d", m, s);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safeText(Label label, String fallback) {
        if (label == null) return fallback;
        String t = label.getText();
        return isBlank(t) ? fallback : t;
    }
}
