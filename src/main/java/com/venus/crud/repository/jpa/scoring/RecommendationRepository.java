package com.venus.crud.repository.jpa.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import com.venus.crud.entity.scoring.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @EntityGraph(attributePaths = "productVersion")
    List<Recommendation> findByUserIdOrderByRankingPosition(Long userId);
    List<Recommendation> findByAnalysisResultId(Long analysisResultId);
    Page<Recommendation> findByUserIdAndRecommendationType(Long userId, RecommendationType recommendationType, Pageable pageable);
    Page<Recommendation> findByProductVersionId(Long productVersionId, Pageable pageable);
}
