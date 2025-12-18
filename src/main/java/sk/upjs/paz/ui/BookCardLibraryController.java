package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;
import sk.upjs.paz.enums.BookState;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BookCardLibraryController {

    @FXML private VBox cardRoot;

    @FXML private Label authorLabel;
    @FXML private StackPane coverLabel;
    @FXML private Label genreLabel;
    @FXML private Label lastTimeRead;
    @FXML private Label pages;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Label titleLabel;

    private long bookId = 0L;

    private Book previewBook;
    private BookState previewState;

    @FXML
    private void initialize() {
        if (cardRoot != null) {
            cardRoot.setCursor(Cursor.HAND);
            cardRoot.setOnMouseClicked(e -> {
                if (bookId > 0 && cardRoot.getScene() != null) {
                    SceneNavigator.showBookDetailsModal(cardRoot.getScene().getWindow(), bookId);
                }
            });
        }
    }

    public void setData(Book book, BookState state) {
        this.previewBook = book;
        this.previewState = state;

        this.bookId = (book != null && book.getId() != null) ? book.getId() : 0L;

        if (titleLabel != null) {
            titleLabel.setText(safe(book != null ? book.getTitle() : null, "(No title)"));
        }

        if (statusLabel != null) {
            statusLabel.setText(state == null ? BookState.NOT_STARTED.name() : state.name());
        }

        if (genreLabel != null) {
            genreLabel.setText(joinOrUnknown(
                    book != null ? book.getGenre() : null,
                    Genre::name,
                    "Genre unknown"
            ));
        }

        if (authorLabel != null) {
            authorLabel.setText(joinOrUnknown(
                    book != null ? book.getAuthors() : null,
                    Author::getName,
                    "Author unknown"
            ));
        }

        if (pages != null) pages.setText("");
        if (lastTimeRead != null) lastTimeRead.setText("");
        if (progressBar != null) progressBar.setProgress(0.0);

        setCover(book != null ? book.getCoverPath() : null);
    }

    private void setCover(String coverPath) {
        coverLabel.getChildren().clear();

        Label placeholder = new Label("Cover");
        placeholder.getStyleClass().add("book-cover-placeholder-text");

        if (coverPath == null || coverPath.isBlank()) {
            coverLabel.getChildren().add(placeholder);
            applyRoundedClipToContainer();
            return;
        }

        String normalized = coverPath.trim();

        if (normalized.startsWith("file:") || normalized.startsWith("http://") || normalized.startsWith("https://")) {
            coverLabel.getChildren().add(createCoverImageView(normalized));
            applyRoundedClipToContainer();
            return;
        }

        String classpathPath = normalized.startsWith("/") ? normalized : "/" + normalized;
        URL resUrl = getClass().getResource(classpathPath);
        if (resUrl != null) {
            coverLabel.getChildren().add(createCoverImageView(resUrl.toExternalForm()));
            applyRoundedClipToContainer();
            return;
        }

        try {
            Path p = Paths.get(normalized);
            if (Files.exists(p)) {
                coverLabel.getChildren().add(createCoverImageView(p.toUri().toString()));
                applyRoundedClipToContainer();
                return;
            }
        } catch (Exception ignored) {
        }

        coverLabel.getChildren().add(placeholder);
        applyRoundedClipToContainer();
    }

    private ImageView createCoverImageView(String imageUrl) {
        Image image = new Image(imageUrl, true);
        ImageView iv = new ImageView(image);
        iv.setSmooth(true);
        iv.setPreserveRatio(true);
        iv.setManaged(false);

        Runnable relayout = () -> layoutAsCover(iv, image);

        coverLabel.layoutBoundsProperty().addListener((obs, o, n) -> relayout.run());
        image.widthProperty().addListener((obs, o, n) -> relayout.run());
        image.heightProperty().addListener((obs, o, n) -> relayout.run());

        relayout.run();
        return iv;
    }

    private void layoutAsCover(ImageView iv, Image image) {
        double cw = coverLabel.getWidth();
        double ch = coverLabel.getHeight();
        double iw = image.getWidth();
        double ih = image.getHeight();

        if (cw <= 0 || ch <= 0 || iw <= 0 || ih <= 0) return;

        double scale = Math.max(cw / iw, ch / ih);

        double fw = iw * scale;
        double fh = ih * scale;

        iv.setFitWidth(fw);
        iv.setFitHeight(fh);

        iv.setLayoutX((cw - fw) / 2.0);
        iv.setLayoutY((ch - fh) / 2.0);
    }

    private void applyRoundedClipToContainer() {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(28);
        clip.setArcHeight(28);

        coverLabel.setClip(clip);

        coverLabel.layoutBoundsProperty().addListener((obs, o, n) -> {
            clip.setWidth(n.getWidth());
            clip.setHeight(n.getHeight());
        });

        clip.setWidth(coverLabel.getLayoutBounds().getWidth());
        clip.setHeight(coverLabel.getLayoutBounds().getHeight());
    }

    private static <T> String joinOrUnknown(List<T> list, Function<T, String> mapper, String unknownText) {
        if (list == null || list.isEmpty()) return unknownText;
        return list.stream().map(mapper).collect(Collectors.joining(", "));
    }

    private static String safe(String s, String fallback) {
        return (s == null || s.trim().isEmpty()) ? fallback : s.trim();
    }
}
