package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientEffect;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientEffectRepository extends JpaRepository<IngredientEffect, Long> {

    @EntityGraph(attributePaths = "profileTag")
    List<IngredientEffect> findByIngredientId(Long ingredientId);
    List<IngredientEffect> findByIngredientIdAndProfileTagId(Long ingredientId, Long profileTagId);

    @Query("""
            select ingredientEffect from IngredientEffect ingredientEffect
            where (:profileTagId is null or ingredientEffect.profileTag.id = :profileTagId)
              and (cast(:effectCategory as String) is null or ingredientEffect.effectCategory = :effectCategory)
              and (cast(:reviewStatus as String) is null or ingredientEffect.reviewStatus = :reviewStatus)
              and (cast(:sourceType as String) is null or ingredientEffect.sourceType = :sourceType)
            """)
    Slice<IngredientEffect> search(Long profileTagId, EffectCategory effectCategory, ReviewStatus reviewStatus,
            SourceType sourceType, Pageable pageable);
}