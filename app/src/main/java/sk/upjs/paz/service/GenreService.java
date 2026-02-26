package sk.upjs.paz.service;

import sk.upjs.paz.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {

    void add(Genre genre);

    void update(Genre genre);

    void delete(Long id);

    Optional<Genre> getById(Long id);

    List<Genre> getAll();

    List<Genre> getByName(String name);
}
