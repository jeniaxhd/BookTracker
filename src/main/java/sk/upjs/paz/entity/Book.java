package sk.upjs.paz.entity;

import javax.swing.*;
import java.util.List;

public class Book {
    private Long id;
    private String title;
    private String description;
    private List<Author> authors;
    private double averageRating;
    private Genre genre;
    private int year;
    private int pages;
    private String language;
    private String coverPath;

    public Book() {
    }

    public Book(Long id, String title, List<Author> authors, Genre genre, int year, int pages, String coverPath,int averageRating, String description, String language) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.genre = genre;
        this.year = year;
        this.pages = pages;
        this.coverPath = coverPath;
        this.description = description;
        this.language = language;
        this.averageRating = averageRating;
    }

    public Book(String title, List<Author> authors, Genre genre, int year, int pages, String coverPath, String description, String language) {
        this.title = title;
        this.authors = authors;
        this.genre = genre;
        this.year = year;
        this.pages = pages;
        this.coverPath = coverPath;
        this.description = description;
        this.language = language;
        this.averageRating = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void addAuthor(Author author) {
        if (this.authors == null) {
            this.authors = new java.util.ArrayList<>();
        }
        this.authors.add(author);
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        String authorNames = "Unknown";

        if (authors != null && !authors.isEmpty()) {
            authorNames = authors.stream()
                    .map(Author::getName)
                    .reduce((a1, a2) -> a1 + ", " + a2)
                    .orElse("Unknown");
        }

        return "[" + title + " by " + authorNames + " " + year + " Description: " + description +
                " with average rating: " + averageRating + " in genres: " + getGenre().toString() +
                " pages: " + pages + " on " + language + "]";
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

}

