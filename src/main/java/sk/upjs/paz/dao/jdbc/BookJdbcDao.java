package sk.upjs.paz.dao.jdbc;

import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookJdbcDao implements BookDao {

    private final Connection conn;

    public BookJdbcDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(Book book) {
        String sql = "INSERT INTO book(title, description, year, pages, cover_path) " +
                "VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getDescription());
            ps.setInt(3, book.getYear());
            ps.setInt(4, book.getPages());
            ps.setString(5, book.getCoverPath());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    book.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (book.getAuthors() != null && book.getId() != null) {
            insertBookAuthors(book);
        }
        if (book.getGenre() != null && book.getId() != null) {
            insertBookGenres(book);
        }
    }

    private void insertBookAuthors(Book book) {
        String linkSql = "INSERT INTO book_has_author(book_id, author_id) VALUES(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(linkSql)) {
            for (Author author : book.getAuthors()) {
                if (author == null || author.getId() == null) continue;
                ps.setLong(1, book.getId());
                ps.setLong(2, author.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔹 зв’язки книга–жанр (Genre = record)
    private void insertBookGenres(Book book) {
        String linkSql = "INSERT INTO book_has_genre(book_id, genre_id) VALUES(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(linkSql)) {
            for (Genre genre : book.getGenre()) {
                // genre.id() – компонент рекорда (типу Long id)
                if (genre == null || genre.id() == null) continue;
                ps.setLong(1, book.getId());
                ps.setLong(2, genre.id());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String deleteAuthorLinksSql = "DELETE FROM book_has_author WHERE book_id = ?";
        String deleteGenreLinksSql = "DELETE FROM book_has_genre WHERE book_id = ?";
        String deleteBookSql = "DELETE FROM book WHERE id = ?";

        try (PreparedStatement ps1 = conn.prepareStatement(deleteAuthorLinksSql);
             PreparedStatement ps2 = conn.prepareStatement(deleteGenreLinksSql);
             PreparedStatement ps3 = conn.prepareStatement(deleteBookSql)) {

            ps1.setLong(1, id);
            ps1.executeUpdate();

            ps2.setLong(1, id);
            ps2.executeUpdate();

            ps3.setLong(1, id);
            ps3.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Book book) {
        String sql = "UPDATE book SET title = ?, description = ?, year = ?, pages = ?, cover_path = ? " +
                "WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getDescription());
            ps.setInt(3, book.getYear());
            ps.setInt(4, book.getPages());
            ps.setString(5, book.getCoverPath());
            ps.setLong(6, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // спочатку чистимо всі зв’язки, потім знову додаємо актуальні
        String deleteAuthorLinksSql = "DELETE FROM book_has_author WHERE book_id = ?";
        String deleteGenreLinksSql = "DELETE FROM book_has_genre WHERE book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(deleteAuthorLinksSql)) {
            ps.setLong(1, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement ps = conn.prepareStatement(deleteGenreLinksSql)) {
            ps.setLong(1, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        insertBookAuthors(book);
        insertBookGenres(book);
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getLong("id"));
        b.setTitle(rs.getString("title"));
        b.setDescription(rs.getString("description"));
        b.setYear(rs.getInt("year"));
        b.setPages(rs.getInt("pages"));
        b.setCoverPath(rs.getString("cover_path"));

        double avg = loadAverageRating(rs.getLong("id"));
        b.setAverageRating(avg);

        // 🔹 жанри через join-таблицю
        List<Genre> genres = loadGenresForBook(b.getId());
        b.setGenre(genres);

        // 🔹 автори як і раніше
        List<Author> authors = loadAuthorsForBook(b.getId());
        b.setAuthors(authors);

        return b;
    }

    private double loadAverageRating(long bookId) throws SQLException {
        String sql = "SELECT AVG(rating) AS avg_rating FROM review WHERE book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }
        return 0.0;
    }

    // 🔹 жанри для книги (Genre = record)
    private List<Genre> loadGenresForBook(long bookId) throws SQLException {
        String sql = "SELECT g.id, g.name " +
                "FROM genre g " +
                "JOIN book_has_genre bg ON g.id = bg.genre_id " +
                "WHERE bg.book_id = ?";

        List<Genre> genres = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Genre g = new Genre(
                            rs.getLong("id"),
                            rs.getString("name")
                    );
                    genres.add(g);
                }
            }
        }
        return genres;
    }

    private List<Author> loadAuthorsForBook(long bookId) throws SQLException {
        String sql = "SELECT a.id, a.name, a.country, a.bio " +
                "FROM author a " +
                "JOIN book_has_author ba ON a.id = ba.author_id " +
                "WHERE ba.book_id = ?";

        List<Author> authors = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Author a = new Author();
                    a.setId(rs.getLong("id"));
                    a.setName(rs.getString("name"));
                    a.setCountry(rs.getString("country"));
                    a.setBio(rs.getString("bio"));
                    authors.add(a);
                }
            }
        }

        return authors;
    }

    @Override
    public Optional<Book> getById(Long id) {
        String sql = "SELECT * FROM book WHERE id = ?";

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
    public List<Book> getAll() {
        String sql = "SELECT * FROM book";
        List<Book> books = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    @Override
    public List<Book> getByAuthor(Author author) {
        if (author == null || author.getId() == null) {
            return List.of();
        }

        String sql = "SELECT b.* " +
                "FROM book b " +
                "JOIN book_has_author ba ON b.id = ba.book_id " +
                "WHERE ba.author_id = ?";
        List<Book> books = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, author.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    @Override
    public List<Book> getByGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return List.of();
        }

        // формуємо ?, ?, ?, ...
        String placeholders = String.join(",", genres.stream().map(g -> "?").toList());

        String sql =
                "SELECT DISTINCT b.* " +
                        "FROM book b " +
                        "JOIN book_has_genre bg ON b.id = bg.book_id " +
                        "WHERE bg.genre_id IN (" + placeholders + ")";

        List<Book> books = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            for (Genre g : genres) {
                ps.setLong(i++, g.id());   // Genre = record(id, name)
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }


    @Override
    public List<Book> getByGenre(Genre genre) {
        if (genre == null || genre.id() == null) return List.of();

        String sql = "SELECT b.* " +
                "FROM book b " +
                "JOIN book_has_genre bg ON b.id = bg.book_id " +
                "WHERE bg.genre_id = ?";
        List<Book> books = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, genre.id());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    @Override
    public List<Book> getByTitle(String title) {
        String sql = "SELECT * FROM book WHERE title LIKE ?";
        List<Book> books = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    @Override
    public List<Book> getByYear(Integer year) {
        String sql = "SELECT * FROM book WHERE year = ?";
        List<Book> books = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    @Override
    public List<Book> getRandom(int limit) {
        String sql = "SELECT * FROM book ORDER BY RAND() LIMIT ?";

        List<Book> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading random books", e);
        }

        return result;
    }

    @Override
    public List<Book> findByFilter(Integer yearFrom,
                                   Integer yearTo,
                                   Integer pagesFrom,
                                   Integer pagesTo,
                                   Double ratingFrom,
                                   Double ratingTo) {

        StringBuilder sb = new StringBuilder("SELECT * FROM book");
        List<Object> params = new ArrayList<>();
        boolean where = false;

        if (yearFrom != null) {
            sb.append(where ? " AND" : " WHERE").append(" year >= ?");
            params.add(yearFrom);
            where = true;
        }
        if (yearTo != null) {
            sb.append(where ? " AND" : " WHERE").append(" year <= ?");
            params.add(yearTo);
            where = true;
        }

        if (pagesFrom != null) {
            sb.append(where ? " AND" : " WHERE").append(" pages >= ?");
            params.add(pagesFrom);
            where = true;
        }
        if (pagesTo != null) {
            sb.append(where ? " AND" : " WHERE").append(" pages <= ?");
            params.add(pagesTo);
            where = true;
        }

        List<Book> books = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // додатковий фільтр по рейтингу в Java
        if (ratingFrom != null || ratingTo != null) {
            books.removeIf(b -> {
                double r = b.getAverageRating();
                if (ratingFrom != null && r < ratingFrom) return true;
                if (ratingTo != null && r > ratingTo) return true;
                return false;
            });
        }

        return books;
    }
}
