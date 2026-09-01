package com.venus.crud.repository.jpa.review;

import com.venus.crud.entity.review.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndProductVersionId(Long userId, Long productVersionId);
    Slice<Review> findByProductVersionId(Long productVersionId, Pageable pageable);
    Slice<Review> findByUserId(Long userId, Pageable pageable);
    Slice<Review> findByProductVersionIdAndVerifiedUseTrue(Long productVersionId, Pageable pageable);
    Slice<Review> findByProductVersionIdAndRatingGreaterThanEqual(Long productVersionId, BigDecimal rating, Pageable pageable);
    Slice<Review> findAllBy(Pageable pageable);
}