package com.venus.crud.repository.jpa.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import com.venus.crud.entity.scoring.Recommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserIdOrderByRankingPosition(Long userId);
    List<Recommendation> findByAnalysisResultId(Long analysisResultId);

    @Query("""
            select recommendation from Recommendation recommendation
            where (:userId is null or recommendation.user.id = :userId)
              and (cast(:recommendationType as String) is null or recommendation.recommendationType = :recommendationType)
              and (:productVersionId is null or recommendation.productVersion.id = :productVersionId)
            """)
    Slice<Recommendation> search(Long userId, RecommendationType recommendationType, Long productVersionId,
            Pageable pageable);
}