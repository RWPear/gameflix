package com.gameflix.gameflix.controller;

import com.gameflix.gameflix.model.Game;
import com.gameflix.gameflix.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminGameController {

    private final GameService gameService;

    public AdminGameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/admin/games/new")
    public String newGame(Model model) {
        model.addAttribute("game", new Game());
        return "admin_game_form";
    }

    @PostMapping("/admin/games")
    public String createGame(@ModelAttribute("game") Game game,
                             RedirectAttributes redirectAttributes) {
        gameService.save(game);
        redirectAttributes.addFlashAttribute("success", "Game saved: " + game.getTitle());
        return "redirect:/catalog";
    }
}
