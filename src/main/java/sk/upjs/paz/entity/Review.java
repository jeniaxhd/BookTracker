package sk.upjs.paz.entity;

import java.time.LocalDateTime;

public class Review {
    private Long id;
    private Book book;
    private User user;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;



    public Review() {
    }
    private void validateRating(int rating){
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 1 and 10");
        }
    }
    public Review(Long id, Book book, User user, int rating, String comment, LocalDateTime createdAt) {
        validateRating(rating);
        this.id = id;
        this.book = book;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Review(Book book, User user, int rating, String comment) {
        validateRating(rating);
        this.book = book;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        validateRating(rating);
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
