package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Author;

import java.util.List;

public interface AuthorDao {
    void add(Author author);
    void delete(long id);
    void update(Author auhtor);
    Author getById(long id);
    List<Author> getAll();
    List<Author> getByName();
    List<Author> getByCountry();

}
