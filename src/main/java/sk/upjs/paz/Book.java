package sk.upjs.paz;

public record Book (
        Long id,
        String title,
        String author,
        Genre genre,
        int year,
        double rating,
        boolean read
) {
    @Override
    public String toString() {
        return "[" + title + "by" + author + "]";
    }
}

