package sk.upjs.paz.dao.jdbc;

import sk.upjs.paz.dao.ReviewDao;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Review;
import sk.upjs.paz.entity.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewJdbcDao implements ReviewDao {

    private final Connection conn;

    public ReviewJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(Review review) {
        String sql = "INSERT INTO review(rating, comment, createdAt, book_id, user_id) VALUES(?, ?, ?, ?, ?)";

        LocalDateTime created = review.getCreatedAt();
        if (created == null) {
            created = LocalDateTime.now();
            review.setCreatedAt(created);
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setTimestamp(3, Timestamp.valueOf(created));
            ps.setLong(4, review.getBook().getId());
            ps.setLong(5, review.getUser().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void update(Review review) {
        String sql = "UPDATE review SET rating = ?, comment = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setLong(3, review.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM review WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getLong("id"));
        r.setRating(rs.getInt("rating"));
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
    }

    @Override
    public List<Review> getByBook(Long bookId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE book_id = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Review> getByUser(Long userId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Optional<Review> getById(Long id) {
        String sql = "SELECT * FROM review WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Review> getByUserAndBook(Long userId, Long bookId) {
        String sql = "SELECT * FROM review WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Review> getAll() {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM review";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
