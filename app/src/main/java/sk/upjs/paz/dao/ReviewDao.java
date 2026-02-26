package sk.upjs.paz.dao;

import sk.upjs.paz.entity.Review;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public interface ReviewDao {
    void add(Review review);
    void update(Review review);
    void delete(Long id);

    List<Review> getByBook(Long bookId);
    List<Review> getByUser(Long userId);
    Optional<Review> getById(Long id);
    Optional<Review> getByUserAndBook(Long userId, Long bookId);

    List<Review> getAll();
}
