package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import sk.upjs.paz.entity.ReadingSession;

public class CardBookLibraryController {

    @FXML
    private Label authorLabel;

    @FXML
    private StackPane coverLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private Label lastTimeRead;

    @FXML
    private Label pages;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label ratingLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label titleLabel;

    public void setData(ReadingSession readingSession){
        titleLabel.setText(readingSession.getBook().getTitle());

    }

}
