package sk.upjs.paz.service;

import sk.upjs.paz.dao.DaoFactory;
import sk.upjs.paz.service.impl.AuthorServiceImpl;
import sk.upjs.paz.service.impl.BookServiceImpl;
import sk.upjs.paz.service.impl.CountryServiceImpl;
import sk.upjs.paz.service.impl.GenreServiceImpl;
import sk.upjs.paz.service.impl.ReadingSessionServiceImpl;
import sk.upjs.paz.service.impl.ReviewServiceImpl;
import sk.upjs.paz.service.impl.UserServiceImpl;

public class ServiceFactory {

    public static final ServiceFactory INSTANCE = new ServiceFactory();

    private final AuthorService authorService;
    private final BookService bookService;
    private final CountryService countryService;
    private final GenreService genreService;
    private final ReadingSessionService readingSessionService;
    private final ReviewService reviewService;
    private final UserService userService;

    private ServiceFactory() {
        DaoFactory daoFactory = DaoFactory.INSTANCE;

        this.authorService = new AuthorServiceImpl(daoFactory.getAuthorDao());
        this.bookService = new BookServiceImpl(daoFactory.getBookDao());
        this.countryService = new CountryServiceImpl(daoFactory.getCountryDao());
        this.genreService = new GenreServiceImpl(daoFactory.getGenreDao());

        this.readingSessionService = new ReadingSessionServiceImpl(
                daoFactory.getReadingSessionDao(),
                daoFactory.getBookDao(),
                daoFactory.getUserDao()
        );

        this.reviewService = new ReviewServiceImpl(daoFactory.getReviewDao());
        this.userService = new UserServiceImpl(daoFactory.getUserDao());
    }

    public AuthorService getAuthorService() {
        return authorService;
    }

    public BookService getBookService() {
        return bookService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public GenreService getGenreService() {
        return genreService;
    }

    public ReadingSessionService getReadingSessionService() {
        return readingSessionService;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public UserService getUserService() {
        return userService;
    }
}
