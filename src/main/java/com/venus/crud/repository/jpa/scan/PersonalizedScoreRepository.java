package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.scan.PersonalizedScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalizedScoreRepository extends JpaRepository<PersonalizedScore, Long> {

    Optional<PersonalizedScore> findByAnalysisResultId(Long analysisResultId);
    Optional<PersonalizedScore> findByUserIdAndProductVersionId(Long userId, Long productVersionId);
    Page<PersonalizedScore> findByUserId(Long userId, Pageable pageable);
    Page<PersonalizedScore> findByUserIdAndRiskLevel(Long userId, RiskLevel riskLevel, Pageable pageable);
    Page<PersonalizedScore> findByUserIdAndRecommendationLevel(Long userId, RecommendationLevel recommendationLevel, Pageable pageable);
}
