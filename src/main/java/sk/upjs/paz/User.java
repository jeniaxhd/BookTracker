package sk.upjs.paz;

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
