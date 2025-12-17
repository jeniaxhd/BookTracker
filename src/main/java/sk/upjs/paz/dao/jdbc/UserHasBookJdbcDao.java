package sk.upjs.paz.dao.jdbc;

import sk.upjs.paz.dao.UserHasBookDao;
import sk.upjs.paz.entity.UserBookLink;
import sk.upjs.paz.enums.BookState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserHasBookJdbcDao implements UserHasBookDao {

    private final Connection conn;

    public UserHasBookJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void upsert(long userId, long bookId, BookState state) {
        String sql = """
            INSERT INTO user_has_book (user_id, book_id, bookstate)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE bookstate = VALUES(bookstate)
        """;

        if (state == null) state = BookState.NOT_STARTED;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            ps.setString(3, state.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(long userId, long bookId) {
        String sql = "DELETE FROM user_has_book WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(long userId, long bookId) {
        String sql = "SELECT 1 FROM user_has_book WHERE user_id = ? AND book_id = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BookState> getState(long userId, long bookId) {
        String sql = "SELECT bookstate FROM user_has_book WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(BookState.valueOf(rs.getString("bookstate")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserBookLink> listByUser(long userId) {
        String sql = """
            SELECT user_id, book_id, bookstate
            FROM user_has_book
            WHERE user_id = ?
            ORDER BY book_id DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<UserBookLink> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private UserBookLink mapRow(ResultSet rs) throws SQLException {
        return new UserBookLink(
                rs.getLong("user_id"),
                rs.getLong("book_id"),
                BookState.valueOf(rs.getString("bookstate"))
        );
    }
}
