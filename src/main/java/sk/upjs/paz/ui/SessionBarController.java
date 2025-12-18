package sk.upjs.paz.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

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
        if (timerLabel != null) timerLabel.setText("00:00");
        if (timerCaptionLabel != null) timerCaptionLabel.setText("Ready to read");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        setPlayIcon();
    }

    // called by SessionBarHost
    public void startSession(String title, String subtitle, String status) {
        if (bookTitleLabel != null) bookTitleLabel.setText(title != null ? title : "Current book");
        if (bookSubtitleLabel != null) bookSubtitleLabel.setText(subtitle != null ? subtitle : "");
        if (statusPillLabel != null) statusPillLabel.setText(status != null ? status : "");

        resetTimer();
        running = true;
        setPauseIcon();

        if (timerCaptionLabel != null) timerCaptionLabel.setText("Reading now");
        if (timeline != null) timeline.playFromStart();
    }

    public void stopAndReset() {
        running = false;
        if (timeline != null) timeline.stop();
        resetTimer();
        setPlayIcon();
        if (timerCaptionLabel != null) timerCaptionLabel.setText("Ready to read");
    }

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
        boolean wasRunning = running;
        running = false;
        if (timeline != null) timeline.pause();
        setPlayIcon();

        // IMPORTANT: open modal via SceneNavigator (with DB context)
        SceneNavigator.openEndSessionFromBar(getElapsedSeconds());

        // if bar is still visible and user closed modal, restore state
        running = wasRunning;
        if (running) {
            setPauseIcon();
            if (timeline != null) timeline.play();
        } else {
            setPlayIcon();
        }
    }

    @FXML
    private void handleMore() {
        if (timerCaptionLabel != null) timerCaptionLabel.setText("More (TODO)");
    }

    private void tick() {
        seconds++;
        if (timerLabel != null) timerLabel.setText(format(seconds));
    }

    private void resetTimer() {
        seconds = 0;
        if (timerLabel != null) timerLabel.setText("00:00");
    }

    public int getElapsedSeconds() {
        return seconds;
    }

    private void setPlayIcon() { setIconText("▶"); }
    private void setPauseIcon() { setIconText("⏸"); }

    private void setIconText(String t) {
        if (playPauseButton == null) return;
        if (playPauseButton.getGraphic() instanceof Label label) label.setText(t);
        else playPauseButton.setText(t);
    }

    private String format(int totalSec) {
        int m = totalSec / 60;
        int s = totalSec % 60;
        return String.format("%02d:%02d", m, s);
    }
}
