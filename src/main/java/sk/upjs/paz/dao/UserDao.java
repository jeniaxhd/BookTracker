package sk.upjs.paz.dao;

import sk.upjs.paz.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void add (User user);
    void delete(Long id);
    void update(User user);

    Optional<User> getById(Long id);
    List<User> getByName(String name);
    List<User> getAll();
    Optional<User> getByEmail(String email);

    void updateReadBooks(Long userId, int newCount);


}
