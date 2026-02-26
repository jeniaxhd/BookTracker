package sk.upjs.paz.dao;

import sk.upjs.paz.entity.*;
import sk.upjs.paz.enums.BookState;

import java.util.List;
import java.util.Optional;

public interface ReadingSessionDao {
    void add(ReadingSession readingSession);
    void update(ReadingSession readingSession);
    void delete(Long id);

    Optional<ReadingSession> getById(Long id);
    List<ReadingSession> getByUser(Long userId);
    List<ReadingSession> getByBook(Long bookId);
    Optional<ReadingSession> getByUserAndBook(Long userId, Long bookId);
    List<ReadingSession> getAll();
    void updateBookState(Long userId, Long bookId, BookState newState);

}
