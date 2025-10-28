package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Author;
import sk.upjs.paz.enums.Country;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    void add(Author author);
    void delete(long id);
    void update(Author author);
    Optional<Author> getById(long id);
    List<Author> getAll();
    Optional<Author> getByName(String name);
    List<Author> searchByName(String name);
    List<Author> getByCountry(Country country);

}
