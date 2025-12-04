package sk.upjs.paz.service;

import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Country;
import sk.upjs.paz.entity.ReadingSession;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    void add(Author author);

    void update(Author author);

    void delete(Long id);

    Optional<Author> getById(Long id);

    List<Author> getAll();

    List<Author> getByCountry(Country country);

    List<Author> searchByName(String namePart);
}
