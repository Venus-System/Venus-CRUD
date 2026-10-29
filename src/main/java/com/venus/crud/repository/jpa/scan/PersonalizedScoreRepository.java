package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.scan.PersonalizedScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalizedScoreRepository extends JpaRepository<PersonalizedScore, Long> {

    Optional<PersonalizedScore> findByAnalysisResultId(Long analysisResultId);
    boolean existsByAnalysisResultId(Long analysisResultId);
    void deleteByAnalysisResultId(Long analysisResultId);
    Slice<PersonalizedScore> findByUserIdAndProductVersionId(Long userId, Long productVersionId, Pageable pageable);
    Slice<PersonalizedScore> findByUserId(Long userId, Pageable pageable);
    Slice<PersonalizedScore> findByUserIdAndRiskLevel(Long userId, RiskLevel riskLevel, Pageable pageable);
    Slice<PersonalizedScore> findByUserIdAndRecommendationLevel(Long userId, RecommendationLevel recommendationLevel, Pageable pageable);
    Slice<PersonalizedScore> findAllBy(Pageable pageable);
}