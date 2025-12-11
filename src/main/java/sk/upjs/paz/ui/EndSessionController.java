package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class EndSessionController {

    @FXML
    private BorderPane root;

    // Book summary
    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label bookMetaLabel;
    @FXML
    private Label statusPill;
    @FXML
    private Label currentPageLabel;

    // Time
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField durationField;

    // Notes
    @FXML
    private TextArea notesArea;

    // Pages
    @FXML
    private TextField startPageField;
    @FXML
    private TextField endPageField;
    @FXML
    private Label pagesReadLabel;

    // Feeling toggles
    @FXML
    private ToggleButton deepFocusToggle;
    @FXML
    private ToggleButton lightReadingToggle;
    @FXML
    private ToggleButton studyToggle;
    @FXML
    private ToggleButton relaxToggle;

    // Checkboxes
    @FXML
    private CheckBox countToGoalCheck;
    @FXML
    private CheckBox markFinishedCheck;

    // Result flags (for caller)
    private boolean sessionSaved = false;
    private boolean discarded = false;

    @FXML
    private void initialize() {
        // Make feel toggles behave like a "single choice" group
        ToggleGroup feelGroup = new ToggleGroup();
        deepFocusToggle.setToggleGroup(feelGroup);
        lightReadingToggle.setToggleGroup(feelGroup);
        studyToggle.setToggleGroup(feelGroup);
        relaxToggle.setToggleGroup(feelGroup);

        // Default selection
        deepFocusToggle.setSelected(true);

        // Update CSS class for active pill when selection changes
        feelGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateFeelPillStyles();
        });
        updateFeelPillStyles();

        // Live calculation of pages read when start/end change
        startPageField.textProperty().addListener((obs, oldVal, newVal) -> updatePagesRead());
        endPageField.textProperty().addListener((obs, oldVal, newVal) -> updatePagesRead());

        // Initial compute
        updatePagesRead();
    }

    // ====== Public API for caller ======

    public boolean isSessionSaved() {
        return sessionSaved;
    }

    public boolean isDiscarded() {
        return discarded;
    }

    /**
     * Optional helper – you can call it from the code that opens this dialog
     * to pre-fill fields based on active reading session.
     */
    public void setInitialData(String bookTitle,
                               String bookMeta,
                               String statusText,
                               String currentPageText,
                               String startTime,
                               String durationMinutes,
                               String startPage,
                               String endPage) {
        if (bookTitle != null) {
            bookTitleLabel.setText(bookTitle);
        }
        if (bookMeta != null) {
            bookMetaLabel.setText(bookMeta);
        }
        if (statusText != null) {
            statusPill.setText(statusText);
        }
        if (currentPageText != null) {
            currentPageLabel.setText(currentPageText);
        }
        if (startTime != null) {
            startTimeField.setText(startTime);
        }
        if (durationMinutes != null) {
            durationField.setText(durationMinutes);
        }
        if (startPage != null) {
            startPageField.setText(startPage);
        }
        if (endPage != null) {
            endPageField.setText(endPage);
        }

        updatePagesRead();
    }

    // ====== Buttons handlers ======

    @FXML
    private void handleSave() {
        // You could add simple validation here if needed
        sessionSaved = true;
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        // Just close, do not mark as saved or discarded
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
        // Same behavior as cancel icon
        closeWindow();
    }

    // ====== Internal helpers ======

    private void closeWindow() {
        Window w = root.getScene() != null ? root.getScene().getWindow() : null;
        if (w != null) {
            w.hide();
        }
    }

    private void updatePagesRead() {
        int start = parseIntSafe(startPageField.getText());
        int end = parseIntSafe(endPageField.getText());
        int pagesRead = 0;
        if (start > 0 && end >= start) {
            pagesRead = end - start;
        }
        pagesReadLabel.setText(String.valueOf(pagesRead));
    }

    private int parseIntSafe(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateFeelPillStyles() {
        // We use CSS class "session-feel-pill--active" for selected pill
        updateSinglePillStyle(deepFocusToggle);
        updateSinglePillStyle(lightReadingToggle);
        updateSinglePillStyle(studyToggle);
        updateSinglePillStyle(relaxToggle);
    }

    private void updateSinglePillStyle(ToggleButton btn) {
        if (btn.isSelected()) {
            if (!btn.getStyleClass().contains("session-feel-pill--active")) {
                btn.getStyleClass().add("session-feel-pill--active");
            }
        } else {
            btn.getStyleClass().remove("session-feel-pill--active");
        }
    }
}
