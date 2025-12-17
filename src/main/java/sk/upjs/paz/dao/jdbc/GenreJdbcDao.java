package sk.upjs.paz.dao.jdbc;

import sk.upjs.paz.dao.GenreDao;
import sk.upjs.paz.entity.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenreJdbcDao implements GenreDao {

    private final Connection conn;

    public GenreJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(Genre genre) {
        String sql = "INSERT INTO genre(name) VALUES (?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre.name()); // або genre.genreName() якщо у рекорді поле інакше
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding genre: " + genre, e);
        }
    }

    @Override
    public void update(Genre genre) {
        String sql = "UPDATE genre SET name = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre.name());
            ps.setLong(2, genre.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating genre with id: " + genre.id(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM genre WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting genre with id: " + id, e);
        }
    }


    @Override
    public List<Genre> getAll() {
        String sql = "SELECT id, name FROM genre ORDER BY name";
        List<Genre> genres = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                genres.add(
                        new Genre(
                                rs.getLong("id"),
                                rs.getString("name")
                        )
                );
            }

            return genres;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading genres", e);
        }
    }

    @Override
    public Optional<Genre> getById(Long id) {
        String sql = "SELECT id, name FROM genre WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(
                            new Genre(
                                    rs.getLong("id"),
                                    rs.getString("name")
                            )
                    );
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting genre by id: " + id, e);
        }
    }

    @Override
    public List<Genre> getByName(String name) {
        String sql = "SELECT id, name FROM genre WHERE name = ?";
        List<Genre> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(
                            new Genre(
                                    rs.getLong("id"),
                                    rs.getString("name")
                            )
                    );
                }
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting genre(s) by name: " + name, e);
        }
    }
}
