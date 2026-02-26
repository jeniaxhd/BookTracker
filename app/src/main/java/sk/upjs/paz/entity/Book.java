package sk.upjs.paz.entity;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private Long id;
    private String title;
    private String description;
    private List<Author> authors;
    private List<Genre> genres = new ArrayList<>();
    private Integer year;
    private int pages;
    private String coverPath;

    public Book() {
    }

    public Book(Long id, String title, List<Author> authors, List<Genre> genre, int year, int pages, String coverPath, String description, String language) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.genres = genre;
        this.year = year;
        this.pages = pages;
        this.coverPath = coverPath;
        this.description = description;
    }

    public Book(String title, List<Author> authors, List<Genre> genre, int year, int pages, String coverPath, String description, String language) {
        this.title = title;
        this.authors = authors;
        this.genres = genre;
        this.year = year;
        this.pages = pages;
        this.coverPath = coverPath;
        this.description = description;
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
        this.authors = (authors != null) ? authors : new ArrayList<>();
    }

    public void addAuthor(Author author) {
        if (this.authors == null) {
            this.authors = new java.util.ArrayList<>();
        }
        this.authors.add(author);
    }

    public List<Genre> getGenre() {
        return genres;
    }

    public void setGenre(List<Genre> genre) {
        this.genres = (genre != null) ? genre : new ArrayList<>();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
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
                " in genres: " + getGenre().toString() +
                " pages: " + pages + "]";
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }


}

