package sk.upjs.paz.dao;

import sk.upjs.paz.entity.*;

import java.util.List;
import java.util.Optional;

public interface ReadingSessionDao {
    void add(ReadingSession readingSession);
    void update(ReadingSession readingSession);
    void delete(Long id);

    Optional<ReadingSession> findById(Long id);
    List<ReadingSession> findByUserId(Long userId);
    List<ReadingSession> findByBookId(Long bookId);
    Optional<ReadingSession> findActiveSession(Long userId, Long bookId);
    List<ReadingSession> getAll();

}
