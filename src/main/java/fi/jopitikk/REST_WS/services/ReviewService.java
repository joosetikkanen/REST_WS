package fi.jopitikk.REST_WS.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.jopitikk.REST_WS.model.Book;
import fi.jopitikk.REST_WS.model.Review;

public class ReviewService {
    
    BookService bookService = new BookService();

    public List<Review> getAllReviews(long bookId) {
        
        Book book = bookService.getBookById(bookId);
        Map<Long, Review> reviews = book.getReviews();
        List<Review> result = new ArrayList<>();
        
        reviews.forEach((id, review) -> {
            result.add(review);
        });
        return result;
        
    }

    public Review getReviewById(long bookId, long reviewId) {
        
        Book book = bookService.getBookById(bookId);
        Map<Long, Review> reviews = book.getReviews();
        return reviews.get(reviewId);
    }

    public Review addReview(long bookId, Review review) {
        
        Book book = bookService.getBookById(bookId);
        Map<Long, Review> reviews = book.getReviews();
        long reviewId = book.getReviewIdCounter();
        book.setReviewIdCounter((int) ++reviewId);
        review.setId(book.getReviewIdCounter());
        reviews.put((long) book.getReviewIdCounter(), review);
        book.setReviews(reviews);
        //book.setReviewIdCounter((int) ++reviewId);
        return review;
    }

    public Review updateReview(long bookId, long reviewId, Review review) {
        
        Book book = bookService.getBookById(bookId);
        Map<Long, Review> reviews = book.getReviews();
        reviews.put(reviewId, review);
        book.setReviews(reviews);
        return review;
    }

    public void removeReview(long bookId, long reviewId) {
        
        Book book = bookService.getBookById(bookId);
        Map<Long, Review> reviews = book.getReviews();
        reviews.remove(reviewId);
        book.setReviews(reviews);
        
    }

}
