package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.scan.RuleEvaluation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleEvaluationRepository extends JpaRepository<RuleEvaluation, Long> {

    @EntityGraph(attributePaths = {"ingredient", "profileTag"})
    List<RuleEvaluation> findByAnalysisResultId(Long analysisResultId);
    List<RuleEvaluation> findByAnalysisResultIdAndWasMatchedTrue(Long analysisResultId);

    @Query("""
            select ruleEvaluation from RuleEvaluation ruleEvaluation
            where (:ingredientId is null or ruleEvaluation.ingredient.id = :ingredientId)
              and (:compatibilityRuleId is null or ruleEvaluation.compatibilityRule.id = :compatibilityRuleId)
            """)
    Slice<RuleEvaluation> search(Long ingredientId, Long compatibilityRuleId, Pageable pageable);
}