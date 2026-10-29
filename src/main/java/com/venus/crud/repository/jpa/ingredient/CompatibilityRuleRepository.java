package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.CompatibilityRule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRule, Long> {

    List<CompatibilityRule> findByScoringModelIdAndIsEnabledTrueOrderByPriority(Long scoringModelId);
    List<CompatibilityRule> findByIngredientEffectId(Long ingredientEffectId);

    @Query("""
            select compatibilityRule from CompatibilityRule compatibilityRule
            where (cast(:effectType as String) is null or compatibilityRule.effectType = :effectType)
              and (cast(:sourceType as String) is null or compatibilityRule.sourceType = :sourceType)
            """)
    Slice<CompatibilityRule> search(EffectType effectType, SourceType sourceType, Pageable pageable);
}