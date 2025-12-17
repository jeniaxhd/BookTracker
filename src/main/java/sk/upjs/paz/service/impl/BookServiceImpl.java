package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.AuthorDao;
import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.dao.BookFilter;
import sk.upjs.paz.dao.GenreDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;
import sk.upjs.paz.service.BookService;

import java.util.*;

public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookServiceImpl(BookDao bookDao, AuthorDao authorDao, GenreDao genreDao) {
        this.bookDao = Objects.requireNonNull(bookDao, "bookDao must not be null");
        this.authorDao = Objects.requireNonNull(authorDao, "authorDao must not be null");
        this.genreDao = Objects.requireNonNull(genreDao, "genreDao must not be null");
    }

    public BookServiceImpl(BookDao bookDao) {
        this.bookDao = Objects.requireNonNull(bookDao, "bookDao must not be null");
        this.authorDao = null;
        this.genreDao = null;
    }

    @Override
    public void add(Book book) {
        Objects.requireNonNull(book, "book must not be null");

        if (authorDao != null) {
            resolveAuthors(book);
        }

        if (genreDao != null) {
            resolveGenres(book);
        }

        bookDao.add(book);
    }

    private void resolveAuthors(Book book) {
        List<Author> authors = book.getAuthors();
        if (authors == null || authors.isEmpty()) return;

        List<Author> resolved = new ArrayList<>();

        for (Author a : authors) {
            if (a == null) continue;

            if (a.getId() != null) {
                resolved.add(a);
                continue;
            }

            String name = a.getName() == null ? "" : a.getName().trim();
            if (name.isEmpty()) continue;

            Optional<Author> existing = authorDao.getByName(name).stream()
                    .filter(x -> x.getName() != null && x.getName().equalsIgnoreCase(name))
                    .findFirst();

            if (existing.isPresent()) {
                resolved.add(existing.get());
            } else {
                Author created = new Author();
                created.setName(name);
                authorDao.add(created);
                resolved.add(created);
            }
        }

        book.setAuthors(resolved);
    }

    private void resolveGenres(Book book) {
        List<Genre> genres = book.getGenre();
        if (genres == null || genres.isEmpty()) return;

        List<Genre> resolved = new ArrayList<>();

        for (Genre g : genres) {
            if (g == null) continue;

            if (g.id() != null) {
                resolved.add(g);
                continue;
            }

            String name = g.name() == null ? "" : g.name().trim();
            if (name.isEmpty()) continue;

            Optional<Genre> existing = genreDao.getByName(name).stream()
                    .filter(x -> x.name() != null && x.name().equalsIgnoreCase(name))
                    .findFirst();

            if (existing.isPresent()) {
                resolved.add(existing.get());
            } else {
                genreDao.add(new Genre(null, name));

                Optional<Genre> created = genreDao.getByName(name).stream()
                        .filter(x -> x.name() != null && x.name().equalsIgnoreCase(name))
                        .findFirst();

                if (created.isEmpty()) {
                    throw new IllegalStateException("Genre was not created: " + name);
                }

                resolved.add(created.get());
            }
        }

        book.setGenre(resolved);
    }

    @Override
    public void delete(Long id) {
        bookDao.delete(id);
    }

    @Override
    public void update(Book book) {
        bookDao.update(book);
    }

    @Override
    public List<Book> getByAuthor(Author author) {
        return bookDao.getByAuthor(author);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return bookDao.getById(id);
    }

    @Override
    public List<Book> getByTitle(String title) {
        return bookDao.getByTitle(title);
    }

    @Override
    public List<Book> getByGenre(Genre genre) {
        return bookDao.getByGenre(genre);
    }

    @Override
    public List<Book> getByAnyGenre(List<Genre> genre) {
        if (genre == null || genre.isEmpty()) {
            return bookDao.getAll();
        }

        Set<Book> result = new HashSet<>();
        for (Genre g : genre) {
            result.addAll(bookDao.getByGenre(g));
        }
        return new ArrayList<>(result);
    }

    @Override
    public List<Book> getByYear(Integer year) {
        return bookDao.getByYear(year);
    }

    @Override
    public List<Book> getRandom(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return bookDao.getRandom(limit);
    }

    @Override
    public List<Book> search(String text) {
        if (text == null || text.isBlank()) {
            return bookDao.getAll();
        }

        String query = text.trim().toLowerCase();

        return bookDao.getAll().stream()
                .filter(b -> {
                    if (b.getTitle() != null && b.getTitle().toLowerCase().contains(query)) {
                        return true;
                    }

                    if (b.getAuthors() != null && b.getAuthors().stream()
                            .anyMatch(a -> a.getName() != null && a.getName().toLowerCase().contains(query))) {
                        return true;
                    }

                    if (b.getGenre() != null && b.getGenre().stream()
                            .anyMatch(g -> g.name() != null && g.name().toLowerCase().contains(query))) {
                        return true;
                    }

                    return false;
                })
                .toList();
    }

    @Override
    public List<Book> findByFilter(BookFilter filter) {
        if (filter == null) {
            return bookDao.getAll();
        }

        List<Book> books = bookDao.findByFilter(
                filter.getFromYear(),
                filter.getToYear(),
                filter.getFromPagesCount(),
                filter.getToPagesCount(),
                filter.getFromAverageRating(),
                filter.getToAverageRating()
        );

        return books.stream()
                .filter(b -> filter.getTitlePart() == null
                        || (b.getTitle() != null
                        && b.getTitle().toLowerCase().contains(filter.getTitlePart().toLowerCase())))
                .filter(b -> filter.getAuthorId() == null
                        || (b.getAuthors() != null
                        && b.getAuthors().stream().anyMatch(a -> a.getId().equals(filter.getAuthorId()))))
                .filter(b -> filter.getGenres() == null
                        || filter.getGenres().isEmpty()
                        || (b.getGenre() != null && b.getGenre().stream().anyMatch(filter.getGenres()::contains)))
                .sorted((b1, b2) -> {
                    if (filter.getSortBy() == null) return 0;

                    int cmp = switch (filter.getSortBy()) {
                        case "title" ->
                                Optional.ofNullable(b1.getTitle()).orElse("")
                                        .compareToIgnoreCase(Optional.ofNullable(b2.getTitle()).orElse(""));
                        case "year" ->
                                Integer.compare(
                                        b1.getYear() == null ? 0 : b1.getYear(),
                                        b2.getYear() == null ? 0 : b2.getYear()
                                );
                        case "pages" -> Integer.compare(b1.getPages(), b2.getPages());
                        case "rating" -> Double.compare(b1.getAverageRating(), b2.getAverageRating());
                        default -> 0;
                    };

                    return filter.isSortAsc() ? cmp : -cmp;
                })
                .toList();
    }
}
