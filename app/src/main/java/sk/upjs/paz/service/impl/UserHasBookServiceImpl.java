package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.UserHasBookDao;
import sk.upjs.paz.entity.UserBookLink;
import sk.upjs.paz.enums.BookState;
import sk.upjs.paz.service.UserHasBookService;

import java.util.List;
import java.util.Optional;

public class UserHasBookServiceImpl implements UserHasBookService {

    private final UserHasBookDao userHasBookDao;

    public UserHasBookServiceImpl(UserHasBookDao userHasBookDao) {
        this.userHasBookDao = userHasBookDao;
    }

    @Override
    public void upsert(long userId, long bookId, BookState state) {
        validateIds(userId, bookId);

        if (state == null) {
            state = BookState.NOT_STARTED;
        }

        userHasBookDao.upsert(userId, bookId, state);
    }

    @Override
    public void remove(long userId, long bookId) {
        validateIds(userId, bookId);
        userHasBookDao.remove(userId, bookId);
    }

    @Override
    public boolean exists(long userId, long bookId) {
        validateIds(userId, bookId);
        return userHasBookDao.exists(userId, bookId);
    }

    @Override
    public Optional<BookState> getState(long userId, long bookId) {
        validateIds(userId, bookId);
        return userHasBookDao.getState(userId, bookId);
    }

    @Override
    public List<UserBookLink> listByUser(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        return userHasBookDao.listByUser(userId);
    }

    // ----------------- private validation -----------------

    private void validateIds(long userId, long bookId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be positive");
        }
    }
}
