package com.gameflix.gameflix.repository;

import com.gameflix.gameflix.model.LibraryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<LibraryEntry, Long> {

    List<LibraryEntry> findByUsername(String username);

    Optional<LibraryEntry> findByUsernameAndGameId(String username, Long gameId);
}
