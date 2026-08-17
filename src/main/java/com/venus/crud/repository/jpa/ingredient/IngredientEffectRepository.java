package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientEffect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientEffectRepository extends JpaRepository<IngredientEffect, Long> {

    @EntityGraph(attributePaths = "profileTag")
    List<IngredientEffect> findByIngredientId(Long ingredientId);
    Page<IngredientEffect> findByProfileTagId(Long profileTagId, Pageable pageable);
    List<IngredientEffect> findByIngredientIdAndProfileTagId(Long ingredientId, Long profileTagId);
    Page<IngredientEffect> findByEffectCategory(EffectCategory effectCategory, Pageable pageable);
    Page<IngredientEffect> findByReviewStatus(ReviewStatus reviewStatus, Pageable pageable);
    Page<IngredientEffect> findBySourceType(SourceType sourceType, Pageable pageable);
}
