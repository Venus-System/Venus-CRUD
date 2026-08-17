package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.RestrictionType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientRegulation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRegulationRepository extends JpaRepository<IngredientRegulation, Long> {

    @EntityGraph(attributePaths = "regulation")
    List<IngredientRegulation> findByIngredientId(Long ingredientId);
    Page<IngredientRegulation> findByRegulationId(Long regulationId, Pageable pageable);
    List<IngredientRegulation> findByIngredientIdAndRestrictionType(Long ingredientId, RestrictionType restrictionType);
    boolean existsByIngredientIdAndRegulationId(Long ingredientId, Long regulationId);
    Page<IngredientRegulation> findBySourceType(SourceType sourceType, Pageable pageable);
}
