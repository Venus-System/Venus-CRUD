package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.ingredient.ProductIngredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Long> {

    @EntityGraph(attributePaths = "ingredient")
    List<ProductIngredient> findByProductVersionIdOrderByPosition(Long productVersionId);
    Page<ProductIngredient> findByIngredientId(Long ingredientId, Pageable pageable);
    boolean existsByProductVersionIdAndIngredientId(Long productVersionId, Long ingredientId);
    void deleteByProductVersionIdAndIngredientId(Long productVersionId, Long ingredientId);
}
