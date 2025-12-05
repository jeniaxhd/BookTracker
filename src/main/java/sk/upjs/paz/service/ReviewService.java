package sk.upjs.paz.service;

import sk.upjs.paz.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    void add(Review review);

    void update(Review review);

    void delete(Long id);

    Optional<Review> getById(Long id);

    List<Review> getAll();

    List<Review> getByBook(Long bookId);

    List<Review> getByUser(Long userId);

    Optional<Review> getByUserAndBook(Long userId, Long bookId);
}
