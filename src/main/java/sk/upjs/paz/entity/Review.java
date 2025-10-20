package sk.upjs.paz.entity;

import java.time.LocalDateTime;

public record Review (
        long id,
        Book book,
        User user,
        int rating,
        String comment,
        LocalDateTime createdAt
){
    public Review{
        if (rating < 1 || rating > 5){
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

}
