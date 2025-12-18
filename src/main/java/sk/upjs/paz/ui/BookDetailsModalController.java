package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import sk.upjs.paz.entity.*;
import sk.upjs.paz.enums.BookState;
import sk.upjs.paz.service.BookService;
import sk.upjs.paz.service.ReadingSessionService;
import sk.upjs.paz.service.ReviewService;
import sk.upjs.paz.service.UserHasBookService;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookDetailsModalController {

    @FXML private BorderPane root;

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;

    @FXML private Label bookTitleBigLabel;
    @FXML private Label bookAuthorBigLabel;

    @FXML private ImageView coverImageView;
    @FXML private Label coverPlaceholderLabel;

    @FXML private Label yearLabel;
    @FXML private Label pagesLabel;

    @FXML private Label genreLabel;
    @FXML private Label statusLabel;

    @FXML private Label descriptionLabel;

    @FXML private TextArea reviewArea;
    @FXML private Button saveReviewButton;

    @FXML private Button closeButton;
    @FXML private Button startSessionFooterButton;

    private BookService bookService;
    private UserHasBookService userHasBookService;
    private ReadingSessionService readingSessionService;
    private ReviewService reviewService;

    private long userId;
    private long bookId;

    private Book loadedBook;
    private BookState loadedState;

    public void setServices(BookService bookService,
                            UserHasBookService userHasBookService,
                            ReadingSessionService readingSessionService,
                            ReviewService reviewService) {
        this.bookService = bookService;
        this.userHasBookService = userHasBookService;
        this.readingSessionService = readingSessionService;
        this.reviewService = reviewService;
    }

    public void setContext(long userId, long bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public void load() {
        if (bookService == null || userHasBookService == null || readingSessionService == null || reviewService == null) {
            throw new IllegalStateException("Services are not set");
        }

        loadedBook = bookService.getById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found: id=" + bookId));

        loadedState = userHasBookService.getState(userId, bookId).orElse(BookState.NOT_STARTED);
        setText(genreLabel, formatGenres(loadedBook.getGenre(), "—"));


        // Fill book UI first
        fillBookUI();


        safeLoadReview();
    }

    private void fillBookUI() {
        String title = safe(loadedBook.getTitle(), "(No title)");
        setText(titleLabel, title);
        setText(subtitleLabel, "Book details and metadata");
        setText(bookTitleBigLabel, title);

        setText(bookAuthorBigLabel, joinAuthors(loadedBook.getAuthors(), "Unknown author"));
        setText(genreLabel, joinGenres(loadedBook.getGenre(), "—"));

        Integer year = loadedBook.getYear();
        setText(yearLabel, year != null ? String.valueOf(year) : "—");

        int pages = loadedBook.getPages();
        setText(pagesLabel, pages > 0 ? String.valueOf(pages) : "—");

        setText(descriptionLabel, safe(loadedBook.getDescription(), "No description yet."));

        applyStatePill(loadedState);
        applyCoverLikeLibrary(loadedBook.getCoverPath());
    }

    private void safeLoadReview() {
        try {
            Optional<Review> existing = reviewService.getByUserAndBook(userId, bookId);
            if (reviewArea != null) {
                reviewArea.setText(existing.map(Review::getComment).orElse(""));
            }
            if (saveReviewButton != null) {
                saveReviewButton.setDisable(false);
            }
        } catch (Exception e) {
            if (reviewArea != null) {
                reviewArea.setText("");
            }
            if (saveReviewButton != null) {
                saveReviewButton.setDisable(false);
            }
        }
    }

    @FXML
    private void handleSaveReview() {
        String comment = reviewArea == null ? "" : (reviewArea.getText() == null ? "" : reviewArea.getText().trim());

        Optional<Review> existing = reviewService.getByUserAndBook(userId, bookId);

        if (existing.isPresent()) {
            Review r = existing.get();
            r.setComment(comment);
            reviewService.update(r);
        } else {
            Review r = new Review();

            Book b = new Book();
            b.setId(bookId);

            User u = new User();
            u.setId(userId);

            r.setBook(b);
            r.setUser(u);
            r.setComment(comment);
            r.setCreatedAt(LocalDateTime.now());

            reviewService.add(r);
        }

        if (saveReviewButton != null) saveReviewButton.setDisable(true);
    }

    @FXML
    private void handleStartSession() {
        ReadingSession session = readingSessionService.startNewSession(userId, bookId, 0);
        long sessionId = session.getId();

        loadedState = BookState.READING;
        userHasBookService.upsert(userId, bookId, loadedState);
        applyStatePill(loadedState);

        String title = (titleLabel != null) ? titleLabel.getText() : "Current book";
        String subtitle = buildSubtitle();

        SceneNavigator.startSessionBar(userId, bookId, sessionId, title, subtitle, loadedState.name());
        handleClose();
    }

    @FXML
    private void handleClose() {
        Window w = (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;
        if (w != null) w.hide();
    }

    private void applyCoverLikeLibrary(String coverPath) {
        if (coverPlaceholderLabel != null) {
            coverPlaceholderLabel.setVisible(true);
            coverPlaceholderLabel.setManaged(true);
        }
        if (coverImageView == null) return;

        if (coverPath == null || coverPath.isBlank()) {
            coverImageView.setImage(null);
            return;
        }

        String normalized = coverPath.trim();

        try {
            // URL
            if (normalized.startsWith("file:") || normalized.startsWith("http://") || normalized.startsWith("https://")) {
                coverImageView.setImage(new Image(normalized, true));
                hideCoverPlaceholder();
                return;
            }

            // Classpath resource
            String classpathPath = normalized.startsWith("/") ? normalized : "/" + normalized;
            URL resUrl = getClass().getResource(classpathPath);
            if (resUrl != null) {
                coverImageView.setImage(new Image(resUrl.toExternalForm(), true));
                hideCoverPlaceholder();
                return;
            }

            // File
            Path p = Paths.get(normalized);
            if (Files.exists(p)) {
                coverImageView.setImage(new Image(p.toUri().toString(), true));
                hideCoverPlaceholder();
                return;
            }

            // Fallback: try as-is
            coverImageView.setImage(new Image(normalized, true));
            hideCoverPlaceholder();

        } catch (Exception ignored) {
            coverImageView.setImage(null);
        }
    }

    private void hideCoverPlaceholder() {
        if (coverPlaceholderLabel != null) {
            coverPlaceholderLabel.setVisible(false);
            coverPlaceholderLabel.setManaged(false);
        }
    }

    private void applyStatePill(BookState state) {
        if (statusLabel == null) return;

        String text = (state != null) ? state.name() : "NOT_STARTED";
        statusLabel.setText(text);

        statusLabel.getStyleClass().removeIf(s -> s.startsWith("status-pill--"));

        if (state == BookState.READING) statusLabel.getStyleClass().add("status-pill--in-progress");
        else if (state == BookState.FINISHED) statusLabel.getStyleClass().add("status-pill--finished");
        else if (state == BookState.WANT_TO_READ) statusLabel.getStyleClass().add("status-pill--planned");
        else if (state == BookState.ABANDONED) statusLabel.getStyleClass().add("status-pill--abandoned");
        else statusLabel.getStyleClass().add("status-pill--not-started");
    }

    private String buildSubtitle() {
        String author = (bookAuthorBigLabel != null) ? bookAuthorBigLabel.getText() : "";
        String genre = (genreLabel != null) ? genreLabel.getText() : "";

        if (isBlank(author) && isBlank(genre)) return "";
        if (isBlank(author)) return genre;
        if (isBlank(genre)) return author;
        return author + " · " + genre;
    }

    private String joinAuthors(List<Author> authors, String fallback) {
        if (authors == null || authors.isEmpty()) return fallback;
        String s = authors.stream()
                .filter(a -> a != null && !isBlank(a.getName()))
                .map(Author::getName)
                .collect(Collectors.joining(", "));
        return isBlank(s) ? fallback : s;
    }

    private String joinGenres(List<?> genres, String fallback) {
        if (genres == null || genres.isEmpty()) return fallback;
        String s = genres.stream()
                .filter(g -> g != null)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return isBlank(s) ? fallback : s;
    }

    private void setText(Label label, String text) {
        if (label != null) label.setText(text);
    }

    private String safe(String s, String fallback) {
        return isBlank(s) ? fallback : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    private String formatGenres(List<Genre> genres, String fallback) {
        if (genres == null || genres.isEmpty()) return fallback;

        return genres.stream()
                .filter(g -> g != null && g.name() != null && !g.name().isBlank())
                .map(Genre::name)
                .distinct()
                .collect(Collectors.joining(", "));
    }

}
