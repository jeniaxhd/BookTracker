package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.AuthorDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Country;
import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.service.AuthorService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;

    public AuthorServiceImpl(AuthorDao authorDao) {
        this.authorDao = Objects.requireNonNull(authorDao, "authorDao must not be null");
    }

    @Override
    public void add(Author author) {
        authorDao.add(author);
    }

    @Override
    public void update(Author author) {
        authorDao.update(author);
    }

    @Override
    public void delete(Long id) {
        authorDao.delete(id);
    }


    @Override
    public Optional<Author> getById(Long id) {
        return authorDao.getById(id);
    }

    @Override
    public List<Author> getAll() {
        return authorDao.getAll();
    }

    @Override
    public List<Author> getByCountry(Country country) {
        return authorDao.getByCountry(country);
    }

    @Override
    public List<Author> searchByName(String namePart) {
        return authorDao.getByName(namePart);
    }
}
