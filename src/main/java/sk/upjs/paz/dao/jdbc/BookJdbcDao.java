package sk.upjs.paz.dao.jdbc;

import org.springframework.dao.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;
import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookJdbcDao implements BookDao {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate tx;

    public BookJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        DataSource ds = jdbcTemplate.getDataSource();
        if (ds == null) {
            throw new IllegalStateException("JdbcTemplate has no DataSource");
        }
        this.tx = new TransactionTemplate(new DataSourceTransactionManager(ds));
    }

    private final RowMapper<Book> mapper = (rs, rowNum) -> {
        Book b = new Book();
        b.setId(rs.getLong("id"));
        b.setTitle(rs.getString("title"));
        b.setDescription(rs.getString("description"));
        b.setYear(rs.getObject("year", Integer.class));
        b.setPages(rs.getInt("pages"));
        b.setCoverPath(rs.getString("cover_path"));

        double avg = loadAverageRating(b.getId());
        b.setAverageRating(avg);

        List<Genre> genres = loadGenresForBook(b.getId());
        b.setGenre(genres);

        List<Author> authors = loadAuthorsForBook(b.getId());
        b.setAuthors(authors);

        return b;
    };

    @Override
    public void add(Book book) {
        tx.execute(status -> {
            String sql = "INSERT INTO book(title, description, year, pages, cover_path) VALUES(?, ?, ?, ?, ?)";
            KeyHolder kh = new GeneratedKeyHolder();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, book.getTitle());
                ps.setString(2, book.getDescription());
                ps.setObject(3, book.getYear(), Types.INTEGER);
                ps.setInt(4, book.getPages());
                ps.setString(5, book.getCoverPath());
                return ps;
            }, kh);

            if (kh.getKey() != null) {
                book.setId(kh.getKey().longValue());
            }

            if (book.getId() != null) {
                if (book.getAuthors() != null) {
                    insertBookAuthors(book);
                }
                if (book.getGenre() != null) {
                    insertBookGenres(book);
                }
            }
            return null;
        });
    }

    private void insertBookAuthors(Book book) {
        if (book.getAuthors() == null || book.getAuthors().isEmpty() || book.getId() == null) return;

        List<Author> authors = book.getAuthors().stream()
                .filter(a -> a != null && a.getId() != null)
                .toList();

        if (authors.isEmpty()) return;

        String sql = "INSERT INTO book_has_author(book_id, author_id) VALUES(?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
                ps.setLong(1, book.getId());
                ps.setLong(2, authors.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return authors.size();
            }
        });
    }

    private void insertBookGenres(Book book) {
        if (book.getGenre() == null || book.getGenre().isEmpty() || book.getId() == null) return;

        List<Genre> genres = book.getGenre().stream()
                .filter(g -> g != null && g.id() != null)
                .toList();

        if (genres.isEmpty()) return;

        String sql = "INSERT INTO book_has_genre(book_id, genre_id) VALUES(?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
                ps.setLong(1, book.getId());
                ps.setLong(2, genres.get(i).id());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public void delete(Long id) {
        tx.execute(status -> {
            jdbcTemplate.update("DELETE FROM book_has_author WHERE book_id = ?", id);
            jdbcTemplate.update("DELETE FROM book_has_genre WHERE book_id = ?", id);
            jdbcTemplate.update("DELETE FROM book WHERE id = ?", id);
            return null;
        });
    }

    @Override
    public void update(Book book) {
        tx.execute(status -> {
            String sql = "UPDATE book SET title = ?, description = ?, year = ?, pages = ?, cover_path = ? WHERE id = ?";

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, book.getTitle());
                ps.setString(2, book.getDescription());
                ps.setObject(3, book.getYear(), Types.INTEGER);
                ps.setInt(4, book.getPages());
                ps.setString(5, book.getCoverPath());
                ps.setLong(6, book.getId());
                return ps;
            });

            jdbcTemplate.update("DELETE FROM book_has_author WHERE book_id = ?", book.getId());
            jdbcTemplate.update("DELETE FROM book_has_genre WHERE book_id = ?", book.getId());

            insertBookAuthors(book);
            insertBookGenres(book);
            return null;
        });
    }

    private double loadAverageRating(long bookId) {
        Double v = jdbcTemplate.queryForObject(
                "SELECT AVG(rating) FROM review WHERE book_id = ?",
                Double.class,
                bookId
        );
        return v == null ? 0.0 : v;
    }

    private List<Genre> loadGenresForBook(long bookId) {
        String sql = "SELECT g.id, g.name " +
                "FROM genre g " +
                "JOIN book_has_genre bg ON g.id = bg.genre_id " +
                "WHERE bg.book_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")), bookId);
    }

    private List<Author> loadAuthorsForBook(long bookId) {
        String sql = "SELECT a.id, a.name, a.country, a.bio " +
                "FROM author a " +
                "JOIN book_has_author ba ON a.id = ba.author_id " +
                "WHERE ba.book_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Author a = new Author();
            a.setId(rs.getLong("id"));
            a.setName(rs.getString("name"));
            a.setCountry(rs.getString("country"));
            a.setBio(rs.getString("bio"));
            return a;
        }, bookId);
    }

    @Override
    public Optional<Book> getById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM book WHERE id = ?", mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> getAll() {
        return jdbcTemplate.query("SELECT * FROM book", mapper);
    }

    @Override
    public List<Book> getByAuthor(Author author) {
        if (author == null || author.getId() == null) return List.of();

        String sql = "SELECT b.* " +
                "FROM book b " +
                "JOIN book_has_author ba ON b.id = ba.book_id " +
                "WHERE ba.author_id = ?";

        return jdbcTemplate.query(sql, mapper, author.getId());
    }

    @Override
    public List<Book> getByGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return List.of();

        List<Long> ids = genres.stream()
                .filter(g -> g != null && g.id() != null)
                .map(Genre::id)
                .toList();

        if (ids.isEmpty()) return List.of();

        String placeholders = String.join(",", ids.stream().map(x -> "?").toList());
        String sql = "SELECT DISTINCT b.* " +
                "FROM book b " +
                "JOIN book_has_genre bg ON b.id = bg.book_id " +
                "WHERE bg.genre_id IN (" + placeholders + ")";

        return jdbcTemplate.query(sql, mapper, ids.toArray());
    }

    @Override
    public List<Book> getByGenre(Genre genre) {
        if (genre == null || genre.id() == null) return List.of();

        String sql = "SELECT b.* " +
                "FROM book b " +
                "JOIN book_has_genre bg ON b.id = bg.book_id " +
                "WHERE bg.genre_id = ?";

        return jdbcTemplate.query(sql, mapper, genre.id());
    }

    @Override
    public List<Book> getByTitle(String title) {
        return jdbcTemplate.query("SELECT * FROM book WHERE title LIKE ?", mapper, "%" + title + "%");
    }

    @Override
    public List<Book> getByYear(Integer year) {
        if (year == null) return List.of();
        return jdbcTemplate.query("SELECT * FROM book WHERE year = ?", mapper, year);
    }

    @Override
    public List<Book> getRandom(int limit) {
        return jdbcTemplate.query("SELECT * FROM book ORDER BY RAND() LIMIT ?", mapper, limit);
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

        List<Book> books = jdbcTemplate.query(sb.toString(), mapper, params.toArray());

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
