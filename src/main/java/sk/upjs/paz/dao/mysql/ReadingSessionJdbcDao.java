package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.ReadingSessionDao;
import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.enums.Bookstate;

import java.util.List;
import java.util.Optional;

public class ReadingSessionJdbcDao implements ReadingSessionDao {

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
    public Optional<ReadingSession> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ReadingSession> getByUser(Long userId) {
        return List.of();
    }

    @Override
    public List<ReadingSession> getByBook(Long bookId) {
        return List.of();
    }

    @Override
    public Optional<ReadingSession> getByUserAndBook(Long userId, Long bookId) {
        return Optional.empty();
    }

    @Override
    public List<ReadingSession> getAll() {
        return List.of();
    }

    @Override
    public void updateBookState(Long userId, Long bookId, Bookstate newState) {

    }
}
