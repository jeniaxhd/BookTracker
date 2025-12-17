package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sk.upjs.paz.dao.CountryDao;
import sk.upjs.paz.entity.Country;

import java.util.List;
import java.util.Optional;

public class CountryJdbcDao implements CountryDao {

    private final JdbcTemplate jdbcTemplate;

    public CountryJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Country> mapper = (rs, rowNum) ->
            new Country(rs.getLong("id"), rs.getString("country_name"));

    @Override
    public void add(Country country) {
        jdbcTemplate.update("INSERT INTO country(country_name) VALUES (?)", country.name());
    }

    @Override
    public void update(Country country) {
        jdbcTemplate.update("UPDATE country SET country_name = ? WHERE id = ?", country.name(), country.id());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM country WHERE id = ?", id);
    }

    @Override
    public List<Country> getAll() {
        return jdbcTemplate.query("SELECT id, country_name FROM country ORDER BY id", mapper);
    }

    @Override
    public Optional<Country> getById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT id, country_name FROM country WHERE id = ?", mapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Country> getByName(String name) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT id, country_name FROM country WHERE country_name = ?", mapper, name)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
