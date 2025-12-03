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

    private final BookJdbcDao bookDao;
    private final GenreJdbcDao genreDao;
    private final AuthorJdbcDao authorDao;

    private DaoFactory() throws SQLException {
        this.connection = DbUtil.getConnection();

        this.bookDao = new BookJdbcDao(connection);
        this.genreDao = new GenreJdbcDao(connection);
        this.authorDao = new AuthorJdbcDao(connection);
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

    public Connection getConnection() {
        return connection;
    }
}