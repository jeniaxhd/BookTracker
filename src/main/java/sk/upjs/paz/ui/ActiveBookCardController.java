package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import sk.upjs.paz.ui.dto.ActiveBookCard;

import java.util.function.Consumer;

public class ActiveBookCardController {

    @FXML private StackPane coverPane;

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label categoryLabel;

    @FXML private ProgressBar progressBar;
    @FXML private Label pagesLabel;
    @FXML private Label timeLabel;

    @FXML private Label statusPill;

    @FXML private Button continueButton;
    @FXML private Hyperlink detailsLink;

    private ActiveBookCard data;

    private Consumer<ActiveBookCard> onContinue;
    private Consumer<ActiveBookCard> onDetails;

    public void setData(ActiveBookCard data) {
        this.data = data;

        titleLabel.setText(data.title());
        authorLabel.setText(data.authorsText());
        categoryLabel.setText(data.genreName());

        statusPill.setText("In Progress");

        int total = Math.max(1, data.totalPages());
        int current = Math.max(0, Math.min(data.currentPage(), total));

        progressBar.setProgress((double) current / total);
        pagesLabel.setText(current + " / " + total + " pages");
        timeLabel.setText(formatMinutes(data.totalMinutes()));
    }

    public void setOnContinue(Consumer<ActiveBookCard> onContinue) {
        this.onContinue = onContinue;
    }

    public void setOnDetails(Consumer<ActiveBookCard> onDetails) {
        this.onDetails = onDetails;
    }

    private String formatMinutes(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        if (h <= 0) return m + "m";
        return h + "h " + m + "m";
    }

    @FXML
    private void onContinue() {
        if (onContinue != null && data != null) onContinue.accept(data);
    }

    @FXML
    private void onViewDetails() {
        if (onDetails != null && data != null) onDetails.accept(data);
    }
}
