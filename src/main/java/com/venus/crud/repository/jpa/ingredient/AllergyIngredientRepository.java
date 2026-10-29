package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.AllergyIngredient;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyIngredientRepository extends JpaRepository<AllergyIngredient, Long> {

    @EntityGraph(attributePaths = "ingredient")
    List<AllergyIngredient> findByAllergyId(Long allergyId);
    @EntityGraph(attributePaths = "ingredient")
    List<AllergyIngredient> findByAllergyIdIn(Collection<Long> allergyIds);
    Optional<AllergyIngredient> findByAllergyIdAndIngredientId(Long allergyId, Long ingredientId);
    boolean existsByAllergyIdAndIngredientId(Long allergyId, Long ingredientId);
    void deleteByAllergyIdAndIngredientId(Long allergyId, Long ingredientId);

    @Query("""
            select allergyIngredient from AllergyIngredient allergyIngredient
            where (:ingredientId is null or allergyIngredient.ingredient.id = :ingredientId)
              and (cast(:sourceType as String) is null or allergyIngredient.sourceType = :sourceType)
            """)
    Slice<AllergyIngredient> search(Long ingredientId, SourceType sourceType, Pageable pageable);
}
