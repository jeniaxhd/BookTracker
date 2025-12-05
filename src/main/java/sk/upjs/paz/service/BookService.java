package sk.upjs.paz.service;

import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;
import sk.upjs.paz.dao.BookFilter;


import java.util.List;
import java.util.Optional;

public interface BookService {
    void add(Book book);

    void delete(Long id);

    void update(Book book);

    List<Book> getByAuthor(Author author);

    Optional<Book> getById(Long id);

    List<Book> getByTitle(String title);

    List<Book> getByGenre(Genre genre);

    List<Book> getByAnyGenre(List<Genre> genre);

    List<Book> getByYear(Integer year);

    List<Book> getRandom(int limit);

    List<Book> search(String text);

    List<Book> findByFilter(BookFilter filter);

}
