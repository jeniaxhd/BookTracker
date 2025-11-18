package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.AuthorDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Country;

import java.util.List;
import java.util.Optional;

public class MysqlAuthorDao implements AuthorDao {
    @Override
    public void add(Author author) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void update(Author author) {

    }

    @Override
    public Optional<Author> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Author> getAll() {
        return List.of();
    }

    @Override
    public Optional<Author> getByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Author> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<Author> getByCountry(Country country) {
        return List.of();
    }

}
