package com.gameflix.gameflix.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gameflix.gameflix.model.Game;
import com.gameflix.gameflix.model.Review;
import com.gameflix.gameflix.service.GameService;
import com.gameflix.gameflix.service.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final GameService gameService;

    public ReviewController(ReviewService reviewService, GameService gameService) {
        this.reviewService = reviewService;
        this.gameService = gameService;
    }

    @GetMapping
    public List<Review> list(@RequestParam(name = "gameId", required = false) Long gameId) {
        if (gameId != null) {
            return reviewService.listForGame(gameId);
        }
        return reviewService.listAll();
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody ReviewRequest req) {
        Map<String, String> res = new HashMap<>();

        if (req.getGameId() == null || req.getUsername() == null || req.getRating() == null || req.getComment() == null) {
            res.put("message", "gameId, username, rating, and comment are required");
            return ResponseEntity.badRequest().body(res);
        }

        if (req.getRating() < 1 || req.getRating() > 5) {
            res.put("message", "Rating must be between 1 and 5");
            return ResponseEntity.badRequest().body(res);
        }

        Optional<Game> game = gameService.findById(req.getGameId());
        if (game.isEmpty()) {
            res.put("message", "Game not found");
            return ResponseEntity.status(404).body(res);
        }

        Review saved = reviewService.create(game.get(), req.getUsername(), req.getRating(), req.getComment());
        res.put("message", "Review saved");
        res.put("id", saved.getId().toString());
        return ResponseEntity.ok(res);
    }

    public static class ReviewRequest {
        private Long gameId;
        private String username;
        private Integer rating;
        private String comment;

        public Long getGameId() {
            return gameId;
        }

        public void setGameId(Long gameId) {
            this.gameId = gameId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
