package sk.upjs.paz.entity;

import sk.upjs.paz.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReadingSession {
    private long id;
    private Book book;
    private User user;
    private Bookstate state;
    private LocalDateTime start;
    private LocalDateTime end;
    private int duration;
    private int endPage;
    private LocalDate lastTimeRead;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getPagesRead() {
        return endPage;
    }

    public void setPagesRead(int endPage) {
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

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ReadingSession() {

    }

    public void UpdateProgress(int newPagesRead) {
        Bookstate finalState = state;

        if (endPage > 0) {
            if (state != Bookstate.FINISHED && state != Bookstate.ABANDONED) {
                finalState = Bookstate.READING;
            }
        }

        state = finalState;
    }
}
