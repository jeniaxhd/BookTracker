package sk.upjs.paz.entity;

import sk.upjs.paz.enums.*;

import java.time.LocalDateTime;

public class ReadingSession {
    private Long id;
    private Book book;
    private User user;
    private BookState state;
    private LocalDateTime start;
    private int duration;
    private int endPage;
    private LocalDateTime lastTimeRead;

    public ReadingSession(){}

    public ReadingSession(Long id, Book book, User user, BookState state, LocalDateTime start,
                          int duration, int endPage, LocalDateTime lastTimeRead){
        this.id = id;
        this.user = user;
        this.book = book;
        this.state = state;
        this.start = start;
        this.duration = duration;
        this.endPage = endPage;
        this.lastTimeRead = lastTimeRead;
    }
    public ReadingSession(Book book, User user, BookState state, LocalDateTime start,
                          int duration, int endPage, LocalDateTime lastTimeRead){
        this.user = user;
        this.book = book;
        this.state = state;
        this.start = start;
        this.duration = duration;
        this.endPage = endPage;
        this.lastTimeRead=lastTimeRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookState getState() {
        return state != null ? state : BookState.NOT_STARTED;
    }
    public void setState(BookState state) {
        this.state = state;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public LocalDateTime getLastTimeRead() {
        return lastTimeRead;
    }

    public void setLastTimeRead(LocalDateTime lastTimeRead) {
        this.lastTimeRead = lastTimeRead;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void updateProgress(int newEndPage) {
        if (newEndPage <= 0) {
            return;
        }

        this.endPage = newEndPage;

        if (state != BookState.FINISHED && state != BookState.ABANDONED) {
            if (book != null && endPage >= book.getPages()) {
                state = BookState.FINISHED;
            } else {
                state = BookState.READING;
            }
        }

        this.lastTimeRead = LocalDateTime.now();
    }
}
