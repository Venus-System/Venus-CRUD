package com.venus.crud.repository.jpa.scoring;

import com.venus.crud.entity.scoring.ProductScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductScoreRepository extends JpaRepository<ProductScore, Long> {

    Optional<ProductScore> findByProductVersionIdAndScoringModelId(Long productVersionId, Long scoringModelId);
    Page<ProductScore> findByProductVersionId(Long productVersionId, Pageable pageable);
    Page<ProductScore> findByOverallScoreGreaterThanEqual(Integer overallScore, Pageable pageable);
}
