package com.gameflix.gameflix.controller;

import com.gameflix.gameflix.model.Game;
import com.gameflix.gameflix.model.PlanTier;
import com.gameflix.gameflix.service.GameService;
import com.gameflix.gameflix.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LibraryController {

    private final LibraryService libraryService;
    private final GameService gameService;

    public LibraryController(LibraryService libraryService, GameService gameService) {
        this.libraryService = libraryService;
        this.gameService = gameService;
    }

    @PostMapping("/library/add")
    public String addToLibrary(@RequestParam("gameId") Long gameId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Object user = session.getAttribute("username");
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "Please sign in to add games to your library.");
            return "redirect:/auth";
        }
        Optional<Game> g = gameService.findById(gameId);
        if (g.isEmpty()) {
            redirectAttributes.addAttribute("error", "Game not found");
            return "redirect:/catalog";
        }

        // Check subscription tier
        String requiredTier = PlanTier.normalize(g.get().getSubscriptionTier());
        String currentTier = PlanTier.normalize((String) session.getAttribute("planTier"));
        if (currentTier == null) {
            currentTier = PlanTier.FREE;
        }
        if (requiredTier != null && currentTier != null && !canAccess(requiredTier, currentTier)) {
            redirectAttributes.addAttribute("error", "This game requires " + requiredTier + " or higher.");
            return "redirect:/game/" + gameId;
        }

        libraryService.add(user.toString(), g.get());
        redirectAttributes.addAttribute("success", "Added to your library");
        return "redirect:/game/" + gameId;
    }

    private boolean canAccess(String required, String current) {
        return PlanTier.weight(current) >= PlanTier.weight(required);
    }
}
