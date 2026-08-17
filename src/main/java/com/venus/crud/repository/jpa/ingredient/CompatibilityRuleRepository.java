package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.CompatibilityRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRule, Long> {

    @EntityGraph(attributePaths = "ingredientEffect")
    List<CompatibilityRule> findByScoringModelIdAndIsEnabledTrueOrderByPriority(Long scoringModelId);
    List<CompatibilityRule> findByIngredientEffectId(Long ingredientEffectId);
    Page<CompatibilityRule> findByEffectType(EffectType effectType, Pageable pageable);
    Page<CompatibilityRule> findBySourceType(SourceType sourceType, Pageable pageable);
}
