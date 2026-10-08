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

    @Query("""
            select ingredient from Ingredient ingredient
            join ingredient.ingredientCategory category
            where (cast(:commonName as String) is null or lower(ingredient.commonName) like lower(concat('%', cast(:commonName as String), '%')))
              and (:ingredientCategoryId is null or category.id = :ingredientCategoryId)
              and (:categoryTreeId is null or category.id = :categoryTreeId or category.parentCategory.id = :categoryTreeId)
              and (:minIrritationRiskLevel is null or ingredient.irritationRiskLevel >= :minIrritationRiskLevel)
            """)
    Slice<Ingredient> search(String commonName, Long ingredientCategoryId, Long categoryTreeId,
            Short minIrritationRiskLevel, Pageable pageable);

    @Query("select i from Ingredient i where upper(i.inciName) in :names")
    List<Ingredient> findByUpperInciNameIn(@Param("names") Collection<String> names);
}
