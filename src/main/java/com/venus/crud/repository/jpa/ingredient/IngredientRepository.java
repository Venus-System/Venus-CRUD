package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.ingredient.Ingredient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByInciName(String inciName);
    Optional<Ingredient> findBySourceReference(String sourceReference);
    Slice<Ingredient> findByCommonNameContainingIgnoreCase(String commonName, Pageable pageable);
    Slice<Ingredient> findByIngredientCategoryId(Long ingredientCategoryId, Pageable pageable);
    Slice<Ingredient> findByIrritationRiskLevelGreaterThanEqual(Short irritationRiskLevel, Pageable pageable);
    Slice<Ingredient> findAllBy(Pageable pageable);
}