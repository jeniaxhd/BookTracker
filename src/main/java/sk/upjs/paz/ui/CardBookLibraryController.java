package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import sk.upjs.paz.entity.*;

import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private Label statusLabel;

    @FXML
    private Label titleLabel;

    public void setData(ReadingSession readingSession){
        titleLabel.setText(readingSession.getBook().getTitle());
        statusLabel.setText(readingSession.getState().toString());
        pages.setText(String.valueOf(readingSession.getEndPage()));
        lastTimeRead.setText(String.valueOf(readingSession.getLastTimeRead()));
        genreLabel.setText(joinOrUnknown(
                readingSession.getBook().getGenre(),
                Genre::name,
                "Genre unknown"
        ));
        setCover(readingSession.getBook().getCoverPath());
        authorLabel.setText(joinOrUnknown(
                readingSession.getBook().getAuthors(),
                Author::getName,
                "Author unknown"
        ));
    }
    private void setCover(String coverPath) {
        coverLabel.getChildren().clear();

        if (coverPath == null || coverPath.isBlank()) {
            return;
        }

        URL url = getClass().getResource(coverPath);
        if (url == null) {
            return;
        }

        ImageView imageView = new ImageView(new Image(url.toExternalForm(), true));
        imageView.setFitWidth(120);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        coverLabel.getChildren().add(imageView);
    }

    private static <T> String joinOrUnknown(
            List<T> list,
            Function<T, String> mapper,
            String unknownText
    ) {
        if (list == null || list.isEmpty()) {
            return unknownText;
        }
        return list.stream()
                .map(mapper)
                .collect(Collectors.joining(", "));
    }
}
