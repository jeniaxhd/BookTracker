package sk.upjs.paz.entity;

import sk.upjs.paz.enums.UserRank;

public record User (
    long id,
    String name,
    String email,
    int readBooks
) {
    public UserRank getRank() {
        return UserRank.getRankByReadBooks(readBooks);
    }
}
