package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

import java.net.URL;

public class EndSessionController {

    @FXML private BorderPane root;

    @FXML private Label bookTitleLabel;
    @FXML private Label bookMetaLabel;
    @FXML private Label statusPill;
    @FXML private Label currentPageLabel;

    @FXML private ImageView coverImageView;

    @FXML private TextField startTimeField;
    @FXML private TextField durationField;

    @FXML private TextField startPageField;
    @FXML private TextField endPageField;

    @FXML private Label pagesReadLabel;

    @FXML private TextArea notesArea;

    private boolean sessionSaved = false;
    private boolean discarded = false;

    private int parsedDurationMinutes = 0;
    private int parsedEndPage = 0;

    @FXML
    private void initialize() {
        if (startTimeField != null) startTimeField.setEditable(false);

        if (startPageField != null) {
            startPageField.textProperty().addListener((o, a, b) -> updatePagesRead());
        }
        if (endPageField != null) {
            endPageField.textProperty().addListener((o, a, b) -> updatePagesRead());
        }
    }

    public void setInitialData(
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
        setLabel(bookTitleLabel, bookTitle);
        setLabel(bookMetaLabel, bookMeta);
        setLabel(statusPill, statusText);
        setLabel(currentPageLabel, currentPageText);

        setField(startTimeField, startTime);
        setField(durationField, durationMinutes);

        setField(startPageField, startPage);
        setField(endPageField, endPage);

        loadCoverSmart(coverPath);

        updatePagesRead();
    }

    public boolean isSessionSaved() { return sessionSaved; }
    public boolean isDiscarded() { return discarded; }
    public int getDurationMinutes() { return parsedDurationMinutes; }
    public int getEndPage() { return parsedEndPage; }

    @FXML
    private void handleSave() {
        int startP = parseNonNegativeIntSafe(startPageField, "Start page");
        Integer endP = parseNonNegativeIntSafeNullable(endPageField, "End page");
        Integer durM = parsePositiveIntSafeNullable(durationField, "Duration (min)");

        if (endP == null) {
            showError("End page is required.");
            return;
        }
        if (durM == null) {
            showError("Duration (min) is required.");
            return;
        }
        if (endP < startP) {
            showError("End page must be >= start page.");
            return;
        }

        parsedEndPage = endP;
        parsedDurationMinutes = durM;

        sessionSaved = true;
        close();
    }

    @FXML
    private void handleDiscard() {
        discarded = true;
        close();
    }

    @FXML
    private void handleClose() {
        close();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private void updatePagesRead() {
        int start = parseIntOrZero(startPageField);
        int end = parseIntOrZero(endPageField);
        int diff = Math.max(0, end - start);
        if (pagesReadLabel != null) pagesReadLabel.setText(String.valueOf(diff));
    }

    //Cover loading

    private void loadCoverSmart(String coverPath) {
        if (coverImageView == null) return;

        // Clear image if nothing
        if (coverPath == null || coverPath.trim().isEmpty()) {
            coverImageView.setImage(null);
            return;
        }

        String path = coverPath.trim();

        // If it's an external URL / file URL
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file:")) {
            trySetImage(path);
            return;
        }

        // Try as classpath resource with leading slash
        if (!path.startsWith("/")) {
            URL url = getClass().getResource("/" + path);
            if (url != null) {
                trySetImage(url.toExternalForm());
                return;
            }
        }

        // Try as classpath resource as-is
        URL url = getClass().getResource(path);
        if (url != null) {
            trySetImage(url.toExternalForm());
            return;
        }

        // Try as local file path
        trySetImage("file:" + path);
    }

    private void trySetImage(String url) {
        try {
            coverImageView.setImage(new Image(url, true));
        } catch (Exception ignored) {
            coverImageView.setImage(null);
        }
    }

    //Parsing helpers

    private int parseIntOrZero(TextField f) {
        if (f == null) return 0;
        String s = f.getText();
        if (s == null) return 0;
        s = s.trim();
        if (s.isEmpty()) return 0;
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private int parseNonNegativeIntSafe(TextField f, String name) {
        Integer v = parseNonNegativeIntSafeNullable(f, name);
        return v == null ? 0 : v;
    }

    private Integer parseNonNegativeIntSafeNullable(TextField f, String name) {
        String s = f != null ? f.getText() : "";
        s = s == null ? "" : s.trim();
        if (s.isEmpty()) return null;
        try {
            int v = Integer.parseInt(s);
            if (v < 0) return null;
            return v;
        } catch (Exception ex) {
            return null;
        }
    }

    private Integer parsePositiveIntSafeNullable(TextField f, String name) {
        String s = f != null ? f.getText() : "";
        s = s == null ? "" : s.trim();
        if (s.isEmpty()) return null;
        try {
            int v = Integer.parseInt(s);
            if (v <= 0) return null;
            return v;
        } catch (Exception ex) {
            return null;
        }
    }

    //UI helpers

    private void setLabel(Label label, String text) {
        if (label == null) return;
        label.setText(text == null ? "" : text);
    }

    private void setField(TextField field, String text) {
        if (field == null) return;
        field.setText(text == null ? "" : text);
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Invalid input");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void close() {
        Window w = (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;
        if (w != null) w.hide();
    }
}
