package sk.upjs.paz.dao;

import sk.upjs.paz.entity.*;
import sk.upjs.paz.enums.*;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    void add(Book book);
    void delete(long id);
    void update(Book book);

    Optional<Book> getById(long id);
    List<Book> getAll();
    List<Book> getByAuthor(Author author);
    List<Book> getByGenre(Genre genre);
    Optional<Book> getByTitle(String title);
    List<Book> searchByTitle(String title);
    List<Book> getByYear(int year);

}
