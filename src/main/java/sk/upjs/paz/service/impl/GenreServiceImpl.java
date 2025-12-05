package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.GenreDao;
import sk.upjs.paz.entity.Genre;
import sk.upjs.paz.service.GenreService;

import java.util.List;
import java.util.Optional;

public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;

    public GenreServiceImpl(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @Override
    public void add(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        if (genre.name() == null || genre.name().isBlank()) { // якщо поле інше — заміни на genre.genreName()
            throw new IllegalArgumentException("Genre name cannot be empty");
        }

        genreDao.add(genre);
    }

    @Override
    public void update(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        if (genre.id() == null) {
            throw new IllegalArgumentException("Genre ID cannot be null");
        }
        if (genre.name() == null || genre.name().isBlank()) {
            throw new IllegalArgumentException("Genre name cannot be empty");
        }

        genreDao.update(genre);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Genre ID cannot be null");
        }

        genreDao.delete(id);
    }

    @Override
    public Optional<Genre> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Genre ID cannot be null");
        }

        return genreDao.getById(id);
    }

    @Override
    public List<Genre> getAll() {
        return genreDao.getAll();
    }

    @Override
    public List<Genre> getByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        return genreDao.getByName(name);
    }
}
