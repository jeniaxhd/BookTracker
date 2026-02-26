package sk.upjs.paz.service;

import sk.upjs.paz.dao.DaoFactory;
import sk.upjs.paz.service.impl.*;

public class ServiceFactory {

    public static final ServiceFactory INSTANCE = new ServiceFactory();

    private final AuthorService authorService;
    private final BookService bookService;
    private final CountryService countryService;
    private final GenreService genreService;
    private final ReadingSessionService readingSessionService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final UserHasBookService userHasBookService;
    private final CurrentlyReadingService currentlyReadingService;
    private final StatisticsService statisticsService;


    private ServiceFactory() {
        DaoFactory daoFactory = DaoFactory.INSTANCE;

        this.authorService = new AuthorServiceImpl(daoFactory.getAuthorDao());
        this.bookService = new BookServiceImpl(
                daoFactory.getBookDao(),
                daoFactory.getAuthorDao(),
                daoFactory.getGenreDao()
        );

        this.countryService = new CountryServiceImpl(daoFactory.getCountryDao());
        this.genreService = new GenreServiceImpl(daoFactory.getGenreDao());

        this.readingSessionService = new ReadingSessionServiceImpl(
                daoFactory.getReadingSessionDao(),
                daoFactory.getBookDao(),
                daoFactory.getUserDao()
        );

        this.reviewService = new ReviewServiceImpl(daoFactory.getReviewDao());
        this.userService = new UserServiceImpl(daoFactory.getUserDao());
        this.userHasBookService = new UserHasBookServiceImpl(daoFactory.getUserHasBookDao());
        this.currentlyReadingService = new CurrentlyReadingServiceImpl(daoFactory.getCurrentlyReadingDao());
        this.statisticsService = new StatisticsServiceImpl(daoFactory.getStatisticsDao());
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

    public UserHasBookService getUserHasBookService() {return userHasBookService;}

    public CurrentlyReadingService getCurrentlyReadingService() {return currentlyReadingService;}

    public StatisticsService getStatisticsService() {return statisticsService;}

}
