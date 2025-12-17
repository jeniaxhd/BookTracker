package sk.upjs.paz.dao.jdbc;

import sk.upjs.paz.dao.UserDao;
import sk.upjs.paz.entity.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJdbcDao implements UserDao {

    private final Connection conn;

    public UserJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(User user) {
        String sql = "INSERT INTO user (name, mail, password_hash, createdAt, readBooks) VALUES (?, ?, ?, ?, ?)";

        LocalDateTime created = user.getCreatedAt();
        if (created == null) {
            created = LocalDateTime.now();
            user.setCreatedAt(created);
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setTimestamp(4, Timestamp.valueOf(created));
            ps.setInt(5, user.getReadBooks());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE user SET name = ?, mail = ?, readBooks = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getReadBooks());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("mail"));

        Timestamp ts = rs.getTimestamp("createdAt");
        if (ts != null) {
            u.setCreatedAt(ts.toLocalDateTime());
        }

        u.setReadBooks(rs.getInt("readBooks"));
        return u;
    }

    @Override
    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";

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
    public List<User> getByName(String name) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE name LIKE ? ORDER BY name";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY name";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        String sql = "SELECT id, name, mail, password_hash, createdAt, readBooks FROM `user` WHERE mail = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                User u = new User();
                u.setId(rs.getLong("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("mail"));
                u.setPasswordHash(rs.getString("password_hash"));
                Timestamp ts = rs.getTimestamp("createdAt");
                if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
                u.setReadBooks(rs.getInt("readBooks"));

                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateReadBooks(Long userId, int newCount) {
        String sql = "UPDATE user SET readBooks = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCount);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
