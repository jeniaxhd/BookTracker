package sk.upjs.paz.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private int readedBooks;

    public User(){}

    public User (String name, String email, int readedBooks) {
        this.name = name;
        this.email = email;
        this.readedBooks = readedBooks;
    }

    public boolean isValid() {
        return name != null && !name.isBlank()
                && email != null && email.contains("@");
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
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getReadedBooks() {
        return readedBooks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReadedBooks(int readedBooks) {
        this.readedBooks = readedBooks;
    }



    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
