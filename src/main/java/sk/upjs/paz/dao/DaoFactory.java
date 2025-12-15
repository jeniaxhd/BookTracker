package sk.upjs.paz.dao;

import sk.upjs.paz.dao.jdbc.*;

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

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final CountryDao countryDao;
    private final UserDao userDao;
    private final ReviewDao reviewDao;
    private final ReadingSessionDao readingSessionDao;

    private DaoFactory() throws SQLException {
        this.connection = DbUtil.getConnection();

        this.bookDao = new BookJdbcDao(connection);
        this.authorDao = new AuthorJdbcDao(connection);
        this.genreDao = new GenreJdbcDao(connection);
        this.countryDao = new CountryJdbcDao(connection);
        this.userDao = new UserJdbcDao(connection);
        this.reviewDao = new ReviewJdbcDao(connection);
        this.readingSessionDao = new ReadingSessionJdbcDao(connection);
    }

    public BookDao getBookDao() {
        return bookDao;
    }

    public AuthorDao getAuthorDao() {
        return authorDao;
    }

    public GenreDao getGenreDao() {
        return genreDao;
    }

    public CountryDao getCountryDao() {
        return countryDao;
    }

    public ReviewDao getReviewDao() {
        return reviewDao;
    }

    public ReadingSessionDao getReadingSessionDao() {
        return readingSessionDao;
    }

    public Connection getConnection() {
        return connection;
    }

    public UserDao getUserDao() {
        return userDao;
    }
}
