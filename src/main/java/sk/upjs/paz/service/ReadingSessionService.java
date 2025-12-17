package sk.upjs.paz.service;

import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.enums.BookState;

import java.util.List;
import java.util.Optional;

public interface ReadingSessionService {
    List<ReadingSession> getSessionsForUser(Long userId);

    List<ReadingSession> getSessionsForBook(Long bookId);

    Optional<ReadingSession> getById(Long id);

    void add(ReadingSession session);

    void update(ReadingSession session);

    void delete(Long id);

    ReadingSession startNewSession(Long userId, Long bookId, int startPage);

    void finishSession(Long sessionId, int endPage, int durationMinutes, BookState finalState);

}
