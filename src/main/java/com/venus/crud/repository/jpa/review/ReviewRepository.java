package com.venus.crud.repository.jpa.review;

import com.venus.crud.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = "user")
    Page<Review> findByProductVersionId(Long productVersionId, Pageable pageable);
    Page<Review> findByUserId(Long userId, Pageable pageable);
    boolean existsByUserIdAndProductVersionId(Long userId, Long productVersionId);
    Page<Review> findByProductVersionIdAndVerifiedUseTrue(Long productVersionId, Pageable pageable);
    Page<Review> findByProductVersionIdAndRatingGreaterThanEqual(Long productVersionId, BigDecimal rating, Pageable pageable);
}
