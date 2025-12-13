package com.gameflix.gameflix.service;

import com.gameflix.gameflix.model.Game;
import com.gameflix.gameflix.model.LibraryEntry;
import com.gameflix.gameflix.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public List<LibraryEntry> listForUser(String username) {
        return libraryRepository.findByUsername(username);
    }


    public boolean exists(String username, Long gameId) {
        return libraryRepository.findByUsernameAndGameId(username, gameId).isPresent();
    }

    public LibraryEntry add(String username, Game game) {
        Optional<LibraryEntry> existing = libraryRepository.findByUsernameAndGameId(username, game.getId());
        if (existing.isPresent()) {
            return existing.get();
        }
        LibraryEntry entry = new LibraryEntry();
        entry.setUsername(username);
        entry.setGame(game);
        entry.setAddedAt(LocalDateTime.now());
        return libraryRepository.save(entry);
    }
}
