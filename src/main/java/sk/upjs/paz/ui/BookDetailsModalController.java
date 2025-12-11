package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class BookDetailsModalController {

    @FXML
    private BorderPane root;

    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;

    @FXML
    private Label yearLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label pagesLabel;

    @FXML
    private Label genreLabel;
    @FXML
    private Label statusLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label authorInitialsLabel;
    @FXML
    private Label authorNameLabel;
    @FXML
    private Label authorCountryLabel;
    @FXML
    private Label authorBioLabel;

    @FXML
    private Button editBookButton;
    @FXML
    private Button startSessionHeaderButton;
    @FXML
    private Button startSessionFooterButton;
    @FXML
    private Button closeIconButton;
    @FXML
    private Button closeButton;

    @FXML
    private void initialize() {
        // Optional: you can set default text here or leave FXML defaults
    }

    public void setBookDetails(
            String title,
            String subtitle,
            String year,
            String language,
            String pages,
            String genre,
            String status,
            String description,
            String authorInitials,
            String authorName,
            String authorCountry,
            String authorBio
    ) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle);
        yearLabel.setText(year);
        languageLabel.setText(language);
        pagesLabel.setText(pages);
        genreLabel.setText(genre);
        statusLabel.setText(status);
        descriptionLabel.setText(description);
        authorInitialsLabel.setText(authorInitials);
        authorNameLabel.setText(authorName);
        authorCountryLabel.setText(authorCountry);
        authorBioLabel.setText(authorBio);
    }

    @FXML
    private void handleClose() {
        Window w = root.getScene() != null ? root.getScene().getWindow() : null;
        if (w != null) {
            w.hide();
        }
    }

    @FXML
    private void handleStartSession() {
        // TODO: open "start reading session" modal / screen
        // For now you can just close this dialog or keep it open.
    }
}
