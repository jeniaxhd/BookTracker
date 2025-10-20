package sk.upjs.paz;

public enum UserRank {
    NOVICE(0, 5),
    READER(6, 20),
    BOOKWORM(21, 50),
    LITERARY_GURU(51, 100),
    MASTER_OF_BOOKS(101, Integer.MAX_VALUE);

    private final int minBooks;
    private final int maxBooks;

    UserRank(int minBooks, int maxBooks) {
        this.minBooks = minBooks;
        this.maxBooks = maxBooks;
    }

    public static UserRank getRankByReadBooks(int readBooks) {
        for (UserRank rank : values()) {
            if (readBooks >= rank.minBooks && readBooks <= rank.maxBooks) {
                return rank;
            }
        }
        return NOVICE; // на всяк випадок
    }

    public int getMinBooks() { return minBooks; }
    public int getMaxBooks() { return maxBooks; }
}
