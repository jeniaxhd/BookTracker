package sk.upjs.paz.dao;

import sk.upjs.paz.entity.*;

import java.util.List;
import java.util.Optional;

public interface ReadingSessionDao {
    void add(ReadingSession readingSession);
    void update(ReadingSession readingSession);
    void delete(long id);

    Optional<ReadingSession> findById(long id);
    List<ReadingSession> findByUserId(long userId);
    List<ReadingSession> findByBookId(long bookId);
    Optional<ReadingSession> findActiveSession(long userId, long bookId);
    List<ReadingSession> getAll();

}
