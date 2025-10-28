package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Review;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public interface ReviewDao {
    void add(Review review);
    void update(Review review);
    void delete(long id);

    List<Review> getAll();
    List<Review> getByBook(long bookId);
    List<Review> getByUser(long userId);
    Optional<Review> findById(long id);
    OptionalDouble getAverageRatingForBook(long bookId);
}
