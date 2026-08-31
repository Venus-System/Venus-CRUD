package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.CompatibilityRule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRule, Long> {

    List<CompatibilityRule> findByScoringModelIdAndIsEnabledTrueOrderByPriority(Long scoringModelId);
    List<CompatibilityRule> findByIngredientEffectId(Long ingredientEffectId);
    Slice<CompatibilityRule> findByEffectType(EffectType effectType, Pageable pageable);
    Slice<CompatibilityRule> findBySourceType(SourceType sourceType, Pageable pageable);
    Slice<CompatibilityRule> findAllBy(Pageable pageable);
}