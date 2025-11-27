package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.UserDao;
import sk.upjs.paz.entity.User;

import java.util.List;
import java.util.Optional;

public class UserJdbcDao implements UserDao {
    @Override
    public void add(User user) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<User> getByName(String name) {
        return List.of();
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void updateReadBooks(Long userId, int newCount) {

    }
}
