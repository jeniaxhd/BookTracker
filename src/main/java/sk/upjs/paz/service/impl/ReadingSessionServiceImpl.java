package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.dao.ReadingSessionDao;
import sk.upjs.paz.dao.UserDao;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.enums.Bookstate;
import sk.upjs.paz.service.ReadingSessionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReadingSessionServiceImpl implements ReadingSessionService {
    private final ReadingSessionDao readingSessionDao;
    private final BookDao bookDao;
    private final UserDao userDao;

    public ReadingSessionServiceImpl(ReadingSessionDao readingSessionDao,
                                     BookDao bookDao,
                                     UserDao userDao) {
        this.readingSessionDao = readingSessionDao;
        this.bookDao = bookDao;
        this.userDao = userDao;
    }

    @Override
    public List<ReadingSession> getSessionsForUser(Long userId) {
        return readingSessionDao.getByUser(userId);
    }

    @Override
    public List<ReadingSession> getSessionsForBook(Long bookId) {
        return readingSessionDao.getByBook(bookId);
    }

    @Override
    public Optional<ReadingSession> getById(Long id) {
        return readingSessionDao.getById(id);
    }

    @Override
    public void add(ReadingSession session) {
        readingSessionDao.add(session);
    }

    @Override
    public void update(ReadingSession session) {
        readingSessionDao.update(session);
    }

    @Override
    public void delete(Long id) {
        readingSessionDao.delete(id);
    }

    @Override
    public ReadingSession startNewSession(Long userId, Long bookId, int startPage) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        Book book = bookDao.getById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with id " + bookId + " not found"));

        if (startPage < 0) {
            throw new IllegalArgumentException("startPage cannot be negative");
        }

        ReadingSession session = new ReadingSession();
        session.setUser(user);
        session.setBook(book);
        session.setState(Bookstate.READING); // uprav podľa tvojho enumu
        session.setStart(LocalDateTime.now());
        session.setDuration(0); // v minútach
        session.setEndPage(startPage);
        session.setLastTimeRead(LocalDate.now());

        readingSessionDao.add(session);

        return session;
    }

    @Override
    public void finishSession(Long sessionId,
                              int endPage,
                              int durationMinutes,
                              Bookstate finalState) {
        ReadingSession session = readingSessionDao.getById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("ReadingSession with id " + sessionId + " not found"));

        if (endPage < 0) {
            throw new IllegalArgumentException("endPage cannot be negative");
        }
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("durationMinutes cannot be negative");
        }

        session.setEndPage(endPage);
        session.setDuration(durationMinutes);
        session.setState(finalState);
        session.setLastTimeRead(LocalDate.now());

        readingSessionDao.update(session);

        // Tu môžeš doplniť ďalšiu logiku:
        // - ak finalState == FINISHED → zmeniť stav knihy v BookDao
        // - aktualizovať štatistiky používateľa (UserDao.updateReadBooks(...))

    }

}
