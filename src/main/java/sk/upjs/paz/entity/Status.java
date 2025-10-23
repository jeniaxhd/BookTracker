package sk.upjs.paz.entity;

import sk.upjs.paz.enums.*;

import java.time.LocalDate;

public class Status {
    private long id;
    private Book book;
    private User user;
    private Bookstate state;
    private int pagesRead;
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
        return pagesRead;
    }

    public void setPagesRead(int pagesRead) {
        this.pagesRead = pagesRead;
    }

    public LocalDate getLastTimeRead() {
        return lastTimeRead;
    }

    public void setLastTimeRead(LocalDate lastTimeRead) {
        this.lastTimeRead = lastTimeRead;
    }

    public Status() {

    }

    public void UpdateProgress(int newPagesRead) {
        Bookstate finalState = state;

        if (pagesRead > 0) {
            if (state != Bookstate.FINISHED && state != Bookstate.ABANDONED) {
                finalState = Bookstate.READING;
            }
        }

        state = finalState;
    }
}
