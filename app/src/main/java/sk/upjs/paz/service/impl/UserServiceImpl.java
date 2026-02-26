package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.UserDao;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void add(User user) {
        validateUserForCreateOrUpdate(user, false);
        userDao.add(user);
    }

    @Override
    public void update(User user) {
        validateUserForCreateOrUpdate(user, true);
        userDao.update(user);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        userDao.delete(id);
    }

    @Override
    public Optional<User> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userDao.getById(id);
    }

    @Override
    public List<User> getByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        return userDao.getByName(name);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        // формат e-mail уже валідовується в entity (setEmail)
        return userDao.getByEmail(email);
    }

    @Override
    public void updateReadBooks(Long userId, int newCount) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (newCount < 0) {
            throw new IllegalArgumentException("readBooks cannot be negative");
        }
        userDao.updateReadBooks(userId, newCount);
    }

    // ----------------- private validation -----------------

    private void validateUserForCreateOrUpdate(User user, boolean requireId) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (requireId && user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null when updating");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }

        if (user.getReadBooks() < 0) {
            throw new IllegalArgumentException("readBooks cannot be negative");
        }
    }
}
