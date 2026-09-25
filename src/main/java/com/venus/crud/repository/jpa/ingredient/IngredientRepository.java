package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.ingredient.Ingredient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByInciName(String inciName);
    Optional<Ingredient> findBySourceReference(String sourceReference);
    Slice<Ingredient> findByCommonNameContainingIgnoreCase(String commonName, Pageable pageable);
    Slice<Ingredient> findByIngredientCategoryId(Long ingredientCategoryId, Pageable pageable);
    Slice<Ingredient> findByIngredientCategoryIdIn(Collection<Long> ingredientCategoryIds, Pageable pageable);
    Slice<Ingredient> findByIrritationRiskLevelGreaterThanEqual(Short irritationRiskLevel, Pageable pageable);
    Slice<Ingredient> findAllBy(Pageable pageable);

    @Query("select i from Ingredient i where upper(i.inciName) in :names")
    List<Ingredient> findByUpperInciNameIn(@Param("names") Collection<String> names);
}
