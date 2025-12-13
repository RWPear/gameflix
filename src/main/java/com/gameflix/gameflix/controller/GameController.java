package com.gameflix.gameflix.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gameflix.gameflix.model.Game;
import com.gameflix.gameflix.service.GameService;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<Game> list() {
        return gameService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Optional<Game> game = gameService.findById(id);
        if (game.isEmpty()) {
            Map<String, String> res = new HashMap<>();
            res.put("message", "Game not found");
            return ResponseEntity.status(404).body(res);
        }
        return ResponseEntity.ok(game.get());
    }
}
