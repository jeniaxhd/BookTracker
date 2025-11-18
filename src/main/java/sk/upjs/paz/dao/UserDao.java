package sk.upjs.paz.dao;

import sk.upjs.paz.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void add (User user);
    void delete(Long id);
    void update(User user);
    Optional<User> getById(Long id);
    List<User> searchByName(String name);
    List<User> findAll();
    Optional<User> getByEmail(String email);


}
