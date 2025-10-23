package sk.upjs.paz.dao;

import sk.upjs.paz.entity.*;
import sk.upjs.paz.enums.*;

import java.util.List;

public interface BookDao {
    void add(Book book);
    void delete(Book book);
    void update(Book book);

    Book getById(long id);
    List<Book> getAll();
    List<Book> getByAuthor(Author author);
    List<Book> getByGenre(Genre genre);
    List<Book> getByName(String name);
    List<Book> getByYear(int year);
}
