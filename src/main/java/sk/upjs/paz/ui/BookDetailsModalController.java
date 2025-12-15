package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class BookDetailsModalController {

    @FXML private BorderPane root;

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;

    @FXML private Label yearLabel;
    @FXML private Label languageLabel;
    @FXML private Label pagesLabel;

    @FXML private Label genreLabel;
    @FXML private Label statusLabel;

    @FXML private Label descriptionLabel;

    @FXML private Label authorInitialsLabel;
    @FXML private Label authorNameLabel;
    @FXML private Label authorCountryLabel;
    @FXML private Label authorBioLabel;

    @FXML private Button editBookButton;
    @FXML private Button startSessionHeaderButton;
    @FXML private Button startSessionFooterButton;
    @FXML private Button closeIconButton;
    @FXML private Button closeButton;

    @FXML
    private void initialize() {
        // nothing required
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
        if (titleLabel != null) titleLabel.setText(title);
        if (subtitleLabel != null) subtitleLabel.setText(subtitle);

        if (yearLabel != null) yearLabel.setText(year);
        if (languageLabel != null) languageLabel.setText(language);
        if (pagesLabel != null) pagesLabel.setText(pages);

        if (genreLabel != null) genreLabel.setText(genre);
        if (statusLabel != null) statusLabel.setText(status);

        if (descriptionLabel != null) descriptionLabel.setText(description);

        if (authorInitialsLabel != null) authorInitialsLabel.setText(authorInitials);
        if (authorNameLabel != null) authorNameLabel.setText(authorName);
        if (authorCountryLabel != null) authorCountryLabel.setText(authorCountry);
        if (authorBioLabel != null) authorBioLabel.setText(authorBio);
    }

    @FXML
    private void handleClose() {
        Window w = (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;
        if (w != null) w.hide();
    }

    @FXML
    private void handleStartSession() {
        openReadingSessionBar();
    }

    private void openReadingSessionBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/upjs/paz/ui/sessionBar.fxml"));
            Parent dialogRoot = loader.load();

            Scene dialogScene = new Scene(dialogRoot);
            ThemeManager.apply(dialogScene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Reading session");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(root.getScene().getWindow());
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Cannot open sessionBar.fxml", e);
        }
    }
}
