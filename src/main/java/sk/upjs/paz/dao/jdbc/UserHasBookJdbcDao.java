package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sk.upjs.paz.dao.UserHasBookDao;
import sk.upjs.paz.entity.UserBookLink;
import sk.upjs.paz.enums.BookState;

import java.util.List;
import java.util.Optional;

public class UserHasBookJdbcDao implements UserHasBookDao {

    private final JdbcTemplate jdbcTemplate;

    public UserHasBookJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<UserBookLink> mapper = (rs, rowNum) ->
            new UserBookLink(
                    rs.getLong("user_id"),
                    rs.getLong("book_id"),
                    BookState.valueOf(rs.getString("bookstate"))
            );

    @Override
    public void upsert(long userId, long bookId, BookState state) {
        if (state == null) state = BookState.NOT_STARTED;

        String sql = """
            INSERT INTO user_has_book (user_id, book_id, bookstate)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE bookstate = VALUES(bookstate)
        """;

        jdbcTemplate.update(sql, userId, bookId, state.name());
    }

    @Override
    public void remove(long userId, long bookId) {
        jdbcTemplate.update("DELETE FROM user_has_book WHERE user_id = ? AND book_id = ?", userId, bookId);
    }

    @Override
    public boolean exists(long userId, long bookId) {
        Integer v = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM user_has_book WHERE user_id = ? AND book_id = ?)",
                Integer.class, userId, bookId
        );
        return v != null && v == 1;
    }

    @Override
    public Optional<BookState> getState(long userId, long bookId) {
        try {
            String s = jdbcTemplate.queryForObject(
                    "SELECT bookstate FROM user_has_book WHERE user_id = ? AND book_id = ?",
                    String.class,
                    userId,
                    bookId
            );
            return s == null ? Optional.empty() : Optional.of(BookState.valueOf(s));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
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

        return jdbcTemplate.query(sql, mapper, userId);
    }
}
