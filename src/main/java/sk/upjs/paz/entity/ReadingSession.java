package sk.upjs.paz.entity;

import sk.upjs.paz.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReadingSession {
    private Long id;
    private Book book;
    private User user;
    private Bookstate state;
    private LocalDateTime start;
    private int duration;
    private int endPage;
    private LocalDate lastTimeRead;

    public ReadingSession(){}

    public ReadingSession(Long id, Book book, User user, Bookstate state, LocalDateTime start,
                          int duration, int endPage, LocalDate lastTimeRead){
        this.id = id;
        this.user = user;
        this.book = book;
        this.state = state;
        this.start = start;
        this.duration = duration;
        this.endPage = endPage;
        this.lastTimeRead = lastTimeRead;
    }
    public ReadingSession(Book book, User user, Bookstate state, LocalDateTime start,
                          int duration, int endPage, LocalDate lastTimeRead){
        this.user = user;
        this.book = book;
        this.state = state;
        this.start = start;
        this.duration = duration;
        this.endPage = endPage;
        this.lastTimeRead = lastTimeRead;
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

    public Bookstate getState() {
        return state;
    }

    public void setState(Bookstate state) {
        this.state = state;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public LocalDate getLastTimeRead() {
        return lastTimeRead;
    }

    public void setLastTimeRead(LocalDate lastTimeRead) {
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

        if (state != Bookstate.FINISHED && state != Bookstate.ABANDONED) {
            if (book != null && endPage >= book.getPages()) {
                state = Bookstate.FINISHED;
            } else {
                state = Bookstate.READING;
            }
        }

        this.lastTimeRead = LocalDate.now();
    }
}
