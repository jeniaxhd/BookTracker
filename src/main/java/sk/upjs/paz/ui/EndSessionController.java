package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class EndSessionController {

    @FXML private BorderPane root;

    // Book summary
    @FXML private Label bookTitleLabel;
    @FXML private Label bookMetaLabel;
    @FXML private Label statusPill;
    @FXML private Label currentPageLabel;

    // Time
    @FXML private TextField startTimeField;
    @FXML private TextField durationField;

    // Notes
    @FXML private TextArea notesArea;

    // Pages
    @FXML private TextField startPageField;
    @FXML private TextField endPageField;
    @FXML private Label pagesReadLabel;

    // Feeling toggles
    @FXML private ToggleButton deepFocusToggle;
    @FXML private ToggleButton lightReadingToggle;
    @FXML private ToggleButton studyToggle;
    @FXML private ToggleButton relaxToggle;

    // Checkboxes
    @FXML private CheckBox countToGoalCheck;
    @FXML private CheckBox markFinishedCheck;

    private boolean sessionSaved = false;
    private boolean discarded = false;

    private ToggleGroup feelGroup;

    @FXML
    private void initialize() {
        // Defensive: if something is missing in FXML, do nothing instead of crashing
        if (deepFocusToggle == null || lightReadingToggle == null || studyToggle == null || relaxToggle == null) {
            return;
        }

        feelGroup = new ToggleGroup();
        deepFocusToggle.setToggleGroup(feelGroup);
        lightReadingToggle.setToggleGroup(feelGroup);
        studyToggle.setToggleGroup(feelGroup);
        relaxToggle.setToggleGroup(feelGroup);

        deepFocusToggle.setSelected(true);

        // Prevent "no selection" state
        feelGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
                return;
            }
            updateFeelPillStyles();
        });
        updateFeelPillStyles();

        if (startPageField != null) startPageField.textProperty().addListener((obs, o, n) -> updatePagesRead());
        if (endPageField != null) endPageField.textProperty().addListener((obs, o, n) -> updatePagesRead());

        updatePagesRead();
    }

    // ===== Public API =====

    public boolean isSessionSaved() {
        return sessionSaved;
    }

    public boolean isDiscarded() {
        return discarded;
    }

    public void setInitialData(
            String bookTitle,
            String bookMeta,
            String statusText,
            String currentPageText,
            String startTime,
            String durationMinutes,
            String startPage,
            String endPage
    ) {
        if (bookTitleLabel != null && bookTitle != null) bookTitleLabel.setText(bookTitle);
        if (bookMetaLabel != null && bookMeta != null) bookMetaLabel.setText(bookMeta);
        if (statusPill != null && statusText != null) statusPill.setText(statusText);
        if (currentPageLabel != null && currentPageText != null) currentPageLabel.setText(currentPageText);

        if (startTimeField != null && startTime != null) startTimeField.setText(startTime);
        if (durationField != null && durationMinutes != null) durationField.setText(durationMinutes);

        if (startPageField != null && startPage != null) startPageField.setText(startPage);
        if (endPageField != null && endPage != null) endPageField.setText(endPage);

        updatePagesRead();
    }

    // ===== Button handlers =====

    @FXML
    private void handleSave() {
        sessionSaved = true;
        discarded = false;
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleDiscard() {
        discarded = true;
        sessionSaved = false;
        closeWindow();
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    // ===== Internal helpers =====

    private void closeWindow() {
        Window w = (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;
        if (w != null) w.hide();
    }

    private void updatePagesRead() {
        if (pagesReadLabel == null) return;

        int start = parseIntSafe(startPageField != null ? startPageField.getText() : null);
        int end = parseIntSafe(endPageField != null ? endPageField.getText() : null);

        int pagesRead = 0;
        if (start > 0 && end >= start) {
            pagesRead = end - start; // keep your logic
        }
        pagesReadLabel.setText(String.valueOf(pagesRead));
    }

    private int parseIntSafe(String value) {
        if (value == null) return 0;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateFeelPillStyles() {
        updateSinglePillStyle(deepFocusToggle);
        updateSinglePillStyle(lightReadingToggle);
        updateSinglePillStyle(studyToggle);
        updateSinglePillStyle(relaxToggle);
    }

    private void updateSinglePillStyle(ToggleButton btn) {
        if (btn == null) return;

        if (btn.isSelected()) {
            if (!btn.getStyleClass().contains("session-feel-pill--active")) {
                btn.getStyleClass().add("session-feel-pill--active");
            }
        } else {
            btn.getStyleClass().remove("session-feel-pill--active");
        }
    }
}
