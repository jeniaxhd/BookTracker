package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sk.upjs.paz.dao.GenreDao;
import sk.upjs.paz.entity.Genre;

import java.util.List;
import java.util.Optional;

public class GenreJdbcDao implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Genre> mapper = (rs, rowNum) ->
            new Genre(rs.getLong("id"), rs.getString("name"));

    @Override
    public void add(Genre genre) {
        jdbcTemplate.update("INSERT INTO genre(name) VALUES (?)", genre.name());
    }

    @Override
    public void update(Genre genre) {
        jdbcTemplate.update("UPDATE genre SET name = ? WHERE id = ?", genre.name(), genre.id());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM genre WHERE id = ?", id);
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT id, name FROM genre ORDER BY name", mapper);
    }

    @Override
    public Optional<Genre> getById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT id, name FROM genre WHERE id = ?", mapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getByName(String name) {
        return jdbcTemplate.query("SELECT id, name FROM genre WHERE name = ?", mapper, name);
    }
}
