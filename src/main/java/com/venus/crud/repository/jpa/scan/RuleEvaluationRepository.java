package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.scan.RuleEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleEvaluationRepository extends JpaRepository<RuleEvaluation, Long> {

    @EntityGraph(attributePaths = {"ingredient", "profileTag"})
    List<RuleEvaluation> findByAnalysisResultId(Long analysisResultId);
    List<RuleEvaluation> findByAnalysisResultIdAndWasMatchedTrue(Long analysisResultId);
    Page<RuleEvaluation> findByIngredientId(Long ingredientId, Pageable pageable);
    Page<RuleEvaluation> findByCompatibilityRuleId(Long compatibilityRuleId, Pageable pageable);
}
