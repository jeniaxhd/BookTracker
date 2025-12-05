package sk.upjs.paz.service;

import sk.upjs.paz.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void add(User user);

    void update(User user);

    void delete(Long id);

    Optional<User> getById(Long id);

    List<User> getByName(String name);

    List<User> getAll();

    Optional<User> getByEmail(String email);

    void updateReadBooks(Long userId, int newCount);
}
