package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.paz.dao.ReviewDao;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Review;
import sk.upjs.paz.entity.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReviewJdbcDao implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    public ReviewJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Review> mapper = (rs, rowNum) -> {
        Review r = new Review();
        r.setId(rs.getLong("id"));
        r.setComment(rs.getString("comment"));

        Timestamp ts = rs.getTimestamp("createdAt");
        if (ts != null) {
            r.setCreatedAt(ts.toLocalDateTime());
        }

        Book b = new Book();
        b.setId(rs.getLong("book_id"));
        r.setBook(b);

        User u = new User();
        u.setId(rs.getLong("user_id"));
        r.setUser(u);

        return r;
    };

    @Override
    public void add(Review review) {
        String sql = "INSERT INTO review (comment, createdAt, book_id, user_id) VALUES (?, ?, ?, ?)";

        LocalDateTime created = review.getCreatedAt();
        if (created == null) {
            created = LocalDateTime.now();
            review.setCreatedAt(created);
        }

        KeyHolder kh = new GeneratedKeyHolder();
        LocalDateTime finalCreated = created;

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getComment());
            ps.setTimestamp(2, Timestamp.valueOf(finalCreated));
            ps.setLong(3, review.getBook().getId());
            ps.setLong(4, review.getUser().getId());
            return ps;
        }, kh);

        if (kh.getKey() != null) {
            review.setId(kh.getKey().longValue());
        }
    }
    @Override
    public void update(Review review) {
        jdbcTemplate.update(
                "UPDATE review SET comment = ?, createdAt = ?, book_id = ?, user_id = ? WHERE id = ?",
                review.getComment(),
                Timestamp.valueOf(review.getCreatedAt() != null ? review.getCreatedAt() : LocalDateTime.now()),
                review.getBook().getId(),
                review.getUser().getId(),
                review.getId()
        );
    }



    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM review WHERE id = ?", id);
    }

    @Override
    public List<Review> getByBook(Long bookId) {
        return jdbcTemplate.query("SELECT * FROM review WHERE book_id = ?", mapper, bookId);
    }

    @Override
    public List<Review> getByUser(Long userId) {
        return jdbcTemplate.query("SELECT * FROM review WHERE user_id = ?", mapper, userId);
    }

    @Override
    public Optional<Review> getById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM review WHERE id = ?", mapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Review> getByUserAndBook(Long userId, Long bookId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM review WHERE user_id = ? AND book_id = ?",
                            mapper,
                            userId,
                            bookId
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getAll() {
        return jdbcTemplate.query("SELECT * FROM review", mapper);
    }
}
