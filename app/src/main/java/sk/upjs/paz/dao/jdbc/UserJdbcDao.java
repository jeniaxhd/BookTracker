package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.paz.dao.UserDao;
import sk.upjs.paz.entity.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> mapperBasic = (rs, rowNum) -> {
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
    };

    private final RowMapper<User> mapperWithPassword = (rs, rowNum) -> {
        User u = mapperBasic.mapRow(rs, rowNum);
        u.setPasswordHash(rs.getString("password_hash"));
        return u;
    };

    @Override
    public void add(User user) {
        String sql = "INSERT INTO `user` (name, mail, password_hash, createdAt, readBooks) VALUES (?, ?, ?, ?, ?)";

        LocalDateTime created = user.getCreatedAt();
        if (created == null) {
            created = LocalDateTime.now();
            user.setCreatedAt(created);
        }

        LocalDateTime finalCreated = created;
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setTimestamp(4, Timestamp.valueOf(finalCreated));
            ps.setInt(5, user.getReadBooks());
            return ps;
        }, kh);

        if (kh.getKey() != null) {
            user.setId(kh.getKey().longValue());
        }
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM `user` WHERE id = ?", id);
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update(
                "UPDATE `user` SET name = ?, mail = ?, readBooks = ? WHERE id = ?",
                user.getName(),
                user.getEmail(),
                user.getReadBooks(),
                user.getId()
        );
    }

    @Override
    public Optional<User> getById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT id, name, mail, createdAt, readBooks FROM `user` WHERE id = ?", mapperBasic, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getByName(String name) {
        return jdbcTemplate.query(
                "SELECT id, name, mail, createdAt, readBooks FROM `user` WHERE name LIKE ? ORDER BY name",
                mapperBasic,
                "%" + name + "%"
        );
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "SELECT id, name, mail, createdAt, readBooks FROM `user` ORDER BY name",
                mapperBasic
        );
    }

    @Override
    public Optional<User> getByEmail(String email) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT id, name, mail, password_hash, createdAt, readBooks FROM `user` WHERE mail = ?",
                            mapperWithPassword,
                            email
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateReadBooks(Long userId, int newCount) {
        jdbcTemplate.update("UPDATE `user` SET readBooks = ? WHERE id = ?", newCount, userId);
    }
}
