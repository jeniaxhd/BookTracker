package sk.upjs.paz.entity;

import sk.upjs.paz.enums.*;

public record Book (
        Long id,
        String title,
        Author author,
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

