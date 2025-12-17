package sk.upjs.paz.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;
import sk.upjs.paz.enums.BookState;
import sk.upjs.paz.service.BookService;
import sk.upjs.paz.service.GenreService;
import sk.upjs.paz.service.UserHasBookService;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddBookModalController {

    @FXML private BorderPane root;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField pagesField;
    @FXML private TextField languageField;
    @FXML private TextField tagsField;
    @FXML private TextField yearField;
    @FXML private TextField categoryField;

    @FXML private FlowPane tagsPane;
    @FXML private TextArea notesArea;

    @FXML private ImageView coverPreview;
    @FXML private Label coverPlaceholderLabel;
    @FXML private Button uploadCoverButton;

    private final Set<String> tags = new LinkedHashSet<>();
    private File selectedCoverFile;

    private GenreService genreService;
    private BookService bookService;

    private final ContextMenu genreMenu = new ContextMenu();
    private List<Genre> allGenres = List.of();
    private Genre selectedGenre;

    private UserHasBookService userHasBookService;
    private long currentUserId;

    public void setUserHasBookService(UserHasBookService userHasBookService) {
        this.userHasBookService = userHasBookService;
    }

    public void setCurrentUserId(long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setGenreService(GenreService genreService) {
        this.genreService = genreService;
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    public void initData() {
        if (genreService == null) {
            throw new IllegalStateException("GenreService is not set");
        }

        allGenres = genreService.getAll();
        setupGenreAutocomplete();
    }

    @FXML
    private void initialize() {
        pagesField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null
        ));

        yearField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d{0,4}") ? change : null
        ));

        tagsField.setOnAction(e -> addTagFromField());
        tagsField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.BACK_SPACE && tagsField.getText().isEmpty()) {
                removeLastTag();
            }
        });

        coverPreview.setImage(null);
        coverPlaceholderLabel.setVisible(true);
        coverPlaceholderLabel.setManaged(true);
    }

    private void setupGenreAutocomplete() {
        genreMenu.setAutoHide(true);

        categoryField.textProperty().addListener((obs, oldV, newV) -> {
            selectedGenre = null;

            String text = newV == null ? "" : newV.trim();
            if (text.isEmpty()) {
                genreMenu.hide();
                return;
            }

            String q = text.toLowerCase();

            List<Genre> matches = allGenres.stream()
                    .filter(g -> g != null && g.name() != null)
                    .filter(g -> g.name().toLowerCase().contains(q))
                    .limit(8)
                    .toList();

            if (matches.isEmpty()) {
                genreMenu.hide();
                return;
            }

            genreMenu.getItems().clear();

            for (Genre g : matches) {
                MenuItem item = new MenuItem(g.name());
                item.setOnAction(e -> selectGenre(g));
                genreMenu.getItems().add(item);
            }

            if (!genreMenu.isShowing()) {
                genreMenu.show(categoryField, Side.BOTTOM, 0, 0);
            }
        });

        categoryField.focusedProperty().addListener((obs, was, is) -> {
            if (!is) genreMenu.hide();
        });

        categoryField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                genreMenu.hide();
            } else if (e.getCode() == KeyCode.ENTER) {
                applyFirstSuggestionIfAny();
            } else if (e.getCode() == KeyCode.DOWN) {
                if (!genreMenu.isShowing()) {
                    genreMenu.show(categoryField, Side.BOTTOM, 0, 0);
                }
            }
        });
    }

    private void selectGenre(Genre g) {
        selectedGenre = g;
        categoryField.setText(g.name());
        categoryField.positionCaret(g.name().length());
        genreMenu.hide();
    }

    private void applyFirstSuggestionIfAny() {
        if (genreMenu.getItems().isEmpty()) return;

        MenuItem first = genreMenu.getItems().get(0);
        String name = first.getText();
        if (name == null || name.isBlank()) return;

        Genre exact = allGenres.stream()
                .filter(g -> g != null && g.name() != null)
                .filter(g -> g.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (exact != null) {
            selectGenre(exact);
        } else {
            genreMenu.hide();
        }
    }

    @FXML
    private void onUploadCover() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose cover image");
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(root.getScene().getWindow());
        if (file == null) return;

        Image img = new Image(file.toURI().toString(), false);
        if (img.isError()) {
            showError("Image can't be loaded.");
            return;
        }

        selectedCoverFile = file;
        coverPreview.setImage(img);

        coverPlaceholderLabel.setVisible(false);
        coverPlaceholderLabel.setManaged(false);
        uploadCoverButton.setText("Cover selected");
    }

    @FXML
    private void onSaveBook() {
        addTagFromField();

        if (bookService == null) { showError("BookService is not set."); return; }
        if (userHasBookService == null) { showError("UserHasBookService is not set."); return; }
        if (currentUserId <= 0) { showError("Current user is not set."); return; }

        if (titleField.getText().isBlank()) { showError("Title is required."); return; }
        if (authorField.getText().isBlank()) { showError("Author is required."); return; }
        if (pagesField.getText().isBlank()) { showError("Pages are required."); return; }

        int pages = Integer.parseInt(pagesField.getText().trim());

        Book book = new Book();
        book.setTitle(titleField.getText().trim());
        book.setPages(pages);

        String notes = notesArea.getText();
        book.setDescription((notes == null || notes.isBlank()) ? null : notes.trim());

        book.setCoverPath(selectedCoverFile == null ? null : selectedCoverFile.getAbsolutePath());

        Author author = new Author();
        author.setName(authorField.getText().trim());
        book.setAuthors(List.of(author));

        Integer year = null;
        String y = yearField.getText() == null ? "" : yearField.getText().trim();
        if (!y.isEmpty()) year = Integer.parseInt(y);
        book.setYear(year);

        Genre genreToSave = resolveGenreFromInput();
        book.setGenre(genreToSave == null ? List.of() : List.of(genreToSave));

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                bookService.add(book);

                if (book.getId() == null) {
                    throw new IllegalStateException("Book ID was not generated after insert");
                }

                userHasBookService.upsert(currentUserId, book.getId(), BookState.NOT_STARTED);
                return null;
            }
        };

        task.setOnSucceeded(e -> onCancel());
        task.setOnFailed(e -> showError(task.getException() == null ? "Save failed" : task.getException().getMessage()));

        new Thread(task, "save-book").start();
    }

    private Genre resolveGenreFromInput() {
        String typed = categoryField.getText() == null ? "" : categoryField.getText().trim();
        if (typed.isEmpty()) return null;

        if (selectedGenre != null && selectedGenre.name() != null
                && selectedGenre.name().equalsIgnoreCase(typed)) {
            return selectedGenre;
        }

        Genre exact = allGenres.stream()
                .filter(g -> g != null && g.name() != null)
                .filter(g -> g.name().equalsIgnoreCase(typed))
                .findFirst()
                .orElse(null);

        if (exact != null) return exact;

        return new Genre(null, typed);
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void addTagFromField() {
        String raw = tagsField.getText().trim();
        if (raw.isEmpty()) return;

        for (String part : raw.split("[,;]")) {
            String tag = part.trim();
            if (tag.isEmpty()) continue;

            if (tags.add(tag)) {
                tagsPane.getChildren().add(createTagChip(tag));
            }
        }
        tagsField.clear();
    }

    private void removeLastTag() {
        if (tags.isEmpty()) return;
        tags.remove(tags.stream().reduce((a, b) -> b).orElse(null));

        int n = tagsPane.getChildren().size();
        if (n > 0) tagsPane.getChildren().remove(n - 1);
    }

    private HBox createTagChip(String tag) {
        HBox chip = new HBox(6);
        chip.getStyleClass().add("tag-chip");

        Text label = new Text(tag);
        Button remove = new Button("×");
        remove.setOnAction(e -> {
            tags.remove(tag);
            tagsPane.getChildren().remove(chip);
        });

        chip.getChildren().addAll(label, remove);
        return chip;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.initOwner(root.getScene().getWindow());
        alert.showAndWait();
    }
}
