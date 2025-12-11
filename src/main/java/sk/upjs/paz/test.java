package sk.upjs.paz;

import sk.upjs.paz.dao.AuthorDao;
import sk.upjs.paz.dao.BookDao;
import sk.upjs.paz.dao.jdbc.AuthorJdbcDao;
import sk.upjs.paz.dao.jdbc.BookJdbcDao;
import sk.upjs.paz.dao.DbUtil;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;

import java.sql.Connection;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException {
        Connection conn = DbUtil.getConnection();
        BookDao bookDao = new BookJdbcDao(conn);
        Genre horror = new Genre(9L, "Horror");
        Author krajci = new Author("Stanislav Krajci", "Slovakia", null, null);
        AuthorDao authorDao = new AuthorJdbcDao(conn);
        Book book = new Book();
        book.setTitle("Introduction to MZI");
        book.addAuthor(krajci);

        book.setYear(2025);
        book.setDescription("Xyeta");
        book.setAverageRating(0.0);
        //book.setGenre(horror);
        book.setPages(666);
        //book.setLanguage("Slovak");

        Genre genre = new Genre(1L, "Fantasy");
        //book.setGenre(genre);

        bookDao.add(book);
        authorDao.add(krajci);

        bookDao.getByTitle("Introduction to MZI");
    }
}