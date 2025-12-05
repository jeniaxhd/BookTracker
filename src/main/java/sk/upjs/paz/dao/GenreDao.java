package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Country;
import sk.upjs.paz.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    void add(Genre genre);

    void update(Genre genre);

    void delete(Long id);

    List<Genre> getAll();

    Optional<Genre> getById(Long id);

    List<Genre> getByName(String name);
}
