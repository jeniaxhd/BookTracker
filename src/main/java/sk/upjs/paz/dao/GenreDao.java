package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Country;
import sk.upjs.paz.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    List<Genre> getAll();

    Optional<Genre> getById(Long id);

    Optional<Genre> getByName(String name);
}
