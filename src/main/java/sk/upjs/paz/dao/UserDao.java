package sk.upjs.paz.dao;

import sk.upjs.paz.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void add (User user);
    void delete(long id);
    void update(User user);
    Optional<User> getById(long id);
    List<User> searchByName(String name);
    Optional<User> getByEmail(String email);


}
