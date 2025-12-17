package sk.upjs.paz.dao.tc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.paz.enums.BookState;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public final class TestData {

    private TestData() {}

    public static long insertCountry(JdbcTemplate jt, String name) {
        KeyHolder kh = new GeneratedKeyHolder();
        jt.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO country(country_name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public static long insertGenre(JdbcTemplate jt, String name) {
        KeyHolder kh = new GeneratedKeyHolder();
        jt.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO genre(name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public static long insertAuthor(JdbcTemplate jt, String name, String country, String bio, Long countryId) {
        KeyHolder kh = new GeneratedKeyHolder();
        jt.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO author(name, country, bio, country_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ps.setString(2, country);
            ps.setString(3, bio);
            if (countryId == null) ps.setObject(4, null);
            else ps.setLong(4, countryId);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public static long insertUser(JdbcTemplate jt, String name, String email, String passwordHash, Integer readBooks) {
        KeyHolder kh = new GeneratedKeyHolder();
        jt.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO `user`(name, mail, password_hash, createdAt, readBooks) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, readBooks == null ? 0 : readBooks);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public static long insertBook(JdbcTemplate jt, String title, Integer year, Integer pages) {
        KeyHolder kh = new GeneratedKeyHolder();
        jt.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO book(title, description, year, pages, cover_path) VALUES(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, title);
            ps.setString(2, "desc");
            if (year == null) ps.setObject(3, null);
            else ps.setInt(3, year);
            if (pages == null) ps.setObject(4, null);
            else ps.setInt(4, pages);
            ps.setString(5, null);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public static void linkBookAuthor(JdbcTemplate jt, long bookId, long authorId) {
        jt.update("INSERT INTO book_has_author(book_id, author_id) VALUES(?, ?)", bookId, authorId);
    }

    public static void linkBookGenre(JdbcTemplate jt, long bookId, long genreId) {
        jt.update("INSERT INTO book_has_genre(book_id, genre_id) VALUES(?, ?)", bookId, genreId);
    }

    public static void upsertUserHasBook(JdbcTemplate jt, long userId, long bookId, BookState state) {
        String sql = "INSERT INTO user_has_book(user_id, book_id, bookstate) VALUES(?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE bookstate = VALUES(bookstate)";
        jt.update(sql, userId, bookId, (state == null ? BookState.NOT_STARTED : state).name());
    }

    public static long insertReadingSession(JdbcTemplate jt, long userId, long bookId, int duration, int endPage) {
        KeyHolder kh = new GeneratedKeyHolder();
        jt.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO readingSession(start, duration, endPage, lastTimeRead, book_id, user_id) VALUES(?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusMinutes(30)));
            ps.setInt(2, duration);
            ps.setInt(3, endPage);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(5, bookId);
            ps.setLong(6, userId);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public static long insertReview(JdbcTemplate jt, long userId, long bookId, int rating, String comment) {
        KeyHolder kh = new GeneratedKeyHolder();
        jt.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO review(rating, comment, createdAt, book_id, user_id) VALUES(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(4, bookId);
            ps.setLong(5, userId);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }
}
