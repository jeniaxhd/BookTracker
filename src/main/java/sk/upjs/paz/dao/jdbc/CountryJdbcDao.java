package sk.upjs.paz.dao.jdbc;

import sk.upjs.paz.dao.CountryDao;
import sk.upjs.paz.entity.Country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountryJdbcDao implements CountryDao {
    private final Connection conn;

    public CountryJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Country> getAll() {
        String sql = "SELECT id, country_name FROM country ORDER BY id";
        List<Country> countries = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                countries.add(
                        new Country(
                                rs.getLong("id"),
                                rs.getString("country_name")
                        )
                );
            }

            return countries;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading countries", e);
        }
    }

    @Override
    public Optional<Country> getById(Long id) {
        String sql = "SELECT id, country_name FROM country WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(
                            new Country(
                                    rs.getLong("id"),
                                    rs.getString("country_name")
                            )
                    );
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting country by id: " + id, e);
        }
    }

    @Override
    public Optional<Country> getByName(String name) {
        String sql = "SELECT id, country_name FROM country WHERE country_name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(
                            new Country(
                                    rs.getLong("id"),
                                    rs.getString("country_name")
                            )
                    );
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting country by name: " + name, e);
        }
    }

}
