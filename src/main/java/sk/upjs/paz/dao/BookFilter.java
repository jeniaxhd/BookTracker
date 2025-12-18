package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Genre;
import sk.upjs.paz.enums.BookState;

import java.util.List;

public class BookFilter {
    private String titlePart;
    private Long authorId;
    private Integer fromYear;
    private Integer toYear;
    private List<Genre> genres;
    private BookState state;
    private int fromPagesCount;
    private int toPagesCount;

    // опційно: сортування
    private String sortBy;        // "title", "year", "author", ...
    private boolean sortAsc = true;

    // гетери/сетери

    public String getTitlePart() {
        return titlePart;
    }

    public void setTitlePart(String titlePart) {
        this.titlePart = titlePart;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Integer getFromYear() {
        return fromYear;
    }

    public void setFromYear(Integer fromYear) {
        this.fromYear = fromYear;
    }

    public Integer getToYear() {
        return toYear;
    }

    public void setToYear(Integer toYear) {
        this.toYear = toYear;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public BookState getState() {
        return state;
    }

    public void setState(BookState state) {
        this.state = state;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public Integer getFromPagesCount() {
        return fromPagesCount;
    }

    public void setFromPagesCount(Integer fromPagesCount) {
        this.fromPagesCount = fromPagesCount;
    }

    public Integer getToPagesCount() {
        return toPagesCount;
    }

    public void setToPagesCount(Integer toPagesCount) {
        this.toPagesCount = toPagesCount;
    }
}
