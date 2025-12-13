package com.gameflix.gameflix.service;

import com.gameflix.gameflix.model.Game;
import com.gameflix.gameflix.model.Review;
import com.gameflix.gameflix.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> listForGame(Long gameId) {
        return reviewRepository.findByGameIdOrderByCreatedAtDesc(gameId);
    }

    public List<Review> listAll() {
        return reviewRepository.findAll();
    }

    public boolean existsForUser(Long gameId, String username) {
        return reviewRepository.findByGameIdAndUsername(gameId, username).isPresent();
    }

    public Review create(Game game, String username, Integer rating, String comment) {
        Review r = new Review();
        r.setGame(game);
        r.setUsername(username);
        r.setRating(rating);
        r.setComment(comment);
        r.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(r);
    }
}
