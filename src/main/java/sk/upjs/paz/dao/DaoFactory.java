package sk.upjs.paz.dao;

import sk.upjs.paz.dao.jdbc.*;
import sk.upjs.paz.dao.DbUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class DaoFactory {

    public static final DaoFactory INSTANCE;

    static {
        try {
            INSTANCE = new DaoFactory();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final Connection connection;

    private final BookJdbcDao bookDao;
    private final GenreJdbcDao genreDao;
    private final AuthorJdbcDao authorDao;
    private final UserJdbcDao userDao;
    private final ReadingSessionJdbcDao readingSessionDao;
    private final ReviewJdbcDao reviewDao;
    private final CountryJdbcDao countryDao;

    private DaoFactory() throws SQLException {
        this.connection = DbUtil.getConnection();

        this.bookDao = new BookJdbcDao(connection);
        this.genreDao = new GenreJdbcDao(connection);
        this.authorDao = new AuthorJdbcDao(connection);
        this.userDao = new UserJdbcDao(connection);
        this.readingSessionDao = new ReadingSessionJdbcDao(connection);
        this.reviewDao = new ReviewJdbcDao(connection);
        this.countryDao = new CountryJdbcDao(connection);
    }

    public BookJdbcDao getBookDao() {
        return bookDao;
    }

    public GenreJdbcDao getGenreDao() {
        return genreDao;
    }

    public AuthorJdbcDao getAuthorDao() {
        return authorDao;
    }

    public UserJdbcDao getUserDao() {
        return userDao;
    }

    public ReadingSessionJdbcDao getReadingSessionDao() {
        return readingSessionDao;
    }

    public ReviewJdbcDao getReviewDao() {
        return reviewDao;
    }

    public CountryJdbcDao getCountryDao() {
        return countryDao;
    }

    public Connection getConnection() {
        return connection;
    }
}
