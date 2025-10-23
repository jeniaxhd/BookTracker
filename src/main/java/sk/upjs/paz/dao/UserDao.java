package sk.upjs.paz.dao;

import sk.upjs.paz.entity.User;

import java.util.List;

public interface UserDao {
    void add (User user);
    void delete(long id);
    void update(User user);
    User getById(long id);
    List<User> getByName(String name);
    User getByEmail(String email);
    User getByEmailAndPassword(String email, String password);


}
