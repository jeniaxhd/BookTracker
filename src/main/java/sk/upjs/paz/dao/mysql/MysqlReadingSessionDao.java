package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.ReadingSessionDao;
import sk.upjs.paz.entity.ReadingSession;

import java.util.List;
import java.util.Optional;

public class MysqlReadingSessionDao implements ReadingSessionDao {
    @Override
    public void add(ReadingSession readingSession) {

    }

    @Override
    public void update(ReadingSession readingSession) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<ReadingSession> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ReadingSession> findByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<ReadingSession> findByBookId(Long bookId) {
        return List.of();
    }

    @Override
    public Optional<ReadingSession> findActiveSession(Long userId, Long bookId) {
        return Optional.empty();
    }

    @Override
    public List<ReadingSession> getAll() {
        return List.of();
    }
}
