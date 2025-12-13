package com.gameflix.gameflix.controller;

import com.gameflix.gameflix.model.Game;
import com.gameflix.gameflix.model.Review;
import com.gameflix.gameflix.service.GameService;
import com.gameflix.gameflix.service.LibraryService;
import com.gameflix.gameflix.service.ReviewService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class GamePageController {

    private final GameService gameService;
    private final ReviewService reviewService;
    private final LibraryService libraryService;

    public GamePageController(GameService gameService, ReviewService reviewService, LibraryService libraryService) {
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.libraryService = libraryService;
    }

    @GetMapping("/catalog")
    public String catalog(@RequestParam(value = "q", required = false) String search,
                          @RequestParam(value = "genre", required = false) String genre,
                          @RequestParam(value = "error", required = false) String error,
                          Model model) {
        List<Game> games = gameService.findAll();
        if (search != null && !search.isBlank()) {
            final String term = search.toLowerCase();
            games = games.stream()
                    .filter(g -> (g.getTitle() != null && g.getTitle().toLowerCase().contains(term)) ||
                                 (g.getDescription() != null && g.getDescription().toLowerCase().contains(term)))
                    .collect(Collectors.toList());
        }
        if (genre != null && !genre.isBlank()) {
            games = games.stream()
                    .filter(g -> genre.equalsIgnoreCase(g.getGenre()))
                    .collect(Collectors.toList());
        }
        List<String> genres = gameService.findAll().stream()
                .map(Game::getGenre)
                .filter(g -> g != null && !g.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("games", games);
        model.addAttribute("genres", genres);
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("error", error);
        return "catalog";
    }

    @GetMapping("/game/{id}")
    public String gameDetail(@PathVariable Long id,
                             @RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "success", required = false) String success,
                             Model model,
                             HttpSession session) {
        Optional<Game> game = gameService.findById(id);
        if (game.isEmpty()) {
            return "redirect:/catalog?error=Game+not+found";
        }
        List<Review> reviews = reviewService.listForGame(id);

        String username = (String) session.getAttribute("username");
        String planTier = (String) session.getAttribute("planTier");
        String requiredTier = game.get().getSubscriptionTier();
        boolean canAccess = canAccess(requiredTier, planTier);
        boolean inLibrary = username != null && libraryService.exists(username, id);
        boolean alreadyReviewed = username != null && reviewService.existsForUser(id, username);

        model.addAttribute("game", game.get());
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewForm", new ReviewForm(id));
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("requiredTier", requiredTier);
        model.addAttribute("planTier", planTier);
        model.addAttribute("canAccess", canAccess);
        model.addAttribute("inLibrary", inLibrary);
        model.addAttribute("alreadyReviewed", alreadyReviewed);
        return "game";
    }

    @PostMapping("/reviews/form")
    public String postReview(@ModelAttribute ReviewForm reviewForm,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            redirectAttributes.addFlashAttribute("loginError", "Please sign in to review.");
            return "redirect:/auth";
        }
        reviewForm.setUsername(username);

        if (reviewForm.getGameId() == null ||
            reviewForm.getComment() == null || reviewForm.getComment().isBlank() ||
            reviewForm.getRating() == null) {
            redirectAttributes.addAttribute("error", "All fields are required");
            return "redirect:/game/" + reviewForm.getGameId();
        }
        if (reviewForm.getRating() < 1 || reviewForm.getRating() > 5) {
            redirectAttributes.addAttribute("error", "Rating must be 1-5");
            return "redirect:/game/" + reviewForm.getGameId();
        }
        Optional<Game> game = gameService.findById(reviewForm.getGameId());
        if (game.isEmpty()) {
            redirectAttributes.addAttribute("error", "Game not found");
            return "redirect:/catalog";
        }
        if (reviewService.existsForUser(reviewForm.getGameId(), username)) {
            redirectAttributes.addAttribute("error", "You already reviewed this game");
            return "redirect:/game/" + reviewForm.getGameId();
        }
        reviewService.create(game.get(), reviewForm.getUsername(), reviewForm.getRating(), reviewForm.getComment());
        redirectAttributes.addAttribute("success", "Review saved");
        return "redirect:/game/" + reviewForm.getGameId();
    }

    private boolean canAccess(String required, String current) {
        int req = tierWeight(required);
        int cur = tierWeight(current);
        return cur >= req;
    }

    private int tierWeight(String tier) {
        if (tier == null) return 0;
        switch (tier.toLowerCase()) {
            case "ultimate": return 3;
            case "pro": return 2;
            case "starter": return 1;
            default: return 0;
        }
    }

    public static class ReviewForm {
        private Long gameId;
        private String username;
        private Integer rating;
        private String comment;

        public ReviewForm() {}

        public ReviewForm(Long gameId) {
            this.gameId = gameId;
        }

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
