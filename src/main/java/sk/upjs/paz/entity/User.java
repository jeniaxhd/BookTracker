package sk.upjs.paz.entity;

import sk.upjs.paz.enums.UserRank;

public class User {
    private long id;
    private String name;
    private String email;
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
