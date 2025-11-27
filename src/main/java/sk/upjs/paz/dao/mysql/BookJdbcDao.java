package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;

import java.util.List;
import java.util.Optional;

public class BookJdbcDao implements BookDao {

    @Override
    public void add(Book book) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void update(Book book) {

    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Book> getAll() {
        return List.of();
    }

    @Override
    public List<Book> getByAuthor(Author author) {
        return List.of();
    }

    @Override
    public List<Book> getByGenre(Genre genre) {
        return List.of();
    }

    @Override
    public List<Book> getByTitle(String title) {
        return List.of();
    }

    @Override
    public List<Book> getByYear(int year) {
        return List.of();
    }

    @Override
    public List<Book> getByLanguage(String language) {
        return List.of();
    }

    @Override
    public List<Book> findByFilter(Integer yearFrom, Integer yearTo, Integer pagesFrom, Integer pagesTo, Double ratingFrom, Double ratingTo) {
        return List.of();
    }
}
