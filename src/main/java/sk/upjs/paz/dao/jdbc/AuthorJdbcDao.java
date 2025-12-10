package sk.upjs.paz.dao.jdbc;

import sk.upjs.paz.dao.AuthorDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorJdbcDao implements AuthorDao {

    private final Connection conn;

    public AuthorJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(Author author) {
        String sql = "INSERT INTO author(name, country, bio, country_id) " + "VALUES(?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getCountry());
            ps.setString(3, author.getBio());
            ps.setObject(4, author.getCountryObj() != null ? author.getCountryObj().id() : null);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    author.setId(rs.getLong(1));
                }
            }

        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM author WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Author author) {
        String sql =
                "UPDATE author SET name = ?, country = ?, bio = ?, country_id = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getCountry());
            ps.setString(3, author.getBio());
            ps.setObject(4, author.getCountryObj() != null ? author.getCountryObj().id() : null);
            ps.setLong(5, author.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Author mapRow(ResultSet rs) throws SQLException {
        Author a = new Author();
        a.setId(rs.getLong("id"));
        a.setName(rs.getString("name"));
        a.setCountry(rs.getString("country"));
        a.setBio(rs.getString("bio"));
        return a;
    }

    @Override
    public Optional<Author> getById(Long id) {
        String sql = "SELECT * FROM author WHERE id = ?";

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
    public List<Author> getAll() {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM author ORDER BY name";

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
    public List<Author> getByName(String name) {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM author WHERE name LIKE ?";

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
    public List<Author> getByCountry(Country country) {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM author WHERE country_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, country.id());

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

}
