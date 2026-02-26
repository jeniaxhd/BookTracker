package sk.upjs.paz.dao;

import sk.upjs.paz.entity.UserBookLink;
import sk.upjs.paz.enums.BookState;

import java.util.List;
import java.util.Optional;

public interface UserHasBookDao {
    void upsert(long userId, long bookId, BookState state);
    void remove(long userId, long bookId);

    boolean exists(long userId, long bookId);
    Optional<BookState> getState(long userId, long bookId);

    List<UserBookLink> listByUser(long userId);
}
