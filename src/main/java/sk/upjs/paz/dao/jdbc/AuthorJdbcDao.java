package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.paz.dao.AuthorDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Country;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;

public class AuthorJdbcDao implements AuthorDao {

    private final JdbcTemplate jdbcTemplate;

    public AuthorJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Author> mapper = (rs, rowNum) -> {
        Author a = new Author();
        a.setId(rs.getLong("id"));
        a.setName(rs.getString("name"));
        a.setCountry(rs.getString("country"));
        a.setBio(rs.getString("bio"));
        return a;
    };

    @Override
    public void add(Author author) {
        String sql = "INSERT INTO author(name, country, bio, country_id) VALUES(?, ?, ?, ?)";

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, author.getName());
            ps.setString(2, author.getCountry());
            ps.setString(3, author.getBio());
            ps.setObject(4, author.getCountryObj() != null ? author.getCountryObj().id() : null);
            return ps;
        }, kh);

        if (kh.getKey() != null) {
            author.setId(kh.getKey().longValue());
        }
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM author WHERE id = ?", id);
    }

    @Override
    public void update(Author author) {
        jdbcTemplate.update(
                "UPDATE author SET name = ?, country = ?, bio = ?, country_id = ? WHERE id = ?",
                author.getName(),
                author.getCountry(),
                author.getBio(),
                author.getCountryObj() != null ? author.getCountryObj().id() : null,
                author.getId()
        );
    }

    @Override
    public Optional<Author> getById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM author WHERE id = ?", mapper, id)
            );
        } catch (EmptyStackException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Author> getAll() {
        return jdbcTemplate.query("SELECT * FROM author ORDER BY name", mapper);
    }

    @Override
    public List<Author> getByName(String name) {
        return jdbcTemplate.query("SELECT * FROM author WHERE name LIKE ?", mapper, "%" + name + "%");
    }

    @Override
    public List<Author> getByCountry(Country country) {
        return jdbcTemplate.query("SELECT * FROM author WHERE country_id = ?", mapper, country.id());
    }
}
