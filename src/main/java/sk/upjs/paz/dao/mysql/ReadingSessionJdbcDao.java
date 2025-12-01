package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.ReadingSessionDao;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.enums.Bookstate;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReadingSessionJdbcDao implements ReadingSessionDao {

    private final Connection conn;

    public ReadingSessionJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(ReadingSession readingSession) {
        String sql = "INSERT INTO readingSession(start, duration, endPage, lastTimeRead, book_id, user_id) VALUES(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    readingSession.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ReadingSession readingSession) {
        String sql = "UPDATE readingSession SET start = ?, duration = ?, endPage = ?, lastTimeRead = ?,book_id = ?, user_id = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM readingSession WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ReadingSession mapRow(ResultSet rs) throws SQLException {
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

        Bookstate state = loadBookState(userId, bookId);
        s.setState(state);

        return s;
    }

    private Bookstate loadBookState(long userId, long bookId) throws SQLException {
        String sql = "SELECT bookstate FROM user_has_book WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stateStr = rs.getString("bookstate");
                    if (stateStr != null) {
                        return Bookstate.valueOf(stateStr);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Optional<ReadingSession> getById(Long id) {
        String sql = "SELECT * FROM readingSession WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<ReadingSession> getByUser(Long userId) {
        String sql = "SELECT * FROM readingSession WHERE user_id = ?";
        List<ReadingSession> sessions = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sessions;
    }

    @Override
    public List<ReadingSession> getByBook(Long bookId) {
        String sql = "SELECT * FROM readingSession WHERE book_id = ?";
        List<ReadingSession> sessions = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sessions;
    }

    @Override
    public Optional<ReadingSession> getByUserAndBook(Long userId, Long bookId) {
        String sql = "SELECT * FROM readingSession WHERE user_id = ? AND book_id = ? ORDER BY start DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<ReadingSession> getAll() {
        String sql = "SELECT * FROM readingSession";
        List<ReadingSession> sessions = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sessions.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sessions;
    }

    @Override
    public void updateBookState(Long userId, Long bookId, Bookstate newState) {
        String sql = "UPDATE user_has_book SET bookstate = ? WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (newState != null) {
                ps.setString(1, newState.name());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            ps.setLong(2, userId);
            ps.setLong(3, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
