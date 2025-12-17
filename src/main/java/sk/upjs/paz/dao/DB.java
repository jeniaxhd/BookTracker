package sk.upjs.paz.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.dao.jdbc.*;

public final class DB {

    public static final DB INSTANCE = new DB();

    private final JdbcTemplate jdbcTemplate;

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final CountryDao countryDao;
    private final UserDao userDao;
    private final ReviewDao reviewDao;
    private final ReadingSessionDao readingSessionDao;
    private final UserHasBookDao userHasBookDao;
    private final CurrentlyReadingDao currentlyReadingDao;

    private DB() {
        this.jdbcTemplate = DbUtil.getJdbcTemplate();

        this.bookDao = new BookJdbcDao(jdbcTemplate);
        this.authorDao = new AuthorJdbcDao(jdbcTemplate);
        this.genreDao = new GenreJdbcDao(jdbcTemplate);
        this.countryDao = new CountryJdbcDao(jdbcTemplate);
        this.userDao = new UserJdbcDao(jdbcTemplate);
        this.reviewDao = new ReviewJdbcDao(jdbcTemplate);
        this.readingSessionDao = new ReadingSessionJdbcDao(jdbcTemplate);
        this.userHasBookDao = new UserHasBookJdbcDao(jdbcTemplate);
        this.currentlyReadingDao = new CurrentlyReadingJdbcDao(jdbcTemplate);
    }

    public BookDao getBookDao() { return bookDao; }
    public AuthorDao getAuthorDao() { return authorDao; }
    public GenreDao getGenreDao() { return genreDao; }
    public CountryDao getCountryDao() { return countryDao; }
    public UserDao getUserDao() { return userDao; }
    public ReviewDao getReviewDao() { return reviewDao; }
    public ReadingSessionDao getReadingSessionDao() { return readingSessionDao; }
    public UserHasBookDao getUserHasBookDao() { return userHasBookDao; }
    public CurrentlyReadingDao getCurrentlyReadingDao() { return currentlyReadingDao; }

    public JdbcTemplate getJdbcTemplate() { return jdbcTemplate; }
}
