package sk.upjs.paz.dao;

import sk.upjs.paz.entity.*;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    void add(Book book);
    void delete(Long id);
    void update(Book book);

    Optional<Book> getById(Long id);
    List<Book> getAll();
    List<Book> getByAuthor(Author author);
    List<Book> getByGenres(List<Genre> genre);

    List<Book> getByGenre(Genre genre);

    List<Book> getByTitle(String title);
    List<Book> getByYear(Integer year);

    List<Book> getRandom(int limit);

    List<Book> findByFilter(
            Integer yearFrom,
            Integer yearTo,
            Integer pagesFrom,
            Integer pagesTo,
            Double ratingFrom,
            Double ratingTo
    );

}
