package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.ReviewDao;
import sk.upjs.paz.entity.Review;
import sk.upjs.paz.service.ReviewService;

import java.util.List;
import java.util.Optional;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;

    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    @Override
    public void add(Review review) {
        validateReviewForCreate(review);
        reviewDao.add(review);
    }

    @Override
    public void update(Review review) {
        reviewDao.update(review);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }
        reviewDao.delete(id);
    }

    @Override
    public Optional<Review> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }
        return reviewDao.getById(id);
    }

    @Override
    public List<Review> getAll() {
        return reviewDao.getAll();
    }

    @Override
    public List<Review> getByBook(Long bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        return reviewDao.getByBook(bookId);
    }

    @Override
    public List<Review> getByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return reviewDao.getByUser(userId);
    }

    @Override
    public Optional<Review> getByUserAndBook(Long userId, Long bookId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        return reviewDao.getByUserAndBook(userId, bookId);
    }

    // ----------------- private validation -----------------

    private void validateReviewForCreate(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }

        if (review.getBook() == null || review.getBook().getId() == null) {
            throw new IllegalArgumentException("Book and its ID cannot be null");
        }

        if (review.getUser() == null || review.getUser().getId() == null) {
            throw new IllegalArgumentException("User and its ID cannot be null");
        }

    }


}
