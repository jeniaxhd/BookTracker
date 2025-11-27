package sk.upjs.paz.dao.mysql;

import sk.upjs.paz.dao.ReviewDao;
import sk.upjs.paz.entity.Review;

import java.util.List;
import java.util.Optional;

public class ReviewJdbcDao implements ReviewDao {

    @Override
    public void add(Review review) {

    }

    @Override
    public void update(Review review) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Review> getByBook(Long bookId) {
        return List.of();
    }

    @Override
    public List<Review> getByUser(Long userId) {
        return List.of();
    }

    @Override
    public Optional<Review> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Review> getByUserAndBook(Long userId, Long bookId) {
        return Optional.empty();
    }

    @Override
    public List<Review> getAll() {
        return List.of();
    }
}
