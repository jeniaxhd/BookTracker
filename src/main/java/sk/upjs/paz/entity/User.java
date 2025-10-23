package sk.upjs.paz.entity;

import sk.upjs.paz.enums.UserRank;

import java.time.LocalDate;

public class User {
    private long id;
    private String name;
    private String email;
    private LocalDate createdAt;
    private int readedBooks;
    private String password;
    private UserRank rank;

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


    public UserRank getRank() {
        return UserRank.getRankByReadBooks(readedBooks);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getReadBooks() {
        return readedBooks;
    }

    public void setReadBooks(int readBooks) {
        this.readedBooks = readBooks;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public int getReadedBooks() {
        return readedBooks;
    }

    public void setReadedBooks(int readedBooks) {
        this.readedBooks = readedBooks;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRank(UserRank rank) {
        this.rank = rank;
    }



    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
