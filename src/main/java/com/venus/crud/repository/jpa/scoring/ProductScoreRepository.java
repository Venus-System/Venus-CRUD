package com.venus.crud.repository.jpa.scoring;

import com.venus.crud.entity.scoring.ProductScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductScoreRepository extends JpaRepository<ProductScore, Long> {

    Optional<ProductScore> findByProductVersionIdAndScoringModelId(Long productVersionId, Long scoringModelId);
    boolean existsByProductVersionIdAndScoringModelId(Long productVersionId, Long scoringModelId);
    void deleteByProductVersionIdAndScoringModelId(Long productVersionId, Long scoringModelId);
    Slice<ProductScore> findByProductVersionId(Long productVersionId, Pageable pageable);
    Slice<ProductScore> findByOverallScoreGreaterThanEqual(Integer overallScore, Pageable pageable);
    Slice<ProductScore> findAllBy(Pageable pageable);
}