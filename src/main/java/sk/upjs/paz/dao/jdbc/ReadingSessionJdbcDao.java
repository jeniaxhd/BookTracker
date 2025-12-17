package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.paz.dao.ReadingSessionDao;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.enums.BookState;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReadingSessionJdbcDao implements ReadingSessionDao {

    private final JdbcTemplate jdbcTemplate;

    public ReadingSessionJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ReadingSession> mapper = (rs, rowNum) -> {
        ReadingSession s = new ReadingSession();
        long id = rs.getLong("id");
        s.setId(id);

        Timestamp startTs = rs.getTimestamp("start");
        if (startTs != null) {
            LocalDateTime start = startTs.toLocalDateTime();
            s.setStart(start);
        }

        s.setDuration(rs.getInt("duration"));
        s.setEndPage(rs.getInt("endPage"));

        Date lastDate = rs.getDate("lastTimeRead");
        if (lastDate != null) {
            LocalDate lastTimeRead = lastDate.toLocalDate();
            s.setLastTimeRead(lastTimeRead);
        }

        long bookId = rs.getLong("book_id");
        Book book = new Book();
        book.setId(bookId);
        s.setBook(book);

        long userId = rs.getLong("user_id");
        User user = new User();
        user.setId(userId);
        s.setUser(user);

        BookState state = loadBookState(userId, bookId);
        s.setState(state);

        return s;
    };

    @Override
    public void add(ReadingSession readingSession) {
        String sql = "INSERT INTO readingSession(start, duration, endPage, lastTimeRead, book_id, user_id) VALUES(?, ?, ?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (readingSession.getStart() != null) {
                ps.setTimestamp(1, Timestamp.valueOf(readingSession.getStart()));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }

            ps.setInt(2, readingSession.getDuration());
            ps.setInt(3, readingSession.getEndPage());

            if (readingSession.getLastTimeRead() != null) {
                ps.setDate(4, Date.valueOf(readingSession.getLastTimeRead()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setLong(5, readingSession.getBook().getId());
            ps.setLong(6, readingSession.getUser().getId());

            return ps;
        }, kh);

        if (kh.getKey() != null) {
            readingSession.setId(kh.getKey().longValue());
        }
    }

    @Override
    public void update(ReadingSession readingSession) {
        String sql = "UPDATE readingSession SET start = ?, duration = ?, endPage = ?, lastTimeRead = ?, book_id = ?, user_id = ? WHERE id = ?";

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);

            if (readingSession.getStart() != null) {
                ps.setTimestamp(1, Timestamp.valueOf(readingSession.getStart()));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }

            ps.setInt(2, readingSession.getDuration());
            ps.setInt(3, readingSession.getEndPage());

            if (readingSession.getLastTimeRead() != null) {
                ps.setDate(4, Date.valueOf(readingSession.getLastTimeRead()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setLong(5, readingSession.getBook().getId());
            ps.setLong(6, readingSession.getUser().getId());
            ps.setLong(7, readingSession.getId());

            return ps;
        });
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM readingSession WHERE id = ?", id);
    }

    private BookState loadBookState(long userId, long bookId) {
        try {
            String stateStr = jdbcTemplate.queryForObject(
                    "SELECT bookstate FROM user_has_book WHERE user_id = ? AND book_id = ?",
                    String.class,
                    userId,
                    bookId
            );
            return stateStr == null ? null : BookState.valueOf(stateStr);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Optional<ReadingSession> getById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM readingSession WHERE id = ?", mapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ReadingSession> getByUser(Long userId) {
        return jdbcTemplate.query("SELECT * FROM readingSession WHERE user_id = ?", mapper, userId);
    }

    @Override
    public List<ReadingSession> getByBook(Long bookId) {
        return jdbcTemplate.query("SELECT * FROM readingSession WHERE book_id = ?", mapper, bookId);
    }

    @Override
    public Optional<ReadingSession> getByUserAndBook(Long userId, Long bookId) {
        String sql = "SELECT * FROM readingSession WHERE user_id = ? AND book_id = ? ORDER BY start DESC LIMIT 1";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, mapper, userId, bookId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ReadingSession> getAll() {
        return jdbcTemplate.query("SELECT * FROM readingSession", mapper);
    }

    @Override
    public void updateBookState(Long userId, Long bookId, BookState newState) {
        jdbcTemplate.update(
                "UPDATE user_has_book SET bookstate = ? WHERE user_id = ? AND book_id = ?",
                newState != null ? newState.name() : null,
                userId,
                bookId
        );
    }
}
