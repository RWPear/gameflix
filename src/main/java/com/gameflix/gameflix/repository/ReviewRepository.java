package com.gameflix.gameflix.repository;

import com.gameflix.gameflix.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByGameIdOrderByCreatedAtDesc(Long gameId);

    Optional<Review> findByGameIdAndUsername(Long gameId, String username);
}
