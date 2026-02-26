package sk.upjs.paz.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.dao.CurrentlyReadingDao;
import sk.upjs.paz.ui.dto.ActiveBookCard;

import java.util.List;

public class CurrentlyReadingJdbcDao implements CurrentlyReadingDao {

    private final JdbcTemplate jdbcTemplate;

    public CurrentlyReadingJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ActiveBookCard> listActiveBooks(long userId) {
        String sql = """
            SELECT
              b.id AS book_id,
              b.title,
              b.pages AS total_pages,
              b.cover_path,
              COALESCE(gq.genres_text, 'Unknown') AS genre_name,
              COALESCE(aq.authors_text, 'Unknown') AS authors_text,
              COALESCE(cp.current_page, 0) AS current_page,
              COALESCE(tot.total_minutes, 0) AS total_minutes
            FROM user_has_book uhb
            JOIN book b ON b.id = uhb.book_id
            LEFT JOIN (
              SELECT bg.book_id,
                     GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', ') AS genres_text
              FROM book_has_genre bg
              JOIN genre g ON g.id = bg.genre_id
              GROUP BY bg.book_id
            ) gq ON gq.book_id = b.id
            LEFT JOIN (
              SELECT bha.book_id,
                     GROUP_CONCAT(DISTINCT a.name ORDER BY a.name SEPARATOR ', ') AS authors_text
              FROM book_has_author bha
              JOIN author a ON a.id = bha.author_id
              GROUP BY bha.book_id
            ) aq ON aq.book_id = b.id
            LEFT JOIN (
              SELECT book_id, MAX(endPage) AS current_page
              FROM readingSession
              WHERE user_id = ?
              GROUP BY book_id
            ) cp ON cp.book_id = b.id
            LEFT JOIN (
              SELECT book_id, SUM(duration) AS total_minutes
              FROM readingSession
              WHERE user_id = ?
              GROUP BY book_id
            ) tot ON tot.book_id = b.id
            WHERE uhb.user_id = ?
              AND uhb.bookstate = 'READING'
            ORDER BY b.title
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ActiveBookCard(
                        rs.getLong("book_id"),
                        rs.getString("title"),
                        rs.getString("authors_text"),
                        rs.getString("genre_name"),
                        rs.getInt("total_pages"),
                        rs.getInt("current_page"),
                        rs.getInt("total_minutes"),
                        rs.getString("cover_path")
                ),
                userId, userId, userId
        );
    }
}
