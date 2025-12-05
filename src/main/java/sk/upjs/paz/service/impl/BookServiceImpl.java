package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.dao.BookFilter;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;
import sk.upjs.paz.service.BookService;

import java.util.*;

public class BookServiceImpl implements BookService {

    private final BookDao bookDao;

    public BookServiceImpl(BookDao bookDao) {
        this.bookDao = Objects.requireNonNull(bookDao, "bookDao must not be null");
    }

    @Override
    public void add(Book book) {
        bookDao.add(book);
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
        Set<Book> result = new HashSet<Book>();

        for(Genre g : genre) {
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
        return List.of();
    }

    @Override
    public List<Book> findByFilter(BookFilter filter) {
        if (filter == null) {
            return bookDao.getAll();
        }

        List<Book> books = bookDao.getAll();  // базовий список

        return books.stream()
                .filter(b -> filter.getTitlePart() == null
                        || b.getTitle().toLowerCase()
                        .contains(filter.getTitlePart().toLowerCase()))
                .filter(b -> filter.getAuthorId() == null
                    || b.getAuthors().stream()
                    .anyMatch(a -> a.getId().equals(filter.getAuthorId())))
                .filter(b -> filter.getFromYear() == null
                        || (b.getYear() != null && b.getYear() >= filter.getFromYear()))
                .filter(b -> filter.getToYear() == null
                        || (b.getYear() != null && b.getYear() <= filter.getToYear()))
                .filter(b -> filter.getGenres() == null || filter.getGenres().isEmpty()
                        || b.getGenres().stream().anyMatch(filter.getGenres()::contains))
                .filter(b -> filter.getState() == null
                        || b.getState() == filter.getState())
                .sorted((b1, b2) -> {
                    if (filter.getSortBy() == null) return 0;

                    int cmp = switch (filter.getSortBy()) {
                        case "title" ->
                                b1.getTitle().compareToIgnoreCase(b2.getTitle());
                        case "year" ->
                                Integer.compare(
                                        b1.getYear() == null ? 0 : b1.getYear(),
                                        b2.getYear() == null ? 0 : b2.getYear()
                                );
                        default -> 0;
                    };

                    return filter.isSortAsc() ? cmp : -cmp;
                })
                .toList();
    }
}
