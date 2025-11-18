package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.UserDao;
import sk.upjs.paz.entity.User;

import java.util.List;
import java.util.Optional;

public class MysqlUserDao implements UserDao {
    @Override
    public void add(User user) {

    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM user WHERE id = ?";

    }

    @Override
    public void update(User user) {

    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<User> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return Optional.empty();
    }
}
