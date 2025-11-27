package sk.upjs.paz.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private int readBooks;

    public User() {
    }

    public User(Long id, String name, String email, LocalDateTime createdAt, int readBooks) {
        this.id = id;
        setName(name);
        setEmail(email);
        this.createdAt = createdAt;
        this.readBooks = readBooks;
    }

    public User(String name, String email, int readBooks) {
        setName(name);
        setEmail(email);
        this.readBooks = readBooks;
    }

    private boolean isEmailValid(String email) {
        return email != null && email.matches("^[^@]+@[^@]+\\.[^@]+$");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        this.email = email;
    }

    public int getReadBooks() {
        return readBooks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReadBooks(int readBooks) {
        if (readBooks < 0) {
            throw new IllegalArgumentException("readBooks cannot be negative");
        }
        this.readBooks = readBooks;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
